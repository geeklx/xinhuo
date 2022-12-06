package tuoyan.com.xinghuo_dayingindex.ui.exercise.detail

import android.os.Bundle
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem

/**
 * 做题模块转原生
 *对应ExerciseDetailFragment
 */
class ExerciseDetailKFragment : LifeV4Fragment<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = 0

    companion object {
        const val ITEM = "item"

        @JvmStatic
        fun newInstance(item: ExerciseFrameItem) =
            ExerciseDetailKFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ITEM, item)
                }
            }
    }
}
