package com.spark.peak.ui.exercise.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Exercise
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

/**
 * Created by 李昊 on 2018/5/7.
 */


/**
 * 单元层级的adapter
 * 包含 单元名和 章节recyclerview
 */
class ExUnitAdapter(var paperClick :(key : String, name : String, parentKey : String) -> Unit, var toReport : (Exercise) -> Unit) : BaseAdapter<Exercise>(){

    override fun convert(holder: ViewHolder, item: Exercise) {
        holder.setText(R.id.tv_unit_name,item.catalogName)
                .setOnClickListener(R.id.rl_title){
                    //当前节点没有下一级列表，则使用当前节点的 catalogKey当做试卷id，否则执行 展开/收起 下一级列表操作
                    if (item.catalogList == null || item.catalogList!!.isEmpty()){
                        if (item.foreignKey == null){
                            paperClick(item.catalogKey?:"", item.catalogName?:"", item.parentKey?:"")
                        }else{
                            toReport(item)
                        }
                    }else{
                        if (holder.getVisible(R.id.rv_section) == View.GONE){
                            holder.setVisible(R.id.rv_section,View.VISIBLE)
                                    .setImageResource(R.id.iv_open_flag,R.drawable.ic_arrow_up)
                        }else{
                            holder.setVisible(R.id.rv_section,View.GONE)
                                    .setImageResource(R.id.iv_open_flag,R.drawable.ic_arrow_down)
                        }
                    }
                }

        item.catalogList?.let {
            if (it.isNotEmpty()){
                var oAdapter = ExSectionAdapter(paperClick, toReport)
                oAdapter.setData(it)
                var rv = holder.getView(R.id.rv_section) as RecyclerView
                rv.layoutManager = LinearLayoutManager(mContext)
                rv.adapter = oAdapter
                holder.setVisible(R.id.iv_open_flag,View.VISIBLE)
            }
        }

        if (item.catalogList==null){
            holder.setText(R.id.tv_flag,if (item.score == null) "0/"+item.total else item.score+"分")
            holder.setVisible(R.id.tv_flag,View.VISIBLE)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
        verticalLayout {
            relativeLayout {
                id = R.id.rl_title
                leftPadding = dip(15)
                rightPadding = dip(15)

                textView {
                    id = R.id.tv_unit_name
                    textSize = 15f
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    textColor = Color.parseColor("#1e1e1e")
                }.lparams{
                    centerVertically()
                }

                imageView {
                    id = R.id.iv_open_flag
                    imageResource = R.drawable.ic_arrow_down
                    visibility = View.GONE
                }.lparams{
                    centerVertically()
                    alignParentRight()
                }

                textView {
                    id = R.id.tv_flag
                    visibility = View.GONE
                }.lparams{
                    centerVertically()
                    alignParentRight()
                }

            }.lparams(matchParent,dip(45))
            view {
                backgroundColor = Color.parseColor("#f7f7f7")
            }.lparams(matchParent,2)

            recyclerView {
                id = R.id.rv_section
                visibility = View.GONE
            }.lparams(matchParent, wrapContent)

        }
    }

}

/**
 * 章节层级adapter
 * 包含 章节名 和 练习recyclerview
 */
class ExSectionAdapter(var paperClick :(key : String, name : String, parentKey : String) -> Unit, var toReport : (Exercise) -> Unit) : BaseAdapter<Exercise>(){
    var openFlagList = mutableListOf<Int>()

    override fun convert(holder: ViewHolder, item: Exercise) {
        var tvName = holder.getView(R.id.tv_section_name) as TextView
        tvName.textColor = if (openFlagList.contains(holder.adapterPosition)) Color.parseColor("#1482ff") else Color.parseColor("#1e1e1e")

        holder.setText(R.id.tv_section_name,item.catalogName)
                .setVisible(R.id.iv_open_flag,if (item.catalogList == null || item.catalogList!!.isEmpty()) View.GONE else View.VISIBLE)
                .setOnClickListener(R.id.rl_title){
                    //当前节点没有下一级列表，则使用当前节点的 catalogKey当做试卷id，否则执行 展开/收起 下一级列表操作
                    if (item.catalogList == null || item.catalogList!!.isEmpty()){
                        if (item.foreignKey == null){
                            paperClick(item.catalogKey?:"", item.catalogName?:"", item.parentKey?:"")
                        }else{
                            toReport(item)
                        }
                    }else{
                        if (openFlagList.contains(holder.adapterPosition)){
                            openFlagList.remove(holder.adapterPosition)
                            holder.setVisible(R.id.rv_exercise,View.GONE)
                                    .setImageResource(R.id.iv_open_flag,R.drawable.ic_arrow_down)
                            tvName.textColor = Color.parseColor("#1e1e1e")
                        }else{
                            openFlagList.add(holder.adapterPosition)
                            holder.setVisible(R.id.rv_exercise,View.VISIBLE)
                                    .setImageResource(R.id.iv_open_flag,R.drawable.ic_arrow_up)
                            tvName.textColor = Color.parseColor("#1482ff")
                        }
                    }
                }


        item.catalogList?.let {
            if (it.isNotEmpty()){
                var oAdapter = ExerciseAdapter(paperClick,toReport)
                oAdapter.setData(it)
                var rv = holder.getView(R.id.rv_exercise) as RecyclerView
                rv.layoutManager = LinearLayoutManager(mContext)
                rv.adapter = oAdapter

                rv.visibility = if (openFlagList.contains(holder.adapterPosition)) View.VISIBLE else View.GONE
                holder.setVisible(R.id.iv_open_flag,View.VISIBLE)
            }
        }

        if (item.catalogList==null){
            holder.setText(R.id.tv_flag,if (item.score == null) "0/"+item.total else item.score+"分")
            holder.setVisible(R.id.tv_flag,View.VISIBLE)
        }

    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
        verticalLayout {
            relativeLayout {
                id = R.id.rl_title
                leftPadding = dip(30)
                rightPadding = dip(15)

                textView {
                    id = R.id.tv_section_name
                    textSize = 15f
                }.lparams{
                    centerVertically()
                }

                imageView {
                    id = R.id.iv_open_flag
                    imageResource = R.drawable.ic_arrow_down
                    visibility = View.GONE
                }.lparams{
                    centerVertically()
                    alignParentRight()
                }

                textView {
                    id = R.id.tv_flag
                    visibility = View.GONE
                }.lparams{
                    centerVertically()
                    alignParentRight()
                }

            }.lparams(matchParent,dip(45))
            view {
                backgroundColor = Color.parseColor("#f7f7f7")
            }.lparams(matchParent,2)

            recyclerView {
                id = R.id.rv_exercise
                visibility = View.GONE
            }.lparams(matchParent, wrapContent)
        }
    }

}


/**
 * 练习列表的revyvlerview
 */
class ExerciseAdapter(var paperClick :(key : String, name : String, parentKey: String) -> Unit, var toReport : (Exercise) -> Unit) : BaseAdapter<Exercise>(){
    override fun convert(holder: ViewHolder, item: Exercise) {
        holder.setText(R.id.tv_exercise_name,item.catalogName)
                .setText(R.id.tv_exercise_mark,if (item.score == null) "0/"+item.total else item.score+"分")
                .setVisible(R.id.line_top,if (holder.adapterPosition == 0) View.GONE else View.VISIBLE)
                .setVisible(R.id.line_bottom,if (holder.adapterPosition == getData().size - 1) View.GONE else View.VISIBLE)
                .setOnClickListener(R.id.rl_title){
                    if (item.foreignKey == null){
                        paperClick(item.catalogKey?:"", item.catalogName?:"",item.parentKey?:"")
                    }else{
                        toReport(item)
                    }
                }

    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
        relativeLayout {
            id = R.id.rl_title
            lparams(matchParent,dip(45))
            view {
                backgroundColor = Color.parseColor("#f7f7f7")
            }.lparams(matchParent,2){
                alignParentBottom()
            }
            relativeLayout {
                id = R.id.rl_lines
                //画线和小圆点
                view {
                    backgroundResource = R.drawable.shape_dot
                }.lparams(dip(8),dip(8)){
                    centerInParent()
                }
                view {
                    id = R.id.line_top
                    backgroundColor = Color.parseColor("#cccccc")
                }.lparams(dip(2),dip(22)){
                    centerHorizontally()
                    alignParentTop()
                }
                view {
                    id = R.id.line_bottom
                    backgroundColor = Color.parseColor("#cccccc")
                }.lparams(dip(2),dip(22)){
                    centerHorizontally()
                    alignParentBottom()
                }
            }.lparams(dip(15), matchParent){
                leftMargin = dip(30)
            }

            textView {
                id = R.id.tv_exercise_name
                textSize = 15f
                textColor = Color.parseColor("#666666")
            }.lparams{
                centerVertically()
                rightOf(R.id.rl_lines)
                leftMargin = dip(15)
            }

            textView {
                id = R.id.tv_exercise_mark
                textSize = 14f
                textColor = Color.parseColor("#747474")
            }.lparams{
                centerVertically()
                alignParentRight()
                rightMargin = dip(15)
            }

        }
    }

}