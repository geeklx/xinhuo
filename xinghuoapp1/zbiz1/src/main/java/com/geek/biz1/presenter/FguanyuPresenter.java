package com.geek.biz1.presenter;

import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.FguanyuBean;
import com.geek.biz1.view.FguanyuView;
import com.geek.libutils.libmvp.Presenter;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FguanyuPresenter extends Presenter<FguanyuView> {

    public void getguanyu(String url) {
//        JSONObject requestData = new JSONObject();
//        requestData.put("configkey", serverVersionCode);//
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).getguanyu(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/getabout").enqueue(new Callback<ResponseSlbBean2<FguanyuBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<FguanyuBean>> call, Response<ResponseSlbBean2<FguanyuBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OnFguanyuNodata(response.body().getMsg());
                    return;
                }
                getView().OnFguanyuSuccess(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<FguanyuBean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnFguanyuFail(string);
                call.cancel();
            }
        });
    }
}
