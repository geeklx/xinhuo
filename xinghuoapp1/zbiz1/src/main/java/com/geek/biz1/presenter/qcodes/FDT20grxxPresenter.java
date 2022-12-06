package com.geek.biz1.presenter.qcodes;

import com.blankj.utilcode.util.SPUtils;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import tuoyan.com.xinghuo_daying.RetrofitNetNew3;
import com.geek.biz1.api.Biz3Api;
import com.geek.biz1.bean.qcodes.FDT20grxxBean;
import com.geek.biz1.view.qcodes.FDT20grxxView;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FDT20grxxPresenter extends Presenter<FDT20grxxView> {

    public void getgrxx20(String url) {

        RetrofitNetNew3.build(Biz3Api.class, getIdentifier()).getUserInfo20(BuildConfigyewu.SERVER_ISERVICE_NEW1 + url, SPUtils.getInstance().getString("token_dt20")).enqueue(new Callback<ResponseSlbBean2<FDT20grxxBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<FDT20grxxBean>> call, Response<ResponseSlbBean2<FDT20grxxBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OngrxxNodata(response.body().getMsg());
                    return;
                }
                getView().OngrxxSuccess(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<FDT20grxxBean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OngrxxFail(string);
                call.cancel();
            }
        });
    }
}
