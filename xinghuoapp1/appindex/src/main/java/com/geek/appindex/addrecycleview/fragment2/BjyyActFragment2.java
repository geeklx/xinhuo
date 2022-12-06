package com.geek.appindex.addrecycleview.fragment2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.appindex.R;
import com.geek.appindex.addrecycleview.BjyyAct;
import com.geek.appindex.addrecycleview.BjyyUtils;
import com.geek.appindex.addrecycleview.fragment1.BjyyActFragment1;
import com.geek.biz1.bean.BjyyActFragment251Bean;
import com.geek.biz1.bean.BjyyBeanYewu1;
import com.geek.biz1.bean.BjyyBeanYewu2;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libbase.widgets.XRecyclerView;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.data.MmkvUtils;

import java.util.ArrayList;
import java.util.List;

public class BjyyActFragment2 extends SlbBaseLazyFragmentNew {

    public XRecyclerView mRecyclerView;
    public BjyyActFragment251Adapter mAdapter;
    public List<BjyyActFragment251Bean> mList;
    public int spanCount = 5;// 几列数据
    public BjyyBeanYewu1 bjyyBeanYewu1;

    public MessageReceiverIndex mMessageReceiver;

    public class MessageReceiverIndex extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if ("BjyyActFragment2".equals(intent.getAction())) {
                    int data = intent.getIntExtra("query1", 0);
                    if (data == 0) {
//                        tv_qx();

                    }
                    if (data == 1) {
//                        tv_bc();
                        MmkvUtils.getInstance().set_common_json2("BjyyActFragment2", bjyyBeanYewu1);

                    }
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onDestroy() {
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
        filter.addAction("BjyyActFragment2");
        LocalBroadcastManagers.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bjyyact1_fragment2;
    }

    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        findview(rootView);
        donetwork();
        onclicklistener();
    }

    private void donetwork() {
//        bjyyBeanYewu1 = ((BjyyAct) requireActivity()).getData();
        bjyyBeanYewu1 = MmkvUtils.getInstance().get_common_json("BjyyActFragment2", BjyyBeanYewu1.class);
        mList = new ArrayList<>();
        mList = getMultipleItemData(bjyyBeanYewu1);
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
    }

    private String currentId;
    private List<String> currentIds;

    private void onclicklistener() {
        currentId = "-1";
        if (mAdapter.getData().size() == 0) {
            currentIds = new ArrayList<>();
        } else {
            currentIds = BjyyUtils.choose_data_my1();
//            ToastUtils.showLong(currentIds.size() + "");
        }
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                BjyyActFragment251Bean addressBean = (BjyyActFragment251Bean) adapter.getItem(position);
                currentId = addressBean.getmBean().getId();
                if (TextUtils.equals(currentId, "-1")) {
//                    HiosHelperNew.resolveAd(getActivity(), getActivity(), addressBean.getmBean().getUrl());// 不可编辑区域bufen
                    ToastUtils.showLong("您没有权限操作此应用，请联系官方");
                    return;
                }
                for (int i = 0; i < currentIds.size(); i++) {
                    if (TextUtils.equals(currentId, currentIds.get(i))) {
                        ToastUtils.showLong("已添加");
                        return;
                    }
                }
//                    mAdapter.bjyyActFragment2Style5Provider.setCurrentPos(currentId, View.GONE);
//                    ToastUtils.showLong(currentId + "," + position + "");
//                    mAdapter.notifyItemChanged(position);
                BjyyBeanYewu1 beanNew1 = BjyyUtils.choose_data(bjyyBeanYewu1, currentId, true);
                mList = getMultipleItemData(beanNew1);
                mAdapter.setNewData(mList);
                currentIds.add(currentId);
                SendToFragment(addressBean);
            }
        });
    }

    private void findview(View rootView) {
        mRecyclerView = rootView.findViewById(R.id.rv_others);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount, RecyclerView.VERTICAL, false));
        mRecyclerView.canScrollVertically(-1);
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
}
