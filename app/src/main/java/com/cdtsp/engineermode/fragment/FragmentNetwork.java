package com.cdtsp.engineermode.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cdtsp.engineermode.R;
import com.cdtsp.engineermode.util.EngineerUtils;
import com.cdtsp.engineermode.util.PermissionHelper;
import com.cdtsp.engineermode.view.SwitchItem;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 */
public class FragmentNetwork extends Fragment implements SwitchItem.OnSwitchCallback {

    private static final String TAG = "FragmentNetwork";
    private static final int REQUEST_PERMISSIONS = 0;
    private final int MSG_SHOW_CONNECTED_WIFI = 0;
    private final int MSG_NOTIFY_WIFI_DATA_CHANGED = 1;
    private final String[] AUTH = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private SwitchItem mSettingSwitchItem;
    private WifiManager mWifiManager;
    private TextView mtvIpAddr;

    /**
     * wifi广播接收器
     */
    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            Log.d(TAG, "wifi onReceive: action=" + action);
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                boolean isScanned = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (isScanned) {
                    List<ScanResult> scanResults = mWifiManager.getScanResults();
                    Log.d(TAG, "onReceive: scanResults.size=" + scanResults.size() + ", isScanned=" + isScanned);
                }
            } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int wifiState = mWifiManager.getWifiState();
                if (wifiState == WifiManager.WIFI_STATE_ENABLING) {
                    Log.d(TAG, "onReceive: WIFI_STATE_ENABLING");
                    mSettingSwitchItem.setSubTitleText(getResources().getString(R.string.wifi_activing));
                } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                    Log.d(TAG, "onReceive: WIFI_STATE_ENABLED");
                    mSettingSwitchItem.setEnabled(true);
                    mSettingSwitchItem.hideStatusText();
                    mSettingSwitchItem.setSubTitleText("");
                } else if (wifiState == WifiManager.WIFI_STATE_DISABLING) {
                    Log.d(TAG, "onReceive: WIFI_STATE_DISABLING");
                    mSettingSwitchItem.setSubTitleText(getResources().getString(R.string.wifi_closing));
                    mSettingSwitchItem.switchStateTo(false);
                    mSettingSwitchItem.setEnabled(false);
                } else if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                    if (!mSwitchingOn) {//每次打开wifi，收到的第一条WIFI_STATE_CHANGED_ACTION的广播时，wifi状态为WIFI_STATE_DISABLED
                        Log.d(TAG, "onReceive: WIFI_STATE_DISABLED");
                        mSettingSwitchItem.setEnabled(true);
                        mSettingSwitchItem.hideStatusText();
                        mSettingSwitchItem.setSubTitleText("");
                        mtvIpAddr.setText("IP: 0.0.0.0");

                        //反注册wifi广播接收器
                        //unregisterWifiReceiver();
                    }
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "onReceive: action=" + action);
                //get the network information   (NetworkInfo)
                final NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                Log.d(TAG, "onReceive: networkInfo.getState().name()=" + networkInfo.getDetailedState().name());
                mSettingSwitchItem.setSubTitleText(networkInfo.getDetailedState().name());
                if (networkInfo.getDetailedState().name().equals("CONNECTED")) {
                    mSettingSwitchItem.switchIconStateTo(true);
                }

                Message msg = mHandler.obtainMessage(MSG_SHOW_CONNECTED_WIFI, networkInfo);
                mHandler.removeMessages(MSG_SHOW_CONNECTED_WIFI);
                mHandler.sendMessageDelayed(msg, 100);
            }
        }
    };

    private String getStateString(DetailedState detailedState) {
        String stateString = "";
        if(detailedState == DetailedState.SCANNING){
            //have connected a network, show the text "正在扫描..."
            stateString = getString(R.string.wifi_scaning);
        }else if(detailedState == DetailedState.AUTHENTICATING){
            //have connected a network, show the text "正在进行身份验证..."
            stateString = getString(R.string.wifi_authenticating);
        }else if(detailedState == DetailedState.OBTAINING_IPADDR){
            //have connected a network, show the text "正在获取IP地址..."
            stateString = getString(R.string.wifi_obtaining_ipaddr);
        }else if(detailedState == DetailedState.DISCONNECTING){
            //have connected a network, show the text "正在断开连接..."
            stateString = getString(R.string.wifi_disconnecting);
        }else if(detailedState == DetailedState.DISCONNECTED){
            //have connected a network, show the text "已断开连接"
            stateString = getString(R.string.wifi_disconnected);
        }else if(detailedState == DetailedState.CONNECTING){
            //Is connecting a network, show the text "正在连接..."
            stateString = getString(R.string.wifi_connecting);
        }else if(detailedState == DetailedState.CONNECTED){
            //have connected a network, show the text "已连接"
            stateString = getString(R.string.wifi_connected);
        }
        return stateString;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        initViews(view);
    }

    private void initViews(View view) {
        //初始化mSettingSwitchItem
        mSettingSwitchItem = view.findViewById(R.id.switch_wifi);
        mSettingSwitchItem.registerSwitchCallback(this);
        boolean isWifiEnabled = mWifiManager.isWifiEnabled();
        mSettingSwitchItem.switchStateTo(isWifiEnabled);

        String ip = "IP: " + getIP();
        mtvIpAddr = view.findViewById(R.id.ip_addr);
        mtvIpAddr.setText(ip);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerWifiReceiver();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterWifiReceiver();
    }

    private boolean mSwitchingOn;
    @Override
    public void onSwitchOn() {
        Log.d(TAG, "onSwitchOn: ");
        mSwitchingOn = true;

        //如果wifi没有打开，那么打开wifi
        if (!mWifiManager.isWifiEnabled()) {
            //mSettingSwitchItem.showStatusText(getResources().getString(R.string.wifi_prepare_for_active));
            mSettingSwitchItem.setSubTitleText(getResources().getString(R.string.wifi_prepare_for_active));
            mSettingSwitchItem.setEnabled(false);
            mWifiManager.setWifiEnabled(true);
        }

        //注册wifi广播接收器
        //registerWifiReceiver();

        //扫描wifi前检查权限
        PermissionHelper permissionHelper = new PermissionHelper(getActivity());
        permissionHelper.setPermNeedToRequest(AUTH);
        if (!permissionHelper.checkMyPermissions()) {
            Log.d(TAG, "onSwitchOn: lack permission, now get permission......");
            permissionHelper.getMyPermissions(REQUEST_PERMISSIONS);
        } else {
            Log.d(TAG, "onSwitchOn: do nothing");
        }
    }

    @Override
    public void onSwitchOff() {
        Log.d(TAG, "onSwitchOff: ");
        mSwitchingOn = false;
        //关闭Wifi
        if (mWifiManager.isWifiEnabled()) {
            mSettingSwitchItem.setSubTitleText(getResources().getString(R.string.wifi_prepare_for_close));
            mSettingSwitchItem.setEnabled(false);
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 注册wifiReceiver
     */
    private void registerWifiReceiver() {
        IntentFilter filter = new IntentFilter();
        //filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(mWifiReceiver, filter);
    }

    /**
     * 反注册wifiReceiver
     */
    private void unregisterWifiReceiver() {
        getActivity().unregisterReceiver(mWifiReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_PERMISSIONS == requestCode) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: permission granted failed !");
                    return;
                }
            }
            Log.d(TAG, "onRequestPermissionsResult: permission granted success ! now do nothing");
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SHOW_CONNECTED_WIFI: {
                    NetworkInfo networkInfo = (NetworkInfo)msg.obj;

                    String ip = "IP: " + getIP();
                    mtvIpAddr.setText(ip);
                }
                    break;
                case MSG_NOTIFY_WIFI_DATA_CHANGED:
                    //mWifiAdapter.notifyDataSetChanged();
                default: {
                    //nothing to do
                }
                 break;
            }
        }
    };

    private String getIP() {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }

    private String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

}
