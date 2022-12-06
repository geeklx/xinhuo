package tuoyan.com.xinghuo_dayingindex.ui.books.list
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_book_list.*
import tuoyan.com.xinghuo_dayingindex.*
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.Advert
import tuoyan.com.xinghuo_dayingindex.ui.books.adapter.BooksPagerAdapter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookListActivity

///admodel/list,get请求，adspace=tspt_zns
class BookListActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_book_list

    val gradeKey: String by lazy { intent.getStringExtra(GRADE_KEY) ?: "" }


    private val fragmentCET4 by lazy { BookListFragment.newInstance(GRAD_KEY_CET4) }
    private val fragmentCET6 by lazy { BookListFragment.newInstance(GRAD_KEY_CET6) }
    private val fragmentYan by lazy { BookListFragment.newInstance(GRAD_KEY_YAN) }
    private val fragmentTEM4 by lazy { BookListFragment.newInstance(GRAD_KEY_TEM4) }
    private val fragmentTEM8 by lazy { BookListFragment.newInstance(GRAD_KEY_TEM8) }

    private val bannerList by lazy { mutableListOf<Advert>() }

    override fun configView() {
        setSupportActionBar(tb_book_list)
        tb_book_list.setNavigationOnClickListener { onBackPressed() }
        tv_title.text = "图书配套"

        val mAdapter = BooksPagerAdapter(supportFragmentManager)
        val dataList = mutableListOf<Fragment>()

        when (gradeKey) {
            "1" -> {
                rb_one.visibility = View.VISIBLE
                rb_two.visibility = View.VISIBLE
                v_1.visibility = View.VISIBLE
                rb_one.text = "四级"
                rb_two.text = "六级"
                dataList.add(fragmentCET4)
                dataList.add(fragmentCET6)
            }
            "3" -> {
                rb_one.visibility = View.VISIBLE
                rb_two.visibility = View.VISIBLE
                v_1.visibility = View.VISIBLE
                rb_one.text = "专四"
                rb_two.text = "专八"
                dataList.add(fragmentTEM4)
                dataList.add(fragmentTEM8)
            }
            "2" -> {
                rb_one.visibility = View.VISIBLE
                rb_one.text = "考研"
                dataList.add(fragmentYan)
            }
            else -> {
                rb_one.visibility = View.VISIBLE
                rb_two.visibility = View.VISIBLE
                rb_three.visibility = View.VISIBLE
                rb_four.visibility = View.VISIBLE
                rb_five.visibility = View.VISIBLE
                v_1.visibility = View.VISIBLE
                v_2.visibility = View.VISIBLE
                v_3.visibility = View.VISIBLE
                v_4.visibility = View.VISIBLE
                dataList.add(fragmentCET4)
                dataList.add(fragmentCET6)
                dataList.add(fragmentYan)
                dataList.add(fragmentTEM4)
                dataList.add(fragmentTEM8)
            }
        }
        mAdapter.dataList = dataList
        vp_book_list.adapter = mAdapter
    }

    override fun handleEvent() {
        super.handleEvent()
        rg_book.setOnCheckedChangeListener { group, checkedId ->
            vp_book_list.currentItem = group.indexOfChild(group.findViewById(checkedId)) / 2
            for (index in 0 until group.childCount) {
                val tempV = group.getChildAt(index)
                if (tempV is RadioButton) {
                    tempV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
            val v = group.findViewById(checkedId) as RadioButton
            v.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shape_book, 0, 0, 0)
        }
        vp_book_list.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val rb = rg_book.getChildAt(position * 2) as RadioButton
                if (!rb.isChecked) rb.isChecked = true
            }
        })
        banner_book.setOnItemClickListener {
            bannerList.let { list ->
                val item = list[it]
                presenter.advertisingPv(item.key)
                when (item.goodtype) {
                    "smartBook" -> {
                        startActivity<EBookListActivity>()
                    }
                }
            }
        }
    }

    override fun initData() {
        presenter.getEBookBanner() {
            bannerList.clear()
            if (it.isNotEmpty()) {
                bannerList.add(it[0])
            }
            banner_book.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            banner_book.setImagesUrl(it)
            banner_book.stopAutoPlay()
        }
    }

    companion object {
        const val GRADE_KEY = "gradeKey"
    }
}
