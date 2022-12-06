package com.geek.appindex.index;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appindex.R;
import com.geek.biz1.bean.BjyyBeanYewu3;

public class ShouyeFragmentAdapter extends BaseQuickAdapter<BjyyBeanYewu3, BaseViewHolder> {

    public ShouyeFragmentAdapter() {
        super(R.layout.activity_shouyef_footer_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, BjyyBeanYewu3 item) {
//        LinearLayout ll1 = helper.itemView.findViewById(R.id.ll1);
        ImageView iv_imgurl = helper.itemView.findViewById(R.id.iv_imgurl);
        TextView tv_content1 = helper.itemView.findViewById(R.id.tv_content1);
        if (TextUtils.isEmpty(item.getName())) {
            tv_content1.setVisibility(View.GONE);
        } else {
            tv_content1.setVisibility(View.VISIBLE);
        }
        tv_content1.setText(item.getName());
//        AppCommonUtils.addSeletorFromNet(ratings.getImg_press(), ratings.getImg_normal(), viewHolder.iv_imgurl);
        if (item.isEnable()) {
            //选中
            iv_imgurl.setPressed(true);
            AppCommonUtils.addSeletorFromNet(item.getImg_press(), item.getImg_normal(), iv_imgurl);
            tv_content1.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        } else {
            //未选中
            iv_imgurl.setPressed(false);
            AppCommonUtils.addSeletorFromNet(item.getImg_normal(), item.getImg_press(), iv_imgurl);
            tv_content1.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }
        helper.addOnClickListener(R.id.iv1);
    }


}