package com.geek.appindex.index.beifen;//package com.geek.appindex.index.beifen;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.blankj.utilcode.util.ActivityUtils;
//import com.blankj.utilcode.util.ToastUtils;
//import com.geek.appindex.R;
//import com.geek.appindex.index.ShouyeFragmentBean;
//import com.geek.appindex.index.ShouyeFragmentFactory;
//import com.geek.appindex.index.fragment.ShouyeF1;
//import com.geek.appindex.index.fragment.ShouyeF5;
//import com.geek.appindex.index.fragment.ShouyeF6;
//import com.geek.libbase.base.SlbBaseFragment;
//import com.geek.libbase.base.SlbBaseLazyFragmentNew;
//import com.geek.libutils.SlbLoginUtil;
//import com.geek.libutils.app.LocalBroadcastManagers;
//import com.geek.libutils.app.MyLogUtil;
//import com.geek.tablayout.entity.TabEntity;
//import com.geek.tablib.tablayout.CommonTabLayout;
//import com.geek.tablib.tablayout.listener.CustomTabEntity;
//import com.geek.tablib.tablayout.listener.OnTabSelectListener;
//import com.just.agentweb.geek.fragment.AgentWebFragment;
//import com.tencent.qcloud.tuikit.tuicontact.ui.pages.TUIContactFragment;
//import com.tencent.qcloud.tuikit.tuiconversation.ui.page.TUIConversationFragment;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ShouyeFragmentbeifen extends SlbBaseLazyFragmentNew {
//
//    private CommonTabLayout mTabLayout_2;
//    private String[] mTitles = {"首页", "消息", "联系人", "我的"};
//    private int[] mIconUnselectIds = {
//            R.mipmap.icon_sy6, R.mipmap.icon_sy4,
//            R.mipmap.icon_sy2, R.mipmap.icon_sy8};
//    private int[] mIconSelectIds = {
//            R.mipmap.icon_sy5, R.mipmap.icon_sy3,
//            R.mipmap.icon_sy1, R.mipmap.icon_sy7};
//    private ArrayList<CustomTabEntity> mTabEntities;
//
//    private int current_pos = 0;
//    private FragmentManager mFragmentManager;
//    private static final String LIST_TAG1 = "list11";
//    private static final String LIST_TAG2 = "list22";
//    private static final String LIST_TAG3 = "list33";
//    private static final String LIST_TAG4 = "list44";
//    private static final String LIST_TAG5 = "list55";
//    //
//    private ShouyeF5 mFragment1; //
//    private TUIConversationFragment mFragment2; // TUIConversationFragment
//    private TUIContactFragment mFragment3; //
//    private ShouyeF6 mFragment4; //
//    private ShouyeF1 mFragment5; //
//
//    private MessageReceiverIndex mMessageReceiver;
//
//    public class MessageReceiverIndex extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            try {
//                if ("ShouyeFragment".equals(intent.getAction())) {
//                    //点击item
//                    if (intent.getIntExtra("id", -1) != -1) {
//                        current_pos = intent.getIntExtra("id", 0);
//                        mTabLayout_2.setCurrentTab(current_pos);
//                    }
//                    if (!TextUtils.isEmpty(intent.getStringExtra("mobid"))) {
////                        mAdapter.setHongdiao_count(intent.getStringExtra("mobid"));
////                        mAdapter.notifyDataSetChanged();
//                    }
//                    if (!TextUtils.isEmpty(intent.getStringExtra("query1"))) {
////                    //TODO 发送广播bufen
////                    Intent msgIntent = new Intent();
////                    msgIntent.setAction("ShouyeFragment");
////                    msgIntent.putExtra("query1", 0);
////                    LocalBroadcastManagers.getInstance(getActivity()).sendBroadcast(msgIntent);
//                        exit();
//                    }
//                }
//            } catch (Exception e) {
//            }
//        }
//    }
//
////
////    @Override
////    public void onAttachFragment(Fragment fragment) {
////        if (mFragment1 == null && fragment instanceof ShouyeF1) {
////            mFragment1 = (ShouyeF1) fragment;
////        }
////        if (mFragment2 == null && fragment instanceof ShouyeF2) {
////            mFragment2 = (ShouyeF2) fragment;
////        }
////        if (mFragment3 == null && fragment instanceof ShouyeF3) {
////            mFragment3 = (ShouyeF3) fragment;
////        }
////        if (mFragment4 == null && fragment instanceof ShouyeF4) {
////            mFragment4 = (ShouyeF4) fragment;
////        }
////    }
//
//    public TextView tv_theme1;
//    private static final String SAVED_CURRENT_ID = "currentId";
//    //    public static final List<String> PAGE_TAGS = new ArrayList<>();
////    private List<Class<? extends SlbBaseLazyFragmentNew>> fragmentClasses = Arrays.asList(ShouyeF5.class,
////            TUIConversationFragment.class, ShouyeF6.class, TUIContactFragment.class, ShouyeF1.class); //自定义的Fragment，主要目的是在初始化时能够通过循环初始化，与重建时的恢复统一
//    private List<Fragment> fragments = new ArrayList<>();
//
//    @Override
//    public void onResume() {
//        if (!SlbLoginUtil.get().isUserLogin()) {
//            mTabLayout_2.setCurrentTab(current_pos);
//        }
//        super.onResume();
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt(SAVED_CURRENT_ID, current_pos);
//    }
//
//    private List<ShouyeFragmentBean> mNavigationList;
//    private ShouyeFragmentFactory shouyeFragmentFactory;
//
//
//    private void initFragments(Bundle savedInstanceState) {
//        // 测试
//        mNavigationList = new ArrayList<>();
//        mNavigationList.add(new ShouyeFragmentBean("11", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "首页", ShouyeFragmentFactory.TAG_shouye, true));
//        mNavigationList.add(new ShouyeFragmentBean("22", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "消息", ShouyeFragmentFactory.TAG_xiaoxi, false));
//        mNavigationList.add(new ShouyeFragmentBean("33", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "联系人", ShouyeFragmentFactory.TAG_people, false));
//        mNavigationList.add(new ShouyeFragmentBean("44", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "我的", ShouyeFragmentFactory.TAG_my, false));
//        mNavigationList.add(new ShouyeFragmentBean("55", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "框架1", ShouyeFragmentFactory.TAG_kuangjia1, false));
//        mNavigationList.add(new ShouyeFragmentBean("66", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "框架2", ShouyeFragmentFactory.TAG_kuangjia2, false));
//        shouyeFragmentFactory = new ShouyeFragmentFactory(mNavigationList);
//        shouyeFragmentFactory.navTabUpdate(mNavigationList);
//        //
//        for (int i = 0; i < mNavigationList.size(); i++) {
//            Class<? extends SlbBaseFragment> classFragment = null;
//            classFragment = shouyeFragmentFactory.getNavigationClass(Integer.parseInt(mNavigationList.get(i).getId()));
//            String tag1 = mNavigationList.get(i).getId()
//                    + mNavigationList.get(i).getName()
//                    + classFragment.getSimpleName();
//            MyLogUtil.e("ShouyeFragment->initFragments->tag1", tag1);
//            fragments.set(i, mFragmentManager.findFragmentByTag(tag1));
//            if (fragments.get(i) == null) {
//                try {
//                    fragments.set(i, classFragment.newInstance());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            //重点：先重置所有fragment的状态为隐藏，彻底解决重叠问题
//            if (fragments.get(i).isAdded()) {
//                mFragmentManager.beginTransaction()
//                        .hide(fragments.get(i))
//                        .commitAllowingStateLoss();
//            }
//        }
//        if (savedInstanceState != null) {
//            int cachedId = savedInstanceState.getInt(SAVED_CURRENT_ID, 0);
//            if (cachedId >= 0 && cachedId <= mNavigationList.size()) {
//                current_pos = cachedId;
//            }
//        }
//        switchFragment(current_pos, false);
//    }
//
//    private void switchFragment(int index) {
//        switchFragment(index, true);
//    }
//
//    private void switchFragment(int index, boolean anim) {
//        /* Fragment 切换 */
//        FragmentTransaction transaction = mFragmentManager.beginTransaction();
//        if (anim) {  //显示切换动画
//            if (index > current_pos) {
//                transaction.setCustomAnimations(R.anim.activity_right_to_left, R.anim.activity_left_to_right);
//            } else {
//                transaction.setCustomAnimations(R.anim.activity_left_to_right, R.anim.activity_right_to_left);
//            }
//        }
//        if (fragments.get(index).isAdded()) {
//            transaction.hide(fragments.get(current_pos));
//            transaction.show(fragments.get(index));
////            fragments.get(index).getCate(current_pos + "", anim);
//        } else {
//            transaction.hide(fragments.get(current_pos));
//            Bundle args = new Bundle();
//            args.putString("tablayoutId", mNavigationList.get(index).getId() + "");
//            fragments.get(index).setArguments(args);
//            String tag1 = mNavigationList.get(index).getId() + "_"
//                    + mNavigationList.get(index).getName() + "_"
//                    + fragments.get(index).getClass().getSimpleName();
//            MyLogUtil.e("ShouyeFragment->switchFragment->tag1", tag1);
//            transaction.add(R.id.container, fragments.get(index), tag1);
//            transaction.show(fragments.get(index));
//        }
//        transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        transaction.commitAllowingStateLoss();
//        current_pos = index;
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_shouye_fragment;
//    }
//
//    @Override
//    protected void setup(final View rootView, @Nullable Bundle savedInstanceState) {
//        super.setup(rootView, savedInstanceState);
//        mFragmentManager = getChildFragmentManager();
//        // 解决fragment布局重叠错乱
//        if (savedInstanceState != null) {
//            int cachedId = savedInstanceState.getInt(SAVED_CURRENT_ID, 0);
//            if (cachedId >= 0 && cachedId <= mTitles.length) {
//                current_pos = cachedId;
//            }
//            mFragment1 = (ShouyeF5) mFragmentManager.findFragmentByTag(LIST_TAG1);
//            mFragment2 = (TUIConversationFragment) mFragmentManager.findFragmentByTag(LIST_TAG2);
//            mFragment3 = (TUIContactFragment) mFragmentManager.findFragmentByTag(LIST_TAG3);
//            mFragment4 = (ShouyeF6) mFragmentManager.findFragmentByTag(LIST_TAG4);
//            mFragment5 = (ShouyeF1) mFragmentManager.findFragmentByTag(LIST_TAG5);
//        }
//        mTabLayout_2 = rootView.findViewById(R.id.tl_2);
//        findview();
////        onclick();
//        doNetWork();
//
//    }
//
//    private void doNetWork() {
//        mTabEntities = new ArrayList<>();
//        for (int i = 0; i < mTitles.length; i++) {
//            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
//        }
////        current_pos = 0;
//        tl2();
//        mTabLayout_2.setCurrentTab(current_pos);
//        showFragment(false);
//    }
//
//    private void tl2() {
//        mTabLayout_2.setTabData(mTabEntities);
//        mTabLayout_2.setOnTabSelectListener(new OnTabSelectListener() {
//            @Override
//            public void onTabSelect(int position) {
////                mViewPager.setCurrentItem(position);
//                if (position == 1 || position == 2) {
//                    SlbLoginUtil.get().loginTowhere(getActivity(), new Runnable() {
//                        @Override
//                        public void run() {
//                            current_pos = position;
//                            mTabLayout_2.setCurrentTab(current_pos);
//                            showFragment(false);
//                        }
//                    });
//                } else {
//                    current_pos = position;
//                    mTabLayout_2.setCurrentTab(current_pos);
//                    showFragment(false);
//                }
//            }
//
//            @Override
//            public void onTabReselect(int position) {
//                if (current_pos == position) {
//                    // refresh
//                    // 不切换当前的item点击 刷新当前页面
//                    mTabLayout_2.setCurrentTab(current_pos);
//                    showFragment(true);
//                }
////                if (position == 0) {
////                    mTabLayout_2.showMsg(0, mRandom.nextInt(100) + 1);
//////                    UnreadMsgUtils.show(mTabLayout_2.getMsgView(0), mRandom.nextInt(100) + 1);
////                }
//            }
//        });
//        //
////        //两位数
////        mTabLayout_2.showMsg(0, 55);
////        mTabLayout_2.setMsgMargin(0, -5, 5);
////
////        //三位数
////        mTabLayout_2.showMsg(1, 100);
////        mTabLayout_2.setMsgMargin(1, -5, 5);
////
////        //设置未读消息红点
////        mTabLayout_2.showDot(2);
////        MsgView rtv_2_2 = mTabLayout_2.getMsgView(2);
////        if (rtv_2_2 != null) {
////            UnreadMsgUtils.setSize(rtv_2_2, dp2px(7.5f));
////        }
////
////        //设置未读消息背景
////        mTabLayout_2.showMsg(3, 5);
////        mTabLayout_2.setMsgMargin(3, 0, 5);
////        MsgView rtv_2_3 = mTabLayout_2.getMsgView(3);
////        if (rtv_2_3 != null) {
////            rtv_2_3.setBackgroundColor(Color.parseColor("#6D8FB0"));
////        }
////        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
////            @Override
////            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
////
////            }
////
////            @Override
////            public void onPageSelected(int position) {
////                mTabLayout_2.setCurrentTab(position);
////            }
////
////            @Override
////            public void onPageScrollStateChanged(int state) {
////
////            }
////        });
////
////        mViewPager.setCurrentItem(1);
//    }
//
//    private void findview() {
//        // 动态设置首页bufen
//        mMessageReceiver = new MessageReceiverIndex();
//        IntentFilter filter = new IntentFilter();
//        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//        filter.addAction("ShouyeFragment");
//        LocalBroadcastManagers.getInstance(getActivity().getApplicationContext()).registerReceiver(mMessageReceiver, filter);
//
//    }
//
//    private long exitTime;
//
//    private void exit() {
//        if (current_pos != 0) {
//            current_pos = 0;
//            mTabLayout_2.setCurrentTab(current_pos);
//            showFragment(false);
//        } else {
//            if ((System.currentTimeMillis() - exitTime) < 1500) {
//                ActivityUtils.finishAllActivities();
//            } else {
//                ToastUtils.showLong("再次点击退出程序哟 ~");
//                exitTime = System.currentTimeMillis();
//            }
//        }
//    }
//
//    private void showFragment(final boolean isrefresh) {
//        if (mFragmentManager == null) {
//            return;
//        }
//        final FragmentTransaction transaction = mFragmentManager.beginTransaction();
//        hideFragments(transaction);
//        if (current_pos == 0) {
//            if (mFragment1 == null && !mFragment1.isAdded()) {
//                mFragment1 = new ShouyeF5();
//                Bundle args = new Bundle();
//                args.putString("tablayoutId", current_pos + "");
//                mFragment1.setArguments(args);
//                transaction.add(R.id.container, mFragment1, LIST_TAG1);
//                MyLogUtil.e("hideFragments->transaction", "mFragment1" + LIST_TAG1);
//            } else {
//                transaction.show(mFragment1);
//                mFragment1.getCate(current_pos + "", isrefresh);
//            }
//        } else if (current_pos == 1) {
//            if (mFragment2 == null && !mFragment2.isAdded()) {
//                mFragment2 = new TUIConversationFragment();
//                Bundle args = new Bundle();
////                args.putString(AgentWebFragment.URL_KEY, "https://m.jd.com/");
////                args.putString(AgentWebFragment.URL_KEY, "http://www.dtdjzx.gov.cn/staticPage/dtsy/redianguanzhu/20201104/2770375.html?share=1");
//                args.putString(AgentWebFragment.URL_KEY, "http://58.58.113.182:8091/#/");
//                mFragment2.setArguments(args);
//                transaction.add(R.id.container, mFragment2, LIST_TAG2);
//                MyLogUtil.e("hideFragments->transaction", "mFragment2" + LIST_TAG2);
//            } else {
//                transaction.show(mFragment2);
////                mFragment2.getCate(current_pos + "", isrefresh);
//            }
//        } else if (current_pos == 2) {
//            if (mFragment3 == null && !mFragment3.isAdded()) {
//                mFragment3 = new TUIContactFragment();
//                Bundle args = new Bundle();
//                args.putString("tablayoutId", current_pos + "");
//                mFragment3.setArguments(args);
//                transaction.add(R.id.container, mFragment3, LIST_TAG3);
//            } else {
//                transaction.show(mFragment3);
////                mFragment3.getCate(current_pos + "", isrefresh);
//            }
//        } else if (current_pos == 3) {
//            if (mFragment4 == null && !mFragment4.isAdded()) {
//                mFragment4 = new ShouyeF6();
//                Bundle args = new Bundle();
//                args.putString("tablayoutId", current_pos + "");
//                mFragment4.setArguments(args);
//                transaction.add(R.id.container, mFragment4, LIST_TAG4);
//            } else {
//                transaction.show(mFragment4);
//                mFragment4.getCate(current_pos + "", isrefresh);
//            }
//        } else if (current_pos == 4) {
//            if (mFragment5 == null && !mFragment5.isAdded()) {
//                mFragment5 = new ShouyeF1();
//                Bundle args = new Bundle();
//                args.putString("tablayoutId", current_pos + "");
//                mFragment5.setArguments(args);
//                transaction.add(R.id.container, mFragment5, LIST_TAG5);
//            } else {
//                transaction.show(mFragment5);
//                mFragment5.getCate(current_pos + "", isrefresh);
//            }
//        }
//        transaction.commitAllowingStateLoss();
//    }
//
//    private void hideFragments(FragmentTransaction transaction) {
//        if (mFragment1 != null) {
//            transaction.hide(mFragment1);
//            mFragment1.give_id(current_pos + "");
//            MyLogUtil.e("hideFragments", "mFragment1");
//        }
//        if (mFragment2 != null) {
//            transaction.hide(mFragment2);
////            mFragment2.give_id(current_pos + "");
//            MyLogUtil.e("hideFragments", "mFragment2");
//        }
//        if (mFragment3 != null) {
//            transaction.hide(mFragment3);
////            mFragment3.give_id(current_pos + "");
//            MyLogUtil.e("hideFragments", "mFragment3");
//        }
//        if (mFragment4 != null) {
//            transaction.hide(mFragment4);
//            mFragment4.give_id(current_pos + "");
//            MyLogUtil.e("hideFragments", "mFragment4");
//        }
//        if (mFragment5 != null) {
//            transaction.hide(mFragment5);
//            mFragment5.give_id(current_pos + "");
//            MyLogUtil.e("hideFragments", "mFragment5");
//        }
//    }
//
//}
