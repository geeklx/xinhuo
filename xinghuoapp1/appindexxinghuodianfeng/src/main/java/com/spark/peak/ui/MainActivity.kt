package com.spark.peak.ui

import android.Manifest
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.base.BaseFragment
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.dialog.PromptDialog
import com.spark.peak.ui.home.HomeFullFragment
import com.spark.peak.ui.home.HomePresenter
import com.spark.peak.ui.home.sa.SaEvent
import com.spark.peak.ui.mine.MineFragment
import com.spark.peak.ui.mine.setting.changePassword.ChangePasswordActivity
import com.spark.peak.ui.netLessons.NetLessonsActivity
import com.spark.peak.ui.scan.ScannerActivity
import com.spark.peak.ui.study.book.BookDetailActivity
import com.spark.peak.utlis.PermissionUtlis
import com.spark.peak.utlis.SpUtil
import com.zxing.google.zxing.integration.android.IntentIntegrator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_maindf.*
import org.jetbrains.anko.startActivity
import java.util.concurrent.TimeUnit

class MainActivity : LifeActivity<HomePresenter>() {
    override val presenter: HomePresenter
        get() = HomePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_maindf

    private val homeFragment by lazy { HomeFullFragment() }
    private val mineFragment by lazy { MineFragment() }
    private var currentIndex = 0

    val screenHeight by lazy {
        var dm = resources.displayMetrics
        dm.heightPixels
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        fullScreen = true
        super.onCreate(savedInstanceState)
    }

    override fun configView() {
        h5toApp()
        initFirstFragment()
        rg_home.setOnCheckedChangeListener { view, index ->
            val transaction = supportFragmentManager.beginTransaction()
            when (index) {
                R.id.rb_home -> {
                    transaction.hide(currentFragments())
                        .show(homeFragment)
                        .commit()
                    currentIndex = 0
                }
                R.id.rb_mine -> {
                    if (!mineFragment.isAdded) transaction.add(R.id.fl_home, mineFragment)
                    transaction.hide(currentFragments())
                        .show(mineFragment)
                        .commit()
                    currentIndex = 1
                }
            }
        }

        fl_home.viewTreeObserver.addOnGlobalLayoutListener {
            var r = Rect()
            fl_home.getWindowVisibleDisplayFrame(r)
            var deleteHeight = screenHeight - r.bottom

            if (deleteHeight > 150) {
                //屏幕被压缩的高度 大于150 则判定为键盘弹出
                rg_home.visibility = View.GONE
            } else {
                rg_home.visibility = View.VISIBLE
            }
        }
    }

    private fun h5toApp() {
        val intentData = intent.data
        if (intentData != null) {
            val type = intentData.getQueryParameter("type")
            val key = intentData.getQueryParameter("key")
            when (type) {
                "1" -> {
                    startActivity<PostActivity>(
                        PostActivity.TITLE to "",
                        PostActivity.URL to "${WEB_BASE_URL}scan/scanIndex/?key=${key}"
                    )
                }
                "2" -> {
                    startActivity<BookDetailActivity>(
                        BookDetailActivity.KEY to key,
                        BookDetailActivity.TYPE to "1"
                    )
                }
                "3" -> {
                    startActivity<NetLessonsActivity>(NetLessonsActivity.KEY to key)
                }
            }

        }
    }

    override fun initData() {
        super.initData()
        initNetStateListener()
        if (SpUtil.user.state == "99") {
            PromptDialog(this@MainActivity, "下次再说", {
                startActivity<ChangePasswordActivity>(
                    ChangePasswordActivity.PHONE to SpUtil.userInfo.phone,
                    ChangePasswordActivity.TITLE to "重置密码"
                )
            }) {

            }.show()
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        img_scanner.setOnClickListener {
            SaEvent.scanClick()
            PermissionUtlis.checkPermissions(this, Manifest.permission.CAMERA) {
                IntentIntegrator(this@MainActivity)
                    .setOrientationLocked(false)
                    .setCaptureActivity(ScannerActivity::class.java)
                    .initiateScan()
            }
        }
    }

    private fun initFirstFragment() {
        supportFragmentManager.beginTransaction().add(R.id.fl_home, homeFragment).show(homeFragment)
            .commit()

    }

    private fun currentFragments(): BaseFragment {
        return when (currentIndex) {
            0 -> homeFragment
            1 -> mineFragment
            else -> homeFragment
        }
    }

    private var isDropOut = true
    override fun onBackPressed() {
        if (isDropOut) {
            isDropOut = false
            mToast("再按一次退出程序")
            Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .take(3)
                .doOnComplete {
                    isDropOut = true
                }.subscribe()
            return
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data ?: return
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        intentResult?.let {
            it.contents?.let {
                val data = HashMap<String, String>()
                data.put("qrcode", it)
                scanTo(data)
            }
        }

        val resultData = data.getStringExtra("code")
        resultData?.let {
            val data = HashMap<String, String>()
            data.put("qrcode", it)
            scanTo(data)
        }
    }

    public override fun onResume() {
        super.onResume()
        ImmersionBar.with(this)
            .fullScreen(true)
            .statusBarColor(android.R.color.transparent)
            .statusBarDarkFont(true)
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .init()
        if (homeFragment.isAdded) {
            homeFragment.initData()
        }
        if (mineFragment.isAdded) {
            mineFragment.initData()
        }
    }
}
