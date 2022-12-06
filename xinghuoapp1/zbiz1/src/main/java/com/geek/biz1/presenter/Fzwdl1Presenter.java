package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.FloginBean;
import com.geek.biz1.view.Fzwdl1View;
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

public class Fzwdl1Presenter extends Presenter<Fzwdl1View> {

    // 指纹登录1
    public void getzwdl1(String url, String fingerkey) {
        JSONObject requestData = new JSONObject();
        requestData.put("fingerkey", fingerkey);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).getzwdl1(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/api/auth/by/finger", requestBody).enqueue(new Callback<ResponseSlbBean2<FloginBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<FloginBean>> call, Response<ResponseSlbBean2<FloginBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().Onzwdl1Nodata(response.body().getMsg());
                    return;
                }
                getView().Onzwdl1Success(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<FloginBean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().Onzwdl1Fail(string);
                call.cancel();
            }
        });
    }

}
