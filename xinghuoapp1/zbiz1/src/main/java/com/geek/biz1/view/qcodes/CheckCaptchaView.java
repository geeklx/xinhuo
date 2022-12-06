package com.geek.biz1.view.qcodes;

import com.geek.biz1.bean.qcodes.CaptchaCheckIt;
import com.geek.libutils.libmvp.IView;

public interface CheckCaptchaView extends IView {

    void OnCheckCaptchaSuccess(CaptchaCheckIt bean);

    void OnCheckCaptchaNodata(String bean);

    void OnCheckCaptchaFail(String msg);

}
