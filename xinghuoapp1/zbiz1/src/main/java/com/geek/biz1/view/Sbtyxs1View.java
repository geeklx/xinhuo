package com.geek.biz1.view;

import com.geek.biz1.bean.HTyxs1Bean;
import com.geek.libutils.libmvp.IView;

public interface Sbtyxs1View extends IView {
    void OnSbtyxs1Success(HTyxs1Bean versionInfoBean);

    void OnSbtyxs1Nodata(String bean);

    void OnSbtyxs1Fail(String msg);
}
