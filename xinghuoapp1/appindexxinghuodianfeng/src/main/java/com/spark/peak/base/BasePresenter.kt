package com.spark.peak.base


import android.os.Build
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.spark.peak.bean.*
import com.spark.peak.net.*
import com.spark.peak.utlis.SpUtil
import com.spark.peak.utlis.log.L
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * 创建者： huoshulei
 * 时间：  2017/4/19.
 */

open class BasePresenter(private val progress: OnProgress) {
    protected val api by lazy { Re.api }
    protected val bookApi by lazy { Re.bookApi }
    private fun <T> Single<DataBase<T>>.map(): Single<Results<T>> {
        return map {
            when (it.code) {
                200 -> when (it.results.ret) {
//                    100 -> it.results
                    100, 10101, 10102 -> it.results
                    401 -> throw ApiException("${it.results.msg},401")
                    else -> throw ApiException(it.results.msg)
                }
                401 -> throw ApiException("${it.code},401")
                else -> throw ApiException(it.code.toString() + "-------错误码")
            }
        }
    }

    fun <T> Single<DataBase<T>>.subs(
        onNext: (Results<T>) -> Unit = {},
        onError: (ApiException) -> Unit = {}
    ) {
        progress.addDisposable(
            map().doOnSubscribe { progress.showProgress() }
                .onErrorResumeNext { handleException(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { d, e ->
                    d?.let { onNext(d) }
//                    e?.let {
                    if (e is ApiException) {
                        onError(e)
//                        progress.onError(e.message ?: "忽略")
                        progress.onError(e.message ?: e.toString())
                    }
//                    }
                    progress.dismissProgress()
                })

    }

    fun <T> Single<DataBase<T>>.sub(
        onNext: (Results<T>) -> Unit = {},
        onError: (ApiException) -> Unit = {}
    ) {
        progress.addDisposable(map()
            .onErrorResumeNext { handleException(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { d, e ->
                d?.let {
                    onNext(d)
                }
                if (e is ApiException) {
                    onError(e)
                    //    progress.onError(e.message ?: "忽略")
                    progress.onError(e.message ?: e.toString())
                }
            })

    }

    fun <T> Single<DataBase<T>>.subNoLife() {
        map().onErrorResumeNext { handleException(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { d, e ->
                d?.let { }
                if (e is ApiException) {
                    //    progress.onError(e.message ?: "忽略")
                    progress.onError(e.message ?: e.toString())
                }
            }

    }

    fun <T> Single<T>.subNotMap(onNext: (T) -> Unit = {}, onError: (ApiException) -> Unit = {}) {
        progress.addDisposable(
            onErrorResumeNext { handleException(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { d, e ->
                    d?.let { onNext(d) }
                    if (e is ApiException) {
                        onError(e)
                        //    progress.onError(e.message ?: "忽略")
                        progress.onError(e.message ?: e.toString())
                    }
                })

    }

    private fun <T> handleException(e: Throwable): Single<T> {
        L.d("错误信息:" + e.toString() + " : " + e.localizedMessage)
        val ex: ApiException
        when (e) {
            is HttpException -> {
                e.response()?.errorBody()?.string()?.let {
                    if (it.isNotBlank() && it.startsWith('{')) {
                        val body = Gson().fromJson(it, ErrorBody::class.java)
                        body?.message?.let {
                            if (isChinese(it.toCharArray())) return Single.error<T>(ApiException(it))
                        }
                    }
                }
                when (e.code()) {
//                    400 -> ex = ApiException("请求错误!请求中有语法错误")
//                    401 -> ex = ApiException("登录过期！请重新登录")
//                    402 -> ex = ApiException("需要付费")
//                    403 -> ex = ApiException("禁止访问!")
//                    404 -> ex = ApiException("not found")
//                    405 -> ex = ApiException("资源被禁止")
//                    406 -> ex = ApiException("请求错误!请求中有语法错误")
//                    407 -> ex = ApiException("需要代理身份认证")
//                    410 -> ex = ApiException("资源不可用")
//                    414 -> ex = ApiException("请求路径过长")
                    500 -> ex = ApiException("服务器开小差了")
//                    502 -> ex = ApiException("网络异常，请稍后再试")
//                    504 -> ex = ApiException("网络异常，请稍后再试")
                    else -> ex = ApiException(/*e.message()*/"忽略")
                }
            }
            is JSONException,
            is JsonParseException,
            is ParseException -> ex = ApiException("数据解析异常")
            is UnknownHostException,
            is ConnectException -> ex = ApiException("网络异常，请稍后再试")
            is TokenException,
//            is ServeException -> ex = ApiException(e.message)
            is SocketException -> ex = ApiException("无法连接到服务器")
            is SocketTimeoutException -> ex = ApiException("连接超时")
//            is NullPointerException -> ex = ApiException(e.message)
            is ApiException -> ex = e
            else -> ex = ApiException(/*e.message*/"忽略")
        }

        return Single.error<T>(ex)
    }

    private fun isChinese(array: CharArray): Boolean {
        return array
            .map { Character.UnicodeBlock.of(it) }
            .any {
                it === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                        || it === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                        || it === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                        || it === Character.UnicodeBlock.GENERAL_PUNCTUATION
                        || it === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                        || it === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
            }
    }

    fun userInfo(onNext: (UserInfo) -> Unit) {
        api.getUserInfo().subs(onNext = {
            onNext(it.body)
            EventBus.getDefault().post(EventMsg("login", -1))
        })
    }

    fun userInfo(onNext: (UserInfo) -> Unit, onError: () -> Unit) {
        api.getUserInfo().subs(onNext = {
            onNext(it.body)
            EventBus.getDefault().post(EventMsg("login", -1))
        }) {
            onError()
        }
    }

    fun changeUserInfo(info: UserInfo, onNext: () -> Unit = {}) {
        api.modifyInfo(info).subs(onNext = {
            userInfo {
                SpUtil.userInfo = it
                EventBus.getDefault().post(EventMsg("home", -1))
                onNext()
            }
        })
    }

    fun ifNewFB(onNext: (Int) -> Unit) {
        api.ifNewFB().sub(onNext = { onNext(it.body["ifnewfb"] ?: 0) })
    }

    fun ifNewNotice(onNext: (Int) -> Unit) {
        api.ifNewNotice().sub(onNext = { onNext((it.body["ifnewnotice"] ?: "0").toInt()) })
    }

    fun isLogin(onNext: (Boolean) -> Unit) {
        onNext(SpUtil.isLogin)
    }

    /**
     * 统计页面点击量
     */
    fun postPV(className: String) {
//        api.postPV(mutableMapOf("type" to className)).sub()
    }

    /**
     * 首次安装调用
     */
    fun postFirstInstall() {
        api.postFirstInstall(
            mutableMapOf(
                "manufacturerName" to Build.BRAND,
                "versionName" to Build.MODEL,
                "osVersion" to Build.VERSION.RELEASE
            )
        ).sub()
    }

    /**
     * 图片验证码
     */
    fun randomCode(phone: String, onNext: (Map<String, String>) -> Unit) {
        api.randomCode(phone).sub(onNext = { onNext(it.body) })
    }

    /**
     * 校验图片验证码
     */
    fun validateCode(
        phone: String,
        code: String,
        type: String,
        onNext: () -> Unit,
        onError: () -> Unit
    ) {
        api.validateCode(phone, code, type).sub(onNext = { onNext() }) { onError() }
    }

    fun addCollection(map: Map<String, String>, onNext: () -> Unit) {
        api.addCollection(map).sub(onNext = { onNext() })
    }

    fun deleteCollection(key: String, onNext: () -> Unit) {
        api.deleteCollection(mutableMapOf("targetkey" to key)).sub(onNext = { onNext() })
    }

//    fun resInfo(id: String, type: String, isDownload: String, function: (Map<String, String>) -> Unit) {
//        api.resInfo(id, type, isDownload).sub(onNext = { function(it.body) })
//    }

    fun getResInfo(
        id: String,
        source: String,
        isDownload: String,
        function: (ResourceInfo) -> Unit
    ) {
        api.getResInfo(id, source, isDownload).sub(onNext = { function(it.body) })
    }

    fun patchVersion(
        channel: String = "",
        appVersion: String = "",
        patchVersion: String = "",
        onNext: (NewVersion) -> Unit
    ) {
        api.getNewVersion(channel, appVersion, patchVersion).sub({ onNext(it.body) })
    }

    fun loadingPatch(url: String, function: (ResponseBody) -> Unit) {
        api.downloadLrc(url).subNotMap(onNext = {
            function(it)
        })
    }

    /**
     * 扫码获取内容
     */
    fun getDataByScan(data: Map<String, String>, onNext: (ScanResult) -> Unit) {
        api.getDataByScan(data).sub({ onNext(it.body) })
    }

    /**
     * 获取科目列表
     */
    fun getSubjects(key: String, onNext: (SubjectBody) -> Unit) {
        api.getSubjects(key).sub({ onNext(it.body) })
    }

    fun login(login: Map<String, String>, onNext: (Int, Boolean) -> Unit, onError: () -> Unit) {
        api.login(login).subs({ results ->
            SpUtil.user = results.body ?: LoginResponse()
            userInfo {
                saLogin(it, results.body.userId ?: "")
                SpUtil.userInfo = it
                val isLogin = !SpUtil.userInfo.grade.isNullOrBlank()
                SpUtil.isLogin = isLogin
                onNext(results.ret, isLogin)
            }
        }) { onError() }
    }

    /**
     * 获取教材同步音频资源
     */
    fun getJCTBRes(referenceKey: String, function: (ResourceInfo) -> Unit) {
        bookApi.getJCTBRes(referenceKey, "synclisten", Re.getBookHeard())
            .sub(onNext = { function(it.body) })
    }

    fun freeLogin(map: Map<String, String>, onNext: (Any, Boolean) -> Unit) {
        api.freePasswLogin(map).subs({ results ->
            val userInfo = results.body.userInfo
            userInfo?.let {
                SpUtil.user = userInfo
            }
            userInfo({
                saLogin(it, userInfo?.userId ?: "")
                SpUtil.userInfo = it
                val isLogin = !SpUtil.userInfo.grade.isNullOrBlank()
                SpUtil.isLogin = isLogin
                onNext(results.ret, isLogin)
            }) {
            }
        }) {
        }
    }

    protected fun saLogin(info: UserInfo, userId: String) {
        try {
            SensorsDataAPI.sharedInstance().login(userId);
            val properties = JSONObject()
            properties.put("phone_number", info.phone)
            properties.put("user_nickname", info.name)
            properties.put("xinghuo_userid", userId)
            properties.put(
                "last_login_time",
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE).format(
                    Date()
                )
            )
            SensorsDataAPI.sharedInstance().profileSet(properties)
        } catch (e: Exception) {
        }
    }
}