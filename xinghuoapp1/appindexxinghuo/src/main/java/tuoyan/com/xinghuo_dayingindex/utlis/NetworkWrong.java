package tuoyan.com.xinghuo_dayingindex.utlis;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import tuoyan.com.xinghuo_dayingindex.R;

/**
 * 创建者： huoshulei
 * 时间：  2017/4/13.
 */

public class NetworkWrong {
    private ViewGroup parentViewGroup;
    private Context context;
    private View rootView;
    private final int topMarginDip;
    private final int bottomMarginDip;
    private final View.OnClickListener listener;

    private NetworkWrong(int topMarginDip, int bottomMarginDip, View.OnClickListener listener) {
        this.topMarginDip = topMarginDip;
        this.bottomMarginDip = bottomMarginDip;
        this.listener = listener;
    }


    private NetworkWrong(View view, int topMarginDip, int height, View.OnClickListener listener) {
        this(topMarginDip, height, listener);
        parentViewGroup = findRootParent(view);
        context = view.getContext();
    }

    private NetworkWrong(Activity activity, int topMarginDip, int height, View.OnClickListener listener) {
        this(topMarginDip, height, listener);
        parentViewGroup = findRootParent(activity.getWindow().getDecorView());
        context = activity;
    }

    private NetworkWrong(Fragment fragment, int topMarginDip, int height, View.OnClickListener listener) {
        this(topMarginDip, height, listener);
        parentViewGroup = findFragmentRootParent(fragment.getView());
        context = fragment.getActivity();

    }

    private NetworkWrong(androidx.fragment.app.Fragment fragment, int topMarginDip, int height, View.OnClickListener listener) {
        this(topMarginDip, height, listener);
        parentViewGroup = findFragmentRootParent(fragment.getView());
        context = fragment.getActivity();

    }


    private ViewGroup findFragmentRootParent(View view) {
        if (view instanceof FrameLayout)
            if (view.getId() == android.R.id.content)
                return (ViewGroup) view;
            else return (ViewGroup) view;
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent instanceof View) {
                return findFragmentRootParent((View) parent);
            }
        }
        return null;
    }

    private ViewGroup findRootParent(View view) {
        ViewGroup fallback = null;
        if (view instanceof FrameLayout)
            if (view.getId() == android.R.id.content)
                return (ViewGroup) view;
            else fallback = (ViewGroup) view;
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent instanceof View) {
                return findRootParent((View) parent);
            }
        }
        return fallback;
    }


    public static NetworkWrong make(Activity activity, View.OnClickListener listener) {
        return new NetworkWrong(activity, 0, -1, listener);
    }

    public static NetworkWrong make(Activity activity, int topMarginDip, int bottomMarginDip, View.OnClickListener listener) {

        return new NetworkWrong(activity, topMarginDip, bottomMarginDip, listener);
    }

    public static NetworkWrong make(Fragment fragment, int topMarginDip, int bottomMarginDip, View.OnClickListener listener) {
        return new NetworkWrong(fragment, topMarginDip, bottomMarginDip, listener);
    }

    public static NetworkWrong make(androidx.fragment.app.Fragment fragment, int topMarginDip, int bottomMarginDip, View.OnClickListener listener) {
        return new NetworkWrong(fragment, topMarginDip, bottomMarginDip, listener);
    }

    public static NetworkWrong make(Fragment fragment, View.OnClickListener listener) {
        if (fragment.getView() == null) throw new NullPointerException("this view is a null");
        return make(fragment, 0, fragment.getView().getHeight(), listener);
    }

    public static NetworkWrong make(View view, int topMarginDip, int height, View.OnClickListener listener) {
        return new NetworkWrong(view, topMarginDip, height, listener);
    }


//    public void show() {
//        if (rootView != null) {
//            if (rootView.getParent() != null && parentViewGroup != null) {
//                parentViewGroup.removeView(rootView);
//            }
//        }
//        rootView = createImageView();
//        if (parentViewGroup != null) {
//            if (rootView.getParent() != null) parentViewGroup.removeView(rootView);
//            parentViewGroup.addView(rootView);
//        }
//    }

    private View createImageView() throws Exception {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, topMarginDip, 0, bottomMarginDip);
        layout.setLayoutParams(params);
        layout.setBackgroundColor(Color.WHITE);
        TextView textView = new TextView(context);
        textView.setTextSize(15f);
        textView.setGravity(Gravity.CENTER);
        int padding = (int) (10 * context.getResources().getDisplayMetrics().density);
//        textView.setCompoundDrawablePadding(padding);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bg_not_network, 0, 0);
        textView.setTextColor(Color.parseColor("#666666"));
        textView.setText("暂无网络");
        Button button = new Button(context);
//        button.setPadding();
        button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//        button.setPadding(padding, padding/2, padding, padding/2);
        button.setText("点击刷新");
        button.setTextColor(context.getResources().getColor(R.color.color_1482ff));
        button.setBackgroundResource(R.drawable.bg_shape_3_edeff0);
        button.setOnClickListener(listener);
        Space spaceTop = new Space(context);
        spaceTop.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 0.5f));
        Space spaceCenter = new Space(context);
        spaceCenter.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                (int) (20 * context.getResources().getDisplayMetrics().density)));
        Space spaceBottom = new Space(context);
        spaceBottom.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1.5f));
        layout.addView(spaceTop);
        layout.addView(textView);
        layout.addView(spaceCenter);
        layout.addView(button);
        layout.addView(spaceBottom);
        layout.setOnClickListener(v -> {

        });
        return layout;
    }

    public NetworkWrong setContentView(@LayoutRes int resId) {
        return setContentView(LayoutInflater.from(context).inflate(resId, null, false));
    }

    public NetworkWrong setContentView(View view) {
        if (rootView != null) {
            if (rootView.getParent() != null && parentViewGroup != null) {
                parentViewGroup.removeView(rootView);
            }
        }
        rootView = view;
        return this;
    }

    public View getRootView() {
        return rootView;
    }

    public void show() {
        try {
            if (rootView == null) rootView = createImageView();
            if (parentViewGroup != null) {
                if (rootView.getParent() != null) parentViewGroup.removeView(rootView);
                parentViewGroup.addView(rootView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        if (rootView.getParent() != null && parentViewGroup != null)
            parentViewGroup.removeView(rootView);
    }

    public ViewGroup getParentViewGroup() {
        return parentViewGroup;
    }
}
