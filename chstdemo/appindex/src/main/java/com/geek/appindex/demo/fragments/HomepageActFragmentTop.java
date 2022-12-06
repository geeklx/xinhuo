package com.geek.appindex.demo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.geek.appindex.MkDemo2Act;
import com.geek.appindex.R;
import com.geek.biz1.bean.home.WeatherBean;
import com.geek.biz1.presenter.home.WeatherPresenter;
import com.geek.biz1.view.home.WeatherView;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author lhw
 * 首页左侧头部状态栏
 */
public class HomepageActFragmentTop extends SlbBaseLazyFragmentNew implements WeatherView {

    private static final int MSG_WEATHER = 200;
    private ScheduledExecutorService scheduledExecutorService;
    private Handler mHandler = new MyHandler(HomepageActFragmentTop.this);
    private WeatherPresenter weatherPresenter;

    private ConstraintLayout ll1;
    private ImageView iv3;
    private ImageView iv4;
    private TextView mTvWeather;

    public WeatherPresenter getWeatherPresenter() {
        return weatherPresenter;
    }

    @Override
    public void call(Object value) {
        String ids = (String) value;
        ToastUtils.showLong(ids);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_homepage_topact_fragment;
    }

    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        ll1 = rootView.findViewById(R.id.ll1);
        iv3 = rootView.findViewById(R.id.iv3);
        iv4 = rootView.findViewById(R.id.iv4);
        mTvWeather = rootView.findViewById(R.id.tv2);
        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showLong("搜索");
            }
        });
        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivity(wifiSettingsIntent);
            }
        });
        iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showLong("用户");
            }
        });

        doNetWork();
    }

    private void doNetWork() {
        weatherPresenter = new WeatherPresenter();
        weatherPresenter.onCreate(this);
        addTimer();
    }

    /**
     * 添加定时器
     */
    private void addTimer() {
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (mHandler != null) {
                    Message msg = Message.obtain();
                    msg.what = MSG_WEATHER;
                    mHandler.sendMessage(msg);
                }

            }
        }, 0, 3600, TimeUnit.SECONDS);
    }

    private static class MyHandler extends Handler {

        private final WeakReference<HomepageActFragmentTop> mActivity;

        MyHandler(HomepageActFragmentTop activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            HomepageActFragmentTop activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {

                    case MSG_WEATHER:
                        WeatherPresenter weatherPresenter = activity.getWeatherPresenter();
                        if (weatherPresenter != null) {
                            weatherPresenter.getWeather("济南");
                        }

                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void net_con_success() {
        super.net_con_success();
        iv3.setImageResource(R.drawable.dp_icon5);
//      ToastUtils.showLong("网络已连接");
    }

    @Override
    public void net_con_none() {
        super.net_con_none();
        iv3.setImageResource(R.drawable.dp_icon5);
//      ToastUtils.showLong("网络已断开");
    }

    @Override
    public void onWeatherSuccess(WeatherBean bean) {
        if (bean != null && !TextUtils.isEmpty(bean.getJointInfo())) {
            if (mTvWeather != null) {
                mTvWeather.setText(bean.getJointInfo());
            }
        }
    }

    @Override
    public void onWeatherSuccessNodata(String msg) {
        ToastUtils.showShort(msg);
    }

    @Override
    public void onWeatherSuccessFail(String msg) {
        ToastUtils.showShort(msg);
    }

    /**
     * 页面传值操作部分
     *
     * @param id1
     */
    private void SendToFragment(String id1) {
        //举例
//        IndexFoodFragmentUpdateIds iff = new IndexFoodFragmentUpdateIds();
//        iff.setFood_definition_id(id1);
//        iff.setFood_name(id2);
        if (getActivity() != null && getActivity() instanceof MkDemo2Act) {
            ((MkDemo2Act) getActivity()).callFragment(id1, MkDemo2ActFragment2.class.getName());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (weatherPresenter != null) {
            weatherPresenter.onDestory();
        }

        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
    }
}
