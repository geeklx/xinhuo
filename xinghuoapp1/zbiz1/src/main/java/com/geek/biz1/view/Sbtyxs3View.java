package com.geek.biz1.view;

import com.geek.libutils.libmvp.IView;

public interface Sbtyxs3View extends IView {
    void OnSbtyxs3Success(String versionInfoBean);

    void OnSbtyxs3Nodata(String bean);

    void OnSbtyxs3Fail(String msg);
}
