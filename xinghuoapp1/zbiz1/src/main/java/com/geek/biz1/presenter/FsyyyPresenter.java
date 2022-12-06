package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.BjyyBeanYewu1;
import com.geek.biz1.view.FsyyyView;
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

public class FsyyyPresenter extends Presenter<FsyyyView> {

    public void getsyyy(String url, String userId, String identityId, String ordId, String cityName, String navigationBtmId) {
        JSONObject requestData = new JSONObject();//
        requestData.put("userId", userId);//
        requestData.put("identityId", identityId);//
        requestData.put("ordId", ordId);//
        requestData.put("cityName", cityName);//
        requestData.put("navigationBtmId", navigationBtmId);//
//        requestData.put("roleId", "3550789336511289491");//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).getyy1(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url,
                requestBody
        ).enqueue(new Callback<ResponseSlbBean2<BjyyBeanYewu1>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<BjyyBeanYewu1>> call, Response<ResponseSlbBean2<BjyyBeanYewu1>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OnsyyyNodata(response.body().getMsg());
                    return;
                }
                getView().OnsyyySuccess(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<BjyyBeanYewu1>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnsyyyFail(string);
                call.cancel();
            }
        });
    }
}
