package com.geek.biz1.presenter.home;

import com.alibaba.fastjson.JSONObject;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import tuoyan.com.xinghuo_daying.UrlManager;
import com.geek.biz1.api.home.HomeApi;
import com.geek.biz1.bean.home.ClassificationListByPageBean;
import com.geek.biz1.view.home.ClassificationByPageView;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;


import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassificationByPagePresenter extends Presenter<ClassificationByPageView> {
    //    "推荐":1003,"党建动态'：1001；"公示公告'：1002；"乡镇要闻":1003; "政策文件'：1004；
    public void getClassificationDataByPage(int page, int limit, String belongId, String belongTypeId) {
        JSONObject requestData = new JSONObject();
        requestData.put("page", page);
        requestData.put("limit", limit);
        requestData.put("belongId", belongId);
        requestData.put("belongTypeId", belongTypeId);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8")
                , requestData.toString());
        RetrofitNetNew2.build(HomeApi.class, getIdentifier()).getClassificationListByPage(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + "/gwapi/workbenchserver/api/business/content/index/queryPage",
                requestBody)
                .enqueue(new Callback<ResponseSlbBean2<ClassificationListByPageBean>>() {
                    @Override
                    public void onResponse(Call<ResponseSlbBean2<ClassificationListByPageBean>> call, Response<ResponseSlbBean2<ClassificationListByPageBean>> response) {
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
                    public void onFailure(Call<ResponseSlbBean2<ClassificationListByPageBean>> call, Throwable t) {
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
