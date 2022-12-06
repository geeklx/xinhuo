package com.geek.appindex.news.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.geek.appindex.R;
import com.geek.appindex.news.model.ZXConvertData;
import com.geek.appindex.news.model.ZXTYPE;
import com.geek.biz1.bean.home.ClassificationBean;
import com.geek.libglide47.base.GlideImageView;
import com.geek.libglide47.base.ShapeImageView;

import org.w3c.dom.Text;

public class ZXItemProvider2 extends BaseItemProvider<ZXConvertData, BaseViewHolder> {
    private Boolean needDivider;

    public ZXItemProvider2(Boolean needDivider) {
        this.needDivider = needDivider;
    }

    @Override
    public int viewType() {
        return ZXTYPE.TYPE_ITEM;
    }

    @Override
    public int layout() {
        return R.layout.item_zx1;
    }

    @Override
    public void convert(BaseViewHolder helper, ZXConvertData data, int position) {
        if (helper != null) {
            TextView mTitleTextView = helper.getView(R.id.tv_title);
            TextView mWatchNumTextView = helper.getView(R.id.tv_watchNum);
            TextView mAmazingNumTextView = helper.getView(R.id.tv_amazingNum);
            GlideImageView mImageView = helper.getView(R.id.iv1);
            TextView mOriginTextView = helper.getView(R.id.tv_origin);
            TextView mPublishTimeTextView = helper.getView(R.id.tv_publishtime);
            View dividerView = helper.getView(R.id.viewDivider);
            // val mIvAmazing = getView<ImageView>(R.id.iv_amazing);
            mImageView.setShapeType(ShapeImageView.ShapeType.RECTANGLE);
            mImageView.setBorderWidth(0);
            mImageView.setRadius(4);
            ClassificationBean classificationBean = data.getData();
            if (classificationBean != null) {
                String img = classificationBean.getImg();
                if (TextUtils.isEmpty(img) && !img.equals("null")) {
                    mImageView.loadImage(img, android.R.color.darker_gray);
                } else {
                    mImageView.loadLocalImage(
                            android.R.color.darker_gray,
                            android.R.color.darker_gray
                    );
                }
                String name = classificationBean.getName();
                String viewNum = classificationBean.getViewNum();
                String voteNum = classificationBean.getVoteNum();
                String source = classificationBean.getSource();
                String pubDate = classificationBean.getPubDate();
                mTitleTextView.setText(name == null ? "" : name);
                mWatchNumTextView.setText(viewNum == null ? "" : viewNum);
                mAmazingNumTextView.setText(voteNum == null ? "" : voteNum);
                mOriginTextView.setText(source == null ? "" : source);
                mPublishTimeTextView.setText(pubDate == null ? "" : pubDate);
                dividerView.setVisibility(View.GONE);

            }

            if (needDivider) {
                if (helper.getLayoutPosition() == 0) {
                    dividerView.setVisibility(View.VISIBLE);
                } else {
                    dividerView.setVisibility(View.GONE);
                }
            }

            helper.addOnClickListener(R.id.iv1);

//                data?.takeIf { it.enable }?.apply {
//                    mIvAmazing.setImageResource(R.drawable.icon_amazing_checked)
//                } ?: mIvAmazing.setImageResource(R.drawable.icon_amazing)
//                helper.addOnClickListener(R.id.iv_amazing)
        }
    }
}

