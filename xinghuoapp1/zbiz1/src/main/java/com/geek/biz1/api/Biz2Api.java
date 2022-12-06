package com.geek.biz1.api;

import com.geek.biz1.bean.BjyyActFragment251Bean2;
import com.geek.biz1.bean.BjyyBeanYewu1;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.biz1.bean.BjyyBeanYewu4;
import com.geek.biz1.bean.FAppCheckUseBean;
import com.geek.biz1.bean.FBannerBean;
import com.geek.biz1.bean.FImBean;
import com.geek.biz1.bean.FWechatJumpBean;
import com.geek.biz1.bean.FaqzxBean;
import com.geek.biz1.bean.FcomBean;
import com.geek.biz1.bean.FconfigBean;
import com.geek.biz1.bean.FgrxxBean;
import com.geek.biz1.bean.FguanyuBean;
import com.geek.biz1.bean.FinitBean;
import com.geek.biz1.bean.FloginBean;
import com.geek.biz1.bean.FshengjiBean;
import com.geek.biz1.bean.Ftencentim1Bean;
import com.geek.biz1.bean.Fytxsphy1Bean;
import com.geek.libutils.libretrofit.ResponseSlbBean2;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface Biz2Api {

    // 升级
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FshengjiBean>> getshengji(@Url String path,
                                                    @Body RequestBody body);

    // 初始化
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FinitBean>> getinit(@Url String path);

    // 关于
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FguanyuBean>> getguanyu(@Url String path);

    // denglu2
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FloginBean>> getlogin2(@Url String path,
                                                 @Body RequestBody body);

    // 初始化获取接口1
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FconfigBean>> getconfig1(@Url String path,
                                                   @Body RequestBody body);

    // 提示语接口1
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<Object>> gettips(@Url String path,
                                           @Body RequestBody body);

    // 安全中心get
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FaqzxBean>> getaq1(@Url String path);


    // 上传用户头像信息1->file
    @Multipart
    @POST()
    Call<ResponseSlbBean2<FcomBean>> get_file1(@Url String path,
                                               @Part MultipartBody.Part body2);

    // 上传用户头像信息2->file
    @Multipart
    @POST()
    Call<ResponseSlbBean2<FcomBean>> get_file2(@Url String path,
                                               @Body RequestBody body);

    // 个人信息1
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FgrxxBean>> getgrxx(@Url String path);

    // 首页应用1
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<BjyyBeanYewu1>> getyy1(@Url String path,
                                                 @Body RequestBody body);

    // 编辑应用-上方应用
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<BjyyActFragment251Bean2>> getyy2(@Url String path,
                                                           @Body RequestBody body);

    // 编辑应用-保存应用
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<Object>> getyy3(@Url String path,
                                          @Body RequestBody body);

    // 指纹登录1
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FloginBean>> getzwdl1(@Url String path,
                                                @Body RequestBody body);

    // cate1
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<BjyyBeanYewu4>> getcate1(@Url String path,
                                                   @Body RequestBody body);

    // 腾讯IM初始化
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<Ftencentim1Bean>> get_tencent_im1(@Url String path,
                                                            @Body RequestBody body);

    // 容联云通讯视频会议初始化
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<Fytxsphy1Bean>> get_ytxsphy1(@Url String path);

    // IM/shipinghuiyi初始化
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FImBean>> get_im1(@Url String path,
                                            @Body RequestBody body);

    // 第三方小程序跳转
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FWechatJumpBean>> get_wechat_jump1(@Url String path,
                                                             @Body RequestBody body);

    // 获取应用维护信息
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FAppCheckUseBean>> get_app_check_use(@Url String path,
                                                               @Body RequestBody body);

    // lunbotu
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<BjyyBeanYewu3>> get_lunbotu1(@Url String path,
                                                       @Body RequestBody body);

    // banner
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST()
    Call<ResponseSlbBean2<FBannerBean>> get_banner(@Url String path,
                                                   @Body RequestBody body);
}
