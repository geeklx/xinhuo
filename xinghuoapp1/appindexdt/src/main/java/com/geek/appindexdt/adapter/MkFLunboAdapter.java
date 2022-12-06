package com.geek.appindexdt.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.geek.libbase.R;
import com.geek.libbase.viewpager2.bean.DataBean;
import com.geek.libbase.viewpager2.viewholder.ImageTitleHolder;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * 自定义布局，图片+标题
 */

public class MkFLunboAdapter extends BannerAdapter<DataBean, ImageTitleHolder> {

    public MkFLunboAdapter(List<DataBean> mDatas) {
        super(mDatas);
    }

    @Override
    public ImageTitleHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new ImageTitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lunbo_banner_image_title, parent, false));
    }

    @Override
    public void onBindView(ImageTitleHolder holder, DataBean data, int position, int size) {
        Glide.with(holder.itemView.getContext()).load(data.imageUrl).placeholder(com.geek.appindexdt.R.drawable.default_unknow_image).into(holder.imageView);
        holder.title.setText(data.title);
    }

}
