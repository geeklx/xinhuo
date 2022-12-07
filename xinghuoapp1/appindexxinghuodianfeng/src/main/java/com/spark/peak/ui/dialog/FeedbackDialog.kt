package com.spark.peak.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.QFeedBack
import com.spark.peak.ui.exercise.detail.ExerciseDetailPresenter
import com.spark.peak.utlis.DeviceUtil
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

/**
 * 创建者： 霍述雷
 * 时间：  2018/1/5.
 */
class FeedbackDialog(context: Context, val presenter: ExerciseDetailPresenter, private val submint: (String, String) -> Unit) : Dialog(context) {
    private var editView: EditText? = null
    private var currentView: View? = null
    private var current: QFeedBack? = null
    private var rlc_view: RecyclerView? = null
    private val fbAdapter by lazy {
        FbAdapter() {
            current = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(with(context) {
            verticalLayout {
                backgroundResource = R.drawable.bg_radius_w_20
                horizontalPadding = dip(12)
                linearLayout {
                    gravity = Gravity.CENTER_VERTICAL
                    space().lparams(dip(15), wrapContent)
                    textView("题目反馈") {
                        textSize = 16f
                        gravity = Gravity.CENTER
                        textColorResource = R.color.color_222831
                    }.lparams(0, wrapContent, 1f)
                    imageView(R.mipmap.ic_close) {
                        setOnClickListener { dismiss() }
                    }.lparams(dip(12), dip(12))
                }.lparams(matchParent, dip(45))
                view { backgroundResource = R.drawable.divider }.lparams(matchParent, dip(0.5f))
                rlc_view = recyclerView {
                    layoutManager = GridLayoutManager(context, 2)
                    adapter = fbAdapter
                }
                editView = editText {
                    textSize = 14f
                    gravity = Gravity.START
                    maxLines = 3
                    padding = dip(10)
                    hint = "手动输入问题"
                    hintTextColor = resources.getColor(R.color.color_a7a7a7)
                    textColorResource = R.color.color_222831
                    backgroundResource = R.drawable.bg_shape_5_00_eee
                }.lparams(matchParent, dip(90)) {
                    topMargin = dip(15)
                }
                view { backgroundResource = R.drawable.divider }.lparams(matchParent, dip(0.5f)) {
                    topMargin = dip(15)
                }
                textView("提交") {
                    textSize = 16f
                    gravity = Gravity.CENTER
                    textColorResource = R.color.color_1482ff
                    setOnClickListener {
                        if (current == null) {
                            Toast.makeText(context, "请选择反馈类型", Toast.LENGTH_LONG).show()
                            return@setOnClickListener
                        }
                        if (editView?.text?.toString()?.trim().isNullOrEmpty()) {
                            Toast.makeText(context, "请输入问题", Toast.LENGTH_LONG).show()
                            return@setOnClickListener
                        }
                        submint(current?.id ?: "", editView?.text?.toString()?.trim() ?: "")
                    }
                }.lparams(matchParent, dip(45))
            }
        })
        presenter.getDictInfo {
            fbAdapter.setData(it)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = context.dip(285)
//        lp?.height = context.dip(133)
        window?.attributes = lp
        window?.setBackgroundDrawable(null)
        window?.setGravity(Gravity.CENTER)
    }
}

class FbAdapter(val onItemClick: (QFeedBack) -> Unit) : BaseAdapter<QFeedBack>() {
    private var selectIndex = -1
    override fun convert(holder: ViewHolder, item: QFeedBack) {
        holder.setSelected(R.id.tv_name, selectIndex == holder.adapterPosition)
                .setText(R.id.tv_name, item.name)
        holder.itemView.setOnClickListener {
            if (selectIndex != holder.adapterPosition) {
                onItemClick(item)
                selectIndex = holder.adapterPosition
                notifyDataSetChanged()
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        val text = TextView(context)
        text.id = R.id.tv_name
        text.textColorResource = R.color.color_222831
        text.textSize = 14f
        text.backgroundResource = R.drawable.bg_shape_5_1482ff_e8e8e8
        text.gravity = Gravity.CENTER
        val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 30f).toInt())
        params.margin = 10
        text.layoutParams = params
        return text

    }
}