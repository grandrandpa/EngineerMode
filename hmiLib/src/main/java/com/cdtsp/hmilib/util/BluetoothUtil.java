package com.cdtsp.hmilib.util;

import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothPbapClient;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import java.util.List;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/4/16.
 */

public class BluetoothUtil {

    private final String TAG = "BluetoothUtil";
    private static BluetoothUtil sInstance;
    private static Context sContext;
    private static LocalBluetoothManager sBtManager;
    private BluetoothAdapter mBluetoothAdapter;
    private HashMap<Integer, BluetoothProfile> mProfiles = new HashMap<Integer, BluetoothProfile>();
    private BluetoothA2dpSink mA2dpSink;
    private BluetoothPbapClient mPbapClient;
    private BluetoothHeadsetClient mHeadsetClient;
    private OnProxyAllReadyCallback mOnProxyAllReadyCallback;

    private BluetoothProfile.ServiceListener mBluetoothServiceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            switch (profile) {
                case BluetoothProfile.A2DP_SINK://蓝牙音乐
                    Log.d(TAG, "onServiceConnected: A2DP_SINK : " + proxy);
                    mA2dpSink = (BluetoothA2dpSink) proxy;
                    mProfiles.put(profile, mA2dpSink);
                    break;
                case BluetoothProfile.PBAP_CLIENT://联系人
                    Log.d(TAG, "onServiceConnected: PBAP_CLIENT : " + proxy);
                    mPbapClient = (BluetoothPbapClient) proxy;
                    mProfiles.put(profile, mPbapClient);
                    break;
                case BluetoothProfile.HEADSET_CLIENT://蓝牙电话
                    Log.d(TAG, "onServiceConnected: HEADSET_CLIENT : " + proxy);
                    mHeadsetClient = (BluetoothHeadsetClient) proxy;
                    mProfiles.put(profile, mHeadsetClient);
                    break;
            }
            if (isProxyReadyAll()) {
                if (mOnProxyAllReadyCallback != null) {
                    mOnProxyAllReadyCallback.onReady(mA2dpSink, mPbapClient, mHeadsetClient);
                }
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            //TODO
        }
    };

    /**
     * 初始化并获取BluetoothUtil
     * @param context
     * @return
     */
    public static BluetoothUtil get(Context context) {
        if (sInstance == null) {
            synchronized (BluetoothUtil.class) {
                if (sInstance == null) {
                    sContext = context;
                    sInstance = new BluetoothUtil(context);
                    sBtManager = LocalBluetoothManager.getInstance(context.getApplicationContext(), null);
                }
            }
        }
        return sInstance;
    }

    public void release() {
        close();
        sInstance = null;
        sContext = null;
        sBtManager = null;
    }

    /**
     * 关闭BluetoothUtil对象
     * 将get(Context context)方法中绑定的BluetoothProfile解绑
     */
    public void close() {
        if (mBluetoothAdapter != null) {
            if (!mProfiles.isEmpty()) {
                for (int profile : mProfiles.keySet()) {
                    mBluetoothAdapter.closeProfileProxy(profile, mProfiles.get(profile));
                }
            }
        }
    }

    private BluetoothUtil(Context context) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        WeakReference<Context> weakReferenceContext = new WeakReference<>(context);
        if (mBluetoothAdapter != null) {
            Context weakContext = weakReferenceContext.get();
            if (weakContext != null) {
                mBluetoothAdapter.getProfileProxy(weakContext, mBluetoothServiceListener, BluetoothProfile.A2DP_SINK);
                mBluetoothAdapter.getProfileProxy(weakContext, mBluetoothServiceListener, BluetoothProfile.PBAP_CLIENT);
                mBluetoothAdapter.getProfileProxy(weakContext, mBluetoothServiceListener, BluetoothProfile.HEADSET_CLIENT);
            }
        } else {
            Log.d(TAG, "BluetoothUtil: mBluetoothAdapter = " + mBluetoothAdapter + ", BT support failed !!");
        }
    }

    /**
     * 连接蓝牙设备
     * 本方法将连接A2DP_SINK，PBAP_CLIENT，HEADSET_CLIENT三种profile
     * @param device
     */
    public void connectBtDevice(BluetoothDevice device) {
        Log.d(TAG, "connectBtDevice() called with: device = [" + device + "]");
        new Thread() {
            @Override
            public void run() {
                super.run();
                long startTime = SystemClock.uptimeMillis();
                while (!isProxyReady()) {
                    Log.d(TAG, "run: waiting for BluetoothProfile proxy !");
                    //wait for BluetoothProfile proxy
                    if (timeOut(startTime)) {
                        Log.d(TAG, "run: time out to waiting for BluetoothProfile proxy !");
                        Toast.makeText(sContext, "对不起，连接超时，请重试！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Log.d(TAG, "run: connect BluetoothA2dpSink and BluetoothHeadsetClient");
                mA2dpSink.connect(device);
                mHeadsetClient.connect(device);
            }
        }.start();
    }

    /**
     * 断开蓝牙设备连接
     * @param device
     */
    public void disconnectBtDevice(Context context, BluetoothDevice device) {
        Log.d(TAG, "disconnectBtDevice() called with: context = [" + context + "], device = [" + device + "]");
        new Thread() {
            @Override
            public void run() {
                super.run();
                long startTime = SystemClock.uptimeMillis();
                while (!isProxyReady()) {
                    Log.d(TAG, "run: waiting for BluetoothProfile proxy !");
                    //wait for BluetoothProfile proxy
                    if (timeOut(startTime)) {
                        Log.d(TAG, "run: time out to waiting for BluetoothProfile proxy !");
                        Toast.makeText(sContext, "对不起，断开超时，请重试！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Log.d(TAG, "run: disconnect BluetoothA2dpSink and BluetoothHeadsetClient");
                mA2dpSink.disconnect(device);
                mHeadsetClient.disconnect(device);
                if (mPbapClient != null) {
                    Log.d(TAG, "run: disconnect BluetoothPbapClient too");
                    mPbapClient.disconnect(device);
                }
            }
        }.start();
    }

    /**
     * 连接蓝牙设备
     * 本方法将连接A2DP_SINK
     * @param device
     */
    public void connectA2DPDevice(BluetoothDevice device) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                long startTime = SystemClock.uptimeMillis();
                while (!isProxyReady()) {
                    Log.d(TAG, "run: waiting for BluetoothProfile proxy !");
                    //wait for BluetoothProfile proxy
                    if (timeOut(startTime)) {
                        Log.d(TAG, "run: time out to waiting for BluetoothProfile proxy !");
                        Toast.makeText(sContext, "对不起，连接超时，请重试！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                mA2dpSink.connect(device);
            }
        }.start();
    }

    /**
     * 断开蓝牙设备A2DP连接
     * @param device
     */
    public void disconnectA2DPDevice(BluetoothDevice device) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                long startTime = SystemClock.uptimeMillis();
                while (!isProxyReady()) {
                    Log.d(TAG, "run: waiting for BluetoothProfile proxy !");
                    //wait for BluetoothProfile proxy
                    if (timeOut(startTime)) {
                        Log.d(TAG, "run: time out to waiting for BluetoothProfile proxy !");
                        Toast.makeText(sContext, "对不起，断开超时，请重试！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                mA2dpSink.disconnect(device);
            }
        }.start();
    }

    /**
     * 连接蓝牙设备
     * 本方法将连接A2DP_SINK，PBAP_CLIENT，HEADSET_CLIENT三种profile
     * @param device
     */
    public void connectBtDeviceWithContact(BluetoothDevice device) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                long startTime = SystemClock.uptimeMillis();
                while (!isProxyReadyAll()) {
                    Log.d(TAG, "run: waiting for BluetoothProfile proxy !");
                    //wait for BluetoothProfile proxy
                    if (timeOut(startTime)) {
                        Log.d(TAG, "run: time out to waiting for BluetoothProfile proxy !");
                        Toast.makeText(sContext, "对不起，连接超时，请重试！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                mA2dpSink.connect(device);
                mPbapClient.connect(device);
                mHeadsetClient.connect(device);
            }
        }.start();
    }

    /**
     * 如果时间超过5秒，视为超时
     * @param startTime
     * @return
     */
    private boolean timeOut(long startTime) {
        return (SystemClock.uptimeMillis() - startTime) > 5000;
    }

    /**
     * 判断BluetoothProfile proxy是否准备好
     * 不包括BluetoothPbapClient
     * @return
     */
    private boolean isProxyReady() {
        return mA2dpSink != null
                && mHeadsetClient != null;
    }

    /**
     * 判断BluetoothProfile proxy是否准备好
     * 包括BluetoothPbapClient
     * @return
     */
    public boolean isProxyReadyAll() {
        return mA2dpSink != null
                && mPbapClient != null
                && mHeadsetClient != null;
    }

    /**
     * 获取BluetoothA2dpSink代理对象
     * @return
     */
    public BluetoothA2dpSink getA2dpSinkProxy() {
        return mA2dpSink;
    }

    /**
     * 获取BluetoothPbapClient代理对象
     * @return
     */
    public BluetoothPbapClient getPbapClientProxy() {
        return mPbapClient;
    }

    /**
     * 获取BluetoothHeadsetClient代理对象
     * @return
     */
    public BluetoothHeadsetClient getHeadsetClientProxy() {
        return mHeadsetClient;
    }

    /**
     * 获取A2DP_SINK连接状态
     * @return
     */
    public int getA2dpConnectionState() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP_SINK);
        }
        return BluetoothAdapter.STATE_DISCONNECTED;
    }

    /**
     * 获取PBAP_CLIENT连接状态
     * @return
     */
    public int getPbapConnectionState() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.PBAP_CLIENT);
        }
        return BluetoothAdapter.STATE_DISCONNECTED;
    }

    /**
     * 获取HEADSET_CLIENT连接状态
     * @return
     */
    public int getHeadsetConnectionState() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET_CLIENT);
        }
        return BluetoothAdapter.STATE_DISCONNECTED;
    }

    /**
     * 获取一个BluetoothDevice的连接状态
     * @param device
     * @return
     */
    public int getConnectionState(BluetoothDevice device) {
//        for (BluetoothProfile profile : mProfiles) {
//            int connectionState = profile.getConnectionState(device);
//            if (connectionState == BluetoothAdapter.STATE_CONNECTED) {
//                return BluetoothAdapter.STATE_CONNECTED;
//            }
//        }
        int a2dpSink = BluetoothAdapter.STATE_DISCONNECTED;
        int pbapClient = BluetoothAdapter.STATE_DISCONNECTED;
        int headsetClient = BluetoothAdapter.STATE_DISCONNECTED;

        if (mA2dpSink != null) {
            a2dpSink = mA2dpSink.getConnectionState(device);
            Log.d(TAG,"get mA2dpSink");
        }
        if (mPbapClient != null) {
            Log.d(TAG,"get mPbapClient");
            pbapClient = mPbapClient.getConnectionState(device);
        }
        if (mHeadsetClient != null) {
            Log.d(TAG,"get mPbapClient");
            headsetClient = mHeadsetClient.getConnectionState(device);
        }

        if (a2dpSink == BluetoothAdapter.STATE_CONNECTED
                || pbapClient == BluetoothAdapter.STATE_CONNECTED
                || headsetClient == BluetoothAdapter.STATE_CONNECTED) {
            Log.d(TAG, "getConnectionState: a2dpSink=" + a2dpSink + ", pbapClient=" + pbapClient + ", headsetClient=" + headsetClient + ", return STATE_CONNECTED");
            return BluetoothAdapter.STATE_CONNECTED;
        } else if (a2dpSink == BluetoothAdapter.STATE_CONNECTING
                || pbapClient == BluetoothAdapter.STATE_CONNECTING
                || headsetClient == BluetoothAdapter.STATE_CONNECTING) {
            Log.d(TAG, "getConnectionState: a2dpSink=" + a2dpSink + ", pbapClient=" + pbapClient + ", headsetClient=" + headsetClient + ", return STATE_CONNECTING");
            return BluetoothAdapter.STATE_CONNECTING;
        } else if (a2dpSink == BluetoothAdapter.STATE_DISCONNECTING
                || pbapClient == BluetoothAdapter.STATE_DISCONNECTING
                || headsetClient == BluetoothAdapter.STATE_DISCONNECTING){
            Log.d(TAG, "getConnectionState: a2dpSink=" + a2dpSink + ", pbapClient=" + pbapClient + ", headsetClient=" + headsetClient + ", return STATE_DISCONNECTING");
            return BluetoothAdapter.STATE_DISCONNECTING;
        } else {
            Log.d(TAG, "getConnectionState: a2dpSink=" + a2dpSink + ", pbapClient=" + pbapClient + ", headsetClient=" + headsetClient + ", return STATE_DISCONNECTED");
            return BluetoothAdapter.STATE_DISCONNECTED;
        }
    }

    public int getA2DPConnectionState(BluetoothDevice device) {
        int a2dpSink = BluetoothAdapter.STATE_DISCONNECTED;
        if (mA2dpSink != null) {
            a2dpSink = mA2dpSink.getConnectionState(device);
            Log.d(TAG,"get mA2dpSink");
        }
        return a2dpSink;
    }

    /**
     * 蓝牙是否处于连接状态
     * @return
     */
    public boolean isBtConnected() {
        int a2dp = getA2dpConnectionState();
        int headSet = getHeadsetConnectionState();
        int pbap = getPbapConnectionState();
        return BluetoothAdapter.STATE_CONNECTED == a2dp
                || BluetoothAdapter.STATE_CONNECTED == headSet
                || BluetoothAdapter.STATE_CONNECTED == pbap;
    }

    /**
     * 获取已连接Hfp蓝牙设备列表
     *
     * @return 成功返回已连接设备列表（如果没有已连接设备返回空list），失败返回空list
     */
    public List<BluetoothDevice> getConnectedHfpRemoteBluetoothDevices() {
        if (null != mHeadsetClient) {
            return mHeadsetClient.getConnectedDevices();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 获取已连接A2DP蓝牙设备列表
     *
     * @return 成功返回已连接设备列表（如果没有已连接设备返回空list），失败返回空list
     */
    public List<BluetoothDevice> getConnectedA2DPRemoteBluetoothDevices() {
        if (null != mA2dpSink) {
            return mA2dpSink.getConnectedDevices();
        } else {
            return new ArrayList<>();
        }
    }

    public void registerProxyAllReadyCallback(OnProxyAllReadyCallback onProxyAllReadyCallback) {
        this.mOnProxyAllReadyCallback = onProxyAllReadyCallback;
    }

    public void unregisterProxyAllReadyCallback() {
        this.mOnProxyAllReadyCallback = null;
    }

    public interface OnProxyAllReadyCallback {
        void onReady(BluetoothA2dpSink a2dpSink, BluetoothPbapClient pbapClient, BluetoothHeadsetClient headsetClient);
    }
}
