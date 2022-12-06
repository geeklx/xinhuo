package tuoyan.com.xinghuo_dayingindex.ui.book
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_basicitem_list.*
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.startActivity
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.ui.book.adapter.MyBasicItemRecyclerViewAdapter
import tuoyan.com.xinghuo_dayingindex.ui.home.word.common.CommonWordActivity

class BasicItemFragment : LifeV4Fragment<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_basicitem_list

    //    val dataList by lazy { arguments?.getSerializable(DATA_LIST) as ArrayList<BookRes> }
    val gradeKey by lazy { arguments?.getString(GRADE_KEY) }
    private val activity by lazy { requireActivity() as BasicWordActivity }

    companion object {
        val DATA_LIST = "data_list"
        val GRADE_KEY = "gradeKey"
        fun newInstance(gradeKey: String): BasicItemFragment {
            val f = BasicItemFragment()
            val args = Bundle()
//            args.putSerializable(DATA_LIST, dataList)
            args.putString(GRADE_KEY, gradeKey)
            f.arguments = args
            return f
        }
    }

    override fun configView(view: View?) {
        super.configView(view)
        list.layoutManager = LinearLayoutManager(ctx)
        list.adapter = adapter
    }

    override fun initData() {
        super.initData()
    }

    fun setData(dataList: List<BookRes>) {
        if (dataList != null && dataList.size > 0) {
            view_empty.visibility = View.GONE
            list.visibility = View.VISIBLE
            adapter.setData(dataList)
        } else {
            list.visibility = View.GONE
            view_empty.visibility = View.VISIBLE
        }
    }

    private var adapter = MyBasicItemRecyclerViewAdapter { item, _ ->
        activity.saOption("点击资源", "单词巩固")
        startActivity<CommonWordActivity>(
            CommonWordActivity.KEY to item.catalogKey,
            CommonWordActivity.GRADE_KEY to gradeKey,
            CommonWordActivity.TITLE to item.catalogName
        )
    }
}
