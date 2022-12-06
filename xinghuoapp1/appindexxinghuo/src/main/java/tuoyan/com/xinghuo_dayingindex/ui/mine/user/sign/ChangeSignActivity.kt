package tuoyan.com.xinghuo_dayingindex.ui.mine.user.sign

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_change_sign.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

/**
 * 创建者：
 * 时间：
 */
class ChangeSignActivity(override val layoutResId: Int = R.layout.activity_change_sign)
    : LifeActivity<BasePresenter>() {
    override val presenter by lazy { BasePresenter(this) }
    private var sign = ""
    override fun handleEvent() {
        et_sign.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                sign = et_sign.text.toString().trim()
                tv_save.isEnabled = (sign.length in 0..50)
                if (tv_save.isEnabled)
                    tv_save.alpha = 1.0f
                else tv_save.alpha = 0.4f
            }
        })
    }

    override fun initData() {
        et_sign.setText(SpUtil.userInfo.signature)
    }

    @SensorsDataTrackViewOnClick
    fun save(v: View) {
        // : 2018/5/14 17:02  保存
        if (v.isEnabled)
            presenter.changeUserInfo(mutableMapOf("signature" to sign)) {
                onBackPressed()
            }
    }

    @SensorsDataTrackViewOnClick
    fun delete(v: View) {
        et_sign.setText("")
    }
}