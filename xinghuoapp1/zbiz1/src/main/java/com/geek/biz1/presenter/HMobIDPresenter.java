package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.biz1.api.Biz1Api;
import com.geek.biz1.view.HMobIDView;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HMobIDPresenter extends Presenter<HMobIDView> {

    public void get_mob_id() {
        JSONObject requestData = new JSONObject();
        requestData.put("terminalUniqueId", SPUtils.getInstance().getString("MOBID"));//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz1Api.class, getIdentifier()).get_mob_id(BuildConfigyewu.SERVER_ISERVICE_NEW1 + "/gwapi/workbenchserver/api/workbench/terminal/bind", requestBody).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
//                if (!response.body().isSuccess()) {
//                    getView().OnMobIDNodata(response.body().getMsg());
//                    return;
//                }
                getView().OnMobIDSuccess(response.body().toString());
                call.cancel();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnMobIDFail(string);
                call.cancel();
            }
        });

    }

}
