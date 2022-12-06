package com.geek.appindex.news.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.geek.appindex.R
import com.geek.appindex.news.adapter.ZXAdapter
import com.geek.appindex.news.model.ZXConvertData
import com.geek.appindex.news.model.ZXTYPE
import com.geek.biz1.bean.BjyyBeanYewu3
import com.geek.libbase.fenlei.fenlei1.FenleiAct1CateBean1
import com.geek.libbase.fenlei.fenlei1.SlbBaseLazyFragmentNewCate
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * 咨询展示2 可以展示ViewPager
 */
//class ZXFragment : SlbBaseLazyFragmentNewCate<FenleiAct1CateBean1>() {

//    private lateinit var mRecyclerView: RecyclerView
//    private lateinit var mAdapter: ZXAdapter
//    private lateinit var mRefreshLayout: RefreshLayout
//    private var data: FenleiAct1CateBean1? = null
////    private var niubiEmptyView: NiubiEmptyViewNew? = null
////    private var canRequest = false//假装网络异常
//
//    companion object {
//        fun getInstance(data: FenleiAct1CateBean1): ZXFragment {
//            val zxFragment2 = ZXFragment()
//            val bundle = Bundle()
//            bundle.putSerializable("FenleiAct1CateBean1", data)
//            zxFragment2.arguments = bundle
//            return zxFragment2
//        }
//    }
//
//
//    override fun setup(rootView: View, savedInstanceState: Bundle?) {
//        initView(rootView)
//        mRecyclerView.adapter = mAdapter
//        mRefreshLayout.autoRefresh()
////        niubiEmptyView = NiubiEmptyViewNew()
////        niubiEmptyView?.bind(activity, mRecyclerView, mAdapter)
////        niubiEmptyView?.setRetry {
////            canRequest = true
////            data?.apply {
////                initFakeData(this)
////            }
//
////        }
//        arguments?.getSerializable("FenleiAct1CateBean1")?.let {
//            it as? FenleiAct1CateBean1
//        }?.apply {
//
//            mAdapter.setNewData(initFakeData(this))
//            mRefreshLayout.finishRefresh()
//
//            data = this
//
//        }
//    }
//
//    private fun initView(rootView: View) {
//        mRefreshLayout = rootView.findViewById(R.id.refreshLayout)
//        mRecyclerView = rootView.findViewById(R.id.rl1)
//        mAdapter = ZXAdapter(mutableListOf())
//        mAdapter.setOnItemChildClickListener { adapter, view, position ->
//            when (view.id) {
//                R.id.iv_amazing -> {
//                    (adapter.getItem(position) as? ZXFakeData)?.apply {
//                        this.enable = !enable
//                        adapter.notifyItemChanged(position)
//                    }
//                }
//            }
//
//        }
//        mRefreshLayout.setOnRefreshListener {
//            data?.apply {
//                mAdapter.setNewData(initFakeData(this))
//                mRefreshLayout.finishRefresh()
//            }
//
//        }
//        mRefreshLayout.setOnLoadMoreListener {
//            data?.apply {
//                mAdapter.addData(initFakeData(this, true))
//                mRefreshLayout.finishLoadMore()
//            }
//        }
//
//    }
//
//
//    //假数据
//    private fun initFakeData(
//        data: FenleiAct1CateBean1,
//        isLoadMore: Boolean = false
//    ): List<ZXFakeData> {
//        val list = mutableListOf<ZXFakeData>()
//        when (data.text_content) {
//            "推荐", "党建动态" -> {
//                repeat(10) {
//                    list.add(ZXFakeData())
//                }
//            }
//            "政策文件", "通知公告" -> {
//                list.add(ZXFakeData(title = "权威发布", type = ZXTYPE.TYPE_TITLE))
//                repeat(10) {
//                    list.add(ZXFakeData())
//                }
//                list.add(ZXFakeData(title = "办事指南", type = ZXTYPE.TYPE_TITLE))
//                repeat(10) {
//                    list.add(ZXFakeData())
//                }
//            }
//            "乡镇要闻" -> {
//                if (!isLoadMore) {
//                    val mBannerList = mutableListOf<BjyyBeanYewu3>()
//                    mBannerList.add(
//                        BjyyBeanYewu3(
//                            "",
//                            "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862286564142395.png",
//                            "",
//                            "",
//                            false
//                        )
//                    )
//                    mBannerList.add(
//                        BjyyBeanYewu3(
//                            "",
//                            "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862378905937488.png",
//                            "",
//                            "",
//                            false
//                        )
//                    )
//                    mBannerList.add(
//                        BjyyBeanYewu3(
//                            "",
//                            "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521863886959548956.png",
//                            "",
//                            "",
//                            false
//                        )
//                    )
//
//                    list.add(ZXFakeData(type = ZXTYPE.TYPE_BANNER, bannerList = mBannerList))
//                }
//
//                list.add(ZXFakeData(title = "权威发布", type = ZXTYPE.TYPE_TITLE))
//                repeat(10) {
//                    list.add(ZXFakeData())
//                }
//                list.add(ZXFakeData(title = "办事指南", type = ZXTYPE.TYPE_TITLE))
//                repeat(10) {
//                    list.add(ZXFakeData())
//                }
//            }
//
//        }
//
//        return list
//
//    }
//
//    override fun getLayoutId(): Int = R.layout.fragment_zx2
//    override fun updateArgumentsData(parentCategory: FenleiAct1CateBean1) {
//        if (this::mAdapter.isInitialized) {
//            mAdapter.setNewData(initFakeData(parentCategory))
//        }
//
//    }
//
//    override fun onReceiveMsg(intent: Intent) {
//
//    }
//}