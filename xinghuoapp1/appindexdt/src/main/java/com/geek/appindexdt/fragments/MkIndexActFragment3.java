package com.geek.appindexdt.fragments;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.geek.appindexdt.R;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.haier.cellarette.baselibrary.marqueelibrary.SimpleMF;
import com.haier.cellarette.baselibrary.marqueelibrary.SimpleMarqueeView;
import com.haier.cellarette.baselibrary.marqueelibrary.util.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MkIndexActFragment3 extends SlbBaseLazyFragmentNew {

    private SimpleMarqueeView simpleMarqueeView;


    @Override
    public void call(Object value) {
        String ids = (String) value;
        ToastUtils.showLong(ids);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mk_indexact_fragment3;
    }

    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        simpleMarqueeView = rootView.findViewById(R.id.marqueeView1);
        initMarqueeView1();
    }

    private void initMarqueeView1() {
        SimpleMF<Spanned> marqueeFactory3 = new SimpleMF<>(getActivity());
        List<Spanned> datas3 = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            datas3.add(Html.fromHtml("<font color=\"#ff0000\">《赋得古原草送别》</font>", Html.FROM_HTML_MODE_COMPACT));
            datas3.add(Html.fromHtml("<font color=\"#00ff00\">离离原上草，</font>一岁一枯荣。", Html.FROM_HTML_MODE_COMPACT));
            datas3.add(Html.fromHtml("野火烧不尽，<font color=\"#0000ff\">春风吹又生。</font>", Html.FROM_HTML_MODE_COMPACT));
            datas3.add(Html.fromHtml("<font color=\"#333333\">远芳侵古道，晴翠接荒城。</font>", Html.FROM_HTML_MODE_COMPACT));
            datas3.add(Html.fromHtml("<font color=\"#ffffff\">又送王孙去，萋萋满别情。</font>", Html.FROM_HTML_MODE_COMPACT));
        }
        marqueeFactory3.setData(datas3);
        simpleMarqueeView.setMarqueeFactory(marqueeFactory3);
        simpleMarqueeView.startFlipping();
        simpleMarqueeView.setOnItemClickListener(new OnItemClickListener<TextView, Spanned>() {
            @Override
            public void onItemClickListener(TextView mView, Spanned mData, int mPosition) {
                ToastUtils.showLong(String.format("mPosition:%s,mData:%s,mView:%s,.", mPosition, mData, mView));
            }
        });
    }

}
