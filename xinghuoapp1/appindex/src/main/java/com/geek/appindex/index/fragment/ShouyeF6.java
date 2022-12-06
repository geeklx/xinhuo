package com.geek.appindex.index.fragment;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static com.geek.libutils.SlbLoginUtil.LOGIN_REQUEST_CODE;
import static com.geek.libutils.SlbLoginUtil.LOGIN_RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.bean.AuthorStatus;
import com.geek.appcommon.util.ImageLoaderUtils;
import com.geek.appindex.R;
import com.geek.appindex.widgets.BottomListPopupView1;
import com.geek.appindex.widgets.XpopOnCancelListener;
import com.geek.appindex.widgets.XpopOnSelectListener;
import com.geek.biz1.bean.FcomBean;
import com.geek.biz1.bean.FconfigBean;
import com.geek.biz1.bean.FgrxxBean;
import com.geek.biz1.presenter.Fconfig1Presenter;
import com.geek.biz1.presenter.Ffile1Presenter;
import com.geek.biz1.presenter.FgrxxPresenter;
import com.geek.biz1.presenter.FtipsPresenter;
import com.geek.biz1.view.Fconfig1View;
import com.geek.biz1.view.Ffile1View;
import com.geek.biz1.view.FgrxxView;
import com.geek.biz1.view.FtipsView;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libglide47.base.GlideImageView;
import com.geek.libglide47.base.ShapeImageView;
import com.geek.libshuiyin.GlideEngine;
import com.geek.libutils.SlbLoginUtil;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.MmkvUtils;
import com.haier.cellarette.baselibrary.qcode.ExpandViewRect;
import com.just.agentweb.geek.hois3.HiosHelperNew;
import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.utils.MediaUtils;
import com.lxj.xpopup.XPopup;

import java.io.File;
import java.util.ArrayList;

import cc.shinichi.library.ImagePreview;
import cc.shinichi.library.tool.image.DownloadPictureUtil;
import cc.shinichi.library.tool.ui.ToastUtil;
import cc.shinichi.library.view.listener.OnBigImageClickListener;
import cc.shinichi.library.view.listener.OnBigImageLongClickListener;
import cc.shinichi.library.view.listener.OnBigImagePageChangeListener;
import cc.shinichi.library.view.listener.OnDownloadClickListener;
import cc.shinichi.library.view.listener.OnOriginProgressListener;

public class ShouyeF6 extends SlbBaseLazyFragmentNew implements Fconfig1View, Ffile1View, FtipsView, FgrxxView {

    private GlideImageView iv1;
    private TextView tv1;
    private ImageView tv2;
    private ImageView tv3;
    private ImageView iv_scan;
    private TextView tv4;
    private TextView tv5;
    private LinearLayout ll1;
    private LinearLayout ll2;
    private LinearLayout ll3;
    private LinearLayout ll4;
    private LinearLayout ll_ydl1;
    private LinearLayout ll_wdl1;
    private String ImgUrl = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fgif.55.la%2Fuploads%2F20210729%2F7f937f84b16ba10a429a94f582542409.gif&refer=http%3A%2F%2Fgif.55.la&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1637490108&t=122270260233c94723774e4cdf1d6e4e";

    private String tablayoutId;
    private String url;
    private String url4;
    private AuthorStatus needAuthorConfig = AuthorStatus.DEFAULT;
    private MessageReceiverIndex mMessageReceiver;
    private Fconfig1Presenter fconfig1Presenter;
    private Ffile1Presenter ffile1Presenter;
    private FtipsPresenter ftipsPresenter;
    private FgrxxPresenter fgrxxPresenter;//刷新用户信息
    private String url1 = "https://www.baidu.com/";
    private String url2 = "https://www.baidu.com/";
    private String url3 = "https://www.baidu.com/";

    public static ShouyeF6 getInstance(Bundle bundle) {
        ShouyeF6 mEasyWebFragment = new ShouyeF6();
        if (bundle != null) {
            mEasyWebFragment.setArguments(bundle);
        }
        return mEasyWebFragment;

    }


    @Override
    public void Onfile1Success(FcomBean bean) {
        ImgUrl = bean.getUrl();
//        ftipsPresenter.gettips_img1(url, ImgUrl);
        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
        if (fgrxxBean != null) {
            //修改头像
            ftipsPresenter.gettips_img2(url, fgrxxBean.getId(), ImgUrl, "", "", "", "");
        }
    }

    @Override
    public void Onfile1Nodata(String bean) {

    }

    @Override
    public void Onfile1Fail(String msg) {

    }

    @Override
    public void OntipsSuccess(String bean) {
        ToastUtils.showLong(bean);
        if (url1 != null && !TextUtils.isEmpty(url1) && fgrxxPresenter != null) {
            fgrxxPresenter.getgrxx(url4);
        }
    }

    @Override
    public void OntipsNodata(String bean) {
        ToastUtils.showLong(bean);
    }

    @Override
    public void OntipsFail(String msg) {
        ToastUtils.showLong(msg);
    }

    @Override
    public void Onconfig2Success(String authorizedType, FconfigBean bean) {
        //重新获取author的路径
        if (needAuthorConfig == AuthorStatus.DEFAULT) {
            url = bean.getIdentity();
            needAuthorConfig = AuthorStatus.Loading;
            fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "authorized");
        } else if (needAuthorConfig == AuthorStatus.Loading) {
            needAuthorConfig = AuthorStatus.Loaded;
            url4 = bean.getIdentity();
        }
    }

    @Override
    public void Onconfig2Nodata(String bean) {

    }

    @Override
    public void Onconfig2Fail(String msg) {

    }

    /*获取登录用户信息开始*/
    @Override
    public void OngrxxSuccess(FgrxxBean bean) {
        MmkvUtils.getInstance().set_common_json2(AppCommonUtils.userInfo, bean);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                retryData();
            }
        });
    }

    @Override
    public void OngrxxNodata(String bean) {

    }

    @Override
    public void OngrxxFail(String msg) {

    }
    /*获取登录用户信息结束*/

    public class MessageReceiverIndex extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if ("ShouyeF6".equals(intent.getAction())) {
                    //TODO 发送广播bufen
                    Intent msgIntent = new Intent();
                    msgIntent.setAction("ShouyeF6");
                    msgIntent.putExtra("query1", 0);
                    LocalBroadcastManagers.getInstance(getActivity()).sendBroadcast(msgIntent);
                } else if ("refreshAction".equals(intent.getAction())) {
                    retryData();
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void call(Object value) {
        tablayoutId = (String) value;
        ToastUtils.showLong("call->" + tablayoutId);
        MyLogUtil.e("tablayoutId->", "call->" + tablayoutId);

    }

    @Override
    public void onDestroy() {
        if (fconfig1Presenter != null) {
            fconfig1Presenter.onDestory();
        }
        if (ffile1Presenter != null) {
            ffile1Presenter.onDestory();
        }
        if (ftipsPresenter != null) {
            ftipsPresenter.onDestory();
        }
        if (fgrxxPresenter != null) {
            fgrxxPresenter.onDestory();
        }
        LocalBroadcastManagers.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        // 从缓存中拿出头像bufen
        if (fconfig1Presenter != null) {
//            fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "authorized");
            fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "resource");
        }
        retryData();
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
//        Bundle arg = getArguments();
        if (getArguments() != null) {
            tablayoutId = getArguments().getString("tablayoutId");
            MyLogUtil.e("tablayoutId->", "onCreate->" + tablayoutId);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shouyef6;
    }

    @Override
    protected void setup(final View rootView, @Nullable Bundle savedInstanceState) {
        super.setup(rootView, savedInstanceState);
        iv1 = rootView.findViewById(R.id.iv1);
        tv1 = rootView.findViewById(R.id.tv1);
        tv2 = rootView.findViewById(R.id.tv2);
        tv3 = rootView.findViewById(R.id.tv3);
        tv4 = rootView.findViewById(R.id.tv4);
        tv5 = rootView.findViewById(R.id.tv5);
        ll1 = rootView.findViewById(R.id.ll1);
        ll2 = rootView.findViewById(R.id.ll2);
        ll3 = rootView.findViewById(R.id.ll3);
        ll4 = rootView.findViewById(R.id.ll4);
        iv_scan = rootView.findViewById(R.id.iv_scan);
        ll_ydl1 = rootView.findViewById(R.id.ll_ydl1);
        ll_wdl1 = rootView.findViewById(R.id.ll_wdl1);
        iv1.setShapeType(ShapeImageView.ShapeType.RECTANGLE);
        iv1.setBorderWidth(0);
        iv1.setRadius(4);
        iv1.setBorderColor(R.color.transparent20);
//        iv1.loadCircleImage(url1, com.geek.libglide47.R.color.black);
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ToastUtils.showLong("上传头像");
                if (!SlbLoginUtil.get().isUserLogin()) {
                    SlbLoginUtil.get().loginTowhere(getActivity(), new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                    return;
                }
//                set_img();
                Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MySettingMyPhotoAct");
                intent.putExtra("imageUrl", ImgUrl);
                startActivity(intent);
            }
        });
        tv1.setText(getActivity().getApplication().getResources().getString(R.string.applogin30));
        ExpandViewRect.expandViewTouchDelegate(tv3, 30, 30, 30, 30);
        // 二维码
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bitmap转换成byte[]
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                iv1.getImageLoader().getImageView().getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte[] datas = baos.toByteArray();
                //
                FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
                Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MySettingEwmAct");
                intent.putExtra("erweima", fgrxxBean.getPhonenum());
                intent.putExtra("erweima2", ImgUrl);
                startActivity(intent);


            }
        });

        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MySaomaAct");
                startActivity(intent);
            }
        });

        // 个人资料
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MyPersonalDataAct"));
            }
        });
        // 签名
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new XPopup.Builder(getContext())
//                        .maxWidth((int) (XPopupUtils.getWindowWidth(getActivity()) * 0.8f))
//                        .hasStatusBarShadow(false)
//                        //.dismissOnBackPressed(false)
//                        .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//                        .autoOpenSoftInput(true)
//                        .isDarkTheme(false)
//                        .setPopupCallback(new DemoXPopupListener())
////                        .autoFocusEditText(false) //是否让弹窗内的EditText自动获取焦点，默认是true
//                        //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
//                        .asInputConfirm("签名修改", null, null, "请填写签名…",
//                                new OnInputConfirmListener() {
//                                    @Override
//                                    public void onConfirm(String text) {
////                                new XPopup.Builder(getContext()).asLoading().show();
//                                        if (text != null && !"".equals(text)) {
//                                            tv5.setText(text);
//                                            FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
//                                            if (fgrxxBean != null) {
//                                                if (text != null && !"".equals(text)) {
//                                                    //修改签名
//                                                    ftipsPresenter.gettips_img2(url, fgrxxBean.getId(), "", text, "", "", "");
//                                                } else {
//                                                    ToastUtils.showLong("请输入签名内容");
//                                                }
//                                            }
//                                        }
//                                        ftipsPresenter.gettips_sign(url, text);
//                                    }
//                                })
//                        .show();
                startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MySettingQmAct"));

            }
        });
        // 我的收藏
        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(url1)) {
                    url1 = "https://www.baidu.com/";
                }
                HiosHelperNew.resolveAd(getActivity(), getActivity(), url1);
            }
        });
        // 企业/组织
        ll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HiosHelperNew.resolveAd(getActivity(), getActivity(),
//                        "http://www.baidu.com/?condition=login");
                if (TextUtils.isEmpty(url2)) {
                    url2 = "https://www.baidu.com/";
                }
                HiosHelperNew.resolveAd(getActivity(), getActivity(), url2);
            }
        });
        // 我的消息
        ll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (TextUtils.isEmpty(url2)) {
//                    url3 = "https://www.baidu.com/";
//                }
//                HiosHelperNew.resolveAd(getActivity(), getActivity(), url3);
                HiosHelperNew.resolveAd(getActivity(), getActivity(), "dataability://" + AppUtils.getAppPackageName() + ".hs.act.slbapp.MySettingAboutAct{act}");
            }
        });
        // 设置
        ll4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MySettingAct"));
//                SlbLoginUtil.get().loginTowhere(getActivity(), new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MySettingAct"));
//                    }
//                });
            }
        });
        // 未登录
        ll_wdl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlbLoginUtil.get().loginTowhere(getActivity(), new Runnable() {
                    @Override
                    public void run() {
                        retryData();
                    }
                });
            }
        });
        //返回
        rootView.findViewById(R.id.tv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        //
        mMessageReceiver = new MessageReceiverIndex();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction("ShouyeF6");
        filter.addAction("refreshAction");
        LocalBroadcastManagers.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);
        //
        fconfig1Presenter = new Fconfig1Presenter();
        fconfig1Presenter.onCreate(this);
        ffile1Presenter = new Ffile1Presenter();
        ffile1Presenter.onCreate(this);
        ftipsPresenter = new FtipsPresenter();
        ftipsPresenter.onCreate(this);

        fgrxxPresenter = new FgrxxPresenter();
        fgrxxPresenter.onCreate(this);
        //
        donetwork();
//        ShouyeFooterBean bean = new Gson().fromJson("{}", ShouyeFooterBean.class);
//        MyLogUtil.e("sssssssssss", bean.toString());
//        MyLogUtil.e("sssssssssss", bean.getText_id());
//        tv_center_content.setText(bean.getText_id());
        //
        initTencentImg(ImgUrl);
    }

    private void initTencentImg(String mIconUrl) {
//        V2TIMUserFullInfo v2TIMUserFullInfo = new V2TIMUserFullInfo();
//        // 头像
//        if (!TextUtils.isEmpty(mIconUrl)) {
//            v2TIMUserFullInfo.setFaceUrl(mIconUrl);
//        }
//        V2TIMManager.getInstance().setSelfInfo(v2TIMUserFullInfo, new V2TIMCallback() {
//            @Override
//            public void onError(int code, String desc) {
//            }
//
//            @Override
//            public void onSuccess() {
//            }
//        });
    }

    private void set_img() {
        // 完全自定义配置
        ImagePreview.getInstance()
                // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好
                .setContext(getActivity())
                // 从第几张图片开始，索引从0开始哦~
                .setIndex(0)

                //=================================================================================================
                // 有三种设置数据集合的方式，根据自己的需求进行三选一：
                // 1：第一步生成的imageInfo List
//                        .setImageInfoList(imageInfoList)

                // 2：直接传url List
                //.setImageList(List<String> imageList)

                // 3：只有一张图片的情况，可以直接传入这张图片的url
                .setImage(ImgUrl)
                //=================================================================================================

                // 加载策略，默认为手动模式
//                        .setLoadStrategy(loadStrategy)

                // 保存的文件夹名称，会在Picture目录进行文件夹的新建。比如："BigImageView"，会在Picture目录新建BigImageView文件夹)
                .setFolderName("GeekApp")

                // 缩放动画时长，单位ms
                .setZoomTransitionDuration(300)

                // 是否显示加载失败的Toast
                .setShowErrorToast(true)

                // 是否启用点击图片关闭。默认启用
                .setEnableClickClose(true)
                // 是否启用下拉关闭。默认不启用
                .setEnableDragClose(true)
                // 是否启用上拉关闭。默认不启用
                .setEnableUpDragClose(true)
                // 是否忽略缩放启用拉动关闭。默认不忽略
//                        .setEnableDragCloseIgnoreScale(enableDragIgnoreScale)

                // 是否显示关闭页面按钮，在页面左下角。默认不显示
//                        .setShowCloseButton(showCloseButton)
                // 设置关闭按钮图片资源，可不填，默认为库中自带：R.drawable.ic_action_close
                .setCloseIconResId(R.drawable.ic_action_close)

                // 是否显示下载按钮，在页面右下角。默认显示
                .setShowDownButton(false)
                // 设置下载按钮图片资源，可不填，默认为库中自带：R.drawable.icon_download_new
                .setDownIconResId(R.drawable.icon_download_new)

                // 设置是否显示顶部的指示器（1/9）默认显示
                .setShowIndicator(false)
                // 设置顶部指示器背景shape，默认自带灰色圆角shape
                .setIndicatorShapeResId(R.drawable.shape_indicator_bg)

                // 设置失败时的占位图，默认为库中自带R.drawable.load_failed，设置为 0 时不显示
                .setErrorPlaceHolder(R.drawable.load_failed)

                // 点击回调
                .setBigImageClickListener(new OnBigImageClickListener() {
                    @Override
                    public void onClick(Activity activity, View view, int position) {
                        // ...
                        Log.d("TAG", "onClick: ");
                    }
                })
                // 长按回调
                .setBigImageLongClickListener(new OnBigImageLongClickListener() {
                    @Override
                    public boolean onLongClick(Activity activity, View view, int position) {
                        // ...
                        BottomListPopupView1 bottomListPopupView1 = new BottomListPopupView1(activity, 0, 0);
                        bottomListPopupView1.setStringData("", new String[]{"拍照", "从手机相册选择", "保存图片"}, null);
                        bottomListPopupView1.setXpopOnSelectListener(new XpopOnSelectListener() {
                            @Override
                            public void onSelect(View textView, int position, String text) {
                                if (TextUtils.equals("拍照", text)) {
                                    // 单独拍照
                                    PictureSelector.create(activity).openCamera(SelectMimeType.ofImage())// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                                            .setCropEngine(new ImageLoaderUtils.ImageFileCropEngine()).forResult(new OnResultCallbackListener<LocalMedia>() {
                                                @Override
                                                public void onResult(ArrayList<LocalMedia> result) {
                                                    for (LocalMedia media : result) {
                                                        if (media.getWidth() == 0 || media.getHeight() == 0) {
                                                            if (PictureMimeType.isHasImage(media.getMimeType())) {
                                                                MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(media.getPath());
                                                                media.setWidth(imageExtraInfo.getWidth());
                                                                media.setHeight(imageExtraInfo.getHeight());
                                                            } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                                                                MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(PictureAppMaster.getInstance().getAppContext(), media.getPath());
                                                                media.setWidth(videoExtraInfo.getWidth());
                                                                media.setHeight(videoExtraInfo.getHeight());
                                                            }
                                                        }
                                                        Log.i("TAG", "文件名: " + media.getFileName());
                                                        Log.i("TAG", "是否压缩:" + media.isCompressed());
                                                        Log.i("TAG", "压缩:" + media.getCompressPath());
                                                        Log.i("TAG", "原图:" + media.getPath());
                                                        Log.i("TAG", "绝对路径:" + media.getRealPath());
                                                        Log.i("TAG", "是否裁剪:" + media.isCut());
                                                        Log.i("TAG", "裁剪:" + media.getCutPath());
                                                        Log.i("TAG", "是否开启原图:" + media.isOriginal());
                                                        Log.i("TAG", "原图路径:" + media.getOriginalPath());
                                                        Log.i("TAG", "Android Q 特有Path:" + media.getAvailablePath());
                                                        Log.i("TAG", "宽高: " + media.getWidth() + "x" + media.getHeight());
                                                        Log.i("TAG", "Size: " + media.getSize());

                                                        Log.i("TAG", "onResult: " + media.toString());

                                                        // TODO 可以通过PictureSelectorExternalUtils.getExifInterface();方法获取一些额外的资源信息，如旋转角度、经纬度等信息
                                                    }
                                                    ImgUrl = result.get(0).getAvailablePath();
                                                    iv1.loadImage(ImgUrl, R.drawable.icon_grxx1);
                                                    activity.finish();
                                                    //
                                                    ffile1Presenter.getfile1(url, new File(ImgUrl));
                                                }

                                                @Override
                                                public void onCancel() {

                                                }
                                            });
                                    Log.d("TAG", "onLongClick: ");
                                }
                                if (TextUtils.equals("从手机相册选择", text)) {
                                    // 进入相册 以下是例子：不需要的api可以不写
                                    PictureSelector.create(activity).openGallery(SelectMimeType.ofImage()).setImageEngine(GlideEngine.createGlideEngine()).setSelectionMode(SelectModeConfig.SINGLE).setCropEngine(new ImageLoaderUtils.ImageFileCropEngine()).isPreviewImage(true)// 是否可预览图片
                                            .isPreviewVideo(false)// 是否可预览视频
                                            .forResult(new OnResultCallbackListener<LocalMedia>() {
                                                @Override
                                                public void onResult(ArrayList<LocalMedia> result) {
                                                    for (LocalMedia media : result) {
                                                        if (media.getWidth() == 0 || media.getHeight() == 0) {
                                                            if (PictureMimeType.isHasImage(media.getMimeType())) {
                                                                MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(media.getPath());
                                                                media.setWidth(imageExtraInfo.getWidth());
                                                                media.setHeight(imageExtraInfo.getHeight());
                                                            } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                                                                MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(PictureAppMaster.getInstance().getAppContext(), media.getPath());
                                                                media.setWidth(videoExtraInfo.getWidth());
                                                                media.setHeight(videoExtraInfo.getHeight());
                                                            }
                                                        }
                                                        Log.i("TAG", "文件名: " + media.getFileName());
                                                        Log.i("TAG", "是否压缩:" + media.isCompressed());
                                                        Log.i("TAG", "压缩:" + media.getCompressPath());
                                                        Log.i("TAG", "原图:" + media.getPath());
                                                        Log.i("TAG", "绝对路径:" + media.getRealPath());
                                                        Log.i("TAG", "是否裁剪:" + media.isCut());
                                                        Log.i("TAG", "裁剪:" + media.getCutPath());
                                                        Log.i("TAG", "是否开启原图:" + media.isOriginal());
                                                        Log.i("TAG", "原图路径:" + media.getOriginalPath());
                                                        Log.i("TAG", "Android Q 特有Path:" + media.getAvailablePath());
                                                        Log.i("TAG", "宽高: " + media.getWidth() + "x" + media.getHeight());
                                                        Log.i("TAG", "Size: " + media.getSize());

                                                        Log.i("TAG", "onResult: " + media.toString());

                                                        // TODO 可以通过PictureSelectorExternalUtils.getExifInterface();方法获取一些额外的资源信息，如旋转角度、经纬度等信息
                                                    }
                                                    ImgUrl = result.get(0).getAvailablePath();
                                                    iv1.loadImage(ImgUrl, R.drawable.icon_grxx1);
                                                    activity.finish();
                                                    //
                                                    ffile1Presenter.getfile1(url, new File(ImgUrl));
                                                }

                                                @Override
                                                public void onCancel() {

                                                }
                                            });
                                    Log.d("TAG", "onLongClick: ");
                                }
                                if (TextUtils.equals("保存图片", text)) {
                                    OnDownloadClickListener downloadClickListener = ImagePreview.getInstance().getDownloadClickListener();
                                    if (downloadClickListener != null) {
                                        boolean interceptDownload = downloadClickListener.isInterceptDownload();
                                        if (interceptDownload) {
                                            // 拦截了下载，不执行下载
                                        } else {
                                            // 没有拦截下载
                                            checkAndDownload(activity);
                                        }
                                        ImagePreview.getInstance().getDownloadClickListener().onClick(activity, textView, ImagePreview.getInstance().getIndex());
                                    } else {
                                        checkAndDownload(activity);
                                    }
                                }
                            }
                        });
                        bottomListPopupView1.setXpopOnCancelListener(new XpopOnCancelListener() {
                            @Override
                            public void onCancel(View textView) {
                                activity.finish();
                            }
                        });
                        new XPopup.Builder(activity).autoDismiss(false).asCustom(bottomListPopupView1).show();
                        return false;
                    }
                })
                // 页面切换回调
                .setBigImagePageChangeListener(new OnBigImagePageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        Log.d("TAG", "onPageScrolled: ");
                    }

                    @Override
                    public void onPageSelected(int position) {
                        Log.d("TAG", "onPageSelected: ");
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        Log.d("TAG", "onPageScrollStateChanged: ");
                    }
                })
                // 下载按钮点击回调，可以拦截下载逻辑，从而实现自己下载或埋点统计
                .setDownloadClickListener(new OnDownloadClickListener() {
                    @Override
                    public void onClick(Activity activity, View view, int position) {
                        // 可以在此处执行您自己的下载逻辑、埋点统计等信息
                        Log.d("TAG", "onClick: position = " + position);
                    }

                    @Override
                    public boolean isInterceptDownload() {
                        // return true 时, 需要自己实现下载
                        // return false 时, 使用内置下载
                        return false;
                    }
                })

                //=================================================================================================
                // 设置查看原图时的百分比样式：库中带有一个样式：ImagePreview.PROGRESS_THEME_CIRCLE_TEXT，使用如下：
                .setProgressLayoutId(ImagePreview.PROGRESS_THEME_CIRCLE_TEXT, new OnOriginProgressListener() {
                    @Override
                    public void progress(View parentView, int progress) {
                        Log.d("TAG", "progress: " + progress);

                        // 需要找到进度控件并设置百分比，回调中的parentView即传入的布局的根View，可通过parentView找到控件：
                        ProgressBar progressBar = parentView.findViewById(R.id.sh_progress_view);
                        TextView textView = parentView.findViewById(R.id.sh_progress_text);
                        progressBar.setProgress(progress);
                        String progressText = progress + "%";
                        textView.setText(progressText);
                    }

                    @Override
                    public void finish(View parentView) {
                        Log.d("TAG", "finish: ");
                    }
                })

                // 使用自定义百分比样式，传入自己的布局，并设置回调，再根据parentView找到进度控件进行百分比的设置：
                //.setProgressLayoutId(R.layout.image_progress_layout_theme_1, new OnOriginProgressListener() {
                //    @Override public void progress(View parentView, int progress) {
                //        Log.d(TAG, "progress: " + progress);
                //
                //        ProgressBar progressBar = parentView.findViewById(R.id.progress_horizontal);
                //        progressBar.setProgress(progress);
                //    }
                //
                //    @Override public void finish(View parentView) {
                //        Log.d(TAG, "finish: ");
                //    }
                //})
                //=================================================================================================

                // 开启预览
                .start();
    }


    /**
     * 第一次进来加载bufen
     */
    private void donetwork() {
        retryData();
    }

    // 刷新
    private void retryData() {
//        mEmptyView.loading();
//        presenter1.getLBBannerData("0");
//        refreshLayout1.finishRefresh();
//        emptyview1.success();
        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
        if (fgrxxBean != null) {
            ll_ydl1.setVisibility(View.VISIBLE);
            ll_wdl1.setVisibility(View.GONE);
            if (TextUtils.equals("0", fgrxxBean.getSex())) {
                iv1.loadImage(fgrxxBean.getPhoto(), R.drawable.icon_grxx1);
            } else {
                iv1.loadImage(fgrxxBean.getPhoto(), R.drawable.icon_grxx2);
            }
            if (!TextUtils.isEmpty(fgrxxBean.getPhoto())) {
                ImgUrl = fgrxxBean.getPhoto();
            }
            tv1.setText(fgrxxBean.getName());
            tv4.setText(fgrxxBean.getOrgName());
            if (!TextUtils.isEmpty(fgrxxBean.getSignature())) {
                tv5.setText(fgrxxBean.getSignature());
            }
            url1 = fgrxxBean.getOrganization();
            url2 = fgrxxBean.getMyCollection();
            url3 = fgrxxBean.getMyMsg();
        } else {
            ll_wdl1.setVisibility(View.VISIBLE);
            ll_ydl1.setVisibility(View.GONE);
            iv1.loadImage("", R.drawable.icon_com_default1);
        }

    }

    /**
     * 底部点击bufen
     *
     * @param cateId
     * @param isrefresh
     */
    @Override
    public void getCate(String cateId, boolean isrefresh) {

        if (!isrefresh) {
            // 从缓存中拿出头像bufen

            return;
        }
        ToastUtils.showLong("下拉刷新啦");
    }

    /**
     * 当切换底部的时候通知每个fragment切换的id是哪个bufen
     *
     * @param cateId
     */
    @Override
    public void give_id(String cateId) {
//        ToastUtils.showLong("下拉刷新啦");
        MyLogUtil.e("tablayoutId->", "give_id->" + cateId);
    }

    /**
     * --------------------------------业务逻辑分割线----------------------------------
     */

    private void checkAndDownload(Activity activity) {
        // 检查权限
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // 拒绝权限
                ToastUtil.getInstance().showShort(activity, getString(cc.shinichi.library.R.string.toast_deny_permission_save_failed));
            } else {
                //申请权限
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
            }
        } else {
            // 下载当前图片
            downloadCurrentImg(activity);
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return super.shouldShowRequestPermissionRationale(permission);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    downloadCurrentImg(getActivity());
                } else {
                    ToastUtil.getInstance().showShort(getActivity(), getString(cc.shinichi.library.R.string.toast_deny_permission_save_failed));
                }
            }
        }
    }

    /**
     * 下载当前图片到SD卡
     */
    private void downloadCurrentImg(Activity activity) {
        DownloadPictureUtil.INSTANCE.downloadPicture(activity.getApplicationContext(), ImagePreview.getInstance().getImageInfoList().get(ImagePreview.getInstance().getIndex()).getOriginUrl());
        activity.finish();
    }

    @Override
    public void onActResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == LOGIN_RESULT_OK && fgrxxPresenter != null) {
            fgrxxPresenter.getgrxx(url4);
        }
    }
}
