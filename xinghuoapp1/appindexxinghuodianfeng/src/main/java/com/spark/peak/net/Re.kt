package com.spark.peak.net

import android.os.Build
import com.google.gson.Gson
import com.spark.peak.BASE_BOOK_URL
import com.spark.peak.BASE_URL
import com.spark.peak.appId
import com.spark.peak.qyt_appId
import com.spark.peak.utlis.MD5Util
import com.spark.peak.utlis.NetWorkUtils
import com.spark.peak.utlis.SpUtil
import com.spark.peak.utlis.log.HttpLoggingInterceptor
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 创建者： huoshulei
 * 时间：  2017/4/19.
 */
object Re {
    val client by lazy {
        OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.MINUTES).writeTimeout(20, TimeUnit.MINUTES)
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            //缓存
            .addInterceptor(OfflineInterceptor()).addNetworkInterceptor(OnlineInterceptor())
            //无缓存
//            .addInterceptor(requestCacheInterceptor())
//            .addNetworkInterceptor(responseCacheInterceptor())
            .addInterceptor(tokenInterceptor()).build()
    }

    /**
     * 有网络的时候
     */
    class OnlineInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)
            val onlineCacheTime = 0 //在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
            return response.newBuilder().header("Cache-Control", "public, max-age=$onlineCacheTime")
                .removeHeader("Pragma").build()
        }
    }

    /**
     * 没有网的时候
     */
    class OfflineInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            if (!NetWorkUtils.isNetWorkReachable()) {
                //从缓存取数据
                val newRequest = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
                val maxTime = 60 * 60 * 24
                val response = chain.proceed(newRequest)
                return response.newBuilder().removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxTime").build()
            }
            return chain.proceed(request)
        }
    }

    val api: ApiService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)
    }

    val bookApi: ApiService by lazy {
        Retrofit.Builder().baseUrl(BASE_BOOK_URL).client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)
    }

    private fun networkInterceptor(): (Interceptor.Chain) -> Response = {
        //缓存拦截
        val request = it.request().newBuilder().removeHeader("User-Agent")
            .addHeader("User-Agent", getUserAgent()).build()
        when {
            NetWorkUtils.isNetWorkReachable() -> it.proceed(request)
            request.method().toLowerCase() == "get" -> it.proceed(
                request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
            )
            else -> it.proceed(request)
        }
    }

    private fun tokenInterceptor(): (Interceptor.Chain) -> Response = {
        //token拦截器
        val request = it.request()
        if (request.header("authorization") != null) {
            it.proceed(request)
        } else {
            val requestBuilder = request.newBuilder().addHeader("authorization", getHeard())
            val response = it.proceed(requestBuilder.build())
            if (NetWorkUtils.isNetWorkReachable()) {//缓存策略
                response.newBuilder().removeHeader("Pragma")
                    .header("Cache-Control", "private, max-age=" + 0).build()
            } else {
                response.newBuilder().removeHeader("Pragma").header(
                    "Cache-Control", "private, only-if-cached, max-stale=" + (60 * 60 * 24 * 7 * 4)
                ).build()
            }
            response
        }
    }

    /**+
     * appId	产品id	每一个app都会有一个唯一的id	"除web端调用的获取appid接口外，
    其他的任何一个接口调用都必须带有此字段"
    v	版本号
    os	操作系统	终端操作系统
    terminalType	终端类型	"1（手机客户端）、2（wap端）、
    3（PC端）、4（微信端）"
    channel	安装渠道	主要针对安卓端
    terminalid	访客id	终端唯一识别id,由终端生产
    timestamp	时间戳	"前端时间戳，时间的前后误差不
    超过10分钟是有效"
    token		登录用户的授权令牌
    sign	签名
    page	分页页数	正整数，从1开始计数	用于列表接口
    step	步长		用于列表接口
    userId	用户id	后台生成的用户唯一标识
    model	业务码	业务模块对应的业务码

     */
    private val headerBean = RequestHeader()

    fun getHeard(): String {
        val user = SpUtil.user
        headerBean.userId = user.userId ?: ""
        headerBean.token = user.token ?: ""
        headerBean.appId = appId

        headerBean.timestamp = (System.currentTimeMillis() / 10000 * 10000).toString()
        val sign =
            "sign" + headerBean.appId + headerBean.timestamp + headerBean.v + headerBean.terminalid + headerBean.token + headerBean.appId + "androidsign"
        headerBean.appSign = MD5Util.encrypt(sign)
        val toJson = Gson().toJson(headerBean)
        return toJson
    }

    fun getBookHeard(): String {
        val user = SpUtil.user
        headerBean.userId = user.userId ?: ""
        headerBean.token = user.token ?: ""
        headerBean.appId = qyt_appId

        headerBean.timestamp = (System.currentTimeMillis() / 10000 * 10000).toString()
        val sign =
            "sign" + headerBean.appId + headerBean.timestamp + headerBean.v + headerBean.terminalid + headerBean.token + headerBean.appId + "androidsign"
        headerBean.appSign = MD5Util.encrypt(sign)
        val toJson = Gson().toJson(headerBean)
        return toJson
    }

    private fun getUserAgent(): String {
        return "Android|Product-" + Build.PRODUCT + "|VERSION_CODES.BASE-" + Build.VERSION_CODES.BASE + "|system-" + Build.DISPLAY + "|MODEL-" + Build.MODEL + "|BRAND-" + Build.BRAND + "|CPU_ABI-" + Build.CPU_ABI + "|SDK-" + Build.VERSION.SDK + "|VERSION.RELEASE-" + Build.VERSION.RELEASE
    }
}
