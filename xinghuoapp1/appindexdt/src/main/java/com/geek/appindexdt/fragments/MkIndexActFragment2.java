package com.geek.appindexdt.fragments;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.appindexdt.R;
import com.geek.appindexdt.adapter.MkF2Adapter;
import com.geek.appindexdt.beans.MkFCateBean;
import com.geek.libbase.widgets.XRecyclerView;

public class MkIndexActFragment2 extends MkIndexActFragmentListCate implements MkIndexActFragmentListCate.SetOnItemClickListenerR, MkIndexActFragmentListCate.SetOnItemChildClickListenerR {

    private MkF2Adapter mAdapter;
    private XRecyclerView recyclerView;

    @Override
    public void call(Object value) {
        String ids = (String) value;
        ToastUtils.showLong(ids);
    }

    @Override
    protected int getlayoutIdR() {
        return R.layout.activity_mk_indexact_fragment_listcate;
    }

    @Override
    protected void donetwork() {
        doNetWork(recyclerView, mAdapter, addList(), 1, RecyclerView.HORIZONTAL);
    }

    @Override
    protected void onclick() {
        onClickListener(mAdapter);
    }


    @Override
    public void onItemClickR(BaseQuickAdapter adapter, View view, int position) {
        MkFCateBean bean = (MkFCateBean) adapter.getItem(position);

    }

    @Override
    public void onItemChildClickR(BaseQuickAdapter adapter, View view, int position) {
        MkFCateBean bean = (MkFCateBean) adapter.getItem(position);

    }

    @Override
    protected void findview(View rootView) {
        recyclerView = rootView.findViewById(R.id.recycler_view1);
        mAdapter = new MkF2Adapter();
    }
}
