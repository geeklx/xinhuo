package com.bokecc.livemodule.live.room;

import static android.view.animation.Animation.RESTART;
import static com.bokecc.livemodule.live.room.rightview.LiveBarrageView.BARRAGE_FULL;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bokecc.livemodule.R;
import com.bokecc.livemodule.live.DWLiveCoreHandler;
import com.bokecc.livemodule.live.DWLiveRoomListener;
import com.bokecc.livemodule.live.chat.KeyboardHeightObserver;
import com.bokecc.livemodule.live.chat.adapter.EmojiAdapter;
import com.bokecc.livemodule.live.chat.barrage.BarrageLayout;
import com.bokecc.livemodule.live.chat.util.DensityUtil;
import com.bokecc.livemodule.live.chat.util.EmojiUtil;
import com.bokecc.livemodule.live.chat.window.BanChatPopup;
import com.bokecc.livemodule.live.room.rightview.LiveRightView;
import com.bokecc.livemodule.popup.SettingPopupWindow;
import com.bokecc.livemodule.popup.callback.SettingPopupInterface;
import com.bokecc.livemodule.popup.callback.SettingPopupWindowCallBack;
import com.bokecc.livemodule.utils.TimeUtil;
import com.bokecc.livemodule.view.BaseRelativeLayout;
import com.bokecc.livemodule.view.CustomToast;
import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.OnMarqueeImgFailListener;
import com.bokecc.sdk.mobile.live.listener.LiveChangeSourceListener;
import com.bokecc.sdk.mobile.live.logging.ELog;
import com.bokecc.sdk.mobile.live.pojo.LiveLineInfo;
import com.bokecc.sdk.mobile.live.pojo.LiveQualityInfo;
import com.bokecc.sdk.mobile.live.pojo.Marquee;
import com.bokecc.sdk.mobile.live.util.NetworkUtils;
import com.bokecc.sdk.mobile.live.widget.MarqueeView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ???????????????
 */
public class LiveRoomLayout extends BaseRelativeLayout implements DWLiveRoomListener, KeyboardHeightObserver, SettingPopupInterface {

    private final static String TAG = LiveRoomLayout.class.getSimpleName();
    private Activity activity;
    // ----------????????????---------
    private RelativeLayout mTopLayout;
    public TextView mLiveTitle;           // ??????
    private ImageView mLiveClose;          // ?????????????????????
    private ImageView mVideoDocMore;       // ????????????

    // ----------????????????----------
    private RelativeLayout mBottomLayout;
    private RelativeLayout mPortraitLiveBottom;   // ??????????????????
    //    private LinearLayout mBarrageControlLayout;   // ??????????????????
    private ImageView mBarrageControl;            // ????????????
    private ImageView mLiveFullScreen;            // ????????????
    private ImageView img_camera;            // ????????????
    public ImageView mLiveVideoDocSwitch;         // ????????????????????????????????????


    private RelativeLayout mBottomChatLayout; // ???????????????????????????
    private GridView mEmojiGrid;            // ????????????
    private Button mChatSend;               // ????????????
    private ImageView mEmoji;               // ??????????????????
    private EditText mInput;                // ???????????????
//    private TextView mChatLabel;            // ?????????????????????

    // ----------??????????????????---------
    private RelativeLayout mNoStreamRoot;
    private TextView mNoStreamText;           // ??????????????????????????????
    private TextView mCountDownTimeText;      // ?????????????????????
    private long downTime;
    private Timer timer;
    private TimerTask timerTask;

    // ----------????????????----------
    public LinearLayout mTipsLayout;    // ????????????
    private TextView mTipsView;         // ????????????
    private TextView mTryBtn;           // ???????????????????????????

    // ????????????????????????
    private SettingPopupWindow settingPopupWindow;
    //??????????????????
    private LiveRightView rightRoot;

    // ???????????????
    private MarqueeView mMarqueeView;
    private boolean isOpenMarquee;          // ?????????????????????

    // ????????????
    private BarrageLayout mLiveBarrage;
//    private boolean isOpenBarrage = false;          // ????????????????????????
//    private boolean isBarrageOn = false;     // ??????????????????

    private InputMethodManager mImm;
    // ???????????????????????????????????????????????????
    private final AtomicBoolean isShowInput = new AtomicBoolean(false);

    boolean isEmojiShow = false;                  // ??????????????????
    boolean isSoftInput = false;                  // ??????????????????
    private int softKeyHeight;                    // ??????????????????
    private boolean showEmojiAction = false;      // ????????????????????????
    private boolean isShowUserCount;              // ????????????????????????
    private boolean isShowQuality;                // ?????????????????????
    private boolean isVideoMain;                  // ???????????????????????????
    private boolean isFloatDismiss;               // ???????????????????????????

    private BanChatPopup banChatPopup;      // ???????????????
    private DWLiveCoreHandler dwLiveCoreHandler;

    private int banChatMode = 0;


    protected static final int HIDE_TIME = 10000;

    //?????????????????????????????????????????????
    private final Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            if (isShowInput.get()) {
                return;
            }
            hide();
        }
    };


    public LiveRoomLayout(Context context) {
        super(context);
    }

    public LiveRoomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveRoomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initViews() {
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        LayoutInflater.from(mContext).inflate(R.layout.live_room_layout, this, true);
        mLiveTitle = findViewById(R.id.tv_portrait_live_title);
//        mBarrageControlLayout = findViewById(R.id.ll_barrage_control);
        mBarrageControl = findViewById(R.id.iv_barrage_control);
        mTopLayout = findViewById(R.id.rl_portrait_live_top_layout);
        mBottomLayout = findViewById(R.id.rl_portrait_live_bottom_layout);
        mLiveVideoDocSwitch = findViewById(R.id.video_doc_switch);
        mLiveFullScreen = findViewById(R.id.iv_portrait_live_full);
        img_camera = findViewById(R.id.img_camera);
        img_camera.setSelected(true);
        mPortraitLiveBottom = findViewById(R.id.portrait_live_bottom);
        mLiveClose = findViewById(R.id.iv_portrait_live_close);
        mBottomChatLayout = findViewById(R.id.id_chat_bottom);
        mEmoji = findViewById(R.id.id_push_chat_emoji);
        mEmojiGrid = findViewById(R.id.id_push_emoji_grid);
        mChatSend = findViewById(R.id.id_push_chat_send);
        mInput = findViewById(R.id.id_push_chat_input);
//        mChatLabel = findViewById(R.id.tv_chat_label);
        rightRoot = findViewById(R.id.right_root);
        mNoStreamRoot = findViewById(R.id.no_stream_root);
        mNoStreamText = findViewById(R.id.tv_no_stream);
        mCountDownTimeText = findViewById(R.id.id_count_down_time);

        mTipsLayout = findViewById(R.id.id_error_layout);
        mTryBtn = findViewById(R.id.id_try);
        mTipsView = findViewById(R.id.id_msg_tips);

        mMarqueeView = findViewById(R.id.marquee_view);
        mLiveBarrage = findViewById(R.id.live_barrage);
        mLiveBarrage.setHeight(getContext().getResources().getDisplayMetrics().widthPixels / 2, 0, 0);
        mVideoDocMore = findViewById(R.id.video_doc_more);

        // ????????????
        mShowAction = new AlphaAnimation(0f, 1.0f);
        mShowAction.setRepeatMode(RESTART);
        mShowAction.setDuration(300);
        // ????????????
        mHiddenAction = new AlphaAnimation(1.0f, 0f);
        mHiddenAction.setRepeatMode(RESTART);
        mHiddenAction.setDuration(300);

        //??????????????????
        settingPopupWindow = new SettingPopupWindow(mContext);
        settingPopupWindow.setSettingPopupInterface(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // ??????????????????
        initListener();
        // ????????????????????????
        initEmojiAndChat();
        // ??????????????????
        openSetting();
        // ??????????????????bar
        removeCallbacks(hideRunnable);
        postDelayed(hideRunnable, HIDE_TIME);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mNoStreamRoot.getVisibility() != VISIBLE) {
            mVideoDocMore.setVisibility(VISIBLE);
        }
        mLiveFullScreen.setSelected(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
        removeCallbacks(hideRunnable);
        postDelayed(hideRunnable, HIDE_TIME);
    }


    /**
     * ?????????????????????
     * <p>
     * ** ????????????????????????,????????????????????????????????????
     *
     * @param isVideoMain ?????????????????????
     */
    public void init(boolean isVideoMain) {
        this.isVideoMain = isVideoMain;
        dwLiveCoreHandler = DWLiveCoreHandler.getInstance();
        if (dwLiveCoreHandler == null) {
            return;
        }
        // ????????????
        dwLiveCoreHandler.setDwLiveRoomListener(this);

        // ??????????????????
        isShowUserCount = dwLiveCoreHandler.isShowUserCount();

        // ?????????????????????
        isShowQuality = dwLiveCoreHandler.isShowQuality();
        // ??????????????????????????????
        if (!dwLiveCoreHandler.hasPdfView()) {
            mLiveVideoDocSwitch.setVisibility(GONE);
        }
        // ?????????????????????
        isOpenMarquee = dwLiveCoreHandler.isOpenMarquee();
        openMarquee();
        // ??????????????????
//        isOpenBarrage = dwLiveCoreHandler.isOpenBarrage();
    }

    /**
     * ????????????
     */
    private void intoFullScreen() {
        if (liveRoomStatusListener != null) {
            liveRoomStatusListener.fullScreen();
        }

    }

    /**
     * ??????????????????
     */
    private void initListener() {
        // ?????????????????????????????????bar
        setOnClickListener(mRoomAnimatorListener);
        // ?????????????????????
        mLiveVideoDocSwitch.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                img_camera.setSelected(true);
                if (liveRoomStatusListener != null) {
                    if (isFloatDismiss) {
                        liveRoomStatusListener.openVideoDoc();
                        setSwitchText(false);
                    } else {
                        isVideoMain = !isVideoMain;
                        liveRoomStatusListener.switchVideoDoc(isVideoMain);
                        setSwitchText(false);
                    }

                }
            }
        });

        //??????????????????
        mLiveFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                intoFullScreen();
                // ?????????activity??????ui
                if (liveRoomStatusListener != null && mLiveFullScreen.isSelected()) {
                    liveRoomStatusListener.closeRoom();
                } else if (liveRoomStatusListener != null) {
                    liveRoomStatusListener.fullScreen();
                }
            }
        });

        // ???????????????????????????
        mLiveClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (liveRoomStatusListener != null) {
                    liveRoomStatusListener.closeRoom();
                }
            }
        });

        // ??????????????????
        mTryBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(getContext())) {
                    mTipsLayout.setVisibility(GONE);
                    DWLive.getInstance().start();
                } else {
                    CustomToast.showToast(mContext, "??????????????????????????????", Toast.LENGTH_SHORT);
                }
            }
        });

        // ????????????
//        mChatLabel.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isShowInput.set(true);
//                isSoftInput = true;
//                removeCallbacks(hideRunnable);
//
//                mInput.requestFocus();
//                mImm.showSoftInput(mInput, 0);
//
//                mPortraitLiveBottom.setVisibility(GONE);
//                mBottomChatLayout.setVisibility(VISIBLE);
//
//
//            }
//        });

        // ?????????????????????????????? -->??????UI
        rightRoot.setRightCallBack(new LiveRightView.LiveRightCallBack() {


            @Override
            public void onChangePlayMode(DWLive.LivePlayMode playMode) {
                DWLiveCoreHandler.getInstance().updatePlayModeUI(playMode);
                setMode(2, playMode == DWLive.LivePlayMode.PLAY_MODE_TYEP_AUDIO);
            }

            @Override
            public void onChangeQuality(int quality, String qualityDesc) {
                setQuality(2, quality, qualityDesc);
            }

            @Override
            public void onChangeBarrage(int type) {
                if (type == BARRAGE_FULL) {
                    mLiveBarrage.setHeight(getContext().getResources().getDisplayMetrics().heightPixels, 0, 0);
                } else {
                    mLiveBarrage.setHeight((getContext().getResources().getDisplayMetrics().heightPixels) / 2, 0, 0);
                }
            }

            @Override
            public void onChangeLine(int line) {
                setLine(2, line);
            }

            @Override
            public void onClose() {
                removeCallbacks(hideRunnable);
                if (!mTopLayout.isShown()) {
                    show();
                }
                postDelayed(hideRunnable, HIDE_TIME);
            }
        });

        img_camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                img_camera.setSelected(!img_camera.isSelected());
                if (!img_camera.isSelected() && liveRoomStatusListener != null) {
                    liveRoomStatusListener.onDismissFloatView();
                    setSwitchText(true);
                } else if (img_camera.isSelected() && liveRoomStatusListener != null) {
                    liveRoomStatusListener.openVideoDoc();
                    setSwitchText(false);
                }
            }
        });

    }

    // ?????????????????????????????????
    public void setSwitchText(boolean isFloatDismiss) {
        this.isFloatDismiss = isFloatDismiss;
        if (isFloatDismiss && isVideoMain && liveRoomStatusListener != null) {
//            mVideoDocSwitch.setImageResource(isVideoMain ? R.drawable.open_doc : R.drawable.open_video);
//            mLandVideoDocSwitch.setImageResource(isVideoMain ? R.drawable.open_doc : R.drawable.open_video);
            isVideoMain = false;
            liveRoomStatusListener.switchVideoDoc(isVideoMain);
        }
//        if (isFloatDismiss) {
//            mLiveVideoDocSwitch.setImageResource(isVideoMain ? R.drawable.open_doc : R.drawable.open_video);
//        } else {
//            mLiveVideoDocSwitch.setImageResource(R.mipmap.exchange_view);
//        }
    }

    /**
     * ????????????????????????
     */
    public boolean isVideoMain() {
        DWLiveCoreHandler dwLiveCoreHandler = DWLiveCoreHandler.getInstance();
        if (dwLiveCoreHandler != null && !dwLiveCoreHandler.hasPdfView()) {
            return true;
        }
        return isVideoMain;
    }

    /**
     * ???????????????????????????????????????
     *
     * @return true ??????
     */
    public boolean onBackPressed() {
        if (rightRoot.getVisibility() == VISIBLE) {
            rightRoot.setVisibility(GONE);
            return true;
        } else {
            return false;
        }
    }

    /**
     * ????????????????????????
     *
     * @param type    1????????????????????????????????????   2??????????????????????????????
     * @param quality ?????????
     */
    private void setQuality(int type, int quality, String desc) {
        if (type == 1) {
            rightRoot.setQuality(quality);
        } else if (type == 2) {
            settingPopupWindow.setQuality(quality);
        }
    }

    /**
     * ?????????????????????
     *
     * @param type 1?????????????????????????????????   2???????????????????????????
     * @param line ??????
     */
    private void setLine(int type, int line) {
        if (event != null) {
            event.changeLine(line);
        }
        if (type == 1) {
            rightRoot.setLine(line);
        } else if (type == 2) {
            settingPopupWindow.setLine(line);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param type    1?????????????????????????????????   2???????????????????????????
     * @param isAudio ???????????????
     */
    private void setMode(int type, boolean isAudio) {
        if (type == 1) {
            rightRoot.setMode(isAudio);
        } else if (type == 2) {
            settingPopupWindow.setMode(isAudio);
        }
    }

    /**
     * ????????????????????????
     */
    private void openSetting() {
        mVideoDocMore.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    rightRoot.showRight(LiveRightView.RIGHT_SHOW_LINE);
                } else {
                    settingPopupWindow.show(activity, getRootView(), Gravity.BOTTOM, 0, 0);
                }

            }
        });

    }


    //**************************************** ?????????????????? **************************************

    /**
     * ???????????????????????????????????????????????????
     *
     * @param isVideoMain ????????????????????????
     */
    @Override
    public void onSwitchVideoDoc(final boolean isVideoMain) {
        // ??????????????????????????????????????????
        if (this.isVideoMain == isVideoMain) {
            return;
        }
        this.isVideoMain = isVideoMain;
        if (liveRoomStatusListener != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    liveRoomStatusListener.switchVideoDoc(isVideoMain);   // ???????????????????????????
                    setSwitchText(false);
                }
            });
        }
    }

    /**
     * ?????????????????????
     */
    @Override
    public void showRoomTitle(final String title) {
//        post(new Runnable() {
//            @Override
//            public void run() {
//                mLiveTitle.setText(title);
//            }
//        });
    }

    /**
     * ?????????????????????
     */
    @Override
    public void showRoomUserNum(final int number) {
    }

    /**
     * ????????????
     */
    @Override
    public void onKickOut() {
        if (liveRoomStatusListener != null) {
            liveRoomStatusListener.kickOut();
        }
        stopTimer();
    }

    @Override
    public void onInformation(final String msg) {
        post(new Runnable() {
            @Override
            public void run() {
                if (banChatPopup == null) {
                    banChatPopup = new BanChatPopup(getContext());
                }
                if (banChatPopup.isShowing()) {
                    banChatPopup.onDestroy();
                }
                banChatPopup.banChat(msg);
                banChatPopup.show(LiveRoomLayout.this);
            }
        });
    }

    @Override
    public void onStreamEnd(boolean isNormal, String reason) {
        mNoStreamRoot.setVisibility(VISIBLE);
        mLiveFullScreen.setVisibility(View.GONE);
        mLiveVideoDocSwitch.setVisibility(GONE);
        mNoStreamText.setText(reason);
        mVideoDocMore.setVisibility(GONE);
        //??????????????????
        if (!isNormal) {
            // ????????????????????????????????????
            if (dwLiveCoreHandler != null && dwLiveCoreHandler.getRoomInfo().getOpenLiveCountdown() == 1) {
                downTime = dwLiveCoreHandler.getRoomInfo().getLiveCountdown();
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                mCountDownTimeText.setText(TimeUtil.getYearMonthDayHourMinuteSecond(
                                        mContext, downTime));
                                downTime -= 1;
                                if (downTime <= 0) {
                                    mCountDownTimeText.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                };
                timer.schedule(timerTask, 0, 1000);
            }
        }
        if (liveRoomStatusListener != null) {
            liveRoomStatusListener.onStreamEnd();
            liveRoomStatusListener.onStreamEnd(isNormal,reason);
        }
        // ???????????????
        closeMarquee();
    }

    @Override
    public void onStreamStart() {
        stopTimer();
        setSwitchText(false);
        mVideoDocMore.setVisibility(VISIBLE);
        mTipsLayout.setVisibility(GONE);
        mNoStreamRoot.setVisibility(View.GONE);
        if (DWLiveCoreHandler.getInstance().hasPdfView()) {
            mLiveVideoDocSwitch.setVisibility(VISIBLE);
        } else {
            mLiveVideoDocSwitch.setVisibility(GONE);
        }

        if (liveRoomStatusListener != null) {
            liveRoomStatusListener.onStreamStart();
        }
        // ???????????????
        openMarquee();


    }

    @Override
    public void showError(final String errMsg) {
        post(new Runnable() {
            @Override
            public void run() {
                mTipsLayout.setVisibility(VISIBLE);
                mTipsView.setText(errMsg);
            }
        });

    }

    @Override
    public void onHDAudioMode(DWLive.LiveAudio hasAudio) {
        rightRoot.showAudio(hasAudio == DWLive.LiveAudio.HAVE_AUDIO_LINE_TRUE);
        if (hasAudio == DWLive.LiveAudio.HAVE_AUDIO_LINE_TRUE) {
            settingPopupWindow.addModeChange();
        }
    }

    @Override
    public void onHDReceivedVideoQuality(List<LiveQualityInfo> videoQuality, LiveQualityInfo currentQuality) {
        rightRoot.showVideoQuality(videoQuality, currentQuality);
        settingPopupWindow.addQuality(videoQuality, currentQuality);
    }

    @Override
    public void onHDReceivedVideoAudioLines(List<LiveLineInfo> lines, int indexNum) {
        rightRoot.showVideoAudioLines(lines, indexNum);
        settingPopupWindow.addLines(lines, indexNum);
    }

    @Override
    public void onBanChat(final int mode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                banChatMode = mode;
                if (mode == 2) {
                    // "???????????????"
                    mInput.setHint("???????????????");
                    mInput.setFocusable(false);
                    mInput.setFocusableInTouchMode(false);
                    banChat();
                }
            }
        });
    }

    @Override
    public void onUnBanChat(final int mode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                banChatMode = 0;
                if (mode == 2) {
                    //  "?????????????????????"
                    mInput.setHint(R.string.chat_input_hint);
                    mInput.setFocusable(true);
                    mInput.setFocusableInTouchMode(true);
                    mInput.requestFocus();
                }
            }
        });
    }


    // ?????????????????????
    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        if (height > 10) {
            isSoftInput = true;
            softKeyHeight = height;
            mPortraitLiveBottom.setTranslationY(-softKeyHeight);
//            mEmoji.setImageResource(R.drawable.push_chat_emoji_normal);
            isEmojiShow = false;

            removeCallbacks(hideRunnable);
        } else {
            if (!showEmojiAction) {
                hideEmoji();
                mPortraitLiveBottom.setTranslationY(0);
            }
            isSoftInput = false;

        }
        //??????????????????
        showEmojiAction = false;
    }


    //***************************************** ?????????????????? **************************************

    // ?????????????????????
    private short maxInput = 300;

    // ??????????????????????????????
    private void initEmojiAndChat() {
        // ????????????????????????300
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputText = mInput.getText().toString();
                if (inputText.length() > maxInput) {
                    CustomToast.showToast(mContext, "????????????300???", Toast.LENGTH_SHORT);
                    mInput.setText(inputText.substring(0, maxInput));
                    mInput.setSelection(maxInput);
                }
                if (TextUtils.isEmpty(inputText)) {
                    mChatSend.setEnabled(false);
                } else {
                    mChatSend.setEnabled(true);
                }
            }
        });
        mEmoji.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (banChatMode != 2) {
                    removeCallbacks(hideRunnable);
                    //???????????????????????????????????????
                    if (isSoftInput) {
                        showEmojiAction = true;
                        //1??????????????????
                        showEmoji();
                        //2???????????????
                        mImm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
                    } else if (isEmojiShow) {  //?????????????????????????????????????????????????????????????????????
                        boolean b = mImm.showSoftInput(mInput, 0);
                        if (b) {
                            hideEmoji();
                        }
                    } else {
                        showEmoji();
                    }
                }
            }
        });

        initEmojiAdapter();

        mChatSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (banChatMode != 2) {
                    if (liveRoomStatusListener != null) {
                        liveRoomStatusListener.sendChat();
                    }
                    String msg = mInput.getText().toString().trim();
                    if (TextUtils.isEmpty(msg)) {
                        CustomToast.showToast(mContext, "????????????????????????", Toast.LENGTH_SHORT);
                        return;
                    }
                    DWLive.getInstance().sendPublicChatMsg(msg);
                    clearChatInput();
                    //??????????????????????????????  ?????????????????????  ??????????????????????????????????????????
                    if (isSoftInput) {
                        hideKeyboard();
                    } else {
                        hideEmoji();
                        mPortraitLiveBottom.setTranslationY(0);
                    }
                    isShowInput.set(false);
                    removeCallbacks(hideRunnable);
                    postDelayed(hideRunnable, HIDE_TIME);
                }
            }
        });

        mBarrageControl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBarrageControl.setSelected(!mBarrageControl.isSelected());
                controlBarrageControl(mBarrageControl.isSelected());
                if (liveRoomStatusListener != null) {
                    liveRoomStatusListener.switchDanMu();
                }
            }
        });
    }

    private void initEmojiAdapter() {
        EmojiAdapter emojiAdapter = new EmojiAdapter(mContext);
        emojiAdapter.bindData(EmojiUtil.imgs);
        mEmojiGrid.setAdapter(emojiAdapter);
        mEmojiGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mInput == null) {
                    return;
                }
                // ????????????span??????8?????????
                if (mInput.getText().length() + 8 > maxInput) {
                    CustomToast.showToast(mContext, "???????????????300???", Toast.LENGTH_SHORT);
                    return;
                }
                if (position == EmojiUtil.imgs.length - 1) {
                    EmojiUtil.deleteInputOne(mInput);
                } else {
                    EmojiUtil.addEmoji(mContext, mInput, position);
                }
            }
        });
    }

    public void clearChatInput() {
        mInput.setText("");
    }

    public void hideKeyboard() {
        hideEmoji();
        mImm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
    }

    private void banChat() {
        clearChatInput();
        hideKeyboard();
    }

    // ??????emoji
    public void showEmoji() {
        if (mEmojiGrid.getHeight() != softKeyHeight && softKeyHeight != 0) {
            ViewGroup.LayoutParams lp = mEmojiGrid.getLayoutParams();
            lp.height = softKeyHeight;
            mEmojiGrid.setLayoutParams(lp);
        }
        mEmojiGrid.setVisibility(View.VISIBLE);
//        mEmoji.setImageResource(R.drawable.push_chat_emoji);
        isEmojiShow = true;
        float transY;
        if (softKeyHeight == 0) {
            transY = -mEmojiGrid.getHeight();
        } else {
            transY = -softKeyHeight;
        }
        mPortraitLiveBottom.setTranslationY(transY);
    }

    // ??????emoji
    public void hideEmoji() {
        mEmojiGrid.setVisibility(View.GONE);
//        mEmoji.setImageResource(R.drawable.push_chat_emoji_normal);
        isEmojiShow = false;
    }

    //***************************************** ?????????????????????????????? ******************************
    private final OnClickListener mRoomAnimatorListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mPortraitLiveBottom.setTranslationY(0);
            removeCallbacks(hideRunnable);
            hideKeyboard();
            toggleTopAndButtom();
        }
    };

    //????????????
    private AlphaAnimation mShowAction;
    //????????????
    private AlphaAnimation mHiddenAction;

    private void hide() {
        mTopLayout.clearAnimation();
        mBottomLayout.clearAnimation();
        mBottomLayout.startAnimation(mHiddenAction);
        mTopLayout.startAnimation(mHiddenAction);
        mBottomLayout.setVisibility(GONE);
        mTopLayout.setVisibility(GONE);
        isShowInput.set(false);
    }

    private void show() {
        mBottomLayout.clearAnimation();
        mTopLayout.clearAnimation();
        mBottomLayout.startAnimation(mShowAction);
        mTopLayout.startAnimation(mShowAction);
        mTopLayout.setVisibility(VISIBLE);
        mBottomLayout.setVisibility(VISIBLE);

        // ????????????????????????
        mPortraitLiveBottom.setVisibility(VISIBLE);
        // ??????????????????
//        mBottomChatLayout.setVisibility(GONE);

        removeCallbacks(hideRunnable);
        postDelayed(hideRunnable, HIDE_TIME);
    }

    private void toggleTopAndButtom() {
        if (mTopLayout.isShown()) {
            hide();
        } else {
            show();
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    //***************************************** ????????????????????? ***********************************

    /**
     * ???????????????
     */
    public void openMarquee() {
        if (isOpenMarquee) {
            mMarqueeView.setVisibility(VISIBLE);
            if (dwLiveCoreHandler.getViewer() != null) {
                setMarquee(dwLiveCoreHandler.getViewer().getMarquee());
            }
        }
    }

    /**
     * ???????????????
     */
    public void closeMarquee() {
        if (mMarqueeView != null) {
            mMarqueeView.stop();
            mMarqueeView.setVisibility(View.GONE);
        }
    }

    private void setMarquee(final Marquee marquee) {
        final ViewGroup parent = (ViewGroup) mMarqueeView.getParent();
        if (parent.getWidth() != 0 && parent.getHeight() != 0) {
            if (marquee != null && marquee.getAction() != null) {
                if (marquee.getType().equals("text")) {
                    mMarqueeView.setTextContent(marquee.getText().getContent());
                    mMarqueeView.setTextColor(marquee.getText().getColor().replace("0x", "#"));
                    mMarqueeView.setTextFontSize((int) DensityUtil.sp2px(mContext, marquee.getText().getFont_size()));
                    // 1????????????
                    mMarqueeView.setType(1);
                } else {
                    mMarqueeView.setMarqueeImage(mContext, marquee.getImage().getImage_url(), marquee.getImage().getWidth(), marquee.getImage().getHeight());
                    // 2????????????
                    mMarqueeView.setType(2);
                }
                mMarqueeView.setMarquee(marquee, parent.getHeight(), parent.getWidth());
                mMarqueeView.setOnMarqueeImgFailListener(new OnMarqueeImgFailListener() {
                    @Override
                    public void onLoadMarqueeImgFail() {
                        ELog.e(TAG, "");

                    }
                });
                mMarqueeView.start();
            }
        } else {
            parent.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int width = parent.getWidth();// ????????????
                            int height = parent.getHeight();// ????????????
                            if (marquee != null && marquee.getAction() != null) {
                                if (marquee.getType().equals("text")) {
                                    mMarqueeView.setTextContent(marquee.getText().getContent());
                                    mMarqueeView.setTextColor(marquee.getText().getColor().replace("0x", "#"));
                                    mMarqueeView.setTextFontSize((int) DensityUtil.sp2px(mContext, marquee.getText().getFont_size()));
                                    mMarqueeView.setType(1);
                                } else {
                                    mMarqueeView.setMarqueeImage(mContext,
                                            marquee.getImage().getImage_url(), marquee.getImage().getWidth(), marquee.getImage().getHeight());
                                    mMarqueeView.setType(2);
                                }
                                mMarqueeView.setMarquee(marquee, height, width);
                                mMarqueeView.setOnMarqueeImgFailListener(new OnMarqueeImgFailListener() {
                                    @Override
                                    public void onLoadMarqueeImgFail() {

                                    }
                                });
                                mMarqueeView.start();
                            }
                        }
                    });
        }

    }


    //***************************************** ?????????????????? ***********************************
    public void showInput(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mBottomChatLayout.setVisibility(VISIBLE);
        } else {
            mBottomChatLayout.setVisibility(GONE);
        }
    }

    /**
     * ????????????
     */
    public void openLiveBarrage() {
        controlBarrageControl(mBarrageControl.isSelected());
    }

    /**
     * ????????????
     */
    public void closeLiveBarrage() {
        controlBarrageControl(false);

    }

    /**
     * ???????????? ???????????????????????????????????? ???????????????????????????????????????????????????
     *
     * @param isBarrageOn ??????????????????
     */
    public void controlBarrageControl(boolean isBarrageOn) {
        if (isBarrageOn) {
            mLiveBarrage.start();
        } else {
            mLiveBarrage.stop();
        }
    }

    /**
     * ??????????????????
     *
     * @return BarrageLayout
     */
    public BarrageLayout getLiveBarrageLayout() {
        return mLiveBarrage;
    }

    //***************************************** ???????????? *****************************************
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                performClick();
                return false;

        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }


    //***************************************** ?????????????????? *************************************
    private LiveRoomStatusListener liveRoomStatusListener;

    public void setLiveRoomStatusListener(LiveRoomStatusListener listener) {
        this.liveRoomStatusListener = listener;
    }

    public void onSpeed(float speed) {

    }

    @Override
    public void onPlayMode(final boolean isAudio, final SettingPopupWindowCallBack callBack) {
        DWLiveCoreHandler instance = DWLiveCoreHandler.getInstance();
        if (instance != null) {
            // ?????????????????????
            instance.changePlayMode(isAudio ? DWLive.LivePlayMode.PLAY_MODE_TYEP_AUDIO : DWLive.LivePlayMode.PLAY_MODE_TYEP_VIDEO, new LiveChangeSourceListener() {

                @Override
                public void onChange(int success) {
                    if (success == 0) {
                        DWLiveCoreHandler.getInstance().updatePlayModeUI(isAudio ? DWLive.LivePlayMode.PLAY_MODE_TYEP_AUDIO : DWLive.LivePlayMode.PLAY_MODE_TYEP_VIDEO);
                        setMode(1, isAudio);
                    }
                    if (callBack != null) {
                        callBack.onResult(success);
                    }
                }
            });
        }
    }

    @Override
    public void onQuality(final int quality, final String desc, final SettingPopupWindowCallBack callBack) {
        DWLiveCoreHandler instance = DWLiveCoreHandler.getInstance();
        if (instance != null) {
            instance.changeQuality(quality, new LiveChangeSourceListener() {
                @Override
                public void onChange(int success) {
                    if (success == 0) {
                        setQuality(1, quality, desc);
                    }
                    if (callBack != null) {
                        callBack.onResult(success);
                    }
                }
            });
        }
    }

    @Override
    public void onLine(final int line, final SettingPopupWindowCallBack callBack) {
        DWLiveCoreHandler instance = DWLiveCoreHandler.getInstance();
        if (instance != null) {
            instance.changeLine(line, new LiveChangeSourceListener() {
                @Override
                public void onChange(int success) {
                    if (success == 0) {
                        setLine(1, line);
                    }
                    if (callBack != null) {
                        callBack.onResult(success);
                    }
                }
            });
        }
    }

    public void destroy() {
        stopTimer();
    }

    public interface LiveRoomStatusListener {

        /**
         * ?????? ??????/?????? ????????????
         */
        void switchVideoDoc(boolean isVideoMain);

        /**
         * ?????? ??????/?????? ??????
         */
        void openVideoDoc();

        /**
         * ?????? Back ?????? ?????????????????????
         */
        void closeRoom();

        /**
         * ?????? ?????? ?????? ??????????????????
         */
        void fullScreen();

        /**
         * ???????????? ???????????? #Called From DWLiveCoreHandler
         */
        void kickOut();

        /**
         * ??????????????????
         */
        void onClickDocScaleType(int scaleType);

        /**
         * ????????????
         */
        void onStreamEnd();
        /**
         * ????????????
         */
        void onStreamEnd(boolean isNormal, String reason);

        /**
         * ????????????
         */
        void onStreamStart();

        void switchDanMu();

        void sendChat();

        void onDismissFloatView();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        settingPopupWindow.setActivity(activity);
    }
}
