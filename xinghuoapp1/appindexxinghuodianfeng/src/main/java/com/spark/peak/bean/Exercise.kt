package com.spark.peak.bean

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
    var issubtitle: Int = 0
    var questionnum: Int = 0
    var questionlist: List<ExerciseFrameItem>? = null
    var remarks: String = ""
    var paperType: String = ""
    var totalCount: String = ""
}

class ExerciseFrameItem(var isNode: Boolean = false, var paperName: String = "", var groupName: String = "", var paperExplain: String = "", var remarks: String = "") : Serializable {
    var groupKey: String? = null
    var isSubtitle: String? = null
    var matpKey: String? = null
    var questionKey: String? = null
    var questionlist: List<ExerciseFrameItem>? = null
    var questionSort: String? = null
    var questionSubject: String? = null
    var questionType: String? = null
    var score: String? = null
    var sort: String? = null
    var totalCount: String? = null
    var questionInfo: Any? = null
}

/**
 * 交卷的body
 */
class AnswerSubmit(
        var paperkey: String,
        var answerlist: List<AnswerQuestion>,
        var time: String,
        var catalogkey: String,
        var practicekey: String,
        var source: String,
        var missionlevelkey: String, //关卡主键 同 父节点主键 parentKey
        var missionkey: String//任务主键 同 图书主键 bookKey
)

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
    var index: Int = 0 // 显示的题号
    var qPosition: Int = 0 //所在大题的索引，用于点击后跳转
    var mPosition: Int = 0 //所在小题的索引，用于点击后跳转
    var haveDone: Boolean = false //判断是否做过

    var sort: String = ""
    var type: String = ""
    var answers: ArrayList<AnswerItem> = ArrayList()
    var questionKey: String = ""
}

/**
 * order	int	序号
answer	string	答案

 */
class AnswerItem(
        var order: String = "", //交卷时的题号
        var answer: String? = null
)

class Report(
        var sucs: String,
        var errs: String,
        var accuracy: String,
        var answersheet: ArrayList<AnswerSheet>,
        var totalscore: String,
        var totalcount: String,
        var userpractisekey: String
) : Serializable

class AnswerSheet(
        var questionlist: List<AnswerSheet>,
        var groupName: String,
        var isRight: String,
        var questionSort: String
) : Serializable


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