package com.geek.appindex.news.adapter

import android.app.Activity
import android.graphics.Color
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.geek.appindex.R
import com.geek.appindex.news.model.ZXConvertData
import com.geek.appindex.news.model.ZXTYPE
import com.geek.biz1.bean.home.ClassificationBean
import com.just.agentweb.geek.hois3.HiosHelperNew
import com.youth.banner.Banner
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.util.BannerUtils

class ZXBannerProvider : BaseItemProvider<ZXConvertData, BaseViewHolder>() {

    override fun viewType(): Int = ZXTYPE.TYPE_BANNER

    override fun layout(): Int = R.layout.item_zx_banner

    override fun convert(helper: BaseViewHolder?, data: ZXConvertData?, position: Int) {
        helper?.apply {
            val banner = getView<Banner<ClassificationBean, MkFLunboAdapter1>>(R.id.banner)
            banner.setAdapter(MkFLunboAdapter1(data?.bannerData ?: mutableListOf()))
//        banner.setAdapter(new MultipleTypesAdapter(getActivity(), getTestData()));
            //        banner.setAdapter(new MultipleTypesAdapter(getActivity(), getTestData()));
            banner.indicator = CircleIndicator(banner.context)
            banner.setIndicatorGravity(IndicatorConfig.Direction.RIGHT)
            banner.setIndicatorNormalColor(Color.parseColor("#C3C3C3"))
            // banner.setIndicatorSelectedWidth(BannerUtils.dp2px(12f))
            banner.setIndicatorSpace(BannerUtils.dp2px(4f))
            banner.setIndicatorRadius(0)
            banner.setOnBannerListener { data, _ ->
                data.url?.takeIf {
                    it != "" && it != "null"
                }?.let { url ->
                    (helper.itemView.context as? Activity)?.apply {
                        HiosHelperNew.resolveAd(this, this, url)
                    }

                }
            }
        }
    }
}
