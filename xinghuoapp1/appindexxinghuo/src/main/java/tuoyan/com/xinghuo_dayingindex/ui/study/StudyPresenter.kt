package tuoyan.com.xinghuo_dayingindex.ui.study

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.*

class StudyPresenter(onProgress: OnProgress) : BasePresenter(onProgress) {
    /**
     * 获取图书列表
     */
    fun getMyBooks(onError: () -> Unit = {}, onNext: (List<Book>) -> Unit) {
        api.getMyBooks().sub({ onNext(it.body) }) { onError() }
    }

    /**
     * 删除图书
     */
    fun deleteMyBooks(map: Map<String, String>, onNext: (String) -> Unit) {
        api.deleteMyBooks(map).sub({ onNext(it.msg) })
    }

    /**
     * 获取今日课程
     */
    fun getTodayLesson(onError: () -> Unit = {}, onNext: (List<TodyLesson>) -> Unit) {
        api.getTodayLesson().sub({ onNext(it.body) }) { onError() }
    }

    /**
     * 获取网课列表
     */
    fun getMyLessons(onError: () -> Unit = {}, onNext: (List<MyBookLesson>) -> Unit) {
        api.getMyLessons().sub({ onNext(it.body) }) { onError() }
    }


    /**
     * 删除网课
     */
    fun deleteMyNetClass(map: Map<String, String>, onNext: (String) -> Unit) {
        api.deleteMyNetClass(map).sub({ onNext(it.msg) })
    }

    /**
     * 激活网课
     * （网课兑换码）
     */
    fun activedNetcourse(map: Map<String, String>, onNext: () -> Unit) {
        api.postActivedNetcourse(map).subs({ onNext() })
    }

    /*
    *获取推荐网课
    * */
    fun getNetClassList(onNext: (List<Lesson>) -> Unit) {
//        cacheApi.getNetClassList("3", "1", "", "1").sub({ onNext(it.body) })
        api.getNetClassList("3", "1", "", "1").sub({ onNext(it.body) })
    }

    fun getMyLessonDetail(key: String, onNext: (LessonDetail) -> Unit, onError: () -> Unit) {
        api.getMyLessonDetail(key).subs({ onNext(it.body) }) { onError() }
    }

    fun getMySmarkBook(onNext: (List<EBook>) -> Unit) {
        api.getMySmarkBook().sub({ onNext(it.body) })
    }

    fun activedGoods(map: Map<String, String>, onNext: () -> Unit) {
        api.activedGoods(map).subs({ onNext() })
    }

}