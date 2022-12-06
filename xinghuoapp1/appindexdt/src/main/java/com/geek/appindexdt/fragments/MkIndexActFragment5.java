package com.geek.appindexdt.fragments;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.appindexdt.R;
import com.geek.appindexdt.adapter.MkF5Adapter;
import com.geek.appindexdt.beans.MkFCateBean;
import com.geek.libbase.widgets.GridSpacingItemDecoration;
import com.geek.libbase.widgets.XRecyclerView;
import com.geek.libglide47.base.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

public class MkIndexActFragment5 extends MkIndexActFragmentListCate implements MkIndexActFragmentListCate.SetOnItemClickListenerR, MkIndexActFragmentListCate.SetOnItemChildClickListenerR {

    private MkF5Adapter mAdapter;
    private XRecyclerView recyclerView;

    public List<MkFCateBean> addList2() {
        List<MkFCateBean> mList = new ArrayList<>();
        mList.add(new MkFCateBean("id1", "首页", "https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id2", "党建要闻", "https://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id3", "组织工作", "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id4", "济南", "https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id11", "首页", "https://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id131", "首页", "https://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id21", "党建要闻", "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id31", "组织工作", "https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id321", "组织工作", "https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id41", "更多", "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg", 0, 0, false));
        return mList;
    }

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
        doNetWork(recyclerView, mAdapter, addList2(), 5, RecyclerView.VERTICAL);
    }

    @Override
    protected void onclick() {
        onClickListener(mAdapter);
    }


    @Override
    public void onItemClickR(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildClickR(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    protected void findview(View rootView) {
        recyclerView = rootView.findViewById(R.id.recycler_view1);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(5, (int) (DisplayUtil.getScreenWidth(getActivity()) * 8f / 375), true));
        recyclerView.canScrollVertically(-1);
        mAdapter = new MkF5Adapter();
    }
}
