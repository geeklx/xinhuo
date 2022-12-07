package com.spark.peak.ui.mine.changeNickname

import android.text.SpannableStringBuilder
import android.view.View
import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.bean.UserInfo
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_change_nicknamedf.*

/**
 * 创建者：
 * 时间：
 */
class ChangeNicknameActivity(override val layoutResId: Int = R.layout.activity_change_nicknamedf)
    : LifeActivity<ChangeNicknamePresenter>() {
    override val presenter by lazy { ChangeNicknamePresenter(this) }
    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun configView() {
        et_nickname.text = SpannableStringBuilder.valueOf(SpUtil.userInfo.name ?: "")
    }

    fun save(v: View) {
        // : 2018/5/14 17:02 霍述雷 保存
        val name = et_nickname.text.toString().trim()
        if (name.isNotBlank())
            presenter.changeUserInfo(UserInfo(name = name)) {
                onBackPressed()
            }
    }

    fun delete(v: View) {
        et_nickname.text = SpannableStringBuilder()
    }
}