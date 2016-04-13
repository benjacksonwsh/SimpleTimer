package com.demo.simpletimer.simpletimerdemo;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by wangshuhe on 2016/4/13.
 */
public class StringAdapter extends BaseAdapter{
    private ArrayList<String> mLogList = new ArrayList<>();
    private int mIndex = 0;
    Context  mContext = null;

    public StringAdapter( Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return mLogList.size();
    }

    @Override
    public Object getItem(int position) {
        return mLogList.get(mLogList.size() - position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) convertView;
        if ( null == v ){
            v = new TextView(mContext);
            v.setEllipsize(TextUtils.TruncateAt.END);
            v.setSingleLine();
        }
        v.setText((String)getItem(position));
        return v;
    }

    public void appLog(String log){
        mLogList.add(mIndex + "." + log);
        ++mIndex;
        notifyDataSetChanged();
    }

    public void clear() {
        mIndex = 0;
        mLogList.clear();
        notifyDataSetChanged();
    }
}
