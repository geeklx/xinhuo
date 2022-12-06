package com.bokecc.livemodule.replay.qa.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bokecc.livemodule.R;
import com.bokecc.livemodule.live.qa.module.QaInfo;
import com.bokecc.livemodule.replay.DWReplayCoreHandler;
import com.bokecc.sdk.mobile.live.pojo.Answer;
import com.bokecc.sdk.mobile.live.pojo.Question;
import com.bokecc.sdk.mobile.live.replay.pojo.ReplayLiveInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * 回放问答适配器
 */
public class ReplayQaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int TYPE_QUESTION = 0;
    private final static int TYPE_ANSWER = 1;

    private ArrayList<String> mPublishedIdList;  // 已经发布的问题Id列表

    // 拿空间换时间
    private LinkedHashMap<String, QaInfo> mQaInfoMapCurrent;
    private LinkedHashMap<String, QaInfo> mQaInfoMapAll;
    private LinkedHashMap<String, QaInfo> mQaInfoMapNormal;
    private LinkedHashMap<String, QaInfo> mQaInfoMapSelf;
    private LayoutInflater mInflater;

    // 获取到直播间信息
    private ReplayLiveInfo liveInfo;


    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat sdf;
    private Calendar cal = Calendar.getInstance();
    private SpannableStringBuilder ss = new SpannableStringBuilder();

    public ReplayQaAdapter(Context context) {
        mQaInfoMapAll = new LinkedHashMap<>();
        mQaInfoMapNormal = new LinkedHashMap<>();
        mQaInfoMapSelf = new LinkedHashMap<>();
        mPublishedIdList = new ArrayList<>();
        mQaInfoMapCurrent = mQaInfoMapNormal;
        mInflater = LayoutInflater.from(context);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf = new SimpleDateFormat("HH:mm");


        // 获取直播间信息，用于计算问答时间
        DWReplayCoreHandler dwReLiveCoreHandler = DWReplayCoreHandler.getInstance();
        if (dwReLiveCoreHandler != null) {
            liveInfo = dwReLiveCoreHandler.getReplayLiveInfo();
        }
    }

    /**
     * 重连的时候，需要重置
     */
    public void resetQaInfos() {
        if (mQaInfoMapCurrent != null) {
            mQaInfoMapAll.clear();
            mQaInfoMapNormal.clear();
            mQaInfoMapSelf.clear();
        }
    }


    // 用于回放的问答添加
    public void addReplayQuestionAnswer(LinkedHashMap<String, QaInfo> mQaInfoMap) {
        this.mQaInfoMapCurrent = mQaInfoMap;
        mPublishedIdList = new ArrayList<>(mQaInfoMapCurrent.keySet());
        notifyDataSetChanged();
    }

    // -----------------单个方法----------------------------------------------------

    // 设置是否只显示自己的
//    public void setOnlyShowSelf(boolean isOnlyShowSelf) {
//        if (isOnlyShowSelf) {
//            mQaInfoMapCurrent = mQaInfoMapSelf;
//        } else {
//            mQaInfoMapCurrent = mQaInfoMapNormal;
//        }
//
//        notifyDataSetChanged();
//    }

    // 添加单个问题
//    public void addQuestion(Question question) {
//        if (mQaInfoMapAll.containsKey(question.getId())) {
//            return;
//        } else {
//            mQaInfoMapAll.put(question.getId(), new QaInfo(question));
//
//            if (question.getQuestionUserId().equals(DWLive.getInstance().getViewer().getId())) {
//                // 本人发布的问题，进行展示
//                mQaInfoMapNormal.put(question.getId(), new QaInfo(question));
//                mQaInfoMapSelf.put(question.getId(), new QaInfo(question));
//            } else if (question.getIsPublish() == 1) {
//                // 如果接收到问题的已经发布了，也展示出来
//                if (!mPublishedIdList.contains(question.getId())) {
//                    mPublishedIdList.add(question.getId());
//                }
//                mQaInfoMapNormal.put(question.getId(), new QaInfo(question));
//            }
//        }
//
//        notifyDataSetChanged();
//    }

    // 收到客户端发布的questionId，将问题展示出来
//    public void showQuestion(String questionId) {
//
//        // 如果当前 QaInfoMapAll 没有存储此id，不做任何处理
//        if (!mQaInfoMapAll.containsKey(questionId)) {
//            return;
//        }
//
//        String currentUserId = DWLive.getInstance().getViewer().getId();
//        mQaInfoMapNormal.clear();
//
//        if (!mPublishedIdList.contains(questionId)) {
//            mPublishedIdList.add(questionId);
//        }
//
//        for (Map.Entry<String, QaInfo> entry : mQaInfoMapAll.entrySet()) {
//            if (entry.getValue().getAnswers().size() > 0) {
//                QaInfo qaInfo = entry.getValue();
//                QaInfo newQaInfo = new QaInfo(qaInfo.getQuestion());
//                newQaInfo.setAnswers((ArrayList<Answer>) qaInfo.getAnswers().clone()); //防止浅拷贝
//                mQaInfoMapNormal.put(entry.getKey(), newQaInfo);
//            } else if (entry.getValue().getQuestion().getQuestionUserId().equals(currentUserId)) {
//                Question mQuestion = entry.getValue().getQuestion();
//                mQaInfoMapNormal.put(mQuestion.getId(), new QaInfo(mQuestion));
//            } else if (entry.getValue().getAnswers().size() == 0 && mPublishedIdList.contains(entry.getValue().getQuestion().getId())) {
//                QaInfo qaInfo = entry.getValue();
//                QaInfo newQaInfo = new QaInfo(qaInfo.getQuestion());
//                mQaInfoMapNormal.put(entry.getKey(), newQaInfo);
//            }
//        }
//
//        notifyDataSetChanged();
//    }
//    public void addAnswer(Answer answer) {
//        if (mQaInfoMapAll.containsKey(answer.getQuestionId())) {
//
//            // 检测要答案是否已经存在，如果已经存在了，就不执行添加了操作
//            ArrayList<Answer> answers = mQaInfoMapAll.get(answer.getQuestionId()).getAnswers();
//            if (answers.size() > 0) {
//                for (Answer mapAnswer : answers) {
//                    if (mapAnswer.equals(answer)) {
//                        Log.e(TAG, "now map has contain this answer, not to add");
//                        return;
//                    }
//                }
//            }
//
//            String currentUserId = DWLive.getInstance().getViewer().getId();
//
//            mQaInfoMapAll.get(answer.getQuestionId()).addAnswer(answer);
//
//            Question question = mQaInfoMapAll.get(answer.getQuestionId()).getQuestion();
//
//            if (mQaInfoMapNormal.containsKey(question.getId())) {
//                mQaInfoMapNormal.get(question.getId()).addAnswer(answer);
//            } else {
//                mQaInfoMapNormal.clear();
//
//                for (Map.Entry<String, QaInfo> entry : mQaInfoMapAll.entrySet()) {
//                    if (entry.getValue().getAnswers().size() > 0) {
//
//                        QaInfo qaInfo = entry.getValue();
//                        QaInfo newQaInfo = new QaInfo(qaInfo.getQuestion());
//                        newQaInfo.setAnswers((ArrayList<Answer>) qaInfo.getAnswers().clone()); //防止浅拷贝
//
//                        mQaInfoMapNormal.put(entry.getKey(), newQaInfo);
//                    } else if (entry.getValue().getQuestion().getQuestionUserId().equals(currentUserId)) {
//                        Question mQuestion = entry.getValue().getQuestion();
//                        mQaInfoMapNormal.put(mQuestion.getId(), new QaInfo(mQuestion));
//                    }
//                }
//            }
//
//            if (question.getQuestionUserId().equals(currentUserId)) {
//                mQaInfoMapSelf.get(answer.getQuestionId()).addAnswer(answer);
//            }
//
//            notifyDataSetChanged();
//        }
//    }
    //    public LinkedHashMap<String, QaInfo> getQaInfos() {
//        return mQaInfoMapCurrent;
//    }
    @Override
    public int getItemViewType(int position) {
        int currentPosition = -1;
        for (String qId : mPublishedIdList) {
            QaInfo info = mQaInfoMapCurrent.get(qId);
            if (info != null) {
                currentPosition = currentPosition + 1;
                if (currentPosition == position) {
                    return TYPE_QUESTION;
                }
                ArrayList<Answer> answers = info.getAnswers();
                if (answers != null && answers.size() > 0) {
                    int size = answers.size();
                    for (int i = 0; i < size; i++) {
                        currentPosition = currentPosition + 1;
                        if (position == currentPosition) {
                            return TYPE_ANSWER;
                        }
                    }
                }
            }
        }
        return super.getItemViewType(position);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_QUESTION) {
            itemView = mInflater.inflate(R.layout.live_pc_qa_single_line, parent, false);
            return new ChatViewHolder(itemView);
        } else {
            itemView = mInflater.inflate(R.layout.live_pc_qa_answer, parent, false);
            return new SubViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemViewType = holder.getItemViewType();

        // 问题列表
        if (itemViewType == TYPE_QUESTION) {
            int[] ints = translateToDoubleIndex(position);
            QaInfo info = mQaInfoMapCurrent.get(mPublishedIdList.get(ints[0]));
            if (info != null) {
                Question question = info.getQuestion();
                // 设置提问者名称
                ((ChatViewHolder) holder).questionName.setText(question.getQuestionUserName());
                // 计算问答时间
                try {
                    int sendTime = Integer.valueOf(question.getTime());
                    if (sendTime > 0) {
                        if (liveInfo != null) {
                            Date date = simpleDateFormat.parse(liveInfo.getStartTime());
                            cal.setTime(date);
                            cal.add(Calendar.SECOND, sendTime);
                            date = cal.getTime();
                            ((ChatViewHolder) holder).questionTime.setText(sdf.format(date));
                        }
                    } else {
                        ((ChatViewHolder) holder).questionTime.setText(sdf.format(new Date()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // 设置问题内容
                ((ChatViewHolder) holder).questionContent.setText(question.getContent());
            }

        }

        // 回答列表
        else if (itemViewType == TYPE_ANSWER) {
            int[] ints = translateToDoubleIndex(position);
            QaInfo info = mQaInfoMapCurrent.get(mPublishedIdList.get(ints[0]));
            if (info != null) {
                Answer answer = info.getAnswers().get(ints[1]);
                String msg = answer.getAnswerUserName() + ": " + answer.getContent();
                ss.clear();
                ss.append(msg);
                ss.setSpan(new ForegroundColorSpan(Color.parseColor("#12ad1a")),
                        0, answer.getAnswerUserName().length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new ForegroundColorSpan(Color.parseColor("#1E1F21")),
                        answer.getAnswerUserName().length() + 1, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((SubViewHolder) holder).answerText.setText(ss);

            }

        }

    }


    private int mSize = 0;

    @Override
    public int getItemCount() {
        if (mSize == 0) {
            int totalSize = 0;

            for (int i = 0; i < mPublishedIdList.size(); i++) {
                QaInfo qaInfo = mQaInfoMapCurrent.get(mPublishedIdList.get(i));
                int size = 0;
                if (qaInfo != null) {
                    ArrayList<Answer> answers = qaInfo.getAnswers();
                    size = answers.size();
                    totalSize += (size + 1);
                }
            }
            mSize = totalSize;
        }
        return mSize;
    }

    private int[] translateToDoubleIndex(int adapterPosition) {
        final int[] result = new int[]{-1, -1};
        final int groupCount = mPublishedIdList.size();
        int adaptPositionCursor = 0;
        for (int groupCursor = 0; groupCursor < groupCount; groupCursor++) {
            if (adaptPositionCursor == adapterPosition) {
                result[0] = groupCursor;
                break;
            }
            QaInfo qaInfo = mQaInfoMapCurrent.get(mPublishedIdList.get(groupCursor));
            if (qaInfo != null) {
                ArrayList<Answer> answers = qaInfo.getAnswers();
                int childCount = answers.size();
                final int offset = adapterPosition - adaptPositionCursor;
                if (childCount >= offset) {
                    result[0] = groupCursor;
                    result[1] = offset - 1;
                    break;
                }
                adaptPositionCursor += childCount;

            }
            adaptPositionCursor++;
        }
        return result;
    }


    final class ChatViewHolder extends RecyclerView.ViewHolder {

        ImageView questionHeadView;
        TextView questionName;
        TextView questionTime;
        TextView questionContent;
        LinearLayout answerContainer;
        View qaSingleLayout;
        View qaSeparateLine;

        public ChatViewHolder(View itemView) {
            super(itemView);
            questionHeadView = itemView.findViewById(R.id.user_head_view);
            questionName = itemView.findViewById(R.id.tv_question_name);
            questionTime = itemView.findViewById(R.id.tv_question_time);
            questionContent = itemView.findViewById(R.id.tv_question);
            answerContainer = itemView.findViewById(R.id.ll_answer);
            qaSingleLayout = itemView.findViewById(R.id.ll_qa_single_layout);
            qaSeparateLine = itemView.findViewById(R.id.qa_separate_line);
        }
    }

    final class SubViewHolder extends RecyclerView.ViewHolder {

        TextView answerText;

        public SubViewHolder(@NonNull View itemView) {
            super(itemView);
            answerText = itemView.findViewById(R.id.qa_answer);
        }
    }


}
