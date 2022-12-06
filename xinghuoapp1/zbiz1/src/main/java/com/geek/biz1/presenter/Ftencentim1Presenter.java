package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.Ftencentim1Bean;
import com.geek.biz1.view.Ftencentim1View;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ftencentim1Presenter extends Presenter<Ftencentim1View> {

    public void gettencentim1(String url, String userId, String expireTime) {
        JSONObject requestData = new JSONObject();
        requestData.put("userId", userId);//
//        requestData.put("appCode", appCode);//
        requestData.put("expireTime", expireTime);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).get_tencent_im1(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "",
                requestBody).enqueue(new Callback<ResponseSlbBean2<Ftencentim1Bean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<Ftencentim1Bean>> call, Response<ResponseSlbBean2<Ftencentim1Bean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OnFtencentim1Nodata(response.body().getMsg());
                    return;
                }
                getView().OnFtencentim1Success(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<Ftencentim1Bean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnFtencentim1Fail(string);
                call.cancel();
            }
        });
    }
}
