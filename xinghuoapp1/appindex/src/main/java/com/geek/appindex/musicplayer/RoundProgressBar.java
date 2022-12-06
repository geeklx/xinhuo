package com.geek.appindex.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.geek.appindex.R;
import com.lzf.easyfloat.utils.DisplayUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RoundProgressBar extends View {
    private Paint circlePaint;
    private int circleColor;
    private float circleRadius;
    private Paint progressPaint;
    private int progressColor;
    private int progressTextColor;
    private float progressRadius;
    private float progressWidth;
    private Paint progressBgPaint;
    private int progressBgColor;
    private Paint progressTextPaint;
    private String progressStr;
    private float circleX;
    private float circleY;
    private float mTxtWidth;
    private float mTxtHeight;
    private final int mTotalProgress;
    private int mProgress;
    private int mWidth;
    private int mHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mWidth = this.getMySize(widthMeasureSpec, this.mWidth);
        this.mHeight = this.getMySize(heightMeasureSpec, this.mHeight);
    }

    private final int getMySize(int measureSpec, int defaultSize) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int var10000;
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                boolean var5 = false;
                var10000 = Math.min(defaultSize, specSize);
                break;
            case MeasureSpec.EXACTLY:
                var10000 = specSize;
                break;
            default:
                var10000 = defaultSize;
        }

        return var10000;
    }

    private final void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray var10000 = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TasksCompletedView, 0, 0);
            TypedArray typeArray = var10000;
            // 内圆
            circleColor = typeArray.getColor(R.styleable.TasksCompletedView_circleColor, circleColor);
            circleRadius = typeArray.getDimension(R.styleable.TasksCompletedView_radius, circleRadius);
            // 进度条
            progressWidth = typeArray.getDimension(R.styleable.TasksCompletedView_progressWidth, progressWidth);
            progressColor = typeArray.getColor(R.styleable.TasksCompletedView_progressColor, progressColor);
            progressTextColor = typeArray.getColor(R.styleable.TasksCompletedView_progressTextColor, progressTextColor);
            progressBgColor = typeArray.getColor(R.styleable.TasksCompletedView_progressBgColor, progressBgColor);
            progressRadius = circleRadius + progressWidth / 2;
        }
    }

    private final void initVariable() {
        this.circlePaint.setAntiAlias(true);
        this.circlePaint.setColor(this.circleColor);
        this.circlePaint.setStyle(Style.FILL);
        this.circlePaint.setXfermode((Xfermode) (new PorterDuffXfermode(Mode.SRC_OVER)));
        this.progressBgPaint.setAntiAlias(true);
        this.progressBgPaint.setColor(this.progressBgColor);
        this.progressBgPaint.setStyle(Style.STROKE);
        this.progressBgPaint.setStrokeWidth(this.progressWidth);
        this.progressPaint.setAntiAlias(true);
        this.progressPaint.setColor(this.progressColor);
        this.progressPaint.setStyle(Style.STROKE);
        this.progressPaint.setStrokeWidth(this.progressWidth);
        this.progressTextPaint.setAntiAlias(true);
        this.progressTextPaint.setStyle(Style.FILL);
        this.progressTextPaint.setColor(this.progressTextColor);
        this.progressTextPaint.setTextSize(this.circleRadius * 0.7F);
        this.progressTextPaint.setXfermode((Xfermode) (new PorterDuffXfermode(Mode.SRC_OVER)));
        FontMetrics fm = this.progressTextPaint.getFontMetrics();
        double var2 = (double) (fm.descent - fm.ascent);
        boolean var4 = false;
        this.mTxtHeight = (float) Math.ceil(var2);
    }

    @Override
    @SuppressLint({"DrawAllocation"})
    protected void onDraw(@NotNull Canvas canvas) {
        this.circleY = (float) this.mHeight * 0.5F;
        this.circleX = (float) this.mWidth * 0.5F;
        canvas.drawCircle(this.circleX, this.circleY, this.circleRadius, this.circlePaint);
        RectF rectF = new RectF(this.circleX - this.progressRadius, this.circleY - this.progressRadius, this.progressRadius * (float) 2 + (this.circleX - this.progressRadius), this.progressRadius * (float) 2 + (this.circleY - this.progressRadius));
        canvas.drawArc(rectF, 0.0F, 360.0F, false, this.progressBgPaint);
        canvas.drawArc(rectF, -90.0F, (float) (this.mProgress * 360 / this.mTotalProgress), false, this.progressPaint);
        this.mTxtWidth = this.progressTextPaint.measureText(this.progressStr, 0, this.progressStr.length());
        canvas.drawText(this.progressStr, this.circleX - this.mTxtWidth / (float) 2, this.circleY + this.mTxtHeight * 0.3F, this.progressTextPaint);
    }

    public final void setProgress(int progress) {
        this.mProgress = progress;
        this.postInvalidate();
    }

    public final void setProgress(int progress, @NotNull String progressStr) {
        this.mProgress = progress;
        this.progressStr = progressStr;
        this.postInvalidate();
    }

    public final int getProgress() {
        return this.mProgress;
    }

    @NotNull
    public final String getProgressStr() {
        return this.progressStr;
    }

    private final float dp2px(float dpValue) {
        DisplayUtils var10000 = DisplayUtils.INSTANCE;
        Context var10001 = this.getContext();
        return (float) var10000.dp2px(var10001, dpValue);
    }

    public RoundProgressBar(@NotNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.circlePaint = new Paint();
        this.circleColor = ContextCompat.getColor(context, R.color.smallCircle);
        this.circleRadius = this.dp2px(20.0F);
        this.progressPaint = new Paint();
        this.progressColor = ContextCompat.getColor(context, R.color.colorPrimary);
        this.progressTextColor = ContextCompat.getColor(context, R.color.colorPrimary);
        this.progressRadius = this.dp2px(21.0F);
        this.progressWidth = this.dp2px(2.0F);
        this.progressBgPaint = new Paint();
        this.progressBgColor = ContextCompat.getColor(context, R.color.progressBgColor);
        this.progressTextPaint = new Paint();
        this.progressStr = "";
        this.mTotalProgress = 100;
        this.mWidth = (int) this.dp2px(60.0F);
        this.mHeight = (int) this.dp2px(60.0F);
        new View(context, attrs);
        this.initAttrs(context, attrs);
        this.initVariable();
    }


}
