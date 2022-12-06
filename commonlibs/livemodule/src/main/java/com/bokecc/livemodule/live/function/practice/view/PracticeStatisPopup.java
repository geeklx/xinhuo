package com.bokecc.livemodule.live.function.practice.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bokecc.livemodule.R;
import com.bokecc.livemodule.live.DWLiveCoreHandler;
import com.bokecc.livemodule.live.function.practice.adapter.PracticeStatisAdapter;
import com.bokecc.livemodule.utils.PopupAnimUtil;
import com.bokecc.livemodule.view.BasePopupWindow;
import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.pojo.PracticeStatisInfo;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * 随堂测统计弹出界面
 */
public class PracticeStatisPopup extends BasePopupWindow {

    String[] orders = new String[]{"A", "B", "C", "D", "E", "F"};
    String[] orders2 = new String[]{"√", "×"};
    private ImageView qsClose;

    private TextView mPracticeOverDesc;
    private TextView mPracticeingDesc;

    private TextView mPracticePeopleNum;
    private TextView mPracticeAnswerDesc;

    private RecyclerView mStatisList;

    private PracticeStatisAdapter mStatisAdapter;
    private PracticeStatisInfo info;
    private OnCloseListener onCloseListener;
    private TextView timerText;
    private boolean isSubmit = false;
    // 构造函数
    public PracticeStatisPopup(Context context) {
        super(context);
    }

    @Override
    protected void onViewCreated() {

        qsClose = findViewById(R.id.qs_close);
        qsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (onCloseListener != null) {
                    onCloseListener.onClose();
                }
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
            }
        });
        mPracticeOverDesc = findViewById(R.id.practiceing_over_desc);
        mPracticeingDesc = findViewById(R.id.practiceing_desc);
        mPracticePeopleNum = findViewById(R.id.practice_people_num);
        mPracticeAnswerDesc = findViewById(R.id.practice_answer_desc);
        mStatisList = findViewById(R.id.statis_list);
        timerText = findViewById(R.id.timer);
        mStatisList.setLayoutManager(new LinearLayoutManager(mContext));
        mStatisAdapter = new PracticeStatisAdapter(mContext);
        mStatisList.setAdapter(mStatisAdapter);
    }

    @SuppressLint("HandlerLeak")
    private Handler  handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {//定时刷新随堂测结果
                DWLive.getInstance().getPracticeStatis(info.getId());
                handler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };

    /**
     * 展示随堂测统计信息
     */
    public void showPracticeStatis(final PracticeStatisInfo info) {
        if (info == null) {
            return;
        }
        handler.removeMessages(1);
        if (info.getStatus() != 2) {
            handler.sendEmptyMessageDelayed(1, 1000);
        }
        this.info = info;
        if (info.getStatus() == 1) {
            mPracticeingDesc.setVisibility(View.VISIBLE);
            mPracticeOverDesc.setVisibility(View.GONE);
            mPracticeingDesc.setText("答题进行中");
        } else if (info.getStatus() == 2||info.getStatus() == 3) {
            showPracticeStop();
        }
        mStatisAdapter.setAllPracticeNumber(info.getAnswerPersonNum());
        mPracticePeopleNum.setText(String.format(Locale.getDefault(), "共%d人回答，正确率%s",
                info.getAnswerPersonNum(), info.getCorrectRate()));
        ArrayList<Integer> practiceHistoryResult = DWLiveCoreHandler.getInstance().getPracticeResult(info.getId());
        StringBuilder yourChoose = new StringBuilder();
        StringBuilder corrects = new StringBuilder();
        for (int i = 0; i < info.getOptionStatis().size(); i++) {
            if (info.getOptionStatis().get(i).isCorrect()) {
                if (info.getType() == 0) {
                    corrects.append(orders2[i]);
                } else {
                    corrects.append(orders[i]);
                }
            }
        }
        if (practiceHistoryResult != null) {
            for (int i = 0; i < practiceHistoryResult.size(); i++) {
                if (info.getType() == 0) {
                    yourChoose.append(orders2[practiceHistoryResult.get(i)]);
                } else {
                    yourChoose.append(orders[practiceHistoryResult.get(i)]);
                }
            }
        }
        String msg = "您的答案：" + yourChoose.toString() + "     正确答案：" + corrects.toString();
        SpannableString ss = new SpannableString(msg);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor(getMyAnswerColor(yourChoose.toString().equals(corrects.toString())))),
                5,
                5 + yourChoose.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#12b88f")),
                5 + yourChoose.length() + 10,
                5 + yourChoose.length() + 10 + corrects.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPracticeAnswerDesc.setText(ss);
        mStatisAdapter.add(info.getOptionStatis(), info.getType());
    }

    // 展示随堂测停止的UI
    public void showPracticeStop() {
        mPracticeOverDesc.setVisibility(View.VISIBLE);
        mPracticeingDesc.setVisibility(View.GONE);
    }

    String wrongTextColor = "#fc512b";
    String rightTextColor = "#12b88f";

    private String getMyAnswerColor(boolean isRight) {
        if (isRight) {
            return rightTextColor;
        } else {
            return wrongTextColor;
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.practice_statis;
    }

    @Override
    protected Animation getEnterAnimation() {
        return PopupAnimUtil.getDefScaleEnterAnim();
    }

    @Override
    protected Animation getExitAnimation() {
        return PopupAnimUtil.getDefScaleExitAnim();
    }


    /**
     * 计算百分比
     */
    private String calculationPrecent(int selectCount, int all) {
        // 判断分母是否为0
        if (all == 0) {
            return "0";
        }
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后1位
        numberFormat.setMaximumFractionDigits(1);
        return numberFormat.format((float) selectCount / (float) all * 100);
    }

    public void show(View view, OnCloseListener onCloseListener) {
        super.show(view);
        this.onCloseListener = onCloseListener;
    }

    public void setText(final String formatTime) {
        if (timerText != null && !TextUtils.isEmpty(formatTime)) {
            timerText.post(new Runnable() {
                @Override
                public void run() {
                    timerText.setText(formatTime);

                }
            });
        }
    }

    //---------------------------解决重复弹出的问题---------------------------------
    private boolean isShow = false;

    @Override
    public boolean isShowing() {
        if (isShow) {
            return true;
        }
        return super.isShowing();
    }

    @Override
    public void show(View view) {
        super.show(view);
        isShow = true;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        handler.removeMessages(1);
        isShow = false;
    }
}