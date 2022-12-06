package tuoyan.com.xinghuo_dayingindex.ui.ht

import kotlinx.android.synthetic.main.activity_no_data.*
import org.jetbrains.anko.longToast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseActivity

class ReplayHTActivity : BaseActivity() {
    override val layoutResId: Int
        get() = R.layout.activity_no_data

    override fun configView() {
        super.configView()
        longToast("由于平台升级，本视频无法观看，请移至相关课程重新下载")
        onBackPressed()
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}
//class ReplayHTActivity : PlaybackNativeActivity(), OnProgress {
//    val presenter: NetLessonsPresenter by lazy { NetLessonsPresenter(this) }
//    override val disposables: ListCompositeDisposable
//        get() = ListCompositeDisposable()
//
//    override fun showProgress() {
//    }
//
//    override fun dismissProgress() {
//    }
//
//    override fun onError(message: String) {
//    }
//
//    override fun upData(type: String) {
//        super.upData(type)
//        if (detail != null && lesson != null) {
//            val map = mutableMapOf<String, String>()
//            map["courseKey"] = "${detail.netcoursekey ?: ""}"
//            map["videoKey"] = "${lesson.periodId ?: ""}"
//            when (type) {
//                "1" -> {
//                    map["total"] = "60"
//                }
//                "2" -> {
//                    map["total"] = "60"
//                    map["schedule"] = "1"//不为空就行
//                }
//                "3" -> {
//                    map["schedule"] = "1"//不为空就行
//                }
//            }
//            presenter.recordPlayLog(map)
//        }
//    }
//
//}