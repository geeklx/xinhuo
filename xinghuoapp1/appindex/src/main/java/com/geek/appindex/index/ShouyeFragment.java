package com.geek.appindex.index;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.appindex.R;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libutils.SlbLoginUtil;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;

import java.util.List;

public class ShouyeFragment extends SlbBaseLazyFragmentNew {

    private RecyclerView recyclerView;
    private ShouyeFragmentAdapter mAdapter;
    private List<BjyyBeanYewu3> mNavigationList;
    private ShouyeFragmentFactory shouyeFragmentFactory;
    private FragmentManager fragmentManager;

    private MessageReceiverIndex mMessageReceiver;

    public class MessageReceiverIndex extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if ("ShouyeFragment".equals(intent.getAction())) {
                    //点击item
                    if (!TextUtils.isEmpty(intent.getStringExtra("id"))) {
                        BjyyBeanYewu3 model = mAdapter.getItem(current_pos);
                        for (int i = 0; i < mAdapter.getItemCount(); i++) {
                            BjyyBeanYewu3 model1 = mAdapter.getItem(i);
                            if (TextUtils.equals(model1.getId(), intent.getStringExtra("id"))) {
                                current_pos = i;
                            }
                        }
                        footer_onclick(current_pos);
                    }
                    if (!TextUtils.isEmpty(intent.getStringExtra("mobid"))) {
//                        mAdapter.setHongdiao_count(intent.getStringExtra("mobid"));
//                        mAdapter.notifyDataSetChanged();
                    }
                    if (!TextUtils.isEmpty(intent.getStringExtra("query1"))) {
//                    //TODO 发送广播bufen
//                    Intent msgIntent = new Intent();
//                    msgIntent.setAction("ShouyeFragment");
//                    msgIntent.putExtra("query1", 0);
//                    LocalBroadcastManagers.getInstance(getActivity()).sendBroadcast(msgIntent);
                        exit();
                    }
                }
            } catch (Exception e) {
            }
        }
    }


    public static ShouyeFragment getInstance(Bundle bundle) {
        ShouyeFragment mEasyWebFragment = new ShouyeFragment();
        if (bundle != null) {
            mEasyWebFragment.setArguments(bundle);
        }
        return mEasyWebFragment;

    }

    private void initFragments(Bundle savedInstanceState) {
      if (getArguments() != null) {
            mNavigationList = (List<BjyyBeanYewu3>) getArguments().getSerializable("tablayoutId");
        }
        if (mNavigationList != null && mNavigationList.size() > 0) {
            //
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mNavigationList.size(), RecyclerView.VERTICAL, false));
            recyclerView.setAdapter(mAdapter);
            mAdapter.setNewData(mNavigationList);
            //
            shouyeFragmentFactory = new ShouyeFragmentFactory(mNavigationList);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                shouyeFragmentFactory.navTabUpdate(mNavigationList);
            }
            //
            for (int i = 0; i < mNavigationList.size(); i++) {
                Class<? extends Fragment> classFragment = null;
                classFragment = shouyeFragmentFactory.getNavigationClass(Integer.parseInt(mNavigationList.get(i).getId()));
                String tag1 = mNavigationList.get(i).getId()
                        + mNavigationList.get(i).getName()
                        + classFragment.getSimpleName();
                MyLogUtil.e("ShouyeFragment->initFragments->tag1", tag1);
                //重点：先重置所有fragment的状态为隐藏，彻底解决重叠问题
                try {
                    Fragment fragment = fragmentManager.findFragmentByTag(tag1);
//                if (classFragment.newInstance().isAdded()) {
                    if (fragment != null && fragment.isAdded()) {
                        fragmentManager.beginTransaction()
                                .hide(classFragment.newInstance())
                                .commitAllowingStateLoss();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState != null) {
                int cachedId = savedInstanceState.getInt(SAVED_CURRENT_ID, 0);
                if (cachedId >= 0 && cachedId <= mNavigationList.size()) {
                    current_pos = cachedId;
                }
            }
            if (mNavigationList != null && mNavigationList.size() > 0){
                if (mNavigationList.size() <= 2) {
                    footer_onclick(current_pos);
                } else {
                    footer_onclick(2);
                }
            }
        }
    }

    private void switchFragment(int index, boolean anim) {
        /* Fragment 切换 */
//        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        if (anim) {  //显示切换动画
//            if (index > current_pos) {
//                transaction.setCustomAnimations(R.anim.activity_right_to_left, R.anim.activity_left_to_right);
//            } else {
//                transaction.setCustomAnimations(R.anim.activity_left_to_right, R.anim.activity_right_to_left);
//            }
//        }
        Class<? extends Fragment> classFragment_new = null;
        Class<? extends Fragment> classFragment_old = null;
        Fragment fragment_old = null;
        Fragment fragment_new = null;
        String tag_new = "";
        String tag_old = "";
        classFragment_new = shouyeFragmentFactory.getNavigationClass(Integer.parseInt(mNavigationList.get(index).getId()));
        classFragment_old = shouyeFragmentFactory.getNavigationClass(Integer.parseInt(mNavigationList.get(current_pos).getId()));
        try {
            fragment_old = classFragment_old.newInstance();
            fragment_new = classFragment_new.newInstance();
            tag_old = mNavigationList.get(current_pos).getId() + "_"
                    + mNavigationList.get(current_pos).getName() + "_"
                    + fragment_old.getClass().getSimpleName();
            tag_new = mNavigationList.get(index).getId() + "_"
                    + mNavigationList.get(index).getName() + "_"
                    + fragment_new.getClass().getSimpleName();
            Fragment fragment_tag_new = (Fragment) fragmentManager.findFragmentByTag(tag_new);
            Fragment fragment_tag_old = (Fragment) fragmentManager.findFragmentByTag(tag_old);
            if (fragment_tag_new != null && fragment_tag_new.isAdded()) {
                transaction.hide(fragment_tag_old);
                transaction.show(fragment_tag_new);
//                fragment_tag_new.getCate(current_pos + "", anim);
            } else {
                if (fragment_tag_old != null) {
                    transaction.hide(fragment_tag_old);
                }
                Bundle args = new Bundle();
                args.putString("tablayoutId", mNavigationList.get(index).getId() + "");
                fragment_new.setArguments(args);
                MyLogUtil.e("ShouyeFragment->switchFragment->tag1", tag_new);
                transaction.add(R.id.container, fragment_new, tag_new);
                transaction.show(fragment_new);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
//        transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commitAllowingStateLoss();
        current_pos = index;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shouye_fragment;
    }

    @Override
    protected void setup(final View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        findview();
        recyclerView = rootView.findViewById(R.id.recycler_view1);
        mAdapter = new ShouyeFragmentAdapter();
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //点击item
//                current_pos = position;
                footer_onclick(position);
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //点击item
//                current_pos = position;
                footer_onclick(position);
            }
        });
        //
        fragmentManager = getChildFragmentManager();
        initFragments(savedInstanceState);
    }

    //点击item
    private void footer_onclick(int position) {
//        set_location2();
        final BjyyBeanYewu3 model = mAdapter.getItem(position);
        if (model.isEnable()) {
            // 不切换当前的item点击 刷新当前页面
            switchFragment(position, true);
        } else {
            // 切换到另一个item
            if (model.getUrl().contains("login")) {
                SlbLoginUtil.get().loginTowhere(getActivity(), new Runnable() {
                    @Override
                    public void run() {
                        set_footer_change(model);
                        switchFragment(position, false);
                    }
                });
            } else {
                set_footer_change(model);
                switchFragment(position, false);
            }
        }
    }

    private void set_footer_change(BjyyBeanYewu3 model) {
        //设置为选中
        for (int i = 0; i < mNavigationList.size(); i++) {
            BjyyBeanYewu3 item = mNavigationList.get(i);
            if (item.isEnable()) {
                item.setEnable(false);
            }
        }
        model.setEnable(true);
        mAdapter.setNewData(mNavigationList);
    }

    private void findview() {
        // 动态设置首页bufen
        mMessageReceiver = new MessageReceiverIndex();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction("ShouyeFragment");
        LocalBroadcastManagers.getInstance(getActivity().getApplicationContext()).registerReceiver(mMessageReceiver, filter);

    }

    private long exitTime;

    private void exit() {
//        BjyyBeanYewu3 model = mAdapter.getItem(0);
//        if (model != null && current_pos != 0) {
//            set_footer_change(model);
//            switchFragment(0, false);
//        } else {
            if ((System.currentTimeMillis() - exitTime) < 1500) {
                ActivityUtils.finishAllActivities();
            } else {
                ToastUtils.showLong("再次点击退出程序");
                exitTime = System.currentTimeMillis();
            }
//        }
    }
}
