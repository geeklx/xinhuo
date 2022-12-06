package tuoyan.com.xinghuo_dayingindex.ui.exercise.detail

import tuoyan.com.xinghuo_dayingindex.TYPE_TEST
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.*

class ExerciseDetailPresenter(onProgress: OnProgress) : BasePresenter(onProgress) {

    /**
     * 试卷结构及试卷详情
     * 做题时调用
     */
//    override fun getExerciseFrame(practisekey: String, ishistory: String, evalKey: String, onNext: (ExerciseFrame) -> Unit) {
//        api.getExerciseFrame(practisekey, ishistory, evalKey).subs({ onNext(it.body) })
//    }

    fun getExerciseFrame2(practisekey: String, ishistory: String, onNext: (Any) -> Unit) {
        api.getExerciseFrame2(practisekey, ishistory).subs({ onNext(it.body) })
    }

    /**
     * 试卷结构及试卷详情
     * 解析时调用
     * isError  1:错题解析
     *
     */
    fun getExerciseParsingFrame(practisekey: String, userpractisekey: String, evalKey: String = "", ishistory: String = "0", isError: String = "", onNext: (ExerciseFrame) -> Unit) {
        api.getExerciseParsingFrame(practisekey, userpractisekey, evalKey, ishistory, isError).subs({ onNext(it.body) })
    }

    /**
     * 获取题目详情
     */
    fun getExerciseDetail(
        practisekey: String, groupkey: String,
        questiontype: String, questionkey: String,
        questionsort: String, onNext: (Any) -> Unit
    ) {

        api.getExerciseDetail(
            practisekey, groupkey, questiontype,
            questionkey, questionsort
        ).sub({ onNext(it.body) })
    }

    /**
     * 获取题目解析
     */
    fun getExerciseParsing(
        practisekey: String, questionkey: String,
        questiontype: String, userpractisekey: String,
        questionsort: String, onNext: (Any) -> Unit
    ) {

        api.getExerciseParsing(
            practisekey, questionkey, questiontype,
            userpractisekey, questionsort
        ).sub({ onNext(it.body) })
    }

    /**
     * 交卷
     */
//    fun submit(answer: AnswerSubmit, type: Int = 0, onNext: (Report) -> Unit) {
//        when (type) {
////            ExerciseDetailActivity.EX_TYPE_SP -> api.exerciseSubmitSP(answer).subs({ onNext(it.body) })
//            ExerciseDetailKActivity.EX_TYPE_PG -> api.exerciseSubmitPG(answer).subs({ onNext(it.body) })
//            else -> api.exerciseSubmit(answer).subs({ onNext(it.body) })
//        }
//    }

    /**
     * 交卷
     */
    fun submitType(answer: AnswerSubmit, type: String = "1", onNext: (Report) -> Unit) {
        when (type) {
//            ExerciseDetailActivity.EX_TYPE_SP -> api.exerciseSubmitSP(answer).subs({ onNext(it.body) })
            TYPE_TEST -> api.exerciseSubmitPG(answer).subs({ onNext(it.body) })
            else -> api.exerciseSubmit(answer).subs({ onNext(it.body) })
        }
    }


    /**
     * 题目反馈
     */
    fun questionFB(map: Map<String, String>, onNext: (Any) -> Unit) {
        api.questionFB(map).sub({ onNext(it) })
    }

    ///////////////////专项练习/////////////////////////////////////////////////

    /**
     * 获取专项练习题目详情
     * 所有题目的完整列表
     */
    fun getSpQuestions(practisekey: String, groupkey: String, paperType: String, onNext: (List<ExerciseFrameItem>) -> Unit) {
        api.getSpQuestions(practisekey, groupkey, paperType).sub({ onNext(it.body) })
    }

    /**
     * 专项解析
     */
    fun getSpAnalyzes(paperkey: String, matpkey: String, userpractisekey: String, onNext: (ExerciseFrameItem) -> Unit) {
        api.getSpAnalyzes(paperkey, matpkey, userpractisekey).sub({ onNext(it.body) })
    }

    /**
     * 获取历史习题练习报告
     */
//    fun getReport(paperkey: String, matpkey: String, userpractisekey: String, onNext: (Report) -> Unit) {
//        api.installAnswercark(paperkey, matpkey, userpractisekey).sub({ onNext(it.body) })
//    }

    fun getRefineQuestion(key: String, userPracticeKey: String, onNext: (EBookExerciseWord) -> Unit) {
        api.refineQuestion(key, userPracticeKey).sub({ onNext(it.body) })
    }

    fun getRefineListeningQuestion(paperKey: String, userPracticeKey: String, questionKey: String, questionType: String, onNext: (QuestionInfo) -> Unit) {
        api.refineListeningQuestion(paperKey, userPracticeKey, questionKey, questionType).sub({ onNext(it.body) })
    }

    fun postLockPaper(map: Map<String, String>, onNext: (Any) -> Unit) {
        api.lockPaper(map).sub({ onNext(it) })
    }

    fun paperQuestions(catalogKey: String, onNext: (ExerciseFrame) -> Unit) {
        api.paperQuestions(catalogKey).subs({ onNext(it.body) })
    }
}