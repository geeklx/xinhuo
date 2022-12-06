package com.geek.appindexdt.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.ToastUtils;
import com.geek.appindexdt.R;
import com.geek.appindexdt.factorys.MkIndexActFragmentFactory;
import com.geek.libbase.base.SlbBaseFragment;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libutils.app.FragmentHelper;
import com.geek.libutils.app.MyLogUtil;

public class MkIndexAct1Fragment extends SlbBaseLazyFragmentNew implements OnClickListener {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mk_indexact;
    }

    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        findview(rootView);
        onclickListener();
        doNetWork();
    }

    private void doNetWork() {

    }

    private void onclickListener() {

    }

    private void findview(View rootView) {
        setupFragments();
    }

    private void setupFragments() {
        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
        SparseArrayCompat<Class<? extends SlbBaseFragment>> array = MkIndexActFragmentFactory.get();
        int size = array.size();
        SlbBaseFragment item;
        for (int i = 0; i < size; i++) {
            item = FragmentHelper.newFragment(array.valueAt(i), null);
            ft.replace(array.keyAt(i), item, item.getClass().getName());
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {

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

//    /**
//     * fragment间通讯bufen
//     *
//     * @param value 要传递的值
//     * @param tag   要通知的fragment的tag
//     */
//    public void callFragment(Object value, String... tag) {
//        FragmentManager fm = requireActivity().getSupportFragmentManager();
//        Fragment fragment;
//        for (String item : tag) {
//            if (TextUtils.isEmpty(item)) {
//                continue;
//            }
//
//            fragment = fm.findFragmentByTag(item);
//            if (fragment != null && fragment instanceof SlbBaseLazyFragmentNew) {
//                ((SlbBaseLazyFragmentNew) fragment).call(value);
//            }
//        }
//    }
}
