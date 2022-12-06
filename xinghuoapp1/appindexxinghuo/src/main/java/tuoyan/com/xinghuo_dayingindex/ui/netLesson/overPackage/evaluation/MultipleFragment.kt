package tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.evaluation

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.BaseV4Fragment
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.AnswerItem
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseModel
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfoItem
import kotlin.properties.Delegates

/**x
 */
class MultipleFragment : BaseV4Fragment() {
    override val layoutResId = 0
    private val parentActivity by lazy { activity as EntryActivity }
    private val adapter by lazy {
        MultipleAdapter {
            //TODO 设置答案，选择题没有小题，order永远为1
            var answerItem = AnswerItem("1", it.order)
            exerciseModel.answers.clear()
            exerciseModel.answers.add(answerItem)
            parentActivity.onCheck()
        }
    }

    private val exerciseModel by lazy { arguments?.getSerializable(DATA) as ExerciseModel }

    var errorTextView by Delegates.notNull<TextView>()
    override fun initView() = UI {
        relativeLayout {
            recyclerView {
                layoutManager = GridLayoutManager(context, 5)
                adapter = this@MultipleFragment.adapter
                horizontalPadding = dip(15)
            }.lparams(matchParent, matchParent)

            errorTextView = textView {
                text = "不支持的题型"
                textSize = 15f
                visibility = View.GONE
            }.lparams {
                centerInParent()
            }
        }

    }.view

    companion object {
        val DATA = "data"

        fun newInstance(data: ExerciseModel): MultipleFragment =
            MultipleFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(DATA, data)
                }
            }
    }


    override fun initData() {
        var infoJson = Gson().toJson(exerciseModel.questionInfo)
        var questionInfo = Gson().fromJson(infoJson, QuestionInfo::class.java)
        if (questionInfo.questionType == "5" || questionInfo.questionType == "5.0") {//TODO 材料题中的选择题时，比对item中的sort和model中的sort一致，则解析题目结构
            var itemJson = Gson().toJson(questionInfo.item)
            var item: List<QuestionInfo> = Gson().fromJson(itemJson, object : TypeToken<List<QuestionInfo>>() {}.type)
            item.forEach {
                if (it.sort == exerciseModel.sort) {
                    questionInfo = it
                }
            }
        }
        if (questionInfo.questionType != "1") {
            //如果当前题型不是选择题，则不继续解析
            errorTextView.visibility = View.VISIBLE
            return
        }
        var itemJson = Gson().toJson(questionInfo.item)
        var item: List<QuestionInfoItem> = Gson().fromJson(itemJson, object : TypeToken<List<QuestionInfoItem>>() {}.type)
        adapter.userAnswer = questionInfo.useranswer
        adapter.setData(item)
    }
}

private class MultipleAdapter(var onClick: (QuestionInfoItem) -> Unit) : BaseAdapter<QuestionInfoItem>() {
    private var currentView: View? = null
    var userAnswer = ""
    override fun convert(holder: ViewHolder, item: QuestionInfoItem) {
        if (userAnswer.isNotEmpty()) {
            if (userAnswer == item.order) {
                currentView = holder.getView(R.id.tv_option)
                currentView?.let {
                    (it as? TextView)?.isSelected = true
                }
                userAnswer = ""
            }
        }
        holder.setText(R.id.tv_option, item.order)
            .setOnClickListener(R.id.tv_option) { it ->
                currentView?.let {
                    (it as? TextView)?.isSelected = false
                }
                currentView = it
                (it as? TextView)?.isSelected = true
                onClick(item)
            }
    }

    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
        linearLayout {
            lparams(matchParent, wrapContent)
            verticalPadding = dip(10)
            gravity = Gravity.CENTER
            textView {
                id = R.id.tv_option
                gravity = Gravity.CENTER
                textSize = 14f
                setTextColor(ContextCompat.getColorStateList(context, R.color.color_white_222))
                backgroundResource = R.drawable.bg_shape_39_4c84ff
            }.lparams(dip(39), dip(39))
        }
    }
}