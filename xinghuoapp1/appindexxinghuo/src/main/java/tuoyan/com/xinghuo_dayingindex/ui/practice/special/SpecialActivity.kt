//package tuoyan.com.xinghuo_daying.ui.practice.special
//
//import android.app.Fragment
//import kotlinx.android.synthetic.main.activity_special.*
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.BaseActivity
//import tuoyan.com.xinghuo_dayingindex.base.BaseFmPagerAdapter
//
//class SpecialActivity : BaseActivity() {
//    override val layoutResId: Int
//        get() = R.layout.activity_special
//
//    val gradKey by lazy { intent.getStringExtra(GRAD_KEY) }
//    var titles = arrayOf("听力","阅读","翻译","写作")
//    private val fragmentListening by lazy { SpecialFragment.newInstance(gradKey,"7") }
//    private val fragmentReading by lazy { SpecialFragment.newInstance(gradKey,"8") }
//    private val fragmentTranslate by lazy { SpecialFragment.newInstance(gradKey,"9") }
//    private val fragmentWritting by lazy { SpecialFragment.newInstance(gradKey,"10") }
//
//    companion object {
//        val GRAD_KEY = "grad_key"
//    }
//    override fun configView() {
//        setSupportActionBar(tb_special)
//    }
//
//    override fun handleEvent() {
//        tb_special.setNavigationOnClickListener { onBackPressed() }
//    }
//
//    override fun initData() {
//        var mPagerAdapter = BaseFmPagerAdapter(fragmentManager)
//        mPagerAdapter.titles = titles
//
//        var fragmentList = mutableListOf<Fragment>()
//        fragmentList.add(fragmentListening)
//        fragmentList.add(fragmentReading)
//        fragmentList.add(fragmentTranslate)
//        fragmentList.add(fragmentWritting)
//
//        mPagerAdapter.dataList = fragmentList
//
//        vp_special.adapter = mPagerAdapter
//        tl_special.setupWithViewPager(vp_special)
//
//    }
//
//}
