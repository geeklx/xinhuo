package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson

import android.Manifest
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.ClassListBean
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_AUDIO
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_CONTENT
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_EX
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_IMG
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_LINK
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_NEWS
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_PARS
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_PDF
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_TEST
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_VIDEO
import tuoyan.com.xinghuo_dayingindex.bean.LessonRes
import tuoyan.com.xinghuo_dayingindex.bean.VideoParam
import tuoyan.com.xinghuo_dayingindex.ui._public.ImageActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.PDFActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.books.report.BookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.parsing.ExerciseParsingActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.message.NewsAndAudioActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.adapter.LessonResFragmentAdapter
import tuoyan.com.xinghuo_dayingindex.ui.practice.composition.CompositionDetailWebActivity
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils
import java.io.File
import kotlin.properties.Delegates

private const val DATA = "data"
private const val QQ_NAME = "qqName"
private const val QQ_NUM = "qqNum"
private const val QQ_CONTENT = "qqContent"
private const val QQ_KEY = "qqKey"
private const val IS_APP_LET = "isApplet"
private const val DOC_DOWN_URL = "docDownUrl"
private const val IS_DOC_DOWN = "isDocDown"
private const val TITLE = "TITLE"
private const val IS_APPLET_LISTEN = "ISAPPLETLISTEN"


class LessonResFragment : LifeV4Fragment<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int = 0

    private var rv_resource_list by Delegates.notNull<RecyclerView>()

    val parent by lazy { activity as MineLessonActivity }

    private val oAdapter by lazy {
        LessonResFragmentAdapter(
            qqNum ?: "",
            isAppLet ?: "",
            isAppletListen ?: "",
            isDocDown ?: "",
            docDownUrl ?: "",
            {
                PermissionUtlis.checkPermissions(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) {
                    parent.saClick("?????????")
                    resourceJump(it)
                }
            },
            {
                if (qqContent.isNullOrBlank()) {
                    if (qqKey.isNullOrBlank()) {
                        toast("QQ?????????????????????????????????~")
                    } else {
                        joinQQGroup(qqKey ?: "")
                    }
                } else {
                    AlertDialog.Builder(this.requireContext()).setTitle("??????QQ???")
                        .setMessage("????????????$qqContent")
                        .setPositiveButton("??????QQ") { _, _ ->
                            //???????????????????????????
                            val cm = this.requireContext()
                                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            // ?????????????????????ClipData
                            val mClipData = ClipData.newPlainText("Label", qqContent)
                            // ???ClipData?????????????????????????????????
                            cm.setPrimaryClip(mClipData)
                            if (qqKey.isNullOrBlank()) {
                                toast("QQ?????????????????????????????????~")
                            } else {
                                joinQQGroup(qqKey ?: "")
                            }
                        }.setNegativeButton("??????") { _, _ ->

                        }.create().show()
                }
            }) { type, url ->
            when (type) {
                1 -> {
                    parent.saClick("??????????????????")
                    //copy
                    //???????????????????????????
                    val cm = this.requireContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    // ?????????????????????ClipData
                    val mClipData = ClipData.newPlainText("Label", url)
                    // ???ClipData?????????????????????????????????
                    cm.setPrimaryClip(mClipData)
                    toast("?????????????????????")
                }
                2 -> {
                    parent.saClick("??????????????????")
                    isLogin {
                        ShareDialog(this.requireContext()) {
                            ShareUtils.share(
                                this.requireActivity(),
                                it,
                                title ?: "????????????",
                                "??????????????????",
                                url
                            )
                        }.show()
                    }
                }
                3 -> {
                    parent.saClick("????????????????????????")
                }
                4 -> {
                    parent.saClick("?????????????????????")
                }
            }

        }
    }

    fun joinQQGroup(key: String): Boolean {
        parent.saClick("????????????")
        val intent = Intent()
        intent.data =
            Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
        // ???Flag??????????????????????????????????????????????????????????????????????????????????????????Q???????????????????????????????????????????????????????????????
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            // ????????????Q???????????????????????????
            toast("????????????Q?????????")
            false
        }
    }

    /**
     * ????????????????????????
     * 1-paper:?????????2-paperAnalysis:????????????;3-media:??????  4- picture:?????? 5-imagesText:??????;6-document:?????????7-frequency :??????; 8-??????
     */
    private fun resourceJump(item: LessonRes?) {
        item?.let { lessonRes ->
            if (lessonRes.field5 == "2" && (lessonRes.field3 == "0" || lessonRes.field3 == "2")) {
                //??????????????????????????????????????????
                startActivity<CompositionDetailWebActivity>(
                    CompositionDetailWebActivity.PRACTISE_KEY to lessonRes.id,
                    CompositionDetailWebActivity.EVAL_KEY to lessonRes.field1,
                    CompositionDetailWebActivity.TITLE to lessonRes.name
                )
            } else {
                when (lessonRes.type) {
                    TYPE_EX -> {
                        if ("1" != lessonRes.isFinish) {
                            startActivity<ExerciseDetailKActivity>(
                                ExerciseDetailKActivity.KEY to lessonRes.id,
                                ExerciseDetailKActivity.NAME to lessonRes.name,
                                ExerciseDetailKActivity.CAT_KEY to lessonRes.parentKey,
                                ExerciseDetailKActivity.TYPE to lessonRes.type,
                                ExerciseDetailKActivity.USER_PRACTISE_KEY to lessonRes.userPracticeKey
                            )
                        } else {
                            presenter.getReport(lessonRes.id, lessonRes.userPracticeKey) {
                                startActivity<ReportActivity>(
                                    ReportActivity.DATA to it,
                                    ReportActivity.TIME to "",
                                    ReportActivity.KEY to lessonRes.id,
                                    ReportActivity.CAT_KEY to lessonRes.parentKey,
                                    ReportActivity.NAME to lessonRes.name,
                                    ReportActivity.TYPE to lessonRes.type,
                                    ReportActivity.EVAL_STATE to "1"
                                )
                            }
                        }
                    }
                    TYPE_PARS -> {
                        startActivity<ExerciseParsingActivity>(
                            ExerciseParsingActivity.KEY to lessonRes.id,
                            ExerciseParsingActivity.P_KET to "",
                            ExerciseParsingActivity.NAME to lessonRes.name
                        )
                    }
                    TYPE_VIDEO -> {
                        val videoParam = VideoParam()
                        videoParam.key = lessonRes.id
                        videoParam.name = lessonRes.name
                        videoParam.type = "2"
                        startActivity<VideoActivity>(VideoActivity.VIDEO_PARAM to videoParam)
                    }
                    TYPE_IMG -> {
                        startActivity<ImageActivity>(
                            ImageActivity.URL to lessonRes.link,
                            ImageActivity.NAME to lessonRes.name
                        )
                    }
                    TYPE_CONTENT -> {
                        startActivity<WebViewActivity>(
                            WebViewActivity.URL to lessonRes.content,
                            WebViewActivity.TITLE to lessonRes.name
                        )
                    }
                    TYPE_PDF -> {
                        val res = lessonRes
                        presenter.getResourceInfo(lessonRes.id, "2") { resourceInfo ->
                            res.playUrl = resourceInfo.url
                            res.downUrl = resourceInfo.downloadUrl
                            val p = DownloadManager.getFilePathWithKey(res.id, res.type)
                            if (p.isNotEmpty() && File(p).exists()) {
                                startActivity<PDFActivity>(
                                    PDFActivity.URL to p,
                                    PDFActivity.TITLE to resourceInfo.name,
                                    PDFActivity.RES_ID to res.id
                                )
                            } else {
                                netCheck(res.count) {
                                    startActivity<PDFActivity>(
                                        PDFActivity.URL to res.downUrl,
                                        PDFActivity.TITLE to resourceInfo.name,
                                        PDFActivity.RES_ID to res.id
                                    )
                                }
                            }
                        }
                    }
                    TYPE_AUDIO -> {
//                        val res = lessonRes
//                        presenter.getResourceInfo(lessonRes.id, "2") { resInfo ->
//                            res.playUrl = resInfo.url
//                            res.downUrl = resInfo.downloadUrl
                        val resList = ArrayList<BookRes>()
                        val bookRes = BookRes()
                        bookRes.name = lessonRes.name
                        bookRes.type = "7"
                        bookRes.playUrl = lessonRes.playUrl ?: ""
                        bookRes.downUrl = lessonRes.downUrl
                        bookRes.id = lessonRes.id ?: ""
                        bookRes.isCollection = lessonRes.isCollection
                        bookRes.downloadFlag = lessonRes.downloadFlag
                        bookRes.lrcurl = lessonRes.lrcurl
                        bookRes.lrcurl2 = lessonRes.lrcurl2
                        bookRes.lrcurl3 = lessonRes.lrcurl3
                        resList.add(bookRes)
//                            val p = DownloadManager.getFilePathWithKey(res.id, res.type)
//                            if (p.isNotEmpty() && File(p).exists()) {
//                                MyApp.instance.bookres = resList
//                                startActivity<AudioActivity>(
//                                    AudioActivity.SUPPORT_KEY to parent.key, AudioActivity.SUPPORT_NAME to (parent.lessonDetail.title ?: "")
//                                )
//                            } else {
//                                netCheck(res.count) {
//                        MyApp.instance.bookres = resList
                        // ???
                        val list1: List<BookRes> = ArrayList()
                        val gson1 = Gson()
                        val data1 = gson1.toJson(resList)
                        SPUtils.getInstance().put("BookRes", data1)
                        startActivity<AudioActivity>(
                            AudioActivity.SUPPORT_KEY to parent.key,
                            AudioActivity.SUPPORT_NAME to (parent.lessonDetail.title ?: "")
                        )
//                                }
//                            }
//                        }
                    }
                    TYPE_LINK -> {
                        startActivity<WebViewActivity>(
                            WebViewActivity.URL to lessonRes.link,
                            WebViewActivity.TITLE to lessonRes.name
                        )
                    }
                    TYPE_NEWS -> {
//              field1:  ??????????????????key??????????????????????????? 1???????????? 2????????????
//               field2	:???????????????????????? 0????????? 1????????? 2???????????????????????????
//               field3 ?????????????????????????????? 0????????? 1????????? 2????????????????????????????????????
                        when (lessonRes.field1) {
                            "1" -> startActivity<NewsAndAudioActivity>(
                                NewsAndAudioActivity.KEY to lessonRes.id,
                                NewsAndAudioActivity.TITLE to lessonRes.name
                            )
                            "2" -> startActivity<WebViewActivity>(
                                WebViewActivity.URL to lessonRes.field2,
                                WebViewActivity.TITLE to lessonRes.name
                            )
                        }

                    }
                    TYPE_TEST -> {
                        if ("1" != lessonRes.isFinish) {
                            //??????activity????????????????????????????????????userPracticeKey?????????
                            parent.isTest = true
                            startActivity<ExerciseDetailKActivity>(
                                ExerciseDetailKActivity.KEY to lessonRes.id,
                                ExerciseDetailKActivity.NAME to lessonRes.name,
                                ExerciseDetailKActivity.CAT_KEY to lessonRes.field1,
                                ExerciseDetailKActivity.EX_TYPE to ExerciseDetailKActivity.EX_TYPE_PG,
                                ExerciseDetailKActivity.TYPE to lessonRes.type,
                                ExerciseDetailKActivity.USER_PRACTISE_KEY to lessonRes.userPracticeKey,
                            )
                        } else {
                            if (lessonRes.field3 == "0" || lessonRes.field3 == "2") {
                                presenter.getReport(
                                    lessonRes.id,
                                    lessonRes.userPracticeKey
                                ) { report ->
                                    startActivity<ReportActivity>(
                                        ReportActivity.DATA to report,
                                        ReportActivity.TIME to "",
                                        ReportActivity.KEY to lessonRes.id,
                                        ReportActivity.NAME to lessonRes.name,
                                        ReportActivity.EX_TYPE to 2,
                                        ReportActivity.TYPE to lessonRes.type,
                                        ReportActivity.CAT_KEY to lessonRes.field1,
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
                                    BookReportActivity.PAPERKEY to item.id,//??????key
                                    BookReportActivity.PAPER_NAME to item.name,//????????????
                                    BookReportActivity.USERPRACTISEKEY to item.userPracticeKey,
                                    BookReportActivity.NEED_UP_LOAD to false,
                                    BookReportActivity.ANSWER_TYPE to "0"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            list: ArrayList<ClassListBean>,
            qqName: String?,
            qqNum: String?,
            qqContent: String?,
            qqKey: String?,
            isApplet: String?,
            isDocDown: String?,
            docDownUrl: String?,
            title: String?,
            isAppletListen: String?
        ) =
            LessonResFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(DATA, list)
                    putString(QQ_NAME, qqName)
                    putString(QQ_NUM, qqNum)
                    putString(QQ_CONTENT, qqContent)
                    putString(QQ_KEY, qqKey)
                    putString(IS_APP_LET, isApplet)
                    putString(IS_DOC_DOWN, isDocDown)
                    putString(DOC_DOWN_URL, docDownUrl)
                    putString(TITLE, title)
                    putString(IS_APPLET_LISTEN, isAppletListen)
                }
            }
    }

    override fun initView(): View? = with(this.requireContext()) {
        rv_resource_list = recyclerView {
            backgroundColor = Color.parseColor("#ffffff")
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ctx)
            lparams(matchParent, matchParent)
        }
        rv_resource_list
    }

    override fun configView(view: View?) {
    }

    var qqName: String? = null
    var qqNum: String? = null
    var qqContent: String? = null
    var qqKey: String? = null
    var isAppLet: String? = null
    var isDocDown: String? = null
    var docDownUrl: String? = null
    var title: String? = null
    var isAppletListen: String? = null
    override fun initData() {
        arguments?.let {
            var dataList = it.getSerializable(DATA) as ArrayList<ClassListBean>
            qqName = it.getString(QQ_NAME)
            qqNum = it.getString(QQ_NUM)
            qqContent = it.getString(QQ_CONTENT)
            qqKey = it.getString(QQ_KEY)
            isAppLet = it.getString(IS_APP_LET)
            isDocDown = it.getString(IS_DOC_DOWN)
            docDownUrl = it.getString(DOC_DOWN_URL)
            title = it.getString(TITLE)
            isAppletListen = it.getString(IS_APPLET_LISTEN)
            rv_resource_list.adapter = oAdapter
            initResourceList(dataList)
        }
    }

    /**
     * ??????????????????????????? ?????????????????????????????? ???????????? -- resourceList
     */
    fun initResourceList(dataList: ArrayList<ClassListBean>) {
        oAdapter.setData(dataList)
    }
}






























