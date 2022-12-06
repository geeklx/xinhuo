package com.bokecc.livemodule.live.morefunction.announce;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bokecc.livemodule.R;
import com.bokecc.livemodule.live.DWLiveCoreHandler;
import com.bokecc.livemodule.live.DWLiveMoreFunctionListener;
import com.bokecc.livemodule.live.chat.module.ChatEntity;
import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.pojo.PrivateChatInfo;

/**
 * 公告界面
 */
public class AnnounceLayout extends LinearLayout implements DWLiveMoreFunctionListener {

    private Context mContext;

    private TextView mAnnounce;

    private View mRoot;

    private NewAnnounce newAnnounce;

    public AnnounceLayout(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public AnnounceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public AnnounceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        DWLiveCoreHandler dwLiveCoreHandler = DWLiveCoreHandler.getInstance();
        if (dwLiveCoreHandler != null) {
            dwLiveCoreHandler.setDwLiveMoreFunctionListener(this);
        }
        mRoot = LayoutInflater.from(mContext).inflate(R.layout.live_portrait_announce, this, true);
        mAnnounce = findViewById(R.id.announce);

        if (DWLive.getInstance() != null && !TextUtils.isEmpty(DWLive.getInstance().getAnnouncement())) {
            mAnnounce.setText(Html.fromHtml("<font color='#f08336'>公告 ：</font><font color='#333'>" + DWLive.getInstance().getAnnouncement() + "</font>"));
        }
    }

    /**
     * 设置公告
     *
     * @param announce 公告内容
     */
    public void setAnnounce(final String announce) {
        if (mAnnounce != null) {
            mAnnounce.post(new Runnable() {
                @Override
                public void run() {
                    mAnnounce.setText(Html.fromHtml("<font color='#f08336'>公告 ：</font><font color='#333'>" + announce + "</font>"));
                }
            });
        }
    }

    /**
     * 删除公告
     */
    public void removeAnnounce() {
        if (mAnnounce != null) {
            mAnnounce.post(new Runnable() {
                @Override
                public void run() {
                    mAnnounce.setText("暂无公告");
                }
            });
        }
    }

    @Override
    public void onAnnouncement(final boolean isRemove, final String announcement) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRemove) {
                    removeAnnounce();
                } else {
                    setAnnounce(announcement);
                    newAnnounce.newAnnounce();
                }
            }
        });
    }

    @Override
    public void jump2PrivateChat(ChatEntity chatEntity) {

    }

    @Override
    public void onPrivateChat(PrivateChatInfo info) {

    }

    @Override
    public void onPrivateChatSelf(PrivateChatInfo info) {

    }

    // 在UI线程执行一些操作
    public void runOnUiThread(Runnable runnable) {
        if (!checkOnMainThread()) {
            new Handler(Looper.getMainLooper()).post(runnable);
        } else {
            runnable.run();
        }
    }

    // 判断当前的线程是否是UI线程
    protected boolean checkOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public void setNewAnnounce(NewAnnounce newAnnounce) {
        this.newAnnounce = newAnnounce;
    }

    public interface NewAnnounce {
        void newAnnounce();
    }
}
