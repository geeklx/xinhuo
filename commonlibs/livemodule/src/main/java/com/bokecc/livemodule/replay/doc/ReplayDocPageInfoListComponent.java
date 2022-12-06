package com.bokecc.livemodule.replay.doc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bokecc.livemodule.replay.DWReplayCoreHandler;
import com.bokecc.livemodule.replay.doc.adapter.ReplayPageInfoListAdapter;
import com.bokecc.sdk.mobile.live.replay.DWReplayPlayer;
import com.bokecc.sdk.mobile.live.replay.pojo.ReplayPageInfo;

import java.util.ArrayList;

/**
 * 回放直播间文档列表
 */
public class ReplayDocPageInfoListComponent extends LinearLayout implements ReplayDocPageInfoListChangeListener {
    private Context mContext;
    private ReplayPageInfoListAdapter adapter;

    public ReplayDocPageInfoListComponent(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public ReplayDocPageInfoListComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new ReplayPageInfoListAdapter(mContext);
        recyclerView.setAdapter(adapter);
        addView(recyclerView);
        // 设置监听
        DWReplayCoreHandler dwReplayCoreHandler = DWReplayCoreHandler.getInstance();
        if (dwReplayCoreHandler != null) {
            dwReplayCoreHandler.setDocPageInfoListChangeListener(this);
        }
        adapter.setOnClick(new ReplayPageInfoListAdapter.OnClick() {
            @Override
            public void OnItemClick(long time) {
                DWReplayCoreHandler dwReplayCoreHandler = DWReplayCoreHandler.getInstance();
                if (dwReplayCoreHandler == null || dwReplayCoreHandler.getPlayer() == null) {
                    return;
                }
                // 获取当前的player，执行seek操作
                DWReplayPlayer player = dwReplayCoreHandler.getPlayer();
                player.seekTo(time);
            }
        });
    }

    @Override
    public void onPageInfoList(ArrayList<ReplayPageInfo> infoList) {
        adapter.setInfoList(infoList);
    }

//    @Override
//    public void updateSize(int pageNum) {
//        adapter.setSelectItem(pageNum);
//    }

}
