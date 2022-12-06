package tuoyan.com.xinghuo_dayingindex.ui.home.word.words

import android.content.Context
import android.graphics.Typeface
import android.text.Html
import android.view.Gravity
import android.view.ViewGroup
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.WordsByCatalogkey

/**
 * 创建者：
 * 时间：  2018/10/9.
 */
class WordsAdapter(isEmpty: Boolean = false,val more: () -> Unit,
                   val click: (WordsByCatalogkey) -> Unit) : BaseAdapter<WordsByCatalogkey>(isEmpty = isEmpty) {
    override fun convert(holder: ViewHolder, item: WordsByCatalogkey) {
        holder.setText(R.id.tv_title, item.word ?: "")
                .setText(R.id.tv_translation, Html.fromHtml(item.paraphrase ?: ""))
                .itemView.setOnClickListener {
            click(item)
        }
    }

    override fun convertView(context: Context,parent: ViewGroup) = with(context) {
        verticalLayout {
            lparams(matchParent, dip(75))
            space().lparams(wrapContent, 0, 1f)
            textView {
                id = R.id.tv_title
                textSize = 18f
                typeface = Typeface.DEFAULT_BOLD
                textColor = resources.getColor(R.color.color_222831)
            }
            space().lparams(wrapContent, dip(5))
            textView {
                id = R.id.tv_translation
                textSize = 12f
                textColor = resources.getColor(R.color.color_c3c7cb)
            }
            space().lparams(wrapContent, 0, 1f)
        }
    }

    override fun loadMore(holder: ViewHolder) {
        more()
    }

    override fun moreView(context: Context,parent: ViewGroup) = with(context) {
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
        return R.drawable.empty_study
    }

    override fun emptyText(): String {
        return "暂无错词哦"
    }
}