package com.spark.peak.ui.mine.offline

import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.spark.peak.MyApp
import com.spark.peak.R
import com.spark.peak.base.BaseActivity
import com.spark.peak.bean.AudioRes
import com.spark.peak.bean.LrcInfo
import com.spark.peak.bean.OfflineRes
import com.spark.peak.ui._public.LocalPdfActivity
import com.spark.peak.ui.dialog.AlertDialog
import com.spark.peak.ui.video.AudioActivity
import com.spark.peak.ui.video.SparkVideoActivity
import com.spark.peak.utlis.DownloadManager
import com.spark.peak.utlis.FileUtils
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_offline_res.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import java.io.File

class OfflineResActivity(override val layoutResId: Int = R.layout.activity_offline_res) :
    BaseActivity() {
    companion object {
        const val NAME = "name"
        const val TYPE = "type"
    }

    private val name by lazy { intent.getStringExtra(NAME) ?: "" }
    private val type by lazy { intent.getStringExtra(TYPE) ?: "" }
    private val adapter = OfflineResAdapter({ position, item ->
        delete(position, item)

    }) { position, item ->
        if (item.name.endsWith(".mp3") || item.name.endsWith(".m4a")) {
            toAudio(item)
        }
        if (item.name.endsWith(".mp4")) {
            val property = JSONObject()
            property.put("course_name", name)
            property.put("period_name", item.name)
            SpUtil.SenorData = property.toString()
            SensorsDataAPI.sharedInstance().track("df_start_watch_course_video", property)
            startActivity<SparkVideoActivity>(
                SparkVideoActivity.URL to item.path,
                SparkVideoActivity.TITLE to item.name.substring(0, item.name.lastIndexOf("."))
            )
        }
        if (item.name.endsWith(".pdf")) {
            startActivity<LocalPdfActivity>(
                LocalPdfActivity.PATH to item.path,
                LocalPdfActivity.NAME to item.name
            )
        }
    }

    private fun delete(position: Int, item: OfflineRes) {
        item.path?.let {
            AlertDialog(ctx, "确定要删除吗？") {
                val file = File(it)
                if (FileUtils.deleteFile(file))
                    adapter.remove(position)
                if (adapter.getDateCount() == 0) onBackPressed()
            }.show()

        }
    }

    private fun toAudio(item: OfflineRes) {
        val data = mutableListOf<AudioRes>()
        var index = 0
        val lrcInfoList: List<LrcInfo> =
            Gson().fromJson(SpUtil.lrcInfo, object : TypeToken<List<LrcInfo>>() {}.type)
        adapter.getData().forEach {
            it?.let { adapterItem ->
                if (adapterItem == item) {
                    index = data.size
                }
                if (adapterItem.name.endsWith(".mp3") || adapterItem.name.endsWith(".m4a")) {
                    val res = AudioRes()
                    res.playUrl = adapterItem.path
                    res.name = adapterItem.name
                    kotlin.run breaking@{
                        lrcInfoList.forEach {
                            if (it.resName == res.playUrl) {
                                res.lrcurl = it.lrcUrl
                                res.duration = it.duration
                                return@breaking
                            }
                        }
                    }
                    data.add(res)
                }
            }
        }

//        MyApp.instance.bookres = data
        // 存
        val list1: List<AudioRes> = ArrayList()
        val gson1 = Gson()
        val data1 = gson1.toJson(data)
        SPUtils.getInstance().put("AudioRes", data1)
        startActivity<AudioActivity>(
            AudioActivity.DATA to data,
            AudioActivity.POSITION to index,
            AudioActivity.TYPE to type,
            AudioActivity.NAME to name
        )
    }

    override fun configView() {
        recycler_offline_res.layoutManager = LinearLayoutManager(ctx)
        val decoration = DividerItemDecoration(ctx, LinearLayout.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.drawable.divider))
        recycler_offline_res.addItemDecoration(decoration)
        recycler_offline_res.adapter = adapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        tv_title.text = name
        getData(type, name)
    }

    /**
     * @param type pt(配套) wk（网课）
     */
    private fun getData(type: String, name: String) {
        val data = mutableListOf<OfflineRes>()
        val list = DownloadManager.getFileByBook(type, name)
        list.forEach {
            data.add(OfflineRes(it.name, it.path))
        }
        adapter.setData(data)
    }

}
