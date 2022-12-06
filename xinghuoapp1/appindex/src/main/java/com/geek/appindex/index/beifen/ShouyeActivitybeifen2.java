//package com.geek.appindex.index.beifen;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.res.Resources;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Looper;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//
//import com.blankj.utilcode.util.AppUtils;
//import com.blankj.utilcode.util.SPUtils;
//import com.blankj.utilcode.util.ToastUtils;
//import com.geek.appcommon.AppCommonUtils;
//import com.geek.appcommon.SlbBase;
//import com.geek.appindex.R;
//import com.geek.appindex.index.ShouyeFragment;
//import com.geek.appindex.index.fragment.ShouyeF1;
//import com.geek.appindex.index.fragment.ShouyeF5;
//import com.geek.appindex.index.fragment.ShouyeF6;
//import com.geek.biz1.bean.BjyyBeanYewu4;
//import com.geek.biz1.bean.FshengjiBean;
//import com.geek.biz1.presenter.FCate1Presenter;
//import com.geek.biz1.presenter.FshengjiPresenter;
//import com.geek.biz1.view.FCate1View;
//import com.geek.biz1.view.FshengjiView;
//import com.geek.libbase.base.SlbBaseLazyFragmentNew;
//import com.geek.libbase.plugins.PluginManager;
//import com.geek.liblanguage.MultiLanguages;
//import com.geek.liblocations.LocListener;
//import com.geek.liblocations.LocUtil;
//import com.geek.liblocations.LocationBean;
//import com.geek.libupdateapp.util.UpdateAppReceiver;
//import com.geek.libupdateapp.util.UpdateAppUtils;
//import com.geek.libutils.app.LocalBroadcastManagers;
//import com.geek.libutils.app.MyLogUtil;
//import com.geek.libutils.data.MmkvUtils;
//import com.geek.libutils.jiami.Md5Utils;
//import com.geek.libutils.shortcut.ShortcutUtils;
//import com.geek.libutils.shortcut.core.Shortcut;
//import com.huawei.agconnect.config.AGConnectServicesConfig;
//import com.huawei.hms.aaid.HmsInstanceId;
//import com.huawei.hms.common.ApiException;
//import com.mob.pushsdk.MobPush;
//import com.mob.pushsdk.MobPushCallback;
//import com.mob.pushsdk.MobPushUtils;
//import com.pgyer.pgyersdk.PgyerSDKManager;
//import com.tencent.imsdk.v2.V2TIMCallback;
//import com.tencent.qcloud.tim.demo.bean.UserInfo;
//import com.tencent.qcloud.tim.demo.thirdpush.ThirdPushTokenMgr;
//import com.tencent.qcloud.tim.demo.utils.BrandUtil;
//import com.tencent.qcloud.tim.demo.utils.DemoLog;
//import com.tencent.qcloud.tim.demo.utils.TUIUtils;
//import com.tencent.qcloud.tuikit.tuicontact.ui.pages.TUIContactFragment;
//import com.tencent.qcloud.tuikit.tuiconversation.ui.page.TUIConversationFragment;
//import com.vivo.push.IPushActionListener;
//import com.vivo.push.PushClient;
//import com.yuntongxun.confwrap.WrapManager;
//import com.yuntongxun.ecsdk.ECDevice;
//import com.yuntongxun.ecsdk.ECError;
//import com.yuntongxun.ecsdk.SdkErrorCode;
//import com.yuntongxun.plugin.common.YTXSDKCoreHelper;
//import com.yuntongxun.plugin.common.ui.setting.YTXCustomSettingUtils;
//import com.yuntongxun.plugin.im.common.YTXCustomMsgItem;
//import com.yuntongxun.plugin.im.manager.YTXIMPluginManager;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import me.jessyan.autosize.AutoSizeCompat;
//
//public class ShouyeActivitybeifen2 extends SlbBase implements FshengjiView, FCate1View {
//
//    private FshengjiPresenter fshengjiPresenter;
//    private FCate1Presenter fCate1Presenter;
//    private String apkPath = "";
//    private int serverVersionCode = 0;
//    private String serverVersionName = "";
//    private String updateInfoTitle = "";
//    private String updateInfo = "";
//    private String md5 = "";
//    private String appPackageName = "";
//    private int current_pos = 0;
////    private String istag;
//
//    private MessageReceiverIndex mMessageReceiver;
//
//    public class MessageReceiverIndex extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            try {
//                if ("ShouyeActivity".equals(intent.getAction())) {
////                    String updatedata = intent.getStringExtra("updatedata");
////                    if (updatedata != null && !"".equals(updatedata)) {
////                        istag = updatedata;
////                    }
//                    //点击item
//                    if (intent.getIntExtra("id", -1) != -1) {
//                        current_pos = intent.getIntExtra("id", 0);
//
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
//            Toast.makeText(ShouyeActivitybeifen2.this, "我是宿主，收到插件消息", Toast.LENGTH_SHORT).show();
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
//                MyLogUtil.d("TAG", "onAsyncCreate: " + "系统会提示");
//            }
//        }
//    };
//
//    UpdateAppReceiver updateAppReceiver = new UpdateAppReceiver();
//
//    private void get_loc() {
//        //
//        LocUtil.getLocation(ShouyeActivitybeifen2.this, new LocListener() {
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
//                LocalBroadcastManagers.getInstance(ShouyeActivitybeifen2.this).sendBroadcast(msgIntent);
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
//    public Resources getResources() {
//        //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
//        if (Looper.myLooper() == Looper.getMainLooper()) {
//            AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
//            AutoSizeCompat.autoConvertDensity((super.getResources()), 667, false);//如果有自定义需求就用这个方法
//        }
//        return super.getResources();
//    }
//
//    @Override
//    protected void onResume() {
//        MultiLanguages.attach(getApplicationContext());
//        updateAppReceiver.setBr(this);
//        PgyerSDKManager.checkSoftwareUpdate(this);
//        Shortcut.getSingleInstance().addShortcutCallback(callback);
//        //initPop();
//        //
////        if (!LocationUtils2.isGpsEnabled()) {
////            LocationUtils2.openGpsSettings();
////        } else {
////            get_loc();
////        }
//        get_loc();
////        if (!SlbLoginUtil.get().isUserLogin()) {
////            mTabLayout_2.setCurrentTab(current_pos);
////        }
////        if (istag != null && !"".equals(istag)) {
////            if (fCate1Presenter != null) {
////                if (istag.equals("1")) {
////                    fCate1Presenter.getcate1list(AppCommonUtils.auth_url + "/navgation",
////                            "", "10", "1", "", AppCommonUtils.get_location_cityname(), "");
////                }
////            }
////        }
//        mUserInfo = UserInfo.getInstance();
//        if (mUserInfo != null && mUserInfo.isAutoLogin()) {
//            TUIUtils.login(mUserInfo.getUserId(), mUserInfo.getUserSig(), new V2TIMCallback() {
//                @Override
//                public void onError(final int code, final String desc) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            ToastUtil.toastLongMessage(getString(com.tencent.qcloud.tim.demo.R.string.failed_login_tip) + ", errCode = " + code + ", errInfo = " + desc);
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
//        // 容联云通讯重连bufen
//        WrapManager.getInstance().app_AutoConnect(new YTXSDKCoreHelper.OnConnectStateListener() {
//            @Override
//            public void onConnectSuccess() {
//
//            }
//
//            @Override
//            public void onConnectFailed(ECError error) {
//                //     用户被挤下线
//                if (error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
//
//                }
//                //      用户未登陆
//                if (error.errorCode == YTXSDKCoreHelper.SDK_UN_LOGIN) {
//
//                }
//            }
//        });
//        YTXCustomSettingUtils.getInstance().setChattingMessageBgDrawableReceive(R.drawable.bg_qipao);
//        YTXCustomSettingUtils.getInstance()
//                /**标题栏颜色*/
//                .setActionBarColor(R.color.ytx_color)
//                /**toolbar背景颜色*/
//                .setToolbarBackgroundColor(R.color.ytx_color)
//                .setChattingMessageBgDrawableReceive(R.drawable.bg_qipao2)
//                .setChattingMessageBgDrawableSend(R.drawable.bg_qipao)
//                /**一级标题颜色*/
//                .setToolbarTextColor(R.color.white)
//                /**二级标题颜色*/
//                .setSubToolbarTextColor(R.color.white);
//        YTXCustomSettingUtils.getInstance().setChattingTopRight(R.drawable.ytx_emoji_261d, new YTXCustomSettingUtils.OnChattingTopRightClickListener() {
//            @Override
//            public void onChattingTopRightClick() {
//                ToastUtils.showLong("点击了自定义消息");
//            }
//        });
//        YTXCustomMsgItem ytxCustomMsgItem = new YTXCustomMsgItem();
//        YTXCustomMsgItem.YTXCustomMsg msg = ytxCustomMsgItem.new YTXCustomMsg();
//        msg.setAvatarUrl("https://s2.51cto.com/wyfs02/M01/89/BA/wKioL1ga-u7QnnVnAAAfrCiGnBQ946_small.jpg");
//        msg.setTitle("灯塔-党建在线消息号");
//        msg.setContent("测试内容");
//        msg.setTime("2022/5/7");
//        ytxCustomMsgItem.setMsg(msg);//设置消息
//        ytxCustomMsgItem.setUnReadNum(102);//设置未读数
//        ytxCustomMsgItem.setVisible(true);//设置是否显示
//        ytxCustomMsgItem.setVisible(true);//设置是否显示
//        YTXIMPluginManager.getManager().updateCustomMsgItem(ytxCustomMsgItem);
//        YTXIMPluginManager.getManager().setYtxOnCustomClickListener(new YTXIMPluginManager.YTXOnCustomClickListener() {
//            @Override
//            public void onItemClick() {
//                ToastUtils.showLong("点击了自定义消息");
//            }
//        });
//        super.onResume();
////        // 调用
////        String appId = "wx321c4f08e435114a"; // 填应用AppId
////        IWXAPI api = WXAPIFactory.createWXAPI(ShouyeActivity.this, appId);
////        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
////        req.userName = "gh_d78b989212c5"; // 填小程序原始id   gh_d43f693ca31f
////        req.path = "https://cx.o2o.bailingjk.net/wechat/#/main/medicalIndex?publicNoCode=jksd_0011&type=1&patientId=&language=";                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
////        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
////        api.sendReq(req);
//        //
////        String url = "https://cx.o2o.bailingjk.net/wechat/#/main/medicalIndex?publicNoCode=jksd_0011&type=1&patientId=&language=";
////        HiosHelper.resolveAd(ShouyeActivity.this,ShouyeActivity.this,url);
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
//        if (fCate1Presenter != null) {
//            fCate1Presenter.onDestory();
//        }
//        LocalBroadcastManagers.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
//        super.onDestroy();
//    }
//
//    public TextView tv_theme1;
//    private static final String SAVED_CURRENT_ID = "currentIdxzbb1";
//    private boolean SAVED_CURRENT_ID2;
//    public static final List<String> PAGE_TAGS = new ArrayList<>();
//    private List<Class<? extends Fragment>> fragmentClasses = Arrays.asList(ShouyeF5.class,
//            TUIConversationFragment.class, ShouyeF6.class, TUIContactFragment.class, ShouyeF1.class); //自定义的Fragment，主要目的是在初始化时能够通过循环初始化，与重建时的恢复统一
//    private List<Fragment> fragments = new ArrayList<>();
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean(SAVED_CURRENT_ID, true);
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
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
//
//        //通道消息
////        ChannelMessage();
//        //
////        tv_theme1 = findViewById(R.id.tv_theme1);
////        tv_theme1.setVisibility(View.GONE);
////        tv_theme1.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                //
//////                if (tv_theme1.getText().toString().startsWith("自定义")) {
//////                    tv_theme1.setText("默认主题");
//////                    theme = R.style.AppThemeBaseAppcommon;
//////                } else if (tv_theme1.getText().toString().startsWith("默认")) {
//////                    tv_theme1.setText("自定义主题");
//////                    theme = R.style.AppThemeBaseAppcommon2;
//////                }
//////                recreate();
////                //
//////                String url = "dataability://cs.znclass.com/com.github.geekcodesteam.app.hs.act.slbapp.ShouyeCateAct1?query1={s}1&query2={s}本地应用&query3={s}5";
//////                String url = "dataability://com.github.geekcodesteam.app.hs.act.slbapp.ShouyeCateAct1{act}?query1={s}1&query2={s}本地应用&query3={s}5";
//////                HiosHelperNew.resolveAd(ShouyeActivity.this, ShouyeActivity.this, url);
////                startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.Dt10Act"));
////            }
////        });
////        if (theme == R.style.AppThemeBaseAppcommon) {
////            tv_theme1.setText("默认主题");
////        } else if (theme == R.style.AppThemeBaseAppcommon2) {
////            tv_theme1.setText("自定义主题");
////        }
//
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
//    private void ChannelMessage() {
//        Intent intent = getIntent();
//        Uri uri = intent.getData();
////        new PlayloadDelegate().playload(this, intent.getExtras());
//        if (intent != null) {
//            MyLogUtil.e("aaaMobPush linkone intent---", intent.toUri(Intent.URI_INTENT_SCHEME));
//            System.out.println("MobPush linkone intent---" + intent.toUri(Intent.URI_INTENT_SCHEME));
//        }
//        StringBuilder sb = new StringBuilder();
//        if (uri != null) {
//            sb.append(" scheme:" + uri.getScheme() + "\n");
//            sb.append(" host:" + uri.getHost() + "\n");
//            sb.append(" port:" + uri.getPort() + "\n");
//            sb.append(" query:" + uri.getQuery() + "\n");
//        }
//
//        //获取link界面传输的数据
//        JSONArray jsonArray = MobPushUtils.parseSchemePluginPushIntent(intent);
//        if (jsonArray != null) {
//            sb.append("\n" + "bundle toString :" + jsonArray.toString());
//        }
//        // 通过scheme跳转详情页面可选择此方式
//        JSONArray var = new JSONArray();
//        var = MobPushUtils.parseSchemePluginPushIntent(intent);
//        MyLogUtil.e("aaaMobPushsssssss", var.toString());
//        //跳转首页可选择此方式
//        JSONArray var2 = new JSONArray();
//        var2 = MobPushUtils.parseMainPluginPushIntent(intent);
//        MyLogUtil.e("aaaMobPush---获取link界面传输的数据--", sb.toString());
//        MyLogUtil.e("aaaMobPush---通过scheme跳转详情页面可选择此方式---", var.toString());
//        MyLogUtil.e("aaaMobPush---跳转首页可选择此方式---", var2.toString());
//        String pluginExtra = null;
//        try {
//            JSONArray arr = new JSONArray(var2.toString());
//            JSONObject temp = (JSONObject) arr.get(1);
//            pluginExtra = temp.getString("pluginExtra");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if (pluginExtra != null && !"".equals(pluginExtra)) {
//            if (pluginExtra.equals("公告推送")) {
//                MyLogUtil.e("aaaaaa", "公告推送: " + pluginExtra);
//                Intent intent0 = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.AdNaticeActivity");
//                intent0.putExtra("url1", "https://www.baidu.com/");
//                intent0.putExtra("content", "公告摘要描述公告摘要描述公告，摘要描述公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
//                startActivity(intent0);
//            } else if (pluginExtra.equals("推送通知")) {
//                MyLogUtil.e("aaaaaa", "推送通知: " + pluginExtra);
//                Intent intent1 = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.AdPushNaticeActivity");
//                intent1.putExtra("url1", "https://www.baidu.com/");
//                intent1.putExtra("content", "公告摘要描述公告摘要描述公告，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
//                startActivity(intent1);
//            } else if (pluginExtra.equals("业务推送")) {
//                MyLogUtil.e("aaaaaa", "业务推送: " + pluginExtra);
//                Intent intent2 = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.AdBusinessNaticeActivity");
//                intent2.putExtra("url1", "https://www.baidu.com/");
//                intent2.putExtra("content", "公告摘要描述公告摘要描述公告，摘要描述公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
//                startActivity(intent2);
//            }
//        }
//    }
//
//    private void prepareThirdPushToken() {
//        //
//        MobPush.getRegistrationId(new MobPushCallback<String>() {
//
//            @Override
//            public void onCallback(String rid) {
//                MyLogUtil.e("RegistrationId", "RegistrationId:" + rid);
//                SPUtils.getInstance().put("MOBID", rid);
//
//            }
//        });
//        MobPush.getDeviceToken(new MobPushCallback<String>() {
//            @Override
//            public void onCallback(String s) {
//                MyLogUtil.e("MobPush----getDeviceToken2--", s);
//                SPUtils.getInstance().put("MOBDeviceToken", s);
//                if (!TextUtils.isEmpty(s)) {
//                    ThirdPushTokenMgr.getInstance().setThirdPushToken(s);
//                    ECDevice.reportHuaWeiToken(s);
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
//                        String appId = AGConnectServicesConfig.fromContext(ShouyeActivitybeifen2.this).getString("client/app_id");
//                        String token = HmsInstanceId.getInstance(ShouyeActivitybeifen2.this).getToken(appId, "HCM");
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
//        } /*else if (HeytapPushManager.isSupportPush()) {
//            // oppo离线推送
//            OPPOPushImpl oppo = new OPPOPushImpl();
//            oppo.createNotificationChannel(this);
//            // oppo接入文档要求，应用必须要调用init(...)接口，才能执行后续操作。
//            HeytapPushManager.init(this, false);
//            HeytapPushManager.register(this, PrivateConstants.OPPO_PUSH_APPKEY, PrivateConstants.OPPO_PUSH_APPSECRET, oppo);
//        } *//*else if (BrandUtil.isGoogleServiceSupport()) {
//            // 谷歌推送
//        }*/
//
//
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_shouye;
//    }
//
//    @Override
//    protected void setup(@Nullable Bundle savedInstanceState) {
//        super.setup(savedInstanceState);
////        if (savedInstanceState != null) {
////            boolean cachedId = savedInstanceState.getBoolean(SAVED_CURRENT_ID, false);
////            if (cachedId) {
////                return;
////            }
////        }
//        findview();
////        onclick();
//        doNetWork();
//    }
//
//    private void doNetWork() {
//        fshengjiPresenter = new FshengjiPresenter();
//        fshengjiPresenter.onCreate(this);
//        fCate1Presenter = new FCate1Presenter();
//        fCate1Presenter.onCreate(this);
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
//        if (fCate1Presenter != null) {
//            fCate1Presenter.getcate1list(AppCommonUtils.auth_url + "/navgation",
//                    "", "10", "1", "", AppCommonUtils.get_location_cityname(), "");
//        }
//    }
//
//    private void findview() {
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
////            exit();
//            Intent msgIntent = new Intent();
//            msgIntent.setAction("ShouyeFragment");
//            msgIntent.putExtra("query1", "0");
//            LocalBroadcastManagers.getInstance(ShouyeActivitybeifen2.this).sendBroadcast(msgIntent);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
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
////    private BasePopupView loadingPopup = null;
//
////    //隐私政策
////    private void initPop() {
////        if (!MmkvUtils.getInstance().get_xiancheng_bool(CommonUtils.MMKV_forceLogin)) {
////            if (loadingPopup != null) {
////                if (!loadingPopup.isShow()) {
////                    loadingPopup.show();
////                }
////                return;
////            }
////            loadingPopup = new XPopup.Builder(this)
////                    .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
//////              .isViewMode(true)
////                    .dismissOnTouchOutside(false)
////                    .asCustom(new CenterPopupView(this) {
////                        private TextView tvContent;
////                        private CheckBox radAgreement;
////                        private TextView btnClose;
////                        private TextView btnOk;
////
////                        @Override
////                        protected int getImplLayoutId() {
////                            return R.layout.dialog_agreement1;
////                        }
////
////                        @Override
////                        protected void onCreate() {
////                            super.onCreate();
////                            tvContent = findViewById(R.id.tv_content);
////                            radAgreement = findViewById(R.id.rad_agreement);
////                            btnClose = findViewById(R.id.btn_cancle);
////                            btnOk = findViewById(R.id.btn_ok);
////                            FinitBean fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
////                            tvContent.setText(SpannableStringUtils.getInstance(ShouyeActivity.this)
////                                    .getBuilder("欢迎使用移动端平台！灯塔非常重视您的隐私保护和个人信息保护。在您使用移动端平台前，请认真阅读")
////                                    .append("《用户须知》")
////                                    .setClickSpan(new ClickableSpan() {
////                                        @Override
////                                        public void onClick(View widget) {
////                                            HiosHelperNew.resolveAd(ShouyeActivity.this, ShouyeActivity.this, fconfigBean.getUser());
////                                        }
////
////                                        @Override
////                                        public void updateDrawState(TextPaint ds) {
////                                            ds.setColor(ContextCompat.getColor(BaseApp.get(), R.color.red));
////                                            ds.setUnderlineText(false);
////                                        }
////                                    })
////                                    .append("及")
////                                    .append("《隐私权政策》")
////                                    .setClickSpan(new ClickableSpan() {
////                                        @Override
////                                        public void onClick(View widget) {
//////                                        Uri url = Uri.parse("http://blog.51cto.com/liangxiao");
//////                                        Intent intent = new Intent(Intent.ACTION_VIEW);
//////                                        intent.setData(url);
//////                                        startActivity(intent);
//////                                        HiosHelper.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, MmkvUtils.getInstance().get_common(CommonUtils.MMKV_privacyPolicy));
////                                            HiosHelperNew.resolveAd(ShouyeActivity.this, ShouyeActivity.this, fconfigBean.getPrivacy());
////                                        }
////
////                                        @Override
////                                        public void updateDrawState(TextPaint ds) {
////                                            ds.setColor(ContextCompat.getColor(ShouyeActivity.this, R.color.red));
////                                            ds.setUnderlineText(false);
////                                        }
////                                    })
////                                    .append("，您同意并接受全部条款后方可使用。")
////                                    .create());
////                            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
////                            BounceView.addAnimTo(btnOk);
////                            BounceView.addAnimTo(btnClose);
////                            btnOk.setOnClickListener(new OnClickListener() {
////                                @Override
////                                public void onClick(View v) {
////                                    if (!radAgreement.isChecked()) {
////                                        com.hjq.toast.ToastUtils.show("请勾选用户须知及隐私权政策");
////                                        return;
////                                    }
////                                    dismiss();
////                                    MmkvUtils.getInstance().set_xiancheng(CommonUtils.MMKV_forceLogin, true);
////                                }
////                            });
////                            btnClose.setOnClickListener(new OnClickListener() {
////                                @Override
////                                public void onClick(View v) {
////                                    dismiss();
////                                    ActivityUtils.finishAllActivities();
////                                    finish();
////                                }
////                            });
////                        }
////                        //        @Override
////                        //        protected int getMaxHeight() {
////                        //            return 200;
////                        //        }
////                        //
////                        //返回0表示让宽度撑满window，或者你可以返回一个任意宽度
////                        //        @Override
////                        //        protected int getMaxWidth() {
////                        //            return 1200;
////                        //        }
////                    });
////            if (!loadingPopup.isShow()) {
////                loadingPopup.show();
////            }
////        }
////    }
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
//        UpdateAppUtils.from(ShouyeActivitybeifen2.this)
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
//    @Override
//    public void OnCate1Success(String authorizedType, BjyyBeanYewu4 bean) {
//        // 测试
////        List<ShouyeFragmentBean> mNavigationList = new ArrayList<>();
////        List<BjyyBeanYewu3> mNavigationList = new ArrayList<>();
//////        mNavigationList.add(new BjyyBeanYewu3("11", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "首页", ShouyeFragmentFactory.TAG_shouye, true));
//////        mNavigationList.add(new BjyyBeanYewu3("33", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "联系人", ShouyeFragmentFactory.TAG_people, false));
//////        mNavigationList.add(new BjyyBeanYewu3("55", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "我的", ShouyeFragmentFactory.TAG_my, false));
//////        mNavigationList.add(new BjyyBeanYewu3("66", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "框架1", ShouyeFragmentFactory.TAG_kuangjia1, false));
//////        mNavigationList.add(new BjyyBeanYewu3("77", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "框架2", ShouyeFragmentFactory.TAG_kuangjia2, false));
////        mNavigationList.add(new BjyyBeanYewu3("88", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "资讯", ShouyeFragmentFactory.TAG_zixun, false));
////        mNavigationList.add(new BjyyBeanYewu3("99", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "数字乡村", ShouyeFragmentFactory.TAG_shuzixiangcun, false));
////        mNavigationList.add(new BjyyBeanYewu3("1010", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "党建引领", ShouyeFragmentFactory.TAG_dangjianyinling, false));
////        mNavigationList.add(new BjyyBeanYewu3("22", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "消息", ShouyeFragmentFactory.TAG_xiaoxi, false));
////        mNavigationList.add(new BjyyBeanYewu3("44", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "会议", ShouyeFragmentFactory.TAG_huiyi, false));
////        istag = "";//刷新完成，更新状态清空
//        AppCommonUtils.clearFragments(this);
//        Fragment mFragment = null;
//        Bundle args = new Bundle();
//        args.putSerializable("tablayoutId", (Serializable) bean.getData());
////        args.putSerializable("tablayoutId", (Serializable) mNavigationList);
//        this.getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.container_framelayout, mFragment = ShouyeFragment.getInstance(args), ShouyeFragment.class.getName())
//                .show(mFragment)
//                .commitAllowingStateLoss();
//        //通道消息
//        ChannelMessage();
//    }
//
//
//    @Override
//    public void OnCate1Nodata(String bean) {
//
//    }
//
//    @Override
//    public void OnCate1Fail(String msg) {
//
//    }
//
//
//}
