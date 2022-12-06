package com.geek.biz1.presenter;

import com.geek.biz1.api.Biz1Api;
import com.geek.biz1.bean.HUserInfoBean;
import com.geek.biz1.view.HUserInfoView;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HUserInfoPresenter extends Presenter<HUserInfoView> {

    public void get_userinfo() {
        RetrofitNetNew2.build(Biz1Api.class, getIdentifier()).get_userinfo(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + "liveApp/getUserInfoNew",
                BanbenUtils.getInstance().getVersion(),
                BanbenUtils.getInstance().getImei(),
                "").enqueue(new Callback<ResponseSlbBean<HUserInfoBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean<HUserInfoBean>> call, Response<ResponseSlbBean<HUserInfoBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (response.body().getCode() != 0) {
                    getView().OnUserInfoNodata(response.body().getMsg());
                    return;
                }
                getView().OnUserInfoSuccess(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean<HUserInfoBean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnUserInfoFail(string);
                call.cancel();
            }
        });

    }

}
