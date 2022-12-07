package com.spark.peak.ui.mine.changeSign

import android.text.SpannableStringBuilder
import android.view.View
import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.bean.UserInfo
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_change_signdf.*

/**
 * 创建者：
 * 时间：
 */
class ChangeSignActivity(override val layoutResId: Int = R.layout.activity_change_signdf)
    : LifeActivity<ChangeSignPresenter>() {
    override val presenter by lazy { ChangeSignPresenter(this) }
    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun configView() {
        SpUtil.userInfo.signature?.let {
            et_sign.text = SpannableStringBuilder.valueOf(it)
        }
    }

    fun save(v: View) {
        // TODO: 2018/5/14 17:02 霍述雷 保存
        val sign = et_sign.text.toString().trim()
        if (sign.isNotBlank())
            presenter.changeUserInfo(UserInfo(signature = sign)) {
                onBackPressed()
            }
    }

}