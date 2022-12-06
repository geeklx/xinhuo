package com.bokecc.livemodule.live;

import com.bokecc.sdk.mobile.live.DWLive;

/**
 * 直播视频回调监听
 */
public interface DWLiveVideoListener {

    /**
     * 未开课或者已下课
     *
     * @param isNormal 流是否正常结束
     */
    void onStreamEnd(boolean isNormal);

    /**
     * 开始上课：流开始
     */
    void onStreamStart();

    /**
     * 播放状态
     *
     * @param status 包括PLAYING, PREPARING共2种状态
     */
    void onLiveStatus(DWLive.PlayStatus status);

    /**
     * 禁播
     *
     * @param reason reason
     */
    void onBanStream(String reason);

    /**
     * 解禁
     */
    void onUnbanStream();

    /**
     * 切换音视频模式
     *
     * @param playMode playMode
     */
    void onChangePlayMode(DWLive.LivePlayMode playMode);

}
