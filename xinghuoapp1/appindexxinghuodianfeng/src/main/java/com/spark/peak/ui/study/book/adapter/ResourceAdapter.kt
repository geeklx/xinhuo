package com.spark.peak.ui.study.book.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.ResourceItem
import com.spark.peak.bean.StudyResource
import com.spark.peak.utlis.DeviceUtil
import org.jetbrains.anko.*

/**
 * Created by 李昊 on 2018/5/10.
 */
class ResourceAdapter(
    private var itemClick: (ResourceItem?) -> Unit,
    private var downloadClick: (type: Int, id: Int, name: String, url: String, position: Int, count: String?, res: ResourceItem) -> Unit
) : BaseAdapter<StudyResource>(isEmpty = true, isFooter = true) {

    var isOwn = ""
    var nodeMap = mutableMapOf<String, Boolean>()

    /**
     * 正常数据
     */
    override fun convert(holder: ViewHolder, item: StudyResource) {
        val context = holder.itemView.context
        holder.itemView.layoutParams = if (nodeMap[item.nodeName] == false) {
            ViewGroup.LayoutParams(0, 0)
        } else {
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                DeviceUtil.dp2px(context, 60f).toInt()
            )
        }
        holder.setText(R.id.tv_data_name, item.name)
                .setEnabled(R.id.tv_state, when (item.downloadState) {
                    StudyResource.STATE_DONE -> false
                    else -> true
                }).setText(R.id.tv_state, when (item.downloadState) {
                    StudyResource.STATE_DEFAULT -> "下载"
                    StudyResource.STATE_DOWNLOADING -> "暂停"
                    StudyResource.STATE_PAUSED -> "下载"
                    StudyResource.STATE_DONE -> "已下载"
                    else -> "下载"
                }).setOnClickListener(R.id.tv_state) {
                    downloadClick(item.downloadState, item.downloadId, item.name, item.content, holder.adapterPosition, item.resource?.count, item.resource!!)
                }.setVisible(R.id.tv_state, if (isOwn == "1" && (item.resource?.type == "3" || item.resource?.type == "7" || item.resource?.type == "6")) View.VISIBLE else View.GONE) //仅当已拥有且资源类型为音频 视频 pdf 时 显示下载按钮
                .setImageResource(R.id.ic_type, when (item.resource?.type) {
                    //1-paper:试卷；2-paperAnalysis:试卷解析;3-media:视频  4- picture:图片 5-imagesText:图文;6-document:文档；7-frequency :音频; 8-外链
                    "3" -> R.drawable.src_type_video
                    "7" -> R.drawable.src_type_audio
                    "5" -> R.drawable.src_type_content
                    "4" -> R.drawable.src_type_content
                    "13" -> R.drawable.src_type_symbol
                    else -> R.drawable.src_type_doc
                })

        holder.itemView.setOnClickListener {
            itemClick(item.resource)
        }
        val pb = holder.getView(R.id.pb_download) as ProgressBar
        pb.progress = item.progress
    }


    override fun convertView(context: Context, parent: ViewGroup): View = LayoutInflater.from(context).inflate(R.layout.item_study_resourse, parent, false)

    /**
     * 节点
     */
    val DEFAULT_NODE = "XUNIMULU_AAAAA_BBBBB"
    private fun nodeConvert(holder: ViewHolder, item: StudyResource) {
        holder.setText(R.id.tv_node_name, item.name)
                .setSelected(R.id.iv_node_flag, nodeMap[item.name] ?: false)
        holder.itemView.setOnClickListener {
            var flag = nodeMap[item.name] ?: false
            nodeMap[item.name] = !flag
            notifyDataSetChanged()
        }
    }

    private fun nodeView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_node_view, parent, false)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mContext == null) mContext = parent.context
        return if (viewType == NODE) ViewHolder(nodeView(parent.context, parent))
        else super.onCreateViewHolder(parent, viewType)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == NODE) nodeConvert(holder, getData()[position])
        else super.onBindViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        if (getData().isNotEmpty()){
            if (position > getDateCount() - 1) return super.getItemViewType(position)
            if (getData()[position].type == StudyResource.TYPE_NODE) return NODE
        }
        return super.getItemViewType(position)
    }

    companion object {
        const val NODE = 0x66
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context){
        linearLayout {
            lparams(matchParent, matchParent)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            imageView {
                imageResource = R.drawable.empty_study
            }.lparams {
                topMargin = dip(40)
            }
            textView {
                text = "暂无配套资源"
                textSize = 15f
                textColor = Color.parseColor("#666666")
            }.lparams {
                topMargin = dip(10)
            }
        }
    }

    override fun footerView(context: Context): View {
        val view = TextView(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            DeviceUtil.dp2px(context, 60f).toInt()
        )
        view.layoutParams = params
        return view
    }
}