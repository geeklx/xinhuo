package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import com.geek.biz1.api.Biz1Api;
import com.geek.biz1.bean.HTyxs1Bean;
import com.geek.biz1.view.Sbtyxs1View;
import com.geek.libutils.libmvp.Presenter;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HTyxs1Presenter extends Presenter<Sbtyxs1View> {

    public void getHTyxs1Presenter(String userId, String courseCode, String source_sys, String orgType, String actionCode) {
        JSONObject requestData = new JSONObject();
        requestData.put("userId", userId);
        requestData.put("courseCode", courseCode);
        requestData.put("source_sys", source_sys);
        requestData.put("orgType", orgType);
        requestData.put("actionCode", actionCode);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz1Api.class, getIdentifier())
                .get7("http://192.168.2.31:8080/api/study/start", requestBody)
                .enqueue(new Callback<ResponseSlbBean<HTyxs1Bean>>() {
                    @Override
                    public void onResponse(Call<ResponseSlbBean<HTyxs1Bean>> call, Response<ResponseSlbBean<HTyxs1Bean>> response) {
                        if (!hasView()) {
                            return;
                        }
                        if (response.body() == null) {
                            return;
                        }
                        if (!response.body().isSuccess()) {
                            getView().OnSbtyxs1Nodata(response.body().getMsg());
                            return;
                        }
                        getView().OnSbtyxs1Success(response.body().getData());
                        call.cancel();
                    }

                    @Override
                    public void onFailure(Call<ResponseSlbBean<HTyxs1Bean>> call, Throwable t) {
                        if (!hasView()) {
                            return;
                        }
                        String string = BanbenUtils.getInstance().error_tips;
                        getView().OnSbtyxs1Fail(BuildConfigyewu.SERVER_ISERVICE_NEW1);
                        t.printStackTrace();
                        call.cancel();
                    }
                });
    }
}
