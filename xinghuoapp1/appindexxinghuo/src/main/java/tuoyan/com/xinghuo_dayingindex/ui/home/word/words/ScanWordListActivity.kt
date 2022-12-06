package tuoyan.com.xinghuo_dayingindex.ui.home.word.words
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_scan_word_list.*
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.CatalogList
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean
import tuoyan.com.xinghuo_dayingindex.bean.ScanWord
import tuoyan.com.xinghuo_dayingindex.ui.home.word.WordPresenter
import tuoyan.com.xinghuo_dayingindex.ui.home.word.common.CommonWordAdapter
import tuoyan.com.xinghuo_dayingindex.ui.home.word.detail.WordDetailActivity

class ScanWordListActivity : LifeActivity<WordPresenter>() {
    override val layoutResId: Int
        get() = R.layout.activity_scan_word_list
    override val presenter: WordPresenter
        get() = WordPresenter(this)

    private val classifyKey by lazy { intent.getStringExtra(CLASSIFY_KEY) ?: "" }
    private val gradeKey by lazy { intent.getStringExtra(GRADE_KEY) ?: "" }
    override val title by lazy { intent.getStringExtra(TITLE) ?: "" }
    private var scanWord: ScanWord? = null
    private var page = 0

    private val groupDialog by lazy {
        val picker = OptionsPickerBuilder(this) { options1, options2, options3, v ->
            scanWord!!.let {
                val item = it.catalogList[options1]
                tv_group.text = item.catalogName ?: ""
                getWordByKey(item.catalogKey ?: "")
            }
        }.setCancelText("取消")//取消按钮文字
            .setSubmitText("完成")//确认按钮文字
            .setContentTextSize(18)//滚轮文字大小
            .setTitleSize(15)//标题文字大小
            .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
            .setDividerColor(resources.getColor(R.color.color_e6e6e6))
            .setTextColorOut(resources.getColor(R.color.color_a7a7a7))
            .setTextColorCenter(resources.getColor(R.color.color_1e1e1e))
            .setSubmitColor(resources.getColor(R.color.color_1482ff))//确定按钮文字颜色
            .setCancelColor(resources.getColor(R.color.color_1482ff))//取消按钮文字颜色
            .setTitleBgColor(resources.getColor(R.color.color_ffffff))//标题背景颜色 Night mode
            .setBgColor(resources.getColor(R.color.color_ffffff))//滚轮背景颜色 Night mode
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setLineSpacingMultiplier(2f)
            .build<CatalogList>()
        picker
    }
    private val adapter by lazy {
        CommonWordAdapter(isEmpty = classifyKey.isNullOrBlank(), more = {
            more()
        }) { pos ->
            goto(pos)
        }
    }

    override fun configView() {
        super.configView()
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter
    }

    override fun initData() {
        super.initData()
        tv_title.text = title
    }

    override fun onResume() {
        super.onResume()
        if (classifyKey.isNullOrBlank()) {
            tv_group.visibility = View.GONE
            page = 0
            presenter.getNewWord(gradeKey, page, "3") {
                adapter.setData(it.body)
                adapter.setMore(adapter.getDateCount() < it.totalCount)
                adapter.setFooter(adapter.getDateCount() >= it.totalCount)
            }
        } else {
            adapter.setFooter(true)
            presenter.wordCatalogInfo(classifyKey) {
                scanWord = it
                adapter.currentKey = it.lastWordKKey
                adapter.setData(it.wordList)
                groupDialog.setPicker(it.catalogList)
                tv_group.visibility = if (it.catalogList.size > 1) View.VISIBLE else View.GONE
                tv_group.text = it.lastCatalogName
                if (it.lastWordKKey.isNotBlank()) {
                    kotlin.run breaking@{
                        it.wordList.forEachIndexed { index, wordsByCatalogkey ->
                            if (it.lastWordKKey == wordsByCatalogkey.key) {
                                recycler_view.smoothScrollToPosition(index)
                                return@breaking
                            }
                        }
                    }
                }
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tv_group.setOnClickListener {
            groupDialog.show()
        }
    }

    companion object {
        val CLASSIFY_KEY = "classifyKey"
        val GRADE_KEY = "gradeKey"
        val TITLE = "title"
    }

    private fun getWordByKey(catalogKey: String) {
        presenter.scanWordByKey(catalogKey) {
            adapter.setData(it.wordList)
        }
    }

    private fun goto(position: Int) {
//        MyApp.instance.data = adapter.getData()
        val list1: List<ResourceListBean> = ArrayList()
        val gson1 = Gson()
        val data1 = gson1.toJson(adapter.getData())
        SPUtils.getInstance().put("WordsByCatalogkey", data1)
        startActivity<WordDetailActivity>(
            WordDetailActivity.POSITION to position,
            WordDetailActivity.GRADE_KEY to gradeKey,
            WordDetailActivity.TITLE to title,
            WordDetailActivity.IS_NEW_WORD to classifyKey.isNullOrBlank()
        )
    }

    private fun more() {
        if (classifyKey.isNullOrBlank()) {
            // : 2018/10/23 13:40  生词本
            page++
            presenter.getNewWord(gradeKey, page, "3") {
                adapter.addData(it.body)
                adapter.setMore(adapter.getDateCount() < it.totalCount)
                adapter.setFooter(adapter.getDateCount() >= it.totalCount)
            }
        }
    }
}