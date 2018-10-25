package com.cdtsp.hmilib.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cdtsp.hmilib.R;

public class TspPasswordDialog extends Dialog {

    private CharSequence mTitle, mMessage, mStrButton1, mStrButton2;
    private View.OnClickListener mOnClickListener1, mOnClickListener2;
    private TextView mTitleView, mMessageView;
    private EditText mPasswordView;
    private Button mButton1, mButton2;
    private int mInputType = -1;
    private int mMaxLength = -1;

    public TspPasswordDialog(Context context) {
        super(context, R.style.DialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_password);
        initViews();
    }

    private void initViews() {

        mTitleView = findViewById(R.id.title_view);
        mMessageView = findViewById(R.id.message_view);
        mPasswordView = findViewById(R.id.password_view);
        mButton1 = findViewById(R.id.button_1);
        mButton2 = findViewById(R.id.button_2);

        if(!TextUtils.isEmpty(mTitle)){
            mTitleView.setText(mTitle);
        }
        if(!TextUtils.isEmpty(mMessage)){
            mMessageView.setText(mMessage);
        }

        if(!TextUtils.isEmpty(mStrButton1)){
            mButton1.setText(mStrButton1);
            mButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnClickListener1 != null){
                        mOnClickListener1.onClick(v);
                    } else {
                        dismiss();
                    }
                }
            });
        }

        if(!TextUtils.isEmpty(mStrButton2)){
            mButton2.setText(mStrButton2);
            mButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnClickListener2 != null){
                        mOnClickListener2.onClick(v);
                    } else {
                        dismiss();
                    }
                }
            });
        }

        if(mInputType != -1) {
            mPasswordView.setInputType(mInputType);
        }

        if(mMaxLength !=-1){
            InputFilter[] filters = {new InputFilter.LengthFilter(mMaxLength)};
            mPasswordView.setFilters(filters);
        }
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        mTitle = title;
        if(mTitleView != null){
            mTitleView.setText(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        mTitle = getContext().getString(titleId);
        if(mTitleView != null){
            mTitleView.setText(titleId);
        }
    }

    public TspPasswordDialog setMessage(@Nullable CharSequence message){
        mMessage = message;

        if(mMessageView != null){
            mMessageView.setText(message);
        }
        return this;
    }

    public TspPasswordDialog setMessage(int messageId){
        mMessage = getContext().getString(messageId);

        if(mMessageView != null){
            mMessageView.setText(mMessage);
        }
        return this;
    }

    public TspPasswordDialog setButton1(int strId){
        mStrButton1 = getContext().getString(strId);
        if(mButton1 != null){
            mButton1.setText(strId);
        }
        return this;
    }

    public TspPasswordDialog setButton1(int strId, View.OnClickListener listener){
        mStrButton1 = getContext().getString(strId);
        mOnClickListener1 = listener;
        if(mButton1 != null){
            mButton1.setText(strId);
            if(listener != null){
                mButton1.setOnClickListener(listener);
            }
        }
        return this;
    }

    public TspPasswordDialog setButton2(int strId){
        mStrButton2 = getContext().getString(strId);
        if(mButton2 != null){
            mButton2.setText(strId);
        }
        return this;
    }

    public TspPasswordDialog setButton2(int strId, View.OnClickListener listener){
        mStrButton2 = getContext().getString(strId);
        mOnClickListener2 = listener;
        if(mButton2 != null){
            mButton2.setText(strId);
            if(listener != null){
                mButton2.setOnClickListener(listener);
            }
        }
        return this;
    }

    public String getPassword() {
        return mPasswordView.getText().toString();
    }

    public TspPasswordDialog setInputType(int type) {
        mInputType = type;
        if(mPasswordView != null) {
            mPasswordView.setInputType(type);
        }
        return this;
    }

    public TspPasswordDialog setMaxLength(int length) {
        mMaxLength = length;
        if(mPasswordView != null) {
            InputFilter[] filters = {new InputFilter.LengthFilter(length)};
            mPasswordView.setFilters(filters);
        }
        return this;
    }
}
