package com.geek.biz1.view;


import com.geek.biz1.bean.FguanyuBean;
import com.geek.libutils.libmvp.IView;

public interface FguanyuView extends IView {

    void OnFguanyuSuccess(FguanyuBean bean);

    void OnFguanyuNodata(String bean);

    void OnFguanyuFail(String msg);

}
