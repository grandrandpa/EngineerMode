package com.cdtsp.hmilib.skin;

import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;
import org.qcode.qskinloader.ILoadSkinListener;
import org.qcode.qskinloader.SkinManager;
import org.qcode.qskinloader.resourceloader.impl.ConfigChangeResourceLoader;
import org.qcode.qskinloader.resourceloader.impl.SuffixResourceLoader;
import java.io.File;
import com.cdtsp.hmilib.util.UITaskRunner;

public class SkinChangeHelper {
    private static final String TAG = "SkinChangeHelper";
    //基于suffix换肤
    private static final int TYPE_SUFFIX = 1;
    //基于apk换肤
    private static final int TYPE_APK = 2;
    //基于UIMode换肤
    private static final int TYPE_UIMODE = 3;

    private static volatile SkinChangeHelper mInstance;
    private Context mContext;

    //目前框架支持三种换肤方式，后缀换肤/APK资源包换肤/UIMode换肤
    private int mSkinChangeType = TYPE_SUFFIX;

    private String mCurrentTheme;


    private SkinChangeHelper(Context context) {
        mContext = context.getApplicationContext();
        mCurrentTheme = getSkinIdentifier();
    }

    public static SkinChangeHelper getInstance(Context context) {
        if (null == mInstance) {
            synchronized (SkinChangeHelper.class) {
                if (null == mInstance) {
                    mInstance = new SkinChangeHelper(context);
                }
            }
        }
        return mInstance;
    }

//    private volatile boolean mIsDefaultMode = false;

    private volatile boolean mIsSwitching = false;

    public void init(Context context) {
        SkinManager.getInstance().init(context);
    }

    public void switchSkinMode(OnSkinChangeListener listener) {
        mIsSwitching = true;
        mCurrentTheme = getSkinIdentifier();
        refreshSkin(listener);
    }

    public void refreshSkin(OnSkinChangeListener listener) {
        if (isDefaultMode()) {
            switch (mSkinChangeType) {
                case TYPE_SUFFIX:
                case TYPE_APK:
                    //恢复到默认皮肤
                    SkinManager.getInstance().restoreDefault(
                            SkinConstant.DEFAULT_SKIN,
                            new MyLoadSkinListener(listener));
                    break;

                case TYPE_UIMODE:
                    //基于UIMode换肤只能通过改回配置才能换肤
                    changeSkinByConfig(ConfigChangeResourceLoader.MODE_DAY, listener);
                    break;
            }

        } else {
            switch (mSkinChangeType) {
                case TYPE_SUFFIX:
                    changeSkinBySuffix(mCurrentTheme, listener);
                    break;

                case TYPE_APK:
                    changeSkinByApk(listener);
                    break;

                case TYPE_UIMODE:
                    //基于UIMode换肤只能通过改回配置才能换肤
                    changeSkinByConfig(ConfigChangeResourceLoader.MODE_NIGHT, listener);
                    break;
            }
        }
    }

    public boolean isDefaultMode() {
        return mCurrentTheme.equals(Settings.Global.THEME_DEFAULT);
    }

    public boolean isSwitching() {
        return mIsSwitching;
    }

    private void changeSkinByApk(OnSkinChangeListener listener) {
        SkinUtils.copyAssetSkin(mContext);

        File skin = new File(
                SkinUtils.getTotalSkinPath(mContext));

        if (skin == null || !skin.exists()) {
            Toast.makeText(mContext, "皮肤初始化失败", Toast.LENGTH_LONG).show();
            return;
        }

        SkinManager.getInstance().loadAPKSkin(
                skin.getAbsolutePath(), new MyLoadSkinListener(listener));
    }

    private void changeSkinBySuffix(String theme, OnSkinChangeListener listener) {
                SkinManager.getInstance().loadSkin(theme,
                new SuffixResourceLoader(mContext),
                new MyLoadSkinListener(listener));
    }

    private void changeSkinByConfig(String mode, OnSkinChangeListener listener) {
        SkinManager.getInstance().loadSkin(mode,
                new ConfigChangeResourceLoader(mContext),
                new MyLoadSkinListener(listener));
    }

    /***
     * 获取当前皮肤包的标识
     */
    public String getSkinIdentifier() {
        return Settings.Global.getString(mContext.getContentResolver(), Settings.Global.SYSTEM_THEME);
    }

    private class MyLoadSkinListener implements ILoadSkinListener {

        private final OnSkinChangeListener mListener;

        public MyLoadSkinListener(OnSkinChangeListener listener) {
            mListener = listener;
        }

        @Override
        public void onLoadStart(String skinIdentifier) {
        }

        @Override
        public void onLoadSuccess(String skinIdentifier) {
            mIsSwitching = false;

            UITaskRunner.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if(null != mListener) {
                        mListener.onSuccess();
                    }
                }
            });
        }

        @Override
        public void onLoadFail(String skinIdentifier) {
            mIsSwitching = false;

            UITaskRunner.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (null != mListener) {
                        mListener.onError();
                    }
                }
            });
        }
    };

    public interface OnSkinChangeListener {
        void onSuccess();

        void onError();
    }
}
