package tuoyan.com.xinghuo_dayingindex.ui.mine.setting
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.View
import com.blankj.utilcode.util.AppUtils
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_setting.*
import tuoyan.com.xinghuo_dayingindex.BuildConfig
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.LoginResponse
import tuoyan.com.xinghuo_dayingindex.bean.UserInfo
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.common.changePassword.ChangePasswordActivity
import tuoyan.com.xinghuo_dayingindex.ui.common.changePhone.ChangePhoneActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.AlertDialog
import tuoyan.com.xinghuo_dayingindex.ui.mine.setting.feedback.FeedActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DataCleanManager
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

class SettingActivity : LifeActivity<SettingPresenter>() {
    override val presenter = SettingPresenter(this)
    override val layoutResId = R.layout.activity_setting
    private var subscribe: Disposable? = null

    override fun configView() {
        super.configView()
        btn_login_out.visibility = if (SpUtil.isLogin) View.VISIBLE else View.GONE
    }

    override fun initData() {
        tv_version.text = AppUtils.getAppVersionName()
        applicationDataSize()
    }

    override fun handleEvent() {
        super.handleEvent()
        tv_unRegister.setOnClickListener {
            isLogin {
                val intent = Intent(this, PostActivity::class.java)
                intent.putExtra(PostActivity.URL, "login/logOff?phone=${SpUtil.userInfo.phone}")
                startActivityForResult(intent, 200)
            }
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == RESULT_OK) {
            signOut()
        }
    }

    @SensorsDataTrackViewOnClick
    fun password(v: View) {
        isLogin {
            startActivity<ChangePasswordActivity>(
                ChangePasswordActivity.PHONE to (SpUtil.userInfo.phone ?: ""),
                ChangePasswordActivity.TITLE to "修改密码"
            )
        }
    }

    @SensorsDataTrackViewOnClick
    fun phone(v: View) {
        isLogin {
            startActivity<ChangePhoneActivity>(
                ChangePhoneActivity.TITLE to "验证手机号",
                ChangePhoneActivity.OLD to true
            )
        }
    }

    @SensorsDataTrackViewOnClick
    fun score(v: View) {
        // : 2018/10/31 9:32  App评分
        val mAddress = Uri.parse("market://details?id=$packageName")
        val marketIntent = Intent(Intent.ACTION_VIEW, mAddress)
        try {
            startActivity(marketIntent)
        } catch (e: Exception) {
            mToast("您没有安装应用市场")
        }
    }

    @SensorsDataTrackViewOnClick
    fun about(v: View) {
        startActivity<AboutActivity>()
    }

    @SensorsDataTrackViewOnClick
    fun feedback(v: View) {
        startActivity<FeedActivity>()
    }

    @SensorsDataTrackViewOnClick
    fun signOut(v: View) {
        signOut()
    }

    private fun signOut() {
        presenter.logout()
        SpUtil.isLogin = false
        SpUtil.user = LoginResponse()
        SpUtil.userInfo = UserInfo()
        SpUtil.loginInfo = ""
//        EventBus.getDefault().post(EventMsg("login", -1))
        onBackPressed()
//        refreshHomeData()
    }

//    private fun deleteUser() {
//        presenter.deleteUser {
//            signOut()
//        }
//    }

    @SensorsDataTrackViewOnClick
    fun cache(v: View) {
        AlertDialog(this@SettingActivity, "确定要清除吗？") {
            clearApplicationDataSize()
        }.show()
    }

    /**
     * 计算缓存
     */
    private fun applicationDataSize() {
        subscribe = Single.create<String> { e ->
            val applicationDataSize = DataCleanManager.getApplicationDataSize(
                applicationContext,
                Environment.getExternalStorageDirectory().path + "/xinghuo_daying/cache"
            )
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
            DataCleanManager.cleanApplicationData(
                applicationContext,
                Environment.getExternalStorageDirectory().path + "/xinghuo_daying/cache"
            )
            val applicationDataSize = DataCleanManager.getApplicationDataSize(applicationContext)
            e.onSuccess(applicationDataSize)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { s ->
                tv_cache.text = s
                if (subscribe != null && !subscribe!!.isDisposed) subscribe!!.dispose()
            }
    }

    override fun onDestroy() {
        if (subscribe != null && !subscribe!!.isDisposed) subscribe!!.dispose()
        super.onDestroy()
    }
}
