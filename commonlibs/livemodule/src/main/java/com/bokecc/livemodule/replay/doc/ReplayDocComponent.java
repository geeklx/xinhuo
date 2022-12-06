package com.bokecc.livemodule.replay.doc;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bokecc.livemodule.replay.DWReplayCoreHandler;
import com.bokecc.sdk.mobile.live.widget.DocView;

/**
 * 回放直播间文档展示控件
 */
public class ReplayDocComponent extends LinearLayout implements DocView.DocViewEventListener {

    private static final String TAG = "sdk_DocView";
    private Context mContext;
    private DocView mDocView;

    public ReplayDocComponent(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public ReplayDocComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        mDocView = new DocView(mContext);
        mDocView.setScrollable(false);
        mDocView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mDocView);

        DWReplayCoreHandler replayCoreHandler = DWReplayCoreHandler.getInstance();
        if (replayCoreHandler != null) {
            replayCoreHandler.setDocView(mDocView);
        }

        mDocView.setDocViewListener(this);
    }

    // 设置文档是否可滑动
    public void setDocScrollable(boolean isDocScrollable) {
        if (mDocView != null) {
            mDocView.setScrollable(isDocScrollable);
        }
    }

    // 设置文档的拉伸模式
    public void setScaleType(int type) {
        if (DocView.ScaleType.CENTER_INSIDE.ordinal() == type) {
            mDocView.setDocScaleType(DocView.ScaleType.CENTER_INSIDE);
        } else if (DocView.ScaleType.FIT_XY.ordinal() == type) {
            mDocView.setDocScaleType(DocView.ScaleType.FIT_XY);
        } else if (DocView.ScaleType.CROP_CENTER.ordinal() == type) {
            mDocView.setDocScaleType(DocView.ScaleType.CROP_CENTER);
        }
    }

    public DocView getmDocView() {
        return mDocView;
    }
    @Override
    public void docLoadCompleteFailedWithIndex(int index) {
        switch (index) {
            case 0:
                Log.i(TAG, "文档组件初始化完成");
                break;
            case 1:
                Log.i(TAG, "动画加载完成");
                break;
            case 2:
                Log.i(TAG, "非动画文档(白板 图片)文档加载完成");
                break;
            case 3:
                Log.i(TAG, "文档组件加载失败");
                break;
            case 4:
                Log.i(TAG, "文档图片加载失败");
                break;
            case 5:
                Log.i(TAG, "文档动画加载失败");
                break;
            case 6:
                Log.i(TAG, "画板加载失败");
                break;
            case 7:
                Log.i(TAG, "执行docLoadingReset成功");
                break;
            case 8:
                Log.i(TAG, "文档动画翻页失败");
                Toast.makeText(mContext, "文档动画翻页失败，需要执行docLoadingReset", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
