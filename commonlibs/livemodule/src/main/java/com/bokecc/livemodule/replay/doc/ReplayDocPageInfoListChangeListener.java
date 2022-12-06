package com.bokecc.livemodule.replay.doc;

import com.bokecc.sdk.mobile.live.replay.pojo.ReplayPageInfo;

import java.util.ArrayList;

public interface ReplayDocPageInfoListChangeListener {
    void onPageInfoList(ArrayList<ReplayPageInfo> infoList);

//    void updateSize(int pageNum);
}
