package tuoyan.com.xinghuo_dayingindex.ui.book

import androidx.fragment.app.Fragment
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.activity_basic_word.*
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.BaseV4FmPagerAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.BookDetail
import tuoyan.com.xinghuo_dayingindex.utlis.TypeUtil

class BasicWordActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_basic_word

    //    private val wordCatalogList by lazy { intent.getSerializableExtra(WORD_CATALOG_LIST) as ArrayList<BookRes> }
//    private val resourceList by lazy { intent.getSerializableExtra(RESOURCE_LIST) as ArrayList<BookRes> }
    private val gradeKey by lazy { intent.getStringExtra(GRADE_KEY) ?: "" }
    private val catalogKey by lazy { intent.getStringExtra(CATALOG_KEY) ?: "" }
    private val bookDetail by lazy { intent.getSerializableExtra(TestListActivity.BOOK_DETAIL) as? BookDetail }
    private val titles = arrayOf("单词巩固", "语法巩固")

    private val wordFragment by lazy { BasicItemFragment.newInstance(gradeKey) }
    private val grammarFragment by lazy { GrammarFragment.newInstance() }
    override fun configView() {
        super.configView()
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        super.initData()
        val mPagerAdapter = BaseV4FmPagerAdapter(supportFragmentManager)
        val fList = mutableListOf<Fragment>()
        fList.add(wordFragment)
        fList.add(grammarFragment)
        mPagerAdapter.fragmentList = fList
        mPagerAdapter.titles = titles
        vp_basic_word.adapter = mPagerAdapter
        tl_basic_word.setupWithViewPager(vp_basic_word)
    }

    override fun onResume() {
        super.onResume()
        presenter.getResourcesByCatalog(catalogKey, gradeKey) { detail ->
            if (wordFragment.isAdded) {
                wordFragment.setData(detail.wordCatalogList)
            }
            if (grammarFragment.isAdded) {
                grammarFragment.setData(detail.resourceList)
            }
        }
    }

    companion object {
        val RESOURCE_LIST = "resourceList"
        val WORD_CATALOG_LIST = "wordCatalogList"
        val GRADE_KEY = "gradeKey"
        val CATALOG_KEY = "catalogKey"
        val BOOK_DETAIL = "bookDetail"
    }

    fun saOption(optionName: String, type: String) {
        try {
            val property = saProperty(bookDetail)
            property.put("operation_name", optionName)
            property.put("resource_type", TypeUtil.getType(type))
            SensorsDataAPI.sharedInstance().track("operation_book_matching_page", property)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun saProperty(bookDetail: BookDetail?): JSONObject {
        val property = JSONObject()
        bookDetail?.let {
            property.put("book_matching_id", bookDetail.supportingKey)
            property.put("book_matching_name", bookDetail.titile)
            property.put("section", bookDetail.gradeName)
        }
        return property
    }
}
