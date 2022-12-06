package tuoyan.com.xinghuo_dayingindex.ui.mine.offline

import android.app.AlertDialog
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_offline_res.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseActivity
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.realm.RealmUtil
import tuoyan.com.xinghuo_dayingindex.realm.bean.Resource
import tuoyan.com.xinghuo_dayingindex.realm.delete
import tuoyan.com.xinghuo_dayingindex.ui._public.PDFActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.LocalReplayCCActivity
import tuoyan.com.xinghuo_dayingindex.ui.ht.ReplayHTActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioLessonActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import java.io.File

class OfflineResActivity : BaseActivity() {
    override val layoutResId: Int
        get() = R.layout.activity_offline_res

    val dataList by lazy { intent.getSerializableExtra(DATA) as ArrayList<DownloadBean> }
    val name by lazy { intent.getStringExtra(NAME) }

    var mAdapter = OfflineResAdapter({ downloadBean ->
        val netLesson = NetLesson()
        netLesson.periodId = downloadBean.id
        netLesson.name = downloadBean.name
        val lessonDetail = LessonDetail()
        lessonDetail.netcoursekey = downloadBean.parentKey
        lessonDetail.title = downloadBean.parentName
        lessonDetail.form = downloadBean.form

        val data = SensorsData()
        data.course_id = lessonDetail.netcoursekey ?: ""
        data.course_name = lessonDetail.title ?: ""
        data.is_live = "1" == lessonDetail.form ?: ""
        data.period_id = netLesson.periodId
        data.period_type = "回放小节"
        data.period_name = netLesson.name
        data.video_service_provider = if ("1" == downloadBean.liveSource) "欢拓" else "cc"
        if ("2" == downloadBean.liveSource && downloadBean.liveType == "1") {//CC
            val intent = Intent(this@OfflineResActivity, LocalReplayCCActivity::class.java)
            intent.putExtra("filePath", downloadBean.path)
            intent.putExtra("title", downloadBean.name)
            intent.putExtra("SensorsData", data)
            startActivity(intent)
        } else if (downloadBean.liveType == "1") {//欢拓
            startActivity<ReplayHTActivity>(
                "token" to downloadBean.liveToken,
                "id" to downloadBean.liveKey,
                "Lesson" to Gson().toJson(netLesson),
                "title" to downloadBean.name,
                "activityLesson" to Gson().toJson(lessonDetail)
            )
        } else if (downloadBean.form == "4") {
            //音频课,在这里只允许单曲播放
            val resList = ArrayList<ResourceListBean>()
            val resBean = ResourceListBean()
            resBean.playUrl = downloadBean.path
            resBean.name = downloadBean.name
            resList.add(resBean)
//            MyApp.instance.resList = resList
            val list1: List<ResourceListBean> = ArrayList()
            val gson1 = Gson()
            val data1 = gson1.toJson(resList)
            SPUtils.getInstance().put("ResourceListBean", data1)

            val intent = Intent(this@OfflineResActivity, AudioLessonActivity::class.java)
            intent.putExtra("POSITION", 0)
            intent.putExtra("TITLE", name)
            intent.putExtra("ClASSKEY", "")
            intent.putExtra("FREETYPE", 3)//离线播放
            startActivity(intent)
        } else {
            jumpRes(downloadBean, netLesson, lessonDetail)
        }
    }) { downloadBean ->
        AlertDialog.Builder(this).setMessage("确定要删除该资源吗？").setPositiveButton("确定") { _, _ ->
            val realm = RealmUtil.instant()
            realm.delete(Resource::class.java, "name", downloadBean.name)
            deleteItem(downloadBean)
            if (downloadBean.liveType == "0") {
                FileUtils.deleteFile(downloadBean.path)
            } else if (downloadBean.liveType == "1" && downloadBean.liveSource == "2") {
                FileUtils.deleteDir(downloadBean.path)
            } else if (downloadBean.liveType == "1") {
                FileUtils.deleteDir("${DownloadManager.downloadPath}/${downloadBean.liveKey}")
//                PlaybackDownloader.getInstance().deleteDownload(downloadBean.liveKey)
            }
        }.setNegativeButton("取消") { _, _ ->

        }.create().show()
    }


    // 4600 1352
    companion object {
        val DATA = "data"
        val NAME = "name"
    }

    override fun configView() {
        setSupportActionBar(toolbar)
        tv_title.text = name
        recycler_view.layoutManager = LinearLayoutManager(ctx)
    }

    override fun initData() {
        mAdapter.setData(dataList)
        recycler_view.adapter = mAdapter
    }

    fun deleteItem(item: DownloadBean) {
        mAdapter.remove(item)
        if (mAdapter.getDateCount() == 0) {
            //TODO 当前网课下全部资源都被删除时，通知上一层，把空目录删除
            setResult(100)
            onBackPressed()
        }
    }

    /**
     * 不同类型资源跳转
     */
    private fun jumpRes(
        downloadBean: DownloadBean, netLesson: NetLesson, lessonDetail: LessonDetail
    ) {
        val file = File(downloadBean.path)
        if (file.exists()) {
            when (downloadBean.type) {
                DownloadBean.TYPE_VIDEO -> {
                    val videoParam = VideoParam()
                    videoParam.path = downloadBean.path
                    videoParam.name = downloadBean.name
                    videoParam.offline = true
                    videoParam.courseKey = downloadBean.parentKey
                    videoParam.key = downloadBean.id
                    startActivity<VideoActivity>(
                        VideoActivity.VIDEO_PARAM to videoParam,
                        VideoActivity.NET_LESSON to netLesson,
                        VideoActivity.ACTIVITY_LESSON to lessonDetail
                    )
                }
                DownloadBean.TYPE_PDF -> {
                    startActivity<PDFActivity>(
                        PDFActivity.URL to downloadBean.path, PDFActivity.TITLE to downloadBean.name
                    )
                }
                DownloadBean.TYPE_AUDIO -> {
                    val resList = ArrayList<BookRes>()
                    val bookRes = BookRes()
                    bookRes.name = downloadBean.name
                    bookRes.playUrl = downloadBean.path
                    bookRes.downUrl = downloadBean.path
                    val lrcList = downloadBean.lrcUrls.split(",")
                    if (lrcList.size >= 3) {
                        bookRes.lrcurl = if (lrcList[0] == "none") "" else lrcList[0]
                        bookRes.lrcurl2 = if (lrcList[1] == "none") "" else lrcList[1]
                        bookRes.lrcurl3 = if (lrcList[2] == "none") "" else lrcList[2]
                    }
                    resList.add(bookRes)
//                    MyApp.instance.bookres = resList
                    // 存
                    val list1: List<ResourceListBean> = ArrayList()
                    val gson1 = Gson()
                    val data1 = gson1.toJson(resList)
                    SPUtils.getInstance().put("BookRes", data1)
                    startActivity<AudioActivity>()
                }
            }
        } else {
            AlertDialog.Builder(this).setMessage("未找到文件，请重新下载")
                .setPositiveButton("确定") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }.create().show()
        }
    }
}
