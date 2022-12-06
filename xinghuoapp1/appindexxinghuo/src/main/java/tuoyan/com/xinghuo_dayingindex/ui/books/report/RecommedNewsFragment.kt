package tuoyan.com.xinghuo_dayingindex.ui.books.report

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_recommed_news.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.NewsBean
import tuoyan.com.xinghuo_dayingindex.ui.message.NewsAndAudioActivity

/**
 * Created by Zzz on 2020/7/20
 */

class RecommedNewsFragment : LifeV4Fragment<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_recommed_news
    val gradeKey: String by lazy { arguments?.getString(GRAD_KEY) ?: "" }
    val clssifyKey: String by lazy { arguments?.getString(CLSSIF_YKEY) ?: "" }
    val evalRecommendKey: String by lazy { arguments?.getString(EVALR_ECOMMEND_KEY) ?: "" }
    private val adapter by lazy {
        NewsAdapter {
            val intent = Intent(activity, NewsAndAudioActivity::class.java)
            intent.putExtra(NewsAndAudioActivity.KEY, it.key)
            intent.putExtra(NewsAndAudioActivity.TITLE, it.title)
            startActivity(intent)
        }
    }

    override fun configView(view: View?) {
        super.configView(view)
        rlv_news.layoutManager = LinearLayoutManager(context)
        rlv_news.adapter = adapter
    }

    override fun initData() {
        super.initData()
        if (!gradeKey.isNullOrEmpty()) {
            presenter.getNewsList(gradeKey) {
                adapter.setData(it)
            }
        } else if (!clssifyKey.isNullOrEmpty() && !evalRecommendKey.isNullOrEmpty()) {
            presenter.evalInfomationList(clssifyKey, evalRecommendKey) {
                adapter.setData(it)
            }
        }
    }

    companion object {
        val GRAD_KEY = "grad_key"
        val CLSSIF_YKEY = "clssifyKey"
        val EVALR_ECOMMEND_KEY = "evalRecommendKey"
        fun newInstance(
            gradKey: String
        ): RecommedNewsFragment {
            val f = RecommedNewsFragment()
            val args = Bundle()
            args.putString(GRAD_KEY, gradKey)
            f.arguments = args
            return f
        }

        fun newInstance(
            clssifyKey: String,
            evalRecommendKey: String
        ): RecommedNewsFragment {
            val f = RecommedNewsFragment()
            val args = Bundle()
            args.putString(CLSSIF_YKEY, clssifyKey)
            args.putString(EVALR_ECOMMEND_KEY, evalRecommendKey)
            f.arguments = args
            return f
        }
    }
}

class NewsAdapter(var onItemClick: (item: NewsBean) -> Unit) :
    BaseAdapter<NewsBean>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: NewsBean) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_see_num, item.pv + "人已学习")
        Glide.with(holder.itemView.context).load(item.img)
            .into(holder.getView(R.id.img_news) as ImageView)
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_item_news, null)
    }

    override fun emptyImageRes(): Int {
        return R.drawable.empty_study
    }

    override fun emptyText(): String {
        return "暂无内容"
    }

}