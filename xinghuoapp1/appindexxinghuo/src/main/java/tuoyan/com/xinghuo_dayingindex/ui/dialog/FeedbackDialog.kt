package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.widget.TextView
import org.jetbrains.anko.matchParent
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.FeedbackQuestion

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class FeedbackDialog(val type: String, context: Context, val click: (item: FeedbackQuestion) -> Unit) : Dialog(context, R.style.custom_dialog) {
    private val adapter by lazy {
        Adapter() {
            click(it)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_feedback_dialog)
        val tv_title = findViewById<TextView>(R.id.tv_title)
        if ("2" == type) {
            tv_title.text = "优化类型"
        }
        findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dismiss()
        }
        val rlv_type = findViewById<RecyclerView>(R.id.rlv_type)
        rlv_type.layoutManager = LinearLayoutManager(context)
        rlv_type.adapter = adapter
        var strs = ArrayList<String>()
        strs.add("1")
        strs.add("2")
        strs.add("3")
        strs.add("4")
    }

    fun setData(data: List<FeedbackQuestion>) {
        adapter.setData(data)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
//        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 9 //设置宽度
        lp?.width = matchParent
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.BOTTOM)
    }

    class Adapter(val itemClick: (item: FeedbackQuestion) -> Unit) : BaseAdapter<FeedbackQuestion>() {
        override fun convert(holder: ViewHolder, item: FeedbackQuestion) {
            holder.setText(R.id.tv_content, item.name)
            holder.itemView.setOnClickListener {
                itemClick(item)
            }
        }

        override fun convertView(context: Context,parent: ViewGroup): View {
            return LayoutInflater.from(context).inflate(R.layout.layout_feedback_item, null)
        }

    }
}