package com.geek.biz1.view;


import com.geek.biz1.bean.Ftencentim1Bean;
import com.geek.libutils.libmvp.IView;

public interface Ftencentim1View extends IView {

    void OnFtencentim1Success(Ftencentim1Bean bean);

    void OnFtencentim1Nodata(String bean);

    void OnFtencentim1Fail(String msg);

}
