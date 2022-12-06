package tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.textColor
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.Promotional
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * Created by Zzz on 2020/7/22
 * Email:
 */

class LessonCouponAdapter : BaseAdapter<Promotional>() {
    override fun convert(holder: ViewHolder, item: Promotional) {
        holder.setText(R.id.tv_title, item.name)
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        var text = TextView(context)
        var params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, DeviceUtil.dp2px(context, 8f).toInt(), 0)
        text.id = R.id.tv_title
        text.setPadding(
            DeviceUtil.dp2px(context, 10f).toInt(),
            DeviceUtil.dp2px(context, 4.5f).toInt(),
            DeviceUtil.dp2px(context, 10f).toInt(),
            DeviceUtil.dp2px(context, 4.5f).toInt()
        )
        text.setBackgroundResource(R.drawable.shape_4_fff0ed)
        text.textSize = 11f
        text.textColor = ContextCompat.getColor(context, R.color.color_ff3f00)
        text.layoutParams = params
        return text
    }
}