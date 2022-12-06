package tuoyan.com.xinghuo_dayingindex.ui.home.word

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.net.Results

/**
 * 创建者：
 * 时间：
 */
class WordPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun wordHome(gradekey: String, fType: String = "", onNext: (WordHome) -> Unit) {
        api.wordHome(gradekey, fType).sub(onNext = { onNext(it.body) })
    }

    fun catalogList(classifyKey: String, onNext: (Results<List<ClassifyList>>) -> Unit) {
        api.catalogList(classifyKey).sub(onNext = { onNext(it) })
    }

    fun getWordsByCatalogkey(catalogKey: String, isLinkQuestion: Int, onNext: (LearnStatus) -> Unit) {
        api.getWordsByCatalogkey(catalogKey, isLinkQuestion = isLinkQuestion).sub(onNext = { onNext(it.body) })
    }

    fun scanWordByKey(catalogKey: String, type: String = "3", onNext: (ScanWord) -> Unit) {
        api.getWordsByCatalogkey(catalogKey, type).sub(onNext = { onNext(it.body) })
    }

    fun getWrongWord(
        gradeKey: String, page: Int,
        onNext: (Results<List<WordsByCatalogkey>>) -> Unit
    ) {
        api.getWrongWord(gradeKey, page).sub(onNext = { onNext(it) })
    }

    fun recordWrongWord(map: Map<String, String>, onNext: () -> Unit) {
        api.recordWrongWord(map).sub(onNext = { onNext() })
    }

    fun deleteWrongWord(key: String, onNext: () -> Unit) {
        api.deleteWrongWord(key).sub(onNext = { onNext() })
    }

    fun recordNewWord(map: Map<String, String>, onNext: (Results<Any>) -> Unit) {
        api.recordNewWord(map).sub(onNext = { onNext(it) })
    }

    fun getWordDetail(key: String, onNext: (WordDetail) -> Unit) {
        api.getWordDetail(key).sub(onNext = { onNext(it.body) })
    }

    fun getNewWord(
        gradeKey: String, page: Int, fType: String = "",
        onNext: (Results<List<WordsByCatalogkey>>) -> Unit
    ) {
        api.getNewWord(gradeKey, page, fType).sub(onNext = { onNext(it) })
    }

    fun deleteNewWord(key: String, onNext: () -> Unit) {
        api.deleteNewWord(key).sub(onNext = { onNext() })
    }

    fun learnSubmit(map: Map<String, String>, onNext: (Map<String, String>) -> Unit) {
        api.learnSubmit(map).sub(onNext = { onNext(it.body) })
    }

    fun reviewSubmit(map: Map<String, String>, onNext: (Map<String, String>) -> Unit) {
        api.reviewSubmit(map).sub(onNext = { onNext(it.body) })
    }

    fun wordCatalogInfo(classifyKey: String, onNext: (ScanWord) -> Unit) {
        api.wordCatalogInfo(classifyKey).sub(onNext = { onNext(it.body) })
    }

    fun wordRecord(classifyKey: String, catalogKey: String, wordKey: String, onNext: () -> Unit) {
        api.wordRecord(classifyKey, catalogKey, wordKey).sub(onNext = { onNext() })
    }

    fun addCollection(targetkey: String, onNext: () -> Unit) {
        val map = mutableMapOf<String, String>()
        map["targetkey"] = targetkey
        map["type"] = "6"
        map["source"] = "3"
        api.addCollection(map).sub(onNext = { onNext() })
    }

    fun delCollection(targetkey: String, onNext: () -> Unit) {
        val map = mutableMapOf<String, String>()
        map["targetkey"] = targetkey
        api.deleteCollection(map).sub(onNext = { onNext() })
    }
}