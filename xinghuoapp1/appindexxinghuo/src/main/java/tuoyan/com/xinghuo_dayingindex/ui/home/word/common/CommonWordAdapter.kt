package tuoyan.com.xinghuo_dayingindex.ui.home.word.common

import android.content.Context
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.WordsByCatalogkey
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * 创建者：
 * 时间：  2018/10/9.
 */
class CommonWordAdapter(isEmpty: Boolean = false, val more: () -> Unit, val click: (Int) -> Unit) : BaseAdapter<WordsByCatalogkey>(isEmpty = isEmpty) {
    var currentKey = ""
    override fun convert(holder: ViewHolder, item: WordsByCatalogkey) {
        holder.setText(R.id.tv_title, item.word ?: "")
            .setText(R.id.tv_translation, Html.fromHtml(item.paraphrase ?: ""))
            .setVisible(R.id.img_learned, if (currentKey == item.key) View.VISIBLE else View.GONE)
            .itemView.setOnClickListener {
                click(holder.layoutPosition)
            }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_common_word, parent, false)
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

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }

    override fun emptyImageRes(): Int {
        return R.drawable.empty_study
    }

    override fun emptyText(): String {
        return "暂无生词哦"
    }
}