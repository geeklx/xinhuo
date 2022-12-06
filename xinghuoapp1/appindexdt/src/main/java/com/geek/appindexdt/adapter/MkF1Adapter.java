package com.geek.appindexdt.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.geek.appindexdt.R;
import com.geek.appindexdt.beans.MkFCateBean;
import com.geek.libbase.widgets.LxRelativeLayout;

public class MkF1Adapter extends BaseQuickAdapter<MkFCateBean, BaseViewHolder> {


    public MkF1Adapter() {
        super(R.layout.mkf1_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MkFCateBean item) {
        TextView tv1 = helper.itemView.findViewById(R.id.tv1);
        View view1 = helper.itemView.findViewById(R.id.view1);
        LxRelativeLayout rl1 = helper.itemView.findViewById(R.id.rl1);
        rl1.setTouch(false);
        tv1.setText(item.getText_content());
        if (item.isEnselect()) {
            //选中
            tv1.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            view1.setVisibility(View.VISIBLE);
        } else {
            //未选中
            tv1.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            view1.setVisibility(View.INVISIBLE);
        }
//        helper.addOnClickListener(R.id.rl1);
        helper.addOnClickListener(R.id.tv1);
    }
}