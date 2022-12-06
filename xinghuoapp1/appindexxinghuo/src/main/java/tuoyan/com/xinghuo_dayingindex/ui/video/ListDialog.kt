package tuoyan.com.xinghuo_dayingindex.ui.video

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_list.*
import org.jetbrains.anko.matchParent
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class ListDialog(
    context: Context,
    val data: List<BookRes>?,
    val click: (Int) -> Unit
) : Dialog(context, R.style.custom_dialog) {
    private val adapter by lazy {
        ListAdapter {
            click(it)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_list)
        tv_size.text = "（共${data?.size ?: 0}节）"
        rlv_content.layoutManager = LinearLayoutManager(context)
        rlv_content.adapter = adapter
        adapter.setData(data)
        img_close.setOnClickListener { dismiss() }
    }

    fun setPosition(pos: Int) {
        adapter.position = pos
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = matchParent
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.BOTTOM)
    }
}

class ListAdapter(val click: (Int) -> Unit) : BaseAdapter<BookRes>(isFooter = true) {
    var position = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: ViewHolder, item: BookRes) {
        holder.setText(R.id.tv_title, item.name)
            .setVisible(R.id.lav_play, if (position == holder.layoutPosition) View.VISIBLE else View.GONE)
            .setSelected(R.id.tv_title, position == holder.layoutPosition)
            .setTypeface(R.id.tv_title, Typeface.defaultFromStyle(if (position == holder.layoutPosition) Typeface.BOLD else Typeface.NORMAL))
            .itemView.setOnClickListener {
                position = holder.layoutPosition
                click(position)
            }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_dialog_audio, parent, false)
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }
}