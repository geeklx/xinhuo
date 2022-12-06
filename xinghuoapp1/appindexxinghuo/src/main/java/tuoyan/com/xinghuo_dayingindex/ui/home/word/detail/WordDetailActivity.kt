package tuoyan.com.xinghuo_dayingindex.ui.home.word.detail

import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_word_detail.*
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean
import tuoyan.com.xinghuo_dayingindex.bean.WordsByCatalogkey
import tuoyan.com.xinghuo_dayingindex.ui.home.word.WordPresenter
import tuoyan.com.xinghuo_dayingindex.utlis.MediaPlayerUtlis
import java.lang.reflect.Type

class WordDetailActivity : LifeActivity<WordPresenter>() {
    override val presenter = WordPresenter(this)
    override val layoutResId = R.layout.activity_word_detail

    private var words: List<WordsByCatalogkey>? = null

    //    private val words by lazy { intent.getSerializableExtra(DATA)as? List<WordsByCatalogkey> }
    private val position by lazy { intent.getIntExtra(POSITION, -1) }
    private val gradeKey by lazy { intent.getStringExtra(GRADE_KEY) ?: "" }
    override val title by lazy { intent.getStringExtra(TITLE) ?: "" }
    private val isNewWord by lazy { intent.getBooleanExtra(IS_NEW_WORD, false) }
    private val adapter by lazy { WordPagerAdapter(supportFragmentManager, gradeKey, isNewWord) }
    override fun configView() {
        view_pager.adapter = adapter
        tv_title.text = title
    }

    override fun handleEvent() {
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                adapter.getItem(position).play(this@WordDetailActivity)
                wordRecord(position)
            }

        })
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun wordRecord(pos: Int) {
        if (!isNewWord) {
            words?.let {
                val word = it[pos]
                presenter.wordRecord(word.classifyKey, word.catalogKey, word.key ?: "") {

                }
            }
        }
    }

    override fun initData() {
//        words = MyApp.instance.data
        // 取
        val data2: String = SPUtils.getInstance().getString("WordsByCatalogkey", "")
        val gson2 = Gson()
        val listType2: Type = object : TypeToken<List<WordsByCatalogkey?>?>() {}.getType()
        words = gson2.fromJson<List<WordsByCatalogkey>>(data2, listType2)
        adapter.setData(words)
        if (words?.isEmpty() == false) {
            if (position < 1) {
                adapter.getItem(0).play(this)
                wordRecord(0)
            } else {
                view_pager.currentItem = position
                wordRecord(position)
            }
        }
    }

    override fun onPause() {
        MediaPlayerUtlis.release()
        super.onPause()
    }

    override fun onDestroy() {
//        MyApp.instance.data = null
        SPUtils.getInstance().put("WordsByCatalogkey", "")
        super.onDestroy()
    }

    companion object {
        const val DATA = "data"
        const val TITLE = "title"
        const val GRADE_KEY = "gradeKey"
        const val POSITION = "position"
        const val IS_NEW_WORD = "isNewWord"
    }
}
