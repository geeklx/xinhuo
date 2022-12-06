package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.FBannerBean;
import com.geek.biz1.view.FBannerView;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FBannerPresenter extends Presenter<FBannerView> {

    public void getBannerList(String url,String belongId,String belongTypeId) {
        JSONObject requestData = new JSONObject();
        requestData.put("belongId", belongId);//
        requestData.put("belongTypeId", belongTypeId);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).get_banner(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/api/business/content/index/slider",
                requestBody).enqueue(new Callback<ResponseSlbBean2<FBannerBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<FBannerBean>> call, Response<ResponseSlbBean2<FBannerBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    getView().OnFBannerNodata("response.body().none()");
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OnFBannerNodata(response.body().getMsg());
                    return;
                }
                getView().OnFBannerSuccess(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<FBannerBean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnFBannerFail(string);
                call.cancel();
            }
        });
    }
}
