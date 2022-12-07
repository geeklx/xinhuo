package com.spark.peak.ui.study.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.SignListItem
import com.spark.peak.utlis.GradUtil
import org.jetbrains.anko.*

/**
 * Created by 李昊 on 2018/4/27.
 */
class SignInListAdapter(var onLoadMore: () -> Unit) : BaseAdapter<SignListItem>(isMore = true) {
    override fun convert(holder: ViewHolder, item: SignListItem) {
        when (holder.adapterPosition) {
            0 -> {
                holder.setBackgroundResource(R.id.iv_sign_in_index, R.drawable.sign_in_no1)
                    .setVisible(R.id.iv_sign_in_index, View.VISIBLE)
                    .setVisible(R.id.tv_sign_in_index, View.GONE)
            }
            1 -> {
                holder.setBackgroundResource(R.id.iv_sign_in_index, R.drawable.sign_in_no2)
                    .setVisible(R.id.iv_sign_in_index, View.VISIBLE)
                    .setVisible(R.id.tv_sign_in_index, View.GONE)
            }
            2 -> {
                holder.setBackgroundResource(R.id.iv_sign_in_index, R.drawable.sign_in_no3)
                    .setVisible(R.id.iv_sign_in_index, View.VISIBLE)
                    .setVisible(R.id.tv_sign_in_index, View.GONE)
            }
            else -> {
                holder.setText(R.id.tv_sign_in_index, (holder.adapterPosition + 1).toString())
                    .setVisible(R.id.tv_sign_in_index, View.VISIBLE)
                    .setVisible(R.id.iv_sign_in_index, View.GONE)
            }
        }

        holder.setText(R.id.tv_sign_in_user_name, item.name).setText(
                R.id.tv_sign_in_user_grade, GradUtil.parseGradStr(item.sectionname, item.gradename)
            ).setText(R.id.tv_sign_in_count, item.keep.toString())

        var header = holder.getView(R.id.iv_sign_in_header) as ImageView
        Glide.with(holder.itemView.context).asBitmap().load(item.img)
            .placeholder(R.mipmap.ic_avatar).error(R.mipmap.ic_avatar).centerCrop()
            .into(object : BitmapImageViewTarget(header) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(view.context.resources, resource)
                    circularBitmapDrawable.isCircular = true
                    view.setImageDrawable(circularBitmapDrawable)
                }
            })
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        relativeLayout {
            lparams(matchParent, dip(61))
            leftPadding = dip(15)
            imageView {
                id = R.id.iv_sign_in_index
                scaleType = ImageView.ScaleType.FIT_XY
            }.lparams(dip(37), dip(37)) {
                centerVertically()
            }

            textView {
                id = R.id.tv_sign_in_index
                textSize = 18f
                textColor = R.color.color_1e1e1e
                gravity = Gravity.CENTER
            }.lparams(dip(37), dip(37)) {
                centerVertically()
            }

            imageView {
                id = R.id.iv_sign_in_header
                scaleType = ImageView.ScaleType.FIT_XY
                imageResource = R.mipmap.ic_avatar
            }.lparams(dip(37), dip(37)) {
                centerVertically()
                leftMargin = dip(47)
            }

            textView {
                id = R.id.tv_sign_in_user_name
                textSize = 15f
                textColor = R.color.color_1e1e1e
            }.lparams(wrapContent, wrapContent) {
                centerVertically()
                leftMargin = dip(10)
                rightOf(R.id.iv_sign_in_header)
            }

            textView {
                id = R.id.tv_sign_in_user_grade
                textSize = 10f
                textColor = Color.parseColor("#ffffff")
                backgroundResource = R.drawable.bg_grade
                leftPadding = dip(5)
                rightPadding = dip(5)
            }.lparams(wrapContent, wrapContent) {
                centerVertically()
                leftMargin = dip(10)
                rightOf(R.id.tv_sign_in_user_name)
            }

            textView {
                id = R.id.tv_sign_in_count
                textSize = 18f
                textColor = Color.parseColor("#ffb414")
            }.lparams(wrapContent, wrapContent) {
                alignParentRight()
                centerVertically()
                rightMargin = dip(15)
            }

            imageView {
                backgroundColor = Color.parseColor("#e6e6e6")
            }.lparams(matchParent, 1) {
                alignParentBottom()
                centerVertically()
            }
        }
    }

    override fun loadMore(holder: ViewHolder) {
        super.loadMore(holder)
        onLoadMore()
    }
}