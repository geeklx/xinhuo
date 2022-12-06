package tuoyan.com.xinghuo_dayingindex.ui.video

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.matchParent
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean


class AudioListDialog(
    contxt: Context,
    val resList: List<ResourceListBean>,
    var onClick: (pos: Int) -> Unit
) : Dialog(contxt, R.style.custom_dialog) {
    private var manager: LinearLayoutManager? = null
    private val adapter by lazy {
        AudioAdapter() {
            onClick(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_audio_list)
        initView()
    }

    fun moveToPosAndShow(pos: Int) {
        show()
        adapter.getData().forEachIndexed { index, resourceListBean ->
            resourceListBean.isChecked = index == pos
        }
        adapter.notifyDataSetChanged()
        manager?.scrollToPositionWithOffset(pos, 0)
    }

    private fun initView() {
        var tv_list_num = findViewById<TextView>(R.id.tv_list_num)
        tv_list_num.text = "(共${resList.size}节)"
        var rlvTime = findViewById<RecyclerView>(R.id.rlv_time)
        manager = LinearLayoutManager(context)
        rlvTime?.layoutManager = manager
        rlvTime?.adapter = adapter
        adapter.setData(resList)
        findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dismiss()
        }
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

class AudioAdapter(var onClick: (pos: Int) -> Unit) : BaseAdapter<ResourceListBean>() {
    override fun convert(holder: ViewHolder, item: ResourceListBean) {
        holder.setText(R.id.tv_name, item.name).setSelected(R.id.tv_name, item.isChecked)
        var sort = holder.adapterPosition
        if (sort < 9) {
            holder.setText(R.id.tv_audio_sort, "0${sort + 1}")
        } else {
            holder.setText(R.id.tv_audio_sort, (sort + 1).toString())
        }
        val img_playing = holder.getView(R.id.img_playing) as ImageView
        val drawable = ContextCompat.getDrawable(
            img_playing.context,
            R.drawable.icon_playing_anim
        ) as AnimationDrawable
        img_playing.backgroundDrawable = drawable
        if (item.isChecked) {
            (holder.getView(R.id.tv_name) as TextView).typeface = Typeface.DEFAULT_BOLD
            holder.setVisible(R.id.img_playing, View.VISIBLE)
            holder.setVisible(R.id.tv_audio_sort, View.GONE)
            drawable.start()
        } else {
            (holder.getView(R.id.tv_name) as TextView).typeface = Typeface.DEFAULT
            holder.setVisible(R.id.img_playing, View.GONE)
            holder.setVisible(R.id.tv_audio_sort, View.VISIBLE)
            drawable.stop()
        }
        if (holder.adapterPosition == 0) {
            holder.setVisible(R.id.line, View.GONE)
        } else {
            holder.setVisible(R.id.line, View.VISIBLE)
        }
        holder.itemView.setOnClickListener {
            if (!item.isChecked) {
                getData().forEachIndexed { index, resourceListBean ->
                    resourceListBean.isChecked = index == holder.adapterPosition
                }
                onClick(holder.adapterPosition)
                notifyDataSetChanged()
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View =
        LayoutInflater.from(context).inflate(R.layout.layout_audio_item, null)
}