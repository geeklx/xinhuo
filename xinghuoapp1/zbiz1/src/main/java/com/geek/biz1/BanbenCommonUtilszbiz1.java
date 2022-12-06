package com.geek.biz1;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.SPUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;

import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import tuoyan.com.xinghuo_daying.UrlManager;

public class BanbenCommonUtilszbiz1 {

    public static String banben_comm = BuildConfigyewu.versionNameConfig;
    public static String dizhi1_comm = BuildConfigyewu.SERVER_ISERVICE_NEW1;

    public void test() {
        //版本判断
        if (TextUtils.equals(BuildConfigyewu.versionNameConfig, "测试")) {

        } else if (TextUtils.equals(BuildConfigyewu.versionNameConfig, "预生产")) {

        } else if (TextUtils.equals(BuildConfigyewu.versionNameConfig, "线上")) {

        }
    }


    public static StartHiddenManager startHiddenManager;

    //
    public static void changeUrl(final Activity activity, View left, View right, String intent) {
        startHiddenManager = new StartHiddenManager(left, right, intent, new StartHiddenManager.OnClickFinish() {
            @Override
            public void onFinish() {
                new XPopup.Builder(activity)
                        //.dismissOnBackPressed(false)
                        .dismissOnTouchOutside(true) //对于只使用一次的弹窗，推荐设置这个
                        .autoOpenSoftInput(true)
//                        .autoFocusEditText(false) //是否让弹窗内的EditText自动获取焦点，默认是true
//                        .isRequestFocus(false)
                        //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
                        .asInputConfirm("修改地址", SPUtils.getInstance().getString(UrlManager.default_url1key, dizhi1_comm), null, "",
                                new OnInputConfirmListener() {
                                    @Override
                                    public void onConfirm(String text) {
//                                        toast("input text: " + text);
                                        if (text.contains("http")) {
                                            String[] content = text.split(",");
                                            SPUtils.getInstance().put(UrlManager.default_url1key, content[0]);
                                            BuildConfigyewu.SERVER_ISERVICE_NEW1 = content[0];
                                            BanbenCommonUtilszbiz1.dizhi1_comm = content[0];
                                        }
//                                new XPopup.Builder(getContext()).asLoading().show();
                                    }
                                })
                        .show();
            }
        });
    }

    public static void changeUrl_ondes() {
        startHiddenManager.onDestory();
    }


    public static void changeUrl2(Activity activity) {
        new XPopup.Builder(activity)
                //.dismissOnBackPressed(false)
                .dismissOnTouchOutside(true) //对于只使用一次的弹窗，推荐设置这个
                .autoOpenSoftInput(true)
//                        .autoFocusEditText(false) //是否让弹窗内的EditText自动获取焦点，默认是true
                .isRequestFocus(false)
                //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
                .asInputConfirm("修改地址", "地址格式为：" + dizhi1_comm, null, "",
                        new OnInputConfirmListener() {
                            @Override
                            public void onConfirm(String text) {
//                                        toast("input text: " + text);
                                if (text.contains("http")) {
//                                            String[] content = text.split(",");
                                    SPUtils.getInstance().put("版本地址1", text);
                                    BuildConfigyewu.SERVER_ISERVICE_NEW1 = text;
                                    BanbenCommonUtilszbiz1.dizhi1_comm = text;

                                }
//                                new XPopup.Builder(getContext()).asLoading().show();
                            }
                        })
                .show();
    }
}
