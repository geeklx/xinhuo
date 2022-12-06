package com.example.bizyewu1.view;

import com.example.bizyewu1.bean.VersionInfoBean;
import com.geek.libutils.libmvp.IView;

public interface CheckverionView extends IView {
    void OnUpdateVersionSuccess(VersionInfoBean versionInfoBean);
    void OnUpdateVersionNodata(String bean);
    void OnUpdateVersionFail(String msg);
}
