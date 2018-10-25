package com.cdtsp.engineermode.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Administrator on 2018/2/27.
 */

public class EngineerUtils {
    public static Toast sToast;
    public static void toast(Context context, String text) {
        if (sToast == null) {
            sToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }

        LinearLayout linearLayout = (LinearLayout) sToast.getView();
        TextView messageTextView = (TextView) linearLayout.getChildAt(0);
        messageTextView.setTextSize(25);

        sToast.setText(text);
        sToast.show();
    }

    /**
     * 判断ScanResult是否加密
     * @param result
     * @return
     */
    public static boolean isLocked(ScanResult result) {
        return result.capabilities.contains("WEP")
                || result.capabilities.contains("PSK")
                || result.capabilities.contains("EAP");
    }

}
