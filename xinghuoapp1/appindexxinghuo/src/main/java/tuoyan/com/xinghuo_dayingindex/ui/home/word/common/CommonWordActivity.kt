package tuoyan.com.xinghuo_dayingindex.ui.home.word.common
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_common_word.*
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean
import tuoyan.com.xinghuo_dayingindex.ui.home.word.WordPresenter
import tuoyan.com.xinghuo_dayingindex.ui.home.word.detail.WordDetailActivity
import tuoyan.com.xinghuo_dayingindex.utlis.MediaPlayerUtlis

class CommonWordActivity : LifeActivity<WordPresenter>() {
    override val presenter = WordPresenter(this)
    override val layoutResId = R.layout.activity_common_word
    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    override val title by lazy { intent.getStringExtra(TITLE) ?: "" }
    private val gradeKey by lazy { intent.getStringExtra(GRADE_KEY) ?: "" }
    private val isNewWord by lazy { intent.getBooleanExtra(IS_NEW_WORD, false) }
    private var page = 0
    private val adapter by lazy {
        CommonWordAdapter(isEmpty = isNewWord, more = {
            more()
        }) { pos ->
            // : 2018/10/12 11:06
            goto(pos)
        }
    }

    private fun more() {
        if (isNewWord) {
            // : 2018/10/23 13:40  生词本
            page++
            presenter.getNewWord(gradeKey, page) {
                adapter.addData(it.body)
                adapter.setMore(adapter.getDateCount() < it.totalCount)
            }
        }
    }

    private fun goto(position: Int) {
//        MyApp.instance.data = adapter.getData()//数据过大传参崩溃
        val list1: List<ResourceListBean> = ArrayList()
        val gson1 = Gson()
        val data1 = gson1.toJson(adapter.getData())
        SPUtils.getInstance().put("WordsByCatalogkey", data1)
        startActivity<WordDetailActivity>(
            WordDetailActivity.POSITION to position,
            WordDetailActivity.IS_NEW_WORD to isNewWord,
            WordDetailActivity.TITLE to title,
            WordDetailActivity.GRADE_KEY to gradeKey
        )
    }

    override fun configView() {
        tv_title.text = title
        recycler_view.layoutManager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this, LinearLayout.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.drawable.divider))
        recycler_view.addItemDecoration(decoration)
        recycler_view.adapter = adapter
//        adapter.setMore(isNewWord)
    }

    override fun initData() {
        if (!isNewWord) {
            presenter.getWordsByCatalogkey(key, 0) {
                adapter.setData(it.list)
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isNewWord) {
            // : 2018/10/23 13:40  生词本
            page = 0
            presenter.getNewWord(gradeKey, page) {
                adapter.setData(it.body)
                adapter.setMore(adapter.getDateCount() < it.totalCount)
            }
        }
    }

    override fun onPause() {
        MediaPlayerUtlis.release()
        super.onPause()
    }

    companion object {
        const val KEY = "key"
        const val TITLE = "title"
        const val GRADE_KEY = "gradeKey"
        const val IS_NEW_WORD = "isNewWord"
    }
}
