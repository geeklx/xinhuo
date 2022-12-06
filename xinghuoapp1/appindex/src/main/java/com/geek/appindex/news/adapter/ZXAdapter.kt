package com.geek.appindex.news.adapter

import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.MultipleItemRvAdapter
import com.geek.appindex.news.model.ZXConvertData
import com.geek.appindex.news.model.ZXTYPE

class ZXAdapter(data: List<ZXConvertData>, private val needDivider: Boolean = false) :
    MultipleItemRvAdapter<ZXConvertData, BaseViewHolder>(data) {

    private lateinit var mItemProvider: ZXItemProvider
    private lateinit var mTitleProvider: ZXTitleProvider
    private lateinit var mBannerProvider: ZXBannerProvider

    init {
        finishInitialize()
    }

    override fun getViewType(data: ZXConvertData?): Int {
        return data?.type ?: kotlin.run {
            return ZXTYPE.TYPE_ITEM
        }
    }

    override fun registerItemProvider() {
        mItemProvider = ZXItemProvider(needDivider)
        mTitleProvider = ZXTitleProvider()
        mBannerProvider = ZXBannerProvider()
        mProviderDelegate.registerProvider(mItemProvider)
        mProviderDelegate.registerProvider(mTitleProvider)
        mProviderDelegate.registerProvider(mBannerProvider)
    }

}