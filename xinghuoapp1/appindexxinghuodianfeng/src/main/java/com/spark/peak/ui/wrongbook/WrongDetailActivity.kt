package com.spark.peak.ui.wrongbook

import android.Manifest
import android.util.Log
import android.view.View
import com.example.questions.QuestionlistBean
import com.example.questions.dadpter.QuestionAdapter
import com.example.questions.utils.MediaPlayerUtlis
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.WrongBook
import com.spark.peak.utlis.DownloadManager
import com.spark.peak.utlis.PermissionUtlis
import kotlinx.android.synthetic.main.activity_wrong_detail.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

class WrongDetailActivity : LifeActivity<WrongBookPresenter>() {
    override val presenter: WrongBookPresenter
        get() = WrongBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_wrong_detail

    val wrongItem by lazy { intent.getSerializableExtra(WRONG_ITEM) as WrongBook }
    val type by lazy { intent.getStringExtra(TYPE) ?: "0" }

    companion object {
        val WRONG_ITEM = "wrong_item"
        val TYPE = "type"//从那个地方调过来1：从消息中，隐藏移除错题本；默认0：错题本
    }

    override fun configView() {
        super.configView()
        setSupportActionBar(tb_wrong_detail)
        if ("1" == type) {
            tv_remove.visibility = View.GONE
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        tb_wrong_detail.setNavigationOnClickListener { onBackPressed() }
        tv_remove.setOnClickListener {
            val map = HashMap<String, String>()
            map.put("errorkey", wrongItem.errorkey)
            presenter.deleteWrongItem(map) {
                toast("移出成功")
                setResult(123)
                finish()
            }
        }

        /**
         * 题目反馈交互
         */
        webView.registerHandler("feedback") { data, _ ->
            Log.d("feedback", data)
            val map = HashMap<String, String>()
            map["questionkey"] = wrongItem.questionkey
            map["content"] = data
            presenter.questionFB(map) {
                toast("反馈成功")
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
    }

    override fun initData() {
        super.initData()
        initHtmlStr()
        presenter.getExerciseParsing(wrongItem.paperkey, wrongItem.questionkey, wrongItem.questiontype, wrongItem.userpracticekey, wrongItem.sort){
            setData(it)
        }
    }

    var html : StringBuilder = StringBuilder()
    private fun initHtmlStr () {
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
        if (json.has("questionType") && "5" == json.getString("questionType") && json.has("lrcUrl") && json.getString("lrcUrl").isNotBlank()) {
            val question = Gson().fromJson(json.toString(), QuestionlistBean.QuestionInfoBean::class.java)
            val lrcUrl = json.getString("lrcUrl")
            val resourceKey = json.getString("resourceKey")
            questions_view.visibility = View.VISIBLE
            webView.visibility = View.GONE
            questions_view.setData("", "", question, QuestionAdapter(isFooter = false, isFeedback = false) {

            }, true)
            getLrc(lrcUrl)
            audioPlay(resourceKey)
        } else if (json.has("resourceKey")) {
            questions_view.visibility = View.GONE
            webView.visibility = View.VISIBLE
            val resourceKey = json.getString("resourceKey")
            if (resourceKey.isNotBlank()) {
                presenter.getResInfo(resourceKey, "3", "0") {
                    json.put("audioFile", it.url)
                    setHtml(json.toString())
                }
            } else {
                setHtml(json.toString())
            }
        } else {
            questions_view.visibility = View.GONE
            webView.visibility = View.VISIBLE
            setHtml(json.toString())
        }
    }

    private fun setHtml(json: String) {
        var htmlStr = html.toString()
        htmlStr = htmlStr.replace("\${question-content}", json)
        htmlStr = htmlStr.replace("\${module-type}", "2")
        webView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null)
    }

    override fun onPause() {
        super.onPause()
        musicStop()
    }

    /**
     * 当前页面不可见时，activity回调该方法，停止web播放音频
     */
    var musicUrl = ""
    private fun musicStop() {
        if (musicUrl.isNotEmpty()) {
            val jsonData = JsonObject()
            jsonData.addProperty("type", "musicStop")
            jsonData.addProperty("url", musicUrl)

            webView.send(jsonData.toString()) {
                toast(it)
            }
        }
    }

    fun audioPlay(audioPlay: String) {
        MediaPlayerUtlis.reset()
        presenter.getResInfo(audioPlay, "3", "0") {
            MediaPlayerUtlis.start(it.url,
                    onStart = {
                        questions_view.start(it)
                    },
                    onCompletion = {
                        questions_view.onCompletion()
                    },
                    onError = {

                    },
                    status = {
                        questions_view.status(it)
                    },
                    callBack = {
                        questions_view.progress(it)
                    })
        }
    }

    override fun onStop() {
        super.onStop()
        MediaPlayerUtlis.pause()
    }

    fun getLrc(url: String) {
        if (url.isNotEmpty()) {
            val path = DownloadManager.getFilePath(url)
            val lycFile = File(path)
            if (lycFile.exists()) {
                questions_view.initLrcData(lycFile)
            } else {
                PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE) {
                    val path = DownloadManager.getFilePath(url)
                    val tempFile = File(path + ".temp")
                    if (tempFile.exists()) {
                        tempFile.delete()
                    }
                    FileDownloader.setup(ctx)
                    FileDownloader.getImpl().create(url).setPath(DownloadManager.getFilePath(url))
                            .setListener(object : FileDownloadListener() {
                                override fun warn(task: BaseDownloadTask?) {}

                                override fun completed(task: BaseDownloadTask?) {
                                    try {
                                        val file = File(task?.path ?: "")
                                        questions_view.initLrcData(file)
                                    } catch (e: Exception) {
                                    }
                                }

                                override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {}

                                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                                    //下载报错temp文件内容正常，取巧
                                    try {
                                        val tempFile = File(task?.path + ".temp")
                                        val file = File(task?.path)
                                        tempFile.renameTo(file)
                                        questions_view.initLrcData(file)
                                    } catch (e: Exception) {
                                    }
                                }

                                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {}

                                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {}
                            }).start()
                }
            }
        }
    }
}
