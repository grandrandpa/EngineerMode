package com.cdtsp.hmilib.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cdtsp.hmilib.R;
import com.cdtsp.hmilib.ui.adapter.DialogItemAdapter;

import java.util.ArrayList;
import java.util.Arrays;


public class TspListDialog extends Dialog {
    private CharSequence mTitle;
    private Dialog.OnClickListener mOnClickListener;
    private TextView mTitleView;
    private ListView mListView;
    private DialogItemAdapter adapter;
    private String[] mItems;

    public TspListDialog(@NonNull Context context) {
        this(context, R.style.DialogTheme);
    }

    public TspListDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_listview);
        initView();
    }

    private void initView(){
        mTitleView = findViewById(R.id.title_view);
        mListView = findViewById(R.id.dialog_list);

        if(!TextUtils.isEmpty(mTitle)){
            mTitleView.setText(mTitle);
        }
        adapter = new DialogItemAdapter(getContext(), Arrays.asList(mItems));
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long ids) {
                if(mOnClickListener != null){
                    mOnClickListener.onClick(TspListDialog.this, position);
                }
            }
        });
    }

    public TspListDialog setTitle(@Nullable String title) {
        mTitle = title;
        if(mTitleView != null){
            mTitleView.setText(title);
        }
        return this;
    }

    public TspListDialog setItems(String[] items, final OnClickListener listener) {
        this.mItems = items;
        this.mOnClickListener = listener;
        return this;
    }
}
