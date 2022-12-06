package com.geek.appindex.addrecycleview.fragment1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appindex.R;
import com.geek.appindex.addrecycleview.BjyyAct;
import com.geek.appindex.addrecycleview.fragment2.BjyyActFragment2;
import com.geek.biz1.bean.BjyyActFragment251Bean;
import com.geek.biz1.bean.BjyyActFragment251Bean2;
import com.geek.biz1.bean.FgrxxBean;
import com.geek.biz1.presenter.Fsyyy3Presenter;
import com.geek.biz1.view.Fsyyy3View;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libbase.itemtouchhelper.CallbackWrap;
import com.geek.libbase.itemtouchhelper.OnTouchListener;
import com.geek.libbase.widgets.XRecyclerView;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.data.MmkvUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BjyyActFragment1 extends SlbBaseLazyFragmentNew implements Fsyyy3View {

    private XRecyclerView rv_syyy;
    private BjyyAdapter1 bjyyAdapter1;
    private List<BjyyActFragment251Bean> mList1;
    private ItemTouchHelper helper;
    private BjyyActFragment251Bean2 data1;
    private FgrxxBean fgrxxBean;
    private Fsyyy3Presenter fsyyy3Presenter;

    private MessageReceiverIndex mMessageReceiver;

    public class MessageReceiverIndex extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if ("BjyyActFragment1".equals(intent.getAction())) {
                    int data = intent.getIntExtra("query1", 0);
                    if (data == 0) {
//                        tv_qx();
                    }
                    if (data == 1) {
                        MmkvUtils.getInstance().set_common_json2("BjyyActFragment1", data1);
                        //接口bufen
//        List<String> currentIds = BjyyUtils.choose_data_my();//
                        BjyyActFragment251Bean2 data_id = MmkvUtils.getInstance().get_common_json("BjyyActFragment1", BjyyActFragment251Bean2.class);
                        List<String> currentIds = new ArrayList<>();
                        for (int i = 0; i < data_id.getData().size(); i++) {
                            currentIds.add(data_id.getData().get(i).getmBean().getId());
                        }
                        StringBuilder ids = new StringBuilder();
                        for (int i = 0; i < currentIds.size(); i++) {
                            ids.append(currentIds.get(i)).append(",");
                        }
                        if (!TextUtils.isEmpty(ids)) {
                            fsyyy3Presenter.getsyyy3(AppCommonUtils.auth_url, fgrxxBean.getUserId(), ids.substring(0, ids.length() - 1).toString());
                        } else {
                            fsyyy3Presenter.getsyyy3(AppCommonUtils.auth_url, fgrxxBean.getUserId(), ids.toString());
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void Onsyyy3Success(String bean) {
        ToastUtils.showLong(bean);
        ((BjyyAct) requireActivity()).onBackPressed();
    }

    @Override
    public void Onsyyy3Nodata(String bean) {
        ToastUtils.showLong(bean);
    }

    @Override
    public void Onsyyy3Fail(String msg) {
        ToastUtils.showLong(msg);
    }

    @Override
    public void onDestroy() {
        if (fsyyy3Presenter != null) {
            fsyyy3Presenter.onDestory();
        }
        LocalBroadcastManagers.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void call(Object value) {
        BjyyActFragment251Bean bean = (BjyyActFragment251Bean) value;
        if (bean != null && bean.getmBean() != null) {
            mList1.add(bean);
            bjyyAdapter1.setNewData(mList1);
            //
            data1.setData(bjyyAdapter1.getData());
        }
    }

    private void SendToFragment(BjyyActFragment251Bean bjyyBeanYewu3) {
        if (getActivity() != null && getActivity() instanceof BjyyAct) {
            ((BjyyAct) getActivity()).callFragment(bjyyBeanYewu3, BjyyActFragment2.class.getName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageReceiver = new MessageReceiverIndex();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction("BjyyActFragment1");
        LocalBroadcastManagers.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);
        //
        fsyyy3Presenter = new Fsyyy3Presenter();
        fsyyy3Presenter.onCreate(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bjyyact1_fragment1;
    }

    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        findview(rootView);
        onclick();
        donetwork();
    }

    private void donetwork() {
//        data1 = ((BjyyAct) requireActivity()).getData1();
        data1 = MmkvUtils.getInstance().get_common_json("BjyyActFragment1", BjyyActFragment251Bean2.class);
        mList1 = new ArrayList<>();
        if (data1 != null && data1.getData() != null) {
            mList1 = data1.getData();
        }
        bjyyAdapter1.setNewData(mList1);
        //
        fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
    }

    private void onclick() {
        bjyyAdapter1.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                BjyyActFragment251Bean bean1 = (BjyyActFragment251Bean) adapter.getItem(position);
//                if (view.getId() == R.id.iv1) {
//                    if (bean1.getmBean().getId().contains("others")) {
//                        if (!AppUtils.isAppInstalled(bean1.getmBean().getUrl())) {
//                            ToastUtils.showLong("未安装此应用服务");
//                            return;
//                        }
//                        AppUtils.launchApp(bean1.getmBean().getUrl());
//                    } else {
//                        HiosHelperNew.resolveAd(getActivity(), getActivity(), bean1.getmBean().getUrl());
//                    }
//                }
//                if (view.getId() == R.id.tv2) {
//                    // 删除应用
//
//                }
            }
        });
        rv_syyy.addOnItemTouchListener(new OnTouchListener(rv_syyy) {
            @Override
            public void onLongClick(RecyclerView.ViewHolder vh) {
                if (vh.getLayoutPosition() != bjyyAdapter1.getData().size() - 1) {
                    helper.startDrag(vh);

                }
            }

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                BjyyActFragment251Bean bean1 = (BjyyActFragment251Bean) bjyyAdapter1.getItem(vh.getLayoutPosition());
//                BjyyBeanYewu3 bjyyBeanYewu3 = bean1.getmBean();
//                bjyyBeanYewu3.setEnable(false);
//                bean1.setmBean(bjyyBeanYewu3);
                SendToFragment(bean1);
                bjyyAdapter1.remove(vh.getLayoutPosition());
                data1.getData().remove(bean1);

//                if (view.getId() == R.id.iv1) {
//                    // 进入应用
//
//                }
//                if (view.getId() == R.id.tv2) {
//                    // 删除应用
//
//                }
            }
        });
    }


    private void findview(View rootView) {
        rv_syyy = rootView.findViewById(R.id.rv_syyy);
        // 1
        rv_syyy.setLayoutManager(new GridLayoutManager(getActivity(), 5, RecyclerView.VERTICAL, false));
//        rv_syyy.addItemDecoration(new GridSpacingItemDecoration(5, (int) (DisplayUtil.getScreenWidth(getActivity()) * 8f / 375), true));
        bjyyAdapter1 = new BjyyAdapter1();
        rv_syyy.setAdapter(bjyyAdapter1);
        helper = new ItemTouchHelper(new CallbackWrap(bjyyAdapter1, getActivity()));
        helper.attachToRecyclerView(rv_syyy);
        // 2

    }

}
