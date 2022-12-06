package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.BjyyActFragment251Bean2;
import com.geek.biz1.view.Fsyyy2View;
import com.geek.libutils.libmvp.Presenter;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.libutils.libretrofit.BanbenUtils;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;
import com.geek.libutils.libretrofit.ResponseSlbBean2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fsyyy2Presenter extends Presenter<Fsyyy2View> {

    public void getsyyy2(String url, String userId, String identityId, String ordId) {
        JSONObject requestData = new JSONObject();//
        requestData.put("userId", userId);//
        requestData.put("identityId", identityId);//
        requestData.put("ordId", ordId);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).getyy2(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/select/my/app",
                requestBody
        ).enqueue(new Callback<ResponseSlbBean2<BjyyActFragment251Bean2>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<BjyyActFragment251Bean2>> call, Response<ResponseSlbBean2<BjyyActFragment251Bean2>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().Onsyyy2Nodata(response.body().getMsg());
                    return;
                }
                getView().Onsyyy2Success(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<BjyyActFragment251Bean2>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().Onsyyy2Fail(string);
                call.cancel();
            }
        });
    }
}
