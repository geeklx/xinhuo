package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson

import android.util.Log
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.*


/**
 * 创建者：
 * 时间：
 */
class NetLessonsPresenter(progress: OnProgress) : BasePresenter(progress) {

    /**
     * 获取购买后网课详情
     */
    fun getMyLessonDetail(key: String, onNext: (LessonDetail) -> Unit) {
        api.getMyLessonDetail(key).subs({ onNext(it.body) })
    }

    /**
     * 获取网课小节列表
     */
    fun getCatalogue(key: String, onNext: (List<ResourceListBean>) -> Unit) {
        api.getCatalogue(key).sub({ onNext(it.body.resouceList) })
    }

    /**
     * 课后作业
     */
    fun getWorks(
        key: String,
        id: String,
        page: Int,
        step: Int = 100,
        onNext: (List<ClassWork>) -> Unit
    ) {
        api.getWorks(key, id, page, step).subs({ onNext(it.body) })
    }

    /**
     * 网课小节观看记录
     * 网课小节观看时长
     * courseKey
     * videoKey
     * startTime @Deprecated("")
     * endTime @Deprecated("")
     * schedule 进度 不为空表示已经看完  进度>=95%
     * total 时长 >=90%
     */
    fun recordPlayLog(map: Map<String, String>) {
        Log.d("recordPlayLog", "recordPlayLog:courseKey "+map.get("courseKey")+"videoKey"+map.get("videoKey"))
        api.recordPlayLog(map).sub()
    }

    /**
     * 获取直播课/录播课列表 --- 2019/4/24 大英网课UI修改
     * 回放数据不取
     */
    fun getLiveCatalogue(key: String, form: String, onNext: (LiveListBean) -> Unit) {
        api.getLiveCatalogue(key, form).subs({ onNext(it.body) })
    }

    /**
     * 获取回放数据列表 --- 2020年7月27日
     * 参数：网课courseKey   type（0按照时间排序展示，1按照目录展示）
     */
    fun getPlaybackListByPackage(courseKey: String, onNext: (PlayBackBean) -> Unit) {
        api.getPlaybackListByPackage(courseKey, "1").subs({ onNext(it.body) })
    }

    /**
     * 获取回放数据列表 --- 2020年7月27日
     * 参数：网课courseKey   type（0按照时间排序展示，1按照目录展示）
     */
    fun getPlaybackListByTime(courseKey: String, onNext: (PlayBackBean) -> Unit) {
        api.getPlaybackListByTime(courseKey, "0").subs({ onNext(it.body) })
    }

    /**
     *获却网课预习列表
     * 参数courseKey网课主键，vidoKey网课小节主键
     */
    fun getPrepareList(courseKey: String, videoKey: String, onNext: (List<BookRes>) -> Unit) {
        api.getPrepareList(courseKey, videoKey).sub({ onNext(it.body) })
    }

    /**
     *记录网课预习接口 get ，参数courseKey网课主键，vidoKey网课小节主键，courseResourceKey预习主键
     */
    fun getLearnedPrepare(courseKey: String, vidoKey: String, courseResourceKey: String, onNext: () -> Unit) {
        api.getLearnedPrepare(courseKey, vidoKey, courseResourceKey).sub({ onNext() })
    }

    /**
     * 每日任务接口 netcourse/getEverydayTask get ,参数courseKey
     */
    fun getEverydayTask(courseKey: String, onNext: (DayWork) -> Unit) {
        api.getEverydayTask(courseKey).sub({ onNext(it.body) })
    }
    fun getLiveShopping( onNext: (LiveShopping) -> Unit) {
        api.getLiveShopping().sub({ onNext(it.body) })
    }

    fun getLivePromotionals( onNext: (List<Coupon>) -> Unit) {
        api.getLivePromotionals().sub({ onNext(it.body) })
    }

    fun couponDetail(key: String,fromType:String, onNext: (Coupon) -> Unit) {
        api.couponDetail(key,fromType).sub({ onNext(it.body) })
    }
    fun recordLiveScore(map: Map<String, String>, onNext: (Any) -> Unit) {
        api.recordLiveScore(map).sub({ onNext(it) })
    }
    fun getLivePop(popKey: String, type: String, onNext: (LivePop) -> Unit) {
        api.getLivePop(popKey,type).sub({ onNext(it.body) })
    }
    fun getLiveBackPopList(videoKey: String, onNext: (List<LiveBackPop>) -> Unit) {
        api.getLiveBackPopList(videoKey).sub({ onNext(it.body) })
    }
}