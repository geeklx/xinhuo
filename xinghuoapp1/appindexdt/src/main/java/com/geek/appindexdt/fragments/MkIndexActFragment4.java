package com.geek.appindexdt.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.geek.appindexdt.R;
import com.geek.appindexdt.adapter.MkFLunboAdapter;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libbase.viewpager2.bean.DataBean;
import com.youth.banner.Banner;
import com.youth.banner.config.BannerConfig;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.indicator.RoundLinesIndicator;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.util.BannerUtils;
import com.youth.banner.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class MkIndexActFragment4 extends SlbBaseLazyFragmentNew {

    private Banner banner;
    private RoundLinesIndicator roundLinesIndicator;

    public static List<DataBean> getTestData() {
        List<DataBean> list = new ArrayList<>();
//        list.add(new DataBean(R.drawable.img_bkg, "听风.赏雨", 3));
//        list.add(new DataBean(R.drawable.img_bkg, "迪丽热巴.迪力木拉提", 1));
//        list.add(new DataBean(R.drawable.img_bkg, "爱美.人间有之", 3));
//        list.add(new DataBean(R.drawable.img_bkg, "洋洋洋.气质篇", 1));
//        list.add(new DataBean(R.drawable.img_bkg, "生活的态度", 3));
        list.add(new DataBean("https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", "听风.赏雨", 1));
        list.add(new DataBean("https://img.zcool.cn/community/01639a56fb62ff6ac725794891960d.jpg", "迪丽热巴.迪力木拉提", 1));
        list.add(new DataBean("https://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg", "爱美.人间有之", 1));
        list.add(new DataBean("https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg", "洋洋洋.气质篇", 1));
        list.add(new DataBean("https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg", "生活的态度", 1));
        return list;
    }

    @Override
    public void call(Object value) {
        String ids = (String) value;
        ToastUtils.showLong(ids);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mk_indexact_fragment4;
    }

    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        banner = rootView.findViewById(R.id.banner);
        roundLinesIndicator = rootView.findViewById(R.id.indicator);
        onclick();
        donetwork();
    }

    private void donetwork() {
        banner.setAdapter(new MkFLunboAdapter(getTestData()));
        banner.setIndicator(new CircleIndicator(getActivity()));
        banner.setIndicatorGravity(IndicatorConfig.Direction.RIGHT);
        banner.setIndicatorMargins(new IndicatorConfig.Margins(0, 0,
                BannerConfig.INDICATOR_MARGIN, (int) BannerUtils.dp2px(12)));
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(Object data, int position) {
                ToastUtils.showLong(((DataBean) data).title);
                LogUtils.d("position：" + position);
            }
        });
    }

    private void updateData() {
        //给banner重新设置数据
        banner.setDatas(DataBean.getTestData());
        //对setdatas不满意？你可以自己控制数据，可以参考setDatas()的实现修改
//                    adapter.updateData(DataBean.getTestData2());
    }

    private void setIndicator() {
        roundLinesIndicator.setVisibility(View.VISIBLE);
        //在布局文件中使用指示器，这样更灵活
        banner.setIndicator(roundLinesIndicator, false);
        banner.setIndicatorSelectedWidth((int) BannerUtils.dp2px(15));
    }

    private void onclick() {

    }
}
