package tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.report

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.BookList
import tuoyan.com.xinghuo_dayingindex.bean.Lesson

/**
 * 创建者：
 * 时间：  2018/9/27.
 */
class ReportAdapter(
    private var special: Boolean = false,
    val click: (position: Int, map: Lesson) -> Unit
) : BaseAdapter<Lesson>() {


    override fun convert(holder: ViewHolder, item: Lesson) {
        if (special && holder.layoutPosition == 0) {
            holder.itemView.topPadding = mContext?.dip(10) ?: 30
        }
        holder.setText(R.id.tv_title_lesson, item.title)
            .setText(R.id.iv_type_lesson, item.netSubjectName)
            .setVisible(
                R.id.iv_type_lesson,
                if (item.netSubjectName.isEmpty()) View.GONE else View.VISIBLE
            )
            .setText(
                R.id.tv_titme_lesson,
                if (item.liveTime.isNullOrEmpty()) (item.period
                    ?: "0") + "课时" else item.liveTime + "  |  " + (item.period ?: "0") + "课时"
            )
            .setText(
                R.id.tv_buyer_count,
                if (item.form == "1" || item.form == "3") "限购" + item.limitBuyers + "人  已有" + item.buyers + "人购买" else item.buyers + "人购买"
            )
            .setText(
                R.id.tv_price,
                if (item.isown == "1" || item.chargeType == "0") "" else "¥" + item.price
            )
            .setText(R.id.tv_discount, ownStr(item))
            .setTextColor(
                R.id.tv_discount,
                if (item.isLimitFree == "1") Color.parseColor("#ff3233") else Color.parseColor("#222831")
            )
            .setImageUrl(
                R.id.iv_cover_lesson,
                item.img,
                0,
                mContext!!.dip(6)
            )
        holder.itemView.setOnClickListener {
            click(holder.layoutPosition, item)
        }

        var tList = item.teacher.split(",")
        if (tList.size > 3) tList = tList.subList(0, 3)
        var llTeacher = holder.getView(R.id.ll_teacher) as LinearLayout
        showTeacher(tList, llTeacher)
    }

    private fun showTeacher(tList: List<String>, llTeacher: LinearLayout) {
        var teacherStr = ""
        llTeacher.removeAllViews()
        tList.forEach {
            teacherStr += it
            var tvTeacher = TextView(mContext)
            tvTeacher.singleLine = true
            with(tvTeacher) {
                text = it
                textColor = Color.parseColor("#ffffff")
                textSize = 10f
                gravity = Gravity.CENTER
                leftPadding = dip(6)
                rightPadding = dip(6)
                topPadding = dip(2)
                bottomPadding = dip(2)
                backgroundResource = R.drawable.bg_teacher_name
                var lp = LinearLayout.LayoutParams(wrapContent, wrapContent)
                lp.rightMargin = dip(4)
                layoutParams = lp
            }
            llTeacher.addView(tvTeacher)
        }

        if (teacherStr.length > 9 && tList.size > 1) {//TODO 若老师的姓名长度大于9，则显示老师数-1
            var tList = tList.subList(0, tList.size - 1)
            showTeacher(tList, llTeacher)
        }
    }

    private fun ownStr(item: Lesson) = if (item.isown == "1" && item.chargeType == "0") {
        "已领取"
    } else if (item.isown == "1" && item.chargeType == "1") {
        "已购买"
    } else if ("1" == item.saleOut) {
        "已售罄"
    } else if (item.chargeType == "0") {
        "免费"
    } else if (item.isLimitFree == "1") {
        "限时免费"
    } else {
        "¥" + item.disprice
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        relativeLayout {
            relativeLayout {
                backgroundResource = R.drawable.bg_lesson
                leftPadding = dip(15)
                rightPadding = dip(15)
                topPadding = dip(4)
                imageView {
                    id = R.id.iv_cover_lesson
                    scaleType = ImageView.ScaleType.FIT_XY
                }.lparams(dip(141), dip(95))

                textView {
                    id = R.id.iv_type_lesson
                    backgroundResource = R.drawable.bg_lesson_type
                    textColor = Color.parseColor("#ffffff")
                    textSize = 9f
                    leftPadding = dip(6)
                    rightPadding = dip(6)
                    gravity = Gravity.CENTER
                }.lparams(wrapContent, dip(16)) {
                    sameRight(R.id.iv_cover_lesson)
                }

                verticalLayout {
                    gravity = Gravity.CENTER_VERTICAL
                    textView {
                        id = R.id.tv_title_lesson
                        maxLines = 2
                        textColor = Color.parseColor("#222831")
                        textSize = 14f
                        paint.isFakeBoldText = true
                        ellipsize = TextUtils.TruncateAt.END
                    }

                    textView {
                        id = R.id.tv_titme_lesson
                        textSize = 10f
                        textColor = Color.parseColor("#c3c7cb")
                    }.lparams {
                        topMargin = dip(10)
                    }

                    linearLayout {
                        id = R.id.ll_teacher
                        orientation = LinearLayout.HORIZONTAL
                    }.lparams(wrapContent, wrapContent) {
                        topMargin = dip(8)
                    }
                }.lparams(matchParent, dip(95)) {
                    rightOf(R.id.iv_cover_lesson)
                    leftMargin = dip(10)
                    rightMargin = dip(10)
                }

                relativeLayout {
                    backgroundColor = Color.parseColor("#f6f7f8")
                    leftPadding = dip(15)
                    rightPadding = dip(15)
                    textView {
                        id = R.id.tv_buyer_count
                        textSize = 12f
                        textColor = Color.parseColor("#8d95a1")
                    }.lparams {
                        centerVertically()
                    }

                    textView {
                        id = R.id.tv_discount
                        textSize = 15f
                        textColor = Color.parseColor("#222831")
                    }.lparams {
                        centerVertically()
                        alignParentEnd()
                    }

                    textView {
                        id = R.id.tv_price
                        textSize = 11f
                        textColor = Color.parseColor("#c3c7cb")
                        paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                    }.lparams {
                        centerVertically()
                        leftOf(R.id.tv_discount)
                        rightMargin = dip(10)
                    }

                }.lparams(matchParent, dip(37)) {
                    below(R.id.iv_cover_lesson)
                }
            }.lparams(matchParent, dip(149))
        }
    }
}

class ReportBookAdapter(val click: (position: Int, map: BookList) -> Unit) :
    BaseAdapter<BookList>() {


    override fun convert(holder: ViewHolder, item: BookList) {

        holder.setText(R.id.tv_title_lesson, item.title ?: "")
//                .setText(R.id.iv_type_lesson, if (item.type == "1") "系统课"
//                else if (item.type == "2") "公开课"
//                else if (item.type == "3") "微课"
//                else "")
            .setText(R.id.tv_titme_lesson, item.edition ?: "")
            .setText(R.id.tv_buyer_count, item.saleNum ?: "0" + "人购买")
            .setText(R.id.tv_price, item.price ?: "")
            .setText(R.id.tv_discount, item.disprice ?: "")
            .setImageUrl(R.id.iv_cover_lesson, item.img ?: "")


    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        relativeLayout {
            relativeLayout {
                backgroundResource = R.drawable.bg_lesson
                leftPadding = dip(15)
                rightPadding = dip(15)
                topPadding = dip(4)
                imageView {
                    id = R.id.iv_cover_lesson
                    scaleType = ImageView.ScaleType.FIT_XY
                }.lparams(dip(95), dip(141))

//                textView {
//                    id = R.id.iv_type_lesson
//                    backgroundResource = R.drawable.bg_lesson_type
//                    textColor = Color.parseColor("#ffffff")
//                    textSize = 9f
//                    leftPadding = dip(6)
//                    rightPadding = dip(6)
//                    gravity = Gravity.CENTER
//                }.lparams(wrapContent, dip(16)) {
//                    sameRight(R.id.iv_cover_lesson)
//                }

                verticalLayout {
                    gravity = Gravity.CENTER_VERTICAL
                    textView {
                        id = R.id.tv_title_lesson
                        maxLines = 2
                        textColor = Color.parseColor("#222831")
                        textSize = 14f
                        paint.isFakeBoldText = true
                        ellipsize = TextUtils.TruncateAt.END
                    }

                    textView {
                        id = R.id.tv_titme_lesson
                        textSize = 10f
                        textColor = Color.parseColor("#c3c7cb")
                    }.lparams {
                        topMargin = dip(10)
                    }

                    space().lparams(wrapContent, 0, 1f)
                    relativeLayout {
                        backgroundColor = Color.parseColor("#f6f7f8")
                        leftPadding = dip(15)
                        rightPadding = dip(15)
                        textView {
                            id = R.id.tv_buyer_count
                            textSize = 12f
                            textColor = Color.parseColor("#8d95a1")
                        }.lparams {
                            centerVertically()
                        }

                        textView {
                            id = R.id.tv_discount
                            textSize = 15f
                            textColor = Color.parseColor("#222831")
                        }.lparams {
                            centerVertically()
                            alignParentEnd()
                        }

                        textView {
                            id = R.id.tv_price
                            textSize = 11f
                            textColor = Color.parseColor("#c3c7cb")
                            paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                        }.lparams {
                            centerVertically()
                            leftOf(R.id.tv_discount)
                            rightMargin = dip(10)
                        }

                    }.lparams(matchParent, dip(37))
                }.lparams(matchParent, dip(141)) {
                    rightOf(R.id.iv_cover_lesson)
                    leftMargin = dip(10)
                    rightMargin = dip(10)
                }

            }.lparams(matchParent, dip(149))
        }
    }

}