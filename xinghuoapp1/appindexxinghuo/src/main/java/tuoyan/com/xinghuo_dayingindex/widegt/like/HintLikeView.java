package tuoyan.com.xinghuo_dayingindex.widegt.like;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import tuoyan.com.xinghuo_dayingindex.R;


public class HintLikeView extends FrameLayout {
    public static final int HINT_LIKE_START = 1;
    public static final int HINT_LIKE_END = 2;
    public static final int HINT_LIKE_IDEL = 3;
    //    public static final int HINT_LIKE_DURING = 2;
    //延迟事件
    public static final int HINT_LIKE_TIME = 0;
    private int sendHintLikeTime = HINT_LIKE_TIME * 200;
    private int hintLikeCount = 0;
    private boolean isInSendHintTimeDuring = false; //是否正在一次点赞中

    private ImageView iv_hint_like;
    private KsgLikeView mLikeView;
    private TextView tv_hint_like_count;
    private double currentLike = 0;
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HINT_LIKE_START:
//                    handler.postDelayed(mLikeRunnable, 300);
                    handler.sendEmptyMessageDelayed(HINT_LIKE_END,sendHintLikeTime);
                    break;
                case HINT_LIKE_END:
                    isInSendHintTimeDuring = false;
//                    handler.removeCallbacks(mLikeRunnable);
                    if (hintLikeCount != 0) {
                       handler.sendEmptyMessage(HINT_LIKE_IDEL);
                    }
                    break;
                case HINT_LIKE_IDEL:
                    sendHintLikeCount(hintLikeCount);
                    hintLikeCount = 0;
            }
        }
    };
//    private LikeManager likeManager;

    public HintLikeView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public HintLikeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HintLikeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HintLikeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context){
        View view = View.inflate(context, R.layout.view_hint_like,null);
        addView(view);

        iv_hint_like = view.findViewById(R.id.iv_hint_like);
//        tv_hint_like_count = view.findViewById(R.id.tv_hint_like_count);
        initLikeView(view);
    }

    private void initLikeView(View view){
        this.mLikeView = view.findViewById(R.id.live_view);
        this.mLikeView.addLikeImages(
                R.drawable.ic_hintlike4, R.drawable.ic_hintlike1,
                R.drawable.ic_hintlike2, R.drawable.ic_hintlike3
              );

        // 多个发送
        iv_hint_like.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hintLikeCount ++;
                add();
                if (!isInSendHintTimeDuring){
                    handler.sendEmptyMessage(HINT_LIKE_START);
                    isInSendHintTimeDuring = true;
                }
            }
        });
    }

    /**
     * LikeRunnable
     */
    private final Runnable mLikeRunnable = new Runnable() {
        @Override
        public void run() {
            // 添加 发送
            mLikeView.addFavor(currentLike);
        }
    };

//    public interface OnClickHintLikeCountListener{
//        void onHintLikeCount(int count);
//    }
//
//    private OnClickHintLikeCountListener onClickHintLikeCountListener;
//    public void setOnClickHintLikeCountListener(OnClickHintLikeCountListener onClickHintLikeCountListener){
//        this.onClickHintLikeCountListener = onClickHintLikeCountListener;
//    }
    /**
     * 发送HINT_LIKE_TIME时间内，点击次数
     * */
    private void sendHintLikeCount(int hintLikeCount){
//        if (likeManager!=null){
//            likeManager.sendLike(hintLikeCount, new SendLikeCallBack() {
//                @Override
//                public void onSuccess() {
//
//                }
//
//                @Override
//                public void onFailure(int count) {
//
//                }
//            });
//        }
    }

    public void destroy() {
        handler.removeCallbacksAndMessages(null);
//        if (likeManager!=null){
//            likeManager.release();
//        }
    }

    public void initConfig() {
//        DWLiveCoreHandler.getInstance().getInteractionToken(new BaseCallback<String>() {
//            @Override
//            public void onError(String error) {
//                Toast.makeText(getContext(),"获取token失败，点赞功能不可用",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onSuccess(String msg) {
//                Log.e("###","token成功  "+msg);
//                likeManager = new LikeManagerImpl();
//                likeManager.init(likeListener, DWLiveCoreHandler.getInstance().getViewer().getId(),DWLiveCoreHandler.getInstance().getRoomInfo().getId(),msg);
//            }
//        });
    }
//    private LikeListener likeListener = new LikeListener() {
//
//        @Override
//        public void onInitFailure() {
//            Toast.makeText(getContext(),"点赞初始化失败，点赞功能不可用",Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onInitSuccess(LikeConfig likeConfig) {
//           setNum(likeConfig.getCurrentNumbers(),true);
//        }
//
//        @Override
//        public void onLike(Like like) {
//            if (like != null) {
//                if (!isInSendHintTimeDuring && currentLike != like.getLikeSum()) {
//                    handler.sendEmptyMessage(HINT_LIKE_START);
//                    isInSendHintTimeDuring = true;
//                }
//                setNum(like.getLikeSum(),false);
//            }
//        }
//
//        @Override
//        public void onConnectFailure() {
//            Toast.makeText(getContext(),"连接断开，点赞功能不可用",Toast.LENGTH_SHORT).show();
//        }
//    };
    private void add(){
        setNum(currentLike+1,true);
        mLikeView.addFavor(currentLike);
    }
    private void setNum(double num,boolean isFromInit){
        if (!isFromInit){
            for (double i =  currentLike;  i < num ; i ++){
//                mLikeView.addFavor();
                handler.postDelayed(mLikeRunnable,(int)(i - currentLike) * 100);
            }
        }
        if (currentLike>=num){
            return;
        }
        currentLike = num;
        String s = "";
        if (currentLike>=1000000000000d){
            s = String.format("%.2f", currentLike/1000000000000d)+"T";
        }else if (currentLike>=1000000000d){
            s = String.format("%.2f", currentLike/1000000000d)+"B";
        }else if (currentLike>=1000000d){
            s = String.format("%.2f", currentLike/1000000d)+"M";
        }else if (currentLike>=1000){
            s = String.format("%.2f", currentLike/1000)+"K";
        }else{
            s = String.valueOf((int) currentLike);
        }
//        tv_hint_like_count.setText(s);
    }
}
