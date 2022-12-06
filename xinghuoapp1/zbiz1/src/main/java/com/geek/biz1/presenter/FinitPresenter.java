package com.geek.biz1.presenter;

import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.FinitBean;
import com.geek.biz1.view.FinitView;
import com.geek.libutils.libmvp.Presenter;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinitPresenter extends Presenter<FinitView> {

    public void getinit(String url) {
//        JSONObject requestData = new JSONObject();
//        requestData.put("configkey", configkey);//
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).getinit(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "").enqueue(new Callback<ResponseSlbBean2<FinitBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<FinitBean>> call, Response<ResponseSlbBean2<FinitBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OnFinitNodata(response.body().getMsg());
                    return;
                }
                getView().OnFinitSuccess(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<FinitBean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnFinitFail(string);
                call.cancel();
            }
        });
    }
}
