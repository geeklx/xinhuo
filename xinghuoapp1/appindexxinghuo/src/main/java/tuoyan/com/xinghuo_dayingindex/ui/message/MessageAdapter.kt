package tuoyan.com.xinghuo_dayingindex.ui.message

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_back.view.*
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.Message

/**
 * 创建者：
 * 时间：  2018/10/9.
 */
class MessageAdapter(
    val more: () -> Unit,
    val click: (Message) -> Unit,
    val longClick: (Message) -> Unit
) : BaseAdapter<Message>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: Message) {
        holder.setText(R.id.tv_title, item.title ?: "")
            .setText(R.id.tv_time, item.time() ?: "")
            .setText(R.id.tv_content, item.content ?: "")
            .itemView.setOnClickListener {
                click(item)
            }
        holder.itemView.setOnLongClickListener {
            longClick(item)
            true
        }
    }

    private fun lessonConvert(holder: ViewHolder, item: Message) {
        holder.setText(R.id.tv_status, item.title ?: "")
            .setText(R.id.tv_time, item.time() ?: "")
            .setText(R.id.tv_title, item.content ?: "")
            .setImageUrl(R.id.iv_img, item.img ?: "")
            .itemView.setOnClickListener {
                click(item)
            }
        holder.itemView.setOnLongClickListener {
            longClick(item)
            true
        }
    }

    private fun bookConvert(holder: ViewHolder, item: Message) {
        holder.setText(R.id.tv_status, item.title ?: "")
            .setText(R.id.tv_time, item.time() ?: "")
            .setText(R.id.tv_title, item.content ?: "")
            .setImageUrl(R.id.iv_img, item.img ?: "")
            .itemView.setOnClickListener {
                click(item)
            }
        holder.itemView.setOnLongClickListener {
            longClick(item)
            true
        }
    }

    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
        verticalLayout {
            lparams(matchParent, wrapContent) {
                horizontalMargin = dip(15)
                verticalMargin = dip(7.5f)
            }
            padding = dip(15)
            backgroundResource = R.drawable.bg_shape_5_ffffff
            linearLayout {
                gravity = Gravity.BOTTOM
                textView {
                    id = R.id.tv_title
                    typeface = Typeface.DEFAULT_BOLD
                    lines = 1
                    ellipsize = TextUtils.TruncateAt.END
                    textSize = 14f
                    textColor = resources.getColor(R.color.color_222)
                }.lparams(0, wrapContent, 1f)
                textView {
                    id = R.id.tv_time
                    textSize = 12f
                    textColor = resources.getColor(R.color.color_8d95a1)
                }
            }
            space().lparams(wrapContent, dip(15))
            textView {
                id = R.id.tv_content
                textSize = 13f
                setLineSpacing(0f, 1.5f)
                textColor = resources.getColor(R.color.color_222)
            }
        }
    }

    private fun lessonView(parent: ViewGroup) = with(parent.context) {
        verticalLayout {
            lparams(matchParent, wrapContent) {
                horizontalMargin = dip(15)
                verticalMargin = dip(7.5f)
            }
            padding = dip(15)
            backgroundResource = R.drawable.bg_shape_5_ffffff
            linearLayout {
                gravity = Gravity.BOTTOM
                textView {
                    id = R.id.tv_status
                    textSize = 14f
                    typeface = Typeface.DEFAULT_BOLD
                    textColor = resources.getColor(R.color.color_222)
                }.lparams(0, wrapContent, 1f)
                textView {
                    id = R.id.tv_time
                    textSize = 12f
                    textColor = resources.getColor(R.color.color_8d95a1)
                }
            }
            space().lparams(wrapContent, dip(15))
            linearLayout {
                imageView {
                    id = R.id.iv_img
                }.lparams(dip(104), dip(70))
                textView {
                    id = R.id.tv_title
                    textSize = 13f
                    padding = dip(10)
                    lines = 2
                    ellipsize = TextUtils.TruncateAt.END
                    setLineSpacing(0f, 1.5f)
                    textColor = resources.getColor(R.color.color_222)
                    backgroundResource = R.color.color_f6f7f8
                }.lparams(matchParent, matchParent)
            }
        }
    }

    private fun bookView(parent: ViewGroup) = with(parent.context) {
        verticalLayout {
            lparams(matchParent, wrapContent) {
                horizontalMargin = dip(15)
                verticalMargin = dip(7.5f)
            }
            padding = dip(15)
            backgroundResource = R.drawable.bg_shape_5_ffffff
            linearLayout {
                gravity = Gravity.BOTTOM
                textView {
                    id = R.id.tv_status
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 14f
                    textColor = resources.getColor(R.color.color_222)
                }.lparams(0, wrapContent, 1f)
                textView {
                    id = R.id.tv_time
                    textSize = 12f
                    textColor = resources.getColor(R.color.color_8d95a1)
                }
            }
            space().lparams(wrapContent, dip(15))
            linearLayout {
                imageView {
                    id = R.id.iv_img
                }.lparams(dip(70), dip(91))
                textView {
                    id = R.id.tv_title
                    textSize = 13f
                    padding = dip(10)
                    setLineSpacing(0f, 1.5f)
                    lines = 2
                    ellipsize = TextUtils.TruncateAt.END
                    textColor = resources.getColor(R.color.color_222)
                    backgroundResource = R.color.color_f6f7f8
                }.lparams(matchParent, matchParent)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (getDateCount() == 0) return super.getItemViewType(position)
        if (isMore() && position == itemCount - 1) return super.getItemViewType(position)
        if (getData()[position].isInstation == "1") return super.getItemViewType(position)
        if (getData()[position].targetType == "5") return super.getItemViewType(position)
        if (getData()[position].targetType == "2" || getData()[position].targetType == "1.2") return NET_LESSON
        if (getData()[position].targetType == "1.1" || getData()[position].targetType == "4") return BOOK
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mContext == null) mContext = parent.context
        return when (viewType) {
            NET_LESSON -> ViewHolder(lessonView(parent))
            BOOK -> ViewHolder(bookView(parent))
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            NET_LESSON -> lessonConvert(holder, getData()[position - if (getIsHeader()) 1 else 0])
            BOOK -> bookConvert(holder, getData()[position - if (getIsHeader()) 1 else 0])
            else -> super.onBindViewHolder(holder, position)
        }
    }

    override fun loadMore(holder: ViewHolder) {
        more()
    }

    override fun moreView(context: Context, parent: ViewGroup) = with(context) {
        verticalLayout {
            lparams(matchParent, dip(50))
            textView("加载更多内容") {
                gravity = Gravity.CENTER
                lines = 1
                textSize = 15f
                textColor = 0xff666666.toInt()
            }.lparams(matchParent, matchParent)
        }
    }

    override fun emptyImageRes(): Int {
        return R.drawable.ic_emtry_message
    }

    override fun emptyText(): String {
        return "暂无消息哦"
    }

    companion object {
        const val NET_LESSON = 0x15d4
        const val BOOK = 0x15f4
    }
}