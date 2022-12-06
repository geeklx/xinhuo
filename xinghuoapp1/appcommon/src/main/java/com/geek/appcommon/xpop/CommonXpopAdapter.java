package com.geek.appcommon.xpop;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.geek.common.R;

import java.util.List;

public class CommonXpopAdapter<T extends BaseShowBean> extends BaseQuickAdapter<T, BaseViewHolder> {


    public CommonXpopAdapter(@Nullable List<T> data) {
        super(R.layout.item_common_xpop, data);
    }

    public CommonXpopAdapter(@Nullable List<T> data, int layout) {
        super(layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        TextView tvName = helper.getView(R.id.tv_name);
        View divider = helper.getView(R.id.divider);
        tvName.setText(item.getShwoContent());
        if (helper.getLayoutPosition() == getData().size() - 1) {
            divider.setVisibility(View.INVISIBLE);
        } else {
            divider.setVisibility(View.VISIBLE);
        }

    }
}
