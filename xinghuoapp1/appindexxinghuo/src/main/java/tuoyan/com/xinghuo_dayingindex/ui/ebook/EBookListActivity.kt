package tuoyan.com.xinghuo_dayingindex.ui.ebook
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_ebook_list.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.base.LifeFullActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.EBookAdapter
import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils

class EBookListActivity : LifeFullActivity<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_ebook_list
    private val adapter by lazy {
        EBookAdapter() { item, _ ->
            startActivity<EBookDetailActivity>(EBookDetailActivity.KEY to item.key)
        }
    }

    override fun configView() {
        super.configView()
        rlv_books.layoutManager = LinearLayoutManager(this)
        rlv_books.adapter = adapter
    }

    override fun initData() {
        super.initData()
        presenter.getSmartBookList { list ->
            adapter.setData(list)
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        img_share.setOnClickListener {
            isLogin {
                ShareDialog(this) {
                    ShareUtils.share(this, it, "专项能力特训营（书+音频+视频+练习测评+数据=一体化学习解决方案）", "", WEB_BASE_URL + "ebook/ebooklist/")
                }.show()
            }
        }
    }
}