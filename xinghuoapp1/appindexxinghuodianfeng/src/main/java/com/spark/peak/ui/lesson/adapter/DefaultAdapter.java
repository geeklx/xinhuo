package com.spark.peak.ui.lesson.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.spark.peak.utlis.DeviceUtil;

import java.util.List;

/**
 * 创建者： 霍述雷
 * 时间：  2018/6/11.
 */
public class DefaultAdapter extends BaseAdapter {

    public int selectIndex = 0;

    public DefaultAdapter(List<String> data) {
        this.data = data;
    }
    private SpinnerItemClickListener listener;


    public void setListener(SpinnerItemClickListener listener) {
        this.listener = listener;
    }

    private List<String> data;
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        TextView view = new TextView(context);
        if (selectIndex == position){
            view.setTextColor(Color.parseColor("#1482ff"));
        }else {
            view.setTextColor(Color.parseColor("#999999"));
        }
        view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int h = (int) DeviceUtil.dp2px(context, 10);
        int w = (int) DeviceUtil.dp2px(context, 20);
        view.setPadding(w, h, w, h);
        view.setGravity(Gravity.CENTER);
//        view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        view.setText(data.get(position));
        view.setOnClickListener(v -> listener.OnItemClick(position,data.get(position)));
        return view;
    }
    public interface SpinnerItemClickListener {
        void OnItemClick(int position ,String id);
    }
}
