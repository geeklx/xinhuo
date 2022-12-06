package tuoyan.com.xinghuo_dayingindex.base

import tuoyan.com.xinghuo_dayingindex.bean.EBookParam
import kotlin.math.ceil


abstract class EBookLifeFullActivity<p : BasePresenter> : LifeFullActivity<p>() {
    val bookParam by lazy { intent.getSerializableExtra(EBOOK_PARAM) as? EBookParam }
    private var startTime = 0L
    override fun onResume() {
        super.onResume()
        startTime = System.currentTimeMillis()
    }

    override fun onPause() {
        val endTime = System.currentTimeMillis()
        val timeStep = ceil((endTime - startTime) / 1000.0).toString()
        if (!bookParam?.bookKey.isNullOrEmpty()) {
//            val map = HashMap<String, String>()
//            map["bussinessKey"] = bookParam?.bookKey ?: ""
//            map["time"] = timeStep
//            map["catalogKey"] = bookParam?.catalogKey ?: ""
//            map["refineKey"] = "refineKey"
//            presenter.postRecordDduration(map) {}
            postRecord(timeStep)
        }
        startTime = endTime
        super.onPause()
    }

    fun postRecord(timeStep: String, lastSource: String = "0") {
        val map = HashMap<String, String>()
        map["bussinessKey"] = bookParam?.bookKey ?: ""
        map["time"] = timeStep
        map["catalogKey"] = bookParam?.catalogKey ?: ""
        map["refineKey"] = "refineKey"
        if (lastSource == "1") {
            map["lastSource"] = lastSource
        }
        presenter.postRecordDduration(map) {}
    }

    companion object {
        const val EBOOK_PARAM = "EBookParam"
    }
}
