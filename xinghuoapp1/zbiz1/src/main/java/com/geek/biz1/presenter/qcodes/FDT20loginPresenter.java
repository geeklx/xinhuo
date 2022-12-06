package com.geek.biz1.presenter.qcodes;

import android.text.TextUtils;

import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import tuoyan.com.xinghuo_daying.RetrofitNetNew3;
import com.geek.biz1.api.Biz3Api;
import com.geek.biz1.bean.qcodes.FDT20loginBean;
import com.geek.biz1.view.qcodes.FDT20loginView;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FDT20loginPresenter extends Presenter<FDT20loginView> {

    public void getlogin20(String url, String mobilePassword, String password, String username, String captchaVerification,
                           String refreshToken, String verification) {
        Map<String, String> requestData = new HashMap<>();//
        requestData.put("grant_type", mobilePassword);//
        requestData.put("password", password);//
        requestData.put("username", username);//
        requestData.put("captchaVerification", captchaVerification);//
        if (!TextUtils.isEmpty(refreshToken)) {
            requestData.put("refresh_token", refreshToken);//
        }

//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), requestData.toString());
        RetrofitNetNew3.build(Biz3Api.class, getIdentifier()).getlogin20(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url,
                verification,
                "",
                requestData).enqueue(new Callback<ResponseSlbBean2<FDT20loginBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<FDT20loginBean>> call, Response<ResponseSlbBean2<FDT20loginBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.code() != 200) {
                    getView().Onlogin2Fail(response.message());
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
            public void onFailure(Call<ResponseSlbBean2<FDT20loginBean>> call, Throwable t) {
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
