package com.geek.biz1.presenter;

import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.FaqzxBean;
import com.geek.biz1.view.FaqzxView;
import com.geek.libutils.libmvp.Presenter;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

public class FaqzxPresenter extends Presenter<FaqzxView> {

    public void getaqzx(String url) {
//        JSONObject requestData = new JSONObject();//
//        requestData.put("password", password);//
//        requestData.put("userName", userName);//
//        requestData.put("verification", verification);//
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).getaq1(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url+"/api/safecenter/view"
        ).enqueue(new Callback<ResponseSlbBean2<FaqzxBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<FaqzxBean>> call, Response<ResponseSlbBean2<FaqzxBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OnaqzxNodata(response.body().getMsg());
                    return;
                }
                getView().OnaqzxSuccess(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<FaqzxBean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OnaqzxFail(string);
                call.cancel();
            }
        });
    }
}
