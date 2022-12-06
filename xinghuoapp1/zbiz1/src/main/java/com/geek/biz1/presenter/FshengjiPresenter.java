package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.FshengjiBean;
import com.geek.biz1.view.FshengjiView;
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

public class FshengjiPresenter extends Presenter<FshengjiView> {

    public void getshengji(String url, String serverVersionCode, String serverVersionName,
                           String appPackageName, String md5) {
        JSONObject requestData = new JSONObject();
        requestData.put("serverVersionCode", serverVersionCode);//
        requestData.put("serverVersionName", serverVersionName);//
        requestData.put("appPackageName", appPackageName);//
        requestData.put("md5", md5);//
//        requestData.put("id", id);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).getshengji(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/upgrade",
                requestBody).enqueue(new Callback<ResponseSlbBean2<FshengjiBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<FshengjiBean>> call, Response<ResponseSlbBean2<FshengjiBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OnFshengjiNodata(response.body().getMsg());
                    return;
                }
                getView().OnFshengjiSuccess(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<FshengjiBean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnFshengjiFail(string);
                call.cancel();
            }
        });
    }
}
