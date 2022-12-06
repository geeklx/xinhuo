package tuoyan.com.xinghuo_dayingindex.bean

import udesk.core.model.Product
import java.io.Serializable

/**
 * Created by  on 2018/9/13.
 */


class HomeData(
    var syzwList: List<Advert>,
    var sybnList: List<Advert>,
    var netList: List<Lesson>,
    var menuList: List<GradeMenu>,
    var examList: List<examList>,
)

class examList {
    var name = ""
    var days = ""
}

class Advert {
    constructor()
    constructor(
        img: String,//图片地址
        alternateImg: String,//非常规屏幕 取用的图片
        sort: Int,//序号
        url: String, //跳转url
        link: String, //跳转url
        title: String,
        goodtype: String,//类型，net：网课，book：点读,小程序：app
        goodkey: String,
        type: String, //商品：goods，外链：link，
        key: String
    ) : this() {
        this.img = img
        this.alternateImg = alternateImg
        this.sort = sort
        this.url = url
        this.link = link
        this.title = title
        this.goodtype = goodtype
        this.goodkey = goodkey
        this.type = type
        this.key = key
    }

    var img: String = ""//图片地址
    var alternateImg: String = ""//非常规屏幕 取用的图片
    var sort: Int = 0//序号
    var url: String = ""//跳转url
    var link: String = ""//跳转url
    var title: String = ""
    var goodtype: String = ""//类型，net：网课，book：点读,小程序：app
    var goodkey: String = ""
    var type: String = "" //商品：goods，外链：link，
    var key: String = ""
}

class NetLesson : Serializable {
    var liveKey: String = ""
    var liveToken: String = ""
    var playUrl: String = ""
    var netList: ArrayList<Lesson>? = null
    var liveSource: String = ""//liveSource 1欢拓 2cc
    var roomid: String = ""
    var userid: String = ""
    var viewername: String = ""
    var viewertoken: String = ""
    var groupid: String = ""
    var recordid: String = ""
    var liveid: String = ""

    //用于神策统计数据
    var chargeType: String = ""
    var periodId: String = ""
    var teacher: String = ""
    var liveState = ""
    var name = ""
    var recommendTime = ""

    //isShowScore是否展示课程打分 1-展示 2-不展示
    var isShowScore = ""
}

class Lesson(
    var isown: String = "",
    var img: String = "",
    var period: String = "",
    var buyers: String = "",
    var title: String = "",
    var name: String = "",
    var limitBuyers: String = "",
    var isLimitFree: String = "",
    var teacher: String = "",
    var form: String = "",
    var netSubjectName: String = "",
    var price: String = "",
    var liveTime: String = "",
    var disprice: String = "",
    var key: String = "",
    var chargeType: String = "",
    var sellingPoint: String = "",
    var isAppointment: String = "",//1预约，0否
    var isAppointedNet: String = "",// 1已预约，0未预
    var upStartTime: String = ""// 预约结束时间
) : Serializable {
    var assembleKey: String = ""// 拼团key
    var assemblePrice: String = ""// 价格
    var dataType: String = ""// 是否网课文件夹0：网课；1：网课文件夹
    var saleOut = ""// 1-售罄 否则-在售
    var privateName = ""// 内部名称
    var sellingPointImg = ""// 卖点图片
    var sellingPointColor = ""// 卖点颜色
    var teacherImg = ""// 老师头像
    var dpSweelPrice = ""// 膨胀金
    var dpPrice = ""// 定金
    var dpKey = ""// 定金key
}

class ScanResult {
    var key: String? = null
    var type: String? = null //1：资源，2：图书
    var state: String? = null //0 未激活 1 已激活
    var targetKey: String? = null
}

class ScanRes(
    var resources: ArrayList<ScanResItem>,
    var bookName: String,
    var name: String
) : Serializable

class ScanResItem(
    var appResourceKey: String,
    var id: String,
    var catalogKey: String,
    var link: String,
    var name: String,
    var resourceType: String,
    var playUrl: String,
    var downloadUrl: String,
    var iscollection: String,
    var downloadFlag: String,
    var size: String,
    var imagesTextContent: String,
    var duration: String,
    var lrcurl: String,
    var lrcurl2: String,
    var lrcurl3: String
) : Serializable

class NewVersion {
    var isForce: String = "" //0不限制，1强制
    var msg: String = ""
    var v: String = ""
    var url: String = ""
    var patchUrl: String = ""
    var patchVersion: String = ""
}

class Level(
    var code: String = "",
    var name: String = "",
    var key: String = "",
    var isAdd: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        return key == (other as Level).key
    }
}

class DayWork {
    var dayList = ArrayList<DayWorkItem>()//	每日任务
    var adTargetImg = ""
    var adTargetType = ""//广告类型：1网课,2文章
    var adTargetKey = ""
    var finishNum = ""//已完成任务数
    var pushList = ArrayList<PushItem>()//推荐学习
    var adInfLink = ""//外链
    var adInfType = ""//文章类型：1、文字内容 2、外链内容
    var name = ""

}

/**
 * 每日必做任务
 */
class DayWorkItem {
    var videoKey = ""//小节key
    var practiceCount = ""//作业完成任务
    var name = ""
    var state = ""//网课小节（0未开课,1直播中,2 维护中,3已转录,4 可回放,5 未进行直播但已到直播结束时间），作业（0直播未开始不能做 ， 1有作业 ，2已完成作业）
    var type = ""//类型：1网课小节 ，2 作业
    var liveStart = ""
    var teacher = ""
    var chargeType = ""
}

/**
 * 推荐学习
 */
class PushItem {
    var paperKey = ""//	试卷key
    var netCoursePushKey = ""//推送主键
    var practiseCount = ""
    var name = ""
    var targetType = ""//推送类型：测评eval 试卷paper 小程序applet
    var state = ""//测评批改状态： 0未批改 1已批改 2批改中
    var targetKey = ""
    var userPracticeKey = ""//测评已做主键
    var isFinish = ""//1 提交完成
}

class GradeMenu {
    var key = ""
    var name = ""

    //imgType图标类型：1图片；2动图
    var imgType = ""
    var imgUrl = ""

    /**
     * 10016003909571888007  敬请期待
     * 10016003909571888006  优惠专区
     * 10016003909571888005  智能模考(全部)
     * 10016003909571888004  备考干货(全部)
     * 10016003909571888003  微信小程序
     * 10016003909571888002  活动页
     * 10016003909571888001  网课
     * 10016003909571888000  图书配套(全部)
     */
    var menuType = ""
    var content = ""
}

class ProductBean : Serializable {
    var name = ""
    var url = ""
    var imgUrl = ""
    var params = mutableListOf<Product.ParamsBean>()
}

class VideoData {
    var isOwn = ""
    var teacher = ""
    var form = ""
    var price = ""
    var chargeType = ""
    var courseKey = ""
    var endTime = ""
    var disprice = ""
    var title = ""
    var privateName = ""
}

class VideoParam : Serializable {
    var courseKey = ""
    var key = ""
    var name = ""
    var type = ""
    var offline = false
    var recommendTime = ""
    var path = ""
}

class LiveFlag {
    //    "viewertoken": "",
//    "roomId": "",
//    "userKey": "1642180908885360436",
//    "isShow": 0
    var viewertoken = ""
    var roomId = ""
    var userId = ""
    var isShow = 0
}