package com.geek.appindex.news.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.AppUtils
import com.geek.appindex.R
import com.geek.appindex.news.adapter.ZXAdapter
import com.geek.appindex.news.model.ZXConvertData
import com.geek.appindex.news.model.ZXTYPE
import com.geek.biz1.bean.BjyyBeanYewu3
import com.geek.biz1.bean.home.ClassificationListBean
import com.geek.biz1.presenter.home.ClassificationPresenter
import com.geek.biz1.view.home.ClassificationView
import com.geek.libbase.base.SlbBaseLazyFragmentNew
import com.geek.libbase.emptyview.EmptyViewNewNew
import com.just.agentweb.geek.fragment.AgentWebFragment

/**
 * 咨询展示2 可以展示ViewPager
 */
class TZGGFragment : SlbBaseLazyFragmentNew(), ClassificationView {

    private var mAdapter1: ZXAdapter? = null
    private var rv1: RecyclerView? = null
    private var ll: LinearLayout? = null
    private var emptyView: EmptyViewNewNew? = null
    private var mPresenter: ClassificationPresenter? = null

    companion object {
        fun getInstance(bean: BjyyBeanYewu3): TZGGFragment {
            val fragment = TZGGFragment()
            val bundle = Bundle()
            bundle.putSerializable("feileiBean", bean)
            fragment.arguments = bundle
            return fragment
        }
    }


    override fun getLayoutId(): Int = R.layout.fragment_tzgg

    override fun setup(rootView: View, savedInstanceState: Bundle?) {
        super.setup(rootView, savedInstanceState)
        initView(rootView)
    }

    private fun initView(rootView: View) {
        rv1 = rootView.findViewById(R.id.rv1)
        ll = rootView.findViewById(R.id.ll)
        emptyView = rootView.findViewById(R.id.emptyView)
        emptyView?.bind(ll)?.setRetryListener {
            loadData()
        }
        emptyView?.notices("暂无数据", "网络出了点问题", "正在加载…", "")
        mPresenter = ClassificationPresenter()
        mPresenter?.onCreate(this)
        mAdapter1 = ZXAdapter(mutableListOf())
        rv1?.adapter = mAdapter1
        onclickAdd()
    }

    fun onclickAdd() {
        mAdapter1?.setOnItemClickListener { _, _, position ->
//            mAdapter1?.data?.get(position)?.takeIf { it.type == ZXTYPE.TYPE_ITEM }?.let {
//                val url = mAdapter1?.data?.get(position)?.data?.url ?: ""
//                Log.e("aaaaaa", url)
//                if (url != null && "" != url && "null" != url) {
//                    val intent =
//                        Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.WeChatH5JsWebActivity")
//                    intent.putExtra(AgentWebFragment.URL_KEY, url)
//                    startActivity(intent)
//                }
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


    private fun loadData() {
        emptyView?.loading()
        mPresenter?.getClassificationData("1002", "1")
    }

    override fun initData() {
        mPresenter = ClassificationPresenter()
        mPresenter?.onCreate(this)
        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestory()
    }


    override fun initEvent() {
    }

    override fun onClassficationDataSuccess(bean: ClassificationListBean) {
        val datas = mutableListOf<ZXConvertData>()
        bean.data?.forEach {
            datas.add(
                ZXConvertData(
                    it.name,
                    it.belongId,
                    it.id,
                    it.belongTypeId,
                    it.notice,
                )
            )
            it.list?.forEach { classificationBean ->
                datas.add(ZXConvertData(classificationBean))
            }

        }
        if (datas.isEmpty()) {
            emptyView?.nodata()
        } else {
            emptyView?.success()
            mAdapter1?.setNewData(datas)
        }

        // emptyView?.errorNet()

    }

    override fun onClassficationDataNoData(msg: String?) {
        emptyView?.nodata()
    }

    override fun onClassficationDataFail(msg: String?) {
        emptyView?.errorNet()
    }


}