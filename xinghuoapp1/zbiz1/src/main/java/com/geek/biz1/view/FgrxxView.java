package com.geek.biz1.view;

import com.geek.biz1.bean.FgrxxBean;
import com.geek.libutils.libmvp.IView;

public interface FgrxxView extends IView {

    void OngrxxSuccess(FgrxxBean bean);

    void OngrxxNodata(String bean);

    void OngrxxFail(String msg);

}
