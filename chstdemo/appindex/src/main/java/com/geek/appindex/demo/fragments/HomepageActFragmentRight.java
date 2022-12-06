package com.geek.appindex.demo.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.widget.SpaceItemDecoration;
import com.geek.appindex.R;
import com.geek.appindex.adapter.MkCateAdapter2;
import com.geek.appindex.web.fragments.MkWebActFragment1;
import com.geek.biz1.bean.UserInfoBean;
import com.geek.biz1.bean.cakes.CakeBeanYewu;
import com.geek.biz1.bean.home.NoticeYewu;
import com.geek.biz1.presenter.home.CakePresenter;
import com.geek.biz1.presenter.home.NoticePresenter;
import com.geek.biz1.view.home.CakeView;
import com.geek.biz1.view.home.NoticeView;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.haier.cellarette.baselibrary.marqueelibrary.SimpleMF;
import com.haier.cellarette.baselibrary.marqueelibrary.SimpleMarqueeView;
import com.haier.cellarette.baselibrary.marqueelibrary.util.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @author lhw
 * 首页右侧应用和通知公告部分
 */
public class HomepageActFragmentRight extends SlbBaseLazyFragmentNew implements NoticeView, CakeView {

    private RecyclerView recyclerView1;
    private MkCateAdapter2 mAdapter1;
    public SimpleMarqueeView marqueeView3;

    private NoticePresenter mNoticePresenter;
    private CakePresenter mFsyyyPresenter;

    @Override
    public void call(Object value) {
        String ids = (String) value;
        ToastUtils.showLong(ids);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_homepage_rightact_fragment;
    }

    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        mNoticePresenter = new NoticePresenter();
        mNoticePresenter.onCreate(this);

        mFsyyyPresenter = new CakePresenter();
        mFsyyyPresenter.onCreate(this);

        recyclerView1 = rootView.findViewById(R.id.recyclerView1);
        marqueeView3 = rootView.findViewById(R.id.marqueeView3);

        recyclerView1.setLayoutManager(new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL, false));
        recyclerView1.addItemDecoration(new SpaceItemDecoration(AutoSizeUtils.mm2px(getContext(), 18), 2));
        mAdapter1 = new MkCateAdapter2();
        recyclerView1.setAdapter(mAdapter1);
        mAdapter1.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CakeBeanYewu.CakeBean bean1 = (CakeBeanYewu.CakeBean) adapter.getItem(position);
                String url = bean1.url;
                if (url.contains("http")) {
                    String token = SPUtils.getInstance().getString("token");
                    Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MkWebDemo1Act");
                    intent.putExtra(MkWebActFragment1.TAG_TITLE, bean1.name);
                    intent.putExtra("url", bean1.url + "&access_token=" + token);
                    startActivity(intent);
//                  HiosHelperNew.resolveAd(getActivity(), getActivity(), bean1.getTab_id());
                } else {
//                  HiosHelperNew.resolveAd(getActivity(), getActivity(), "dataability://com.fosung.lighthouse.hstbigscreen.hs.act.slbapp.MkAppsDemo1Act{act}");
                    startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MkAppsDemo1Act"));

                }
            }
        });
        mAdapter1.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                CakeBeanYewu.CakeBean bean1 = (CakeBeanYewu.CakeBean) adapter.getItem(position);
                String url = bean1.url;
                if (url.contains("http")) {
                    String token = SPUtils.getInstance().getString("token");
                    Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MkWebDemo1Act");
                    intent.putExtra(MkWebActFragment1.TAG_TITLE, bean1.name);
                    intent.putExtra("url", bean1.url + "&access_token=" + token);
                    startActivity(intent);
//                  HiosHelperNew.resolveAd(getActivity(), getActivity(), bean1.getTab_id());
                } else {
//                  HiosHelperNew.resolveAd(getActivity(), getActivity(), "dataability://com.fosung.lighthouse.hstbigscreen.hs.act.slbapp.MkAppsDemo1Act{act}");
                    startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MkAppsDemo1Act"));
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        doNetwork();
    }

    private void doNetwork() {
        UserInfoBean userInfoBean = AppCommonUtils.getUserInfoBean();
        mNoticePresenter.getNotice("/gwapi/dthst/api/notice/app", 0, 20, userInfoBean.userId, userInfoBean.orgId, userInfoBean.orgCode);
        mFsyyyPresenter.getCakes("/gwapi/workbenchserver/api/workbench/v2/search/first", "", "", "", "", "");

    }

    @Override
    public void onNoticeSuccess(NoticeYewu bean) {
        if (bean != null && bean.list != null) {
            SimpleMF<Spanned> marqueeFactory3 = new SimpleMF<>(getActivity());
            List<Spanned> datas3 = new ArrayList<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                for (NoticeYewu.NoticeBean p : bean.list) {
                    datas3.add(Html.fromHtml("<font color=\"#646B76\">" + p.content + "</font>", Html.FROM_HTML_MODE_COMPACT));
                }
            }
            marqueeFactory3.setData(datas3);
            marqueeView3.setMarqueeFactory(marqueeFactory3);
            marqueeView3.setOnItemClickListener(new OnItemClickListener<TextView, Spanned>() {
                @Override
                public void onItemClickListener(TextView mView, Spanned mData, int mPosition) {
//                Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.SearchListActivity1");
//                intent.putExtra("search_key", mData.toString());
//                startActivity(intent);
//                ToastUtils.showLong(mData.toString());
                }
            });
            marqueeView3.startFlipping();
        }
    }

    @Override
    public void onNoticeSuccessNodata(String msg) {

    }

    @Override
    public void onNoticeFail(String msg) {

    }

    @Override
    public void onCakeSuccess(CakeBeanYewu bean) {
        if (bean.data != null) {
            mAdapter1.setNewData(bean.data);
        }
    }

    @Override
    public void onCakeNodata(String msg) {
        ToastUtils.showLong(msg);
    }

    @Override
    public void onCakeFail(String msg) {
        ToastUtils.showLong(msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNoticePresenter == null) {
            mNoticePresenter.onDestory();
        }
        if (mFsyyyPresenter != null) {
            mFsyyyPresenter.onDestory();
        }
    }
}
