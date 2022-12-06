package tuoyan.com.xinghuo_dayingindex.ui.cc.livegoods;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bokecc.livemodule.live.DWLiveChatListener;
import com.bokecc.livemodule.live.DWLiveCoreHandler;
import com.bokecc.livemodule.live.UserListener;
import com.bokecc.livemodule.live.chat.KeyboardHeightObserver;
import com.bokecc.livemodule.live.chat.OnChatComponentClickListener;
import com.bokecc.livemodule.live.chat.barrage.BarrageLayout;
import com.bokecc.livemodule.live.chat.module.ChatEntity;
import com.bokecc.livemodule.live.chat.window.BanChatPopup;
import com.bokecc.livemodule.utils.ChatImageUtils;
import com.bokecc.livemodule.view.AutoScrollView;
import com.bokecc.livemodule.view.BaseRelativeLayout;
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

import tuoyan.com.xinghuo_dayingindex.R;
import tuoyan.com.xinghuo_dayingindex.widegt.like.HintLikeView;

/**
 * 直播间聊天展示控件（公共聊天）
 */
public class LiveGoodsChatComponent extends BaseRelativeLayout implements DWLiveChatListener, KeyboardHeightObserver, UserListener {

    private final static String TAG = "LiveChatComponent";

    private RecyclerView mChatList;
    private TextView tvInput;
    private LinearLayout ll_input;
    private RelativeLayout rlGoodsList;
    private TextView tvGoodsNum;
    private HintLikeView ivLikes;

    // 软键盘是否显示
    private boolean isSoftInput = false;
    // emoji是否需要显示
    private boolean isEmojiShow = false;
    // 聊天是否显示
    private boolean isChat = false;

    // 公共聊天适配器
    private LiveGoodsPublicChatAdapter mChatAdapter;

    // 软键盘监听
    private InputMethodManager mImm;

    // 定义当前支持的最大的可输入的文字数量
    private short maxInput = 300;

    private OnChatComponentClickListener mChatComponentClickListener;
    private OnGoodsChatComponentClickListener onGoodsChatComponentClickListener;
    //软键盘的高度
    private int softKeyHeight;
    private boolean showEmojiAction = false;
    private BanChatPopup banChatPopup;
    private AutoScrollView autoScrollView;
    private int banChatMode = 0;//禁言类型 1：个人禁言  2：全员禁言
    //    private CommentDialog commentDialog;
    private BottomSheetTextDialog bottomSheetTextDialog;

    public void setOnChatComponentClickListener(OnChatComponentClickListener listener) {
        mChatComponentClickListener = listener;
    }

    public void setOnGoodsChatComponentClickListener(OnGoodsChatComponentClickListener onGoodsChatComponentClickListener) {
        this.onGoodsChatComponentClickListener = onGoodsChatComponentClickListener;
    }

    public LiveGoodsChatComponent(Context context) {
        super(context);
        initChat();
    }

    public LiveGoodsChatComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initChat();
    }

    public void initViews() {
        LayoutInflater.from(mContext).inflate(R.layout.live_goods_portrait_chat_layout, this, true);
        mChatList = findViewById(R.id.chat_container);


        tvInput = findViewById(R.id.tv_input);
        ll_input = findViewById(R.id.ll_input);
        rlGoodsList = findViewById(R.id.rl_goods_list);
        tvGoodsNum = findViewById(R.id.tv_goods_num);
        ivLikes = findViewById(R.id.iv_likes);


        //用户进入动画
        autoScrollView = findViewById(R.id.auto_scroll_view);

        //点击输入框按钮弹出输入框
        tvInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "点击输入框", Toast.LENGTH_SHORT).show();
//                mChatLayout .setVisibility(VISIBLE);
//                mInput.setHint(R.string.chat_input_hint);
//                mInput.setFocusable(true);
//                mInput.setFocusableInTouchMode(true);
//                mInput.requestFocus();
//              new InputDialog(mContext,item->{
//               Toast.makeText(mContext,item,Toast.LENGTH_SHORT).show();
//                  DWLive.getInstance().sendPublicChatMsg(item);
//                  return null;
//              }).show();
                if (banChatMode == 0) {


                    BottomSheetTextDialog bottomSheetTextDialog = new BottomSheetTextDialog(mContext);

                    bottomSheetTextDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            Log.i("TAG", "键盘code---" + keyCode);
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                dialog.dismiss();
                                return false;
                            } else if (keyCode == KeyEvent.KEYCODE_DEL) {//删除键
                                return false;
                            } else {
                                return true;

                            }
                        }
                    });
                    bottomSheetTextDialog.setOnTextFinishListener(new BottomSheetTextDialog.OnTextFinishListener() {
                        @Override
                        public void onText(String message) {
                            if (banChatMode == 0) {
                                if (TextUtils.isEmpty(message)) {
                                    toastOnUiThread("聊天内容不能为空");
                                    return;
                                }
                                DWLive.getInstance().sendPublicChatMsg(message);

                            }
                        }
                    });
                    bottomSheetTextDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ll_input.setVisibility(View.VISIBLE);
                        }
                    });
                    if (bottomSheetTextDialog != null) {
                        bottomSheetTextDialog.show();
                        ll_input.setVisibility(GONE);
                    }


                }


            }
        });
        //点击商品列表
        rlGoodsList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "点击商品列表", Toast.LENGTH_SHORT).show();
                onGoodsChatComponentClickListener.onClickGoodsList();
            }
        });      //点击点赞
        ivLikes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "点击点赞", Toast.LENGTH_SHORT).show();
                onGoodsChatComponentClickListener.onClickLikes();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initChat() {
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mChatList.setLayoutManager(new LinearLayoutManager(mContext));
        mChatAdapter = new LiveGoodsPublicChatAdapter(mContext);
        mChatList.setAdapter(mChatAdapter);

        mChatAdapter.setOnChatImageClickListener(new LiveGoodsPublicChatAdapter.onChatComponentClickListener() {
            @Override
            public void onChatComponentClick(View view, Bundle bundle) {
                if (mChatComponentClickListener != null) {
                    mChatComponentClickListener.onClickChatComponent(bundle);
                }
            }
        });

        mChatAdapter.setOnItemClickListener(new LiveGoodsPublicChatAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ChatEntity chatEntity = mChatAdapter.getChatEntities().get(position);
                // 判断聊天的角色，目前机制只支持和主讲、助教、主持人进行私聊
                // 主讲（publisher）、助教（teacher）、主持人（host）、学生或观众（student）、其他没有角色（unknow）
                if (chatEntity.getUserRole() == null || "student".equals(chatEntity.getUserRole()) || "unknow".equals(chatEntity.getUserRole()) || !DWLive.getInstance().getRoomInfo().getPrivateChat().equals("1")) {
                    Log.w(TAG, "只支持和主讲、助教、主持人进行私聊");
                    //toastOnUiThread("只支持和主讲、助教、主持人进行私聊");
                    return;
                }
                // 调用DWLiveCoreHandler.jump2PrivateChat方法，通知私聊控件，展示和此聊天相关的私聊内容列表
                DWLiveCoreHandler dwLiveCoreHandler = DWLiveCoreHandler.getInstance();
                if (dwLiveCoreHandler != null) {
                    dwLiveCoreHandler.jump2PrivateChat(chatEntity);
                }
            }
        });

//        mChatList.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                mChatLayout.setTranslationY(0);
//                autoScrollView.setTranslationY(0);
//                hideKeyboard();
//                return false;
//            }
//        });

        initChatView();

        DWLiveCoreHandler dwLiveCoreHandler = DWLiveCoreHandler.getInstance();
        if (dwLiveCoreHandler != null) {
            dwLiveCoreHandler.setDwLiveChatListener(this);
            dwLiveCoreHandler.setUserListener(this);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    public void initChatView() {
//
//
//        EmojiAdapter emojiAdapter = new EmojiAdapter(mContext);
//        emojiAdapter.bindData(EmojiUtil.imgs);
//        if (commentDialog==null) {
//            commentDialog = new CommentDialog(mContext);
//        }


    }


    public boolean onBackPressed() {
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

    /*** 修改聊天内容的显示状态（0：显示  1：不显示）***/
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
     * 展示广播内容
     */
    private void showBroadcastMsg(final BroadCastMsg msg) {
//        if (msg == null) {
//            return;
//        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // 构建一个对象
//                ChatEntity chatEntity = new ChatEntity();
//                chatEntity.setChatId(msg.getId());
//                chatEntity.setIsBroadcast(true);
//                chatEntity.setUserId("");
//                chatEntity.setUserName("");
//                chatEntity.setPrivate(false);
//                chatEntity.setPublisher(true);
//                chatEntity.setMsg(String.format("系统消息: %s", msg.getContent()));
//                chatEntity.setTime("");
//                chatEntity.setStatus("0");  // 显示
//                chatEntity.setUserAvatar("");
//                addChatEntity(chatEntity);
//            }
//        });
    }

    //------------------------ 处理直播聊天回调信息 ------------------------------------

    // 获取历史消息集合
    private ArrayList<ChatEntity> list = new ArrayList<>();
    private boolean hasBroadcastMsg = false; // 主要为了防止数据重复

    // 收到历史广播信息，在收到历史消息之前处理
    @Override
    public void onHistoryBroadcastMsg(ArrayList<BroadCastMsg> msgs) {
        Log.d(TAG, "onHistoryBroadcastMsg: " + msgs.get(0).getContent());
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
//            chatEntity.setMsg(String.format("系统消息: %s", castMsg.getContent()));
//            chatEntity.setTime(String.valueOf(castMsg.getTime()));
//            chatEntity.setStatus("0");  // 显示
//            chatEntity.setUserAvatar("");
//            if (list1.contains(chatEntity)) {
//                continue;
//            }
//            list.add(chatEntity);
//        }

    }

    // 收到历史聊天信息
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
                // 聊天支持发送图片，需要判断聊天内容是否为图片，如果不是图片，再添加到弹幕 && 聊天状态为显示
                if (!ChatImageUtils.isImgChatMessage(historyChats.get(i).getMessage()) && "0".equals(historyChats.get(i).getStatus())) {
                    //判断横竖屏
                    if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        barrageLayout.addNewInfo(historyChats.get(i));
                    } else if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        //竖屏不需要添加
                    }
                }
            }
            list.add(chatEntity);
        }
        // 排序 -->兼容历史服务器版本处理，服务器最新版不需要加try catch
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
        // 注：历史聊天信息中 ChatMessage 的 currentTime = ""
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 将历史聊天信息添加到UI
                mChatAdapter.add(list);
                if (mChatAdapter.getItemCount() - 1 > 0) {
                    mChatList.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
                }

            }
        });
    }

    /**
     * 接收到聊天信息状态改变消息，重新发送弹幕
     */
    private void publishBarrageOnly(final ChatMessage msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (barrageLayout != null) {
                    // 聊天支持发送图片，需要判断聊天内容是否为图片，如果不是图片，再添加到弹幕
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
                    // 聊天支持发送图片，需要判断聊天内容是否为图片，如果不是图片，再添加到弹幕
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
     * 收到聊天信息状态管理事件
     *
     * @param msgStatusJson 聊天信息状态管理事件json
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
                banChatMode = 1;
                tvInput.setText("被禁言中");
                tvInput.setEnabled(false);
//                addChatEntity(getChatEntity(msg));
            }
        });
    }

    /**
     * 收到禁言事件
     *
     * @param mode 禁言类型 1：个人禁言  2：全员禁言
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
                    tvInput.setText("被禁言中");
                    tvInput.setEnabled(false);
//                    banChatPopup.banChat("个人被禁言");
                } else if (mode == 2) {
//                    banChatPopup.banChat("全员被禁言");
//                    mInput.setHint("全员禁言中");
//                    mInput.setFocusable(false);
//                    mInput.setFocusableInTouchMode(false);
//                    mChatSend.setSelected(true);
//                    banChat();
                    tvInput.setText("全员禁言中");
                    tvInput.setEnabled(false);
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
     * 收到解除禁言事件
     *
     * @param mode 禁言类型 1：个人禁言  2：全员禁言
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
                    tvInput.setText("说点什么...");
                    tvInput.setEnabled(true);
//                    banChatPopup.banChat("解除个人禁言");
                } else if (mode == 2) {
//                    banChatPopup.banChat("解除全员被禁言");
//                    mInput.setHint(R.string.chat_input_hint);
//                    mInput.setFocusable(true);
//                    mInput.setFocusableInTouchMode(true);
//                    mInput.requestFocus();
//                    mChatSend.setSelected(false);
                    tvInput.setText("说点什么...");
                    tvInput.setEnabled(true);
                }
//                banChatPopup.show(rootView);
            }
        });

    }


    @Deprecated
    @Override
    public void onBroadcastMsg(String msg) {
        Log.d(TAG, "onHistoryBroadcastMsg3: " + msg);
        // showBroadcastMsg(msg);
    }


    /**
     * 收到广播信息
     */
    @Override
    public void onBroadcastMsg(BroadCastMsg msg) {
        Log.d(TAG, "onHistoryBroadcastMsg2: " + msg.getContent());
        onGoodsChatComponentClickListener.onBroadcastMsg(msg);
        showBroadcastMsg(msg);
    }


    /*删除广播消息*/
    @Override
    public void onBroadcastMsgDel(final String id) {
        Log.d(TAG, "onBroadcastMsgDel: " + id);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                delChatEntity(id);
            }
        });

    }

    /***************************** 弹幕 ******************************/
    private BarrageLayout barrageLayout;

    /**
     * 设置弹幕组件
     */
    public void setBarrageLayout(BarrageLayout layout) {
        barrageLayout = layout;
    }

//    @Override
//    public void onKeyboardHeightChanged(int height, int orientation) {
////        Log.d(TAG, "onKeyboardHeightChanged: "+height);
//        if (height > 10) {
////            if (!showEmojiAction) {
////                mChatLayout.setTranslationY(0);
////                autoScrollView.setTranslationY(0);
////                hideEmoji();
////            }
//            isSoftInput = true;
//            softKeyHeight = height;
////            mChatLayout.setTranslationY(softKeyHeight);
////            autoScrollView.setTranslationY(softKeyHeight);
////            mEmoji.setImageResource(R.drawable.push_chat_emoji_normal);
//            isEmojiShow = false;
//        } else {
//            if (!showEmojiAction) {
//                mChatLayout.setTranslationY(0);
//                autoScrollView.setTranslationY(0);
//                hideEmoji();
//            }
//            isSoftInput = false;
//
//        }
//        //结束动作指令
//        showEmojiAction = false;
//    }

    @Override
    public void HDUserRemindWithAction(UserRedminAction userJoinExitAction) {
        //判断观众段是否需要接受
        if (userJoinExitAction.getClientType().contains(4)) {
            autoScrollView.addDate(userJoinExitAction);
        }
    }

    @Override
    public void HDBanChatBroadcastWithData(BanChatBroadcast banChatBroadcast) {
        toastOnUiThread("用户" + banChatBroadcast.getUserName() + "被禁言了");
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        Log.d(TAG, "onKeyboardHeightChanged: " + height);
        if (height > 10) {
            mChatList.setTranslationY(-(height + 20));
        } else {
            mChatList.setTranslationY(0);

        }
    }

}
