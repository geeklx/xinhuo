package tuoyan.com.xinghuo_dayingindex.ui.books.detail

import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.MODE_FIXED
import com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.activity_book_detail.*
import org.jetbrains.anko.toast
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.base.BaseV4FmPagerAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.BookDetail
import tuoyan.com.xinghuo_dayingindex.ui.books.BooksPresenter
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils
import tuoyan.com.xinghuo_dayingindex.utlis.TypeUtil
import kotlin.properties.Delegates

class BookDetailActivity : LifeActivity<BooksPresenter>() {
    override val presenter: BooksPresenter
        get() = BooksPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_book_detail

    private val bookKey by lazy { intent.getStringExtra(KEY) ?: "" }
    private val type by lazy { intent.getStringExtra(TYPE) ?: "" }

    private val audioFragment by lazy { BookResFragment.newInstance() }
    private val videoFragment by lazy { BookResFragment.newInstance() }
    private val practiceFragment by lazy { BookResTestFragment.newInstance() }
    private val resFragment by lazy { BookResTestFragment.newInstance() }
    private val testFragment by lazy { BookResTestFragment.newInstance() }
    private var titles = arrayListOf<String>()
    var refresh = false

    companion object {
        val KEY = "key"
        val TYPE = "type"
    }

    override fun configView() {
        setSupportActionBar(tb_book_detail)
    }

    override fun handleEvent() {
        tb_book_detail.setNavigationOnClickListener { onBackPressed() }
        ic_share.setOnClickListener {
            saOption("点击分享", "")
            ShareDialog(this@BookDetailActivity) {
                ShareUtils.setCallback(object : ShareUtils.Callback {
                    override fun onSure() {
                        addIntegral("图书")
                    }
                })
                ShareUtils.share(this, it, bookDetail.titile, "", WEB_BASE_URL + "download.html")
            }.show()
        }
        btn_add.setOnClickListener {
            isLogin {
                saOption("加入书架", "")
                val data = HashMap<String, String>()
                data.put("key", bookKey)
                data.put("type", "1")

                presenter.addMyStudy(data) {
                    toast(it)
                    btn_add.setCompoundDrawables(null, null, null, null)
//                    btn_add.background = resources.getDrawable(R.drawable.shape_btn_added)
                    btn_add.background = ContextCompat.getDrawable(this, R.drawable.shape_btn_added)
                    btn_add.isEnabled = false
                    btn_add.text = "已加入"
                    refreshOwnFlag(true)
                }
            }
        }
        tl_book_detail.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                saOption("点击${tab?.text}tab", "")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    var bookDetail by Delegates.notNull<BookDetail>()
    override fun initData() {
        presenter.getBookDetail(bookKey, type) { detail ->
            bookDetail = detail
            saView(detail)
            Glide.with(this).load(detail.img).into(iv_cover_book)
            tv_title_book.text = detail.titile
            if (detail.gradeName.isNullOrEmpty()) {
                tv_remark1.visibility = View.GONE
            } else {
                tv_remark1.visibility = View.VISIBLE
                tv_remark1.text = detail.gradeName
            }

            if (detail.yearStr.isNullOrEmpty()) {
                tv_remark2.visibility = View.GONE
            } else {
                tv_remark2.visibility = View.VISIBLE
                tv_remark2.text = detail.yearStr
            }


            val mPagerAdapter = BaseV4FmPagerAdapter(supportFragmentManager)

//                    arrayOf("字幕听力", "名师视频", "配套练习", "配套资源", "电子试卷")
            val fList = mutableListOf<Fragment>()
            bookDetail.hearingList.let { list ->
                if (list.size > 0) {
                    titles.add("字幕听力")
                    audioFragment.setData(list)
                    fList.add(audioFragment!!)
                }
            }
            bookDetail.videoList.let { list ->
                if (list.size > 0) {
                    titles.add("名师视频")
                    videoFragment.setData(list)
                    fList.add(videoFragment!!)
                }
            }
            bookDetail.exerciseList.let { list ->
                if (list.size > 0) {
                    titles.add("配套练习")
                    practiceFragment.setDataList(detail.exerciseList)
                    fList.add(practiceFragment)
                }
            }
            bookDetail.resourceList.let { list ->
                if (list.size > 0) {
                    titles.add("配套资源")
                    resFragment.setDataList(detail.resourceList)
                    fList.add(resFragment)
                }
            }
            bookDetail.electronicPaperList.let { list ->
                if (list.size > 0) {
                    titles.add("电子试卷")
                    testFragment.gradeKey(bookDetail.gradeKey)
                    testFragment.setDataList(detail.electronicPaperList)
                    fList.add(testFragment)
                }
            }
            if (fList.size == 0) {
                view_empty.visibility = View.VISIBLE
                vp_book_detail.visibility = View.GONE
            } else {
                view_empty.visibility = View.GONE
                vp_book_detail.visibility = View.VISIBLE
            }
            if (fList.size > 3) {
                tl_book_detail.tabMode = MODE_SCROLLABLE
            } else {
                tl_book_detail.tabMode = MODE_FIXED
            }
            mPagerAdapter.fragmentList = fList
            mPagerAdapter.titles = titles.toTypedArray()
            vp_book_detail.adapter = mPagerAdapter
//tablayout 绑定 viewpager
            tl_book_detail.setupWithViewPager(vp_book_detail)

            if (detail.isown == "0") {
                if (detail.supportingKey.isEmpty()) {
                    btn_add.isEnabled = false
                    btn_add.setCompoundDrawables(null, null, null, null)
                    btn_add.background = ContextCompat.getDrawable(this, R.drawable.shape_btn_added)
                    btn_add.text = "未上架"
                }
            } else if (detail.isown == "1") {
                btn_add.isEnabled = false
                btn_add.setCompoundDrawables(null, null, null, null)
                btn_add.background = ContextCompat.getDrawable(this, R.drawable.shape_btn_added)
                btn_add.text = "已加入"
                refreshOwnFlag(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (refresh) {
            presenter.getBookDetail(bookKey, type) { detail ->
                bookDetail = detail
                if (practiceFragment.isAdded) {
                    practiceFragment.setDataList(detail.exerciseList)
                }
                if (resFragment.isAdded) {
                    resFragment.setDataList(detail.resourceList)
                }
                if (testFragment.isAdded) {
                    testFragment.setDataList(detail.electronicPaperList)
                }
            }
        }
        refresh = false
    }

    /**
     * 获取图书信息
     * 下载模块使用
     */
    fun getBookInfo() = bookDetail

    fun refreshOwnFlag(flag: Boolean) {
        Handler().postDelayed({
            try {
                if (audioFragment?.isAdded == true) audioFragment?.ownFlg(flag)
                if (videoFragment?.isAdded == true) videoFragment?.ownFlg(flag)
                if (practiceFragment.isAdded) practiceFragment.ownFlg(false)
                if (resFragment.isAdded) resFragment.ownFlg(flag)
                if (testFragment.isAdded) testFragment.ownFlg(false)
            } catch (e: Exception) {
            }
        }, 500)
    }

    fun saOption(optionName: String, type: String) {
        try {
            val property = saProperty(bookDetail)
            property.put("operation_name", optionName)
            property.put("resource_type", TypeUtil.getType(type))
            SensorsDataAPI.sharedInstance().track("operation_book_matching_page", property)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun saView(bookDetail: BookDetail) {
        try {
            val property = saProperty(bookDetail)
            property.put("matching_page_type", "图书配套页")
            SensorsDataAPI.sharedInstance().track("view_book_matching_page", property)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun saProperty(bookDetail: BookDetail): JSONObject {
        val property = JSONObject()
        property.put("book_matching_id", bookDetail.supportingKey)
        property.put("book_matching_name", bookDetail.titile)
        property.put("section", bookDetail.gradeName)
        return property
    }
}


