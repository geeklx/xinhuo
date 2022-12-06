package com.geek.appcommon.huadongyanzhengpop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.SlbBase;
import com.geek.appcommon.interfaces.CommonXPopupListener;
import com.geek.appcommon.sharepop.FSharePopupView;
import com.geek.appcommon.xpop.BaseShowBean;
import com.geek.appcommon.xpop.CommonXpopListener;
import com.geek.appcommon.xpop.CommonXpopUtils;
import com.geek.appcommon.xpop.PopulationLabelListBean;
import com.geek.appcommon.xpop.PopulationLabelListItemBean;
import com.geek.biz1.bean.FShareBean;
import com.geek.common.R;
import com.geek.libglide47.base.GlideRoundImageView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.haier.cellarette.baselibrary.toasts3.MProgressDialog;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.impl.ConfirmPopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopupdemo.custom.CustomAttachPopup;

import java.util.ArrayList;
import java.util.List;

public class PopviewDemoAct extends SlbBase implements View.OnClickListener {

    private TextView tv_pop12;
    private ImageView iv1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_popview_demo;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        findViewById(R.id.tv_bottom_list).setOnClickListener(this);
        findViewById(R.id.tv_bottom_list_check).setOnClickListener(this);
        findViewById(R.id.tv_pop1).setOnClickListener(this);
        findViewById(R.id.tv_pop11).setOnClickListener(this);
        findViewById(R.id.tv_pop2).setOnClickListener(this);
        findViewById(R.id.tv_pop3).setOnClickListener(this);
        findViewById(R.id.tv_pop4).setOnClickListener(this);
        findViewById(R.id.tv_pop5).setOnClickListener(this);
        findViewById(R.id.tv_pop6).setOnClickListener(this);
        findViewById(R.id.tv_pop7).setOnClickListener(this);
        findViewById(R.id.tv_pop8).setOnClickListener(this);
        findViewById(R.id.tv_pop9).setOnClickListener(this);
        findViewById(R.id.tv_pop10).setOnClickListener(this);
        tv_pop12 = findViewById(R.id.tv_pop12);
        iv1 = findViewById(R.id.iv1);
        tv_pop12.setOnClickListener(this);

        //推荐Application 设置全局
        ///1、设置主色调 默认情况下，XPopup的主色为灰色，主色作用于Button文字，EditText边框和光标，Check文字的颜色上。
        // 主色调只需要设置一次即可，可以放在Application中设置。
        XPopup.setPrimaryColor(getResources().getColor(R.color.defaultred));
        //2、设置全局的动画时长 默认情况下，弹窗的动画时长为360毫秒。你可以通过下面的方法进行修改：
        // 传入的时长最小为0，动画的时长会影响除Drawer弹窗外的所有弹窗
//        XPopup.setAnimationDuration(200);
        //3、设置全局弹窗的半透明背景色值
//        XPopup.setShadowBgColor(xxx);
        //4、设置全局弹窗的状态栏色值
//        XPopup.setNavigationBarColor(xxx);
        commonXpopUtils = new CommonXpopUtils();
    }

    private CommonXpopUtils commonXpopUtils;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_bottom_list) {
            new XPopup.Builder(PopviewDemoAct.this).autoDismiss(false).asBottomList("标题", new String[]{"拍照", "相册"}, new OnSelectListener() {
                @Override
                public void onSelect(int position, String text) {
                }
            }).show();
        } else if (view.getId() == R.id.tv_bottom_list_check) {
            new XPopup.Builder(PopviewDemoAct.this)
                    //是否在消失的时候销毁资源，默认false。如果你的弹窗对象只使用一次，
                    //非常推荐设置这个，可以杜绝内存泄漏。如果会使用多次，千万不要设置
                    .isDestroyOnDismiss(true).asBottomList("标题可以没有", new String[]{"条目1", "条目2", "条目3", "条目4", "条目5"}, null, 2, new OnSelectListener() {
                        @Override
                        public void onSelect(int position, String text) {
                            ToastUtils.showLong("click: " + text);
                        }
                    }).show();
        } else if (view.getId() == R.id.tv_pop1) {
            new XPopup.Builder(PopviewDemoAct.this).borderRadius(30).isDestroyOnDismiss(true).asConfirm("标题", "我是内容我是内容我是内容我是内容我是内容我是内容我是内容我是内容我是内容我是内容。", "取消", "确定", new OnConfirmListener() {
                @Override
                public void onConfirm() {
                    ToastUtils.showShort("click confirm");
                }
            }, null, false).show();

        } else if (view.getId() == R.id.tv_pop11) {

            XPopup.Builder builder = new XPopup.Builder(PopviewDemoAct.this);
            builder.dismissOnTouchOutside(false).isDestroyOnDismiss(true);
            builder.asConfirm("标题", "我是内容我是内容我是内容我是内容我是内容我是内容我是内容我是内容", null, "确定", new OnConfirmListener() {
                @Override
                public void onConfirm() {

                }
            }, null, false, R.layout.app_maintain_confim_popup1).show();

        } else if (view.getId() == R.id.tv_pop2) {
            XPopup.Builder builder = new XPopup.Builder(PopviewDemoAct.this);
            builder.dismissOnTouchOutside(false).isDestroyOnDismiss(true);
            ConfirmPopupView confirmPopupView = builder.asConfirm("标题", "我是内容我是内容我是内容我是内容我是内容我是内容我是内容我是内容", null, "确定", new OnConfirmListener() {
                @Override
                public void onConfirm() {

                }
            }, null, true, R.layout.app_maintain_confim_popup);

            //增加弹窗背景图 可忽略 （应用实例：维护信息弹窗）
            GlideRoundImageView ivBg = confirmPopupView.findViewById(R.id.iv_pop_bg);
            ivBg.setCornerRadius(12);
            ivBg.loadImage("https://www.dtdjzx.gov.cn/u/cms/dtdjzx/201909/16082730fc5d.jpg", 0);
            confirmPopupView.show();
        } else if (view.getId() == R.id.tv_pop3) {
            //多输入框可用自定义布局
            new XPopup.Builder(PopviewDemoAct.this).hasStatusBarShadow(false).isDestroyOnDismiss(true).autoOpenSoftInput(true)
                    //设置弹窗显示和隐藏的回调监听
                    .setPopupCallback(new CommonXPopupListener())
//                        .autoFocusEditText(false) //是否让弹窗内的EditText自动获取焦点，默认是true
                    //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
                    .asInputConfirm("我是标题", "我是内容", null, "我是默认Hint文字", new OnInputConfirmListener() {
                        @Override
                        public void onConfirm(String text) {
                        }
                    }).show();
        } else if (view.getId() == R.id.tv_pop4) {
            new XPopup.Builder(PopviewDemoAct.this)
//                        .maxWidth(600)
                    //最大高度
                    .maxHeight(800).isDestroyOnDismiss(true).asCenterList("请选择一项", new String[]{"条目1", "条目2", "条目3", "条目4", "条目1", "条目2", "条目3", "条目4", "条目1", "条目2", "条目3", "条目4", "条目1", "条目2", "条目3", "条目4", "条目1", "条目2", "条目3", "条目4", "条目1", "条目2", "条目3", "条目4", "条目1", "条目2", "条目3", "条目4", "条目1", "条目2", "条目3", "条目4",}, new OnSelectListener() {
                        @Override
                        public void onSelect(int position, String text) {
                            ToastUtils.showLong("click: " + text);
                        }
                    })
//                        .bindLayout(R.layout.my_custom_attach_popup) //自定义布局
                    .show();
        } else if (view.getId() == R.id.tv_pop5) {
            new XPopup.Builder(PopviewDemoAct.this).isDestroyOnDismiss(true).asCenterList("请选择一项", new String[]{"条目1", "条目2", "条目3", "条目4"}, null, 1, new OnSelectListener() {
                @Override
                public void onSelect(int position, String text) {
                    ToastUtils.showLong("click: " + text);
                }
            }).show();
        } else if (view.getId() == R.id.tv_pop6) {
            new XPopup.Builder(PopviewDemoAct.this).isDestroyOnDismiss(true).hasShadowBg(false).atView(view)
                    //就是增加一个自定义的View xpopup库自带一些自定义效果，详情看目录（com.lxj.xpopupdemo.custom）
                    .asCustom(new CustomAttachPopup(PopviewDemoAct.this)).show();
        } else if (view.getId() == R.id.tv_pop7) {
            MProgressDialog.showProgress(PopviewDemoAct.this, "请稍等...");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MProgressDialog.dismissProgress();
                }
            }, 2000);
        } else if (view.getId() == R.id.tv_pop8) {
            Intent intent = new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.XpopupMainActivity");
            startActivity(intent);
        } else if (view.getId() == R.id.tv_pop9) {
            FShareBean shareBean = new FShareBean();
            shareBean.title = "分享";
//                    shareBean.content = "我是内容";
            shareBean.url = "http://www.baidu.com";
            shareBean.isH5 = true;
//                    shareBean.imageUrl = "https://www.dtdjzx.gov.cn/u/cms/dtdjzx/202111/15155729f9n7.jpg";

            new XPopup.Builder(PopviewDemoAct.this)
                    //半透明阴影背
                    .hasShadowBg(true).asCustom(new FSharePopupView(PopviewDemoAct.this, shareBean)).show();
        } else if (view.getId() == R.id.tv_pop10) {

            List<String> mList;
            mList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                mList.add("item " + i);
            }

            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme);
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_fragment);

            RecyclerView recyclerView = (RecyclerView) bottomSheetDialog.findViewById(R.id.rv_item);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(mList, this);
            recyclerView.setAdapter(recyclerAdapter);
            recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(View item, int position) {
                    Toast.makeText(PopviewDemoAct.this, "item " + position, Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            });
            bottomSheetDialog.show();
        } else if (view.getId() == R.id.tv_pop12) {
            PopulationLabelListBean bean = new PopulationLabelListBean();
            List<PopulationLabelListItemBean> mlist = new ArrayList<>();
            mlist.add(new PopulationLabelListItemBean("1", "c1", "11", false));
            mlist.add(new PopulationLabelListItemBean("2", "c2", "111", false));
            mlist.add(new PopulationLabelListItemBean("3", "c3", "1111", false));
            mlist.add(new PopulationLabelListItemBean("4", "c4", "11111", false));
            mlist.add(new PopulationLabelListItemBean("5", "c5", "111111", false));
            bean.setList(mlist);
            commonXpopUtils.singleSelectShow(PopviewDemoAct.this, tv_pop12, mlist, new CommonXpopListener() {
                @Override
                public void onItemSelect(BaseShowBean bean) {
                    PopulationLabelListItemBean bean1 = (PopulationLabelListItemBean) bean;
                    tv_pop12.setText("12、学科分类下拉弹窗-"+bean1.getTagName());
                }
            }, 0, 0, view.getWidth(), 0, PopupPosition.Bottom, iv1);
        }
    }

    /**
     * RecyclerView适配器
     * 展示BottomSheetDialog，列表形式
     */
    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private List<String> list;
        private Context mContext;
        private OnItemClickListener onItemClickListener;

        public RecyclerAdapter(List<String> list, Context mContext) {
            this.list = list;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_layout, parent, false);
            return new ViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClickListener(v, position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout llItem;

            public ViewHolder(View itemView) {
                super(itemView);
                llItem = (LinearLayout) itemView.findViewById(R.id.ll_item);
            }
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public interface OnItemClickListener {
            void onItemClickListener(View item, int position);
        }
    }
}
