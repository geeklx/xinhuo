package com.example.shining.libclearmermory.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import android.text.TextUtils;

import com.example.shining.libutils.utilslib.app.MyLogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class LxClearMermoryService extends Service {

    private static final String[] WHITE_LIST = {".*haier.*", ".*android.*"};// .*haier.*

    private static final String TAG = "LxClearMermoryService";
    private static final long clear_neicun_time = 5 * 1000;
    private Timer timer;
    private RefreshTask refreshTask;

    private final IBinder mBinder = new LocalBinder();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public LxClearMermoryService getService() {
            return LxClearMermoryService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
//        startCloseBeats();
//        add_neicun();
        MyLogUtil.d(TAG, "进onCreate");
        // clear_mermory
        if (timer == null) {
            timer = new Timer();
            if (refreshTask == null) {
                refreshTask = new RefreshTask();
                timer.schedule(refreshTask, 0, clear_neicun_time);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (refreshTask != null) {
            refreshTask.cancel();
            refreshTask = null;
        }

    }

    class RefreshTask extends TimerTask {

        @Override
        public void run() {
            MyLogUtil.d(TAG, "clear_data~");
            clear_data();

        }
    }


    /**
     * @param intent
     * @param flags
     * @param startId
     * @return
     * @IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        MyLogUtil.d(TAG, "进onStartCommand");
//        // clear_mermory
//        if (timer == null) {
//            timer = new Timer();
//            if (refreshTask == null) {
//                refreshTask = new RefreshTask();
//                timer.schedule(refreshTask, 0, clear_neicun_time);
//            }
//        }
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            String action = intent.getAction();
            if (action.equals("LxClearMermoryService_open")){

            }
        }
        return START_STICKY;
    }

    private long afterMem;

    public long getAfterMem() {
        return afterMem;
    }

    /**
     * 是否在清理白名单中
     *
     * @param pkgName
     * @return
     */
    private boolean inWhiteList(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }
        String[] whiteList = WHITE_LIST;
        for (String regex : whiteList) {
            if (pkgName.matches(regex)) {
                MyLogUtil.d("geek", pkgName + " in white list");
                return true;
            }
        }

        return false;
    }

    public void clear_data() {
        //To change body of implemented methods use File | Settings | File Templates.
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
        List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(100);

        long beforeMem = LxClearMermoryUtil.getAvailMemory(getApplicationContext());
        MyLogUtil.d(TAG, "-----------before memory info : " + beforeMem);
        int count = 0;
        if (infoList != null) {

            for (int i = 0; i < infoList.size(); ++i) {
                ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
                MyLogUtil.d(TAG, "process name : " + appProcessInfo.processName);
                //importance 该进程的重要程度  分为几个级别，数值越低就越重要。
                MyLogUtil.d(TAG, "importance : " + appProcessInfo.importance);

                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
                if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    String[] pkgList = appProcessInfo.pkgList;
                    for (int j = 0; j < pkgList.length; ++j) {//pkgList 得到该进程下运行的包名
                        MyLogUtil.d("geek", pkgList[j]);
                        if (inWhiteList(pkgList[j])) {
                            continue;
                        }

//                        if (!pkgList[j].contains("haier")) {
//                            MyLogUtil.d(TAG, "It will be killed, package name : " + pkgList[j]);
//                            am.killBackgroundProcesses(pkgList[j]);
//                            count++;
//                        }
                        MyLogUtil.d(TAG, "It will be killed, package name : " + pkgList[j]);
//                        am.killBackgroundProcesses(pkgList[j]);
                        count++;
                    }
                }

            }
        }

        afterMem = LxClearMermoryUtil.getAvailMemory(getApplicationContext());
//        LxMermoryActivity.setCount(afterMem);
        LxClearMermoryUtil.getMaxclearmermory(afterMem);
        MyLogUtil.d(TAG, "----------- after memory info : " + afterMem);
//        Looper.prepare();
//        ToastUtil.showToastCenter("clear " + count + " process, "
//                + (afterMem - beforeMem) + "M");
//        Looper.loop();
        MyLogUtil.d(TAG, "清除 " + count + " 个进程,腾出空间 "
                + (afterMem - beforeMem) + "M");
    }


    private Timer timer2;
    private TimerTask timerTask2;
    private long closeBeatsTime = 0;
    private boolean is_be_op_clo;

    public void startCloseBeats() {
        if (timer2 == null) {
            timer2 = new Timer();
            if (timerTask2 == null) {
                timerTask2 = new TimerTask() {
                    @Override
                    public void run() {
                        closeBeatsTime++;
                        if (closeBeatsTime >= 100) {
                            is_be_op_clo = false;
                            Intent intent = new Intent("com.smarthaier.fridge.action.dameon.heartbeat.disable");
                            sendBroadcast(intent);
//                            Intent intent = new Intent(getApplicationContext(), BeatsService.class);
//                            stopService(intent);
                        } else {
                            is_be_op_clo = true;
                        }
                        closeBeatsService();
                    }
                };
                timer2.schedule(timerTask2, 0, 1000);
            }
        }

    }

    private void closeBeatsService() {
        Intent intent2 = new Intent();
        intent2.setAction("beats_end");
        intent2.putExtra("data", is_be_op_clo + "");
        sendBroadcast(intent2);
    }

    private List<byte[]> list = new ArrayList<>();

    private void add_neicun() {
        for (int i = 0; i < 3; i++) {
//            getbitmaplist();
            try {
                byte[] bytes = new byte[1024];
                list.add(bytes);
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
