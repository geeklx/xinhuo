package com.geek.appindexdt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.SlbBase;
import com.geek.appindexdt.fragments.MkIndexAct1Fragment;
import com.geek.libbase.base.SlbBaseActivity;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libbase.plugins.PluginManager;
import com.geek.libbase.utils.CommonUtils;
import com.geek.libutils.app.BaseApp;
import com.geek.libutils.app.BaseAppManager;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.MmkvUtils;
import com.geek.libutils.shortcut.ShortcutUtils;
import com.geek.libutils.shortcut.core.Shortcut;
import com.geek.tablayout.entity.TabEntity;
import com.geek.tablib.tablayout.CommonTabLayout;
import com.geek.tablib.tablayout.listener.CustomTabEntity;
import com.geek.tablib.tablayout.listener.OnTabSelectListener;
import com.geek.tablib.tablayout.utils.UnreadMsgUtils;
import com.geek.tablib.tablayout.widget.MsgView;
import com.haier.cellarette.baselibrary.btnonclick.view.BounceView;
import com.haier.cellarette.baselibrary.zothers.SpannableStringUtils;
import com.geek.libwebview.hois2.HiosHelper;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;
import com.pgyer.pgyersdk.PgyerSDKManager;

import java.util.ArrayList;

public class IndexAct extends SlbBase {

    private CommonTabLayout mTabLayout_2;
    private String[] mTitles = {"首页", "消息", "联系人", "更多"};
    private int[] mIconUnselectIds = {
    R.mipmap.tab_home_unselect, R.mipmap.tab_speech_unselect,
            R.mipmap.tab_contact_unselect, R.mipmap.tab_more_unselect};
    private int[] mIconSelectIds = {
            R.mipmap.tab_home_select, R.mipmap.tab_speech_select,
            R.mipmap.tab_contact_select, R.mipmap.tab_more_select};
    private ArrayList<CustomTabEntity> mTabEntities;

    private int current_pos = 0;
    private FragmentManager mFragmentManager;
    private static final String LIST_TAG1 = "list11";
    private static final String LIST_TAG2 = "list22";
    private static final String LIST_TAG3 = "list33";
    private static final String LIST_TAG4 = "list44";
    //
//    private CheckverionPresenter presenter;
    private String apkPath = "";
    private int serverVersionCode = 0;
    private String serverVersionName = "";
    private String updateInfoTitle = "";
    private String updateInfo = "";
    private String md5 = "";
    private String appPackageName = "";
    //
    private MkIndexAct1Fragment mFragment1; //
    private IndexF1 mFragment2; //
    private IndexF2 mFragment3; //
    private IndexF1 mFragment4; //

    private MessageReceiverIndex mMessageReceiver;

    public class MessageReceiverIndex extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if ("ShouyeActivity".equals(intent.getAction())) {
                    //点击item
                    if (intent.getIntExtra("id", -1) != -1) {
                        current_pos = intent.getIntExtra("id", 0);
                        mTabLayout_2.setCurrentTab(current_pos);
                    }
                    if (!TextUtils.isEmpty(intent.getStringExtra("mobid"))) {
//                        mAdapter.setHongdiao_count(intent.getStringExtra("mobid"));
//                        mAdapter.notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    //
    private final static String ACTION = "com.example.receiver.action";
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(IndexAct.this, "我是宿主，收到插件消息", Toast.LENGTH_SHORT).show();
        }
    };

    private final Shortcut.Callback callback = new Shortcut.Callback() {
        @Override
        public void onAsyncCreate(String id, String label) {
            ShortcutUtils.dismissTryTipDialog();
            if (!"huawei".equalsIgnoreCase(Build.MANUFACTURER) && !"samsung".equalsIgnoreCase(Build.MANUFACTURER)) {
                MyLogUtil.e("创建成功，id = " + id + ", label = " + label);
            } else {
                Log.d("TAG", "onAsyncCreate: " + "系统会提示");
            }
        }
    };

    @Override
    protected void onResume() {
        PgyerSDKManager.checkSoftwareUpdate(this);
        Shortcut.getSingleInstance().addShortcutCallback(callback);
        initPop();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Shortcut.getSingleInstance().removeShortcutCallback(callback);
        ShortcutUtils.dismissPermissionTipDialog();
        ShortcutUtils.dismissTryTipDialog();
//        if (presenter != null) {
//            presenter.onDestory();
//        }
        LocalBroadcastManagers.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
//
//    @Override
//    public void onAttachFragment(Fragment fragment) {
//        if (mFragment1 == null && fragment instanceof ShouyeF1) {
//            mFragment1 = (ShouyeF1) fragment;
//        }
//        if (mFragment2 == null && fragment instanceof ShouyeF2) {
//            mFragment2 = (ShouyeF2) fragment;
//        }
//        if (mFragment3 == null && fragment instanceof ShouyeF3) {
//            mFragment3 = (ShouyeF3) fragment;
//        }
//        if (mFragment4 == null && fragment instanceof ShouyeF4) {
//            mFragment4 = (ShouyeF4) fragment;
//        }
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mFragmentManager = getSupportFragmentManager();
        // 解决fragment布局重叠错乱
        if (savedInstanceState != null) {
            mFragment1 = (MkIndexAct1Fragment) mFragmentManager.findFragmentByTag(LIST_TAG1);
            mFragment2 = (IndexF1) mFragmentManager.findFragmentByTag(LIST_TAG2);
            mFragment3 = (IndexF2) mFragmentManager.findFragmentByTag(LIST_TAG3);
            mFragment4 = (IndexF1) mFragmentManager.findFragmentByTag(LIST_TAG4);
        }
        super.onCreate(savedInstanceState);
        // 插件bufen
        PluginManager.getInstance().setContext(this);
        //宿主中正常注册广播
        registerReceiver(mReceiver, new IntentFilter(ACTION));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_indexdt;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        findview();
//        onclick();
        doNetWork();
        apkPath = "";
        updateInfoTitle = "";
        updateInfo = "";
        serverVersionCode = AppUtils.getAppVersionCode();
        serverVersionName = AppUtils.getAppVersionName();
        appPackageName = AppUtils.getAppPackageName();
        md5 = AppUtils.getAppSignaturesMD5(appPackageName).get(0);
//        presenter = new CheckverionPresenter();
//        presenter.onCreate(this);
////        presenter.checkVerion("android", serverVersionCode + "", appPackageName, serverVersionName);
//        presenter.checkVerion("android");

    }

    private void doNetWork() {
        mTabEntities = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        current_pos = 0;
        tl2();
        showFragment(false);
    }

    private void tl2() {
        mTabLayout_2.setTabData(mTabEntities);
        mTabLayout_2.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
//                mViewPager.setCurrentItem(position);
                current_pos = position;
                showFragment(false);
            }

            @Override
            public void onTabReselect(int position) {
                if (current_pos == position) {
                    // refresh
                    // 不切换当前的item点击 刷新当前页面
                    showFragment(true);
                }
//                if (position == 0) {
//                    mTabLayout_2.showMsg(0, mRandom.nextInt(100) + 1);
////                    UnreadMsgUtils.show(mTabLayout_2.getMsgView(0), mRandom.nextInt(100) + 1);
//                }
            }
        });
        //
        //两位数
        mTabLayout_2.showMsg(0, 55);
        mTabLayout_2.setMsgMargin(0, -5, 5);

        //三位数
        mTabLayout_2.showMsg(1, 100);
        mTabLayout_2.setMsgMargin(1, -5, 5);

        //设置未读消息红点
        mTabLayout_2.showDot(2);
        MsgView rtv_2_2 = mTabLayout_2.getMsgView(2);
        if (rtv_2_2 != null) {
            UnreadMsgUtils.setSize(rtv_2_2, dp2px(7.5f));
        }

        //设置未读消息背景
        mTabLayout_2.showMsg(3, 5);
        mTabLayout_2.setMsgMargin(3, 0, 5);
        MsgView rtv_2_3 = mTabLayout_2.getMsgView(3);
        if (rtv_2_3 != null) {
            rtv_2_3.setBackgroundColor(Color.parseColor("#6D8FB0"));
        }
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                mTabLayout_2.setCurrentTab(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//        mViewPager.setCurrentItem(1);
    }

    private void findview() {
        mTabLayout_2 = findViewById(R.id.tl_2);
        // 动态设置首页bufen
        mMessageReceiver = new MessageReceiverIndex();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction("ShouyeActivity");
        LocalBroadcastManagers.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver, filter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long exitTime;

    private void exit() {
        if (current_pos != 0) {
            current_pos = 0;
            mTabLayout_2.setCurrentTab(current_pos);
            showFragment(false);
        } else {
            if ((System.currentTimeMillis() - exitTime) < 1500) {
                ActivityUtils.finishAllActivities();
            } else {
                ToastUtils.showLong("再次点击退出程序哟 ~");
                exitTime = System.currentTimeMillis();
            }
        }
    }

    private void showFragment(final boolean isrefresh) {
        final FragmentTransaction transaction = mFragmentManager.beginTransaction();
        hideFragments(transaction);
        if (current_pos == 0) {
            if (mFragment1 == null) {
                mFragment1 = new MkIndexAct1Fragment();
                Bundle args = new Bundle();
                args.putString("tablayoutId", current_pos + "");
                mFragment1.setArguments(args);
                transaction.add(R.id.container, mFragment1, LIST_TAG1);
            } else {
                transaction.show(mFragment1);
                mFragment1.getCate(current_pos + "", isrefresh);
            }
        } else if (current_pos == 1) {
            if (mFragment2 == null) {
                mFragment2 = new IndexF1();
                Bundle args = new Bundle();
                args.putString("url_key", "https://www.baidu.com");
                mFragment2.setArguments(args);
                transaction.add(R.id.container, mFragment2, LIST_TAG2);
            } else {
                transaction.show(mFragment2);
                mFragment2.getCate(current_pos + "", isrefresh);
            }
        } else if (current_pos == 2) {
            if (mFragment3 == null) {
                mFragment3 = new IndexF2();
                Bundle args = new Bundle();
                args.putString("tablayoutId", current_pos + "");
                mFragment3.setArguments(args);
                transaction.add(R.id.container, mFragment3, LIST_TAG3);
            } else {
                transaction.show(mFragment3);
                mFragment3.getCate(current_pos + "", isrefresh);
            }
        } else if (current_pos == 3) {
            if (mFragment4 == null) {
                mFragment4 = new IndexF1();
                Bundle args = new Bundle();
                args.putString("tablayoutId", current_pos + "");
                mFragment4.setArguments(args);
                transaction.add(R.id.container, mFragment4, LIST_TAG4);
            } else {
                transaction.show(mFragment4);
                mFragment4.getCate(current_pos + "", isrefresh);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mFragment1 != null) {
            transaction.hide(mFragment1);
            mFragment1.give_id(current_pos + "");
        }
        if (mFragment2 != null) {
            transaction.hide(mFragment2);
            mFragment2.give_id(current_pos + "");
        }
        if (mFragment3 != null) {
            transaction.hide(mFragment3);
            mFragment3.give_id(current_pos + "");
        }
        if (mFragment4 != null) {
            transaction.hide(mFragment4);
            mFragment4.give_id(current_pos + "");
        }
    }

    /**
     * fragment间通讯bufen
     *
     * @param value 要传递的值
     * @param tag   要通知的fragment的tag
     */
    public void callFragment(Object value, String... tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;
        for (String item : tag) {
            if (TextUtils.isEmpty(item)) {
                continue;
            }

            fragment = fm.findFragmentByTag(item);
            if (fragment instanceof SlbBaseLazyFragmentNew) {
                ((SlbBaseLazyFragmentNew) fragment).call(value);
            }
        }
    }

    public int dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    //隐私政策
    private void initPop() {
        if (!MmkvUtils.getInstance().get_xiancheng_bool(CommonUtils.MMKV_forceLogin)) {
            new XPopup.Builder(this)
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//              .isViewMode(true)
                    .dismissOnTouchOutside(false)
                    .asCustom(new CenterPopupView(this) {
                        private TextView tvContent;
                        private CheckBox radAgreement;
                        private Button btnClose;
                        private Button btnOk;

                        @Override
                        protected int getImplLayoutId() {
                            return R.layout.dialog_agreement_indexact;
                        }

                        @Override
                        protected void onCreate() {
                            super.onCreate();
                            tvContent = findViewById(R.id.tv_content);
                            radAgreement = findViewById(R.id.rad_agreement);
                            btnClose = findViewById(R.id.btn_cancle);
                            btnOk = findViewById(R.id.btn_ok);

                            tvContent.setText(SpannableStringUtils.getInstance(IndexAct.this)
                                    .getBuilder("欢迎使用移动端平台！灯塔非常重视您的隐私保护和个人信息保护。在您使用移动端平台前，请认真阅读")
                                    .append("《用户须知》")
                                    .setClickSpan(new ClickableSpan() {
                                        @Override
                                        public void onClick(View widget) {
                                            HiosHelper.resolveAd(IndexAct.this, IndexAct.this, "https://syzs.qq.com/campaign/3977.html");
                                        }

                                        @Override
                                        public void updateDrawState(TextPaint ds) {
                                            ds.setColor(ContextCompat.getColor(BaseApp.get(), R.color.red));
                                            ds.setUnderlineText(false);
                                        }
                                    })
                                    .append("及")
                                    .append("《隐私权政策》")
                                    .setClickSpan(new ClickableSpan() {
                                        @Override
                                        public void onClick(View widget) {
//                                        Uri url = Uri.parse("http://blog.51cto.com/liangxiao");
//                                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                                        intent.setData(url);
//                                        startActivity(intent);
//                                        HiosHelper.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, MmkvUtils.getInstance().get_common(CommonUtils.MMKV_privacyPolicy));
                                            HiosHelper.resolveAd(IndexAct.this, IndexAct.this, "https://app.dtdjzx.gov.cn/r/cms/qilu/qilu/html/yszc.html?4");
                                        }

                                        @Override
                                        public void updateDrawState(TextPaint ds) {
                                            ds.setColor(ContextCompat.getColor(IndexAct.this, R.color.red));
                                            ds.setUnderlineText(false);
                                        }
                                    })
                                    .append("，您同意并接受全部条款后方可使用。")
                                    .create());
                            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
                            BounceView.addAnimTo(btnOk);
                            BounceView.addAnimTo(btnClose);
                            btnOk.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!radAgreement.isChecked()) {
                                        com.hjq.toast.ToastUtils.show("请勾选用户须知及隐私权政策");
                                        return;
                                    }
                                    dismiss();
                                    MmkvUtils.getInstance().set_xiancheng(CommonUtils.MMKV_forceLogin, true);
                                }
                            });
                            btnClose.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dismiss();
                                    ActivityUtils.finishAllActivities();
                                    finish();
                                }
                            });
                        }
                        //        @Override
                        //        protected int getMaxHeight() {
                        //            return 200;
                        //        }
                        //
                        //返回0表示让宽度撑满window，或者你可以返回一个任意宽度
                        //        @Override
                        //        protected int getMaxWidth() {
                        //            return 1200;
                        //        }
                    }).show();
        }
    }

    /**
     *  升级接口业务bufen
     * @param versionInfoBean
     */
//    @Override
//    public void OnUpdateVersionSuccess(VersionInfoBean versionInfoBean) {
//        apkPath = versionInfoBean.getTargetUrl();
//        serverVersionCode = Integer.valueOf(versionInfoBean.getTargetUrl());
//        serverVersionName = versionInfoBean.getTargetUrl();
//        updateInfoTitle = versionInfoBean.getTargetUrl();
//        updateInfo = versionInfoBean.getTargetUrl();
//        if (TextUtils.equals(versionInfoBean.getTargetUrl(), "1")) {
//            // 强制检查更新bufen
//            UpdateAppUtils.from(ShouyeActivity.this)
//                    .serverVersionCode(serverVersionCode)
//                    .serverVersionName(serverVersionName)
//                    .downloadPath("apks/" + getPackageName() + ".apk")
//                    .showProgress(true)
//                    .isForce(true)
//                    .apkPath(apkPath)
//                    .downloadBy(UpdateAppUtils.DOWNLOAD_BY_APP)    //default
//                    .checkBy(UpdateAppUtils.CHECK_BY_VERSION_CODE) //default
//                    .updateInfoTitle(updateInfoTitle)
//                    .updateInfo(updateInfo.replace("|", "\n"))
//                    .showNotification(true)
////                    .needFitAndroidN(true)
////                    .updateInfoTitle("新版本已准备好")
////                    .updateInfo("版本：1.01" + "    " + "大小：10.41M\n" + "1.修改已知问题\n2.加入动态绘本\n3.加入小游戏等你来学习升级")
//                    .update();
//        } else {
//            // 检查更新bufen
//            UpdateAppUtils.from(ShouyeActivity.this)
//                    .serverVersionCode(serverVersionCode)
//                    .serverVersionName(serverVersionName)
//                    .downloadPath("apks/" + getPackageName() + ".apk")
//                    .showProgress(true)
//                    .apkPath(apkPath)
//                    .downloadBy(UpdateAppUtils.DOWNLOAD_BY_APP)    //default
//                    .checkBy(UpdateAppUtils.CHECK_BY_VERSION_CODE) //default
//                    .updateInfoTitle(updateInfoTitle)
//                    .updateInfo(updateInfo.replace("|", "\n"))
//                    .showNotification(true)
////                    .needFitAndroidN(true)
////                    .updateInfoTitle("新版本已准备好")
////                    .updateInfo("版本：1.01" + "    " + "大小：10.41M\n" + "1.修改已知问题\n2.加入动态绘本\n3.加入小游戏等你来学习升级")
//                    .update();
//        }
//    }
//
//    @Override
//    public void OnUpdateVersionNodata(String bean) {
//
//    }
//
//    @Override
//    public void OnUpdateVersionFail(String msg) {
//
//    }
}
