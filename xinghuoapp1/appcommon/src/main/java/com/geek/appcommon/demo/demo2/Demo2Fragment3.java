package com.geek.appcommon.demo.demo2;

import android.os.Bundle;

import com.geek.common.R;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;

/**
 * @author: lhw
 * @date: 2022/7/18
 * @desc
 */
public class Demo2Fragment3 extends SlbBaseLazyFragmentNew {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_demo2_fragment3;
    }

    public static Demo2Fragment3 newInstance(Bundle bundle) {
        Demo2Fragment3 fragment = new Demo2Fragment3();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

}
