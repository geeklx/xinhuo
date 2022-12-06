package com.example.bizyewu2.view;

import com.geek.libutils.libmvp.IView;

public interface HPolyvUserOutView extends IView {

    void OnPolyvUserOutSuccess(String bean);

    void OnPolyvUserOutNodata(String bean);

    void OnPolyvUserOutFail(String msg);

}
