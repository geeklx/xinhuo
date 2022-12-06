package tuoyan.com.xinghuo_dayingindex.ui.cc.livegoods;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.bokecc.livemodule.view.CustomToast;

import tuoyan.com.xinghuo_dayingindex.R;

public class CommentDialog extends AlertDialog {
    private EditText editText;
    private TextView send;
    private InputMethodManager inputMethodManager;
    private View view;
    private LinearLayout ll;
    // 定义当前支持的最大的可输入的文字数量
    private short maxInput = 30;
    private Context context;
    public CommentDialog(@NonNull Context context) {
        this(context, R.style.CustomDialog); //设置Style
        this.context=context;
    }

    protected CommentDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }

    private void init(final Context context) {
        view = View.inflate(context, R.layout.comment_dialog, null);
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        initView(view);
        initListener();
        setContentView(view);
        setCancelable(true);//是否可以取消 (也可以在调用处设置)
        setCanceledOnTouchOutside(true);//是否点击外部消失

//        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        Rect outRect2 = new Rect();
//        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect2);
//        Rect outRect1 = new Rect();
//        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
//        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels - 2;
//        layoutParams.height = outRect2.height();
//        view.setLayoutParams(layoutParams);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        Window dialog_window = this.getWindow();
        dialog_window.setGravity(Gravity.TOP);//设置显示的位置
        dialog_window.setAttributes(params);//设置显示的大小
        dialog_window.setWindowAnimations(R.style.dialog_anim);


    }
    private int getStatusBarHeight(Activity activity) {
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return activity.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
    private void hideSoftInput() {
        inputMethodManager.hideSoftInputFromWindow(ll.getWindowToken(), 0);
    }

    private void initListener() {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);//使得点击 Dialog 中的EditText 可以弹出键盘
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);//总是显示键盘
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    Toast.makeText(getContext(), "说点什么...", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    onTextFinishListener.onText(editText.getText().toString());
//                    Toast.makeText(getContext(), editText.getText().toString(), Toast.LENGTH_SHORT).show();
                    editText.setText("");
                    dismiss();
                }
            }
        });
    }

    private void initView(View view) {
        editText = (EditText) view.findViewById(R.id.edit_text);
        ll = (LinearLayout) view.findViewById(R.id.ll);
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
            }
        }, 200);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputText = editText.getText().toString();
                if (inputText.length() > maxInput) {
                    CustomToast.showToast(context, "字数超过"+maxInput+"字", Toast.LENGTH_SHORT);
                    editText.setText(inputText.substring(0, maxInput));
                    editText.setSelection(maxInput);
                }
            }
        });

        send = (TextView) view.findViewById(R.id.send);
//        View outview = (View) view.findViewById(R.id.view);
//        outview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
    }

    @Override
    public void show() {
        super.show();

    }

    @Override
    public void dismiss() {
        hideSoftInput();
        super.dismiss();
    }

    @Override
    public void onBackPressed() {
        hideSoftInput();
        super.onBackPressed();
        dismiss();
    }

    @Override
    public void setOnKeyListener(@Nullable OnKeyListener onKeyListener) {
        super.setOnKeyListener(onKeyListener);
    }
private OnTextFinishListener onTextFinishListener;

    public void setOnTextFinishListener(OnTextFinishListener onTextFinishListener) {
        this.onTextFinishListener = onTextFinishListener;
    }

    public interface OnTextFinishListener{
        void onText(String message);
    }
}