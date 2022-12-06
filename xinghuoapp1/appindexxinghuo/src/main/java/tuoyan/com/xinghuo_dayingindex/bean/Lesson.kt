package tuoyan.com.xinghuo_dayingindex.bean

import java.io.File
import java.io.Serializable

/**
 * 创建者：
 * 时间：  2018/9/29.
 */
class LessonDetail : Serializable {
    /**
     * title : y^4S@
     * time : s2qn7
     * videoUrl : 9$RhDV*
     * lessonList : [{"title":"1y&u","duration":"v0%2","type":"cptho","liveDate":"l8Ji46","workKey":"[UT","workState":"z5)#ORs","lessonKey":"(mQ36","playUrl":"HVj","comments":"(oM3"}]
     * resourceList : [{"title":"C*5","type":"[7@OI","url":"3S2gkd","resourceList":[]}]
     * qqGroup : ADw21
     */
    var isThereTrialCourse = ""//是 ”1“  否 “0”
    var title: String? = null
    var time: String? = null

    //    var videoUrl: String? = null
//    var qqGroup: String? = null
//    var lessonList: ArrayList<Lesson>? = null
//    var resourceList: ArrayList<ResourceList>? = null
    //----------------------------------------------------------
    var downTime: String? = null
    var buyers: Int = 0
    var limitEndTime: String? = null
    var isunsub: Int = 0
    var content: String? = null
    var limitBuyers = ""
    var points: Double? = null
    var netcoursekey: String? = null
    var iseffect: String? = null//针对已购买，1过期，0未过期；否则为空
    var netSubjectName: String? = null
    var price: String? = null
    var liveTime: String? = null
    var details: String? = null
    var limitStartTime: String? = null
    var isown: String? = null
    var period: Int = 0
    var coverimg: String? = null
    var iscomment: String? = null
    var validitytime: String? = null
    var form: String = "" //TODO 1直播 2录播 3过级包 4音频课
    var videourl: String = ""
    var coverUrl: String = ""
    var coverTopImg: String = ""
    var disprice: String? = null
    var chargetype: String? = null
    var teacher: String? = null
    var teacherList: ArrayList<TeacherListBean>? = null
    var resouceList = ArrayList<ResourceListBean>()
    var classList = ArrayList<ClassListBean>()
    var evalList: ArrayList<Eval>? = null
    var endTime: String = ""
    var isLimitFree: String = ""
    var qqNum: String = ""
    var qqKey: String = ""
    var qqContent: String = ""
    var isSoldOut: String = "" //是否售罄 ，1是 0否
    var state: String = "" //1 未上架 2 正常 9 已删除（不存在）
    var liveState: String = ""
    var nextBeginTime: String = ""
    var resourceKey: String = "" //TODO 用于请求网课详情短视频的 key
    var isAppointment: String = ""//1预约，0否
    var isAppointedNet: String = ""//1已预约，0未预约
    var composeList: ArrayList<Compose>? = null//关联网课
    var isApplet: String = ""//是否有小程序，1有，0否
    var assembleKey: String = ""//拼团key
    var assembleStartTime: Long = 0// 开始时间时间戳
    var assembleEndTime: Long = 0// 结束戳
    var assemblePrice: String = ""// 价格
    var assembleLimitNum: String = ""// 上限人数
    var assembleRemarks: String = ""// 规则
    var assembleState: String = ""// 0预热 1拼团开始
    var orderKey: String = ""//，订单主键
    var assembleNumber: String = ""//拼团实际人数
    var assembleTeamEndTime: Long = 0//此团结束时间
    var docDownUrl: String = ""//
    var isDocDown: String = ""//
    var promotionalList = ArrayList<Promotional>()//关联优惠券
    var appointmentStartTime: Long = 0L//预约课时间
    var isAppletListen: String = ""//是否有虐耳小程序，1有 0无
    var postgraduateRecordKey: String = ""//0未填跳过，“”需要填报，其他已填报档案的主键key
    var IsEverydayTask: String = ""//是否开启每日任务1:开 0:关；
    var videoNum: String = ""//小节总数
    var workNum: String = ""//作业总数,如果workNum=0 即不存在作业
    var videoLearnedNum: String = ""//完成学习总数
    var workLearnedNum: String = ""//完成作业总数
    var privateName: String = ""//网课内部名称
    var isAdmissionTicket: String = ""//是否开启准考证
    var allowAddAdmissionCard: String = ""//是否允许添加准考证
    var necessary: String = ""//是否包含必修
    var prepareLearnedNum: String = ""//完成预习数量
    var prepareNum: String = ""//预习总数
    var admissionTicketImg: String = ""//图片地址
    var jPSaleRemark: String = ""//联报课说明
    var jointProgramKey: String = ""//联报key
    var dpState: String = ""//定金状态dpState：11可支付订单 ，12不可支付定金 ，20定金已付，尾款未开始，21可支付尾款，22不可付尾款
    var dpPrice: String = ""//定金
    var dpFinalPrice: String = ""//尾款
    var dpKey: String = ""//定金key
    var dpOrderKey: String = ""//定金支付完成 支付订单key
    var dpEndTime: Long = 0//定金结束时间
    var dpFinalEndTime: Long = 0//尾款结束时间
    var dpFinalStartTime: String = ""//尾款开始时间
    var rateLearningShow: String = ""//学习进度是否展示 0-否 1-是
    var videoLearnedRateState: String = ""//0未学习 1学习中（1.2至少学习完一节，否则都是学习进行中1.1） 2已完成学习
    var qrCode:String=""//一键加群  图片地址  有跳转到小程序
    var playbackSourceKey:String=""//增加回放入口参数playbackSourceKey，有值即展示，否则不展示
}
class Spoken{
    var spoken: SpokenDetail? =null
}
class SpokenDetail(
    var appUserKey: String,
    var applyEndTime: Any,
    var applyEndTime1: Any,
    var applyStartTime: Any,
    var applyStartTime1: Any,
    var buyCount: String,
    var checkEndTime: Any,
    var checkEndTime1: Any,
    var checkStartTime: Any,
    var checkStartTime1: Any,
    var checkState: String,
    var createDate: Any,
    var cuCheck: String,
    var cuCheckOriginal: String,
    var examEndTime: String,
    var examEndTime1: Any,
    var examStartTime: String,
    var examStartTime1: Any,
    var examinationNum: String,
    var examinationType: Any,
    var groupNumber: Any,
    var imgList: List<String>,
    var introduce: String,
    var isBuy: String,
    var jingCheck: String,
    var jingCheckOriginal: String,
    var orderGoodsPrice: Any,
    var orderKey: Any,
    var orderType: Any,
    var publishType: Any,
    var remark: Any,
    var resourcePath: String,
    var resourcePath2: String,
    var scoreList: Any,
    var scoreName: Any,
    var sectionType: Any,
    var spokenExaminationKey: String,
    var spokenExaminationName: String,
    var spokenPaperKey: Any,
    var spokenPaperName: Any,
    var state: Any,
    var stateTime: Any,
    var tryEndTime: Any,
    var tryEndTime1: Any,
    var tryStratTime: Any,
    var tryStratTime1: Any,
    var type: Any,
    var urlList: Any
)
//优惠券
class Promotional {
    var name = ""
    var promotionalKey = ""
}

class Compose : Serializable {
    var key: String = ""
    var title: String = ""
}

class LessonList : Serializable {

    /**
     * title : aFlsD
     * liveTime : k)e
     * teacher : L8*rXQ
     * limit : 7mQ2
     * buyers : fw3
     * type : Y9jO
     * price : 0ibr
     * disprice : 5y#ux
     * img : fB@%
     * key : DzwoBe
     * netSubjectName : SGKJ
     * isLimitFree : O2WBL$
     * form : k!Lqyq
     * period : ig(yyr
     */

    var title: String? = null
    var liveTime: String? = null
    var teacher: String? = null
    var limit: String? = null
    var buyers: String? = null
    var type: String? = null
    var price: String? = null
    var disprice: String? = null
    var img: String? = null
    var key: String? = null
    var netSubjectName: String? = null
    var isLimitFree: String? = null
    var form: String? = null
    var period: String? = null
}

class RecommedBean {
    var key: String = ""
    var recommendKey: String = ""
    var name: String = ""
    var type: String = ""//1:bookList 图书;2: netList 网课;3: paperList试卷;4:infomationList 资讯;5:appletList小程序
    var userPracticeKey: String = ""//试卷做题记录
    var isFinish = ""//1 完成
}

class PaperBean {
    var key: String = ""
    var name: String = ""
    var userPracticeKey: String = ""//试卷做题记录
    var isFinish: String = ""//1:完成
}

class InfomationBean {
    var clssifyKey: String = ""
    var clssifyName: String = ""
    var evalRecommendKey: String = ""
}

class BookList : Serializable {

    /**
     * key : R80d
     * img : uVm
     * title : fe07r
     * disprice : FNg)
     * price : ^uvzmRA
     * edition : fOfc&
     * saleNum : ngi!!
     * isAddCart : yw6qi
     */

    var key: String? = null
    var img: String? = null
    var title: String? = null
    var disprice: String? = null
    var price: String? = null
    var edition: String? = null
    var saleNum: String? = null
    var isAddCart: String? = null
    var sellingPoint: String = ""
}

class ResourceList : Serializable {
    /**
     * title : C*5
     * type : [7@OI
     * url : 3S2gkd
     * resourceList : []
     */

    var title: String? = null
    var type: String? = null
    var url: String? = null
    var resourceList: List<*>? = null
}

/**
 * 测评报告i
 */
class EvalReport {
    /**
     * hearingScore :
     * hearingTotal :
     * writingTotal :
     * translationScore :
     * translationTotal :
     * remark :
     * title :
     * totalScore : 0
     * writingScore :
     * rightScore : 0
     * netList : []
     * readingScore :
     * readingTotal :
     * bookList : []
     */

    var hearingScore: Float = 0f
    var hearingTotal: Float = 0f
    var writingTotal: Float = 0f
    var translationScore: Float = 0f
    var translationTotal: Float = 0f
    var remark: String? = null
    var title: String? = null
    var type: String? = null
    var totalScore: String? = null
    var writingScore: Float = 0f
    var rightScore: String? = null
    var readingScore: Float = 0f
    var readingTotal: Float = 0f
    var isComment: String = ""
    var evalMode: String = ""
    var netList: List<Lesson>? = null
    var bookList: List<BookList>? = null

    var paperList: List<PaperBean>? = null
    var infomationList: List<InfomationBean>? = null
    var appletList = ArrayList<MiniProgram>()

    var imgList: List<String>? = null
    var correctList: List<CorrectBean>? = null
    var subiEvalList: List<SubiEvaBean>? = null
    var questionKey: String = ""
}

class SubiEvaBean {
    var totalSore: String = ""
    var name: String = ""
    var key: String = ""
    var rightScore: String = ""
}

class CorrectFromHtml {
    var correctList: List<CorrectBean> = ArrayList<CorrectBean>()
    var index = 0
}


class CorrectBean : Serializable {
    var imgUrl: String = ""
    var key: String = ""
    var thumbnailUrl: String = ""
    var isLocal = false//主观题做题 是否从本地上传
    var file: File? = null//本地上传的file文件
}

class TeacherListBean : Serializable {
    var teacherKey: String? = null
    var teacherName: String? = null
    var headImg: String? = null
    var introduce: String? = null
}

class ResourceListBean : DownloadBean(),
    Serializable {
    var sort: String? = null //TODO 序号，大英直播列表UI更新后新增
    var downloadFlag: String? = null
    var teacher: String? = null
    var count: String? = null
    var chargeType: String? = null
    var liveTime: String? = null
    var playUrl: String? = null
    var liveState: String = "" //TODO 未开课0  直播中1 维护中2 已转录3 可回放4 未进行直播但已到直播结束时间5 今日开课99
    var workState: String = ""//TODO 0 没作业 1 有作业 2 已完成 3 继续学习
    var pointsCount: String = "" //TODO 评价数量
    var isLearn: String = "" //TODO 是否已看过
    var isChecked: Boolean = false//音频课用
    var isLastLook = ""//是否最后观看 1：观看
    var userPracticeKey = ""//livetype==3 为测评时，是否已经提交    测评只能提交一次 生成测评报告
    var field1 = ""
    var field6 = ""//livetype==3 为测评时，测评时为小节key
    var field3 = ""//livetype==3 为测评时，批改状态
    var prepareState = ""// 0 不包含预习，1包含，2预习中，3已预习
    var sectionProperties = ""//0 选修小节  1必修小节
    var completeState = ""//0 小节状态  0未开始 1上课中 2已完成
    var allFinished = ""//true false
}

class LiveListBean {
    var liveList = ArrayList<ResourceListBean>()
    var playBackList = ArrayList<ResourceListBean>()
}


class ClassListBean : Serializable {
    var icon: String = ""
    var catalogName: String? = null
    var catalogKey: String? = null
    var catalogList: ArrayList<ClassListBean>? = null
    var resourceList: ArrayList<LessonRes>? = null
    var isExpand: Boolean = true
}

class LessonRes : DownloadBean(), Serializable {
    var count: String? = null
    var playUrl: String? = null
    var score: String? = null
    var link: String? = null
    var lrcurl: String = ""
    var lrcurl2: String = ""
    var lrcurl3: String = ""
    var content: String = ""
    var isCollection: String = ""
    var downloadFlag: String = ""
    var field1: String = ""
    var field2: String = ""
    var field3: String = ""
    var field4: String = ""
    var field5: String = ""
    var userPracticeKey: String = ""
}

/**
 * 评论
 */
class Comment(
    var img: String,
    var name: String,
    var content: String,
    var time: String,
    var recunm: String,
    var likesnum: String,
    var commentkey: String,
    var isofficial: Int,
    var isthumbup: Int,
    var thumbnails: String,
    var section: String,
    var grade: String,
    var points: Double
)

class Eval(
    var practiseCount: String,
    var score: String,
    var appPaperKey: String,
    var userPracticeKey: String,
    var evalName: String,
    var answerType: String, //0线上  1线下
    var evalKey: String,
    var evalMode: String, //0 自判 1人工 2 可选
    var state: String //阅卷状态 1 批改完成  0、2 批改中
) : Serializable {
    var isFinish = ""//1提交完成
}

class LessonWork(
    var paperKey: String,
    var videoKey: String,
    var paperName: String,
    var courseKey: String,
    var userPracticeKey: String,
    var totalCount: String
) : Serializable {
    var state = ""//0未开始1进行中2完成
    var isFinish = ""//1提交完成
}


class ClassWork() : Serializable {
    var catalogName = ""
    var catalogKey = ""
    var sort = ""
    var floor = ""
    var resourceList = ArrayList<LessonWork>()
    var isExpand = true
}

class TodyLesson(
    var name: String,
    var liveTime: String,
    var teacher: String,
    var liveState: String,
    var id: String,
    var liveKey: String,
    var parentKey: String
) : Serializable {
    var chargeType = ""
}

class CatalogBean : Serializable {
    var catalogName = ""
    var catalogList = ArrayList<CatalogBean>()
    var catalogKey = ""
    var isLastLook = ""//是否最后观看 1：观看
    var resourceList = ArrayList<ResourceListBean>()
}

class PlayBackBean {
    var catalogkList = ArrayList<CatalogBean>()
    var playBackList = ArrayList<ResourceListBean>()
    var isHasLastLearn = ""//0:没有学习过；1：学习过
}

class Catalogue {
    var catalogList = ArrayList<Catalog>()
    var resouceList = ArrayList<ResourceListBean>()
}

class Catalog {
    var pv = ""
    var icon = ""
    var sort = ""
    var type = ""
    var playUrl = ""
    var catalogName = ""
    var score = ""
    var total = ""
    var parentKey = ""
    var downUrl = ""
    var catalogKey = ""
    var id = ""
    var time = ""
    var floor = ""
    var foreignKey = ""
    var resourceList = ArrayList<ResourceListBean>()
    var catalogList = ArrayList<Catalog>()
}

class LessonStudyData {
    var totalDuration = ""//今日累计学习时长
    var mantra = ""//激励语
    var totalWorkLearnedNum = ""//累计学习情况-累计练习完成
    var weekLearnedNum = ""//当前周 完成听课数量
    var weekDuration = ""//当前周 累计学习时长
    var curWeek = ""//当前周
    var totalWorkNum = ""//作业总数
    var curDuration = ""//今日累计学习时长
    var averageVideoNum = ""//班级平均学习时长
    var averageWorkNum = ""//班级平均完成练习
    var weekWorkLearnedNum = ""//当前周 完成练习数量
    var totalVideoNum = ""//小节总数
    var totalLearnedNum = ""//累计学习情况--累计听课完成
    var weekList = ArrayList<LessonStudyWeekData>()
    var ymNum = ""//学习月报数量
    var maxNum = ""//每周学习时长情况下，最大上限时长
}

class LessonStudyWeekData {
    var week = ""//周日期
    var avgDuration = ""//班级平均
    var userDuration = ""//	用户累计
}

class LessonStudyMonthData : Serializable {
    var courseKey = ""//
    var userKey = ""//
    var name = ""//
    var ym = ""//
}

class LessonStudyMonthDataDetail : Serializable {
    var videoNum = ""//我的
    var videoAvgNum = ""//班级平均
    var videoBeat = ""//击败率
    var videoMax = ""//击败率

    var workNum = ""//
    var workAvgNum = ""//
    var workBeat = ""//
    var workMax = ""//
    var homeworkState = ""//0无作业，1有作业

    var durationNumStr = ""//我的
    var durationAvgNumStr = ""//班级平郡
    var durationNum = ""//
    var durationAvgNum = ""//
    var durationBeat = ""//
    var durationMax = ""//
}

class MiniProgram {
    var key = ""//小程序key
    var name = ""
}