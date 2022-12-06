package com.geek.biz1.view;

import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.biz1.bean.BjyyBeanYewu4;
import com.geek.libutils.libmvp.IView;

public interface FCate1View extends IView {

    void OnCate1Success(String authorizedType, BjyyBeanYewu4 bean);

    void OnCate1Nodata(String bean);

    void OnCate1Fail(String msg);

}
