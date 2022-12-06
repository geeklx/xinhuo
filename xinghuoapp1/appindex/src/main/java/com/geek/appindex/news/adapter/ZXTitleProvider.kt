package com.geek.appindex.news.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.geek.appindex.news.model.ZXConvertData
import com.geek.appindex.news.model.ZXTYPE
import com.geek.appindex.R

class ZXTitleProvider : BaseItemProvider<ZXConvertData, BaseViewHolder>() {
    override fun viewType(): Int = ZXTYPE.TYPE_TITLE

    override fun layout(): Int = R.layout.item_zx_title

    override fun convert(helper: BaseViewHolder?, data: ZXConvertData?, position: Int) {
        helper?.apply {
            val mTitleTextView = helper.getView<TextView>(R.id.tv_title)
            mTitleTextView.text = data?.name ?: ""
        }
    }
}