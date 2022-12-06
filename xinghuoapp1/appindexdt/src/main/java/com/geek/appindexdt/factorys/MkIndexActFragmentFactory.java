package com.geek.appindexdt.factorys;

import androidx.collection.SparseArrayCompat;

import com.geek.appindexdt.R;
import com.geek.appindexdt.fragments.MkIndexActFragment1;
import com.geek.appindexdt.fragments.MkIndexActFragment2;
import com.geek.appindexdt.fragments.MkIndexActFragment3;
import com.geek.appindexdt.fragments.MkIndexActFragment4;
import com.geek.appindexdt.fragments.MkIndexActFragment5;
import com.geek.appindexdt.fragments.MkIndexActFragment6;
import com.geek.libbase.base.SlbBaseFragment;

public class MkIndexActFragmentFactory {
    private static SparseArrayCompat<Class<? extends SlbBaseFragment>> sIndexFragments = new SparseArrayCompat<>();

    static {

        sIndexFragments.put(R.id.mkdemo1_page_0_item_0, MkIndexActFragment1.class);//模块1
        sIndexFragments.put(R.id.mkdemo1_page_0_item_1, MkIndexActFragment2.class);//模块2
        sIndexFragments.put(R.id.mkdemo1_page_0_item_2, MkIndexActFragment3.class);//模块3
        sIndexFragments.put(R.id.mkdemo1_page_0_item_3, MkIndexActFragment4.class);//模块4
        sIndexFragments.put(R.id.mkdemo1_page_0_item_4, MkIndexActFragment5.class);//模块5
        sIndexFragments.put(R.id.mkdemo1_page_0_item_5, MkIndexActFragment6.class);//模块6

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
