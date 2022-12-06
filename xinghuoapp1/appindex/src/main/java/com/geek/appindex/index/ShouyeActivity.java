package com.geek.appindex.index;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.SlbBase;
import com.geek.appindex.R;
import com.geek.appindex.index.fragment.ShouyeF1;
import com.geek.appindex.index.fragment.ShouyeF5;
import com.geek.appindex.index.fragment.ShouyeF6;
import com.geek.biz1.bean.BjyyBeanYewu4;
import com.geek.biz1.bean.FshengjiBean;
import com.geek.biz1.presenter.FCate1Presenter;
import com.geek.biz1.presenter.FshengjiPresenter;
import com.geek.biz1.view.FCate1View;
import com.geek.biz1.view.FshengjiView;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libbase.plugins.PluginManager;
import com.geek.liblocations.LocListener;
import com.geek.liblocations.LocUtil;
import com.geek.liblocations.LocationBean;
import com.geek.libupdateapp.util.UpdateAppReceiver;
import com.geek.libupdateapp.util.UpdateAppUtils;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.MmkvUtils;
import com.geek.libutils.jiami.Md5Utils;
import com.geek.libutils.shortcut.ShortcutUtils;
import com.geek.libutils.shortcut.core.Shortcut;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.mob.pushsdk.MobPush;
import com.mob.pushsdk.MobPushCallback;
import com.mob.pushsdk.MobPushUtils;
import com.pgyer.pgyersdk.PgyerSDKManager;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.jessyan.autosize.AutoSizeCompat;

public class ShouyeActivity extends SlbBase implements FshengjiView, FCate1View {

    private FshengjiPresenter fshengjiPresenter;
    private FCate1Presenter fCate1Presenter;
    private String apkPath = "";
    private int serverVersionCode = 0;
    private String serverVersionName = "";
    private String updateInfoTitle = "";
    private String updateInfo = "";
    private String md5 = "";
    private String appPackageName = "";

    @Override
    public Resources getResources() {
        //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
        if (Looper.myLooper() == Looper.getMainLooper()) {
            AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
            AutoSizeCompat.autoConvertDensity((super.getResources()), 667, false);//如果有自定义需求就用这个方法
        }
        return super.getResources();
    }

    private final Shortcut.Callback callback = new Shortcut.Callback() {
        @Override
        public void onAsyncCreate(String id, String label) {
            ShortcutUtils.dismissTryTipDialog();
            if (!"huawei".equalsIgnoreCase(Build.MANUFACTURER) && !"samsung".equalsIgnoreCase(Build.MANUFACTURER)) {
                MyLogUtil.e("创建成功，id = " + id + ", label = " + label);
            } else {
                MyLogUtil.d("TAG", "onAsyncCreate: " + "系统会提示");
            }
        }
    };

    private UpdateAppReceiver updateAppReceiver = new UpdateAppReceiver();

    private void get_loc() {
        //
        LocUtil.getLocation(ShouyeActivity.this, new LocListener() {

            @Override
            public void success(LocationBean model) {
                String lastLatitude = String.valueOf(model.getLatitude());
                String lastLongitude = String.valueOf(model.getLongitude());
                String aaaa = lastLatitude + "," + lastLongitude;
                MmkvUtils.getInstance().set_common_json2("location", model);
                // shuaxin
                Intent msgIntent = new Intent();
                msgIntent.setAction("gongzuotai");
                msgIntent.putExtra("dingwei", model.getCity());
                LocalBroadcastManagers.getInstance(ShouyeActivity.this).sendBroadcast(msgIntent);
            }

            @Override
            public void fail(int msg) {
                String aaa = msg + "";
//                ToastUtils.showLong(aaa);
            }
        });
    }


    @Override
    protected void onResume() {
        updateAppReceiver.setBr(this);
        PgyerSDKManager.checkSoftwareUpdate(this);
        Shortcut.getSingleInstance().addShortcutCallback(callback);
        get_loc();
        super.onResume();
//        // 调用
//        String appId = "wx321c4f08e435114a"; // 填应用AppId
//        IWXAPI api = WXAPIFactory.createWXAPI(ShouyeActivity.this, appId);
//        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
//        req.userName = "gh_d78b989212c5"; // 填小程序原始id   gh_d43f693ca31f
//        req.path = "https://cx.o2o.bailingjk.net/wechat/#/main/medicalIndex?publicNoCode=jksd_0011&type=1&patientId=&language=";                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
//        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
//        api.sendReq(req);
        //
//        String url = "https://cx.o2o.bailingjk.net/wechat/#/main/medicalIndex?publicNoCode=jksd_0011&type=1&patientId=&language=";
//        HiosHelperNew.resolveAd(ShouyeActivity.this,ShouyeActivity.this,url);
    }


    @Override
    protected void onDestroy() {
        updateAppReceiver.desBr(this);
        Shortcut.getSingleInstance().removeShortcutCallback(callback);
        ShortcutUtils.dismissPermissionTipDialog();
        ShortcutUtils.dismissTryTipDialog();
        if (fshengjiPresenter != null) {
            fshengjiPresenter.onDestory();
        }
        if (fCate1Presenter != null) {
            fCate1Presenter.onDestory();
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        prepareThirdPushToken();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MyLogUtil.e("TencentIM", "onNewIntent");
        setIntent(intent);
        prepareThirdPushToken();
    }

    private void ChannelMessage() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
//        new PlayloadDelegate().playload(this, intent.getExtras());
        if (intent != null) {
            MyLogUtil.e("aaaMobPush linkone intent---", intent.toUri(Intent.URI_INTENT_SCHEME));
            System.out.println("MobPush linkone intent---" + intent.toUri(Intent.URI_INTENT_SCHEME));
        }
        StringBuilder sb = new StringBuilder();
        if (uri != null) {
            sb.append(" scheme:" + uri.getScheme() + "\n");
            sb.append(" host:" + uri.getHost() + "\n");
            sb.append(" port:" + uri.getPort() + "\n");
            sb.append(" query:" + uri.getQuery() + "\n");
        }

        //获取link界面传输的数据
        JSONArray jsonArray = MobPushUtils.parseSchemePluginPushIntent(intent);
        if (jsonArray != null) {
            sb.append("\n" + "bundle toString :" + jsonArray.toString());
        }
        // 通过scheme跳转详情页面可选择此方式
        JSONArray var = new JSONArray();
        var = MobPushUtils.parseSchemePluginPushIntent(intent);
        MyLogUtil.e("aaaMobPushsssssss", var.toString());
        //跳转首页可选择此方式
        JSONArray var2 = new JSONArray();
        var2 = MobPushUtils.parseMainPluginPushIntent(intent);
        MyLogUtil.e("aaaMobPush---获取link界面传输的数据--", sb.toString());
        MyLogUtil.e("aaaMobPush---通过scheme跳转详情页面可选择此方式---", var.toString());
        MyLogUtil.e("aaaMobPush---跳转首页可选择此方式---", var2.toString());
        String pluginExtra = null;
        try {
            JSONArray arr = new JSONArray(var2.toString());
            JSONObject temp = (JSONObject) arr.get(1);
            pluginExtra = temp.getString("pluginExtra");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (pluginExtra != null && !"".equals(pluginExtra)) {
            if (pluginExtra.equals("公告推送")) {
                MyLogUtil.e("aaaaaa", "公告推送: " + pluginExtra);
                Intent intent0 = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.slbapp.AdNaticeActivity");
                intent0.putExtra("url1", "https://www.baidu.com/");
                intent0.putExtra("content", "公告摘要描述公告摘要描述公告，摘要描述公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
                startActivity(intent0);
            } else if (pluginExtra.equals("推送通知")) {
                MyLogUtil.e("aaaaaa", "推送通知: " + pluginExtra);
                Intent intent1 = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.slbapp.AdPushNaticeActivity");
                intent1.putExtra("url1", "https://www.baidu.com/");
                intent1.putExtra("content", "公告摘要描述公告摘要描述公告，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
                startActivity(intent1);
            } else if (pluginExtra.equals("业务推送")) {
                MyLogUtil.e("aaaaaa", "业务推送: " + pluginExtra);
                Intent intent2 = new Intent(com.blankj.utilcode.util.AppUtils.getAppPackageName() + ".hs.act.slbapp.AdBusinessNaticeActivity");
                intent2.putExtra("url1", "https://www.baidu.com/");
                intent2.putExtra("content", "公告摘要描述公告摘要描述公告，摘要描述公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
                startActivity(intent2);
            }
        }
    }

    private void prepareThirdPushToken() {
        //
        MobPush.getRegistrationId(new MobPushCallback<String>() {

            @Override
            public void onCallback(String rid) {
                MyLogUtil.e("RegistrationId", "RegistrationId:" + rid);
                SPUtils.getInstance().put("MOBID", rid);

            }
        });
        MobPush.getDeviceToken(new MobPushCallback<String>() {
            @Override
            public void onCallback(String s) {
                MyLogUtil.e("MobPush----getDeviceToken2--", s);
                SPUtils.getInstance().put("MOBDeviceToken", s);
                if (!TextUtils.isEmpty(s)) {
//                    ThirdPushTokenMgr.getInstance().setThirdPushToken(s);
//                    ECDevice.reportHuaWeiToken(s);
                }
            }
        });
        //
       /* ThirdPushTokenMgr.getInstance().setPushTokenToTIM();

        if (BrandUtil.isBrandHuawei()) {
            // 华为离线推送
            new Thread() {
                @Override
                public void run() {
                    try {
                        // read from agconnect-services.json
                        String appId = AGConnectServicesConfig.fromContext(ShouyeActivity.this).getString("client/app_id");
                        String token = HmsInstanceId.getInstance(ShouyeActivity.this).getToken(appId, "HCM");
                        DemoLog.e("TencentIM", "huawei get token:" + token);
                        if (!TextUtils.isEmpty(token)) {
                            ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
                            ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
                        }
                    } catch (ApiException e) {
                        DemoLog.e("TAG", "huawei get token failed, " + e);
                    }
                }
            }.start();
        } else if (BrandUtil.isBrandVivo()) {
            // vivo离线推送
            DemoLog.e("TencentIM", "vivo support push: " + PushClient.getInstance(getApplicationContext()).isSupport());
            PushClient.getInstance(getApplicationContext()).turnOnPush(new IPushActionListener() {
                @Override
                public void onStateChanged(int state) {
                    if (state == 0) {
                        String regId = PushClient.getInstance(getApplicationContext()).getRegId();
                        DemoLog.e("TencentIM", "vivopush open vivo push success regId = " + regId);
                        ThirdPushTokenMgr.getInstance().setThirdPushToken(regId);
                        ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
                    } else {
                        // 根据vivo推送文档说明，state = 101 表示该vivo机型或者版本不支持vivo推送，链接：https://dev.vivo.com.cn/documentCenter/doc/156
                        DemoLog.e("TencentIM", "vivopush open vivo push fail state = " + state);
                    }
                }
            });
        }*/ /*else if (HeytapPushManager.isSupportPush()) {
            // oppo离线推送
            OPPOPushImpl oppo = new OPPOPushImpl();
            oppo.createNotificationChannel(this);
            // oppo接入文档要求，应用必须要调用init(...)接口，才能执行后续操作。
            HeytapPushManager.init(this, false);
            HeytapPushManager.register(this, PrivateConstants.OPPO_PUSH_APPKEY, PrivateConstants.OPPO_PUSH_APPSECRET, oppo);
        } *//*else if (BrandUtil.isGoogleServiceSupport()) {
            // 谷歌推送
        }*/


    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shouye;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
//        findview();
//        onclick();
        doNetWork();
    }

    private void doNetWork() {
        fshengjiPresenter = new FshengjiPresenter();
        fshengjiPresenter.onCreate(this);
        fCate1Presenter = new FCate1Presenter();
        fCate1Presenter.onCreate(this);
        apkPath = "";
        updateInfoTitle = "";
        updateInfo = "";
        serverVersionCode = AppUtils.getAppVersionCode();
        serverVersionName = AppUtils.getAppVersionName();
        appPackageName = AppUtils.getAppPackageName();
        md5 = Md5Utils.get32Md5LowerCase(appPackageName);
        MyLogUtil.e("ssssssssssssss", md5);
        fshengjiPresenter.getshengji(AppCommonUtils.auth_url,
                serverVersionCode + "", serverVersionName, appPackageName, md5);
        if (fCate1Presenter != null) {
            fCate1Presenter.getcate1list(AppCommonUtils.auth_url + "/navgation",
                    "", "10", "1", "", AppCommonUtils.get_location_cityname(), "");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            exit();
            Intent msgIntent = new Intent();
            msgIntent.setAction("ShouyeFragment");
            msgIntent.putExtra("query1", "0");
            LocalBroadcastManagers.getInstance(ShouyeActivity.this).sendBroadcast(msgIntent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    @Override
    public void OnFshengjiSuccess(FshengjiBean bean) {
        apkPath = bean.getApkPath();
        serverVersionCode = Integer.valueOf(bean.getServerVersionCode());
        serverVersionName = bean.getServerVersionName();
        updateInfoTitle = bean.getUpdateInfoTitle();
        updateInfo = bean.getUpdateInfo();
        if (TextUtils.isEmpty(apkPath)) {
            return;
        }
        UpdateAppUtils.from(ShouyeActivity.this)
                .serverVersionCode(serverVersionCode)
                .serverVersionName(serverVersionName)
                .downloadPath("apks/" + getPackageName() + ".apk")
                .showProgress(true)
                .isForce(bean.getIsForce())
                .apkPath(apkPath)
                .downloadBy(UpdateAppUtils.DOWNLOAD_BY_APP)    //default
                .checkBy(UpdateAppUtils.CHECK_BY_VERSION_CODE) //default
                .updateInfoTitle(updateInfoTitle)
                .updateInfo(updateInfo.replace("|", "\n"))
//                    .showNotification(true)
//                    .needFitAndroidN(true)
                .update();
    }

    @Override
    public void OnFshengjiNodata(String msg) {
//        ToastUtils.showLong(msg);
    }

    @Override
    public void OnFshengjiFail(String msg) {
//        ToastUtils.showLong(msg);
    }

    @Override
    public void OnCate1Success(String authorizedType, BjyyBeanYewu4 bean) {
        // 测试
//        List<ShouyeFragmentBean> mNavigationList = new ArrayList<>();
//        List<BjyyBeanYewu3> mNavigationList = new ArrayList<>();
////        mNavigationList.add(new BjyyBeanYewu3("11", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "首页", ShouyeFragmentFactory.TAG_shouye, true));
////        mNavigationList.add(new BjyyBeanYewu3("33", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "联系人", ShouyeFragmentFactory.TAG_people, false));
////        mNavigationList.add(new BjyyBeanYewu3("55", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "我的", ShouyeFragmentFactory.TAG_my, false));
////        mNavigationList.add(new BjyyBeanYewu3("66", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "框架1", ShouyeFragmentFactory.TAG_kuangjia1, false));
////        mNavigationList.add(new BjyyBeanYewu3("77", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "框架2", ShouyeFragmentFactory.TAG_kuangjia2, false));
//        mNavigationList.add(new BjyyBeanYewu3("88", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "资讯", ShouyeFragmentFactory.TAG_zixun, false));
//        mNavigationList.add(new BjyyBeanYewu3("99", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "数字乡村", ShouyeFragmentFactory.TAG_shuzixiangcun, false));
//        mNavigationList.add(new BjyyBeanYewu3("1010", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "党建引领", ShouyeFragmentFactory.TAG_dangjianyinling, false));
//        mNavigationList.add(new BjyyBeanYewu3("22", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "消息", ShouyeFragmentFactory.TAG_xiaoxi, false));
//        mNavigationList.add(new BjyyBeanYewu3("44", "", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515304962944931528.png", "http://119.188.115.252:8090/resource-handle/uploads/image/2021-11-15/3515303549380602766.png", "会议", ShouyeFragmentFactory.TAG_huiyi, false));
//        istag = "";//刷新完成，更新状态清空
        AppCommonUtils.clearFragments(this);
        Fragment mFragment = null;
        Bundle args = new Bundle();
        args.putSerializable("tablayoutId", (Serializable) bean.getData());
//        args.putSerializable("tablayoutId", (Serializable) mNavigationList);
        this.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_framelayout, mFragment = ShouyeFragment.getInstance(args), ShouyeFragment.class.getName())
                .show(mFragment)
                .commitAllowingStateLoss();
        //通道消息
        ChannelMessage();
    }


    @Override
    public void OnCate1Nodata(String bean) {

    }

    @Override
    public void OnCate1Fail(String msg) {

    }


}
