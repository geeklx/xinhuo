package com.geek.appindex.fenlei;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appindex.R;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.biz1.bean.BjyyBeanYewu4;
import com.geek.biz1.presenter.FCate1Presenter;
import com.geek.biz1.view.FCate1View;
import com.geek.libbase.baserecycleview.SlbBaseActivityYewuList;
import com.geek.libbase.utils.CommonUtils;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libwebview.hois2.HiosHelper;
import com.just.agentweb.geek.fragment.AgentWebFragment;
import com.just.agentweb.geek.hois3.HiosHelperNew;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;

public class ShouyeCateAct1 extends SlbBaseActivityYewuList implements View.OnClickListener, FCate1View {

    private FCate1Presenter fCate1Presenter;
    //
    private ShouyeCateAdapter1 mAdapter1;
    private List<BjyyBeanYewu3> mList1;
    private String tablayoutId;
    private String title;
    private String count;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        BarUtils.setStatusBarLightMode(this, true);
    }

    @Override
    public void onResume() {
        // 从缓存中拿出头像bufen
        MyLogUtil.e("sssssss", "onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (fCate1Presenter != null) {
            fCate1Presenter.onDestory();
        }
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_co_index1;
    }

    @Override
    protected void findviewAdd() {
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setImageResource(R.drawable.icon_common_back1);
        tv_right.setVisibility(View.GONE);
        tablayoutId = getIntent().getStringExtra(AppCommonUtils.intent_id);
        title = getIntent().getStringExtra(AppCommonUtils.intent_title);
        count = getIntent().getStringExtra(AppCommonUtils.intent_count);
        tvCenterContent.setTextColor(ContextCompat.getColor(ShouyeCateAct1.this, R.color.black));
        tvCenterContent.setText(title);
        //HIOS协议
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        if (appLinkIntent != null) {
            String appLinkAction = appLinkIntent.getAction();
            if (appLinkAction != null) {
                Uri appLinkData = appLinkIntent.getData();
                if (appLinkData != null) {
                    tablayoutId = appLinkData.getQueryParameter("query1");
                    title = appLinkData.getQueryParameter("query2");
                    count = appLinkData.getQueryParameter("query3");

                }
            }
        }
        // 1
//        recyclerView1.setLayoutManager(new GridLayoutManager(this, 1, RecyclerView.VERTICAL, false));
        // 4
        if (!TextUtils.isEmpty(count)) {
        } else {
            count = "1";
        }
        recyclerView1.setLayoutManager(new GridLayoutManager(this, Integer.parseInt(count), RecyclerView.VERTICAL, false));
//        recyclerView1.addItemDecoration(new GridSpacingItemDecoration(Integer.parseInt(count), (int) (DisplayUtil.getScreenWidth(this) * 8f / 375), true));
        mList1 = new ArrayList<>();
        mAdapter1 = new ShouyeCateAdapter1();
        recyclerView1.setAdapter(mAdapter1);
        init_adapter(mAdapter1);
    }

    @Override
    protected void donetworkAdd() {
        // 接口初始化bufen
        if (fCate1Presenter == null) {
            fCate1Presenter = new FCate1Presenter();
            fCate1Presenter.onCreate(this);

        }
    }

    @Override
    protected void retryDataAdd() {
        String limit = PAGE_SIZE + "";
        String page = mNextRequestPage + "";
        MyLogUtil.e("--ShouyeCateAct1-tablayoutId----", tablayoutId);
        // 接口bufen
        fCate1Presenter.getcate1list(AppCommonUtils.auth_url + "/getmore", tablayoutId, limit, page,
                "tag", "", tablayoutId);
        //TODO test
//        mList1 = new ArrayList<>();
////        mList1 = hMyorderBean.getList();
//        for (int i = 0; i < 10; i++) {
//            mList1.add(new BjyyBeanYewu3("1",
//                    "https://s2.51cto.com//wyfs02/M01/89/BA/wKioL1ga-u7QnnVnAAAfrCiGnBQ946_middle.jpg",
//                    "本地应用" + i, "com.tencent.mm", false));
//        }
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                OnOrderSuccess(mList1);
//            }
//        }, 1000);
    }

    @Override
    protected void refreshLayoutAdd() {
        // 业务bufen
//      mAdapter1.cancle_timer_all();
    }

    @Override
    protected void emptyviewAdd() {
        // 业务bufen
//      mAdapter1.cancle_timer_all();
    }

    @Override
    protected void onclickAdd() {
        tvCenterContent.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        mAdapter1.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BjyyBeanYewu3 bean = (BjyyBeanYewu3) adapter.getData().get(position);
                HiosHelperNew.resolveAd(ShouyeCateAct1.this, ShouyeCateAct1.this, bean.getUrl());

            }
        });
        mAdapter1.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
//                final SlbBaseActivityViewPageTabBean1 bean = (SlbBaseActivityViewPageTabBean1) adapter.getData().get(position);
//                int i = view.getId();
//                if (i == com.geek.libbase.R.id.tv1) {
//                    // 入学通知书
////                    Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.NoticeDetailActivity");
////                    intent.putExtra("orderType", bean.getOrderType());
////                    intent.putExtra("orderId", bean.getId());
////                    startActivity(intent);
//                }
                BjyyBeanYewu3 bean = (BjyyBeanYewu3) adapter.getData().get(position);
//                HiosHelperNew.resolveAd(ShouyeCateAct1.this, ShouyeCateAct1.this, bean.getUrl());
                if (bean.getUrl().startsWith("gh")) {
                    // 调用
                    String appId = "wxa3fa50c49fcd271c"; // 填应用AppId
                    IWXAPI api = WXAPIFactory.createWXAPI(ShouyeCateAct1.this, appId);
                    WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                    req.userName = "gh_d43f693ca31f"; // 填小程序原始id
//                req.path = "path";                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
                    req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
                    api.sendReq(req);
                } else if (bean.getUrl().startsWith("http")) {
                    Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity");
                    intent.putExtra(AgentWebFragment.URL_KEY, bean.getUrl());
                    startActivity(intent);
//                    HiosHelperNew.resolveAd(getActivity(), getActivity(),  bean.getUrl());
                } else {
                    HiosHelperNew.resolveAd(ShouyeCateAct1.this, ShouyeCateAct1.this, bean.getUrl());
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.baserecycleview_tv_center_content1) {
            Intent msgIntent = new Intent();
            msgIntent.setAction(CommonUtils.ActYewuList1);
            if (!isEnscrolly()) {
                // fragment传值 刷新
                msgIntent.putExtra(CommonUtils.SlbBaseActivityViewPageAct1, CommonUtils.SlbBaseAct_update);
                LocalBroadcastManagers.getInstance(ShouyeCateAct1.this).sendBroadcast(msgIntent);
            } else {
                // fragment传值 滚动
                msgIntent.putExtra(CommonUtils.SlbBaseActivityViewPageAct1, CommonUtils.SlbBaseAct_scroll);
                LocalBroadcastManagers.getInstance(ShouyeCateAct1.this).sendBroadcast(msgIntent);
            }
        } else if (view.getId() == R.id.baserecycleview_tv_right1) {
            // 用户协议
            HiosHelper.resolveAd(ShouyeCateAct1.this, this, "http://pc.jiuzhidao.com/portal/page/index/id/9.html");
        } else {

        }
    }

    /**
     * --------------------------------业务逻辑分割线----------------------------------
     */


    @Override
    public void OnCate1Success(String authorizedType, BjyyBeanYewu4 bean) {
        mList1 = new ArrayList<>();
        mList1 = bean.getData();
        OnOrderSuccess(mList1);
    }

    @Override
    public void OnCate1Nodata(String bean) {
        OnOrderNodata();
    }

    @Override
    public void OnCate1Fail(String msg) {
        OnOrderFail();
    }
}
