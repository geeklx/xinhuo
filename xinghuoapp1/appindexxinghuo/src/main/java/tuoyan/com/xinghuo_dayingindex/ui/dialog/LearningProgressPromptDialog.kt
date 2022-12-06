package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.layout_couponlist_dialog_item.view.*
import kotlinx.android.synthetic.main.layout_googdslist_item.view.*
import kotlinx.android.synthetic.main.layout_learning_progress_prompt_dialog.*
import org.jetbrains.anko.matchParent
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import java.util.*

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class LearningProgressPromptDialog(context: Context) :
    Dialog(context, R.style.custom_dialog) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_learning_progress_prompt_dialog)
//        val iv_flower = findViewById<ImageView>(R.id.iv_flower)
//        val ic_close = findViewById<ImageView>(R.id.ic_close)
        ic_close.setOnClickListener({
            dismiss()
        })


    }






    fun setType(type: String, num: String,time:Int) {

//0未学习 1学习中（1.2至少学习完一节，否则都是学习进行中1.1） 2已完成学习
        when (type) {
            "0" -> {
                iv_emo.setImageResource(R.drawable.ic_learning_notice)
                iv_learning_text.setImageResource(R.drawable.ic_learning_do_it)
                iv_flower.visibility = View.GONE
                tv_1.text = "你还没有开始学习"
                tv_2.text = "迈出第一步\n 距离过级更近一步!"
            }
            "1.1" -> {
                iv_emo.setImageResource(R.drawable.ic_learning_notice)
                iv_learning_text.setImageResource(R.drawable.ic_learning_good_job)
                iv_flower.visibility = View.GONE

                tv_1.text = "你已成功迈出第一步"
                tv_2.text = "再接再励\n" +
                        "距离过级更近哦~"
            }
            "1.2" -> {
                iv_emo.setImageResource(R.drawable.ic_learning_notice)
                iv_learning_text.setImageResource(R.drawable.ic_learning_come_on)
                iv_flower.visibility = View.GONE

                tv_1.text = "你已完成${num}节必修课"
                tv_2.text = "继续努力\n" +
                        "付出终有回报!"
            }
            "2" -> {
                iv_emo.setImageResource(R.drawable.ic_learning_finish)
                iv_learning_text.setImageResource(R.drawable.ic_learning_good_job)
                iv_flower.visibility = View.VISIBLE
                Glide.with(context).asGif()
                    .load(R.drawable.ic_flower).into(iv_flower)
                tv_1.text = "你已完成所有必修课"
                tv_2.text = "恭喜你\n" +
                        "距离过级更近喽!"
            }
            else -> {


            }
        }
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                dismiss()
            }
        }, (1000*time).toLong(), 3000)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
//        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 9 //设置宽度
        lp?.width = matchParent

        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.CENTER)
    }

    var timer: Timer? = null


    override fun dismiss() {
        super.dismiss()
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }

    }
}


