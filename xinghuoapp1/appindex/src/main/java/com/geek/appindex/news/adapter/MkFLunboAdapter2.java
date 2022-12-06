package com.geek.appindex.news.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.geek.appindex.R;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.biz1.bean.home.ClassificationBean;
import com.geek.libbase.viewpager2.viewholder.ImageTitleHolder;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class MkFLunboAdapter2 extends BannerAdapter<ClassificationBean, ImageTitleHolder> {

    public MkFLunboAdapter2(List<ClassificationBean> mDatas) {
        super(mDatas);
    }

    @Override
    public ImageTitleHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new ImageTitleHolder(
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(R.layout.lunbo_banner_image_title, parent, false)
        );
    }

    @Override
    public void onBindView(ImageTitleHolder holder, ClassificationBean data, int position, int size) {
        String img = data.getImg();
        if (img == null) {
            img = "";
        }
        Glide.with(holder.itemView.getContext()).load(img)
                .placeholder(R.drawable.default_unknow_image).into(holder.imageView);
        String title = data.getName();
        if (!TextUtils.isEmpty(title)) {
            holder.title.setText(title);
        }

    }
}
