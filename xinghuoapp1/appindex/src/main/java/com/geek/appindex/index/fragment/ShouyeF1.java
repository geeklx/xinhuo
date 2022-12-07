package com.geek.appindex.index.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appindex.R;
import com.geek.appindex.news.fragment.TZGGFragment2;
import com.geek.appindex.tyxsbofangqi.VideoPlayerAct;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libbase.utils.CommonUtils;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libupdateapp.util.UpdateAppUtils;
import com.just.agentweb.geek.hois3.HiosHelperNew;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.util.XPopupUtils;

import xyz.doikki.dkplayer.util.DataUtilDk;

public class ShouyeF1 extends SlbBaseLazyFragmentNew implements View.OnClickListener {

    private String tablayoutId;
    private TextView textView;
    private TextView tv_center_content1;
    private TextView tv_center_content2;
    private TextView tv_center_content3;
    private TextView tv_center_content4;
    private TextView tv_center_content5;
    private TextView tv_center_content6;
    private TextView tv_center_content7;
    private TextView tv_center_content8;
    private TextView tv_center_content9;
    private TextView tv_center_content10;
    private TextView tv_center_content11;
    private TextView tv_center_content12;
    private TextView tv_center_content13;
    private TextView tv_center_content14;
    private TextView tv_center_content15;
    private TextView tv_center_content16;
    private TextView tv_center_content17;
    private TextView tv_center_content18;
    private TextView tv_center_content19;
    private TextView tv_center_content20;
    private TextView tv_center_content21;
    private TextView tv_center_content22;
    private MessageReceiverIndex mMessageReceiver;

    public class MessageReceiverIndex extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if ("ShouyeF1".equals(intent.getAction())) {
                    //TODO 发送广播bufen
                    Intent msgIntent = new Intent();
                    msgIntent.setAction("ShouyeF1");
                    msgIntent.putExtra("query1", 0);
                    LocalBroadcastManagers.getInstance(getActivity()).sendBroadcast(msgIntent);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static ShouyeF1 getInstance(BjyyBeanYewu3 bean) {
        ShouyeF1 fragment = new ShouyeF1();
        Bundle bundle = new Bundle();
        bundle.putSerializable("feileiBean", bean);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ShouyeF1 getInstance(Bundle bundle) {
        ShouyeF1 mEasyWebFragment = new ShouyeF1();
        if (bundle != null) {
            mEasyWebFragment.setArguments(bundle);
        }
        return mEasyWebFragment;

    }

    @Override
    public void call(Object value) {
        tablayoutId = (String) value;
        ToastUtils.showLong("call->" + tablayoutId);
        MyLogUtil.e("tablayoutId->", "call->" + tablayoutId);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManagers.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        // 从缓存中拿出头像bufen

        super.onResume();
    }

    private BjyyBeanYewu3 feileiBean;

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
//        Bundle arg = getArguments();
        if (getArguments() != null) {
            tablayoutId = getArguments().getString("tablayoutId");
            MyLogUtil.e("tablayoutId->", "onCreate->" + tablayoutId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                feileiBean = getArguments().getSerializable("feileiBean",BjyyBeanYewu3.class);
                MyLogUtil.e("tablayoutId->", "onCreate->" + feileiBean.getId());
            }
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shouyef1;
    }

    @Override
    protected void setup(final View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        //
        textView = rootView.findViewById(R.id.textView);
        if (!CommonUtils.isHarmonyOS()) {
            textView.setText("Android系统");
        } else {
            textView.setText("HarmonyOS系统");
        }
        mMessageReceiver = new MessageReceiverIndex();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction("ShouyeF1");
        LocalBroadcastManagers.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);
        //
        tv_center_content1 = rootView.findViewById(R.id.shouyef1_tv1);
        tv_center_content2 = rootView.findViewById(R.id.shouyef1_tv2);
        tv_center_content3 = rootView.findViewById(R.id.shouyef1_tv3);
        tv_center_content4 = rootView.findViewById(R.id.shouyef1_tv4);
        tv_center_content5 = rootView.findViewById(R.id.shouyef1_tv5);
        tv_center_content6 = rootView.findViewById(R.id.shouyef1_tv6);
        tv_center_content7 = rootView.findViewById(R.id.shouyef1_tv7);
        tv_center_content8 = rootView.findViewById(R.id.shouyef1_tv8);
        tv_center_content9 = rootView.findViewById(R.id.shouyef1_tv9);
        tv_center_content10 = rootView.findViewById(R.id.shouyef1_tv10);
        tv_center_content11 = rootView.findViewById(R.id.shouyef1_tv11);
        tv_center_content12 = rootView.findViewById(R.id.shouyef1_tv12);
        tv_center_content13 = rootView.findViewById(R.id.shouyef1_tv13);
        tv_center_content14 = rootView.findViewById(R.id.shouyef1_tv14);
        tv_center_content15 = rootView.findViewById(R.id.shouyef1_tv15);
        tv_center_content16 = rootView.findViewById(R.id.shouyef1_tv16);
        tv_center_content17 = rootView.findViewById(R.id.shouyef1_tv17);
        tv_center_content18 = rootView.findViewById(R.id.shouyef1_tv18);
        tv_center_content19 = rootView.findViewById(R.id.shouyef1_tv19);
        tv_center_content20 = rootView.findViewById(R.id.shouyef1_tv20);
        tv_center_content21 = rootView.findViewById(R.id.shouyef1_tv21);
        tv_center_content22 = rootView.findViewById(R.id.shouyef1_tv22);
        tv_center_content1.setOnClickListener(this);
        tv_center_content2.setOnClickListener(this);
        tv_center_content3.setOnClickListener(this);
        tv_center_content4.setOnClickListener(this);
        tv_center_content5.setOnClickListener(this);
        tv_center_content6.setOnClickListener(this);
        tv_center_content7.setOnClickListener(this);
        tv_center_content8.setOnClickListener(this);
        tv_center_content9.setOnClickListener(this);
        tv_center_content10.setOnClickListener(this);
        tv_center_content11.setOnClickListener(this);
        tv_center_content12.setOnClickListener(this);
        tv_center_content13.setOnClickListener(this);
        tv_center_content14.setOnClickListener(this);
        tv_center_content15.setOnClickListener(this);
        tv_center_content16.setOnClickListener(this);
        tv_center_content17.setOnClickListener(this);
        tv_center_content18.setOnClickListener(this);
        tv_center_content19.setOnClickListener(this);
        tv_center_content20.setOnClickListener(this);
        tv_center_content21.setOnClickListener(this);
        tv_center_content22.setOnClickListener(this);
        donetwork();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.shouyef1_tv1) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.IndexAct"));
        }
        if (id == R.id.shouyef1_tv2) {
            VideoPlayerAct.start(getActivity(), DataUtilDk.SAMPLE_URL, "点播", false);
        }
        if (id == R.id.shouyef1_tv3) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.Saoma3CommonScanActivity1"));
        }
        if (id == R.id.shouyef1_tv4) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.TencentIMSplashActivity"));
        }
        if (id == R.id.shouyef1_tv5) {
//            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatWebActivity"));
            HiosHelperNew.resolveAd(getActivity(), getActivity(), "http://www.baidu.com/");
        }
        if (id == R.id.shouyef1_tv6) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MusicAct"));
        }
        if (id == R.id.shouyef1_tv7) {
//            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.HSTact"));
        }
        if (id == R.id.shouyef1_tv8) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.BjyyAct"));
        }
        if (id == R.id.shouyef1_tv9) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.RCardAct3"));
        }
        if (id == R.id.shouyef1_tv10) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.RCardAct1"));
//            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.PhoneAct1"));
        }
        if (id == R.id.shouyef1_tv11) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.StackAct"));
//            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.PhoneAct2"));
        }
        if (id == R.id.shouyef1_tv12) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.CityPickerAct"));
//            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.PhoneAct3"));
        }
        if (id == R.id.shouyef1_tv13) {
            String apkPath = "https://ip3501727548.mobgslb.tbcache.com/fs08/2020/12/20/8/110_9735b48a09cbfe495f138201aa78e3ec.apk?fname=%E8%85%BE%E8%AE%AF%E6%96%B0%E9%97%BB&productid=2011&packageid=400964468&pkg=com.tencent.news&vcode=6361&yingid=wdj_web&pos=detail-ndownload-com.tencent.news&appid=280347&apprd=280347&iconUrl=http%3A%2F%2Fandroid-artworks.25pp.com%2Ffs08%2F2020%2F12%2F21%2F1%2F110_205b543f1a7b8f019e240f0f841bec73_con.png&did=19faf86fa212b232c23791f221a215e2&md5=030693066c95ced4f805edb7911bd916&ali_redirect_domain=alissl.ucdl.pp.uc.cn&ali_redirect_ex_ftag=fa35a7a50589ab0834939893ee826c42fa4f4851c60badda&ali_redirect_ex_tmining_ts=1608532468&ali_redirect_ex_tmining_expire=3600&ali_redirect_ex_hot=100";
//            String apkPath = "";// 下载地址
            int serverVersionCode = AppUtils.getAppVersionCode();// 文件版本号
            String serverVersionName = AppUtils.getAppVersionName();// 文件版本名字
            String appPackageName = AppUtils.getAppPackageName();// 文件包名
            String updateInfoTitle = "";// 更新标题
            String updateInfo = "";// 更新内容 支持换行"版本：1.01" + "    " + "大小：10.41M\n" + "1.修改已知问题\n2.加入动态绘本\n3.加入小游戏等你来学习升级"
            String md5 = AppUtils.getAppSignaturesMD5(appPackageName).get(0);// md5校验
            // 接口bufen
            UpdateAppUtils.from(getActivity())
                    .serverVersionCode(serverVersionCode + 1)
                    .serverVersionName(serverVersionName)
//                    .downloadPath("11111.apk")
                    .showProgress(true)
                    .apkPath(apkPath)
                    .isForce(false)
                    .downloadBy(UpdateAppUtils.DOWNLOAD_BY_APP)    //default
                    .checkBy(UpdateAppUtils.CHECK_BY_VERSION_CODE) //default
                    .updateInfoTitle("新版本已准备好")
                    .updateInfo("版本：1.01" + "    " + "大小：2.41M\n" + "1.商户加入群聊，在线沟通更方便\n2.配送费专属优惠，下单更便宜\n3.新客加大福利，更多优惠等你来")
//                .showNotification(false)
//                .needFitAndroidN(false)
                    .update();
        }
        if (id == R.id.shouyef1_tv14) {
            new XPopup.Builder(getContext())
//                    .autoOpenSoftInput(true)
                    .maxWidth((int) (XPopupUtils.getWindowWidth(getContext()) * 0.8f))
                    .dismissOnTouchOutside(false)
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .asConfirm("帐号过期", "由于长期不等到导致信息过期，由于长期不等到导致信息过期，由于长期不等到导致信息过期。",
                            "取消", "登录",
                            new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    ToastUtils.showLong("确定");
                                }
                            }, new OnCancelListener() {
                                @Override
                                public void onCancel() {
                                    ToastUtils.showLong("取消");
                                }
                            }, false, R.layout.my_confim_popup)
                    .show(); //最后一个参数绑定已有布局
        }
        if (id == R.id.shouyef1_tv15) {
            new XPopup.Builder(getContext())
//                    .autoOpenSoftInput(true)
                    .maxWidth((int) (XPopupUtils.getWindowWidth(getContext()) * 0.8f))
                    .dismissOnTouchOutside(false)
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .asConfirm("维护中…", "应用正在维护，维护时间在，请耐心等待",
                            "取消", "我知道了",
                            new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    ToastUtils.showLong("确定");
                                }
                            }, new OnCancelListener() {
                                @Override
                                public void onCancel() {
                                    ToastUtils.showLong("取消");
                                }
                            }, true, R.layout.my_confim_popup2)
                    .show(); //最后一个参数绑定已有布局
        }
        if (id == R.id.shouyef1_tv16) {
            Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.SearchListActivity1");
            intent.putExtra("search_key", "搜索");
            startActivity(intent);
        }
        if (id == R.id.shouyef1_tv17) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.AgentwebAct"));
        }
        if (id == R.id.shouyef1_tv18) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.FenleiAct"));
        }
        if (id == R.id.shouyef1_tv19) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.PIPActivityDk"));
        }
        if (id == R.id.shouyef1_tv20) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.PopviewDemoAct"));
        }
        if (id == R.id.shouyef1_tv21) {
            HiosHelperNew.resolveAd(getActivity(), getActivity(),
                    "dataability://" + AppUtils.getAppPackageName() + ".hs.act.slbapp.StartActivityxh{act}?query1=11111&query2={s}aaaaaa&query3={s}本地应用");
        }
        if (id == R.id.shouyef1_tv22) {
            HiosHelperNew.resolveAd(getActivity(), getActivity(),
                    "dataability://" + AppUtils.getAppPackageName() + ".hs.act.slbapp.StartActivityxhdf{act}?query1=11111&query2={s}aaaaaa&query3={s}本地应用");
        }
    }

    /**
     * 第一次进来加载bufen
     */
    private void donetwork() {
        retryData();
    }

    // 刷新
    private void retryData() {
//        mEmptyView.loading();
//        presenter1.getLBBannerData("0");
//        refreshLayout1.finishRefresh();
//        emptyview1.success();
    }

    /**
     * 底部点击bufen
     *
     * @param cateId
     * @param isrefresh
     */
    @Override
    public void getCate(String cateId, boolean isrefresh) {

        if (!isrefresh) {
            // 从缓存中拿出头像bufen

            return;
        }
        ToastUtils.showLong("下拉刷新啦");
    }

    /**
     * 当切换底部的时候通知每个fragment切换的id是哪个bufen
     *
     * @param cateId
     */
    @Override
    public void give_id(String cateId) {
//        ToastUtils.showLong("下拉刷新啦");
        MyLogUtil.e("tablayoutId->", "give_id->" + cateId);
    }

    /**
     * --------------------------------业务逻辑分割线----------------------------------
     */


}
