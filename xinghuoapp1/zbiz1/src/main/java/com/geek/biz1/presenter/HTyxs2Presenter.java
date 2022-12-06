package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import com.geek.biz1.api.Biz1Api;
import com.geek.biz1.view.Sbtyxs2View;
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


public class HTyxs2Presenter extends Presenter<Sbtyxs2View> {

    public void getHTyxs2Presenter(String userId, String courseCode, String studyTimes) {
        JSONObject requestData = new JSONObject();
        requestData.put("userId", userId);//
        requestData.put("courseCode", courseCode);//
        requestData.put("studyTimes", studyTimes);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz1Api.class, getIdentifier())
                .get8("http://192.168.2.31:8080/api/study/progress2", requestBody)
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
                            getView().OnSbtyxs2Nodata(response.body().getMsg());
                            return;
                        }
                        getView().OnSbtyxs2Success(response.body().getMsg());
                        call.cancel();
                    }

                    @Override
                    public void onFailure(Call<ResponseSlbBean<Object>> call, Throwable t) {
                        if (!hasView()) {
                            return;
                        }
                        String string = BanbenUtils.getInstance().error_tips;
                        getView().OnSbtyxs2Fail(BuildConfigyewu.SERVER_ISERVICE_NEW1);
                        t.printStackTrace();
                        call.cancel();
                    }
                });
    }
}
