package com.geek.biz1.api;

import com.geek.biz1.bean.qcodes.CaptchaCheckIt;
import com.geek.biz1.bean.qcodes.CaptchaGetIt;
import com.geek.biz1.bean.qcodes.FDT20grxxBean;
import com.geek.biz1.bean.qcodes.FDT20loginBean;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface Biz3Api {


    // 灯塔2.0获取验证码
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<CaptchaGetIt>> getCaptcha(@Url String path, @Header("token") String token, @Body RequestBody body);

    // 灯塔2.0检测验证码
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<CaptchaCheckIt>> checkCaptcha(@Url String path,@Header("token") String token,  @Body RequestBody body);

    // 灯塔2.0登录
    @FormUrlEncoded
    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FDT20loginBean>> getlogin20(@Url String path,
                                                      @Header("Authorization") String authorizationm,
                                                      @Header("token") String token,
                                                      @FieldMap Map<String, String> map);

    // 灯塔2.0个人信息1
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FDT20grxxBean>> getUserInfo20(@Url String path,@Header("token") String token);


}
