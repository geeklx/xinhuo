package tuoyan.com.xinghuo_dayingindex.ui.book

import android.os.Bundle
import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.fragment_sentence.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.LrcDetail

class SentenceFragment : LifeV4Fragment<BasePresenter>() {
    override val layoutResId: Int
        get() = R.layout.fragment_sentence
    override val presenter: BasePresenter
        get() = BasePresenter(this)

    private val parentActivity by lazy { requireActivity() as SentenceListenActivity }
    private val lrcDetail by lazy { arguments?.getSerializable(LRC_DATA) as? LrcDetail }

    companion object {
        const val LRC_DATA = "LRC_DATA"
        const val POSITION = "POSITION"

        @JvmStatic
        fun newInstance(lrcRow: LrcDetail) =
            SentenceFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(LRC_DATA, lrcRow)
                }
            }
    }

    override fun configView(view: View?) {
        super.configView(view)
        lrcDetail?.let { detail ->
            tv_num.text = Html.fromHtml("<font color='#008aff'><big>${detail.sort}</big></font><small>/${parentActivity.lrcData?.list?.size ?: 0}</small>")
            img_collect.isSelected = "1" == detail.isSign
            tv_en.text = detail.enContent
            tv_ch.text = detail.cnContent
        }
    }

    override fun initData() {
        super.initData()
    }

    override fun handleEvent() {
        super.handleEvent()
        img_collect.setOnClickListener {
            lrcDetail?.let { detail ->
                img_collect.isSelected = !img_collect.isSelected
                val map = mutableMapOf<String, String>()
                map["lrcKey"] = detail.lrcKey ?: ""
                map["lrcDetailKey"] = detail.lrcDetailKey ?: ""
                map["isSign"] = if (img_collect.isSelected) "1" else "0"
                presenter.recordLrc(map) {}
                parentActivity.setSignNum(if (img_collect.isSelected) 1 else -1)
                detail.isSign = if (img_collect.isSelected) "1" else "0"
            }
        }
        img_play.setOnClickListener {
            lrcDetail?.let { lrc -> parentActivity.playCurrentPage(lrc) }
        }
    }

    fun setImgStateAndPlay(state: Boolean) {
        setImgState(state)
        if (state) {
            lrcDetail?.let { lrc -> parentActivity.seekToCurrentPage(lrc) }
        }
    }

    fun setImgState(state: Boolean) {
        img_play.isSelected = state
    }

    fun showChAndEn(isShow: Boolean) {
        tv_ch.visibility = if (!isShow) View.VISIBLE else View.GONE
    }
}