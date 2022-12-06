package com.geek.biz1.view;


import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.biz1.bean.FAppCheckUseBean;
import com.geek.biz1.bean.FBannerBean;
import com.geek.libutils.libmvp.IView;

public interface FBannerView extends IView {

    void OnFBannerSuccess(FBannerBean bean);

    void OnFBannerNodata(String bean);

    void OnFBannerFail(String msg);

}
