package tuoyan.com.xinghuo_dayingindex.ui.common.changePhone
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.blankj.utilcode.util.SPUtils
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_change_pwd.*
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.ui.MainActivity
import tuoyan.com.xinghuo_dayingindex.utlis.MD5Util
import tuoyan.com.xinghuo_dayingindex.utlis.RegularUtils

/**
 * 创建者：
 * 时间：
 */
class ChangePwdActivity(override val layoutResId: Int = R.layout.activity_change_pwd) : LifeActivity<ChangePhonePresenter>() {
    override val presenter by lazy { ChangePhonePresenter(this) }
    var disposable: Disposable? = null
    private val phone by lazy { intent.getStringExtra(PHONE) ?: "" }
    private val code by lazy { intent.getStringExtra(CODE) ?: "" }
    var pwd: String = ""
    override fun configView() {
        et_phone.text = phone
    }

    override fun handleEvent() {
        et_pwd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                pwd = p0.toString().trim()
                bt_complete.isEnabled = (RegularUtils.isPWD(pwd))
            }
        })
    }


    @SensorsDataTrackViewOnClick
    fun complete(v: View) {

        v.isEnabled = false
        presenter.bindPhone(mutableMapOf(
            "phone" to phone,
            "smsCode" to code,
//            "equipmentId" to MyApp.instance.equipmentId,
            "equipmentId" to SPUtils.getInstance().getString("equipmentId",""),
            "password" to MD5Util.encrypt(pwd)
        ), {
            v.isEnabled = true
//            refreshHomeData()
            startActivity<MainActivity>()
            finish()
        }) {
            v.isEnabled = true
        }
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    companion object {
        const val CODE = "code"
        const val PHONE = "phone"
    }
}