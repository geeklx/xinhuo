package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.dialog_sentence_setting.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.ui.book.adapter.LoopAdapter
import tuoyan.com.xinghuo_dayingindex.ui.book.adapter.TimeAdapter
import tuoyan.com.xinghuo_dayingindex.ui.video.adapter.SpeedAdapter

/**
 *
 */
class SentenceSettingDialog(
    context: Context,
    private val click: (Float, Int, Int, Boolean, Boolean) -> Unit
) : Dialog(context, R.style.custom_dialog) {
    private val speeds = listOf(0.5f, 0.8f, 1.0f, 1.2f, 1.5f, 1.8f, 2.0f)
    private val time = listOf(0, 2, 5, 10, 15, 30)
    private val loopNum = listOf(1, 2, 3, 4, 5, -1)

    private var speedSelected = 1.0f
    private var timeSelected = 0
    private var loopSelected = 1

    private val speedAdapter by lazy {
        SpeedAdapter {
            speedSelected = it
            click(speedSelected, timeSelected, loopSelected, img_sort.isSelected, img_warming.isSelected)
        }
    }

    private val timeAdapter by lazy {
        TimeAdapter {
            timeSelected = it
            click(speedSelected, timeSelected, loopSelected, img_sort.isSelected, img_warming.isSelected)
        }
    }

    private val loopAdapter by lazy {
        LoopAdapter {
            loopSelected = it
            click(speedSelected, timeSelected, loopSelected, img_sort.isSelected, img_warming.isSelected)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_sentence_setting)
        img_close.setOnClickListener { dismiss() }
        rlv_speed.layoutManager = GridLayoutManager(context, speeds.size)
        rlv_speed.adapter = speedAdapter
        speedAdapter.setData(speeds)

        rlv_time.layoutManager = GridLayoutManager(context, time.size)
        rlv_time.adapter = timeAdapter
        timeAdapter.setData(time)

        rlv_num.layoutManager = GridLayoutManager(context, loopNum.size)
        rlv_num.adapter = loopAdapter
        loopAdapter.setData(loopNum)
        img_sort.setOnClickListener {
            img_sort.isSelected = !img_sort.isSelected
            click(speedSelected, timeSelected, loopSelected, img_sort.isSelected, img_warming.isSelected)
        }
        img_warming.setOnClickListener {
            img_warming.isSelected = !img_warming.isSelected
            click(speedSelected, timeSelected, loopSelected, img_sort.isSelected, img_warming.isSelected)
        }
        img_warming.isSelected = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.BOTTOM)
    }
}