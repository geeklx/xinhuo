package tuoyan.com.xinghuo_dayingindex.ui.mine.offline

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.layoutInflater
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean
import tuoyan.com.xinghuo_dayingindex.realm.bean.Group
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import java.io.File

class OfflineResAdapter(var onClick: (DownloadBean) -> Unit, var onDelete: (DownloadBean) -> Unit) :
    BaseAdapter<DownloadBean>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: DownloadBean) {
        holder.setVisible(R.id.pb_book_res, View.GONE)
            .setVisible(R.id.iv_download, View.GONE)
            .setText(R.id.tv_title_book, item.name)
            .setText(R.id.tv_duration, FileUtils.formatBytes(FileUtils.getFileSize(File(item.path))))
        var tvTitle = holder.getView(R.id.tv_title_book) as TextView
        tvTitle.setCompoundDrawablesWithIntrinsicBounds(
            if (item.type == "3") mContext!!.resources.getDrawable(R.drawable.ic_video) else mContext!!.resources.getDrawable(
                R.drawable.ic_music
            ), null, null, null
        )
        holder.itemView.setOnClickListener {
            onClick(item)
        }
        holder.itemView.setOnLongClickListener {
            onDelete(item)
            true
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View =
        context.layoutInflater.inflate(R.layout.item_book_res, null)

    override fun emptyImageRes(): Int {
        return R.drawable.ic_emtry_content
    }

    override fun emptyText(): String {
        return "暂无缓存内容哦"
    }
}


class EBookDownAdapter(val onClick: (Int, Group) -> Unit) : BaseAdapter<Group>(isHeader = true, isEmpty = true) {
    override fun convert(holder: ViewHolder, item: Group) {
        val path = "${DownloadManager.downloadPathEN}/${item.key}"
        holder.setText(R.id.tv_title, item.name).setText(R.id.tv_size, FileUtils.formatBytes(FileUtils.getFileSize(File(path))))
            .setOnClickListener(R.id.img2) {
                onClick(2, item)
            }.itemView.setOnClickListener {
                onClick(1, item)
            }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_ebook_down, parent, false)
    }

    override fun headerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 5f).toInt())
        view.layoutParams = params
        return view
    }

    override fun emptyImageRes(): Int {
        return R.drawable.ic_emtry_content
    }

    override fun emptyText(): String {
        return "暂无缓存内容哦"
    }
}