package tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.TeacherListBean

/**
 * Created by  on 2018/10/9.
 */
class TeacherAdapter : BaseAdapter<TeacherListBean>() {
    override fun convert(holder: ViewHolder, item: TeacherListBean) {
        holder.setText(R.id.tv_teacher, item.teacherName)
            .setText(R.id.tv_teacher_info, item.introduce)
            .setCircularImage(R.id.iv_avatar, item.headImg ?: "")
    }

    override fun convertView(context: Context, parent: ViewGroup): View = context.relativeLayout {
        imageView {
            id = R.id.iv_avatar
        }.lparams(dip(34), dip(34)) {
            topMargin = dip(15)
            leftMargin = dip(15)
        }

        textView {
            id = R.id.tv_teacher
            textSize = 15f
            textColor = Color.parseColor("#222831")
            paint.isFakeBoldText = true
        }.lparams(wrapContent, wrapContent) {
            topMargin = dip(20)
            leftMargin = dip(15)
            rightOf(R.id.iv_avatar)
        }

        textView {
            id = R.id.tv_teacher_info
            textSize = 14f
            textColor = Color.parseColor("#222831")
        }.lparams(wrapContent, wrapContent) {
            topMargin = dip(17)
            rightMargin = dip(15)
            sameLeft(R.id.tv_teacher)
            below(R.id.tv_teacher)
        }
    }
}