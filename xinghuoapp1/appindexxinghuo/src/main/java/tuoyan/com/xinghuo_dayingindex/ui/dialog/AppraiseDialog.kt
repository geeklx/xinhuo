package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import kotlinx.android.synthetic.main.layout_appraise_dialog.*

import org.jetbrains.anko.matchParent
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.bean.Coupon
import java.util.*

/**
 * 评价弹窗
 */
class AppraiseDialog(context: Context, val click: (Int, Int, Int) -> Unit) :
    Dialog(context, R.style.custom_dialog) {

    var data: Coupon? = null
    var rating1 = 0
    var rating2 = 0
    var rating3 = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_appraise_dialog)
        tv_submit.setOnClickListener({
            if (isCheck())
                click(rating1, rating2, rating3)

        })
        iv_close.setOnClickListener({
            dismiss()
        })
        rb_1.setOnRatingChangeListener { ratingBar, rating, fromUser ->
            Log.e("ratingBar1", "rating:$rating")
            rating1 = rating.toInt()
//            isCheck()
        }
        rb_2.setOnRatingChangeListener { ratingBar, rating, fromUser ->
            Log.e("ratingBar2", "rating:$rating")
            rating2 = rating.toInt()
//            isCheck()
        }
        rb_3.setOnRatingChangeListener { ratingBar, rating, fromUser ->
            Log.e("ratingBar3", "rating:$rating")
            rating3 = rating.toInt()
//            isCheck()
        }
//
//        iv_close.setOnClickListener({
//            dismiss()
//        })
//
//
//        tv_get.setOnClickListener({
//
//            data?.let { it1 ->
//                if(data?.isOwn == "0") {
//                    click(it1)
//                }
//            };
//
//        })
    }

    fun isCheck(): Boolean {

        if (rating1 > 0 && rating2 > 0 && rating3 > 0) {
            tv_notice.visibility = View.GONE
            return true
        } else {
            var title = ""
            if (rating1 > 0)
            else {
                title += "课程内容/";
            }
            if (rating2 > 0)
            else {
                title += "课程方法/";
            }
            if (rating3 > 0)
            else {
                title += "课程效果/";
            }
            tv_notice.visibility = View.VISIBLE
            tv_notice.text = "您“${title.substring(0, title.lastIndexOf("/"))}”未打分，请打分后提交哦！"
            return false
        }
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

    override fun dismiss() {
        rb_1?.rating=0f
        rb_2?.rating=0f
        rb_3?.rating=0f
        super.dismiss()
    }

}