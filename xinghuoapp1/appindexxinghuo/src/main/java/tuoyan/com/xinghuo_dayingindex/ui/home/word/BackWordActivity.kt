package tuoyan.com.xinghuo_dayingindex.ui.home.word
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_back_word.*
import org.jetbrains.anko.ctx
import tuoyan.com.xinghuo_dayingindex.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.WordHome
import tuoyan.com.xinghuo_dayingindex.ui.dialog.GradeDialog
import tuoyan.com.xinghuo_dayingindex.ui.home.word.common.CommonWordActivity
import tuoyan.com.xinghuo_dayingindex.ui.home.word.core.CoreWordActivity
import tuoyan.com.xinghuo_dayingindex.ui.home.word.words.WordsActivity
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

class BackWordActivity : LifeActivity<WordPresenter>() {
    override val presenter = WordPresenter(this)
    override val layoutResId = R.layout.activity_back_word
    private var gradeKey: String? = null
    private val adapter by lazy {
        CommonAdapter {
            // : 2018/10/12 9:59  常用词汇item点击回调
            startActivity<CommonWordActivity>(CommonWordActivity.KEY to it.catalogKey,
                    CommonWordActivity.GRADE_KEY to gradeKey,
                    CommonWordActivity.TITLE to it.catalogName)
        }
    }
    private val dialog by lazy {
        GradeDialog("四级", "六级", "", "", "", this) {
            tv_title.text = it
            when (it) {
                "四级" -> {
                    gradeKey = GRAD_KEY_CET4
                    SpUtil.defaultSection = GRAD_KEY_CET4
                    loadData()
                }
                "六级" -> {
                    gradeKey = GRAD_KEY_CET6
                    SpUtil.defaultSection = GRAD_KEY_CET6
                    loadData()
                }
                "考研" -> {
                    gradeKey = GRAD_KEY_YAN
                    loadData()
                }
                "专四" -> {
                    gradeKey = GRAD_KEY_TEM4
                    loadData()
                }
                "专八" -> {
                    gradeKey = GRAD_KEY_TEM8
                    loadData()
                }
            }
        }
    }
    private var wordHome: WordHome? = null

    companion object {
        val GRADE_KEY = "gradeKey"
    }

    override fun configView() {
        gradeKey = intent.getStringExtra(GRADE_KEY)
        if (gradeKey == null) gradeKey = SpUtil.defaultSection
        tv_title.text = if (gradeKey == GRAD_KEY_CET4) "四级" else "六级"
        recycler_view.layoutManager = LinearLayoutManager(ctx)
        recycler_view.adapter = adapter
    }

    override fun refresh() {
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        presenter.wordHome(gradeKey ?: "") {
            wordHome = it
            adapter.setData(it.catalogList)
            tv_core.text = "核心词汇（${it.coreCount ?: "0"}）"
            tv_wrong.text = "错词本（${it.wrongWordCount ?: "0"}）"
            tv_new_word.text = "生词本（${it.newWordCount ?: "0"}）"
        }
    }

    @SensorsDataTrackViewOnClick
    fun grade(v: View) {
        dialog.show()
    }

    @SensorsDataTrackViewOnClick
    fun core(v: View) {
        wordHome?.coreKey?.let {
            startActivity<CoreWordActivity>(CoreWordActivity.KEY to it, CoreWordActivity.GRADE_KEY to gradeKey)
        }
    }

    @SensorsDataTrackViewOnClick
    fun common(v: View) {
        if (tv_common.isSelected) {
            tv_common.isSelected = !tv_common.isSelected
            recycler_view.visibility = View.GONE

        } else {
            tv_common.isSelected = !tv_common.isSelected

            recycler_view.visibility = View.VISIBLE
        }
    }

    @SensorsDataTrackViewOnClick
    fun wrong(v: View) {
        startActivity<WordsActivity>(WordsActivity.IS_WRONG to true,
                WordsActivity.GRADE_KEY to gradeKey,
                WordsActivity.TITLE to "错词本")

    }

    @SensorsDataTrackViewOnClick
    fun newWord(v: View) {
        startActivity<CommonWordActivity>(
                CommonWordActivity.IS_NEW_WORD to true,
                CommonWordActivity.GRADE_KEY to gradeKey,
                CommonWordActivity.TITLE to "生词本")
    }

}
