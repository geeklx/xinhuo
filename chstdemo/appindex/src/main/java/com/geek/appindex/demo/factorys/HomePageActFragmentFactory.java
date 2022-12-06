package com.geek.appindex.demo.factorys;

import androidx.collection.SparseArrayCompat;

import com.geek.appindex.R;
import com.geek.appindex.demo.fragments.MkDemo2ActFragment1;
import com.geek.appindex.demo.fragments.MkDemo2ActFragment2;
import com.geek.appindex.demo.fragments.MkDemo2ActFragment3;
import com.geek.libbase.base.SlbBaseFragment;

/**
 * @author lhw
 */
public class HomePageActFragmentFactory {
    private static SparseArrayCompat<Class<? extends SlbBaseFragment>> sIndexFragments = new SparseArrayCompat<>();

    static {
        sIndexFragments.put(R.id.mkdemo2_page_0_item_0, MkDemo2ActFragment1.class);//头部标题栏
        sIndexFragments.put(R.id.mkdemo2_page_0_item_1, MkDemo2ActFragment2.class);//左侧banner
        sIndexFragments.put(R.id.mkdemo2_page_0_item_2, MkDemo2ActFragment3.class);//右侧通知和应用
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
