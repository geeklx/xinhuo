//package tuoyan.com.xinghuo_daying.ui.exercise.detail
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Intent
//import android.graphics.Color
//import android.graphics.Typeface
//import android.os.Bundle
//import android.util.Log
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewManager
//import android.webkit.WebChromeClient
//import android.webkit.WebView
//import android.widget.LinearLayout
//import android.widget.TextView
//import com.github.lzyzsd.jsbridge.BridgeWebView
//import com.github.lzyzsd.jsbridge.BridgeWebViewClient
//import com.google.gson.GsonBuilder
//import com.google.gson.JsonArray
//import com.google.gson.JsonObject
//import org.jetbrains.anko.*
//import org.jetbrains.anko.custom.ankoView
//import org.jetbrains.anko.support.v4.ctx
//import org.jetbrains.anko.support.v4.toast
//import org.json.JSONArray
//import org.json.JSONObject
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
//import tuoyan.com.xinghuo_dayingindex.bean.AnswerItem
//import tuoyan.com.xinghuo_dayingindex.bean.AnswerQuestion
//import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem
//import tuoyan.com.xinghuo_dayingindex.ui.practice.TeacherModifyDetailActivity
//import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
//import java.io.File
//import kotlin.properties.Delegates
//
//private const val ITEM = "item"
//
//class ExerciseDetailFragment : LifeV4Fragment<ExerciseDetailPresenter>() {
//    override val presenter: ExerciseDetailPresenter
//        get() = ExerciseDetailPresenter(this)
//    override val layoutResId: Int
//        get() = 0
//
//    var webView by Delegates.notNull<BridgeWebView>()
//    var llQinfo by Delegates.notNull<LinearLayout>()
//    var tvPaperName by Delegates.notNull<TextView>()
//    var tvQName by Delegates.notNull<TextView>()
//    var tvQExplain by Delegates.notNull<TextView>()
//    var tvRemarks by Delegates.notNull<TextView>()
//
//    val parentActivity by lazy { activity as ExerciseDetailActivity }
//
//    //TODO ??????????????????????????????????????????base64??????,[str] ->base64???
//    // [index] ->?????????????????????????????????????????????????????????????????????????????????0
//    val imgs = mutableListOf<Map<String, String>>()
//
//    //TODO ??????????????????????????????????????????filePath?????????[str] ->???????????????
//    // [index] ->?????????????????????????????????????????????????????????????????????????????????0
////    val imgPaths = mutableListOf<Map<String, String>>()
//    val imgPaths = ArrayList<File>()
//
//    //0????????????1?????????
//    var evalMode: String = ""
//
//
//    var flag = ""
//    var startX = -1.0
//    var endX = -1.0
//    var currentSort = 0
//
//    var pageFinish = false
//    var isJump = false
//    var jumpToIndex = 0
//
//    /**
//     * ???????????????????????????????????? ??????webview???????????????
//     * ??????????????????????????????
//     */
//    val defaultLineY by lazy { webView.height * 0.3 }
//    var webHeight = 0.0 //webView??? web??????????????????????????????
//
//    var lineY = 0.0
//
//    private fun changePage() {
//        var moveX = endX - startX
//        if (moveX < -50 && currentSort == total - 1) {
//            parentActivity.goNext()
//        } else if (moveX > 50 && currentSort == 0) {
//            parentActivity.goBefore()
//        }
//    }
//
//    fun isNode(): Boolean {
//        return try {  //TODO ?????????????????????????????????????????????item????????????????????????
//            item?.isNode ?: false
//        } catch (e: Exception) {
//            return false
//        }
//    }
//
//    /**
//     * ????????????????????????????????????
//     * ?????????????????????web????????????????????????
//     * ??????????????? ?????????+1 ?????????????????????????????? ?????????
//     */
//    var qIndex = 0
//    var answerIndex = 0
//
//    var dragging = false
//
//    @SuppressLint("SetJavaScriptEnabled")
//    override fun initView(): View? = ctx.relativeLayout {
//        webView = mWebView {
//            id = R.id.web_view
//            settings.javaScriptEnabled = true
//            settings.domStorageEnabled = true
//            settings.javaScriptCanOpenWindowsAutomatically = true
//            webChromeClient = WebChromeClient()
//            webViewClient = object : BridgeWebViewClient(this) {
//                override fun onPageFinished(view: WebView?, url: String?) {
//                    super.onPageFinished(view, url)
//                    pageFinish = true
//                    if (isJump) {
//                        jumpIndex(jumpToIndex)
//                    }
//                }
//            }
//            /**
//             * ????????????webview????????????viewpager???????????????
//             */
//            registerHandler("touchEvent") { data, function ->
//                try {
//                    var jsonData = JSONObject(data)
//                    flag = jsonData.getString("type")
//
//                    if (flag == "touchStart") {
//                        startX = jsonData.getDouble("offsetX")
//                        currentSort = jsonData.getInt("index")
//                        total = jsonData.getInt("total")
//                    } else if (flag == "touchEnd") {
//                        endX = jsonData.getDouble("offsetX")
//                        changePage()
//                    }
//                } catch (e: Exception) {
//                }
//            }
//
//            registerHandler("playerTouchStart") { data, function ->
//                dragging = true
//            }
//
//            registerHandler("playerTouchMove") { data, function ->
//                dragging = true
//            }
//
//            registerHandler("playerTouchEnd") { data, function ->
//                dragging = false
//            }
//
//            setOnTouchListener { view, motionEvent ->
//                when (motionEvent.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        if (item?.isNode != true && (item?.questionType == "4" || item?.questionType == "5")) {//TODO ?????????????????????????????????????????????????????????????????????????????????
//                            var y = if (lineY == 0.0) defaultLineY else lineY
//                            var yy = motionEvent.y
//                            if (dragging) {
//                                requestDisallowInterceptTouchEvent(true)
//                            } else if (yy < y) {
//                                requestDisallowInterceptTouchEvent(false)
//                            } else {
//                                requestDisallowInterceptTouchEvent(true)
//                            }
//                        } else {
//                            requestDisallowInterceptTouchEvent(false)
//                        }
//                    }
//                }
//                onTouchEvent(motionEvent)
//            }
//
//        }.lparams(matchParent, matchParent)
//
//        llQinfo = verticalLayout {
//            tvPaperName = textView {
//                textSize = 18f
//                textColor = Color.parseColor("#1e1e1e")
//                typeface = Typeface.defaultFromStyle(Typeface.BOLD) // ??????
//            }.lparams(matchParent, wrapContent) {
//                leftMargin = dip(15)
//                rightMargin = dip(15)
//                topMargin = dip(15)
//            }
//
//            tvRemarks = textView {
//                textSize = 14f
//                textColor = Color.parseColor("#666666")
//            }.lparams(matchParent, wrapContent) {
//                leftMargin = dip(15)
//                rightMargin = dip(15)
//                topMargin = dip(15)
//            }
//
//            tvQName = textView {
//                textSize = 16f
//                textColor = Color.parseColor("#1e1e1e")
//            }.lparams(matchParent, wrapContent) {
//                leftMargin = dip(15)
//                rightMargin = dip(15)
//                topMargin = dip(150)
//            }
//
//            tvQExplain = textView {
//                textSize = 14f
//                textColor = Color.parseColor("#666666")
//            }.lparams(matchParent, wrapContent) {
//                leftMargin = dip(15)
//                rightMargin = dip(15)
//                topMargin = dip(15)
//            }
//
//        }.lparams(matchParent, matchParent)
//    }
//
//    var item: ExerciseFrameItem? = null
//
//    var total = 0 //?????????
//    private var isLast = false //????????????????????????????????????????????????
//    var musicUrl = ""
//
//    override fun initData() {
//        /**
//         * ???????????????????????????????????????????????????
//         * ????????????????????????????????????web?????????px???
//         * ?????????????????????????????? ??????????????? ?????? ????????????
//         * ??????????????????????????????
//         */
//        webView.registerHandler("touchHeight") { data, _ ->
//            if (data.isNotEmpty()) {
//                try {
//                    var jsonData = JSONObject(data)
//                    var top = jsonData.getString("top")
//                    var height = jsonData.getString("height")
//                    if (top.isNotEmpty() && height.isNotEmpty()) {
//                        var pxY = top.replace("px", "").toDouble()
//                        webHeight = height.replace("px", "").toDouble()
//                        lineY = webView.height * (pxY / webHeight) // ??????????????? ???????????????????????????????????????
//                    }
//                } catch (e: Exception) {
//                }
//            }
//        }
//
//
//        /**
//         * ?????????????????? ??? JS?????????
//         */
//        webView.registerHandler("handleMessage") { data, _ ->
//            item ?: return@registerHandler
//            if ((item!!.questionType == "6" || (item!!.questionType == "5" && item!!.questionlist!![answerIndex].questionType == "6")) && imgs.size > 0) {
//                parentActivity.answerList.forEach {
//                    it.qList.forEach {
//                        if ((it.sort == (
//                                        if (item!!.questionType == "6")
//                                            item!!.questionSort ?: ""
//                                        else
//                                            item!!.questionlist!![answerIndex].questionSort))) {
//                            it.haveDone = true
//                            parentActivity.haveDone = true
//                            parentActivity.goNext()
//                        }
//                    }
//                }
//            } else if (data.isNotEmpty() && data != "\"\"") {
//                if (parentActivity.exType == ExerciseDetailActivity.EX_TYPE_SP && item!!.questionType == "6") {
//                    //TODO ??????????????????????????????????????????
//                    var jsonData = JSONObject(data)
//                    var data = jsonData.getJSONObject("value")
//                    var answerItem = AnswerItem(data.getString("order"), data.getString("answer"))
//
//                    var answers = ArrayList<AnswerItem>()
//                    answers.add(answerItem)
//                    var tempItem = AnswerQuestion()
//                    tempItem.index = 0
//                    tempItem.qPosition = 0
//                    tempItem.mPosition = 0
//                    tempItem.haveDone = false
//                    tempItem.sort = item!!.questionSort ?: ""
//                    tempItem.type = item!!.questionType ?: ""
//                    tempItem.questionKey = item!!.questionKey
//                    tempItem.answers = answers
//                    parentActivity.spSubmit(mutableListOf(tempItem))
//                    parentActivity.goNext()
//                } else {
//                    var jsonData = JSONObject(data)
//
//                    var currentSort = 0
//                    try {
//                        currentSort = jsonData.getString("sort").toInt()
//                        total = jsonData.getString("total").toInt()
//                        isLast = currentSort == total - 1
//                    } catch (e: Exception) {
//                        total = 1
//                        isLast = true
//                    }
//
//                    parentActivity.haveDone = true
//                    try {
//                        if (jsonData.optJSONObject("value") != null) {
//                            var value = jsonData.optJSONObject("value")
//                            parentActivity.answerList.forEach {
//                                it.qList.forEach {
//                                    //????????? ????????????????????? ??????????????????
//                                    if (it.index == qIndex + (if (item!!.questionType ?: "" == "4") 0 else currentSort) + 1) { //???????????????????????????????????????????????????????????????????????????????????????????????????
//                                        Log.e("handleMessage--MEACHED", "==============>>>" + (qIndex + currentSort + 1))
//                                        var sorce = if (value.has("sorce")) value.getString("sorce") else ""
//                                        setAnswerItems(it, value.getString("order"), value.getString("answer"))
//                                    }
//                                }
//                            }
//                        } else if (jsonData.optJSONArray("value") != null) {
//                            var value = jsonData.optJSONArray("value")
//                            parentActivity.answerList.forEach {
//                                it.qList.forEach {
//                                    if (it.index == qIndex + currentSort + 1) { //???????????????????????????????????????????????????????????????????????????????????????????????????
//                                        setAnswerItemsArray(it, value)
//                                    }
//                                }
//                            }
//                        } else if (jsonData.optString("value") != null && jsonData.optString("value") == "noFull") {
//                            toast("??????????????????????????????")
//                        } else {
//                            toast("???????????????")
//                        }
//                    } catch (e: Exception) {
//                        print(e)
//                        toast("???????????????")
//                    }
//                }
//            }
//
//        }
//        /**
//         * web???????????????????????????????????????????????????
//         */
//        webView.registerHandler("touchIndex") { data, _ ->
//            if (data.toInt() == total - 1) isLast = true
//            if (item?.questionType == "5") { //??????????????????????????????????????????????????????
//                answerIndex = data.toInt()
//                parentActivity.setQIndex((qIndex + answerIndex + 1).toString())
//            } else if (item?.questionType == "4") {
//
//            }
//
//        }
//
//        webView.registerHandler("getDefaultIndex") { _, function ->
//            function.onCallBack("0")
//        }
//
//        /**
//         * ?????????????????????????????????????????????url
//         * ??????????????????
//         */
//        webView.registerHandler("sendUrl") { data, _ ->
//            if (musicUrl.isNotEmpty() && musicUrl != data) {
//                musicStop()
//            }
//            musicUrl = data
//        }
//
//        /**
//         * ??????????????????
//         */
//        webView.registerHandler("feedback") { data, _ ->
//            Log.d("feedback", data)
//            var jsonData = JSONObject(data)
//            var content = jsonData.getString("content")
//            var remark = jsonData.getString("remark")
//            if (content == "null" && remark == "") {
//                toast("??????????????????????????????????????????")
//            } else if (content == "null") {
//                toast("?????????????????????")
//            } else {
//                var map = HashMap<String, String>()
//                map["questionkey"] = item?.questionKey ?: ""
//                map["content"] = content
//                map["remark"] = remark
//
//                presenter.questionFB(map) {
//                    toast("????????????")
//                }
//            }
//
//        }
//
//        webView.registerHandler("addImages") { _, _ ->
//            //TODO ?????????????????????
//            PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
//                parentActivity.selectDialog.show()
//            }
//        }
//        webView.registerHandler("correctByUser") { _, _ ->
//            val intent = Intent(parentActivity, TeacherModifyDetailActivity::class.java)
//            intent.putExtra(TeacherModifyDetailActivity.QUESTION_KEY, item?.questionKey)
//            intent.putExtra(TeacherModifyDetailActivity.EVAL_KEY, parentActivity.catKey)
//            intent.putExtra(TeacherModifyDetailActivity.NAME, parentActivity.paperName)
//            parentActivity.startActivityForResult(intent, 999)
//        }
//        webView.registerHandler("correctBySelf") { _, _ ->
//            evalMode = "0"
//        }
//
//        webView.registerHandler("removeImages") { index0, _ ->
//            var index = index0.toInt()
//            imgs.removeAt(index)
//            imgPaths.removeAt(index)
//            showImages()
//        }
//
//        arguments?.let {
//            item = it.getSerializable(ITEM) as ExerciseFrameItem
//            item ?: return
//            if (item!!.isNode) {
//                //?????????????????????????????????????????????????????????
//                tvPaperName.text = item!!.paperName
//                if ("1" != item!!.isSubtitle) {
//                    tvQName.text = item!!.groupName
//                    tvQExplain.text = item!!.paperExplain
//                } else {
//                    tvQName.text = ""
//                    tvQExplain.text = ""
//                }
//                tvRemarks.text = item!!.remarks
//            } else {
//                initDefaultEvalMode()
//                //?????????webview??????????????????????????????
//                llQinfo.visibility = View.GONE
//                var htmlStr = parentActivity.htmlStr.toString()
//                val gson = GsonBuilder().disableHtmlEscaping().create()
//                htmlStr = htmlStr.replace("\${question-content}", gson.toJson(item!!.questionInfo))
////                <!--module-type:1.?????? 2.?????? module-subject:???????????? 0.????????? 1.?????? 2.???????????? -->
//                htmlStr = htmlStr.replace("\${module-type}", "1")
//                if (parentActivity.exType == ExerciseDetailActivity.EX_TYPE_PG && evalMode == "1") {
//                    htmlStr = htmlStr.replace("\${module-subject}", "2")
//                } else if (parentActivity.exType == ExerciseDetailActivity.EX_TYPE_PG) {
//                    htmlStr = htmlStr.replace("\${module-subject}", "1")
//                } else {
//                    htmlStr = htmlStr.replace("\${module-subject}", "0")
//                }
//                webView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null)
//                if (parentActivity.exType == ExerciseDetailActivity.EX_TYPE_SP && item!!.questionType == "6") {
//                    //TODO ??????????????????????????????????????????????????????js
//                    var jsonData = JsonObject()
//                    jsonData.addProperty("type", "getHistory")
//                    jsonData.addProperty("history", item!!.isRight)
//
//                    webView.send(jsonData.toString()) {
//                        toast(it)
//                    }
//                }
//            }
//        }
//    }
//
//    var audioUrl = "" //TODO ???????????? ????????????????????????
//    fun getAudioUrl() {
//        item ?: return
//        initDefaultEvalMode()
//        val gson = GsonBuilder().disableHtmlEscaping().create()
//        var json = JSONObject(gson.toJson(item!!.questionInfo))
//        if (audioUrl == "" && json.has("audioFile") && json.has("resourceKey")) {
//            var audioFile = json.getString("audioFile")
//            var resourceKey = json.getString("resourceKey")
//            if (audioFile.isNotBlank() && resourceKey.isNotBlank()) {
//                presenter.getResourceInfo(resourceKey, "3") {
//                    audioUrl = it.url
//                    json.put("audioFile", "$audioUrl|||$audioFile")
//                    var htmlStr = parentActivity.htmlStr.toString()
//                    htmlStr = htmlStr.replace("\${question-content}", json.toString())
//                    htmlStr = htmlStr.replace("\${module-type}", "1")
//                    if (parentActivity.exType == ExerciseDetailActivity.EX_TYPE_PG && evalMode == "1") {
//                        htmlStr = htmlStr.replace("\${module-subject}", "2")
//                    } else if (parentActivity.exType == ExerciseDetailActivity.EX_TYPE_PG) {
//                        htmlStr = htmlStr.replace("\${module-subject}", "1")
//                    } else {
//                        htmlStr = htmlStr.replace("\${module-subject}", "0")
//                    }
//                    webView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null)
//                }
//            }
//        }
//    }
//
//    /**
//     * ???????????????????????????????????????
//     */
//    private fun setAnswerItems(answerQuestion: AnswerQuestion, order: String, answer: String) {
//        item ?: return
//        if (answer.isNotEmpty()) {
//            var haveDone = false
//            for (i in 0 until answerQuestion.answers.size) {
//                if (answerQuestion.answers[i].order == order) { // ????????????????????????????????????????????????answer
//                    haveDone = true
//                    answerQuestion.answers[i].answer = answer
//                }
//            }
//            if (!haveDone) { // ????????????????????????????????????AnswerItem
//                var answerItem = AnswerItem(order, answer)
//                answerQuestion.answers.add(answerItem)
//            }
//            answerQuestion.haveDone = true //???????????????????????????????????????
//        }
//        parentActivity.saPaperAnswer(answerQuestion)
//        if (item!!.questionType ?: "" == "1" || item!!.questionType ?: "" == "3" || item!!.questionType ?: "" == "6" || (item!!.questionType ?: "" == "4" && isLast) || (item!!.questionType ?: "" == "5" && isLast)) {
//            parentActivity.goNext() //??????????????? ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//        }
//
//    }
//
//    /**
//     * ???????????????????????????????????????
//     * ????????? ??? ??????????????????????????????web??????jsonarry????????????
//     */
//    private fun setAnswerItemsArray(answerQuestion: AnswerQuestion, jsonArray: JSONArray) {
//        answerQuestion.answers.clear()
//        if (answerQuestion.type == "4" || answerQuestion.type == "6") {
//            //?????????
//            for (i in 0 until jsonArray.length()) {
//                var jsonObject = jsonArray[i] as JSONObject
//                var answerItem = AnswerItem(jsonObject.getString("order"), jsonObject.getString("answer"), if (jsonObject.has("score")) jsonObject.getString("score") else "")
//                answerQuestion.answers.add(answerItem)
//            }
//            parentActivity.goNext()
//        } else if (answerQuestion.type == "2") {
//            //?????????
//            var order = ""
//            var answer = ""
//            for (i in 0 until jsonArray.length()) {
//                var jsonObject = jsonArray[i] as JSONObject
//                order = jsonObject.getString("order")
//                answer += jsonObject.getString("answer")
//            }
//
//            var answerItem = AnswerItem(order, answer)
//            answerQuestion.answers.add(answerItem)
//        }
//
//        answerQuestion.haveDone = true //???????????????????????????????????????
//    }
//
//    /**
//     * ???????????????????????????activity????????????????????????web????????????
//     */
//    fun musicStop() {
//        if (musicUrl.isNotEmpty()) {
//            var jsonData = JsonObject()
//            jsonData.addProperty("type", "musicStop")
//            jsonData.addProperty("url", musicUrl)
//
//            webView.send(jsonData.toString()) {
//                toast(it)
//            }
//        }
//
//    }
//
//    /**
//     * ????????????????????????
//     */
//    fun jumpIndex(index: Int) {
//        isJump = false
//        if (pageFinish) {
//            var jsonData = JsonObject()
//            jsonData.addProperty("type", "setIndex")
//            jsonData.addProperty("index", index)
//            webView.send(jsonData.toString()) {
//                toast(it)
//            }
//        } else {
//            //?????????????????????
//            isJump = true
//            jumpToIndex = index
//        }
//    }
//
//    /**
//     * ?????????????????????????????????base64???????????????
//     * ????????????JS
//     */
//    fun addImages(base64: String, path: String) {
//        var str0 = "data:image/png;base64,$base64"
//        imgs.add(mutableMapOf("str" to str0, "index" to answerIndex.toString()))
////        imgPaths.add(mutableMapOf("str" to path, "index" to answerIndex.toString()))
//        imgPaths.add(File(path))
//        showImages()
//    }
//
//    fun showImages() {
//        var jsonData = JsonObject()
//        var images = JsonArray()
//        imgs.forEach {
//            if (it["index"] == answerIndex.toString()) {
//                images.add(it["str"])
//            }
//        }
//        jsonData.addProperty("type", "imageUpload")
//        jsonData.add("image", images)
//
//        webView.send(jsonData.toString()) {
//            toast(it)
//        }
//    }
//
//    fun getCollection() = item?.isCollected
//
//    fun setCollection(flag: String) {
//        item?.let {
//            it.isCollected = flag
//        }
//    }
//
//    companion object {
//        @JvmStatic
//        fun newInstance(item: ExerciseFrameItem) =
//                ExerciseDetailFragment().apply {
//                    arguments = Bundle().apply {
//                        putSerializable(ITEM, item)
//                    }
//                }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
//            webView.loadUrl("javascript:paySuccess()")
//            evalMode = "1"
//        }
//    }
//
//    fun initDefaultEvalMode() {
//        //??????evalMode?????????
//        // questionInfo isOwn 0:????????????1???????????? ?????????????????? ????????????
//        // evalMode    0?????? ,1????????????, 2????????????, 10??????+????????????,20??????+????????????
//        when (item!!.questionInfo!!.evalMode) {
//            "0" -> {
//                evalMode = "0"
//            }
//            "1" -> {
//                evalMode = "1"
//            }
//            "2" -> {
//                if (item!!.questionInfo!!.isOwn == "1") {
//                    evalMode = "1"
//                }
//            }
//            "10" -> {
//                //???????????????????????????
//                evalMode = "0"
//            }
//            "20" -> {
//                //???????????????????????????
//                if (item!!.questionInfo!!.isOwn == "1") {
//                    evalMode = "1"
//                } else {
//                    evalMode = "0"
//                }
//            }
//        }
//    }
//
//}
//
//
//inline fun ViewManager.mWebView(): BridgeWebView = mWebView() {}
//inline fun ViewManager.mWebView(init: (BridgeWebView).() -> Unit): BridgeWebView {
//    return ankoView({ BridgeWebView(it) }, theme = 0) { init() }
//}