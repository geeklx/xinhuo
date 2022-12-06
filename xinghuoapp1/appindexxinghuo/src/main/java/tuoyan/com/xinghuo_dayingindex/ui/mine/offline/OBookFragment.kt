package tuoyan.com.xinghuo_dayingindex.ui.mine.offline
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.app.AlertDialog
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.base.BaseV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean
import tuoyan.com.xinghuo_dayingindex.bean.VideoParam
import tuoyan.com.xinghuo_dayingindex.realm.RealmUtil
import tuoyan.com.xinghuo_dayingindex.realm.bean.Resource
import tuoyan.com.xinghuo_dayingindex.realm.delete
import tuoyan.com.xinghuo_dayingindex.ui._public.PDFActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import java.io.File
import kotlin.properties.Delegates

class OBookFragment : BaseV4Fragment() {
    override val layoutResId: Int
        get() = 0

    private var rv by Delegates.notNull<RecyclerView>()
    var mAdapter = OfflineResAdapter({
        jumpRes(it)
    }) {
        AlertDialog.Builder(ctx).setMessage("确定要删除该资源吗？").setPositiveButton("确定") { _, _ ->
            val realm = RealmUtil.instant()
            realm.delete(Resource::class.java, "name", it.name)
            deleteItem(it)
            FileUtils.deleteFile(it.path)
        }.setNegativeButton("取消") { _, _ ->
        }.create().show()
    }

    override fun initView(): View? {
        rv = ctx.recyclerView {
            lparams(matchParent, matchParent)
            layoutManager = LinearLayoutManager(ctx)
        }
        return rv
    }

    override fun initData() {
        val resList = DownloadManager.getDownloadedResAll()
        val dataList = ArrayList<DownloadBean>() //TODO RealmList 不能当传值用
        resList.forEach {
            val res = DownloadBean()
            res.name = it.name
            res.type = it.type
            res.path = it.path
            res.duration = it.duration
            res.lrcUrls = it.lrcUrls
            res.id = it.key
            dataList.add(res)
        }
        mAdapter.setData(dataList)
        rv.adapter = mAdapter
    }

    fun deleteItem(item: DownloadBean) {
        mAdapter.remove(item)
    }

    /**
     * 不同类型资源跳转
     * type: String, path: String, name: String, lrcUrls: String
     * it.type, it.path, it.name, it.lrcUrls
     */
    private fun jumpRes(bean: DownloadBean) {
        val file = File(bean.path)
        if (file.exists()) {
            when (bean.type) {
                DownloadBean.TYPE_VIDEO -> {
                    val videoParam = VideoParam()
                    videoParam.path = bean.path
                    videoParam.name = bean.name
                    videoParam.offline = true
                    startActivity<VideoActivity>(VideoActivity.VIDEO_PARAM to videoParam)
                }
                DownloadBean.TYPE_PDF -> {
                    startActivity<PDFActivity>(PDFActivity.URL to bean.path, PDFActivity.TITLE to bean.name)
                }
                DownloadBean.TYPE_AUDIO -> {
                    val resList = ArrayList<BookRes>()
                    val bookRes = BookRes()
                    bookRes.name = bean.name
                    bookRes.playUrl = bean.path
                    bookRes.downUrl = bean.path
                    bookRes.id = bean.id
                    bookRes.type = bean.type
                    val lrcList = bean.lrcUrls.split(",")
                    if (lrcList.size >= 3) {
                        bookRes.lrcurl = if (lrcList[0] == "none") "" else lrcList[0]
                        bookRes.lrcurl2 = if (lrcList[1] == "none") "" else lrcList[1]
                        bookRes.lrcurl3 = if (lrcList[2] == "none") "" else lrcList[2]
                    }
                    resList.add(bookRes)
//                    MyApp.instance.bookres = resList
                    // 存
                    val list1: List<BookRes> = ArrayList()
                    val gson1 = Gson()
                    val data1 = gson1.toJson(resList)
                    SPUtils.getInstance().put("BookRes", data1)
                    startActivity<AudioActivity>()
                }
            }
        } else {
            AlertDialog.Builder(ctx).setMessage("未找到文件，请重新下载").setPositiveButton("确定") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.create().show()
        }
    }
}