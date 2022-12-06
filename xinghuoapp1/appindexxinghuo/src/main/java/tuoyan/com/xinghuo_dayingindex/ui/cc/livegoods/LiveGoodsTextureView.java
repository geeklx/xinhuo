package tuoyan.com.xinghuo_dayingindex.ui.cc.livegoods;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

/**
 * 用于显示video的，
 */

public class LiveGoodsTextureView extends TextureView {
    private static final String TAG = "ResizeTextureView";
    private int mVideoWidth;
    private int mVideoHeight;

    public LiveGoodsTextureView(Context context) {
        super(context);
    }

    public LiveGoodsTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setVideoSize(int width, int height) {
        if (mVideoWidth!=width||mVideoHeight!=height){
            mVideoWidth = width;
            mVideoHeight = height;
            Log.i(TAG, "setVideoSize: width:"+width+" height:"+height);
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
//        Resources resources = this.getResources();
//        DisplayMetrics dm = resources.getDisplayMetrics();
//        int screenWidth = dm.widthPixels;
//        int screenHeight = dm.heightPixels;
//        Log.i(TAG, "onMeasure0: width:"+mVideoWidth+" height:"+mVideoHeight);
//        Log.d(TAG, "onMeasure1: "+widthMeasureSpec+"height"+heightMeasureSpec+"screenWidth"+screenWidth+"screenHeight"+screenHeight);
//        if (mVideoWidth == 0 || mVideoHeight == 0) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        }
//        if (screenWidth == 0 || screenHeight == 0) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        }
//
//        if (getRotation() == 90 || getRotation() == 270) { // 软解码时处理旋转信息，交换宽高
//            widthMeasureSpec = widthMeasureSpec + heightMeasureSpec;
//            heightMeasureSpec = widthMeasureSpec - heightMeasureSpec;
//            widthMeasureSpec = widthMeasureSpec - heightMeasureSpec;
//        }
//        int width = getDefaultSize(widthMeasureSpec, widthMeasureSpec);
//        int height = getDefaultSize(heightMeasureSpec, heightMeasureSpec);
//        Log.d(TAG, "onMeasure2: "+width+"height"+height);
//        if (mVideoWidth > 0 && mVideoHeight > 0) {
//            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
//            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
//            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
//            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
//
//            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
//                width = widthSpecSize;
//                height = heightSpecSize;
//                if (mVideoWidth * height < width * mVideoHeight) {
//                    width = height * mVideoWidth / mVideoHeight;
//                } else if (mVideoWidth * height > width * mVideoHeight) {
//                    height = width * mVideoHeight / mVideoWidth;
//                }
//            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
//                width = widthSpecSize;
//                height = width * mVideoHeight / mVideoWidth;
//                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
//                    height = heightSpecSize;
//                }
//            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
//                height = heightSpecSize;
//                width = height * mVideoWidth / mVideoHeight;
//                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
//                    width = widthSpecSize;
//                }
//            } else {
//                width = mVideoWidth;
//                height = mVideoHeight;
//                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
//                    height = heightSpecSize;
//                    width = height * mVideoWidth / mVideoHeight;
//                }
//                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
//                    width = widthSpecSize;
//                    height = width * mVideoHeight / mVideoWidth;
//                }
//            }
//        }
//        Log.d(TAG, "onMeasure3: "+width+"height"+height);
//        setMeasuredDimension(1080, 2276);
    }
}