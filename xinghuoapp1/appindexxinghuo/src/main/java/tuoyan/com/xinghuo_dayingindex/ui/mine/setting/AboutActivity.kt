package tuoyan.com.xinghuo_dayingindex.ui.mine.setting
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.View
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_about.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils


class AboutActivity(override val layoutResId: Int = R.layout.activity_about) : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)

    //    override fun statusColor() {
//        ImmersionBar.with(this)
//                .transparentStatusBar()
//                .fitsSystemWindows(false)
//                .statusBarDarkFont(true)
//                .statusBarColor(R.color.color_ffffff)
//                .init()
//    }
    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tv_service.setOnClickListener {
            //1用户服务协议
            startActivity<WebViewActivity>(WebViewActivity.URL to WEB_BASE_URL + "login/agreement?isApp=1", WebViewActivity.TITLE to "用户服务协议")
        }
        tv_personal.setOnClickListener {
            //2隐私政策
            startActivity<WebViewActivity>(WebViewActivity.URL to WEB_BASE_URL + "login/privacy?isApp=1", WebViewActivity.TITLE to "隐私政策")
        }
    }

    override fun initData() {
    }

    @SensorsDataTrackViewOnClick
    fun share(v: View) {
        ShareDialog(this) {
            ShareUtils.share(
                this, it, "星火英语", "",
                "https://m.sparke.cn/download.html"
//                    "$WEB_BASE_URL/share?" +
//                            "type=${2}" +
//                            "&title=currentRes?.name&number1=99&number2=66"
            )

            // TODO: 2018/11/23 9:41  分享链接
        }.show()
    }

    @SensorsDataTrackViewOnClick
    fun www(v: View) {
        val uri = Uri.parse("http://www.sparke.cn")
        val intent = Intent(ACTION_VIEW, uri)
        startActivity(intent)
    }
}
