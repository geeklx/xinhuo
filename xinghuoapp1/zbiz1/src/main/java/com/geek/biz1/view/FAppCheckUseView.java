package com.geek.biz1.view;

import com.geek.biz1.bean.FAppCheckUseBean;
import com.geek.libutils.libmvp.IView;

public interface FAppCheckUseView extends IView {

    void OnFAppCheckUseSuccess(FAppCheckUseBean bean);

    void OnFAppCheckUseNodata(String bean);

    void OnFAppCheckUseFail(String msg);

}
