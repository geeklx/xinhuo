package com.geek.appindex.news.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.appindex.R;
import com.geek.appindex.news.adapter.ZXAdapter;
import com.geek.appindex.news.adapter.ZXAdapter2;
import com.geek.appindex.news.model.ZXConvertData;
import com.geek.appindex.news.model.ZXTYPE;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.biz1.bean.home.ClassificationBean;
import com.geek.biz1.bean.home.ClassificationListByPageBean;
import com.geek.biz1.presenter.home.ClassificationByPagePresenter;
import com.geek.biz1.view.home.ClassificationByPageView;
import com.geek.libbase.baserecycleview.SlbBaseFragmentViewPageYewuList;
import com.just.agentweb.geek.fragment.AgentWebFragment;

import java.util.ArrayList;
import java.util.List;

public class DJDTFragment2 extends SlbBaseFragmentViewPageYewuList implements ClassificationByPageView {

    private ZXAdapter2 mAdapter;

    private ClassificationByPagePresenter mPresenter;

    public static DJDTFragment2 getInstance(BjyyBeanYewu3 bean) {
        DJDTFragment2 fragment = new DJDTFragment2();
        Bundle bundle = new Bundle();
        bundle.putSerializable("feileiBean", bean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void donetworkAdd() {
        emptyview1.loading();
        // 业务bufen
        init_adapter(mAdapter);
    }

    @Override
    protected void retryDataAdd() {
        refreshData();
    }

    @Override
    protected void onclickAdd() {
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                    List<ZXConvertData> data = mAdapter.getData();
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

            mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                    List<ZXConvertData> data = mAdapter.getData();
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
    protected void findviewAdd() {
        mPresenter = new ClassificationByPagePresenter();
        mPresenter.onCreate(this);
        recyclerView1.setLayoutManager(new GridLayoutManager(getActivity(), 1, RecyclerView.VERTICAL, false));
        mAdapter = new ZXAdapter2(new ArrayList<ZXConvertData>(), true);
        recyclerView1.setAdapter(mAdapter);
        emptyview1.notices("暂无数据", "网络出了点问题", "正在加载…", "");
    }

    @Override
    protected void emptyviewAdd() {
        emptyview1.loading();
        // 业务bufen
        init_adapter(mAdapter);

    }

    @Override
    protected void refreshLayoutAdd() {
        // 业务bufen
        init_adapter(mAdapter);
    }

    private void refreshData() {
        if (mPresenter != null) {
            mPresenter.getClassificationDataByPage(mNextRequestPage, PAGE_SIZE, "1001", "1");
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragmentyewulist1_common2;
    }

    @Override
    public void onClassficationDataSuccess(ClassificationListByPageBean bean) {
        ArrayList<ZXConvertData> list = new ArrayList<ZXConvertData>();


        if (bean != null) {
            List<ClassificationBean> datas = bean.getData();
            if (datas != null && datas.size() > 0) {
                for (int i = 0; i < datas.size(); i++) {
                    ClassificationBean classificationBean = datas.get(i);
                    list.add(new ZXConvertData(classificationBean));
                }
            }
        }
        OnOrderSuccess(list);
    }

    @Override
    public void onClassficationDataNoData(String msg) {
        OnOrderNodata();
    }

    @Override
    public void onClassficationDataFail(String msg) {
        OnOrderFail();
    }
}
