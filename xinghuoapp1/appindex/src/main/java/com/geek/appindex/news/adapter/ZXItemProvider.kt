package com.geek.appindex.news.adapter

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.geek.appindex.R
import com.geek.appindex.news.model.ZXConvertData
import com.geek.appindex.news.model.ZXTYPE
import com.geek.libglide47.base.GlideImageView
import com.geek.libglide47.base.ShapeImageView

class ZXItemProvider(private val needDivider: Boolean = false) :
    BaseItemProvider<ZXConvertData, BaseViewHolder>() {
    override fun viewType(): Int = ZXTYPE.TYPE_ITEM

    override fun layout(): Int = R.layout.item_zx1

    override fun convert(helper: BaseViewHolder?, data: ZXConvertData?, position: Int) {
        helper?.apply {
            val mTitleTextView = getView<TextView>(R.id.tv_title)
            val mWatchNumTextView = getView<TextView>(R.id.tv_watchNum)
            val mAmazingNumTextView = getView<TextView>(R.id.tv_amazingNum)
            val mImageView = getView<GlideImageView>(R.id.iv1)
            val mOriginTextView = getView<TextView>(R.id.tv_origin)
            val mPublishTimeTextView = getView<TextView>(R.id.tv_publishtime)
            val dividerView = getView<View>(R.id.viewDivider)
            // val mIvAmazing = getView<ImageView>(R.id.iv_amazing);
            mImageView.setShapeType(ShapeImageView.ShapeType.RECTANGLE)
            mImageView.setBorderWidth(0)
            mImageView.setRadius(4)
            data?.data?.apply {
                img.takeIf { !it.isNullOrBlank() && "null" != it }?.apply {
                    mImageView.loadImage(img, android.R.color.darker_gray)
                } ?: kotlin.run {
                    //政策文件img字段为null，等待后台接口更新
                    mImageView.loadLocalImage(
                        android.R.color.darker_gray,
                        android.R.color.darker_gray
                    )
                }

                mTitleTextView.text = name ?: ""
                mWatchNumTextView.text = viewNum
                mAmazingNumTextView.text = voteNum
                mOriginTextView.text = source ?: ""
                mPublishTimeTextView.text = pubDate ?: ""
                dividerView.visibility = View.GONE
                if (needDivider) {
                    layoutPosition.takeIf { it == 0 }?.apply {
                        dividerView.visibility = View.VISIBLE
                    } ?: kotlin.run {
                        dividerView.visibility = View.GONE
                    }
                }
                helper.addOnClickListener(R.id.iv1)

//                data?.takeIf { it.enable }?.apply {
//                    mIvAmazing.setImageResource(R.drawable.icon_amazing_checked)
//                } ?: mIvAmazing.setImageResource(R.drawable.icon_amazing)
//                helper.addOnClickListener(R.id.iv_amazing)
            }

        }

    }
}