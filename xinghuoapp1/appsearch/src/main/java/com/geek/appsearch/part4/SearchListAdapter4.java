package com.geek.appsearch.part4;

import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.geek.appsearch.R;
import com.geek.biz1.bean.SNewListBean1;
import com.geek.libglide47.base.GlideImageView;

public class SearchListAdapter4 extends BaseQuickAdapter<SNewListBean1.ListBean, BaseViewHolder> {

    public SearchListAdapter4(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SNewListBean1.ListBean item) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(!TextUtils.isEmpty(item.getTitle())){
                helper.setText(R.id.tv1, Html.fromHtml(item.getTitle(), Html.FROM_HTML_MODE_LEGACY));
            }
        } else {
            if(!TextUtils.isEmpty(item.getTitle())){
                helper.setText(R.id.tv1, Html.fromHtml(item.getTitle()));
            }
        }
        GlideImageView iv_bktt1 = helper.itemView.findViewById(R.id.iv1);
        helper.addOnClickListener(R.id.iv1);
    }

}
