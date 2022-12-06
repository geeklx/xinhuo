package tuoyan.com.xinghuo_dayingindex.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.bokecc.sdk.mobile.live.DWLive
import com.bokecc.sdk.mobile.live.DWLiveLoginListener
import com.bokecc.sdk.mobile.live.Exception.DWLiveException
import com.bokecc.sdk.mobile.live.pojo.*
import kotlinx.android.synthetic.main.activity_lesson_deatil.*
import kotlinx.android.synthetic.main.activity_lesson_list2.*
import kotlinx.android.synthetic.main.activity_lesson_list2.drag_float
import kotlinx.android.synthetic.main.activity_lesson_list2.rlv_lessons
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.Lesson
import tuoyan.com.xinghuo_dayingindex.bean.LiveFlag
import tuoyan.com.xinghuo_dayingindex.ui.cc.GoodsLiveActivity
import tuoyan.com.xinghuo_dayingindex.ui.home.HomePresenter
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

/**
 * gradeFragment中网课文件夹跳转到此页面
 */
class LessonListActivity : LifeActivity<HomePresenter>() {
    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    private val mTitle by lazy { intent.getStringExtra(TITLE) ?: "" }
    private val dataList by lazy { intent.getSerializableExtra(DATA_LIST) as? List<Lesson> }
    val adapter by lazy {
        Adapter() {
            if ("1" == it.dataType) {
                val intent = Intent(this, LessonListActivity::class.java)
                intent.putExtra(KEY, it.key)
                intent.putExtra(TITLE, it.title)
                startActivity(intent)
            } else {
                val intent = Intent(this, LessonDetailActivity::class.java)
                intent.putExtra(LessonDetailActivity.KEY, it.key)
                startActivity(intent)
            }
        }
    }
    override val presenter: HomePresenter
        get() = HomePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_lesson_list2
    private  var lav_play: LottieAnimationView?=null
    private  var liveFlag: LiveFlag?=null
    private var checkLogin=false;
    override fun configView() {
        super.configView()
        tv_title.text = mTitle
        rlv_lessons.layoutManager = LinearLayoutManager(this)
        rlv_lessons.adapter = adapter

        drag_float.setOnClickListener({})
        lav_play = drag_float.findViewById<LottieAnimationView>(R.id.lav_play)
        drag_float.findViewById<View>(R.id.live_goods).setOnClickListener({
            checkLogin=false;
            isLogin {  //点击进入到直播带货
                checkLogin=true
                liveFlag?.let {
                    goLiveGoodsLiving(liveFlag!!)
                } }


        })
        drag_float.findViewById<View>(R.id.iv_close).setOnClickListener({
            if (drag_float.visibility== View.VISIBLE) {
                drag_float.visibility= View.GONE
            }
        })
    }
    private fun goLiveGoodsLiving(liveFlag: LiveFlag) {
        val loginInfo = LoginInfo()
        loginInfo.roomId = liveFlag.roomId
        loginInfo.userId = liveFlag.userId
        loginInfo.viewerName = SpUtil.userInfo.name
        loginInfo.viewerToken = liveFlag.viewertoken
        DWLive.getInstance().setDWLiveLoginParams(object : DWLiveLoginListener {
            override fun onLogin(templateInfo: TemplateInfo?, viewer: Viewer?, roomInfo: RoomInfo?, publishInfo: PublishInfo?) {
                val intent = Intent(this@LessonListActivity, GoodsLiveActivity::class.java)
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
                    Toast.makeText(this@LessonListActivity, e?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }, loginInfo)
        // 执行登录操作
        DWLive.getInstance().startLogin()
    }
    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun initData() {
        super.initData()
        if (key.isNotEmpty()) {
            getData(key)
        } else {
            adapter.setData(dataList)
        }
        getLiveFlag()
    }

    override fun onResume() {
        super.onResume()
        if ((!checkLogin)&&SpUtil.isLogin) {
            getLiveFlag()
        }
    }
    private fun getLiveFlag() {
        presenter.getLiveFlag("h1", {
            liveFlag = it
            if (it.isShow == 0) {
                drag_float.visibility = View.GONE
                lav_play?.cancelAnimation()
                lav_play?.progress = 0f
            } else {
                drag_float.visibility = View.VISIBLE
                lav_play?.playAnimation()
            }
        })
    }

    private fun getData(key: String) {
        presenter.getSpecialNet(key) {
            adapter.setData(it)
        }
    }

    companion object {
        const val KEY = "key"
        const val TITLE = "title"
        const val DATA_LIST = "dataList"
    }

    override fun onDestroy() {
        super.onDestroy()
        lav_play?.cancelAnimation()
        lav_play?.progress=0f
    }
}