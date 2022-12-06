package tuoyan.com.xinghuo_dayingindex.ui.home.word.words
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_scan_words.*
import tuoyan.com.xinghuo_dayingindex.*
import tuoyan.com.xinghuo_dayingindex.base.LifeFullActivity
import tuoyan.com.xinghuo_dayingindex.bean.WordCatalog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.GradeDialog
import tuoyan.com.xinghuo_dayingindex.ui.home.word.WordPresenter
import tuoyan.com.xinghuo_dayingindex.ui.home.word.adapter.ScanWordsAdapter

/**
 * 扫码单词
 */
class ScanWordsActivity : LifeFullActivity<WordPresenter>() {
    override val layoutResId: Int
        get() = R.layout.activity_scan_words
    override val presenter: WordPresenter
        get() = WordPresenter(this)

    private var gradeKey: String? = null
    private val dialog by lazy {
        GradeDialog("四级", "六级", "", "", "", this) {
            tv_section.text = it
            when (it) {
                "四级" -> {
                    gradeKey = GRAD_KEY_CET4
                    getWordHome()
                }
                "六级" -> {
                    gradeKey = GRAD_KEY_CET6
                    getWordHome()
                }
                "考研" -> {
                    gradeKey = GRAD_KEY_YAN
                    getWordHome()
                }
                "专四" -> {
                    gradeKey = GRAD_KEY_TEM4
                    getWordHome()
                }
                "专八" -> {
                    gradeKey = GRAD_KEY_TEM8
                    getWordHome()
                }
            }
        }
    }
    private val wordsAdapter by lazy {
        ScanWordsAdapter { type, item ->
            when (type) {
                1 -> startActivity<ScanWordListActivity>(
                    ScanWordListActivity.CLASSIFY_KEY to item.wordClassifyKey,
                    ScanWordListActivity.GRADE_KEY to gradeKey,
                    ScanWordListActivity.TITLE to item.wordClassifyName.split("(")[0]
                )
                2 -> addAndDelCollect(item)
            }

        }
    }

    override fun configView() {
        super.configView()
        rlv.layoutManager = LinearLayoutManager(this)
        rlv.adapter = wordsAdapter
        gradeKey = intent.getStringExtra(GRADE_KEY)
        tv_section.text = if (gradeKey == GRAD_KEY_CET4) "四级" else "六级"
    }

    override fun onResume() {
        super.onResume()
        getWordHome()
    }

    private fun getWordHome() {
        presenter.wordHome(gradeKey ?: "", "3") {
            wordsAdapter.setData(it.list)
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        tv_section.setOnClickListener {
            dialog.show()
        }
    }

    companion object {
        val GRADE_KEY = "gradeKey"
    }

    private fun addAndDelCollect(item: WordCatalog) {
        if (item.isCollection == "1") {
            presenter.delCollection(item.wordClassifyKey) {
                item.isCollection = "0"
                wordsAdapter.notifyDataSetChanged()
            }
        } else {
            presenter.addCollection(item.wordClassifyKey) {
                item.isCollection = "1"
                wordsAdapter.notifyDataSetChanged()
            }
        }
    }
}