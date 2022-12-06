package com.geek.appindexdtcate.demo.factorys;

import androidx.collection.SparseArrayCompat;

import com.geek.appindexdtcate.demo.fragments.MkDemo1ActFragment1;
import com.geek.appindexdtcate.demo.fragments.MkDemo1ActFragment2;
import com.geek.appindexdtcate.R;
import com.geek.libbase.base.SlbBaseFragment;

public class MkDemo1ActFragmentFactory {
    private static SparseArrayCompat<Class<? extends SlbBaseFragment>> sIndexFragments = new SparseArrayCompat<>();

    static {

        sIndexFragments.put(R.id.mkdemo1_page_0_item_0, MkDemo1ActFragment1.class);//模块1
        sIndexFragments.put(R.id.mkdemo1_page_0_item_1, MkDemo1ActFragment2.class);//模块2

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
