package tuoyan.com.xinghuo_dayingindex.ui.exercise.detail

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_choice_item.*
import org.jetbrains.anko.textColor
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.AnswerItem
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfoItem
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * 做题模块转原生
 *选项卡片
 */
class ExerciseChoiceItemFragment : LifeV4Fragment<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_choice_item
    private val parentActivity by lazy { activity as ExerciseDetailKActivity }
    private val parentFragment by lazy { getParentFragment() as ExerciseDetailChoiceFragment }
    private var itemData: QuestionInfo? = null
    var qSort = ""//当前维护的题号
    var questionKey = ""//当前小题的key

    override fun initData() {
        val gson = GsonBuilder().disableHtmlEscaping().create()
        arguments?.let {
            itemData = it.getSerializable(ITEM) as QuestionInfo
            qSort = itemData?.sort ?: ""
            questionKey = itemData?.questionKey ?: ""
            val index = it.getString(INDEX)
            val total = it.getString(TOTAL)
            val infoList = gson.fromJson<List<QuestionInfoItem>>(gson.toJson(itemData?.item), object : TypeToken<List<QuestionInfoItem>>() {}.type)//any 转为 questionInfo类型
            val adapter = ChoiceAdapter(itemData?.stem!!) { answer ->
                initAnswer(answer)
                parentFragment.goNext()
            }
            rlv_choices.layoutManager = LinearLayoutManager(context)
            rlv_choices.adapter = adapter
            itemData?.let { info ->
                if (info.useranswer.isNotEmpty()) {
                    initAnswer(info.useranswer)
                    adapter.order = info.useranswer
                }
            }
            adapter.setData(infoList)
            tv_title.text = Html.fromHtml("选择题 <font color='#4c84ff'><big>${index}</big></font>/${total}")
        }
    }

    override fun configView(view: View?) {
    }

    override fun handleEvent() {
        super.handleEvent()
    }

    /**
     *
     */
    private fun initAnswer(answer: String) {
        parentActivity.isDone = true
        val answers = mutableListOf<AnswerItem>()
        val answerItem = AnswerItem("1", answer, "")
        answers.add(answerItem)
        // 答案单选多选问题answerQ.answers=
        // 图片作答answerQ.imgs
        //  answerQ.answerType
        // answerQ.answerText
        parentActivity.answerList.forEach {
            it.qList.forEach { qItem ->
                if (qItem.questionKey == itemData?.questionKey!!) {
                    qItem.haveDone = true
                    qItem.answers.clear()
                    qItem.answers.addAll(answers)
                    parentActivity.saPaperAnswer(qItem)
                    return
                }
            }
        }
    }

    companion object {
        private const val ITEM = "item"
        private const val INDEX = "INDEX"//位于当前小节的第几个题
        private const val TOTAL = "TOTAL"//当前小节的总体数

        @JvmStatic
        fun newInstance(item: QuestionInfo, index: String, total: String) =
            ExerciseChoiceItemFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ITEM, item)
                    putSerializable(INDEX, index)
                    putSerializable(TOTAL, total)
                }
            }
    }
}

class ChoiceAdapter(val sort: String = "", val ItemClick: (answer: String) -> Unit) : BaseAdapter<QuestionInfoItem>(isHeader = true) {
    var order = ""
    override fun convert(holder: ViewHolder, item: QuestionInfoItem) {
        holder.setText(R.id.tv_order, item.order)
            .setText(R.id.tv_content, item.content)
        holder.itemView.isSelected = order == item.order
        holder.itemView.setOnClickListener {
            order = item.order
            notifyDataSetChanged()
            ItemClick(item.order)
        }
    }

    override fun header(holder: ViewHolder) {
        holder.setText(R.id.tv_title, sort)
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_choice_exercise, parent, false)
    }

    override fun headerView(context: Context,parent: ViewGroup): View {
        val tv = TextView(context)
        val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        params.setMargins(0, 0, 0, DeviceUtil.dp2px(context, 15f).toInt())
        tv.layoutParams = params
        tv.id = R.id.tv_title
        tv.textColor = ContextCompat.getColor(context, R.color.color_222)
        tv.textSize = 14f
        return tv
    }
}
