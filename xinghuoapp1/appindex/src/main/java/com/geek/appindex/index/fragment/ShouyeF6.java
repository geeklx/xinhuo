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
    private FgrxxPresenter fgrxxPresenter;//??????????????????
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
            //????????????
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
        //????????????author?????????
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

    /*??????????????????????????????*/
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
    /*??????????????????????????????*/

    public class MessageReceiverIndex extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if ("ShouyeF6".equals(intent.getAction())) {
                    //TODO ????????????bufen
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
        // ????????????????????????bufen
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
//                ToastUtils.showLong("????????????");
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
        // ?????????
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bitmap?????????byte[]
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

        // ????????????
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.MyPersonalDataAct"));
            }
        });
        // ??????
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new XPopup.Builder(getContext())
//                        .maxWidth((int) (XPopupUtils.getWindowWidth(getActivity()) * 0.8f))
//                        .hasStatusBarShadow(false)
//                        //.dismissOnBackPressed(false)
//                        .isDestroyOnDismiss(true) //???????????????????????????????????????????????????
//                        .autoOpenSoftInput(true)
//                        .isDarkTheme(false)
//                        .setPopupCallback(new DemoXPopupListener())
////                        .autoFocusEditText(false) //?????????????????????EditText??????????????????????????????true
//                        //.moveUpToKeyboard(false)   //??????????????????????????????????????????true
//                        .asInputConfirm("????????????", null, null, "??????????????????",
//                                new OnInputConfirmListener() {
//                                    @Override
//                                    public void onConfirm(String text) {
////                                new XPopup.Builder(getContext()).asLoading().show();
//                                        if (text != null && !"".equals(text)) {
//                                            tv5.setText(text);
//                                            FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
//                                            if (fgrxxBean != null) {
//                                                if (text != null && !"".equals(text)) {
//                                                    //????????????
//                                                    ftipsPresenter.gettips_img2(url, fgrxxBean.getId(), "", text, "", "", "");
//                                                } else {
//                                                    ToastUtils.showLong("?????????????????????");
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
        // ????????????
        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(url1)) {
                    url1 = "https://www.baidu.com/";
                }
                HiosHelperNew.resolveAd(getActivity(), getActivity(), url1);
            }
        });
        // ??????/??????
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
        // ????????????
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
        // ??????
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
        // ?????????
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
        //??????
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
//        // ??????
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
        // ?????????????????????
        ImagePreview.getInstance()
                // ?????????????????????activity?????????????????????????????????????????????????????????
                .setContext(getActivity())
                // ????????????????????????????????????0?????????~
                .setIndex(0)

                //=================================================================================================
                // ??????????????????????????????????????????????????????????????????????????????
                // 1?????????????????????imageInfo List
//                        .setImageInfoList(imageInfoList)

                // 2????????????url List
                //.setImageList(List<String> imageList)

                // 3??????????????????????????????????????????????????????????????????url
                .setImage(ImgUrl)
                //=================================================================================================

                // ????????????????????????????????????
//                        .setLoadStrategy(loadStrategy)

                // ?????????????????????????????????Picture??????????????????????????????????????????"BigImageView"?????????Picture????????????BigImageView?????????)
                .setFolderName("GeekApp")

                // ???????????????????????????ms
                .setZoomTransitionDuration(300)

                // ???????????????????????????Toast
                .setShowErrorToast(true)

                // ?????????????????????????????????????????????
                .setEnableClickClose(true)
                // ??????????????????????????????????????????
                .setEnableDragClose(true)
                // ??????????????????????????????????????????
                .setEnableUpDragClose(true)
                // ??????????????????????????????????????????????????????
//                        .setEnableDragCloseIgnoreScale(enableDragIgnoreScale)

                // ?????????????????????????????????????????????????????????????????????
//                        .setShowCloseButton(showCloseButton)
                // ?????????????????????????????????????????????????????????????????????R.drawable.ic_action_close
                .setCloseIconResId(R.drawable.ic_action_close)

                // ????????????????????????????????????????????????????????????
                .setShowDownButton(false)
                // ?????????????????????????????????????????????????????????????????????R.drawable.icon_download_new
                .setDownIconResId(R.drawable.icon_download_new)

                // ???????????????????????????????????????1/9???????????????
                .setShowIndicator(false)
                // ???????????????????????????shape???????????????????????????shape
                .setIndicatorShapeResId(R.drawable.shape_indicator_bg)

                // ???????????????????????????????????????????????????R.drawable.load_failed???????????? 0 ????????????
                .setErrorPlaceHolder(R.drawable.load_failed)

                // ????????????
                .setBigImageClickListener(new OnBigImageClickListener() {
                    @Override
                    public void onClick(Activity activity, View view, int position) {
                        // ...
                        Log.d("TAG", "onClick: ");
                    }
                })
                // ????????????
                .setBigImageLongClickListener(new OnBigImageLongClickListener() {
                    @Override
                    public boolean onLongClick(Activity activity, View view, int position) {
                        // ...
                        BottomListPopupView1 bottomListPopupView1 = new BottomListPopupView1(activity, 0, 0);
                        bottomListPopupView1.setStringData("", new String[]{"??????", "?????????????????????", "????????????"}, null);
                        bottomListPopupView1.setXpopOnSelectListener(new XpopOnSelectListener() {
                            @Override
                            public void onSelect(View textView, int position, String text) {
                                if (TextUtils.equals("??????", text)) {
                                    // ????????????
                                    PictureSelector.create(activity).openCamera(SelectMimeType.ofImage())// ?????????????????????????????????????????? ??????????????????????????????or??????
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
                                                        Log.i("TAG", "?????????: " + media.getFileName());
                                                        Log.i("TAG", "????????????:" + media.isCompressed());
                                                        Log.i("TAG", "??????:" + media.getCompressPath());
                                                        Log.i("TAG", "??????:" + media.getPath());
                                                        Log.i("TAG", "????????????:" + media.getRealPath());
                                                        Log.i("TAG", "????????????:" + media.isCut());
                                                        Log.i("TAG", "??????:" + media.getCutPath());
                                                        Log.i("TAG", "??????????????????:" + media.isOriginal());
                                                        Log.i("TAG", "????????????:" + media.getOriginalPath());
                                                        Log.i("TAG", "Android Q ??????Path:" + media.getAvailablePath());
                                                        Log.i("TAG", "??????: " + media.getWidth() + "x" + media.getHeight());
                                                        Log.i("TAG", "Size: " + media.getSize());

                                                        Log.i("TAG", "onResult: " + media.toString());

                                                        // TODO ????????????PictureSelectorExternalUtils.getExifInterface();??????????????????????????????????????????????????????????????????????????????
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
                                if (TextUtils.equals("?????????????????????", text)) {
                                    // ???????????? ??????????????????????????????api????????????
                                    PictureSelector.create(activity).openGallery(SelectMimeType.ofImage()).setImageEngine(GlideEngine.createGlideEngine()).setSelectionMode(SelectModeConfig.SINGLE).setCropEngine(new ImageLoaderUtils.ImageFileCropEngine()).isPreviewImage(true)// ?????????????????????
                                            .isPreviewVideo(false)// ?????????????????????
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
                                                        Log.i("TAG", "?????????: " + media.getFileName());
                                                        Log.i("TAG", "????????????:" + media.isCompressed());
                                                        Log.i("TAG", "??????:" + media.getCompressPath());
                                                        Log.i("TAG", "??????:" + media.getPath());
                                                        Log.i("TAG", "????????????:" + media.getRealPath());
                                                        Log.i("TAG", "????????????:" + media.isCut());
                                                        Log.i("TAG", "??????:" + media.getCutPath());
                                                        Log.i("TAG", "??????????????????:" + media.isOriginal());
                                                        Log.i("TAG", "????????????:" + media.getOriginalPath());
                                                        Log.i("TAG", "Android Q ??????Path:" + media.getAvailablePath());
                                                        Log.i("TAG", "??????: " + media.getWidth() + "x" + media.getHeight());
                                                        Log.i("TAG", "Size: " + media.getSize());

                                                        Log.i("TAG", "onResult: " + media.toString());

                                                        // TODO ????????????PictureSelectorExternalUtils.getExifInterface();??????????????????????????????????????????????????????????????????????????????
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
                                if (TextUtils.equals("????????????", text)) {
                                    OnDownloadClickListener downloadClickListener = ImagePreview.getInstance().getDownloadClickListener();
                                    if (downloadClickListener != null) {
                                        boolean interceptDownload = downloadClickListener.isInterceptDownload();
                                        if (interceptDownload) {
                                            // ?????????????????????????????????
                                        } else {
                                            // ??????????????????
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
                // ??????????????????
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
                // ?????????????????????????????????????????????????????????????????????????????????????????????
                .setDownloadClickListener(new OnDownloadClickListener() {
                    @Override
                    public void onClick(Activity activity, View view, int position) {
                        // ?????????????????????????????????????????????????????????????????????
                        Log.d("TAG", "onClick: position = " + position);
                    }

                    @Override
                    public boolean isInterceptDownload() {
                        // return true ???, ????????????????????????
                        // return false ???, ??????????????????
                        return false;
                    }
                })

                //=================================================================================================
                // ?????????????????????????????????????????????????????????????????????ImagePreview.PROGRESS_THEME_CIRCLE_TEXT??????????????????
                .setProgressLayoutId(ImagePreview.PROGRESS_THEME_CIRCLE_TEXT, new OnOriginProgressListener() {
                    @Override
                    public void progress(View parentView, int progress) {
                        Log.d("TAG", "progress: " + progress);

                        // ?????????????????????????????????????????????????????????parentView????????????????????????View????????????parentView???????????????
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

                // ????????????????????????????????????????????????????????????????????????????????????parentView?????????????????????????????????????????????
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

                // ????????????
                .start();
    }


    /**
     * ?????????????????????bufen
     */
    private void donetwork() {
        retryData();
    }

    // ??????
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
     * ????????????bufen
     *
     * @param cateId
     * @param isrefresh
     */
    @Override
    public void getCate(String cateId, boolean isrefresh) {

        if (!isrefresh) {
            // ????????????????????????bufen

            return;
        }
        ToastUtils.showLong("???????????????");
    }

    /**
     * ????????????????????????????????????fragment?????????id?????????bufen
     *
     * @param cateId
     */
    @Override
    public void give_id(String cateId) {
//        ToastUtils.showLong("???????????????");
        MyLogUtil.e("tablayoutId->", "give_id->" + cateId);
    }

    /**
     * --------------------------------?????????????????????----------------------------------
     */

    private void checkAndDownload(Activity activity) {
        // ????????????
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // ????????????
                ToastUtil.getInstance().showShort(activity, getString(cc.shinichi.library.R.string.toast_deny_permission_save_failed));
            } else {
                //????????????
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
            }
        } else {
            // ??????????????????
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
     * ?????????????????????SD???
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
