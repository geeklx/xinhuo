package com.geek.appindex.addrecycleview.fragment1;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.geek.appindex.R;
import com.geek.biz1.bean.BjyyActFragment251Bean;

public class BjyyAdapter1 extends BaseQuickAdapter<BjyyActFragment251Bean, BaseViewHolder> {

    public BjyyAdapter1() {
        super(R.layout.activity_bjyyact_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, BjyyActFragment251Bean item) {
        ImageView iv1 = helper.itemView.findViewById(R.id.iv1);
        TextView tv1 = helper.itemView.findViewById(R.id.tv1);
        TextView tv2 = helper.itemView.findViewById(R.id.tv2);
        tv2.setBackgroundResource(R.drawable.icon_jianjian1);
        Glide.with(helper.itemView).load(item.getmBean().getImg()).into(iv1);
        tv1.setText(item.getmBean().getName());
//        if (!item.getmBean().isEnable()) {
//            tv2.setVisibility(View.VISIBLE);
//            helper.addOnClickListener(R.id.tv2);
//        } else {
//            tv2.setVisibility(View.GONE);
//            helper.addOnClickListener(R.id.iv1);
//        }
        tv2.setVisibility(View.VISIBLE);
        helper.addOnClickListener(R.id.tv2);
        helper.addOnClickListener(R.id.iv1);
    }
}