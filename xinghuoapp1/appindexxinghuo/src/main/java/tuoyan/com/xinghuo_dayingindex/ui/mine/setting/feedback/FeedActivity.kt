package tuoyan.com.xinghuo_dayingindex.ui.mine.setting.feedback

import android.content.Intent
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_feedback.toolbar
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity

/**
 * 创建者：
 * 时间：
 */
class FeedActivity(override val layoutResId: Int = R.layout.activity_feed)
    : LifeActivity<FeedbackPresenter>() {
    override val presenter by lazy { FeedbackPresenter(this) }

    override fun configView() {
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tv_one.setOnClickListener {
            var intent = Intent(this@FeedActivity, FeedbackActivity::class.java)
            intent.putExtra("TYPE", "1")
            startActivity(intent)
        }
        tv_two.setOnClickListener {
            var intent = Intent(this@FeedActivity, FeedbackActivity::class.java)
            intent.putExtra("TYPE", "2")
            startActivity(intent)
        }
    }
}