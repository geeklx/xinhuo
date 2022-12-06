package com.geek.appindex.addrecycleview;

import androidx.collection.SparseArrayCompat;

import com.geek.appindex.R;
import com.geek.appindex.addrecycleview.fragment1.BjyyActFragment1;
import com.geek.appindex.addrecycleview.fragment2.BjyyActFragment2;
import com.geek.libbase.base.SlbBaseFragment;

public class BjyyActFragmentFactory {
    private static SparseArrayCompat<Class<? extends SlbBaseFragment>> sIndexFragments = new SparseArrayCompat<>();

    static {

        sIndexFragments.put(R.id.bjyyact1_page_0_item_0, BjyyActFragment1.class);//模块1
        sIndexFragments.put(R.id.bjyyact1_page_0_item_1, BjyyActFragment2.class);//模块2

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
