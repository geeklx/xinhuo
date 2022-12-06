package com.geek.biz1.presenter;

import com.geek.biz1.api.Biz1Api;
import com.geek.biz1.bean.SbbdBean;
import com.geek.biz1.view.SbbdView;
import com.geek.libutils.libmvp.Presenter;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SbbdPresenter extends Presenter<SbbdView> {

    public void getSbbdPresenter() {
        RetrofitNetNew2.build(Biz1Api.class, getIdentifier())
                .get_version6("http://t-ums.ireign.cn/gwapi/ums/api/user/configure")
                .enqueue(new Callback<ResponseSlbBean<SbbdBean>>() {
                    @Override
                    public void onResponse(Call<ResponseSlbBean<SbbdBean>> call, Response<ResponseSlbBean<SbbdBean>> response) {
                        if (!hasView()) {
                            return;
                        }
                        if (response.body() == null) {
                            return;
                        }
                        if (!response.body().isSuccess()) {
                            getView().OnSbbdNodata(response.body().getMsg());
                            return;
                        }
                        getView().OnSbbdSuccess(response.body().getData());
                        call.cancel();
                    }

                    @Override
                    public void onFailure(Call<ResponseSlbBean<SbbdBean>> call, Throwable t) {
                        if (!hasView()) {
                            return;
                        }
                        String string = BanbenUtils.getInstance().error_tips;
                        getView().OnSbbdFail(BuildConfigyewu.SERVER_ISERVICE_NEW1);
                        t.printStackTrace();
                        call.cancel();
                    }
                });
    }
}
