package tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_material_qes.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookExerciseActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.ExercisePagerAdapter
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import java.util.*

private const val ITEM = "ExerciseFrameItem"

/**
 * 材料题
 */
class MaterialQesFragment : LifeV4Fragment<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_material_qes

    private val itemData by lazy { arguments?.getSerializable(ITEM) as? ExerciseFrameItem }
    private val activity by lazy { requireActivity() as EBookExerciseActivity }
    var qIndex = 0//当前小节第一个题位置-1
    var answerIndex = 0 //当前小题位于当前小节的位置
    var qSort = ""//
    private val fragmentList by lazy { mutableListOf<Fragment>() }

    companion object {
        @JvmStatic
        fun newInstance(param: ExerciseFrameItem) =
            MaterialQesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ITEM, param)
                }
            }
    }

    override fun configView(view: View?) {
        super.configView(view)
    }

    override fun initData() {
        super.initData()
        val gson = GsonBuilder().disableHtmlEscaping().create()
        itemData?.let { question ->
            val params = RadioGroup.LayoutParams(DeviceUtil.dp2px(this.requireContext(), 70f).toInt(), RadioGroup.LayoutParams.MATCH_PARENT)
            tv_content.text = Html.fromHtml(question.questionInfo?.stem)
            val list = question.questionInfo?.item as? ArrayList<*>
            list?.forEachIndexed { index, infoItem ->
                val info = gson.fromJson(gson.toJson(infoItem), QuestionInfo::class.java)//any 转为 questionInfo类型
                val rb1 = RadioButton(this.requireContext())
                rb1.layoutParams = params
                rb1.textSize = 17f
                rb1.text = "第${info.sort}题"
                rb1.setTextColor(ContextCompat.getColorStateList(this.requireContext(), R.color.color_008aff_c4cbde))
                rb1.typeface = Typeface.DEFAULT_BOLD
                rb1.gravity = Gravity.CENTER
                rb1.background = null
                rb1.buttonDrawable = null
                rg_qes.addView(rb1)
                fragmentList.add(ChoiceQesFragment.newInstance(info))
                if (index == 0) {
                    qSort = info.sort
                    rb1.isChecked = true
                }
            }
            vp_exercise.offscreenPageLimit = 10
            vp_exercise.adapter = ExercisePagerAdapter(childFragmentManager, fragmentList)
//            getLrcData(question.questionInfo?.lrcUrl ?: "")
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        rg_qes.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                vp_exercise.currentItem = group.indexOfChild(group.findViewById(checkedId))
                for (index in 0 until group.childCount) {
                    val tempV = group.getChildAt(index) as RadioButton
                    tempV.textSize = 15f
                }
                val v = group.findViewById(checkedId) as RadioButton
                v.textSize = 17f
            }
        }
        vp_exercise.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(position: Int) {
                answerIndex = position
                val rb = rg_qes.getChildAt(position) as RadioButton
                if (!rb.isChecked) {
                    rb.isChecked = true
                }
                activity.initTitle()
            }

            override fun onPageScrollStateChanged(p0: Int) {
            }
        })
    }

    fun scrollPos(pos: Int) {
        vp_exercise.currentItem = pos
    }

//    private fun getLrcData(url: String) {
//        if (url.isNotEmpty()) {
//            presenter.lrc(url) { data ->
//                lrcData = LrcDataBuilder().Build(data, parser)
//            }
//        }
//    }

    fun goNext() {
        if (vp_exercise.currentItem == fragmentList.size - 1) {
            activity.goNext()
        } else {
            answerIndex += 1
            vp_exercise.currentItem = answerIndex
        }
    }
}