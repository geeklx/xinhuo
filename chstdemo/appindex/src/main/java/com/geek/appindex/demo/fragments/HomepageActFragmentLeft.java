package com.geek.appindex.demo.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appindex.R;
import com.geek.appindex.adapter.MkFLunbo1Adapter;
import com.geek.appindex.adapter.NotifiAdapter;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libbase.widgets.ViewPagerSlide;
import com.geek.tablib.tablayout.SlidingTabLayout;
import com.haier.cellarette.baselibrary.marqueelibrary.SimpleMF;
import com.haier.cellarette.baselibrary.marqueelibrary.SimpleMarqueeView;
import com.haier.cellarette.baselibrary.marqueelibrary.util.OnItemClickListener;
import com.youth.banner.Banner;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.utils.AutoSizeUtils;


/**
 * @author lhw
 * 首页左侧banner
 */
public class HomepageActFragmentLeft extends SlbBaseLazyFragmentNew {

    public Banner banner;
    public SimpleMarqueeView marqueeView3;
    private SlidingTabLayout tabLayout;
    private ViewPagerSlide vp;
    private NotifiAdapter adapter;


    @Override
    public void call(Object value) {
        String ids = (String) value;
        ToastUtils.showLong(ids);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_homepage_leftact_fragment;
    }

    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        findview(rootView);
        onclick();
        donetwork();
        set_viewpager();
    }

    private void donetwork() {
        set_lunbo_data();
        set_marqueeview_data();

    }

    private void set_viewpager() {
        vp.removeAllViews();
        List<BjyyBeanYewu3> list = new ArrayList<>();
        list.add(new BjyyBeanYewu3("", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862286564142395.png", "党建要闻", "", false));
        list.add(new BjyyBeanYewu3("", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862286564142395.png", "党史百科", "", false));

        String[] titlesString = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            titlesString[i] = list.get(i).getName();
        }
        adapter = new NotifiAdapter(
//                    getActivity().getSupportFragmentManager(),
                getChildFragmentManager(),
                getActivity(),
                titlesString,
                list,
                NotifiAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vp.setOffscreenPageLimit(list.size());
        vp.setScroll(true);
        vp.setAdapter(adapter);
        tabLayout.setViewPager(vp);
    }


    private void onclick() {

    }

    private void findview(View rootView) {
        marqueeView3 = rootView.findViewById(R.id.marqueeView3);
        banner = rootView.findViewById(R.id.banner);
        vp = rootView.findViewById(R.id.vp);
        tabLayout = rootView.findViewById(R.id.tl_fenlei1);
    }

    private void set_lunbo_data() {
        List<BjyyBeanYewu3> mlist1 = new ArrayList<>();
        mlist1.add(new BjyyBeanYewu3("", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862286564142395.png", "十九届六中全会", "", false));
        mlist1.add(new BjyyBeanYewu3("", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862378905937488.png", "十九届六中全会", "", false));
        mlist1.add(new BjyyBeanYewu3("", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521863886959548956.png", "十九届六中全会", "", false));
        banner.setAdapter(new MkFLunbo1Adapter(mlist1));
//      banner.setAdapter(new MultipleTypesAdapter(getActivity(), getTestData()));
        banner.setIndicator(new CircleIndicator(getActivity()));
        banner.setIndicatorGravity(IndicatorConfig.Direction.RIGHT);
        banner.setIndicatorNormalColor(Color.parseColor("#FE965A"));
        banner.setIndicatorSelectedColor(Color.parseColor("#BB0C0C"));
        banner.setIndicatorSelectedWidth((int) AutoSizeUtils.mm2px(getActivity(), 5));
        banner.setIndicatorNormalWidth((int) AutoSizeUtils.mm2px(getActivity(), 4));
        banner.setIndicatorSpace((int) AutoSizeUtils.mm2px(getActivity(), 8));
        banner.setIndicatorRadius(0);
        banner.setIndicatorMargins(new IndicatorConfig.Margins(AutoSizeUtils.mm2px(getActivity(), 10), 0, 0, AutoSizeUtils.mm2px(getActivity(), 20)));
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(Object data, int position) {
//                ToastUtils.showLong(((BjyyBeanYewu3) data).getName());
                LogUtils.d("position：" + position);
            }
        });
    }

    private void set_marqueeview_data() {
        SimpleMF<Spanned> marqueeFactory3 = new SimpleMF<>(getActivity());
        List<Spanned> datas3 = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            datas3.add(Html.fromHtml("<font color=\"#FFFFFF\">庆祝祖国</font>-<font color=\"#E60000\">十一国庆节</font>", Html.FROM_HTML_MODE_COMPACT));
            datas3.add(Html.fromHtml("<font color=\"#FFFFFF\">庆祝祖国</font>-<font color=\"#E60000\">八一建党节</font>", Html.FROM_HTML_MODE_COMPACT));
            datas3.add(Html.fromHtml("<font color=\"#FFFFFF\">庆祝祖国</font>-<font color=\"#E60000\">七一建军节</font>", Html.FROM_HTML_MODE_COMPACT));
            datas3.add(Html.fromHtml("<font color=\"#FFFFFF\">庆祝祖国</font>-<font color=\"#E60000\">春节到</font>", Html.FROM_HTML_MODE_COMPACT));
        }
        marqueeFactory3.setData(datas3);
        marqueeView3.setMarqueeFactory(marqueeFactory3);
        marqueeView3.setOnItemClickListener(new OnItemClickListener<TextView, Spanned>() {
            @Override
            public void onItemClickListener(TextView mView, Spanned mData, int mPosition) {
//                Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.SearchListActivity1");
//                intent.putExtra("search_key", mData.toString());
//                startActivity(intent);
                ToastUtils.showLong(mData.toString());
            }
        });
        marqueeView3.startFlipping();
    }

    @Override
    public void onPause() {
        marqueeView3.stopFlipping();
        super.onPause();
    }

    @Override
    public void onResume() {
        marqueeView3.startFlipping();
        donetwork();
        super.onResume();
    }
}
