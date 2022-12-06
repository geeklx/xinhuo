package com.geek.appindex.news.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.geek.appindex.R
import com.geek.biz1.bean.home.ClassificationBean
import com.geek.libbase.viewpager2.viewholder.ImageTitleHolder
import com.youth.banner.adapter.BannerAdapter

class MkFLunboAdapter1(datas: List<ClassificationBean>) :
    BannerAdapter<ClassificationBean, ImageTitleHolder>(datas) {
    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ImageTitleHolder {
        return ImageTitleHolder(
            LayoutInflater.from(
                parent!!.context
            ).inflate(R.layout.lunbo_banner_image_title, parent, false)
        )

    }

    override fun onBindView(
        holder: ImageTitleHolder,
        data: ClassificationBean?,
        position: Int,
        size: Int
    ) {
        Glide.with(holder.itemView.context).load(data?.img)
            .placeholder(R.drawable.default_unknow_image).into(holder.imageView)
        holder.title?.text = data?.name ?: ""
    }
}