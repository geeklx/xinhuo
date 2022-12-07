package com.spark.peak.ui._public.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.spark.peak.R
import com.spark.peak.bean.PdfImg

/**
 * Created by Zzz on 2020/12/26
 */

class PDFAdapter() : PagerAdapter() {
    private var context: Context? = null

    constructor(context: Context) : this() {
        this.context = context
    }

    var dataList = arrayListOf<PdfImg>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)
        Glide.with(context!!).load(dataList[position].img).error(R.drawable.default_book).into(imageView)
        container.addView(imageView)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

}