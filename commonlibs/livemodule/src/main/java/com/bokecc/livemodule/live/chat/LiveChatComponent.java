package com.bokecc.livemodule.live.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bokecc.livemodule.R;
import com.bokecc.livemodule.live.DWLiveChatListener;
import com.bokecc.livemodule.live.DWLiveCoreHandler;
import com.bokecc.livemodule.live.UserListener;
import com.bokecc.livemodule.live.chat.adapter.EmojiAdapter;
import com.bokecc.livemodule.live.chat.adapter.LivePublicChatAdapter;
import com.bokecc.livemodule.live.chat.barrage.BarrageLayout;
import com.bokecc.livemodule.live.chat.module.ChatEntity;
import com.bokecc.livemodule.live.chat.util.EmojiUtil;
import com.bokecc.livemodule.live.chat.window.BanChatPopup;
import com.bokecc.livemodule.utils.ChatImageUtils;
import com.bokecc.livemodule.view.AutoScrollView;
import com.bokecc.livemodule.view.BaseRelativeLayout;
import com.bokecc.livemodule.view.CustomToast;
import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.pojo.BanChatBroadcast;
import com.bokecc.sdk.mobile.live.pojo.BroadCastMsg;
import com.bokecc.sdk.mobile.live.pojo.ChatMessage;
import com.bokecc.sdk.mobile.live.pojo.UserRedminAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ?????????????????????????????????????????????
 */
public class LiveChatComponent extends BaseRelativeLayout implements DWLiveChatListener, KeyboardHeightObserver, UserListener {

    private final static String TAG = "LiveChatComponent";

    private RecyclerView mChatList;
    private RelativeLayout mChatLayout;
    private EditText mInput;
    private ImageView mEmoji;
    private Button mChatSend;
    private GridView mEmojiGrid;

    // ?????????????????????
    private boolean isSoftInput = false;
    // emoji??????????????????
    private boolean isEmojiShow = false;
    // ??????????????????
    private boolean isChat = false;

    // ?????????????????????
    private LivePublicChatAdapter mChatAdapter;

    // ???????????????
    private InputMethodManager mImm;

    // ??????????????????????????????????????????????????????
    private short maxInput = 300;

    private OnChatComponentClickListener mChatComponentClickListener;
    private OnBroadcastClickListener onBroadcastClickListener;
    //??????????????????
    private int softKeyHeight;
    private boolean showEmojiAction = false;
    private BanChatPopup banChatPopup;
    private AutoScrollView autoScrollView;
    private int banChatMode = 0;//???????????? 1???????????????  2???????????????

    public void setOnChatComponentClickListener(OnChatComponentClickListener listener) {
        mChatComponentClickListener = listener;
    }
public void  setOnBroadcastClickListener(OnBroadcastClickListener listener){
    onBroadcastClickListener = listener;
}
    public LiveChatComponent(Context context) {
        super(context);
        initChat();
    }

    public LiveChatComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initChat();
    }

    public void initViews() {
        LayoutInflater.from(mContext).inflate(R.layout.live_portrait_chat_layout, this, true);
        mChatList = findViewById(R.id.chat_container);
        mChatLayout = findViewById(R.id.id_push_chat_layout);
        mInput = findViewById(R.id.id_push_chat_input);
        mEmoji = findViewById(R.id.id_push_chat_emoji);
        mEmojiGrid = findViewById(R.id.id_push_emoji_grid);
        mChatSend = findViewById(R.id.id_push_chat_send);
        //??????????????????
        autoScrollView = findViewById(R.id.auto_scroll_view);
        mEmoji.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (banChatMode != 2) {
                    //???????????????????????????????????????
                    if (isSoftInput) {
                        showEmojiAction = true;
                        //1??????????????????
                        showEmoji();
                        //2???????????????
                        mImm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
                    } else if (isEmojiShow) {  //?????????????????????????????????????????????????????????????????????
                        mImm.showSoftInput(mInput, 0);
                    } else { //???????????????????????????????????????
                        //??????????????????
                        showEmoji();
                    }
                }
            }
        });
        mChatSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (banChatMode != 2) {
                    String msg = mInput.getText().toString().trim();
                    if (TextUtils.isEmpty(msg)) {
                        toastOnUiThread("????????????????????????");
                        return;
                    }
                    DWLive.getInstance().sendPublicChatMsg(msg);
                    clearChatInput();
                    mImm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initChat() {
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mChatList.setLayoutManager(new LinearLayoutManager(mContext));
        mChatAdapter = new LivePublicChatAdapter(mContext);
        mChatList.setAdapter(mChatAdapter);

        mChatAdapter.setOnChatImageClickListener(new LivePublicChatAdapter.onChatComponentClickListener() {
            @Override
            public void onChatComponentClick(View view, Bundle bundle) {
                if (mChatComponentClickListener != null) {
                    mChatComponentClickListener.onClickChatComponent(bundle);
                }
            }
        });

        mChatAdapter.setOnItemClickListener(new LivePublicChatAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ChatEntity chatEntity = mChatAdapter.getChatEntities().get(position);
                // ???????????????????????????????????????????????????????????????????????????????????????
                // ?????????publisher???????????????teacher??????????????????host????????????????????????student???????????????????????????unknow???
                if (chatEntity.getUserRole() == null || "student".equals(chatEntity.getUserRole()) || "unknow".equals(chatEntity.getUserRole()) || !DWLive.getInstance().getRoomInfo().getPrivateChat().equals("1")) {
                    Log.w(TAG, "???????????????????????????????????????????????????");
                    //toastOnUiThread("???????????????????????????????????????????????????");
                    return;
                }
                // ??????DWLiveCoreHandler.jump2PrivateChat???????????????????????????????????????????????????????????????????????????
                DWLiveCoreHandler dwLiveCoreHandler = DWLiveCoreHandler.getInstance();
                if (dwLiveCoreHandler != null) {
                    dwLiveCoreHandler.jump2PrivateChat(chatEntity);
                }
            }
        });

        mChatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mChatLayout.setTranslationY(0);
                autoScrollView.setTranslationY(0);
                hideKeyboard();
                return false;
            }
        });

        initChatView();

        DWLiveCoreHandler dwLiveCoreHandler = DWLiveCoreHandler.getInstance();
        if (dwLiveCoreHandler != null) {
            dwLiveCoreHandler.setDwLiveChatListener(this);
            dwLiveCoreHandler.setUserListener(this);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    public void initChatView() {
        mInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                hideEmoji();
                return false;
            }
        });
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
            }
        });

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
                    toastOnUiThread("???????????????300???");
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


    public void hideChatLayout() {
        if (isChat) {
            AlphaAnimation animation = new AlphaAnimation(0f, 1f);
            animation.setDuration(300L);
            mInput.setFocusableInTouchMode(false);
            mInput.clearFocus();
            mChatLayout.setVisibility(View.VISIBLE);
            isChat = false;
        }
    }

    /**
     * ??????emoji
     */
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
        mChatLayout.setTranslationY(transY);
        autoScrollView.setTranslationY(transY);
    }

    /**
     * ??????emoji
     */
    public void hideEmoji() {
        mEmojiGrid.setVisibility(View.GONE);
//        mEmoji.setImageResource(R.drawable.push_chat_emoji_normal);
        isEmojiShow = false;
    }

    public void clearChatInput() {
        mInput.setText("");
//        hideKeyboard();
    }

    public void hideKeyboard() {
        hideEmoji();
        mImm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
    }

    private void banChat() {
        clearChatInput();
        hideKeyboard();
    }

    public boolean onBackPressed() {
        if (isEmojiShow) {
            mChatLayout.setTranslationY(0);
            autoScrollView.setTranslationY(0);
            hideEmoji();
            hideChatLayout();
            return true;
        }
        return false;
    }

    private void clearChatEntities() {
        if (mChatAdapter != null) {
            mChatAdapter.clearData();
        }
    }

    public void addChatEntity(ChatEntity chatEntity) {
        mChatAdapter.add(chatEntity);
        if (mChatAdapter.getItemCount() - 1 > 0) {
            mChatList.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
        }
    }

    /*** ????????????????????????????????????0?????????  1???????????????***/
    public void changeChatStatus(String status, ArrayList<String> chatIds) {
        List<ChatEntity> chatEntities = mChatAdapter.changeStatus(status, chatIds);
        String selfId = "";
        if (DWLive.getInstance() != null
                && DWLive.getInstance().getViewer() != null
                && !TextUtils.isEmpty(DWLive.getInstance().getViewer().getId())) {
            selfId = DWLive.getInstance().getViewer().getId();
        }
        for (ChatEntity chatEntity : chatEntities) {
            if (chatEntity != null && selfId.equals(chatEntity.getUserId())) {
                continue;
            }
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setChatId(chatEntity.getChatId());
            chatMessage.setUserId(chatEntity.getUserId());
            chatMessage.setUserName(chatEntity.getUserName());
            chatMessage.setUserRole(chatEntity.getUserRole());
            chatMessage.setMessage(chatEntity.getMsg());
            chatMessage.setTime(chatEntity.getTime());
            chatMessage.setAvatar(chatEntity.getUserAvatar());
            chatMessage.setStatus(chatEntity.getStatus());
            publishBarrageOnly(chatMessage);
        }
    }

    public void delChatEntity(String chatId) {
        mChatAdapter.remove(chatId);
    }


    private ChatEntity getChatEntity(ChatMessage msg) {
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setChatId(msg.getChatId());
        chatEntity.setUserId(msg.getUserId());
        chatEntity.setUserName(msg.getUserName());
        chatEntity.setPrivate(!msg.isPublic());
        chatEntity.setUserRole(msg.getUserRole());

        if (msg.getUserId().equals(DWLive.getInstance().getViewer().getId())) {
            chatEntity.setPublisher(true);
        } else {
            chatEntity.setPublisher(false);
        }

        chatEntity.setMsg(msg.getMessage());
        chatEntity.setTime(msg.getTime());
        chatEntity.setUserAvatar(msg.getAvatar());
        chatEntity.setStatus(msg.getStatus());
        return chatEntity;
    }

    /**
     * ??????????????????
     */
    private void showBroadcastMsg(final BroadCastMsg msg) {
        if (msg == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // ??????????????????
                ChatEntity chatEntity = new ChatEntity();
                chatEntity.setChatId(msg.getId());
                chatEntity.setIsBroadcast(true);
                chatEntity.setUserId("");
                chatEntity.setUserName("");
                chatEntity.setPrivate(false);
                chatEntity.setPublisher(true);
                chatEntity.setMsg(String.format("????????????: %s", msg.getContent()));
                chatEntity.setTime("");
                chatEntity.setStatus("0");  // ??????
                chatEntity.setUserAvatar("");
                addChatEntity(chatEntity);
            }
        });
    }

    //------------------------ ?????????????????????????????? ------------------------------------

    // ????????????????????????
    private ArrayList<ChatEntity> list = new ArrayList<>();
    private boolean hasBroadcastMsg = false; // ??????????????????????????????

    // ????????????????????????????????????????????????????????????
    @Override
    public void onHistoryBroadcastMsg(ArrayList<BroadCastMsg> msgs) {
//        hasBroadcastMsg = true;
//        list.clear();
//        final ArrayList<ChatEntity> list1 = mChatAdapter.getChatEntities();
//        for (final BroadCastMsg castMsg : msgs) {
//            ChatEntity chatEntity = new ChatEntity();
//            chatEntity.setChatId(castMsg.getId());
//            chatEntity.setIsBroadcast(true);
//            chatEntity.setUserId("");
//            chatEntity.setUserName("");
//            chatEntity.setPrivate(false);
//            chatEntity.setPublisher(true);
//            chatEntity.setMsg(String.format("????????????: %s", castMsg.getContent()));
//            chatEntity.setTime(String.valueOf(castMsg.getTime()));
//            chatEntity.setStatus("0");  // ??????
//            chatEntity.setUserAvatar("");
//            if (list1.contains(chatEntity)) {
//                continue;
//            }
//            list.add(chatEntity);
//        }

    }

    // ????????????????????????
    @Override
    public void onHistoryChatMessage(final ArrayList<ChatMessage> historyChats) {
        if (historyChats == null || historyChats.size() == 0) {
            return;
        }
        if (!hasBroadcastMsg) {
            list.clear();
        }
        hasBroadcastMsg = false;
        for (int i = 0; i < historyChats.size(); i++) {

            ChatEntity chatEntity = getChatEntity(historyChats.get(i));
            ArrayList<ChatEntity> chatEntities = mChatAdapter.getChatEntities();
            if (chatEntities.contains(chatEntity)) {
                continue;
            }
            if (barrageLayout != null) {
                // ???????????????????????????????????????????????????????????????????????????????????????????????????????????? && ?????????????????????
                if (!ChatImageUtils.isImgChatMessage(historyChats.get(i).getMessage()) && "0".equals(historyChats.get(i).getStatus())) {
                    //???????????????
                    if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        barrageLayout.addNewInfo(historyChats.get(i));
                    } else if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        //?????????????????????
                    }
                }
            }
            list.add(chatEntity);
        }
        // ?????? -->??????????????????????????????????????????????????????????????????try catch
        try {
            Collections.sort(list, new Comparator<ChatEntity>() {
                @Override
                public int compare(ChatEntity o1, ChatEntity o2) {
                    int time = Integer.parseInt(o1.getTime());
                    int time2 = Integer.parseInt(o2.getTime());
                    if (time == time2) {
                        return 0;
                    }
                    if (time > time2) {
                        return 1;
                    } else {
                        return -1;
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ??????????????????????????? ChatMessage ??? currentTime = ""
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // ??????????????????????????????UI
                mChatAdapter.add(list);

            }
        });
    }

    /**
     * ????????????????????????????????????????????????????????????
     */
    private void publishBarrageOnly(final ChatMessage msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (barrageLayout != null) {
                    // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    boolean flag = false;
                    if (DWLive.getInstance() != null
                            && DWLive.getInstance().getViewer() != null
                            && !TextUtils.isEmpty(DWLive.getInstance().getViewer().getId())
                            && !TextUtils.isEmpty(msg.getUserId())
                            && DWLive.getInstance().getViewer().getId().equals(msg.getUserId())) {
                        flag = true;
                    }

                    if (!ChatImageUtils.isImgChatMessage(msg.getMessage()) && getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                            && (flag || "0".equals(msg.getStatus()))) {
                        barrageLayout.addNewInfo(msg);
                    }
                }
            }
        });
    }

    @Override
    public void onPublicChatMessage(final ChatMessage msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (barrageLayout != null) {
                    // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    boolean flag = false;
                    if (DWLive.getInstance() != null
                            && DWLive.getInstance().getViewer() != null
                            && !TextUtils.isEmpty(DWLive.getInstance().getViewer().getId())
                            && !TextUtils.isEmpty(msg.getUserId())
                            && DWLive.getInstance().getViewer().getId().equals(msg.getUserId())) {
                        flag = true;
                    }

                    if (!ChatImageUtils.isImgChatMessage(msg.getMessage()) && getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                            && (flag || "0".equals(msg.getStatus()))) {
                        barrageLayout.addNewInfo(msg);
                    }
                }
                addChatEntity(getChatEntity(msg));
            }
        });
    }

    @Override
    public void onBanDeleteChat(final String userId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mChatAdapter != null) {
                    mChatAdapter.banDeleteChat(userId);
                }
            }
        });
    }

    /**
     * ????????????????????????????????????
     *
     * @param msgStatusJson ??????????????????????????????json
     */
    @Override
    public void onChatMessageStatus(final String msgStatusJson) {
        if (TextUtils.isEmpty(msgStatusJson)) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(msgStatusJson);
                    String status = jsonObject.getString("status");
                    JSONArray chatIdJson = jsonObject.getJSONArray("chatIds");
                    ArrayList<String> chatIds = new ArrayList<>();
                    for (int i = 0; i < chatIdJson.length(); i++) {
                        chatIds.add(chatIdJson.getString(i));
                    }
                    changeChatStatus(status, chatIds);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onSilenceUserChatMessage(final ChatMessage msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addChatEntity(getChatEntity(msg));
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param mode ???????????? 1???????????????  2???????????????
     */
    @Override
    public void onBanChat(final int mode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if (banChatPopup == null) {
//                    banChatPopup = new BanChatPopup(getContext());
//                }
//                if (banChatPopup.isShowing()) {
//                    banChatPopup.onDestroy();
//                }
                banChatMode = mode;
                if (mode == 1) {
//                    banChatPopup.banChat("???????????????");
                } else if (mode == 2) {
//                    banChatPopup.banChat("???????????????");
                    mInput.setHint("???????????????");
                    mInput.setFocusable(false);
                    mInput.setFocusableInTouchMode(false);
                    mChatSend.setSelected(true);
                    banChat();
                }
//                banChatPopup.show(rootView);
            }
        });

    }

    private View rootView;

    public void setPopView(View rootView) {
        this.rootView = rootView;
    }

    /**
     * ????????????????????????
     *
     * @param mode ???????????? 1???????????????  2???????????????
     */
    @Override
    public void onUnBanChat(final int mode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                banChatMode = 0;
//                if (banChatPopup == null) {
//                    banChatPopup = new BanChatPopup(getContext());
//                }
//                if (banChatPopup.isShowing()) {
//                    banChatPopup.onDestroy();
//                }
                if (mode == 1) {
//                    banChatPopup.banChat("??????????????????");
                } else if (mode == 2) {
//                    banChatPopup.banChat("?????????????????????");
                    mInput.setHint(R.string.chat_input_hint);
                    mInput.setFocusable(true);
                    mInput.setFocusableInTouchMode(true);
                    mInput.requestFocus();
                    mChatSend.setSelected(false);
                }
//                banChatPopup.show(rootView);
            }
        });

    }


    @Deprecated
    @Override
    public void onBroadcastMsg(String msg) {
        // showBroadcastMsg(msg);
    }


    /**
     * ??????????????????
     */
    @Override
    public void onBroadcastMsg(BroadCastMsg msg) {
        if (msg.getContent().startsWith("????????????")) {
            onBroadcastClickListener.onBroadcastMsg(msg);
        }else {
            showBroadcastMsg(msg);
        }

    }


    /*??????????????????*/
    @Override
    public void onBroadcastMsgDel(final String id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                delChatEntity(id);
            }
        });

    }

    /***************************** ?????? ******************************/
    private BarrageLayout barrageLayout;

    /**
     * ??????????????????
     */
    public void setBarrageLayout(BarrageLayout layout) {
        barrageLayout = layout;
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        if (height > 10) {
            isSoftInput = true;
            softKeyHeight = height;
            mChatLayout.setTranslationY(-softKeyHeight);
            autoScrollView.setTranslationY(-softKeyHeight);
//            mEmoji.setImageResource(R.drawable.push_chat_emoji_normal);
            isEmojiShow = false;
        } else {
            if (!showEmojiAction) {
                mChatLayout.setTranslationY(0);
                autoScrollView.setTranslationY(0);
                hideEmoji();
            }
            isSoftInput = false;

        }
        //??????????????????
        showEmojiAction = false;
    }

    @Override
    public void HDUserRemindWithAction(UserRedminAction userJoinExitAction) {
        //?????????????????????????????????
        if (userJoinExitAction.getClientType().contains(4)) {
            autoScrollView.addDate(userJoinExitAction);
        }
    }

    @Override
    public void HDBanChatBroadcastWithData(BanChatBroadcast banChatBroadcast) {
        toastOnUiThread("??????" + banChatBroadcast.getUserName() + "????????????");
    }

    public interface OnBroadcastClickListener {
        void onBroadcastMsg(BroadCastMsg msg);
    }
}
