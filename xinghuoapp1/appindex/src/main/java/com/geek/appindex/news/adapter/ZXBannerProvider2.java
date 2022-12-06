package com.geek.appindex.news.adapter;

import android.graphics.Color;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.geek.appindex.R;
import com.geek.appindex.news.model.ZXConvertData;
import com.geek.appindex.news.model.ZXTYPE;
import com.geek.biz1.bean.home.ClassificationBean;
import com.youth.banner.Banner;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.util.BannerUtils;

public class ZXBannerProvider2 extends BaseItemProvider<ZXConvertData, BaseViewHolder> {
    @Override
    public int viewType() {
        return ZXTYPE.TYPE_BANNER;
    }

    @Override
    public int layout() {
        return R.layout.item_zx_banner;
    }

    @Override
    public void convert(BaseViewHolder helper, ZXConvertData data, int position) {
        if (helper != null) {
            Banner<ClassificationBean, MkFLunboAdapter1> banner = helper.getView(R.id.banner);

            banner.setAdapter(new MkFLunboAdapter1(data.getBannerData()));
//        banner.setAdapter(new MultipleTypesAdapter(getActivity(), getTestData()));
            //        banner.setAdapter(new MultipleTypesAdapter(getActivity(), getTestData()));
            banner.setIndicator(new CircleIndicator(banner.getContext()));
            banner.setIndicatorGravity(IndicatorConfig.Direction.RIGHT);
            banner.setIndicatorNormalColor(Color.parseColor("#C3C3C3"));
            // banner.setIndicatorSelectedWidth(BannerUtils.dp2px(12f))
            banner.setIndicatorSpace(BannerUtils.dp2px(4f));
            banner.setIndicatorRadius(0);
            banner.setOnBannerListener(new OnBannerListener<ClassificationBean>() {
                @Override
                public void OnBannerClick(ClassificationBean data, int position) {
                    ToastUtils.showShort("当前点击的是" + position);
                }
            });
        }

    }
}
