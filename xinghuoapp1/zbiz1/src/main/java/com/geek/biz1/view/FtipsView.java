package com.geek.biz1.view;

import com.geek.libutils.libmvp.IView;

public interface FtipsView extends IView {

    void OntipsSuccess(String bean);

    void OntipsNodata(String bean);

    void OntipsFail(String msg);

}
