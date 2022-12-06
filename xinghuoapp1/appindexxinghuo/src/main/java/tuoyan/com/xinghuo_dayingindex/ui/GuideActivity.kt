//package tuoyan.com.xinghuo_daying.ui
//
//import android.app.Activity
//import android.os.Bundle
//import androidx.viewpager.widget.PagerAdapter
//import androidx.viewpager.widget.ViewPager
//import android.view.View
//import android.view.ViewGroup
//import android.view.WindowManager
//import android.widget.ImageView
//import kotlinx.android.synthetic.main.activity_guide.*
//import org.jetbrains.anko.*
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.BaseActivity
//
//class GuideActivity : BaseActivity() {
//    override val layoutResId: Int
//        get() = R.layout.activity_guide
//    private val images by lazy { ArrayList<Int>() }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        fullScreen = true
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun configView() {
//        super.configView()
//        images.add(R.mipmap.guide_1)
//        images.add(R.mipmap.guide_2)
//        images.add(R.mipmap.guide_3)
//        images.add(R.mipmap.guide_4)
//
//
//        p1.isEnabled = true
//        p2.isEnabled = false
//        p3.isEnabled = false
//        p4.isEnabled = false
//        vp_guide.adapter = GuidePagerAdapter(this,images)
//
//        vp_guide.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            var flag = false
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//
//            }
//
//            override fun onPageSelected(position: Int) {
//                p1.isEnabled = false
//                p2.isEnabled = false
//                p3.isEnabled = false
//                p4.isEnabled = false
//                when(position){
//                    0 ->{
//                        p1.isEnabled = true
//                        btn_goOn.visibility = View.GONE
//                    }
//                    1 -> {
//                        p2.isEnabled = true
//                        btn_goOn.visibility = View.GONE
//                    }
//                    2 -> {
//                        p3.isEnabled = true
//                        btn_goOn.visibility = View.GONE
//                    }
//                    3 -> {
//                        p4.isEnabled = true
//                        btn_goOn.visibility = View.VISIBLE
//                    }
//                }
//
//                btn_goOn.isEnabled = position == images.size-1
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {
//                when (state) {
//                    ViewPager.SCROLL_STATE_DRAGGING -> flag = true
//                    ViewPager.SCROLL_STATE_SETTLING -> flag = false
//                    ViewPager.SCROLL_STATE_IDLE -> {
//                        if (flag && vp_guide.currentItem == images.size - 1) {//最后一页继续滑动，跳转首页
////                            startActivity<MainActivity>()  路保发说继续滑动不要跳转了
////                            finish()
//                        }
//                    }
//                }
//            }
//        })
//    }
//
//    override fun handleEvent() {
//        super.handleEvent()
//        btn_goOn.setOnClickListener {
//            startActivity<MainActivity>()
//            finish()
//        }
//    }
//}
//
//class GuidePagerAdapter(private val ctx : Activity, private val images: List<Int>) : PagerAdapter() {
//
//    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`
//
//    override fun getCount(): Int = images.size
//
//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        container.removeView(`object` as View)
//    }
//
//    override fun instantiateItem(container: ViewGroup, position: Int): Any = container.context.relativeLayout {
//        container.addView(this)
//        imageView {
//            scaleType = ImageView.ScaleType.FIT_XY
//            imageResource = images[position]
//            setOnClickListener {
//                if (position == images.size - 1){
//                    ctx.startActivity<MainActivity>()
//                    ctx.finish()
//                }
//            }
//        }.lparams(matchParent, matchParent)
//    }
//
//}
