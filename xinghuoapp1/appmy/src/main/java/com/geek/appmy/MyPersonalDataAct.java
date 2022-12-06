package com.geek.appmy;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.interfaces.CommonXPopupListener;
import com.geek.appcommon.SlbBase;
import com.geek.appcommon.bean.AuthorStatus;
import com.geek.appcommon.util.ImageLoaderUtils;
import com.geek.appmy.widgets.BottomListPopupView1;
import com.geek.appmy.widgets.XpopOnCancelListener;
import com.geek.appmy.widgets.XpopOnSelectListener;
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
import com.geek.libglide47.base.GlideImageView;
import com.geek.libglide47.base.ShapeImageView;
import com.geek.libshuiyin.GlideEngine;
import com.geek.libswipebacklayout.SwipeBack;
import com.geek.libutils.SlbLoginUtil;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.data.MmkvUtils;
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
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.XPopupUtils;

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

@SwipeBack(value = true)
public class MyPersonalDataAct extends SlbBase implements Ffile1View, Fconfig1View, FtipsView, FgrxxView {

    private Activity mActivity;
    private TextView tv_left;//返回
    private TextView tv_center;//标题名称
    private GlideImageView iv_headportrait;//头像
    private TextView tv_personalname;//姓名txt
    private TextView tv_personalphone;//手机号txt
    private TextView tv_personalsex;//性别txt
    private TextView tv_personalautograph;//个性签名txt

    private RelativeLayout rl1;//头像
    private RelativeLayout rl2;//姓名
    private RelativeLayout rl3;//手机号
    private RelativeLayout rl4;//性别
    private RelativeLayout rl5;//个性签名
    private RelativeLayout rl6;//所属组织
    private RelativeLayout rl7;//职位

    private String ImgUrl = "";
    private String url;
    private String url1;
    private Ffile1Presenter ffile1Presenter;
    private Fconfig1Presenter fconfig1Presenter;
    private FtipsPresenter ftipsPresenter;
    private FgrxxPresenter fgrxxPresenter;
    private String sex;
    private AuthorStatus needAuthorConfig = AuthorStatus.DEFAULT;

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
    protected int getLayoutId() {
        return R.layout.activity_mypersonal;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        BarUtils.setStatusBarLightMode(this, true);
        super.setup(savedInstanceState);
        mActivity = this;
        findview();
        onclick();
        donetwork();
        fconfig1Presenter = new Fconfig1Presenter();
        fconfig1Presenter.onCreate(this);
        ffile1Presenter = new Ffile1Presenter();
        ffile1Presenter.onCreate(this);
        ftipsPresenter = new FtipsPresenter();
        ftipsPresenter.onCreate(this);
        fgrxxPresenter = new FgrxxPresenter();
        fgrxxPresenter.onCreate(this);
    }

    private void donetwork() {
        retryData();
    }

    // 刷新
    private void retryData() {
        tv_center.setText(getApplication().getResources().getString(R.string.appmy30));
        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
        if (fgrxxBean != null) {
            sex = fgrxxBean.getSex() != null && !"".equals(fgrxxBean.getSex()) ? fgrxxBean.getSex() : "1";
            if (TextUtils.equals("0", fgrxxBean.getSex())) {
                iv_headportrait.loadImage(fgrxxBean.getPhoto(), R.drawable.icon_grxx1);
                tv_personalsex.setText("男");
            } else {
                iv_headportrait.loadImage(fgrxxBean.getPhoto(), R.drawable.icon_grxx2);
                tv_personalsex.setText("女");
            }
            if (!TextUtils.isEmpty(fgrxxBean.getPhoto())) {
                ImgUrl = fgrxxBean.getPhoto();
            }
            tv_personalname.setText(fgrxxBean.getName() != null && !"".equals(fgrxxBean.getName()) ? fgrxxBean.getName() : "...");
            tv_personalphone.setText(fgrxxBean.getPhonenum() != null && !"".equals(fgrxxBean.getPhonenum()) ? fgrxxBean.getPhonenum() : "...");
            tv_personalautograph.setText(fgrxxBean.getSignature() != null && !"".equals(fgrxxBean.getSignature()) ? fgrxxBean.getSignature() : "未设置");
        } else {
            iv_headportrait.loadImage("", R.drawable.icon_com_default1);
        }

    }


    private void onclick() {
        //返回监听
        tv_left.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                onBackPressed();
            }
        });
        //头像选择
        rl1.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {

            }
        });
        rl2.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                //姓名修改
                xpopwindows("姓名修改", "请填写姓名…", "name");
            }
        });
        rl3.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                //手机号修改
                xpopwindows("手机号修改", "请填写手机号…", "phone");
            }
        });

        rl4.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                //性别修改
                new XPopup.Builder(mActivity)
                        .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                        .asCenterList("请选择性别", new String[]{"男", "女"},
                                null, Integer.parseInt(sex),
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);

                                        if (text != null && !"".equals(text)) {
                                            tv_personalsex.setText(text);
                                            if (fgrxxBean != null) {
                                                if (text != null && !"".equals(text)) {
                                                    if (text.equals("男")) {
                                                        sex = "0";
                                                    } else {
                                                        sex = "1";
                                                    }
                                                    //修改性别
                                                    ftipsPresenter.gettips_img2(url, fgrxxBean.getId(), "", "", "", "", sex);
                                                } else {
                                                    ToastUtils.showLong("请选择性别");
                                                }
                                            }
                                        }
                                    }
                                })
                        .show();

            }
        });
        rl5.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                //个人签名
//                xpopwindows("签名修改", "请填写签名…", "sign");
                startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MySettingQmAct"));

            }
        });
        rl6.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                // 所属组织
            }
        });

        rl7.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                // 职位
            }
        });
    }

    public void xpopwindows(String title, String hint, String type) {
        new XPopup.Builder(mActivity)
                .maxWidth((int) (XPopupUtils.getWindowWidth(mActivity) * 0.8f))
                .hasStatusBarShadow(false)
                //.dismissOnBackPressed(false)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .autoOpenSoftInput(true)
                .isDarkTheme(false)
                .setPopupCallback(new CommonXPopupListener())
//                        .autoFocusEditText(false) //是否让弹窗内的EditText自动获取焦点，默认是true
                //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
                .asInputConfirm(title, null, null, hint,
                        new OnInputConfirmListener() {
                            @Override
                            public void onConfirm(String text) {
                                FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
                                if (text != null && !"".equals(text)) {
                                    if (type.equals("name")) {
                                        tv_personalname.setText(text);
                                        if (fgrxxBean != null) {
                                            if (text != null && !"".equals(text)) {
                                                //修改真实姓名
                                                ftipsPresenter.gettips_img2(url, fgrxxBean.getId(), "", "", text, "", "");
                                            } else {
                                                ToastUtils.showLong("姓名不能为空");
                                            }
                                        }
                                    } else if (type.equals("phone")) {
                                        tv_personalphone.setText(text);
                                        if (fgrxxBean != null) {
                                            if (text != null && !"".equals(text)) {
                                                //修改手机号
                                                ftipsPresenter.gettips_img2(url, fgrxxBean.getId(), "", "", "", text, "");
                                            } else {
                                                ToastUtils.showLong("手机号不能为空");
                                            }
                                        }
                                    } else if (type.equals("sign")) {
                                        tv_personalautograph.setText(text);
                                        if (fgrxxBean != null) {
                                            if (text != null && !"".equals(text)) {
                                                //修改签名
                                                ftipsPresenter.gettips_img2(url, fgrxxBean.getId(), "", text, "", "", "");
                                            } else {
                                                ToastUtils.showLong("签名不能为空");
                                            }
                                        }
                                    }
                                }
                            }
                        })
                .show();

    }

    private void findview() {
        tv_left = findViewById(R.id.tv_left);
        tv_center = findViewById(R.id.tv_center);
        iv_headportrait = findViewById(R.id.iv_headportrait);
        tv_personalname = findViewById(R.id.tv_personalname);
        tv_personalphone = findViewById(R.id.tv_personalphone);
        tv_personalsex = findViewById(R.id.tv_personalsex);
        tv_personalautograph = findViewById(R.id.tv_personalautograph);
        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl3 = findViewById(R.id.rl3);
        rl4 = findViewById(R.id.rl4);
        rl5 = findViewById(R.id.rl5);
        rl6 = findViewById(R.id.rl6);
        rl7 = findViewById(R.id.rl7);

        iv_headportrait.setShapeType(ShapeImageView.ShapeType.RECTANGLE);
        iv_headportrait.setBorderWidth(0);
        iv_headportrait.setRadius(4);
        iv_headportrait.setBorderColor(R.color.transparent20);
//        iv1.loadCircleImage(url1, com.geek.libglide47.R.color.black);
        iv_headportrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ToastUtils.showLong("上传头像");
                SlbLoginUtil.get().loginTowhere(mActivity, new Runnable() {
                    @Override
                    public void run() {
//                        set_img();

                        Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MySettingMyPhotoAct");
                        intent.putExtra("imageUrl", ImgUrl);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void set_img() {
        // 完全自定义配置
        ImagePreview.getInstance()
                // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好
                .setContext(mActivity)
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
                                    PictureSelector.create(activity)
                                            .openCamera(SelectMimeType.ofImage())// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                                            .setCropEngine(new ImageLoaderUtils.ImageFileCropEngine())
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
                                                    iv_headportrait.loadImage(ImgUrl, R.drawable.icon_grxx1);
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
                                    PictureSelector.create(activity)
                                            .openGallery(SelectMimeType.ofImage())
                                            .setImageEngine(GlideEngine.createGlideEngine())
                                            .setSelectionMode(SelectModeConfig.SINGLE)
                                            .setCropEngine(new ImageLoaderUtils.ImageFileCropEngine())
                                            .isPreviewImage(true)// 是否可预览图片
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
                                                    iv_headportrait.loadImage(ImgUrl, R.drawable.icon_grxx1);
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
                        new XPopup.Builder(activity)
                                .autoDismiss(false)
                                .asCustom(bottomListPopupView1)
                                .show();
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

    //获取第三方服务拼接地址标识
    @Override
    public void Onconfig2Success(String authorizedType, FconfigBean bean) {
        //重新获取author的路径
        if (needAuthorConfig == AuthorStatus.DEFAULT) {
            url = bean.getIdentity();
            needAuthorConfig = AuthorStatus.Loading;
            fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "authorized");
        } else if (needAuthorConfig == AuthorStatus.Loading) {
            needAuthorConfig = AuthorStatus.Loaded;
            url1 = bean.getIdentity();
        }
    }

    @Override
    public void Onconfig2Nodata(String bean) {

    }

    @Override
    public void Onconfig2Fail(String msg) {

    }


    @Override
    public void OntipsSuccess(String bean) {
        ToastUtils.showLong(bean);
        if (url1 != null && !TextUtils.isEmpty(url1) && fgrxxPresenter != null) {
            fgrxxPresenter.getgrxx(url1);
        }
    }

    @Override
    public void OntipsNodata(String bean) {

    }

    @Override
    public void OntipsFail(String msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    downloadCurrentImg(mActivity);
                } else {
                    ToastUtil.getInstance().showShort(mActivity, getString(cc.shinichi.library.R.string.toast_deny_permission_save_failed));
                }
            }
        }
    }

    /**
     * 下载当前图片到SD卡
     */
    private void downloadCurrentImg(Activity activity) {
        DownloadPictureUtil.INSTANCE.downloadPicture(activity.getApplicationContext(),
                ImagePreview.getInstance().getImageInfoList().get(ImagePreview.getInstance().getIndex()).getOriginUrl());
        activity.finish();
    }


    @Override
    public void OngrxxSuccess(FgrxxBean bean) {
        MmkvUtils.getInstance().set_common_json2(AppCommonUtils.userInfo, bean);
        retryData();
        Intent intent = new Intent();
        intent.setAction("refreshAction");
        LocalBroadcastManagers.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void OngrxxNodata(String bean) {
        ToastUtils.showShort(bean);
    }

    @Override
    public void OngrxxFail(String msg) {
        ToastUtils.showShort(msg);
    }
}
