package com.bokecc.livemodule.localplay.chat.adapter;

import static com.bokecc.livemodule.live.chat.adapter.LivePublicChatAdapter.regular;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bokecc.livemodule.R;
import com.bokecc.livemodule.live.chat.module.ChatEntity;
import com.bokecc.livemodule.live.chat.util.EmojiUtil;
import com.bokecc.livemodule.utils.ChatImageUtils;
import com.bokecc.livemodule.utils.UserRoleUtils;
import com.bokecc.livemodule.view.HeadView;
import com.bokecc.sdk.mobile.live.replay.DWLiveLocalReplay;
import com.bokecc.sdk.mobile.live.replay.DWLiveReplay;
import com.bokecc.sdk.mobile.live.replay.pojo.ReplayLiveInfo;
import com.bokecc.sdk.mobile.live.replay.pojo.Viewer;
import com.bumptech.glide.Glide;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalReplayChatAdapter extends RecyclerView.Adapter<LocalReplayChatAdapter.ChatViewHolder> {

    private final Pattern pattern;
    private final Context mContext;
    private ArrayList<ChatEntity> mChatEntities;
    private final LayoutInflater mInflater;
    private final String selfId;
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat sdf;
    // 获取到直播间信息
    private ReplayLiveInfo liveInfo;
    private Calendar cal = Calendar.getInstance();
    // 获取直播间信息，用于计算问答时间
    private DWLiveLocalReplay dwLiveCoreHandler = DWLiveLocalReplay.getInstance();

    private OnChatComponentClickListener mChatcomponentClickListener;

    public static final String CONTENT_IMAGE_COMPONENT = "content_image";
    public static final String CONTENT_ULR_COMPONET = "content_url";

    public interface OnChatComponentClickListener {
        void OnChatComponentClickListener(View view, Bundle bundle);
    }

    public void setOnChatcomponentClickListener(OnChatComponentClickListener listener) {
        mChatcomponentClickListener = listener;
    }


    public LocalReplayChatAdapter(Context context) {
        mChatEntities = new ArrayList<>();
        mContext = context;
        mInflater = LayoutInflater.from(context);
        Viewer viewer = DWLiveReplay.getInstance().getViewer();
        if (viewer == null) {
            selfId = "";
        } else {
            selfId = viewer.getId();
        }
        pattern = Pattern.compile(regular, Pattern.CASE_INSENSITIVE);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf = new SimpleDateFormat("HH:mm:ss");
        if (dwLiveCoreHandler != null) {
            liveInfo = dwLiveCoreHandler.getReplayLiveInfo();
        }
    }

    /**
     * 清空聊天数据
     */
    public void clearChatData() {
        this.mChatEntities = new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * 添加数据，用于回放的添加
     */
    public void add(ArrayList<ChatEntity> mChatEntities) {
        this.mChatEntities = mChatEntities;
        notifyDataSetChanged();
    }

    /**
     * 添加数据，用于回放的添加
     */
    public void append(ArrayList<ChatEntity> mChatEntities) {
        this.mChatEntities.addAll(mChatEntities);
        // 当消息达到300条的时候，移除最早的消息
        while (this.mChatEntities.size() > 300) {
            this.mChatEntities.remove(0);
        }
        notifyDataSetChanged();
    }

    // 获取当前的聊天数量
    public int getChatListSize() {
        return mChatEntities == null ? 0 : mChatEntities.size();
    }

    /**
     * 添加数据
     */
    public void add(ChatEntity chatEntity) {
        mChatEntities.add(chatEntity);
        if (mChatEntities.size() > 300) { // 当消息达到300条的时候，移除最早的消息
            mChatEntities.remove(0);
        }
        notifyDataSetChanged();
    }

    @Override
    public LocalReplayChatAdapter.ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == selfType) {
            // 展示自己发出去的公聊
            itemView = mInflater.inflate(R.layout.live_portrait_chat_single, parent, false);
            return new ChatViewHolder(itemView);
        } else if (viewType == otherType) {
            // 展示收到的别人发出去的公聊
            itemView = mInflater.inflate(R.layout.live_portrait_chat_single, parent, false);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
            return new ChatViewHolder(itemView);
        } else {
            // 展示收到的广播消息
            itemView = mInflater.inflate(R.layout.live_protrait_system_broadcast, parent, false);
            return new ChatViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final LocalReplayChatAdapter.ChatViewHolder holder, int position) {
        final ChatEntity chatEntity = mChatEntities.get(position);

        // 判断是是否是广播，如果是，就展示广播信息
        if (chatEntity.getUserId().isEmpty() && chatEntity.getUserName().isEmpty() && !chatEntity.isPrivate()
                && chatEntity.isPublisher() && chatEntity.getTime().isEmpty() && chatEntity.getUserAvatar().isEmpty()) {
            // 展示广播信息
            holder.mBroadcast.setText(chatEntity.getMsg());
        } else {
            try {
                SpannableString name = new SpannableString((URLDecoder.decode(chatEntity.getUserName(), "UTF-8")));
                name.setSpan(UserRoleUtils.getUserRoleColorSpan(chatEntity.getUserRole()), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tv_name.setText(name);
                if (chatEntity.getTime().contains(":")) {
                    holder.tv_time.setText(chatEntity.getTime());
                } else {
                    int sendTime = Integer.valueOf(chatEntity.getTime());
                    if (sendTime > 0) {
                        if (liveInfo==null){
                            liveInfo = dwLiveCoreHandler.getReplayLiveInfo();
                        }
                        long time = Long.parseLong(liveInfo.getStartTime()) + sendTime * 1000L;
//                        Date date = simpleDateFormat.parse(liveInfo.getStartTime());
                        cal.setTimeInMillis(time);
                        holder.tv_time.setText(sdf.format(cal.getTime()));
                    } else {
                        holder.tv_time.setText(sdf.format(new Date()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 展示聊天信息
            // 1. 判断聊天内容是否是图片，如果是就提取出图片链接地址，然后展示图片
            if (ChatImageUtils.isImgChatMessage(chatEntity.getMsg())) {
                holder.mContent.setVisibility(View.GONE);
                holder.mChatImg.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(ChatImageUtils.getImgUrlFromChatMessage(chatEntity.getMsg())).into(holder.mChatImg);
                holder.mChatImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mChatcomponentClickListener != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("type", CONTENT_IMAGE_COMPONENT);
                            bundle.putString("url", ChatImageUtils.getImgUrlFromChatMessage(chatEntity.getMsg()));
                            mChatcomponentClickListener.OnChatComponentClickListener(holder.mChatImg, bundle);
                        }
                    }
                });
            } else {
                String msg = chatEntity.getMsg();
                String url = null;
                int start = -1;
                int end = -1;
                Matcher matcher = pattern.matcher(msg);
                if (matcher.find()) {
                    start = matcher.start();
                    end = matcher.end();
                    url = matcher.group();
                }
                SpannableString ss = new SpannableString(msg);
                ss.setSpan(new ForegroundColorSpan(Color.parseColor("#1E1F21")), 0, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (url != null) {
                    final String finalUrl = url;
                    ss.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            if (mChatcomponentClickListener != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("type", CONTENT_ULR_COMPONET);
                                bundle.putString("url", finalUrl);
                                mChatcomponentClickListener.OnChatComponentClickListener(widget, bundle);
                            }
                        }

                        //去除连接下划线
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setUnderlineText(false);
                        }

                    }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#2292DD")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.mContent.setMovementMethod(LinkMovementMethod.getInstance());
                }

                holder.mContent.setText(EmojiUtil.parseFaceMsg(mContext, ss));
                holder.mContent.setVisibility(View.VISIBLE);
                holder.mChatImg.setVisibility(View.GONE);
            }

            //  如果头像字段有数据就展示头像，否则按照角色展示角色头像
            if (TextUtils.isEmpty(chatEntity.getUserAvatar())) {
                holder.mUserHeadView.setImageResource(UserRoleUtils.getUserRoleAvatar(chatEntity.getUserRole()));
            } else {
                Glide.with(mContext).load(chatEntity.getUserAvatar()).placeholder(R.drawable.user_head_icon).into(holder.mUserHeadView);
            }
            //设置头像位置的 讲师 助教 主持人的标
            if (holder.mHeadLogo != null) {
                int userRoleAvatarLogo = UserRoleUtils.getUserRoleAvatarLogo(chatEntity.getUserRole());
                if (userRoleAvatarLogo != -1) {
                    holder.mHeadLogo.setVisibility(View.VISIBLE);
                    holder.mHeadLogo.setImageResource(userRoleAvatarLogo);
                } else {
                    holder.mHeadLogo.setVisibility(View.GONE);
                }
            }
        }
    }

    private final int otherType = 0; // 别人发送的聊天
    private final int selfType = 1; // 自己发送的聊天

    @Override
    public int getItemViewType(int position) {

        ChatEntity chat = mChatEntities.get(position);

        // 系统广播 --- 只有 chatEntity.getMsg() 不为空
        if (chat.getUserId().isEmpty() && chat.getUserName().isEmpty() && !chat.isPrivate() && chat.isPublisher()
                && chat.getTime().isEmpty() && chat.getUserAvatar().isEmpty()) {
            // 系统广播
            int systemType = 2;
            return systemType;
        }

        // 聊天
        if (chat.getUserId().equals(selfId)) {
            return selfType; // 自己发出去的
        } else {
            return otherType; // 收到别人的
        }
    }

    @Override
    public int getItemCount() {
        return mChatEntities == null ? 0 : mChatEntities.size();
    }

    static final class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView mContent;
        TextView tv_name;
        TextView tv_time;

        TextView mBroadcast;

        HeadView mUserHeadView;

        ImageView mChatImg;

        ImageView mHeadLogo;

        ChatViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            mContent = itemView.findViewById(R.id.pc_chat_single_msg);
            mBroadcast = itemView.findViewById(R.id.pc_chat_system_broadcast);
            mUserHeadView = itemView.findViewById(R.id.id_private_head);
            mChatImg = itemView.findViewById(R.id.pc_chat_img);
            mHeadLogo = itemView.findViewById(R.id.iv_headview_logo);
        }
    }
}
