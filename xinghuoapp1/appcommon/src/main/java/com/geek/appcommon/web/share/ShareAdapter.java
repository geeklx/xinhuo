package com.geek.appcommon.web.share;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.geek.common.R;

import java.util.List;

/**
 * @author: lhw
 * @date: 2021/12/30
 * @desc
 */
public class ShareAdapter extends BaseQuickAdapter<ShareBean, BaseViewHolder> {

    public ShareAdapter(@Nullable List<ShareBean> data) {
        super(R.layout.item_share, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShareBean item) {
        helper.setText(R.id.tv_name, item.name)
                .setImageResource(R.id.icon, item.icon);
    }
}
