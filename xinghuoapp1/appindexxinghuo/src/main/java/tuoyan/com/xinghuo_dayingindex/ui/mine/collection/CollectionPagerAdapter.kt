//package tuoyan.com.xinghuo_daying.ui.mine.collection
//
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentManager
//import tuoyan.com.xinghuo_dayingindex.widegt.FragmentV4StatePagerAdapter
//
///**
// * 创建者：
// * 时间：  2018/10/16.
// */
//class CollectionPagerAdapter(fragmentManager: FragmentManager) : FragmentV4StatePagerAdapter(fragmentManager) {
//    override fun getItem(position: Int): Fragment =
//        when (position) {
//            0 -> QuestionFragment.newInstance()
//            1 -> AudioFragment.newInstance()
//            else -> QuestionFragment.newInstance()
//        }
//
//
//    override fun getCount(): Int {
//        return 2
//    }
//
//    override fun getPageTitle(position: Int) =
//            when (position) {
//                0 -> "试题"
//                1 -> "音频"
//                else -> ""
//            }
//}
