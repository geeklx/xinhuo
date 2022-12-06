package com.geek.biz1.view;

import com.geek.libutils.libmvp.IView;

public interface HTuichudengluView extends IView {

    void OnTuichudengluSuccess(String bean);
    void OnTuichudengluNodata(String bean);
    void OnTuichudengluFail(String msg);

}
