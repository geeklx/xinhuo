package tuoyan.com.xinghuo_dayingindex.base


//import okhttp3.MediaType.Companion.toMediaTypeOrNull
import android.os.Build
import android.util.Log
import com.geek.libutils.app.BaseApp
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.net.*
import tuoyan.com.xinghuo_dayingindex.utlis.NetWorkUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.log.L
import java.io.File
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.charset.Charset
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * 创建者： huoshulei
 * 时间：  2017/4/19.
 */

open class BasePresenter(private val progress: OnProgress) {
    protected val api by lazy { Re.api }

    //    protected val cacheApi by lazy { Re.apiCache }
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
        onNext: (Results<T>) -> Unit = {}, onError: (ApiException) -> Unit = {}
    ) {
        progress.addDisposable(map().doOnSubscribe { progress.showProgress() }
            .onErrorResumeNext { handleException(it) }.observeOn(AndroidSchedulers.mainThread())
            .subscribe { d, e ->
                progress.dismissProgress()
                d?.let { onNext(d) }
//                    e?.let {
                if (e is ApiException) {
                    onError(e)
//                        progress.onError(e.message ?: "忽略")
                    progress.onError(e.message ?: e.toString())
                }
//                    }
            })

    }

    fun <T> Single<DataBase<T>>.sub(
        onNext: (Results<T>) -> Unit = {}, onError: (ApiException) -> Unit = {}
    ) {
        progress.addDisposable(map().onErrorResumeNext { handleException(it) }
            .observeOn(AndroidSchedulers.mainThread()).subscribe { d, e ->
                d?.let { onNext(d) }
                if (e is ApiException) {
                    onError(e)
                    //    progress.onError(e.message ?: "忽略")
                    progress.onError(e.message ?: e.toString())
                }
            })

    }

    fun <T> Single<T>.subNotMap(onNext: (T) -> Unit = {}, onError: (ApiException) -> Unit = {}) {
        progress.addDisposable(onErrorResumeNext { handleException(it) }.observeOn(AndroidSchedulers.mainThread())
            .subscribe { d, e ->
                d?.let { onNext(d) }
                if (e is ApiException) {
                    onError(e)
                    //    progress.onError(e.message ?: "忽略")
                    progress.onError(e.message ?: e.toString())
                }
            })

    }


    fun <T> Single<DataBase<T>>.subNoLife() {
        map().onErrorResumeNext { handleException(it) }.observeOn(AndroidSchedulers.mainThread())
            .subscribe { d, e ->
                d?.let { }
                if (e is ApiException) {
                    //    progress.onError(e.message ?: "忽略")
                    progress.onError(e.message ?: e.toString())
                }
            }

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
                    else -> ex = ApiException(/*"别在意这条提示${e.message()}"*/"忽略")
                }
            }
            is JSONException, is JsonParseException, is ParseException -> ex =
                ApiException("数据解析异常")
            is UnknownHostException, is ConnectException -> ex = ApiException("网络异常，请稍后再试")
            is TokenException,
//            is ServeException -> ex = ApiException(e.message)
            is SocketException -> ex = ApiException("无法连接到服务器")
            is SocketTimeoutException -> ex = ApiException("连接超时")
//            is NullPointerException -> ex = ApiException(e.message)
            is ApiException -> ex = e
            else -> ex = ApiException(/*"别在意这条提示${e.message}"*/"忽略")
        }

        return Single.error<T>(ex)
    }

    private fun isChinese(array: CharArray): Boolean {
        return array.map { Character.UnicodeBlock.of(it) }.any {
            it === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || it === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || it === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || it === Character.UnicodeBlock.GENERAL_PUNCTUATION || it === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || it === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
        }
    }

    fun userInfo(onNext: (UserInfo) -> Unit) {
        api.getUserInfo().sub(onNext = {
            onNext(it.body)
//            EventBus.getDefault().post(EventMsg("login", -1))
        })
    }

    fun changeUserInfo(info: Map<String, String>, onNext: () -> Unit = {}) {
        api.modifyInfo(info).subs(onNext = { _ ->
            userInfo {
                SpUtil.userInfo = it
                try {
                    val properties = JSONObject()
                    properties.put("user_nickname", it.name)
                    SensorsDataAPI.sharedInstance().profileSet(properties)
                } catch (e: Exception) {
                }
//                EventBus.getDefault().post(EventMsg("home", -1))
                onNext()
            }
        })
    }

    fun ifNewFB(flag: String = "a", onNext: (Boolean) -> Unit) {
        isLogin { it ->
            if (it) {
                // : 2018/9/18 13:24  新消息
                api.ifNewFB(flag).sub(onNext = { onNext((it.body["readFlag"] ?: 0) == 0) })
            }
        }
    }

    fun isLogin(onNext: (Boolean) -> Unit) {
        val token = SpUtil.user.token
        if (NetWorkUtils.isNetWorkReachable()) {
            if (token == null || token == "" || !SpUtil.isLogin) {
                onNext(false)
            } else {
                api.validToken(token).sub({
                    onNext("1" == it.body["isValid"])
                })
            }
        } else {
            onNext(SpUtil.isLogin && !token.isNullOrEmpty())
        }
    }


    /**
     * 统计页面点击量
     */
    fun postPV(className: String) {
//        api.postPV(mutableMapOf("type" to className)).sub(onError = {
//            if (it.message == "网络异常，请稍后再试" || it.message == "无法连接到服务器" || it.message == "连接超时") {
//            } else
//                it.message = "忽略"
//        })
    }

    /**
     * 统计广告点击量
     */
    fun advertisingPv(key: String) {
        api.advertisingPv(key).sub(onError = {
            if (it.message == "网络异常，请稍后再试" || it.message == "无法连接到服务器" || it.message == "连接超时") {
            } else it.message = "忽略"
        })
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
        api.getTenectJS(phone).sub(onNext = { onNext(it.body) })
    }

    fun getTenectJS(phone: String, onNext: (Map<String, String>) -> Unit, onError: () -> Unit) {
        api.getTenectJS(phone).sub(onNext = { onNext(it.body) }, onError = { onError() })
    }

    fun getCheckType(phone: String, onNext: (Map<String, String>) -> Unit, onError: () -> Unit) {
        api.getCheckType(phone).sub(onNext = { onNext(it.body) }, onError = { onError() })
    }

    fun getAliChecked(
        phone: String,
        token: String,
        sessionid: String,
        sig: String,
        onNext: (Map<String, String>) -> Unit,
        onError: () -> Unit
    ) {
        api.getAliChecked(
            mutableMapOf(
                "phone" to phone, "token" to token, "sessionid" to sessionid, "sig" to sig
            )
        ).sub(onNext = { onNext(it.body) }, onError = { onError() })
    }


    /**
     * 校验图片验证码
     */
    fun validateCode(
        phone: String, code: String, type: String, onNext: () -> Unit, onError: () -> Unit
    ) {
        api.validateCode(phone, code, type).sub(onNext = { onNext() }) { onError() }
    }

    fun validateCode(phone: String, code: String, onNext: () -> Unit, onError: () -> Unit) {
        api.validateCode(phone, code).sub(onNext = { onNext() }) { onError() }
    }

    fun sendCode(smsCode: SMSCode, onNext: () -> Unit, onError: () -> Unit) {
        api.getSms(smsCode).sub({ onNext() }) { onError() }
    }

    fun addCollection(map: Map<String, String>, onNext: () -> Unit) {
        api.addCollection(map).sub(onNext = { onNext() })
    }

    fun deleteCollection(key: String, onNext: () -> Unit) {
        api.deleteCollection(mutableMapOf("targetkey" to key)).sub(onNext = { onNext() })
    }

    /**
     * 扫码获取内容
     */
    fun getDataByScan(data: Map<String, String>, onNext: (ScanResult) -> Unit) {
        api.getDataByScan(data).sub({ onNext(it.body) })
    }

    /**
     * 扫码获取资源列表
     */
    fun getResList(key: String, onNext: (ScanRes) -> Unit) {
        api.getResList(key).sub({ onNext(it.body) })
    }

    /**
     * 扫描图书激活码
     */
    fun activatedFakeCode(data: Map<String, String>, onNext: () -> Unit) {
        api.activatedFakeCode(data).sub({ onNext() })
    }

    /**
     * 批量上传图片
     */
    fun upload(
        model: String,
        files: List<File>,
        onError: (String) -> Unit = {},
        onNext: (ArrayList<String>) -> Unit
    ) {
        val imgs = mutableListOf<MultipartBody.Part>()
//        files.forEach {
//            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), it)
//            val part = MultipartBody.Part.createFormData("file", it.name, requestBody)
//            imgs.add(part)
//        }
//        api.uploadImage(model, imgs).subs({
//            var imgs = it.body["list"] as ArrayList<String>
//            Log.e("image_list", imgs.toString())
//            onNext(imgs)
//        })
        var size = 0
        luban(files) {
            it?.let {
//                val requestBody = it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), it)
                val part = MultipartBody.Part.createFormData("file", it.name, requestBody)
                imgs.add(part)
            }
            size++
            if (size == files.size) {
                api.uploadImage(model, imgs).subs({
                    val imgs = it.body["list"] as ArrayList<String>
                    Log.e("image_list", imgs.toString())
                    onNext(imgs)
                }, {
                    onError(it.message ?: "")
                })
            }
        }
    }

    /**
     * 获取直播课token & id
     * liveToken liveKey
     */
    fun getMTCloudUrl(liveKey: String, onNext: (NetLesson) -> Unit) {
        api.getMTCloudUrl(liveKey).subs({ onNext(it.body) })
    }

    fun luban(file: List<File>, onNext: (File?) -> Unit) {
        Luban.with(BaseApp.get()).load(file).ignoreBy(300)
            .setTargetDir(BaseApp.get().externalCacheDir?.path)
            .setCompressListener(object : OnCompressListener {
//            override fun onError(e: Throwable?) {
//            }

                override fun onStart() {
                }

                override fun onSuccess(index: Int, file: File?) {
                    TODO("Not yet implemented")
                    onNext(file)
                }

                override fun onError(index: Int, e: Throwable?) {
                    TODO("Not yet implemented")
                }

//                override fun onSuccess(file: File?) {
//                onNext(file)
//            }
            }).launch()
    }

    /**
     * 获取富文本详情
     */
    fun informationDetail(key: String, onNext: (Map<String, String>) -> Unit) {
        api.informationDetail(key).sub({ onNext(it.body) })
    }

    /**
     * 获取富文本详情
     */
    fun getEvalByScan(key: String, onNext: (List<Eval>) -> Unit) {
        api.getEvalByScan(key).sub({ onNext(it.body) })
    }

    /**
     * 获取历史习题练习报告
     */
    fun getReport(
        paperkey: String, userpractisekey: String, catalogKey: String = "", onNext: (Report) -> Unit
    ) {
        api.exerciseReport(paperkey, userpractisekey, catalogKey).sub({ onNext(it.body) })
    }

    /**
     * 资讯点击量
     */
    fun informationPv(key: String) {
        api.informationPv(key).sub()
    }

    /**
     * 验证登录状态
     */
    fun checkLogin(onNext: (Boolean) -> Unit) {
        var token = SpUtil.user.token
        if (token == null || token == "") {
            onNext(false)
        } else {
            api.validToken(token).sub({
                if (it.body["isValid"] == "1") {
                    onNext(true)
                } else {
                    onNext(false)
                }
            })
        }

    }

    /**
     * 获取新版本
     */
    fun getNewVersion(flavor: String, onNext: (NewVersion) -> Unit) {
        api.getNewVersion(flavor, "", "").sub({ onNext(it.body) })
    }


    /**
     * 资讯点击量
     */
    fun userEquipmentId(id: String, onNext: (Map<String, String>) -> Unit) {
        api.userEquipmentId(id).sub({
            it.body?.let { map ->
                onNext(map)
            }
        })
    }

    /**
     * 判断网课是否有效
     * 用于离线中心判断
     * 失效则 删除对应目录
     */
    fun isValidOfNetcourse(key: String, onNext: (Boolean) -> Unit) {
        api.isValidOfNetcourse(key).sub({
            if (it.body["isValid"] == "0") {
                onNext(false)
            } else if (it.body["isValid"] == "1") {
                onNext(true)
            } else {
                onNext(false)
            }
        })
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
     * 获取资源下载 播放信息
     * isDownload“0”播放  “1”下载
     * source  1.1:网课短视频,1.2网课目录/小节,1.3网课预习，2:配套（网课/配套），3：试卷，4：单词 ,
     * 4.1:单词解析视频,5精听课程学习音频，5.1 精听网课资源音频  6:简听力
     */
    fun getResourceInfo(key: String, source: String, onNext: (ResourceInfo) -> Unit) {
        api.getResourceInfo(key, source).sub({
            onNext(it.body)
        })
    }

    /**
     * 获取资源下载 播放信息
     * isDownload“0”播放  “1”下载
     */
    fun getResourceInfo(
        key: String, source: String, isDownload: String, onNext: (ResourceInfo) -> Unit
    ) {
        api.getResourceInfo(key, source, isDownload).subs({
            onNext(it.body)
        })
    }

    /**
     *获取主观题的付费信息
     */
    fun getQuestionPayplan(questionKey: String, onNext: (QuestionPayplan) -> Unit) {
        api.getQuestionPayplan(questionKey).sub({ onNext(it.body) })
    }

    //配套目录key获取配套资源
    fun getResourcesByCatalog(catalogKey: String, gradeKey: String, onNext: (BookDetail) -> Unit) {
        api.getResourcesByCatalog(catalogKey, gradeKey).sub({ onNext(it.body) })
    }

    //直接下订单
    fun getDirectOrder(
        goodsKey: String, evalPlanType: String, evalKey: String, onNext: (OrderBean) -> Unit
    ) {
//        1图书，2网课，3课课练，4试题
        val map = mutableMapOf(
            "goodsKey" to goodsKey,
            "goodsType" to "4",
            "discountkey" to "",
            "remarks" to "",
            "addressKey" to "",
            "evalPlanType" to evalPlanType,
            "evalKey" to evalKey,
            "goodsNum" to "1"
        )
        api.getDirectOrder(map).sub({ onNext(it.body) })
    }

    //获取排行榜
    fun getAnswerRankingDetail(
        paperKey: String, userPractiseKey: String, evalkey: String, onNext: (RankBean) -> Unit
    ) {
        api.getAnswerRankingDetail(paperKey, userPractiseKey, evalkey).sub({ onNext(it.body) })
    }

    //获取推荐资讯列表
    fun evalInfomationList(
        clssifyKey: String, evalRecommendKey: String, onNext: (List<NewsBean>) -> Unit
    ) {
        api.evalInfomationList(clssifyKey, evalRecommendKey).sub({ onNext(it.body) })
    }

    /**
     * 试卷结构及试卷详情
     * 做题时调用
     */
    fun getExerciseFrame(
        practisekey: String, ishistory: String, evalKey: String, onNext: (ExerciseFrame) -> Unit
    ) {
        api.getExerciseFrame(practisekey, ishistory, evalKey).subs({ onNext(it.body) })
    }

    /*
    *keyword：搜索内容
    *sortingmode：1综合、2销量、3价格升序、4价格降序
    * gradeKey：年级
    * contentType：	内容类型
    * page：
    * step：
    * */
    fun getBookList(
        keyword: String,
        sortingmode: String,
        gradeKey: String,
        contentType: String,
        page: Int,
        step: Int,
        onNext: (List<BookList>) -> Unit
    ) {
//        cacheApi.getBookList(keyword, sortingmode, gradeKey, contentType, page, step)
//            .sub({ onNext(it.body) })
        api.getBookList(keyword, sortingmode, gradeKey, contentType, page, step)
            .sub({ onNext(it.body) })
    }

    fun getBookBanner(onNext: (List<Advert>) -> Unit) {
//        cacheApi.getBookBanner("tsjx", "1").sub({ onNext(it.body) })
        api.getBookBanner("tsjx", "1").sub({ onNext(it.body) })
    }

    fun getEBookBanner(onNext: (List<Advert>) -> Unit) {
        api.getEBookBanner("tspt_zns").sub({ onNext(it.body) })
    }

    /**
     *资讯推荐列表
     *gradeKey  年级key
     *spaceType	2首页资讯
     */
    fun getNewsList(gradeKey: String, onNext: (List<NewsBean>) -> Unit) {
        api.getNewsList(gradeKey, "2").sub({ onNext(it.body) })
    }

    fun getAssembleTeam(orderKey: String, onNext: (AssembleTeam) -> Unit) {
        api.getAssembleTeam(orderKey).sub({ onNext(it.body) })
    }

    //兑换优惠券
    fun exchangeCoupon(map: Map<String, String>, onNext: (Coupon) -> Unit) {
        api.exchangeCoupon(map).sub(onNext = { onNext(it.body) })
    }

    //是否有新优惠券提醒(get获取标识，del清空标识)
    fun getPromotionalFlag(onNext: (Map<String, String>) -> Unit) {
        api.getPromotionalFlag("get").sub(onNext = { onNext(it.body) })
    }

    //是否有新优惠券提醒(get获取标识，del清空标识)
    fun delPromotionalFlag(onNext: () -> Unit) {
        api.delPromotionalFlag("del").sub(onNext = { onNext() })
    }

    fun goodsInfoAfterPay(orderKey: String, onNext: (AssembleTeam) -> Unit) {
        api.goodsInfoAfterPay(orderKey).sub({ onNext(it.body) })
    }

    fun getHomePromotionals(key: String, onNext: (List<Coupon>) -> Unit) {
        api.getHomePromotionals(key).sub({ onNext(it.body) })
    }

    fun getNetVideoInfo(key: String, onNext: (VideoData) -> Unit, onError: () -> Unit) {
        api.getNetVideoInfo(key).sub({ onNext(it.body) }) {
            onError()
        }
    }

    fun freeLogin(map: Map<String, String>, onNext: (Int) -> Unit) {
        api.freePasswLogin(map).sub({ results ->
            val userInfo = results.body.userInfo
            userInfo?.let {
                SpUtil.user = userInfo
                try {
                    if ("1" == it.type) {
                        val property = JSONObject()
                        property.put("source_registration", "一键登录注册")
                        SensorsDataAPI.sharedInstance().track("registe_front_application", property)
                    }
                    SensorsDataAPI.sharedInstance().login(userInfo.userId ?: "");
                } catch (e: Exception) {
                }
            }
            userInfo {
                SpUtil.userInfo = it
                SpUtil.isLogin = true
                try {
                    val properties = JSONObject()
                    properties.put("phone_number", it.phone)
                    properties.put("user_nickname", it.name)
                    properties.put("xinghuo_userid", userInfo?.userId ?: "")
                    properties.put(
                        "last_login_time",
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE).format(
                            Date()
                        )
                    )
                    SensorsDataAPI.sharedInstance().profileSet(properties)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (SpUtil.userInfo.grade.isNullOrBlank()) changeUserInfo(mutableMapOf("grade" to SpUtil.defaultGrade.id)) {
                    onNext(results.ret)
                }
                else onNext(results.ret)
            }
        }) {}
    }

    fun getQrCodeRecord(page: Int, onNext: (List<Scan>) -> Unit, onError: () -> Unit) {
        api.getQrCodeRecord(page, 20).sub({ onNext(it.body) }) {
            onError()
        }
    }

    fun clearQrcodeRecord(onNext: () -> Unit) {
        api.clearQrcodeRecord().sub({ onNext() })
    }

    /**
     *统计时长
     */
    fun postRecordDduration(map: Map<String, String>, onNext: () -> Unit) {
        api.postRecordDduration(map).sub({ onNext() })
    }

    fun lrc(url: String, onNext: (String?) -> Unit) {
        api.downloadLrc(url).subNotMap(onNext = {
            val source = it.source()
            try {
                onNext(source.readString(Charset.forName("GB18030")))
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
//                Util.closeQuietly(source)
            }
        }) {}
    }

    fun getLrc(url: String, onNext: (String?) -> Unit, onError: () -> Unit) {
        api.downloadLrc(url).subNotMap(onNext = {
            val source = it.source()
            try {
                onNext(source.readString(Charset.forName("GB18030")))
            } catch (e: Exception) {
                e.printStackTrace()
                onError()
            } finally {
//                Util.closeQuietly(source)
            }
        }) {
            onError()
        }
    }

    /**
     * 获取字幕列表文件
     */
    fun getLrcDetail(lrcKey: String, onNext: (LrcData) -> Unit) {
        api.getLrcDetail(lrcKey).sub({ onNext(it.body) })
    }

    fun recordLrc(map: Map<String, String>, onNext: () -> Unit) {
        api.recordLrc(map).sub({ onNext() })
    }

    //获得带货直播入口是否展示
    fun getLiveFlag(positionKey: String, onNext: (LiveFlag) -> Unit) {
        api.getLiveFlag(positionKey).sub({ onNext(it.body) })
    }

    //精选、四六级、考研 专四专八、 书城
    fun getDialog(gradeKey: String, onNext: (List<Advert>) -> Unit) {
        api.getDialog("pop", gradeKey).sub({ onNext(it.body) })
    }

    //精选、四六级、考研 专四专八、 书城
    fun tastIntegral(type: String, text: String, onNext: (Map<String, String>) -> Unit) {
        api.tastIntegral("pop", text).sub({ onNext(it.body) })
    }
}