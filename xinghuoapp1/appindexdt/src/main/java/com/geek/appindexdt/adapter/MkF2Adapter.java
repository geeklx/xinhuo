package com.geek.appindexdt.adapter;

import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.geek.appindexdt.R;
import com.geek.appindexdt.beans.MkFCateBean;
import com.geek.libbase.widgets.LxRelativeLayout;

public class MkF2Adapter extends BaseQuickAdapter<MkFCateBean, BaseViewHolder> {


    public MkF2Adapter() {
        super(R.layout.mkf2_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MkFCateBean item) {
        TextView tv1 = helper.itemView.findViewById(R.id.tv1);
        LxRelativeLayout rl1 = helper.itemView.findViewById(R.id.rl1);
        rl1.setTouch(false);
        tv1.setText(item.getText_content());
        if (item.isEnselect()) {
            //选中
            tv1.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            tv1.setBackgroundResource(R.drawable.mkf2_shape2);
        } else {
            //未选中
            tv1.setTextColor(ContextCompat.getColor(mContext, R.color.grayD4CACB));
            tv1.setBackgroundResource(R.drawable.mkf2_shape3);
        }
//        helper.addOnClickListener(R.id.rl1);
        helper.addOnClickListener(R.id.tv1);
    }
}