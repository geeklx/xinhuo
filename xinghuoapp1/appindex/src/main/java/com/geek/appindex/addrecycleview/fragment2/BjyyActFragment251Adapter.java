package com.geek.appindex.addrecycleview.fragment2;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.MultipleItemRvAdapter;
import com.geek.biz1.bean.BjyyActFragment251Bean;

import java.util.List;

public class BjyyActFragment251Adapter extends MultipleItemRvAdapter<BjyyActFragment251Bean, BaseViewHolder> {

    public static final int STYLE_ONE = 100;
    public static final int STYLE_ONE11 = 1001;
    public static final int STYLE_FIVE = 500;
    public static final int STYLE_DJYL = 5001;

    public BjyyActFragment251Adapter(@Nullable List<BjyyActFragment251Bean> data) {
        super(data);

        //构造函数若有传其他参数可以在调用finishInitialize()之前进行赋值，赋值给全局变量
        //这样getViewType()和registerItemProvider()方法中可以获取到传过来的值
        //getViewType()中可能因为某些业务逻辑，需要将某个值传递过来进行判断，返回对应的viewType
        //registerItemProvider()中可以将值传递给ItemProvider
        finishInitialize();

    }

    @Override
    protected int getViewType(BjyyActFragment251Bean BjyyActFragment251Bean) {
        //根据实体类判断并返回对应的viewType，具体判断逻辑因业务不同，这里这是简单通过判断type属性
        if (BjyyActFragment251Bean.type == com.geek.biz1.bean.BjyyActFragment251Bean.style1) {
            return STYLE_ONE;
        } else if (BjyyActFragment251Bean.type == com.geek.biz1.bean.BjyyActFragment251Bean.style5) {
            return STYLE_FIVE;
        } else if (BjyyActFragment251Bean.type == com.geek.biz1.bean.BjyyActFragment251Bean.style11) {
            return STYLE_ONE11;
        } else if (com.geek.biz1.bean.BjyyActFragment251Bean.STYLE_DJYL == BjyyActFragment251Bean.type) {
            return STYLE_DJYL;
        }
        return 0;
    }

    public BjyyActFragment2Style1Provider bjyyActFragment2Style1Provider;
    public BjyyActFragment2Style5Provider bjyyActFragment2Style5Provider;
    public BjyyActFragment2Style1Provider1 bjyyActFragment2Style1Provider1;
    public BjyyActFragment2Style3Provider bjyyActFragment2Style3Provider;

    @Override
    public void registerItemProvider() {
        //注册相关的条目provider
        bjyyActFragment2Style1Provider = new BjyyActFragment2Style1Provider();
        bjyyActFragment2Style5Provider = new BjyyActFragment2Style5Provider();
        bjyyActFragment2Style1Provider1 = new BjyyActFragment2Style1Provider1();
        bjyyActFragment2Style3Provider = new BjyyActFragment2Style3Provider();
        mProviderDelegate.registerProvider(bjyyActFragment2Style1Provider);
        mProviderDelegate.registerProvider(bjyyActFragment2Style5Provider);
        mProviderDelegate.registerProvider(bjyyActFragment2Style1Provider1);
        mProviderDelegate.registerProvider(bjyyActFragment2Style3Provider);
    }
}
