package tuoyan.com.xinghuo_dayingindex.ui.study

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.sensorsdata.analytics.android.sdk.SensorsDataFragmentTitle
import kotlinx.android.synthetic.main.fragment_study_book.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EventMsg
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.Book
import tuoyan.com.xinghuo_dayingindex.ui.books.adapter.BooksItemAdapter
import tuoyan.com.xinghuo_dayingindex.ui.books.detail.BookDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.list.BookListActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.main.Adapter
import tuoyan.com.xinghuo_dayingindex.ui.main.LessonListActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import kotlin.math.abs

@SensorsDataFragmentTitle(title = "书架")
class MyBooksFragment : LifeV4Fragment<StudyPresenter>() {
    override val presenter: StudyPresenter
        get() = StudyPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_study_book

    private var myBooks: List<Book>? = null
    private var dDialog: DDialog? = null

    val recommedAdapter by lazy {
        Adapter() {
            if ("1" == it.dataType) {
                val intent = Intent(activity, LessonListActivity::class.java)
                intent.putExtra(LessonListActivity.KEY, it.key)
                intent.putExtra(LessonListActivity.TITLE, it.title)
                startActivity(intent)
            } else {
                val intent = Intent(activity, LessonDetailActivity::class.java)
                intent.putExtra(LessonDetailActivity.KEY, it.key)
                startActivity(intent)
            }
        }
    }

    private val bookAdapter by lazy {
        BooksItemAdapter() { type, item, position ->
            when (type) {
                "1" -> {
                    startActivity<BookDetailActivity>(
                        BookDetailActivity.KEY to item.key,
                        BookDetailActivity.TYPE to "2"
                    )
                }
                "2" -> {
                    dDialog = DDialog(this.requireContext()).setMessage("确定删除该图书吗？").setNegativeButton("取消") {
                        dDialog?.dismiss()
                    }.setPositiveButton("删除") {
                        dDialog?.dismiss()
                        val map = HashMap<String, String>()
                        map["key"] = item.key
                        presenter.deleteMyBooks(map) {
                            toast(it)
                            removeView(position)
                        }
                    }
                    dDialog?.show()
                }
                "3" -> {
                    isLogin {
                        startActivity<BookListActivity>()
                    }
                }
            }
        }
    }

    override fun configView(view: View?) {
        rlv_books.isNestedScrollingEnabled = false
        rlv_books.layoutManager = GridLayoutManager(activity, 3)
        rlv_books.adapter = bookAdapter
        srfl.setColorSchemeResources(R.color.color_1482ff)

        rlv_recommend.layoutManager = LinearLayoutManager(activity)
        rlv_recommend.adapter = recommedAdapter
    }

    override fun handleEvent() {
        srfl.setOnRefreshListener {
            isLogin({
                refreshData()
                EventBus.getDefault().post(EventMsg("onRefresh", 0))
            }) {
                srfl.isRefreshing = false
            }
        }

        tv_top_edit.setOnClickListener {
            isLogin {
                tv_top_edit.isSelected = !tv_top_edit.isSelected
                bookAdapter.isDel = tv_top_edit.isSelected
                bookAdapter.setFooter(!tv_top_edit.isSelected)
                tv_top_edit.text = if (tv_top_edit.isSelected) "退出管理" else "管理"
            }
        }
        abl.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            srfl.isEnabled = verticalOffset >= 0
            when {
                abs(verticalOffset) >= img_re.top - ctl_top.top - ctl_top.height -> {
                    ctl_top.visibility = View.GONE
                    v_top.visibility = View.GONE
                    tv_top_edit.visibility = View.GONE
                }
                else -> {
                    ctl_top.translationY = 0f
                    ctl_top.visibility = View.VISIBLE
                    v_top.visibility = View.VISIBLE
                    tv_top_edit.visibility = if (myBooks != null && myBooks!!.isNotEmpty()) View.VISIBLE else View.GONE
                }
            }
        })
    }

    override fun initData() {
        presenter.getNetClassList {
            recommedAdapter.setData(it)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }


    fun refreshData() {
        if (SpUtil.isLogin) {
            presenter.getMyBooks({
                srfl.isRefreshing = false
            }) {
                myBooks = it
                tv_top_edit.text = "管理"
                tv_top_edit.isSelected = false
                bookAdapter.isDel = false
                bookAdapter.setFooter(true)
                if (it.isEmpty()) {
                    tv_top_title.text = "图书配套"
                    bookAdapter.setData(ArrayList())
                    tv_top_edit.visibility = View.GONE
                } else {
                    tv_top_title.text = "图书配套（${it.size}）"
                    bookAdapter.setData(it)
                    tv_top_edit.visibility = View.VISIBLE
                }
                srfl.isRefreshing = false
            }
        } else {
            myBooks = null
            tv_top_edit.visibility = View.GONE
            tv_top_title.text = "图书配套"
            tv_top_edit.text = "管理"
            tv_top_edit.isSelected = false
            tv_top_edit.visibility = View.GONE
            bookAdapter.isDel = false
            bookAdapter.setFooter(true)
            bookAdapter.setData(ArrayList())
        }
    }

    private fun removeView(position: Int) {
        bookAdapter.remove(position)
        val size = bookAdapter.getDateCount()
        tv_top_title.text = if (size == 0) "图书配套" else "图书配套（${size}）"
        if (size == 0) {
            bookAdapter.isDel = false
            bookAdapter.setFooter(true)
            tv_top_edit.text = "管理"
            tv_top_edit.isSelected = false
            tv_top_edit.visibility = View.GONE
        }
    }
}