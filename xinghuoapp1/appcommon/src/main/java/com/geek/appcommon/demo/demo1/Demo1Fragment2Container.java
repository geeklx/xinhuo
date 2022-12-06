package com.geek.appcommon.demo.demo1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.geek.common.R;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libbase.emptyview.EmptyViewNewNew;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: lhw
 * @date: 2022/7/18
 * @desc
 */
public class Demo1Fragment2Container extends SlbBaseLazyFragmentNew {

    private EmptyViewNewNew emptyView;
    private FrameLayout flContainer;
    private Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        initView(rootView);
        doNetWork();

    }

    private void doNetWork() {
        emptyView.loading();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                emptyView.success();
                DemoResultBean demoResultBean = new DemoResultBean();
                List<DemoBean> list = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    DemoBean demoBean = new DemoBean();
                    demoBean.setTitle("nihao" + i);
                    demoBean.setCreateTime("2022-07-28");
                    list.add(demoBean);

                }
                demoResultBean.setDatas(list);
                setFragment(demoResultBean);

            }
        }, 2000);
    }

    private void initView(View rootView) {
        emptyView = rootView.findViewById(R.id.emptyView);
        flContainer = rootView.findViewById(R.id.fl_demo2_container);
        emptyView.bind(flContainer);
        emptyView.notices("暂无数据", "网络出了点问题", "正在加载…", "");
        emptyView.setRetryListener(new EmptyViewNewNew.RetryListener() {
            @Override
            public void retry() {
                doNetWork();
            }
        });
    }

    private void setFragment(DemoResultBean demoResultBean) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", demoResultBean);
        Demo1Fragment2Item fragment = Demo1Fragment2Item.getInstance(bundle);
        fragmentTransaction.add(
                R.id.fl_demo2_container,
                fragment,
                Demo1Fragment2Item.class.getName()
        );
        fragmentTransaction.show(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_demo1_fragment2_conatiner;
    }
}




