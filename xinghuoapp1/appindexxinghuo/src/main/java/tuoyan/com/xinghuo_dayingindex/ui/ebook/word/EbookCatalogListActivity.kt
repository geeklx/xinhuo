package tuoyan.com.xinghuo_dayingindex.ui.ebook.word
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_ebook_catalog_list.*
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeFullActivity
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.EBookCatalog
import tuoyan.com.xinghuo_dayingindex.bean.EBookParam
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.CatalogWordAdapter
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import java.io.File

class EbookCatalogListActivity : LifeActivity<EBookPresenter>() {

    override val layoutResId: Int
        get() = R.layout.activity_ebook_catalog_list
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)

    private val bookParam by lazy { intent.getSerializableExtra(EBOOK_PARAM) as? EBookParam }

    private val wordAdapter by lazy {
        CatalogWordAdapter(true) { type, item ->
            when (type) {
                1 -> {//阅读
                    bookParam?.let { params ->
                        params.catalogName = item.name
                        params.catalogKey = item.catalogKey
                        params.from = "1"
                        startActivity<EBookWordImgActivity>(EBookLifeFullActivity.EBOOK_PARAM to params, EBookWordImgActivity.IS_OWN to "1")
                    }
                }
            }
        }
    }

    override fun configView() {
        super.configView()
        tv_title.text = bookParam?.name ?: ""
        rlv_catalog.layoutManager = LinearLayoutManager(this)
        rlv_catalog.adapter = wordAdapter
        wordAdapter.catalogType = "3"
        wordAdapter.isOwn = "1"
    }

    override fun initData() {
        super.initData()
        bookParam?.let { book ->
            val file = File("${DownloadManager.downloadPathEN}/${book.bookKey ?: ""}/smart.json")
            if (file.exists()) {
                FileUtils.readFileSB(file, "UTF-8", {
                    runOnUiThread {
                        toast("读取本地文件失败，请重新进入")
                    }
                }) { jsonStr ->
                    runOnUiThread {
                        val list: List<EBookCatalog> = Gson().fromJson(jsonStr, object : TypeToken<List<EBookCatalog>>() {}.type)
                        wordAdapter.setData(formatCatalogList(list))
                    }
                }
            } else {
                Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun formatCatalogList(list: List<EBookCatalog>): List<EBookCatalog> {
        val dataList = mutableListOf<EBookCatalog>()
        list.forEach { pCatalog ->
            val temp = EBookCatalog()
            temp.type = "1"
            temp.showType = "0"
            temp.catalogKey = pCatalog.catalogKey
            temp.name = pCatalog.name
            dataList.add(temp)
            pCatalog.catalogList.forEach { cCatalog ->
                val temp1 = EBookCatalog()
                temp1.showType = "0"
                temp1.type = "2"
                temp1.parentKey = pCatalog.catalogKey
                temp1.name = cCatalog.name
                temp1.catalogKey = cCatalog.catalogKey
                temp1.existRead = if (cCatalog.readList.isNotEmpty()) "1" else ""
                dataList.add(temp1)
            }
            dataList.last().lineType = "1"
        }
        return dataList
    }

    companion object {
        val EBOOK_PARAM = "EBOOK_PARAM"
    }
}