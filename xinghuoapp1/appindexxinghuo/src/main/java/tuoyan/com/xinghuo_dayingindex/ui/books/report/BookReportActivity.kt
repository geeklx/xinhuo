package tuoyan.com.xinghuo_dayingindex.ui.books.report

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_book_report.*
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.appId
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
import tuoyan.com.xinghuo_dayingindex.ui.exercise.report.ReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.LessonComActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.EvalCardActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.overPackage.report.ReportPresenter
import tuoyan.com.xinghuo_dayingindex.ui.practice.PracticeRankActivity
import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini

class BookReportActivity : LifeActivity<ReportPresenter>() {
    var correctList = mutableListOf<String>()
    override val presenter: ReportPresenter
        get() = ReportPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_book_report

    var evalReport: EvalReport = EvalReport()

    private val evalkey by lazy { intent.getStringExtra(EVALKEY) ?: "" }
    private val paperkey by lazy { intent.getStringExtra(PAPERKEY) ?: "" }
    private val userpractisekey by lazy { intent.getStringExtra(USERPRACTISEKEY) ?: "" }
    private val needUpLoad by lazy { intent.getBooleanExtra(NEED_UP_LOAD, false) }
    private val paperName by lazy { intent.getStringExtra(PAPER_NAME) ?: "" }
    private val answerType by lazy { intent.getStringExtra(ANSWER_TYPE) ?: "" } //TODO 0线上  1线下
    private val spQKey by lazy { intent.getStringExtra("SP_Q_KEY") ?: "" }
    private val eval2this by lazy { intent.getBooleanExtra(EVAL2THIS, false) }

    private var mBorderColor = Color.parseColor("#99a4ff")
    private var mBorderWidth = 3
    private var mWaveHelper: WaveHelper? = null

    private var isFresh = false
    override fun configView() {
        setSupportActionBar(tb_book_report)
        tb_book_report.setNavigationOnClickListener { onBackPressed() }
        rlv_my_answer.layoutManager = GridLayoutManager(ctx, 3)
        rlv_my_answer.adapter = adapter
        rlv_teacher_answer.layoutManager = GridLayoutManager(ctx, 3)
        rlv_teacher_answer.adapter = teacherAdapter
        wave.setBorder(mBorderWidth, mBorderColor)
        mWaveHelper = WaveHelper(wave)
        rlv_recommend.layoutManager = LinearLayoutManager(this)
        rlv_recommend.isNestedScrollingEnabled = false
        rlv_recommend.adapter = recommedAdapter
    }

    private val adapter by lazy {
        SelfAdapter {
            //展示图片
//            Glide.with(ctx).load(exModelList.get(it)).into(img_detail)
//            rl_img_detail.visibility = View.VISIBLE
        }
    }
    private val teacherAdapter by lazy {
        AnswerAdapter { _, item ->
            //展示图片
            rl_img_detail.removeAllViews()
            val imgView = ImageView(this)
            val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            params.centerInParent()
            rl_img_detail.addView(imgView, params)
            Glide.with(ctx).load(item.imgUrl).into(imgView)
            rl_img_detail.visibility = View.VISIBLE
        }
    }
    private val recommedAdapter by lazy {
        RecommedAdapter { item ->
            //1:bookList;2: netList;3: paperList;4:infomationList 5：小程序添加一个程序的
            when (item.type) {
                "1" -> {
                    startActivity<PostActivity>(PostActivity.URL to "bookdetail?id=${item.key}")
                }
                "2" -> {
                    //3.配套二维码 3.1营销二维码 测评推荐相当于扫描配套二维码
                    startActivity<LessonDetailActivity>(
                        LessonDetailActivity.KEY to item.key,
                        LessonDetailActivity.TYPE to "3"
                    )
                }
                "3" -> {
                    isFresh = true
                    if ("1" != item.isFinish) {
                        startActivity<ExerciseDetailKActivity>(
                            ExerciseDetailKActivity.KEY to item.key,
                            ExerciseDetailKActivity.NAME to item.name,
                            ExerciseDetailKActivity.CAT_KEY to "",
                            ExerciseDetailKActivity.TYPE to "1",
                            ExerciseDetailKActivity.USER_PRACTISE_KEY to item.userPracticeKey,
                        )
                    } else {
                        presenter.getReport(item.key, item.userPracticeKey) {
                            startActivity<ReportActivity>(
                                ReportActivity.DATA to it,
                                ReportActivity.TIME to "",
                                ReportActivity.KEY to item.key,
                                ReportActivity.NAME to item.name,
                                ReportActivity.TYPE to "1",
                                ReportActivity.EVAL_STATE to "1"
                            )
                        }
                    }
                }
                "4" -> {
                    startActivity<RecommedNewsActivity>(
                        RecommedNewsActivity.TITLE to item.name,
                        RecommedNewsActivity.CLSSIFY_KEY to item.key,
                        RecommedNewsActivity.EVAL_RECOMMEND_KEY to item.recommendKey
                    )
                }
                "5" -> {
                    WxMini.goWxMini(this, item.key)
                }
            }
        }
    }

    override fun initData() {
        super.initData()
        presenter.evalReport(evalkey, paperkey, userpractisekey) { report ->
            evalReport = report
            tv_score.text = report.rightScore
            tv_total.text = "满分\u0020" + report.totalScore
            if (report.totalScore!!.toFloat() == 0f) {
                wave.waterLevelRatio = 0f
            } else {
                wave.waterLevelRatio = (report.rightScore!!.toFloat() / report.totalScore!!.toFloat())
            }
            tv_comment.text = Html.fromHtml(report.remark ?: "")
            //	1真题，2主观题，3客观题
            if (report.type == "2") {
                //主观题显示按钮范文 显示图片列表
                if (report.evalMode == "1") {
                    ll_answer.visibility = View.VISIBLE
                    adapter.setData(report.imgList)
                    teacherAdapter.setData(report.correctList)
                } else {
                    ll_answer.visibility = View.GONE
                }
                tv_answer.text = "查看范文"
            } else {
                if (report.type == "1") {
                    //真题显示排行榜
                    img_ranking.visibility = View.VISIBLE
                }
                ll_answer.visibility = View.GONE
            }

            if (report.type == "1") {
                //真题 四个维度
                ll_table.visibility = View.VISIBLE
                val subiEvalList = mutableListOf<SubiEvaBean>()
                val itemHear = SubiEvaBean()
                itemHear.totalSore = report.hearingTotal.toString()
                itemHear.rightScore = report.hearingScore.toString()
                itemHear.name = "听力"

                val itemRead = SubiEvaBean()
                itemRead.totalSore = report.readingTotal.toString()
                itemRead.rightScore = report.readingScore.toString()
                itemRead.name = "阅读"

                val itemTran = SubiEvaBean()
                itemTran.totalSore = report.translationTotal.toString()
                itemTran.rightScore = report.translationScore.toString()
                itemTran.name = "翻译"

                val itemWrite = SubiEvaBean()
                itemWrite.totalSore = report.writingTotal.toString()
                itemWrite.rightScore = report.writingScore.toString()
                itemWrite.name = "写作"

                subiEvalList.add(itemWrite)
                subiEvalList.add(itemHear)
                subiEvalList.add(itemRead)
                subiEvalList.add(itemTran)
                initTable(subiEvalList)
            } else if (report.type == "2" && report.subiEvalList!!.size > 0) {
                //主观题5个维度
                ll_table.visibility = View.VISIBLE
                initTable(report.subiEvalList!!)
            } else {
                ll_table.visibility = View.GONE
            }
            val recommedList = mutableListOf<RecommedBean>()
            //1:bookList;2: netList;3: paperList;4:infomationList
            report.bookList?.forEach { bookList ->
                val bean = RecommedBean()
                bean.key = bookList.key.toString()
                bean.name = bookList.title.toString()
                bean.type = "1"
                recommedList.add(bean)
            }
            report.netList?.forEach { lesson ->
                val bean = RecommedBean()
                bean.key = lesson.key.toString()
                bean.name = lesson.title.toString()
                bean.type = "2"
                recommedList.add(bean)
            }
            report.paperList?.forEach { paperBean ->
                val bean = RecommedBean()
                bean.key = paperBean.key.toString()
                bean.name = paperBean.name.toString()
                bean.type = "3"
                bean.isFinish = paperBean.isFinish
                bean.userPracticeKey = paperBean.userPracticeKey
                recommedList.add(bean)
            }
            report.infomationList?.forEach { infomationBean ->
                val bean = RecommedBean()
                bean.key = infomationBean.clssifyKey.toString()
                bean.name = infomationBean.clssifyName.toString()
                bean.recommendKey = infomationBean.evalRecommendKey.toString()
                bean.type = "4"
                recommedList.add(bean)
            }
            report.appletList.forEach { applet ->
                val bean = RecommedBean()
                bean.key = applet.key
                bean.name = applet.name
                bean.type = "5"
                recommedList.add(bean)
            }
            if (recommedList.isEmpty()) {
                rl_recommend.visibility = View.GONE
            } else {
                rl_recommend.visibility = View.VISIBLE
                recommedAdapter.setData(recommedList)
            }
        }
    }

    fun initTable(subiEvalList: List<SubiEvaBean>) {
        val totalHeight = dip(200)
        if (subiEvalList.size == 5) {
            //主观题 5个维度
            tv_5.text = subiEvalList[4].name
            tv_fraction_5.text = subiEvalList[4].rightScore
            tv_table_5.text = subiEvalList[4].name
            val p5 = subiEvalList[4].rightScore.toFloat() / subiEvalList[4].totalSore.toFloat()
            val params5 = view_height_5.layoutParams
            params5.height = (p5 * totalHeight).toInt()
            view_height_5.layoutParams = params5

            tv_4.text = subiEvalList[3].name
            tv_fraction_4.text = subiEvalList[3].rightScore
            tv_3.text = subiEvalList[2].name
            tv_fraction_3.text = subiEvalList[2].rightScore
            tv_2.text = subiEvalList[1].name
            tv_fraction_2.text = subiEvalList[1].rightScore
            tv_1.text = subiEvalList[0].name
            tv_fraction_1.text = subiEvalList[0].rightScore

            tv_table_1.text = subiEvalList[0].name
            tv_table_2.text = subiEvalList[1].name
            tv_table_3.text = subiEvalList[2].name
            tv_table_4.text = subiEvalList[3].name

            val p1 = subiEvalList[0].rightScore.toFloat() / subiEvalList[0].totalSore.toFloat()
            val p2 = subiEvalList[1].rightScore.toFloat() / subiEvalList[1].totalSore.toFloat()
            val p3 = subiEvalList[2].rightScore.toFloat() / subiEvalList[2].totalSore.toFloat()
            val p4 = subiEvalList[3].rightScore.toFloat() / subiEvalList[3].totalSore.toFloat()

            val params1 = view_height_1.layoutParams
            params1.height = (p1 * totalHeight).toInt()
            view_height_1.layoutParams = params1

            val params2 = view_height_2.layoutParams
            params2.height = (p2 * totalHeight).toInt()
            view_height_2.layoutParams = params2

            val params3 = view_height_3.layoutParams
            params3.height = (p3 * totalHeight).toInt()
            view_height_3.layoutParams = params3

            val params4 = view_height_4.layoutParams
            params4.height = (p4 * totalHeight).toInt()
            view_height_4.layoutParams = params4
        } else if (subiEvalList.size == 4) {
            //真题4个维度
            ll_5.visibility = View.GONE
            ll_table_5.visibility = View.GONE
            sp_5.visibility = View.GONE
            tv_table_5.visibility = View.GONE
            sp_table_5.visibility = View.GONE

            tv_4.text = subiEvalList[3].name
            tv_fraction_4.text = subiEvalList[3].rightScore
            tv_3.text = subiEvalList[2].name
            tv_fraction_3.text = subiEvalList[2].rightScore
            tv_2.text = subiEvalList[1].name
            tv_fraction_2.text = subiEvalList[1].rightScore
            tv_1.text = subiEvalList[0].name
            tv_fraction_1.text = subiEvalList[0].rightScore

            tv_table_1.text = subiEvalList[0].name
            tv_table_2.text = subiEvalList[1].name
            tv_table_3.text = subiEvalList[2].name
            tv_table_4.text = subiEvalList[3].name

            val p1 = subiEvalList[0].rightScore.toFloat() / subiEvalList[0].totalSore.toFloat()
            val p2 = subiEvalList[1].rightScore.toFloat() / subiEvalList[1].totalSore.toFloat()
            val p3 = subiEvalList[2].rightScore.toFloat() / subiEvalList[2].totalSore.toFloat()
            val p4 = subiEvalList[3].rightScore.toFloat() / subiEvalList[3].totalSore.toFloat()

            val params1 = view_height_1.layoutParams
            params1.height = (p1 * totalHeight).toInt()
            view_height_1.layoutParams = params1

            val params2 = view_height_2.layoutParams
            params2.height = (p2 * totalHeight).toInt()
            view_height_2.layoutParams = params2

            val params3 = view_height_3.layoutParams
            params3.height = (p3 * totalHeight).toInt()
            view_height_3.layoutParams = params3

            val params4 = view_height_4.layoutParams
            params4.height = (p4 * totalHeight).toInt()
            view_height_4.layoutParams = params4

        } else if (subiEvalList.size == 3) {
            ll_5.visibility = View.GONE
            ll_table_5.visibility = View.GONE
            sp_5.visibility = View.GONE
            tv_table_5.visibility = View.GONE
            sp_table_5.visibility = View.GONE

            ll_4.visibility = View.GONE
            ll_table_4.visibility = View.GONE
            sp_4.visibility = View.GONE
            tv_table_4.visibility = View.GONE
            sp_table_4.visibility = View.GONE

            tv_3.text = subiEvalList[2].name
            tv_fraction_3.text = subiEvalList[2].rightScore
            tv_2.text = subiEvalList[1].name
            tv_fraction_2.text = subiEvalList[1].rightScore
            tv_1.text = subiEvalList[0].name
            tv_fraction_1.text = subiEvalList[0].rightScore

            tv_table_1.text = subiEvalList[0].name
            tv_table_2.text = subiEvalList[1].name
            tv_table_3.text = subiEvalList[2].name

            val p1 = subiEvalList[0].rightScore.toFloat() / subiEvalList[0].totalSore.toFloat()
            val p2 = subiEvalList[1].rightScore.toFloat() / subiEvalList[1].totalSore.toFloat()
            val p3 = subiEvalList[2].rightScore.toFloat() / subiEvalList[2].totalSore.toFloat()

            val params1 = view_height_1.layoutParams
            params1.height = (p1 * totalHeight).toInt()
            view_height_1.layoutParams = params1

            val params2 = view_height_2.layoutParams
            params2.height = (p2 * totalHeight).toInt()
            view_height_2.layoutParams = params2

            val params3 = view_height_3.layoutParams
            params3.height = (p3 * totalHeight).toInt()
            view_height_3.layoutParams = params3
        } else if (subiEvalList.size == 2) {
            ll_5.visibility = View.GONE
            ll_table_5.visibility = View.GONE
            sp_5.visibility = View.GONE
            tv_table_5.visibility = View.GONE
            sp_table_5.visibility = View.GONE

            ll_4.visibility = View.GONE
            ll_table_4.visibility = View.GONE
            sp_4.visibility = View.GONE
            tv_table_4.visibility = View.GONE
            sp_table_4.visibility = View.GONE

            ll_3.visibility = View.GONE
            ll_table_3.visibility = View.GONE
            sp_3.visibility = View.GONE
            tv_table_3.visibility = View.GONE
            sp_table_3.visibility = View.GONE

            tv_2.text = subiEvalList[1].name
            tv_fraction_2.text = subiEvalList[1].rightScore
            tv_1.text = subiEvalList[0].name
            tv_fraction_1.text = subiEvalList[0].rightScore

            tv_table_1.text = subiEvalList[0].name
            tv_table_2.text = subiEvalList[1].name

            val p1 = subiEvalList[0].rightScore.toFloat() / subiEvalList[0].totalSore.toFloat()
            val p2 = subiEvalList[1].rightScore.toFloat() / subiEvalList[1].totalSore.toFloat()

            val params1 = view_height_1.layoutParams
            params1.height = (p1 * totalHeight).toInt()
            view_height_1.layoutParams = params1

            val params2 = view_height_2.layoutParams
            params2.height = (p2 * totalHeight).toInt()
            view_height_2.layoutParams = params2
        } else if (subiEvalList.size == 1) {
            ll_5.visibility = View.GONE
            ll_table_5.visibility = View.GONE
            sp_5.visibility = View.GONE
            tv_table_5.visibility = View.GONE
            sp_table_5.visibility = View.GONE

            ll_4.visibility = View.GONE
            ll_table_4.visibility = View.GONE
            sp_4.visibility = View.GONE
            tv_table_4.visibility = View.GONE
            sp_table_4.visibility = View.GONE

            ll_3.visibility = View.GONE
            ll_table_3.visibility = View.GONE
            sp_3.visibility = View.GONE
            tv_table_3.visibility = View.GONE
            sp_table_3.visibility = View.GONE

            ll_2.visibility = View.GONE
            ll_table_2.visibility = View.GONE
            sp_2.visibility = View.GONE
            tv_table_2.visibility = View.GONE
            sp_table_2.visibility = View.GONE

            tv_1.text = subiEvalList[0].name
            tv_fraction_1.text = subiEvalList[0].rightScore

            tv_table_1.text = subiEvalList[0].name

            val p1 = subiEvalList[0].rightScore.toFloat() / subiEvalList[0].totalSore.toFloat()

            val params1 = view_height_1.layoutParams
            params1.height = (p1 * totalHeight).toInt()
            view_height_1.layoutParams = params1
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        rl_img_detail.setOnClickListener {
            it.visibility = View.GONE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scv_content.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (scrollY > 88) {
                    tb_book_report.backgroundColor = resources.getColor(R.color.white)
                    tb_book_report.navigationIcon = getDrawable(R.mipmap.ic_back)
                    ic_share.imageResource = R.mipmap.ic_share
                    tv_title.textColor = ContextCompat.getColor(this, R.color.color_1e1e1e)
                } else {
                    tb_book_report.backgroundColor = android.R.color.transparent
                    tb_book_report.navigationIcon = getDrawable(R.mipmap.icon_back_white)
                    ic_share.imageResource = R.mipmap.ic_share_white
                    tv_title.textColor = ContextCompat.getColor(this, R.color.white)
                }
            }
        }

        ic_share.setOnClickListener {
            isLogin {
                ShareDialog(ctx) {
                    ShareUtils.share(
                        this, it, "我发现了一个提分利器APP，自我测评学情诊断，四六级有效提分！", "专业让学习简单",
                        WEB_BASE_URL + "reportSharePage?userKey=${SpUtil.user.userId}" +
                                "&userPractiseKey=${userpractisekey}" +
                                "&evalKey=${evalkey}" +
                                "&paperKey=${paperkey}" +
                                "&appKey=$appId"
                    )
                }.show()
            }
        }

        tv_eval.setOnClickListener {
            val intent = Intent(this, LessonComActivity::class.java)
            intent.putExtra(LessonComActivity.P_KEY, evalkey)
            intent.putExtra(LessonComActivity.KEY, evalReport.questionKey)
            intent.putExtra(LessonComActivity.IS_COMMENT, evalReport.isComment)
            intent.putExtra(LessonComActivity.TYPE, "8")
            startActivityForResult(intent, 999)
        }

        tv_answer.setOnClickListener {
            if (answerType == "1") {
                if (eval2this) {
                    finish()
                } else {
                    presenter.getReport(paperkey, userpractisekey) {
                        startActivity<EvalCardActivity>(
                            EvalCardActivity.KEY to paperkey,
                            EvalCardActivity.NAME to paperName,
                            EvalCardActivity.EVAL_STATE to "1",
                            EvalCardActivity.DATA to it,
                            EvalCardActivity.CAT_KEY to evalkey,
                            EvalCardActivity.REPORT2THIS to true
                        )
                    }
                }
            } else {
                presenter.getReport(paperkey, userpractisekey) {
                    startActivity<ReportActivity>(
                        ReportActivity.DATA to it,
                        ReportActivity.TIME to "",
                        ReportActivity.KEY to paperkey,
                        ReportActivity.NAME to paperName,
                        ReportActivity.EX_TYPE to 2,
                        ReportActivity.TYPE to "10",
                        ReportActivity.CAT_KEY to evalkey,
                        ReportActivity.PRA_KEY to "",
                        ReportActivity.SP_Q_KEY to "",
                        ReportActivity.SP_G_NAME to "",
                        ReportActivity.IS_HIDE_BOTTOM to true,
                        ReportActivity.EVAL_STATE to "0"
                    )
                }
            }
        }

        img_ranking.setOnClickListener {
            val item = BookRes()
            item.field1 = evalkey
            item.userPracticeKey = userpractisekey
            item.id = paperkey
            startActivity<PracticeRankActivity>(PracticeRankActivity.BOOK_RES to item)
        }
    }


    override fun onPause() {
        super.onPause()
        mWaveHelper!!.cancel()
    }

    override fun onResume() {
        super.onResume()
        mWaveHelper!!.start()
        if (isFresh) {
            presenter.evalReport(evalkey, paperkey, userpractisekey) { report ->
                val recommedList = mutableListOf<RecommedBean>()
                //1:bookList;2: netList;3: paperList;4:infomationList
                report.bookList?.forEach { bookList ->
                    val bean = RecommedBean()
                    bean.key = bookList.key.toString()
                    bean.name = bookList.title.toString()
                    bean.type = "1"
                    recommedList.add(bean)
                }
                report.netList?.forEach { lesson ->
                    val bean = RecommedBean()
                    bean.key = lesson.key.toString()
                    bean.name = lesson.title.toString()
                    bean.type = "2"
                    recommedList.add(bean)
                }
                report.paperList?.forEach { paperBean ->
                    val bean = RecommedBean()
                    bean.key = paperBean.key.toString()
                    bean.name = paperBean.name.toString()
                    bean.type = "3"
                    bean.isFinish = paperBean.isFinish
                    bean.userPracticeKey = paperBean.userPracticeKey
                    recommedList.add(bean)
                }
                report.infomationList?.forEach { infomationBean ->
                    val bean = RecommedBean()
                    bean.key = infomationBean.clssifyKey.toString()
                    bean.name = infomationBean.clssifyName.toString()
                    bean.recommendKey = infomationBean.evalRecommendKey.toString()
                    bean.type = "4"
                    recommedList.add(bean)
                }
                report.appletList.forEach { applet ->
                    val bean = RecommedBean()
                    bean.key = applet.key
                    bean.name = applet.name
                    bean.type = "5"
                    recommedList.add(bean)
                }
                if (recommedList.isEmpty()) {
                    rl_recommend.visibility = View.GONE
                } else {
                    rl_recommend.visibility = View.VISIBLE
                    recommedAdapter.setData(recommedList)
                }
            }
        }
        isFresh = false
    }

    companion object {
        const val EVALKEY = "evalkey"
        const val PAPERKEY = "paperkey"
        const val USERPRACTISEKEY = "userpractisekey"
        const val NEED_UP_LOAD = "needUpLoad"
        const val PAPER_NAME = "paperName"
        const val ANSWER_TYPE = "answerType"
        const val SP_Q_KEY = "spQKey"
        const val EVAL2THIS = "EVAL2THIS"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
            evalReport.isComment = data!!.getStringExtra("isComment").toString()
        }
    }
}

private class SelfAdapter(var imgClick: (pos: Int) -> Unit) : BaseAdapter<String>() {
    private var currentView: View? = null
    override fun convert(holder: ViewHolder, item: String) {
        holder.setImageUrl(R.id.iv_img, item)
    }

    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
        frameLayout {
            lparams(dip(100), dip(100))
            imageView {
                id = R.id.iv_img
                scaleType = ImageView.ScaleType.CENTER
            }.lparams {
                width = matchParent
                height = matchParent
                topMargin = dip(10)
            }
        }
    }
}

private class AnswerAdapter(var imgClick: (pos: Int, item: CorrectBean) -> Unit) : BaseAdapter<CorrectBean>() {
    private var currentView: View? = null
    override fun convert(holder: ViewHolder, item: CorrectBean) {
        holder.setImageUrl(R.id.iv_img, item.thumbnailUrl).setOnClickListener(R.id.iv_img) {
            imgClick(holder.adapterPosition, item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
        frameLayout {
            lparams(dip(100), dip(100))
            imageView {
                id = R.id.iv_img
                scaleType = ImageView.ScaleType.CENTER
            }.lparams {
                width = matchParent
                height = matchParent
                topMargin = dip(10)
            }
        }
    }
}

private class RecommedAdapter(var itemClick: (item: RecommedBean) -> Unit) : BaseAdapter<RecommedBean>() {
    private var currentView: View? = null
    override fun convert(holder: ViewHolder, item: RecommedBean) {
        val context = holder.itemView.context
        if ("5" == item.type) {
            //当是小程序时展示图标设置drablepadding
            holder.setVisible(R.id.img_mini, View.VISIBLE)
        } else {
            holder.setVisible(R.id.img_mini, View.GONE)
        }

        holder.setText(R.id.tv_recomment_title, item.name)
        holder.itemView.setOnClickListener {
            itemClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_recommend_report, parent, false)
    }
}
