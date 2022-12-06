package com.geek.appindex.news.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
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
import com.geek.biz1.bean.home.ClassificationListBean;
import com.geek.biz1.bean.home.ClassificationListData;
import com.geek.biz1.presenter.home.ClassificationPresenter;
import com.geek.biz1.view.home.ClassificationView;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libbase.emptyview.EmptyViewNewNew;
import com.just.agentweb.geek.fragment.AgentWebFragment;

import java.util.ArrayList;
import java.util.List;

public class ZCWJFragment2 extends SlbBaseLazyFragmentNew implements ClassificationView {
    private ZXAdapter2 mAdapter1;
    private RecyclerView rv1;
    private LinearLayout ll;
    private EmptyViewNewNew emptyView;
    private ClassificationPresenter mPresenter;

    public static ZCWJFragment2 getInstance(BjyyBeanYewu3 bean) {
        ZCWJFragment2 fragment = new ZCWJFragment2();
        Bundle bundle = new Bundle();
        bundle.putSerializable("feileiBean", bean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tzgg2;
    }

    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        initView(rootView);
    }

    private void initView(View rootView) {
        rv1 = rootView.findViewById(R.id.rv1);
        ll = rootView.findViewById(R.id.ll);
        emptyView = rootView.findViewById(R.id.emptyView);
        emptyView.bind(ll).setRetryListener(new EmptyViewNewNew.RetryListener() {
            @Override
            public void retry() {
                loadData();
            }
        });
        emptyView.notices("暂无数据", "网络出了点问题", "正在加载…", "");
        mPresenter = new ClassificationPresenter();
        mPresenter.onCreate(this);
        mAdapter1 = new ZXAdapter2(new ArrayList<>(), false);
        rv1.setAdapter(mAdapter1);
        onclickAdd();
    }

    private void loadData() {
        if (emptyView != null && mPresenter != null) {
            emptyView.loading();
            mPresenter.getClassificationData("1004", "1");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestory();
        }
    }


    @Override
    protected void initData() {
        super.initData();
        mPresenter = new ClassificationPresenter();
        mPresenter.onCreate(this);
        loadData();
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
    public void onClassficationDataSuccess(ClassificationListBean bean) {
        List<ZXConvertData> list = new ArrayList<>();
        List<ClassificationListData> datas = bean.getData();
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                ClassificationListData classificationListData = datas.get(i);
                list.add(new ZXConvertData(classificationListData.getName(), classificationListData.getBelongId(),
                        classificationListData.getId(), classificationListData.getBelongTypeId(), classificationListData.getNotice()));
                List<ClassificationBean> list1 = classificationListData.getList();
                if (list1 != null && list1.size() > 0) {
                    for (int j = 0; j < list1.size(); j++) {
                        ClassificationBean classificationBean = list1.get(j);
                        list.add(new ZXConvertData(classificationBean));
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
}
