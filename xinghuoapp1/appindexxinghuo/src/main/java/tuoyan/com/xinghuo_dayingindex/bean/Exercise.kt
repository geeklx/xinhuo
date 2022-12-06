package tuoyan.com.xinghuo_dayingindex.bean

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable


class Version {
    var key: String? = null
    var name: String? = null
    var order: String? = null
}

class ExBooks {
    var key: String? = null
    var name: String? = null
    var order: Int? = null
    var img: String? = null
}

class ExerciseHistory {
    var paperkey: String? = null
    var score: String? = null
    var createtime: String? = null
    var time: String? = null
    var practicekey: String? = null
    var userpractisekey: String? = null
    var gradeName: String? = null
    var subjectName: String? = null
    var editionName: String? = null
    var paperName: String? = null
    var catalogKey: String? = null
}

class ExerciseBody {
    var list: List<Exercise>? = null
}

class Exercise {
    var catalogKey: String? = null
    var catalogList: List<Exercise>? = null
    var catalogName: String? = null
    var floor: String? = null
    var parentKey: String? = null

    //    var resourceList: List<> ?= null
    var score: String? = null
    var sort: String? = null
    var total: String? = null
    var foreignKey: String? = null
    var time: String? = null
}

class ExerciseFrame {
    var issubtitle = ""
    var questionnum: Int = 0
    var questionlist: List<ExerciseFrameItem>? = null
    var totalCount = ""
    var evalMode = ""
    var remarks: String = ""
    var sourcePaperKey: String = ""//虚拟试卷key
    var paperKey: String = ""//真正的试卷key
    var gradeName: String = ""//学段
    var scoreSwitch: String = ""//是否有分支

    //点读书添加
    var lastQuestionKey: String = ""//
    var paperLrcUrl: String = ""//听力材料题总的lrc文件
    var paperResourceKey: String = ""//听力材料题总的音频文件key
    var lastAudioTime: Long = 0L//当前考试的播放时间
}

class ExerciseFrameItem(var isNode: Boolean = false, var paperName: String = "", var groupName: String = "", var paperExplain: String = "", var remarks: String = "") : Serializable {
    var groupKey: String? = null
    var isSubtitle: String = ""//2包含大题，1仅包含小题
    var matpKey: String? = null
    var questionKey: String = ""
    var questionlist: List<ExerciseFrameItem>? = null
    var questionSort: String? = null
    var questionSubject: String? = null
    var questionType: String? = null
    var score: String? = null
    var sort: String? = null
    var totalCount: String? = null
    var questionInfo: QuestionInfo? = null
    var userPracticeKey: String? = null
    var isComment: String = ""
    var isCollected: String? = null //是否收藏，1是 0否
    var isRight: String? = null //专项练习-主观题-历史选择情况（我答对了 & 我答错了）
}

//====================================questionInfo的结构===================================================================
class QuestionInfo : Serializable {
    var item: Any? = null //TODO 单选时为QuestionInfoItem>list结构，材料题时为QuestionInfo>object结构
    var sort: String = ""
    var answerText: String = ""
    var questionType: String = ""
    var type: String = ""
    var isOwn: String = ""//0:未购买；1：已购买
    var isFree: String = ""//0:收费；1：免费  主观题
    var evalMode: String = "0"// 0自判 ,1免费人工, 2付费人工, 10自判+免费人工,20自判+付费人工
    var stem: String = ""
    var isComment: String = ""
    var audioFile: String = ""
    var resourceKey: String = ""
    var teacherGrade: String = ""
    var isright: String = ""
    var analyze: String = ""
    var useranswer: String = ""
    var totalCount: String = ""
    var teacherRemark: String = ""
    var groupKey: String = ""
    var appQuestionKey: String = ""
    var score: String = ""
    var questionSort: String = ""
    var userPracticeKey: String = ""
    var questionKey: String = ""
    var questionSubject: String = ""
    var paperType: String = ""
    var questionInstructions: String = ""
    var paperTotalCount: String = ""
    var groupName: String = ""
    var isRight: String = ""
    var userAnswer: String = ""
    var paperExplain: String = ""
    var isCollected: String = ""
    var subiList: List<SubiListBean>? = null //TODO 自主判卷时的判卷维度
    var imgList: List<String>? = null //图片
    var correctList: List<CorrectBean>? = null //图片
    var imgKeyList: List<CorrectBean>? = null //图片

    //点读书添加字段
    var lrcUrl: String = "" //字幕文件
    var anylizeVideoKey: String = "" //视频解析
    var answerTime: String = "" //答题时长
    var avgTime: String = "" //平均时长
    var avgAccuracy: String = "" //平均正确率
    var rightanswer: String = "" //
    var paraphrase: String = "" //单词释义
    var lastQuestionKey: String = "" //

    //点读书二期添加字段
    var materialTitle: String? = null//材料题标题
    var listeningText: String? = null//听力原文
    var preAuditoryPrediction: String? = null//听前预测

    //巧记速记添加字段
    var boSource=""//题来源
    var imgUrl=""//单词图片
    var examType=""//examType==常考0,压轴1,错题2,好题3,英译汉4,汉译英5 构词拼写:6 听音选释义:7 选词填空:8 听音选词:9 场景释义10
}

class QuestionInfoItem() : Parcelable {
    var order: String = ""
    var content: String = ""
    var imgs: String = ""
    var fitbkey: String = "" //填空题时存在
    var type: String = "0" //1:我不确定
    var isAnswer: String = "0"//默认0：未选择；1：正确答案；2：我选择的错误答案

    constructor(parcel: Parcel) : this() {
        order = parcel.readString().toString()
        content = parcel.readString().toString()
        imgs = parcel.readString().toString()
        fitbkey = parcel.readString().toString()
        type = parcel.readString().toString()
        isAnswer = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(order)
        parcel.writeString(content)
        parcel.writeString(imgs)
        parcel.writeString(fitbkey)
        parcel.writeString(type)
        parcel.writeString(isAnswer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionInfoItem> {
        override fun createFromParcel(parcel: Parcel): QuestionInfoItem {
            return QuestionInfoItem(parcel)
        }

        override fun newArray(size: Int): Array<QuestionInfoItem?> {
            return arrayOfNulls(size)
        }
    }
}

class SubiListBean : Serializable {
    var levelList: List<LevelListBean>? = null
    var name: String = ""
    var key: String = ""
}

class LevelListBean(var type: String = "NODE") : Serializable {
    var levelComment: String = ""
    var levelKey: String = ""
    var levelName: String = ""
    var levelPoint: String = ""

    var subName = "" //TODO 维度外层的name
    var subKey = "" //TODO 维度外层的key
}
//====================================questionInfo的结构===================================================================
/**
 * 线下录题时使用的结构
 */
class ExerciseModel(
    var itemType: String = "",
    var nodeName: String = ""
) : Serializable {
    var sort: String = ""
    var type: String = ""
    var answers: ArrayList<AnswerItem> = ArrayList()
    var imgs: ArrayList<String> = ArrayList()
    var questionInfo: QuestionInfo? = null
    var state = ""
    var evalMode = "0"
    var imgPaths: ArrayList<String> = ArrayList()
    var userAnswer = ""
    var questionKey = ""
    var isSubtitle = ""
    var index = 0
    var parentType = ""
}

/**
 * 线下录题时 交卷的body
 */
class AnswerSubmitPG(
    var paperkey: String,
    var answerlist: List<ExerciseModel>,
    var time: String,
    var catalogkey: String,
    var source: String,
    var isTeacherEvaluate: String = "" //TODO 阅卷方式，过级包测评时存在 0试卷自评，1教师批改
)

/**
 * 交卷的body
 */
class AnswerSubmit {
    var paperkey: String = ""
    var answerlist = ArrayList<AnswerQuestion>()
    var time: String = "0"
    var catalogkey: String = ""
    var practicekey: String = ""
    var source: String = ""//"ZXLX":专项练习,现在没了  "STLX":
    var missionlevelkey: String = "" //关卡主键 同 父节点主键 parentKey
    var missionkey: String = ""//任务主键 同 图书主键 bookKey
    var matpkey: String = "" //材料题id，专项练习时存在
    var groupkey: String = "" // 大题id，专项练习时存在
    var isTeacherEvaluate: String = "" //TODO 阅卷方式，过级包测评时存在 0试卷自评，1教师批改
    var sourcePaperKey: String = ""
    var netCourseKey: String = ""//网课key 网课跳测评时需要传入，用于统计当前做题状态
    var netCourseVideoKey: String = ""//小节key 网课跳测评时需要传入，用于统计当前做题状态
    var netCourseResourceKey: String = ""//网课预习key

    //点读书添加
    var lastQuestionKey: String = ""//退出时 所在位置的key
    var userPractiseKey: String = ""//
    var submitType: String = ""//0:保存;1:提交;
}

/**
 * 用于构建显示答题卡列表的二级结构
 */
class Answer(
    var qName: String,
    var qList: List<AnswerQuestion>
)

/**
 * sort	int	题号
answers	list	小题答案列表

type	string	题目类型

 */
class AnswerQuestion {
    //==========交卷要的结构 ↓↓↓========
    var answers: ArrayList<AnswerItem> = ArrayList()
    var sort: String = ""
    var type: String = ""
    var imgs: ArrayList<String> = ArrayList()
    var evalMode: String = "0"//0：自测；1：人工
    var answerType: String = ""//1:图片作答；2：文字作答
    var answerText: String = ""

    //==========交卷要的结构 ⬆⬆⬆⬆⬆⬆========
    var index: Int = 0 // 显示的题号
    var qPosition: Int = 0 //所在大题的索引在viewpager中的位置，用于点击后跳转
    var mPosition: Int = 0 //所在小题在webview中索引，用于点击后跳转
    var haveDone: Boolean = false //判断是否做过
    var itemType: String = "NODE" //TODO 线下录题 显示区分
    var nodeName: String = "NODE" //TODO 线下录题 显示区分
    var questionKey: String = ""
    var parentType: String = ""
    var time: String = ""//每个小题的答题时长
}


/**
 * order	int	序号
answer	string	答案

 */
class AnswerItem(
    var order: String = "", //交卷时的题号
    var answer: String = "",
    var score: String = ""
) : Serializable

class Report(
    var sucs: String,
    var errs: String,
    var accuracy: String,//正确率
    var answersheet: ArrayList<AnswerSheet>,
    var totalscore: String,//得分
    var totalcount: Int,
    var userpractisekey: String
) : Serializable {
    var scoreSwitch = ""
    var gradeName = ""
    var paperScore = ""//试卷总分
    var beatRate = ""//击败考生
    var time = ""
}

class AnswerSheet() : Serializable {
    constructor(questionlist: List<AnswerSheet>, groupName: String, isRight: String, questionSort: String) : this() {
        this.questionlist = questionlist as ArrayList<AnswerSheet>
        this.groupName = groupName
        this.questionSort = questionSort
    }

    var questionlist = ArrayList<AnswerSheet>()
    var groupName: String = ""
    var isRight: String = ""
    var questionSort: String = ""
    var userAnswer: String? = null
    var score: String? = null
    var questionType: String? = null
    var isSubtitle = ""
    var index: Int = 0
    var questionKey = ""
}


class WrongBook(
    var paperkey: String = "",
    var errorkey: String = "",
    var questionsubject: String = "", //题干
    var subjectname: String = "", //科目或试卷分类
    var time: String = "",
    var questionkey: String = "",
    var userpracticekey: String = "",
    var sort: String = "",
    var questiontype: String = ""
) : Serializable

class WrongBookDate(var time: String? = null, var count: Int = 0)

//////////////////////////专项练习///////////////////////////////////////

class SPAnalyzes(
    var questionInfo: Any
)

class EBookExerciseWord {
    var lastQuestionKey: String = ""
    var qestionList: ArrayList<QuestionInfo> = ArrayList()
}

class MatcherBean() {
    var parentStr: String? = null//被匹配的文本
    var patternStr: String? = null//匹配到的文本
    var startIndex = 0//匹配到文本的开始位置
    var isClick = false//是否点击
}
