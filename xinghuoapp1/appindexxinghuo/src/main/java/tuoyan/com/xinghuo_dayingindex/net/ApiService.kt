package tuoyan.com.xinghuo_dayingindex.net

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*
import tuoyan.com.xinghuo_dayingindex.bean.*


interface ApiService {
    /*###################################基础模块#############################################*/
    /**
     * 获取新版本
     */
    @GET("common/isForceUp")
    fun getNewVersion(
        @Query("patchSource") patchSource: String, @Query("appVersion") appVersion: String, @Query("patchVersion") patchVersion: String
    ): Single<DataBase<NewVersion>>


    /**
     * 获取图片验证码
     */
//    @GET("user/randomCode")
//    fun randomCode(@Query("phone") phone: String): Single<DataBase<Map<String, String>>>

    @GET("common/getTenectJS")
    fun getTenectJS(@Query("phone") phone: String): Single<DataBase<Map<String, String>>>

    /**
     * 获取当前验证类型
     * 0数字验证，1阿里滑动验证
     */
    @GET("common/getCheckType")
    fun getCheckType(@Query("phone") phone: String): Single<DataBase<Map<String, String>>>

    @POST("common/getAliChecked")
    fun getAliChecked(@Body body: Map<String, String>): Single<DataBase<Map<String, String>>>

    /**
     * 获取富文本详情
     */
    @GET("information/detail")
    fun informationDetail(@Query("key") key: String): Single<DataBase<Map<String, String>>>

    /**
     * 验证token是否过期
     */
    @GET("common/validToken")
    fun validToken(@Query("token") token: String): Single<DataBase<Map<String, String>>>

    /**
     * 校验图片验证码
     */
    @GET("common/getTenectChecked")
    fun validateCode(
        @Query("phone") phone: String, @Query("ticket") ticket: String
    ): Single<DataBase<Any>>

    /**
     * 校验图片验证码
     */
    @GET("user/validateCode")
    fun validateCode(
        @Query("phone") phone: String, @Query("code") code: String, @Query("type") type: String
    ): Single<DataBase<Any>>

    /**
     * 提交注册的极光设备id
     */
    @GET("user/userEquipmentId")
    fun userEquipmentId(@Query("equipmentId") equipmentId: String): Single<DataBase<Map<String, String>>>

    /**
     * 发送短信
     */
    @POST("user/sendIdentifyingCode")
    fun getSms(@Body resultSms: SMSCode): Single<DataBase<Any>>

    /**
     * 获取学段列表
     */
//    @GET("common/getSectionList")
//    fun getSectionList(): Single<DataBase<List<Section>>>

    /**
     * 获取年级列表
     */
//    @GET("common/getGradetList")
//    fun getGradeList(@Query("key") key: String): Single<DataBase<List<Grade>>>

    /**
     * 获取学段与年级列表
     */
    @GET("common/getSectionAndGradeList")
    fun getSectionAndGradeList(): Single<DataBase<Lt<List<Section>>>>

//    /**
//     * 获取版本号
//     */
//    @GET("fragment/version")
//    fun getVersion(): Single<Version>
//
//    /**
//     * 启动
//     */
//    @GET("common/getSectionAndGradeList")
//    fun getSectionAndGradeList(): Single<DataBase<Lt<List<Section>>>>
//
////    /**
////     * 获取版本号
////     */
////    @GET("fragment/version")
////    fun getVersion(): Single<Version>
////
////    /**
////     * 启动
////     */
////    @GET("fragment/start")
////    fun start(): Single<AppStart>
//
    /**
     * 统计页面点击量
     */
//    @POST("common/recordModelPv")
//    fun postPV(@Body body: Map<String, String>): Single<DataBase<Any>>

    /**
     * 统计广告点击量
     */
    @GET("admodel/advertisingPv")
    fun advertisingPv(@Query("key") key: String): Single<DataBase<Any>>


//    @GET("fragment/start")
//    fun start(): Single<AppStart>

//    /**
//     * 统计页面点击量
//     */
//    @POST("common/recordModelPv")
//    fun postPV(@Body body: Map<String, String>): Single<DataBase<Any>>
//
    /**
     * 首次安装调用
     */
    @POST("common/recordInstalls")
    fun postFirstInstall(@Body body: Map<String, String>): Single<DataBase<Any>>

    /**
     * 获取资源的播放地址 和 下载地址 等信息
     * isDownload“0”播放  “1”下载
     */
    @GET("common/getResourceInfo")
    fun getResourceInfo(
        @Query("key") key: String, @Query("source") source: String
    ): Single<DataBase<ResourceInfo>>

    /**
     * 获取资源的播放地址 和 下载地址 等信息
     * isDownload“0”播放  “1”下载
     */
    @GET("common/getResourceInfo")
    fun getResourceInfo(
        @Query("key") key: String, @Query("source") source: String, @Query("isDownload") isDownload: String
    ): Single<DataBase<ResourceInfo>>

//    /*###################################用户模块########################################*/
    /**
     * 登录
     */
    @POST("user/login")
    fun login(@Body request: Map<String, String>): Single<DataBase<LoginResponse>>

    /**
     * 退出登录
     */
    @PUT("user/logOff")
    fun logout(): Single<DataBase<Any>>

//    /**
//     * 用户是否存在
//     */
//    @GET("users")
//    fun isUserExist(@Query("phone") phone: String): Single<ResponseBody>
//
    /**
     * 注册
     */
    @POST("user/regist")
    fun register(@Body registered: Map<String, String>): Single<DataBase<LoginResponse>>

    /**
     * 忘记密码
     */
    @PUT("user/findPwd")
    fun findPwd(@Body resetPwd: Register): Single<DataBase<Any>>

//    /**
//     * 修改密码
//     */
//    @PUT("users/passwordModify")
//    fun modifyPassword(@Body modify: ModifyPassword): Single<ResponseBody>
//
    /**
     * bind手机号
     */
    @POST("user/checkThird")
    fun bindPhone(@Body modify: Map<String, String>): Single<DataBase<LoginResponse>>

    /**
     *修改手机号
     */
    @POST("user/changePhone")
    fun changePhone(@Body modify: Map<String, String>): Single<DataBase<LoginResponse>>

    /**
     * 个人详情
     */
    @GET("user/profile")
    fun getUserInfo(): Single<DataBase<UserInfo>>

    /**
     * 修改资料
     */
    @PUT("user/modifyProfile")
    fun modifyInfo(@Body userInfo: Map<String, String>): Single<DataBase<UserInfo>>

    /**
     * 修改年级学段
     */
//    @GET("user/updUserSG")
//    fun updUserSG(
//        @Query("sectionkey") sectionkey: String,
//        @Query("gradekey") gradekey: String
//    )
//            : Single<DataBase<Any>>

    /**
     * 修改年级学段
     */
    @GET("user/userEvalList")
    fun userEvalList(
        @Query("page") page: Int, @Query("step") step: Int = 20
    ): Single<DataBase<List<Eval>>>

    /**
     * 添加收藏
     * type	1:问答 ，2帖子，3资讯，4试卷，5音频	是
     * targetkey
     */
    @POST("user/addCollection")
    fun addCollection(@Body map: Map<String, String>): Single<DataBase<Any>>

    /**
     * 我收藏的试卷
     */
//    @HTTP(method = "DELETE", path = "collections", hasBody = true)
    @GET("user/collectedQuestionList")
    fun collectionPaper(
        @Query("page") page: Int, @Query("step") step: Int = 20
    ): Single<DataBase<List<Paper>>>

    /**
     * 我收藏的试卷详情
     */
    @GET("practise/questionAnalyze")
    fun questionAnalyze(
        @Query("questionkey") questionkey: String, @Query("questiontype") questiontype: String, @Query("userPracticeKey") userPracticeKey: String, @Query("paperKey") paperKey: String
    ): Single<DataBase<Any>>

    /**
     * 我收藏的音频
     */
//    @HTTP(method = "DELETE", path = "collections", hasBody = true)
    @GET("user/collectedAudioList")
    fun collectionAvdio(
        @Query("page") page: Int, @Query("step") step: Int = 20
    ): Single<DataBase<List<Audio>>>


    /**
     * 移除收藏
     */
    @POST("user/delCollection")
    fun deleteCollection(@Body map: Map<String, String>): Single<DataBase<Any>>

//    /**
//     * 关注人列表
//     */
//    @GET("user/attentionList")
//    fun getAttentionList(): Single<DataBase<List<Attention>>>
//
//    /**
//     * 关注圈子
//     */
//    @GET("community/userAttentionList")
//    fun getCircleList(): Single<DataBase<List<Circle>>>
//
//    /**
//     * 添加关注圈子
//     */
//    @POST("community/addUserAttention")
//    fun addCircle(@Body communitykey: Map<String, String>): Single<DataBase<List<Circle>>>
//
//    /**
//     * 取消关注圈子
//     */
//    @POST("community/delUserAttention")
//    fun delCircle(@Body communitykey: Map<String, String>): Single<DataBase<List<Circle>>>
//
//    /**
//     *关注人数量
//     */
//    @GET("user/attentionCount")
//    fun getAttentionCount(): Single<DataBase<String>>
//
//    /**
//     * 粉丝数量
//     */
//    @POST("user/fansCount")
//    fun getFansCount(): Single<DataBase<String>>
//
//    /**
//     * 粉丝列表
//     */
//    @GET("user/fansList")
//    fun getFansList(): Single<DataBase<List<Attention>>>
//
    /**
     * 个人主页信息
     */
    @GET("user/homePageProfile")
    fun getHomePageProfile(): Single<DataBase<HomePageInfo>>


    /**
     * 加入我的学习
     */
    @GET("user/paperHistoryTimes")
    fun paperHistoryTimes(): Single<DataBase<List<WrongBookDate>>>

    /**
     * 联系记录
     */
    @GET("user/historyPractice")
    fun historyPractice(
        @Query("page") page: Int, @Query("year") year: String, @Query("step") step: Int = 20
    ): Single<DataBase<List<ExerciseHistory>>>

    /**
     * 用户收货地址列表
     */
    @GET("user/getUserAddress")
    fun addresses(
        @Query("page") page: Int = 0, @Query("step") step: Int = 99
    ): Single<DataBase<List<Address>>>

    /**
     * 添加用户收货地址-已完成
     */
    @POST("user/addUserAddress")
    fun addAddress(@Body body: Map<String, String?>): Single<DataBase<Any>>

    /**
     * 修改收获地址-已完成-已完成
     */
    @POST("user/editUserAddress")
    fun editAddress(@Body body: Map<String, String?>): Single<DataBase<Any>>

    /**
     * 修改收获地址-已完成-已完成
     */
    @DELETE("user/deleteAddress/{key}")
    fun deleteAddress(@Path("key") key: String): Single<DataBase<Any>>


//
//    /*###################################订单模块########################################*/
//
//
//    /**
//     * 提交订单
//     */
//    @POST("netcourse/submitOrder")
//    fun submitOrder(@Body order: SubmitOrder): Single<DataBase<Map<String, String>>>
//
//    /**
//     * 订单列表
//     */
//    @GET("order/list")
//    fun getOrderList(@Query("type") type: Int? = null,@Query("step") step: Int=99): Single<DataBase<List<Order>>>
//
    /**
     * 订单详情
     */
    @GET("order/detail")
    fun orderDetail(@Query("key") key: String): Single<DataBase<OrderDetail>>
//
//    /**
//     * 删除订单
//     */
//    @POST("order/delete")
//    fun deleteOrder(@Body key: Map<String, String>): Single<DataBase<Any>>
//
//    /**
//     * 取消订单
//     */
//    @POST("order/cancel")
//    fun cancelOrder(@Body key: Map<String, String>): Single<DataBase<Any>>

    /**
     * 支付接口
     */
    @POST("pay/submit")
    fun payOrder(@Body body: Map<String, String>): Single<DataBase<Map<String, String>>>

    /*###################################网课模块########################################*/


    @PUT("netcourse/activedNetcourse/{code}")
    fun activedNetcourse(@Path("code") code: String): Single<DataBase<Any>>

    @POST("netcourse/activedNetcourse")
    fun postActivedNetcourse(@Body key: Map<String, String>): Single<DataBase<Any>>

    @GET("netcourse/search")
    fun getLessonList(
        @Query("type") type: String, @Query("grade") grade: String, @Query("page") page: Int, @Query("step") step: Int
    ): Single<DataBase<List<Lesson>>>

    @GET("netcourse/detail")
    fun getLessonDetail(@Query("key") key: String): Single<DataBase<LessonDetail>>

    @GET("netcourse/spokenDetail")
    fun getSpokenDetail(@Query("key") key: String): Single<DataBase<Spoken>>

    @GET("packs/detail")
    fun getPacksDetail(@Query("key") key: String): Single<DataBase<Any>>

    /**
     * 网课小节观看记录
     */
    @POST("netcourse/recordPlayLog")
    fun recordPlayLog(@Body key: Map<String, String>): Single<DataBase<Any>>

    /**
     * 测评试卷的测评报告
     */
    @GET("paper/evalReport")
    fun evalReport(
        @Query("evalkey") evalkey: String, @Query("paperkey") paperkey: String, @Query("userpractisekey") userpractisekey: String
    ): Single<DataBase<EvalReport>>

    /**
     * 网课详情目录
     */
    @GET("netcourse/catalogue")
    fun getCatalogue(@Query("key") key: String): Single<DataBase<Catalogue>>

    /**
     *  获取今日课程 --- 2019/4/24 大英网课UI修改
     */
    @GET("netcourse/curClass")
    fun getTodayLesson(): Single<DataBase<List<TodyLesson>>>

    /**
     *  获取直播课/录播课列表 --- 2019/4/24 大英网课UI修改
     */
    @GET("netcourse/liveCatalogue")
    fun getLiveCatalogue(
        @Query("courseKey") key: String, @Query("form") form: String
    ): Single<DataBase<LiveListBean>>

    /**
     * 获取回放数据列表 --- 2020年7月27日
     * 参数：网课courseKey   type（0按照时间排序展示，1按照目录展示）
     */
    @GET("netcourse/getPlaybackList")
    fun getPlaybackListByPackage(
        @Query("courseKey") key: String, @Query("type") form: String
    ): Single<DataBase<PlayBackBean>>

    /**
     * 获取回放数据列表 --- 2020年7月27日
     * 参数：网课courseKey   type（0按照时间排序展示，1按照目录展示）
     */
    @GET("netcourse/getPlaybackList")
    fun getPlaybackListByTime(
        @Query("courseKey") key: String, @Query("type") form: String
    ): Single<DataBase<PlayBackBean>>

    /**
     * 获取直播课token id
     */
    @GET("common/getMTCloudUrl")
    fun getMTCloudUrl(@Query("videoKey") liveKey: String): Single<DataBase<NetLesson>>
//
    /**
     * 领取网课
     */
    @POST("netcourse/freeBuyNetCourse")
    fun freeBuyNetCourse(@Body key: Map<String, String>): Single<DataBase<AssembleTeam>>

    /**
     * 获取小节下的作业列表
     * 2019年8月28日接口homeWorkList更改为homeWorkCatalogList
     */
    @GET("netcourse/homeWorkCatalogList")
    fun getWorks(
        @Query("coursekey") coursekey: String, @Query("vidokey") vidokey: String, @Query("page") page: Int, @Query("step") step: Int
    ): Single<DataBase<List<ClassWork>>>

    /**
     * 判断网课是否有效
     * 用于离线中心判断
     * 失效则 删除对应目录
     */
    @GET("netcourse/isValidOfNetcourse")
    fun isValidOfNetcourse(@Query("courseKey") key: String): Single<DataBase<Map<String, String>>>

//
//    /*###################################评论模块########################################*/
//
    /**
     * 评论列表
     */
    @GET("comment/list")
    fun getComments(
        @Query("targetkey") targetkey: String, @Query("type") type: String, @Query("page") page: Int, @Query("step") step: Int = 10
    ): Single<DataBase<List<Comment>>>

    /**
     * 意见反馈
     */
    @POST("feedback/add")
    fun feedback(@Body feedback: Feedback): Single<DataBase<Any>>

    /**
     * 获取反馈类型；查询考研学校信息type=（学校= SCHOOL、 专业=MAJOR）
     */
    @GET("common/getDictInfo")
    fun getDictInfo(@Query("type") type: String): Single<DataBase<List<FeedbackQuestion>>>

    /**
     * 评论
     */
    @POST("comment/add")
    fun addComment(@Body map: Map<String, String>): Single<DataBase<Any>>
//
    /*###################################消息模块########################################*/
    /**
     * 是否有新回复
     */
    @GET("notice/msgReaded")
    fun ifNewFB(@Query("flag") flag: String): Single<DataBase<Map<String, Int>>>

//    /**
//     * 是否有新通知
//     */
//    @GET("notice/ifNewNotice")
//    fun ifNewNotice(): Single<DataBase<Map<String, String>>>
//
    /**
     * 通知列表
     */
    @GET("notice/messageList")
    fun notices(
        @Query("page") page: Int = 0, @Query("step") step: Int = 20
    ): Single<DataBase<MMMM>>

    /**
     * 删除消息
     */
    @DELETE("notice/deleteMessage/{key}/{userKey}")
    fun deletedMessage(
        @Path("key") key: String, @Path("userKey") userKey: String
    ): Single<DataBase<Any>>


    /*####################################优惠券########################################*/


    /**
     * 可用优惠券列表
     */
//    @GET("netcourse/getCoupon")
//    fun coupons(
//        @Query("goodkey") goodkey: String,
//        @Query("step") step: Int = 99
//    ): Single<DataBase<List<Coupon>>>

    /**
     * 兑换优惠券
     */
    @POST("user/exchangeCoupon")
    fun exchangeCoupon(@Body code: Map<String, String>): Single<DataBase<Coupon>>

    /**
     * 优惠券列表
     */
    @GET("user/couponList")
    fun getCouponList(
        @Query("status") status: Int, @Query("page") page: Int = 0, @Query("step") step: Int = 99
    ): Single<DataBase<List<Coupon>>>

    /**
     * 优惠券详情
     */
//    @GET("user/couponList")
//    fun getCouponDetail(@Query("key") key: String): Single<DataBase<Coupon>>
    /*###################################首页########################################*/

    /**
     * 首页数据刷新
     * banner  公告  推荐课程  精选图书  学习咨询
     * 没有返回网课专题，新版换成homePageInfoN
     */
//    @GET("common/homePageInfo")
//    fun homePageInfo(@Query("gradeKey") gradeKey: String): Single<DataBase<HomeData>>

    /**
     * 首页数据刷新
     * banner  公告  推荐课程  精选图书  学习咨询
     * 增加必填参数 flag=1,区分网课专题flag=2 展示小程序
     */
    @GET("common/homePageInfoN")
    fun homePageInfoN(
        @Query("gradeKey") gradeKey: String, @Query("flag") flag: String
    ): Single<DataBase<HomeData>>


    /**
     * 获取推荐网课列表
     * spaceType推荐位：1首页网课 3学习
     *terminalType终端类型:1移动端、3PC端
     * gradeKey年级key
     */
    @GET("netcourse/recommendListN")
    fun getNetClassList(
        @Query("spaceType") spaceType: String, @Query("terminalType") terminalType: String, @Query("gradeKey") gradeKey: String, @Query("flag") flag: String
    ): Single<DataBase<List<Lesson>>>

    @GET("admodel/list")
    fun getAdvs(
        @Query("adspace") adspace: String, @Query("page") page: Int, @Query("step") step: Int, @Query("flag") flag: String
    ): Single<DataBase<List<Advert>>>

    // 书城banner
    @GET("admodel/list")
    fun getDialog(@Query("adspace") adspace: String, @Query("gradeKey") flag: String): Single<DataBase<List<Advert>>>

    // 书城banner
    @GET("admodel/list")
    fun getBookBanner(@Query("adspace") adspace: String, @Query("flag") flag: String): Single<DataBase<List<Advert>>>

    // 书城banner
    @GET("admodel/list")
    fun getEBookBanner(@Query("adspace") adspace: String): Single<DataBase<List<Advert>>>
    //
//    @GET("admodel/advertisingPv")
//    fun addAdvsPv(@Query("key") key: String): Single<DataBase<Any>>
//
//    @GET("common/getSubjectList")
//    fun getSubjects(@Query("key") key: String): Single<DataBase<SubjectBody>>
//

    /*
    * 资讯推荐列表
    * gradeKey	年级key
    * spaceType	2首页资讯
    * */
    @GET("information/recommendList")
    fun getNewsList(
        @Query("gradeKey") gradeKey: String, @Query("spaceType") spaceType: String
    ): Single<DataBase<List<NewsBean>>>

    //
//    @GET("announcement/list")
//    fun getAnnouncement(): Single<DataBase<List<Announcement>>>
//
//    @GET("announcement/detail")
//    fun getAnnouncementDetail(@Query("key") key: String): Single<DataBase<AnnouncementDetail>>
//
    @POST("support/getKeyByQrCode")
    fun getDataByScan(@Body data: Map<String, String>): Single<DataBase<ScanResult>>

    @GET("support/resourceList")
    fun getResList(@Query("key") key: String): Single<DataBase<ScanRes>>

    /**
     * 扫描图书防伪码
     */
    @POST("support/activatedFakeCode")
    fun activatedFakeCode(@Body data: Map<String, String>): Single<DataBase<Any>>

    //
//    @GET("support/getKeyByInsideCode")
//    fun getDataByCode(@Query("code") code: String): Single<DataBase<ScanResult>>
//
    @GET("information/informationPv")
    fun informationPv(@Query("key") key: String): Single<DataBase<Any>>

    //    /*###################################随堂练习########################################*/
//
    @GET("practise/paperHistory")
    fun getExHistory(
        @Query("page") page: Int, @Query("step") step: Int
    ): Single<DataBase<List<ExerciseHistory>>>

    @POST("practise/clearPaperHistory")
    fun clearExHistory(): Single<DataBase<Any>>
//    @GET("practise/paperHistory")
//    fun getExHistory(@Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<ExerciseHistory>>>
//
//    @POST("practise/clearPaperHistory")
//    fun clearExHistory(): Single<DataBase<Any>>

    //    @GET("common/getEditionList")
//    fun getVersions(@Query("subjectkey") subjectkey: String): Single<DataBase<List<Version>>>
//
//    @GET("practise/bookList")
//    fun getBooks(@Query("editionkey") editionkey: String,
//                 @Query("page") page: Int,
//                 @Query("step") step: Int): Single<DataBase<List<ExBooks>>>
//
//    @GET("practise/catalogueList")
//    fun getExerciseList(@Query("bookkey") bookkey: String): Single<DataBase<ExerciseBody>>
//
//    @GET("practise/paperQuestions")
//    fun getExerciseFrame(@Query("practisekey") practisekey: String, @Query("ishistory")
//    /** 0 未做 1 未做 **/
//    ishistory: String): Single<DataBase<ExerciseFrame>>
//
//    @GET("practise/paperAnalyzes")
//    fun getExerciseParsingFrame(@Query("practisekey") practisekey: String,@Query("userpractisekey") userpractisekey: String,
//                                @Query("ishistory") /** 0 未做 1 未做 **/ ishistory: String): Single<DataBase<ExerciseFrame>>
//
//    @GET("practise/newQuestion")
//    fun getExerciseDetail(@Query("practisekey") practisekey: String, @Query("groupkey") groupkey: String,
//                          @Query("questiontype") questiontype: String, @Query("questionkey") questionkey: String,
//                          @Query("questionsort") questionsort: String): Single<DataBase<Any>>
//
//    @GET("practise/analyze")
//    fun getExerciseParsing(@Query("practisekey") practisekey: String, @Query("questionkey") questionkey: String,
//                           @Query("questiontype") questiontype: String, @Query("userpractisekey") userpractisekey: String,
//                           @Query("questionsort") questionsort: String): Single<DataBase<Any>>
//
//    @POST("practise/submit")
//    fun exerciseSubmit(@Body answer: AnswerSubmit): Single<DataBase<Report>>
//
//    @POST("mission/submit")
//    fun exerciseSubmitDF(@Body answer: AnswerSubmit): Single<DataBase<Report>>
//
    @GET("practise/installAnswercark")
    fun exerciseReport(
        @Query("paperkey") paperkey: String, @Query("userpracticekey") userpractisekey: String, @Query("catalogKey") catalogKey: String
    ): Single<DataBase<Report>>
//
//    @POST("practise/questionFB")
//    fun questionFB(@Body map: Map<String, String>): Single<DataBase<Any>>
//
//    /*###################################错题本########################################*/

    @GET("practise/wrongBookList")
    fun getWrongList(
        @Query("year") year: String, @Query("gradekey") gradekey: String, @Query("subjectkey") subjectkey: String, @Query("page") page: Int, @Query("step") step: Int
    ): Single<DataBase<List<WrongBook>>>

    @POST("practise/wrongBookClear")
    fun deleteWrongItem(@Body map: Map<String, String>): Single<DataBase<Any>>

    @GET("practise/wrongBookTimes")
    fun getWrongDate(
        @Query("page") page: Int, @Query("step") step: Int
    ): Single<DataBase<List<WrongBookDate>>>

    @GET("common/getErrorSubjectList")
    fun getWrongSubjects(@Query("key") key: String): Single<DataBase<List<Subject>>>

    /*###################################学习模块########################################*/

    @GET("support/supportBookList")
    fun getBooks(@Query("grade") grade: String): Single<DataBase<List<Book>>>

//    @GET("support/myBookList")
//    fun getMyBooks(@Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<MyBookNetClass>>>
//
//    @POST("support/deleteBook")
//    fun deleteMyBooks(@Body map: Map<String, String>): Single<DataBase<Any>>

    @GET("support/bookDetailByKey")
    fun getBookDetail(
        @Query("key") key: String, @Query("type") type: String
        /**1 扫描、2 书架**/
    ): Single<DataBase<BookDetail>>


    @GET("support/myBookList")
    fun getMyBooks(/*@Query("page") page: Int, @Query("step") step: Int = 99*/): Single<DataBase<List<Book>>>

    //
    @POST("support/deleteBook")
    fun deleteMyBooks(@Body map: Map<String, String>): Single<DataBase<Any>>

    @POST("netcourse/deleteNetCourse")
    fun deleteMyNetClass(@Body map: Map<String, String>): Single<DataBase<Any>>

    //
    @GET("netcourse/myNetCourse")
    fun getMyLessons(/*@Query("page") page: Int, @Query("step") step: Int = 99*/): Single<DataBase<List<MyBookLesson>>>

    //
//    @GET("support/bookDetailByKey")
//    fun getMyBookDetail(@Query("key") key: String, @Query("type") type: String): Single<DataBase<MyBookDetail>>
//
//    @GET("user/signInDetails")
//    fun getSignInfo(): Single<DataBase<SignInfo>>
//
//    @POST("user/signIn")
//    fun sign(): Single<DataBase<Any>>
//
//    @GET("user/signInRankingList")
//    fun getSignList(@Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<SignListItem>>>
//
    @POST("support/addMyStudy")
    fun addMyStudy(@Body data: Map<String, String>): Single<DataBase<Any>>

    @GET("netcourse/netOrderDetail")
    fun getMyLessonDetail(@Query("key") key: String): Single<DataBase<LessonDetail>>

//
//    /*###################################搜索模块########################################*/
//
//    @GET("netcourse/search")
//    fun search(@Query("keyword") keyword: String, @Query("sortingmode") sortingmode: Int
//            /** 0:默认；1:综合 **/
//               ,
//               @Query("grade") grade: String, @Query("subject") subject: String,
//               @Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<NetLessonItem>>>
//    /*###################################闯关联系########################################*/
//    /**
//     *任务列表
//     */
//    @GET("mission/list")
//    fun cgPractice(
//            @Query("grade") grade: String,
//            @Query("page") page: Int = 0,
//            @Query("section") section: String? = null,
//            @Query("subject") subject: String? = null,
//            @Query("step") step: Int = 10): Single<DataBase<List<Map<String, String>>>>
//
//    /**
//     *关卡列表
//     */
//    @GET("mission/levelList")
//    fun cgPass(@Query("key") key: String,@Query("step") step: Int=99): Single<DataBase<LL<List<Map<String, String>>>>>
//
//    /**
//     *试卷列表
//     */
//    @GET("mission/paperList")
//    fun cgPaper(@Query("key") key: String,@Query("step") step: Int=99): Single<DataBase<PL<List<Map<String, String>>>>>
//
//
//    /**
//     * 闯关成功弹窗
//     */
//    @POST("mission/wordShowed")
//    fun wordShowed(@Body data: Map<String, String>): Single<DataBase<Any>>
//
//    /*###################################图片上传########################################*/

    @Multipart
    @POST("image/upload/{model}")
    fun uploadImage(
        @Path("model") model: String, @Part imgs: List<MultipartBody.Part>
    ): Single<DataBase<Map<String, Any>>>

    @Multipart
    @POST("image/topimg")
    fun uploadTopImage(@Part img: MultipartBody.Part): Single<DataBase<Map<String, String>>>

    /*###################################背单词########################################*/
    /**
     *背单词首页
     * 532648787350438272
     * 增加 fType 词汇类型：常用词汇-0  核心词汇-1  高频词汇-2 新扫码单词-3
     */
    @GET("word/wordHome")
    fun wordHome(@Query("gradekey") key: String, @Query("fType") fType: String): Single<DataBase<WordHome>>

    /**
     *词库分类下的一级目录
     *
     */
    @GET("word/catalogList")
    fun catalogList(
        @Query("classifyKey") key: String, @Query("isPersonal") isPersonal: Int = 1
    ): Single<DataBase<List<ClassifyList>>>

    /**
     *根据目录id获取单词
     *
     */
    @GET("word/getWordsByCatalogkey")
    fun getWordsByCatalogkey(
        @Query("catalogKey") key: String, @Query("page") page: Int = 0, @Query("step") step: Int = 99, @Query("isLinkQuestion") isLinkQuestion: Int = 1
    ): Single<DataBase<LearnStatus>>

    @GET("word/getWordsByCatalogkey")
    fun getWordsByCatalogkey(
        @Query("catalogKey") key: String, @Query("type") type: String
    ): Single<DataBase<ScanWord>>

    /**
     *背词复习：获取词库对应的试题信息
     *
     */
//    @GET("word/getWordQuestions")
//    fun getWordQuestions(
//        @Query("catalogKey") key: String,
//        @Query("page") page: Int = 0,
//        @Query("step") step: Int = 20
//    )
//            : Single<DataBase<List<WordsByCatalogkey>>>

    /**
     *错题本列表
     *
     */
    @GET("word/getWrongWord")
    fun getWrongWord(
        @Query("gradeKey") key: String, @Query("page") page: Int = 0, @Query("step") step: Int = 20
    ): Single<DataBase<List<WordsByCatalogkey>>>

    /**
     *记录错词
     *@Query("key") key: String,
     * @Query("sectionKey") sectionKey: String = "",
     * @Query("gradeKey") gradeKey: String = ""
     */
    @POST("word/recordWrongWord")
    fun recordWrongWord(@Body map: Map<String, String>): Single<DataBase<Any>>

    /**
     * 移除错词
     */
    @DELETE("word/deleteWrongWord/{key}")
    fun deleteWrongWord(@Path("key") key: String): Single<DataBase<Any>>

    /**
     *生词本列表
     *
     */
    @GET("word/getNewWord")
    fun getNewWord(
        @Query("gradeKey") key: String, @Query("page") page: Int = 0, @Query("fType") fType: String, @Query("step") step: Int = 20
    ): Single<DataBase<List<WordsByCatalogkey>>>

    /**
     *记录生词
     *@Query("key") key: String,
     * @Query("sectionKey") sectionKey: String = "",
     * @Query("gradeKey") gradeKey: String = ""
     */
    @POST("word/recordNewWord")
    fun recordNewWord(@Body map: Map<String, String>): Single<DataBase<Any>>

    /**
     * 移除错词
     */
    @DELETE("word/deleteNewWord/{key}")
    fun deleteNewWord(@Path("key") key: String): Single<DataBase<Any>>

    /**
     *单词详情
     *@Query("key") key: String,
     * @Query("sectionKey") sectionKey: String = "",
     * @Query("gradeKey") gradeKey: String = ""
     */
    @GET("word/getWordDetail")
    fun getWordDetail(@Query("key") key: String): Single<DataBase<WordDetail>>

    /**
     *单词学习提交
     *@Query("	catalogKey") key: String,
     * @Query("totalNum	") sectionKey: String = "",
     */
    @POST("word/learnSubmit")
    fun learnSubmit(@Body map: Map<String, String>): Single<DataBase<Map<String, String>>>

    /**
     *记录生词
     *@Query("catalogKey") key: String,
     * @Query("totalNum") sectionKey: String = "",
     * @Query("rightNum") gradeKey: String = ""
     * @Query("wrongNum") gradeKey: String = ""
     */
    @POST("word/reviewSubmit")
    fun reviewSubmit(@Body map: Map<String, String>): Single<DataBase<Map<String, String>>>

    /*###################################字幕下载########################################*/
    @GET
    fun downloadLrc(@Url lrcUrl: String): Single<ResponseBody>

    /*###################################提分练习########################################*/
    @GET("paper/specialPractise")
    fun getSpecialInfo(
        @Query("gradeKey") gradeKey: String, @Query("paperType") paperType: String
    ): Single<DataBase<SpecialInfos>>

    @GET("paper/list")
    fun getRealList(
        @Query("gradeKey") gradeKey: String, @Query("paperType") paperType: String
    ): Single<DataBase<List<RealListItem>>>

    /*###################################做题模块########################################*/
    /**
     * practisekey:试卷key
     *
     * evalKey：测评的key
     */
    @GET("practise/paperQuestions")
    fun getExerciseFrame(
        @Query("practisekey") practisekey: String, @Query("ishistory")
        /** 0 未做 1 未做 **/
        ishistory: String, @Query("evalKey") evalKey: String
    ): Single<DataBase<ExerciseFrame>>

    @GET("practise/paperQuestions")
    fun getExerciseFrame2(
        @Query("practisekey") practisekey: String, @Query("ishistory")
        /** 0 未做 1 未做 **/
        ishistory: String
    ): Single<DataBase<Any>>

    @GET("practise/paperAnalyzes")
    fun getExerciseParsingFrame(
        @Query("practisekey") practisekey: String, @Query("userpractisekey") userpractisekey: String, @Query("evalKey") evalKey: String,
        /** 0 未做 1 未做 **/
        @Query("ishistory") ishistory: String, @Query("isError") isError: String
    ): Single<DataBase<ExerciseFrame>>

    @GET("practise/newQuestion")
    fun getExerciseDetail(
        @Query("practisekey") practisekey: String, @Query("groupkey") groupkey: String, @Query("questiontype") questiontype: String, @Query("questionkey") questionkey: String, @Query("questionsort") questionsort: String
    ): Single<DataBase<Any>>

    @GET("practise/analyze")
    fun getExerciseParsing(
        @Query("practisekey") practisekey: String, @Query("questionkey") questionkey: String, @Query("questiontype") questiontype: String, @Query("userpractisekey") userpractisekey: String, @Query("questionsort") questionsort: String
    ): Single<DataBase<Any>>

    @POST("practise/submit")
    fun exerciseSubmit(@Body answer: AnswerSubmit): Single<DataBase<Report>>

    //TODO 专项练习交卷
//    @POST("paper/submit")
//    fun exerciseSubmitSP(@Body answer: AnswerSubmit): Single<DataBase<Report>>

    //TODO 测评交卷
    @POST("netcourse/paperSubmit")
    fun exerciseSubmitPG(@Body answer: AnswerSubmit): Single<DataBase<Report>>

    //TODO 过级包测评交卷
//    @POST("netcourse/paperSubmit")
//    fun exerciseSubmitPG(@Body answer: AnswerSubmitPG): Single<DataBase<Report>>


    @POST("practise/questionFB")
    fun questionFB(@Body map: Map<String, String>): Single<DataBase<Any>>

    @GET("netcourse/getEval")
    fun getEvalByScan(@Query("key") key: String): Single<DataBase<List<Eval>>>

    ////////////////专项练习模块////////////////////////////////////

    @GET("paper/specialQuestions")
    fun getSpQuestions(
        @Query("practisekey") practisekey: String, @Query("groupkey") groupkey: String, @Query("paperType") paperType: String
    ): Single<DataBase<List<ExerciseFrameItem>>>

    @GET("paper/analyzes")
    fun getSpAnalyzes(
        @Query("paperkey") paperkey: String, @Query("matpkey") matpkey: String, @Query("userpractisekey") userpractisekey: String
    ): Single<DataBase<ExerciseFrameItem>>

//    @GET("paper/installAnswercark")
//    fun installAnswercark(
//        @Query("paperkey") paperkey: String,
//        @Query("matpkey") matpkey: String,
//        @Query("userpracticekey") userpractisekey: String
//    ): Single<DataBase<Report>>


    //    主观题的付费信息
    @GET("practise/getQuestionPayplan")
    fun getQuestionPayplan(@Query("questionKey") questionKey: String): Single<DataBase<QuestionPayplan>>

    //    电子试卷
    @GET("support/getResourcesByCatalog")
    fun getResourcesByCatalog(
        @Query("catalogKey") catalogKey: String, @Query("gradeKey") gradeKey: String
    ): Single<DataBase<BookDetail>>

    //    直接下单
    @POST("order/directOrder")
    fun getDirectOrder(@Body map: Map<String, String>): Single<DataBase<OrderBean>>

    //    排行榜
    @GET("paper/answerRankingDetail")
    fun getAnswerRankingDetail(
        @Query("paperKey") paperKey: String, @Query("userPractiseKey") userPractiseKey: String, @Query("evalKey") evalkey: String
    ): Single<DataBase<RankBean>>

    //    推荐资讯列表
    @GET("information/evalInfomationList")
    fun evalInfomationList(
        @Query("clssifyKey") clssifyKey: String, @Query("evalRecommendKey") evalRecommendKey: String
    ): Single<DataBase<List<NewsBean>>>

    // 书城列表
    @GET("goods/bookList")
    fun getBookList(
        @Query("keyword") keyword: String, @Query("sortingmode") sortingmode: String, @Query("gradeKey") gradeKey: String, @Query("contentType") contentType: String, @Query("page") page: Int, @Query("step") step: Int
    ): Single<DataBase<List<BookList>>>

    // 书城banner
    @GET("common/getLevelByGrade")
    fun getLevelByGrade(
        @Query("key") key: String, @Query("code") code: String
    ): Single<DataBase<List<Level>>>

    //网课预约
    @POST("common/addShoppingAppointment")
    fun addShoppingAppointment(@Body data: Map<String, String>): Single<DataBase<Any>>

    // 获取拼团和一键加群信息
    @GET("order/getAssembleTeam")
    fun getAssembleTeam(@Query("orderKey") orderKey: String): Single<DataBase<AssembleTeam>>

    //获取网课绑定优惠券
    @GET("netcourse/netPromotional")
    fun getNetPromotional(@Query("courseKey") courseKey: String): Single<DataBase<List<Coupon>>>

    //获取网课文件夹列表
    @GET("netcourse/getSpecialNet")
    fun getSpecialNet(@Query("specialKey") specialKey: String): Single<DataBase<List<Lesson>>>

    //是否有新优惠券提醒(get获取标识，del清空标识)
    @GET("user/getPromotionalFlag")
    fun getPromotionalFlag(@Query("type") type: String): Single<DataBase<Map<String, String>>>//是否有新优惠券提醒(get获取标识，del清空标识)

    @GET("user/getPromotionalFlag")
    fun delPromotionalFlag(@Query("type") type: String): Single<DataBase<Any>>

    // 通过订单号获取一键加群信息
    @GET("order/goodsInfoAfterPay")
    fun goodsInfoAfterPay(@Query("key") orderKey: String): Single<DataBase<AssembleTeam>>

    /**
     *获取考研档案
     */
    @GET("user/getPostgraduateRecord")
    fun getPostgraduateRecord(@Query("key") key: String): Single<DataBase<YanMessage>>

    /**
     *查询学校专业/，type=（学校= SCHOOL、 专业=MAJOR）
     */
    @GET("common/getDictInfo")
    fun getDictInfoSchool(@Query("type") type: String, @Query("name") name: String): Single<DataBase<List<FeedbackQuestion>>>

    /**
     *跳过考研档案
     */
    @GET("user/ignorePostgraduateRecord")
    fun ignorePostgraduateRecord(): Single<DataBase<Any>>

    /**
     *考研档案新增更新
     */
    @POST("user/postgraduateRecord")
    fun postgraduateRecord(@Body data: YanMessage): Single<DataBase<Any>>

    /**
     *获却网课预习列表
     * 参数courseKey网课主键，vidoKey网课小节主键
     */
    @GET("netcourse/prepareList")
    fun getPrepareList(@Query("courseKey") courseKey: String, @Query("videoKey") videoKey: String): Single<DataBase<List<BookRes>>>

    /**
     * netcourse/learnedPrepare记录网课预习接口 get ，参数courseKey网课主键，vidoKey网课小节主键，courseResourceKey预习主键
     */
    @GET("netcourse/learnedPrepare")
    fun getLearnedPrepare(@Query("courseKey") courseKey: String, @Query("videoKey") videoKey: String, @Query("courseResourceKey") courseResourceKey: String): Single<DataBase<Any>>

    /**
     * 每日任务接口 netcourse/getEverydayTask get ,参数courseKey
     */
    @GET("netcourse/getEverydayTask")
    fun getEverydayTask(@Query("courseKey") courseKey: String): Single<DataBase<DayWork>>

    /**
     * netcourse/learnedDetail,get,参数courseKey
     */
    @GET("netcourse/learnedDetail")
    fun getLearnedDetail(@Query("courseKey") courseKey: String): Single<DataBase<LessonStudyData>>

    /**
     * netcourse/getLearnYmList,get,参数courseKey
     */
    @GET("netcourse/getLearnYmList")
    fun getLearnYmList(@Query("courseKey") courseKey: String): Single<DataBase<List<LessonStudyMonthData>>>

    /**
     * netcourse/getYmDetail，,get,参数courseKey
     */
    @GET("netcourse/getYmDetail")
    fun getYmDetail(@Query("courseKey") courseKey: String, @Query("ym") ym: String): Single<DataBase<LessonStudyMonthDataDetail>>

    @GET("netcourse/selectHomeNets")
    fun getHomeNets(@Query("key") key: String): Single<DataBase<List<Lesson>>>

    @GET("netcourse/selectHomeNets")
    fun getHomeNets(@Query("key") key: String, @Query("gradeKey") gradeKey: String): Single<DataBase<List<Lesson>>>

    @GET("common/getHomeApplets")
    fun getHomeApplets(@Query("key") key: String): Single<DataBase<List<Grade>>>

    @GET("common/getHomePromotionals")
    fun getHomePromotionals(@Query("key") key: String): Single<DataBase<List<Coupon>>>

    @GET("netcourse/getNetVideoInfo")
    fun getNetVideoInfo(@Query("key") key: String): Single<DataBase<VideoData>>

    @GET("support/userQrcodeRecord")
    fun getQrCodeRecord(@Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<Scan>>>

    @GET("support/clearQrcodeRecord")
    fun clearQrcodeRecord(): Single<DataBase<Any>>

    /**
     * 免密登录
     */
    @POST("user/freePasswLogin")
    fun freePasswLogin(@Body request: Map<String, String>): Single<DataBase<FreeLogin>>

    /**
     * 点读书列表
     */
    @GET("smartBook/list")
    fun getSmartBookList(): Single<DataBase<List<EBook>>>

    /**
     * 点读书详情
     */
    @GET("smartBook/detail")
    fun getSmartBookDetail(@Query("key") key: String): Single<DataBase<EBook>>

    /**
     * 点读书目录
     * type 智能书类型
     */
    @GET("smartBook/catalogList")
    fun getSmartBookCatalogList(@Query("key") key: String, @Query("type") type: String): Single<DataBase<List<EBookCatalog>>>

    /**
     * 精练列表
     */
    @GET("smartBook/refineCatalogList")
    fun getRefineCatalogList(@Query("key") key: String, @Query("smartKey") smartKey: String, @Query("catalogKey") catalogKey: String): Single<DataBase<List<EBookLineData>>>

    @GET("smartBook/mySmarkBook")
    fun getMySmarkBook(): Single<DataBase<List<EBook>>>

    /**
     * 点读书兑换
     */
    @POST("netcourse/activedGoods")
    fun activedGoods(@Body request: Map<String, String>): Single<DataBase<Any>>

    /**
     * 点读书每个页面学习时长
     */
    @POST("smartBook/recordDduration")
    fun postRecordDduration(@Body request: Map<String, String>): Single<DataBase<Any>>

    /**
     * 点读书学习数据
     */
    @GET("smartBook/learnStatistics")
    fun learnStatistics(@Query("key") key: String): Single<DataBase<EBookData>>

    /**
     * 点读书学习数据
     */
    @GET("smartBook/learnDurationDetail")
    fun learnDurationDetail(@Query("key") key: String, @Query("type") type: String): Single<DataBase<List<EBookLineData>>>

    /**
     * 点读书学习数据
     */
    @GET("smartBook/learnAccuracyDetail")
    fun learnAccuracyDetail(@Query("key") key: String, @Query("type") type: String): Single<DataBase<List<EBookLineData>>>

    /**
     * 精练词汇
     */
    @GET("smartBook/refineQuestion")
    fun refineQuestion(@Query("key") key: String, @Query("userPracticeKey") userPracticeKey: String): Single<DataBase<EBookExerciseWord>>

    /**
     * 听力练习
     */
    @GET("smartBook/refineListeningQuestion")
    fun refineListeningQuestion(@Query("paperKey") paperKey: String, @Query("userPracticeKey") userPracticeKey: String, @Query("questionKey") questionKey: String, @Query("questionType") questionType: String): Single<DataBase<QuestionInfo>>

    @GET("support/getLrcDetail")
    fun getLrcDetail(@Query("lrcKey") lrcKey: String): Single<DataBase<LrcData>>

    @POST("support/recordLrc")
    fun recordLrc(@Body request: Map<String, String>): Single<DataBase<Any>>

    /**
     * 注销账户
     */
    @GET("user/deleteUser")
    fun deleteUser(): Single<DataBase<Any>>

    /**
     * 试卷每分钟调用一次
     */
    @POST("paper/lockPaper")
    fun lockPaper(@Body request: Map<String, String>): Single<DataBase<Any>>

    /**
     * 新扫码单词列表
     */
    @GET("word/wordCatalogInfo")
    fun wordCatalogInfo(@Query("classifyKey") classifyKey: String): Single<DataBase<ScanWord>>

    /**
     * 添加单词学习记录
     */
    @GET("word/recordLastPosition")
    fun wordRecord(@Query("classifyKey") classifyKey: String, @Query("catalogKey") catalogKey: String, @Query("wordKey") wordKey: String): Single<DataBase<Any>>

    /**
     * 添加单词学习记录
     */
    @GET("word/collectedClassifyList")
    fun collectedClassifyList(@Query("gradekey") gradekey: String): Single<DataBase<Any>>

    /**
     * 收藏数量
     * 参数type 1问答 ，2帖子，3资讯，4试题，5音频, 6词汇、source=3
     */
    @GET("user/collectedCount")
    fun collectedCount(@Query("type") type: String, @Query("source") source: String): Single<DataBase<Map<String, String>>>

    /**
     * 收藏的词表
     */
    @GET("word/collectedClassifyList")
    fun collectedClassifyList(): Single<DataBase<WordHome>>

    /*是否展示直播标签的
    ，参数positionKey  （直播入口展示位置：0精选 1四六级  2考研 3专四专八
    h0学习-网课详情 h1网课专辑 h2书城）
    */
    @GET("netcourse/getLiveFlag")
    fun getLiveFlag(@Query("positionKey") positionKey: String): Single<DataBase<LiveFlag>>

    /*
    * 获取直播间商品，get，无参数
    * */
    @GET("netcourse/getLiveShopping")
    fun getLiveShopping(): Single<DataBase<LiveShopping>>

    /*
   * 直播间优惠券列表，get，无参数
   * */
    @GET("netcourse/getLivePromotionals")
    fun getLivePromotionals(): Single<DataBase<List<Coupon>>>

    /**
     * 优惠券详情
     */
    @GET("user/couponDetail")
    fun couponDetail(@Query("key") key: String, @Query("fromType") fromType: String): Single<DataBase<Coupon>>

    /**
     * 优惠券详情
     */
    @POST("netcourse/recordLiveScore")
    fun recordLiveScore(@Body request: Map<String, String>): Single<DataBase<Any>>

    /**
     * 获取直播弹窗配置信息
     */
    @GET("netcourse/getLivePop")
    fun getLivePop(@Query("popKey") popKey: String, @Query("type") type: String): Single<DataBase<LivePop>>

    /**
     * /netcourse/getLiveBackPopList直播间回放弹窗列表
     */
    @GET("netcourse/getLiveBackPopList")
    fun getLiveBackPopList(@Query("videoKey") videoKey: String): Single<DataBase<List<LiveBackPop>>>


    /**
     * 巧记速记 图书单词列表 图片
     */
    @GET("smartBook/getReadList")
    fun getReadList(@Query("catalogKey") catalogKey: String): Single<DataBase<List<EBookImg>>>

    /**
     * 巧记速记 练习
     */
    @GET("smartBook/getQuestionList")
    fun getQuestionList(@Query("catalogKey") catalogKey: String): Single<DataBase<List<EBookPractice>>>

    /**
     * 巧记速记 练习提交
     */
    @POST("smartBook/questionSubmit")
    fun questionSubmit(@Body params: EBookPracticeAnswer): Single<DataBase<Map<String, String>>>

    /**
     * 巧记速记 练习
     */
    @GET("smartBook/paperQuestions")
    fun paperQuestions(@Query("catalogKey") catalogKey: String): Single<DataBase<ExerciseFrame>>

    //我的考试倒计时列表
    @GET("user/getExamList")
    fun getExamList(): Single<DataBase<List<examList>>>

    //我的口语考试
    @GET("examination/mySpokenExamine")
    fun mySpokenExamine(@Query("state") state: String): Single<DataBase<SpokenExamin>>

    //四六级口语模考
    @GET("examination/appList")
    fun examinationList(): Single<DataBase<ExaminationList>>

    //完成任务得积分接口 完成任务得积分接口，get请求，参数type（1签到,7分享），text（7分享时，对应的类型）
    @GET("integral/tastIntegral")
    fun tastIntegral(@Query("type") type: String, @Query("text") text: String): Single<DataBase<Map<String, String>>>
}