package com.geek.appindex.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.geek.appcommon.wechat.fragment.H5WebFragment;
import com.geek.appindex.index.fragment.ShouyeF1;
import com.geek.appindex.index.fragment.ShouyeF3;
import com.geek.appindex.news.fragment.DJDTFragment2;
import com.geek.appindex.news.fragment.RecommandFragment2;
import com.geek.appindex.news.fragment.TZGGFragment2;
import com.geek.appindex.news.fragment.XZYWFragment2;
import com.geek.appindex.news.fragment.ZCWJFragment2;
import com.geek.biz1.bean.BjyyBeanYewu3;

import java.util.List;

public class Shouye10Adapter1<T> extends FragmentPagerAdapter {

    private Context mContext;
    //private List<Fragment> fragmentList;
    private String[] titles;
    private List<T> bean;
    private FragmentManager fm;

    //推荐
    public static final String TAG_RECOMMAND = "com.geek.appindex.news.fragment.RecommandFragment";
    //党建动态
    public static final String TAG_DJDT = "com.geek.appindex.news.fragment.DJDTFragment";
    //工作通知
    public static final String TAG_TZGG = "com.geek.appindex.news.fragment.TZGGFragment";
    //乡镇要闻
    public static final String TAG_XZYW = "com.geek.appindex.news.fragment.XZYWFragment";
    //政策文件
    public static final String TAG_ZCWJ = "com.geek.appindex.news.fragment.ZCWJFragment";

    public Shouye10Adapter1(FragmentManager fm, Context context, String[] titles, List<T> bean, int behavior) {
        super(fm, behavior);
        this.fm = fm;
        this.mContext = context;
        this.bean = bean;
        //this.fragmentList = fragmentList;
        this.titles = titles;
    }

    public void setdata(String[] titles, List<T> bean) {
        this.bean = bean;
        this.titles = titles;
    }


    @Override
    public Fragment getItem(int position) {
        BjyyBeanYewu3 fenleiAct1CateBean1 = (BjyyBeanYewu3) bean.get(position);
        String url = fenleiAct1CateBean1.getUrl();
        if (url.contains("http")) {
            Bundle bundle = new Bundle();
            bundle.putString("tablayoutId", url);
            return H5WebFragment.getInstance(bundle);
        }
        switch (url.trim()) {
            case TAG_RECOMMAND:
                //return RecommandFragment.Companion.getInstance(fenleiAct1CateBean1);
                return ShouyeF1.getInstance(fenleiAct1CateBean1);
            case TAG_DJDT:
                //return DJDTFragment.Companion.getInstance(fenleiAct1CateBean1);
                return ShouyeF3.getInstance(fenleiAct1CateBean1);
            case TAG_TZGG:
                //return TZGGFragment.Companion.getInstance(fenleiAct1CateBean1);
                return TZGGFragment2.getInstance(fenleiAct1CateBean1);
            case TAG_XZYW:
                // return XZYWFragment.Companion.getInstance(fenleiAct1CateBean1);
                return XZYWFragment2.getInstance(fenleiAct1CateBean1);
            case TAG_ZCWJ:
                //return ZCWJFragment.Companion.getInstance(fenleiAct1CateBean1);
                return ZCWJFragment2.getInstance(fenleiAct1CateBean1);
            default:
                //return RecommandFragment.Companion.getInstance(fenleiAct1CateBean1);
                return RecommandFragment2.getInstance(fenleiAct1CateBean1);
        }
//        switch (url.trim()) {
//            case TAG_RECOMMAND:
//                //return RecommandFragment.Companion.getInstance(fenleiAct1CateBean1);
//                return RecommandFragment2.getInstance(fenleiAct1CateBean1);
//            case TAG_DJDT:
//                //return DJDTFragment.Companion.getInstance(fenleiAct1CateBean1);
//                return DJDTFragment2.getInstance(fenleiAct1CateBean1);
//            case TAG_TZGG:
//                //return TZGGFragment.Companion.getInstance(fenleiAct1CateBean1);
//                return TZGGFragment2.getInstance(fenleiAct1CateBean1);
//            case TAG_XZYW:
//                // return XZYWFragment.Companion.getInstance(fenleiAct1CateBean1);
//                return XZYWFragment2.getInstance(fenleiAct1CateBean1);
//            case TAG_ZCWJ:
//                //return ZCWJFragment.Companion.getInstance(fenleiAct1CateBean1);
//                return ZCWJFragment2.getInstance(fenleiAct1CateBean1);
//            default:
//                //return RecommandFragment.Companion.getInstance(fenleiAct1CateBean1);
//                return RecommandFragment2.getInstance(fenleiAct1CateBean1);
//        }

    }

    @Override
    public int getCount() {
        return bean.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        ZXFragment2 fragment = (ZXFragment2) super.instantiateItem(container, position);
//        fragment.updateArguments((FenleiAct1CateBean1) bean.get(position));
//        return fragment;
//
//    }


//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        return super.instantiateItem(container, position);
//
//    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fm.beginTransaction().remove((Fragment) object);
    }

    @Override
    public int getItemPosition(Object object) {
        // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法
        return POSITION_NONE;
    }
}