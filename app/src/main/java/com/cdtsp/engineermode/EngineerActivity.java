package com.cdtsp.engineermode;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.cdtsp.engineermode.R;
import com.cdtsp.engineermode.fragment.FragmentAbout;
import com.cdtsp.engineermode.fragment.FragmentSet;
import com.cdtsp.engineermode.fragment.FragmentMenu;
import com.cdtsp.engineermode.fragment.FragmentNetwork;
import com.cdtsp.engineermode.fragment.FragmentMap;

import java.util.ArrayList;

public class EngineerActivity extends AppCompatActivity implements FragmentMenu.Callback {
    private static final String TAG = "EngineerActivity";
    private FragmentManager mFragmentManager;
    private ArrayList<Fragment> mContentFragments = new ArrayList<>();
    private int mCurSettingsPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer);

        mFragmentManager = getSupportFragmentManager();

        createContentFragments();
        initUI();
    }

    @Override
    public void onSwitchContent(int pos) {
        if (pos == mCurSettingsPos) return;

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment curFragment = mContentFragments.get(mCurSettingsPos);
        transaction.hide(curFragment);

        Fragment toFragment = mContentFragments.get(pos);
        String toFragmentTag = toFragment.getClass().getSimpleName();

        if (mFragmentManager.findFragmentByTag(toFragmentTag) == null) {
            transaction.add(R.id.container_content, toFragment, toFragmentTag);
        } else {
            transaction.show(toFragment);
        }
        transaction.commit();
        mCurSettingsPos = pos;
    }

    private void createContentFragments() {
        mContentFragments.add(new FragmentAbout());
        mContentFragments.add(new FragmentMap());
        mContentFragments.add(new FragmentSet());
        mContentFragments.add(new FragmentNetwork());
    }

    private void initUI() {
        initMenuUI();
        initContentUI();
    }

    private void initMenuUI() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        FragmentMenu fragmentMenu = new FragmentMenu();
        Fragment fragmentByTag = mFragmentManager.findFragmentByTag(FragmentMenu.class.getSimpleName());
        if (fragmentByTag != null) {
            transaction.replace(R.id.container_menu, fragmentMenu, FragmentMenu.class.getSimpleName());
        } else {
            transaction.add(R.id.container_menu, fragmentMenu, FragmentMenu.class.getSimpleName());
        }
        transaction.commit();
    }

    private void initContentUI() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment fragment = mContentFragments.get(0);
        Fragment fragmentByTag = mFragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());
        if (fragmentByTag != null) {
            transaction.replace(R.id.container_content, fragment, fragment.getClass().getSimpleName());
        } else {
            transaction.add(R.id.container_content, fragment, fragment.getClass().getSimpleName());
        }
        transaction.commit();
    }

    public void setDisplayBrightness(int brightness) {
        Log.d(TAG, "setDisplayBrightness value:" + brightness);
//        if (mCarInfoManager != null) {
//            try {
//                mCarInfoManager.setDisplayBrightness(VehicleDisplayType.DSI1, brightness);
//            } catch (CarNotConnectedException e) {
//                Log.d(TAG, "setDisplayBrightness: failed, " + e.getMessage());
//            }
//        }
    }

    public int getDisplayBrightness() {
//        if (mCarInfoManager != null) {
//            try {
//                return mCarInfoManager.getDisplayBrightness(VehicleDisplayType.DSI1);
//            } catch (CarNotConnectedException e) {
//                Log.d(TAG, "getDisplayBrightness: failed, " + e.getMessage());
//            }
//        }
        return 0;
    }
}
