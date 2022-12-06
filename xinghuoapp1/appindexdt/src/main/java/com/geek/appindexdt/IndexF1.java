package com.geek.appindexdt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;

public class IndexF1 extends SlbBaseLazyFragmentNew implements View.OnClickListener {

    private String tablayoutId;
    private MessageReceiverIndex mMessageReceiver;
    private ImageView iv1;

    public class MessageReceiverIndex extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if ("IndexF1".equals(intent.getAction())) {
//                    //TODO 发送广播bufen
//                    Intent msgIntent = new Intent();
//                    msgIntent.setAction("IndexF1");
//                    msgIntent.putExtra("query1", 0);
//                    LocalBroadcastManagers.getInstance(getActivity()).sendBroadcast(msgIntent);
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void call(Object value) {
        tablayoutId = (String) value;
        ToastUtils.showLong("call->" + tablayoutId);
        MyLogUtil.e("tablayoutId->", "call->" + tablayoutId);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManagers.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        // 从缓存中拿出头像bufen

        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
//        Bundle arg = getArguments();
        if (getArguments() != null) {
            tablayoutId = getArguments().getString("tablayoutId");
            MyLogUtil.e("tablayoutId->", "onCreate->" + tablayoutId);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_indexf1;
    }

    @Override
    protected void setup(final View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        mMessageReceiver = new MessageReceiverIndex();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction("IndexF1");
        LocalBroadcastManagers.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);
        iv1 = rootView.findViewById(R.id.iv1);
        iv1.setOnClickListener(this);
        donetwork();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv1) {
            SendToFragment("我是门户的传值");
        }
    }

    private void SendToFragment(String id1) {
        //举例
//        IndexFoodFragmentUpdateIds iff = new IndexFoodFragmentUpdateIds();
//        iff.setFood_definition_id(id1);
//        iff.setFood_name(id2);
        if (getActivity() != null && getActivity() instanceof IndexAct) {
            ((IndexAct) getActivity()).callFragment(id1, IndexAct.class.getName());
        }
    }

    /**
     * 第一次进来加载bufen
     */
    private void donetwork() {
        retryData();
    }

    // 刷新
    private void retryData() {
//        mEmptyView.loading();
//        presenter1.getLBBannerData("0");
//        refreshLayout1.finishRefresh();
//        emptyview1.success();
    }

    /**
     * 底部点击bufen
     *
     * @param cateId
     * @param isrefresh
     */
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
    public void give_id(String cateId) {
//        ToastUtils.showLong("下拉刷新啦");
        MyLogUtil.e("tablayoutId->", "give_id->" + cateId);
    }

    /**
     * --------------------------------业务逻辑分割线----------------------------------
     */


}
