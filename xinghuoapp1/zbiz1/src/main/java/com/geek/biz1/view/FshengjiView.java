package com.geek.biz1.view;


import com.geek.biz1.bean.FshengjiBean;
import com.geek.libutils.libmvp.IView;

public interface FshengjiView extends IView {

    void OnFshengjiSuccess(FshengjiBean bean);
    void OnFshengjiNodata(String bean);
    void OnFshengjiFail(String msg);

}
