//package tuoyan.com.xinghuo_daying.ui.practice
//
//import android.view.View
//import kotlinx.android.synthetic.main.activity_practice.*
//import org.jetbrains.anko.startActivity
//import tuoyan.com.xinghuo_dayingindex.GRAD_KEY_CET4
//import tuoyan.com.xinghuo_dayingindex.GRAD_KEY_CET6
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.BaseActivity
//import tuoyan.com.xinghuo_dayingindex.ui.dialog.GradeDialog
//import tuoyan.com.xinghuo_dayingindex.ui.practice.real.RealActivity
//import tuoyan.com.xinghuo_dayingindex.ui.practice.special.SpecialActivity
//import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
//
//class PracticeActivity : BaseActivity() {
//    override val layoutResId: Int
//        get() = R.layout.activity_practice
//
//    var gradeKey = ""
//
//    var gradStr = when {
////        SpUtil.isLogin -> SpUtil.userInfo.gradename?:""
//        SpUtil.gradStr.isEmpty() -> {
//            SpUtil.gradStr = "四级"
//            "四级"
//        }
//        else -> SpUtil.gradStr
//    }
//
//    private val dialog by lazy {
//        GradeDialog("四级", "六级", "", "", "", this) {
//            tv_title.text = it+""
//            SpUtil.gradStr = it
//            gradStr = it
//            getGradKey()
//        }
//    }
//
//    override fun configView() {
//        setSupportActionBar(tb_practice)
//        tv_title.text = gradStr
//        getGradKey()
//    }
//
//    override fun handleEvent() {
//        tb_practice.setNavigationOnClickListener { onBackPressed() }
//        tv_title.setOnClickListener {
//            dialog.show()
//        }
//
//    }
//
//    override fun initData() {
//        getGradKey()
//    }
//
//    private fun getGradKey(){
//        gradeKey = when(gradStr){
//            "四级" -> GRAD_KEY_CET4
//            "六级" -> GRAD_KEY_CET6
//            else -> GRAD_KEY_CET4
//        }
//    }
//    fun onBegin(view: View){
//        when(view.id){
//            R.id.ll_model1 -> startActivity<SpecialActivity>( SpecialActivity.GRAD_KEY to gradeKey)
//            R.id.ll_model2 -> startActivity<RealActivity>( RealActivity.GRAD_KEY to gradeKey)
//        }
//
//    }
//}
