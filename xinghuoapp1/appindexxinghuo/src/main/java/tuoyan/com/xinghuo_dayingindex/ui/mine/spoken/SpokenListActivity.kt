package tuoyan.com.xinghuo_dayingindex.ui.mine.spoken
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Context
import android.graphics.Paint
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_ebook_list.*
import kotlinx.android.synthetic.main.activity_ebook_list.toolbar
import kotlinx.android.synthetic.main.activity_ebook_list.tv_title
import kotlinx.android.synthetic.main.activity_lesson_list2.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeFullActivity
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.ExaminationListBean
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.main.LessonListActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tuoyan.com.xinghuo_dayingindex.utlis.Utils

class SpokenListActivity : LifeFullActivity<EBookPresenter>() {
    private val mTitle by lazy { intent.getStringExtra(LessonListActivity.TITLE) ?: "" }
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_spoken_list
    private val adapter by lazy {
        SpokenListAdapter() { item, _ ->
            startActivity<SpokenDetailsActivity>(SpokenDetailsActivity.KEY to item.spokenExaminationKey)
        }
    }

    override fun configView() {
        super.configView()
        tv_title.text = mTitle
        rlv_books.layoutManager = LinearLayoutManager(this)
        rlv_books.adapter = adapter
    }

    override fun initData() {
        super.initData()
//        presenter.getSmartBookList { list ->
//            adapter.setData(list)
//        }
        presenter.examinationList {
            adapter.setData(it.list)
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    class SpokenListAdapter(val onItemClick: (ExaminationListBean, Int) -> Unit) :
        BaseAdapter<ExaminationListBean>(isFooter = true, isEmpty = true) {
        override fun convert(holder: ViewHolder, item: ExaminationListBean) {
            var cl: ConstraintLayout = holder.getView(R.id.cl) as ConstraintLayout
            var lp: RecyclerView.LayoutParams = cl.layoutParams as RecyclerView.LayoutParams
            if (holder.adapterPosition == 0) {
                lp.topMargin = Utils.dip2px(mContext, 10f)
            } else {
                lp.topMargin = Utils.dip2px(mContext, 18f)
            }
            cl.layoutParams = lp
            holder.setImageUrl(R.id.img_cover, item.imageUrl, 0, 0)
                .setText(R.id.tv_title, item.spokenExaminationName)
                .setText(
                    R.id.tv_time,
                    item.examStartTime.replace("-", ".") + "-" + item.examEndTime
                )
                .setText(R.id.tv_buy_num, "已购${item.buyCount}")
                .setText(R.id.tv_limit_num, "限购${item.examinationNum}")


            val tvOlderPrice = holder.getView(R.id.tv_disprice) as TextView
            tvOlderPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.setText(R.id.tv_price, Html.fromHtml("<small>￥</small>${item.cuCheck}"))
                .setText(R.id.tv_disprice, "￥${item.cuCheckOriginal}")
                .setVisible(R.id.tv_price, if ("1" == item.isBuy) View.GONE else View.VISIBLE)
                .setVisible(R.id.tv_disprice, if ("1" == item.isBuy) View.GONE else View.VISIBLE)
                .setText(R.id.tv_right, if ("1" == item.isBuy) "已购买" else "")
                .setSelected(R.id.tv_right, "1" == item.isBuy)
                .setSelected(R.id.tv_buy_num, "1" == item.isBuy)
                .setSelected(R.id.img_l, "1" == item.isBuy).setSelected(R.id.v1, "1" == item.isBuy)
                .setSelected(R.id.tv_limit_num, "1" == item.isBuy)
                .setSelected(R.id.tv_line, "1" == item.isBuy)
            holder.itemView.setOnClickListener {
                onItemClick(item, holder.layoutPosition)
            }
        }

        override fun convertView(context: Context, parent: ViewGroup): View {
            return LayoutInflater.from(context).inflate(R.layout.layout_spoken_list, parent, false)
        }

        override fun footerView(context: Context, parent: ViewGroup): View {
            val view = View(context)
            val params = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                DeviceUtil.dp2px(context, 60f).toInt()
            )
            view.layoutParams = params
            return view
        }

        override fun emptyView(context: Context, parent: ViewGroup): View {
            var view =
                LayoutInflater.from(context).inflate(R.layout.layout_empty_1, parent, false)
            view.findViewById<TextView>(R.id.tv).text = "还没有上架的考试哦~"
            return view
        }


    }

    companion object {
        const val TITLE = "title"
    }
}