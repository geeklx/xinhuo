package tuoyan.com.xinghuo_dayingindex.ui.ebook

import android.text.Html
import kotlinx.android.synthetic.main.activity_ebook_data.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeFullActivity
import tuoyan.com.xinghuo_dayingindex.bean.EBookParam

//学习数据分为三个接口：smartBook/learnStatistics，get，参数key；
//smartBook/learnDurationDetail，get,参数key ，type(0:按天，1：按月)；
//smartBook/learnAccuracyDetail，get,参数key ，type(0:按天，1：按月)
class EBookDataActivity : LifeFullActivity<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_ebook_data

    private val bookParam by lazy { intent.getSerializableExtra(EBOOK_PARAM) as? EBookParam }
    private val type by lazy { intent.getStringExtra(TYPE) ?: "1" }//1智能书模考 2简听力 3词汇星火式巧记速记

    override fun configView() {
        super.configView()
        when (type) {
            "1" -> {
            }
            "2" -> {
                tv_rate_name.text = "进阶特训正确率"
                tv_right_rate.text = "进阶特训正确率"
            }
            "3" -> {
                tv_rate_name.text = "章节测试正确率"
                tv_right_rate.text = "章节测试正确率"
            }
        }
    }

    override fun initData() {
        super.initData()
        presenter.getLearnStatistics(bookParam?.bookKey ?: "") {
            tv_today.text = it.curDate
            tv_time.text = Html.fromHtml(it.curDuration.replace("小时", "<small> 小时 </small>").replace("分钟", "<small> 分钟</small>"))
            tv_rate.text = Html.fromHtml("${it.curAccuracy}<small>%</small>")
        }
        getLearnDurationDetail("0")
        getLearnAccuracyDetail("0")
    }


    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener { onBackPressed() }
        rg_time.setOnCheckedChangeListener { radioGroup, id ->
            when (id) {
                R.id.rb_time_7 -> {
                    getLearnDurationDetail("0")
                }
                R.id.rb_time_30 -> {
                    getLearnDurationDetail("1")
                }
            }
        }
        rg_rate.setOnCheckedChangeListener { radioGroup, id ->
            when (id) {
                R.id.rb_rate_7 -> {
                    getLearnAccuracyDetail("0")
                }
                R.id.rb_rate_30 -> {
                    getLearnAccuracyDetail("1")
                }
            }
        }
    }

    companion object {
        const val EBOOK_PARAM = "reportData"
        const val TYPE = "type"
    }

    fun getLearnDurationDetail(type: String) {
        presenter.getLearnDurationDetail(bookParam?.bookKey ?: "", type) { datas ->
            val xlist = mutableListOf<String>()
            val ylist = mutableListOf<Float>()
            datas.forEach { data ->
                xlist.add(data.name)
                ylist.add(data.value / 3600)
            }
            dlv_line.setData(xlist, ylist)
            dlv_line.invalidate()
        }
    }

    fun getLearnAccuracyDetail(type: String) {
        presenter.getLearnAccuracyDetail(bookParam?.bookKey ?: "", type) { datas ->
            val xlist = mutableListOf<String>()
            val ylist = mutableListOf<Float>()
            datas.forEach { data ->
                xlist.add(data.name)
                ylist.add(data.value)
            }
            dlv_rate.setData(xlist, ylist, "1")
            dlv_rate.invalidate()
        }
    }
}