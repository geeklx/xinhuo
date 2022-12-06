package com.geek.biz1.presenter;

import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.FgrxxBean;
import com.geek.biz1.view.FgrxxView;
import com.geek.libutils.libmvp.Presenter;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FgrxxPresenter extends Presenter<FgrxxView> {

    public void getgrxx(String url) {
//        JSONObject requestData = new JSONObject();//
//        requestData.put("token", token);//
//        requestData.put("userName", userName);//
//        requestData.put("verification", verification);//
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).getgrxx(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/api/userInfo"
        ).enqueue(new Callback<ResponseSlbBean2<FgrxxBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<FgrxxBean>> call, Response<ResponseSlbBean2<FgrxxBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OngrxxNodata(response.body().getMsg());
                    return;
                }
                getView().OngrxxSuccess(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<FgrxxBean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OngrxxFail(string);
                call.cancel();
            }
        });
    }
}
