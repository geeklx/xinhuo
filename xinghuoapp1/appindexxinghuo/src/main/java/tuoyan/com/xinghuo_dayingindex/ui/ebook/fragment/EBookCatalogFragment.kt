package tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_e_book_catalog.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeFullActivity
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.EBookCatalog
import tuoyan.com.xinghuo_dayingindex.bean.EBookParam
import tuoyan.com.xinghuo_dayingindex.bean.VideoParam
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.*
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.CatalogAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.CatalogListenAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.CatalogWordAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.listen.EBookJianReadActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordExerciseActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordImgActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EBookWordPracticeActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity

private const val TYPE = "type"//1智能书模考 2简听力 3词汇星火式巧记速记
private const val KEY = "key"
private const val ISOWN = "isOwn"

class EBookCatalogFragment : LifeV4Fragment<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_e_book_catalog

    private val key by lazy { arguments?.getString(KEY) ?: "" }
    private val type by lazy { arguments?.getString(TYPE) ?: "1" }
    private val activity by lazy { requireActivity() as EBookDetailActivity }
    private var dDialog: DDialog? = null
    private var time = 1
    private val adapter by lazy {
        CatalogAdapter { type, item ->
            goDetail(type, item)
        }
    }

    private val catalogListenAdapter by lazy {
        CatalogListenAdapter { type, item ->
            toCatalogDetail(type, item)
        }
    }

    private val wordAdapter by lazy {
        CatalogWordAdapter(true) { type, item ->
            isLogin {
                when (type) {
                    1 -> {//阅读
                        activity.reFresh = true
                        val params = EBookParam()
                        params.name = activity.bookDetail?.name ?: ""
                        params.catalogName = item.name
                        params.bookKey = activity.key
                        params.catalogKey = item.catalogKey
                        startActivity<EBookWordImgActivity>(EBookLifeFullActivity.EBOOK_PARAM to params, EBookWordImgActivity.IS_OWN to isOwn)
                    }
                    2 -> {//练习
                        activity.reFresh = true
                        val params = EBookParam()
                        params.name = activity.bookDetail?.name ?: ""
                        params.catalogName = item.name
                        params.bookKey = activity.key
                        params.catalogKey = item.catalogKey
                        startActivity<EBookWordPracticeActivity>(EBookLifeFullActivity.EBOOK_PARAM to params)
                    }
                    3 -> {//购买
                        activity.goBuy()
                    }
                    4 -> {//章节测试
                        activity.reFresh = true
                        val params = EBookParam()
                        params.name = activity.bookDetail?.name ?: ""
                        params.catalogName = item.name
                        params.bookKey = activity.key
                        params.catalogKey = item.catalogKey
                        startActivity<EBookWordExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params)
                    }
                }
            }
        }
    }

    private var isOwn = "0"

    companion object {
        @JvmStatic
        fun newInstance(key: String, isOwn: String, type: String) =
            EBookCatalogFragment().apply {
                arguments = Bundle().apply {
                    putString(TYPE, type)
                    putString(KEY, key)
                    putString(ISOWN, isOwn)
                }
            }
    }

    override fun configView(view: View?) {
        super.configView(view)
        isOwn = arguments?.getString(ISOWN) ?: "0"
        rlv_c.layoutManager = LinearLayoutManager(this.requireContext())
        when (type) {
            "1" -> {
                rlv_c.adapter = adapter
                adapter.isOwn = isOwn
            }
            "2" -> {
                rlv_c.adapter = catalogListenAdapter
                catalogListenAdapter.isOwn = isOwn
            }
            "3" -> {
                rlv_c.adapter = wordAdapter
                wordAdapter.isOwn = isOwn
            }
        }
    }

    override fun initData() {
        super.initData()
        presenter.getSmartBookCatalogList(key, type) { list ->
            when (type) {
                "1" -> adapter.setData(list)
                "2" -> {
                    catalogListenAdapter.setData(formatCatalogList(list))
                }
                "3" -> {
                    wordAdapter.setData(formatCatalogList(list))
                }
            }
            time++
        }
    }

    override fun handleEvent() {
        super.handleEvent()
    }

    fun formatCatalogList(list: List<EBookCatalog>): List<EBookCatalog> {
        val dataList = mutableListOf<EBookCatalog>()
        list.forEach { pCatalog ->
            pCatalog.type = "1"
            if (time > 1) {
                pCatalog.showType = if (pCatalog.lastTime.isNotBlank()) "1" else "0"
            }
            dataList.add(pCatalog)
            pCatalog.catalogList.forEachIndexed { index, cCatalog ->
                cCatalog.type = "2"
                cCatalog.parentKey = pCatalog.catalogKey
                cCatalog.showType = pCatalog.showType
                dataList.add(cCatalog)
            }
            dataList.last().lineType = "1"
        }
        return dataList
    }

    fun freshData(isOwn: String) {
        this.isOwn = isOwn
        presenter.getSmartBookCatalogList(key, type) { list ->
            when (type) {
                "1" -> {
                    adapter.isOwn = isOwn
                    adapter.setData(list)
                }
                "2" -> {
                    catalogListenAdapter.isOwn = isOwn
                    catalogListenAdapter.setData(formatCatalogList(list))
                }
                "3" -> {
                    wordAdapter.isOwn = isOwn
                    wordAdapter.setData(formatCatalogList(list))
                }
            }
        }
    }

    private fun toCatalogDetail(type: Int, item: EBookCatalog) {
        isLogin {
            when (type) {
                1 -> {
                    toCatalogDetail(item)
                }
                3 -> {
                    activity.goBuy()
                }
            }
        }
    }

    private fun toCatalogDetail(item: EBookCatalog) {
        activity.reFresh = true
        //1:试卷(模考) 2:试卷(简听力)进阶篇 3:练习（）基础篇 4:电子书 5:视频
        when (item.resourceType) {
            "2" -> {
                val params = EBookParam()
                params.name = activity.bookDetail?.name ?: ""
                params.catalogName = item.name
                params.bookKey = key
                params.catalogKey = item.catalogKey
                params.resourceKey = item.resourceKey
                params.userpractisekey = item.userPracticeKey
                if ((item.userPracticeKey.isNullOrEmpty() || item.lastTime.isNullOrEmpty()) && !item.resourceKey.isNullOrEmpty()) {
                    startActivity<EBookListenExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookListenExerciseActivity.TYPE to "5")
                } else if (!item.resourceKey.isNullOrEmpty()) {
                    dDialog = DDialog(this.requireContext()).setMessage("上次答题时间<br><font color='#15d25f'><b>${item.lastTime}</b></font>")
                        .setNegativeButton("重新答题") {
                            dDialog?.dismiss()
                            params.userpractisekey = ""
                            startActivity<EBookListenExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookListenExerciseActivity.TYPE to "5")
                        }.setPositiveButton("继续答题") {
                            dDialog?.dismiss()
                            if ("1" == item.isFinished) {
                                getReport(params, "7")
                            } else {
                                startActivity<EBookListenExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookListenExerciseActivity.TYPE to "5")
                            }
                        }
                    dDialog?.show()
                } else {
                    Toast.makeText(this.requireContext(), "暂无内容", Toast.LENGTH_LONG).show()
                }
            }
            "3" -> {
                val params = EBookParam()
                params.name = activity.bookDetail?.name ?: ""
                params.catalogName = item.name
                params.bookKey = activity.key
                params.catalogKey = item.catalogKey
                params.resourceKey = item.resourceKey
                params.userpractisekey = item.userPracticeKey
                if ((item.userPracticeKey.isNullOrEmpty() || item.lastTime.isNullOrEmpty()) && !item.resourceKey.isNullOrEmpty()) {
                    startActivity<EBookListenExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookListenExerciseActivity.TYPE to "4")
                } else if (!item.resourceKey.isNullOrEmpty()) {
                    dDialog = DDialog(this.requireContext()).setMessage("上次答题时间<br><font color='#15d25f'><b>${item.lastTime}</b></font>")
                        .setNegativeButton("重新答题") {
                            dDialog?.dismiss()
                            params.userpractisekey = ""
                            startActivity<EBookListenExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookListenExerciseActivity.TYPE to "4")
                        }.setPositiveButton("继续答题") {
                            dDialog?.dismiss()
                            startActivity<EBookListenExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookListenExerciseActivity.TYPE to "4")
                        }
                    dDialog?.show()
                } else {
                    Toast.makeText(this.requireContext(), "暂无内容", Toast.LENGTH_LONG).show()
                }
            }
            "4" -> {
                val params = EBookParam()
                params.name = activity.bookDetail?.name ?: ""
                params.catalogName = item.name
                params.bookKey = activity.key
                params.catalogKey = item.catalogKey
                params.resourceKey = item.smarkResourceKey
                startActivity<EBookJianReadActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookJianReadActivity.IS_OWN to isOwn)
            }
            "5" -> {
                val videoParam = VideoParam()
                videoParam.key = item.smarkResourceKey
                videoParam.type = "6"
                val params = EBookParam()
                params.name = activity.bookDetail?.name ?: ""
                params.catalogName = item.name
                params.bookKey = activity.key
                params.catalogKey = item.catalogKey
                params.resourceKey = item.smarkResourceKey
                startActivity<VideoActivity>(VideoActivity.VIDEO_PARAM to videoParam, EBookLifeActivity.EBOOK_PARAM to params)
            }
        }
    }

    private fun goDetail(type: Int, item: EBookCatalog) {
        isLogin {
            when (type) {
                //模考
                1 -> {
                    activity.saClick("点击模考")
                    val params = EBookParam()
                    params.name = activity.bookDetail?.name ?: ""
                    params.catalogName = item.name
                    params.bookKey = activity.key
                    params.catalogKey = item.catalogKey
                    params.resourceKey = item.resourceKey
                    params.userpractisekey = item.userPracticeKey
                    if ((item.userPracticeKey.isNullOrEmpty() || item.lastTime.isNullOrEmpty()) && !item.resourceKey.isNullOrEmpty()) {
                        activity.reFresh = true
                        startActivity<EBookExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params)
                    } else if (!item.resourceKey.isNullOrEmpty()) {
                        dDialog = DDialog(this.requireContext()).setMessage("上次答题时间<br><font color='#15d25f'><b>${item.lastTime}</b></font>")
                            .setNegativeButton("重新作答") {
                                dDialog?.dismiss()
                                activity.reFresh = true
                                params.userpractisekey = ""
                                startActivity<EBookExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params)
                            }.setPositiveButton("继续作答") {
                                dDialog?.dismiss()
                                if ("1" == item.isFinished) {
                                    getReport(params, "1")
                                } else {
                                    activity.reFresh = true
                                    startActivity<EBookExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params)
                                }
                            }
                        dDialog?.show()
                    } else {
                        Toast.makeText(this.requireContext(), "暂无内容", Toast.LENGTH_LONG).show()
                    }
                }
                //精练
                2 -> {
                    if (item.smarkResourceKey.isNullOrEmpty()) {
                        Toast.makeText(this.requireContext(), "暂无内容", Toast.LENGTH_LONG).show()
                    } else {
                        activity.saClick("点击精练")
                        activity.reFresh = true
                        val params = EBookParam()
                        params.bookKey = activity.key
                        params.catalogKey = item.catalogKey
                        params.resourceKey = item.resourceKey
                        params.smarkResourceKey = item.smarkResourceKey
                        params.isOwn = isOwn
                        params.name = item.name
                        startActivity<EBookPracticeActivity>(EBookLifeActivity.EBOOK_PARAM to params)
                    }
                }
                3 -> {
                    activity.goBuy()
                }
            }
        }
    }

    private fun getReport(params: EBookParam, type: String) {
        presenter.getReport(params.resourceKey, params.userpractisekey, params.catalogKey) {
            startActivity<EBookReportActivity>(EBookReportActivity.DATA to it, EBookLifeActivity.EBOOK_PARAM to params, EBookReportActivity.TYPE to type)
        }
    }
}