package com.geek.biz1.view;


import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.libutils.libmvp.IView;

public interface FLunbotuView extends IView {

    void OnFLunbotuSuccess(BjyyBeanYewu3 bean);

    void OnFLunbotuNodata(String bean);

    void OnFLunbotuFail(String msg);

}
