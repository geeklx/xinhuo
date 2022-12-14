package com.spark.peak.ui.study.book

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.spark.peak.MyApp
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.LifeFragment
import com.spark.peak.bean.*
import com.spark.peak.ui._public.ImageActivity
import com.spark.peak.ui._public.LocalPdfActivity
import com.spark.peak.ui._public.PDFActivity
import com.spark.peak.ui._public.WebViewActivity
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.exercise.detail.ExerciseDetailActivity2
import com.spark.peak.ui.exercise.parsing.ExerciseParsingActivity
import com.spark.peak.ui.lesson.LessonDetailActivity
import com.spark.peak.ui.study.book.adapter.ResourceAdapter
import com.spark.peak.ui.video.AudioActivity
import com.spark.peak.ui.video.SparkVideoActivity
import com.spark.peak.utlis.DownloadManager
import com.spark.peak.utlis.NetWorkUtils
import com.spark.peak.utlis.PermissionUtlis
import com.spark.peak.utlis.SpUtil
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.io.File
import kotlin.properties.Delegates

private const val DATA = "data"
private const val COUNT = "count"
private const val SIZE = "size"
private const val IS_OWN = "isown"
private const val ENABLE = "enable"
private const val TYPE = "type"
private const val NAME = "name"


/**
 * Created by ?????? on 2018/5/10.
 */
open class BookResourceFragment : LifeFragment<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int = 0


    private var tv_resource_mark by Delegates.notNull<TextView>()
    private var tv_downloadAll by Delegates.notNull<TextView>()
    private var rv_resource_list by Delegates.notNull<RecyclerView>()

    val parent by lazy { activity }

    private var oAdapter = ResourceAdapter({ item ->
        item?.let {
            if (parent is BookDetailActivity) {
                (parent as BookDetailActivity).operation("????????????", it, "????????????")
            }
            if (parent is LessonDetailActivity) {
                (parent as LessonDetailActivity).saOperation("????????????", it)
            }
        }
        PermissionUtlis.checkPermissions(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) {
            resourceJump(item)
        }
    }) { type, id, name, url, position, count, res ->
        if (parent is BookDetailActivity) {
            (parent as BookDetailActivity).operation("????????????", res, "????????????")
        }
        if (parent is LessonDetailActivity) {
            (parent as LessonDetailActivity).saOperation("????????????", res)
        }
        PermissionUtlis.checkPermissions(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) {
            when (type) {
                StudyResource.STATE_DEFAULT -> {
//                    ???0?????????  ???1?????????
                    presenter.getResInfo(res.id ?: "", if (this.type == "wk") "1.2" else "2", "1") {
                        res.downUrl = it.downloadUrl
                        res.playUrl = it.url
                        res.lrcurl = it.lrcurl1 ?: ""
                        netCheck(count) {
                            if (!res.downUrl.isNullOrEmpty()) {
                                val task = FileDownloader.getImpl().create(res.downUrl).setPath(
                                    DownloadManager.getFilePath(
                                        this.type,
                                        this.name,
                                        name,
                                        res.downUrl!!
                                    )
                                )
                                task.tag = res.id
                                if (res.lrcurl.isNotEmpty()) {//TODO ?????????????????????????????????,???????????????????????????url
                                    FileDownloader.getImpl()
                                        .create(res.lrcurl)
                                        .setPath(DownloadManager.getFilePath(res.lrcurl))
                                        .setListener(object : FileDownloadListener() {
                                            override fun warn(task: BaseDownloadTask?) {}

                                            override fun completed(task: BaseDownloadTask?) {}

                                            override fun pending(
                                                task: BaseDownloadTask?,
                                                soFarBytes: Int,
                                                totalBytes: Int
                                            ) {
                                            }

                                            override fun progress(
                                                task: BaseDownloadTask?,
                                                soFarBytes: Int,
                                                totalBytes: Int
                                            ) {
                                            }

                                            override fun paused(
                                                task: BaseDownloadTask?,
                                                soFarBytes: Int,
                                                totalBytes: Int
                                            ) {
                                            }

                                            override fun error(
                                                task: BaseDownloadTask?,
                                                e: Throwable?
                                            ) {
                                                //????????????temp???????????????????????????
                                                try {
                                                    var tempFile = File(task?.path + ".temp")
                                                    var file = File(task?.path)
                                                    tempFile.renameTo(file)
                                                } catch (e: Exception) {
                                                }
                                            }
                                        })
                                        .start()

                                    var lrcInfoList = if (SpUtil.lrcInfo.isEmpty()) {
                                        mutableListOf<LrcInfo>()
                                    } else {
                                        Gson().fromJson(
                                            SpUtil.lrcInfo,
                                            object : TypeToken<List<LrcInfo>>() {}.type
                                        )
                                    }
                                    var havaSame = false
                                    kotlin.run breaking@{
                                        lrcInfoList.forEach {
                                            if (it.resName == task.path) {
//                                          lrcInfoList.remove(it)//TODO ?????????
                                                it.lrcUrl = res?.lrcurl ?: ""
                                                it.id = res?.id ?: ""
                                                it.duration = res?.duration ?: ""
                                                havaSame = true
                                                return@breaking
                                            }
                                        }
                                    }
                                    if (!havaSame) lrcInfoList.add(
                                        LrcInfo(
                                            task.path,
                                            res?.lrcurl ?: "",
                                            res.id ?: "",
                                            res.duration ?: ""
                                        )
                                    )//TODO ?????????
                                    SpUtil.lrcInfo = Gson().toJson(lrcInfoList)//TODO ?????????
                                }
                                task.listener = listener
                                task.start()
                                DownloadManager.taskList.add(task)
                                DownloadManager.saveDownloadInfo(task.id, task.url)
                                refreshDownloadState(
                                    res.id
                                        ?: "", task.id, StudyResource.STATE_DOWNLOADING
                                )


                            }
                        }
                    }

                }
                StudyResource.STATE_DOWNLOADING -> {
                    Log.e("PAUSED", "PAUSED!!!!!!!!!!!!!")
                    var task = DownloadManager.getRunTask(res.id ?: "")
                    task?.let {
                        it.pause()
                        refreshDownloadState(res.id ?: "", it.id, StudyResource.STATE_PAUSED)
                    }
                }
                StudyResource.STATE_PAUSED -> {
                    netCheck(count) {
                        var task = DownloadManager.getRunTask(res.id ?: "")
                        task?.let {
                            it.reuse()
                            it.start()
                            refreshDownloadState(
                                res.id
                                    ?: "", it.id, StudyResource.STATE_DOWNLOADING
                            )
                        }
                    }
                }
            }
        }

    }

    private var listener = object : FileDownloadListener() {
        var oldProgress = 0
        override fun warn(task: BaseDownloadTask?) {
        }

        override fun completed(task: BaseDownloadTask?) {
            oldProgress = 0
            task?.let {
                val key = it.tag as String?
                refreshDownloadState(key ?: "", it.id, StudyResource.STATE_DONE)
                refreshProgress(key ?: "", 0) //???????????????????????????????????????????????????0
            }
        }

        override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            toast("??????????????????")
        }

        override fun error(task: BaseDownloadTask?, e: Throwable?) {
            oldProgress = 0
            toast("????????????...")
            task?.let {
                var key = it.tag as String?
                refreshDownloadState(key ?: "", it.id, StudyResource.STATE_DEFAULT)
            }
        }

        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            task?.let {
                val key = it.tag as String?
                val progress =
                    if (soFarBytes > 10000) (soFarBytes / 100) / (totalBytes / 10000) else soFarBytes * 100 / totalBytes
                if (progress - oldProgress > 10) {
                    oldProgress = progress
                    refreshProgress(key ?: "", progress)
                }
            }
        }

        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            toast("???????????????...")
        }
    }

    /**
     * ????????????????????????
     * 1-paper:?????????
     * 2-paperAnalysis:????????????;
     * 3-media:??????
     * 4- picture:??????
     * 5-imagesText:??????;
     * 6-document:?????????
     * 7-frequency :??????;
     * 8-??????
     */
    private fun resourceJump(item: ResourceItem?) {
        item?.let {
            if (enable) {
                when (it.type) {
                    "1" -> {
                        if (parent is BookDetailActivity) {
                            startActivity<ExerciseDetailActivity2>(
                                ExerciseDetailActivity2.BOOK_KEY to (parent as BookDetailActivity).bookDetail?.supportingKey,
                                ExerciseDetailActivity2.KEY to item.id,
                                ExerciseDetailActivity2.NAME to item.name,
                                ExerciseDetailActivity2.BOOK_TITLE to (parent as BookDetailActivity).bookDetail?.titile,
                                ExerciseDetailActivity2.PARENT_KEY to item.parentKey,
                                ExerciseDetailActivity2.TYPE to ExerciseDetailActivity2.TYPE_QYT
                            )
                        }
                    }
                    "2" -> {
                        if (parent is BookDetailActivity) {
                            startActivity<ExerciseParsingActivity>(
                                ExerciseParsingActivity.KEY to item.id,
                                ExerciseParsingActivity.P_KET to "",
                                ExerciseParsingActivity.NAME to item.name
                            )
                        }
                    }
                    "3" -> {
                        val file = File(
                            DownloadManager.getFilePath(
                                type,
                                name,
                                item.name ?: "",
                                item.downUrl ?: ""
                            )
                        )
                        if (file.exists()) {
                            startActivity<SparkVideoActivity>(
                                SparkVideoActivity.TITLE to it.name,
                                SparkVideoActivity.URL to file.path
                            )
                        } else {
                            netCheck(it.count) {
                                startActivity<SparkVideoActivity>(
                                    SparkVideoActivity.TITLE to it.name,
                                    SparkVideoActivity.URL to item.playUrl
                                )
                            }
                        }
                    }
                    "4" -> {
                        startActivity<ImageActivity>(
                            ImageActivity.URL to it.link,
                            ImageActivity.NAME to it.name
                        )
                    }
                    "5" -> {
                        startActivity<WebViewActivity>(
                            WebViewActivity.URL to it.content,
                            WebViewActivity.TITLE to it.name
                        )
                    }
                    "6" -> {
                        val file = File(
                            DownloadManager.getFilePath(
                                type, name, item.name
                                    ?: "", it.link ?: ""
                            )
                        )
                        if (file.exists()) {
                            startActivity<LocalPdfActivity>(
                                LocalPdfActivity.PATH to file.path,
                                LocalPdfActivity.NAME to item.name
                            )
                        } else {
                            startActivity<PDFActivity>(
                                PDFActivity.KEY to it.id,
                                PDFActivity.NAME to item.name
                            )
                        }
                    }
                    "7" -> {
                        val data = mutableListOf<AudioRes>()
                        var index = 0
                        resourceList.forEach {
                            it.resource?.let {
                                if (it.id == item.id) {
                                    index = data.size
                                }
                                if (it.type == "7") {
                                    val res = AudioRes()
                                    val file = File(
                                        DownloadManager.getFilePath(
                                            type,
                                            name,
                                            it.name ?: "",
                                            it.downUrl ?: ""
                                        )
                                    )
                                    res.playUrl =
                                        if (file.exists()) file.path else /*it.playUrl ?:*/ ""
//                                    res.downUrl = it.downUrl ?: ""
                                    res.isCollection = it.isCollection
                                    res.id = it.id ?: ""
                                    res.downUrl = it.downUrl
                                    res.name = it.name ?: ""
                                    res.lrcurl = it.lrcurl ?: ""
                                    res.duration = it.duration ?: ""
                                    data.add(res)
                                }
                            }
                        }

//                        MyApp.instance.bookres = data
                        // ???
                        val list1: List<AudioRes> = ArrayList()
                        val gson1 = Gson()
                        val data1 = gson1.toJson(data)
                        SPUtils.getInstance().put("AudioRes", data1)
                        startActivity<AudioActivity>(
                            AudioActivity.URL to it.playUrl,
                            AudioActivity.TITLE to it.name,
                            AudioActivity.DATA to data,
                            AudioActivity.POSITION to index,
                            AudioActivity.SUPPORT_KEY to ((activity as? BookDetailActivity)?.bookKey
                                ?: ""),
                            AudioActivity.SECTION to ((activity as? BookDetailActivity)?.bookDetail?.gradeName
                                ?: ""),
                            AudioActivity.TYPE to type,
                            AudioActivity.NAME to name,
                            AudioActivity.LYC_URL to it.lrcurl
                        )
                    }
                    "8" -> {
                        startActivity<WebViewActivity>(
                            WebViewActivity.URL to it.link,
                            WebViewActivity.TITLE to it.name
                        )
                    }
                    "13" -> {
                        val uri = Uri.parse("${WEB_BASE_URL}scan/phoneticVoice?key=${it.id}")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }
                    "16" -> {
                        startActivity<PostActivity>(
                            PostActivity.URL to "${WEB_BASE_URL}scan/singleListen?key=${it.id}"
                        )
                    }
                }
            }
        }

    }

    /**
     * ????????????????????????
     */
    private fun netCheck(content: String?, action: () -> Unit) {
        if (NetWorkUtils.isNetWorkReachable()) {
            if (NetWorkUtils.isWifiConnected()) {
                action()
            } else {
                AlertDialog.Builder(this.requireContext())
                    .setMessage("??????????????????wifi??????????????????" + if (content != null) content + "M" else "" + "????????????")
                    .setPositiveButton("??????") { _, _ -> action() }
                    .setNegativeButton("??????") { dialog, _ -> dialog.dismiss() }.show()
            }
        } else {
            mToast("???????????????")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            list: ArrayList<MyBookResource>,
            count: String,
            type: String,
            name: String,
            size: String,
            isOwn: String,
            enable: Boolean = false
        ) =
            BookResourceFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(DATA, list)
                    putSerializable(COUNT, count)
                    putString(TYPE, type)
                    putString(NAME, name)
                    putSerializable(SIZE, size)
                    putSerializable(IS_OWN, isOwn)
                    putSerializable(ENABLE, enable)
                }
            }
    }

    override fun initView(): View? = with(this.requireContext()) {
        rv_resource_list = recyclerView {
            backgroundColor = Color.parseColor("#ffffff")
            layoutManager = LinearLayoutManager(this.context)
            lparams(matchParent, matchParent)
        }
        rv_resource_list
    }

    override fun configView(view: View?) {
    }

    var dataList by Delegates.notNull<ArrayList<MyBookResource>>()
    private val resourceList by lazy { ArrayList<StudyResource>() }
    var count by Delegates.notNull<String>()
    private val type by lazy { arguments?.getString(TYPE) ?: "" }
    private val name by lazy { arguments?.getString(NAME) ?: "" }
    var size by Delegates.notNull<String>()
    var isOwn = ""
    var enable = false // ???????????????????????????
    override fun initData() {
        initDownloader()

        arguments?.let {
            dataList = it.getSerializable(DATA) as ArrayList<MyBookResource>
            count = it.getString(COUNT) ?: ""
            size = it.getString(SIZE) ?: ""
            isOwn = it.getString(IS_OWN) ?: ""
            enable = it.getBoolean(ENABLE)
            initResourceList()
        }

        configDownloadId()
        resumeDownload()
    }

    fun setData(data: ArrayList<MyBookResource>) {
        parseCatalog(data, true)
    }

    /**
     * ??????????????????????????? ?????????????????????????????? ???????????? -- resourceList
     */
    private fun initResourceList() {
        resourceList.clear()
        parseCatalog(dataList, true)
        oAdapter.setData(resourceList)
        oAdapter.nodeMap = resNodeMap
        oAdapter.isOwn = isOwn
        rv_resource_list.adapter = oAdapter
    }

    /**
     * ??????????????????????????????????????????
     */
    fun refreshState() {
        oAdapter.isOwn = "1"
        oAdapter.notifyDataSetChanged()
    }

    /**
     * ??????????????????????????????????????????????????????
     * ?????????????????????????????????????????????
     */
    private var resNodeMap = mutableMapOf<String, Boolean>()

    /** ?????????????????????????????????**/
    private fun parseCatalog(
        catalogList: List<MyBookResource>,
        isFirst: Boolean,
        nodeName: String = ""
    ) {
        catalogList.forEach {
            val nodeName0 = if (isFirst) it.catalogName ?: "" else nodeName
            if (isFirst && nodeName0 != "XUNIMULU_AAAAA_BBBBB") {
                resourceList.add(StudyResource(StudyResource.TYPE_NODE, nodeName0, ""))
                resNodeMap[nodeName0] = resNodeMap.isEmpty()
            }
            if (it.resourceList!!.isNotEmpty()) {
                addResourceItem(it.resourceList!!, nodeName0)
            } else if (it.catalogList!!.isNotEmpty()) {
                parseCatalog(it.catalogList!!, false, nodeName0)
            }
        }
    }

    /**
     * ????????????????????????????????????????????????
     * ?????????????????????????????????????????? resourceList
     */
    private fun addResourceItem(itemList: List<ResourceItem>, nodeName: String) {
        itemList.forEach {
            var sr = StudyResource(StudyResource.TYPE_DATA, it.name ?: "unkonw", it.downUrl ?: "")
            sr.resource = it
            sr.nodeName = nodeName
            resourceList.add(sr)
        }
    }

    /**
     * ?????????????????????????????????
     */
    private fun configDownloadId() {
        resourceList.forEach {
            try {
                if (File(
                        DownloadManager.getFilePath(
                            type, name, it.name, it.resource?.downUrl
                                ?: ""
                        )
                    ).exists()
                ) {
                    //????????????????????? ??????????????????????????????
                    it.downloadState = StudyResource.STATE_DONE
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun initDownloader() {
        FileDownloader.setup(this.requireContext())
        FileDownloader.setGlobalPost2UIInterval(50)
    }

    /**
     * ??????????????????????????????????????????
     */
    private fun resumeDownload() {
        DownloadManager.taskList.forEach {
            var task = it
            var key = task.tag as String?
            resourceList.forEach {
                if (key == it.resource?.id/* && it.getResType().isNotEmpty()*/) {
                    try {
                        task.listener = listener
                        if (task.soFarBytes != task.totalBytes) {
                            var progress =
                                if (task.soFarBytes > 10000) (task.soFarBytes / 100) / (task.totalBytes / 10000) else task.soFarBytes * 100 / task.totalBytes
                            it.progress = progress
                        }
                        it.downloadState = DownloadManager.getDownloadState(
                            task.id, type, name, it.name, it.resource?.downUrl
                                ?: ""
                        )
                    } catch (e: Exception) {
                    }
                    return@forEach
                }
            }
        }
    }

    /**
     * ??????????????????
     */
    private fun refreshProgress(key: String, progress: Int) {
        resourceList.forEach {
            if (it.resource?.id == key) {
                it.progress = progress
                oAdapter.notifyDataSetChanged()
                return@forEach
            }
        }
    }

    /**
     * ??????????????????
     */
    private fun refreshDownloadState(key: String, id: Int, state: Int) {
        resourceList.forEach {
            if (it.resource?.id == key) {
                it.downloadState = state
                it.downloadId = id
                oAdapter.notifyDataSetChanged()
                return@forEach
            }
        }
    }
}