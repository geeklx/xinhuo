package tuoyan.com.xinghuo_dayingindex.ui.books.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mersens.view.CircleNumberProgressBar
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DEFAULT
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DONE
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DOWNLOADING
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_PAUSED

/**
 * Created by  on 2018/9/15.
 */
class BookResAdapter(var onClick: (item: BookRes, position: Int) -> Unit,
                     var downloadClick: (poisition: Int, res: BookRes) -> Unit) : BaseAdapter<BookRes>(isEmpty = true) {


    var isOwn = false
    override fun convertView(context: Context,parent: ViewGroup): View = context.layoutInflater.inflate(R.layout.item_book_res, null)

    override fun convert(holder: ViewHolder, item: BookRes) {
        holder.setText(R.id.tv_title_book, item.name)
            .setText(R.id.tv_duration, item.duration)
            .setOnClickListener(R.id.iv_download) {
                downloadClick(holder.adapterPosition, item)
            }
            .setImageResource(
                R.id.iv_download, when (item.state) {
                    STATE_DONE -> R.drawable.ic_download_done
                    STATE_DEFAULT -> R.mipmap.icon_download_round
                    STATE_DOWNLOADING -> R.drawable.ic_download_pause
                    STATE_PAUSED -> R.drawable.ic_download_start
                    else -> R.mipmap.icon_download_round
                }
            ).setContentDescription(
                R.id.iv_download, when (item.state) {
                    STATE_DONE -> "下载完成"
                    STATE_DEFAULT -> "下载"
                    STATE_DOWNLOADING -> "暂停"
                    STATE_PAUSED -> "开始"
                    else -> "下载"
                }
            ).setVisible(
                R.id.pb_book_res, when (item.state) {
                    STATE_DONE,
                    STATE_DEFAULT -> View.GONE
                    STATE_DOWNLOADING,
                    STATE_PAUSED -> View.VISIBLE
                    else -> View.GONE
                }
            ).setVisible(R.id.iv_download, if (isOwn && item.downloadFlag == "1") View.VISIBLE else View.GONE)
        var tvTitle = holder.getView(R.id.tv_title_book) as TextView
        tvTitle.setCompoundDrawablesWithIntrinsicBounds(
                if (item.type == "3") {
                    mContext?.let { ContextCompat.getDrawable(it, R.drawable.ic_video) }
                } else {
                    mContext?.let { ContextCompat.getDrawable(it, R.drawable.ic_music) }
                }, null, null, null)
        holder.itemView.setOnClickListener {
            onClick(item, holder.layoutPosition)
        }
        var pb = holder.getView(R.id.pb_book_res) as CircleNumberProgressBar

        pb.progress = item.progress
    }

    override fun emptyView(context: Context,parent: ViewGroup): View = with(context) {
        linearLayout {
            lparams(matchParent, matchParent)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            imageView {
                imageResource = R.drawable.ic_no_content
            }.lparams {
                topMargin = dip(20)
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

//    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
//        relativeLayout {
//            textView {
//                id = R.id.tv_title_book
//                textSize = 14f
//                textColor = Color.parseColor("#222831")
//                paint.isFakeBoldText = true
//                maxLines = 1
//                ellipsize = TextUtils.TruncateAt.END
//
////                setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_music,null),null,null,null)
//                compoundDrawablePadding = dip(15)
//            }.lparams(matchParent, wrapContent){
//                leftMargin = dip(15)
//                topMargin = dip(15)
//            }
//
//            textView {
//                id = R.id.tv_duration
//                textColor = Color.parseColor("#8d95a1")
//                textSize = 11f
//
//                setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_duration),null,null,null)
//                compoundDrawablePadding = dip(15)
//            }.lparams(matchParent, wrapContent){
//                leftMargin = dip(45)
//                topMargin = dip(5)
//                below(R.id.tv_title_book)
//            }
//
//            view {
//                backgroundColor = Color.parseColor("#edeff0")
//            }.lparams(matchParent,dip(1)){
//                leftMargin = dip(45)
//                topMargin = dip(15)
//                below(R.id.tv_duration)
//            }
//
//            nicePgBar {
//                id = R.id.pb_book_res
//                progress = 20
//
//            }.lparams(dip(30),dip(30)){
//                centerVertically()
//                alignParentRight()
//                rightMargin = dip(15)
//            }
//
//        }
//    }
}