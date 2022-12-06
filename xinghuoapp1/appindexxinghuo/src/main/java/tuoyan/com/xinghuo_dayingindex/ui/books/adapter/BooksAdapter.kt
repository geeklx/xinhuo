package tuoyan.com.xinghuo_dayingindex.ui.books.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.Book
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 */
class BooksAdapter(var onclick: (String) -> Unit) : BaseAdapter<List<Book>>() {
    override fun convert(holder: ViewHolder, item: List<Book>) {
        val mAdapter = BooksItemAdapter() { _, item, _ ->
            onclick(item.key)
        }
        mAdapter.setData(item)

        holder.setText(R.id.tv_book_date, if (item[0].year.isEmpty()) "it is empty" else item[0].year)
            .setAdapter(R.id.rv_books, mAdapter)
        val rlv_books = holder.getView(R.id.rv_books) as RecyclerView
        if (getDateCount() == holder.adapterPosition + 1) {
            rlv_books.bottomPadding = DeviceUtil.dp2px(holder.itemView.context, 60f).toInt()
        } else {
            rlv_books.bottomPadding = DeviceUtil.dp2px(holder.itemView.context, 0f).toInt()
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        relativeLayout {
            textView {
                id = R.id.tv_book_date
                textSize = 17f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textColor = ContextCompat.getColor(context, R.color.color_222831)
                leftPadding = dip(15)
                topPadding = dip(20)
                bottomPadding = dip(10)
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_title_right, 0)
                compoundDrawablePadding = dip(5)
                visibility = View.GONE
            }.lparams(wrapContent, wrapContent)

            recyclerView {
                id = R.id.rv_books
                layoutManager = GridLayoutManager(context, 3)
            }.lparams(matchParent, wrapContent) {
                below(R.id.tv_book_date)
                leftMargin = dip(8)
                rightMargin = dip(7)
                topMargin = dip(60)
            }
        }
    }
}

/**
 * type =1 图书配套，0：点读书
 */
class BooksItemAdapter(var type: String = "1", var onclick: (String, Book, Int) -> Unit) : BaseAdapter<Book>() {
    var isDel = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: ViewHolder, item: Book) {
        holder.setText(R.id.tv_title, item.name)
            .setImageUrl(R.id.img_cover, item.coverImg, 0, 0)
        holder.setVisible(R.id.img_del, if (isDel) View.VISIBLE else View.GONE)
            .setVisible(R.id.ctl_time_out, if ("1" == item.isEffect) View.VISIBLE else View.GONE)
        holder.itemView.setOnClickListener {
            //点击进入图书
            if (type == "0" && "1" == item.isEffect) {
                onclick("2", item, holder.layoutPosition)
            } else {
                onclick("1", item, 0)
            }
        }
        holder.setOnClickListener(R.id.img_del) {
            //点击删除图书
            onclick("2", item, holder.layoutPosition)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = LayoutInflater.from(context).inflate(R.layout.layout_book, parent, false)

    override fun footer(holder: ViewHolder) {
        super.footer(holder)
        holder.itemView.setOnClickListener {
            //添加图书，跳入图书列表
            onclick("3", Book(), 0)
        }
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_book_add, null)
    }
}