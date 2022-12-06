package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.biz1.view.FLunbotuView;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Flubotu1Presenter extends Presenter<FLunbotuView> {

    public void getlubotu1(String url,String aaaaa) {
        JSONObject requestData = new JSONObject();
        requestData.put("configkey", aaaaa);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),
                requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).get_lunbotu1(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "",requestBody)
                .enqueue(new Callback<ResponseSlbBean2<BjyyBeanYewu3>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<BjyyBeanYewu3>> call,
                                   Response<ResponseSlbBean2<BjyyBeanYewu3>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OnFLunbotuNodata(response.body().getMsg());
                    return;
                }
                getView().OnFLunbotuSuccess(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<BjyyBeanYewu3>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnFLunbotuFail(string);
                call.cancel();
            }
        });
    }
}
