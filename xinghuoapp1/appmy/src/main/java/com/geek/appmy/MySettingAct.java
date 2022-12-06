package com.geek.appmy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.geek.libbase.base.SlbBaseActivity;
import com.geek.liblanguage.MultiLanguages;
import com.geek.libswipebacklayout.SwipeBack;
import com.geek.libutils.SlbLoginUtil;
import com.geek.libutils.app.BaseApp;
import com.geek.libutils.data.MmkvUtils;
import com.haier.cellarette.baselibrary.cacheutil.CacheUtil;
import com.haier.cellarette.baselibrary.switchbutton.SwitchButtonK;
import com.just.agentweb.geek.hois3.HiosHelperNew;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.XPopupUtils;

import java.util.Locale;

//@SwipeBack(value = true)
public class MySettingAct extends SlbBaseActivity {

    private TextView tv_left;
    private TextView tv_center;
    private TextView tv_jtzw1;
    private TextView tv_qchc1;
    private RelativeLayout rl1;
    private RelativeLayout rl2;
    private RelativeLayout rl3;
    private RelativeLayout rl4;
    private RelativeLayout rl5;
    private RelativeLayout rl7;
    private RelativeLayout rl8;
    private SwitchButtonK sb_ios;
    private static final String TAG = MySettingAct.class.getName();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mysetting;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
//        BarUtils.setStatusBarLightMode(this, true);
        super.setup(savedInstanceState);
        findview();
        onclick();
        donetwork();
    }


    private void donetwork() {
        showlogin();
        tv_center.setText(getApplication().getResources().getString(R.string.appmy1));
        tv_jtzw1.setText(getApplication().getResources().getString(R.string.current_language));
        try {
            tv_qchc1.setText(CacheUtil.getTotalCacheSize(BaseApp.get()));
        } catch (Exception e) {
            tv_qchc1.setText("");
        }
        sb_ios.setChecked(Boolean.parseBoolean(MmkvUtils.getInstance().get_common("xinxiaoxitongzhi", "true")));
    }

    private void onclick() {
        tv_left.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                onBackPressed();
            }
        });
        rl1.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                // 安全中心
                SlbLoginUtil.get().loginTowhere(MySettingAct.this, new Runnable() {
                    @Override
                    public void run() {
                        showlogin();
                        HiosHelperNew.resolveAd(MySettingAct.this, MySettingAct.this, "dataability://" + AppUtils.getAppPackageName() + ".hs.act.slbapp.MySettingAqAct{act}");
                    }
                });
//                HiosHelperNew.resolveAd(MySettingAct.this, MySettingAct.this,
//                        "dataability://" + AppUtils.getAppPackageName() + ".hs.act.slbapp.MySettingAqAct{act}");
            }
        });
        rl2.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                // 安全中心

            }
        });
        rl3.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                // 语言切换

                int aaa = Integer.parseInt(MmkvUtils.getInstance().get_common("yuyanqiehuan", "0"));
                new XPopup.Builder(MySettingAct.this).isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                        .asBottomList("请选择切换的语言", new String[]{"跟随系统", "简体中文", "繁体中文", "英语"}, null, aaa, new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                // 是否需要重启
                                boolean restart = false;
                                if (position == 0) {
                                    // 跟随系统
                                    restart = MultiLanguages.setSystemLanguage(MySettingAct.this);
                                } else if (position == 1) {
                                    // 简体中文
                                    restart = MultiLanguages.setAppLanguage(MySettingAct.this, Locale.CHINA);
                                } else if (position == 2) {
                                    // 繁体中文
                                    restart = MultiLanguages.setAppLanguage(MySettingAct.this, Locale.TAIWAN);
                                } else if (position == 3) {
                                    // 英语
                                    restart = MultiLanguages.setAppLanguage(MySettingAct.this, Locale.ENGLISH);
                                }
                                if (restart) {
//                                        tv_jtzw1.setText(getApplication().getResources().getString(R.string.current_language));
                                    MmkvUtils.getInstance().set_common("yuyanqiehuan", position + "");
//                                            //TODO 发送广播bufen
//                                            Intent msgIntent = new Intent();
//                                            msgIntent.setAction("ShouyeActivity");
//                                            msgIntent.putExtra("id", 0);
//                                            LocalBroadcastManagers.getInstance(MySettingAct.this).sendBroadcast(msgIntent);
                                    // 1.使用recreate来重启Activity的效果差，有闪屏的缺陷
                                    // recreate();

                                    // 2.使用常规startActivity来重启Activity，有从左向右的切换动画，稍微比recreate的效果好一点，但是这种并不是最佳的效果
                                    // startActivity(new Intent(this, LanguageActivity.class));
                                    // finish();

                                    // 3.我们可以充分运用 Activity 跳转动画，在跳转的时候设置一个渐变的效果，相比前面的两种带来的体验是最佳的
                                    ActivityUtils.finishAllActivities();
                                    startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WelComeActivity"));
                                    overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out);
                                    finish();
                                }
                            }
                        }).show();
            }
        });
        rl4.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                // 清除缓存
                new XPopup.Builder(MySettingAct.this)
//                    .autoOpenSoftInput(true)
                        .maxWidth((int) (XPopupUtils.getWindowWidth(MySettingAct.this) * 0.8f)).dismissOnTouchOutside(false).isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                        .asConfirm("提示", "缓存大小为" + CacheUtil.getTotalCacheSize(BaseApp.get()) + ",确定要清理吗?", "取消", "确定", new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
//                                        ToastUtils.showLong("确定");
                                CacheUtil.clearAllCache(BaseApp.get());
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            tv_qchc1.setText(CacheUtil.getTotalCacheSize(BaseApp.get()));
                                        } catch (Exception e) {
                                            tv_qchc1.setText("");
                                        }
                                    }
                                }, 500);
                            }
                        }, new OnCancelListener() {
                            @Override
                            public void onCancel() {
//                                        ToastUtils.showLong("取消");

                            }
                        }, false, R.layout.app_maintain_confim_popup).show(); //最后一个参数绑定已有布局
            }
        });
        rl5.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                // 关于
                HiosHelperNew.resolveAd(MySettingAct.this, MySettingAct.this, "dataability://" + AppUtils.getAppPackageName() + ".hs.act.slbapp.MySettingAboutAct{act}");
            }
        });
        rl7.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                // 退出
                SlbLoginUtil.get().loginOutTowhere(MySettingAct.this, new Runnable() {
                    @Override
                    public void run() {
//                        Thread logoutThread = new Thread(() -> TUIUtils.logout(new V2TIMCallback() {
//                            @Override
//                            public void onSuccess() {
//
//                            }
//
//                            @Override
//                            public void onError(int code, String desc) {
//                                ToastUtil.toastLongMessage("logout fail: " + code + "=" + desc);
//                            }
//                        }));
//                        logoutThread.setName("Logout-Thread");
//                        logoutThread.start();
//                        UserInfo.getInstance().setToken("");
//                        UserInfo.getInstance().setAutoLogin(false);
//                        Bundle bundle = new Bundle();
//                        bundle.putBoolean(TUIKitConstants.LOGOUT, true);
//                        TUIUtils.startActivity("LoginForDevActivity", bundle);
                        finish();
//                        ToastUtils.showLong("已退出");
                    }
                });
            }
        });
        rl8.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                SlbLoginUtil.get().loginTowhere(MySettingAct.this, new Runnable() {
                    @Override
                    public void run() {
                        showlogin();
                    }
                });
            }
        });
        if (sb_ios != null) {
            sb_ios.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    sb_ios.setEnabled(isChecked);
                    MmkvUtils.getInstance().set_common("xinxiaoxitongzhi", isChecked + "");
                }
            });
        }
    }

    private void findview() {
        tv_left = findViewById(R.id.tv_left);
        tv_center = findViewById(R.id.tv_center);

        tv_jtzw1 = findViewById(R.id.tv_jtzw1);
        tv_qchc1 = findViewById(R.id.tv_qchc1);
        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl3 = findViewById(R.id.rl3);
        rl4 = findViewById(R.id.rl4);
        rl5 = findViewById(R.id.rl5);
        rl7 = findViewById(R.id.rl7);
        rl8 = findViewById(R.id.rl8);
        sb_ios = findViewById(R.id.sb_ios);
    }

    public void showlogin() {
        if (SlbLoginUtil.get().isUserLogin()) {
            Log.e(TAG, "showlogin: " + "登录了");
            rl7.setVisibility(View.VISIBLE);
            rl8.setVisibility(View.GONE);
        } else {
            Log.e(TAG, "showlogin: " + "没有登录");
            rl7.setVisibility(View.GONE);
            rl8.setVisibility(View.VISIBLE);
        }
    }
}
