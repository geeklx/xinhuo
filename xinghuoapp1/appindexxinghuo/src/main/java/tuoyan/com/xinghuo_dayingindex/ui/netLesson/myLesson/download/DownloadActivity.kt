package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.download

import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_download.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseActivity
import tuoyan.com.xinghuo_dayingindex.base.BaseV4FmPagerAdapter
import tuoyan.com.xinghuo_dayingindex.bean.ClassListBean
import tuoyan.com.xinghuo_dayingindex.bean.LessonDetail
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean

class DownloadActivity : BaseActivity() {
    override val layoutResId: Int
        get() = R.layout.activity_download


    val titles by lazy { arrayOf("课程目录","课程资料") }
    val lessonFragment by lazy { DownloadLessonFragment.newInstance(lessonList) }
    val resFragment by lazy { DownloadResFragment.newInstance(resList) }

    val lessonList by lazy { intent.getSerializableExtra(LESSON_LIST) as ArrayList<ResourceListBean> }
    val resList by lazy { intent.getSerializableExtra(RES_LIST) as ArrayList<ClassListBean> }

    val lessonInfo by lazy { intent.getSerializableExtra(LESSON_INFO) as LessonDetail}
    companion object {
        val LESSON_LIST = "lesson_list"
        val RES_LIST = "res_list"
        val LESSON_INFO = "lesson_info"
    }

    override fun configView() {
        setSupportActionBar(tb_download)
    }

    override fun handleEvent() {
        tb_download.setNavigationOnClickListener { onBackPressed() }
    }

    override fun initData() {
        var fList = mutableListOf<Fragment>()
        fList.add(lessonFragment)
        fList.add(resFragment)

        var mAdapter = BaseV4FmPagerAdapter(supportFragmentManager)
        mAdapter.fragmentList = fList
        mAdapter.titles = titles

        vp_download.adapter = mAdapter

        tl_download.setupWithViewPager(vp_download)
    }
}
