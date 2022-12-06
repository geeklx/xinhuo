package tuoyan.com.xinghuo_dayingindex.ui.message
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_message.*
import org.jetbrains.anko.ctx
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.Message
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.AlertDialog
import tuoyan.com.xinghuo_dayingindex.ui.message.detail.MessageDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity

class MessageActivity : LifeActivity<MessagePresenter>() {
    override val presenter = MessagePresenter(this)
    override val layoutResId = R.layout.activity_message
    private var page = 1
    private val adapter by lazy {
        MessageAdapter({
            more()
        }, {
            // TODO: 2018/10/24 10:28  去详情 根据类型分发
            if (it.targetType == "3")
                startActivity<MessageDetailActivity>(MessageDetailActivity.KEY to it.targetKey)
            if (it.targetType == "2")
                startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to it.targetKey)
            if (it.targetType == "1.1" || it.targetType == "1.2")
                startActivity<PostActivity>(PostActivity.URL to "orderdetail?orderId=${it.targetKey}")
            if (it.targetType == "5")
                startActivity<PostActivity>(PostActivity.URL to "refunddetail?refundKey=${it.targetKey}&isMessage=1")
            if (it.targetType == "4")
                startActivity<PostActivity>(PostActivity.URL to "bookdetail?id=${it.targetKey}")

        }) {
            // : 2018/11/6 16:24  删除
            delete(it)
        }
    }

    private fun delete(message: Message) {
        AlertDialog(ctx, "确定要删除本条消息吗？") {
            presenter.deletedMessage(message.key ?: "") {
                adapter.remove(message)
            }
        }.show()
    }

    private fun more() {
        page++
        presenter.notices(page, {
            adapter.addData(it.list)
            adapter.setMore(it.list?.size ?: 0 > 0)
        }) {}
    }


    override fun configView() {
        recycler_view.layoutManager = LinearLayoutManager(ctx)
        recycler_view.adapter = adapter
    }

    override fun handleEvent() {
        sfl.setOnRefreshListener {
            initData()
        }
    }

    override fun initData() {
        page = 1
        presenter.notices(page, {
            adapter.setData(it.list)
            adapter.setMore(it.list?.size ?: 0 > 0)
            if (sfl.isRefreshing) {
                sfl.isRefreshing = false
            }
        }) {
            if (sfl.isRefreshing) {
                sfl.isRefreshing = false
            }
        }
    }
}
