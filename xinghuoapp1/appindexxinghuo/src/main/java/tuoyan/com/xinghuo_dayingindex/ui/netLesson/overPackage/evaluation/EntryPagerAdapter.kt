package tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.evaluation

import androidx.fragment.app.FragmentManager
import tuoyan.com.xinghuo_dayingindex.base.BaseV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseModel
import tuoyan.com.xinghuo_dayingindex.widegt.FragmentV4StatePagerAdapter


/**
 * 创建者：
 * 时间：  2018/9/21.
 */
class EntryPagerAdapter(fm: FragmentManager) : FragmentV4StatePagerAdapter(fm) {
    private var data = mutableListOf<ExerciseModel>()
    private var fList = mutableListOf<BaseV4Fragment>()

    override fun getItem(position: Int) = fList[position]

    fun setData(data: List<ExerciseModel>) {
        this.data = mutableListOf<ExerciseModel>()
        this.data.addAll(data)
        initFList()
        notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    fun getData(position: Int) = data[position]

    /**
     * 初始化fragment列表
     */
    public fun initFList() {
        this.fList = mutableListOf<BaseV4Fragment>()
        data.forEach { exerciseModel ->
            when (exerciseModel.type) {
                "1" -> fList.add(MultipleFragment.newInstance(exerciseModel))//选择题
                "6" -> {
//                  evalMode  0自判 ,1免费人工, 2付费人工, 10自判+免费人工,20自判+付费人工
                    val isOwn = exerciseModel.questionInfo!!.isOwn == "1"
                    if (isOwn || exerciseModel.questionInfo!!.evalMode == "1" || exerciseModel.questionInfo!!.evalMode == "2") {
                        fList.add(TranslationEditFragment.newInstance(exerciseModel))//老师判卷的主观题
                    } else {
                        fList.add(TranslationFragment.newInstance(exerciseModel))//自己判卷的主观题
                    }
                }
                else -> fList.add(MultipleFragment.newInstance(exerciseModel))
            }
        }
    }

    fun getFragment(index: Int) = fList[index]

    override fun getCount() = data.size
    fun index(item: ExerciseModel) = data.indexOf(item)

}