package tuoyan.com.xinghuo_dayingindex.ui.exercise.parsing
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewManager
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.CorrectFromHtml
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem
import tuoyan.com.xinghuo_dayingindex.bean.VideoParam
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailPresenter
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.LessonComActivity
import java.util.*
import kotlin.properties.Delegates

private const val ITEM = "item"

@RequiresApi(Build.VERSION_CODES.HONEYCOMB)
class ExerciseParsingFragment : LifeV4Fragment<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = 0

    var webView by Delegates.notNull<BridgeWebView>()
    var llQinfo by Delegates.notNull<LinearLayout>()
    var tvPaperName by Delegates.notNull<TextView>()
    var tvQName by Delegates.notNull<TextView>()
    var tvQExplain by Delegates.notNull<TextView>()
    var tvRemarks by Delegates.notNull<TextView>()
    val parentActivity by lazy { activity as ExerciseParsingActivity }
    var flag = ""
    var startX = -1.0
    var endX = -1.0
    var currentSort = 0
    var pageFinish = false
    var isJump = false
    var jumpToIndex = 0

    /**
     * 存在小题时，小题区域顶端 距离webview的默认距离
     * 用于解决滑动事件冲突
     */
    val defaultLineY by lazy { webView.height * 0.3 }
    var webHeight = 0.0 //webView中 web页面的高度，用于计算
    var lineY = 0.0

    private fun changePage() {
        val moveX = endX - startX
        if (moveX < -50 && currentSort == total - 1) {
            parentActivity.goNext()
        } else if (moveX > 50 && currentSort == 0) {
            parentActivity.goBefore()
        }
    }

    fun isNode(): Boolean {
        return item.isNode
    }

    /**
     * 这两个值用于表示当前题号
     * 针对个别题目在web中有翻页，小题。
     * 展示题号时 相加并+1 ，用于查找题目索引时 仅相加
     */
    var qIndex = 0
    var answerIndex = 0
    var qSort = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView(): View? = UI {
        relativeLayout {
            webView = mWebView {
                id = R.id.web_view
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                webViewClient = object : BridgeWebViewClient(this) {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        pageFinish = true
                        if (isJump) {
                            jumpIndex(jumpToIndex)
                        }
                    }
                }
                /**
                 * 用于解决webview中滑动与viewpager冲突的问题
                 */
                registerHandler("touchEvent") { data, function ->
                    try {
                        val jsonData = JSONObject(data)
                        flag = jsonData.getString("type")

                        if (flag == "touchStart") {
                            startX = jsonData.getDouble("offsetX")
                            currentSort = jsonData.getInt("index")
                            total = jsonData.getInt("total")
                        } else if (flag == "touchEnd") {
                            endX = jsonData.getDouble("offsetX")
                            changePage()
                        }
                    } catch (e: Exception) {
                    }
                }

                setOnTouchListener { view, motionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            if (!item.isNode && (item.questionType == "4" || item.questionType == "5")) {//填空题或材料题，存在小题时，启用判断横向滑动区域的交互
                                var y = if (lineY == 0.0) defaultLineY else lineY
                                var yy = motionEvent.y
                                if (yy < y) {
                                    requestDisallowInterceptTouchEvent(false)
                                } else {
                                    requestDisallowInterceptTouchEvent(true)
                                }
                            } else {
                                requestDisallowInterceptTouchEvent(false)
                            }
                        }
                    }
                    onTouchEvent(motionEvent)
                }

            }.lparams(matchParent, matchParent)

            llQinfo = verticalLayout {
                tvPaperName = textView {
                    textSize = 18f
                    textColor = Color.parseColor("#1e1e1e")
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD) // 加粗
                }.lparams(matchParent, wrapContent) {
                    leftMargin = dip(15)
                    rightMargin = dip(15)
                    topMargin = dip(15)
                }

                tvRemarks = textView {
                    textSize = 14f
                    textColor = Color.parseColor("#666666")
                }.lparams(matchParent, wrapContent) {
                    leftMargin = dip(15)
                    rightMargin = dip(15)
                    topMargin = dip(15)
                }

                tvQName = textView {
                    textSize = 16f
                    textColor = Color.parseColor("#1e1e1e")
                }.lparams(matchParent, wrapContent) {
                    leftMargin = dip(15)
                    rightMargin = dip(15)
                    topMargin = dip(150)
                }

                tvQExplain = textView {
                    textSize = 14f
                    textColor = Color.parseColor("#666666")
                }.lparams(matchParent, wrapContent) {
                    leftMargin = dip(15)
                    rightMargin = dip(15)
                    topMargin = dip(15)
                }

            }.lparams(matchParent, matchParent)
        }
    }.view

    var item by Delegates.notNull<ExerciseFrameItem>()
    var musicUrl = ""
    var total = 0 //小题数
    override fun initData() {
        arguments?.let {
            item = it.getSerializable(ITEM) as ExerciseFrameItem
            if (item.isNode) {
                //判断为展示题型信息页面，则显示题型详情
                tvPaperName.text = item.paperName
                if ("1" != item.isSubtitle) {
                    tvQName.text = item.groupName
                    tvQExplain.text = item.paperExplain
                } else {
                    tvQName.text = ""
                    tvQExplain.text = ""
                }
                tvRemarks.text = item.remarks
            } else {
                getAudioUrl()
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        /**
         * 存在小题时，上下滑动小题区域的回调
         * 返回当前小题区域顶端距离web顶端的px值
         * 用于计算当前触摸区域 在大题区域 还是 小题区域
         * 解决滑动事件冲突问题
         */
        webView.registerHandler("touchHeight") { data, _ ->
            if (data.isNotEmpty()) {
                try {
                    val jsonData = JSONObject(data)
                    val top = jsonData.getString("top")
                    val height = jsonData.getString("height")
                    val pxY = top.replace("px", "").toDouble()
                    webHeight = height.replace("px", "").toDouble()
                    lineY = webView.height * (pxY / webHeight) // 计算出当前 小题区域与大题区域的分界线
                } catch (e: Exception) {
                }
            }
        }

        /**
         * web中翻页的交互，用于变更右上角的题号
         */
        webView.registerHandler("touchIndex") { data, _ ->
            if (item.questionType == "5") { //当前为材料题时，执行翻页变题号的操作
                answerIndex = data.toInt()
                parentActivity.setQSort(qIndex + answerIndex)
            }
        }
        /**
         * 题目反馈交互
         */
        webView.registerHandler("feedback") { data, _ ->
            val jsonData = JSONObject(data)
            val content = jsonData.getString("content")
            val remark = jsonData.getString("remark")
            if (content == "null" && remark == "") {
                toast("请选择错误类型或输入反馈内容")
            } else if (content == "null") {
                toast("请选择错误类型")
            } else {
                val map = HashMap<String, String>()
                map["questionkey"] = item.questionKey ?: ""
                map["content"] = content
                map["remark"] = remark
                presenter.questionFB(map) {
                    toast("反馈成功")
                }
            }
        }

        /**
         * 播放音频的回调，发过来的是音频url
         * 用于暂停音频
         */
        webView.registerHandler("sendUrl") { data, _ ->
            if (musicUrl.isNotEmpty() && musicUrl != data) {
                musicStop()
            }
            musicUrl = data
        }
        webView.registerHandler("showImages") { data, _ ->
            val bean = Gson().fromJson(data, CorrectFromHtml::class.java)
            val item = bean.correctList.get(bean.index)
            parentActivity.showImg(item)
        }
        //评论
        webView.registerHandler("showEvaluation") { questionKey, _ ->
            val intent = Intent(activity, LessonComActivity::class.java)
            intent.putExtra(LessonComActivity.P_KEY, parentActivity.evalkey)
            intent.putExtra(LessonComActivity.KEY, item.questionKey)
            intent.putExtra(LessonComActivity.IS_COMMENT, item.questionInfo?.isComment)
            intent.putExtra(LessonComActivity.TYPE, "8")
            activity?.startActivityForResult(intent, 999)
        }
        /*
        * playVideo(videoKey)
        * */
        webView.registerHandler("playVideo") { data, _ ->
            val videoParam = VideoParam()
            videoParam.key = data
            startActivity<VideoActivity>(VideoActivity.VIDEO_PARAM to videoParam)
        }
    }

    fun getAudioUrl() {
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = JSONObject(gson.toJson(item.questionInfo))
        if (json.has("resourceKey")) {
            val resourceKey = json.getString("resourceKey")
            if (resourceKey.isNotBlank()) {
                presenter.getResourceInfo(resourceKey, "3") {
                    json.put("audioFile", it.url)
                    var htmlStr = parentActivity.htmlStr.toString()
                    htmlStr = htmlStr.replace("\${question-content}", json.toString())
                    htmlStr = htmlStr.replace("\${module-type}", "2")
                    if (parentActivity.exType == ExerciseParsingActivity.EX_TYPE_PG && item.questionInfo?.evalMode == "1") {
                        htmlStr = htmlStr.replace("\${module-subject}", "2")
                    } else if (parentActivity.exType == ExerciseParsingActivity.EX_TYPE_PG && item.questionInfo?.evalMode == "0") {
                        htmlStr = htmlStr.replace("\${module-subject}", "1")
                    } else {
                        htmlStr = htmlStr.replace("\${module-subject}", "0")
                    }
                    webView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null)
                }
            } else {
                var htmlStr = parentActivity.htmlStr.toString()
                htmlStr = htmlStr.replace("\${question-content}", json.toString())
                htmlStr = htmlStr.replace("\${module-type}", "2")
                if (parentActivity.exType == ExerciseParsingActivity.EX_TYPE_PG && item.questionInfo?.evalMode == "1") {
                    htmlStr = htmlStr.replace("\${module-subject}", "2")
                } else if (parentActivity.exType == ExerciseParsingActivity.EX_TYPE_PG && item.questionInfo?.evalMode == "0") {
                    htmlStr = htmlStr.replace("\${module-subject}", "1")
                } else {
                    htmlStr = htmlStr.replace("\${module-subject}", "0")
                }
                webView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null)
            }
        } else {
            var htmlStr = parentActivity.htmlStr.toString()
            htmlStr = htmlStr.replace("\${question-content}", json.toString())
            htmlStr = htmlStr.replace("\${module-type}", "2")
            if (parentActivity.exType == ExerciseParsingActivity.EX_TYPE_PG && item.questionInfo?.evalMode == "1") {
                htmlStr = htmlStr.replace("\${module-subject}", "2")
            } else if (parentActivity.exType == ExerciseParsingActivity.EX_TYPE_PG && item.questionInfo?.evalMode == "0") {
                htmlStr = htmlStr.replace("\${module-subject}", "1")
            } else {
                htmlStr = htmlStr.replace("\${module-subject}", "0")
            }
            webView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null)
        }
    }

    /**
     * 当前页面不可见时，activity回调该方法，停止web播放音频
     */
    fun musicStop() {
        if (musicUrl.isNotEmpty()) {
            val jsonData = JsonObject()
            jsonData.addProperty("type", "musicStop")
            jsonData.addProperty("url", musicUrl)
            webView.send(jsonData.toString()) {
                toast(it)
            }
        }
    }

    /**
     * 点击跳转指定小题
     */
    fun jumpIndex(index: Int) {
        isJump = false
        if (pageFinish) {
            val jsonData = JsonObject()
            jsonData.addProperty("type", "setIndex")
            jsonData.addProperty("index", index)
            webView.send(jsonData.toString()) {
                toast(it)
            }
        } else {
            //加载完毕了执行
            isJump = true
            jumpToIndex = index
        }
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
        @JvmStatic
        fun newInstance(item: ExerciseFrameItem) =
            ExerciseParsingFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ITEM, item)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //修改参数  已经评论
        item.questionInfo?.isComment = data!!.getStringExtra("isComment").toString()
    }

}

inline fun ViewManager.mWebView(): BridgeWebView = mWebView() {}
inline fun ViewManager.mWebView(init: (BridgeWebView).() -> Unit): BridgeWebView {
    return ankoView({ BridgeWebView(it) }, theme = 0) { init() }
}