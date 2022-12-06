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
//    //TODO 主观题需要上传图片时，存储的base64数组,[str] ->base64，
//    // [index] ->对应的小题索引（针对材料题中存在主观题的情况），默认为0
//    val imgs = mutableListOf<Map<String, String>>()
//
//    //TODO 主观题需要上传图片时，存储的filePath数组，[str] ->图片路径，
//    // [index] ->对应的小题索引（针对材料题中存在主观题的情况），默认为0
////    val imgPaths = mutableListOf<Map<String, String>>()
//    val imgPaths = ArrayList<File>()
//
//    //0：自测；1：人工
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
//     * 存在小题时，小题区域顶端 距离webview的默认距离
//     * 用于解决滑动事件冲突
//     */
//    val defaultLineY by lazy { webView.height * 0.3 }
//    var webHeight = 0.0 //webView中 web页面的高度，用于计算
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
//        return try {  //TODO 避免主观题下，跳转默认题目时，item未完成初始化报错
//            item?.isNode ?: false
//        } catch (e: Exception) {
//            return false
//        }
//    }
//
//    /**
//     * 这两个值用于表示当前题号
//     * 针对个别题目在web中有翻页，小题。
//     * 展示题号时 相加并+1 ，用于查找题目索引时 仅相加
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
//             * 用于解决webview中滑动与viewpager冲突的问题
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
//                        if (item?.isNode != true && (item?.questionType == "4" || item?.questionType == "5")) {//TODO 填空题或材料题，存在小题时，启用判断横向滑动区域的交互
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
//                typeface = Typeface.defaultFromStyle(Typeface.BOLD) // 加粗
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
//    var total = 0 //小题数
//    private var isLast = false //标识是否为当前题目的最后一个小题
//    var musicUrl = ""
//
//    override fun initData() {
//        /**
//         * 存在小题时，上下滑动小题区域的回调
//         * 返回当前小题区域顶端距离web顶端的px值
//         * 用于计算当前触摸区域 在大题区域 还是 小题区域
//         * 解决滑动事件冲突问题
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
//                        lineY = webView.height * (pxY / webHeight) // 计算出当前 小题区域与大题区域的分界线
//                    }
//                } catch (e: Exception) {
//                }
//            }
//        }
//
//
//        /**
//         * 用户做题部分 与 JS的交互
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
//                    //TODO 专项练习的主观题单独处理交卷
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
//                                    //填空题 不计算小题索引 ↓↓↓↓↓↓
//                                    if (it.index == qIndex + (if (item!!.questionType ?: "" == "4") 0 else currentSort) + 1) { //用答案列表中的题号比对当前题目的题号，相同则执行当前题目的做题方法
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
//                                    if (it.index == qIndex + currentSort + 1) { //用答案列表中的题号比对当前题目的题号，相同则执行当前题目的做题方法
//                                        setAnswerItemsArray(it, value)
//                                    }
//                                }
//                            }
//                        } else if (jsonData.optString("value") != null && jsonData.optString("value") == "noFull") {
//                            toast("请将评分标准填写完整")
//                        } else {
//                            toast("请正确作答")
//                        }
//                    } catch (e: Exception) {
//                        print(e)
//                        toast("请正确作答")
//                    }
//                }
//            }
//
//        }
//        /**
//         * web中翻页的交互，用于变更右上角的题号
//         */
//        webView.registerHandler("touchIndex") { data, _ ->
//            if (data.toInt() == total - 1) isLast = true
//            if (item?.questionType == "5") { //当前为材料题时，执行翻页变题号的操作
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
//         * 播放音频的回调，发过来的是音频url
//         * 用于暂停音频
//         */
//        webView.registerHandler("sendUrl") { data, _ ->
//            if (musicUrl.isNotEmpty() && musicUrl != data) {
//                musicStop()
//            }
//            musicUrl = data
//        }
//
//        /**
//         * 题目反馈交互
//         */
//        webView.registerHandler("feedback") { data, _ ->
//            Log.d("feedback", data)
//            var jsonData = JSONObject(data)
//            var content = jsonData.getString("content")
//            var remark = jsonData.getString("remark")
//            if (content == "null" && remark == "") {
//                toast("请选择错误类型或输入反馈内容")
//            } else if (content == "null") {
//                toast("请选择错误类型")
//            } else {
//                var map = HashMap<String, String>()
//                map["questionkey"] = item?.questionKey ?: ""
//                map["content"] = content
//                map["remark"] = remark
//
//                presenter.questionFB(map) {
//                    toast("反馈成功")
//                }
//            }
//
//        }
//
//        webView.registerHandler("addImages") { _, _ ->
//            //TODO 主观题上传图片
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
//                //判断为展示题型信息页面，则显示题型详情
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
//                //判断为webview页面，则请求题目详情
//                llQinfo.visibility = View.GONE
//                var htmlStr = parentActivity.htmlStr.toString()
//                val gson = GsonBuilder().disableHtmlEscaping().create()
//                htmlStr = htmlStr.replace("\${question-content}", gson.toJson(item!!.questionInfo))
////                <!--module-type:1.做题 2.解析 module-subject:是否自评 0.非自评 1.自评 2.人工阅卷 -->
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
//                    //TODO 专项练习主观题，存在历史答案时，传给js
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
//    var audioUrl = "" //TODO 重新获取 听力题型流化地址
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
//     * 给答案列表中的数据设置答案
//     */
//    private fun setAnswerItems(answerQuestion: AnswerQuestion, order: String, answer: String) {
//        item ?: return
//        if (answer.isNotEmpty()) {
//            var haveDone = false
//            for (i in 0 until answerQuestion.answers.size) {
//                if (answerQuestion.answers[i].order == order) { // 若这个题已经做过，则用新答案覆盖answer
//                    haveDone = true
//                    answerQuestion.answers[i].answer = answer
//                }
//            }
//            if (!haveDone) { // 若这个题没做过，直接插入AnswerItem
//                var answerItem = AnswerItem(order, answer)
//                answerQuestion.answers.add(answerItem)
//            }
//            answerQuestion.haveDone = true //最后把当前的题目置为已做答
//        }
//        parentActivity.saPaperAnswer(answerQuestion)
//        if (item!!.questionType ?: "" == "1" || item!!.questionType ?: "" == "3" || item!!.questionType ?: "" == "6" || (item!!.questionType ?: "" == "4" && isLast) || (item!!.questionType ?: "" == "5" && isLast)) {
//            parentActivity.goNext() //当前题目为 单选题、判断题、主观题、最后一个填空题的小题、最后一个材料题的小题时，做完当前题目，翻页
//        }
//
//    }
//
//    /**
//     * 给答案列表中的数据设置答案
//     * 多选题 及 材料题中的填空题时（web返回jsonarry时）调用
//     */
//    private fun setAnswerItemsArray(answerQuestion: AnswerQuestion, jsonArray: JSONArray) {
//        answerQuestion.answers.clear()
//        if (answerQuestion.type == "4" || answerQuestion.type == "6") {
//            //填空题
//            for (i in 0 until jsonArray.length()) {
//                var jsonObject = jsonArray[i] as JSONObject
//                var answerItem = AnswerItem(jsonObject.getString("order"), jsonObject.getString("answer"), if (jsonObject.has("score")) jsonObject.getString("score") else "")
//                answerQuestion.answers.add(answerItem)
//            }
//            parentActivity.goNext()
//        } else if (answerQuestion.type == "2") {
//            //多选题
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
//        answerQuestion.haveDone = true //最后把当前的题目置为已做答
//    }
//
//    /**
//     * 当前页面不可见时，activity回调该方法，停止web播放音频
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
//     * 点击跳转指定小题
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
//            //加载完毕了执行
//            isJump = true
//            jumpToIndex = index
//        }
//    }
//
//    /**
//     * 选择图片后，将转换后的base64添加进数组
//     * 并发送给JS
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
//        //当前evalMode默认值
//        // questionInfo isOwn 0:未购买；1：已购买 主观题已购买 人工阅卷
//        // evalMode    0自判 ,1免费人工, 2付费人工, 10自判+免费人工,20自判+付费人工
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
//                //用户选择，返回赋值
//                evalMode = "0"
//            }
//            "20" -> {
//                //用户选择，返回赋值
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