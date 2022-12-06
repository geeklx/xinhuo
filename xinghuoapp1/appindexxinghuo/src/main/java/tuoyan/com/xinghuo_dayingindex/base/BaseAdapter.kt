package tuoyan.com.xinghuo_dayingindex.base

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.bean.ClassWork

abstract class BaseAdapter<T>(
    private var isHeader: Boolean = false,
    private var isFooter: Boolean = false,
    private var isEmpty: Boolean = false,
    private var isMore: Boolean = false,
    private val mData: MutableList<T> = mutableListOf()
) : RecyclerView.Adapter<ViewHolder>() {

    var mContext: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mContext == null) mContext = parent.context
        return when (viewType) {
            HEADER -> ViewHolder(headerView(mContext!!, parent))
            FOOTER -> ViewHolder(footerView(mContext!!, parent))
            EMPTY -> ViewHolder(emptyView(mContext!!, parent))
            MORE -> ViewHolder(moreView(mContext!!, parent))
            DEFAULT -> ViewHolder(convertView(parent.context, parent))
            else -> ViewHolder(convertView(parent.context, parent))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            HEADER -> header(holder)
            FOOTER -> footer(holder)
            EMPTY -> empty(holder)
            MORE -> more(holder)
            DEFAULT -> convert(holder, mData[position - if (isHeader) 1 else 0])
        }
    }

    abstract fun convert(holder: ViewHolder, item: T)
    abstract fun convertView(context: Context, parent: ViewGroup): View
    open fun footer(holder: ViewHolder) {
    }

    open fun footerView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, wrapContent)
            textView("底线在此，无法突破") {
                gravity = Gravity.CENTER
                lines = 1
                textSize = 13f
                padding = dip(25)
                textColor = ContextCompat.getColor(context, R.color.color_c7cdd7)
            }.lparams(matchParent, matchParent)
        }
    }

    open fun header(holder: ViewHolder) {
    }

    open fun headerView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {

        }
    }


    fun setHeader(header: Boolean) {
        isHeader = header
    }

    open fun empty(holder: ViewHolder) {

    }

    @DrawableRes
    open fun emptyImageRes(): Int {
        return 0
    }

    open fun emptyText(): String {
        return ""
    }

    open fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, matchParent)
            backgroundResource = R.color.color_ffffff
            space().lparams(wrapContent, 0, 0.5f)
            textView {
                text = emptyText()
                textSize = 15f
                gravity = Gravity.CENTER
//                compoundDrawablePadding = dip(10)
                setCompoundDrawablesWithIntrinsicBounds(0, emptyImageRes(), 0, 0)
                textColor = Color.parseColor("#666666")
            }
            space().lparams(wrapContent, 0, 1.5f)
        }
    }

    open fun more(holder: ViewHolder) {
        loadMore(holder)
    }

    open fun loadMore(holder: ViewHolder) {

    }

    open fun moreView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, dip(50))
            textView("加载更多内容") {
                gravity = Gravity.CENTER
                lines = 1
                textSize = 15f
                textColor = 0xff666666.toInt()
                backgroundColor = 0xffe6e6e6.toInt()
            }.lparams(matchParent, matchParent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (isHeader && position == 0) return HEADER
        if (isFooter && position == itemCount - 1) return FOOTER
        if (isMore && position == itemCount - 1) return MORE
        if (isEmpty) {
            if (mData.isEmpty()) {
                return EMPTY
            } else if (mData.size == 1) {
                var item = mData[0]
                if (item is ClassWork && item.resourceList!!.isEmpty()) {
                    return EMPTY
                }
            }
        }
        return DEFAULT
    }

    override fun getItemCount(): Int {
        return mData.size +
                (if ((isFooter || isMore)) 1 else 0) +
                (if (isEmpty && mData.isEmpty()) 1 else 0) +
                (if (isHeader) 1 else 0)
    }

    fun getDateCount(): Int {
        when (mData != null) {
            true -> return mData.size
            false -> return 0
        }
    }

    fun setFooter(footer: Boolean) {
        isFooter = footer
    }

    fun getFooter() = isFooter

    fun setEmpty(empty: Boolean) {
        isEmpty = empty
    }

    fun getIsHeader() = isHeader
    fun setMore(more: Boolean) {
        isMore = more
//        notifyItemChanged(itemCount - 1)// TODO: 2018/6/27 15:52  这个地方可能会崩溃
        notifyDataSetChanged()
    }

    protected fun isMore() = isMore

    /**
     * @param data 添加数据
     */
    open fun setData(data: List<T>?) {
        clear()
        data?.let {
            mData.addAll(it)
            notifyItemRangeInserted(0, mData.size)
        }
    }

    /**
     * @param data 添加数据
     */
    fun addData(data: List<T>?) {
        data?.let {
            if (it.isEmpty()) {
                return
            }
            val count = mData.size
            mData.addAll(it)
            notifyItemRangeInserted(count, it.size)
        }
    }

    /**
     * @param data 添加数据
     */
    fun addData(data: T?) {
        data?.let {
            val count = mData.size
            mData.add(it)
            notifyItemInserted(count)
        }
    }

    /**
     * @param data 添加数据
     */
    fun addData(data: List<T>?, position: Int) {
        data?.let {
            if (it.isEmpty()) {
                return
            }
            mData.addAll(position, it)
            notifyItemRangeInserted(position, it.size)
        }
    }

    /**
     * @param data 添加数据
     */
    fun addData(data: T?, position: Int) {
        data?.let {
            mData.add(position, it)
            notifyItemInserted(position)
        }
    }

    /**
     * 清空适配器数据
     */
    fun clear() {
        mData.clear()
        notifyDataSetChanged()
    }

    /**
     * @param position 删除角标为position的数据
     */
    fun remove(position: Int) {
        if (position < 0) return
        val i = position - if (isHeader) 1 else 0
        mData.removeAt(i)
        notifyItemRemoved(position)
    }

    /**
     * @param position 删除角标为position的数据
     */
    fun removeAt(position: Int): T? {
        if (position < 0) return null
        val i = position - if (isHeader) 1 else 0
        val at = mData.removeAt(i)
        notifyItemRemoved(position)
        return at
    }

    /**
     * @param data 删除指定数据
     */
    fun remove(data: T?) {
        data?.let {
            val index = mData.indexOf(it)
            val i = index + if (isHeader) 1 else 0

            if (index >= 0) remove(i)
        }
    }

    fun getData(): MutableList<T> {
        return mData
    }

    companion object {
        private val HEADER = 49
        private val FOOTER = 50
        private val EMPTY = 51
        private val MORE = 52
        private val DEFAULT = 0
    }
}