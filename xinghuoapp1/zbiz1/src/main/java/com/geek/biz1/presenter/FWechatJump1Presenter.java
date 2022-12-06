package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.FWechatJumpBean;
import com.geek.biz1.view.FWechatJump1View;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FWechatJump1Presenter extends Presenter<FWechatJump1View> {

    public void getWechatJump1(String url,String userName, String appId) {
        JSONObject requestData = new JSONObject();
        requestData.put("userName", userName);//
        requestData.put("appId", appId);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).get_wechat_jump1(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "",
                requestBody).enqueue(new Callback<ResponseSlbBean2<FWechatJumpBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<FWechatJumpBean>> call, Response<ResponseSlbBean2<FWechatJumpBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OnFWechatJump1Nodata(response.body().getMsg());
                    return;
                }
                getView().OnFWechatJump1Success(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<FWechatJumpBean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnFWechatJump1Fail(string);
                call.cancel();
            }
        });
    }
}
