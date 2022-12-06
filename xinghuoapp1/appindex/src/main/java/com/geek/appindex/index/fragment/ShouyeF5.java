package com.geek.appindex.index.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.appcommon.adapter.MkFLunboAdapter;
import com.geek.liblocations.LocationBean;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.huadongyanzhengpop.BlockPuzzleDialog;
import com.geek.appindex.R;
import com.geek.appindex.addrecycleview.BjyyAct;
import com.geek.appindex.addrecycleview.BjyyUtils;
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
import com.geek.libbase.emptyview.EmptyViewNewNew;
import com.geek.libbase.viewpager2.bean.DataBean;
import com.geek.libbase.widgets.XRecyclerView;
import com.geek.libglide47.base.GlideRoundImageView;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.LocationUtils;
import com.geek.libutils.data.MmkvUtils;
import com.haier.cellarette.baselibrary.marqueelibrary.SimpleMF;
import com.haier.cellarette.baselibrary.marqueelibrary.SimpleMarqueeView;
import com.haier.cellarette.baselibrary.marqueelibrary.util.OnItemClickListener;
import com.haier.cellarette.baselibrary.toasts3.MProgressDialog;
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
import java.util.Random;

public class ShouyeF5 extends SlbBaseLazyFragmentNew implements FsyyyView, FWechatJump1View, FBannerView, FAppCheckUseView {

    public SimpleMarqueeView marqueeView3;
    public TextView tv1;
    public ImageView iv_sysm1;
    public LinearLayout ll_content;
    public EmptyViewNewNew emptyView;
    public XRecyclerView mRecyclerView;
    public BjyyActFragment251Adapter mAdapter;
    public List<BjyyActFragment251Bean> mList;
    public int spanCount = 4;// 几列数据
    public BjyyBeanYewu1 bjyyBeanYewu1;
    public Banner banner;
    //
    private FsyyyPresenter fsyyyPresenter;
    private FWechatJump1Presenter fWechatJump1Presenter;
    private FAppCheckUsePresenter fAppCheckUsePresenter;
    private FBannerPresenter fBannerPresenter;
    private FBannerBean fBannerBean;

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

    public static ShouyeF5 getInstance(Bundle bundle) {
        ShouyeF5 mEasyWebFragment = new ShouyeF5();
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


    // 接口数据bufen
    public BjyyBeanYewu1 getInitData() {
        BjyyBeanYewu1 data1 = new BjyyBeanYewu1();
        List<BjyyBeanYewu2> data2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            BjyyBeanYewu2 data3 = new BjyyBeanYewu2();
            List<BjyyBeanYewu3> data4 = new ArrayList<>();
//            for (int j = 0; j < new Random().nextInt(20); j++) {
            for (int j = 0; j < 8; j++) {
                String aaaaa = new Random().nextInt(100000) + "";
                BjyyBeanYewu3 bean = new BjyyBeanYewu3(
                        aaaaa,
                        "https://s2.51cto.com//wyfs02/M01/89/BA/wKioL1ga-u7QnnVnAAAfrCiGnBQ946_middle.jpg",
                        "geek" + aaaaa,
                        "dataability://" + AppUtils.getAppPackageName() + ".hs.act.slbapp.BjyyAct{act}?query1={s}" + aaaaa + "&query2={s}2a&query3={s}3a",
                        true);
                data4.add(bean);
            }
            data3.setId("-1");
            data3.setImg("");
            data3.setName("应用分类" + i);
            data3.setUrl("dataability://" + AppUtils.getAppPackageName() + ".hs.act.slbapp.BjyyAct{act}?query1={s}" + i + "&query2={s}2a&query3={s}3a");
            data3.setEnable(false);
            data3.setData(data4);
            data2.add(data3);
        }
        data1.setData(data2);
        return data1;
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
        marqueeView3.stopFlipping();
        LocalBroadcastManagers.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();

    }

    @Override
    public void call(Object value) {
        BjyyActFragment251Bean bjyyBeanYewu3 = (BjyyActFragment251Bean) value;
        if (bjyyBeanYewu3 != null) {
//            mAdapter.bjyyActFragment2Style5Provider.setCurrentPos(bjyyBeanYewu3.getmBean().getId(), View.VISIBLE);
//            mAdapter.notifyDataSetChanged();
            BjyyBeanYewu1 beanNew1 = BjyyUtils.choose_data(bjyyBeanYewu1, bjyyBeanYewu3.getmBean().getId(), false);
            mList = getMultipleItemData(beanNew1);
            mAdapter.setNewData(mList);
            currentIds.remove(bjyyBeanYewu3.getmBean().getId());
        }
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
        return R.layout.fragment_shouyef5;
    }

    private String tablayoutId;

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
        fBannerPresenter.getBannerList("/gwapi/workbenchserver", "1008", "1");//智慧社区

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
                    "",
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

    private void findview(View rootView) {
        marqueeView3 = rootView.findViewById(R.id.marqueeView3);
        banner = rootView.findViewById(R.id.banner);
        tv1 = rootView.findViewById(R.id.tv1);
        iv_sysm1 = rootView.findViewById(R.id.iv_sysm1);
        iv_sysm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 扫码
//                startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.Saoma3CommonScanActivity1"));
                Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.SearchListActivity1");
                intent.putExtra("search_key", "");
                startActivity(intent);
            }
        });

        ll_content = rootView.findViewById(R.id.ll_content);
        emptyView = rootView.findViewById(R.id.emptyView);
        emptyView.bind(ll_content).setRetryListener(new EmptyViewNewNew.RetryListener() {
            @Override
            public void retry() {
                donetwork();
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

                MProgressDialog.showProgress(getActivity(), "请稍等...");
                fAppCheckUsePresenter.getAppCheckUse("/gwapi/workbenchserver/api/workbench/ismaintain", currentId);
            }
        });
        //
        //
        SimpleMF<Spanned> marqueeFactory3 = new SimpleMF<>(getActivity());
        List<Spanned> datas3 = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            datas3.add(Html.fromHtml("<font color=\"#E5E5E5\">庆祝祖国</font>-<font color=\"#E60000\">十一国庆节</font>", Html.FROM_HTML_MODE_COMPACT));
            datas3.add(Html.fromHtml("<font color=\"#E5E5E5\">庆祝祖国</font>-<font color=\"#E60000\">八一建党节</font>", Html.FROM_HTML_MODE_COMPACT));
            datas3.add(Html.fromHtml("<font color=\"#E5E5E5\">庆祝祖国</font>-<font color=\"#E60000\">七一建军节</font>", Html.FROM_HTML_MODE_COMPACT));
            datas3.add(Html.fromHtml("<font color=\"#E5E5E5\">庆祝祖国</font>-<font color=\"#E60000\">春节到</font>", Html.FROM_HTML_MODE_COMPACT));
        }
        marqueeFactory3.setData(datas3);
        marqueeView3.setMarqueeFactory(marqueeFactory3);
        marqueeView3.setOnItemClickListener(new OnItemClickListener<TextView, Spanned>() {
            @Override
            public void onItemClickListener(TextView mView, Spanned mData, int mPosition) {
                Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.SearchListActivity1");
                intent.putExtra("search_key", mData.toString());
                startActivity(intent);
            }
        });
        marqueeView3.startFlipping();
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
                BjyyActFragment251Bean bean1 = new BjyyActFragment251Bean(BjyyActFragment251Bean.style1, mBean);
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

    private void setData(boolean error) {
        BjyyBeanYewu1 bjyyBeanYewu1 = MmkvUtils.getInstance().get_common_json("ShouyeF5应用列表", BjyyBeanYewu1.class);
        mList = new ArrayList<>();
        mList = getMultipleItemData(bjyyBeanYewu1);

        if (mList.size() == 0 && error) {
            emptyView.errorNet();
        } else if (mList.size() == 0) {
            emptyView.nodata();
        } else {
            emptyView.success();
            mAdapter.setNewData(mList);
        }
    }

    @Override
    public void OnsyyySuccess(BjyyBeanYewu1 bean) {
        bjyyBeanYewu1 = getInitData();
        bjyyBeanYewu1 = bean;
        MmkvUtils.getInstance().set_common_json2("ShouyeF5应用列表", bjyyBeanYewu1);
        setData(false);
    }

    @Override
    public void OnsyyyNodata(String bean) {
        setData(false);
    }

    @Override
    public void OnsyyyFail(String msg) {
        setData(true);
    }

    @Override
    public void OnFBannerSuccess(FBannerBean bean) {
        fBannerBean = bean;
        set_lunbo_data();
    }

    @Override
    public void OnFBannerNodata(String bean) {
        ToastUtils.showLong(bean);
    }

    @Override
    public void OnFBannerFail(String msg) {
        ToastUtils.showLong(msg);
    }

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
            fWechatJump1Presenter.getWechatJump1("/gwapi/workbenchserver/api/workbench/getAppletpath", bean.getAddress(), currentId);
        } else if (bean.getAddress().startsWith("http")) {
            Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity");
//            intent.putExtra(AgentWebFragment.URL_KEY, "http://192.168.2.250:9527/organization/home");
            intent.putExtra(AgentWebFragment.URL_KEY, bean.getAddress());
            intent.putExtra("isHideBar", false);
            startActivity(intent);
//                    HiosHelperNew.resolveAd(getActivity(), getActivity(), addressBean.getmBean().getUrl());
        } else {
            ToastUtils.showLong("地址配置错误");
//            HiosHelperNew.resolveAd(getActivity(), getActivity(), bean.getAddress().replace("null", ""));
        }

    }

    private BlockPuzzleDialog blockPuzzleDialog;

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
}
