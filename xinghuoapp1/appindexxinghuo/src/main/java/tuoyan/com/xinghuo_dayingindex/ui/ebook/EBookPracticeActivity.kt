package tuoyan.com.xinghuo_dayingindex.ui.ebook
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_ebook_practice.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.PracticeAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.EBookWordListActivity

class EBookPracticeActivity : EBookLifeActivity<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_ebook_practice

    private var dDialog: DDialog? = null
    private val adapter by lazy {
        PracticeAdapter(bookParam?.isOwn ?: "0") { item, pos ->
            if ("0" == bookParam?.isOwn && 0 != pos) {
                dDialog = DDialog(this).setMessage("购买后可以开始学习，是否购买？")
                    .setNegativeButton("考虑一下") {
                        dDialog?.dismiss()
                    }.setPositiveButton("立即购买") {
                        dDialog?.dismiss()
                        bookParam?.let { book ->
                            startActivity<PostActivity>(PostActivity.URL to "ordersubmit?goodsKey=${book.bookKey}&goodsNum=1&goodsType=7&isCart=0")
                        }
                    }
                dDialog?.show()
            } else {
                bookParam?.wordKey = item.key
                bookParam?.matpKey = item.matpKey
                bookParam?.userpractisekey = item.userPracticeKey
                bookParam?.catalogName = item.name
                if (item.lastTime.isNullOrEmpty() || item.lastSource.isNullOrEmpty()) {
                    bookParam?.userpractisekey = ""
                    startActivity<EBookWordsActivity>(EBOOK_PARAM to bookParam)
                } else {
                    dDialog = DDialog(this).setMessage("上次答题时间<br><font color='#15d25f'><b>${item.lastTime}</b></font>")
                        .setNegativeButton("重新作答") {
                            dDialog?.dismiss()
                            bookParam?.userpractisekey = ""
                            startActivity<EBookWordsActivity>(EBOOK_PARAM to bookParam)
                        }.setPositiveButton("继续答题") {
                            dDialog?.dismiss()
                            when (item.lastSource) {
                                "10" -> {
                                    startActivity<EBookWordsActivity>(EBOOK_PARAM to bookParam)
                                }
                                "10.1" -> {
                                    presenter.getRefineQuestion(bookParam?.wordKey ?: "", bookParam?.userpractisekey ?: "") { paper ->
                                        startActivity<EBookWordListActivity>(
                                            EBOOK_PARAM to bookParam,
                                            EBookWordListActivity.ANSWER_LIST to paper.qestionList
                                        )
                                    }
                                }
                                "11" -> {
                                    startActivity<EBookListenActivity>(EBOOK_PARAM to bookParam)
                                }
                            }
                        }
                    dDialog?.show()
                }
            }
        }
    }

    override fun configView() {
        super.configView()
        rlv_c.layoutManager = LinearLayoutManager(this)
        rlv_c.adapter = adapter
        tv_title.text = bookParam?.name ?: ""
    }

    override fun initData() {
        super.initData()
    }

    override fun onResume() {
        super.onResume()
        bookParam?.let { book ->
            presenter.getRefineCatalogList(book.smarkResourceKey, book.bookKey, book.catalogKey) { list ->
                adapter.setData(list)
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    companion object {
        val DATA = "data"
    }
}