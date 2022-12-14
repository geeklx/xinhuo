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
     * ???????????????????????????????????? ??????webview???????????????
     * ??????????????????????????????
     */
    val defaultLineY by lazy { webView.height * 0.3 }
    var webHeight = 0.0 //webView??? web??????????????????????????????
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
     * ????????????????????????????????????
     * ?????????????????????web????????????????????????
     * ??????????????? ?????????+1 ?????????????????????????????? ?????????
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
                 * ????????????webview????????????viewpager???????????????
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
                            if (!item.isNode && (item.questionType == "4" || item.questionType == "5")) {//?????????????????????????????????????????????????????????????????????????????????
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
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD) // ??????
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
    var total = 0 //?????????
    override fun initData() {
        arguments?.let {
            item = it.getSerializable(ITEM) as ExerciseFrameItem
            if (item.isNode) {
                //?????????????????????????????????????????????????????????
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
         * ???????????????????????????????????????????????????
         * ????????????????????????????????????web?????????px???
         * ?????????????????????????????? ??????????????? ?????? ????????????
         * ??????????????????????????????
         */
        webView.registerHandler("touchHeight") { data, _ ->
            if (data.isNotEmpty()) {
                try {
                    val jsonData = JSONObject(data)
                    val top = jsonData.getString("top")
                    val height = jsonData.getString("height")
                    val pxY = top.replace("px", "").toDouble()
                    webHeight = height.replace("px", "").toDouble()
                    lineY = webView.height * (pxY / webHeight) // ??????????????? ???????????????????????????????????????
                } catch (e: Exception) {
                }
            }
        }

        /**
         * web???????????????????????????????????????????????????
         */
        webView.registerHandler("touchIndex") { data, _ ->
            if (item.questionType == "5") { //??????????????????????????????????????????????????????
                answerIndex = data.toInt()
                parentActivity.setQSort(qIndex + answerIndex)
            }
        }
        /**
         * ??????????????????
         */
        webView.registerHandler("feedback") { data, _ ->
            val jsonData = JSONObject(data)
            val content = jsonData.getString("content")
            val remark = jsonData.getString("remark")
            if (content == "null" && remark == "") {
                toast("??????????????????????????????????????????")
            } else if (content == "null") {
                toast("?????????????????????")
            } else {
                val map = HashMap<String, String>()
                map["questionkey"] = item.questionKey ?: ""
                map["content"] = content
                map["remark"] = remark
                presenter.questionFB(map) {
                    toast("????????????")
                }
            }
        }

        /**
         * ?????????????????????????????????????????????url
         * ??????????????????
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
        //??????
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
     * ???????????????????????????activity????????????????????????web????????????
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
     * ????????????????????????
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
            //?????????????????????
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
        //????????????  ????????????
        item.questionInfo?.isComment = data!!.getStringExtra("isComment").toString()
    }

}

inline fun ViewManager.mWebView(): BridgeWebView = mWebView() {}
inline fun ViewManager.mWebView(init: (BridgeWebView).() -> Unit): BridgeWebView {
    return ankoView({ BridgeWebView(it) }, theme = 0) { init() }
}