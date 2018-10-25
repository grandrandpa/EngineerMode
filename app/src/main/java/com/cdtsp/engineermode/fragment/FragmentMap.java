package com.cdtsp.engineermode.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import com.cdtsp.engineermode.util.EngineeringMode;
import com.cdtsp.engineermode.util.PermissionHelper;

/**
 * Created by Administrator on 2018/1/18.
 */

public class FragmentMap extends Fragment implements View.OnClickListener {

    private static final String TAG = "FragmentMap";
    private TextView mTvMap;

    private final String[] AUTH = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSIONS = 4;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvMap = view.findViewById(R.id.map);
        mTvMap.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map:
                EngineerUtils.toast(getContext().getApplicationContext(), "Copying map data...");

                PermissionHelper permissionHelper = new PermissionHelper(getActivity());
                permissionHelper.setPermNeedToRequest(AUTH);
                if (!permissionHelper.checkMyPermissions()) {
                    Log.d(TAG, "Copy map data: lack permission, now get permission......");
                    permissionHelper.getMyPermissions(REQUEST_PERMISSIONS);
                } else {
                    Log.d(TAG, "Copy map data...");
                    EngineeringMode.getInstance(getContext()).processEngineerMode(4);
                }
                break;

            default:
                Log.d(TAG, "onClick: default");
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (REQUEST_PERMISSIONS == requestCode) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: read/write permission granted failed !");
                    return;
                }
            }
            Log.d(TAG, "onRequestPermissionsResult: read/write permission granted success ! now do nothing");
            EngineeringMode.getInstance(getContext()).processEngineerMode(4);
        }
    }
}
