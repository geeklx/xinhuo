package tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.evaluation

import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.BaseV4Fragment
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.AnswerItem
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseModel
import tuoyan.com.xinghuo_dayingindex.bean.LevelListBean
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo

/**x
 */
class TranslationFragment : BaseV4Fragment() {
    override val layoutResId = 0
    private val parentActivity by lazy { activity as EntryActivity }

    private val adapter by lazy {
        TranslationAdapter({

        }) {
            if (it.size < subCount) {
                toast("请将评分标准填写完整")
            } else {
                exerciseModel.answers.clear()
                it.forEach {
                    var answerItem = AnswerItem(it.key, it.value.levelKey, it.value.levelPoint)
                    exerciseModel.answers.add(answerItem)
                }
                parentActivity.onCheck()
            }
        }
    }
    private var subCount = 0 //TODO 评分标准数量，判断当前选择的评分标准是否完整
    override fun initView() = UI {
        recyclerView {
            layoutManager = LinearLayoutManager(context)
            horizontalPadding = dip(15)
            adapter = this@TranslationFragment.adapter
        }
    }.view

    var dataList = mutableListOf<LevelListBean>()

    private val exerciseModel by lazy { arguments?.getSerializable(MultipleFragment.DATA) as ExerciseModel }

    companion object {
        val DATA = "data"

        fun newInstance(data: ExerciseModel): TranslationFragment =
            TranslationFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(DATA, data)
                }
            }
    }

    override fun initData() {
        var infoJson = Gson().toJson(exerciseModel.questionInfo)
        var questionInfo = Gson().fromJson(infoJson, QuestionInfo::class.java)
        var subList = questionInfo.subiList
        subCount = subList?.size ?: 0

        subList?.forEach { sub ->
            var sub = sub
            var nodeLevel = LevelListBean("NODE")
            nodeLevel.subName = sub.name
            nodeLevel.subKey = sub.key
            dataList.add(nodeLevel) //TODO 添加一个节点数据
            sub.levelList?.forEach { level ->
                level.type = "DATA"
                level.subName = sub.name
                level.subKey = sub.key
                dataList.add(level)
            }
        }
        adapter.setData(dataList)
    }
}

private class TranslationAdapter(var onClick: (Map<String, LevelListBean>) -> Unit, var onCommit: (Map<String, LevelListBean>) -> Unit) : BaseAdapter<LevelListBean>(isFooter = true) {
    private var viewMap = mutableMapOf<String, View>()
    private var keyMap = mutableMapOf<String, LevelListBean>()
    override fun convert(holder: ViewHolder, item: LevelListBean) {
        holder.setIsRecyclable(false)
        holder.setText(R.id.tv_title, item.levelName)
            .setText(R.id.tv_des, item.levelComment)
            .itemView.setOnClickListener { view ->
                viewMap[item.subKey]?.backgroundResource = R.drawable.bg_shape_5_c3c7cb_ffffff
                view.backgroundResource = R.drawable.bg_shape_5_4c84ff_ffffff
                viewMap[item.subKey] = view
                keyMap[item.subKey] = item
                onClick(keyMap)
            }
        keyMap[item.subKey]?.let {
            if (it.levelKey == item.levelKey) {
                holder.itemView.setBackgroundResource(R.drawable.bg_shape_5_4c84ff_ffffff)
                viewMap[item.subKey] = holder.itemView
            }
        }
    }

    private fun subConvert(holder: ViewHolder, bean: LevelListBean) {
        if (holder.itemView is TextView)
            (holder.itemView as TextView).text = bean.subName
    }

    override fun footer(holder: ViewHolder) {
        holder.setOnClickListener(R.id.tv_commit) {
            onCommit(keyMap)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
        linearLayout {
            lparams(matchParent, wrapContent) {
                bottomMargin = dip(10)
            }
            orientation = LinearLayout.HORIZONTAL
            weightSum = 7f
            backgroundResource = R.drawable.bg_shape_5_c3c7cb_ffffff
            horizontalPadding = dip(12)
            verticalPadding = dip(15)
            gravity = Gravity.CENTER_VERTICAL
            textView {
                id = R.id.tv_title
                textSize = 14f
                textColor = resources.getColor(R.color.color_4c84ff)
                gravity = Gravity.CENTER
                maxEms = 6
            }.lparams(0, wrapContent) {
                weight = 1f
            }
            space().lparams(dip(15), wrapContent)
            textView {
                id = R.id.tv_des
                textSize = 14f
                setLineSpacing(dip(3).toFloat(), 1f)
                textColor = resources.getColor(R.color.color_8d95a1)
            }.lparams(0, wrapContent) {
                weight = 6f
            }
        }
    }

    private fun subView(parent: ViewGroup) = TextView(parent.context).apply {
        id = R.id.tv_tab_title
        textSize = 12f
        textColor = resources.getColor(R.color.color_222831)
        typeface = Typeface.DEFAULT_BOLD
        topPadding = dip(10)
        bottomPadding = dip(15)
    }

    override fun footerView(context: Context,parent: ViewGroup) = with(context) {
        linearLayout {
            lparams(matchParent, wrapContent)
            button("确认答案") {
                id = R.id.tv_commit
                textSize = 15f
                textColor = resources.getColor(R.color.color_ffffff)
                backgroundResource = R.drawable.bg_shape_5_4c84ff
            }.lparams(matchParent, dip(40)) {
                verticalMargin = dip(20)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position > getDateCount() - 1) return super.getItemViewType(position)
        if (getData()[position].type == "NODE") return SUB
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mContext == null) mContext = parent.context
        return if (viewType == SUB) ViewHolder(subView(parent))
        else super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == SUB) subConvert(holder, getData()[position - if (getIsHeader()) 1 else 0])
        else super.onBindViewHolder(holder, position)
    }

    companion object {
        const val SUB = 0x15d4
    }
}