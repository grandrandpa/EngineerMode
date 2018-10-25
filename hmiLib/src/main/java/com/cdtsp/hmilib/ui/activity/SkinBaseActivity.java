package com.cdtsp.hmilib.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.cdtsp.hmilib.R;

import org.qcode.qskinloader.IActivitySkinEventHandler;
import org.qcode.qskinloader.ISkinActivity;
import org.qcode.qskinloader.SkinManager;

public abstract class SkinBaseActivity extends FragmentActivity implements ISkinActivity {
    private IActivitySkinEventHandler mSkinEventHandler;
    private boolean mFirstTimeApplySkin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSkinEventHandler = SkinManager.newActivitySkinEventHandler()
                .setSwitchSkinImmediately(isSwitchSkinImmediately())
                .setSupportSkinChange(isSupportSkinChange())
                .setWindowBackgroundResource(getWindowBackgroundResource())
                .setNeedDelegateViewCreate(true);
        mSkinEventHandler.onCreate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSkinEventHandler.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //皮肤相关，此通知放在此处，尽量让子类的view都添加到view树内
        if (mFirstTimeApplySkin) {
            mSkinEventHandler.onViewCreated();
            mFirstTimeApplySkin = false;
        }

        mSkinEventHandler.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //皮肤相关
        mSkinEventHandler.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSkinEventHandler.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSkinEventHandler.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //皮肤相关
        mSkinEventHandler.onDestroy();
    }

    @Override
    public boolean isSupportSkinChange() {
        //告知当前界面是否支持换肤：true支持换肤，false不支持
        return true;
    }

    @Override
    public boolean isSwitchSkinImmediately() {
        //告知当切换皮肤时，是否立刻刷新当前界面；true立刻刷新，false表示在界面onResume时刷新；
        //减轻换肤时性能压力
        return true;
    }

    @Override
    public void handleSkinChange() {
        //当前界面在换肤时收到的回调，可以在此回调内做一些其他事情；
        //比如：通知WebView内的页面切换到夜间模式等
    }

    /**
     * 告知当前界面Window的background资源，换肤时会寻找对应的资源替换
     */
    protected int getWindowBackgroundResource() {
        return 0;
    }
}
