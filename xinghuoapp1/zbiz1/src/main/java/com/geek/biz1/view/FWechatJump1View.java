package com.geek.biz1.view;


import com.geek.biz1.bean.FWechatJumpBean;
import com.geek.libutils.libmvp.IView;

public interface FWechatJump1View extends IView {

    void OnFWechatJump1Success(FWechatJumpBean bean);

    void OnFWechatJump1Nodata(String bean);

    void OnFWechatJump1Fail(String msg);

}
