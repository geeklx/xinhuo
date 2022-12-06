package tuoyan.com.xinghuo_dayingindex.ui._public

import android.Manifest
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.activity_scan_res_list.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.parsing.ExerciseParsingActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.message.NewsAndAudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.practice.composition.CompositionDetailWebActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import java.io.File

class ScanResListActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_scan_res_list

    //    val resList by lazy { intent.getSerializableExtra(LIST) as ArrayList<BookRes> }
//    val detail by lazy { intent.getSerializableExtra(DETAIL) as? BookDetail }
    //    override val title by lazy { intent.getStringExtra(TITLE) }
    private var detail: BookDetail? = null
    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    private val mAdapter by lazy {
        ScanResAdapter { it, pos ->
            // field5	测评时，试卷类型 1真题，2主观题，3客观题
            //   field3	  测评时，试卷批改状态 -1已购买未答题 0未批改 1已批改 2批改中；资讯时，文字内容
            if (it.field5 == "2" && (it.field3 == "0" || it.field3 == "2")) {
                //主观题批改中跳转到正在批改中
                startActivity<CompositionDetailWebActivity>(
                    CompositionDetailWebActivity.PRACTISE_KEY to it.id,
                    CompositionDetailWebActivity.EVAL_KEY to it.field1,
                    CompositionDetailWebActivity.TITLE to it.name
                )
            } else {
                PermissionUtlis.checkPermissions(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) {
                    resourceJump(it, pos)
                }
            }
        }
    }

    companion object {
        //        val LIST = "list"
//        val TITLE = "title"
//        val DETAIL = "detail"
        const val KEY = "KEY"
    }

    override fun configView() {
        setSupportActionBar(tb_scan_res)
        rv_res_list.layoutManager = LinearLayoutManager(this)
        rv_res_list.adapter = mAdapter
    }

    override fun handleEvent() {
        tb_scan_res.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        presenter.getResourcesByCatalog(key, "") { bookDetail ->
            detail = bookDetail
            tv_title.text = bookDetail?.name ?: ""
            mAdapter.setData(bookDetail?.resourceList)
            bookDetail?.let { saView(it) }
        }
    }

    /**
     * 不同资源进行跳转
     * 1:试卷,2:试卷解析,3:视频,4:图片,5:图文,6:文档,7:音频,8:链接,9:字幕,10:测评,11:资讯,12:PC模考 13口语测评
     */
    private fun resourceJump(item: BookRes, position: Int) {
        when (item.type) {
            DownloadBean.TYPE_EX -> {
                if ("1" != item.isFinish) {
                    startActivity<ExerciseDetailKActivity>(
                        ExerciseDetailKActivity.KEY to item.id,
                        ExerciseDetailKActivity.NAME to item.name,
                        ExerciseDetailKActivity.CAT_KEY to item.parentKey,
                        ExerciseDetailKActivity.TYPE to item.type,
                        ExerciseDetailKActivity.USER_PRACTISE_KEY to item.userPracticeKey
                    )
                } else {
                    presenter.getReport(item.id, item.userPracticeKey) {
                        startActivity<ReportActivity>(
                            ReportActivity.DATA to it,
                            ReportActivity.TIME to "",
                            ReportActivity.KEY to item.id,
                            ReportActivity.CAT_KEY to item.parentKey,
                            ReportActivity.NAME to item.name,
                            ReportActivity.TYPE to item.type,
                            ReportActivity.EVAL_STATE to "1"
                        )
                    }
                }
            }
            DownloadBean.TYPE_PARS -> {
                startActivity<ExerciseParsingActivity>(
                    ExerciseParsingActivity.KEY to item.id,
                    ExerciseParsingActivity.P_KET to "",
                    ExerciseParsingActivity.NAME to item.name
                )
            }
            DownloadBean.TYPE_VIDEO -> {
                val sensorDetail = SensorBook()
                detail?.let { book ->
                    sensorDetail.section = book.gradeName ?: ""
                    sensorDetail.book_matching_name = book.bookName ?: ""
                    sensorDetail.book_matching_id = book.bookKey ?: ""
                }
                val videoParam = VideoParam()
                videoParam.key = item.id
                videoParam.type = "2"
                startActivity<VideoActivity>(
                    VideoActivity.VIDEO_PARAM to videoParam,
                    VideoActivity.SENSORSBOOK to sensorDetail
                )
//                val res = item
//                presenter.getResourceInfo(res.id, "2") {
//                    res.playUrl = it.url
//                    res.downUrl = it.downloadUrl
//
//                    val p = DownloadManager.getFilePathWithKey(res.id, item.type)
//                    if (p.isNotEmpty() && File(p).exists()) {
//                        startActivity<VideoActivity>(
//                            VideoActivity.URL to p,
//                            VideoActivity.SENSORSBOOK to sensorDetail
//                        )
//                    } else {
//                        netCheck(null) {
//                            startActivity<VideoActivity>(
//                                VideoActivity.URL to res.playUrl,
//                                VideoActivity.SENSORSBOOK to sensorDetail
//                            )
//                        }
//                    }
//                }
            }
            DownloadBean.TYPE_IMG -> {
                startActivity<ImageActivity>(
                    ImageActivity.URL to item.link, ImageActivity.NAME to item.name
                )
            }
            DownloadBean.TYPE_CONTENT -> {
                startActivity<WebViewActivity>(
                    WebViewActivity.URL to item.content, WebViewActivity.TITLE to item.name
                )
            }
            DownloadBean.TYPE_PDF -> {
                val res = item
                presenter.getResourceInfo(res.id, "2") {
                    res.playUrl = it.url
                    res.downUrl = it.downloadUrl

                    val p = DownloadManager.getFilePathWithKey(res.id, item.type)
                    if (p.isNotEmpty() && File(p).exists()) {
                        startActivity<PDFActivity>(
                            PDFActivity.URL to p,
                            PDFActivity.TITLE to item.name,
                            PDFActivity.RES_ID to res.id
                        )
                    } else {
                        netCheck(null) {
                            startActivity<PDFActivity>(
                                PDFActivity.URL to res.downUrl,
                                PDFActivity.TITLE to item.name,
                                PDFActivity.RES_ID to res.id
                            )
                        }
                    }
                }
            }
            DownloadBean.TYPE_AUDIO -> {
//                presenter.getResourceInfo(item.id, "2") {
//                    item.playUrl = it.url
//                    item.downUrl = it.downloadUrl
//                    var resList = ArrayList<BookRes>()
//                    var bookRes = item
//                    resList.add(bookRes)
                val sensorDetail = SensorBook()
                detail?.let { book ->
                    sensorDetail.section = book.gradeName ?: ""
                    sensorDetail.book_matching_name = book.bookName ?: ""
                    sensorDetail.book_matching_id = book.bookKey ?: ""
                }
//                    val p = DownloadManager.getFilePathWithKey(item.id, item.type)
//                    if (p.isNotEmpty() && File(p).exists()) {
//                        MyApp.instance.bookres = detail?.resourceList
//                        startActivity<AudioActivity>(
//                            AudioActivity.POSITION to position,
//                            AudioActivity.SENSORBOOK to sensorDetail
//                        )
//                    } else {
//                        netCheck(null) {
//                MyApp.instance.bookres = detail?.resourceList
                // 存
                val list1: List<ResourceListBean> = ArrayList()
                val gson1 = Gson()
                val data1 = gson1.toJson(detail?.resourceList)
                SPUtils.getInstance().put("BookRes", data1)
                startActivity<AudioActivity>(
                    AudioActivity.POSITION to position, AudioActivity.SENSORBOOK to sensorDetail
                )
//                        }
//                    }
//                }
            }
            DownloadBean.TYPE_LINK -> {
                startActivity<WebViewActivity>(
                    WebViewActivity.URL to item.link, WebViewActivity.TITLE to item.name
                )
            }
            DownloadBean.TYPE_TEST -> {
                if ("1" != item.isFinish) {
                    startActivity<ExerciseDetailKActivity>(
                        ExerciseDetailKActivity.KEY to item.id,
                        ExerciseDetailKActivity.CAT_KEY to item.field1,
                        ExerciseDetailKActivity.NAME to item.name,
                        ExerciseDetailKActivity.EX_TYPE to ExerciseDetailKActivity.EX_TYPE_PG,
                        ExerciseDetailKActivity.TYPE to item.type,
                        ExerciseDetailKActivity.USER_PRACTISE_KEY to item.userPracticeKey
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
            DownloadBean.TYPE_NEWS -> {
//              field1:  测评时，测评key；资讯时，资讯类型 1文字内容 2外链内容
//               field2	:测评时，阅卷方式 0是自判 1是人工 2可选；资讯时，外链
//               field3 测评时，试卷批改状态 0未批改 1已批改 2批改中；资讯时，文字内容
                when (item.field1) {
                    "1" -> startActivity<NewsAndAudioActivity>(
                        NewsAndAudioActivity.KEY to item.id, NewsAndAudioActivity.TITLE to item.name
                    )
                    "2" -> startActivity<WebViewActivity>(
                        WebViewActivity.URL to item.field2, WebViewActivity.TITLE to item.name
                    )
                }

            }
        }
    }

    private fun saView(bookDetail: BookDetail) {
        try {
            val property = saProperty(bookDetail)
            SensorsDataAPI.sharedInstance().track("view_book_matching_page", property)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun saProperty(bookDetail: BookDetail): JSONObject {
        val property = JSONObject()
        property.put("book_matching_id", bookDetail.bookKey)
        property.put("book_matching_name", bookDetail.bookName)
        property.put("section", bookDetail.gradeName)
        property.put("matching_subcode_id", bookDetail.resourceList[0].parentKey)
        property.put("matching_page_type", "列表页")
        return property
    }
}


class ScanResAdapter(var onClick: (itemd: BookRes, pos: Int) -> Unit) : BaseAdapter<BookRes>() {

    override fun convert(holder: ViewHolder, item: BookRes) {
        holder.setVisible(R.id.pb_book_res, View.GONE).setVisible(R.id.iv_download, View.GONE)
            .setText(R.id.tv_title_book, item.name).setText(R.id.tv_duration, item.duration)
            .setVisible(
                R.id.tv_duration, if (item.duration.isNullOrEmpty()) View.GONE else View.VISIBLE
            ).setVisible(
                R.id.tv_duration,
                if (item.type == "3" || item.type == "7") View.VISIBLE else View.GONE
            )
        val tvTitle = holder.getView(R.id.tv_title_book) as TextView
        tvTitle.setCompoundDrawablesWithIntrinsicBounds(
            when (item.type) {
                //1-paper:试卷；2-paperAnalysis:试卷解析;3-media:视频  4- picture:图片 5-imagesText:图文;6-document:文档；7-frequency :音频; 8-外链
                "3" -> R.drawable.src_type_video
                "7" -> R.drawable.src_type_audio
                "5" -> R.drawable.src_type_content
                "4" -> R.drawable.src_type_content
                else -> R.drawable.src_type_doc
            }, 0, 0, 0
        )
        holder.itemView.setOnClickListener {
            onClick(item, holder.adapterPosition)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View =
        context.layoutInflater.inflate(R.layout.item_book_res, null)

    // field5	测评时，试卷类型 1真题，2主观题，3客观题
//   field3	  测评时，试卷批改状态 -1已购买未答题 0未批改 1已批改 2批改中；资讯时，文字内容
    override fun getItemViewType(position: Int): Int {
        val item: BookRes = getData()[position]
        when (item.field5) {
            "2" -> {
                return 2
            }
        }
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            2 -> {
                ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_composition, null)
                )
            }
            else -> {
                super.onCreateViewHolder(parent, viewType)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: BookRes = getData()[position]
        //  field3  测评时，试卷批改状态 -1已购买未答题 0未批改 1已批改 2批改中；资讯时，文字内容
        holder.itemView.setOnClickListener {
            onClick(item, position)
        }
        when (holder.itemViewType) {
            2 -> {
                if (position < 9) {
                    holder.setText(R.id.tv_sort, "0" + (position + 1))
                } else {
                    holder.setText(R.id.tv_sort, (position + 1).toString())
                }
                holder.setText(R.id.tv_title, item.name)
                when (item.field3) {
//                    1已购买未答题
                    "-1" -> {
                        holder.setBackgroundResource(
                            R.id.rl_item_bg, R.mipmap.bg_composition_submit
                        )
                    }
//                    0未批改状态 按 批改中
                    "0" -> {
                        holder.setBackgroundResource(R.id.rl_item_bg, R.mipmap.bg_composition_ing)
                    }
//                    1已批改
                    "1" -> {
                        holder.setBackgroundResource(
                            R.id.rl_item_bg, R.mipmap.bg_composition_correct
                        )
                    }
//                    2批改中
                    "2" -> {
                        holder.setBackgroundResource(R.id.rl_item_bg, R.mipmap.bg_composition_ing)
                    }
                    else -> {
                        holder.setBackgroundResource(
                            R.id.rl_item_bg, R.mipmap.bg_composition_normal
                        )
                    }
                }
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }
    }
}
