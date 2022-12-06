package com.geek.biz1.view.home;

import com.geek.biz1.bean.home.ClassificationListBean;
import com.geek.libutils.libmvp.IView;

import java.util.List;

public interface ClassificationView extends IView {

    void onClassficationDataSuccess(ClassificationListBean bean);

    void onClassficationDataNoData(String msg);

    void onClassficationDataFail(String msg);
}
