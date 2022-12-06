package tuoyan.com.xinghuo_dayingindex.ui.video

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import org.jetbrains.anko.matchParent
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.AudioTime
import java.text.SimpleDateFormat
import java.util.*


class AudioTimeCloseDialog(contxt: Context, var onClick: (item: AudioTime) -> Unit) :
    Dialog(contxt, R.style.custom_dialog) {
    private var audioTimes = mutableListOf<AudioTime>()
    private val adapter by lazy {
        TimeAdapter() { it, isDis ->
            if (isDis) {
                onClick(it)
            }
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_audio_time_close)
        initTimes()
        initView()
    }

    fun resetTimes() {
        initTimes()
        adapter.setData(audioTimes)
    }

    private fun initTimes() {
        audioTimes.clear()
        audioTimes.add(AudioTime(1, "不开启", 0, true))
        audioTimes.add(AudioTime(2, "播完当前音频", 0, false))
        audioTimes.add(AudioTime(3, "15分钟后", 15, false))
        audioTimes.add(AudioTime(4, "30分钟后", 30, false))
        audioTimes.add(AudioTime(5, "60分钟后", 60, false))
    }

    private fun initView() {
        var rlvTime = findViewById<RecyclerView>(R.id.rlv_time)
        rlvTime.layoutManager = LinearLayoutManager(context)
        rlvTime.adapter = adapter
        adapter.setData(audioTimes)
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

class TimeAdapter(var onClick: (item: AudioTime, Boolean) -> Unit) : BaseAdapter<AudioTime>() {
    private val format by lazy { SimpleDateFormat("aa hh:mm", Locale.ENGLISH) }
    override fun convert(holder: ViewHolder, item: AudioTime) {
        holder.setText(R.id.tv_name, item.name).setSelected(R.id.tv_name, item.isChecked)
        (holder.getView(R.id.chb_checked) as CheckBox).isChecked = item.isChecked
        if (holder.adapterPosition == 0) {
            holder.setVisible(R.id.line, View.GONE)
        } else {
            holder.setVisible(R.id.line, View.VISIBLE)
        }
        if (item.isChecked) {
            (holder.getView(R.id.tv_name) as TextView).typeface = Typeface.DEFAULT_BOLD
        } else {
            (holder.getView(R.id.tv_name) as TextView).typeface = Typeface.DEFAULT
        }
        if (item.isChecked && item.time > 0) {
            var calendaer = Calendar.getInstance()
            calendaer.add(Calendar.MINUTE, item.time)
            holder.setText(R.id.chb_checked, format.format(calendaer.time) + " 停止播放")
        } else {
            holder.setText(R.id.chb_checked, "")
        }
        holder.itemView.setOnClickListener {
            if (!item.isChecked) {
                getData().forEach {
                    it.isChecked = it == item
                }
                notifyDataSetChanged()
                onClick(item, true)
            } else {
                onClick(item, false)
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View =
        LayoutInflater.from(context).inflate(R.layout.layout_audio_time, null)
}