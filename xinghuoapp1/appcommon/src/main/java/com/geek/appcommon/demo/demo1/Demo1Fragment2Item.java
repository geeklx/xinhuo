package com.geek.appcommon.demo.demo1;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.geek.common.R;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libbase.widgets.XRecyclerView;

/**
 * @author: lhw
 * @date: 2022/7/18
 * @desc
 */
public class Demo1Fragment2Item extends SlbBaseLazyFragmentNew {

    private XRecyclerView rvMessage;
    private MessageAdapter mAdapter;


    public static Demo1Fragment2Item getInstance(Bundle bundle) {
        Demo1Fragment2Item fragment2Item = new Demo1Fragment2Item();
        if (bundle != null) {
            fragment2Item.setArguments(bundle);
        }

        return fragment2Item;
    }


    @Override
    protected void setup(View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        initView(rootView);
        doNetWork();
    }

    private void doNetWork() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            DemoResultBean data = (DemoResultBean) arguments.getSerializable("data");
            mAdapter.setNewData(data.getDatas());
        }

    }

    private void initView(View rootView) {
        rvMessage = rootView.findViewById(R.id.rv_message);
        mAdapter = new MessageAdapter();
        rvMessage.setAdapter(mAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_demo1_fragment2_item;
    }
}




