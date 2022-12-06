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
import tuoyan.com.xinghuo_dayingindex.bean.AudioSpeed
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil


class AudioSpeedDialog(contxt: Context, var onClick: (item: AudioSpeed) -> Unit) : Dialog(contxt, R.style.custom_dialog) {
    private var audioTimes = mutableListOf<AudioSpeed>()
    private val adapter by lazy {
        SpeedAdapter() { it, isDis ->
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

    private fun initTimes() {
        var speed = SpUtil.audioSpeed
        if (speed == 0.5f) {
            audioTimes.add(AudioSpeed(1, "0.5x", 0.5f, true))
        } else {
            audioTimes.add(AudioSpeed(1, "0.5x", 0.5f, false))
        }
        if (speed == 0.8f) {
            audioTimes.add(AudioSpeed(2, "0.8x", 0.8f, true))
        } else {
            audioTimes.add(AudioSpeed(2, "0.8x", 0.8f, false))
        }
        if (speed == 1.0f) {
            audioTimes.add(AudioSpeed(3, "1.0x • 正常速度", 1.0f, true))
        } else {
            audioTimes.add(AudioSpeed(3, "1.0x • 正常速度", 1.0f, false))
        }
        if (speed == 1.2f) {
            audioTimes.add(AudioSpeed(4, "1.2x", 1.2f, true))
        } else {
            audioTimes.add(AudioSpeed(4, "1.2x", 1.2f, false))
        }
        if (speed == 1.5f) {
            audioTimes.add(AudioSpeed(5, "1.5x", 1.5f, true))
        } else {
            audioTimes.add(AudioSpeed(5, "1.5x", 1.5f, false))
        }
        if (speed == 1.8f) {
            audioTimes.add(AudioSpeed(6, "1.8x", 1.8f, true))
        } else {
            audioTimes.add(AudioSpeed(6, "1.8x", 1.8f, false))
        }
        if (speed == 2.0f) {
            audioTimes.add(AudioSpeed(7, "2.0x", 2f, true))
        } else {
            audioTimes.add(AudioSpeed(7, "2.0x", 2f, false))
        }
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

class SpeedAdapter(var onClick: (item: AudioSpeed, Boolean) -> Unit) : BaseAdapter<AudioSpeed>() {
    override fun convert(holder: ViewHolder, item: AudioSpeed) {
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
        holder.itemView.setOnClickListener {
            if (!item.isChecked) {
                getData().forEach {
                    it.isChecked = it == item
                }
                SpUtil.audioSpeed = item.speed
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