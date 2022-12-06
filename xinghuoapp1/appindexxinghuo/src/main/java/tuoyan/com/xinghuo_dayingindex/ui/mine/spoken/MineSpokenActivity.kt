package tuoyan.com.xinghuo_dayingindex.ui.mine.spoken
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.hd.http.util.TextUtils
import kotlinx.android.synthetic.main.activity_mine_spoken.*
import org.jetbrains.anko.textColor
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.SpokenExaminBean
import tuoyan.com.xinghuo_dayingindex.ui.mine.user.UserPresenter
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

class MineSpokenActivity : LifeActivity<UserPresenter>() {
    override val presenter = UserPresenter(this)
    override val layoutResId = R.layout.activity_mine_spoken
    var state = ""
    private val adapter by lazy {
        MineSpokenAdapter(){
            startActivity<SpokenDetailsActivity>(SpokenDetailsActivity.KEY to it.spokenExaminationKey)
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        val textViews = arrayOf(tv_all, tv_no_start, tv_ing, tv_end, tv_scored)
        tv_all.setOnClickListener({
            state=""
            setView(textViews, tv_all)
        })
        tv_no_start.setOnClickListener({
            state="1"
            setView(textViews, tv_no_start)
        })
        tv_ing.setOnClickListener({
            state="2"
            setView(textViews, tv_ing)
        })
        tv_end.setOnClickListener({
            state="3"
            setView(textViews, tv_end)
        })
        tv_scored.setOnClickListener({
            state="4"
            setView(textViews, tv_scored)
        })
    }

    fun setView(textViews: Array<TextView>, view: TextView) {
        textViews.forEach { view ->
            view.textSize = 15f
            view.textColor = Color.parseColor("#ff222222")
        }
        view.textSize = 17f
        view.textColor = Color.parseColor("#ff008aff")
        presenter.mySpokenExamine(state) {

            adapter.setData(it.spokenExaminBeanList)
            adapter.notifyDataSetChanged()
        }
    }

    override fun configView() {
        rv_spoken.layoutManager = LinearLayoutManager(this)
        rv_spoken.adapter = adapter;
//        var list= ArrayList<String>()
//        list.add("")
//        list.add("")
//        list.add("")
//        adapter.setData(list)
//        adapter.notifyDataSetChanged()
    }

    override fun initData() {
        presenter.mySpokenExamine(state) {
            adapter.setData(it.spokenExaminBeanList)
            adapter.notifyDataSetChanged()
        }

    }

    class MineSpokenAdapter(val onItemClick: (SpokenExaminBean) -> Unit) : BaseAdapter<SpokenExaminBean>(isEmpty = true, isFooter = true) {
        private var labels = arrayListOf<Int>(
            R.drawable.ic_spoken_nostart,
            R.drawable.ic_spoken_ing,
            R.drawable.ic_spoken_end,
            R.drawable.ic_spoken_score
        )

        override fun convert(holder: ViewHolder, item: SpokenExaminBean) {
            holder.setText(R.id.tv_title, item.spokenExaminationName)
            holder.setText(
                R.id.tv_level,
                if (item.state == "3" || item.state == "4") {if(!TextUtils.isEmpty(item.groupType)){item.groupType} else "-"} else "-"
            )
            holder.setText(R.id.tv_time, item.examStartTime + " - " + item.examEndTime)
            holder.setVisible(
                R.id.tv_look_notice,
                if (item.state == "3" || item.state == "4"&&!TextUtils.isEmpty(item.groupType)) View.VISIBLE else View.GONE
            )
            holder.setImageResource(R.id.iv_label,labels[if (!TextUtils.isEmpty(item.state))item.state.toInt()-1 else 0])
            Glide.with(holder.itemView.context).load(item.imageUrl)
                .into(holder.getView(R.id.img_cover) as ImageView)
            holder.itemView.setOnClickListener {
                onItemClick(item)
            }
        }

        override fun convertView(context: Context, parent: ViewGroup): View {
            return LayoutInflater.from(context).inflate(R.layout.item_mine_spoken, parent, false)
        }

        override fun emptyView(context: Context, parent: ViewGroup): View {
            var view =
                LayoutInflater.from(context).inflate(R.layout.layout_empty_1, parent, false)
            view.findViewById<TextView>(R.id.tv).text = "参加口语考试后才能显示详情哦~"
            return view
        }

        //    override fun emptyImageRes(): Int {
//        return R.mipmap.icon_empty_sentence
//    }
//
//    override fun emptyText(): String {
//        return "购课后才能显示倒计时哦~"
//    }
        override fun footerView(context: Context, parent: ViewGroup): View {
            val view = View(context)
            val params = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                DeviceUtil.dp2px(context, 60f).toInt()
            )
            view.layoutParams = params
            return view
        }
    }
}