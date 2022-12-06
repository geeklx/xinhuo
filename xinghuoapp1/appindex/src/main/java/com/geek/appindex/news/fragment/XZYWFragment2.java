package com.geek.appindex.news.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.appindex.R;
import com.geek.appindex.news.adapter.MkFLunboAdapter1;
import com.geek.appindex.news.adapter.MkFLunboAdapter2;
import com.geek.appindex.news.adapter.ZXAdapter;
import com.geek.appindex.news.adapter.ZXAdapter2;
import com.geek.appindex.news.model.ZXConvertData;
import com.geek.appindex.news.model.ZXTYPE;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.biz1.bean.home.ClassificationBean;
import com.geek.biz1.bean.home.ClassificationListBean;
import com.geek.biz1.bean.home.ClassificationListData;
import com.geek.biz1.presenter.home.ClassificationPresenter;
import com.geek.biz1.view.home.ClassificationView;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libbase.emptyview.EmptyViewNewNew;
import com.just.agentweb.geek.fragment.AgentWebFragment;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

public class XZYWFragment2 extends SlbBaseLazyFragmentNew implements ClassificationView {

    private ZXAdapter2 mAdapter1;
    //private ConstraintLayout mClTop1;
    private RecyclerView rv1;
    private EmptyViewNewNew emptyView;
    private ConstraintLayout clContainer;
    // private Banner<ClassificationBean, MkFLunboAdapter2> banner;
    private ClassificationPresenter mPresenter;

    public static XZYWFragment2 getInstance(BjyyBeanYewu3 bean) {
        XZYWFragment2 fragment = new XZYWFragment2();
        Bundle bundle = new Bundle();
        bundle.putSerializable("feileiBean", bean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_xzyw;
    }

    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        rv1 = rootView.findViewById(R.id.rv1);
        //mClTop1 = rootView.findViewById(R.id.cl_top1);
        //banner = rootView.findViewById(R.id.banner);
        emptyView = rootView.findViewById(R.id.emptyView);
        clContainer = rootView.findViewById(R.id.cl_container);
        if (emptyView != null) {
            emptyView.bind(clContainer).setRetryListener(new EmptyViewNewNew.RetryListener() {
                @Override
                public void retry() {
                    loadData();
                }
            });
            emptyView.notices("暂无数据", "网络出了点问题", "正在加载…", "");
        }

        initView();
    }

    private void loadData() {
        if (emptyView != null && mPresenter != null) {
            emptyView.loading();
            mPresenter.getClassificationData("1003", "1");
        }
    }

    private void initView() {
        mAdapter1 = new ZXAdapter2(new ArrayList<ZXConvertData>(), false);
        rv1.setAdapter(mAdapter1);
        //lbAdapter = MkFLunboAdapter1(mutableListOf())
//        banner?.setAdapter(lbAdapter)
//        banner?.indicator = CircleIndicator(activity)
//        banner?.setIndicatorGravity(IndicatorConfig.Direction.RIGHT)
//        banner?.setIndicatorNormalColor(Color.parseColor("#C3C3C3"))
//        banner?.setIndicatorMargins(
//            IndicatorConfig.Margins(
//                0, 0,
//                BannerConfig.INDICATOR_MARGIN, BannerUtils.dp2px(12f)
//            )
//        )
//
//        banner?.setOnBannerListener { data, position ->
//            ToastUtils.showShort("当前点击的是$position")
//        }
        onclickAdd();
    }

    private void onclickAdd() {
        if (mAdapter1 != null) {
            mAdapter1.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                    List<ZXConvertData> data = mAdapter1.getData();
//                    if (data != null && data.size() > 0) {
//                        String url = data.get(position).getData().getUrl();
//                        Log.e("aaaaaa", url);
//                        Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity");
//                        if (url != null && "" != url && "null" != url) {
//                            intent.putExtra(AgentWebFragment.URL_KEY, url);
//                            startActivity(intent);
//                        }
//
//                    }

                    //startActivity(Intent(requireActivity(), DKMainActivity::class.java));
//            HiosHelperNew.resolveAd(
//                requireActivity(), requireActivity(),
//                "dataability://${AppUtils.getAppPackageName()}.hs.act.slbapp.TikTokListActivityDK{act}"
//            )
                    handleItemClick(position);
                }
            });

            mAdapter1.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                    List<ZXConvertData> data = mAdapter1.getData();
//                    if (data != null && data.size() > 0) {
//                        String url = data.get(position).getData().getUrl();
//                        Log.e("aaaaaa", url);
//                        Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity");
//                        if (url != null && "" != url && "null" != url) {
//                            intent.putExtra(AgentWebFragment.URL_KEY, url);
//                            startActivity(intent);
//                        }
//
//                    }
                    handleItemClick(position);
                }
            });
        }
    }

    private void handleItemClick(int position) {

        List<ZXConvertData> data = mAdapter1.getData();
        if (data.size() > 0) {
            ZXConvertData zxConvertData = data.get(position);
            if (zxConvertData != null && zxConvertData.getType() == ZXTYPE.TYPE_ITEM) {
                ClassificationBean data1 = zxConvertData.getData();
                if (data1 != null) {
                    String url = data1.getUrl();
                    Log.e("aaaaaa", url);
                    Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity");
                    if (url != null && !"".equals(url) && !"null".equals(url)) {
                        intent.putExtra(AgentWebFragment.URL_KEY, url);
                        startActivity(intent);
                    }
                }

            }
        }
    }

    @Override
    protected void initData() {
        mPresenter = new ClassificationPresenter();
        mPresenter.onCreate(this);
//        val mBannerList = mutableListOf<BjyyBeanYewu3>()
//        mBannerList.add(
//            BjyyBeanYewu3(
//                "",
//                "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862286564142395.png",
//                "孙村吴翠莲，30年如一日照顾瘫痪婆婆",
//                "",
//                false
//            )
//        )
//        mBannerList.add(
//            BjyyBeanYewu3(
//                "",
//                "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862378905937488.png",
//                "孙村吴翠莲，30年如一日照顾瘫痪婆婆",
//                "",
//                false
//            )
//        )
//        mBannerList.add(
//            BjyyBeanYewu3(
//                "",
//                "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521863886959548956.png",
//                "孙村吴翠莲，30年如一日照顾瘫痪婆婆",
//                "",
//                false
//            )
//        )
//        lbAdapter?.setDatas(mBannerList)
        loadData();
    }


    @Override
    public void onClassficationDataSuccess(ClassificationListBean bean) {
        List<ZXConvertData> list = new ArrayList<>();
        List<ClassificationListData> datas = bean.getData();
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                ClassificationListData classificationListData = datas.get(i);
                list.add(new ZXConvertData(classificationListData.getName(), classificationListData.getBelongId(),
                        classificationListData.getId(), classificationListData.getBelongTypeId(), classificationListData.getNotice()));
                List<ClassificationBean> list1 = classificationListData.getList();
                if (i == 0) {
                    if (list1 != null && list1.size() > 0) {
                        list.add(new ZXConvertData(list1));
                    }
                } else {
                    if (list1 != null && list1.size() > 0) {
                        for (int j = 0; j < list1.size(); j++) {
                            ClassificationBean classificationBean = list1.get(j);
                            list.add(new ZXConvertData(classificationBean));
                        }
                    }
                }
            }
        }

        if (list.isEmpty()) {
            if (emptyView != null) {
                emptyView.nodata();
            }

        } else if (emptyView != null && mAdapter1 != null) {

            emptyView.success();
            mAdapter1.setNewData(list);
        }
    }

    @Override
    public void onClassficationDataNoData(String msg) {
        if (emptyView != null) {
            emptyView.nodata();
        }

    }

    @Override
    public void onClassficationDataFail(String msg) {
        if (emptyView != null) {
            emptyView.errorNet();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestory();
        }

    }
}
