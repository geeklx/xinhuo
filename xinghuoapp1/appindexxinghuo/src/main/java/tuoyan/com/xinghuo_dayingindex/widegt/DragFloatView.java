package tuoyan.com.xinghuo_dayingindex.widegt;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import tuoyan.com.xinghuo_dayingindex.R;


public class DragFloatView extends RelativeLayout {
    private int parentHeight;//悬浮的父布局高度
    private int parentWidth;
    private Context context;
    private String TAG="DragFloatView";

    public DragFloatView(Context context) {
        this(context, null, 0);
        this.context = context;
    }

    public DragFloatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }


    public DragFloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.float_view, this);
        renderView(view);
    }

    private void renderView(View view) {
        //初始化那些布局
//        view.findViewById(R.id.live_goods).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, "333333333333333333", Toast.LENGTH_SHORT).show();
//            }
//        });
//        view.findViewById(R.id.iv_close).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, "111111111111", Toast.LENGTH_SHORT).show();
//            }
//        });
//        LottieAnimationView lav_play= findViewById(R.id.lav_play);
//        lav_play.playAnimation();
//        view.findViewById(R.id.live_goods).setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                Log.d("drag", "onLongClick: ");
//                return true;
//            }
//        });


        view.findViewById(R.id.live_goods).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "onTouch: ACTION_DOWN");
                        timeDown = System.currentTimeMillis();
                        isLongClick = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "onTouch: ACTION_MOVE");
                        timeMove = System.currentTimeMillis();
                        long durationMs = timeMove - timeDown;

                        if (isLongClick||durationMs > 500) {

                            isLongClick = true;
                            Log.d(TAG, "onTouch: isLongClick=" + isLongClick);

                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "onTouch: ACTION_UP");
                        break;
                }
                onTouchEvent(event);
                if (!isLongClick){
                    return false;
                }else {

                    return true;
                }



//                Log.d("drag", "onLongClick:onTouch ");

//                return true;
            }
        });

    }
    //按下和移动时的时间，用于判断是否是长按事件
    long timeDown, timeMove;
    //是否是长按事件
    boolean isLongClick;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        View view = getChildAt(0);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    private int lastX;
    private int lastY;

    private boolean isDrag;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);//默认是点击事件
                isDrag = false;//默认是非拖动而是点击事件
                getParent().requestDisallowInterceptTouchEvent(true);//父布局不要拦截子布局的监听
                lastX = rawX;
                lastY = rawY;
                ViewGroup parent;
                if (getParent() != null) {
                    parent = (ViewGroup) getParent();
                    parentHeight = parent.getHeight();
                    parentWidth = parent.getWidth();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isDrag = (parentHeight > 0 && parentWidth > 0);//只有父布局存在你才可以拖动
                if (!isDrag) break;

                int dx = rawX - lastX;
                int dy = rawY - lastY;
                //这里修复一些华为手机无法触发点击事件
                int distance = (int) Math.sqrt(dx * dx + dy * dy);
                isDrag = distance > 0;//只有位移大于0说明拖动了
                if (!isDrag) break;

                float x = getX() + dx;
                float y = getY() + dy;
                //检测是否到达边缘 左上右下
                x = x < 0 ? 0 : x > parentWidth - getWidth() ? parentWidth - getWidth() : x;
                y = y < 0 ? 0 : y > parentHeight - getHeight() ? parentHeight - getHeight() : y;
                setX(x);
                setY(y);
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_UP:

                //如果是拖动状态下即非点击按压事件
                setPressed(!isDrag);
//                if(!isDrag) break;
                moveHide(rawX);

                break;
        }

        //如果不是拖拽，那么就不消费这个事件，以免影响点击事件的处理
        //拖拽事件要自己消费
        Log.d("drag", "event.getAction() "+event.getAction() +"& MotionEvent.ACTION_MASK"+MotionEvent.ACTION_MASK+"onTouchEvent: "+isDrag+"super.onTouchEvent(event)"+super.onTouchEvent(event));
        return isDrag || super.onTouchEvent(event);
    }

    private void moveHide(int rawX) {
        if (rawX >= parentWidth / 2) {
            //靠右吸附
            animate().setInterpolator(new DecelerateInterpolator())
                    .setDuration(500)
                    .xBy(parentWidth - getWidth() - getX())
                    .start();
//            myRunable();
        } else {
            //靠左吸附
            ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
            oa.setInterpolator(new DecelerateInterpolator());
            oa.setDuration(500);
            oa.start();
//            myRunable();

        }
    }
}



