package com.geek.appindex.news.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.geek.appindex.R
import com.geek.appindex.news.adapter.MkFLunboAdapter1
import com.geek.appindex.news.adapter.ZXAdapter
import com.geek.appindex.news.model.ZXConvertData
import com.geek.appindex.news.model.ZXTYPE
import com.geek.biz1.bean.BjyyBeanYewu3
import com.geek.biz1.bean.home.ClassificationBean
import com.geek.biz1.bean.home.ClassificationListBean
import com.geek.biz1.presenter.home.ClassificationPresenter
import com.geek.biz1.view.home.ClassificationView
import com.geek.libbase.base.SlbBaseLazyFragmentNew
import com.geek.libbase.emptyview.EmptyViewNewNew
import com.just.agentweb.geek.fragment.AgentWebFragment
import com.youth.banner.Banner


/**
 * 咨询展示2 可以展示ViewPager
 */
class XZYWFragment : SlbBaseLazyFragmentNew(), ClassificationView {

    private var mAdapter1: ZXAdapter? = null
    private var mClTop1: ConstraintLayout? = null
    private var rv1: RecyclerView? = null
    private var emptyView: EmptyViewNewNew? = null
    private var clContainer: ConstraintLayout? = null
    private var banner: Banner<ClassificationBean, MkFLunboAdapter1>? = null

    //private var lbAdapter: MkFLunboAdapter1? = null
    private var mPresenter: ClassificationPresenter? = null

    companion object {
        fun getInstance(bean: BjyyBeanYewu3): XZYWFragment {
            val fragment = XZYWFragment()
            val bundle = Bundle()
            bundle.putSerializable("feileiBean", bean)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_xzyw

    override fun setup(rootView: View, savedInstanceState: Bundle?) {
        super.setup(rootView, savedInstanceState)
        rv1 = rootView.findViewById(R.id.rv1)
        mClTop1 = rootView.findViewById(R.id.cl_top1)
        banner = rootView.findViewById(R.id.banner)
        emptyView = rootView.findViewById(R.id.emptyView)
        clContainer = rootView.findViewById(R.id.cl_container)
        emptyView?.bind(clContainer)?.setRetryListener {
            loadData()
        }
        emptyView?.notices("暂无数据", "网络出了点问题", "正在加载…", "")
        initView()
    }


    private fun loadData() {
        emptyView?.loading()
        mPresenter?.getClassificationData("1003", "1")
    }

    private fun initView() {
        mAdapter1 = ZXAdapter(mutableListOf())
        rv1?.adapter = mAdapter1
        //lbAdapter = MkFLunboAdapter1(mutableListOf())
//        banner?.setAdapter(lbAdapter)
//        banner?.indicator = CircleIndicator(activity)
//        banner?.setIndicatorGravity(IndicatorConfig.Direction.RIGHT)
//        banner?.setIndicatorNormalColor(Color.parseColor("#C3C3C3"))
//        banner?.setIndicatorMargins(
//            IndicatorConfig.Margins(
//                0, 0,
//                BannerConfig.INDICATOR_MARGIN, BannerUtils.dp2px(12f)
//            )
//        )
//
//        banner?.setOnBannerListener { data, position ->
//            ToastUtils.showShort("当前点击的是$position")
//        }
        onclickAdd()
    }

    fun onclickAdd() {
        mAdapter1?.setOnItemClickListener { _, _, position ->
//            val url = mAdapter1?.data?.get(position)?.data?.url.toString();
//            Log.e("aaaaaa", url)
//            if (url != null && "" != url && "null" != url) {
//                val intent =
//                    Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity")
//                intent.putExtra(AgentWebFragment.URL_KEY, url)
//                startActivity(intent)
//            }
            handleItemClick(position)
        }

        mAdapter1?.setOnItemChildClickListener { _, _, position ->
//            val url = mAdapter1?.data?.get(position)?.data?.url.toString();
//            Log.e("aaaaaa", url)
//            if (url != null && "" != url && "null" != url) {
//                val intent =
//                    Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity")
//                intent.putExtra(AgentWebFragment.URL_KEY, url)
//                startActivity(intent)
//            }
            handleItemClick(position)
        }
    }

    fun handleItemClick(position: Int) {
        mAdapter1?.data?.get(position)?.takeIf { it.type == ZXTYPE.TYPE_ITEM }?.let {
            val url = mAdapter1?.data?.get(position)?.data?.url ?: ""
            Log.e("aaaaaa", url)
            if ("" != url && "null" != url) {
                val intent =
                    Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity")
                intent.putExtra(AgentWebFragment.URL_KEY, url)
                startActivity(intent)
            }
        }
    }

    override fun initData() {
        mPresenter = ClassificationPresenter()
        mPresenter?.onCreate(this)
//        val mBannerList = mutableListOf<BjyyBeanYewu3>()
//        mBannerList.add(
//            BjyyBeanYewu3(
//                "",
//                "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862286564142395.png",
//                "孙村吴翠莲，30年如一日照顾瘫痪婆婆",
//                "",
//                false
//            )
//        )
//        mBannerList.add(
//            BjyyBeanYewu3(
//                "",
//                "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521862378905937488.png",
//                "孙村吴翠莲，30年如一日照顾瘫痪婆婆",
//                "",
//                false
//            )
//        )
//        mBannerList.add(
//            BjyyBeanYewu3(
//                "",
//                "http://119.188.115.252:8090/resource-handle/uploads/image/2021-12-20/3521863886959548956.png",
//                "孙村吴翠莲，30年如一日照顾瘫痪婆婆",
//                "",
//                false
//            )
//        )
//        lbAdapter?.setDatas(mBannerList)
        loadData()
    }


    override fun initEvent() {
        mClTop1?.setOnClickListener {
            ToastUtils.showShort("点击1")
        }
    }

    override fun onClassficationDataSuccess(bean: ClassificationListBean?) {
        val datas = mutableListOf<ZXConvertData>()

        bean?.data?.forEachIndexed { index, classificationListData ->
            datas.add(
                ZXConvertData(
                    classificationListData.name,
                    classificationListData.belongId,
                    classificationListData.id,
                    classificationListData.belongTypeId,
                    classificationListData.notice,
                )
            )
            if (index == 0) {
                //第一条数据充当轮播数据
                classificationListData.list?.apply {
                    datas.add(ZXConvertData(this))
                }
            } else {
                classificationListData.list?.forEach {
                    datas.add(ZXConvertData(it))
                }
            }
        }
        if (datas.isEmpty()) {
            emptyView?.nodata()
        } else {
            emptyView?.success()
            mAdapter1?.setNewData(datas)
        }
        //emptyView?.errorNet()
    }

    override fun onClassficationDataNoData(msg: String?) {
        emptyView?.nodata()
    }

    override fun onClassficationDataFail(msg: String?) {
        emptyView?.errorNet()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestory()
    }

}