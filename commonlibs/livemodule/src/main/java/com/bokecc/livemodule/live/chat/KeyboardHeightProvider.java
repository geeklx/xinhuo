package com.bokecc.livemodule.live.chat;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

import com.bokecc.livemodule.R;

import java.util.ArrayList;
import java.util.List;


/**
 * The keyboard height provider, this class uses a PopupWindow
 * to calculate the window height when the floating keyboard is opened and closed.
 */
public class KeyboardHeightProvider extends PopupWindow {

    /**
     * The tag for logging purposes
     */
    private final static String TAG = "sample_KeyboardHeightProvider";

    /**
     * The keyboard height observerList
     */
    private List<KeyboardHeightObserver> observerList;

    /**
     * The view that is used to calculate the keyboard height
     */
    private final View popupView;

    /**
     * The parent view
     */
    private final View parentView;

    /**
     * The root activity that uses this KeyboardHeightProvider
     */
    private final Activity activity;
    private int bottom = 0;

    /**
     * Construct a new KeyboardHeightProvider
     *
     * @param activity The parent activity
     */
    public KeyboardHeightProvider(Activity activity) {
        super(activity);
        this.activity = activity;
        LayoutInflater inflator = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.popupView = inflator.inflate(R.layout.popupwindow, null, false);
        setContentView(popupView);
        setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE | LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        observerList = new ArrayList<>();
        parentView = activity.findViewById(android.R.id.content);
        setWidth(0);
        setHeight(LayoutParams.MATCH_PARENT);
        popupView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (popupView != null) {
                    popupView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    Rect rect = new Rect();
                    popupView.getWindowVisibleDisplayFrame(rect);
                    bottom = rect.bottom;
                }
            }
        });
        popupView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (popupView != null) {
                    if (bottom < 10) return;
                    handleOnGlobalLayout();
                }
            }
        });
    }

    /**
     * Start the KeyboardHeightProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    public void start() {
        if (!isShowing() && parentView.getWindowToken() != null) {
            setBackgroundDrawable(new ColorDrawable(0));
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    /**
     * Close the keyboard height provider,
     * this provider will not be used anymore.
     */
    public void close() {
        this.observerList = null;
        bottom = 0;
        dismiss();
    }

    /**
     * Set the keyboard height observerList to this provider. The
     * observerList will be notified when the keyboard height has changed.
     * For example when the keyboard is opened or closed.
     *
     * @param observer The observerList to be added to this provider.
     */
    public void addKeyboardHeightObserver(KeyboardHeightObserver observer) {
        if (observer == null) return;
        observerList.add(observer);
    }

    /**
     * remove single observer
     *
     * @param observer observer
     */
    public void removeKeyboardHeightObserver(KeyboardHeightObserver observer) {
        observerList.remove(observer);
    }

    /**
     * remove all observer
     *
     */
    public void clearObserver() {
        observerList.clear();
    }

    /**
     * Get the screen orientation
     *
     * @return the screen orientation
     */
    private int getScreenOrientation() {
        return activity.getResources().getConfiguration().orientation;
    }

    /**
     * Popup window itself is as big as the window of the Activity.
     * The keyboard can then be calculated by extracting the popup view bottom
     * from the activity window height.
     */
    private void handleOnGlobalLayout() {

        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);
//
        Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);

        // REMIND, you may like to change this using the fullscreen size of the phone
        // and also using the status bar and navigation bar heights of the phone to calculate
        // the keyboard height. But this worked fine on a Nexus.
        int orientation = getScreenOrientation();
        int keyboardHeight = 0;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            keyboardHeight = screenSize.y - rect.bottom;
        } else {
            keyboardHeight = bottom - rect.bottom;
        }

        if (keyboardHeight == 0) {
            notifyKeyboardHeightChanged(0, orientation);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            /**
             * The cached portrait height of the keyboard
             */
            notifyKeyboardHeightChanged(keyboardHeight, orientation);
        } else {
            /**
             * The cached landscape height of the keyboard
             */
            notifyKeyboardHeightChanged(keyboardHeight, orientation);
        }
    }

    private void notifyKeyboardHeightChanged(int height, int orientation) {
        if (observerList != null) {
            for (KeyboardHeightObserver observer : observerList)
                observer.onKeyboardHeightChanged(height, orientation);
        }
    }
}
