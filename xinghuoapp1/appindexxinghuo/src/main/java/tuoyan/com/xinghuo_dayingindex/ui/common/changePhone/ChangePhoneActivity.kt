package tuoyan.com.xinghuo_dayingindex.ui.common.changePhone
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.SPUtils
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_change_phone.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.ui.MainActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.CodeDialog
import tuoyan.com.xinghuo_dayingindex.utlis.RegularUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import java.util.concurrent.TimeUnit

/**
 * 创建者：
 * 时间：
 */
class ChangePhoneActivity(override val layoutResId: Int = R.layout.activity_change_phone)
    : LifeActivity<ChangePhonePresenter>() {
    override val presenter by lazy { ChangePhonePresenter(this) }
    override val title by lazy { intent.getStringExtra(TITLE)?:"" }
    private val oldPhone by lazy { intent.getStringExtra(PHONE) ?:""}
    private val isBind by lazy { intent.getBooleanExtra(BIND, false) }
    private val isOldPhone by lazy { intent.getBooleanExtra(OLD, false) }
    var disposable: Disposable? = null
    var phone: String = ""
    var code: String = ""
    //    var pwd: String = ""
    override fun configView() {
        tv_title.text = title
    }

    override fun handleEvent() {
        et_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                phone = p0.toString().trim()
                bt_complete.isEnabled = (RegularUtils.isMobileSimple(phone) && code.isNotBlank())
            }
        })
        et_code.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                code = p0.toString().trim()
                bt_complete.isEnabled = (RegularUtils.isMobileSimple(phone) && code.isNotBlank())
            }
        })
    }

    override fun initData() {
        if (isOldPhone) {
            et_phone.setText(SpUtil.userInfo.phone)
            et_phone.isClickable = false
            et_phone.isEnabled = false
            et_phone.isFocusable = false
        }
    }

    @SensorsDataTrackViewOnClick
    fun complete(v: View) {
        val phone = et_phone.text.toString().trim()
//        val pwd = et_pwd.text.toString().trim()
        if (!RegularUtils.isMobileSimple(phone)) {
            mToast("请输入正确的手机号码")
            return
        }
//        if (!RegularUtils.isPWD(pwd)) {
//            mToast("密码格式不正确")
//            return
//        }

        v.isEnabled = false
        when {
            isBind -> {
                presenter.bindPhone(mutableMapOf("phone" to phone,
//                        "equipmentId" to  MyApp.instance.equipmentId,
                        "equipmentId" to  SPUtils.getInstance().getString("equipmentId",""),
                        "smsCode" to code), {
                    if (it == 10101) {
                        startActivity<ChangePwdActivity>(ChangePwdActivity.CODE to code,
                                ChangePwdActivity.PHONE to phone)
                        return@bindPhone
                    }
                    startActivity<MainActivity>()
//                    refreshHomeData()
                    finish()
                    v.isEnabled = true
                }) {
                    v.isEnabled = true
                }
            }
            isOldPhone -> presenter.changePhone(mutableMapOf("oldPhone" to phone,
//                    "equipmentId" to MyApp.instance.equipmentId,
                    "equipmentId" to SPUtils.getInstance().getString("equipmentId",""),
                    "code" to code), {
                startActivity<ChangePhoneActivity>(
                        TITLE to "新手机号",
                        PHONE to phone,
                        OLD to false)
                onBackPressed()
                v.isEnabled = true
            }) {
                v.isEnabled = true
            }
            else -> presenter.changePhone(mutableMapOf("oldPhone" to oldPhone,
                    "newPhone" to phone,
//                    "equipmentId" to MyApp.instance.equipmentId,
                    "equipmentId" to SPUtils.getInstance().getString("equipmentId",""),
                    "code" to code), { _ ->
                presenter.userInfo {
                    mToast("修改成功")
                    SpUtil.userInfo = it
                    onBackPressed()
                }
                v.isEnabled = true
            }) {
                v.isEnabled = true
            }
        }
    }

    @SensorsDataTrackViewOnClick
    fun code(v: View) {
        v as TextView
        phone = et_phone.text.toString().trim()
        if (RegularUtils.isMobileSimple(phone)) {
            CodeDialog(this, phone, "4", presenter) {
                v.isEnabled = false
                disposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .map { 60 - it }
                        .take(61)
                        .doOnComplete {
                            v.isEnabled = true
                            v.textColor = resources.getColor(R.color.color_1482ff)
                            v.backgroundResource = R.drawable.bg_shape_25_1482ff
                            v.text = "获取验证码"
                        }
                        .doOnDispose {
                            v.isEnabled = true
                            v.textColor = resources.getColor(R.color.color_1482ff)
                            v.backgroundResource = R.drawable.bg_shape_25_1482ff
                            v.text = "获取验证码"
                        }
                        .subscribe {
                            v.text = "${it}s"
                        }
//            presenter.sendCode(SMSCode(phone, "2"), {
//            }) {
//                disposable?.dispose()
//            }
                v.textColor = resources.getColor(R.color.color_ffffff)
                v.backgroundResource = R.drawable.bg_shape_25_abaeb0
            }.show()
        } else mToast("请输入正确的手机号码")
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    companion object {
        const val TITLE = "title"
        const val PHONE = "phone"
        const val BIND = "bind"
        const val OLD = "old"
    }
}