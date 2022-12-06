package com.geek.appindex.addrecycleview.fragment2;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.geek.appindex.R;
import com.geek.biz1.bean.BjyyActFragment251Bean;

public class BjyyActFragment2Style1Provider1 extends BaseItemProvider<BjyyActFragment251Bean, BaseViewHolder> {

    @Override
    public int viewType() {
        return BjyyActFragment251Adapter.STYLE_ONE11;
    }

    @Override
    public int layout() {
        return R.layout.activity_bjyyact_item3;
    }

    @Override
    public void convert(BaseViewHolder helper, BjyyActFragment251Bean data, int position) {

    }

}
