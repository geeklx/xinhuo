package com.geek.appindex.fenlei;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.geek.appindex.R;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.libbase.widgets.LxRelativeLayout;
import com.geek.libglide47.base.GlideImageView;
import com.geek.libglide47.base.ShapeImageView;

public class ShouyeCateAdapter1 extends BaseQuickAdapter<BjyyBeanYewu3, BaseViewHolder> {

    public ShouyeCateAdapter1() {
        super(R.layout.shouyecate_item1);
    }

    @Override
    protected void convert(BaseViewHolder helper, BjyyBeanYewu3 item) {
        GlideImageView iv1 = helper.itemView.findViewById(R.id.iv1);
        TextView tv1 = helper.itemView.findViewById(R.id.tv1);
        LxRelativeLayout rl1 = helper.itemView.findViewById(R.id.rl1);
        rl1.setTouch(true);
        iv1.setShapeType(ShapeImageView.ShapeType.RECTANGLE);
        iv1.setBorderWidth(0);
        iv1.setRadius(4);
        iv1.setBorderColor(R.color.transparent20);
        iv1.loadImage(item.getImg(), R.drawable.icon_com_default1);
        tv1.setText(item.getName());
        helper.addOnClickListener(R.id.rl1);
    }
}