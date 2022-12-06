package com.geek.biz1.presenter.home;

import com.alibaba.fastjson.JSONObject;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.biz1.api.home.HomeApi;
import com.geek.biz1.bean.home.ClassificationListBean;
import com.geek.biz1.view.home.ClassificationView;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassificationBannerPresenter extends Presenter<ClassificationView> {
    public void getClassificationData(String belongId, String belongTypeId, String classificationId) {
        JSONObject requestData = new JSONObject();
        requestData.put("belongId", belongId);
        requestData.put("belongTypeId", belongTypeId);
        requestData.put("classificationId", classificationId);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8")
                , requestData.toString());
        RetrofitNetNew2.build(HomeApi.class, getIdentifier()).getClassificationList(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + "/api/business/content/index/slider",
                requestBody)
                .enqueue(new Callback<ResponseSlbBean2<ClassificationListBean>>() {
                    @Override
                    public void onResponse(Call<ResponseSlbBean2<ClassificationListBean>> call, Response<ResponseSlbBean2<ClassificationListBean>> response) {
                        if (!hasView()) {
                            return;
                        }
                        if (response.body() == null) {
                            return;
                        }
                        if (!response.body().isSuccess()) {
                            getView().onClassficationDataNoData(response.body().getMsg());
                            return;
                        }
                        getView().onClassficationDataSuccess(response.body().getData());
                        call.cancel();
                    }

                    @Override
                    public void onFailure(Call<ResponseSlbBean2<ClassificationListBean>> call, Throwable t) {
                        if (!hasView()) {
                            return;
                        }
                        String string = BanbenUtils.getInstance().net_tips;
                        getView().onClassficationDataFail(string);
                        call.cancel();
                    }
                });
    }
}
