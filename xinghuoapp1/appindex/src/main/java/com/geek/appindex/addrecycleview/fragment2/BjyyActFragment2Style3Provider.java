package com.geek.appindex.addrecycleview.fragment2;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.geek.appindex.R;
import com.geek.biz1.bean.BjyyActFragment251Bean;

/**
 * 党建引领样式
 */
public class BjyyActFragment2Style3Provider extends BaseItemProvider<BjyyActFragment251Bean, BaseViewHolder> {

    @Override
    public int viewType() {
        return BjyyActFragment251Adapter.STYLE_DJYL;
    }

    @Override
    public int layout() {
        return R.layout.activity_bjyyact_item4;
    }

    @Override
    public void convert(BaseViewHolder helper, BjyyActFragment251Bean data, int position) {
        TextView tvTitle = helper.itemView.findViewById(R.id.tv_title);
        tvTitle.setText(data.getmBean().getName());
        helper.addOnClickListener(R.id.tvTitle);
    }
}
