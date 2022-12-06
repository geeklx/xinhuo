package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.view.Fsyyy3View;
import com.geek.libutils.libmvp.Presenter;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fsyyy3Presenter extends Presenter<Fsyyy3View> {

    public void getsyyy3(String url, String userId, String appId) {
        JSONObject requestData = new JSONObject();//
        requestData.put("userId", userId);//
        requestData.put("appIds", appId);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).getyy3(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/savemyapp",
                requestBody
        ).enqueue(new Callback<ResponseSlbBean2<Object>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<Object>> call, Response<ResponseSlbBean2<Object>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().Onsyyy3Nodata(response.body().getMsg());
                    return;
                }
                getView().Onsyyy3Success(response.body().getMsg());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<Object>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().Onsyyy3Fail(string);
                call.cancel();
            }
        });
    }
}
