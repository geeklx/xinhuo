package com.bokecc.livemodule.live.chat.adapter;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bokecc.livemodule.R;
import com.bokecc.livemodule.live.DWLiveCoreHandler;
import com.bokecc.livemodule.live.chat.module.ChatEntity;
import com.bokecc.livemodule.live.chat.util.EmojiUtil;
import com.bokecc.livemodule.utils.ChatImageUtils;
import com.bokecc.livemodule.utils.UserRoleUtils;
import com.bokecc.livemodule.view.HeadView;
import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.pojo.LiveInfo;
import com.bokecc.sdk.mobile.live.pojo.Viewer;
import com.bumptech.glide.Glide;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 直播公聊适配器
 */
public class LivePublicChatAdapter extends RecyclerView.Adapter<LivePublicChatAdapter.ChatViewHolder> {

    private Context mContext;
    private ArrayList<ChatEntity> mChatEntities;
    private ArrayList<ChatEntity> mChatEntitiesForShow;
    private LayoutInflater mInflater;
    private String selfId;
    private onChatComponentClickListener mChatCompontentClickListener;
    private onItemClickListener onItemClickListener;
    public static final String regular = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";
    public static final String CONTENT_IMAGE_COMPONENT = "content_image";
    public static final String CONTENT_ULR_COMPONET = "content_url";
    private final Pattern pattern;
    private SimpleDateFormat sdf;
    private SimpleDateFormat simpleDateFormat;
    private Calendar cal = Calendar.getInstance();
    // 获取到直播间信息
    private LiveInfo liveInfo;

    public interface onChatComponentClickListener {
        void onChatComponentClick(View view, Bundle bundle);
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnChatImageClickListener(onChatComponentClickListener listener) {
        mChatCompontentClickListener = listener;
    }

    public LivePublicChatAdapter(Context context) {
        mChatEntities = new ArrayList<>();
        mChatEntitiesForShow = new ArrayList<>();
        mContext = context;
        mInflater = LayoutInflater.from(context);
        Viewer viewer = DWLive.getInstance().getViewer();
        if (viewer == null) {
            selfId = "";
        } else {
            selfId = viewer.getId();
        }
        pattern = Pattern.compile(regular, Pattern.CASE_INSENSITIVE);
        sdf = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取直播间信息，用于计算问答时间
        DWLiveCoreHandler dwLiveCoreHandler = DWLiveCoreHandler.getInstance();
        if (dwLiveCoreHandler != null) {
            liveInfo = DWLiveCoreHandler.getInstance().getLiveInfo();
        }
    }


    public void addBroadcast(ArrayList<ChatEntity> mChatEntities) {
        this.mChatEntities.addAll(mChatEntities);
        this.mChatEntitiesForShow.addAll(mChatEntities);
        notifyItemRangeInserted(0, mChatEntities.size());
    }

    public void add(ArrayList<ChatEntity> mChatEntities) {
        this.mChatEntities.addAll(mChatEntities);
        this.mChatEntitiesForShow.clear();
        for (ChatEntity chatEntity : this.mChatEntities) {
            if ("0".equals(chatEntity.getStatus()) || selfId.equals(chatEntity.getUserId()) || chatEntity.isBroadcast()) {
                mChatEntitiesForShow.add(chatEntity);
            }
        }
        notifyDataSetChanged();
    }


    public void remove(String chatId) {
        int size = mChatEntitiesForShow.size();
        int position = -1;
        for (int i = 0; i < size; i++) {
            ChatEntity chatEntity = mChatEntitiesForShow.get(i);
            if (chatEntity.getChatId() != null && chatEntity.getChatId().equals(chatId)) {
                position = i;
                break;
            }
        }
        if (position >= 0) {
            mChatEntitiesForShow.remove(position);
            notifyItemRemoved(position);
        }
    }


    /**
     * 清楚聊天数据
     */
    public void clearData() {
        if (mChatEntities == null) return;
        mChatEntities.clear();
        mChatEntitiesForShow.clear();
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     */
    public void add(ChatEntity chatEntity) {
        mChatEntities.add(chatEntity);
        if (mChatEntities.size() > 300) { // 当消息达到300条的时候，移除最早的消息
            mChatEntities.remove(0);
        }
        if ("0".equals(chatEntity.getStatus()) || selfId.equals(chatEntity.getUserId())) {
            mChatEntitiesForShow.add(chatEntity);
        }
        notifyDataSetChanged();
    }


    /**
     * 禁言删除聊天
     *
     * @param userId
     */
    public void banDeleteChat(String userId) {
        if (userId.equals(selfId)) return;
        Iterator<ChatEntity> it = mChatEntities.iterator();
        while (it.hasNext()) {
            ChatEntity entity = it.next();
            if (entity.getUserId().equals(userId)) {
                it.remove();
            }
        }
        Iterator<ChatEntity> it2 = mChatEntitiesForShow.iterator();
        while (it2.hasNext()) {
            ChatEntity entity = it2.next();
            if (entity.getUserId().equals(userId)) {
                it2.remove();
            }
        }
        notifyDataSetChanged();
    }

    public List<ChatEntity> changeStatus(String status, ArrayList<String> chatIds) {
        List<ChatEntity> list = new ArrayList<>();
        if (mChatEntities != null && mChatEntities.size() > 0 && chatIds != null && chatIds.size() > 0) {
            for (ChatEntity chatEntity : mChatEntities) {
                if (chatIds.contains(chatEntity.getChatId())) {
                    chatEntity.setStatus(status);
                    list.add(chatEntity);
                }
            }
            mChatEntitiesForShow.clear();
            for (ChatEntity chatEntity : mChatEntities) {
                if ("0".equals(chatEntity.getStatus()) || selfId.equals(chatEntity.getUserId())) {
                    mChatEntitiesForShow.add(chatEntity);
                }
            }
        }
        notifyDataSetChanged();
        return list;
    }

    public ArrayList<ChatEntity> getChatEntities() {
        return mChatEntitiesForShow;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == selfType) {
            // 展示自己发出去的公聊
            itemView = mInflater.inflate(R.layout.live_portrait_chat_single, parent, false);
            return new ChatViewHolder(itemView, viewType);
        } else if (viewType == otherType) {
            // 展示收到的别人发出去的公聊
            itemView = mInflater.inflate(R.layout.live_portrait_chat_single, parent, false);

            return new ChatViewHolder(itemView, viewType);
        } else {
            // 展示收到的广播消息
            itemView = mInflater.inflate(R.layout.live_protrait_system_broadcast, parent, false);
            return new ChatViewHolder(itemView, viewType);
        }
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ChatEntity chatEntity = mChatEntitiesForShow.get(position);

        if (holder.viewType == otherType) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });
        }
        // 判断是是否是广播，如果是，就展示广播信息
        if (chatEntity.isBroadcast()) {
            // 展示广播信息
            holder.mBroadcast.setText(chatEntity.getMsg());
        } else {
            // 判断聊天内容的状态，是否显示
            if (selfId.equals(chatEntity.getUserId())) {
                holder.mChatItem.setVisibility(View.VISIBLE);
            } else {
                if ("1".equals(chatEntity.getStatus())) {
                    holder.mChatItem.setVisibility(View.GONE);
                } else if ("0".equals(chatEntity.getStatus())) {
                    holder.mChatItem.setVisibility(View.VISIBLE);
                }
            }
            try {
                SpannableString name = new SpannableString((URLDecoder.decode(chatEntity.getUserName(), "UTF-8")));
                name.setSpan(UserRoleUtils.getUserRoleColorSpan(chatEntity.getUserRole()), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tv_name.setText(name);
                if (chatEntity.getTime().contains(":")) {
                    holder.tv_time.setText(chatEntity.getTime());
                } else {
                    int sendTime = Integer.valueOf(chatEntity.getTime());
                    if (sendTime > 0) {
                        Date date = null;
                        date = simpleDateFormat.parse(liveInfo.getLiveStartTime());
                        cal.setTime(date);
                        cal.add(Calendar.SECOND, sendTime);
                        date = cal.getTime();
                        holder.tv_time.setText(sdf.format(date));
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
//                holder.mContent.setText(EmojiUtil.parseFaceMsg(mContext, ss));
                holder.mContent.setVisibility(View.GONE);
                holder.mChatImg.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(ChatImageUtils.getImgUrlFromChatMessage(chatEntity.getMsg())).into(holder.mChatImg);
                holder.mChatImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mChatCompontentClickListener != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("type", CONTENT_IMAGE_COMPONENT);
                            bundle.putString("url", ChatImageUtils.getImgUrlFromChatMessage(chatEntity.getMsg()));
                            mChatCompontentClickListener.onChatComponentClick(holder.mChatImg, bundle);
                        }
                    }
                });
            } else {
                holder.mContent.setMovementMethod(LinkMovementMethod.getInstance());
                // 2. 如果聊天内容是正常的内容，就直接展示聊天内容
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
                //如果确定有url 需要匹配url之前的uri_ 并删除
                ss.setSpan(new ForegroundColorSpan(Color.parseColor("#1E1F21")), 0, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (url != null) {
                    final String finalUrl = url;
                    ss.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {

                            if (mChatCompontentClickListener != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("type", CONTENT_ULR_COMPONET);
                                bundle.putString("url", finalUrl);
                                mChatCompontentClickListener.onChatComponentClick(widget, bundle);
                            }
                        }

                        //去除连接下划线
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setUnderlineText(false);
                        }

                    }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#2292DD")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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

    // 获取角色对应的名字的颜色
    private ForegroundColorSpan getRoleNameColorSpan(ChatEntity chatEntity) {
        // 判断是否是自己发出的聊天
        if (chatEntity.getUserId().equalsIgnoreCase(selfId)) {
            return new ForegroundColorSpan(Color.parseColor("#ff6633"));
        }
        return UserRoleUtils.getUserRoleColorSpan(chatEntity.getUserRole());
    }

    private int otherType = 0; // 别人发送的聊天
    private int selfType = 1; // 自己发送的聊天
    private int systemType = 2;  // 系统广播

    @Override
    public int getItemViewType(int position) {

        ChatEntity chat = mChatEntitiesForShow.get(position);

        // 系统广播 --- 只有 chatEntity.getMsg() 不为空
        if (chat.isBroadcast()) {
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
        return mChatEntitiesForShow == null ? 0 : mChatEntitiesForShow.size();
    }

    final class ChatViewHolder extends RecyclerView.ViewHolder {

        int viewType;

        TextView mContent;
        TextView tv_name;
        TextView tv_time;

        TextView mBroadcast;

        HeadView mUserHeadView;

        ImageView mChatImg;

        LinearLayout mChatItem;

        ImageView mHeadLogo;

        ChatViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            mContent = itemView.findViewById(R.id.pc_chat_single_msg);
            mBroadcast = itemView.findViewById(R.id.pc_chat_system_broadcast);
            mUserHeadView = itemView.findViewById(R.id.id_private_head);
            mChatImg = itemView.findViewById(R.id.pc_chat_img);
            mChatItem = itemView.findViewById(R.id.chat_item_layout);
            mHeadLogo = itemView.findViewById(R.id.iv_headview_logo);
        }
    }
}