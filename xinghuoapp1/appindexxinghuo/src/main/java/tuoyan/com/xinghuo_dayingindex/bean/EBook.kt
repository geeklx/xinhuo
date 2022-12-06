package tuoyan.com.xinghuo_dayingindex.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created on 2021/7/13
 */
/**
 * 点读书列表
 */
class EBook {
    var coverUrl = ""
    var isOwn = ""//1拥有；0：未拥有
    var buyers = ""
    var price = ""
    var name = ""
    var originalCost = ""
    var endTime = ""
    var key = ""
    var type = "1"//1智能书模考 2简听力 3词汇星火式巧记速记
    var details = ""
    var gradeName = ""
    var state = ""//2 该题库未上架
    var isEffect: String = ""
    var downloadUrl: String = ""//阅读下载链接
    var fileSize: String = ""//阅读下载size
}

class EBookCatalog {
    var lastTime = ""// 巧记速记时 是否空字符串 来判断上次学到
    var resourceKey = ""

    @SerializedName(value = "catalogKey", alternate = ["smartBookCatalogKey"])
    var catalogKey = ""
    var name = ""
    var resourceType = ""//1:试卷(模考) 2:试卷(简听力) 3:练习 4:电子书 5:视频
    var smarkResourceKey = ""//精练key
    var userPracticeKey = ""//练习记录key
    var isFinished = ""//是否完成1：提交完成
    var type = ""//1：一级标题；2：二级标题
    var isTryLearn = ""//试学 1开启试学 2关闭试学
    var parentKey = ""//二级标题加入
    var catalogList = mutableListOf<EBookCatalog>()
    var lineType = "0"//是否展示第三本书的横线 1隐藏   最后一个
    var showType = "0"// 隐藏子列表 0 隐藏  1展示
    var existChapter = ""// 章节测试 1有 0 没有
    var existRead = ""// 阅读 2:已完成 1有 0 没有
    var existQuestion = ""// 练习 1有 0 没有
    var readList = mutableListOf<EBookImg>()
}

/**
 * 点读书页面间传递数据
 */
class EBookParam : Serializable {
    //图书key
    var bookKey = ""

    //目录key
    var catalogKey = ""

    //精练key
    var smarkResourceKey = ""

    //模考试卷key
    var resourceKey = ""

    //练习记录key
    var userpractisekey = ""

    //精练请求单词
    var wordKey = ""

    //材料题key
    var matpKey = ""

    //当前小题key,用于做题报告跳转到解析对应的小题,精练的时候对应材料题的key
    var questionKey = ""
    var isOwn = ""
    var name = ""
    var catalogName = ""
    var from = ""//1下载
}

class EBookData {
    var curDate = ""
    var curDuration = ""
    var curAccuracy = ""
}

class EBookLineData {
    var name = ""
    var value = 0.0f
    var totalCount = ""
    var key = ""
    var lastTime = ""
    var lastSource = ""
    var userPracticeKey = ""
    var matpKey = ""//材料题key用于精练中的听力练习
}

/**
 * 巧记速记  图书单词图片
 */
class EBookImg {
    var key = ""
    var name = ""
    var videoUrl = ""
    var audioUrl = ""
    var imgUrl = ""
    var imgName = ""
    var audioName = ""
    var videoResourceKey = ""
}

class EBookPractice() : Parcelable {
    var name = ""
    var questionList = mutableListOf<QuestionP>()
    var qIndex = 0//当前小题展示的位置 错题之后展示不同上一次的题 (qIndex+1)%questionList.length 顺序展示

    constructor(parcel: Parcel) : this() {
        name = parcel.readString().toString()
        qIndex = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(qIndex)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EBookPractice> {
        override fun createFromParcel(parcel: Parcel): EBookPractice {
            return EBookPractice(parcel)
        }

        override fun newArray(size: Int): Array<EBookPractice?> {
            return arrayOfNulls(size)
        }
    }
}

class QuestionP() : Parcelable {
    var questionKey = ""
    var stem = ""
    var questionType = ""
    var examType = ""//examType==常考0,压轴1,错题2,好题3,英译汉4,汉译英5 构词拼写:6 听音选释义:7 选词填空:8 听音选词:9 场景释义10
    var rightanswer = ""//正确答案order
    var rightAnswerContent = ""//正确答案content
    var resourceKey = ""
    var item = mutableListOf<QuestionInfoItem>()
    var imgUrl = ""

    constructor(parcel: Parcel) : this() {
        questionKey = parcel.readString().toString()
        stem = parcel.readString().toString()
        questionType = parcel.readString().toString()
        examType = parcel.readString().toString()
        rightanswer = parcel.readString().toString()
        resourceKey = parcel.readString().toString()
        imgUrl = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(questionKey)
        parcel.writeString(stem)
        parcel.writeString(questionType)
        parcel.writeString(examType)
        parcel.writeString(rightanswer)
        parcel.writeString(resourceKey)
        parcel.writeString(imgUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionP> {
        override fun createFromParcel(parcel: Parcel): QuestionP {
            return QuestionP(parcel)
        }

        override fun newArray(size: Int): Array<QuestionP?> {
            return arrayOfNulls(size)
        }
    }
}

/**
 * 巧记速记 提交
 */

class EBookPracticeAnswer {
    var smartBookKey = ""
    var catalogKey = ""
    var questionList = mutableListOf<EBookPracticeQuestion>()
}

class EBookPracticeQuestion {
    var appQuestionKey = ""
    var isRight = "0"
    var questionAnswer = ""
}

class ExaminationList {
    var list = mutableListOf<ExaminationListBean>()
}

class ExaminationListBean {
    //    "isBuy": "1",
    var isBuy = ""

    //    "orderKey": null,
    var orderKey = ""

    //    "buyCount": "0",
    var buyCount = ""

    //    "cuCheck": null,
    var cuCheck = ""

    //    "examEndTime": "2022-10-20 23:59",
    var examEndTime = ""

    //    "examStartTime1": null,
//    "spokenExaminationName": "四级口语模考1017(副本)",
    var spokenExaminationName = ""

    //    "groupNumber": null,
//    "spokenExaminationKey": "1647150383617182784",
    var spokenExaminationKey = ""

    //    "cuCheckOriginal": null,
    var cuCheckOriginal = ""

    //    "checkStartTime1": null,
//    "examinationNum": "20",
    var examinationNum = ""

    //    "imageUrl": "https://imgdev.sparke.cn/images/english/2022/10/1642754080862786048.jpg",
    var imageUrl = ""

    //    "examStartTime": "2022-10-20 00:00",
    var examStartTime = ""

    //    "examinationType": null,
    var examinationType = ""

    //    "state": null,
    var state = ""
//    "examEndTime1": null


}