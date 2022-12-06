package com.geek.appcommon.util;

import android.app.Activity;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.geek.common.R;
import com.haier.cellarette.baselibrary.btnonclick.view.BounceView;
import com.just.agentweb.geek.hois3.HiosHelperNew;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;

public class NaticeUtil {
    private static BasePopupView loadingPopup1 = null;//公告弹框
    private static BasePopupView loadingPopup2 = null;//允许推送通知
    private static BasePopupView loadingPopup3 = null;//业务弹框

    /*默认公告*/
    public static void notice(Context mActivity) {
        loadingPopup1 = new XPopup.Builder(mActivity)
                .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
                .isViewMode(true)
                .dismissOnTouchOutside(false)
                .asCustom(new CenterPopupView(mActivity) {
                    private TextView tvContent;
                    private TextView btnClose;
                    private TextView btnOk;

                    @Override
                    protected int getImplLayoutId() {
                        return R.layout.dialog_notice;
                    }

                    @Override
                    protected void onCreate() {
                        super.onCreate();
                        tvContent = findViewById(R.id.tv_content);
                        btnClose = findViewById(R.id.btn_cancle);
                        btnOk = findViewById(R.id.btn_ok);
                        /*第二种样式*/
//                        btnClose.setBackgroundResource(R.drawable.common_dialog_notice1);
//                        btnOk.setVisibility(View.GONE);
                        /*第三种样式*/
//                        btnClose.setBackgroundResource(R.drawable.common_dialog_notice);
//                        btnOk.setVisibility(View.VISIBLE);
                        tvContent.setText("公告摘要描述公告摘要描述公告，摘要描述公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
                        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
                        BounceView.addAnimTo(btnOk);
                        BounceView.addAnimTo(btnClose);
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HiosHelperNew.resolveAd((Activity) mActivity, mActivity, "www.baidu.com");
                                ToastUtils.showShort("点击了去看看");
                                dismiss();
                                return;
                            }
                        });
                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ToastUtils.showShort("点击了知道了");
                                dismiss();
                                return;
                            }
                        });
                    }
                });
        if (!loadingPopup1.isShow()) {
            loadingPopup1.show();
        }
    }

    /*公告1*/
    public static void notice1(Activity mActivity) {
        loadingPopup1 = new XPopup.Builder(mActivity)
                .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
                .isViewMode(true)
                .dismissOnTouchOutside(false)
                .asCustom(new CenterPopupView(mActivity) {
                    private TextView tvContent;
                    private TextView btnClose;
                    private TextView btnOk;

                    @Override
                    protected int getImplLayoutId() {
                        return R.layout.dialog_notice;
                    }

                    @Override
                    protected void onCreate() {
                        super.onCreate();
                        tvContent = findViewById(R.id.tv_content);
                        btnClose = findViewById(R.id.btn_cancle);
                        btnOk = findViewById(R.id.btn_ok);
                        /*第二种样式*/
                        btnClose.setBackgroundResource(R.drawable.common_dialog_notice1);
                        btnOk.setVisibility(View.GONE);
                        /*第三种样式*/
//                        btnClose.setBackgroundResource(R.drawable.common_dialog_notice);
//                        btnOk.setVisibility(View.VISIBLE);
                        tvContent.setText("公告摘要描述公告摘要描述公告，摘要描述公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
                        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
                        BounceView.addAnimTo(btnOk);
                        BounceView.addAnimTo(btnClose);
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HiosHelperNew.resolveAd(mActivity, mActivity, "www.baidu.com");
                                ToastUtils.showShort("点击了去看看");
                                dismiss();
                                return;
                            }
                        });
                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ToastUtils.showShort("点击了知道了");
                                dismiss();
                                return;
                            }
                        });
                    }
                });
        if (!loadingPopup1.isShow()) {
            loadingPopup1.show();
        }
    }

    /*公告2*/
    public static void notice2(Activity mActivity) {
        loadingPopup1 = new XPopup.Builder(mActivity)
                .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
                .isViewMode(true)
                .dismissOnTouchOutside(false)
                .asCustom(new CenterPopupView(mActivity) {
                    private TextView tvContent;
                    private TextView btnClose;
                    private TextView btnOk;

                    @Override
                    protected int getImplLayoutId() {
                        return R.layout.dialog_notice;
                    }

                    @Override
                    protected void onCreate() {
                        super.onCreate();
                        tvContent = findViewById(R.id.tv_content);
                        btnClose = findViewById(R.id.btn_cancle);
                        btnOk = findViewById(R.id.btn_ok);
                        /*第二种样式*/
//                        btnClose.setBackgroundResource(R.drawable.common_dialog_notice1);
//                        btnOk.setVisibility(View.GONE);
                        /*第三种样式*/
                        btnClose.setBackgroundResource(R.drawable.common_dialog_notice);
                        btnOk.setVisibility(View.VISIBLE);
                        tvContent.setText("公告摘要描述公告摘要描述公告，摘要描述公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述，公告摘要描述公告摘要描述公告摘要描述公告摘要描述");
                        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
                        BounceView.addAnimTo(btnOk);
                        BounceView.addAnimTo(btnClose);
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HiosHelperNew.resolveAd(mActivity, mActivity, "www.baidu.com");
                                ToastUtils.showShort("点击了去看看");
                                dismiss();
                                return;
                            }
                        });
                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ToastUtils.showShort("点击了知道了");
                                dismiss();
                                return;
                            }
                        });
                    }
                });
        if (!loadingPopup1.isShow()) {
            loadingPopup1.show();
        }
    }

    /*允许推送通知*/
    public static void pushnotice(Activity mActivity) {
        loadingPopup2 = new XPopup.Builder(mActivity)
                .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
                .isViewMode(true)
                .dismissOnTouchOutside(false)
                .asCustom(new CenterPopupView(mActivity) {
                    private TextView tvContent;
                    private TextView btnClose;
                    private TextView btnOk;

                    @Override
                    protected int getImplLayoutId() {
                        return R.layout.dialog_push_notice;
                    }

                    @Override
                    protected void onCreate() {
                        super.onCreate();
                        tvContent = findViewById(R.id.tv_content);
                        btnClose = findViewById(R.id.btn_cancle);
                        btnOk = findViewById(R.id.btn_ok);
                        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
                        BounceView.addAnimTo(btnOk);
                        BounceView.addAnimTo(btnClose);
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HiosHelperNew.resolveAd(mActivity, mActivity, "www.baidu.com");
                                ToastUtils.showShort("点击了去看看");
                                dismiss();
                                return;
                            }
                        });
                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ToastUtils.showShort("点击了知道了");
                                dismiss();
                                return;
                            }
                        });
                    }
                });
        if (!loadingPopup2.isShow()) {
            loadingPopup2.show();
        }
    }

    /*业务弹窗*/
    public static void businessnotice(Activity mActivity) {
        loadingPopup3 = new XPopup.Builder(mActivity)
                .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
                .isViewMode(true)
                .dismissOnTouchOutside(false)
                .asCustom(new CenterPopupView(mActivity) {
                    private TextView tvContent;
                    private TextView btnClose;
                    private TextView btnOk;
                    private ImageView ivCloseBusiness;

                    @Override
                    protected int getImplLayoutId() {
                        return R.layout.dialog_business_notice;
                    }

                    @Override
                    protected void onCreate() {
                        super.onCreate();
                        tvContent = findViewById(R.id.tv_content);
                        btnClose = findViewById(R.id.btn_cancle);
                        btnOk = findViewById(R.id.btn_ok);
                        ivCloseBusiness = findViewById(R.id.iv_close_business);
                        btnClose.setVisibility(View.GONE);
                        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
                        BounceView.addAnimTo(btnOk);
                        BounceView.addAnimTo(btnClose);
                        BounceView.addAnimTo(ivCloseBusiness);
                        ivCloseBusiness.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ToastUtils.showShort("点击了关闭");
                                dismiss();
                                return;
                            }
                        });
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HiosHelperNew.resolveAd(mActivity, mActivity, "www.baidu.com");
                                ToastUtils.showShort("点击了按钮1");
                                dismiss();
                                return;
                            }
                        });
                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HiosHelperNew.resolveAd(mActivity, mActivity, "www.baidu.com");
                                ToastUtils.showShort("点击了按钮2");
                                dismiss();
                                return;
                            }
                        });
                    }
                });
        if (!loadingPopup3.isShow()) {
            loadingPopup3.show();
        }
    }

    /*业务弹窗1*/
    public static void businessnotice1(Activity mActivity) {
        loadingPopup3 = new XPopup.Builder(mActivity)
                .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
                .isViewMode(true)
                .dismissOnTouchOutside(false)
                .asCustom(new CenterPopupView(mActivity) {
                    private TextView tvContent;
                    private TextView btnClose;
                    private TextView btnOk;
                    private ImageView ivCloseBusiness;

                    @Override
                    protected int getImplLayoutId() {
                        return R.layout.dialog_business_notice;
                    }

                    @Override
                    protected void onCreate() {
                        super.onCreate();
                        tvContent = findViewById(R.id.tv_content);
                        btnClose = findViewById(R.id.btn_cancle);
                        btnOk = findViewById(R.id.btn_ok);
                        ivCloseBusiness = findViewById(R.id.iv_close_business);
                        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
                        BounceView.addAnimTo(btnOk);
                        BounceView.addAnimTo(btnClose);
                        BounceView.addAnimTo(ivCloseBusiness);
                        ivCloseBusiness.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ToastUtils.showShort("点击了关闭");
                                dismiss();
                                return;
                            }
                        });
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HiosHelperNew.resolveAd(mActivity, mActivity, "www.baidu.com");
                                ToastUtils.showShort("点击了按钮1");
                                dismiss();
                                return;
                            }
                        });
                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HiosHelperNew.resolveAd(mActivity, mActivity, "www.baidu.com");
                                ToastUtils.showShort("点击了按钮2");
                                dismiss();
                                return;
                            }
                        });
                    }
                });
        if (!loadingPopup3.isShow()) {
            loadingPopup3.show();
        }
    }

    /*业务弹窗2*/
    public static void businessnotice2(Activity mActivity) {
        loadingPopup3 = new XPopup.Builder(mActivity)
                .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
                .isViewMode(true)
                .dismissOnTouchOutside(false)
                .asCustom(new CenterPopupView(mActivity) {
                    private TextView tvContent;
                    private TextView btnClose;
                    private TextView btnOk;
                    private ImageView ivCloseBusiness;

                    @Override
                    protected int getImplLayoutId() {
                        return R.layout.dialog_business_notice;
                    }

                    @Override
                    protected void onCreate() {
                        super.onCreate();
                        tvContent = findViewById(R.id.tv_content);
                        btnClose = findViewById(R.id.btn_cancle);
                        btnOk = findViewById(R.id.btn_ok);
                        ivCloseBusiness = findViewById(R.id.iv_close_business);
                        btnClose.setVisibility(View.GONE);
                        btnOk.setVisibility(View.GONE);
                        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
                        BounceView.addAnimTo(btnOk);
                        BounceView.addAnimTo(btnClose);
                        BounceView.addAnimTo(ivCloseBusiness);
                        ivCloseBusiness.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ToastUtils.showShort("点击了关闭");
                                dismiss();
                                return;
                            }
                        });
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HiosHelperNew.resolveAd(mActivity, mActivity, "www.baidu.com");
                                ToastUtils.showShort("点击了按钮1");
                                dismiss();
                                return;
                            }
                        });
                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HiosHelperNew.resolveAd(mActivity, mActivity, "www.baidu.com");
                                ToastUtils.showShort("点击了按钮2");
                                dismiss();
                                return;
                            }
                        });
                    }
                });
        if (!loadingPopup3.isShow()) {
            loadingPopup3.show();
        }
    }

}
