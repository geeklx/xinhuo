package com.spark.peak.net

import com.google.gson.JsonObject
import com.spark.peak.bean.*
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*


interface ApiService {
    /*###################################基础模块#############################################*/
    /**
     * 获取新版本
     */
    @GET("common/isForceUp")
    fun getNewVersion(@Query("patchSource") patchSource: String,
                      @Query("appVersion") appVersion: String,
                      @Query("patchVersion") patchVersion: String): Single<DataBase<NewVersion>>

    /**
     * 下载
     */
    @GET
    fun downloadLrc(@Url lrcUrl: String): Single<ResponseBody>

//    //isDownload“0”播放  “1”下载
//    @GET("common/getResourceInfo")
//    fun resInfo(@Query("key") key: String,
//                @Query("source") source: String,
//                @Query("isDownload") isDownload: String): Single<DataBase<Map<String, String>>>

    //isDownload“0”播放  “1”下载
    @GET("common/getResourceInfo")
    fun getResInfo(@Query("key") key: String,
                   @Query("source") source: String,
                   @Query("isDownload") isDownload: String): Single<DataBase<ResourceInfo>>


    /**
     * 获取图片验证码
     */
    @GET("user/randomCode")
    fun randomCode(@Query("phone") phone: String): Single<DataBase<Map<String, String>>>

    /**
     * 校验图片验证码
     */
    @GET("user/validateCode")
    fun validateCode(@Query("phone") phone: String,
                     @Query("code") code: String,
                     @Query("type") type: String): Single<DataBase<Any>>


    /**
     * 发送短信
     */
    @POST("user/sendIdentifyingCode")
    fun getSms(@Body resultSms: SMSCode): Single<DataBase<Any>>

    /**
     * 获取学段列表
     */
    @GET("common/getSectionList")
    fun getSectionList(): Single<DataBase<List<Section>>>

    /**
     * 获取年级列表
     */
    @GET("common/getGradetList")
    fun getGradeList(@Query("key") key: String): Single<DataBase<List<Grade>>>

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
//    @GET("fragment/start")
//    fun start(): Single<AppStart>

    /**
     * 统计页面点击量
     */
    @POST("common/recordModelPv")
    fun postPV(@Body body: Map<String, String>): Single<DataBase<Any>>

    /**
     * 首次安装调用
     */
    @POST("common/recordInstalls")
    fun postFirstInstall(@Body body: Map<String, String>): Single<DataBase<Any>>
    /*###################################用户模块########################################*/
    /**
     * 登录
     */
    @POST("user/login")
    fun login(@Body request: Map<String, String>): Single<DataBase<LoginResponse>>

    /**
     * 免密登录
     */
    @POST("user/freePasswLogin")
    fun freePasswLogin(@Body request: Map<String, String>): Single<DataBase<FreeLogin>>

    /**
     * 退出登录
     */
    @PUT("user/logOff")
    fun logout(): Single<DataBase<ResponseBody>>

    /**
     * 注销
     */
    @GET("user/cancellation")
    fun cancellation(): Single<DataBase<Any>>

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
     * 个人详情
     */
    @GET("user/profile")
    fun getUserInfo(): Single<DataBase<UserInfo>>

    /**
     * 修改资料
     */
    @PUT("user/modifyProfile")
    fun modifyInfo(@Body userInfo: UserInfo): Single<DataBase<UserInfo>>

    /**
     * 修改年级学段
     */
    @GET("user/updUserSG")
    fun updUserSG(@Query("sectionkey") sectionkey: String, @Query("gradekey") gradekey: String): Single<DataBase<Any>>

    /**
     * 关注用户
     */
    @POST("user/attention")
    fun attention(@Body userkey: Map<String, String>): Single<DataBase<Any>>

    /**
     * 添加收藏
     * type	1:问答 ，2帖子，3资讯，4试卷，5音频	是    [string]
     * targetkey
     */
    @POST("user/addCollection")
    fun addCollection(@Body map: Map<String, String>): Single<DataBase<Any>>

    /**
     * 移除收藏
     */
    @POST("user/delCollection")
    fun deleteCollection(@Body map: Map<String, String>)
            : Single<DataBase<Any>>

    /**
     * 取消关注
     */
//    @HTTP(method = "DELETE", path = "collections", hasBody = true)
    @POST("user/cancel")
    fun removeAttention(@Body userkey: Map<String, String>): Single<DataBase<Any>>

    /**
     * 关注人列表
     */
    @GET("user/attentionList")
    fun getAttentionList(): Single<DataBase<List<Attention>>>

    /**
     * 关注圈子
     */
    @GET("community/userAttentionList")
    fun getCircleList(): Single<DataBase<List<Circle>>>

    /**
     * 添加关注圈子
     */
    @POST("community/addUserAttention")
    fun addCircle(@Body communitykey: Map<String, String>): Single<DataBase<List<Circle>>>

    /**
     * 取消关注圈子
     */
    @POST("community/delUserAttention")
    fun delCircle(@Body communitykey: Map<String, String>): Single<DataBase<List<Circle>>>

    /**
     *关注人数量
     */
    @GET("user/attentionCount")
    fun getAttentionCount(): Single<DataBase<String>>

    /**
     * 粉丝数量
     */
    @POST("user/fansCount")
    fun getFansCount(): Single<DataBase<String>>

    /**
     * 粉丝列表
     */
    @GET("user/fansList")
    fun getFansList(): Single<DataBase<List<Attention>>>

    /**
     * 我收藏的音频
     */
//    @HTTP(method = "DELETE", path = "collections", hasBody = true)
    @GET("user/collectedAudioList")
    fun collectionAvdio(@Query("page") page: Int,
                        @Query("step") step: Int = 20): Single<DataBase<List<Audio>>>


    /**
     * 个人主页信息
     */
    @GET("user/homePageProfile")
    fun getHomePageProfile(): Single<DataBase<HomePageInfo>>


    /**
     * 加入我的学习
     */
    @POST("user/addMyStudy")
    fun addStudy(@Query("bookey") bookey: String): Single<DataBase<Any>>

    /**
     * 联系记录
     */
    @GET("user/historyPractice")
    fun historyPractice(@Query("page") page: Int, @Query("step") step: Int = 20): Single<DataBase<List<ExerciseHistory>>>


    /*###################################订单模块########################################*/


    /**
     * 提交订单
     */
    @POST("netcourse/submitOrder")
    fun submitOrder(@Body order: SubmitOrder): Single<DataBase<Map<String, String>>>

    /**
     * 订单列表
     */
    @GET("order/list")
    fun getOrderList(@Query("type") type: Int? = null, @Query("step") step: Int = 99): Single<DataBase<List<Order>>>

    /**
     * 订单列表
     */
    @GET("support/supportingResourceList")
    fun supportingResourceList(@Query("type") type: String,
                               @Query("source") source: String,
                               @Query("key") key: String): Single<DataBase<BookResList>>

    /**
     * 订单详情
     */
    @GET("order/detail")
    fun orderDetail(@Query("key") key: String): Single<DataBase<Order>>

    /**
     * 删除订单
     */
    @POST("order/delete")
    fun deleteOrder(@Body key: Map<String, String>): Single<DataBase<Any>>

    /**
     * 取消订单
     */
    @POST("order/cancel")
    fun cancelOrder(@Body key: Map<String, String>): Single<DataBase<Any>>

    /**
     * 订单支付信息
     */
    @POST
    fun payOrder(@Body map: Map<String, String>): Single<DataBase<Map<String, String>>>

    /*###################################网课模块########################################*/

    /**
     * 网课详情
     */
    @GET("netcourse/detail")
    fun getNetLesson(@Query("key") key: String): Single<DataBase<NetLesson>>

    /**
     * 网课详情目录
     */
    @GET("netcourse/catalogue")
    fun getCatalogue(@Query("key") key: String): Single<DataBase<NetRes>>

    /**
     * 领取网课
     */
    @POST("netcourse/freeBuyNetCourse")
    fun freeBuyNetCourse(@Body key: Map<String, String>): Single<DataBase<Any>>

    /*###################################评论模块########################################*/

    /**
     * 评论列表
     */
    @GET("comment/list")
    fun getComments(@Query("targetkey") targetkey: String,
                    @Query("page") page: Int,
                    @Query("step") step: Int = 10): Single<DataBase<List<Comment>>>

    /**
     * 意见反馈
     */
    @POST("feedback/add")
    fun feedback(@Body feedback: Feedback): Single<DataBase<Any>>

    /**
     * 评论
     */
    @POST("comment/add")
    fun addComment(@Body map: Map<String, String>, @Query("model") model: String
            /**赵志坤让改的**/
    ): Single<DataBase<Any>>

    /*###################################消息模块########################################*/
    /**
     * 是否有新回复
     */
    @GET("notice/ifNewFB")
    fun ifNewFB(): Single<DataBase<Map<String, Int>>>

    /**
     * 是否有新通知
     */
    @GET("notice/ifNewNotice")
    fun ifNewNotice(): Single<DataBase<Map<String, String>>>

    /**
     * 回复列表
     */
    @GET("notice/feedbackList")
    fun feedbacks(): Single<DataBase<Message>>

    /**
     * 通知列表
     */
    @GET("notice/noticeList")
    fun notices(): Single<DataBase<List<Map<String, String>>>>

    /**
     * 通知列表
     */
    @GET("notice/cancel")
    fun cancel(@Query("type") type: Int = 0, @Query("step") step: Int = 99): Single<DataBase<Any>>


    /*####################################优惠券########################################*/


    /**
     * 可用优惠券列表
     */
    @GET("netcourse/getCoupon")
    fun coupons(@Query("goodkey") goodkey: String, @Query("step") step: Int = 99): Single<DataBase<List<Coupon>>>

    /**
     * 兑换优惠券
     */
    @POST("user/exchangeCoupon")
    fun exchangeCoupon(@Body code: Map<String, String>): Single<DataBase<Coupon>>

    /**
     * 优惠券列表
     */
    @GET("user/couponList")
    fun getCouponList(@Query("status") status: Int,
                      @Query("page") page: Int, @Query("step") step: Int = 99): Single<DataBase<List<Coupon>>>

    /**
     * 优惠券详情
     */
    @GET("user/couponList")
    fun getCouponDetail(@Query("key") key: String): Single<DataBase<Coupon>>
    /*###################################首页########################################*/

    /**
     * 首页数据刷新
     * banner  公告  推荐课程  精选图书  学习咨询
     */
    @GET("common/homePageInfo")
    fun homePageInfo(): Single<DataBase<HomeInfo>>

    /**
     * 首页获取推荐网课列表
     */
    @GET("netcourse/recommendList")
    fun getNetClassList(@Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<NetClass>>>

    @GET("admodel/list")
    fun getAdvs(@Query("adspace") adspace: String, @Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<Advert>>>

    @GET("admodel/advertisingPv")
    fun addAdvsPv(@Query("key") key: String): Single<DataBase<Any>>

    @GET("common/getSubjectList")
    fun getSubjects(@Query("key") key: String): Single<DataBase<SubjectBody>>

    @GET("information/recommendList")
    fun getEduInfos(@Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<EduInfo>>>

    @GET("announcement/list")
    fun getAnnouncement(): Single<DataBase<List<Announcement>>>

    @GET("announcement/detail")
    fun getAnnouncementDetail(@Query("key") key: String): Single<DataBase<AnnouncementDetail>>

    @POST("support/getKeyByQrCode")
    fun getDataByScan(@Body data: Map<String, String>): Single<DataBase<ScanResult>>

    @GET("support/getKeyByInsideCode")
    fun getDataByCode(@Query("code") code: String): Single<DataBase<ScanResult>>

    @GET("/information/informationPv")
    fun informationPv(@Query("key") key: String): Single<DataBase<Any>>
    /*###################################随堂练习########################################*/

    @GET("practise/paperHistory")
    fun getExHistory(@Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<ExerciseHistory>>>

    @POST("practise/clearPaperHistory")
    fun clearExHistory(): Single<DataBase<Any>>

    @GET("common/getEditionList")
    fun getVersions(@Query("subjectkey") subjectkey: String): Single<DataBase<List<Version>>>

    @GET("practise/bookList")
    fun getBooks(@Query("editionkey") editionkey: String,
                 @Query("page") page: Int,
                 @Query("step") step: Int): Single<DataBase<List<ExBooks>>>

    @GET("practise/catalogueList")
    fun getExerciseList(@Query("bookkey") bookkey: String): Single<DataBase<ExerciseBody>>

    @GET("practise/paperQuestions")
    fun getExerciseFrame(@Query("practisekey") practisekey: String, @Query("ishistory")
    /** 0 未做 1 未做 **/
    ishistory: String): Single<DataBase<ExerciseFrame>>

    @GET("practise/paperQuestions")
    fun getExerciseFrame1(@Query("practisekey") practisekey: String, @Query("ishistory")
    /** 0 未做 1 未做 **/
    ishistory: String): Single<DataBase<JsonObject>>

    @GET("practise/paperAnalyzes")
    fun getExerciseParsingFrame(@Query("practisekey") practisekey: String, @Query("userpractisekey") userpractisekey: String,
                                @Query("ishistory")
                                /** 0 未做 1 未做 **/
                                ishistory: String): Single<DataBase<ExerciseFrame>>

    @GET("practise/paperAnalyzes")
    fun getExerciseParsingFrame1(@Query("practisekey") practisekey: String, @Query("userpractisekey") userpractisekey: String,
                                 @Query("ishistory")
                                 /** 0 未做 1 未做 **/
                                 ishistory: String): Single<DataBase<JsonObject>>

    @GET("practise/newQuestion")
    fun getExerciseDetail(@Query("practisekey") practisekey: String, @Query("groupkey") groupkey: String,
                          @Query("questiontype") questiontype: String, @Query("questionkey") questionkey: String,
                          @Query("questionsort") questionsort: String): Single<DataBase<Any>>

    @GET("practise/analyze")
    fun getExerciseParsing(@Query("practisekey") practisekey: String, @Query("questionkey") questionkey: String,
                           @Query("questiontype") questiontype: String, @Query("userpractisekey") userpractisekey: String,
                           @Query("questionsort") questionsort: String): Single<DataBase<Any>>

    @POST("practise/submit")
    fun exerciseSubmit(@Body answer: AnswerSubmit): Single<DataBase<Report>>

    @POST("mission/submit")
    fun exerciseSubmitDF(@Body answer: AnswerSubmit): Single<DataBase<Report>>

    @GET("practise/installAnswercark")
    fun exerciseReport(@Query("paperkey") paperkey: String, @Query("userpracticekey") userpractisekey: String): Single<DataBase<Report>>

    @POST("practise/questionFB")
    fun questionFB(@Body map: Map<String, String>): Single<DataBase<Any>>

    /*###################################错题本########################################*/

    @GET("practise/wrongBookList")
    fun getWrongList(@Query("year") year: String, @Query("gradekey") gradekey: String, @Query("subjectkey") subjectkey: String,
                     @Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<WrongBook>>>

    @POST("practise/wrongBookClear")
    fun deleteWrongItem(@Body map: Map<String, String>): Single<DataBase<Any>>

    @GET("practise/wrongBookTimes")
    fun getWrongDate(@Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<WrongBookDate>>>

    @GET("common/getErrorSubjectList")
    fun getWrongSubjects(@Query("key") key: String): Single<DataBase<List<Subject>>>

    /*###################################学习模块########################################*/

    @GET("support/myBookList")
    fun getMyBooks(@Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<MyBookNetClass>>>

    @POST("support/deleteBook")
    fun deleteMyBooks(@Body map: Map<String, String>): Single<DataBase<Any>>

    @POST("netcourse/deleteNetCourse")
    fun deleteMyNetClass(@Body map: Map<String, String>): Single<DataBase<Any>>

    @GET("netcourse/myNetCourse")
    fun getMyNetClass(): Single<DataBase<List<MyBookNetClass>>>

    @GET("netcourse/myNetCourse")
    fun getMyNetLesson(): Single<DataBase<List<NetLessonItem>>>

    @GET("support/bookDetailByKey")
    fun getMyBookDetail(@Query("key") key: String, @Query("type") type: String): Single<DataBase<MyBookDetail>>

    @GET("user/signInDetails")
    fun getSignInfo(): Single<DataBase<SignInfo>>

    @POST("user/signIn")
    fun sign(): Single<DataBase<Any>>

    @GET("user/signInRankingList")
    fun getSignList(@Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<SignListItem>>>

    @POST("support/addMyStudy")
    fun addMyStudy(@Body data: Map<String, String>): Single<DataBase<Any>>

    /*###################################搜索模块########################################*/

    @GET("netcourse/search")
    fun search(@Query("keyword") keyword: String, @Query("sortingmode") sortingmode: Int
            /** 0:默认；1:综合 **/
               ,
               @Query("grade") grade: String, @Query("subject") subject: String,
               @Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<NetLessonItem>>>

    @GET("netcourse/search")
    fun getLessons(@Query("grade") grade: String): Single<DataBase<List<NetLessonItem>>>
    /*###################################闯关联系########################################*/
    /**
     *任务列表
     */
    @GET("mission/list")
    fun cgPractice(
            @Query("grade") grade: String,
            @Query("page") page: Int = 0,
            @Query("section") section: String? = null,
            @Query("subject") subject: String? = null,
            @Query("step") step: Int = 10): Single<DataBase<List<Map<String, String>>>>

    /**
     *关卡列表
     */
    @GET("mission/levelList")
    fun cgPass(@Query("key") key: String, @Query("step") step: Int = 99): Single<DataBase<LL<List<Map<String, String>>>>>

    /**
     *试卷列表
     */
    @GET("mission/paperList")
    fun cgPaper(@Query("key") key: String, @Query("step") step: Int = 99): Single<DataBase<PL<List<Map<String, String>>>>>

    /**
     *扫码记录
     */
    @GET("support/userQrcodeRecord")
    fun getUserQrcodeRecord(@Query("page") page: Int, @Query("step") step: Int): Single<DataBase<List<HomeQr>>>

    /**
     *首页数据
     */
    @GET("common/homePageInfoDF")
    fun getHomePageInfoDF(): Single<DataBase<HomeData>>


    /**
     * 闯关成功弹窗
     */
    @POST("mission/wordShowed")
    fun wordShowed(@Body data: Map<String, String>): Single<DataBase<Any>>

    /*###################################图片上传########################################*/

    @Multipart
    @POST("image/upload/4")
    fun uploadImage(@Query("model") model: String,
                    @Part imgs: List<MultipartBody.Part>): Single<DataBase<Map<String, Any>>>

    @Multipart
    @POST("image/topimg")
    fun uploadTopImage(@Part img: MultipartBody.Part): Single<DataBase<Map<String, String>>>

    @GET("grammar/getCatalog")
    fun getGrammarCatalog(@Query("parentKey") key: String, @Query("serial") serial: String): Single<DataBase<List<Grammar>>>

    @GET("grammar/detail")
    fun getGrammarDetail(@Query("key") key: String): Single<DataBase<GrammarDetail>>

    @GET("grammar/search")
    fun getGrammarSearch(@Query("context") context: String, @Query("parentKey") parentKey: String): Single<DataBase<List<GrammarSearch>>>

    /**
     * sectionKey学段key
     * gradeKey年级key
     * gradeCX 年级dictKey
     */
    @GET("syncListen/selectEditor")
    fun getSelectEditor(@Query("type") type: String,
                        @Query("sectionKey") sectionKey: String,
                        @Query("gradeKey") gradeKey: String,
                        @Query("gradeCX") gradeCX: String,
                        @Query("pvalue") pvalue: String): Single<DataBase<GradeType>>

    @GET("syncListen/selectSection")
    fun getSelectSection(@Query("type") type: String,
                         @Query("pvalue") pvalue: String): Single<DataBase<List<GradeBean>>>

    @GET("syncListen/selectGrade")
    fun getSelectGrade(@Query("type") type: String,
                       @Query("sectionCX") sectionCX: String,
                       @Query("pvalue") pvalue: String): Single<DataBase<List<GradeBean>>>

    /**
     * sectionKey学段key
     * gradeKey年级key
     * subjectKey
     * edition 版本key
     * term 册数kkey
     */
    @GET("syncListen/selectSyncListen")
    fun getSelectSyncListen(@Query("sectionKey") sectionKey: String,
                            @Query("gradeKey") gradeKey: String,
                            @Query("subjectKey") subjectKey: String,
                            @Query("edition") edition: String,
                            @Query("term") term: String,
                            @Query("pvalue") pvalue: String): Single<DataBase<BookContent>>

    @GET("syncListen/catalogResources")
    fun getCatalogResources(@Query("appSyncStudyKey") appSyncStudyKey: String,
                            @Query("resourceType") resourceType: String,
                            @Query("pvalue") pvalue: String): Single<DataBase<BookContent>>

    @GET("syncListen/resLrc")
    fun getJCTBRes(@Query("referenceKey") referenceKey: String,
                   @Query("type") type: String,
                   @Query("pvalue") pvalue: String): Single<DataBase<ResourceInfo>>

    @GET("special/specialList")
    fun getSpecialList(@Query("sectionKey") sectionKey: String,
                       @Query("gradeKey") gradeKey: String,
                       @Query("subjectKey") subjectKey: String): Single<DataBase<List<GradeExercise>>>

    /**
     * 专题对应试卷列表/special/specialPaperList，get,参数specialKey
     */
    @GET("special/specialPaperList")
    fun getSpecialPaperList(@Query("specialKey") sectionKey: String): Single<DataBase<List<SpacialPaper>>>

    /**
     * 问题反馈
     */
    @POST("paper/paperFB")
    fun paperFB(@Body map: Map<String, String>): Single<DataBase<Any>>

    /**
     * 获取问题类型
     */
    @GET("common/getDictInfo")
    fun getDictInfo(@Query("type") type: String): Single<DataBase<List<QFeedBack>>>

    /**
     * 消息列表
     */
    @GET("notice/messageList")
    fun getMessage(@Query("page") page: Int,
                   @Query("step") step: Int): Single<DataBase<MsgList>>

    /**
     * 个人中心 是否有有消息
     */
    @GET("notice/msgReaded")
    fun getMsgReaded(): Single<DataBase<Map<String, String>>>
}