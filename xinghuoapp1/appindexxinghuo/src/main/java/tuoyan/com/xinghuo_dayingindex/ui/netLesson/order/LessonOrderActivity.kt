//package tuoyan.com.xinghuo_daying.ui.netLesson.order
//
//import kotlinx.android.synthetic.main.activity_lesson_order.*
//import org.jetbrains.anko.toast
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
//import tuoyan.com.xinghuo_dayingindex.appId
//import tuoyan.com.xinghuo_dayingindex.base.BaseActivity
//import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
//
//class LessonOrderActivity : BaseActivity() {
//    override val layoutResId: Int
//        get() = R.layout.activity_lesson_order
//
//    private val goodsKey by lazy { intent.getStringExtra(GOOD_KEY) }
//
//    companion object {
//        val GOOD_KEY = "goodKey"
//    }
//    override fun configView() {
//        web_view.settings.javaScriptEnabled = true
//        web_view.settings.databaseEnabled=true
//        web_view.settings.domStorageEnabled = true
//
//        web_view.registerHandler("getToken"){_, f ->
//            f.onCallBack("${SpUtil.user.token?:""},${SpUtil.user.userId?:""},$appId,${SpUtil.userInfo.grade?:""}")
//        }
//        web_view.registerHandler("goBack"){_, _ ->
//            onBackPressed()
//        }
//        web_view.registerHandler("toPay"){_, _ ->
////            onBackPressed()
//            toast("支付")
//        }
//
//    }
//
//    override fun initData() {
//        web_view.loadUrl(WEB_BASE_URL+"ordersubmit?goodsKey=$goodsKey&goodsNum=1&goodsType=2&isApp=1&isCart=0")
//    }
//}
