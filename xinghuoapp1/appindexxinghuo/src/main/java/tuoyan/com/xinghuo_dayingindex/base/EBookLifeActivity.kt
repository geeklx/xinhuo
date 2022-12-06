package tuoyan.com.xinghuo_dayingindex.base

import tuoyan.com.xinghuo_dayingindex.bean.EBookParam
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookExerciseActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookListenActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookListenExerciseActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookWordsActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.EBookWordListActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordExerciseActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordPracticeActivity
import kotlin.math.ceil


abstract class EBookLifeActivity<p : BasePresenter> : LifeActivity<p>() {
    val bookParam by lazy { intent.getSerializableExtra(EBOOK_PARAM) as? EBookParam }
    private var startTime = 0L
    override fun onResume() {
        super.onResume()
        startTime = System.currentTimeMillis()
    }

    override fun onPause() {
        val endTime = System.currentTimeMillis()
        if (!bookParam?.bookKey.isNullOrEmpty()) {
            val map = HashMap<String, String>()
            map["bussinessKey"] = bookParam?.bookKey ?: ""
            map["time"] = ceil((endTime - startTime) / 1000.0).toString()
            map["catalogKey"] = bookParam?.catalogKey ?: ""
            when (this) {
                is EBookExerciseActivity, is EBookListenExerciseActivity -> {
                    map["refineKey"] = bookParam?.resourceKey ?: ""//模考key
                }
                is EBookWordsActivity -> {
                    map["refineKey"] = bookParam?.wordKey ?: ""//精练key
                    map["lastSource"] = "10"//精练学习环节：10词汇扫盲  10.1词汇学习 、11听力练习
                }
                is EBookWordListActivity -> {
                    map["refineKey"] = bookParam?.wordKey ?: ""//精练key
                    map["lastSource"] = "10.1"//精练学习环节：10词汇扫盲  10.1词汇学习 、11听力练习
                }
                is EBookListenActivity -> {
                    map["refineKey"] = bookParam?.wordKey ?: ""//精练key
                    map["lastSource"] = "11"//精练学习环节：10词汇扫盲  10.1词汇学习 、11听力练习
                }
                is EBookWordPracticeActivity, is EBookWordExerciseActivity -> {
                    map["refineKey"] = "refineKey"
                }
            }
            presenter.postRecordDduration(map) {}
        }
        startTime = endTime
        super.onPause()
    }

    companion object {
        val EBOOK_PARAM = "EBookParam"
    }
}
