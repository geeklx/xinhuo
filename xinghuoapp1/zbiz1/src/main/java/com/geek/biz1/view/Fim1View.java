package com.geek.biz1.view;


import com.geek.biz1.bean.FImBean;
import com.geek.libutils.libmvp.IView;

public interface Fim1View extends IView {

    void OnFim1Success(FImBean bean);

    void OnFim1Nodata(String bean);

    void OnFim1Fail(String msg);

}
