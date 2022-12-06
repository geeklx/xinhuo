package tuoyan.com.xinghuo_dayingindex.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import tuoyan.com.xinghuo_dayingindex.utlis.DateUtil
import java.io.Serializable

/**
 * 创建者：
 * 时间：  2018/10/22.
 */
class Address : Serializable {
    /**
     * zipcode : asda
     * isDefault : 0
     * receiver : asda
     * countyId : 3id
     * receiverMobile : sda
     * fullName : 1id2id3d
     * detailAddress : as
     * cityId : 2id
     * provinceId : 1id
     * key : 546728720939609344
     * userKey :
     */

    var zipcode: String? = null
    var isDefault: String? = null
    var receiver: String? = null
    var countyId: String? = null
    var receiverMobile: String? = null
    var fullName: String? = null
    var detailAddress: String? = null
    var cityId: String? = null
    var provinceId: String? = null
    var key: String? = null
    var userKey: String? = null
}

class MMMM {
    var list: List<Message>? = null
}

class Message : Serializable {
    /**
     * title : Z3iMNC
     * time : d!^K
     * img : tDY*d
     * des : bJRW3
     * key : nL9qA
     * url : z$G7r
     * type : cGG51S
     * targetKey : kvZ
     */

    var title: String? = null
    var createDate: Long = 0L
    var userKey: String? = null
    var isInstation: String? = null
    var img: String? = null
    var content: String? = null
    var key: String? = null
    var url: String? = null
    var targetType: String? = null//1.1 1.2 2 3
    var targetKey: String? = null
    var targetUrl: String? = null
    fun time(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val currentDate = DateUtil.longToString(currentTimeMillis, "yyyy-MM-dd")
        val currentDataLong = DateUtil.stringToLong(currentDate, "yyyy-MM-dd")
        if (createDate == 0L) return ""
        if (createDate > currentDataLong) {
            return DateUtil.longToString(createDate, "HH:mm")
        }
        return DateUtil.longToString(createDate, "yyyy-MM-dd")
    }
}

class Coupon {
    /**
     * key : 4*HX
     * name : fjZY1%
     * content : $(nO
     * facevalue : 1Lv%vN
     * validitytime : rX1][
     */

    var key: String = ""
    var name: String? = null
    var content: String? = null
    var facevalue: String? = null
    var validitytime: String? = null
    var startTime: String? = null
    var endTime: String? = null
    var orderAmountLimit: String? = null
    var goodList: List<GoodList>? = null
    var isUniversal: String? = null
    var isOwn = "0"
    var ownNum = "0"//优惠券详情接口用这个字段代表了是否领取
    var code = ""
    var remarks = ""
    var isLive = ""
    var name2 = ""

}

class GoodList {
    var name: String? = null
    var id: String? = null
    var type: String? = null
}

/**
 * 个人信息
 */

class UserInfo(
    var gradename: String? = null,
    var img: String? = null,
    var address: String? = null,
    var phone: String? = null,
    var signature: String? = null,
    var sysUserKey: String? = null,
    var sex: String? = null,
    var grade: String? = null,
    var name: String? = null,
    var section: String? = null,
    var birth: String? = null,
    var sectionname: String? = null
) {
    var postgraduateRecordKey: String = ""
}


/**
 * 个人主页信息
 */
class HomePageInfo(
    var img: String? = null,
    var name: String? = null,
    var fanscount: String? = null,
    var attentioncount: String? = null,
    var gradename: String? = null,
    var sectionname: String? = null,
    var signature: String? = null,
    var errQuestionCount: String? = null,
    var followCirclecount: String? = null,
    var discountcount: String? = null,
    var collectioncount: String? = null,
    var quizcount: String? = null,
    var ordercount: String? = null,
    var examCount: String? = null,
    var spokenCount: String? = null
)

/**
 * 试卷收藏列表
 */
class Paper : Serializable {
    /**
     * title : zn%jC
     * type : I^1Yp
     * date : IGy7
     * id : rWGrp
     */

    var key: String? = null
    var name: String? = null
    var createDate: String? = null

    //    var title: String? = null
    var type: String? = null
//    var date: String? = null
//    var id: String? = null
}

/**
 * 我收藏的音频
 */
class Audio {
    /**
     * title : zn%jC
     * type : I^1Yp
     * date : IGy7
     * id : rWGrp
     */

    var key: String? = null
    var name: String? = null
    var type: String? = null
    var size: String? = null
    var palyUrl: String? = null
    var downloadUrl: String? = null
    var lrcUrl: String? = null
    var lrcUrl2: String? = null
    var lrcUrl3: String? = null
    var downloadFlag: String? = null
}

@Parcelize
class GradeBean(
    var name: String = "",
    var isAdd: Boolean = false,
    var id: String = "",
    var key: String = ""//精选：0；四六级 ：1；考研：2；专四专八：3；
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        return id == (other as GradeBean).id
    }
}

class YanMessage {
    var name = ""
    var phone = ""
    var currentSchoolId = ""
    var currentSchool = ""
    var applySchoolId = ""
    var applySchool = ""
    var applyMajorId = ""
    var applyMajor = ""
}

class SpokenExamin {
    var spokenExaminBeanList = mutableListOf<SpokenExaminBean>()
}

class SpokenExaminBean {
    //    "groupType": null,
    var groupType = ""
//    "examineNumber": null,
//    "appUserKey": null,
//    "commentScore": null,
    var commentScore = ""

    //    "examEndTime": "19:00",
    var examEndTime = ""

    //    "examStartTime1": "2022-10-14 09:50:00",
//    "spokenExaminationName": "四级口语模考1012",
    var spokenExaminationName = ""
    var scoreNumber = ""

    //    "scoreNumber": null,
//    "commentB": null,
//    "commentA": null,
//    "imageUrl": null,
    var imageUrl = ""

    //    "examStartTime": "2022-10-14 09:50",
    var examStartTime = ""

    //    "state": "4",
    var state = ""
//    "checkType": null,
//    "spokenReadKey": null,
//    "list": null,
//    "groupNumber": null,
//    "spokenExaminationKey": "1611722761766804544",
var spokenExaminationKey = ""
//    "studentState": null,
//    "checkStartTime1": "2022-10-15 00:00:00",
//    "checkEndTime1": "2022-11-15 23:59:59",
//    "comment": null,
//    "examinationType": null,
//    "sectionType": null,
//    "examEndTime1": "2022-10-14 19:00:00"


}