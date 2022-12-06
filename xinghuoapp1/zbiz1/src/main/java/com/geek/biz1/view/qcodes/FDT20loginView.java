package com.geek.biz1.view.qcodes;

import com.geek.biz1.bean.qcodes.FDT20loginBean;
import com.geek.libutils.libmvp.IView;

public interface FDT20loginView extends IView {

    void Onlogin2Success(FDT20loginBean bean);
    void Onlogin2Nodata(String bean);
    void Onlogin2Fail(String msg);

}
