package tuoyan.com.xinghuo_dayingindex.ui.ebook
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.graphics.Paint
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import kotlinx.android.synthetic.main.activity_ebook_detail.*
import org.jetbrains.anko.longToast
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.base.LifeActivityStateAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeFullActivity
import tuoyan.com.xinghuo_dayingindex.bean.EBook
import tuoyan.com.xinghuo_dayingindex.bean.EBookParam
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.EBookCatalogFragment
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailFragment
import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.UdeskUtils
import kotlin.math.abs

class EBookDetailActivity : LifeFullActivity<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_ebook_detail
    private val pagerAdapter by lazy { LifeActivityStateAdapter(this) }
    val key by lazy { intent.getStringExtra(KEY) ?: "" }
    var bookDetail: EBook? = null
    private var dDialog: DDialog? = null
    var reFresh = false
    private var eBookCatalogFragment: EBookCatalogFragment? = null
    private var isLogin = false

    override fun configView() {
        super.configView()
        tv_older_price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        vp_ebook.offscreenPageLimit = 10
        vp_ebook.adapter = pagerAdapter
        isLogin = SpUtil.isLogin
    }

    override fun initData() {
        super.initData()
        presenter.getEBookDetail(key) { book ->
            if ("2" == book.state) {
                longToast("该产品未上架")
                onBackPressed()
            } else if ("3" == book.state) {
                longToast("该产品已过期")
                onBackPressed()
            } else {
                bookDetail = book
                tv_title.text = book.name
                toolbar_title.text = book.name
                tv_time.text = book.endTime
                tv_data.visibility = if ("1" == book.isOwn) View.VISIBLE else View.GONE
                ctl_bottom.visibility = if ("1" == book.isOwn) View.GONE else View.VISIBLE
                tv_price.text = Html.fromHtml("<small>￥</small>${book.price}")
                tv_older_price.visibility = if (book.originalCost.isNullOrEmpty()) View.GONE else View.VISIBLE
                tv_older_price.text = "￥${book.originalCost}"
                tv_num.text = "已购 ${book.buyers}"
                val list = mutableListOf<Fragment>()
                list.add(LessonDetailFragment.newInstance(book.details))
                eBookCatalogFragment = EBookCatalogFragment.newInstance(key, book.isOwn, book.type ?: "1")//需要根据课程类型判断
                list.add(eBookCatalogFragment!!)
                pagerAdapter.fragmentList = list
                if ("1" == book.isOwn) {
                    vp_ebook.currentItem = 1
                }
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener { onBackPressed() }
        img_share.setOnClickListener {
            isLogin {
                ShareDialog(this) {
                    ShareUtils.setCallback(object : ShareUtils.Callback {
                        override fun onSure() {
                            addIntegral("电子书")
                        }
                    })
                    ShareUtils.share(
                        this, it, bookDetail?.name ?: "", "", WEB_BASE_URL + "ebook/ebookdetail?key=" + key
                    )
                }.show()
            }
        }
        rg_book.setOnCheckedChangeListener { radioGroup, checkedId ->
            vp_ebook.currentItem = radioGroup.indexOfChild(radioGroup.findViewById(checkedId))
            for (index in 0 until radioGroup.childCount) {
                val tempV = radioGroup.getChildAt(index)
                if (tempV is RadioButton) {
                    tempV.textSize = 15f
                }
            }
            val v = radioGroup.findViewById(checkedId) as RadioButton
            v.textSize = 18f
        }
        vp_ebook.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val rb = rg_book.getChildAt(position) as RadioButton
                if (!rb.isChecked) {
                    rb.isChecked = true
                }
                if (position == 0) {
                    saClick("点击图书介绍（智能书）")
                }
            }
        })
        tv_service.setOnClickListener {
            isLogin {
                UdeskUtils.openChatView(this)
            }
        }
        abl.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            when {
                verticalOffset == 0 -> {
                    //展开状态
                    toolbar_title.visibility = View.GONE
                    img_shadow.visibility = View.VISIBLE
                }
                abs(verticalOffset) >= appBarLayout.totalScrollRange -> {
                    //折叠状态
                    toolbar_title.visibility = View.VISIBLE
                    img_shadow.visibility = View.GONE
                }
                else -> {
                    if (abs(verticalOffset) > tv_title.height) {
                        toolbar_title.visibility = View.VISIBLE
                        img_shadow.visibility = View.GONE
                    } else {
                        toolbar_title.visibility = View.GONE
                        img_shadow.visibility = View.VISIBLE
                    }
                }
            }
        })
        tv_data.setOnClickListener {
            val params = EBookParam()
            params.bookKey = key
            startActivity<EBookDataActivity>(EBookDataActivity.EBOOK_PARAM to params, EBookDataActivity.TYPE to (bookDetail?.type ?: "1"))
        }
        tv_buy.setOnClickListener {
            isLogin {
                val msg = "该资源为虚拟产品，购买后不可退款，${if ("长期" != bookDetail?.endTime) "有效期至${bookDetail?.endTime}，过期则失效不能使用，" else ""}是否确认购买？"
                dDialog = DDialog(this).setWidth(250).setGravity(Gravity.LEFT).setMessage(msg).setNegativeButton("考虑一下") {
                    dDialog?.dismiss()
                }.setPositiveButton("立即购买") {
                    dDialog?.dismiss()
                    startActivity<PostActivity>(PostActivity.URL to "ordersubmit?goodsKey=${key}&goodsNum=1&goodsType=7&isCart=0")
                }
                dDialog?.show()
            }
        }
    }


    companion object {
        val KEY = "key"
        val TYPE = "type"//1智能书模考 2简听力 3词汇星火式巧记速记
    }

    fun splitTime(string: String): String {
        val array = string.split(":")
        return if (array.size > 1) {
            array[1]
        } else {
            ""
        }
    }

    override fun onResume() {
        super.onResume()
        saStart()
        if (!isLogin && SpUtil.isLogin) {
            presenter.getEBookDetail(key) { book ->
                bookDetail = book
                tv_data.visibility = if ("1" == book.isOwn) View.VISIBLE else View.GONE
                ctl_bottom.visibility = if ("1" == book.isOwn) View.GONE else View.VISIBLE
                if ("1" == book.isOwn) {
                    vp_ebook.currentItem = 1
                }
                eBookCatalogFragment?.let { fragment ->
                    if (fragment.isAdded) {
                        fragment.freshData(book.isOwn)
                    }
                }
            }
        } else {
            eBookCatalogFragment?.let { fragment ->
                if (fragment.isAdded && reFresh) {
                    reFresh = false
                    fragment.initData()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        saEnd(bookDetail)
    }

    fun goBuy() {
        val msg = "购买后可以开始学习，该资源为虚拟产品，购买后不可退款，${if ("长期" != bookDetail?.endTime) "有效期至${bookDetail?.endTime}，过期则失效不能使用，" else ""}是否确认购买？"
        dDialog = DDialog(this).setMessage(msg).setWidth(250).setGravity(Gravity.LEFT).setNegativeButton("考虑一下") {
            dDialog?.dismiss()
        }.setPositiveButton("立即购买") {
            dDialog?.dismiss()
            startActivity<PostActivity>(PostActivity.URL to "ordersubmit?goodsKey=${key}&goodsNum=1&goodsType=7&isCart=0")
        }
        dDialog?.show()
    }

    private fun setProperty(detail: EBook?): JSONObject {
        val property = JSONObject()
        property.put("commodity_id", key)
        property.put("commodity_type", "智能书")
        detail?.let { book ->
            property.put("commodity_name", book.name)
            property.put("commodity_validity", book.endTime)
            property.put("original_price", book.originalCost ?: "0")
            property.put("current_price", book.price ?: "0")
            property.put("buy_it_or_not", "1" == book.isOwn)
        }
        property.put("is_sign_in", SpUtil.isLogin)
        return property
    }

    private fun saStart() {
        try {
            SensorsDataAPI.sharedInstance().trackTimerStart("view_commodity_detail")
        } catch (e: Exception) {
        }
    }

    private fun saEnd(detail: EBook?) {
        try {
            val property = setProperty(detail)
            SensorsDataAPI.sharedInstance().trackTimerEnd("view_commodity_detail", property)
        } catch (e: Exception) {
        }
    }

    fun saClick(name: String) {
        try {
            val property = setProperty(bookDetail)
            property.put("operation_name", name)
            SensorsDataAPI.sharedInstance().track("operation_commodity_detail", property)
        } catch (e: Exception) {
        }
    }
}