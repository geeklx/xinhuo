package com.geek.appcommon.demo.demo1;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.FragmentTransaction;

import com.geek.appcommon.SlbBase;
import com.geek.common.R;
import com.geek.libbase.base.SlbBaseFragment;
import com.geek.libutils.app.FragmentHelper;

/**
 * @author: lhw
 * @date: 2022/7/18
 * @desc
 */
public class Demo1Activity extends SlbBase {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_demo1;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        setFragment();
    }

    private void setFragment() {
        SparseArrayCompat<Class<? extends SlbBaseFragment>> array = Demo1FragmentFactory.get();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        SlbBaseFragment item;
        for (int i = 0; i < array.size(); i++) {
            item = FragmentHelper.newFragment(array.valueAt(i), new Bundle());
            transaction.replace(array.keyAt(i), item, item.getClass().getName());
        }
        transaction.commitAllowingStateLoss();
    }
}
