package tuoyan.com.xinghuo_dayingindex.ui.mine.collection

import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_words.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.WordCatalog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.home.word.adapter.ScanWordsAdapter
import tuoyan.com.xinghuo_dayingindex.ui.home.word.words.ScanWordListActivity

class WordsFragment : LifeV4Fragment<CollectionPresenter>() {
    override val presenter = CollectionPresenter(this)
    override val layoutResId = R.layout.fragment_words

    private var dDialog: DDialog? = null
    private val adapter by lazy {
        ScanWordsAdapter { type, item ->
            when (type) {
                1 -> {
                    startActivity<ScanWordListActivity>(
                        ScanWordListActivity.CLASSIFY_KEY to item.wordClassifyKey,
                        ScanWordListActivity.GRADE_KEY to item.gradeKey,
                        ScanWordListActivity.TITLE to item.wordClassifyName.split("(")[0]
                    )
                }
                2 -> {
                    dDialog = DDialog(this.requireContext()).setWidth(190).setMessage("确定将该词表取消收藏吗？")
                        .setNegativeButton("取消收藏") {
                            dDialog?.dismiss()
                            delCollect(item)
                        }.setPositiveButton("考虑一下") {
                            dDialog?.dismiss()
                        }
                    dDialog?.show()
                }
            }
        }
    }

    override fun configView(view: View?) {
        super.configView(view)
        rlv.layoutManager = LinearLayoutManager(this.requireContext())
        rlv.adapter = adapter
        adapter.from = "2"
    }

    override fun initData() {
        super.initData()
        presenter.collectedClassifyList {
            adapter.setData(it.list)
        }
    }

    override fun handleEvent() {
        super.handleEvent()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WordsFragment().apply {
                arguments = Bundle().apply {
                }
            }

        private const val WORD = "word"
    }

    private fun delCollect(item: WordCatalog) {
        presenter.delCollection(item.wordClassifyKey) {
            initData()
        }
    }
}