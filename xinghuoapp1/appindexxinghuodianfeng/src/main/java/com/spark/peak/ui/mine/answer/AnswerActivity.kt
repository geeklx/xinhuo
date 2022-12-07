package com.spark.peak.ui.mine.answer

import com.google.android.material.tabs.TabLayout
import com.spark.peak.R
import com.spark.peak.WEB_BASE_URL
import com.spark.peak.appId
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.common.login.LoginActivity
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.mine.user.UserPresenter
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_answer.*
import org.jetbrains.anko.startActivity

class AnswerActivity(override val layoutResId: Int = R.layout.activity_answer)
    : LifeActivity<UserPresenter>() {
    override val presenter by lazy { UserPresenter(this) }


    override fun configView() {
        web_view.settings.javaScriptEnabled = true
        web_view.settings.domStorageEnabled = true
        web_view.settings.javaScriptCanOpenWindowsAutomatically = true
        web_view.registerHandler("getToken") { data, function ->
            function?.onCallBack("${SpUtil.user.token},${SpUtil.user.userId},$appId,${SpUtil.userInfo.grade}")
        }
        web_view.registerHandler("showDialog") { data, _ ->
            val list = data.split(',')
            startActivity<PostActivity>(PostActivity.URL to if (list.size > 1) list[1] else "",
                    PostActivity.TITLE to if (list.isNotEmpty()) list[0] else "")
        }

        web_view.registerHandler("login") { data, _ ->
            startActivity<LoginActivity>()
        }

    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> web_view.loadUrl("${WEB_BASE_URL}mine/mineAnswer?type=1")
                    1 -> web_view.loadUrl("${WEB_BASE_URL}mine/mineblogs?type=1")
                }
            }

        })
    }

    override fun initData() {
        web_view.loadUrl("${WEB_BASE_URL}mine/mineAnswer?type=1")
    }


}
