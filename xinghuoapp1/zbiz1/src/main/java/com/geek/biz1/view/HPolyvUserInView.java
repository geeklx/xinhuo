package com.geek.biz1.view;

import com.geek.libutils.libmvp.IView;

public interface HPolyvUserInView extends IView {

    void OnPolyvUserInSuccess(String bean);

    void OnPolyvUserInNodata(String bean);

    void OnPolyvUserInFail(String msg);

}
