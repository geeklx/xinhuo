package com.spark.peak.ui.exercise.parsing

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.spark.peak.R
import com.spark.peak.base.LifeFragment
import com.spark.peak.bean.ExerciseFrameItem
import com.spark.peak.ui.exercise.detail.ExerciseDetailPresenter
import com.spark.peak.ui.exercise.detail.mWebView
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject
import java.util.*
import kotlin.properties.Delegates

private const val ITEM = "item"

@RequiresApi(Build.VERSION_CODES.HONEYCOMB)
class ExerciseParsingFragment : LifeFragment<ExerciseDetailPresenter>() {
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

    /**
     * 存在小题时，小题区域顶端 距离webview的默认距离
     * 用于解决滑动事件冲突
     */
    val defaultLineY by lazy { webView.height * 0.3 }
    var webHeight = 0.0 //webView中 web页面的高度，用于计算

    var lineY = 0.0


    private fun changePage(){
        Log.d("=====touch===start====",startX.toString()+"========"+endX)
        var moveX = endX - startX
        if (moveX < -50 && currentSort == total-1){
            parentActivity.goNext()
        }else if (moveX > 50 && currentSort == 0){
            parentActivity.goBefore()
        }
    }

    fun isNode(): Boolean {
        return item.isNode
    }

//    fun index(): Int {
//        return item.index
//    }

    /**
     * 这两个值用于表示当前题号
     * 针对个别题目在web中有翻页，小题。
     * 展示题号时 相加并+1 ，用于查找题目索引时 仅相加
     */
    var qIndex = 0
    var answerIndex = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView(): View? = this.requireContext().relativeLayout {
        webView = mWebView {
            id = R.id.web_view
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true

            /**
             * 用于解决webview中滑动与viewpager冲突的问题
             */
            registerHandler("touchEvent") { data, function ->
                var jsonData = JSONObject(data)
                flag = jsonData.getString("type")

                if (flag == "touchStart"){
                    startX = jsonData.getDouble("offsetX")
                    currentSort = jsonData.getInt("index")
                    total = jsonData.getInt("total")
                    Log.d("=====touch===start====",startX.toString())
                }else if (flag == "touchEnd"){
                    endX = jsonData.getDouble("offsetX")
                    Log.d("=====touch===end=====",startX.toString())
                    changePage()
                }
            }

            var lastState = ""
            var lastTime: Long = 0
            var touchFlag = false
            setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (!item.isNode && (item.questionType == "4"||item.questionType == "5") ){//填空题或材料题，存在小题时，启用判断横向滑动区域的交互
                            var y = if (lineY == 0.0) defaultLineY else lineY
                            var yy = motionEvent.y
                            if (yy < y){
                                requestDisallowInterceptTouchEvent(false)
                            }else{
                                requestDisallowInterceptTouchEvent(true)
                            }
                        }else{
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

    var item by Delegates.notNull<ExerciseFrameItem>()

    var musicUrl = ""
    var total = 0 //小题数
    private var isLast = false //标识是否为当前题目的最后一个小题
    override fun initData() {
        /**
         * 存在小题时，上下滑动小题区域的回调
         * 返回当前小题区域顶端距离web顶端的px值
         * 用于计算当前触摸区域 在大题区域 还是 小题区域
         * 解决滑动事件冲突问题
         */
        webView.registerHandler("touchHeight"){data, _ ->
            if (data.isNotEmpty()) {
                try {
                    var jsonData = JSONObject(data)
                    var top = jsonData.getString("top")
                    var height = jsonData.getString("height")
                    if (top.isNotEmpty() && height.isNotEmpty()){
                        var pxY = top.replace("px","").toDouble()
                        webHeight = height.replace("px","").toDouble()

                        lineY = webView.height * (pxY / webHeight) // 计算出当前 小题区域与大题区域的分界线

                        Log.e("touchHeight", "webH---$webHeight   pxY-----$pxY")
                        Log.e("touchHeight", "webViewH---"+webView.height+"   lineY-----$lineY")
                    }
                } catch (e: Exception) {
                }
            }
        }

        /**
         * web中翻页的交互，用于变更右上角的题号
         */
        webView.registerHandler("touchIndex") { data, _ ->
            musicStop()
            if (item.questionType == "5") { //当前为材料题时，执行翻页变题号的操作
                answerIndex = data.toInt()
                parentActivity.setQIndex((qIndex+answerIndex+1).toString())
            }
        }
        arguments?.let {
            item = it.getSerializable(ITEM) as ExerciseFrameItem
            if (item.isNode) {
                //判断为展示题型信息页面，则显示题型详情
                tvPaperName.text = item.paperName
                tvQName.text = item.groupName
                tvQExplain.text = item.paperExplain
                tvRemarks.text = item.remarks
            } else {
                var htmlStr = parentActivity.htmlStr.toString()
                val gson = GsonBuilder().disableHtmlEscaping().create()
                htmlStr = htmlStr.replace("\${question-content}", gson.toJson(item.questionInfo))
                htmlStr = htmlStr.replace("\${module-type}", "2")
                webView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null)
            }
        }

        /**
         * 题目反馈交互
         */
        webView.registerHandler("feedback"){data, _ ->
            Log.d("feedback",data)
            var map = HashMap<String,String>()
            map["questionkey"] = item.questionKey?:""
            map["content"] = data
            presenter.questionFB(map){
                toast("反馈成功")
            }
        }

        /**
         * 播放音频的回调，发过来的是音频url
         * 用于暂停音频
         */
        webView.registerHandler("sendUrl"){data, _ ->
            if (musicUrl.isNotEmpty() && musicUrl != data){
                musicStop()
            }
            musicUrl = data
        }
    }

    /**
     * 当前页面不可见时，activity回调该方法，停止web播放音频
     */
    fun musicStop(){
        if (musicUrl.isNotEmpty()){
            var jsonData = JsonObject()
            jsonData.addProperty("type","musicStop")
            jsonData.addProperty("url",musicUrl)

            webView.send(jsonData.toString()){
                toast(it)
            }
        }
    }

    /**
     * 点击跳转指定小题
     */
    fun jumpIndex(index: Int){
        var jsonData = JsonObject()
        jsonData.addProperty("type","setIndex")
        jsonData.addProperty("index",index)

        webView.send(jsonData.toString()){
            toast(it)
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

}