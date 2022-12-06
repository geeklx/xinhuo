package tuoyan.com.xinghuo_dayingindex.ui.mine.user.nickname

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_change_nickname.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

/**
 * 创建者：
 * 时间：
 */
class ChangeNicknameActivity(override val layoutResId: Int = R.layout.activity_change_nickname)
    : LifeActivity<BasePresenter>() {
    override val presenter by lazy { BasePresenter(this) }
    private var name = ""
    override fun handleEvent() {
        et_nickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                name = et_nickname.text.toString().trim()
                tv_save.isEnabled = (name.length in 2..14)
                if (tv_save.isEnabled)
                    tv_save.alpha = 1.0f
                else tv_save.alpha = 0.4f
            }
        })
    }

    override fun initData() {
//        et_nickname.setText("蛇皮")
        et_nickname.setText(SpUtil.userInfo.name ?: "")
    }

    @SensorsDataTrackViewOnClick
    fun save(v: View) {
        // : 2018/5/14 17:02  保存
//        val name = et_nickname.text.toString().trim()
        if (v.isEnabled)
            presenter.changeUserInfo(mutableMapOf("name" to name)) {
                onBackPressed()
            }
    }

    @SensorsDataTrackViewOnClick
    fun delete(v: View) {
        et_nickname.setText("")
    }
}