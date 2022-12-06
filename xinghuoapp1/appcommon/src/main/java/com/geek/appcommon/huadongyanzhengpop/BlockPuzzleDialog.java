package com.geek.appcommon.huadongyanzhengpop;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.biz1.bean.qcodes.CaptchaCheckIt;
import com.geek.biz1.bean.qcodes.CaptchaGetIt;
import com.geek.biz1.bean.qcodes.FDT20grxxBean;
import com.geek.biz1.bean.qcodes.FDT20loginBean;
import com.geek.biz1.bean.qcodes.Point;
import com.geek.biz1.presenter.qcodes.CheckCaptchaPresenter;
import com.geek.biz1.presenter.qcodes.FDT20grxxPresenter;
import com.geek.biz1.presenter.qcodes.FDT20loginPresenter;
import com.geek.biz1.presenter.qcodes.GetCaptchaPresenter;
import com.geek.biz1.view.qcodes.CheckCaptchaView;
import com.geek.biz1.view.qcodes.FDT20grxxView;
import com.geek.biz1.view.qcodes.FDT20loginView;
import com.geek.biz1.view.qcodes.GetCaptchaView;
import com.geek.common.R;
import com.geek.libutils.SlbLoginUtil;
import com.geek.libutils.data.MmkvUtils;
import com.google.gson.Gson;

/**
 * Date:2020/5/19
 * author:wuyan
 */
public class BlockPuzzleDialog extends Dialog implements GetCaptchaView, CheckCaptchaView, FDT20loginView, FDT20grxxView {
    private String baseImageBase64;//背景图片
    private String slideImageBase64;//滑动图片
    private String token;
    private Context mContext;
    private TextView tvDelete;
    private ImageView tvRefresh;
    private DragImageView dragView;
    //    private Handler handler = new Handler();
    private String key;
    private long mCurrentMs = System.currentTimeMillis();
    private GetCaptchaPresenter getCaptchaPresenter;
    private CheckCaptchaPresenter checkCaptchaPresenter;
    private FDT20loginPresenter fdt20loginPresenter;
    private FDT20grxxPresenter fdt20grxxPresenter;
    private String pointStr;
    private CaptchaGetIt captchaGetIt;

    public BlockPuzzleDialog(@NonNull Context context) {
        super(context, R.style.cap_dialog);
        this.mContext = context;
        setContentView(R.layout.dialog_block_puzzle);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager windowManager = ((Activity) mContext).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        ViewGroup.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 9 / 10;
        getWindow().setAttributes((WindowManager.LayoutParams) lp);
        setCanceledOnTouchOutside(false);//点击外部Dialog不消失
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        loadCaptcha();

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MProgressDialog.dismissProgress();
                dismiss();
            }
        });
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCaptcha();
            }
        });
    }

    private void initView() {
        tvDelete = findViewById(R.id.tv_delete);
        tvRefresh = findViewById(R.id.tv_refresh);
        dragView = findViewById(R.id.dragView);

        Bitmap bitmap = ImageCaptchUtil.getBitmap(getContext(), R.drawable.bg_default);
        dragView.setUp(bitmap, bitmap);
        dragView.setSBUnMove(false);

        getCaptchaPresenter = new GetCaptchaPresenter();
        getCaptchaPresenter.onCreate(this);

        checkCaptchaPresenter = new CheckCaptchaPresenter();
        checkCaptchaPresenter.onCreate(this);

        fdt20loginPresenter = new FDT20loginPresenter();
        fdt20loginPresenter.onCreate(this);
        fdt20grxxPresenter = new FDT20grxxPresenter();
        fdt20grxxPresenter.onCreate(this);
    }

    public CaptchaGetIt getCaptchaGetIt() {
        return captchaGetIt;
    }

    private void loadCaptcha() {
        getCaptchaPresenter.getCaptcha("/gwapi/workbenchserver/sso/captcha/get/format", "blockPuzzle");
    }

    private void checkCaptcha(double sliderXMoved) {
        Point point = new Point();
        point.setY(5.0);
        point.setX(sliderXMoved);
        pointStr = new Gson().toJson(point);
        checkCaptchaPresenter.checkCaptcha("/gwapi/workbenchserver/sso/captcha/check/format", "blockPuzzle", token, AESUtil.encode(pointStr, key));

    }

    private void initEvent() {
        dragView.setDragListenner(new DragImageView.DragListenner() {
            @Override
            public void onDrag(double position) {
                checkCaptcha(position);
            }
        });
    }

    private OnResultsListener mOnResultsListener;

    @Override
    public void OnCaptchaSuccess(CaptchaGetIt data) {
        captchaGetIt = data;
        baseImageBase64 = data.getRepData().getOriginalImageBase64();
        slideImageBase64 = data.getRepData().getJigsawImageBase64();
        token = data.getRepData().getToken();
        key = data.getRepData().getSecretKey();
        dragView.setUp(ImageCaptchUtil.base64ToBitmap(baseImageBase64), ImageCaptchUtil.base64ToBitmap(slideImageBase64));
        dragView.setSBUnMove(true);
        initEvent();
    }

    @Override
    public void OnCaptchaNodata(String bean) {

    }

    @Override
    public void OnCaptchaFail(String errorMsg) {
        dragView.setSBUnMove(false);
        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getIdentifier() {
        return getClass().getName() + mCurrentMs;
    }

    @Override
    public void OnCheckCaptchaSuccess(CaptchaCheckIt bean) {
        dragView.ok();
//        loadCaptcha();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            }
//        }, 1500);

        String result = AESUtil.encode(token + "---" + pointStr, key);
//        if (mOnResultsListener != null) {
//            mOnResultsListener.onResultsClick(result);
//        }
        // 灯塔2.0登录业务bufen
        SlbLoginUtil.get().loginTowhere((Activity) mContext, new Runnable() {
            @Override
            public void run() {
//                MProgressDialog.showProgress(mContext, "请稍等...");
                fdt20loginPresenter.getlogin20("/gwapi/workbenchserver/sso/oauth/token/format", "mobile_password", SPUtils.getInstance().getString("password_dt20"), SPUtils.getInstance().getString("username_dt20"), result, "", AuthorizationUtil.getAuthStr());
            }
        });
    }

    @Override
    public void OnCheckCaptchaNodata(String bean) {
        dragView.fail();
        //刷新验证码
        loadCaptcha();
    }

    @Override
    public void OnCheckCaptchaFail(String msg) {
        dragView.fail();
        //刷新验证码
        loadCaptcha();
    }

    @Override
    public void Onlogin2Success(FDT20loginBean bean) {
        SPUtils.getInstance().put("token_dt20", bean.getAccess_token());
        SPUtils.getInstance().put("refresh_token_dt20", bean.getRefresh_token());
        fdt20grxxPresenter.getgrxx20("/gwapi/workbenchserver/api/mine/v1/app/login/info");
    }

    @Override
    public void Onlogin2Nodata(String bean) {

    }

    @Override
    public void Onlogin2Fail(String msg) {

    }


    @Override
    public void OngrxxSuccess(FDT20grxxBean bean) {
        if (bean.getResult() == null) {
            bean.setResult(new FDT20grxxBean.ResultBean());
        }
        MmkvUtils.getInstance().set_common_json2(AppCommonUtils.userInfo_dt, bean);
//        DTAppModel.Companion.getUserinfo();//老系统传值(暂时存放)
//        MProgressDialog.dismissProgress();
        dismiss();
        String result = AESUtil.encode(token + "---" + pointStr, key);
        if (mOnResultsListener != null) {
            mOnResultsListener.onResultsClick(result);
        }
    }

    @Override
    public void OngrxxNodata(String bean) {

    }

    @Override
    public void OngrxxFail(String msg) {

    }

    public interface OnResultsListener {
        void onResultsClick(String result);
    }

    public void setOnResultsListener(OnResultsListener mOnResultsListener) {
        this.mOnResultsListener = mOnResultsListener;
    }
}
