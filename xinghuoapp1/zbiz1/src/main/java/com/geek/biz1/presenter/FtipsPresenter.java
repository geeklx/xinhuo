package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.biz1.api.Biz2Api;
import com.geek.biz1.view.FtipsView;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean2;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FtipsPresenter extends Presenter<FtipsView> {

    // 验证码获取接口bufen
    public void gettips(String url, String phoneNum) {
        JSONObject requestData = new JSONObject();
        requestData.put("phoneNum", phoneNum);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).gettips(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/api/verification/code", requestBody).enqueue(new Callback<ResponseSlbBean2<Object>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<Object>> call, Response<ResponseSlbBean2<Object>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OntipsNodata(response.body().getMsg());
                    return;
                }
                getView().OntipsSuccess(response.body().getMsg());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<Object>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OntipsFail(string);
                call.cancel();
            }
        });
    }

    // 退出登录接口bufen
    public void gettips_token(String url, String token) {
        JSONObject requestData = new JSONObject();
        requestData.put("token", token);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).gettips(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/api/logout", requestBody).enqueue(new Callback<ResponseSlbBean2<Object>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<Object>> call, Response<ResponseSlbBean2<Object>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OntipsNodata(response.body().getMsg());
                    return;
                }
                getView().OntipsSuccess(response.body().getMsg());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<Object>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OntipsFail(string);
                call.cancel();
            }
        });
    }

    // 忘记密码/注册接口bufen
    public void gettips(String url, String password, String userName, String verification) {
        JSONObject requestData = new JSONObject();
        requestData.put("password", password);//
        requestData.put("username", userName);//
        requestData.put("verification", verification);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).gettips(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url, requestBody).enqueue(new Callback<ResponseSlbBean2<Object>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<Object>> call, Response<ResponseSlbBean2<Object>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OntipsNodata(response.body().getMsg());
                    return;
                }
                getView().OntipsSuccess(response.body().getMsg());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<Object>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OntipsFail(string);
                call.cancel();
            }
        });
    }

    // 注册接口bufen
    public void getRegistertips(String url, String password, String userName, String verification, String idcard, String name) {
        JSONObject requestData = new JSONObject();
        requestData.put("password", password);//
        requestData.put("username", userName);//
        requestData.put("verification", verification);//
        requestData.put("idcard", idcard);
        requestData.put("name", name);
        requestData.put("projectCode", "fs-user-project-1641547050881");
        requestData.put("orgCode", "fs-user-org-1641547101342");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).gettips(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url, requestBody).enqueue(new Callback<ResponseSlbBean2<Object>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<Object>> call, Response<ResponseSlbBean2<Object>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OntipsNodata(response.body().getMsg());
                    return;
                }
                getView().OntipsSuccess(response.body().getMsg());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<Object>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OntipsFail(string);
                call.cancel();
            }
        });
    }

    // 安全中心接口setbufen
    public void gettips_aq2(String url, String id, String telephone, String email, String fixedTelephone) {
        JSONObject requestData = new JSONObject();
        requestData.put("id", id);//用户id
        if (telephone != null && !"".equals(telephone)) {
            requestData.put("telephone", telephone);//手机号
        } else if (email != null && !"".equals(email)) {
            requestData.put("email", email);//邮箱
        } else if (fixedTelephone != null && !"".equals(fixedTelephone)) {
            requestData.put("fixedTelephone", fixedTelephone);//座机
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).gettips(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/api/saveuser", requestBody).enqueue(new Callback<ResponseSlbBean2<Object>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<Object>> call, Response<ResponseSlbBean2<Object>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OntipsNodata(response.body().getMsg());
                    return;
                }
                getView().OntipsSuccess(response.body().getMsg());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<Object>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OntipsFail(string);
                call.cancel();
            }
        });
    }

    // 安全中心接口setbufen
    public void gettips_aq1(String url, String telphone, String hostmail, String landline, String isfinger, String fingerkey) {
        JSONObject requestData = new JSONObject();
        requestData.put("telphone", telphone);//
        requestData.put("hostmail", hostmail);//
        requestData.put("landline", landline);//
        requestData.put("isfinger", isfinger);//
        requestData.put("fingerkey", fingerkey);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).gettips(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/api/safecenter/update", requestBody).enqueue(new Callback<ResponseSlbBean2<Object>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<Object>> call, Response<ResponseSlbBean2<Object>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OntipsNodata(response.body().getMsg());
                    return;
                }
                getView().OntipsSuccess(response.body().getMsg());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<Object>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OntipsFail(string);
                call.cancel();
            }
        });
    }

    // 上传图片地址接口bufen
    public void gettips_img2(String url, String id, String url2, String sign, String realName, String telephone, String sex) {
        JSONObject requestData = new JSONObject();
        requestData.put("id", id);//用户id
        if (url2 != null && !"".equals(url2)) {
            requestData.put("headImgUrl", url2);//图片地址
        } else if (sign != null && !"".equals(sign)) {
            requestData.put("signature", sign);//签名地址
        } else if (realName != null && !"".equals(realName)) {
            requestData.put("realName", realName);//真实姓名
        } else if (telephone != null && !"".equals(telephone)) {
            requestData.put("telephone", telephone);//手机号
        } else if (sex != null && !"".equals(sex)) {
            requestData.put("sex", sex);//性别
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).gettips(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/api/saveuser", requestBody).enqueue(new Callback<ResponseSlbBean2<Object>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<Object>> call, Response<ResponseSlbBean2<Object>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OntipsNodata(response.body().getMsg());
                    return;
                }
                getView().OntipsSuccess(response.body().getMsg());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<Object>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OntipsFail(string);
                call.cancel();
            }
        });
    }

    // 上传图片地址接口bufen
    public void gettips_img1(String url, String url2) {
        JSONObject requestData = new JSONObject();
        requestData.put("url", url2);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).gettips(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/api/saveImg", requestBody).enqueue(new Callback<ResponseSlbBean2<Object>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<Object>> call, Response<ResponseSlbBean2<Object>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OntipsNodata(response.body().getMsg());
                    return;
                }
                getView().OntipsSuccess(response.body().getMsg());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<Object>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OntipsFail(string);
                call.cancel();
            }
        });
    }

    // 保存签名接口bufen
    public void gettips_sign(String url, String sign) {
        JSONObject requestData = new JSONObject();
        requestData.put("sign", sign);//
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).gettips(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/api/updateSign", requestBody).enqueue(new Callback<ResponseSlbBean2<Object>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<Object>> call, Response<ResponseSlbBean2<Object>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OntipsNodata(response.body().getMsg());
                    return;
                }
                getView().OntipsSuccess(response.body().getMsg());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<Object>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OntipsFail(string);
                call.cancel();
            }
        });
    }

    // 保存签名接口bufen
    public void gettips_change_pwd(String url, String originPassword,String password) {
        JSONObject requestData = new JSONObject();
        requestData.put("originPassword", originPassword);
        requestData.put("password", password);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz2Api.class, getIdentifier()).gettips(
                BuildConfigyewu.SERVER_ISERVICE_NEW1 + url + "/api/update/password", requestBody).enqueue(new Callback<ResponseSlbBean2<Object>>() {
            @Override
            public void onResponse(Call<ResponseSlbBean2<Object>> call, Response<ResponseSlbBean2<Object>> response) {
                if (!hasView()) {
                    return;
                }
                if (response.body() == null) {
                    return;
                }
                if (!response.body().isSuccess()) {
                    getView().OntipsNodata(response.body().getMsg());
                    return;
                }
                getView().OntipsSuccess(response.body().getMsg());
                call.cancel();
            }

            @Override
            public void onFailure(Call<ResponseSlbBean2<Object>> call, Throwable t) {
                if (!hasView()) {
                    return;
                }
//                String string = t.toString();
                String string = BanbenUtils.getInstance().net_tips;
                getView().OntipsFail(string);
                call.cancel();
            }
        });
    }
}
