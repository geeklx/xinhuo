package tuoyan.com.xinghuo_dayingindex.ui.mine.wrong
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.app.AlertDialog
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_wrong_detail.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.VideoParam
import tuoyan.com.xinghuo_dayingindex.bean.WrongBook
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class WrongDetailActivity : LifeActivity<WrongPresenter>() {
    override val presenter: WrongPresenter
        get() = WrongPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_wrong_detail
    var musicUrl = ""

    val wrongItem by lazy { intent.getSerializableExtra(WRONG_ITEM) as WrongBook }

    companion object {
        val WRONG_ITEM = "wrong_item"
    }

    override fun configView() {
        super.configView()
    }

    override fun handleEvent() {
        super.handleEvent()
        tv_remove.setOnClickListener { _ ->
            AlertDialog.Builder(ctx).setMessage("确定要移出该错题吗？").setPositiveButton("确定") { _, _ ->
                val map = HashMap<String, String>()
                map.put("errorkey", wrongItem.errorkey)
                presenter.deleteWrongItem(map) {
                    mToast("移出成功")
                    setResult(123)
                    finish()
                }
            }.setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }.create().show()
        }

        /**
         * 题目反馈交互
         */
        webView.registerHandler("feedback") { data, _ ->
            Log.d("feedback", data)
            val jsonData = JSONObject(data)
            val content = jsonData.getString("content")
            val remark = jsonData.getString("remark")
            if (content == "null" && remark == "") {
                toast("请选择错误类型或输入反馈内容")
            } else if (content == "null") {
                toast("请选择错误类型")
            } else {
                val map = HashMap<String, String>()
                map["questionkey"] = wrongItem.questionkey ?: ""
                map["content"] = content
                map["remark"] = remark

                presenter.questionFB(map) {
                    toast("反馈成功")
                }
            }
        }

        webView.registerHandler("playVideo") { data, _ ->
            val videoParam = VideoParam()
            videoParam.key = data
            startActivity<VideoActivity>(VideoActivity.VIDEO_PARAM to videoParam)
        }

        webView.registerHandler("sendUrl") { data, _ ->
            if (musicUrl.isNotEmpty() && musicUrl != data) {
                musicStop()
            }
            musicUrl = data
        }
    }

    override fun initData() {
        super.initData()
        initHtmlStr()
        presenter.getExerciseParsing(wrongItem.paperkey, wrongItem.questionkey, wrongItem.questiontype, wrongItem.userpracticekey, wrongItem.sort) {
            setData(it)
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

    val html: StringBuilder = StringBuilder()
    private fun initHtmlStr() {
        val ips: InputStream = resources.assets.open("index.html")
        val reder = BufferedReader(InputStreamReader(ips))
        var str = reder.readLine()

        while (str != null) {
            html.append(str + "\n")
            str = reder.readLine()
        }
    }

    private fun setData(data: Any) {
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = JSONObject(gson.toJson(data))
        if (json.has("resourceKey")) {
            val resourceKey = json.getString("resourceKey")
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

    private fun setHtml(json: String) {
        var htmlStr = html.toString()
        htmlStr = htmlStr.replace("\${question-content}", json)
        htmlStr = htmlStr.replace("\${module-type}", "2")
        webView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null)
    }

    override fun onResume() {
        super.onResume()
        webView.resumeTimers()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
        webView.pauseTimers()
    }

    override fun onDestroy() {
        webView.destroy()
        musicStop()
        super.onDestroy()
    }
}
