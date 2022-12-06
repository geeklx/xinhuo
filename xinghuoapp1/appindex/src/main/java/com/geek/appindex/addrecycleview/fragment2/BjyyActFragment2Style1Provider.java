package com.geek.appindex.addrecycleview.fragment2;

import android.graphics.drawable.PictureDrawable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.geek.appindex.R;
import com.geek.biz1.bean.BjyyActFragment251Bean;

public class BjyyActFragment2Style1Provider extends BaseItemProvider<BjyyActFragment251Bean, BaseViewHolder> {

    @Override
    public int viewType() {
        return BjyyActFragment251Adapter.STYLE_ONE;
    }

    @Override
    public int layout() {
        return R.layout.activity_bjyyact_item2;
    }

    @Override
    public void convert(BaseViewHolder helper, BjyyActFragment251Bean data, int position) {
        TextView tv1 = helper.itemView.findViewById(R.id.tv1);
        TextView tv2 = helper.itemView.findViewById(R.id.tv2);
        tv1.setText(data.getmBean().getName());

        helper.addOnClickListener(R.id.tv1);
        helper.addOnClickListener(R.id.tv2);

    }

//    @Override
//    public void onClick(BaseViewHolder helper, BjyyActFragment251Bean data, int position) {
//        if (helper.getItemId() == helper.getView(R.id.tv).getId()) {
//            Toasty.normal(BaseApp.get(), position + "item click=" + data.getmBean().getUserName()).show();
//        } else if (helper.getItemId() == helper.getView(R.id.tv1).getId()) {
//            Toasty.normal(BaseApp.get(), position + "item click=" + data.getmBean().getText()).show();
//        } else {
//        }
//
//    }

//    @Override
//    public boolean onLongClick(BaseViewHolder helper, BjyyActFragment251Bean data, int position) {
//        Toast.makeText(mContext, "longClick", Toast.LENGTH_SHORT).show();
//        return true;
//    }
}
