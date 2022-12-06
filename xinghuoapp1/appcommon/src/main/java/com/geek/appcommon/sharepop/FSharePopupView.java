package com.geek.appcommon.sharepop;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.geek.biz1.bean.FShareBean;
import com.geek.common.R;
import com.haier.cellarette.baselibrary.toasts3.utils.MSizeUtils;
import com.lxj.xpopup.core.BottomPopupView;
import com.mob.MobSDK;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import cn.sharesdk.dingding.friends.Dingding;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class FSharePopupView extends BottomPopupView implements View.OnClickListener {

    private Context context;
    private FShareBean fShareBean;

    public FSharePopupView(@NonNull @NotNull Context context, FShareBean fShareBean) {
        super(context);
        this.context = context;
        this.fShareBean = fShareBean;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.view_share_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        TextView tvQq = findViewById(R.id.tv_qq);
        tvQq.setOnClickListener(this);
        TextView tvVx = findViewById(R.id.tv_vx);
        tvVx.setOnClickListener(this);
        TextView tvVxpyq = findViewById(R.id.tv_vxpyq);
        tvVxpyq.setOnClickListener(this);
        TextView tvDingding = findViewById(R.id.tv_dingding);
        tvDingding.setOnClickListener(this);
        TextView tvClose = findViewById(R.id.tv_close);
        tvClose.setOnClickListener(this);
        TextView tvCopy = findViewById(R.id.tv_copy);
        tvCopy.setOnClickListener(this);

        LinearLayout llCopy = findViewById(R.id.ll_copy);
        if (fShareBean.isH5) {
            llCopy.setVisibility(VISIBLE);
        } else {
            llCopy.setVisibility(GONE);
        }
    }

    @Override
    protected int getMaxHeight() {
        return MSizeUtils.dp2px(context, 350);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_qq) {
            showShare(QQ.NAME);
        } else if (view.getId() == R.id.tv_vx) {
            showShare(Wechat.NAME);
        } else if (view.getId() == R.id.tv_vxpyq) {
            showShare(WechatMoments.NAME);
        } else if (view.getId() == R.id.tv_dingding) {
            showShare(Dingding.NAME);
        } else if (view.getId() == R.id.tv_copy) {
            ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, fShareBean.url));
            ToastUtils.showLong("复制成功");
        }
        this.dismiss();
    }

    private void showShare(String platform) {
        final OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform != null) {
            oks.setPlatform(platform);
        }
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(fShareBean.title);
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(fShareBean.url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(fShareBean.content);
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl(fShareBean.imageUrl);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(fShareBean.url);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                ToastUtils.showLong("分享成功");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                ToastUtils.showLong("分享失败");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                ToastUtils.showLong("分享取消");
            }
        });
        //启动分享
        oks.show(MobSDK.getContext());

    }
}
