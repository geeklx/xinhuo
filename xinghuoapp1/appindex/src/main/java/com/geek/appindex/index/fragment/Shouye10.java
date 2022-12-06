package com.geek.appindex.index.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appindex.R;
import com.geek.appindex.adapters.Shouye10Adapter1;
import com.geek.biz1.BanbenCommonUtilszbiz1;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.biz1.bean.BjyyBeanYewu4;
import com.geek.biz1.presenter.FCate1Presenter;
import com.geek.biz1.view.FCate1View;
import com.geek.libbase.base.SlbBaseLazyFragmentNewCate;
import com.geek.libbase.emptyview.EmptyViewNewNew;
import com.geek.libbase.fenlei.fenlei1.FenleiAct1CateBean1;
import com.geek.libbase.widgets.ViewPagerSlide;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;
import com.geek.tablib.tablayout.SlidingTabLayout;
import com.haier.cellarette.baselibrary.toasts3.MProgressDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Shouye10 extends SlbBaseLazyFragmentNewCate implements View.OnClickListener, FCate1View {

    private TextView text_title;
    private TextView text_title2;
    private SlidingTabLayout tabLayout;
    private ViewPagerSlide viewpager;
    private Shouye10Adapter1 fenleiViewPagerAdapter1;
    private List<BjyyBeanYewu3> dataList;
    private FCate1Presenter fCate1Presenter;
    private ConstraintLayout clContent;
    private EmptyViewNewNew emptyView;

    //private ArrayList<Fragment> mFragmentList;

    public static Shouye10 getInstance(Bundle bundle) {
        Shouye10 mEasyWebFragment = new Shouye10();
        if (bundle != null) {
            mEasyWebFragment.setArguments(bundle);
        }
        return mEasyWebFragment;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if (getChildFragmentManager() != null) {
//            Parcelable p = getChildFragmentManager().saveAllState();
//            if (p != null) {
//                outState.putParcelable("FragmentActivity.FRAGMENTS_TAG", p);
//            }
//        }
    }

    @Override
    protected void updateArgumentsData(Object parentCategory) {

    }

    @Override
    protected void onReceiveMsg(Intent intent) {
        dataList = (List<BjyyBeanYewu3>) intent.getSerializableExtra("dingwei");
        if (dataList != null) {
            setNewData(dataList);
        }
    }

    @Override
    public void call(Object value) {
//        String ids = (String) value;
//        ToastUtils.showLong(ids);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shouye10;
//        return 0;
    }

    private String tablayoutId;

    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
//        if (savedInstanceState != null) {
//            boolean cachedId = savedInstanceState.getBoolean(SAVED_CURRENT_ID, false);
//            if (cachedId) {
//                return;
//            }
//        }
        text_title = rootView.findViewById(R.id.tv1);
        text_title2 = rootView.findViewById(R.id.tv2);
        clContent = rootView.findViewById(R.id.cl_content);
        emptyView = rootView.findViewById(R.id.emptyView);
        if (emptyView != null) {
            emptyView.bind(clContent).setRetryListener(new EmptyViewNewNew.RetryListener() {
                @Override
                public void retry() {
                    donetWork();
                }
            });
            emptyView.notices("暂无数据", "网络出了点问题", "正在加载…", "");
        }
        text_title.setOnClickListener(this);
        text_title2.setOnClickListener(this);
        BanbenCommonUtilszbiz1.changeUrl(getActivity(), text_title, text_title2, null);
        tabLayout = rootView.findViewById(R.id.tl_fenlei1);
        viewpager = rootView.findViewById(R.id.vs_fenlei1);
        if (getArguments() != null) {
            tablayoutId = getArguments().getString("tablayoutId");
            MyLogUtil.e("tablayoutId->", "onCreate->" + tablayoutId);
            if (tabLayout != null && !TextUtils.isEmpty(tablayoutId)) {
                donetWork();
            }

//            // ces
//            Intent msgIntent = new Intent();
//            msgIntent.setAction("SlbBaseLazyFragmentNewCate");
//            msgIntent.putExtra("dingwei", (Serializable) addList1());
//            LocalBroadcastManagers.getInstance(getActivity()).sendBroadcast(msgIntent);
        }
    }

    private void donetWork() {
        if (fCate1Presenter == null) {
            fCate1Presenter = new FCate1Presenter();
            fCate1Presenter.onCreate(this);
        }
        //MProgressDialog.showProgress(getActivity(), "加载中...");
        fCate1Presenter.getcate1list(AppCommonUtils.auth_url + "/navigationtop",
                "", "", "", "", AppCommonUtils.get_location_cityname(), tablayoutId);
    }

    public List<FenleiAct1CateBean1> addList1() {
        List<FenleiAct1CateBean1> mList = new ArrayList<>();
        mList.add(new FenleiAct1CateBean1("id1", "推荐", "com.geek.appindex.news.fragment.RecommandFragment", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", com.geek.libbase.R.drawable.slb_run1, 0, false));
        mList.add(new FenleiAct1CateBean1("id2", "党建动态", "com.geek.appindex.news.fragment.DJDTFragment", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", com.geek.libbase.R.drawable.slb_run1, 0, false));
//        mList.add(new FenleiAct1CateBean1("id3", "通知公告", "com.geek.appindex.news.fragment.TZGGFragment", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", com.geek.libbase.R.drawable.slb_run1, 0, false));
//        mList.add(new FenleiAct1CateBean1("id4", "乡镇要闻", "com.geek.appindex.news.fragment.XZYWFragment", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", com.geek.libbase.R.drawable.slb_run1, 0, false));
//        mList.add(new FenleiAct1CateBean1("id5", "政策文件", "com.geek.appindex.news.fragment.ZCWJFragment", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", com.geek.libbase.R.drawable.slb_run1, 0, false));

//        mList.add(new FenleiAct1CateBean1("id1", "全部", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2.png", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", com.geek.libbase.R.drawable.slb_run1, 0, false));
//        mList.add(new FenleiAct1CateBean1("id2", "自建栏目1", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2.png", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", com.geek.libbase.R.drawable.slb_run1, 0, false));
//        mList.add(new FenleiAct1CateBean1("id3", "自建栏目2", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2.png", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", com.geek.libbase.R.drawable.slb_run1, 0, false));
//        mList.add(new FenleiAct1CateBean1("id4", "自建栏目3", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2.png", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", com.geek.libbase.R.drawable.slb_run1, 0, false));
//        mList.add(new FenleiAct1CateBean1("id5", "自建栏目4", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2.png", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", com.geek.libbase.R.drawable.slb_run1, 0, false));
//        mList.add(new FenleiAct1CateBean1("id6", "自建栏目5", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2.png", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", com.geek.libbase.R.drawable.slb_run1, 0, false));
//        mList.add(new FenleiAct1CateBean1("id7", "自建栏目6", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2.png", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", com.geek.libbase.R.drawable.slb_run1, 0, false));
        return mList;
    }

    public List<BjyyBeanYewu3> addList2() {
        List<BjyyBeanYewu3> mList = new ArrayList<>();
        mList.add(new BjyyBeanYewu3("id1", "", "com.geek.appindex.news.fragment.RecommandFragment", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", "推荐", Shouye10Adapter1.TAG_RECOMMAND, false));
        mList.add(new BjyyBeanYewu3("id2", "", "com.geek.appindex.news.fragment.DJDTFragment", "http://119.188.115.252:8090/resource-handle/uploads/image/ic_table_2_u.png", "党建动态", Shouye10Adapter1.TAG_DJDT, false));
        return mList;
    }

    private void setNewData(List<BjyyBeanYewu3> mlist) {
//        mFragmentList = new ArrayList<>();
//        mFragmentList.clear();
//        for (int i = 0; i < mlist.size(); i++) {
//            mFragmentList.add(ZXFactory.INSTANCE.getFragmentByURL(mlist.get(i).getUrl()));
//        }
        // 标题bufen
        String[] titlesString = new String[mlist.size()];
        for (int i = 0; i < mlist.size(); i++) {
            titlesString[i] = mlist.get(i).getName();
        }
        if (fenleiViewPagerAdapter1 == null) {
            viewpager.removeAllViews();
            fenleiViewPagerAdapter1 = new Shouye10Adapter1<BjyyBeanYewu3>(
//                    getActivity().getSupportFragmentManager(),
                    getChildFragmentManager(),
                    getActivity(),
                    titlesString,
                    mlist,
                    Shouye10Adapter1.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            viewpager.setOffscreenPageLimit(mlist.size());
            viewpager.setScroll(true);
            viewpager.setAdapter(fenleiViewPagerAdapter1);
        } else {
            fenleiViewPagerAdapter1.setdata(titlesString, mlist);
            fenleiViewPagerAdapter1.notifyDataSetChanged();
        }
        tabLayout.setViewPager(viewpager);
        viewpager.setCurrentItem(current_pos);
        //        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
//            @Override
//            public void onTabSelect(int position) {
//                current_pos = position;
//            }
//
//            @Override
//            public void onTabReselect(int position) {
//
//            }
//        });
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current_pos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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


    @Override
    public void onDestroy() {
        BanbenCommonUtilszbiz1.changeUrl_ondes();
        super.onDestroy();
        if (fCate1Presenter != null) {
            fCate1Presenter.onDestory();
        }
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void OnCate1Success(String authorizedType, BjyyBeanYewu4 bean) {
        //MProgressDialog.dismissProgress();
        emptyView.success();
//        setNewData(bean.getData());
        setNewData(addList2());
    }

    @Override
    public void OnCate1Nodata(String bean) {
        //MProgressDialog.dismissProgress();
        if (emptyView != null) {
            emptyView.nodata();
        }
    }

    @Override
    public void OnCate1Fail(String msg) {
        //MProgressDialog.dismissProgress();
        if (emptyView != null) {
            emptyView.errorNet();
        }
    }
}