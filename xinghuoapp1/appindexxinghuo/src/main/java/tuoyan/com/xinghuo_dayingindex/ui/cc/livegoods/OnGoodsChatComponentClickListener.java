package tuoyan.com.xinghuo_dayingindex.ui.cc.livegoods;

import com.bokecc.sdk.mobile.live.pojo.BroadCastMsg;

public interface OnGoodsChatComponentClickListener {
    void onClickGoodsList( );
    void onClickLikes( );
    void onBroadcastMsg(BroadCastMsg msg);
}
