//package com.geek.appindex.index.beifen;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Build;
//import android.os.Bundle;
//import android.text.TextPaint;
//import android.text.TextUtils;
//import android.text.method.LinkMovementMethod;
//import android.text.style.ClickableSpan;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.CheckBox;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.blankj.utilcode.util.ActivityUtils;
//import com.blankj.utilcode.util.AppUtils;
//import com.blankj.utilcode.util.SPUtils;
//import com.blankj.utilcode.util.ToastUtils;
//import com.geek.liblocations.LocListener;
//import com.geek.liblocations.LocUtil;
//import com.geek.liblocations.LocationBean;
//import com.geek.appcommon.AppCommonUtils;
//import com.geek.appcommon.SlbBase;
//import com.geek.appindex.R;
//import com.geek.appindex.index.fragment.ShouyeF1;
//import com.geek.appindex.index.fragment.ShouyeF5;
//import com.geek.appindex.index.fragment.ShouyeF6;
//import com.geek.biz1.bean.FinitBean;
//import com.geek.biz1.bean.FshengjiBean;
//import com.geek.biz1.presenter.FshengjiPresenter;
//import com.geek.biz1.view.FshengjiView;
//import com.geek.libbase.base.SlbBaseLazyFragmentNew;
//import com.geek.libbase.plugins.PluginManager;
//import com.geek.libbase.utils.CommonUtils;
//import com.geek.liblanguage.MultiLanguages;
//import com.geek.libupdateapp.util.UpdateAppReceiver;
//import com.geek.libutils.SlbLoginUtil;
//import com.geek.libutils.app.BaseApp;
//import com.geek.libutils.app.LocalBroadcastManagers;
//import com.geek.libutils.app.MyLogUtil;
//import com.geek.libutils.data.MmkvUtils;
//import com.geek.libutils.jiami.Md5Utils;
//import com.geek.libutils.shortcut.ShortcutUtils;
//import com.geek.libutils.shortcut.core.Shortcut;
//import com.geek.tablayout.entity.TabEntity;
//import com.geek.tablib.tablayout.CommonTabLayout;
//import com.geek.tablib.tablayout.listener.CustomTabEntity;
//import com.geek.tablib.tablayout.listener.OnTabSelectListener;
//import com.geek.libupdateapp.util.UpdateAppUtils;
//import com.haier.cellarette.baselibrary.btnonclick.view.BounceView;
//import com.haier.cellarette.baselibrary.zothers.SpannableStringUtils;
//import com.heytap.msp.push.HeytapPushManager;
//import com.huawei.agconnect.config.AGConnectServicesConfig;
//import com.huawei.hms.aaid.HmsInstanceId;
//import com.huawei.hms.common.ApiException;
//import com.just.agentweb.geek.fragment.AgentWebFragment;
//import com.just.agentweb.geek.hois3.HiosHelperNew;
//import com.lxj.xpopup.XPopup;
//import com.lxj.xpopup.core.BasePopupView;
//import com.lxj.xpopup.core.CenterPopupView;
//import com.mob.pushsdk.MobPush;
//import com.mob.pushsdk.MobPushCallback;
//import com.tencent.imsdk.v2.V2TIMCallback;
//import com.tencent.qcloud.tim.demo.bean.UserInfo;
//import com.tencent.qcloud.tim.demo.thirdpush.OPPOPushImpl;
//import com.tencent.qcloud.tim.demo.thirdpush.ThirdPushTokenMgr;
//import com.tencent.qcloud.tim.demo.utils.BrandUtil;
//import com.tencent.qcloud.tim.demo.utils.DemoLog;
//import com.tencent.qcloud.tim.demo.utils.PrivateConstants;
//import com.tencent.qcloud.tim.demo.utils.TUIUtils;
//import com.tencent.qcloud.tuicore.util.ToastUtil;
//import com.tencent.qcloud.tuikit.tuicontact.ui.pages.TUIContactFragment;
//import com.tencent.qcloud.tuikit.tuiconversation.ui.page.TUIConversationFragment;
//import com.vivo.push.IPushActionListener;
//import com.vivo.push.PushClient;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class ShouyeActivitybeifen extends SlbBase implements FshengjiView {
//
//    private CommonTabLayout mTabLayout_2;
//    private String[] mTitles = {"首页", "消息"/*, "联系人", "我的"*/};
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
//    private FshengjiPresenter fshengjiPresenter;
//    private String apkPath = "";
//    private int serverVersionCode = 0;
//    private String serverVersionName = "";
//    private String updateInfoTitle = "";
//    private String updateInfo = "";
//    private String md5 = "";
//    private String appPackageName = "";
//    //
//    private ShouyeF1 mFragment1; //
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
//                if ("ShouyeActivity".equals(intent.getAction())) {
//                    //点击item
//                    if (intent.getIntExtra("id", -1) != -1) {
//                        current_pos = intent.getIntExtra("id", 0);
//                        mTabLayout_2.setCurrentTab(current_pos);
//                    }
//                    if (!TextUtils.isEmpty(intent.getStringExtra("mobid"))) {
////                        mAdapter.setHongdiao_count(intent.getStringExtra("mobid"));
////                        mAdapter.notifyDataSetChanged();
//                    }
//                }
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    //
//    private final static String ACTION = "com.example.receiver.action";
//    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(ShouyeActivitybeifen.this, "我是宿主，收到插件消息", Toast.LENGTH_SHORT).show();
//        }
//    };
//
//    private final Shortcut.Callback callback = new Shortcut.Callback() {
//        @Override
//        public void onAsyncCreate(String id, String label) {
//            ShortcutUtils.dismissTryTipDialog();
//            if (!"huawei".equalsIgnoreCase(Build.MANUFACTURER) && !"samsung".equalsIgnoreCase(Build.MANUFACTURER)) {
//                MyLogUtil.e("创建成功，id = " + id + ", label = " + label);
//            } else {
//                Log.d("TAG", "onAsyncCreate: " + "系统会提示");
//            }
//        }
//    };
//
//    UpdateAppReceiver updateAppReceiver = new UpdateAppReceiver();
//
//    private void get_loc() {
//        //
//        LocUtil.getLocation(ShouyeActivitybeifen.this, new LocListener() {
//
//            @Override
//            public void success(LocationBean model) {
//                String lastLatitude = String.valueOf(model.getLatitude());
//                String lastLongitude = String.valueOf(model.getLongitude());
//                String aaaa = lastLatitude + "," + lastLongitude;
//                MmkvUtils.getInstance().set_common_json2("location", model);
//                // shuaxin
//                Intent msgIntent = new Intent();
//                msgIntent.setAction("gongzuotai");
//                msgIntent.putExtra("dingwei", model.getCity());
//                LocalBroadcastManagers.getInstance(ShouyeActivitybeifen.this).sendBroadcast(msgIntent);
//            }
//
//            @Override
//            public void fail(int msg) {
//                String aaa = msg + "";
////                ToastUtils.showLong(aaa);
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        MultiLanguages.attach(getApplicationContext());
//        updateAppReceiver.setBr(this);
////        PgyerSDKManager.checkSoftwareUpdate(this);
//        Shortcut.getSingleInstance().addShortcutCallback(callback);
//        initPop();
//        //
////        if (!LocationUtils2.isGpsEnabled()) {
////            LocationUtils2.openGpsSettings();
////        } else {
////            get_loc();
////        }
//        get_loc();
//        if (!SlbLoginUtil.get().isUserLogin()) {
//            mTabLayout_2.setCurrentTab(current_pos);
//        }
//        mUserInfo = UserInfo.getInstance();
//        if (mUserInfo != null && mUserInfo.isAutoLogin()) {
//            TUIUtils.login(mUserInfo.getUserId(), mUserInfo.getUserSig(), new V2TIMCallback() {
//                @Override
//                public void onError(final int code, final String desc) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ToastUtil.toastLongMessage(getString(com.tencent.qcloud.tim.demo.R.string.failed_login_tip) + ", errCode = " + code + ", errInfo = " + desc);
////                        startLogin();
//                        }
//                    });
//                    DemoLog.i("TAG", "imLogin errorCode = " + code + ", errorInfo = " + desc);
//                }
//
//                @Override
//                public void onSuccess() {
//
//                }
//            });
//        }
//        super.onResume();
//    }
//
//    private UserInfo mUserInfo;
//
//    @Override
//    protected void onDestroy() {
//        updateAppReceiver.desBr(this);
//        Shortcut.getSingleInstance().removeShortcutCallback(callback);
//        ShortcutUtils.dismissPermissionTipDialog();
//        ShortcutUtils.dismissTryTipDialog();
//        if (fshengjiPresenter != null) {
//            fshengjiPresenter.onDestory();
//        }
//        LocalBroadcastManagers.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
//        super.onDestroy();
//    }
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
//    public static final List<String> PAGE_TAGS = new ArrayList<>();
//    private List<Class<? extends Fragment>> fragmentClasses = Arrays.asList(ShouyeF5.class,
//            TUIConversationFragment.class, ShouyeF6.class, TUIContactFragment.class, ShouyeF1.class); //自定义的Fragment，主要目的是在初始化时能够通过循环初始化，与重建时的恢复统一
//    private List<Fragment> fragments = new ArrayList<>();
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt(SAVED_CURRENT_ID, current_pos);
//    }
//
//    private void initFragments(Bundle savedInstanceState) {
//        for (int i = 0; i < fragments.size(); i++) {
//            fragments.set(i, mFragmentManager.findFragmentByTag(PAGE_TAGS.get(i)));
//            if (fragments.get(i) == null) {
//                try {
//                    fragments.set(i, fragmentClasses.get(i).newInstance());
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
//            if (cachedId >= 0 && cachedId <= 4) {
//                current_pos = cachedId;
//            }
//        }
//        switchFragment(current_pos, false);
//    }
//
//    private void switchFragment(int index, boolean anim) {
//        /* Fragment 切换 */
//        FragmentTransaction transaction = mFragmentManager.beginTransaction();
//        if (anim) {  //显示切换动画
////            if (index > current_pos) {
////                transaction.setCustomAnimations(R.anim.activity_right_to_left, R.anim.activity_left_to_right);
////            } else {
////                transaction.setCustomAnimations(R.anim.activity_left_to_right, R.anim.activity_right_to_left);
////            }
//        }
//        if (fragments.get(index).isAdded()) {
//            transaction.hide(fragments.get(current_pos));
//            transaction.show(fragments.get(index));
//        } else {
//            transaction.hide(fragments.get(current_pos));
//            transaction.add(R.id.container, fragments.get(index), PAGE_TAGS.get(index));
//            transaction.show(fragments.get(index));
//        }
////        transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        transaction.commitAllowingStateLoss();
//        current_pos = index;
//    }
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        mFragmentManager = getSupportFragmentManager();
//        // 解决fragment布局重叠错乱
//        if (savedInstanceState != null) {
//            int cachedId = savedInstanceState.getInt(SAVED_CURRENT_ID, 0);
//            if (cachedId >= 0 && cachedId <= mTitles.length) {
//                current_pos = cachedId;
//            }
//            mFragment1 = (ShouyeF1) mFragmentManager.findFragmentByTag(LIST_TAG1);
//            mFragment2 = (TUIConversationFragment) mFragmentManager.findFragmentByTag(LIST_TAG2);
//            mFragment3 = (TUIContactFragment) mFragmentManager.findFragmentByTag(LIST_TAG3);
//            mFragment4 = (ShouyeF6) mFragmentManager.findFragmentByTag(LIST_TAG4);
//            mFragment5 = (ShouyeF1) mFragmentManager.findFragmentByTag(LIST_TAG5);
////            showFragment(false);
//            // zhutibufen
////            theme = savedInstanceState.getInt(KEY);
////            setTheme(theme > 0 ? theme : R.style.AppThemeBaseAppcommon);
//        }
//        super.onCreate(savedInstanceState);
//        // 插件bufen
//        PluginManager.getInstance().setContext(this);
//        //宿主中正常注册广播
//        registerReceiver(mReceiver, new IntentFilter(ACTION));
//        //
//        prepareThirdPushToken();
//        //
////        if (theme == R.style.AppThemeBaseAppcommon) {
////            tv_theme1.setText("默认主题");
////        } else if (theme == R.style.AppThemeBaseAppcommon2) {
////            tv_theme1.setText("自定义主题");
////        }
//        tv_theme1 = findViewById(R.id.tv_theme1);
//        tv_theme1.setVisibility(View.GONE);
//        tv_theme1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //
////                if (tv_theme1.getText().toString().startsWith("自定义")) {
////                    tv_theme1.setText("默认主题");
////                    theme = R.style.AppThemeBaseAppcommon;
////                } else if (tv_theme1.getText().toString().startsWith("默认")) {
////                    tv_theme1.setText("自定义主题");
////                    theme = R.style.AppThemeBaseAppcommon2;
////                }
////                recreate();
//                //
////                String url = "dataability://cs.znclass.com/com.github.geekcodesteam.app.hs.act.slbapp.ShouyeCateAct1?query1={s}1&query2={s}本地应用&query3={s}5";
////                String url = "dataability://com.github.geekcodesteam.app.hs.act.slbapp.ShouyeCateAct1{act}?query1={s}1&query2={s}本地应用&query3={s}5";
////                HiosHelperNew.resolveAd(ShouyeActivity.this, ShouyeActivity.this, url);
//                startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.Dt10Act"));
//            }
//        });
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        MyLogUtil.e("TencentIM", "onNewIntent");
//        setIntent(intent);
//        prepareThirdPushToken();
//    }
//
//    private void prepareThirdPushToken() {
//        //
//        MobPush.getDeviceToken(new MobPushCallback<String>() {
//            @Override
//            public void onCallback(String s) {
//                MyLogUtil.e("MobPush----getDeviceToken--", s);
//                SPUtils.getInstance().put("MOBDeviceToken", s);
//                if (!TextUtils.isEmpty(s)) {
//                    ThirdPushTokenMgr.getInstance().setThirdPushToken(s);
//                }
//            }
//        });
//        //
//        ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
//
//        if (BrandUtil.isBrandHuawei()) {
//            // 华为离线推送
//            new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        // read from agconnect-services.json
//                        String appId = AGConnectServicesConfig.fromContext(ShouyeActivitybeifen.this).getString("client/app_id");
//                        String token = HmsInstanceId.getInstance(ShouyeActivitybeifen.this).getToken(appId, "HCM");
//                        DemoLog.e("TencentIM", "huawei get token:" + token);
//                        if (!TextUtils.isEmpty(token)) {
//                            ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
//                            ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
//                        }
//                    } catch (ApiException e) {
//                        DemoLog.e("TAG", "huawei get token failed, " + e);
//                    }
//                }
//            }.start();
//        } else if (BrandUtil.isBrandVivo()) {
//            // vivo离线推送
//            DemoLog.e("TencentIM", "vivo support push: " + PushClient.getInstance(getApplicationContext()).isSupport());
//            PushClient.getInstance(getApplicationContext()).turnOnPush(new IPushActionListener() {
//                @Override
//                public void onStateChanged(int state) {
//                    if (state == 0) {
//                        String regId = PushClient.getInstance(getApplicationContext()).getRegId();
//                        DemoLog.e("TencentIM", "vivopush open vivo push success regId = " + regId);
//                        ThirdPushTokenMgr.getInstance().setThirdPushToken(regId);
//                        ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
//                    } else {
//                        // 根据vivo推送文档说明，state = 101 表示该vivo机型或者版本不支持vivo推送，链接：https://dev.vivo.com.cn/documentCenter/doc/156
//                        DemoLog.e("TencentIM", "vivopush open vivo push fail state = " + state);
//                    }
//                }
//            });
//        } else if (HeytapPushManager.isSupportPush()) {
//            // oppo离线推送
//            OPPOPushImpl oppo = new OPPOPushImpl();
//            oppo.createNotificationChannel(this);
//            // oppo接入文档要求，应用必须要调用init(...)接口，才能执行后续操作。
//            HeytapPushManager.init(this, false);
//            HeytapPushManager.register(this, PrivateConstants.OPPO_PUSH_APPKEY, PrivateConstants.OPPO_PUSH_APPSECRET, oppo);
//        } /*else if (BrandUtil.isGoogleServiceSupport()) {
//            // 谷歌推送
//        }*/
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_shouyebeifen;
//    }
//
//    @Override
//    protected void setup(@Nullable Bundle savedInstanceState) {
//        super.setup(savedInstanceState);
//        findview();
////        onclick();
//        doNetWork();
//        //
//        fshengjiPresenter = new FshengjiPresenter();
//        fshengjiPresenter.onCreate(this);
//        apkPath = "";
//        updateInfoTitle = "";
//        updateInfo = "";
//        serverVersionCode = AppUtils.getAppVersionCode();
//        serverVersionName = AppUtils.getAppVersionName();
//        appPackageName = AppUtils.getAppPackageName();
//        md5 = Md5Utils.get32Md5LowerCase(appPackageName);
//        MyLogUtil.e("ssssssssssssss", md5);
//        fshengjiPresenter.getshengji(AppCommonUtils.auth_url,
//                serverVersionCode + "", serverVersionName, appPackageName, md5);
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
//                    SlbLoginUtil.get().loginTowhere(ShouyeActivitybeifen.this, new Runnable() {
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
//        mTabLayout_2 = findViewById(R.id.tl_2);
//        // 动态设置首页bufen
//        mMessageReceiver = new MessageReceiverIndex();
//        IntentFilter filter = new IntentFilter();
//        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//        filter.addAction("ShouyeActivity");
//        LocalBroadcastManagers.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver, filter);
//
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            exit();
//
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
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
//            if (mFragment1 == null) {
//                mFragment1 = new ShouyeF1();
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
//            if (mFragment2 == null) {
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
//            if (mFragment3 == null) {
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
//            if (mFragment4 == null) {
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
//            if (mFragment5 == null) {
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
//    /**
//     * fragment间通讯bufen
//     *
//     * @param value 要传递的值
//     * @param tag   要通知的fragment的tag
//     */
//    public void callFragment(Object value, String... tag) {
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment;
//        for (String item : tag) {
//            if (TextUtils.isEmpty(item)) {
//                continue;
//            }
//
//            fragment = fm.findFragmentByTag(item);
//            if (fragment instanceof SlbBaseLazyFragmentNew) {
//                ((SlbBaseLazyFragmentNew) fragment).call(value);
//            }
//        }
//    }
//
//    public int dp2px(float dp) {
//        final float scale = getResources().getDisplayMetrics().density;
//        return (int) (dp * scale + 0.5f);
//    }
//
//    private BasePopupView loadingPopup = null;
//
//    //隐私政策
//    private void initPop() {
//        if (!MmkvUtils.getInstance().get_xiancheng_bool(CommonUtils.MMKV_forceLogin)) {
//            if (loadingPopup != null) {
//                if (!loadingPopup.isShow()) {
//                    loadingPopup.show();
//                }
//                return;
//            }
//            loadingPopup = new XPopup.Builder(this)
//                    .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
////              .isViewMode(true)
//                    .dismissOnTouchOutside(false)
//                    .asCustom(new CenterPopupView(this) {
//                        private TextView tvContent;
//                        private CheckBox radAgreement;
//                        private TextView btnClose;
//                        private TextView btnOk;
//
//                        @Override
//                        protected int getImplLayoutId() {
//                            return R.layout.dialog_agreement1;
//                        }
//
//                        @Override
//                        protected void onCreate() {
//                            super.onCreate();
//                            tvContent = findViewById(R.id.tv_content);
//                            radAgreement = findViewById(R.id.rad_agreement);
//                            btnClose = findViewById(R.id.btn_cancle);
//                            btnOk = findViewById(R.id.btn_ok);
//                            FinitBean fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
//                            tvContent.setText(SpannableStringUtils.getInstance(ShouyeActivitybeifen.this)
//                                    .getBuilder("欢迎使用移动端平台！灯塔非常重视您的隐私保护和个人信息保护。在您使用移动端平台前，请认真阅读")
//                                    .append("《用户须知》")
//                                    .setClickSpan(new ClickableSpan() {
//                                        @Override
//                                        public void onClick(View widget) {
//                                            HiosHelperNew.resolveAd(ShouyeActivitybeifen.this, ShouyeActivitybeifen.this, fconfigBean.getUser());
//                                        }
//
//                                        @Override
//                                        public void updateDrawState(TextPaint ds) {
//                                            ds.setColor(ContextCompat.getColor(BaseApp.get(), R.color.red));
//                                            ds.setUnderlineText(false);
//                                        }
//                                    })
//                                    .append("及")
//                                    .append("《隐私权政策》")
//                                    .setClickSpan(new ClickableSpan() {
//                                        @Override
//                                        public void onClick(View widget) {
////                                        Uri url = Uri.parse("http://blog.51cto.com/liangxiao");
////                                        Intent intent = new Intent(Intent.ACTION_VIEW);
////                                        intent.setData(url);
////                                        startActivity(intent);
////                                        HiosHelper.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, MmkvUtils.getInstance().get_common(CommonUtils.MMKV_privacyPolicy));
//                                            HiosHelperNew.resolveAd(ShouyeActivitybeifen.this, ShouyeActivitybeifen.this, fconfigBean.getPrivacy());
//                                        }
//
//                                        @Override
//                                        public void updateDrawState(TextPaint ds) {
//                                            ds.setColor(ContextCompat.getColor(ShouyeActivitybeifen.this, R.color.red));
//                                            ds.setUnderlineText(false);
//                                        }
//                                    })
//                                    .append("，您同意并接受全部条款后方可使用。")
//                                    .create());
//                            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
//                            BounceView.addAnimTo(btnOk);
//                            BounceView.addAnimTo(btnClose);
//                            btnOk.setOnClickListener(new OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    if (!radAgreement.isChecked()) {
//                                        com.hjq.toast.ToastUtils.show("请勾选用户须知及隐私权政策");
//                                        return;
//                                    }
//                                    dismiss();
//                                    MmkvUtils.getInstance().set_xiancheng(CommonUtils.MMKV_forceLogin, true);
//                                }
//                            });
//                            btnClose.setOnClickListener(new OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    dismiss();
//                                    ActivityUtils.finishAllActivities();
//                                    finish();
//                                }
//                            });
//                        }
//                        //        @Override
//                        //        protected int getMaxHeight() {
//                        //            return 200;
//                        //        }
//                        //
//                        //返回0表示让宽度撑满window，或者你可以返回一个任意宽度
//                        //        @Override
//                        //        protected int getMaxWidth() {
//                        //            return 1200;
//                        //        }
//                    });
//            if (!loadingPopup.isShow()) {
//                loadingPopup.show();
//            }
//        }
//    }
//
//    @Override
//    public void OnFshengjiSuccess(FshengjiBean bean) {
//        apkPath = bean.getApkPath();
//        serverVersionCode = Integer.valueOf(bean.getServerVersionCode());
//        serverVersionName = bean.getServerVersionName();
//        updateInfoTitle = bean.getUpdateInfoTitle();
//        updateInfo = bean.getUpdateInfo();
//        if (TextUtils.isEmpty(apkPath)) {
//            return;
//        }
//        UpdateAppUtils.from(ShouyeActivitybeifen.this)
//                .serverVersionCode(serverVersionCode)
//                .serverVersionName(serverVersionName)
//                .downloadPath("apks/" + getPackageName() + ".apk")
//                .showProgress(true)
//                .isForce(bean.getIsForce())
//                .apkPath(apkPath)
//                .downloadBy(UpdateAppUtils.DOWNLOAD_BY_APP)    //default
//                .checkBy(UpdateAppUtils.CHECK_BY_VERSION_CODE) //default
//                .updateInfoTitle(updateInfoTitle)
//                .updateInfo(updateInfo.replace("|", "\n"))
////                    .showNotification(true)
////                    .needFitAndroidN(true)
//                .update();
//    }
//
//    @Override
//    public void OnFshengjiNodata(String msg) {
////        ToastUtils.showLong(msg);
//    }
//
//    @Override
//    public void OnFshengjiFail(String msg) {
////        ToastUtils.showLong(msg);
//    }
//
//    /**
//     *  升级接口业务bufen
//     * @param versionInfoBean
//     */
////    @Override
////    public void OnUpdateVersionSuccess(VersionInfoBean versionInfoBean) {
////        apkPath = versionInfoBean.getTargetUrl();
////        serverVersionCode = Integer.valueOf(versionInfoBean.getTargetUrl());
////        serverVersionName = versionInfoBean.getTargetUrl();
////        updateInfoTitle = versionInfoBean.getTargetUrl();
////        updateInfo = versionInfoBean.getTargetUrl();
////        if (TextUtils.equals(versionInfoBean.getTargetUrl(), "1")) {
////            // 强制检查更新bufen
////            UpdateAppUtils.from(ShouyeActivity.this)
////                    .serverVersionCode(serverVersionCode)
////                    .serverVersionName(serverVersionName)
////                    .downloadPath("apks/" + getPackageName() + ".apk")
////                    .showProgress(true)
////                    .isForce(true)
////                    .apkPath(apkPath)
////                    .downloadBy(UpdateAppUtils.DOWNLOAD_BY_APP)    //default
////                    .checkBy(UpdateAppUtils.CHECK_BY_VERSION_CODE) //default
////                    .updateInfoTitle(updateInfoTitle)
////                    .updateInfo(updateInfo.replace("|", "\n"))
////                    .showNotification(true)
//////                    .needFitAndroidN(true)
//////                    .updateInfoTitle("新版本已准备好")
//////                    .updateInfo("版本：1.01" + "    " + "大小：10.41M\n" + "1.修改已知问题\n2.加入动态绘本\n3.加入小游戏等你来学习升级")
////                    .update();
////        } else {
////            // 检查更新bufen
////            UpdateAppUtils.from(ShouyeActivity.this)
////                    .serverVersionCode(serverVersionCode)
////                    .serverVersionName(serverVersionName)
////                    .downloadPath("apks/" + getPackageName() + ".apk")
////                    .showProgress(true)
////                    .apkPath(apkPath)
////                    .downloadBy(UpdateAppUtils.DOWNLOAD_BY_APP)    //default
////                    .checkBy(UpdateAppUtils.CHECK_BY_VERSION_CODE) //default
////                    .updateInfoTitle(updateInfoTitle)
////                    .updateInfo(updateInfo.replace("|", "\n"))
////                    .showNotification(true)
//////                    .needFitAndroidN(true)
//////                    .updateInfoTitle("新版本已准备好")
//////                    .updateInfo("版本：1.01" + "    " + "大小：10.41M\n" + "1.修改已知问题\n2.加入动态绘本\n3.加入小游戏等你来学习升级")
////                    .update();
////        }
////    }
////
////    @Override
////    public void OnUpdateVersionNodata(String bean) {
////
////    }
////
////    @Override
////    public void OnUpdateVersionFail(String msg) {
////
////    }
//}
