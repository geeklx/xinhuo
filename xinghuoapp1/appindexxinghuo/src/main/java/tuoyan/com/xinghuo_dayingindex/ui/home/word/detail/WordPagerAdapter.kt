package tuoyan.com.xinghuo_dayingindex.ui.home.word.detail

import androidx.fragment.app.FragmentManager
import tuoyan.com.xinghuo_dayingindex.bean.WordsByCatalogkey
import tuoyan.com.xinghuo_dayingindex.widegt.FragmentV4StatePagerAdapter

/**
 * 创建者：
 * 时间：  2018/10/16.
 */
class WordPagerAdapter(fragmentManager: FragmentManager, val gradeKey: String, private val isNewWord: Boolean) : FragmentV4StatePagerAdapter(fragmentManager) {
    private val mData = mutableListOf<WordsByCatalogkey>()
    private val fragments = mutableMapOf<Int, WordDetailFragment>()
    fun setData(data: List<WordsByCatalogkey>?) {
        this.mData.clear()
        data?.let {
            this.mData.addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): WordDetailFragment {
        var fragment = fragments[position]
        if (fragment != null) return fragment
        fragment = WordDetailFragment.newInstance(mData[position], gradeKey,isNewWord)
        fragments[position] = fragment
        return fragment
    }

    override fun getCount(): Int {
        return mData.size
    }

}
