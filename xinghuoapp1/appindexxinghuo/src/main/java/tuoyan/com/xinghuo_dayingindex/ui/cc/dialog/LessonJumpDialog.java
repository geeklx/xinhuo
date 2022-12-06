package tuoyan.com.xinghuo_dayingindex.ui.cc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import tuoyan.com.xinghuo_dayingindex.R;

public class LessonJumpDialog extends Dialog {

    private String titleStr = "";

    public LessonJumpDialog(@NonNull Context context, String title) {
        super(context, R.style.custom_dialog);
        titleStr = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lesson_jump);
        TextView tvTitle = findViewById(R.id.tv_lesson_name);
        tvTitle.setText(titleStr);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 2000);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
