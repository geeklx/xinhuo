package tuoyan.com.xinghuo_dayingindex.bean

/**
 * 做题模块的bean
 */
class SpecialInfos {
    var practiseKey: String = ""
    var groupList: List<SpecialGroup>? = null
    var specialList: List<SpecialExplain>? = null
}

class SpecialGroup(
    var groupName: String,
    var total: String,
    var catalogKey: String,
    var answerTotal: String,
    var groupKey: String
)

class SpecialExplain(
    var title: String,
    var key: String,
    var content: String,
    var pv: String
)


class RealListItem(
    var paperKey: String,
    var score: String,
    var year: String,
    var paperName: String,
    var totalCount: String,
    var showYear: Boolean,
    var practiseCount: String
) {
    var userPracticeKey = ""//保存做题记录
    var isFinish=""//1;完成
}
