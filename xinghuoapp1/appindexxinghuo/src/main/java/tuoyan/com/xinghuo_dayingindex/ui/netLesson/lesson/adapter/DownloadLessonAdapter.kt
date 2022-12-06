package tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Html
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DEFAULT
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DONE
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DOWNLOADING
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_PAUSED
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_NODE
import tuoyan.com.xinghuo_dayingindex.bean.LessonRes
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean

class DownloadLessonAdapter(var onClick: (Int, ResourceListBean) -> Unit, var onDelete: (Int, ResourceListBean) -> Unit) : BaseAdapter<ResourceListBean>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: ResourceListBean) {
        holder.setText(R.id.tv_title, item.name)
                .setText(R.id.tv_info, if (item.liveType == "1") Html.fromHtml("直播\u3000|\u3000${getLiveStateStr(item)}") else if (item.liveType == "4") "音频 | " + item.duration else "录播")
                .setVisible(R.id.rl_download_info, if (item.state == STATE_DEFAULT || item.state == STATE_DONE) View.INVISIBLE else View.VISIBLE).setVisible(R.id.tv_download, if ((item.liveType == "4" || item.liveType == "0" || (item.liveType == "1" && item.liveState == "4")) && item.downloadFlag == "1") View.VISIBLE else View.GONE) //TODO 直播课可回放状态才下载
                .setOnClickListener(R.id.tv_download) {
                    onClick(holder.adapterPosition, item)
                }.setOnClickListener(R.id.tv_delete) {
                    onDelete(holder.adapterPosition, item)
                }.setBackgroundResource(R.id.tv_download, when (item.state) {
                    STATE_DONE -> R.drawable.shape_download_cancle
                    STATE_DEFAULT -> R.drawable.shape_download_start
                    STATE_DOWNLOADING -> R.drawable.shape_download_pause
                    STATE_PAUSED -> R.drawable.shape_download_start
                    else -> R.drawable.shape_download_start
                }).setText(R.id.tv_download, when (item.state) {
                    STATE_DONE -> "删除"
                    STATE_DEFAULT -> "下载"
                    STATE_DOWNLOADING -> "暂停"
                    STATE_PAUSED -> "继续"
                    else -> "下载"
                }).setTextColor(R.id.tv_download, when (item.state) {
                    STATE_DONE -> Color.parseColor("#8d95a1")
                    STATE_DEFAULT -> Color.parseColor("#4c84ff")
                    STATE_DOWNLOADING -> Color.parseColor("#ffaf30")
                    STATE_PAUSED -> Color.parseColor("#4c84ff")
                    else -> Color.parseColor("#4c84ff")
                }).setVisible(R.id.rl_download_info, when (item.state) {
                    STATE_DONE,
                    STATE_DEFAULT -> View.INVISIBLE
                    STATE_DOWNLOADING,
                    STATE_PAUSED -> View.VISIBLE
                    else -> View.GONE
                }).setVisible(R.id.tv_info, when (item.state) {
                    STATE_DONE,
                    STATE_DEFAULT -> View.VISIBLE
                    STATE_DOWNLOADING,
                    STATE_PAUSED -> View.GONE
                    else -> View.GONE
                }).setSelected(R.id.tv_info, item.downloadFlag == "0")
                .setText(R.id.tv_download_info, item.downloadInfo)

        var pb = holder.getView(R.id.progress_bar) as ProgressBar
        pb.progress = item.progress
    }

    override fun convertView(context: Context, parent: ViewGroup): View = context.layoutInflater.inflate(R.layout.item_download_lesson, null)

    fun getLiveStateStr(item: ResourceListBean): String {
        return when (item.liveState) {
            "0" -> item.liveTime ?: ""
            "1" -> "<span style='color: #00ca0d'>直播中...</span>"
            "2" -> "<span style='color: #ffaf30'>回放生成中</span>"
            "3" -> "已转录"
            "4" -> "<span style='color: #8d95a1'>可回放...</span>"
            "5" -> "已结束"
            else -> item.liveTime ?: ""
        }
    }

    override fun emptyImageRes(): Int {
        return R.drawable.ic_no_content
    }

    override fun emptyText(): String {
        return "暂无课程"
    }

}


class DownloadResAdapter(var onClick: (Int, LessonRes) -> Unit) : BaseAdapter<LessonRes>(isEmpty = true) {
    //TODO 正常数据节点
    override fun convert(holder: ViewHolder, item: LessonRes) {
        var tv = holder.getView(R.id.tv_name) as TextView
        tv.setCompoundDrawablesWithIntrinsicBounds(when (item.type) {
            //1-paper:试卷；2-paperAnalysis:试卷解析;3-media:视频  4- picture:图片 5-imagesText:图文;6-document:文档；7-frequency :音频; 8-外链
            "3" -> R.drawable.src_type_video
            "7" -> R.drawable.src_type_audio
            "5" -> R.drawable.src_type_content
            "4" -> R.drawable.src_type_content
            else -> R.drawable.src_type_doc
        }, 0, 0, 0)
        holder.setText(R.id.tv_name, item.name)
                .setVisible(R.id.tv_download, when {
                    item.type == "3" || item.type == "6" || item.type == "7" -> View.VISIBLE
                    else -> View.GONE
                })
                .setOnClickListener(R.id.tv_download) {
                    onClick(holder.adapterPosition, item)
                }.setBackgroundResource(R.id.tv_download, when (item.state) {
                    STATE_DONE -> R.drawable.shape_download_cancle
                    STATE_DEFAULT -> R.drawable.shape_download_start
                    STATE_DOWNLOADING -> R.drawable.shape_download_pause
                    STATE_PAUSED -> R.drawable.shape_download_start
                    else -> R.drawable.shape_download_start
                }).setText(R.id.tv_download, when (item.state) {
                    STATE_DONE -> "删除"
                    STATE_DEFAULT -> "下载"
                    STATE_DOWNLOADING -> "暂停"
                    STATE_PAUSED -> "继续"
                    else -> "下载"
                }).setTextColor(R.id.tv_download, when (item.state) {
                    STATE_DONE -> Color.parseColor("#8d95a1")
                    STATE_DEFAULT -> Color.parseColor("#4c84ff")
                    STATE_DOWNLOADING -> Color.parseColor("#ffaf30")
                    STATE_PAUSED -> Color.parseColor("#4c84ff")
                    else -> Color.parseColor("#4c84ff")
                }).setVisible(R.id.rl_download_info, when (item.state) {
                    STATE_DONE,
                    STATE_DEFAULT -> View.GONE
                    STATE_DOWNLOADING,
                    STATE_PAUSED -> View.VISIBLE
                    else -> View.GONE
                })

        var pb = holder.getView(R.id.progress_bar) as ProgressBar
        pb.progress = item.progress
    }

    override fun convertView(context: Context, parent: ViewGroup): View = context.layoutInflater.inflate(R.layout.item_download_res, null)

    //TODO 章节节点
    val DEFAULT_NODE = "XUNIMULU_AAAAA_BBBBB"

    private fun nodeConvert(holder: ViewHolder, item: LessonRes) {
        if (item.name == DEFAULT_NODE) {
            holder.setVisible(R.id.tv_node_name, View.GONE)
        } else {
            holder.setVisible(R.id.tv_node_name, View.VISIBLE)
        }
        holder.setText(R.id.tv_node_name, item.name)
    }

    private fun nodeView(context: Context): View = with(context) {
        verticalLayout {
            backgroundResource = R.color.color_ffffff
            lparams(matchParent, wrapContent)
            textView {
                id = R.id.tv_node_name
                textSize = 14f
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_data_item_l, 0, R.drawable.ic_data_item_r, 0)
                compoundDrawablePadding = dip(15)
                textColor = resources.getColor(R.color.color_222)
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER_VERTICAL
                horizontalPadding = dip(15)
            }.lparams(matchParent, dip(50))
            view {
                backgroundResource = R.color.color_edeff0
            }.lparams(matchParent, dip(0.5f)) {
                marginStart = dip(45)
            }
        }
    }

    companion object {
        const val NODE = 0x66
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mContext == null) mContext = parent.context
        return if (viewType == NODE) ViewHolder(nodeView(parent.context))
        else super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == NODE) nodeConvert(holder, getData()[position])
        else super.onBindViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        if (getData().isNotEmpty()) {
            if (getData()[position].type == TYPE_NODE) return NODE
        }
        return super.getItemViewType(position)
    }

    override fun emptyImageRes(): Int {
        return R.drawable.ic_no_content
    }

    override fun emptyText(): String {
        return "暂无资源"
    }


}