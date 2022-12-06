package tuoyan.com.xinghuo_dayingindex.ui.mine.offline
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_e_book_down_load.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.EBookParam
import tuoyan.com.xinghuo_dayingindex.realm.bean.Group
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.EbookCatalogListActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import tuoyan.com.xinghuo_dayingindex.utlis.NetWorkUtils

class EBookDownLoadFragment : LifeV4Fragment<BasePresenter>() {
    override val layoutResId: Int
        get() = R.layout.fragment_e_book_down_load
    override val presenter: BasePresenter
        get() = BasePresenter(this)

    private var dDialog: DDialog? = null
    private val adapter by lazy {
        EBookDownAdapter { type, group ->
            when (type) {
                1 -> {
                    if (NetWorkUtils.isNetWorkReachable()) {
                        presenter.isValidOfNetcourse(group.key) {
                            if (it) {
                                goImgDetail(group)
                            } else {
                                dDialog = DDialog(this.requireContext()).setMessage("该资源已失效，是否删除该资源?")
                                    .setNegativeButton("确定") {
                                        dDialog?.dismiss()
                                        delete(group)
                                    }.setPositiveButton("考虑一下") {
                                        dDialog?.dismiss()
                                    }
                                dDialog?.show()
                            }
                        }
                    } else {
                        goImgDetail(group)
                    }
                }
                2 -> {
                    dDialog = DDialog(this.requireContext()).setMessage("确定要删除该资源吗?")
                        .setNegativeButton("确定") {
                            dDialog?.dismiss()
                            delete(group)
                        }.setPositiveButton("考虑一下") {
                            dDialog?.dismiss()
                        }
                    dDialog?.show()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EBookDownLoadFragment()
    }

    override fun configView(view: View?) {
        super.configView(view)
        rlv_res.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_res.adapter = adapter
    }

    override fun initData() {
        super.initData()
        val list = DownloadManager.getEBookWord()
        adapter.setData(list)
    }

    override fun handleEvent() {
        super.handleEvent()
    }

    private fun delete(group: Group) {
        adapter.remove(group)
        val filePath = "${DownloadManager.downloadPathEN}/${group.key}"
        val zipPath = "${DownloadManager.downloadPathEN}/${group.key}.zip"
        DownloadManager.deleteEBookWord(group.key)
        Thread {
            FileUtils.deleteDir(filePath)
            FileUtils.deleteDir(zipPath)
        }.start()
    }

    private fun goImgDetail(group: Group) {
        val bookParam = EBookParam()
        bookParam.bookKey = group.key
        bookParam.name = group.name
        startActivity<EbookCatalogListActivity>(EbookCatalogListActivity.EBOOK_PARAM to bookParam)
    }
}