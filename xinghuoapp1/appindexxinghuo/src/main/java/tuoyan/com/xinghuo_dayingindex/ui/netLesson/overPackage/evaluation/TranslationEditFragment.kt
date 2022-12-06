package tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.evaluation

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.BaseV4Fragment
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseModel
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis

/**x
 */
class TranslationEditFragment : BaseV4Fragment() {
    override val layoutResId = 0
    private val adapter by lazy {
        EditAdapter {
            //未购买并且是只配置人工付费
            if (exerciseModel.questionInfo!!.isOwn != "1" && exerciseModel.questionInfo!!.evalMode == "2") {
                mToast("请选择批改方式")
            } else {
                PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    parentActivity.selectDialog.show()
                }
            }
        }
    }
    private val parentActivity by lazy { activity as EntryActivity }

    override fun initView() = UI {
        verticalLayout {
            horizontalPadding = dip(15)
            recyclerView {
                overScrollMode = LinearLayout.OVER_SCROLL_NEVER
                layoutManager = GridLayoutManager(context, 3)
                verticalPadding = dip(10)
                adapter = this@TranslationEditFragment.adapter
            }.lparams(matchParent, dip(120))
            textView("最多可上传3张图片") {
                verticalPadding = dip(5)
                textSize = 11f
                textColor = resources.getColor(R.color.color_8d95a1)
            }
            space().lparams(wrapContent, 0, 1f)
            button("确认答案") {
                textSize = 15f
                textColor = resources.getColor(R.color.color_ffffff)
                backgroundResource = R.drawable.bg_shape_5_4c84ff
                setOnClickListener {
                    commit()
                }
            }.lparams(matchParent, dip(40)) {
                verticalMargin = dip(20)
            }
        }

    }.view


    private val exerciseModel by lazy { arguments?.getSerializable(DATA) as ExerciseModel }

    companion object {
        val DATA = "data"
        fun newInstance(data: ExerciseModel): TranslationEditFragment =
            TranslationEditFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(DATA, data)
                }
            }
    }

    fun addImg(path: String) {
        adapter.getData().add(path)
        adapter.setFooter(adapter.getData().size < 3)
        adapter.notifyDataSetChanged()
    }

    private fun commit() {
        exerciseModel.imgPaths.clear()
        exerciseModel.imgPaths.addAll(adapter.getData())
        parentActivity.onCheck()
    }
}

private class EditAdapter(var addClick: () -> Unit) : BaseAdapter<String>(isFooter = true) {
    override fun convert(holder: ViewHolder, item: String) {
        holder.setImageUrl(R.id.iv_img, item)
            .setOnClickListener(R.id.iv_close) {
                remove(holder.layoutPosition)
                setFooter(getDateCount() < 3)
            }
    }

    override fun footer(holder: ViewHolder) {
        holder.setOnClickListener(R.id.iv_img) {
            addClick()
        }

    }

    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
        frameLayout {
            lparams(dip(100), dip(100))
            imageView {
                id = R.id.iv_img
            }.lparams(matchParent, matchParent)
            imageView(R.mipmap.ic_close) {
                id = R.id.iv_close
                padding = dip(3)
            }.lparams {
                gravity = Gravity.END
            }
        }
    }

    override fun footerView(context: Context,parent: ViewGroup) = with(context) {
        frameLayout {
            lparams(dip(100), dip(100))
            imageView(R.drawable.ic_over_up_lmg) {
                id = R.id.iv_img
            }.lparams(matchParent, matchParent)
        }
    }
}
