package com.geek.biz1.presenter;

import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.Fytxsphy1Bean;
import com.geek.biz1.view.Fytxsphy1View;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fytxsphy1Presenter extends Presenter<Fytxsphy1View> {

    public void getytxsphy1(String url) {
//        JSONObject requestData = new JSONObject();
//        requestData.put("id", id);
//        requestData.put("limit", limit);
//        requestData.put("page", page);
//        requestData.put("tag", tag);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).get_ytxsphy1(BuildConfigyewu.SERVER_ISERVICE_NEW1 + url)
                .enqueue(new Callback<ResponseSlbBean2<Fytxsphy1Bean>>() {
                    @Override
                    public void onResponse(Call<ResponseSlbBean2<Fytxsphy1Bean>> call, Response<ResponseSlbBean2<Fytxsphy1Bean>> response) {
                        if (!hasView()) {
                            return;
                        }
                        if (response.body() == null) {
                            return;
                        }
                        if (!response.body().isSuccess()) {
                            getView().Onytxsphy1Nodata(response.body().getMsg());
                            return;
                        }
                        getView().Onytxsphy1Success(response.body().getData());
                        call.cancel();
                    }

                    @Override
                    public void onFailure(Call<ResponseSlbBean2<Fytxsphy1Bean>> call, Throwable t) {
                        if (!hasView()) {
                            return;
                        }
//                String string = t.toString();
                        String string = BanbenUtils.getInstance().net_tips;
                        getView().Onytxsphy1Fail(string);
                        call.cancel();
                    }
                });

    }

}
