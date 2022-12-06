package tuoyan.com.xinghuo_dayingindex.ui.books.list
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.Book
import tuoyan.com.xinghuo_dayingindex.ui.books.BooksPresenter
import tuoyan.com.xinghuo_dayingindex.ui.books.adapter.BooksAdapter
import tuoyan.com.xinghuo_dayingindex.ui.books.detail.BookDetailActivity
import tuoyan.com.xinghuo_dayingindex.widegt.SectionDecoration
import kotlin.properties.Delegates

/**
 * Created by  on 2018/9/14.
 */
class BookListFragment : LifeV4Fragment<BooksPresenter>() {
    override val presenter: BooksPresenter
        get() = BooksPresenter(this)
    override val layoutResId: Int
        get() = 0

    var sfl by Delegates.notNull<SwipeRefreshLayout>()
    var rv by Delegates.notNull<RecyclerView>()
    private val years by lazy { mutableListOf<String>() }
    private val decoration by lazy { SectionDecoration(this.requireContext()) }

    val gradKey by lazy { arguments?.getString(GRAD_KEY) ?: "" }
    private val mAdapter by lazy {
        BooksAdapter {
            startActivity<BookDetailActivity>(BookDetailActivity.KEY to it, BookDetailActivity.TYPE to "2")
        }
    }

    companion object {
        val GRAD_KEY = "grad_key"

        fun newInstance(gradKey: String): BookListFragment {
            val f = BookListFragment()
            val args = Bundle()
            args.putString(GRAD_KEY, gradKey)

            f.arguments = args

            return f
        }
    }

    override fun initView(): View? {
        sfl = ctx.swipeRefreshLayout {
            rv = recyclerView {
                layoutManager = LinearLayoutManager(ctx)
            }
        }
        return sfl
    }

    override fun handleEvent() {
        sfl.setOnRefreshListener {
            initData()
        }
    }

    override fun configView(view: View?) {
        super.configView(view)
        rv.adapter = mAdapter
        rv.addItemDecoration(decoration)
    }

    override fun initData() {
        presenter.getBooks(gradKey) {
            sfl.isRefreshing = false
            initList(it)
        }
    }

    override fun onError(message: String) {
        super.onError(message)
        if (message == "网络异常，请稍后再试" || message == "无法连接到服务器" || message == "连接超时") {
            showNetDialog(0, 0)
            return
        }
    }

    val dataList: ArrayList<List<Book>> by lazy { ArrayList<List<Book>>() }
    private fun initList(list: List<Book>) {
        dataList.clear()
        years.clear()
        var yearStr = "1"//根据年份划分
        var bookList = ArrayList<Book>()
        list.forEach {
            if (yearStr != it.year) {
                if (bookList.isNotEmpty()) {
                    dataList.add(bookList)
                }
                yearStr = it.year
                bookList = ArrayList()
                bookList.add(it)
                years.add(it.year)
            } else {
                bookList.add(it)
            }
        }

        if (bookList.isNotEmpty()) {
            dataList.add(bookList)
        }
        decoration.setDataList(years)
        mAdapter.setData(dataList)
    }
}