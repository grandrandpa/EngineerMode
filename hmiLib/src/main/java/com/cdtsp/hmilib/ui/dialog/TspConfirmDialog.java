package com.cdtsp.hmilib.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.cdtsp.hmilib.R;


public class TspConfirmDialog extends Dialog {
    private CharSequence mTitle, mMessage, mStrButton1, mStrButton2, mStrButton3;
    private View.OnClickListener mOnClickListener1, mOnClickListener2, mOnClickListener3;
    private TextView mTitleView, mMessageView;
    private Button mButton1, mButton2, mButton3;
    private int mCountBtn;

    public TspConfirmDialog(@NonNull Context context) {
        this(context, R.style.DialogTheme);
    }

    public TspConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mCountBtn == 3) {
            setContentView(R.layout.dialog_three_button);
        } else {
            setContentView(R.layout.dialog_two_button);
        }
        initView();
    }

    private void initView(){
        mTitleView = findViewById(R.id.title_view);
        mMessageView = findViewById(R.id.message_view);
        mButton1 = findViewById(R.id.button_1);
        mButton2 = findViewById(R.id.button_2);
        mButton3 = findViewById(R.id.button_3);

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
                    dismiss();
                    if(mOnClickListener1 != null){
                        mOnClickListener1.onClick(v);
                    }
                }
            });
        }

        if(!TextUtils.isEmpty(mStrButton2)){
            mButton2.setText(mStrButton2);
            mButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if(mOnClickListener2 != null){
                        mOnClickListener2.onClick(v);
                    }
                }
            });
        }

        if(!TextUtils.isEmpty(mStrButton3) && mButton3 != null){
            mButton3.setText(mStrButton3);
            mButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if(mOnClickListener3 != null){
                        mOnClickListener3.onClick(v);
                    }
                }
            });
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

    public TspConfirmDialog setMessage(@Nullable CharSequence message){
        mMessage = message;

        if(mMessageView != null){
            mMessageView.setText(message);
        }
        return this;
    }

    public TspConfirmDialog setMessage(int messageId){
        mMessage = getContext().getString(messageId);

        if(mMessageView != null){
            mMessageView.setText(mMessage);
        }
        return this;
    }

    public TspConfirmDialog setButton1(int strId){
        mStrButton1 = getContext().getString(strId);
        if(mButton1 != null){
            mButton1.setText(strId);
        }
        return this;
    }

    public TspConfirmDialog setButton1(int strId, View.OnClickListener listener){
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

    public TspConfirmDialog setButton2(int strId){
        mStrButton2 = getContext().getString(strId);
        if(mButton2 != null){
            mButton2.setText(strId);
        }
        return this;
    }

    public TspConfirmDialog setButton2(int strId, View.OnClickListener listener){
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

    public TspConfirmDialog setButton3(int strId){
        mStrButton3 = getContext().getString(strId);
        if(mButton3 != null){
            mButton3.setText(strId);
        }
        return this;
    }

    public TspConfirmDialog setButton3(int strId, View.OnClickListener listener){
        mStrButton3 = getContext().getString(strId);
        mOnClickListener3 = listener;
        if(mButton3 != null){
            mButton3.setText(strId);
            if(listener != null){
                mButton3.setOnClickListener(listener);
            }
        }
        return this;
    }

    public TspConfirmDialog setCountBtn(int countBtn) {
        this.mCountBtn = countBtn;
        return this;
    }
}
