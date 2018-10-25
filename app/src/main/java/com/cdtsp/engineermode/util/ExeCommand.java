package com.cdtsp.engineermode.util;

import android.util.Log;
import com.cdtsp.engineermode.util.LogUtils;
import java.io.File;
import java.io.FileOutputStream;
import android.os.Environment;
import android.content.Context;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Runtime.getRuntime;
import android.content.ContextWrapper;
/**
 * 执行命令的类
 * Created by Kappa
 */
public class ExeCommand {
    private static final String TAG = "ExeCommand";
    //shell进程
    private Process process;
    //对应进程的3个流
    private BufferedReader successResult;
    private BufferedReader errorResult;
    private DataOutputStream os;
    //是否同步，true：run会一直阻塞至完成或超时。false：run会立刻返回
    private boolean bSynchronous;
    //表示shell进程是否还在运行
    private boolean bRunning = false;
    //同步锁
    ReadWriteLock lock = new ReentrantReadWriteLock();

    //保存执行结果
    private StringBuffer result = new StringBuffer();

    /**
     * 构造函数
     *
     * @param synchronous true：同步，false：异步
     */
    public ExeCommand(boolean synchronous) {
        bSynchronous = synchronous;
    }

    /**
     * 默认构造函数，默认是同步执行
     */
    public ExeCommand() {
        bSynchronous = true;
    }

    /**
     * 还没开始执行，和已经执行完成 这两种情况都返回false
     *
     * @return 是否正在执行
     */
    public boolean isRunning() {
        return bRunning;
    }

    /**
     * @return 返回执行结果
     */
    public String getResult() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            LogUtils.i(TAG, "getResult");
            return new String(result);
        } finally {
            readLock.unlock();
        }
    }

//    openFileOutput:(写入文件，如果没有文件名可以创建，这里不需要判断是否有这个文件)---> FileOutputStream
//    openFileInput:(读取文件，没有文件名会保存，debug的时候会看到，不影响ui)---> FileInputStream
//
//    保存文件：（FileOutputStream 保存地址；data/data/包名/files/, 下面是写入的四种模式）
//    MODE_APPEND：即向文件尾写入数据
//    MODE_PRIVATE：即仅打开文件可写入数据
//    MODE_WORLD_READABLE：所有程序均可读该文件数据
//    MODE_WORLD_WRITABLE：即所有程序均可写入数据。

    public void writeCmd(Context context, String command) {
        String fileName = "/data/wormhole";
//        String msg = command + " \n";
//        FileOutputStream outputStream;
//
//        try {
//            outputStream = context.openFileOutput("/storage/emulated/0/incall/media/1.txt", Context.MODE_MULTI_PROCESS);
//            outputStream.write(msg.getBytes());
//            outputStream.flush();
//            outputStream.close();
//        } catch (Exception e) {
//            LogUtils.d(TAG, "writeCmd Exception: " + e.getMessage());
//            e.printStackTrace();
//        }

        LogUtils.d(TAG, "fileName: " + fileName);
        File myfile = new File(fileName);
        try {
            if (!myfile.exists()) {
                try {
                    myfile.createNewFile();
                } catch (IOException e) {
                    LogUtils.d(TAG, "createNewFile Exception: " + e.getMessage());
                }
            }
            LogUtils.d(TAG, "canWrite: " + myfile.canWrite());
            FileOutputStream fos = new FileOutputStream(myfile);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(command + "\n");
            osw.flush();
            osw.close();
            fos.close();

        } catch (IOException e) {

           LogUtils.d(TAG, "writeCmd Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 执行命令
     *
     * @param command eg: cat /sdcard/test.txt
     *                路径最好不要是自己拼写的路径，最好是通过方法获取的路径
     *                example：Environment.getExternalStorageDirectory()
     * @param maxTime 最大等待时间 (ms)
     * @return this
     */
    public ExeCommand run(String command, final int maxTime) {
        LogUtils.i(TAG, "run command:" + command + ",maxtime:" + maxTime);
        if (command == null || command.length() == 0) {
            return this;
        }
        try {
            process = getRuntime().exec("sh");//看情况可能是su
        } catch (Exception e) {
            return this;
        }
        bRunning = true;
        successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
        errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        os = new DataOutputStream(process.getOutputStream());

        try {
            //向sh写入要执行的命令
            os.write(command.getBytes());
            os.writeBytes("\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();

            LogUtils.i(TAG, "run flushed");
            os.close();
            //如果等待时间设置为非正，就不开启超时关闭功能
            if (maxTime > 0) {
                //超时就关闭进程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(maxTime);
                        } catch (Exception e) {
                        }
                        try {
                            int ret = process.exitValue();
                            LogUtils.i(TAG, "exitValue Stream over" + ret);
                        } catch (IllegalThreadStateException e) {
                            LogUtils.i(TAG, "take maxTime,forced to destroy process");
                            process.destroy();
                        }
                    }
                }).start();
            }

            //开一个线程来处理input流
            final Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    String line;
                    Lock writeLock = lock.writeLock();
                    try {
                        while ((line = successResult.readLine()) != null) {
                            line += "\n";
                            writeLock.lock();
                            result.append(line);
                            writeLock.unlock();
                        }
                    } catch (Exception e) {
                        LogUtils.i(TAG, "read InputStream exception:" + e.toString());
                    } finally {
                        try {
                            successResult.close();
                            LogUtils.i(TAG, "read InputStream over");
                        } catch (Exception e) {
                            LogUtils.i(TAG, "close InputStream exception:" + e.toString());
                        }
                    }
                }
            });
            t1.start();

            //开一个线程来处理error流
            final Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    String line;
                    Lock writeLock = lock.writeLock();
                    try {
                        while ((line = errorResult.readLine()) != null) {
                            line += "\n";
                            writeLock.lock();
                            result.append(line);
                            writeLock.unlock();
                        }
                    } catch (Exception e) {
                        LogUtils.i(TAG, "read ErrorStream exception:" + e.toString());
                    } finally {
                        try {
                            errorResult.close();
                            LogUtils.i(TAG, "read ErrorStream over");
                        } catch (Exception e) {
                            LogUtils.i(TAG, "read ErrorStream exception:" + e.toString());
                        }
                    }
                }
            });
            t2.start();

            Thread t3 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //等待执行完毕
                        t1.join();
                        t2.join();
                        process.waitFor();
                    } catch (Exception e) {

                    } finally {
                        bRunning = false;
                        LogUtils.i(TAG, "run command process end");
                    }
                }
            });
            t3.start();

            if (bSynchronous) {
                LogUtils.i(TAG, "run is go to end");
                t3.join();
                LogUtils.i(TAG, "run is end");
            }
        } catch (Exception e) {
            LogUtils.i(TAG, "run command process exception:" + e.toString());
        }
        return this;
    }

}