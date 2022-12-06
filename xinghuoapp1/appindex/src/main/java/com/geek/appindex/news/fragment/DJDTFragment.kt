package com.geek.appindex.news.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.AppUtils
import com.geek.appindex.R
import com.geek.appindex.news.adapter.ZXAdapter
import com.geek.appindex.news.model.ZXConvertData
import com.geek.appindex.news.model.ZXTYPE
import com.geek.biz1.bean.BjyyBeanYewu3
import com.geek.biz1.bean.home.ClassificationListByPageBean
import com.geek.biz1.presenter.home.ClassificationByPagePresenter
import com.geek.biz1.view.home.ClassificationByPageView
import com.geek.libbase.baserecycleview.SlbBaseFragmentViewPageYewuList
import com.just.agentweb.geek.fragment.AgentWebFragment
import com.just.agentweb.geek.hois3.HiosHelperNew

/**
 * 咨询展示2 可以展示ViewPager
 */
class DJDTFragment : SlbBaseFragmentViewPageYewuList(), ClassificationByPageView {

    private var mAdapter: ZXAdapter? = null

    private var mPresenter: ClassificationByPagePresenter? = null

    companion object {
        fun getInstance(bean: BjyyBeanYewu3): DJDTFragment {
            val fragment = DJDTFragment()
            val bundle = Bundle()
            bundle.putSerializable("feileiBean", bean)
            fragment.arguments = bundle
            return fragment
        }
    }


    override fun getLayoutId(): Int = R.layout.fragmentyewulist1_common2


    override fun donetworkAdd() {
        emptyview1.loading()
        // 业务bufen
        init_adapter(mAdapter)

    }

    override fun retryDataAdd() {
        refreshData()
    }

    override fun onclickAdd() {
        mAdapter?.setOnItemClickListener { _, _, position ->
//            val url = mAdapter?.data?.get(position)?.data?.url.toString();
//            Log.e("aaaaaa", url)
//            if (url != null && "" != url && "null" != url) {
//                val intent =
//                    Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity")
//                intent.putExtra(AgentWebFragment.URL_KEY, url)
//                startActivity(intent)
//            }
//            HiosHelperNew.resolveAd(
//                requireActivity(), requireActivity(),
//                "dataability://${AppUtils.getAppPackageName()}.hs.act.slbapp.PIPActivityDk{act}"
//            )

            handleItemClick(position)
            handleItemClick(position)
        }

        mAdapter?.setOnItemChildClickListener { _, _, position ->
//            val url = mAdapter?.data?.get(position)?.data?.url.toString();
//            Log.e("aaaaaa", url)
//            if (url != null && "" != url && "null" != url) {
//                val intent =
//                    Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity")
//                intent.putExtra(AgentWebFragment.URL_KEY, url)
//                startActivity(intent)
//            }
            handleItemClick(position)
//            HiosHelperNew.resolveAd(
//                requireActivity(), requireActivity(),
//                "dataability://${AppUtils.getAppPackageName()}.hs.act.slbapp.PIPActivityDk{act}"
//            )
        }
    }

    fun handleItemClick(position: Int) {
        mAdapter?.data?.get(position)?.takeIf { it.type == ZXTYPE.TYPE_ITEM }?.let {
            val url = mAdapter?.data?.get(position)?.data?.url ?: ""
            Log.e("aaaaaa", url)
            if ("" != url && "null" != url) {
                val intent =
                    Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity")
                intent.putExtra(AgentWebFragment.URL_KEY, url)
                startActivity(intent)
            }
        }
    }

    override fun findviewAdd() {
        mPresenter = ClassificationByPagePresenter()
        mPresenter?.onCreate(this)
        recyclerView1.layoutManager =
            GridLayoutManager(activity, 1, RecyclerView.VERTICAL, false)
        mAdapter = ZXAdapter(mutableListOf(), true)
        recyclerView1.adapter = mAdapter
        emptyview1.notices("暂无数据", "网络出了点问题", "正在加载…", "")
    }

    override fun emptyviewAdd() {
        emptyview1.loading()
        // 业务bufen
        init_adapter(mAdapter)

    }

    override fun refreshLayoutAdd() {
        // 业务bufen
        init_adapter(mAdapter)

    }


    private fun refreshData() {
        mPresenter?.getClassificationDataByPage(mNextRequestPage, PAGE_SIZE, "1001", "1")
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestory()
    }

    override fun onClassficationDataSuccess(bean: ClassificationListByPageBean?) {
        val list = mutableListOf<ZXConvertData>()
        bean?.also { it ->
            it.data?.map {
                ZXConvertData(data = it, type = ZXTYPE.TYPE_ITEM)
            }?.apply {
                list.addAll(this)
            }
        }

        OnOrderSuccess(list)


    }

    override fun onClassficationDataNoData(msg: String?) {
        OnOrderNodata()
    }

    override fun onClassficationDataFail(msg: String?) {
        OnOrderFail()
    }


}