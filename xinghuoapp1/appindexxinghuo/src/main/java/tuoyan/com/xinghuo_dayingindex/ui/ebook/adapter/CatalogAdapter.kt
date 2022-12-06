package tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.EBookCatalog
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * Created by Zzz on 2021/7/1
 * Email:
 */

class CatalogAdapter(val onClick: (Int, EBookCatalog) -> Unit) : BaseAdapter<EBookCatalog>(isEmpty = true, isFooter = true) {
    var isOwn = "0"
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    override fun convert(holder: ViewHolder, item: EBookCatalog) {
        val title = if ("1" != isOwn && "1" == item.isTryLearn) "\u3000\u3000\u0020\u0020${item.name}" else item.name
        holder.setVisible(R.id.tv_try, if ("1" != isOwn && "1" == item.isTryLearn) View.VISIBLE else View.GONE)
            .setText(R.id.tv_title, title).setText(R.id.tv_time, if (item.lastTime.isNullOrEmpty()) "未学习" else "上次学习 ${item.lastTime}")
        val tv_time = holder.getView(R.id.tv_time) as TextView
        tv_time.isSelected = item.lastTime.isNotEmpty()
        holder.setOnClickListener(R.id.tv_exam) {
            if ("1" != isOwn && "1" != item.isTryLearn) {
                onClick(3, item)
            } else {
                onClick(1, item)
            }
        }
        holder.setOnClickListener(R.id.tv_practice) {
            if ("1" != isOwn && "1" != item.isTryLearn) {
                onClick(3, item)
            } else {
                onClick(2, item)
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_catalog_item, parent, false)
    }

    override fun emptyText(): String {
        return "暂无目录"
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }
}

class CatalogListenAdapter(val onClick: (Int, EBookCatalog) -> Unit) : BaseAdapter<EBookCatalog>(isHeader = true, isFooter = true) {
    var isOwn = "0"
        set(value) {
            field = value
//            notifyItemChanged(0)
            notifyDataSetChanged()
        }

    //是否阅读中的目录 0 课程详情  1：阅读中的目录 隐藏学习状态
    var catalogType = "0"
        set(value) {
            field = value
        }

    override fun convert(holder: ViewHolder, item: EBookCatalog) {
        val context = holder.itemView.context
        val tv_title = holder.getView(R.id.tv_title) as TextView
        val img_learned = holder.getView(R.id.img_learned) as ImageView
        val titles = item.name.trim().split("#")
        if (titles.size > 1) {
            val title = if ("0" == isOwn && "1" == item.isTryLearn) "\u3000\u3000\u0020\u0020${titles[1]}" else titles[1]
            tv_title.text = title
            holder.setText(R.id.tv_sort, titles[0])
        }
        holder.setVisible(R.id.img_learned, if ("1" == isOwn && "1" != catalogType && !item.lastTime.isNullOrEmpty() && ("4" == item.resourceType || "5" == item.resourceType)) View.VISIBLE else View.GONE)
            .setVisible(R.id.tv_temp2, if ("1" == isOwn && "1" != catalogType && ("2" == item.resourceType || "3" == item.resourceType)) View.VISIBLE else View.GONE)
            .setVisible(R.id.tv_state, if ("1" == isOwn && "1" != catalogType && ("2" == item.resourceType || "3" == item.resourceType)) View.VISIBLE else View.GONE)
            .setVisible(R.id.tv_tag, if ("0" == isOwn && "1" == item.isTryLearn) View.VISIBLE else View.GONE)
            .setSelected(R.id.tv_state, !item.lastTime.isNullOrEmpty())
            .setText(R.id.tv_state, if (item.lastTime.isNullOrEmpty()) "未学习" else "上次学习 ${item.lastTime}")
            .itemView.setOnClickListener {
                if ("1" != isOwn && "1" != item.isTryLearn) {
                    //去购买
                    onClick(3, item)
                } else {
                    //继续
                    onClick(1, item)
                }
            }
        val params = tv_title.layoutParams as ConstraintLayout.LayoutParams
        if ("1" != catalogType && img_learned.visibility == View.VISIBLE) {
            params.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
            params.rightToRight = -1
            tv_title.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 158f).toInt()
        } else {
            params.width = 0
            params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_catalog_listen_sec, parent, false)
    }

    fun firstLevelView(holder: ViewHolder, item: EBookCatalog) {
        val titles = item.name.trim().split("#")
        if (titles.size > 1) {
            holder.setText(R.id.tv_sort, titles[0]).setText(R.id.tv_title, titles[1])
        }
    }

    fun firstLevel(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_catalog_listen_item, parent, false)
    }

    override fun getItemViewType(position: Int): Int {
        if (getIsHeader() && position == 0) return super.getItemViewType(position)
        val pos = if (getIsHeader()) position - 1 else position
        return if (pos < getData().size) {
            when (getData()[pos].type) {
                "1" -> FIRSTLEVEL
                else -> super.getItemViewType(position)
            }
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            FIRSTLEVEL -> ViewHolder(firstLevel(parent.context, parent))
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            FIRSTLEVEL -> firstLevelView(holder, getData()[position - if (getIsHeader()) 1 else 0])
            else -> super.onBindViewHolder(holder, position)
        }
    }

    override fun headerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 10f).toInt())
        view.layoutParams = params
        return view
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }


    companion object {
        const val FIRSTLEVEL = 101
    }

}

class CatalogWordAdapter(isHeader: Boolean, val onClick: (Int, EBookCatalog) -> Unit) : BaseAdapter<EBookCatalog>(isHeader = isHeader, isFooter = true) {
    var isOwn = "0"
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var currentCatalogKey = ""
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    //是否阅读中的目录 0 课程详情  1：阅读中的目录 隐藏学习状态
    var catalogType = "0"
        set(value) {
            field = value
        }

    override fun convert(holder: ViewHolder, item: EBookCatalog) {
        val context = holder.itemView.context
        holder.setText(R.id.tv_title, item.name).setVisible(R.id.tv_try, if (isOwn == "0" && "1" == item.isTryLearn) View.VISIBLE else View.GONE)
            .setSelected(R.id.tv_title, catalogType == "1" && currentCatalogKey == item.catalogKey)//从阅读目录进&&当前词组
            .setVisible(R.id.img_learn, if (isOwn == "1" && item.lastTime.isNotBlank() && catalogType == "0") View.VISIBLE else View.GONE)
            .setVisible(R.id.line, if (item.lineType == "0") View.VISIBLE else View.GONE)
            .setVisible(R.id.tv_practice, if (item.existQuestion == "1" || item.existChapter == "1") View.VISIBLE else View.GONE)
            .setText(R.id.tv_practice, if (item.existChapter == "1") "进入测试" else "练习")
            .setVisible(R.id.tv_read, if (item.existRead == "1" || item.existRead == "2") View.VISIBLE else View.GONE)
            .setSelected(R.id.tv_read, item.existRead == "2")
            .setText(R.id.tv_read, if (item.existRead == "2") "已阅读" else "阅读")
            .setVisible(R.id.v_line, if ((item.existRead == "1" || item.existRead == "2") && item.existQuestion == "1") View.VISIBLE else View.GONE)
            .setOnClickListener(R.id.tv_practice) {
                if ("1" != isOwn && "1" != item.isTryLearn) {
                    //去购买
                    onClick(3, item)
                } else if (item.existChapter == "1") {
                    //继续 章节测试
                    onClick(4, item)
                } else {
                    //练习
                    onClick(2, item)
                }
            }
        val tv_title = holder.getView(R.id.tv_title) as TextView
        if (catalogType == "1") {
            val params = tv_title.layoutParams as ConstraintLayout.LayoutParams
            params.leftMargin = DeviceUtil.dp2px(context, 30f).toInt()
        }
        val maxWidth = if (isOwn == "0" && "1" == item.isTryLearn && catalogType == "0") {//课程详情进&&未购买&&试学状态
            DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 220f).toInt()
        } else if (isOwn == "1" && item.lastTime.isNotBlank() && catalogType == "0") {//课程详情进&&已购买&&上次学到
            DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 205f).toInt()
        } else if (catalogType == "0" || catalogType == "3") {//课程详情进&&   未购买&&非试学   已购买&&非上次学到
            DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 175f).toInt()
        } else if (isOwn == "0" && "1" == item.isTryLearn && catalogType == "1") {
            DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 265f).toInt()
        } else {
            DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 220f).toInt()
        }
        tv_title.maxWidth = maxWidth
        if (item.showType == "1") {
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 66f).toInt())
            holder.itemView.layoutParams = params
        } else {
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
            holder.itemView.layoutParams = params
        }

        holder.itemView.setOnClickListener {
            if ("1" != isOwn && "1" != item.isTryLearn) {
                //去购买
                onClick(3, item)
            } else if (item.existRead == "1" || item.existRead == "2") {
                //阅读
                onClick(1, item)
//                if (catalogType != "3") {
//                    formatLastLook(item)
//                    notifyDataSetChanged()
//                }
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_catalog_word, parent, false)
    }

    fun firstLevelView(holder: ViewHolder, item: EBookCatalog) {
        val context = holder.itemView.context
        holder.setText(R.id.tv_title, item.name).setVisible(R.id.img_learn, if (isOwn == "1" && item.lastTime.isNotBlank() && catalogType == "0") View.VISIBLE else View.GONE)
            .setVisible(R.id.tv_read, if (isOwn == "1" && item.existRead == "2") View.VISIBLE else View.GONE)
            .setSelected(R.id.img_arrow, item.showType == "1")
        holder.itemView.setOnClickListener {
            formatData(item)
            notifyDataSetChanged()
        }
        val title = holder.getView(R.id.tv_title) as TextView
        if (catalogType == "1" && holder.layoutPosition == 0) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.topMargin = 0
        }
        val maxWidth = if (isOwn == "1" && item.lastTime.isNotBlank() && item.existRead == "2" && catalogType == "0") {//已购买&&上次学到&&已完成阅读
            DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 219f).toInt()
        } else if (isOwn == "1" && item.lastTime.isNotBlank() && catalogType == "0") {//已购买&&上次学到
            DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 170f).toInt()
        } else if (isOwn == "1" && item.existRead == "2" && catalogType == "0") {//已购买&&已完成阅读
            DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 139f).toInt()
        } else if (catalogType == "0" || catalogType == "3") {//已购买&&已完成阅读
            DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 90f).toInt()
        } else if (isOwn == "1" && item.existRead == "2" && catalogType == "1") {//已购买&&已完成阅读
            DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 199f).toInt()
        } else {
            DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 150f).toInt()
        }
        title.maxWidth = maxWidth
    }

    fun firstLevel(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_catalog_word_first_level, parent, false)
    }

    override fun getItemViewType(position: Int): Int {
        if (getIsHeader() && position == 0) return super.getItemViewType(position)
        val pos = if (getIsHeader()) position - 1 else position
        return if (pos < getData().size) {
            when (getData()[pos].type) {
                "1" -> FIRSTLEVEL
                else -> super.getItemViewType(position)
            }
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            FIRSTLEVEL -> ViewHolder(firstLevel(parent.context, parent))
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            FIRSTLEVEL -> firstLevelView(holder, getData()[position - if (getIsHeader()) 1 else 0])
            else -> super.onBindViewHolder(holder, position)
        }
    }

    override fun headerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 5f).toInt())
        view.layoutParams = params
        return view
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }

    private fun formatData(data: EBookCatalog) {
        data.showType = if (data.showType == "1") "0" else "1"
        getData().forEach { item ->
            if (item.parentKey == data.catalogKey) {
                item.showType = data.showType
            }
        }
    }

    private fun formatLastLook(data: EBookCatalog) {
        getData().forEach { item ->
            item.lastTime = if (item.catalogKey == data.catalogKey || item.catalogKey == data.parentKey) "1" else ""
        }
    }

    companion object {
        const val FIRSTLEVEL = 101
    }
}
