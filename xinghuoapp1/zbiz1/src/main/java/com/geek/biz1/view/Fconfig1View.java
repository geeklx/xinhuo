package com.geek.biz1.view;

import com.geek.biz1.bean.FconfigBean;
import com.geek.libutils.libmvp.IView;

public interface Fconfig1View extends IView {

    void Onconfig2Success(String authorizedType, FconfigBean bean);

    void Onconfig2Nodata(String bean);

    void Onconfig2Fail(String msg);

}
