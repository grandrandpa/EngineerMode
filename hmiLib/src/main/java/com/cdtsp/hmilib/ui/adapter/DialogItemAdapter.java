package com.cdtsp.hmilib.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cdtsp.hmilib.R;
import java.util.List;

public class DialogItemAdapter extends BaseAdapter {

    public List<String> datas;
    LayoutInflater inflater;

    public DialogItemAdapter(Context context, List<String> datas) {
        this.datas = datas;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public String getItem(int i) {
        if (i == getCount() || datas == null) {
            return null;
        }
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.dialog_listview_item, null);
            holder.typeTextview = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.typeTextview.setText(getItem(position));
        return convertView;
    }

    public static class ViewHolder {
        public TextView typeTextview;
    }
}