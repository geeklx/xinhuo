package tuoyan.com.xinghuo_dayingindex.utlis;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.geek.libutils.app.BaseAppManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by dph on 2015/12/4.
 */
public class DeviceUtil {
    /**
     * 获取dip
     *
     * @param context
     * @param dipNum
     * @return
     */
    public static int getDip(Context context, int dipNum) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipNum, context.getResources().getDisplayMetrics());
    }

    public static InputStream Bitmap2InputStream(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getDeviceWidth(Context context) {
//        DisplayMetrics dm = new DisplayMetrics();
        return context.getResources().getDisplayMetrics().widthPixels;
//        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
//        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getDeviceHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
//        DisplayMetrics dm = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//        return dm.heightPixels;
    }

    /**
     * 用来将dp转换为px：
     *
     * @param context
     * @param dp      传入的dp值
     * @return
     */
    public static float dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 显示系统状态栏
     *
     * @param activity
     */
    public static void showStatusBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.VISIBLE);
    }

    /**
     * 隐藏系统状态栏
     *
     * @param activity
     */
    public static void hideStatusBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        //设置成低调模式(隐藏状态栏上的其他通知图标,显示时间)
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }

    /**
     * 获得手机串号IMEI
     *
     * @param context
     * @param imei
     * @return
     */
    public static String getImei(Context context, String imei) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            imei = "0000000000000000";
            return imei;
        }
        return imei;
    }

    public static int getFullActivityHeight(@Nullable Context context) {
        if (!isAllScreenDevice()) {
            return getScreenHeight(context);
        }
        return getScreenRealHeight(context);
    }

    private static final int PORTRAIT = 0;
    private static final int LANDSCAPE = 1;
    @NonNull
    private volatile static Point[] mRealSizes = new Point[2];


    public static int getScreenRealHeight(@Nullable Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return getScreenHeight(context);
        }

        int orientation = context != null ? context.getResources().getConfiguration().orientation
//                : MyApp.Companion.getInstance().getCurActivity().getResources().getConfiguration().orientation;
                : BaseAppManager.getInstance().top().getResources().getConfiguration().orientation;
        orientation = orientation == Configuration.ORIENTATION_PORTRAIT ? PORTRAIT : LANDSCAPE;

        if (mRealSizes[orientation] == null) {
            WindowManager windowManager = context != null ? (WindowManager) context.getSystemService(Context.WINDOW_SERVICE)
//                    : (WindowManager) MyApp.Companion.getInstance().getCurActivity().getSystemService(Context.WINDOW_SERVICE);
                    : (WindowManager) BaseAppManager.getInstance().top().getSystemService(Context.WINDOW_SERVICE);
            if (windowManager == null) {
                return getScreenHeight(context);
            }
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            mRealSizes[orientation] = point;
        }
        return mRealSizes[orientation].y;
    }

    public static int getScreenHeight(@Nullable Context context) {
        if (context != null) {
            return context.getResources().getDisplayMetrics().heightPixels;
        }
        return 0;
    }

    private volatile static boolean mHasCheckAllScreen;
    private volatile static boolean mIsAllScreenDevice;

    public static boolean isAllScreenDevice() {
        if (mHasCheckAllScreen) {
            return mIsAllScreenDevice;
        }
        mHasCheckAllScreen = true;
        mIsAllScreenDevice = false;
        // 低于 API 21的，都不会是全面屏。。。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
//        WindowManager windowManager = (WindowManager) MyApp.Companion.getInstance().getCurActivity().getSystemService(Context.WINDOW_SERVICE);
        WindowManager windowManager = (WindowManager) BaseAppManager.getInstance().top().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            float width, height;
            if (point.x < point.y) {
                width = point.x;
                height = point.y;
            } else {
                width = point.y;
                height = point.x;
            }
            if (height / width >= 1.97f) {
                mIsAllScreenDevice = true;
            }
        }
        return mIsAllScreenDevice;
    }

}
