package com.geek.appcommon.demo.demo1;

import androidx.collection.SparseArrayCompat;

import com.geek.common.R;
import com.geek.libbase.base.SlbBaseFragment;

/**
 * @author: lhw
 * @date: 2022/7/18
 * @desc
 */
public class Demo1FragmentFactory {
    private static SparseArrayCompat<Class<? extends SlbBaseFragment>> sIndexFragments = new SparseArrayCompat<>();

    static {

        sIndexFragments.put(R.id.fragment_item_0, Demo1Fragment1.class);//模块1
        sIndexFragments.put(R.id.fragment_item_1, Demo1Fragment2Container.class);//模块2
        sIndexFragments.put(R.id.fragment_item_2, Demo1Fragment3.class);//模块2

    }

    public static Class<? extends SlbBaseFragment> get(int id) {
        if (sIndexFragments.indexOfKey(id) < 0) {
            throw new UnsupportedOperationException("cannot find fragment by " + id);
        }
        return sIndexFragments.get(id);
    }

    public static SparseArrayCompat<Class<? extends SlbBaseFragment>> get() {
        return sIndexFragments;
    }
}