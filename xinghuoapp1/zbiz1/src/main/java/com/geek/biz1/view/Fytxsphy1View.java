package com.geek.biz1.view;

import com.geek.biz1.bean.Fytxsphy1Bean;
import com.geek.libutils.libmvp.IView;

public interface Fytxsphy1View extends IView {

    void Onytxsphy1Success(Fytxsphy1Bean bean);

    void Onytxsphy1Nodata(String bean);

    void Onytxsphy1Fail(String msg);

}
