package com.geek.appindexdt.adapter;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.geek.appindexdt.R;
import com.geek.appindexdt.beans.MkFCateBean;
import com.geek.libbase.widgets.LxRelativeLayout;
import com.geek.libglide47.base.GlideImageView;
import com.haier.cellarette.baselibrary.recycleviewalluses.demo4baseadpterhelp.second.bean.BaseRecActDemo42Bean;
import com.haier.cellarette.baselibrary.widget.LxLinearLayout;

public class MkF5Adapter extends BaseQuickAdapter<MkFCateBean, BaseViewHolder> {


    public MkF5Adapter() {
        super(R.layout.mkf5_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MkFCateBean item) {
        GlideImageView iv1 = helper.itemView.findViewById(R.id.iv1);
        TextView tv1 = helper.itemView.findViewById(R.id.tv1);
        LxLinearLayout rl1 = helper.itemView.findViewById(R.id.rl1);
        rl1.setTouch(false);
        iv1.loadImage(item.getUrl(), R.color.placeholder_color);
        tv1.setText(item.getText_content());
//        helper.addOnClickListener(R.id.rl1);
        helper.addOnClickListener(R.id.iv1);
        helper.addOnClickListener(R.id.tv1);
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showLong(item.getText_content());
            }
        });
    }

    //    @Override
//    public void onClick(BaseViewHolder helper, BaseRecActDemo42Bean data, int position) {
//        if (helper.getItemId() == helper.getView(R.id.tv).getId()) {
//            Toasty.normal(BaseApp.get(), position + "item click=" + data.getmBean().getUserName()).show();
//        } else if (helper.getItemId() == helper.getView(R.id.tv1).getId()) {
//            Toasty.normal(BaseApp.get(), position + "item click=" + data.getmBean().getText()).show();
//        } else {
//        }
//
//    }

//    @Override
//    public boolean onLongClick(BaseViewHolder helper, BaseRecActDemo42Bean data, int position) {
//        Toast.makeText(mContext, "longClick", Toast.LENGTH_SHORT).show();
//        return true;
//    }
}