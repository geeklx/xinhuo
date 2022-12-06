package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.FloginBean;
import com.geek.biz1.view.FloginView;
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

public class FloginPresenter extends Presenter<FloginView> {

    public void getlogin2(String url, String password, String username, String verification) {
        JSONObject requestData = new JSONObject();//
        requestData.put("password", password);//
        requestData.put("username", username);//
        requestData.put("verification", verification);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).getlogin2(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/api/login",
                requestBody).enqueue(new Callback<ResponseSlbBean2<FloginBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<FloginBean>> call, Response<ResponseSlbBean2<FloginBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().Onlogin2Nodata(response.body().getMsg());
                    return;
                }
                getView().Onlogin2Success(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<FloginBean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().Onlogin2Fail(string);
                call.cancel();
            }
        });
    }
}
