package com.geek.appcommon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.libbase.R;
import com.geek.libbase.viewpager2.viewholder.ImageTitleHolder;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * 自定义布局，图片+标题
 */

public class MkFLunboAdapter extends BannerAdapter<BjyyBeanYewu3, ImageTitleHolder> {

    public MkFLunboAdapter(List<BjyyBeanYewu3> mDatas) {
        super(mDatas);
    }

    @Override
    public ImageTitleHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new ImageTitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lunbo_banner_image_title, parent, false));
    }

    @Override
    public void onBindView(ImageTitleHolder holder, BjyyBeanYewu3 data, int position, int size) {
        Glide.with(holder.itemView.getContext()).load(data.getImg()).placeholder(com.geek.libutils.R.drawable.default_unknow_image).into(holder.imageView);
        holder.title.setVisibility(View.GONE);
        holder.title.setText(data.getName());
    }

}
