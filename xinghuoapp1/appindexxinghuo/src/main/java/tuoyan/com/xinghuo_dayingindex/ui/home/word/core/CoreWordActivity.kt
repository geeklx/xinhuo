package tuoyan.com.xinghuo_dayingindex.ui.home.word.core
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_core_word.*
import org.jetbrains.anko.ctx
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.ui.home.word.WordPresenter
import tuoyan.com.xinghuo_dayingindex.ui.home.word.words.WordsActivity

class CoreWordActivity : LifeActivity<WordPresenter>() {
    override val presenter = WordPresenter(this)
    override val layoutResId = R.layout.activity_core_word
    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    private val gradeKey by lazy { intent.getStringExtra(GRADE_KEY) ?: "" }
    private val adapter by lazy {
        CoreWordAdapter { it ->
            startActivity<WordsActivity>(
                WordsActivity.KEY to (it.catalogKey ?: ""),
                WordsActivity.GRADE_KEY to gradeKey,
                WordsActivity.TITLE to it.catalogName
            )
        }
    }

    override fun configView() {
        recycler_view.layoutManager = LinearLayoutManager(ctx)
        val decoration = DividerItemDecoration(ctx, LinearLayout.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.drawable.divider))
        recycler_view.addItemDecoration(decoration)
        recycler_view.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        presenter.catalogList(key) {
            adapter.setData(it.body)
        }
    }

    companion object {
        const val GRADE_KEY = "gradeKey"
        const val KEY = "key"
    }
}
