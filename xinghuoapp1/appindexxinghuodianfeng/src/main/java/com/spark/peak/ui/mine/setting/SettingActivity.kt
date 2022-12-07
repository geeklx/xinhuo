package com.spark.peak.ui.mine.setting

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.View
import com.blankj.utilcode.util.AppUtils
import com.spark.peak.R
import com.spark.peak.base.EventMsg
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.LoginResponse
import com.spark.peak.bean.UserInfo
import com.spark.peak.ui.dialog.AlertDialog
import com.spark.peak.ui.dialog.ShareDialog
import com.spark.peak.ui.mine.feedback.FeedbackActivity
import com.spark.peak.ui.mine.setting.changePassword.ChangePasswordActivity
import com.spark.peak.utlis.DataCleanManager
import com.spark.peak.utlis.ShareUtils
import com.spark.peak.utlis.SpUtil
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_settingdf.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

/**
 * 创建者：
 * 时间：
 */
class SettingActivity(override val layoutResId: Int = R.layout.activity_settingdf)
    : LifeActivity<SettingPresenter>() {
    override val presenter: SettingPresenter = SettingPresenter(this)
    private var subscribe: Disposable? = null
    private val dialog by lazy {
        ShareDialog(ctx) {
            ShareUtils.share(this, it, "title", "description",
                    "http://git.jszx.sparke.cn/spark/android/spark_ns",
                    "https://upload.jianshu.io/users/upload_avatars/2962875/e1bb1d43-8845-47e1-9229-89c73db5d929.jpg?imageMogr2/auto-orient/strip|imageView2/1/w/96/h/96")
        }
    }

    override fun configView() {

        bt_login.visibility = if (!SpUtil.isLogin) View.GONE else View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        tv_version.text = "V${AppUtils.getAppVersionName()}"
        applicationDataSize()
    }

    override fun handleEvent() {
        tv_feedback.setOnClickListener {
            checkLogin { startActivity<FeedbackActivity>() }
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tv_del_account.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this@SettingActivity).setTitle("注销提示")
                    .setMessage("注销账号后，您的个人资料会被删除。\n账号会自动退出登录状态。\n感谢您对星火产品的长久支持。")
                    .setNegativeButton("确认注销") { dialogInterface: DialogInterface, i: Int ->
                        presenter.cancellation {
                            loginOut()
                        }
                    }
                    .setPositiveButton("关闭弹窗") { dialogInterface: DialogInterface, i: Int ->

                    }.create().show()
        }
    }

    /**
     * 计算缓存
     */
    private fun applicationDataSize() {
        subscribe = Single.create<String> { e ->
            val applicationDataSize = DataCleanManager.getApplicationDataSize(applicationContext,
                    Environment.getExternalStorageDirectory().path + "/Peak/cache")
            e.onSuccess(applicationDataSize)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { s ->
                    tv_cache.text = s
                    if (subscribe != null && !subscribe!!.isDisposed) subscribe!!.dispose()
                }

    }

    /**
     * 清楚缓存
     */
    private fun clearApplicationDataSize() {
        subscribe = Single.create<String> { e ->
            DataCleanManager.cleanApplicationData(applicationContext,
                    Environment.getExternalStorageDirectory().path + "/Peak/cache")
            val applicationDataSize = DataCleanManager.getApplicationDataSize(applicationContext)
            e.onSuccess(applicationDataSize)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { s ->
                    tv_cache.text = s
                    if (subscribe != null && !subscribe!!.isDisposed) subscribe!!.dispose()
                }
    }

    fun changePassword(v: View) {
        checkLogin {
            SpUtil.userInfo.phone?.let {
                startActivity<ChangePasswordActivity>(ChangePasswordActivity.PHONE to it,
                        ChangePasswordActivity.TITLE to "修改密码")
            }
        }
        // : 2018/4/19 15:52 霍述雷 修改密码
    }

    fun clearCache(v: View) {
        // : 2018/4/19 15:52 霍述雷 清理缓存
        AlertDialog(ctx, "确定要清除吗？") {
            clearApplicationDataSize()
        }.show()
    }

    fun inviteFriends(v: View) {
        // : 2018/4/19 15:52 霍述雷 邀请好友
        dialog.show()
    }

    fun seekPraise(v: View) {
        // : 2018/4/19 15:52 霍述雷 求好评
        val mAddress = Uri.parse("market://details?id=$packageName")
        val marketIntent = Intent(Intent.ACTION_VIEW, mAddress)
        try {
            startActivity(marketIntent)
        } catch (e: Exception) {
            mToast("您没有安装应用市场")
        }
    }

    fun aboutUs(v: View) {// TODO: 2018/4/19 15:52 霍述雷 关于我们
        startActivity<AboutActivity>()
    }

    fun clickSignOut(v: View) {
// : 2018/4/16 11:24 霍述雷 退出登录
        presenter.logout()
        loginOut()
    }

    fun loginOut() {
        SpUtil.isLogin = false
        SpUtil.user = LoginResponse()
        SpUtil.userInfo = UserInfo()
        EventBus.getDefault().post(EventMsg("login", -1))
        onBackPressed()
        refreshHomeData()
    }
}