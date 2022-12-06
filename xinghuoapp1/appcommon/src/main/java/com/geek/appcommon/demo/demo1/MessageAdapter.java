package com.geek.appcommon.demo.demo1;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.geek.common.R;

/**
 * @author: lhw
 * @date: 2022/6/8
 * @desc
 */
public class MessageAdapter extends BaseQuickAdapter<DemoBean, BaseViewHolder> {

    public MessageAdapter() {
        super(R.layout.item_apps_message);
    }

    @Override
    protected void convert(BaseViewHolder helper, DemoBean item) {
        helper.setText(R.id.tv_title, item.getTitle())
                .setText(R.id.tv_time, item.getCreateTime())
                .setVisible(R.id.read, item.getStatus() == 0);
    }
}
