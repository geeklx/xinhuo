package tuoyan.com.xinghuo_dayingindex.ui.home

import android.util.Log
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

/**
 * Created by  on 2018/9/18.
 */
class HomePresenter(onProgress: OnProgress) : BasePresenter(onProgress) {

    fun getHomeData(gradeKey: String, onNext: (HomeData) -> Unit, onError: () -> Unit) {
//        cacheApi.homePageInfoN(gradeKey, "2").sub({ onNext(it.body) })
        api.homePageInfoN(gradeKey, "2").sub({ onNext(it.body) }) {
            onError()
        }
    }

    /**
     * 获取首页广告位 或 推荐图书列表
     *  首页banner	sybn
    图书精选	tsjx
    闪屏	spgg
     */
    fun getBanner(adspace: String, page: Int, step: Int, onNext: (List<Advert>) -> Unit) {
        api.getAdvs(adspace, page, step,"1").sub({
            onNext(it.body)
        })
    }

    fun login(login: Map<String, String>, onNext: (Int) -> Unit, onError: () -> Unit) {
        api.login(login).sub({ it ->
            if (it.body == null) {
                onNext(it.ret)
                return@sub
            }
            val results = it
            SpUtil.user = it.body ?: LoginResponse()
            Log.e("fuck-you", "newToken---" + it.body.token)

            userInfo {
                SpUtil.userInfo = it
                SpUtil.isLogin = true
                if (SpUtil.userInfo.grade.isNullOrBlank())
                    changeUserInfo(mutableMapOf("grade" to SpUtil.defaultGrade.id)) {
                        onNext(results.ret)
                    }
                else
                    onNext(results.ret)
            }
        }) { onError() }
    }

    fun getLevelByGrade(key: String, onNext: (List<Level>) -> Unit) {
        api.getLevelByGrade(key, "BOOK").sub({ onNext(it.body) })
    }

    fun getSpecialNet(specialKey: String, onNext: (List<Lesson>) -> Unit) {
        api.getSpecialNet(specialKey).sub({ onNext(it.body) })
    }

    fun getHomeNets(key: String, onNext: (List<Lesson>) -> Unit) {
        api.getHomeNets(key).sub({ onNext(it.body) })
    }

    fun getHomeNets(key: String, gradeKey: String, onNext: (List<Lesson>) -> Unit) {
        api.getHomeNets(key, gradeKey).sub({ onNext(it.body) })
    }

    fun getHomeApplets(key: String, onNext: (List<Grade>) -> Unit) {
        api.getHomeApplets(key).sub({ onNext(it.body) })
    }
}