package com.geek.biz1.presenter.qcodes;

import com.alibaba.fastjson.JSONObject;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import tuoyan.com.xinghuo_daying.RetrofitNetNew3;
import com.geek.biz1.api.Biz3Api;
import com.geek.biz1.bean.qcodes.CaptchaGetIt;
import com.geek.biz1.view.qcodes.GetCaptchaView;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetCaptchaPresenter extends Presenter<GetCaptchaView> {

    public void getCaptcha(String url, String captchaType) {
        JSONObject requestData = new JSONObject();
        requestData.put("captchaType", captchaType);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), requestData.toString());
        RetrofitNetNew3.build(Biz3Api.class, getIdentifier()).getCaptcha(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url, "",
                requestBody
        ).enqueue(new Callback<ResponseSlbBean2<CaptchaGetIt>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<CaptchaGetIt>> call, Response<ResponseSlbBean2<CaptchaGetIt>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OnCaptchaNodata(response.body().getMsg());
                    return;
                }
                getView().OnCaptchaSuccess(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<CaptchaGetIt>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnCaptchaFail(string);
                call.cancel();
            }
        });
    }
}
