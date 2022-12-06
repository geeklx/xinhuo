package tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.net.Results


/**
 * 创建者：
 * 时间：
 */
class OverPackagePresenter(progress: OnProgress) : BasePresenter(progress) {
    fun loadData(key: String, onNext: (Map<String,String>) -> Unit) {
//        api.getNetLesson(key).sub({ onNext(it.body) }) { }
    }

    fun getNetLessonCatalogue(s: String, onNext: (Map<String,String>) -> Unit) {
//        api.getCatalogue(s).sub({ onNext(it.body) })
    }

    fun loadComment(key: String, page: Int, onNext: (Results<List<Map<String,String>>>) -> Unit) {
//        api.getComments(key, page).sub({ onNext(it) })
    }

    fun freeBuyNetCourse(key: String, onNext: () -> Unit) {
//        api.freeBuyNetCourse(mutableMapOf("key" to key)).sub({ onNext() })
    }

    fun getDetail(key: String, onNext: () -> Unit){
        api.getPacksDetail(key).subs({ onNext() })
    }
}