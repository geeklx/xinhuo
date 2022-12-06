package com.geek.biz1.presenter;

import com.alibaba.fastjson.JSONObject;
import tuoyan.com.xinghuo_daying.BuildConfigyewu;
import com.geek.biz1.api.Biz1Api;
import com.geek.biz1.bean.SNewListBean1;
import com.geek.biz1.view.SNewSearchListView;
import com.geek.libutils.libmvp.Presenter;
import com.geek.libutils.libretrofit.BanbenUtils;
import com.geek.libutils.libretrofit.ResponseSlbBean;
import tuoyan.com.xinghuo_daying.RetrofitNetNew2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SNewSearchListPresenter extends Presenter<SNewSearchListView> {

    /*获取全部搜索结果*/
    public void getNew1SearchData1(String word, String userId, String userName, String searchCategoryId, String terminalId,final int which) {
        JSONObject requestData = new JSONObject();
        requestData.put("word", word);
        requestData.put("userId", userId);
        requestData.put("userName", userName);
        requestData.put("searchCategoryId", searchCategoryId);
        requestData.put("terminalId", terminalId);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData.toString());
        RetrofitNetNew2.build(Biz1Api.class, getIdentifier())
                .get_my_search_new2(BuildConfigyewu.SERVER_ISERVICE_NEW1 + "/gwapi/workbenchserver/api/unisearch/api/search", requestBody)
                .enqueue(new Callback<ResponseSlbBean<SNewListBean1>>() {
                    @Override
                    public void onResponse(Call<ResponseSlbBean<SNewListBean1>> call, Response<ResponseSlbBean<SNewListBean1>> response) {
                        if (!hasView()) {
                            return;
                        }
                        if (response.body() == null) {
                            return;
                        }
                        if (response.body().getCode() != 0) {
                            getView().OnNewSearchListNodata(response.body().getMsg(), which);
                            return;
                        }
                        getView().OnNewSearchListSuccess(response.body().getData(), which);
                        call.cancel();
                    }

                    @Override
                    public void onFailure(Call<ResponseSlbBean<SNewListBean1>> call, Throwable t) {
                        if (!hasView()) {
                            return;
                        }
//                String string = t.toString();
                        String string = BanbenUtils.getInstance().net_tips;
                        getView().OnNewSearchListFail(string, which);
                        call.cancel();
                    }
                });

    }

}
