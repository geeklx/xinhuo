package com.geek.appcommon.web.share;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.common.R;
import com.lxj.xpopup.core.BottomPopupView;

import java.util.ArrayList;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * @author: lhw
 * @date: 2021/12/30
 * @desc
 */
public class ShareDialog extends BottomPopupView {

    private TextView tvCancel;
    private RecyclerView recyclerView;
    private ShareAdapter adapter;

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_share;
    }

    public ShareDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        tvCancel = findViewById(R.id.tv_cancle);
        recyclerView = findViewById(R.id.recyclerView);

        tvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        GridLayoutManager manager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(manager);
        adapter = new ShareAdapter(getData());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position == adapter.getData().size() - 1) {
                    // 得到剪贴板管理器
//                    ClipboardManager mClipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//                    mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, targetUrl));
                } else {
//                    MobShareUtil.INSTANCE.share();
                }
            }
        });

    }

    private ArrayList<ShareBean> getData() {
        ArrayList list = new ArrayList();
        list.add(new ShareBean("微信", R.drawable.share_wechat2, Wechat.NAME));
        list.add(new ShareBean("微信朋友圈", R.drawable.share_wpyq, WechatMoments.NAME));
        list.add(new ShareBean("QQ好友", R.drawable.share_qq2, QQ.NAME));
        list.add(new ShareBean("QQ空间", R.drawable.share_qqzone, QZone.NAME));
        list.add(new ShareBean("新浪微博", R.drawable.share_sinbo, SinaWeibo.NAME));
        list.add(new ShareBean("复制链接", R.drawable.share_copy, ""));
        return list;
    }
}
