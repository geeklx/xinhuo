package tuoyan.com.xinghuo_dayingindex.ui.books.detail
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.Manifest
import android.app.AlertDialog
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import org.jetbrains.anko.ctx
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DEFAULT
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DONE
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DOWNLOADING
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_PAUSED
import tuoyan.com.xinghuo_dayingindex.ui.books.adapter.BookResAdapter
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import java.io.File
import kotlin.properties.Delegates

/**
 * Created by  on 2018/9/15.
 * 图书详情页 底部字幕听力& 名师视频
 */
class BookResFragment : LifeV4Fragment<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = 0
    private val context by lazy { activity as BookDetailActivity }
    val bookInfo by lazy {
        (activity as BookDetailActivity).getBookInfo()
    }
    var rv by Delegates.notNull<RecyclerView>()

    val dataList by lazy { ArrayList<BookRes>() }

    companion object {
        val DATA_LIST = "data_list"

//        @Deprecated("数据量太多  崩溃")
//        fun newInstance(dataList: ArrayList<BookRes>): BookResFragment {
//            var f = BookResFragment()
//            var args = Bundle()
//            args.putSerializable(DATA_LIST, dataList)
//            f.arguments = args
//            return f
//        }

        fun newInstance(): BookResFragment {
            return BookResFragment()
        }
    }

    /**
     * 已拥有的图书显示下载按钮
     */
    fun ownFlg(flag: Boolean) {
        mAdapter.isOwn = flag
        if (onLoad) bookInfo.isown = if (flag) "1" else "0"
        mAdapter.notifyDataSetChanged()
    }

    override fun initView(): View? = with(ctx) {
        rv = recyclerView {
            lparams(matchParent, matchParent)
            layoutManager = LinearLayoutManager(ctx)
            adapter = mAdapter
        }
        return rv
    }

    private var mAdapter = BookResAdapter({ item, position ->
        context.saOption("点击资源", item.type)
        isLogin {
            PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE) {
                val sensorBook = SensorBook()
                sensorBook.book_matching_id = context.bookDetail.supportingKey
                sensorBook.book_matching_name = context.bookDetail.titile
                sensorBook.section = context.bookDetail.gradeName
                val filePath = DownloadManager.getFilePathWithKey(item.id, item.type)
                if (item.type == "3") {
                    val videoParam = VideoParam()
                    videoParam.key = item.id
                    videoParam.name = item.name
                    videoParam.type = "2"
                    startActivity<VideoActivity>(
                        VideoActivity.VIDEO_PARAM to videoParam,
                        VideoActivity.SENSORSBOOK to sensorBook
                    )
                } else if (item.type == "7") {
                    if (filePath.isNotEmpty() && File(filePath).exists()) {
                        gotoAudio(position)
                    } else {
                        netCheck(null) {
                            gotoAudio(position)
                        }
                    }
                }

            }
        }
    }) { position, res ->
        context.saOption("点击下载资源", res.type)
        isLogin {
            PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE) {
                when (res.state) {
                    STATE_DEFAULT -> {
                        getResInfo(res.id, "2", "1") {
                            res.playUrl = it.url
                            res.downUrl = it.downloadUrl
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
                                if (res.lrcurl.isNotEmpty()) FileDownloader.getImpl().create(res.lrcurl).setPath(DownloadManager.getFilePath(res.lrcurl)).start()
                                if (res.lrcurl2.isNotEmpty()) FileDownloader.getImpl().create(res.lrcurl2).setPath(DownloadManager.getFilePath(res.lrcurl2)).start()
                                if (res.lrcurl3.isNotEmpty()) FileDownloader.getImpl().create(res.lrcurl3).setPath(DownloadManager.getFilePath(res.lrcurl3)).start()
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
                            refreshState(position, "", DownloadBean.STATE_DOWNLOADING)
                        }
                    }
                }

            }
        }
    }

    private fun gotoAudio(position: Int) {
        val sensorBook = SensorBook()
        sensorBook.book_matching_id = context.bookDetail.supportingKey
        sensorBook.book_matching_name = context.bookDetail.titile
        sensorBook.section = context.bookDetail.gradeName
//        MyApp.instance.bookres = mAdapter.getData()
        // 存
        val list1: List<ResourceListBean> = ArrayList()
        val gson1 = Gson()
        val data1 = gson1.toJson(mAdapter.getData())
        SPUtils.getInstance().put("BookRes", data1)
        startActivity<AudioActivity>(
            AudioActivity.POSITION to position,
            AudioActivity.CAN_DOWNLOAD to (bookInfo.isown == "1"),
            AudioActivity.SENSORBOOK to sensorBook,
            AudioActivity.SUPPORT_KEY to bookInfo.supportingKey
        )
    }

    private var listener = object : FileDownloadListener() {
        override fun warn(task: BaseDownloadTask?) {
            toast("已经在下载列表")
        }

        override fun completed(task: BaseDownloadTask?) {
            try {
                task?.let {
                    var key = it.tag as String?
                    saveDownload(key ?: "")
                    Log.e("DOWNLOAD_FLAG", "----------------saved------------")
                    var thisTask = DownloadManager.getRunTask(key ?: "")
                    DownloadManager.taskList.remove(thisTask)
                    refreshState(-1, key ?: "", STATE_DONE)
                }
                toast("下载成功")
            } catch (e: Exception) {
                Log.e("DOWNLOAD_FLAG", "--catch--" + e.message)
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
                AlertDialog.Builder(ctx).setMessage("下载出错， " + e?.message).setPositiveButton("确定") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }.create().show()
            } catch (e: Exception) {
            }

        }

        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            task?.let {
                var key = it.tag as String?
                var progress = if (soFarBytes > 10000) (soFarBytes / 100) / (totalBytes / 10000) else soFarBytes * 100 / totalBytes
                refreshProgress(key ?: "", progress)
            }
        }

        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            toast("暂停...")
        }

    }

    var onLoad = false
    override fun initData() {
        initDownloader()
    }

    fun setData(dataList: ArrayList<BookRes>) {
        this.dataList.clear()
        this.dataList.addAll(dataList)
        mAdapter.setData(this.dataList)
    }

    private fun getResInfo(key: String, source: String, isDownload: String, onNext: (ResourceInfo) -> Unit) {
        presenter.getResourceInfo(key, source, isDownload) {
            onNext(it)
        }
    }

    private fun initDownloader() {
        FileDownloader.setup(ctx)
        FileDownloader.setGlobalPost2UIInterval(150)
        configDownloadState()
        reShowDownload()
        onLoad = true
    }

    /**
     * 更新下载状态
     */
    private fun refreshState(position: Int, key: String, state: Int) {
        var pos = -1
        if (position == -1) {
            kotlin.run breaking@{
                dataList.forEachIndexed { index, it ->
                    if (it.id == key) {
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
        mAdapter.notifyItemChanged(pos)
    }

    /**
     * 更新下载进度
     */
    private fun refreshProgress(key: String, progress: Int) {
        var pos = -1
        kotlin.run breaking@{
            dataList.forEachIndexed { index, it ->
                if (it.id == key) {
                    it.progress = progress
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
     */
    private fun saveDownload(key: String) {
        dataList.forEach {
            if (it.id == key) {
                it.lrcUrls = "${if (it.lrcurl.isEmpty()) "none" else it.lrcurl},${if (it.lrcurl2.isEmpty()) "none" else it.lrcurl2},${if (it.lrcurl3.isEmpty()) "none" else it.lrcurl3}"
                DownloadManager.saveDownloadInfo(bookInfo.titile, bookInfo.supportingKey, "图书", it)
            }
        }
    }

    /**
     * 查找 并配置已完成下载状态
     */
    private fun configDownloadState() {
        dataList.forEach {
            val p = DownloadManager.getFilePathWithKey(it.id, it.type)
            if (p.isNotEmpty() && File(p).exists() && DownloadManager.isDown(it.id)) {
                //本地存在文件的 标记下载完成状态
                it.state = STATE_DONE
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

    var refreshFlag = false
    override fun onResume() {
        super.onResume()
        if (refreshFlag) {
            reShowDownload()
        }
        refreshFlag = true
    }
}