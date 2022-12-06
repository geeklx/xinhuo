package tuoyan.com.xinghuo_dayingindex.ui.mine.collection

import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.ctx
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.Audio
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean
import tuoyan.com.xinghuo_dayingindex.ui.dialog.AlertDialog
import tuoyan.com.xinghuo_dayingindex.ui.video.AudioActivity

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [QuestionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AudioFragment : LifeV4Fragment<CollectionPresenter>() {
    override val presenter = CollectionPresenter(this)
    override val layoutResId = 0
    private var page = 0
    private val adapter by lazy {
        AudioAdapter({
            more()
        }, {
            // TODO: 2018/11/2 11:19  音频
            val bookRes = BookRes()
            bookRes.id = it.key ?: ""
            bookRes.type = "7"
            bookRes.playUrl = it.palyUrl ?: ""
            bookRes.name = it.name ?: ""
            bookRes.downloadFlag = it.downloadFlag ?: ""
            bookRes.lrcurl = it.lrcUrl ?: ""
            bookRes.lrcurl2 = it.lrcUrl2 ?: ""
            bookRes.lrcurl3 = it.lrcUrl3 ?: ""
            bookRes.downUrl = it.downloadUrl ?: ""
            bookRes.isCollection = "1"
//            MyApp.instance.bookres = mutableListOf(bookRes)
            // 存
            val list1: List<ResourceListBean> = ArrayList()
            val gson1 = Gson()
            val data1 = gson1.toJson(mutableListOf(bookRes))
            SPUtils.getInstance().put("BookRes", data1)
            startActivity<AudioActivity>()
        }) {
            // TODO: 2018/11/2 11:21  删除
            deleted(it)
        }
    }

    private fun deleted(audio: Audio) {
        AlertDialog(ctx, "确认将该音频取消收藏？") {
            presenter.deleteCollection(audio.key ?: "") {
                adapter.remove(audio)
            }
        }.show()

    }

    private fun more() {
        page++
        presenter.collectionAvdio(page) {
            adapter.addData(it.body)
            adapter.setMore(adapter.getDateCount() < it.totalCount)
        }
    }

    override fun initView() = UI {
        recyclerView {
            layoutManager = LinearLayoutManager(context)
            val decoration = DividerItemDecoration(ctx, LinearLayout.VERTICAL)
            decoration.setDrawable(resources.getDrawable(R.drawable.divider))
            addItemDecoration(decoration)
            adapter = this@AudioFragment.adapter
            leftPadding = dip(15)
        }
    }.view


    override fun onResume() {
        page = 0
        presenter.collectionAvdio(page) {
            adapter.setData(it.body)
            adapter.setMore(adapter.getDateCount() < it.totalCount)
        }
        super.onResume()
    }

    override fun handleEvent() {


    }


    companion object {
        @JvmStatic
        fun newInstance() =
            AudioFragment().apply {
                arguments = Bundle().apply {
                }
            }

        private const val WORD = "word"

    }
}

class AudioAdapter(
    val more: () -> Unit, val click: (Audio) -> Unit,
    val longClick: (Audio) -> Unit
) : BaseAdapter<Audio>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: Audio) {
        holder.setText(R.id.tv_title, item.name ?: "")
            .setText(R.id.tv_time, item.size ?: "")
            .itemView.setOnClickListener {
                click(item)
            }
        holder.itemView.setOnLongClickListener {
            longClick(item)
            true
        }
    }

    override fun loadMore(holder: ViewHolder) {
        more()

    }

    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
        linearLayout {
            lparams(matchParent, wrapContent)
            rightPadding = dip(15)
            verticalPadding = dip(15)
            imageView(R.drawable.ic_music) {
                id = R.id.iv_icon
            }.lparams(dip(15), dip(15))
            space().lparams(dip(15), wrapContent)
            verticalLayout {
                textView {
                    id = R.id.tv_title
                    textSize = 14f
                    textColor = resources.getColor(R.color.color_222831)
                    typeface = Typeface.DEFAULT_BOLD
                }
                space().lparams(wrapContent, dip(5))
                textView {
                    id = R.id.tv_time
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_duration, 0, 0, 0)
                    compoundDrawablePadding = dip(5)
                    textSize = 11f
                    textColor = resources.getColor(R.color.color_8d95a1)
                }
            }.lparams(matchParent, wrapContent)

        }
    }

    override fun emptyImageRes(): Int {
        return R.drawable.ic_emtry_collection
    }

    override fun emptyText(): String {
        return "暂无音频哦"
    }
}