package com.geek.appindexdt.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.appindexdt.IndexAct;
import com.geek.appindexdt.beans.MkFCateBean;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libbase.widgets.XRecyclerView;
import com.geek.libutils.SlbLoginUtil;
import com.haier.cellarette.baselibrary.recycleviewutils.JackSnapHelper;

import java.util.ArrayList;
import java.util.List;


public abstract class MkIndexActFragmentListCate extends SlbBaseLazyFragmentNew {

    private List<MkFCateBean> mList;

    @Override
    public void call(Object value) {
        String ids = (String) value;
        ToastUtils.showLong(ids);
    }

    /**
     * 页面传值操作部分
     *
     * @param id1
     */
    protected void SendToFragment(String id1) {
        //举例
//        IndexFoodFragmentUpdateIds iff = new IndexFoodFragmentUpdateIds();
//        iff.setFood_definition_id(id1);
//        iff.setFood_name(id2);
        if (getActivity() != null && getActivity() instanceof IndexAct) {
            ((IndexAct) getActivity()).callFragment(id1, MkIndexActFragment2.class.getName());
        }
    }

    @Override
    protected int getLayoutId() {
        return getlayoutIdR();
    }

    protected abstract int getlayoutIdR();

    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        findview(rootView);
        onclick();
        donetwork();
    }

    protected abstract void donetwork();

    protected abstract void onclick();

    protected abstract void findview(View rootView);

    protected void doNetWork(XRecyclerView recyclerView, BaseQuickAdapter mAdapter, List<MkFCateBean> mList, int spanCount, int recH_V) {
        this.mList = mList;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount, recH_V, false));
//        JackSnapHelper mLinearSnapHelper = new JackSnapHelper(JackSnapHelper.TYPE_SNAP_START);
//        mLinearSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setNewData(mList);
        selectdata(mAdapter, (MkFCateBean) mAdapter.getItem(0), "");
    }


    //点击item
    protected void selectdata(BaseQuickAdapter adapter, MkFCateBean model, String id) {
        if (model.isEnselect()) {
            // 不切换当前的item点击 刷新当前页面
        } else {
            // 切换到另一个item
            if (model.getText_id().equalsIgnoreCase(id)) {
                SlbLoginUtil.get().loginTowhere(getActivity(), new Runnable() {
                    @Override
                    public void run() {
                        //设置为选中
                        initList();
                        model.setEnselect(true);
                        adapter.notifyDataSetChanged();
                    }
                });
            } else {
                //设置为选中
                initList();
                model.setEnselect(true);
                adapter.notifyDataSetChanged();
            }
        }
    }

    protected void onClickListener(BaseQuickAdapter mAdapter) {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                MkFCateBean bean = (MkFCateBean) adapter.getItem(position);
//                HiosHelper.resolveAd(SearchListActivity.this, SearchListActivity.this, bean.getHios());
                MkFCateBean bean = (MkFCateBean) adapter.getItem(position);
                selectdata(adapter, bean, "");
                if (setOnItemClickListenerR != null) {
                    setOnItemClickListenerR.onItemClickR(adapter, view, position);
                }
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                MkFCateBean bean = (MkFCateBean) adapter.getItem(position);
                selectdata(adapter, bean, "");
                if (setOnItemChildClickListenerR != null) {
                    setOnItemChildClickListenerR.onItemChildClickR(adapter, view, position);
                }
            }
        });
    }

    public SetOnItemClickListenerR setOnItemClickListenerR;

    public interface SetOnItemClickListenerR {
        void onItemClickR(BaseQuickAdapter adapter, View view, int position);
    }

    public SetOnItemChildClickListenerR setOnItemChildClickListenerR;

    public interface SetOnItemChildClickListenerR {
        void onItemChildClickR(BaseQuickAdapter adapter, View view, int position);
    }

    public List<MkFCateBean> addList() {
        List<MkFCateBean> mList = new ArrayList<>();
        mList.add(new MkFCateBean("id1", "首页", "https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg",0, 0, false));
        mList.add(new MkFCateBean("id2", "党建要闻", "https://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg",0, 0, false));
        mList.add(new MkFCateBean("id3", "组织工作","https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id4", "济南", "https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg",0, 0, false));
        mList.add(new MkFCateBean("id11", "首页", "https://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg",0, 0, false));
        mList.add(new MkFCateBean("id21", "党建要闻","https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id31", "组织工作","https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id41", "济南", "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg",0, 0, false));
        mList.add(new MkFCateBean("id111", "首页", "https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg",0, 0, false));
        mList.add(new MkFCateBean("id211", "党建要闻","https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id311", "组织工作","https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id411", "济南","https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id1111", "首页", "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg",0, 0, false));
        mList.add(new MkFCateBean("id2111", "党建要闻","https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id3111", "组织工作","https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", 0, 0, false));
        mList.add(new MkFCateBean("id4111", "济南", "https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg",0, 0, false));
        return mList;
    }




    protected void initList() {
        for (int i = 0; i < mList.size(); i++) {
            MkFCateBean item = mList.get(i);
            if (item.isEnselect()) {
                item.setEnselect(false);
            }
        }
    }
}
