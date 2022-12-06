package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;
import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.FAppCheckUseBean;
import com.geek.biz1.bean.FgrxxBean;
import com.geek.biz1.view.FAppCheckUseView;
import com.geek.libutils.data.MmkvUtils;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import com.tencent.mmkv.MMKV;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FAppCheckUsePresenter extends Presenter<FAppCheckUseView> {

    public void getAppCheckUse(String url, String id) {
        JSONObject requestData = new JSONObject();

        requestData.put("id", id);//
        String password = SPUtils.getInstance().getString("password_dt20", "");
        String username = SPUtils.getInstance().getString("username_dt20", "");
        requestData.put("u", username);//现在写死的
        requestData.put("p", password);//现在写死的
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).get_app_check_use(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "",
                requestBody).enqueue(new Callback<ResponseSlbBean2<FAppCheckUseBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<FAppCheckUseBean>> call, Response<ResponseSlbBean2<FAppCheckUseBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OnFAppCheckUseNodata(response.body().getMsg());
                    return;
                }
                getView().OnFAppCheckUseSuccess(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<FAppCheckUseBean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnFAppCheckUseFail(string);
                call.cancel();
            }
        });
    }
}
