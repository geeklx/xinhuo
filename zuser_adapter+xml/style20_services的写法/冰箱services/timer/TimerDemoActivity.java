package com.haiersmart.sfnation.ui.tool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;

import com.google.android.material.tabs.TabLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haiersmart.sfnation.R;
import com.haiersmart.sfnation.application.FridgeApplication;
import com.haiersmart.sfnation.base.BaseActivity;
import com.haiersmart.sfnation.bizutils.MobEventHelper;
import com.haiersmart.sfnation.bizutils.TabUtils;
import com.haiersmart.sfnation.bizutils.TimeUtil;
import com.haiersmart.sfnation.bizutils.ToastUtil;
import com.haiersmart.sfnation.constant.ConstantUtil;
import com.haiersmart.sfnation.popwindows.PopSelectRing;
import com.haiersmart.sfnation.service.TimerDemoService;
import com.haiersmart.sfnation.widget.TimerPicker;
import com.haiersmart.utilslib.app.MyLogUtil;
import com.haiersmart.utilslib.data.SpUtils;
import com.haiersmart.utilslib.device.DeviceUtil;
import com.haiersmart.utilslib.etc.TabSelectAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.haiersmart.sfnation.bizutils.TimeUtil.getTimeStrSec;
import static com.haiersmart.sfnation.bizutils.TimeUtil.initTpTime;
import static com.haiersmart.sfnation.bizutils.TimeUtil.setTpTime;
import static com.haiersmart.sfnation.bizutils.TimeUtil.time_change_one;

/**
 * Created by shining on 2017/3/15 0015.
 */

public class TimerDemoActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = TimerActivity.class.getSimpleName();
    @BindView(R.id.tab)
    TabLayout mCateTabLayout;
    @BindView(R.id.rl_tab)
    RelativeLayout rl_tab;
    @BindView(R.id.home)
    LinearLayout ll_home;
    @BindView(R.id.back)
    LinearLayout ll_back;
    @BindView(R.id.tp_hour)
    TimerPicker tp_hour;//???
    @BindView(R.id.tp_min)
    TimerPicker tp_min;//???
    @BindView(R.id.tp_sec)
    TimerPicker tp_sec;//???
    @BindView(R.id.tv_voice_select)
    TextView tv_voice_select;
    @BindView(R.id.tv_select)
    TextView tv_select;

    @BindView(R.id.ll_starttime)
    LinearLayout mTimeView;                 //?????????????????????
    @BindView(R.id.rl_timer_count_layout)
    RelativeLayout timer_count_layout;        //??????picktimer
    @BindView(R.id.rl_onclick)
    RelativeLayout rl_onclick;

    @BindView(R.id.button_start)
    Button mStartButton;                    //???????????????
    @BindView(R.id.button_cancel)
    Button mCancelButton;                   //???????????????

    @BindView(R.id.tv_hour_decade)
    TextView tv_hour_decade;//????????????

    @BindView(R.id.vline1)
    View vline1;
    @BindView(R.id.vline2)
    View vline2;

    private static String[] tabs = new String[]{"?????????", "??????", "?????????", "??????", "??????", "??????", "??????", "?????????"};
    private boolean isBind;
    private TimerDemoService timerService;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_timer_demo;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        findView();
        onclickListener();
        doNetwork();
    }

    private void doNetwork() {
        mCateTabLayout.removeAllTabs();
        for (String item : tabs) {
            mCateTabLayout.addTab(mCateTabLayout.newTab().setText(item));
        }
        checkIsStart();

//        if (!isBind) {
//            bindService(new Intent(this, TimerDemoService.class), conn, Context.BIND_AUTO_CREATE);
//        }
        mCancelButton.setEnabled(isStart());
        initRingStr();
        findViewById(R.id.tv_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testMob(1);
                testMob(2);
                testMob(3);
                MyLogUtil.d("MobclickAgent", "click");
            }
        });
    }

    private void testMob(int which) {
        List<String> keypaths = new ArrayList<>();
        keypaths.add("MACUSER");
        keypaths.add(DeviceUtil.getMac(this).replace(":", ""));
        switch (which) {
            case 1:
                keypaths.add("even1");
                break;
            case 2:
                keypaths.add("even2");
                break;
            case 3:
                keypaths.add("even3");
                break;
        }
        MobEventHelper.onEvent(this, 1, "label", keypaths);
    }

    private void initRingStr() {
        int spItem = (int) SpUtils.getInstance(FridgeApplication.get()).get(ConstantUtil.CURRENTALARMSOUND, 1);
        if (spItem <= 0 || spItem >= 4) {
            spItem = 1;
        }
        tv_voice_select.setText(getSoundStr(spItem));
    }

    private String getSoundStr(int which) {
        if (which == 1) {
            return getResources().getString(R.string.ring_music1);
        } else if (which == 2) {
            return getResources().getString(R.string.ring_music2);
        } else if (which == 3) {
            return getResources().getString(R.string.ring_music3);
        }
        return getResources().getString(R.string.ring_music1);
    }

    private void initTimeData() {
        tp_hour.setData(initTpTime(0, 24));
        tp_min.setData(initTpTime(0, 60));
        tp_sec.setData(initTpTime(0, 60));
        tp_hour.setTimerType("???");
        tp_min.setTimerType("???");
        tp_sec.setTimerType("???");
    }

    private void setTimePickerTime(final int hour, final int min, final int sec) {
        tp_hour.setData(setTpTime(hour, 24));
        tp_min.setData(setTpTime(min, 60));
        tp_sec.setData(setTpTime(sec, 60));
        tp_sec.fresh();
        tp_hour.fresh();
        tp_min.fresh();
    }

    private void onclickListener() {
        ll_home.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        mStartButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mCateTabLayout.addOnTabSelectedListener(new TabSelectAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TabUtils.tabBoldCurrent(mCateTabLayout, tab);
                String tag = (String) tab.getText();
                if (TextUtils.isEmpty(tag)) {
                    return;
                }
                switch (tag) {
                    case "?????????":
                        setTimePickerTime(0, 0, 0);
                        break;
                    case "??????":
                        setTimePickerTime(1, 0, 0);
                        break;
                    case "?????????":
                        setTimePickerTime(0, 40, 0);
                        break;
                    case "??????":
                        setTimePickerTime(0, 32, 0);
                        break;
                    case "??????":
                        setTimePickerTime(0, 30, 0);
                        break;
                    case "??????":
                        setTimePickerTime(0, 15, 0);
                        break;
                    case "??????":
                        setTimePickerTime(0, 10, 0);
                        break;
                    case "?????????":
                        setTimePickerTime(0, 5, 0);
                        break;
                    default:
                        break;
                }

            }
        });
        rl_onclick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isStart()) {
                    ToastUtil.showToastCenter("?????????????????????");
                    return true;
                }
                return false;
            }
        });
    }

    private void findView() {
        initTimeData();

    }

    /**
     * ?????????????????????????????????bufen
     *
     * @param visible
     */
    public void setTimePickVisible(boolean visible) {
        if (visible) {
            vline1.setVisibility(View.VISIBLE);
            vline2.setVisibility(View.VISIBLE);
            timer_count_layout.setVisibility(View.VISIBLE);
            mTimeView.setVisibility(View.GONE);
            mCancelButton.setEnabled(false);

        } else {
            vline1.setVisibility(View.GONE);
            vline2.setVisibility(View.GONE);
            timer_count_layout.setVisibility(View.GONE);
            mTimeView.setVisibility(View.VISIBLE);
            mCancelButton.setEnabled(true);

        }
    }

    private H mHandler2 = new H(this);
    private static final int MSG_RUN = 189;
    private static final int DELAY_MILLIS = 1000;
    private long mCurrentTime;

    private static class H extends Handler {
        private WeakReference<TimerDemoActivity> mActivity;
        private long mNextTime;

        public H(TimerDemoActivity activity) {
            mActivity = new WeakReference<TimerDemoActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TimerDemoActivity activity = mActivity.get();
            if (activity == null || msg.what != MSG_RUN) {
                return;
            }
            if (activity.decrTime(SystemClock.uptimeMillis() - mNextTime)) {
                mNextTime = SystemClock.uptimeMillis();
                sendEmptyMessageDelayed(MSG_RUN, DELAY_MILLIS);
            }
        }
    }

    private void handler_chushihua() {
        mHandler2.removeMessages(MSG_RUN);
        mHandler2.mNextTime = SystemClock.uptimeMillis();
        decrTime(0);
    }

    /**
     * ?????????
     *
     * @param ms
     * @return true ?????????????????? false???????????????
     */
    private boolean decrTime(long ms) {
        mCurrentTime -= ms;
        if (mCurrentTime <= 0) {
            // ?????????
            doCancel();
            return false;
        }

        long[] times = TimeUtil.compute(mCurrentTime);
        tv_hour_decade.setText(time_change_one(times[1]) + " : " + time_change_one(times[2]) + " : " + time_change_one(times[3]));
        return true;
    }

    private void startTimer(int hour, int min, int sec) {
        //???????????????bufen
        setTime(hour, min, sec);
        startTime();
    }

    private void startTime() {
        handler_chushihua();
        //??????handlerbufen
        mHandler2.sendEmptyMessageDelayed(MSG_RUN, DELAY_MILLIS);
    }

    /**
     * @param
     * @return void
     * @throws Exception
     * @throws
     * @Description: ????????????????????????
     */
    public void setTime(int hour, int min, int sec) {
//        int[] result_hour = TimeUtil.setTime(hour);
//        int[] result_min = TimeUtil.setTime(min);
//        int[] result_sec = TimeUtil.setTime(sec);
//        tv_hour_decade.setText(result_hour[0] + ":" + result_hour[1] + ":" +
//                result_min[0] + ":" + result_min[1] + ":" +
//                result_sec[0] + ":" + result_sec[1]);
        mCurrentTime = (hour * 60 * 60 + min * 60 + sec) * 1000;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home:
                onHomePressed();
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.tv_select:
                showSelectRing();
                break;
            case R.id.button_start:
                //????????????bufen
                if (isStart()) {
                    int hour = Integer.parseInt(tp_hour.getText());
                    int min = Integer.parseInt(tp_min.getText());
                    int sec = Integer.parseInt(tp_sec.getText());
                    if (hour > 0 || min > 0 || sec > 0) {
                        //??????????????????bufen
                        //????????? ?????? ??????bufen
                        toPause();
                        //??????????????????bufen
                        setTimePickVisible(false);
                        //??????bufen
                        startTimer(hour, min, sec);
                    } else {
                        ToastUtil.showToastShort("???????????????");
                    }
                } else if (isPause()) {
                    //????????????bufen
                    toGoon();
                    mHandler2.removeMessages(MSG_RUN);
                } else if (isGoon()) {
                    //????????????bufen
                    toPause();
                    //??????bufen
                    startTime();
                }
                break;
            case R.id.button_cancel:
                //????????????bufen
                doCancel();
                if (isBind) {
                    timerService.initTime();
                }
                break;
            default:

                break;
        }
    }

    private void showSelectRing() {
        PopSelectRing popSelectRing = new PopSelectRing(this);
        popSelectRing.setPopListener(new PopSelectRing.BtnListener() {
            @Override
            public void onOkClick(int ring) {
                tv_voice_select.setText(getSoundStr(ring));
                SpUtils.getInstance(FridgeApplication.get()).put(ConstantUtil.CURRENTALARMSOUND, ring);

            }
        });
        popSelectRing.showUpdateDialog();

    }

    /**
     * tablayout????????????bufen
     */
    private void checkIsStart() {
        TabUtils.enableTabs(mCateTabLayout, isStart());
        if (isStart()) {
            rl_onclick.setVisibility(View.GONE);
        } else {
            rl_onclick.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ??????????????? ???????????? ?????? bufen
     *
     * @return
     */
    public boolean isStart() {
        if (mStartButton.getText().toString().equals("????????????")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ??????????????? ?????? ?????? bufen
     *
     * @return
     */
    public boolean isPause() {
        if (mStartButton.getText().toString().equals("??????")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ??????????????? ?????? ?????? bufen
     *
     * @return
     */
    public boolean isGoon() {
        if (mStartButton.getText().toString().equals("??????")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ????????? ???????????? bufen
     *
     * @return
     */
    private void toStart() {
        mStartButton.setText("????????????");
        mStartButton.setBackground(getResources().getDrawable(R.drawable.btn_green_timer));
        checkIsStart();
    }

    /**
     * ????????? ?????? bufen
     *
     * @return
     */
    private void toPause() {
        mStartButton.setText("??????");
        mStartButton.setBackground(getResources().getDrawable(R.drawable.btn_timer_orange));
        checkIsStart();
    }

    /**
     * ????????? ?????? bufen
     *
     * @return
     */
    private void toGoon() {
        mStartButton.setText("??????");
        mStartButton.setBackground(getResources().getDrawable(R.drawable.btn_green_timer));
        checkIsStart();
    }

    private void doCancel() {
        mHandler2.removeMessages(MSG_RUN);
        toStart();
        setTimePickVisible(true);
        setTime(0, 0, 0);
        tp_hour.setSelected(0);
        tp_min.setSelected(0);
        tp_sec.setSelected(0);
        checkIsStart();
        startActivity(TimerFinishActivity.class);
    }

    /**
     * connect timerservice
     */
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timerService = ((TimerDemoService.MsgBinder) service).getService();
            isBind = true;

            long count = timerService.getCount();
            int hour_service = 0;
            int minute_service = 0;
            int second_service = 0;
            if (count > 0) {
                long[] times = TimeUtil.compute(count * 1000);
                hour_service = Integer.parseInt(String.valueOf(times[1]));
                minute_service = Integer.parseInt(String.valueOf(times[2]));
                second_service = Integer.parseInt(String.valueOf(times[3]));

            }

            boolean isStart = timerService.isStart();
            boolean isPause = timerService.isPause();
            boolean isGoon = timerService.isGoon();
            mCancelButton.setEnabled(!isStart());
            if (isStart) {
                toStart();
                setTimePickVisible(true);
                setTime(0, 0, 0);
                tv_hour_decade.setText("00 : 00 : 00");
            }
            if (isGoon) {
                toGoon();
                setTimePickVisible(false);
                setTime(hour_service, minute_service, second_service);
                tv_hour_decade.setText(time_change_one(hour_service) + " : " + time_change_one(minute_service) + " : " + time_change_one(second_service));
            }
            if (isPause) {
                toPause();
                setTimePickVisible(false);
                startTimer(hour_service, minute_service, second_service);
            }

            mCateTabLayout.getTabAt(timerService.getTabPosition()).select();

            timerService.closeTimer();
            timerService.initTime();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!isBind) {
            bindService(new Intent(this, TimerDemoService.class), conn, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        mHandler2.removeMessages(MSG_RUN);

        if (isBind) {
            //???????????????????????????bufen
            boolean isstart = isStart();
            boolean ispause = isPause();
            boolean isgoon = isGoon();
            timerService.setStart(isstart);//????????????
            timerService.setPause(ispause);//??????
            timerService.setGoon(isgoon);//??????
            //??????????????????????????????hour min sec??????bufen
            timerService.setCount(getTimeStrSec(tv_hour_decade.getText().toString()));//?????? hour

            timerService.setTabPosition(mCateTabLayout.getSelectedTabPosition());//??????tablayout????????????????????????bufen

            //???????????? ????????????????????????
            if (isPause() && !isStart() && !isGoon()) {
                timerService.startTimer();
            } else {
                timerService.closeTimer();
            }
            unbindService(conn);
            isBind = false;
        }
    }
}
