package tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_e_book_catalog.rlv_c
import kotlinx.android.synthetic.main.fragment_e_book_read_catalog.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.base.LifeV4FullFragment
import tuoyan.com.xinghuo_dayingindex.bean.EBookCatalog
import tuoyan.com.xinghuo_dayingindex.bean.EBookParam
import tuoyan.com.xinghuo_dayingindex.bean.VideoParam
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookListenExerciseActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookReportActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.CatalogListenAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.listen.EBookJianReadActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity

private const val KEY = "key"
private const val ISOWN = "isOwn"

class EBookReadCatalogFragment : LifeV4FullFragment<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_e_book_read_catalog

    private val activity by lazy { this.requireActivity() as EBookJianReadActivity }

    private val key by lazy { arguments?.getString(KEY) ?: "" }

    private var dDialog: DDialog? = null

    private val catalogListenAdapter by lazy {
        CatalogListenAdapter { type, item ->
            toCatalogDetail(type, item)
        }
    }
    private var isOwn = "0"

    companion object {
        @JvmStatic
        fun newInstance(key: String, isOwn: String) =
            EBookReadCatalogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY, key)
                    putString(ISOWN, isOwn)
                }
            }
    }

    override fun configView(view: View?) {
        super.configView(view)
        isOwn = arguments?.getString(ISOWN) ?: "0"
        rlv_c.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_c.adapter = catalogListenAdapter
        catalogListenAdapter.setHeader(false)
        catalogListenAdapter.catalogType = "1"
        catalogListenAdapter.isOwn = isOwn
    }

    override fun initData() {
        super.initData()
        presenter.getSmartBookCatalogList(key, "2") { list ->
            catalogListenAdapter.setData(formatCatalogList(list))
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
        //1:试卷(模考) 2:试卷(简听力) 3:练习 4:电子书 5:视频
        when (item.resourceType) {
            "2" -> {
                activity.refresh = true
                val params = EBookParam()
                params.catalogName = item.name
                params.bookKey = key
                params.catalogKey = item.catalogKey
                params.resourceKey = item.resourceKey
                params.userpractisekey = item.userPracticeKey
                if ((item.userPracticeKey.isNullOrEmpty() || item.lastTime.isNullOrEmpty()) && !item.resourceKey.isNullOrEmpty()) {
                    startActivity<EBookListenExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookListenExerciseActivity.TYPE to "5")
                    activity.mFinish()
                } else if (!item.resourceKey.isNullOrEmpty()) {
                    dDialog = DDialog(this.requireContext()).setMessage("上次答题时间<br><font color='#15d25f'><b>${item.lastTime}</b></font>")
                        .setNegativeButton("重新答题") {
                            dDialog?.dismiss()
                            params.userpractisekey = ""
                            startActivity<EBookListenExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookListenExerciseActivity.TYPE to "5")
                            activity.mFinish()
                        }.setPositiveButton("继续答题") {
                            dDialog?.dismiss()
                            if ("1" == item.isFinished) {
                                getReport(params, "7")
                            } else {
                                startActivity<EBookListenExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookListenExerciseActivity.TYPE to "5")
                                activity.mFinish()
                            }
                        }
                    dDialog?.show()
                } else {
                    Toast.makeText(this.requireContext(), "暂无内容", Toast.LENGTH_LONG).show()
                }
            }
            "3" -> {
                activity.refresh = true
                val params = EBookParam()
                params.catalogName = item.name
                params.bookKey = key
                params.catalogKey = item.catalogKey
                params.resourceKey = item.resourceKey
                params.userpractisekey = item.userPracticeKey
                if ((item.userPracticeKey.isNullOrEmpty() || item.lastTime.isNullOrEmpty()) && !item.resourceKey.isNullOrEmpty()) {
                    startActivity<EBookListenExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookListenExerciseActivity.TYPE to "4")
                    activity.mFinish()
                } else if (!item.resourceKey.isNullOrEmpty()) {
                    dDialog = DDialog(this.requireContext()).setMessage("上次答题时间<br><font color='#15d25f'><b>${item.lastTime}</b></font>")
                        .setNegativeButton("重新答题") {
                            dDialog?.dismiss()
                            params.userpractisekey = ""
                            startActivity<EBookListenExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookListenExerciseActivity.TYPE to "4")
                            activity.mFinish()
                        }.setPositiveButton("继续答题") {
                            dDialog?.dismiss()
                            startActivity<EBookListenExerciseActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookListenExerciseActivity.TYPE to "4")
                            activity.mFinish()
                        }
                    dDialog?.show()
                } else {
                    Toast.makeText(this.requireContext(), "暂无内容", Toast.LENGTH_LONG).show()
                }
            }
            "4" -> {
                val params = EBookParam()
                params.catalogName = item.name
                params.bookKey = key
                params.catalogKey = item.catalogKey
                params.resourceKey = item.smarkResourceKey
                startActivity<EBookJianReadActivity>(EBookLifeActivity.EBOOK_PARAM to params, EBookJianReadActivity.IS_OWN to isOwn)
                activity.mFinish()
            }
            "5" -> {
                val videoParam = VideoParam()
                videoParam.key = item.smarkResourceKey
                videoParam.type = "6"
                val params = EBookParam()
                params.catalogName = item.name
                params.bookKey = key
                params.catalogKey = item.catalogKey
                params.resourceKey = item.smarkResourceKey
                startActivity<VideoActivity>(VideoActivity.VIDEO_PARAM to videoParam, EBookLifeActivity.EBOOK_PARAM to params)
                activity.mFinish()
            }
        }
    }

    private fun formatCatalogList(list: List<EBookCatalog>): List<EBookCatalog> {
        val dataList = mutableListOf<EBookCatalog>()
        list.forEach { pCatalog ->
            pCatalog.type = "1"
            dataList.add(pCatalog)
            pCatalog.catalogList.forEach { cCatalog ->
                cCatalog.type = "2"
                cCatalog.parentKey = pCatalog.catalogKey
                dataList.add(cCatalog)
            }
        }
        return dataList
    }

    override fun handleEvent() {
        super.handleEvent()
        fragment.setOnClickListener {
            activity.catalogClick()
        }
    }

    private fun getReport(params: EBookParam, type: String) {
        presenter.getReport(params.resourceKey, params.userpractisekey, params.catalogKey) {
            startActivity<EBookReportActivity>(EBookReportActivity.DATA to it, EBookLifeActivity.EBOOK_PARAM to params, EBookReportActivity.TYPE to type)
            activity.mFinish()
        }
    }
}