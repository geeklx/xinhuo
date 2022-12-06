package com.bokecc.livemodule.live.morefunction;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.bokecc.livemodule.R;
import com.bokecc.livemodule.live.DWLiveCoreHandler;
import com.bokecc.livemodule.live.DWLiveMoreFunctionListener;
import com.bokecc.livemodule.live.chat.KeyboardHeightObserver;
import com.bokecc.livemodule.live.chat.module.ChatEntity;
import com.bokecc.livemodule.live.morefunction.announce.AnnounceLayout;
import com.bokecc.livemodule.live.morefunction.fab.FabAttributes;
import com.bokecc.livemodule.live.morefunction.fab.MoreFunctionFab;
import com.bokecc.livemodule.live.morefunction.fab.OnFabClickListener;
import com.bokecc.livemodule.live.morefunction.privatechat.LivePrivateChatLayout;
import com.bokecc.livemodule.live.morefunction.rtc.RTCControlLayout;
import com.bokecc.livemodule.utils.DensityUtil;
import com.bokecc.livemodule.view.BaseRelativeLayout;
import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.pojo.PrivateChatInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * 更多功能 布局 （公告、私聊）
 */
public class MoreFunctionLayout extends BaseRelativeLayout implements OnFabClickListener, DWLiveMoreFunctionListener, KeyboardHeightObserver {

    private static final int ANNOUNCE = 1;
    private static final int RTC = 2;
    private static final int PRIVATE_CHAT = 3;

    private AnnounceLayout mAnnounceLayout;

    private LivePrivateChatLayout mPrivateChatLayout;

    private RTCControlLayout mRTCControlLayout;

    private MoreFunctionFab mFabTop;
    private LinearLayout mRemindRoot;
    /**
     * 消息提醒的点击事件
     */
    private RemindItem.RemindListener remindListener = new RemindItem.RemindListener() {
        @Override
        public void onClickListener(RemindItem remindItem, RemindItem.RemindType type) {
            if (type == RemindItem.RemindType.ANNOUNCE) {
                mAnnounceLayout.setVisibility(VISIBLE);
                mPrivateChatLayout.setVisibility(GONE);
                mRTCControlLayout.setVisibility(GONE);
            } else if (type == RemindItem.RemindType.PRIVATE_CHAT) {
                mPrivateChatLayout.setVisibility(VISIBLE);
                mAnnounceLayout.setVisibility(GONE);
                mRTCControlLayout.setVisibility(GONE);
            }
            mRemindRoot.removeView(remindItem);
            if (mRemindRoot.getChildCount() <= 0) {
                mRemindRoot.setVisibility(GONE);
            }
        }

        @Override
        public void onClose(RemindItem remindItem) {
            mRemindRoot.removeView(remindItem);
            if (mRemindRoot.getChildCount() <= 0) {
                mRemindRoot.setVisibility(GONE);
            }
        }
    };

    public MoreFunctionLayout(Context context) {
        super(context);
        initMoreFunctionListener();
    }

    public MoreFunctionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMoreFunctionListener();
    }

    public MoreFunctionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMoreFunctionListener();
    }

    public void initViews() {
        LayoutInflater.from(mContext).inflate(R.layout.live_portrait_more_function, this, true);
        mRemindRoot = findViewById(R.id.ll_remind);
        mAnnounceLayout = findViewById(R.id.announce_layout);
        mPrivateChatLayout = findViewById(R.id.private_chat_layout);
        mRTCControlLayout = findViewById(R.id.rtc_layout);

        mFabTop = findViewById(R.id.fab_top);

        FabAttributes announce = new FabAttributes.Builder()
                .setBackgroundTint(Color.parseColor("#FFFFFF"))
                .setSrc(getResources().getDrawable(R.drawable.more_function_announce))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setPressedTranslationZ(10)
                .setTag(ANNOUNCE)
                .build();

        FabAttributes rtc = new FabAttributes.Builder()
                .setBackgroundTint(Color.parseColor("#FFFFFF"))
                .setSrc(getResources().getDrawable(R.drawable.more_function_rtc))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setPressedTranslationZ(10)
                .setTag(RTC)
                .build();

        // 如果直播间模版没有聊天，那么私聊功能也不开放
        DWLiveCoreHandler dwLiveCoreHandler = DWLiveCoreHandler.getInstance();
        if (dwLiveCoreHandler != null && dwLiveCoreHandler.hasChatView() && DWLive.getInstance().getRoomInfo().getPrivateChat().equals("1")) {
            FabAttributes chat = new FabAttributes.Builder()
                    .setBackgroundTint(Color.parseColor("#FFFFFF"))
                    .setSrc(getResources().getDrawable(R.drawable.more_function_private_chat))
                    .setFabSize(FloatingActionButton.SIZE_MINI)
                    .setPressedTranslationZ(10)
                    .setTag(PRIVATE_CHAT)
                    .build();
            mFabTop.addFab(announce, rtc, chat);
        } else {
            mFabTop.addFab(announce, rtc);
        }

        mFabTop.setFabClickListener(this);
    }

    /**
     * 根据类型 展示消息提醒
     */
    private void showRemindByType(final RemindItem.RemindType remindType) {
        post(new Runnable() {
            @Override
            public void run() {
                if (mRemindRoot.getVisibility() != VISIBLE) {
                    mRemindRoot.setVisibility(VISIBLE);
                }
                if (mRemindRoot.getChildCount() > 0) {
                    for (int i = 0; i < mRemindRoot.getChildCount(); i++) {
                        if (mRemindRoot.getChildAt(i) instanceof RemindItem) {
                            RemindItem remindItem = (RemindItem) mRemindRoot.getChildAt(i);
                            if (remindItem.getType() == remindType) {
                                return;
                            }
                        }
                    }
                }
                RemindItem remindItem = new RemindItem(getContext());
                remindItem.setContent(remindType);
                remindItem.setRemindListener(remindListener);
                mRemindRoot.addView(remindItem, 0);
            }
        });
    }

    /**
     * 根据类型 移除消息提醒
     */
    private void removeRemindByType(final RemindItem.RemindType remindType) {
        post(new Runnable() {
            @Override
            public void run() {
                RemindItem remindItem = null;
                if (mRemindRoot.getChildCount() > 0) {
                    for (int i = 0; i < mRemindRoot.getChildCount(); i++) {
                        if (mRemindRoot.getChildAt(i) instanceof RemindItem) {
                            RemindItem remindItemTemp = (RemindItem) mRemindRoot.getChildAt(i);
                            if (remindItemTemp.getType() == remindType) {
                                remindItem = remindItemTemp;
                                break;
                            }
                        }
                    }
                }
                if (remindItem != null) {
                    mRemindRoot.removeView(remindItem);
                }
            }
        });
    }

    @Override
    public void onFabClick(FloatingActionButton fab, Object tag) {
        if (tag.equals(ANNOUNCE)) {
            if (mAnnounceLayout.getVisibility() == VISIBLE) {
                mAnnounceLayout.setVisibility(GONE);
            } else {
                mFabTop.getFabFromTag(ANNOUNCE).setImageResource(R.drawable.more_function_announce);
                mAnnounceLayout.setVisibility(VISIBLE);
                mPrivateChatLayout.setVisibility(GONE);
                mRTCControlLayout.setVisibility(GONE);
                removeRemindByType(RemindItem.RemindType.ANNOUNCE);
            }
        } else if (tag.equals(RTC)) {
            if (mRTCControlLayout.getVisibility() == VISIBLE) {
                mRTCControlLayout.setVisibility(GONE);
            } else {
                DWLiveCoreHandler dwLiveCoreHandler = DWLiveCoreHandler.getInstance();
                if (dwLiveCoreHandler != null && dwLiveCoreHandler.isAllowRtc()) {
                    mRTCControlLayout.setVisibility(VISIBLE);
                    mAnnounceLayout.setVisibility(GONE);
                    mPrivateChatLayout.setVisibility(GONE);
                } else {
                    toastOnUiThread("主播未开通连麦");
                }
            }
        } else if (tag.equals(PRIVATE_CHAT)) {
            if (mPrivateChatLayout.getVisibility() == VISIBLE) {
                mPrivateChatLayout.setVisibility(GONE);
            } else {
                mPrivateChatLayout.setVisibility(VISIBLE);
                mAnnounceLayout.setVisibility(GONE);
                mRTCControlLayout.setVisibility(GONE);
                removeRemindByType(RemindItem.RemindType.PRIVATE_CHAT);
            }
        }
    }

    @Override
    public void onFabVisible(boolean visible) {
        if (!visible) {
            mRTCControlLayout.setVisibility(GONE);
        }
        if (visible) {
            mRemindRoot.setTranslationY(0);
        } else {
            mRemindRoot.setTranslationY(mFabTop.getHeight() - mFabTop.getChildAt(0).getHeight() - DensityUtil.dp2px(getContext(), 10));
        }

    }

    /**
     * 初始化更多功能监听
     */
    private void initMoreFunctionListener() {
        DWLiveCoreHandler dwLiveCoreHandler = DWLiveCoreHandler.getInstance();
        if (dwLiveCoreHandler != null) {
            dwLiveCoreHandler.setDwLiveMoreFunctionListener(this);
        }
    }

    //***************************** 实现 DWLiveMoreFunctionListener 定义的方法 *************************************/

    /**
     * 公告
     *
     * @param isRemove     是否是公告删除，如果为true，表示公告删除且announcement参数为null
     * @param announcement 公告内容
     */
    @Override
    public void onAnnouncement(final boolean isRemove, final String announcement) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRemove) {
                    mAnnounceLayout.removeAnnounce();
                } else {
                    if (mAnnounceLayout.getVisibility() != VISIBLE) {
                        showRemindByType(RemindItem.RemindType.ANNOUNCE);
                    }
                    mAnnounceLayout.setAnnounce(announcement);
                }
            }
        });
    }

    /**
     * 别人私聊我
     */
    @Override
    public void onPrivateChat(PrivateChatInfo info) {
        // 向私聊控件添加数据
        mPrivateChatLayout.onPrivateChat(info);
        // 判断当前私聊界面是否展示中，如果没有展示，就提示用户有新私聊消息
        if (mPrivateChatLayout.getVisibility() != VISIBLE) {
            showRemindByType(RemindItem.RemindType.PRIVATE_CHAT);
        }
    }

    /**
     * 我发出的私聊
     */
    @Override
    public void onPrivateChatSelf(PrivateChatInfo info) {
        mPrivateChatLayout.onPrivateChatSelf(info);
    }

    /**
     * 跳转到私聊列表
     *
     * @param chatEntity 公聊消息
     */
    @Override
    public void jump2PrivateChat(ChatEntity chatEntity) {
        // 隐藏其他控件，展示私聊控件
        mAnnounceLayout.setVisibility(GONE);
        mPrivateChatLayout.setVisibility(VISIBLE);
        // 通知私聊控件，展示私聊列表
        mPrivateChatLayout.jump2PrivateChat(chatEntity);
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        if (mPrivateChatLayout != null && mPrivateChatLayout.getVisibility() == VISIBLE) {
            mPrivateChatLayout.onKeyboardHeightChanged(height, orientation);
        }
    }
}
