package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson

import android.Manifest
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_work_pre_list.*
import org.jetbrains.anko.startActivity
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.VideoParam
import tuoyan.com.xinghuo_dayingindex.ui._public.ImageActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.PDFActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.message.NewsAndAudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.composition.CompositionDetailWebActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import java.io.File

class WorkPreListActivity : LifeActivity<NetLessonsPresenter>() {
    override val presenter: NetLessonsPresenter
        get() = NetLessonsPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_work_pre_list

    private var isRefresh = false
    private val courseKey by lazy { intent.getStringExtra(COURSE_KEY) ?: "" }
    private val videoKey by lazy { intent.getStringExtra(VIDEO_KEY) ?: "" }
    private val dataList by lazy { intent.getSerializableExtra(DATA_LIST) as ArrayList<BookRes> }
    private val workAdapter by lazy {
        WorkPreAdapter() {
            goDetail(it)
        }
    }

    override fun configView() {
        super.configView()
        rlv_work.layoutManager = LinearLayoutManager(this)
        rlv_work.adapter = workAdapter
    }

    override fun initData() {
        super.initData()
        workAdapter.setData(dataList)
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isRefresh) {
            presenter.getPrepareList(courseKey, videoKey) { dataList ->
                workAdapter.setData(dataList)
            }
        }
        isRefresh = true
    }

    companion object {
        const val COURSE_KEY = "courseKey"
        const val VIDEO_KEY = "videoKey"
        const val DATA_LIST = "DATA_LIST"
    }

    /**
     *记录网课预习接口 get ，参数courseKey网课主键，vidoKey网课小节主键，courseResourceKey预习主键
     */
    private fun getLearnedPrepare(courseResourceKey: String, success: () -> Unit) {
        presenter.getLearnedPrepare(courseKey, videoKey, courseResourceKey) {
            success()
        }
    }

    /**
     * //配套联系type:1:试卷,2:试卷解析,3:视频,4:图片,5:图文,6:文档,7:音频,8:链接,10:测评,11:资讯
     */
    private fun goDetail(item: BookRes) {
        if (item.field5 == "2" && (item.field3 == "0" || item.field3 == "2")) {
            //主观题批改中跳转到正在批改中
            startActivity<CompositionDetailWebActivity>(
                CompositionDetailWebActivity.PRACTISE_KEY to item.id,
                CompositionDetailWebActivity.EVAL_KEY to item.field1,
                CompositionDetailWebActivity.TITLE to item.name
            )
        } else {
            PermissionUtlis.checkPermissions(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) {
                when (item.type) {
                    "1" -> {
                        if ("1" != item.isFinish) {
                            val intent = Intent(this, ExerciseDetailKActivity::class.java)
                            intent.putExtra(ExerciseDetailKActivity.KEY, item.id)
                            intent.putExtra(ExerciseDetailKActivity.NAME, item.name)
                            intent.putExtra(ExerciseDetailKActivity.CAT_KEY, item.parentKey)
                            intent.putExtra(ExerciseDetailKActivity.NET_COURSE_KEY, courseKey)
                            intent.putExtra(ExerciseDetailKActivity.NET_COURSE_VIDEO_KEY, videoKey)
                            intent.putExtra(
                                ExerciseDetailKActivity.SOURCE, "5"
                            )//预习列表中的 试卷、测评，调用对应的提交接口是传source=5 课后作业source=6 其他不传
                            intent.putExtra(
                                ExerciseDetailKActivity.NET_COURSE_RESOURCE_KEY, item.field6
                            )//预习列表中的 试卷、测评，field6 为预习key netCourseResourceKey
                            intent.putExtra(ExerciseDetailKActivity.TYPE, item.type)
                            intent.putExtra(
                                ExerciseDetailKActivity.USER_PRACTISE_KEY, item.userPracticeKey
                            )
                            startActivity(intent)
                        } else {
                            presenter.getReport(item.id, item.userPracticeKey) {
                                startActivity<ReportActivity>(
                                    ReportActivity.DATA to it,
                                    ReportActivity.TIME to "",
                                    ReportActivity.KEY to item.id,
                                    ReportActivity.CAT_KEY to item.parentKey,
                                    ReportActivity.NAME to item.name,
                                    ReportActivity.TYPE to "1",
                                    ReportActivity.EVAL_STATE to "1"
                                )
                            }
                        }
                    }
                    "3" -> {
                        getLearnedPrepare(item.field6) {
                            val videoParam = VideoParam()
                            videoParam.key = item.id
                            videoParam.type = "1.3"
                            startActivity<VideoActivity>(VideoActivity.VIDEO_PARAM to videoParam)
                        }
                    }
                    "4" -> {
                        getLearnedPrepare(item.field6) {
                            startActivity<ImageActivity>(
                                ImageActivity.URL to item.link, ImageActivity.NAME to item.name
                            )
                        }
                    }
                    "5" -> {
                        getLearnedPrepare(item.field6) {
                            startActivity<WebViewActivity>(
                                WebViewActivity.URL to item.content,
                                WebViewActivity.TITLE to item.name
                            )
                        }
                    }
                    "6" -> {
                        getLearnedPrepare(item.field6) {
                            presenter.getResourceInfo(item.id, "1.3") {
                                val p = DownloadManager.getFilePathWithKey(item.id, item.type)
                                if (p.isNotEmpty() && File(p).exists()) {
                                    startActivity<PDFActivity>(
                                        PDFActivity.URL to p,
                                        PDFActivity.TITLE to item.name,
                                        PDFActivity.RES_ID to item.id,
                                        PDFActivity.SOURCE_TYPE to "1.3"
                                    )
                                } else {
                                    netCheck(null) {
                                        startActivity<PDFActivity>(
                                            PDFActivity.URL to it.url,
                                            PDFActivity.TITLE to item.name,
                                            PDFActivity.RES_ID to item.id,
                                            PDFActivity.SOURCE_TYPE to "1.3"
                                        )
                                    }
                                }
                            }
                        }
                    }
                    "7" -> {
                        getLearnedPrepare(item.field6) {
                            val resList = ArrayList<BookRes>()
                            resList.add(item)
//                            val p = DownloadManager.getFilePathWithKey(item.id, item.type)
//                            if (p.isNotEmpty() && File(p).exists()) {
//                                MyApp.instance.bookres = resList
//                                startActivity<AudioActivity>()
//                            } else {
//                                netCheck(null) {
//                            MyApp.instance.bookres = resList
                            // 存
                            val list1: List<BookRes> = ArrayList()
                            val gson1 = Gson()
                            val data1 = gson1.toJson(resList)
                            SPUtils.getInstance().put("BookRes", data1)
                            val intent = Intent(this, AudioActivity::class.java)
                            intent.putExtra(AudioActivity.RESOURCE_TYPE, "1.3")
                            startActivity(intent)
//                                }
//                            }
                        }
                    }
                    "8" -> {
                        getLearnedPrepare(item.field6) {
                            startActivity<WebViewActivity>(
                                WebViewActivity.URL to item.link, WebViewActivity.TITLE to item.name
                            )
                        }
                    }
                    "10" -> {
                        if ("1" != item.isFinish) {
                            startActivity<ExerciseDetailKActivity>(
                                ExerciseDetailKActivity.KEY to item.id,
                                ExerciseDetailKActivity.CAT_KEY to item.field1,
                                ExerciseDetailKActivity.NAME to item.name,
                                ExerciseDetailKActivity.EX_TYPE to ExerciseDetailKActivity.EX_TYPE_PG,
                                ExerciseDetailKActivity.SOURCE to "5",
                                ExerciseDetailKActivity.NET_COURSE_KEY to courseKey,
                                ExerciseDetailKActivity.NET_COURSE_VIDEO_KEY to videoKey,
                                ExerciseDetailKActivity.NET_COURSE_RESOURCE_KEY to item.field6,
                                ExerciseDetailKActivity.TYPE to item.type,
                                ExerciseDetailKActivity.USER_PRACTISE_KEY to item.userPracticeKey,
                            )
                        } else {
                            if (item.field3 == "0" || item.field3 == "2") {
                                presenter.getReport(item.id, item.userPracticeKey) {
                                    startActivity<ReportActivity>(
                                        ReportActivity.DATA to it,
                                        ReportActivity.TIME to "",
                                        ReportActivity.KEY to item.id,
                                        ReportActivity.NAME to item.name,
                                        ReportActivity.EX_TYPE to 2,
                                        ReportActivity.TYPE to item.type,
                                        ReportActivity.CAT_KEY to item.field1,
                                        ReportActivity.PRA_KEY to "",
                                        ReportActivity.SP_Q_KEY to "",
                                        ReportActivity.SP_G_NAME to "",
                                        ReportActivity.NEED_UP_LOAD to true,
                                        ReportActivity.EVAL_STATE to "0"
                                    )
                                }
                            } else {
                                startActivity<BookReportActivity>(
                                    BookReportActivity.EVALKEY to item.field1,
                                    BookReportActivity.PAPERKEY to item.id,//试卷key
                                    BookReportActivity.PAPER_NAME to item.name,//试卷名曾
                                    BookReportActivity.USERPRACTISEKEY to item.userPracticeKey,
                                    BookReportActivity.NEED_UP_LOAD to false,
                                    BookReportActivity.ANSWER_TYPE to "0"
                                )
                            }
                        }
                    }
                    "11" -> {
//              field1:  测评时，测评key；资讯时，资讯类型 1文字内容 2外链内容
//               field2	:测评时，阅卷方式 0是自判 1是人工 2可选；资讯时，外链
//               field3 测评时，试卷批改状态 0未批改 1已批改 2批改中；资讯时，文字内容
                        getLearnedPrepare(item.field6) {
                            when (item.field1) {
                                "1" -> startActivity<NewsAndAudioActivity>(
                                    NewsAndAudioActivity.KEY to item.id,
                                    NewsAndAudioActivity.TITLE to item.name
                                )
                                "2" -> startActivity<WebViewActivity>(
                                    WebViewActivity.URL to item.field2,
                                    WebViewActivity.TITLE to item.name
                                )
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }
}

class WorkPreAdapter(val OnClick: (item: BookRes) -> Unit) : BaseAdapter<BookRes>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: BookRes) {
        holder.setText(R.id.tv_title, item.name)
        holder.itemView.setOnClickListener {
            OnClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_month_item, parent, false)
    }

    override fun emptyImageRes(): Int {
        return R.drawable.empty_study
    }

    override fun emptyText(): String {
        return "暂无内容"
    }
}