package com.spark.peak.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.AudioRes
import kotlinx.android.synthetic.main.layout_dialog_list.*
import org.jetbrains.anko.matchParent

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class ListDialog(context: Context, val data: List<AudioRes>?, val click: (Int) -> Unit) :
    Dialog(context) {
    private val adapter by lazy {
        ListAdapter(data) {
            click(it)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        setContentView(with(context) {
//            verticalLayout {
//                backgroundResource = R.color.color_f2f2f2
//                recyclerView {
//                    layoutManager = LinearLayoutManager(context)
//                    val decoration = DividerItemDecoration(context, LinearLayout.VERTICAL)
//                    decoration.setDrawable(resources.getDrawable(R.drawable.line_eee))
//                    addItemDecoration(decoration)
//                    adapter = this@ListDialog.adapter
//
//                }.lparams(matchParent, dip(300)) {
//                    horizontalMargin = dip(15)
//                }
//                button("取消") {
//                    textSize = 15f
//                    gravity = Gravity.CENTER
//                    padding = 0
//                    typeface = Typeface.DEFAULT_BOLD
//                    textColor = resources.getColor(R.color.color_222831)
//                    backgroundResource = R.color.color_ffffff
//                    setOnClickListener {
//                        dismiss()
//                    }
//                }.lparams(matchParent, dip(55))
//            }
//        })
        setContentView(R.layout.layout_dialog_list)
        rlv.layoutManager = LinearLayoutManager(context)
        rlv.adapter = adapter
        close.setOnClickListener { dismiss() }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
//        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 9 //设置宽度
        lp?.width = matchParent
        window?.attributes = lp
        window?.setBackgroundDrawable(null)
        window?.setGravity(Gravity.BOTTOM)
    }

    fun setPos(pos: Int) {
        adapter.setPos(pos)
    }
}

class ListAdapter(data: List<AudioRes>?, val click: (Int) -> Unit) : BaseAdapter<AudioRes>() {
    private var pos = 0

    init {
        setData(data)
    }

    override fun convert(holder: ViewHolder, item: AudioRes) {
        holder.setText(R.id.tv_title, item.name.replace("/AA/", "   "))
            .setText(R.id.tv_time, "时长：${item.duration}")
            .setSelected(R.id.tv_title, holder.adapterPosition == pos)
            .setVisible(
                R.id.img_playing,
                if (holder.adapterPosition == pos) View.VISIBLE else View.GONE
            )
            .itemView.setOnClickListener {
                click(holder.layoutPosition)
            }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_dialog_item, parent, false)
    }

    fun setPos(pos: Int) {
        this.pos = pos
        notifyDataSetChanged()
    }
}