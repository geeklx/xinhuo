package com.geek.appcommon.xpop;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.interfaces.XPopupCallback;

import java.util.List;

public class CommonXpopUtils<T extends BaseShowBean> {

    public void singleSelectShow(Context context,
                                 View view,
                                 List<T> list,
                                 CommonXpopListener listener,
                                 int layout,
                                 int layout_item,
                                 int width,
                                 int height,
                                 PopupPosition popupPosition,
                                 View trans) {
        new XPopup.Builder(context).isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .atView(view).isViewMode(true).hasShadowBg(false) // 去掉半透明背景
                .popupPosition(popupPosition) //手动指定弹窗的位置
                .setPopupCallback(new XPopupCallback() {
                    @Override
                    public void onCreated(BasePopupView popupView) {

                    }

                    @Override
                    public void beforeShow(BasePopupView popupView) {

                    }

                    @Override
                    public void onShow(BasePopupView popupView) {
                        if (trans == null) {
                            return;
                        }
                        ObjectAnimator.ofFloat(trans, "rotation", 0, 180).setDuration(200).start();

                    }

                    @Override
                    public void onDismiss(BasePopupView popupView) {
                        if (trans == null) {
                            return;
                        }
                        ObjectAnimator.ofFloat(trans, "rotation", 180, 360).setDuration(200).start();
                    }

                    @Override
                    public void beforeDismiss(BasePopupView popupView) {

                    }

                    @Override
                    public boolean onBackPressed(BasePopupView popupView) {
                        return false;
                    }

                    @Override
                    public void onKeyBoardStateChanged(BasePopupView popupView, int height) {

                    }

                    @Override
                    public void onDrag(BasePopupView popupView, int value, float percent, boolean upOrLeft) {

                    }
                })
                .asCustom(new CommonXpop(context, list, listener, layout, layout_item, width, height)).show();
    }



//    public static void singleSelectShow(Context context, View view, CommonXpopListener listener, int width, int height, PopupPosition popupPosition, String type, String[] args) {
//        new XPopup.Builder(context).isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//                .atView(view).isViewMode(true).hasShadowBg(false) // 去掉半透明背景
//                .popupPosition(popupPosition) //手动指定弹窗的位置
//                .asCustom(new CommonXpop(context, listener, width, height, type, args)).show();
//    }


//    public static void multiSelectShow(Context context, View view, CommonMultiSelectXpopListener listener, int width, int height, PopupPosition popupPosition, String type, String[] args, List<String> selectIds) {
//        new XPopup.Builder(context).isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//                .atView(view).isViewMode(true).hasShadowBg(false) // 去掉半透明背景
//                .popupPosition(popupPosition) //手动指定弹窗的位置
//                .asCustom(new NetWorkMultiSelectCommonXpop(context, listener, width, height, type, args, selectIds)).show();
//    }

}
