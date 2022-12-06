package com.geek.biz1.view;

import com.geek.biz1.bean.SNew1SearchBean;
import com.geek.biz1.bean.SNewListBean1;
import com.geek.libutils.libmvp.IView;

public interface SNewSearchListView extends IView {
    void OnNewSearchListSuccess(SNewListBean1 bean, int which);

    void OnNewSearchListNodata(String bean, int which);

    void OnNewSearchListFail(String msg, int which);

}
