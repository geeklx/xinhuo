package com.geek.biz1.presenter;

import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.bean.FcomBean;
import com.geek.biz1.view.Ffile1View;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ffile1Presenter extends Presenter<Ffile1View> {

    public void getfile1(String url, File imgfile) {
        MultipartBody.Part requestBody = MultipartBody.Part.createFormData("file", imgfile.getName(),
                RequestBody.create(MediaType.parse("image/*"), imgfile));
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).get_file1(
//                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/api/oss/resource-handle/upload?inner=false",
                "http://119.188.115.252:8090/api/oss/resource-handle/upload?inner=false",
                requestBody).enqueue(new Callback<ResponseSlbBean2<FcomBean>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<FcomBean>> call, Response<ResponseSlbBean2<FcomBean>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().Onfile1Nodata(response.body().getMsg());
                    return;
                }
                getView().Onfile1Success(response.body().getData());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<FcomBean>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().Onfile1Fail(string);
                call.cancel();
            }
        });
    }

}
