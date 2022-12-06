package tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * Created by Zzz on 2021/7/7
 * Email:
 */

class EBookWordAdapter(val onClick: (QuestionInfo) -> Unit) : BaseAdapter<QuestionInfo>(isFooter = true) {
    var currentPos = -1
        set(value) {
            field = value
            notifyItemRangeChanged(currentPos - 1, 2)
        }

    override fun convert(holder: ViewHolder, item: QuestionInfo) {
        holder.setText(R.id.tv_title, item.stem)
            .setText(R.id.tv_paraphrase, item.paraphrase)
        val tvTitle = holder.getView(R.id.tv_title) as TextView
        tvTitle.isSelected = item.useranswer == item.rightanswer
        val lav_play = holder.getView(R.id.lav_play) as LottieAnimationView
        if (currentPos == holder.adapterPosition) {
            lav_play.playAnimation()
        } else {
            lav_play.cancelAnimation()
            lav_play.progress = 0f
        }
        holder.itemView.setOnClickListener {
            val pos = currentPos
            currentPos = -1
            notifyItemChanged(pos)
            lav_play.playAnimation()
            Handler().postDelayed({
                lav_play.cancelAnimation()
                lav_play.progress = 0f
            }, 2000)
            onClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_ebook_word, parent, false)
    }

    override fun footerView(context: Context,parent: ViewGroup): View {
        val view = TextView(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 100f).toInt())
        view.layoutParams = params
        return view
    }
}