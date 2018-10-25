package com.cdtsp.hmilib.ui.dialog;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.widget.TextView;
import com.cdtsp.hmilib.R;

public class TspErrorDialog extends Presentation {
    private static final String TAG = "ToastPresentation";
    static final long SHORT_DURATION_TIMEOUT = 4000;
    static final long LONG_DURATION_TIMEOUT = 7000;
    private static final int MSG_AUTO_DISMISS = 0;
    private TextView mMessageView;
    private CharSequence mMessage;

    public TspErrorDialog(Context outerContext, Display display) {
        this(outerContext, display, R.style.DialogTheme);
    }

    public TspErrorDialog(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
        final Window w = getWindow();
        w.setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mHandler.hasMessages(MSG_AUTO_DISMISS)){
            mHandler.removeMessages(MSG_AUTO_DISMISS);
        }
    }

    @Override
    public void show() {
        super.show();
        mHandler.sendEmptyMessageDelayed(MSG_AUTO_DISMISS, SHORT_DURATION_TIMEOUT);
    }

    public TspErrorDialog setMessage(@Nullable CharSequence message){
        mMessage = message;

        if(mMessageView != null){
            mMessageView.setText(message);
        }
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_error);
        Log.d(TAG, "onCreate");
        mMessageView = findViewById(R.id.message_text);
        if(mMessage != null){
            mMessageView.setText(mMessage);
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_AUTO_DISMISS:
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    };
}
