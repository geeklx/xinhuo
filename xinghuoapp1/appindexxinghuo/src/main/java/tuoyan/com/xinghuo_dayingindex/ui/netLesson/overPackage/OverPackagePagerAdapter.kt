//package tuoyan.com.xinghuo_daying.ui.netLesson.overPackage
//
//import androidx.fragment.app.FragmentManager
//import tuoyan.com.xinghuo_dayingindex.utlis.FragmentV4PagerAdapter
//
//
///**
// * 创建者：
// * 时间：  2018/9/21.
// */
//class OverPackagePagerAdapter(fm: FragmentManager) : FragmentV4PagerAdapter(fm) {
//    override fun getItem(position: Int) = when (position) {
//        0 -> NetLessonFragment()
//        1 -> EvaluationFragment()
//        else -> NetLessonFragment()
//    }
//
//    override fun getCount() = 2
//
//    override fun getPageTitle(position: Int) = when (position) {
//        0 -> "网课"
//        1 -> "测评"
//        else -> ""
//    }
//}