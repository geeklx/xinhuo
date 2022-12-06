package tuoyan.com.xinghuo_dayingindex.ui.ht

import kotlinx.android.synthetic.main.activity_no_data.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseActivity

class LiveHTActivity : BaseActivity() {
    override val layoutResId: Int
        get() = R.layout.activity_no_data

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}
//class LiveHTActivity : LiveNativeActivity(), OnProgress {
//    val presenter: NetLessonsPresenter by lazy { NetLessonsPresenter(this) }
//    override val disposables: ListCompositeDisposable
//        get() = ListCompositeDisposable()
//
//    private var startTime = 0L
//    override fun showProgress() {
//    }
//
//    override fun dismissProgress() {
//    }
//
//    override fun onError(message: String) {
//    }
//
//    override fun upData() {
//        super.upData()
//        val map = mutableMapOf<String, String>()
//        map["courseKey"] = "${detail.netcoursekey}"
//        map["videoKey"] = "${lesson.periodId}"
//        map["total"] = "60"
//        presenter.recordPlayLog(map)
//    }
//}