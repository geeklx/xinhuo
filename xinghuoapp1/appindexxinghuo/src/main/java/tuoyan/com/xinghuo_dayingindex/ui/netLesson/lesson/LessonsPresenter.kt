package tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.net.Results

/**
 * Created by  on 2018/9/28.
 */
class LessonsPresenter(onProgress: OnProgress) : BasePresenter(onProgress) {
    /**
     * type	类型（1 系统课，2 公开课）
     * grade	网课学段（0 公开课，1 四六级，2 考研,3 专四专八
     */
    fun getLessonList(
        typ: String,
        grad: String,
        page: Int,
        step: Int = 10,
        onNext: (Results<List<Lesson>>) -> Unit
    ) {
//        cacheApi.getLessonList(typ, grad, page, step).sub({ onNext(it) })
        api.getLessonList(typ, grad, page, step).sub({ onNext(it) })
    }

    /**
     * 测试key：536149374675416705
     */
    fun getLessonDetail(key: String, onNext: (LessonDetail) -> Unit) {
        api.getLessonDetail(key).sub({ onNext(it.body) })
    }
    //获得口语详情
    fun getSpokenDetail(key: String, onNext: (Spoken) -> Unit) {
        api.getSpokenDetail(key).sub({ onNext(it.body) })
    }

    /**
     * 获取网课小节列表
     */
    fun getCatalogue(key: String, onNext: (Catalogue) -> Unit) {
        api.getCatalogue(key).sub({ onNext(it.body) })
    }

    /**
     * 网课免费领取
     */
    fun freeBuyNetCourse(key: String, onNext: (AssembleTeam) -> Unit) {
        api.freeBuyNetCourse(mutableMapOf("key" to key)).sub({ onNext(it.body) })
    }

    /**
     * 获取评价
     */
    fun loadComment(key: String, type: String, page: Int, onNext: (List<Comment>, Int) -> Unit) {
        api.getComments(key, type, page).sub({ onNext(it.body, it.totalCount) })
    }

    /**
     * 添加评价
     * targetkey	被评论对象的主键
     * content
     * points       星星评分
     * netcoursekey 网课key（网课小节时）/测评key
     * type         1-话题帖子 2-问答 3-资讯 4-网课 5-图书 6-网课小节 7课课练 8试题
     *
     */
    fun comment(
        key: String,
        content: String,
        points: String,
        pKey: String = "",
        type: String,
        onNext: () -> Unit
    ) {
        api.addComment(
            mutableMapOf(
                "targetkey" to key,
                "content" to content,
                "points" to points,
                "netcoursekey" to pKey,
                "type" to type
            )
        ).sub({ onNext() }) //赵志坤让改的
    }

    //网课预约
    fun addShoppingAppointment(key: String, onNext: () -> Unit) {
        api.addShoppingAppointment(
            mutableMapOf(
                "key" to key,
                "type" to "1"//1网课
            )
        ).sub({ onNext() })
    }

    //课程绑定优惠券列表
    fun netPromotional(courseKey: String, onNext: (List<Coupon>) -> Unit) {
        api.getNetPromotional(courseKey).sub({ onNext(it.body) })
    }

    //学习数据
    fun getLearnedDetail(courseKey: String, onNext: (LessonStudyData) -> Unit) {
        api.getLearnedDetail(courseKey).sub({ onNext(it.body) })
    }

    //学习数据月份
    fun getLearnYmList(courseKey: String, onNext: (List<LessonStudyMonthData>) -> Unit) {
        api.getLearnYmList(courseKey).sub({ onNext(it.body) })
    }

    //学习数据月份详情
    fun getYmDetail(courseKey: String, ym: String, onNext: (LessonStudyMonthDataDetail) -> Unit) {
        api.getYmDetail(courseKey, ym).sub({ onNext(it.body) })
    }
}