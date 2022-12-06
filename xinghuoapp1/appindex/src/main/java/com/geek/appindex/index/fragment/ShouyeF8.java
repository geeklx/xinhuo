package com.geek.appindex.index.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.adapter.MkFLunboAdapter;
import com.geek.appcommon.huadongyanzhengpop.BlockPuzzleDialog;
import com.geek.appindex.R;
import com.geek.appindex.addrecycleview.BjyyAct;
import com.geek.appindex.addrecycleview.fragment1.BjyyActFragment1;
import com.geek.appindex.addrecycleview.fragment2.BjyyActFragment251Adapter;
import com.geek.appindex.bean.DwCity;
import com.geek.biz1.bean.BjyyActFragment251Bean;
import com.geek.biz1.bean.BjyyBeanYewu1;
import com.geek.biz1.bean.BjyyBeanYewu2;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.biz1.bean.FAppCheckUseBean;
import com.geek.biz1.bean.FBannerBean;
import com.geek.biz1.bean.FWechatJumpBean;
import com.geek.biz1.bean.FgrxxBean;
import com.geek.biz1.presenter.FAppCheckUsePresenter;
import com.geek.biz1.presenter.FBannerPresenter;
import com.geek.biz1.presenter.FWechatJump1Presenter;
import com.geek.biz1.presenter.FsyyyPresenter;
import com.geek.biz1.view.FAppCheckUseView;
import com.geek.biz1.view.FBannerView;
import com.geek.biz1.view.FWechatJump1View;
import com.geek.biz1.view.FsyyyView;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libbase.widgets.XRecyclerView;
import com.geek.libglide47.base.GlideRoundImageView;
import com.geek.liblocations.LocationBean;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.LocationUtils;
import com.geek.libutils.data.MmkvUtils;
import com.haier.cellarette.baselibrary.qcode.ExpandViewRect;
import com.haier.cellarette.baselibrary.toasts3.MProgressDialog;
import com.haier.cellarette.baselibrary.toasts3.utils.MSizeUtils;
import com.haier.cellarette.baselibrary.widget.LxLinearLayout;
import com.just.agentweb.geek.fragment.AgentWebFragment;
import com.just.agentweb.geek.hois3.HiosHelperNew;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.ConfirmPopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.util.XPopupUtils;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.youth.banner.Banner;
import com.youth.banner.config.BannerConfig;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.RectangleIndicator;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.util.BannerUtils;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ShouyeF8 extends SlbBaseLazyFragmentNew implements FsyyyView, FWechatJump1View, FAppCheckUseView, FBannerView {

    public TextView tv1;
    public LxLinearLayout lxll1;
    public LinearLayout ll_search1;
    public ImageView iv_sysm1;
    public XRecyclerView mRecyclerView;
    public NestedScrollView scrollView;
    public BjyyActFragment251Adapter mAdapter;
    public List<BjyyActFragment251Bean> mList;
    public int spanCount = 4;// 几列数据
    public BjyyBeanYewu1 bjyyBeanYewu1;
    public Banner banner;
    public View bgView;
    public ImageView ivContract;
    public ImageView ivMine;
    public int height = 250;
    //
    private FsyyyPresenter fsyyyPresenter;
    private FWechatJump1Presenter fWechatJump1Presenter;
    private FAppCheckUsePresenter fAppCheckUsePresenter;
    private FBannerPresenter fBannerPresenter;
    private FBannerBean fBannerBean;

    public static ShouyeF8 getInstance(Bundle bundle) {
        ShouyeF8 mEasyWebFragment = new ShouyeF8();
        if (bundle != null) {
            mEasyWebFragment.setArguments(bundle);
        }
        return mEasyWebFragment;

    }

    public MessageReceiverIndex mMessageReceiver;

    public class MessageReceiverIndex extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if ("gongzuotai".equals(intent.getAction())) {
                    String data = intent.getStringExtra("dingwei");
                    if (!TextUtils.isEmpty(data)) {
                        tv1.setText(data);
                    }
                }
            } catch (Exception e) {
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        donetwork();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (fsyyyPresenter != null) {
            fsyyyPresenter.onDestory();
        }
        if (fWechatJump1Presenter != null) {
            fWechatJump1Presenter.onDestory();
        }
        if (fAppCheckUsePresenter != null) {
            fAppCheckUsePresenter.onDestory();
        }
        if (fBannerPresenter != null) {
            fBannerPresenter.onDestory();
        }
        LocalBroadcastManagers.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();

    }

    @Override
    public void call(Object value) {

    }

    public void SendToFragment(BjyyActFragment251Bean bean) {
        if (getActivity() != null && getActivity() instanceof BjyyAct) {
            ((BjyyAct) getActivity()).callFragment(bean, BjyyActFragment1.class.getName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageReceiver = new MessageReceiverIndex();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction("gongzuotai");
        LocalBroadcastManagers.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shouyef8;
    }

    private String tablayoutId;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        findview(rootView);
        onclicklistener();
        if (getArguments() != null) {
            tablayoutId = getArguments().getString("tablayoutId");
            MyLogUtil.e("tablayoutId->", "onCreate->" + tablayoutId);
        }
//        donetwork();
    }

    private void set_lunbo_data() {
//        List<BjyyBeanYewu3> mlist1 = new ArrayList<>();
//        mlist1.add(new BjyyBeanYewu3("", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862286564142395.png", "", "", false));
//        mlist1.add(new BjyyBeanYewu3("", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862378905937488.png", "", "", false));
//        mlist1.add(new BjyyBeanYewu3("", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521863886959548956.png", "", "", false));
        banner.setAdapter(new MkFLunboAdapter(fBannerBean.getData()));
//        banner.setAdapter(new MultipleTypesAdapter(getActivity(), getTestData()));
        banner.setIndicator(new RectangleIndicator(getActivity()));
        banner.setIndicatorSelectedWidth((int) BannerUtils.dp2px(12));
        banner.setIndicatorSpace((int) BannerUtils.dp2px(4));
        banner.setIndicatorRadius(0);
        banner.setBannerRound(BannerUtils.dp2px(50));
        IndicatorConfig.Margins margins = new IndicatorConfig.Margins(BannerConfig.INDICATOR_MARGIN, BannerConfig.INDICATOR_MARGIN, BannerConfig.INDICATOR_MARGIN, BannerUtils.dp2px(70));
        banner.setIndicatorMargins(margins);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(Object data, int position) {
//                ToastUtils.showLong(((BjyyBeanYewu3) data).getName());

                String url = ((BjyyBeanYewu3) data).getUrl();
                LogUtils.d(url + "position：" + position);
                if (url != null && !"".equals(url)) {
                    HiosHelperNew.resolveAd(getActivity(), getActivity(), url);
                }
            }
        });
    }

    private void donetwork() {
        //
        DwCity bean = MmkvUtils.getInstance().get_common_json("city", DwCity.class);
        if (bean != null) {
            tv1.setText(bean.getName());
        } else {
            LocationBean bean2 = MmkvUtils.getInstance().get_common_json("location", LocationBean.class);
            if (bean2 != null) {
                tv1.setText(bean2.getCity());
            } else {
                tv1.setText("定位失败");
            }
        }
        if (fsyyyPresenter == null) {
            fsyyyPresenter = new FsyyyPresenter();
            fsyyyPresenter.onCreate(this);
        }

        if (fWechatJump1Presenter == null) {
            fWechatJump1Presenter = new FWechatJump1Presenter();
            fWechatJump1Presenter.onCreate(this);
        }

        if (fAppCheckUsePresenter == null) {
            fAppCheckUsePresenter = new FAppCheckUsePresenter();
            fAppCheckUsePresenter.onCreate(this);
        }

        if (fBannerPresenter == null) {
            fBannerPresenter = new FBannerPresenter();
            fBannerPresenter.onCreate(this);
        }

//        fBannerPresenter.getBannerList(AppCommonUtils.auth_url + "/getplaypic", tablayoutId,"");
        fBannerPresenter.getBannerList("/gwapi/workbenchserver", "1009", "1");//党建引领

        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
        if (fgrxxBean != null) {
            fsyyyPresenter.getsyyy(AppCommonUtils.auth_url + "/first/page",
                    fgrxxBean.getUserId(),
                    fgrxxBean.getIdentityId(),
                    fgrxxBean.getOrgId(),
                    fgrxxBean.getCityName(),
                    tablayoutId);
        } else {
            fsyyyPresenter.getsyyy(AppCommonUtils.auth_url + "/first/page",
                    "",
                    "",
                    "",
                    AppCommonUtils.get_location_cityname(),
                    tablayoutId);
        }


    }

    private String currentId;
    private List<String> currentIds;

    private void onclicklistener() {
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PhoneUtils.isSimCardReady()) {
                    // 如果没有sim卡就开启GPS，否则拿不到定位
                    if (!LocationUtils.isGpsEnabled()) {
                        LocationUtils.openGpsSettings();
                    }
                }
                if (!LocationUtils.isGpsEnabled()) {
                    LocationUtils.openGpsSettings();
                } else {
                    set_loc();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void findview(View rootView) {
        banner = rootView.findViewById(R.id.banner);
        tv1 = rootView.findViewById(R.id.tv1);
        lxll1 = rootView.findViewById(R.id.lxll1);
        lxll1.setTouch(false);
        ll_search1 = rootView.findViewById(R.id.ll_search1);
        iv_sysm1 = rootView.findViewById(R.id.iv_sysm1);
        scrollView = rootView.findViewById(R.id.scrollView);
        bgView = rootView.findViewById(R.id.view);
        ivContract = rootView.findViewById(R.id.ivContract1);
        ivMine = rootView.findViewById(R.id.ivMine);
        ExpandViewRect.expandViewTouchDelegate(ivContract, 20, 20, 20, 20);
        ExpandViewRect.expandViewTouchDelegate(ivMine, 20, 20, 20, 20);
        ivContract.setOnClickListener(v -> {
            HiosHelperNew.resolveAd(getActivity(), getActivity(), "dataability://" + MmkvUtils.getInstance().get_common("choose_im"));
        });
        ivMine.setOnClickListener(v -> {
            Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.GztFragmentMyAct");
            startActivity(intent);
        });
        ll_search1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 党建引领搜索
//                startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.Saoma3CommonScanActivity1"));
                Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.SearchListActivity1");
                intent.putExtra("search_key", "");
                startActivity(intent);
            }
        });
        mRecyclerView = rootView.findViewById(R.id.rv_others);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount, RecyclerView.VERTICAL, false));
        mRecyclerView.canScrollVertically(-1);
        //
        mList = new ArrayList<>();
        mAdapter = new BjyyActFragment251Adapter(mList);
//        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        mAdapter.setNotDoAnimationCount(3);
        mAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                int type = mList.get(position).type;
                if (type == BjyyActFragment251Bean.style1 || type == BjyyActFragment251Bean.style11) {
                    return spanCount;
                } else if (type == BjyyActFragment251Bean.style5) {
                    return 1;
                } else {
                    return spanCount;
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        currentId = "-1";
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                BjyyActFragment251Bean addressBean = (BjyyActFragment251Bean) adapter.getItem(position);
                currentId = addressBean.getmBean().getId();
                if (TextUtils.isEmpty(addressBean.getmBean().getImg())) {
                    // 一级分类标题跳转bufen
                    return;
                }
                MProgressDialog.showProgress(getActivity(), "请稍等...");
                fAppCheckUsePresenter.getAppCheckUse(AppCommonUtils.auth_url + "/ismaintain", currentId);
            }
        });

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (nestedScrollView, i, i1, i2, i3) -> {

            int height = MSizeUtils.dp2px(getContext(), 60);
            bgView.setVisibility(View.VISIBLE);

            if (i1 <= 0) {
                bgView.setAlpha(0);
            } else if (i1 < height) {
                //获取渐变率
                float scale = (float) i1 / height;
                //获取渐变数值
                bgView.setAlpha((scale));

            } else {
                bgView.setAlpha(1f);
            }

        });

    }

    private void set_loc() {
        List<HotCity> hotCities = new ArrayList<>();
        hotCities.add(new HotCity("北京", "北京", "101010100"));
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));
        CityPicker.from(requireActivity())
                .enableAnimation(true)
                .setAnimationStyle(R.style.CityPickerStyleCustomAnim)
                .setLocatedCity(null)
                .setHotCities(hotCities)
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
//                        tv1.setText(String.format("当前城市：%s，%s", data.getName(), data.getCode()));
                        DwCity dwCity = new DwCity();
                        dwCity.setCode(data.getCode());
                        dwCity.setPinyin(data.getPinyin());
                        dwCity.setProvince(data.getProvince());
                        dwCity.setName(data.getName());
                        MmkvUtils.getInstance().set_common_json2("city", dwCity);
                        tv1.setText(data.getName());
//                        Toast.makeText(
//                                getActivity(),
//                                String.format("点击的数据：%s，%s", data.getName(), data.getCode()),
//                                Toast.LENGTH_SHORT)
//                                .show();
                        // refreshjiekoubufen
//                        ToastUtils.showLong("shuaxin~");
                        donetwork();
                    }

                    @Override
                    public void onCancel() {
//                        Toast.makeText(getApplicationContext(), "取消选择", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLocate() {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                CityPicker.from(requireActivity()).locateComplete(new LocatedCity("深圳", "广东", "101280601"), LocateState.SUCCESS);
                                LocationBean bean = MmkvUtils.getInstance().get_common_json("location", LocationBean.class);
                                if (bean != null) {
                                    CityPicker.from(requireActivity()).locateComplete(new LocatedCity(bean.getCity(), bean.getProvince(), bean.getAdCode()), LocateState.SUCCESS);
                                }
                            }
                        }, 1000);
                    }
                })
                .show();
    }

    // 组件数据格式
    public List<BjyyActFragment251Bean> getMultipleItemData(BjyyBeanYewu1 data) {
//        BjyyBeanYewu1 data = MmkvUtils.getInstance().get_common_json("BjyyActFragment2", BjyyBeanYewu1.class);
        List<BjyyActFragment251Bean> list = new ArrayList<>();
        if (data != null && data.getData().size() > 0) {
            List<BjyyBeanYewu2> data1 = data.getData();
            for (int i = 0; i < data1.size(); i++) {
                BjyyBeanYewu3 mBean = new BjyyBeanYewu3(data1.get(i).getId(), data1.get(i).getImg(), data1.get(i).getName(), data1.get(i).getUrl(), data1.get(i).isEnable());
                BjyyActFragment251Bean bean1;
//                if ("党建引领".equals(mBean.getName()) || "3522174196056603551".equals(mBean.getId())) {
//                    bean1 = new BjyyActFragment251Bean(BjyyActFragment251Bean.STYLE_DJYL, mBean);
//                } else {
//                    bean1 = new BjyyActFragment251Bean(BjyyActFragment251Bean.style1, mBean);
//                }
                if (i == 0) {
                    bean1 = new BjyyActFragment251Bean(BjyyActFragment251Bean.STYLE_DJYL, mBean);
                } else {
                    bean1 = new BjyyActFragment251Bean(BjyyActFragment251Bean.style1, mBean);
                }
                list.add(bean1);
                List<BjyyBeanYewu3> data2 = data1.get(i).getData();
                //
                for (int j = 0; j < data2.size(); j++) {
                    BjyyBeanYewu3 bean2 = new BjyyBeanYewu3(
                            data2.get(j).getId(),
                            data2.get(j).getImg(),
                            data2.get(j).getName(),
                            data2.get(j).getUrl(),
                            data2.get(j).isEnable());
                    BjyyActFragment251Bean bean3 = new BjyyActFragment251Bean(BjyyActFragment251Bean.style5, bean2);
                    list.add(bean3);
                }
                // 此处为填写空白区域
                if (data2.size() % spanCount != 0) {
                    for (int n = 0; n < (spanCount - (data2.size() % spanCount)); n++) {
                        BjyyBeanYewu3 bean2 = new BjyyBeanYewu3(
                                "-1",
                                "",
                                "",
                                "",
                                false);
                        BjyyActFragment251Bean bean3 = new BjyyActFragment251Bean(BjyyActFragment251Bean.style5, bean2);
                        list.add(bean3);
                    }
                }
                BjyyBeanYewu3 mBean1 = new BjyyBeanYewu3(data1.get(i).getId(), "", data1.get(i).getName(), data1.get(i).getUrl(), false);
                BjyyActFragment251Bean bean11 = new BjyyActFragment251Bean(BjyyActFragment251Bean.style11, mBean1);
                list.add(bean11);
            }
        }
        return list;
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
//        ToastUtils.showLong("下拉刷新啦");
        donetwork();
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

    private void setData() {
        BjyyBeanYewu1 bjyyBeanYewu1 = MmkvUtils.getInstance().get_common_json("首页应用列表", BjyyBeanYewu1.class);
//        //TODO 现在返回的数据第一项是全部如果以后有改动请修改该逻辑
//        Iterator<BjyyBeanYewu2> iterator = bjyyBeanYewu1.getData().iterator();
//        while (iterator.hasNext()) {
//            BjyyBeanYewu2 data = iterator.next();
//            if ("全部".equals(data.getName())) {
//                iterator.remove();
//            }
//        }
        mList = new ArrayList<>();
        mList = getMultipleItemData(bjyyBeanYewu1);
        mAdapter.setNewData(mList);


    }

    @Override
    public void OnsyyySuccess(BjyyBeanYewu1 bean) {
//        bjyyBeanYewu1 = getInitData();
        bjyyBeanYewu1 = bean;
        MmkvUtils.getInstance().set_common_json2("首页应用列表", bjyyBeanYewu1);
        setData();
    }

    @Override
    public void OnsyyyNodata(String bean) {
        setData();
    }

    @Override
    public void OnsyyyFail(String msg) {
        setData();
    }


    @Override
    public void OnFBannerSuccess(FBannerBean bean) {
        fBannerBean = bean;
        set_lunbo_data();
    }

    @Override
    public void OnFBannerNodata(String bean) {
//        ToastUtils.showLong(bean);
        fBannerBean = new FBannerBean();
        List<BjyyBeanYewu3> mlist = new ArrayList<>();
        mlist.add(new BjyyBeanYewu3("", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862286564142395.png", "", "", false));
        fBannerBean.setData(mlist);
        set_lunbo_data();
    }

    @Override
    public void OnFBannerFail(String msg) {
//        ToastUtils.showLong(msg);
        fBannerBean = new FBannerBean();
        List<BjyyBeanYewu3> mlist = new ArrayList<>();
        mlist.add(new BjyyBeanYewu3("", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862286564142395.png", "", "", false));
        fBannerBean.setData(mlist);
        set_lunbo_data();
    }

//    @Override
//    public void onProgress(float percent) {
//        Log.e("progress", percent + "");
//        if (percent < 0.5) {
//            bgView.setVisibility(View.VISIBLE);
//        } else {
//            bgView.setVisibility(View.GONE);
//        }
//        banner.setAlpha(percent);
//        bgView.setAlpha(1 - percent);
//    }

    @Override
    public void OnFWechatJump1Success(FWechatJumpBean bean) {
        // 调用
        String appId = bean.getAppletId(); // 填应用AppId
        IWXAPI api = WXAPIFactory.createWXAPI(getActivity(), appId);
//        IWXAPI api = WXAPIFactory.createWXAPI(getActivity(), "wxf03542b770d557e6");
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = bean.getFrontUrl(); // 填小程序原始id
        req.path = bean.getAppletPath();                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);
    }

    @Override
    public void OnFWechatJump1Nodata(String bean) {

    }

    @Override
    public void OnFWechatJump1Fail(String msg) {

    }

    private BlockPuzzleDialog blockPuzzleDialog;

    @Override
    public void OnFAppCheckUseSuccess(FAppCheckUseBean bean) {
        MProgressDialog.dismissProgress();
        if (bean == null) {
            return;
        }
        String xz_token = bean.getXz_token();
        if (!TextUtils.isEmpty(xz_token)) {
            SPUtils.getInstance().put("token_fc", xz_token);
        }
        if (bean.isMaintain()) {
            XPopup.Builder builder = new XPopup.Builder(getContext());
            builder.maxWidth((int) (XPopupUtils.getWindowWidth(getContext()) * 0.8f))
                    .dismissOnTouchOutside(false)
                    .isDestroyOnDismiss(true);
            ConfirmPopupView confirmPopupView = builder.asConfirm(bean.getMaintainTitle(), bean.getMaintainMessage(),
                    null, "确定",
                    new OnConfirmListener() {
                        @Override
                        public void onConfirm() {

                        }
                    }, null, true, R.layout.app_maintain_confim_popup);

            GlideRoundImageView ivBg = confirmPopupView.findViewById(R.id.iv_pop_bg);
            ivBg.setCornerRadius(12);
            ivBg.loadImage(bean.getMaintainBackgroundUrl(), 0);
            confirmPopupView.show();

            return;
        }

        // 灯塔2.0跳转老版本，后续要改了
        if (bean.getAddress().contains("com.fosung.lighthouse.dt2")) {
//            if (TextUtils.isEmpty(SPUtils.getInstance().getString("token_dt20"))) {
//
//            } else {
//                if (bean.getAddress().startsWith("http")) {
//                    Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity");
//                    intent.putExtra(AgentWebFragment.URL_KEY, bean.getAddress());
//                    startActivity(intent);
////                    HiosHelperNew.resolveAd(getActivity(), getActivity(), addressBean.getmBean().getUrl());
//                } else if (bean.getAddress().startsWith("data")) {
//                    HiosHelperNew.resolveAd(getActivity(), getActivity(),
//                            bean.getAddress().replace(
//                                    "com.fosung.lighthouse.dt2",
//                                    AppUtils.getAppPackageName()));
//                }
//            }
            blockPuzzleDialog = new BlockPuzzleDialog(getActivity());
            blockPuzzleDialog.setOnResultsListener(new BlockPuzzleDialog.OnResultsListener() {
                @Override
                public void onResultsClick(String result) {
                    if (bean.getAddress().startsWith("http")) {
                        Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity");
                        intent.putExtra(AgentWebFragment.URL_KEY, bean.getAddress());
                        startActivity(intent);
//                    HiosHelperNew.resolveAd(getActivity(), getActivity(), addressBean.getmBean().getUrl());
                    } else {
                        HiosHelperNew.resolveAd(getActivity(), getActivity(),
                                bean.getAddress().replace(
                                        "com.fosung.lighthouse.dt2",
                                        AppUtils.getAppPackageName()));
                    }
                }
            });
            blockPuzzleDialog.show();
            return;
        }

        if (bean.getAddress().startsWith("gh")) {
            //
            fWechatJump1Presenter.getWechatJump1(AppCommonUtils.auth_url + "/getAppletpath", bean.getAddress(), currentId);
        } else if (bean.getAddress().startsWith("http")) {
            Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity");
            intent.putExtra(AgentWebFragment.URL_KEY, bean.getAddress());
            intent.putExtra("isHideBar", false);
            startActivity(intent);
//                    HiosHelperNew.resolveAd(getActivity(), getActivity(), addressBean.getmBean().getUrl());
        } else {
            ToastUtils.showLong("地址配置错误");
//            HiosHelperNew.resolveAd(getActivity(), getActivity(), bean.getAddress().replace("null", ""));
        }

    }

    @Override
    public void OnFAppCheckUseNodata(String bean) {
        MProgressDialog.dismissProgress();
        ToastUtils.showLong(bean);

    }

    @Override
    public void OnFAppCheckUseFail(String msg) {
        MProgressDialog.dismissProgress();
        ToastUtils.showLong(msg);
    }

//    @Override
//    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//        float percent = MathUtils.clamp(scrollY, 0f, height) / height;
//        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
//        if (scrollY > height) {
//            layoutParams.setMargins(0, height, 0, 0);
//        } else {
//            layoutParams.setMargins(0, scrollY, 0, 0);
//            bgView.setVisibility(View.VISIBLE);
//        }
//        v.setLayoutParams(layoutParams);
//        if (percent > 0.5) {
//            bgView.setVisibility(View.VISIBLE);
//        } else {
//            bgView.setVisibility(View.GONE);
//        }
//        banner.setAlpha(1 - percent);
//        bgView.setAlpha(percent);
//        Log.e("scroll", scrollY + "");
//    }

}
