package com.geek.biz1.presenter.qcodes;

import com.alibaba.fastjson.JSONObject;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.biz1.api.Biz3Api;
import com.geek.biz1.bean.qcodes.CaptchaCheckIt;
import com.geek.biz1.view.qcodes.CheckCaptchaView;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tuoyan.com.xinghuo_daying.RetrofitNetNew3;

public class CheckCaptchaPresenter extends Presenter<CheckCaptchaView> {

    public void checkCaptcha(String url, String captchaType, String token, String pointJson) {
        JSONObject requestData = new JSONObject();
        requestData.put("captchaType", captchaType);
        requestData.put("token", token);
        requestData.put("pointJson", pointJson);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew3.build(Biz3Api.class, getIdentifier()).checkCaptcha(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url, "",
                requestBody
        ).enqueue(new Callback<ResponseSlbBean2<CaptchaCheckIt>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<CaptchaCheckIt>> call, Response<ResponseSlbBean2<CaptchaCheckIt>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OnCheckCaptchaNodata(response.body().getMsg());
                    return;
                }
                getView().OnCheckCaptchaSuccess(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<CaptchaCheckIt>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnCheckCaptchaFail(string);
                call.cancel();
            }
        });
    }
}
