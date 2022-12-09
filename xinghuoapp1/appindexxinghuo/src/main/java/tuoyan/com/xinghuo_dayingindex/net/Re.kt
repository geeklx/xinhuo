package tuoyan.com.xinghuo_dayingindex.net


import android.os.Build
import android.util.Base64
import com.geek.libutils.app.BaseApp
import com.google.gson.Gson
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import tuoyan.com.xinghuo_dayingindex.BASE_URL
import tuoyan.com.xinghuo_dayingindex.appId
import tuoyan.com.xinghuo_dayingindex.utlis.MD5Util
import tuoyan.com.xinghuo_dayingindex.utlis.NetWorkUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.log.HttpLoggingInterceptor
import tuoyan.com.xinghuo_dayingindex.utlis.log.RequestFilterInterceptor
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * 创建者： huoshulei
 * 时间：  2017/4/19.
 */

object Re {
    val api: ApiService by lazy {
        val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.MINUTES).writeTimeout(20, TimeUnit.MINUTES)
            .protocols(Collections.singletonList(Protocol.HTTP_1_1)).cache(
                Cache(
                    File(BaseApp.get().externalCacheDir, "cache_daying"), 1024 * 1024 * 200
                )
            )//缓存
            .addInterceptor(HttpLoggingInterceptor())//
            .addInterceptor(RequestFilterInterceptor())//重复请求过滤器
            .addInterceptor(tokenInterceptor())
            //缓存
            .addInterceptor(OfflineInterceptor()).addNetworkInterceptor(OnlineInterceptor())
            //无缓存
//            .addInterceptor(requestCacheInterceptor())
//            .addNetworkInterceptor(responseCacheInterceptor())
            .build()
        Retrofit.Builder().baseUrl(BASE_URL).client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)
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


//    val apiCache: ApiService by lazy {
//        val client = OkHttpClient.Builder()
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(20, TimeUnit.MINUTES)
//            .writeTimeout(20, TimeUnit.MINUTES)
//            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
//            .cache(Cache(File(MyApp.instance.externalCacheDir, "cache_daying"), 1024 * 1024 * 200))//缓存
//            .addInterceptor(tokenInterceptor())
//            .addInterceptor(cacheInterceptor())
//            .build()
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(client)
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService::class.java)
//    }

    private fun tokenInterceptor(): (Interceptor.Chain) -> Response = {
        //token拦截器
        val request = it.request()
        if (request.header("authorization") != null) {
            it.proceed(request)
        } else {
            val requestBuilder = request.newBuilder().addHeader("authorization", getHeard())
            it.proceed(requestBuilder.build())
        }
    }

    private fun networkInterceptor(): (Interceptor.Chain) -> Response = {
        val request = it.request().newBuilder().removeHeader("User-Agent")
            .addHeader("User-Agent", getUserAgent()).cacheControl(CacheControl.FORCE_NETWORK)
            .build()
        val response = it.proceed(request)
        response.newBuilder().removeHeader("Pragma")
            .header("Cache-Control", "private, max-age=" + (60 * 60 * 24 * 7)).build()
    }

    private fun requestCacheInterceptor(): (Interceptor.Chain) -> Response = { chain ->
        val req = chain.request()
        val build =
            req.newBuilder().removeHeader("User-Agent").addHeader("User-Agent", getUserAgent())
        if (isCache(req)) {
            build.cacheControl(CacheControl.FORCE_CACHE)
        }
        val request = build.build()
        chain.proceed(request)
    }

    private fun isCache(req: Request): Boolean {
        return !NetWorkUtils.isNetWorkReachable() && isCacheUrl(req)
    }

    private fun isCacheUrl(req: Request): Boolean {
        val baseUrl = req.url().toString()
        return baseUrl.contains("common/homePageInfoN") || baseUrl.contains("goods/bookList") || baseUrl.contains(
            "admodel/list"
        )
    }

    private fun responseCacheInterceptor(): (Interceptor.Chain) -> Response = { chain ->
        val response = chain.proceed(chain.request()).newBuilder().removeHeader("Pragma")
            .removeHeader("Cache-Control")
            .addHeader("Cache-Control", CacheControl.FORCE_CACHE.toString()).build()
        response
    }

    private fun cacheInterceptor(): (Interceptor.Chain) -> Response = {
        val request = it.request().newBuilder().removeHeader("User-Agent")
            .addHeader("User-Agent", getUserAgent()).cacheControl(CacheControl.FORCE_CACHE).build()
        val response = it.proceed(request)
        response.newBuilder().removeHeader("Pragma")
            .header("Cache-Control", "private, max-age=" + (60 * 60 * 24 * 7)).build()
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
        if (SpUtil.ipStr.isNullOrBlank()) {
            headerBean.ipStr = ""
        } else {
            headerBean.ipStr = Base64.encodeToString(SpUtil.ipStr.toByteArray(), Base64.DEFAULT)
        }

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
