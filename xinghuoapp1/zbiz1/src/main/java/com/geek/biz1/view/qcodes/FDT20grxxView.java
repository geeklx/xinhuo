package com.geek.biz1.view.qcodes;

import com.geek.biz1.bean.qcodes.FDT20grxxBean;
import com.geek.libutils.libmvp.IView;

public interface FDT20grxxView extends IView {

    void OngrxxSuccess(FDT20grxxBean bean);

    void OngrxxNodata(String bean);

    void OngrxxFail(String msg);

}
