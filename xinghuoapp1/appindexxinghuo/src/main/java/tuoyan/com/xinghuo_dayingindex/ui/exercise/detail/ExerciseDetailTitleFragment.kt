package tuoyan.com.xinghuo_dayingindex.ui.exercise.detail

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_exercise_detail_title.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem

/**
 * 做题模块转原生
 *对应ExerciseDetailFragment
 */
class ExerciseDetailTitleFragment : LifeV4Fragment<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_exercise_detail_title

    override fun initData() {
        arguments?.let {
            val item = it.getSerializable(ITEM) as ExerciseFrameItem
            tv_paper_name.text = item.paperName
            if ("2" == item.isSubtitle) {
                tv_group_name.text = item.groupName
                tv_paper_explain.text = item.paperExplain
            }
            tv_remarks.text = item.remarks
        }
    }

    companion object {
        private const val ITEM = "item"

        @JvmStatic
        fun newInstance(item: ExerciseFrameItem) =
            ExerciseDetailTitleFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ITEM, item)
                }
            }
    }
}
