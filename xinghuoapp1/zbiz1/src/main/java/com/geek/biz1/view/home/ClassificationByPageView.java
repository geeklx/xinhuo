package com.geek.biz1.view.home;


import com.geek.biz1.bean.home.ClassificationListByPageBean;
import com.geek.libutils.libmvp.IView;

import java.util.List;

public interface ClassificationByPageView extends IView {

    void onClassficationDataSuccess(ClassificationListByPageBean bean);

    void onClassficationDataNoData(String msg);

    void onClassficationDataFail(String msg);
}
