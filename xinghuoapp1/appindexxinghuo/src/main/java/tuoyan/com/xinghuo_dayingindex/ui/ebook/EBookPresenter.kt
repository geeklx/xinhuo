package tuoyan.com.xinghuo_dayingindex.ui.ebook

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.*

/**
 * Created by Zzz on 2021/6/28
 * Email:
 */

class EBookPresenter(onProgress: OnProgress) : BasePresenter(onProgress) {
    /**
     * 点读书列表
     */
    fun getSmartBookList(onNext: (List<EBook>) -> Unit) {
        api.getSmartBookList().sub({ onNext(it.body) })
    }

    fun getEBookDetail(key: String, onNext: (EBook) -> Unit) {
        api.getSmartBookDetail(key).sub({ onNext(it.body) })
    }

    fun getSmartBookCatalogList(key: String, type: String, onNext: (List<EBookCatalog>) -> Unit) {
        api.getSmartBookCatalogList(key, type).sub({ onNext(it.body) })
    }

    fun getRefineCatalogList(key: String, bookKey: String, catalogKey: String, onNext: (List<EBookLineData>) -> Unit) {
        api.getRefineCatalogList(key, bookKey, catalogKey).sub({ onNext(it.body) })
    }

    fun getLearnStatistics(key: String, onNext: (EBookData) -> Unit) {
        api.learnStatistics(key).sub({ onNext(it.body) })
    }

    fun getLearnDurationDetail(key: String, type: String, onNext: (List<EBookLineData>) -> Unit) {
        api.learnDurationDetail(key, type).sub({ onNext(it.body) })
    }

    fun getLearnAccuracyDetail(key: String, type: String, onNext: (List<EBookLineData>) -> Unit) {
        api.learnAccuracyDetail(key, type).sub({ onNext(it.body) })
    }

    fun getRefineQuestion(key: String, userPracticeKey: String, onNext: (EBookExerciseWord) -> Unit) {
        api.refineQuestion(key, userPracticeKey).sub({ onNext(it.body) })
    }

    fun getReadList(catalogKey: String, onNext: (List<EBookImg>) -> Unit) {
        api.getReadList(catalogKey).sub({ onNext(it.body) })
    }

    fun getQuestionList(catalogKey: String, onNext: (List<EBookPractice>) -> Unit) {
        api.getQuestionList(catalogKey).sub({ onNext(it.body) })
    }

    fun questionSubmit(params: EBookPracticeAnswer, onNext: () -> Unit) {
        api.questionSubmit(params).subs({ onNext() })
    }
    fun examinationList( onNext: (ExaminationList) -> Unit) {
        api.examinationList().subs({ onNext(it.body) })
    }

}