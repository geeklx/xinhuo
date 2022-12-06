/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxing.journeyapps.barcodescanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.zxing.R;
import com.zxing.UtlisX;

import java.util.ArrayList;
import java.util.List;


/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public class ViewfinderView extends View {
    protected static final String TAG = ViewfinderView.class.getSimpleName();

    protected static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    protected static final long ANIMATION_DELAY = 80L;
    protected static final int CURRENT_POINT_OPACITY = 0xA0;
    protected static final int MAX_RESULT_POINTS = 20;
    protected static final int POINT_SIZE = 6;

    protected final Paint paint;
    protected Bitmap resultBitmap;
    protected final int maskColor;
    protected final int resultColor;
    protected final int laserColor;
    protected final int resultPointColor;
    protected int scannerAlpha;
    protected List<ResultPoint> possibleResultPoints;
    protected List<ResultPoint> lastPossibleResultPoints;
    protected CameraPreview cameraPreview;

    // Cache the framingRect and previewFramingRect, so that we can still draw it after the preview
    // stopped.
    protected Rect framingRect;
    protected Rect previewFramingRect;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        customInit(context);
        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Resources resources = getResources();

        // Get setted attributes on view
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.zxing_finder);

        this.maskColor = attributes.getColor(R.styleable.zxing_finder_zxing_viewfinder_mask,
                resources.getColor(R.color.zxing_viewfinder_mask));
        this.resultColor = attributes.getColor(R.styleable.zxing_finder_zxing_result_view,
                resources.getColor(R.color.zxing_result_view));
        this.laserColor = attributes.getColor(R.styleable.zxing_finder_zxing_viewfinder_laser,
                resources.getColor(R.color.zxing_viewfinder_laser));
        this.resultPointColor = attributes.getColor(R.styleable.zxing_finder_zxing_possible_result_points,
                resources.getColor(R.color.zxing_possible_result_points));

        attributes.recycle();

        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<>(MAX_RESULT_POINTS);
        lastPossibleResultPoints = new ArrayList<>(MAX_RESULT_POINTS);
    }

    public void setCameraPreview(CameraPreview view) {
        this.cameraPreview = view;
        view.addStateListener(new CameraPreview.StateListener() {
            @Override
            public void previewSized() {
                refreshSizes();
                invalidate();
            }

            @Override
            public void previewStarted() {

            }

            @Override
            public void previewStopped() {

            }

            @Override
            public void cameraError(Exception error) {

            }

            @Override
            public void cameraClosed() {

            }
        });
    }

    protected void refreshSizes() {
        if(cameraPreview == null) {
            return;
        }
        Rect framingRect = cameraPreview.getFramingRect();
        Rect previewFramingRect = cameraPreview.getPreviewFramingRect();
        if(framingRect != null && previewFramingRect != null) {
            this.framingRect = framingRect;
            this.previewFramingRect = previewFramingRect;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        refreshSizes();
        if (framingRect == null || previewFramingRect == null) {
            return;
        }

        final Rect frame = framingRect;
        final Rect previewFrame = previewFramingRect;

        customDraw(frame,canvas);
        final int width = canvas.getWidth();
        final int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {

            // Draw a red "laser scanner" line through the middle to show decoding is active
//            paint.setColor(laserColor);
//            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
//            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
//            final int middle = frame.height() / 2 + frame.top;
//            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

            final float scaleX = frame.width() / (float) previewFrame.width();
            final float scaleY = frame.height() / (float) previewFrame.height();

            final int frameLeft = frame.left;
            final int frameTop = frame.top;

            // draw the last possible result points
            if (!lastPossibleResultPoints.isEmpty()) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                float radius = POINT_SIZE / 2.0f;
                for (final ResultPoint point : lastPossibleResultPoints) {
                    canvas.drawCircle(
                            frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            radius, paint
                    );
                }
                lastPossibleResultPoints.clear();
            }

            // draw current possible result points
            if (!possibleResultPoints.isEmpty()) {
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                for (final ResultPoint point : possibleResultPoints) {
                    canvas.drawCircle(
                            frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            POINT_SIZE, paint
                    );
                }

                // swap and clear buffers
                final List<ResultPoint> temp = possibleResultPoints;
                possibleResultPoints = lastPossibleResultPoints;
                lastPossibleResultPoints = temp;
                possibleResultPoints.clear();
            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param result An image of the result.
     */
    public void drawResultBitmap(Bitmap result) {
        resultBitmap = result;
        invalidate();
    }

    /**
     * Only call from the UI thread.
     *
     * @param point a point to draw, relative to the preview frame
     */
    public void addPossibleResultPoint(ResultPoint point) {
        if (possibleResultPoints.size() < MAX_RESULT_POINTS)
            possibleResultPoints.add(point);
    }




    /*-----------------------自定义方法和属性--------------------------*/
    //画边框相关属性
    private Paint mLinePaint;//边框蓝色拐角画笔
    private final int mLineColor = Color.parseColor("#0196FF");//边框的颜色

    private Paint mLinePaintW;//白色边框画笔
    private final int mLineColorW = Color.parseColor("#ffffff");//边框的颜色
    //滑动条相关属性
    private Bitmap mLineBm;//滑动条图片
    private RectF mLineReact;//滑动条区域
    private final int mStepSize = 12;//滑动条每次滑动的速度
    private final int mLineHeight = 30;//滑动条的高度
    private boolean isBottom = false;//滑动条是否滑动到扫码框底部

    //文字相关属性
    private Paint mTextPaint;//画提示语的画笔
    private String mPromptText;//扫码的提示语
    private int mTextMargin;//提示语距离扫描框的大小


    /**
     * 改方法在构造方法中调用用来初始化属性
     *
     * @param context
     */
    private void customInit(Context context) {
        //初始化滑动线的画笔
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(10);
        mLinePaint.setColor(mLineColor);

        mLinePaintW = new Paint();
        mLinePaintW.setStyle(Paint.Style.FILL);
        mLinePaintW.setStrokeWidth(3);
        mLinePaintW.setColor(mLineColorW);

        //初始化滑动条
        mLineBm = BitmapFactory.decodeResource(getResources(),Color.parseColor("#0196FF") );
        //初始化提示语的画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.parseColor("#ffffff"));
        mTextPaint.setTextSize(getContext().getResources().getDimension(R.dimen.scan_text_size));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextMargin = (int) getContext().getResources().getDimension(R.dimen.scan_text_margin);
    }


    /**
     * 该方法在onDraw中调用，放在
     * Rect frame = framingRect;
     * Rect previewFrame = previewFramingRect;
     * 两段代码之后
     *
     * @param frame
     * @param canvas
     */
    private void customDraw(Rect frame, Canvas canvas) {
//        drawSlipLine(frame, canvas);//画滑动的线
        drawEdge(frame, canvas);//画边框
        drawPromptText(frame, canvas);//画提示语
    }


    /**
     * 画框边的四个角
     *
     * @param frame
     * @param canvas
     */
    private void drawEdge(Rect frame, Canvas canvas) {
        canvas.drawRect(frame.left, frame.top, frame.left + 10, frame.top + 70, mLinePaint);
        canvas.drawRect(frame.left , frame.top , frame.left + 70, frame.top + 10, mLinePaint);


        canvas.drawRect(frame.right - 70, frame.top , frame.right , frame.top + 10, mLinePaint);
        canvas.drawRect(frame.right - 10, frame.top, frame.right, frame.top + 70, mLinePaint);


        canvas.drawRect(frame.left, frame.bottom - 70, frame.left + 10, frame.bottom, mLinePaint);
        canvas.drawRect(frame.left, frame.bottom - 10, frame.left + 70, frame.bottom, mLinePaint);

        canvas.drawRect(frame.right - 70, frame.bottom - 10, frame.right, frame.bottom, mLinePaint);
        canvas.drawRect(frame.right - 10, frame.bottom - 70, frame.right ,frame.bottom, mLinePaint);


//        canvas.drawLine(frame.left,frame.top,frame.left + 70,frame.top,mLinePaint);
//        canvas.drawLine(frame.left,frame.top,frame.left,frame.top + 70,mLinePaint);
//
//        canvas.drawLine(frame.right, frame.top, frame.right, frame.top + 70,mLinePaint);
//        canvas.drawLine(frame.right, frame.top, frame.right - 70, frame.top, mLinePaint);
//
//        canvas.drawLine(frame.left, frame.bottom,frame.left + 70, frame.bottom,mLinePaint);
//        canvas.drawLine(frame.left, frame.bottom,frame.left, frame.bottom - 70,mLinePaint);
//
//        canvas.drawLine(frame.right, frame.bottom, frame.right - 70, frame.bottom,mLinePaint);
//        canvas.drawLine(frame.right, frame.bottom, frame.right, frame.bottom - 70,mLinePaint);

        canvas.drawLine(frame.left + 70,frame.top + 1,frame.right-70,frame.top + 1,mLinePaintW);
        canvas.drawLine(frame.right - 1,frame.top + 70,frame.right - 1,frame.bottom - 70,mLinePaintW);
        canvas.drawLine(frame.left + 70,frame.bottom - 1,frame.right-70,frame.bottom - 1,mLinePaintW);
        canvas.drawLine(frame.left + 1,frame.top + 70,frame.left + 1,frame.bottom - 70,mLinePaintW);

    }

    /**
     * 传入提示语
     *
     * @param text
     */
    public void setPromptText(String text) {
        this.mPromptText = text;
    }

    /**
     * 画提示语
     *
     * @param frame
     * @param canvas
     */
    private void drawPromptText(Rect frame, Canvas canvas) {
        int startX = frame.left + frame.width() / 2;
        int startY = frame.top - 30;
        if (!TextUtils.isEmpty(mPromptText)) {
            canvas.drawText(mPromptText, startX, startY, mTextPaint);
        }
    }


}
