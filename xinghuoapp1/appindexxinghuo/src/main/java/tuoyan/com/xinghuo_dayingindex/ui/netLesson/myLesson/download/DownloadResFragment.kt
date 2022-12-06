package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.download

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.ClassListBean
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DEFAULT
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DONE
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DOWNLOADING
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_PAUSED
import tuoyan.com.xinghuo_dayingindex.bean.LessonRes
import tuoyan.com.xinghuo_dayingindex.bean.ResourceInfo
import tuoyan.com.xinghuo_dayingindex.realm.RealmUtil
import tuoyan.com.xinghuo_dayingindex.realm.bean.Resource
import tuoyan.com.xinghuo_dayingindex.realm.delete
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.adapter.DownloadResAdapter
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import java.io.File
import kotlin.properties.Delegates

class DownloadResFragment : LifeV4Fragment<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = 0

    var rv by Delegates.notNull<RecyclerView>()

    val lessonInfo by lazy { (activity as DownloadActivity).lessonInfo }
    val dataList by lazy { arguments?.getSerializable(DATA_LIST) as ArrayList<ClassListBean> }
    private val resourceList by lazy { ArrayList<LessonRes>() }
    private var listener = object : FileDownloadListener() {
        override fun warn(task: BaseDownloadTask?) {
            toast("warn...")
        }

        override fun completed(task: BaseDownloadTask?) {
            try {
                task?.let {
                    var key = it.tag as String?
                    saveDownload(key ?: "")
                    var thisTask = DownloadManager.getRunTask(key ?: "")
                    DownloadManager.taskList.remove(thisTask)
                    refreshState(-1, key ?: "", STATE_DONE)
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
                    refreshState(-1, key ?: "", STATE_DEFAULT)
                }
                AlertDialog.Builder(ctx).setMessage("下载出错，请检查资源或网络连接设置").setPositiveButton("确定") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }.create().show()
            } catch (e: Exception) {
            }
        }

        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            task?.let {
                try {
                    var key = it.tag as String?
                    var progress = if (soFarBytes > 10000) (soFarBytes / 100) / (totalBytes / 10000) else soFarBytes * 100 / totalBytes
                    refreshProgress(key ?: "", progress)
                } catch (e: Exception) {
                }
            }
        }

        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            toast("暂停...")
        }

    }

    var mAdapter = DownloadResAdapter { position, res ->
        PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE) {
            when (res.state) {
                STATE_DONE -> {
                    AlertDialog.Builder(ctx).setMessage("确定要删除该资源吗？").setPositiveButton("确定") { _, _ ->
                        val realm = RealmUtil.instant()
                        realm.delete(Resource::class.java, "name", res.name)
                        var p = DownloadManager.getFilePathWithKey(res.id, res.type)
                        if (p.isNotEmpty()) {
                            FileUtils.deleteFile(p)
                        } else {
                            toast("文件删除失败：文件路径异常")
                        }
                        refreshState(position, "", DownloadBean.STATE_DEFAULT)
                    }.setNegativeButton("取消") { _, _ ->

                    }.create().show()
                }
                STATE_DEFAULT -> {
                    getResInfo(res.id, "2") {
                        res.downUrl = it.downloadUrl
                        res.playUrl = it.url
                        res.lrcurl = it.lrcurl1
                        res.lrcurl2 = it.lrcurl2
                        res.lrcurl3 = it.lrcurl3

                        var p = DownloadManager.getFilePathWithKey(res.id, res.type)
                        if (p.isNotEmpty()) {
                            var task = FileDownloader.getImpl().create(res.downUrl).setPath(p).setListener(listener)
                            task.tag = res.id //TODO 为下载task，设置tag，为对应资源的id，用于标识资源，及下载目录
                            DownloadManager.taskList.add(task)
                            task.start()
                            refreshState(position, "", STATE_DOWNLOADING)
                            //TODO 同步下载不同的字幕文件
                            if (!res.lrcurl.isNullOrEmpty()) FileDownloader.getImpl().create(res.lrcurl).setPath(DownloadManager.getFilePath(res.lrcurl)).start()
                            if (!res.lrcurl2.isNullOrEmpty()) FileDownloader.getImpl().create(res.lrcurl2).setPath(DownloadManager.getFilePath(res.lrcurl2)).start()
                            if (!res.lrcurl3.isNullOrEmpty()) FileDownloader.getImpl().create(res.lrcurl3).setPath(DownloadManager.getFilePath(res.lrcurl3)).start()
                        } else {
                            toast("数据异常，下载失败")
                        }
                    }
                }
                STATE_DOWNLOADING -> {
                    var task = DownloadManager.getRunTask(res.id)
                    task?.let {
                        it.pause()
                        refreshState(position, "", STATE_PAUSED)
                    }
                }
                STATE_PAUSED -> {
                    var task = DownloadManager.getRunTask(res.id)
                    task?.let {
                        it.reuse()
                        it.start()
                        refreshState(position, "", STATE_DOWNLOADING)
                    }
                }
            }
        }

    }

    companion object {
        val DATA_LIST = "data_list"
        fun newInstance(dataList: ArrayList<ClassListBean>): DownloadResFragment {
            var f = DownloadResFragment()
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
        rv.adapter = mAdapter
        parseCatalog(dataList, true)
        initDownloader()
        mAdapter.setData(resourceList)
    }

    private fun getResInfo(key: String, source: String, onNext: (ResourceInfo) -> Unit) {
        presenter.getResourceInfo(key, source, "1") {
            onNext(it)
        }
    }

    private fun initDownloader() {
        FileDownloader.setup(ctx)
        FileDownloader.setGlobalPost2UIInterval(150)
        configDownloadState()
        reShowDownload()
    }

    /**
     * 更新下载状态
     */
    private fun refreshState(position: Int, key: String, state: Int) {
        if (position == -1) {
            resourceList.forEach {
                if (it.id == key) {
                    it.state = state
                }
            }
        } else {
            resourceList[position].state = state
        }
        mAdapter.notifyDataSetChanged()
    }

    /**
     * 更新下载进度
     */
    private fun refreshProgress(key: String, progress: Int) {
        resourceList.forEach {
            if (it.id == key) {
                it.progress = progress
            }
        }
        mAdapter.notifyDataSetChanged()
    }

    /**
     * 存储下载信息到realm
     */
    private fun saveDownload(key: String) {
        resourceList.forEach {
            if (it.id == key) {
                it.lrcUrls = "${if (it.lrcurl.isEmpty()) "none" else it.lrcurl},${if (it.lrcurl2.isEmpty()) "none" else it.lrcurl2},${if (it.lrcurl3.isEmpty()) "none" else it.lrcurl3}"
                DownloadManager.saveDownloadInfo(lessonInfo.title
                        ?: "NO TITLE", lessonInfo.netcoursekey ?: "", "网课", it)
            }
        }
    }

    /**
     * 查找 并配置已完成下载状态
     */
    private fun configDownloadState() {
        var resList = DownloadManager.getDownloadedListByGroupName(lessonInfo.title
                ?: "NO TITLE", lessonInfo.netcoursekey ?: "")
        resList.forEach {
            var res = it
            resourceList.forEach {
                if (it.id == res.key || (it.name == res.name && it.type == res.type)) {
                    var p = DownloadManager.getFilePathWithKey(it.id, it.type)
                    if (p.isNotEmpty() && File(p).exists()) {
                        //本地存在文件的 标记下载完成状态
                        it.state = STATE_DONE
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
                resourceList.forEach {
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
     * 当存在下级列表，则判定为存在下级节点
     * 遍历下级节点列表，直至最终节点
     * 改版之后本页面没有用到，在下载下页用到了resourceList 不能注释
     */
    /** 第一个节点，显示节点名**/
    private fun parseCatalog(catalogList: List<ClassListBean>, isFirst: Boolean) {
        catalogList.forEach {
            if (isFirst) { //TODO 确认为章节节点，构建一个虚拟节点，并加入
                var res = LessonRes()
                res.type = DownloadBean.TYPE_NODE
                res.name = it.catalogName ?: "unkonw"
                resourceList.add(res)
            }
            if (it.resourceList != null && it.resourceList!!.isNotEmpty()) {
                resourceList.addAll(it.resourceList!!)
            } else if (it.catalogList!!.isNotEmpty()) {
                parseCatalog(it.catalogList!!, false)
            }
        }
    }
}