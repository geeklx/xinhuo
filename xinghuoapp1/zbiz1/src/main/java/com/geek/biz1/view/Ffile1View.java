package com.geek.biz1.view;

import com.geek.biz1.bean.FcomBean;
import com.geek.libutils.libmvp.IView;

public interface Ffile1View extends IView {

    void Onfile1Success(FcomBean bean);

    void Onfile1Nodata(String bean);

    void Onfile1Fail(String msg);

}
