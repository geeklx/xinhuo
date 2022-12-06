package com.geek.biz1.view;

import com.geek.biz1.bean.BjyyBeanYewu1;
import com.geek.biz1.bean.FaqzxBean;
import com.geek.libutils.libmvp.IView;

public interface FsyyyView extends IView {

    void OnsyyySuccess(BjyyBeanYewu1 bean);

    void OnsyyyNodata(String bean);

    void OnsyyyFail(String msg);

}
