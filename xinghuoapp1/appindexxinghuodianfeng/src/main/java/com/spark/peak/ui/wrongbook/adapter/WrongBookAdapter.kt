package com.spark.peak.ui.wrongbook.adapter

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Html
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.WrongBook
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

/**
 * Created by 李昊 on 2018/5/15.
 */
class WrongBookAdapter(private var onExpand : (position : Int) -> Unit) : BaseAdapter<Any>(isEmpty = true){
    var openList = mutableListOf<Int>()

    override fun convert(holder: ViewHolder, item: Any) {
        var oAdapter = WrongExAdapter({},{

        }){}
        var dataList = mutableListOf<Any>()
        dataList.add(1)
        dataList.add(1)
        dataList.add(1)
        dataList.add(1)

//        oAdapter.setData(dataList)

        var tv_title = holder.getView(R.id.tv_title) as TextView
        holder.setText(R.id.tv_title,"2018年4月（3题）")
                .setOnClickListener(R.id.tv_title){
                    if (openList.contains(holder.adapterPosition)){
                        openList.remove(holder.adapterPosition)
                        holder.setVisible(R.id.rv_exercise,View.GONE)

                        var draw = mContext!!.resources.getDrawable(R.drawable.ic_arrow_down)
                        draw.setBounds(0, 0, draw.minimumWidth, draw.minimumHeight)
                        tv_title.setCompoundDrawables(null,null,draw,null)
                    }else{
                        openList.add(holder.adapterPosition)
                        holder.setVisible(R.id.rv_exercise,View.VISIBLE)

                        var draw = mContext!!.resources.getDrawable(R.drawable.ic_arrow_up)
                        draw.setBounds(0, 0, draw.minimumWidth, draw.minimumHeight)
                        tv_title.setCompoundDrawables(null,null,draw,null)

                        onExpand(holder.adapterPosition)
                    }
                }
                .setAdapter(R.id.rv_exercise,oAdapter)
                .setVisible(R.id.rv_exercise,if (openList.contains(holder.adapterPosition)) View.VISIBLE else View.GONE)
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
        relativeLayout {
            lparams(matchParent, wrapContent)
            textView {
                id = R.id.tv_title
                textSize = 11f
                textColor = Color.parseColor("#1e1e1e")
                backgroundColor = Color.parseColor("#F7F7F7")

                var draw = resources.getDrawable(R.drawable.ic_arrow_down)
                draw.setBounds(0, 0, draw.minimumWidth, draw.minimumHeight)
                setCompoundDrawables(null,null,draw,null)
                gravity = Gravity.CENTER_VERTICAL
                leftPadding = dip(15)
                rightPadding = dip(15)

            }.lparams(matchParent,dip(35))

            recyclerView {
                id = R.id.rv_exercise
                layoutManager = LinearLayoutManager(context)
                backgroundColor = Color.parseColor("#ffffff")
            }.lparams(matchParent, matchParent){
                below(R.id.tv_title)
            }

            view {
                backgroundColor = Color.parseColor("#ececec")
            }.lparams(matchParent,2){
                below(R.id.tv_title)
            }
        }
    }
}

class WrongExAdapter(var onClick : (item : WrongBook) -> Unit, var deleteClick: (item : WrongBook) -> Unit, var more: ()-> Unit) : BaseAdapter<WrongBook>(isEmpty = true, isMore = true){
    override fun convert(holder: ViewHolder, item: WrongBook) {
        holder.setText(R.id.tv_name,Html.fromHtml(item.questionsubject))
                .setText(R.id.tv_subject,item.subjectname)
                .setText(R.id.tv_create_time,item.time)
                .setBackgroundResource(R.id.tv_subject,getBackground(item.subjectname))
                .setVisible(R.id.tv_subject, if (item.subjectname.isEmpty()) View.GONE else View.VISIBLE)
        holder.itemView.setOnClickListener {
            onClick(getData()[holder.adapterPosition])
        }
        holder.itemView.setOnLongClickListener {
            deleteClick(getData()[holder.adapterPosition])
            true
        }
    }

    fun getBackground(subject: String): Int{
        return R.drawable.shape_corner_yuwen
//        return when(subject){
//            "语文" -> R.drawable.shape_corner_yuwen
//            "数学" -> R.drawable.shape_corner_shuxue
//            "英语" -> R.drawable.shape_corner_yingyu
//            "历史" -> R.drawable.shape_corner_lishi
//            "地理" -> R.drawable.shape_corner_dili
//            "生物" -> R.drawable.shape_corner_shengwu
//            "政治" -> R.drawable.shape_corner_zhengzhi
//            "物理" -> R.drawable.shape_corner_wuli
//            "化学" -> R.drawable.shape_corner_huaxue
//            else -> R.drawable.shape_corner_yuwen
//        }
    }
    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
        relativeLayout {
            lparams(matchParent, wrapContent)
            textView {
                id = R.id.tv_name
                textSize = 15f
                textColor = Color.parseColor("#1e1e1e")

                maxLines = 2
                ellipsize = TextUtils.TruncateAt.END
            }.lparams(matchParent, wrapContent){
                margin = dip(15)
            }

            textView {
                id = R.id.tv_subject
                backgroundResource = R.drawable.shape_corner_39db00
                textColor = Color.parseColor("#1482ff")
                textSize = 8f
                topPadding = dip(3)
                bottomPadding = dip(3)
                leftPadding = dip(5)
                rightPadding = dip(5)
            }.lparams{
                leftMargin = dip(15)
                bottomMargin = dip(15)
                alignParentLeft()
                below(R.id.tv_name)
            }

            textView {
                id = R.id.tv_create_time
                textColor = Color.parseColor("#666666")
                textSize = 11f
            }.lparams{
                rightMargin = dip(15)
                bottomMargin = dip(15)
                alignParentRight()
                below(R.id.tv_name)
            }

            view {
                backgroundColor = Color.parseColor("#ececec")
            }.lparams(matchParent,2){
                below(R.id.tv_create_time)
            }
        }
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        linearLayout {
            lparams(matchParent, matchParent)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            imageView {
                imageResource = R.drawable.empty_study
            }.lparams {
                topMargin = dip(120)
            }

            textView {
                text = "暂无错题"
                textSize = 15f
                textColor = Color.parseColor("#666666")
            }.lparams {
                topMargin = dip(10)
            }
        }
    }

    override fun loadMore(holder: ViewHolder) {
        super.loadMore(holder)
        more()
    }

}