package com.geek.appindex.news.adapter;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.MultipleItemRvAdapter;
import com.geek.appindex.news.model.ZXConvertData;
import com.geek.appindex.news.model.ZXTYPE;

import java.util.List;

public class ZXAdapter2 extends MultipleItemRvAdapter<ZXConvertData, BaseViewHolder> {

    private Boolean needDivider;
    private ZXItemProvider mItemProvider;
    private ZXTitleProvider mTitleProvider;
    private ZXBannerProvider mBannerProvider;

    public ZXAdapter2(List<ZXConvertData> data, Boolean needDivider) {
        super(data);
        this.needDivider = needDivider;
        //构造函数若有传其他参数可以在调用finishInitialize()之前进行赋值，赋值给全局变量
        //这样getViewType()和registerItemProvider()方法中可以获取到传过来的值
        //getViewType()中可能因为某些业务逻辑，需要将某个值传递过来进行判断，返回对应的viewType
        //registerItemProvider()中可以将值传递给ItemProvider
        finishInitialize();
    }

    @Override
    protected int getViewType(ZXConvertData zxConvertData) {
        return zxConvertData.getType();
    }

    @Override
    public void registerItemProvider() {
        mItemProvider = new ZXItemProvider(needDivider);
        mTitleProvider = new ZXTitleProvider();
        mBannerProvider = new ZXBannerProvider();
        mProviderDelegate.registerProvider(mItemProvider);
        mProviderDelegate.registerProvider(mTitleProvider);
        mProviderDelegate.registerProvider(mBannerProvider);
    }
}
