package tuoyan.com.xinghuo_dayingindex.ui.mine.collection

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.Audio
import tuoyan.com.xinghuo_dayingindex.bean.Paper
import tuoyan.com.xinghuo_dayingindex.bean.WordHome
import tuoyan.com.xinghuo_dayingindex.net.Results

/**
 * 创建者：
 * 时间：
 */
class CollectionPresenter(progress: OnProgress) : BasePresenter(progress) {

    fun collectionPaper(page: Int, onNext: (Results<List<Paper>>) -> Unit) {
        api.collectionPaper(page).sub(onNext = { onNext(it) })
    }

    fun questionAnalyze(questionkey: String, questiontype: String, userPracticeKey: String = "", paperKey: String = "", onNext: (Any) -> Unit) {
        api.questionAnalyze(questionkey, questiontype, userPracticeKey, paperKey).sub(onNext = { onNext(it.body) })
    }

    fun collectionAvdio(page: Int, onNext: (Results<List<Audio>>) -> Unit) {
        api.collectionAvdio(page).sub(onNext = { onNext(it) })
    }

    fun collectedCount(type: String, source: String = "3", onNext: (Map<String, String>) -> Unit) {
        api.collectedCount(type, source).sub(onNext = { onNext(it.body) })
    }

    fun collectedClassifyList(onNext: (WordHome) -> Unit) {
        api.collectedClassifyList().sub(onNext = { onNext(it.body) })
    }

    fun delCollection(targetkey: String, onNext: () -> Unit) {
        val map = mutableMapOf<String, String>()
        map["targetkey"] = targetkey
        api.deleteCollection(map).sub(onNext = { onNext() })
    }
}