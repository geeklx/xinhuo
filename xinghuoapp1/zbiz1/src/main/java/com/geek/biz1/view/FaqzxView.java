package com.geek.biz1.view;

import com.geek.biz1.bean.FaqzxBean;
import com.geek.libutils.libmvp.IView;

public interface FaqzxView extends IView {

    void OnaqzxSuccess(FaqzxBean bean);

    void OnaqzxNodata(String bean);

    void OnaqzxFail(String msg);

}
