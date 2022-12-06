package tuoyan.com.xinghuo_dayingindex.ui.study
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.sensorsdata.analytics.android.sdk.SensorsDataFragmentTitle
import kotlinx.android.synthetic.main.fragment_e_book.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EventMsg
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookListActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.EBookAdapter
import tuoyan.com.xinghuo_dayingindex.ui.main.Adapter
import tuoyan.com.xinghuo_dayingindex.ui.main.LessonListActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import kotlin.math.abs

@SensorsDataFragmentTitle(title = "特训营")
class EBooksFragment : LifeV4Fragment<StudyPresenter>() {
    override val presenter: StudyPresenter
        get() = StudyPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_e_book

    private var dDialog: DDialog? = null

    val recommedAdapter by lazy {
        Adapter() {
            if ("1" == it.dataType) {
                val intent = Intent(activity, LessonListActivity::class.java)
                intent.putExtra(LessonListActivity.KEY, it.key)
                intent.putExtra(LessonListActivity.TITLE, it.title)
                startActivity(intent)
            } else {
                val intent = Intent(activity, LessonDetailActivity::class.java)
                intent.putExtra(LessonDetailActivity.KEY, it.key)
                startActivity(intent)
            }
        }
    }

    private val eBookAdapter by lazy {
        EBookAdapter("0") { item, pos ->
            if (item.isEffect == "0" || item.isOwn == "1") {
                startActivity<EBookDetailActivity>(EBookDetailActivity.KEY to item.key)
            } else if (item.isEffect == "1") {
                dDialog = DDialog(this.requireContext()).setMessage("该产品已失效，是否删除？").setNegativeButton("取消") {
                    dDialog?.dismiss()
                }.setPositiveButton("删除") {
                    dDialog?.dismiss()
                    val map = HashMap<String, String>()
                    map["key"] = item.key
                    presenter.deleteMyNetClass(map) {
                        toast(it)
                        removeEBook(pos)
                    }
                }
                dDialog?.show()
            }
        }
    }

    override fun configView(view: View?) {
        rlv_ebook.isNestedScrollingEnabled = false
        rlv_ebook.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_ebook.adapter = eBookAdapter
        srfl.setColorSchemeResources(R.color.color_1482ff)

        rlv_recommend.layoutManager = LinearLayoutManager(activity)
        rlv_recommend.adapter = recommedAdapter
    }

    override fun handleEvent() {
        srfl.setOnRefreshListener {
            isLogin({
                refreshData()
                EventBus.getDefault().post(EventMsg("onRefresh", 0))
            }) {
                srfl.isRefreshing = false
            }
        }
        tv_top_edit.setOnClickListener {
            isLogin {
                startActivity<EBookListActivity>()
            }
        }
        tv_edit.setOnClickListener {
            tv_top_edit.performClick()
        }
        abl.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            srfl.isEnabled = verticalOffset >= 0
            when {
                abs(verticalOffset) >= img_re.top -> {
                    b2.visibility = View.VISIBLE
                    ctl_top.visibility = View.GONE
                }
                abs(verticalOffset) > ctl_search.height -> {
                    b2.visibility = View.VISIBLE
                    ctl_top.visibility = View.VISIBLE
                }
                else -> {
                    b2.visibility = View.GONE
                    ctl_top.visibility = View.GONE
                }
            }
        })
        tv_sub.setOnClickListener {
            isLogin {
                val code = et_code.text.toString().trim()
                if (code.isEmpty()) {
                    mToast("请输入兑换码")
                } else {
                    val map = HashMap<String, String>()
                    map["code"] = code
                    map["type"] = "1"
                    presenter.activedGoods(map) {
                        toast("兑换成功")
                        et_code.setText("")
                        getMySmartBook()
                    }
                }
            }
        }
    }

    override fun initData() {
        presenter.getNetClassList {
            recommedAdapter.setData(it)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    fun getMySmartBook() {
        presenter.getMySmarkBook() {
            srfl.isRefreshing = false
            if (it.isNotEmpty()) {
                tv_top_title.text = "特训营（${it.size}）"
                tv_title.text = "特训营（${it.size}）"
            } else {
                tv_top_title.text = "特训营"
                tv_title.text = "特训营"
            }
            eBookAdapter.setFooter(it.isNotEmpty())
            eBookAdapter.setData(it)
        }
    }

    fun refreshData() {
        if (SpUtil.isLogin) {
            getMySmartBook()
        } else {
            tv_top_title.text = "特训营"
            tv_title.text = "特训营"
            eBookAdapter.setFooter(false)
            eBookAdapter.setData(ArrayList())
        }
    }

    private fun removeEBook(position: Int) {
        eBookAdapter.remove(position)
        val size = eBookAdapter.getDateCount()
        tv_top_title.text = if (size == 0) "特训营" else "特训营（${size}）"
        tv_title.text = if (size == 0) "特训营" else "特训营（${size}）"
    }
}