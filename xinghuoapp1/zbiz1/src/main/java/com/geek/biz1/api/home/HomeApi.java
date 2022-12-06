package com.geek.biz1.api.home;


import com.geek.biz1.bean.home.ClassificationListBean;
import com.geek.biz1.bean.home.ClassificationListByPageBean;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface HomeApi {

    // 分类列表请求/轮播图
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<ClassificationListBean>> getClassificationList(@Url String path, @Body RequestBody body);

    // 根据分类请求内容分页
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<ClassificationListByPageBean>> getClassificationListByPage(@Url String path, @Body RequestBody body);

    //轮播图
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<ClassificationListBean>> getClassificationBanner(@Url String path, @Body RequestBody body);


}
