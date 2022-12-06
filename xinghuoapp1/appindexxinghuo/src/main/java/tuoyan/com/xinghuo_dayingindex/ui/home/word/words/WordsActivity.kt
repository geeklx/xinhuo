package tuoyan.com.xinghuo_dayingindex.ui.home.word.words
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_words.*
import org.jetbrains.anko.ctx
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean
import tuoyan.com.xinghuo_dayingindex.ui.home.word.WordPresenter
import tuoyan.com.xinghuo_dayingindex.ui.home.word.back.BackActivity
import tuoyan.com.xinghuo_dayingindex.utlis.MediaPlayerUtlis

class WordsActivity : LifeActivity<WordPresenter>() {
    override val presenter = WordPresenter(this)
    override val layoutResId = R.layout.activity_words
    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    override val title by lazy { intent.getStringExtra(TITLE) ?: "" }
    private val isWrong by lazy { intent.getBooleanExtra(IS_WRONG, false) }
    private val gradeKey by lazy { intent.getStringExtra(GRADE_KEY) ?: "" }
    private var page = 0
    private var learn = 0
    private val adapter by lazy {
        WordsAdapter(isEmpty = isWrong, more = {
            //加载更多
            if (isWrong) more()
        }) { it ->
            // : 2018/10/12 11:06   播放音频
            it.sound?.let { sound ->
                MediaPlayerUtlis.start(ctx, sound, {
                    //                    view.startAnim()
                }, {
                    //                    view.stopAnim()
                }) {
                    //                    view.stopAnim()
                    if (it.isNotBlank())
                        mToast(it)
                }
            }
        }
    }

    private fun more() {
        page++
        presenter.getWrongWord(gradeKey, page) {
            adapter.addData(it.body)
            adapter.setMore(adapter.getDateCount() < it.totalCount)
        }
    }

    override fun configView() {
        tv_title.text = title
        recycler_view.layoutManager = LinearLayoutManager(ctx)
        val decoration = DividerItemDecoration(ctx, LinearLayout.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.drawable.divider))
        recycler_view.addItemDecoration(decoration)
        recycler_view.adapter = adapter
        if (isWrong) {
            ll_wrong.visibility = View.VISIBLE
            ll_core.visibility = View.GONE
//            adapter.setMore(true)
        } else {
            ll_wrong.visibility = View.GONE
            ll_core.visibility = View.VISIBLE
//            adapter.setMore(false)
        }
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        if (isWrong) {
            page = 0
            presenter.getWrongWord(gradeKey, page) {
                adapter.setData(it.body)
                tv_title.text = "错词本（${it.totalCount}）"
                adapter.setMore(adapter.getDateCount() < it.totalCount)
                ll_wrong.visibility = if (adapter.getDateCount() > 0) View.VISIBLE else View.GONE
            }
        } else {
            presenter.getWordsByCatalogkey(key, 0) {
                learn = it.isLearn
                adapter.setData(it.list)
                if (learn == 1) {//学习是否已完成
                    btn_review.setBackgroundResource(R.drawable.bg_shape_5_4c84ff)
                } else {
                    btn_review.setBackgroundResource(R.drawable.bg_shape_5_c3c7cb)
                }
            }
        }

    }

    @SensorsDataTrackViewOnClick
    fun learn(v: View) {
//        App.instance.data = adapter.getData()//数据过大传参崩溃
        startActivity<BackActivity>(
            BackActivity.TITLE to title,
            BackActivity.GRADE_KEY to gradeKey,
            BackActivity.KEY to key,
            BackActivity.LEARN to true
        )
    }

    @SensorsDataTrackViewOnClick
    fun review(v: View) {
//        App.instance.data = adapter.getData()//数据过大传参崩溃
        if (learn == 0) return
        startActivity<BackActivity>(
            BackActivity.GRADE_KEY to gradeKey,
            BackActivity.KEY to key,
            BackActivity.TITLE to title,
            BackActivity.LEARN to false
        )
    }

    @SensorsDataTrackViewOnClick
    fun eliminate(v: View) {
//        if (adapter.getDateCount() > 20) {
//            App.instance.data = adapter.getData().subList(0, 20)
//        } else {
//        MyApp.instance.data = adapter.getData()//数据过大传参崩溃
        val list1: List<ResourceListBean> = ArrayList()
        val gson1 = Gson()
        val data1 = gson1.toJson(adapter.getData())
        SPUtils.getInstance().put("WordsByCatalogkey", data1)
//        }
        startActivity<BackActivity>(
            BackActivity.TITLE to "消灭错词",
            BackActivity.IS_WRONG to true,
            BackActivity.KEY to key,
            BackActivity.GRADE_KEY to gradeKey,
            BackActivity.LEARN to true
        )
    }

    override fun onPause() {
        MediaPlayerUtlis.release()
        super.onPause()
    }

    companion object {
        const val KEY = "key"
        const val TITLE = "title"
        const val GRADE_KEY = "gradeKey"
        const val IS_WRONG = "isWrong"
    }
}
