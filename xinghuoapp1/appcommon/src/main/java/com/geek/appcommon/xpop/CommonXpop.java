package com.geek.appcommon.xpop;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geek.common.R;
import com.geek.libbase.widgets.XRecyclerView;
import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.util.XPopupUtils;

import java.util.ArrayList;
import java.util.List;

public class CommonXpop<T extends BaseShowBean> extends AttachPopupView {

    private XRecyclerView rvSjgm;
    private List<T> datas = new ArrayList<>();
    private CommonXpopAdapter<T> mAdapter;
    private CommonXpopListener listener;
    private int width;
    private int height;
    private int layout = 0;
    private int layout_item = 0;
    private String type;//请求接口类型
    private String[] args;

    public CommonXpop(Context context,
                      List<T> list,
                      CommonXpopListener listener,
                      int layout,
                      int layout_item,
                      int width,
                      int height) {
        super(context);
        datas = list;
        this.layout = layout;
        this.layout_item = layout_item;
        //
        this.listener = listener;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void applyBg() {
        super.applyBg();
        attachPopupContainer.setElevation(XPopupUtils.dp2px(getContext(), 5));
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        rvSjgm = findViewById(R.id.rv_sjgm);
        if (layout_item == 0) {
            mAdapter = new CommonXpopAdapter(datas);
        } else {
            mAdapter = new CommonXpopAdapter(datas, layout_item);
        }
//        mAdapter = new CommonXpopAdapter(datas);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (listener != null) {
                    T t = mAdapter.getData().get(position);
                    listener.onItemSelect(t);
                    dismiss();
                }
            }
        });
        rvSjgm.setAdapter(mAdapter);
    }

    @Override
    protected int getMaxWidth() {
        if (this.width == 0) {
            return super.getMaxWidth();
        } else {
            return width;
        }
    }

    @Override
    protected int getPopupWidth() {
        if (this.width == 0) {
            return super.getPopupWidth();
        } else {
            return width;
        }
    }

    @Override
    protected int getPopupHeight() {
        if (this.height == 0) {
            return super.getPopupHeight();
        } else {
            return height;
        }

    }

    @Override
    protected int getImplLayoutId() {
        if (layout == 0) {
            return R.layout.xpop_common;
        }
        return layout;
    }
}
