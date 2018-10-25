package com.cdtsp.hmilib.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cdtsp.hmilib.R;


public class TspWaitDialog extends Dialog {
    private CharSequence mMessage;
    private TextView mMessageView;

    public TspWaitDialog(@NonNull Context context) {
        this(context, R.style.DialogTheme);
    }

    public TspWaitDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wait);
        initView();
    }

    private void initView(){
        mMessageView = findViewById(R.id.message_view);
        if(!TextUtils.isEmpty(mMessage)){
            mMessageView.setText(mMessage);
        }
    }

    public TspWaitDialog setMessage(@Nullable CharSequence message){
        mMessage = message;

        if(mMessageView != null){
            mMessageView.setText(message);
        }
        return this;
    }

    public TspWaitDialog setMessage(int messageId){
        mMessage = getContext().getString(messageId);

        if(mMessageView != null){
            mMessageView.setText(mMessage);
        }
        return this;
    }
}
