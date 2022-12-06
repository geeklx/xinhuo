package com.geek.appmy;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.SlbBase;
import com.geek.appcommon.bean.AuthorStatus;
import com.geek.appcommon.util.ImageLoaderUtils;
import com.geek.appcommon.util.MProgressDialogUtils;
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
import com.geek.libshuiyin.GlideEngine;
import com.geek.libswipebacklayout.SwipeBack;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.data.MmkvUtils;
import com.haier.cellarette.baselibrary.toasts3.MProgressDialog;
import com.luck.picture.lib.animators.AnimationType;
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
import com.lxj.xpopup.core.BasePopupView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.shinichi.library.ImagePreview;
import cc.shinichi.library.glide.FileTarget;
import cc.shinichi.library.glide.ImageLoader;
import cc.shinichi.library.tool.common.HttpUtil;
import cc.shinichi.library.tool.file.FileUtil;
import cc.shinichi.library.tool.image.DownloadPictureUtil;
import cc.shinichi.library.tool.image.ImageUtil;
import cc.shinichi.library.tool.ui.ToastUtil;
import cc.shinichi.library.view.helper.ImageSource;
import cc.shinichi.library.view.helper.SubsamplingScaleImageViewDragClose;
import cc.shinichi.library.view.listener.OnDownloadClickListener;
import cc.shinichi.library.view.photoview.PhotoView;

@SwipeBack(value = true)
public class MySettingMyPhotoAct extends SlbBase implements Ffile1View, Fconfig1View, FtipsView, FgrxxView {

    private static final String TAG = "MySettingMyPhotoAct";
    private String imageUrl;
    private HashMap<String, SubsamplingScaleImageViewDragClose> imageHashMap = new HashMap<>();
    private HashMap<String, PhotoView> imageGifHashMap = new HashMap<>();
    private String finalLoadUrl = "";

    private Ffile1Presenter ffile1Presenter;
    private Fconfig1Presenter fconfig1Presenter;
    private FtipsPresenter ftipsPresenter;
    private FgrxxPresenter fgrxxPresenter;

    private String url;
    private String url1;

    private AuthorStatus needAuthorConfig = AuthorStatus.DEFAULT;
    private BasePopupView popupView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mysetting_photoview;
    }

    @Override
    public void onResume() {
        // 从缓存中拿出头像bufen
        if (fconfig1Presenter != null) {
//            fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "authorized");
            fconfig1Presenter.getconfig1(AppCommonUtils.auth_url, "resource");
        }

        super.onResume();
    }

//    @Override
//    protected void refreshToken() {
//        if (fgrxxPresenter != null) {
//            fgrxxPresenter.getgrxx(url1);
//        }
//    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {

        fconfig1Presenter = new Fconfig1Presenter();
        fconfig1Presenter.onCreate(this);

        ffile1Presenter = new Ffile1Presenter();
        ffile1Presenter.onCreate(this);

        ftipsPresenter = new FtipsPresenter();
        ftipsPresenter.onCreate(this);

        fgrxxPresenter = new FgrxxPresenter();
        fgrxxPresenter.onCreate(this);

        imageUrl = getIntent().getStringExtra("imageUrl");

        TextView tvLeft = findViewById(R.id.tv_left);
        TextView tvCenter = findViewById(R.id.tv_center);
        TextView tvRight = findViewById(R.id.tv_right2);
        tvRight.setVisibility(View.VISIBLE);
        tvLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                onBackPressed();
            }
        });
        tvRight.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                uploadPhoto();
            }
        });

        tvCenter.setText("个人头像");

        final ProgressBar progressBar = findViewById(R.id.progress_view);
        final SubsamplingScaleImageViewDragClose imageView = findViewById(R.id.photo_view);
        final PhotoView imageGif = findViewById(R.id.gif_view);

        final String originPathUrl = imageUrl;

        imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_CENTER_INSIDE);
        imageView.setDoubleTapZoomStyle(SubsamplingScaleImageViewDragClose.ZOOM_FOCUS_CENTER);
        imageView.setDoubleTapZoomDuration(ImagePreview.getInstance().getZoomTransitionDuration());
        imageView.setMinScale(ImagePreview.getInstance().getMinScale());
        imageView.setMaxScale(ImagePreview.getInstance().getMaxScale());
        imageView.setDoubleTapZoomScale(ImagePreview.getInstance().getMediumScale());

        imageGif.setZoomTransitionDuration(ImagePreview.getInstance().getZoomTransitionDuration());
        imageGif.setMinimumScale(ImagePreview.getInstance().getMinScale());
        imageGif.setMaximumScale(ImagePreview.getInstance().getMaxScale());
        imageGif.setScaleType(ImageView.ScaleType.FIT_CENTER);


        imageGifHashMap.remove(originPathUrl);
        imageGifHashMap.put(originPathUrl + "_0", imageGif);

        imageHashMap.remove(originPathUrl);
        imageHashMap.put(originPathUrl + "_0", imageView);

        finalLoadUrl = originPathUrl;
        finalLoadUrl = finalLoadUrl.trim();
        final String url = finalLoadUrl;

        // 显示加载圈圈
        progressBar.setVisibility(View.VISIBLE);

        // 判断原图缓存是否存在，存在的话，直接显示原图缓存，优先保证清晰。
        File cacheFile = ImageLoader.getGlideCacheFile(MySettingMyPhotoAct.this, originPathUrl);
        if (cacheFile != null && cacheFile.exists()) {
            String imagePath = cacheFile.getAbsolutePath();
            boolean isStandardImage = ImageUtil.INSTANCE.isStandardImage(originPathUrl, imagePath);
            if (isStandardImage) {
                loadImageStandard(imagePath, imageView, imageGif, progressBar);
            } else {
                loadImageSpec(url, imagePath, imageView, imageGif, progressBar);
            }
        } else {
            Glide.with(MySettingMyPhotoAct.this).downloadOnly().load(url).addListener(new RequestListener<File>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target,
                                            boolean isFirstResource) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String fileFullName = String.valueOf(System.currentTimeMillis());
                            String saveDir = FileUtil.Companion.getAvailableCacheDir(MySettingMyPhotoAct.this).getAbsolutePath() + File.separator + "image/";
                            File downloadFile = HttpUtil.INSTANCE.downloadFile(url, fileFullName, saveDir);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (downloadFile != null && downloadFile.exists() && downloadFile.length() > 0) {
                                        // 通过urlConn下载完成
                                        loadSuccess(originPathUrl, downloadFile, imageView, imageGif, progressBar);
                                    } else {
                                        loadFailed(imageView, imageGif, progressBar, e);
                                    }
                                }
                            });
                        }
                    }).start();
                    return true;
                }

                @Override
                public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource,
                                               boolean isFirstResource) {
                    loadSuccess(url, resource, imageView, imageGif, progressBar);
                    return true;
                }
            }).into(new FileTarget() {
                @Override
                public void onLoadStarted(@Nullable Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                }
            });
        }
    }

    private void loadImageStandard(final String imagePath, final SubsamplingScaleImageViewDragClose imageView,
                                   final ImageView imageGif, final ProgressBar progressBar) {

        imageGif.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);

        setImageSpec(imagePath, imageView);

        imageView.setOrientation(SubsamplingScaleImageViewDragClose.ORIENTATION_USE_EXIF);
        ImageSource imageSource = ImageSource.uri(Uri.fromFile(new File(imagePath)));
        if (ImageUtil.INSTANCE.isBmpImageWithMime(imagePath, imagePath)) {
            imageSource.tilingDisabled();
        }
        imageView.setImage(imageSource);
        imageView.setOnImageEventListener(new SubsamplingScaleImageViewDragClose.OnImageEventListener() {
            @Override
            public void onReady() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onImageLoaded() {

            }

            @Override
            public void onPreviewLoadError(Exception e) {

            }

            @Override
            public void onImageLoadError(Exception e) {

            }

            @Override
            public void onTileLoadError(Exception e) {

            }

            @Override
            public void onPreviewReleased() {

            }
        });
//        imageView.setOnImageEventListener(new SimpleOnImageEventListener() {
//            @Override
//            public void onReady() {
//                progressBar.setVisibility(View.GONE);
//            }
//        });
    }

    private void loadImageSpec(final String imageUrl, final String imagePath, final SubsamplingScaleImageViewDragClose imageView,
                               final ImageView imageSpec, final ProgressBar progressBar) {

        imageSpec.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);

        boolean isGifFile = ImageUtil.INSTANCE.isGifImageWithMime(imageUrl, imagePath);
        if (isGifFile) {
            Glide.with(MySettingMyPhotoAct.this)
                    .asGif()
                    .load(imagePath)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).error(ImagePreview.getInstance().getErrorPlaceHolder()))
                    .listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target,
                                                    boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            imageSpec.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setImage(ImageSource.resource(ImagePreview.getInstance().getErrorPlaceHolder()));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageSpec);
        } else {
            Glide.with(MySettingMyPhotoAct.this)
                    .load(imageUrl)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).error(ImagePreview.getInstance().getErrorPlaceHolder()))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            imageSpec.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setImage(ImageSource.resource(ImagePreview.getInstance().getErrorPlaceHolder()));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageSpec);
        }
    }


    private void setImageSpec(final String imagePath, final SubsamplingScaleImageViewDragClose imageView) {
        boolean isLongImage = ImageUtil.INSTANCE.isLongImage(MySettingMyPhotoAct.this, imagePath);
        if (isLongImage) {
            imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_START);
            imageView.setMinScale(ImageUtil.INSTANCE.getLongImageMinScale(MySettingMyPhotoAct.this, imagePath));
            imageView.setMaxScale(ImageUtil.INSTANCE.getLongImageMaxScale(MySettingMyPhotoAct.this, imagePath));
            imageView.setDoubleTapZoomScale(ImageUtil.INSTANCE.getLongImageMaxScale(MySettingMyPhotoAct.this, imagePath));
        } else {
            boolean isWideImage = ImageUtil.INSTANCE.isWideImage(MySettingMyPhotoAct.this, imagePath);
            boolean isSmallImage = ImageUtil.INSTANCE.isSmallImage(MySettingMyPhotoAct.this, imagePath);
            if (isWideImage) {
                imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_CENTER_INSIDE);
                imageView.setMinScale(ImagePreview.getInstance().getMinScale());
                imageView.setMaxScale(ImagePreview.getInstance().getMaxScale());
                imageView.setDoubleTapZoomScale(ImageUtil.INSTANCE.getWideImageDoubleScale(MySettingMyPhotoAct.this, imagePath));
            } else if (isSmallImage) {
                imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_CUSTOM);
                imageView.setMinScale(ImageUtil.INSTANCE.getSmallImageMinScale(MySettingMyPhotoAct.this, imagePath));
                imageView.setMaxScale(ImageUtil.INSTANCE.getSmallImageMaxScale(MySettingMyPhotoAct.this, imagePath));
                imageView.setDoubleTapZoomScale(ImageUtil.INSTANCE.getSmallImageMaxScale(MySettingMyPhotoAct.this, imagePath));
            } else {
                imageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_CENTER_INSIDE);
                imageView.setMinScale(ImagePreview.getInstance().getMinScale());
                imageView.setMaxScale(ImagePreview.getInstance().getMaxScale());
                imageView.setDoubleTapZoomScale(ImagePreview.getInstance().getMediumScale());
            }
        }
    }


    private void loadFailed(SubsamplingScaleImageViewDragClose imageView, ImageView imageGif, ProgressBar progressBar,
                            GlideException e) {
        progressBar.setVisibility(View.GONE);
        imageGif.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);

        imageView.setZoomEnabled(false);
        imageView.setImage(ImageSource.resource(ImagePreview.getInstance().getErrorPlaceHolder()));

        if (ImagePreview.getInstance().isShowErrorToast()) {
            String errorMsg = MySettingMyPhotoAct.this.getString(R.string.toast_load_failed);
            if (e != null) {
                errorMsg = errorMsg.concat(":\n").concat(e.getMessage());
            }
            if (errorMsg.length() > 200) {
                errorMsg = errorMsg.substring(0, 199);
            }
            ToastUtil.getInstance().showShort(MySettingMyPhotoAct.this.getApplicationContext(), errorMsg);
        }
    }

    private void loadSuccess(String imageUrl, File resource, SubsamplingScaleImageViewDragClose imageView, ImageView imageGif,
                             ProgressBar progressBar) {
        String imagePath = resource.getAbsolutePath();
        boolean isStandardImage = ImageUtil.INSTANCE.isStandardImage(imageUrl, imagePath);
        if (isStandardImage) {
            loadImageStandard(imagePath, imageView, imageGif, progressBar);
        } else {
            loadImageSpec(imageUrl, imagePath, imageView, imageGif, progressBar);
        }
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

        String originUrl = imageUrl + "_0";
        try {
            if (imageHashMap != null) {
                SubsamplingScaleImageViewDragClose imageViewDragClose = imageHashMap.get(originUrl);
                if (imageViewDragClose != null) {
                    imageViewDragClose.resetScaleAndCenter();
                    imageViewDragClose.destroyDrawingCache();
                    imageViewDragClose.recycle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (imageGifHashMap != null) {
                PhotoView photoView = imageGifHashMap.get(originUrl);
                if (photoView != null) {
                    photoView.destroyDrawingCache();
                    photoView.setImageBitmap(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ImageLoader.clearMemory(MySettingMyPhotoAct.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (imageHashMap != null && imageHashMap.size() > 0) {
                for (Object o : imageHashMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    if (entry != null && entry.getValue() != null) {
                        ((SubsamplingScaleImageViewDragClose) entry.getValue()).destroyDrawingCache();
                        ((SubsamplingScaleImageViewDragClose) entry.getValue()).recycle();
                    }
                }
                imageHashMap.clear();
            }
            if (imageGifHashMap != null && imageGifHashMap.size() > 0) {
                for (Object o : imageGifHashMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    if (entry != null && entry.getValue() != null) {
                        ((PhotoView) entry.getValue()).destroyDrawingCache();
                        ((PhotoView) entry.getValue()).setImageBitmap(null);
                    }
                }
                imageGifHashMap.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadPhoto() {
        // ...
        BottomListPopupView1 bottomListPopupView1 = new BottomListPopupView1(MySettingMyPhotoAct.this, 0, 0);
        bottomListPopupView1.setStringData("", new String[]{"拍照", "从手机相册选择", "保存图片"}, null);
        bottomListPopupView1.setXpopOnSelectListener(new XpopOnSelectListener() {
            @Override
            public void onSelect(View textView, int position, String text) {
                if (TextUtils.equals("拍照", text)) {
                    // 单独拍照
                    PictureSelector.create(MySettingMyPhotoAct.this)
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

                                    MProgressDialogUtils.showMprogressDialog(MySettingMyPhotoAct.this, "请稍等...");

                                    imageUrl = result.get(0).getAvailablePath();
                                    popupView.dismiss();
                                    ffile1Presenter.getfile1(url, new File(imageUrl));
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                    Log.d("TAG", "onLongClick: ");
                }
                if (TextUtils.equals("从手机相册选择", text)) {
                    // 进入相册 以下是例子：不需要的api可以不写
                    PictureSelector.create(MySettingMyPhotoAct.this)
                            .openGallery(SelectMimeType.ofImage())
                            .setImageEngine(GlideEngine.createGlideEngine())
                            .setSelectionMode(SelectModeConfig.SINGLE)
                            .setCropEngine(new ImageLoaderUtils.ImageFileCropEngine())
                            .setRecyclerAnimationMode(AnimationType.DEFAULT_ANIMATION)// 列表动画效果
                            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                            .isPreviewImage(true)// 是否可预览图片
                            .isPreviewVideo(false)// 是否可预览视频
                            .isGif(true)// 是否显示gif图片
                            .isOpenClickSound(false)// 是否开启点击声音
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

                                    MProgressDialogUtils.showMprogressDialog(MySettingMyPhotoAct.this, "请稍等...");

                                    imageUrl = result.get(0).getAvailablePath();
                                    popupView.dismiss();
                                    ffile1Presenter.getfile1(url, new File(imageUrl));

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
                            checkAndDownload(MySettingMyPhotoAct.this);
                        }
                        ImagePreview.getInstance().getDownloadClickListener().onClick(MySettingMyPhotoAct.this, textView, ImagePreview.getInstance().getIndex());
                    } else {
                        checkAndDownload(MySettingMyPhotoAct.this);
                    }
                }
            }
        });

        bottomListPopupView1.setXpopOnCancelListener(new XpopOnCancelListener() {
            @Override
            public void onCancel(View textView) {
                MySettingMyPhotoAct.this.finish();
            }
        });

        if (popupView != null) {
            return;
        }

        popupView = new XPopup.Builder(MySettingMyPhotoAct.this)
                .autoDismiss(false)
                .asCustom(bottomListPopupView1)
                .show();
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
                    downloadCurrentImg(MySettingMyPhotoAct.this);
                } else {
                    ToastUtil.getInstance().showShort(MySettingMyPhotoAct.this, getString(cc.shinichi.library.R.string.toast_deny_permission_save_failed));
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

    //------------------------------接口回调分界线--------------------------------

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
    public void Onfile1Success(FcomBean bean) {
        imageUrl = bean.getUrl();
//        ftipsPresenter.gettips_img1(url, ImgUrl);
        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
        if (fgrxxBean != null) {
            //修改头像
            ftipsPresenter.gettips_img2(url, fgrxxBean.getId(), imageUrl, "", "", "", "");
        }
    }

    @Override
    public void Onfile1Nodata(String bean) {
        MProgressDialog.dismissProgress();
    }

    @Override
    public void Onfile1Fail(String msg) {
        MProgressDialog.dismissProgress();
    }

    @Override
    public void OntipsSuccess(String bean) {
        if (url1 != null && !TextUtils.isEmpty(url1) && fgrxxPresenter != null) {
            fgrxxPresenter.getgrxx(url1);
        }
    }

    @Override
    public void OntipsNodata(String bean) {
        MProgressDialog.dismissProgress();
        ToastUtils.showLong(bean);
    }

    @Override
    public void OntipsFail(String msg) {
        MProgressDialog.dismissProgress();
        ToastUtils.showLong(msg);
    }

    @Override
    public void OngrxxSuccess(FgrxxBean bean) {
        MProgressDialog.dismissProgress();
        ToastUtils.showLong("修改成功");
        MmkvUtils.getInstance().set_common_json2(AppCommonUtils.userInfo, bean);
        Intent intent = new Intent();
        intent.setAction("refreshAction");
        LocalBroadcastManagers.getInstance(this).sendBroadcast(intent);
        finish();
    }

    @Override
    public void OngrxxNodata(String bean) {
        MProgressDialog.dismissProgress();
        ToastUtils.showShort(bean);
    }

    @Override
    public void OngrxxFail(String msg) {
        MProgressDialog.dismissProgress();
        ToastUtils.showShort(msg);
    }
}
