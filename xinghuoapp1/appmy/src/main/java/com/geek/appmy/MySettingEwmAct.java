package com.geek.appmy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.geek.appcommon.AppCommonUtils;
import com.geek.biz1.bean.FgrxxBean;
import com.geek.libbase.base.SlbBaseActivity;
import com.geek.libglide47.base.GlideImageView;
import com.geek.libutils.app.MyLogUtil;
import com.geek.libutils.data.MmkvUtils;
import com.geek.libswipebacklayout.SwipeBack;
import com.geek.zxinglibs3.saoma3zxing.encode.EncodingHandler;
import com.google.zxing.WriterException;
import com.lxj.xpopup.util.XPopupUtils;

import java.io.UnsupportedEncodingException;
@SwipeBack(value = true)
public class MySettingEwmAct extends SlbBaseActivity {

    private TextView tv_left;
    private TextView tv_center;
    private ImageView iv1;
    private GlideImageView iv2;
    private String content;
    private String content2;
    private TextView tv_name;
    private TextView tv_signature;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mysettingewm;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        //
//        BarUtils.setStatusBarLightMode(this, true);
        super.setup(savedInstanceState);
        tv_left = findViewById(R.id.tv_left);
        tv_center = findViewById(R.id.tv_center);
        tv_name = findViewById(R.id.tv_name);
        tv_signature = findViewById(R.id.tv_signature);
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        tv_left.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                onBackPressed();
            }
        });
        tv_center.setText(getApplication().getResources().getString(R.string.appmy20));
        content = getIntent().getStringExtra("erweima");
        content2 = getIntent().getStringExtra("erweima2");
        FgrxxBean fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
        if (fgrxxBean != null) {
            tv_name.setText(fgrxxBean.getName());
            tv_signature.setText(fgrxxBean.getOrgName());
            if (TextUtils.equals("0", fgrxxBean.getSex())) {
                iv2.loadImage(fgrxxBean.getPhoto(), R.drawable.icon_grxx1);
            } else {
                iv2.loadImage(fgrxxBean.getPhoto(), R.drawable.icon_grxx2);
            }
        }
        if (!TextUtils.isEmpty(content)) {
//            create2Code(content);
//            Bitmap bitmap = create2Code(content);
//            Bitmap bitmap2 = BitmapFactory.decodeByteArray(content2, 0, content2.length);
            getHeadBitmap((int) (XPopupUtils.getWindowWidth(MySettingEwmAct.this) * 0.1f));
        }
    }

    /**
     * 生成二维码
     *
     * @param key
     */
    private Bitmap create2Code(String key) {
        Bitmap qrCode = null;
        try {
            qrCode = EncodingHandler.create2Code(key, (int) (XPopupUtils.getWindowWidth(MySettingEwmAct.this) * 0.6f));
            iv1.setImageBitmap(qrCode);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return qrCode;
    }

    /**
     * 初始化头像图片
     */
    private void getHeadBitmap(int size) {
        try {
            // 这里采用从asset中加载图片abc.jpg
//            Bitmap portrait = BitmapFactory.decodeResource(getResources(), R.drawable.img03);
            MyLogUtil.e("sssssssssss", content2);
//            FutureTarget<Bitmap> futureTarget =
//                    Glide.with(getApplicationContext())
//                            .asBitmap()
//                            .load(content2)
//                            .submit(size, size);
//            Bitmap portrait = futureTarget.get();
            //
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(content2)
                    .into(new Target<Bitmap>() {

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onStop() {

                        }

                        @Override
                        public void onDestroy() {

                        }

                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {

                        }

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            //
                            Bitmap bitmap = create2Code(content);
                            // 对原有图片压缩显示大小
                            Matrix mMatrix = new Matrix();
                            float width = resource.getWidth();
                            float height = resource.getHeight();
                            mMatrix.setScale(size / width, size / height);
                            Bitmap headBitmap = Bitmap.createBitmap(resource, 0, 0, (int) width,
                                    (int) height, mMatrix, true);
                            if (bitmap != null && headBitmap != null) {
                                createQRCodeBitmapWithPortrait(bitmap, headBitmap);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }

                        @Override
                        public void getSize(@NonNull SizeReadyCallback cb) {
                            cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                        }

                        @Override
                        public void removeCallback(@NonNull SizeReadyCallback cb) {

                        }

                        @Override
                        public void setRequest(@Nullable Request request) {

                        }

                        @Nullable
                        @Override
                        public Request getRequest() {
                            return null;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在二维码上绘制头像
     */
    private void createQRCodeBitmapWithPortrait(Bitmap qr, Bitmap portrait) {
        // 头像图片的大小
        int portrait_W = portrait.getWidth();
        int portrait_H = portrait.getHeight();

        // 设置头像要显示的位置，即居中显示
        int left = (qr.getWidth() - portrait_W) / 2;
        int top = (qr.getHeight() - portrait_H) / 2;
        int right = left + portrait_W;
        int bottom = top + portrait_H;
        Rect rect1 = new Rect(left, top, right, bottom);

        // 取得qr二维码图片上的画笔，即要在二维码图片上绘制我们的头像
        Canvas canvas = new Canvas(qr);

        // 设置我们要绘制的范围大小，也就是头像的大小范围
        Rect rect2 = new Rect(0, 0, portrait_W, portrait_H);
        // 开始绘制
        canvas.drawBitmap(portrait, rect2, rect1, null);
    }
}
