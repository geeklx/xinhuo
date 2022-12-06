package tuoyan.com.xinghuo_dayingindex.ui.ebook.listen
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.os.Build
import android.view.Gravity
import android.view.View
import android.webkit.WebSettings
import kotlinx.android.synthetic.main.activity_ebook_jian_read.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeFullActivity
import tuoyan.com.xinghuo_dayingindex.bean.EBook
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.EBookReadCatalogFragment

class EBookJianReadActivity : EBookLifeFullActivity<EBookPresenter>() {
    override val layoutResId: Int
        get() = R.layout.activity_ebook_jian_read
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)

    private val isOwn by lazy { intent.getStringExtra(IS_OWN) ?: "0" }

    private val catalogFragment by lazy { EBookReadCatalogFragment.newInstance(bookParam?.bookKey ?: "", isOwn) }

    private var bookDetail: EBook? = null

    private var dDialog: DDialog? = null

    var refresh = false

    override fun configView() {
        super.configView()
        initCatalog()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

    override fun initData() {
        super.initData()
        presenter.getResourceInfo(bookParam?.resourceKey ?: "", "6") {
            webview.loadUrl(it.url)
        }
        if ("1" != isOwn) {
            presenter.getEBookDetail(bookParam?.bookKey ?: "") { book ->
                bookDetail = book
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        img_catalog.setOnClickListener {
            fragment.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        if (refresh && catalogFragment.isAdded) {
            catalogFragment.initData()
        }
    }

    override fun onBackPressed() {
        if (fragment.visibility == View.VISIBLE) {
            fragment.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    fun mFinish() {
        super.onBackPressed()
    }

    fun catalogClick() {
        fragment.visibility = View.GONE
    }

    private fun initCatalog() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment, catalogFragment)
        transaction.show(catalogFragment).commit()
    }

    companion object {
        //是否购买
        const val IS_OWN = "isOwn"
    }

    fun goBuy() {
        val msg = "该资源为虚拟产品，购买后不可退款，${if ("长期" != bookDetail?.endTime) "有效期至${bookDetail?.endTime}，过期则失效不能使用，" else ""}是否确认购买？"
        dDialog = DDialog(this).setWidth(250).setGravity(Gravity.LEFT).setMessage(msg)
            .setNegativeButton("考虑一下") {
                dDialog?.dismiss()
            }.setPositiveButton("立即购买") {
                dDialog?.dismiss()
                startActivity<PostActivity>(PostActivity.URL to "ordersubmit?goodsKey=${bookParam?.bookKey ?: ""}&goodsNum=1&goodsType=7&isCart=0")
                mFinish()
            }
        dDialog?.show()
    }
}