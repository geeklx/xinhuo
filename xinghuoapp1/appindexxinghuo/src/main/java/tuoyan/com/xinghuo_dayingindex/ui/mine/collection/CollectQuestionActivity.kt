package tuoyan.com.xinghuo_dayingindex.ui.mine.collection

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_wrong_detail.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EventMsg
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.Paper
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class CollectQuestionActivity : LifeActivity<CollectionPresenter>() {
    override val presenter: CollectionPresenter
        get() = CollectionPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_collect_detail


    val paperItem by lazy { intent.getSerializableExtra(ITEM) as Paper }
    val position by lazy { intent.getIntExtra(ITEM, -1) }

    companion object {
        val ITEM = "wrong_item"
        val POSITION = "position"
    }

    override fun configView() {
        super.configView()
    }

    override fun handleEvent() {
        super.handleEvent()
        tv_remove.setOnClickListener { _ ->
            presenter.deleteCollection(paperItem.key ?: "") {
                //TODO eventBus 刷新收藏列表
                EventBus.getDefault().post(EventMsg("question", position))
                mToast("移出成功")
                finish()
            }
        }

//        /**
//         * 题目反馈交互
//         */
//        webView.registerHandler("feedback"){data, _ ->
//            Log.d("feedback",data)
//            var map = HashMap<String,String>()
//            map["questionkey"] = wrongItem.questionkey
//            map["content"] = data
//            presenter.questionFB(map){
//                toast("反馈成功")
//            }
//        }
    }

    override fun initData() {
        super.initData()
        initHtmlStr()
        presenter.questionAnalyze(paperItem.key ?: "", paperItem.type ?: "") {
            setData(it)
        }
    }

    var html: StringBuilder = StringBuilder()
    private fun initHtmlStr() {
        var ips: InputStream = resources.assets.open("index.html")
        var reder = BufferedReader(InputStreamReader(ips))
        var str = reder.readLine()

        while (str != null) {
            html.append(str + "\n")
            str = reder.readLine()
        }
    }

    var musicUrl = ""
    private fun setData(data: Any) {

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

        val gson = GsonBuilder().disableHtmlEscaping().create()
        var json = JSONObject(gson.toJson(data))
        if (json.has("resourceKey")) {
            var resourceKey = json.getString("resourceKey")
            if (resourceKey.isNotBlank()) {
                presenter.getResourceInfo(resourceKey, "3") {
                    json.put("audioFile", it.url)
                    setHtml(json.toString())
                }
            } else {
                setHtml(json.toString())
            }
        } else {
            setHtml(json.toString())
        }
    }

    fun setHtml(json: String) {
        var htmlStr = html.toString()
        htmlStr = htmlStr.replace("\${question-content}", json)
        htmlStr = htmlStr.replace("\${module-type}", "2")
        webView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null)
    }

    /**
     * 当前页面不可见时，activity回调该方法，停止web播放音频
     */
    fun musicStop() {
        if (musicUrl.isNotEmpty()) {
            var jsonData = JsonObject()
            jsonData.addProperty("type", "musicStop")
            jsonData.addProperty("url", musicUrl)

            webView.send(jsonData.toString()) {
                toast(it)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        musicStop()
    }

}
