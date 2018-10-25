package com.cdtsp.engineermode.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.cdtsp.hmilib.ui.dialog.TspWaitDialog;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/******************************************************************************
 *  Copyright (C), China TSP, All right reserved
 ******************************************************************************
 **
 * @file EngineeringMode
 * @author Li Jiwen
 * @created 2018/9/25
 * @brief
 **/
public class EngineeringMode {
    private static final String TAG = "EngineeringMode";

    private static EngineeringMode sInstance;
    private Context mContext;

    private Timer mTimer;
    static int mClickCount = 0;
    private final int PRESET_TIMES = 5;

    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            mClickCount = 0;
        }
    };

    private TspWaitDialog mWaitDialog;

    public static EngineeringMode getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new EngineeringMode(context);
        }

        return sInstance;
    }

    /**
     * 默认构造函数，默认是同步执行
     */
    public EngineeringMode(Context context) {
        mContext = context;
    }

    public void clear() {
        mContext = null;
    }

    public void processEngineerMode(int index) {

        String result;
        String cmd;
        switch(index) {
            case 0:
                break;

            case 1:
                cmd = "htalk shell fastboot.sh";
                LogUtils.d("processEngineerMode cmd = ", cmd);
                showWaitDialog(mContext, "正在进入fastboot，请等待...");
                new ExeCommand().writeCmd(mContext,cmd);
                break;

            case 2: //ADB
                cmd = "echo \"peripheral\" > /sys/devices/soc/6a00000.ssusb/mode";
                LogUtils.d("processEngineerMode cmd = ", cmd);
                new ExeCommand().writeCmd(mContext,cmd);
                break;

            case 3: //USB
                cmd = "echo \"host\" > /sys/devices/soc/6a00000.ssusb/mode";
                LogUtils.d("processEngineerMode cmd = ", cmd);
                new ExeCommand().writeCmd(mContext,cmd);
                break;

            case 4:
                if(!TextUtils.isEmpty(getUsbPath(mContext))) {
                    LogUtils.d("processEngineerMode", "usb path: " + getUsbPath(mContext));
                    cmd = "cd " + getUsbPath(mContext) + " && " + "cp -rf amapauto8 /sdcard/";
                    LogUtils.d("processEngineerMode cmd = ", cmd);
                    new shellAsyncTask().execute(cmd);
                } else {
                    EngineerUtils.toast(mContext, "No devices!");
                }

                break;
            default:
                break;
        }

    }

    public static String getUsbPath(Context context){
        StorageManager storageManager = context.getSystemService(StorageManager.class);
        List<StorageVolume> volumes = storageManager.getStorageVolumes();
        String path = null;
        if(volumes != null && volumes.size() > 1){
            StorageVolume volume = volumes.get(1);
            if(volume.getState().equals("mounted")){
                path = "/storage/"+volume.getUuid();
            }
        }
        return path;
    }

    private class shellAsyncTask extends AsyncTask<String,Void,Boolean> {

        //onPreExecute用于异步处理前的操作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //此处将progressBar设置为可见.
            mWaitDialog = showWaitDialog(mContext, "正在拷贝地图数据，请等待...");
        }

        //在doInBackground方法中进行异步任务的处理.
        @Override
        protected Boolean doInBackground(String... params) {
            //获取传进来的参数
            String command = params[0];
            String result = new ExeCommand().run(command, 120000).getResult();
            LogUtils.d("processEngineerMode result = ", result);
            return true;
        }

        //onPostExecute用于UI的更新.此方法的参数为doInBackground方法返回的值.
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
                mWaitDialog = null;
            }
        }
    }


    public TspWaitDialog showWaitDialog(final Context context, String info) {
        TspWaitDialog dialog = new TspWaitDialog(context);
        dialog.setMessage(info);
        dialog.show();
        return dialog;
    }

    private void checkClickTimes() {
        mClickCount++;
        LogUtils.d("SideBarFragment", "checkClickTimes : " + mClickCount);
        stopTimer();
        if(PRESET_TIMES == mClickCount) {
            //showSelectDialog(mContext);

        }

        startTimer();
    }

    private void startTimer(){

        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mClickCount = 0;
                }
            };
        }

        if(mTimer != null && mTimerTask != null )
            mTimer.schedule(mTimerTask, 2000);
    }

    private void stopTimer(){

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }
}
