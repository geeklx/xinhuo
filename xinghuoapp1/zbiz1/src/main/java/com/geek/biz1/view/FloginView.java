package com.geek.biz1.view;

import com.geek.biz1.bean.FloginBean;
import com.geek.libutils.libmvp.IView;

public interface FloginView extends IView {

    void Onlogin2Success(FloginBean bean);
    void Onlogin2Nodata(String bean);
    void Onlogin2Fail(String msg);

}
