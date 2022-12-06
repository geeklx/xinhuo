//package com.geek.appindexdt.fragments;
//
//import android.os.Bundle;
//import android.view.View;
//
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.blankj.utilcode.util.ToastUtils;
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.geek.appindexdt.IndexAct;
//import com.geek.appindexdt.R;
//import com.geek.appindexdt.adapter.MkF1Adapter;
//import com.geek.appindexdt.beans.MkF1Bean;
//import com.geek.libbase.base.SlbBaseLazyFragmentNew;
//import com.geek.libbase.widgets.XRecyclerView;
//import com.haier.cellarette.baselibrary.recycleviewutils.JackSnapHelper;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class MkIndexActFragment11 extends SlbBaseLazyFragmentNew {
//
//    private List<MkF1Bean> mList;
//    private MkF1Adapter mAdapter;
//    private XRecyclerView recyclerView;
//
//    @Override
//    public void call(Object value) {
//        String ids = (String) value;
//        ToastUtils.showLong(ids);
//    }
//
//    /**
//     * 页面传值操作部分
//     *
//     * @param id1
//     */
//    private void SendToFragment(String id1) {
//        //举例
////        IndexFoodFragmentUpdateIds iff = new IndexFoodFragmentUpdateIds();
////        iff.setFood_definition_id(id1);
////        iff.setFood_name(id2);
//        if (getActivity() != null && getActivity() instanceof IndexAct) {
//            ((IndexAct) getActivity()).callFragment(id1, MkIndexActFragment2.class.getName());
//        }
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_mk_indexact_fragment_listcate;
//    }
//
//    @Override
//    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
//        super.setup(rootView, savedInstanceState);
////        rootView.findViewById(R.id.tv1).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                SendToFragment("demo1的fragment1页面的传值");
////            }
////        });
//        recyclerView = rootView.findViewById(R.id.recycler_view1);
//        mAdapter = new MkF1Adapter();
//        onclick();
//        doNetWork(1, RecyclerView.HORIZONTAL);
//    }
//
//    public void doNetWork(int spanCount, int recH_V) {
//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount, recH_V, false));
//        JackSnapHelper mLinearSnapHelper = new JackSnapHelper(JackSnapHelper.TYPE_SNAP_START);
//        mLinearSnapHelper.attachToRecyclerView(recyclerView);
//        recyclerView.setAdapter(mAdapter);
//        //
//        setData(addList());
//    }
//
//    public void setData(List<MkF1Bean> mList) {
//        mAdapter.setNewData(mList);
//        footer_onclick((MkF1Bean) mAdapter.getItem(0));
//    }
//
//    //点击item
//    public void footer_onclick(MkF1Bean model) {
//        if (model.isEnselect()) {
//            // 不切换当前的item点击 刷新当前页面
////            showFragment(model.getText_id(), true);
//        } else {
//            // 切换到另一个item
//            if (model.getText_id().equalsIgnoreCase("id2")) {
////                SlbLoginUtil.get().loginTowhere(getActivity(), new Runnable() {
////                    @Override
////                    public void run() {
////                        set_footer_change(model);
//////                showFragment(model.getText_id(), false);
////                    }
////                });
//                set_footer_change(model);
//            } else {
//                set_footer_change(model);
////                showFragment(model.getText_id(), false);
//            }
//        }
//    }
//
//    private void set_footer_change(MkF1Bean model) {
//        //设置为选中
//        initList();
//        model.setEnselect(true);
//        mAdapter.setNewData(mList);
//    }
//
//    private void onclick() {
//        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                MkF1Bean bean = (MkF1Bean) adapter.getItem(position);
////                HiosHelper.resolveAd(SearchListActivity.this, SearchListActivity.this, bean.getHios());
//            }
//        });
//        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                MkF1Bean bean = (MkF1Bean) adapter.getItem(position);
//                footer_onclick(bean);
//            }
//        });
//    }
//
//    private List<MkF1Bean> addList() {
//        List<MkF1Bean> mList = new ArrayList<>();
//        mList.add(new MkF1Bean("id1", "首页", 0, 0, false));
//        mList.add(new MkF1Bean("id2", "党建要闻", 0, 0, false));
//        mList.add(new MkF1Bean("id3", "组织工作", 0, 0, false));
//        mList.add(new MkF1Bean("id4", "济南", 0, 0, false));
//        mList.add(new MkF1Bean("id1", "首页", 0, 0, false));
//        mList.add(new MkF1Bean("id2", "党建要闻", 0, 0, false));
//        mList.add(new MkF1Bean("id3", "组织工作", 0, 0, false));
//        mList.add(new MkF1Bean("id4", "济南", 0, 0, false));
//        return mList;
//    }
//
//    private void initList() {
//        for (int i = 0; i < mList.size(); i++) {
//            MkF1Bean item = mList.get(i);
//            if (item.isEnselect()) {
//                item.setEnselect(false);
//            }
//        }
//    }
//}
