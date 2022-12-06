package tuoyan.com.xinghuo_dayingindex.ui.main
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.bokecc.sdk.mobile.live.DWLive
import com.bokecc.sdk.mobile.live.DWLiveLoginListener
import com.bokecc.sdk.mobile.live.Exception.DWLiveException
import com.bokecc.sdk.mobile.live.pojo.*
import com.gyf.immersionbar.ImmersionBar
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.sensorsdata.analytics.android.sdk.SensorsDataFragmentTitle
import com.zxing.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_study_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.runOnUiThread
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.EventMsg
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.LiveFlag
import tuoyan.com.xinghuo_dayingindex.ui.ScannerActivity
import tuoyan.com.xinghuo_dayingindex.ui.cc.GoodsLiveActivity
import tuoyan.com.xinghuo_dayingindex.ui.message.MessageActivity
import tuoyan.com.xinghuo_dayingindex.ui.study.EBooksFragment
import tuoyan.com.xinghuo_dayingindex.ui.study.MyBooksFragment
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

@SensorsDataFragmentTitle(title = "学习")
class StudyFragment : LifeV4Fragment<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_study_main
    private val lessonsFragement by lazy { LessonsFragement() }
    val booksFragment by lazy { MyBooksFragment() }
    val ebookFragment by lazy { EBooksFragment() }
    private lateinit var lav_play: LottieAnimationView
    private lateinit var liveFlag: LiveFlag
    private var checkLogin = false;
    override fun configView(view: View?) {
        initStatusBar()
        super.configView(view)
        EventBus.getDefault().register(this)
        val fgs = ArrayList<Fragment>()
        fgs.add(lessonsFragement)
        fgs.add(booksFragment)
        fgs.add(ebookFragment)
        vp_study.adapter = PagerV4Adapter(childFragmentManager, fgs)
        (rg_study.getChildAt(0) as RadioButton).isChecked = true
        lav_play = drag_float.findViewById(R.id.lav_play)
        drag_float.setOnClickListener({})
        drag_float.findViewById<View>(R.id.live_goods).setOnClickListener {
            checkLogin = false;
            isLogin {  //点击进入到直播带货
                checkLogin = true;
                liveFlag?.let {
                    goLiveGoodsLiving(liveFlag)
                }
            }


        }
        drag_float.findViewById<View>(R.id.iv_close).setOnClickListener {
            if (drag_float.visibility == View.VISIBLE) {
                drag_float.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if ((!checkLogin) && SpUtil.isLogin) {
            getLiveFlag()
        }
    }

    override fun handleEvent() {
        super.handleEvent()
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

        img_msg.setOnClickListener {
            isLogin {
                startActivity<MessageActivity>()
            }
        }
        rg_study.setOnCheckedChangeListener { group, checkedId ->
            vp_study.currentItem = group.indexOfChild(group.findViewById(checkedId))
            for (index in 0 until group.childCount) {
                val temp = group.getChildAt(index) as RadioButton
                temp.textSize = 17f
            }
            val radio = group.findViewById(checkedId) as RadioButton
            radio.textSize = 23f
        }
        vp_study.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                val rb = rg_study.getChildAt(position) as RadioButton
                if (!rb.isChecked) rb.isChecked = true
            }
        })
    }

    private fun initStatusBar() {
        val params = top.layoutParams as ConstraintLayout.LayoutParams
        params.height = ImmersionBar.getStatusBarHeight(this.requireActivity())
    }

    override fun initData() {
        super.initData()
        getLiveFlag()
    }

    //网课操作
    fun saClick() {
        try {
            SensorsDataAPI.sharedInstance().track("click_code_scanning_button")
        } catch (e: Exception) {
        }
    }

    private fun goLiveGoodsLiving(liveFlag: LiveFlag) {
        val loginInfo = LoginInfo()
        loginInfo.roomId = liveFlag.roomId
        loginInfo.userId = liveFlag.userId
        loginInfo.viewerName = SpUtil.userInfo.name
        loginInfo.viewerToken = liveFlag.viewertoken
        DWLive.getInstance().setDWLiveLoginParams(object : DWLiveLoginListener {
            override fun onLogin(templateInfo: TemplateInfo?, viewer: Viewer?, roomInfo: RoomInfo?, publishInfo: PublishInfo?) {
                val intent = Intent(this@StudyFragment.requireContext(), GoodsLiveActivity::class.java)
                val bundle = Bundle()
//                bundle.putSerializable("marquee", viewer?.getMarquee())
                bundle.putString("title", getString(R.string.live_goods_name))
//                bundle.putSerializable("lesson", netLesson)
//                bundle.putParcelable("SensorsData", data)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onException(e: DWLiveException?) {
                runOnUiThread {
                    Toast.makeText(this@StudyFragment.requireContext(), e?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }, loginInfo)
        // 执行登录操作
        DWLive.getInstance().startLogin()
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun update(msg: EventMsg) {
        if (msg.action == "onRefresh") {
            getLiveFlag()
        }

    }

    private fun getLiveFlag() {
        presenter.getLiveFlag("h0", {
            liveFlag = it
            if (it.isShow == 0) {
                drag_float.visibility = View.GONE
                lav_play.cancelAnimation()
                lav_play.progress = 0f
            } else {
                drag_float.visibility = View.VISIBLE
                lav_play.playAnimation()
            }
        })
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}