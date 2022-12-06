package tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.nestedScrollView
import tuoyan.com.xinghuo_dayingindex.base.BaseV4Fragment
import kotlin.properties.Delegates

/**
 * Created by  on 2018/10/9.
 */
class LessonDetailFragment : BaseV4Fragment() {
    override val layoutResId: Int
        get() = 0

    var headerHtml =
        "<!doctype html><html><head><meta charset=\\\"utf-8\\\"><meta name=\\\"viewport\\\" content=\\\"width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no\\\" />" +
                "<style>*{margin:0; padding:0;word-wrap:break-word;}img {width: 100% !important;display: block;}</style></head><body>"
    var footerHtml = "</body></html>"
    private var webView by Delegates.notNull<WebView>()
    private var textView by Delegates.notNull<TextView>()

    val content by lazy { arguments!!.getString(CONTENT) }

    companion object {
        val CONTENT = "content"
        fun newInstance(content: String): LessonDetailFragment {
            var f = LessonDetailFragment()

            var arg = Bundle()
            arg.putString(CONTENT, content)
            f.arguments = arg
            return f
        }
    }

    override fun initView(): View? = with(requireContext()) {
        nestedScrollView {
            lparams(matchParent, matchParent)
            relativeLayout {
                setPadding(dip(15), dip(20), dip(15), dip(30))
                textView = textView("暂无详情") {
                    visibility = View.GONE
                }.lparams(wrapContent, wrapContent) {
                    topMargin = dip(40)
                    centerHorizontally()
                }
                webView = webView {
                    settings.javaScriptEnabled = true
                    backgroundColor = Color.parseColor("#ffffff")
                }.lparams(matchParent, matchParent) {
                }
            }.lparams(matchParent, matchParent)
        }
    }

    override fun initData() {
        if (content.isNullOrEmpty()) {
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
            webView.loadDataWithBaseURL(null, headerHtml + content + footerHtml, "text/html", "utf-8", null)
        }
    }
}