package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import com.geek.biz1.api.Biz1Api;
import com.geek.biz1.view.Sbtyxs3View;
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


public class HTyxs3Presenter extends Presenter<Sbtyxs3View> {

    public void getHTyxs3Presenter(String userId, String courseCode, String source_sys, String orgType, String actionCode) {
        JSONObject requestData = new JSONObject();
        requestData.put("userId", userId);
        requestData.put("courseCode", courseCode);
        requestData.put("source_sys", source_sys);
        requestData.put("orgType", orgType);
        requestData.put("actionCode", actionCode);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz1Api.class, getIdentifier())
                .get9("http://192.168.2.31:8080/api/study/end", requestBody)
                .enqueue(new Callback<ResponseSlbBean<Object>>() {
                    @Override
                    public void onResponse(Call<ResponseSlbBean<Object>> call, Response<ResponseSlbBean<Object>> response) {
                        if (!hasView()) {
                            return;
                        }
                        if (response.body() == null) {
                            return;
                        }
                        if (!response.body().isSuccess()) {
                            getView().OnSbtyxs3Nodata(response.body().getMsg());
                            return;
                        }
                        getView().OnSbtyxs3Success(response.body().getMsg());
                        call.cancel();
                    }

                    @Override
                    public void onFailure(Call<ResponseSlbBean<Object>> call, Throwable t) {
                        if (!hasView()) {
                            return;
                        }
                        String string = BanbenUtils.getInstance().error_tips;
                        getView().OnSbtyxs3Fail(BuildConfigyewu.SERVER_ISERVICE_NEW1);
                        t.printStackTrace();
                        call.cancel();
                    }
                });
    }
}
