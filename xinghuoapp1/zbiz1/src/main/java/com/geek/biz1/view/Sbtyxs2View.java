package com.geek.biz1.view;

import com.geek.libutils.libmvp.IView;

public interface Sbtyxs2View extends IView {
    void OnSbtyxs2Success(String versionInfoBean);

    void OnSbtyxs2Nodata(String bean);

    void OnSbtyxs2Fail(String msg);
}
