package com.geek.appindex;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.SlbBase;
import com.geek.appindex.demo.factorys.HomePageActFragmentFactory;
import com.geek.biz1.bean.FshengjiBean;
import com.geek.biz1.bean.UserInfoBean;
import com.geek.biz1.presenter.FshengjiPresenter;
import com.geek.biz1.view.FshengjiView;
import com.geek.libbase.base.SlbBaseFragment;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libutils.app.FragmentHelper;
import com.geek.libutils.data.MmkvUtils;
import com.geek.libutils.jiami.Md5Utils;
import com.github.commonlibs.libupdateapputilsold.util.UpdateAppUtils;

import me.jessyan.autosize.AutoSizeCompat;

/**
 * @author lhw
 * 小屏端首页
 */
public class HomePageAct extends SlbBase implements OnClickListener, FshengjiView {

    private FshengjiPresenter fshengjiPresenter;
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
            AutoSizeCompat.autoConvertDensity((super.getResources()), 540, false);//如果有自定义需求就用这个方法
        }
        return super.getResources();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mk_homepageact;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        String userJson = "{\n" +
                "        \"id\":\"3533917618656262353\",\n" +
                "        \"name\":\"sunxxxxxx\",\n" +
                "        \"tid\":\"222222222222222223\",\n" +
                "        \"sex\":\"1\",\n" +
                "        \"orgId\":\"3533903102841203901\",\n" +
                "        \"orgCode\":\"000003000012000001\",\n" +
                "        \"orgName\":\"测试1\",\n" +
                "        \"userId\":\"3533917618656262157\",\n" +
                "        \"telephone\":\"15512341111\",\n" +
                "        \"logo\":null,\n" +
                "        \"idCard\":\"370101199408306211\",\n" +
                "        \"idCardHash\":\"9dguldFfzDRmtiy6lk5JZmionuU=\",\n" +
                "        \"currentRole\":null,\n" +
                "        \"roles\":[\n" +
                "            {\n" +
                "                \"roleId\":\"1\",\n" +
                "                \"roleName\":\"管理员\",\n" +
                "                \"roleDescription\":\"管理员角色\",\n" +
                "                \"orgType\":\"DW\",\n" +
                "                \"manageId\":\"1\",\n" +
                "                \"manageName\":\"测试的党组织\",\n" +
                "                \"manageCode\":\"000003000012\",\n" +
                "                \"clientId\":null\n" +
                "            },\n" +
                "            {\n" +
                "                \"roleId\":\"1\",\n" +
                "                \"roleName\":\"管理员\",\n" +
                "                \"roleDescription\":\"管理员角色\",\n" +
                "                \"orgType\":\"1\",\n" +
                "                \"manageId\":\"3533902711999179965\",\n" +
                "                \"manageName\":\"测试添加党组织\",\n" +
                "                \"manageCode\":\"000003000002\",\n" +
                "                \"clientId\":null\n" +
                "            },\n" +
                "            {\n" +
                "                \"roleId\":\"2\",\n" +
                "                \"roleName\":\"党员\",\n" +
                "                \"roleDescription\":\"党员角色\",\n" +
                "                \"orgType\":\"1\",\n" +
                "                \"manageId\":\"3533903102841203901\",\n" +
                "                \"manageName\":\"测试1\",\n" +
                "                \"manageCode\":\"000003000012000001\",\n" +
                "                \"clientId\":null\n" +
                "            }\n" +
                "        ]\n" +
                "    }";
        UserInfoBean bean = GsonUtils.fromJson(userJson, UserInfoBean.class);
        MmkvUtils.getInstance().set_common_json2(AppCommonUtils.userInfo, bean);
        SPUtils.getInstance().put("tid", bean.tid);
        fshengjiPresenter = new FshengjiPresenter();
        fshengjiPresenter.onCreate(this);

        findview();
        onclickListener();
        doNetWork();
    }

    private void findview() {
        setupFragments();
    }

    private void onclickListener() {

    }

    /**
     * 网络请求
     */
    private void doNetWork() {
        apkPath = "";
        updateInfoTitle = "";
        updateInfo = "";
        serverVersionCode = AppUtils.getAppVersionCode();
        serverVersionName = AppUtils.getAppVersionName();
        appPackageName = AppUtils.getAppPackageName();
        md5 = Md5Utils.get32Md5LowerCase(appPackageName);
        fshengjiPresenter.getshengji(AppCommonUtils.auth_url, serverVersionCode + "", serverVersionName, appPackageName, md5);
    }

    /**
     * 初始化首页fragments
     */
    private void setupFragments() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SparseArrayCompat<Class<? extends SlbBaseFragment>> array = HomePageActFragmentFactory.get();
        int size = array.size();
        SlbBaseFragment item;
        for (int i = 0; i < size; i++) {
            item = FragmentHelper.newFragment(array.valueAt(i), null);
            ft.replace(array.keyAt(i), item, item.getClass().getName());
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {

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

        UpdateAppUtils.from(this)
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
//              .showNotification(true)
//              .needFitAndroidN(true)
                .update();
    }

    @Override
    public void OnFshengjiNodata(String bean) {

    }

    @Override
    public void OnFshengjiFail(String msg) {

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
            if (fragment != null && fragment instanceof SlbBaseLazyFragmentNew) {
                ((SlbBaseLazyFragmentNew) fragment).call(value);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fshengjiPresenter != null) {
            fshengjiPresenter.onDestory();
        }
    }
}
