package tuoyan.com.xinghuo_dayingindex.ui.mine.collection


import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.ctx
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.*
import tuoyan.com.xinghuo_dayingindex.bean.Paper
import tuoyan.com.xinghuo_dayingindex.ui.dialog.AlertDialog

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [QuestionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class QuestionFragment : LifeV4Fragment<CollectionPresenter>() {
    override val presenter = CollectionPresenter(this)
    override val layoutResId = 0
    private var page = 0
    private val adapter by lazy {
        QuestionAdapter({
            more()
        }, {
            // TODO: 2018/11/2 11:18  试卷目标
            ctx.startActivity<CollectQuestionActivity>(CollectQuestionActivity.ITEM to it)
        }) {
            // : 2018/11/2 11:21  删除
            deleted(it)
        }
    }

    private fun deleted(audio: Paper) {
        AlertDialog(ctx, "确认将该题取消收藏？") {
            presenter.deleteCollection(audio.key ?: "") {
                adapter.remove(audio)
            }
        }.show()

    }

    private fun more() {
        page++
        presenter.collectionPaper(page) {
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
            adapter = this@QuestionFragment.adapter
            leftPadding = dip(15)
        }
    }.view

    override fun configView(view: View?) {

        EventBus.getDefault().register(this)

    }


    override fun onResume() {
        page = 0
        presenter.collectionPaper(page) {
            adapter.setData(it.body)
            adapter.setMore(adapter.getDateCount() < it.totalCount)
        }
        super.onResume()
    }

    override fun handleEvent() {


    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun update(msg: EventMsg) {
        if (msg.action == "question")
            adapter.remove(msg.position)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            QuestionFragment().apply {
                arguments = Bundle().apply {
                }
            }

        private const val WORD = "word"

    }
}

class QuestionAdapter(
    val more: () -> Unit,
    val click: (Paper) -> Unit,
    val longClick: (Paper) -> Unit
) : BaseAdapter<Paper>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: Paper) {
        holder.setText(R.id.tv_content, Html.fromHtml(item.name ?: ""))
            .setText(R.id.tv_time, item.createDate ?: "")
            .setText(R.id.tv_score, "")
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
        verticalLayout {
            lparams(matchParent, wrapContent)
            rightPadding = dip(15)
            verticalPadding = dip(15)
            textView {
                id = R.id.tv_content
                maxLines = 2
                ellipsize = TextUtils.TruncateAt.END
                textSize = 15f
                textColor = resources.getColor(R.color.color_222831)
            }.lparams(matchParent, wrapContent)
            space().lparams(wrapContent, dip(15))
            linearLayout {
                textView {
                    id = R.id.tv_time
                    textSize = 11f
                    textColor = resources.getColor(R.color.color_8d95a1)
                }
                space().lparams(0, wrapContent, 1f)
                textView {
                    id = R.id.tv_score
                    textSize = 13f
                    textColor = resources.getColor(R.color.color_ff5d32)
                }
            }.lparams(matchParent, wrapContent)
        }
    }

    override fun emptyImageRes(): Int {
        return R.drawable.ic_emtry_collection
    }

    override fun emptyText(): String {
        return "暂无试题哦"
    }
}