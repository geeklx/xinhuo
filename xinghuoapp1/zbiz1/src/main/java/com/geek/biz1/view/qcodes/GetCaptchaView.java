package com.geek.biz1.view.qcodes;

import com.geek.biz1.bean.qcodes.CaptchaGetIt;
import com.geek.libutils.libmvp.IView;

public interface GetCaptchaView extends IView {

    void OnCaptchaSuccess(CaptchaGetIt bean);

    void OnCaptchaNodata(String bean);

    void OnCaptchaFail(String msg);

}
