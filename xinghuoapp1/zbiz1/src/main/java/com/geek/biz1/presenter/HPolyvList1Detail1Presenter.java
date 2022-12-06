package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import com.geek.biz1.api.Biz1Api;
import com.geek.biz1.bean.SPolyvList1Detail1Bean1;
import com.geek.biz1.view.HPolyvList1Detail1View;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HPolyvList1Detail1Presenter extends Presenter<HPolyvList1Detail1View> {

    public void get_polyvList1Detail1(String id,String userId,String playbackVideoId) {
        JSONObject requestData = new JSONObject();
        requestData.put("id", id);//
        requestData.put("userId", userId);//
        requestData.put("playbackVideoId", playbackVideoId);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz1Api.class, getIdentifier()).get_polyvList1Detail1(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + "/api/live/viewer/live/record/getViewerLiveDetail",
                BanbenUtils.getInstance().getVersion(),
                BanbenUtils.getInstance().getImei(),
                "", requestBody).enqueue(new Callback<ResponseSlbBean<SPolyvList1Detail1Bean1>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean<SPolyvList1Detail1Bean1>> call,
                                   Response<ResponseSlbBean<SPolyvList1Detail1Bean1>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OnPolyvList1Detail1Nodata(response.body().getMsg());
                    return;
                }
                getView().OnPolyvList1Detail1Success(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean<SPolyvList1Detail1Bean1>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnPolyvList1Detail1Fail(string);
                call.cancel();
            }
        });

    }

}
