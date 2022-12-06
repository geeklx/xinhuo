package com.geek.biz1.view;

import com.geek.biz1.bean.FloginBean;
import com.geek.libutils.libmvp.IView;

public interface Fzwdl1View extends IView {

    void Onzwdl1Success(FloginBean bean);

    void Onzwdl1Nodata(String bean);

    void Onzwdl1Fail(String msg);

}
