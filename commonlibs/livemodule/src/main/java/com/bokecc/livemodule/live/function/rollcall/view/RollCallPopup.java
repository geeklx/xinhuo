package com.bokecc.livemodule.live.function.rollcall.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bokecc.livemodule.R;
import com.bokecc.livemodule.live.DWLiveCoreHandler;
import com.bokecc.livemodule.utils.PopupAnimUtil;
import com.bokecc.livemodule.view.BasePopupWindow;
import com.bokecc.livemodule.view.CustomToast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 签到弹出框
 */
public class RollCallPopup extends BasePopupWindow {

    public RollCallPopup(Context context) {
        super(context);
    }

    TextView reverseTime;
    TextView endTips;

    Button performRollcall;
    private long currentTime = 0;

    @Override
    protected void onViewCreated() {

        reverseTime = findViewById(R.id.rollcall_reverse_time);
        performRollcall = findViewById(R.id.btn_rollcall);

        endTips = findViewById(R.id.rollcall_end);

        performRollcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentTime == 0 || System.currentTimeMillis() - currentTime > 2000) {
                    onDestroy();
                    CustomToast.showToast(mContext, "您已签到", Toast.LENGTH_SHORT);
                    DWLiveCoreHandler dwLiveCoreHandler = DWLiveCoreHandler.getInstance();
                    if (dwLiveCoreHandler != null) {
                        dwLiveCoreHandler.sendRollCall();
                    }
                    currentTime = System.currentTimeMillis();
                }
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.rollcall_layout;
    }

    @Override
    protected Animation getEnterAnimation() {
        return PopupAnimUtil.getDefScaleEnterAnim();
    }

    @Override
    protected Animation getExitAnimation() {
        return PopupAnimUtil.getDefScaleExitAnim();
    }

    private int duration;

    public void startRollcall(int duration) {
        this.duration = duration;
        reverseTime.setVisibility(View.VISIBLE);
        performRollcall.setVisibility(View.VISIBLE);
        endTips.setVisibility(View.GONE);

        reverseTime.setText(String.format("签到倒计时：%s", getFormatTime(duration)));
        startTimerTask();
    }

    Timer timer = new Timer();
    TimerTask timerTask;

    Handler handler = new Handler(Looper.getMainLooper());

    private void startTimerTask() {
        stopTimerTask();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (duration > -3) {
                            if (duration >= 0) {
                                reverseTime.setText(String.format("签到倒计时：%s", getFormatTime(duration)));
                            } else {
                                reverseTime.setVisibility(View.GONE);
                                performRollcall.setVisibility(View.GONE);
                                endTips.setVisibility(View.VISIBLE);
                            }
                        } else {
                            onDestroy();
                        }
                        duration--;
                    }
                });
            }
        };

        timer.schedule(timerTask, 0, 1000);
    }

    private void stopTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    public void onDestroy() {
        dismiss();
        stopTimerTask();
    }

    public String getFormatTime(int oriTime) {
        return getFillZero(oriTime / 60) + ":" + getFillZero(oriTime % 60);
    }

    public String getFillZero(long number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return String.valueOf(number);
        }
    }

}