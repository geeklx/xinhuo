package tuoyan.com.xinghuo_dayingindex.ui.main

import android.Manifest
import android.content.Intent
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.sensorsdata.analytics.android.sdk.SensorsDataFragmentTitle
import com.zxing.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_home_up.*
import tuoyan.com.xinghuo_dayingindex.*
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.GradeBean
import tuoyan.com.xinghuo_dayingindex.ui.ScannerActivity
import tuoyan.com.xinghuo_dayingindex.ui.home.HomePresenter
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import java.util.*

@SensorsDataFragmentTitle(title = "首页")
class HomeFragment : LifeV4Fragment<HomePresenter>() {
    //顶部学段
    private var selGradeList = ArrayList<GradeBean>()
    override val presenter: HomePresenter
        get() = HomePresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_home_up

    override fun configView(view: View?) {
        initStatusBar()
        super.configView(view)
        getGrade()
    }

    private fun initStatusBar() {
        val params = top.layoutParams as ConstraintLayout.LayoutParams
        params.height = ImmersionBar.getStatusBarHeight(this.requireActivity())
    }

    fun getGrade() {
        val grade = SpUtil.gradeSelect
        if (grade.isNotEmpty()) {
            selGradeList = Gson().fromJson(grade, object : TypeToken<ArrayList<GradeBean>>() {}.type)
        } else {
            selGradeList = ArrayList()
            selGradeList.add(GradeBean(name = "四六级", id = GRAD_KEY_CET4_CET6, key = "1"))
            selGradeList.add(GradeBean(name = "考研", id = GRAD_KEY_YAN, key = "2"))
            selGradeList.add(GradeBean(name = "专四专八", id = GRAD_KEY_TEM4_TEM8, key = "3"))
        }
        initRGGrade(selGradeList)
    }

    //动态显示顶部RadioButton和viewpager
    fun initRGGrade(gradeList: ArrayList<GradeBean>) {
        val tempList = mutableListOf<GradeBean>()
        tempList.add(GradeBean(name = "精选", id = GRAD_KEY_RECOMMED, key = "0"))
        tempList.addAll(gradeList)
        val fgs = ArrayList<Fragment>()
        for (index in 0 until rg_grade.childCount) {
            rg_grade.getChildAt(index).visibility = View.GONE
        }
        for ((index, item) in tempList.withIndex()) {
            val rb = rg_grade.getChildAt(index) as RadioButton
            rb.visibility = View.VISIBLE
            rb.text = item.name
            fgs.add(GradeFragment.newInstance(item))
        }
        rb_one.isChecked = true
        vp_home.offscreenPageLimit = 10
        vp_home.adapter = PagerV4Adapter(fragmentManager, fgs)
    }

    override fun handleEvent() {
        super.handleEvent()
        img_grade.setOnClickListener {
            //学段添加
            val intent = Intent(activity, SelectGradeActivity::class.java)
            intent.putExtra(SelectGradeActivity.SEL_GRADE_LIST, selGradeList)
            activity?.startActivityForResult(intent, 999)
        }
        img_scan.setOnClickListener {
            //扫码
            PermissionUtlis.checkPermissions(this, Manifest.permission.CAMERA) {
                isLogin {
                    saClick()
                    IntentIntegrator(activity)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScannerActivity::class.java)
                        .setPrompt("将二维码放入框内，即可自动扫描")
                        .initiateScan()
                }
            }
        }
        rg_grade.setOnCheckedChangeListener { group, checkedId ->
            rb_one.textSize = 17f
            rb_two.textSize = 17f
            rb_three.textSize = 17f
            rb_four.textSize = 17f
            val bt = group.findViewById(checkedId) as RadioButton
            val index = group.indexOfChild(bt)
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (index > 1) {
                        hs_rg.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                    } else {
                        hs_rg.fullScroll(HorizontalScrollView.FOCUS_LEFT)
                    }
                }
            }, 100L)
            vp_home.currentItem = index
            bt.textSize = 23f
        }
        vp_home.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                (rg_grade.getChildAt(position) as RadioButton).isChecked = true
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            selGradeList = data.getParcelableArrayListExtra(SelectGradeActivity.SEL_GRADE_LIST)!!
            SpUtil.gradeSelect = Gson().toJson(selGradeList)
            initRGGrade(selGradeList)
        }
    }

    //网课操作
    fun saClick() {
        try {
            SensorsDataAPI.sharedInstance().track("click_code_scanning_button")
        } catch (e: Exception) {
        }
    }
}
