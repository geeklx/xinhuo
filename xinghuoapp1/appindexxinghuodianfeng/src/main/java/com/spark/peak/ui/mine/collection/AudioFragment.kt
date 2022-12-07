package com.spark.peak.ui.mine.collection


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.LifeFragment
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Audio
import com.spark.peak.bean.AudioRes
import com.spark.peak.ui.dialog.AlertDialog
import com.spark.peak.ui.video.AudioActivity
import kotlinx.android.synthetic.main.fragment_lessondf.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.startActivity

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [QuestionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AudioFragment : LifeFragment<CollectionPresenter>() {
    override val presenter = CollectionPresenter(this)
    override val layoutResId = R.layout.fragment_lessondf
    private var page = 0
    private val adapter by lazy {
        AudioAdapter({
            more()
        }, {
            // TODO: 2018/11/2 11:19 霍述雷 音频
            val bookRes = AudioRes()
            bookRes.id = it.key ?: ""
            bookRes.playUrl = it.palyUrl ?: ""
            bookRes.name = it.name ?: ""
            bookRes.downloadFlag = it.downloadFlag ?: ""
            bookRes.lrcurl = it.lrcUrl ?: ""
            bookRes.downUrl = it.downloadUrl ?: ""
            bookRes.isCollection = "1"
            bookRes.duration = it.duration
            val bookres = mutableListOf(bookRes)
//            startActivity<AudioActivity>()
//            MyApp.instance.bookres = bookres
            // 存
            val list1: List<AudioRes> = ArrayList()
            val gson1 = Gson()
            val data1 = gson1.toJson(bookres)
            SPUtils.getInstance().put("AudioRes", data1)
            startActivity<AudioActivity>(
                AudioActivity.DATA to bookres,
                AudioActivity.POSITION to 0,
                AudioActivity.TYPE to "pt",
                AudioActivity.NAME to (it.bookName ?: "我的收藏")
            )
        }) {
            // TODO: 2018/11/2 11:21 霍述雷 删除
            deleted(it)
        }
    }

    private fun deleted(audio: Audio) {
        AlertDialog(this.requireContext(), "确认将该音频取消收藏？") {
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

    override fun configView(view: View?) {
        super.configView(view)
        rlv_list.layoutManager = LinearLayoutManager(context)
        rlv_list.adapter = this@AudioFragment.adapter
    }

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
        holder.setImageResource(R.id.img_type, R.drawable.src_type_audio)
            .setText(R.id.tv_name, item.name ?: "")
//                .setText(R.id.tv_time, item.size ?: "")
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

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_scan_historydf, parent, false)
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, matchParent)
            backgroundResource = R.color.color_ffffff
            space().lparams(wrapContent, 0, 0.5f)
            textView {
                text = "暂无音频哦"
                textSize = 15f
                gravity = Gravity.CENTER
//                compoundDrawablePadding = dip(10)
                setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_emtry_collection, 0, 0)
                textColor = Color.parseColor("#666666")
            }
            space().lparams(wrapContent, 0, 1.5f)
        }
    }

}