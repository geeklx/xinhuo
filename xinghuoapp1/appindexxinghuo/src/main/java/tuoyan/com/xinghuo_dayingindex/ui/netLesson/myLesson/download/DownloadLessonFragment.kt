package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.download

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean
import tuoyan.com.xinghuo_dayingindex.bean.ResourceInfo
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean
import tuoyan.com.xinghuo_dayingindex.realm.RealmUtil
import tuoyan.com.xinghuo_dayingindex.realm.bean.Resource
import tuoyan.com.xinghuo_dayingindex.realm.delete
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.adapter.DownloadLessonAdapter
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.NetLessonsPresenter
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import java.io.File
import kotlin.properties.Delegates

class DownloadLessonFragment : LifeV4Fragment<NetLessonsPresenter>() {
    override val presenter: NetLessonsPresenter
        get() = NetLessonsPresenter(this)
    override val layoutResId: Int
        get() = 0

    var rv by Delegates.notNull<RecyclerView>()
    val lessonInfo by lazy { (activity as DownloadActivity).lessonInfo }
    val dataList by lazy { arguments?.getSerializable(DATA_LIST) as ArrayList<ResourceListBean> }

    private var listener = object : FileDownloadListener() {
        override fun warn(task: BaseDownloadTask?) {
            toast("已经在下载列表...")
        }

        override fun completed(task: BaseDownloadTask?) {
            try {
                task?.let {
                    val key = it.tag as String
                    saveDownload(key)
                    val thisTask = DownloadManager.getRunTask(key)
                    DownloadManager.taskList.remove(thisTask)
                    refreshState(-1, key, DownloadBean.STATE_DONE)
                }
                toast("下载成功")
            } catch (e: Exception) {
            }
        }

        override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            toast("正在下载...")
        }

        override fun error(task: BaseDownloadTask?, e: Throwable?) {
            try {
                task?.let {
                    var key = it.tag as String?
                    refreshState(-1, key ?: "", DownloadBean.STATE_DEFAULT)
                }
                Log.e("下载出错", "")
//                AlertDialog.Builder(ctx).setMessage("下载出错，" + e?.message).setPositiveButton("确定") { dialogInterface, _ ->
//                    dialogInterface.dismiss()
//                }.create().show()
            } catch (e: Exception) {
            }
        }

        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            task?.let {
                try {
                    var key = it.tag as String?

                    var progress = if (soFarBytes > 10000) (soFarBytes / 100) / (totalBytes / 10000) else soFarBytes * 100 / totalBytes
                    refreshProgress(
                        key
                            ?: "", progress, FileUtils.formatBytes(soFarBytes.toLong()) + "/" + FileUtils.formatBytes(totalBytes.toLong())
                    )
                } catch (e: Exception) {
                }
            }
        }

        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
//            toast("暂停...")
        }

    }

//    var HTListener = DownLoadManager.DownLoadObserver { info ->
//        Log.e("playBackDownload", "onDownLoadInfoChange")
//        info?.let {
//            when (it.state) {
//                PlaybackDownloader.Status.STATE_DOWNLOADING -> {
//                    refreshProgress(
//                        info.id, (it.finishSize * 1.0f / it.totalSize * 1.0f * 100f).toInt(),
//                        DimensionUtils.formatBytes(info.finishSize) + "/" + DimensionUtils.formatBytes(info.totalSize), false
//                    )
//                }
//                PlaybackDownloader.Status.STATE_DOWNLOADED -> {
//                    try {
//                        saveDownload(it.id, false)
//                        refreshState(-1, it.id, DownloadBean.STATE_DONE, false)
//                    } catch (e: Exception) {
//                    }
//                }
//                PlaybackDownloader.Status.STATE_DOWNLOADFAILED -> {
//                    try {
//                        refreshState(-1, it.id, DownloadBean.STATE_DEFAULT, false)
//                        AlertDialog.Builder(ctx).setMessage("下载出错，请检查资源或网络连接设置").setPositiveButton("确定") { dialogInterface, _ ->
//                            dialogInterface.dismiss()
//                        }.create().show()
//                    } catch (e: Exception) {
//                    }
//                }
//            }
//
//        }
//    }

    var mAdapter = DownloadLessonAdapter({ position, res ->
        PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE) {
            if (res.state == DownloadBean.STATE_DONE) {
                AlertDialog.Builder(ctx).setMessage("确定要删除该资源吗？").setPositiveButton("确定") { _, _ ->
                    val realm = RealmUtil.instant()
                    realm.delete(Resource::class.java, "name", res.name)
                    var p = if (res.liveType == "0" || res.liveType == "4") {
                        DownloadManager.getFilePathWithKey(res.id, res.type)
                    } else {
                        DownloadManager.getHTFilePath(res.liveKey ?: "")
                    }
                    if (p.isNotEmpty()) {
                        FileUtils.deleteFile(p)
                    } else {
                        toast("文件删除失败：文件路径异常")
                    }

                    refreshState(position, "", DownloadBean.STATE_DEFAULT)
                }.setNegativeButton("取消") { _, _ ->
                }.create().show()
            } else if (res.liveType == "0" || res.liveType == "4") {
                getResInfo(res.id, "1.2") {
                    res.playUrl = it.url
                    res.downUrl = it.downloadUrl
                    var p = DownloadManager.getFilePathWithKey(res.id, res.type)
                    var preferences = PreferenceManager.getDefaultSharedPreferences(activity)
                    preferences.edit().putString(p, Gson().toJson(it)).commit()
                    lessonDownload(position, res)
                }
            } else if (res.liveType == "1" && res.liveState == "4" && "1" == res.liveSource) {
//                playBackDownload(position, res)
            } else if (res.liveType == "1" && res.liveState == "4" && "2" == res.liveSource) {
                //getResourceInfo http://ccr.csslcloud.net/userId(用户id)/roomId(房间id)/recordId(回放id).ccr
                // http://ccr.csslcloud.net/26E9AA272AF852E6/00E94C6E1B22CE3A9C33DC5901307461/4F8C5AC3122912B1.ccr
                presenter.getResourceInfo(res.id, "1.4") {
                    res.downUrl = it.downloadUrl
                    res.state = DownloadBean.STATE_DEFAULT
                    lessonDownload(position, res)
                }
            }
        }
    }) { position, res ->
        if (res.liveType == "0" || res.liveType == "4") {
            var task = DownloadManager.getRunTask(res.id)
            task?.let {
                it.pause()
                refreshState(position, "", DownloadBean.STATE_DEFAULT)
                FileUtils.deleteFile(it.path)
                DownloadManager.taskList.remove(it)
            }
        } else if (res.liveType == "1") {
//            PlaybackDownloader.getInstance().deleteDownload(res.liveKey)
            refreshState(position, "", DownloadBean.STATE_DEFAULT)
            FileUtils.deleteDir("${DownloadManager.downloadPath}/${res.liveKey}")
        }
    }

    companion object {
        val DATA_LIST = "data_list"
        fun newInstance(dataList: ArrayList<ResourceListBean>): DownloadLessonFragment {
            var f = DownloadLessonFragment()
            var args = Bundle()
            args.putSerializable(DATA_LIST, dataList)
            f.arguments = args
            return f
        }
    }

    override fun initView(): View? {
        rv = ctx.recyclerView {
            layoutManager = LinearLayoutManager(ctx)
        }
        return rv
    }

    override fun initData() {
        initDownloader()
        initHTDownload()
        mAdapter.setData(dataList)
        rv.adapter = mAdapter
    }

    private fun getResInfo(key: String, source: String, onNext: (ResourceInfo) -> Unit) {
        presenter.getResourceInfo(key, source, "1") {
            onNext(it)
        }
    }

    private fun initDownloader() {//TODO 初始化FileDownloader相关的下载
        FileDownloader.setup(ctx)
        FileDownloader.setGlobalPost2UIInterval(150)
        configDownloadState()
        reShowDownload()
    }

    private fun initHTDownload() {//TODO 初始化 欢拓 相关的下载
        configDownloadState()
//        setHTDownload()
    }

    /**
     * 更新下载状态
     * flag 普通网课：true； 直播课：false
     */
    private fun refreshState(position: Int, key: String, state: Int, flag: Boolean = true) {
        var pos = -1
        if (position == -1) {
            kotlin.run breaking@{
                dataList.forEachIndexed { index, it ->
                    if (flag && it.id == key) {
                        it.state = state
                        pos = index
                        return@breaking
                    } else if (!flag && it.liveKey == key) {
                        it.state = state
                        pos = index
                        return@breaking
                    }
                }
            }
        } else {
            pos = position
            dataList[position].state = state
        }
        if (pos > -1) {
            mAdapter.notifyItemChanged(pos)
        }
    }

    /**
     * 更新下载进度
     * flag 普通网课：true； 直播课：false
     */
    private fun refreshProgress(key: String, progress: Int, info: String, flag: Boolean = true) {
        var pos = -1
        kotlin.run breaking@{
            dataList.forEachIndexed { index, it ->
                if (flag && it.id == key) {
                    it.progress = progress
                    it.downloadInfo = info
                    pos = index
                    return@breaking
                } else if (!flag && it.liveKey == key) {
                    it.progress = progress
                    it.downloadInfo = info
                    pos = index
                    return@breaking
                }
            }
        }
        if (pos > -1) {
            mAdapter.notifyItemChanged(pos)
        }
    }

    /**
     * 存储下载信息到realm
     * flag 普通网课：true； 直播课：false
     */
    private fun saveDownload(key: String, flag: Boolean = true) {
        dataList.forEach {
            if ((flag && it.id == key) || (!flag && it.liveKey == key)) {
                DownloadManager.saveDownloadInfo(lessonInfo.title ?: "NO TITLE", lessonInfo.netcoursekey ?: "", "网课", it)
                return@forEach
            }
        }
    }

    /**
     * 查找 并配置已完成下载状态
     */
    private fun configDownloadState() {
        var resList = DownloadManager.getDownloadedListByGroupName(
            lessonInfo.title
                ?: "NO TITLE", lessonInfo.netcoursekey ?: ""
        )
        resList.forEach {
            var res = it
            dataList.forEach {
                if (it.liveSource == "1" && it.liveType == "1" && it.liveKey == res.liveKey) {
                    val p = DownloadManager.getHTFilePath(it.liveKey ?: "")
                    if (p.isNotEmpty() && File(p).exists()) {
                        //本地存在文件的 标记下载完成状态
                        it.state = DownloadBean.STATE_DONE
                    }
                } else if (it.id == res.key || (it.name == res.name && it.type == res.type)) {
                    val p = DownloadManager.getFilePathWithKey(it)
                    if (p.isNotEmpty() && File(p).exists()) {
                        //本地存在文件的 标记下载完成状态
                        it.state = DownloadBean.STATE_DONE
                    }
                }
            }
        }
    }

    /**
     * 当前页存在正在下载的任务时
     * 恢复显示
     */
    private fun reShowDownload() {
        try {
            DownloadManager.taskList.forEach {
                var task = it
                var key = task.tag as String?
                dataList.forEach {
                    if (key == it.id) {
                        task.listener = listener
                        it.state = DownloadManager.getDownloadState(task.id, it)
                        var progress = if (task.soFarBytes > 10000) (task.soFarBytes / 100) / (task.totalBytes / 10000) else task.soFarBytes * 100 / task.totalBytes
                        it.progress = progress
                        return@forEach
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    /**
     * 普通录播网课下载，CC下载归到普通网课下
     */
    private fun lessonDownload(position: Int, res: ResourceListBean) {
        when (res.state) {
            DownloadBean.STATE_DEFAULT -> {
                val p = DownloadManager.getFilePathWithKey(res)
                if (p.isNotEmpty()) {
                    var task = DownloadManager.getRunTask(res.id)
                    if (task == null) {
                        task = FileDownloader.getImpl().create(res.downUrl).setPath(p).setListener(listener)
                        task.tag = res.id //TODO 为下载task，设置tag，为对应资源的id，用于标识资源，及下载目录
                        DownloadManager.taskList.add(task)
                    } else {
                        task.pause()
                        task.reuse()
                    }
                    task?.start()
                    refreshState(position, "", DownloadBean.STATE_DOWNLOADING)
                } else {
                    toast("数据异常，下载失败")
                }

            }
            DownloadBean.STATE_DOWNLOADING -> {
                val task = DownloadManager.getRunTask(res.id)
                task?.let {
                    it.pause()
                    refreshState(position, "", DownloadBean.STATE_PAUSED)
                }
            }
            DownloadBean.STATE_PAUSED -> {
                val task = DownloadManager.getRunTask(res.id)
                task?.let {
                    it.reuse()
                    it.start()
                    refreshState(position, "", DownloadBean.STATE_DOWNLOADING)
                }
            }
        }
    }

    /**
     * 直播回放下载
     * 调用欢拓
     */
//    private var mPreClickTime: Long = 0

//    private fun playBackDownload(position: Int, res: ResourceListBean) {
//        when (res.state) {
//            DownloadBean.STATE_DEFAULT -> {
//                checkLiveToken(position, res) {
//                    //判断下载列表是否已经存在该ID
//                    if (PlaybackDownloader.getInstance().containsID(res.liveKey) && FileUtils.isFileExists("${DownloadManager.downloadPath}/${res.liveKey}")) {
//                        if (System.currentTimeMillis() - mPreClickTime > 2000) {
//                            toast("已在下载队列")
//                            mPreClickTime = System.currentTimeMillis()
//                        }
//                        return@checkLiveToken
//                    }
//                    PlaybackDownloader.getInstance().appendDownloadTask(res.liveToken, res.liveKey, null, null, object : PreDownLoad.OnappendDownloadListener {
//                        override fun success() {
//                            PlaybackDownloader.getInstance().startDownload(res.liveKey)
//                            refreshState(position, "", DownloadBean.STATE_DOWNLOADING)
//                            PlaybackDownloader.getInstance().addDownLoadObserver(res.liveKey, HTListener)
////                            setHTDownload()
//                            toast("正在下载")
//                        }
//
//                        override fun fail(code: Int, msg: String) {
//                            if (code == PlaybackDownloader.Callback_Code.OutMemory_Code) {
//                                toast("内存不足")
//                            }
//                        }
//                    })
//                }
//            }
//            DownloadBean.STATE_DOWNLOADING -> {
//                PlaybackDownloader.getInstance().pauseDownload(res.liveKey)
//                refreshState(position, "", DownloadBean.STATE_PAUSED)
//            }
//            DownloadBean.STATE_PAUSED -> {
//                PlaybackDownloader.getInstance().startDownload(res.liveKey)
//                refreshState(position, "", DownloadBean.STATE_DOWNLOADING)
//            }
//        }
//    }

    private fun checkLiveToken(position: Int, res: ResourceListBean, onNext: () -> Unit) {
        if (dataList.size > 0 && position > -1) {
            if (res.liveToken.isNullOrEmpty()) {
                presenter.getMTCloudUrl(res.id ?: "") {
                    res.liveToken = it.liveToken
                    dataList[position].liveToken = it.liveToken
                    onNext()
                }
            } else {
                onNext()
            }
        }
    }

    /**
     * 显示欢拓直播回放下载信息
     */
//    private fun setHTDownload() {
//        var downloadList = PlaybackDownloader.getInstance().downloadList
//        if (downloadList.isNotEmpty()) {
//            downloadList.forEach {
//                for (i in 0 until dataList.size) {
//                    var item = dataList[i]
//                    if (item.liveKey == it.id && it.state != PlaybackDownloader.Status.STATE_DOWNLOADED) {
//                        if (it.state == PlaybackDownloader.Status.STATE_PAUSEDOWNLOAD) {
//                            refreshState(i, "", DownloadBean.STATE_PAUSED)
//                        } else {
//                            refreshState(i, "", DownloadBean.STATE_DOWNLOADING)
//                        }
//                        refreshProgress(
//                            it.id, (it.finishSize * 1.0f / it.totalSize * 1.0f * 100f).toInt(),
//                            DimensionUtils.formatBytes(it.finishSize) + "/" + DimensionUtils.formatBytes(it.totalSize), false
//                        )
//
//                        if (item.liveToken.isNullOrEmpty()) {
//                            presenter.getMTCloudUrl(item.id ?: "") {
//                                item.liveToken = it.liveToken
//                                PlaybackDownloader.getInstance().addDownLoadObserver(item.liveKey, HTListener)
//                            }
//                        } else {
//                            PlaybackDownloader.getInstance().addDownLoadObserver(item.liveKey, HTListener)
//                        }
//                    }
//                }
//            }
//        }
//    }
}