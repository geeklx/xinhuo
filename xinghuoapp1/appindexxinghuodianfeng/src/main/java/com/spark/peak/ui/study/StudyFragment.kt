package com.spark.peak.ui.study


import android.Manifest
import android.view.View
import androidx.fragment.app.Fragment
import com.spark.peak.R
import com.spark.peak.base.LifeFragment
import com.spark.peak.bean.SignInfo
import com.spark.peak.ui.message.MessageNoticeActivity
import com.spark.peak.ui.scan.ScannerActivity
import com.spark.peak.ui.study.adapter.StudyPagerAdapter
import com.spark.peak.ui.study.sign_in.SignInActivity
import com.spark.peak.utlis.PermissionUtlis
import com.spark.peak.utlis.SpUtil
import com.zxing.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_studydf.*
import org.jetbrains.anko.support.v4.startActivity


class StudyFragment : LifeFragment<StudyPresenter>() {
    override val presenter: StudyPresenter
        get() = StudyPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_studydf


    private val booksFragment by lazy { MyBooksFragment() }
    private val netClassFragment by lazy { MyNetclassFragment() }

    var dataList = ArrayList<Fragment>()
    override fun configView(view: View?) {
        dataList.add(booksFragment)
        dataList.add(netClassFragment)
        vp_study.adapter = fragmentManager?.let { StudyPagerAdapter(dataList, it) }
        yl_study.setupWithViewPager(vp_study)
    }

    var signInfo : SignInfo ?= null
    private fun initSignInfo(signInfo0 : SignInfo){
        signInfo = signInfo0
        if (signInfo!!.iscard == 1){
            btn_punch_card.isClickable = false
            btn_punch_card.isSelected = true
        }else{
            btn_punch_card.isClickable = true
            btn_punch_card.isSelected = false
        }

        day_count.text = signInfo!!.total.toString()
    }

    override fun handleEvent() {
        ic_scan.setOnClickListener {
            PermissionUtlis.checkPermissions(this, Manifest.permission.CAMERA) {
                IntentIntegrator(activity)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScannerActivity::class.java)
                        .setPrompt("将二维码放入框内，即可自动扫描")
                        .initiateScan()
            }
        }

        rl_sign_in.setOnClickListener {
            signInfo?.let {
                checkLogin {
                    startActivity<SignInActivity>(SignInActivity.SIGN_INFO to signInfo)
                }
            }
        }

        btn_punch_card.setOnClickListener {
            checkLogin{
                presenter.sign {
                    presenter.getSignInfo {
                        initSignInfo(it)
                    }
                }
            }
        }

        ic_message.setOnClickListener {
            checkLogin {
                startActivity<MessageNoticeActivity>()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        upmsg()
        if (SpUtil.isLogin){
            presenter.getSignInfo {
                initSignInfo(it)
            }
        }else{
            day_count.text = "0"
            btn_punch_card.isClickable = true
            btn_punch_card.isSelected = false
        }

    }
    fun upmsg() {
        try {
            iv_msg_red_dot?.visibility = View.GONE
            if (!SpUtil.isLogin) return
            presenter.ifNewFB {
                if (it == 1)
                    iv_msg_red_dot?.visibility = View.VISIBLE
            }
            presenter.ifNewNotice {
                if (it == 1)
                    iv_msg_red_dot?.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
        }
    }
}
