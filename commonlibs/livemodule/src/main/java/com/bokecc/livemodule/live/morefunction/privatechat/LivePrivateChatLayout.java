package com.bokecc.livemodule.live.morefunction.privatechat;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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

import com.bokecc.livemodule.R;
import com.bokecc.livemodule.live.chat.adapter.EmojiAdapter;
import com.bokecc.livemodule.live.chat.module.ChatEntity;
import com.bokecc.livemodule.live.chat.module.ChatUser;
import com.bokecc.livemodule.live.chat.module.PrivateUser;
import com.bokecc.livemodule.live.chat.util.BaseOnItemTouch;
import com.bokecc.livemodule.live.chat.util.EmojiUtil;
import com.bokecc.livemodule.live.chat.util.SoftKeyBoardState;
import com.bokecc.livemodule.live.morefunction.privatechat.adapter.PrivateChatAdapter;
import com.bokecc.livemodule.live.morefunction.privatechat.adapter.PrivateUserAdapter;
import com.bokecc.livemodule.view.BaseRelativeLayout;
import com.bokecc.livemodule.view.CustomToast;
import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.pojo.PrivateChatInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 私聊界面
 */
public class LivePrivateChatLayout extends BaseRelativeLayout {

    private LinearLayout mPrivateChatUserLayout;
    private LinearLayout mPrivateChatMsgLayout;

    private RecyclerView mPrivateChatUserList;
    private RecyclerView mPrivateChatMsgList;

    private TextView mPrivateChatUserName;
    private ImageView mPrivateChatBack;
    private ImageView mPrivateChatClose;
    private ImageView mPrivateChatUserClose;

    private RelativeLayout mPrivateChatInputLayout;
    private Button mChatSend;
    private EditText mInput;
    private ImageView mEmoji;
    private GridView mEmojiGrid;

    private SoftKeyBoardState mSoftKeyBoardState;
    private InputMethodManager mImm;

    // 私聊用户列表适配器
    private PrivateUserAdapter mPrivateUserAdapter;
    // 私聊信息列表
    private PrivateChatAdapter mPrivateChatAdapter;
    // 存放所有的私聊信息
    private ArrayList<ChatEntity> mPrivateChats;
    // 当前私聊的用户Id
    private String mCurPrivateUserId = "";
    // 定义当前支持的最大的可输入的文字数量
    private short maxInput = 300;
    // emoji是否显示
    private boolean isEmojiShow = false;
    // 聊天id与信息映射表
    private Map<String, ChatEntity> mChaIdMap;

    //软键盘的高度
    private int softKeyHeight;


    public LivePrivateChatLayout(Context context) {
        super(context);
        initPrivateChatView();
    }

    public LivePrivateChatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPrivateChatView();
    }

    public LivePrivateChatLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPrivateChatView();
    }



    public void initViews() {
        LayoutInflater.from(mContext).inflate(R.layout.live_portrait_private_chat, this, true);
        mPrivateChatUserLayout = findViewById(R.id.id_private_chat_user_layout);
        mPrivateChatMsgLayout = findViewById(R.id.id_private_chat_msg_layout);
        mPrivateChatInputLayout = findViewById(R.id.id_push_chat_layout);
        mPrivateChatUserList = findViewById(R.id.id_private_chat_user_list);
        mPrivateChatMsgList = findViewById(R.id.id_private_chat_list);
        mPrivateChatUserName = findViewById(R.id.id_private_chat_title);
        mPrivateChatClose = findViewById(R.id.id_private_chat_close);
        mPrivateChatUserClose = findViewById(R.id.id_private_chat_user_close);
        mPrivateChatBack = findViewById(R.id.id_private_chat_back);
        mChatSend = findViewById(R.id.id_push_chat_send);
        mInput = findViewById(R.id.id_push_chat_input);
        mEmoji = findViewById(R.id.id_push_chat_emoji);
        mEmojiGrid = findViewById(R.id.id_push_emoji_grid);

        mPrivateChatUserLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
                hideKeyboard();
            }
        });

        mPrivateChatMsgLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
                hideKeyboard();
            }
        });

        // 私聊内容列表回退按钮 点击-->隐藏私聊内容列表，展示私聊用户列表
        mPrivateChatBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                mPrivateChatInputLayout.setTranslationY(0);
                mPrivateChatMsgLayout.setVisibility(GONE);
                mPrivateChatUserLayout.setVisibility(VISIBLE);
                mPrivateChatInputLayout.setVisibility(GONE);
            }
        });

        // 私聊用户列表关闭按钮
        mPrivateChatUserClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                mPrivateChatInputLayout.setTranslationY(0);
                mPrivateChatMsgLayout.setVisibility(GONE);
                mPrivateChatUserLayout.setVisibility(VISIBLE);
                mPrivateChatInputLayout.setVisibility(GONE);
                setVisibility(GONE);
            }
        });

        // 私聊内容列表关闭按钮
        mPrivateChatClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                mPrivateChatInputLayout.setTranslationY(0);
                mPrivateChatMsgLayout.setVisibility(GONE);
                mPrivateChatUserLayout.setVisibility(VISIBLE);
                mPrivateChatInputLayout.setVisibility(GONE);

                setVisibility(GONE);
            }
        });

        // 聊天表情
        mEmoji.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmojiShow){
                    hideEmoji();
                    boolean b = mImm.showSoftInput(mInput, 0);
                    if (!b){
                        //如果没有打开软键盘 需要将输入框移动到底部
                        mPrivateChatInputLayout.setTranslationY(0);
                    }
                }else{
                    showEmoji();
                    mImm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
                }
            }
        });




        // 私聊发送按钮
        mChatSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mInput.getText().toString().trim();
                if (TextUtils.isEmpty(msg)) {
                    toastOnUiThread("聊天内容不能为空");
                    return;
                }
                DWLive.getInstance().sendPrivateChatMsg(mTo.getUserId(), msg);
                clearChatInput();
            }
        });
    }

    public void clearChatInput() {
        mInput.setText("");
        hideKeyboard();
    }

    public void hideKeyboard() {
        hideEmoji();
        mImm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
        mPrivateChatInputLayout.setTranslationY(0);
    }

    /**
     * 显示emoji
     */
    public void showEmoji() {
        if (mEmojiGrid.getHeight() != softKeyHeight && softKeyHeight != 0) {
            ViewGroup.LayoutParams lp = mEmojiGrid.getLayoutParams();
            lp.height = softKeyHeight;
            mEmojiGrid.setLayoutParams(lp);
        }
        mEmojiGrid.setVisibility(View.VISIBLE);
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setRepeatMode(Animation.REVERSE);
        mShowAction.setDuration(100);
        mEmojiGrid.startAnimation(mShowAction);
        mEmoji.setImageResource(R.drawable.push_chat_emoji);
        isEmojiShow = true;
        final float[] transY = new float[1];
        if(softKeyHeight == 0){
            //这一步主要针对首次进入私聊界面 不弹出软键盘 直接打开表情
            if (mEmojiGrid.getHeight()!=0){
                transY[0] = -mEmojiGrid.getHeight();
                mPrivateChatInputLayout.setTranslationY(transY[0]);
            }else{
                ViewTreeObserver observer = mEmojiGrid.getViewTreeObserver();
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mEmojiGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        transY[0] = mEmojiGrid.getMeasuredHeight();
                        mPrivateChatInputLayout.setTranslationY(-transY[0]);
                    }
                });
            }
        }else {
            transY[0] = -softKeyHeight;
            mPrivateChatInputLayout.setTranslationY(transY[0]);
        }

    }


    /**
     * 隐藏emoji
     */
    public void hideEmoji() {
        mEmoji.setImageResource(R.drawable.push_chat_emoji_normal);
        isEmojiShow = false;
        mEmojiGrid.setVisibility(View.GONE);
    }

    public void initPrivateChatView() {
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mChaIdMap = new HashMap<>();
        mInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideEmoji();
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
                    CustomToast.showToast(mContext, "字数超过300字", Toast.LENGTH_SHORT);
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
                // 一个表情span占位8个字符
                if (mInput.getText().length() + 8 > maxInput) {
                    toastOnUiThread("字符数超过300字");
                    return;
                }
                if (position == EmojiUtil.imgs.length - 1) {
                    EmojiUtil.deleteInputOne(mInput);
                } else {
                    EmojiUtil.addEmoji(mContext, mInput, position);
                }
            }
        });
        mSoftKeyBoardState = new SoftKeyBoardState(this, false);
        mPrivateChats = new ArrayList<>(); // 初始化私聊数据集合
        mPrivateChatUserList.setLayoutManager(new LinearLayoutManager(mContext));
        mPrivateUserAdapter = new PrivateUserAdapter(mContext);
        mPrivateChatUserList.setAdapter(mPrivateUserAdapter);

        mPrivateChatUserList.addOnItemTouchListener(new BaseOnItemTouch(mPrivateChatUserList, new com.bokecc.livemodule.live.chat.util.OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                hideKeyboard();
                // 隐藏用户列表
                mPrivateChatUserLayout.setVisibility(View.GONE);
                int position = mPrivateChatUserList.getChildAdapterPosition(viewHolder.itemView);
                PrivateUser privateUser = mPrivateUserAdapter.getPrivateUsers().get(position);
                privateUser.setRead(true);
                mPrivateUserAdapter.notifyDataSetChanged();

                ChatEntity chatEntity = new ChatEntity();
                chatEntity.setUserId(privateUser.getId());
                chatEntity.setUserName(privateUser.getName());
                chatEntity.setUserAvatar(privateUser.getAvatar());
                click2PrivateChat(chatEntity, true);
            }
        }));

        mPrivateChatMsgList.setLayoutManager(new LinearLayoutManager(mContext));
        mPrivateChatAdapter = new PrivateChatAdapter(mContext);
        mPrivateChatMsgList.setAdapter(mPrivateChatAdapter);

        mPrivateChatMsgList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mPrivateChatInputLayout.setTranslationY(0);
                hideKeyboard();
                return false;
            }
        });

    }

    /**
     * 点击发起私聊
     */
    private void click2PrivateChat(ChatEntity chatEntity, boolean flag) {
        if (flag) { // 私聊用户列表点击发起私聊
            goPrivateChat(chatEntity);
            mCurPrivateUserId = chatEntity.getUserId();
        } else {
            if (!chatEntity.isPublisher()) { // 如果当前被点击的用户不是主播，则进行私聊
                mPrivateChatUserLayout.setVisibility(View.GONE);
                goPrivateChat(chatEntity);
                mCurPrivateUserId = chatEntity.getUserId();
            }
        }
    }

    // 私聊对象
    private ChatUser mTo;

    /**
     * 跳转私聊
     */
    private void goPrivateChat(ChatEntity chatEntity) {
        mTo = null;
        mTo = new ChatUser();
        mTo.setUserId(chatEntity.getUserId());
        mTo.setUserName(chatEntity.getUserName());
        ArrayList<ChatEntity> toChatEntitys = new ArrayList<>();
        for (ChatEntity entity : mPrivateChats) {
            // 从私聊列表里面读取到 当前发起私聊的俩个用户聊天列表
            if (entity.getUserId().equals(chatEntity.getUserId()) || entity.getReceiveUserId().equals(chatEntity.getUserId())) {
                toChatEntitys.add(entity);
            }
        }
        mPrivateChatAdapter.setDatas(toChatEntitys);
        showPrivateChatMsgList(chatEntity.getUserName());
    }

    /**
     * 显示私聊信息列表
     */
    public void showPrivateChatMsgList(final String username) {
        TranslateAnimation animation = new TranslateAnimation(1f, 1f, 0f, 1f);
        animation.setDuration(300L);
        mPrivateChatUserName.setText(username);
        mPrivateChatMsgLayout.startAnimation(animation);
        mPrivateChatMsgLayout.setVisibility(View.VISIBLE);
        if (mPrivateChatAdapter.getItemCount() - 1 > 0) {
            mPrivateChatMsgList.smoothScrollToPosition(mPrivateChatAdapter.getItemCount() - 1);// 进行定位
        }
        mPrivateChatInputLayout.setVisibility(VISIBLE);
    }

    /**
     * 更新私聊信息列表，私聊用户列表
     */
    public void updatePrivateChat(ChatEntity chatEntity) {
        boolean isReaded = false;
        // 如果当前界面是私聊信息界面直接在该界面进行数据更新
        if (mPrivateChatMsgList.getVisibility() == VISIBLE && (chatEntity.isPublisher() || chatEntity.getUserId().equals(mCurPrivateUserId))) {
            mPrivateChatAdapter.add(chatEntity);
            mPrivateChatMsgList.smoothScrollToPosition(mPrivateChatAdapter.getItemCount() - 1);// 进行定位
            isReaded = true;
        }
        PrivateUser privateUser = new PrivateUser();
        if (chatEntity.isPublisher()) {
            privateUser.setId(chatEntity.getReceiveUserId());
            privateUser.setName(chatEntity.getReceivedUserName());
            privateUser.setAvatar(chatEntity.getReceiveUserAvatar());
        } else {
            privateUser.setId(chatEntity.getUserId());
            privateUser.setName(chatEntity.getUserName());
            privateUser.setAvatar(chatEntity.getUserAvatar());
        }

        // 根据公聊传过来的聊天内容，补齐一些参数
        ChatEntity tempChatEntity = mChaIdMap.get(privateUser.getId());
        if (tempChatEntity != null) {
            privateUser.setRole(tempChatEntity.getUserRole());
            privateUser.setName(tempChatEntity.getUserName());
        } else {
            privateUser.setRole(chatEntity.getUserRole());
        }
        privateUser.setMsg(chatEntity.getMsg());
        privateUser.setTime(chatEntity.getTime());
        privateUser.setRead(isReaded);
        mPrivateUserAdapter.add(privateUser);
        mPrivateChats.add(chatEntity);
    }

    private ChatEntity getChatEntity(PrivateChatInfo info, boolean isPublisher) {
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setUserId(info.getFromUserId());
        chatEntity.setUserName(info.getFromUserName());
        chatEntity.setPrivate(true);
        chatEntity.setReceiveUserId(info.getToUserId());
        chatEntity.setUserRole(info.getFromUserRole());
        chatEntity.setReceivedUserName(info.getToUserName());
        chatEntity.setReceiveUserAvatar("");
        chatEntity.setPublisher(isPublisher);
        chatEntity.setMsg(info.getMsg());
        chatEntity.setTime(info.getTime());
        chatEntity.setUserAvatar("");
        return chatEntity;
    }

    // 收到跳转到私聊列表的通知
    public void jump2PrivateChat(ChatEntity chatEntity) {
        toastOnUiThread("展示私聊");
        mChaIdMap.put(chatEntity.getUserId(), chatEntity);
        mTo = null;
        mTo = new ChatUser();
        mTo.setUserId(chatEntity.getUserId());
        mTo.setUserName(chatEntity.getUserName());
        mTo.setUserRole(chatEntity.getUserRole());
        goPrivateChat(chatEntity);
        // 更新当前私聊的用户的id
        mCurPrivateUserId = chatEntity.getUserId();
    }

    // 收到别人私聊我的消息
    public void onPrivateChat(final PrivateChatInfo info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updatePrivateChat(getChatEntity(info, false));
            }
        });
    }

    // 我发出的私聊的消息
    public void onPrivateChatSelf(final PrivateChatInfo info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updatePrivateChat(getChatEntity(info, true));
            }
        });
    }

    /**
     *
     */
    public void onKeyboardHeightChanged(int height, int orientation) {
        if (height > 10) {
            softKeyHeight = height;
            mPrivateChatInputLayout.setTranslationY(-softKeyHeight);
        } else {
            if(!isEmojiShow){
                mPrivateChatInputLayout.setTranslationY(0);
            }
        }
    }


    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (mPrivateChatUserLayout.getVisibility() == VISIBLE) {
                mPrivateChatInputLayout.setVisibility(View.GONE);
            }

            if (mPrivateChatMsgLayout.getVisibility() == VISIBLE) {
                mPrivateChatInputLayout.setVisibility(View.VISIBLE);
            }
        }
        super.setVisibility(visibility);
    }
}