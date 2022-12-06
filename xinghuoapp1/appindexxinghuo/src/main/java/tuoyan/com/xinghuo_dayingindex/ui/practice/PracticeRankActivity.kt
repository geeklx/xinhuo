package tuoyan.com.xinghuo_dayingindex.ui.practice

import android.graphics.Bitmap
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.bumptech.glide.request.target.BitmapImageViewTarget
import kotlinx.android.synthetic.main.activity_practice_rank.*
import org.jetbrains.anko.ctx
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.appId
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.BookRes
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.ui.practice.adapter.PracticeRankAdapter
import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

class PracticeRankActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_practice_rank
    private val bookDetail by lazy { intent.getSerializableExtra(Companion.BOOK_RES) as BookRes }

    override fun configView() {
        super.configView()
        setSupportActionBar(tb_rank)
        tb_rank.setNavigationOnClickListener { onBackPressed() }
        if (bookDetail.userPracticeKey.isNullOrEmpty()) {
            ic_share.visibility = View.GONE
        }
        rlv_rank.layoutManager = LinearLayoutManager(this)
        rlv_rank.adapter = adapter
    }

    override fun initData() {
        super.initData()
        presenter.getAnswerRankingDetail(bookDetail.id, bookDetail.userPracticeKey, bookDetail.field1) {
            Glide.with(this)
                    .asBitmap()
                    .load(it.img)
                    .placeholder(R.mipmap.ic_avatar)
                    .error(R.mipmap.ic_avatar)
                    .centerCrop()
                    .into(object : BitmapImageViewTarget(iv_avatar) {
                        override fun setResource(resource: Bitmap?) {
                            val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(view.context.resources, resource)
                            circularBitmapDrawable.isCircular = true
                            view.setImageDrawable(circularBitmapDrawable)
                        }
                    })
            if (bookDetail.userPracticeKey.isNullOrEmpty() || it.rowNo.isNullOrEmpty()) {
                ic_share.visibility = View.GONE
                tv_sort.text = "--"
                tv_score.text = "--"
                tv_rank.text = "您当前排名超越--%用户"
            } else {
                ic_share.visibility = View.VISIBLE
                tv_sort.text = it.rowNo ?: "--"
                tv_score.text = it.rightScore ?: "--"
                var rate = it.beatRate ?: "--"
                tv_rank.text = "您当前排名超越${rate}%用户"
            }
            tv_name.text = it.name ?: "--"
            adapter.setData(it.rankingList)
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        ic_share.setOnClickListener {
            isLogin {
                ShareDialog(ctx) {
                    //                    http://10.20.3.109:8083/
                    ShareUtils.share(this, it, "我发现了一个提分利器APP，成绩排行榜，看看谁更胜一筹", "专业让学习简单",
                            WEB_BASE_URL + "rankSharePage?userKey=${SpUtil.user.userId}" +
                                    "&userPractiseKey=${bookDetail.userPracticeKey}" +
                                    "&evalKey=${bookDetail.field1}" +
                                    "&paperKey=${bookDetail.id}" +
                                    "&appKey=$appId")
                }.show()
            }
        }
    }

    private val adapter by lazy {
        PracticeRankAdapter()
    }

    companion object {
        val PAPER_KEY = "paperKey"
        val USER_PRACTISE_KEY = "userPractiseKey"
        val BOOK_RES = "BOOK_RES"
    }
}