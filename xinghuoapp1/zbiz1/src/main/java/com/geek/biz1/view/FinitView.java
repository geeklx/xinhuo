package com.geek.biz1.view;


import com.geek.biz1.bean.FinitBean;
import com.geek.libutils.libmvp.IView;

public interface FinitView extends IView {

    void OnFinitSuccess(FinitBean bean);
    void OnFinitNodata(String bean);
    void OnFinitFail(String msg);

}
