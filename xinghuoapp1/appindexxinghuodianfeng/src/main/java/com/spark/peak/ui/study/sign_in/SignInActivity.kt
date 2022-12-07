package com.spark.peak.ui.study.sign_in

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.Glide

import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.SignInfo
import com.spark.peak.ui.study.StudyPresenter
import com.spark.peak.ui.study.adapter.SignInListAdapter
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_sing_indf.*
import org.jetbrains.anko.ctx

class SignInActivity : LifeActivity<StudyPresenter>() {
    override val presenter: StudyPresenter
        get() = StudyPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_sing_indf

    private var signInfo : SignInfo ?= null
    val oAdapter by lazy { SignInListAdapter{
        onLoadMore()
    } }
    var page = 1
    val step = 15

    companion object {
        const val SIGN_INFO = "sign_info"
    }
    override fun configView() {
        setSupportActionBar(tb_sign_in)
        rv_sign_in.layoutManager = LinearLayoutManager(ctx)
        Glide.with(this)
                .asBitmap()
                .load(SpUtil.userInfo.img)
                .placeholder(R.mipmap.ic_avatar)
                .error(R.mipmap.ic_avatar)
                .centerCrop()
                .into(object : BitmapImageViewTarget(iv_header) {
                    override fun setResource(resource: Bitmap?) {
                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(view.context.resources, resource)
                        circularBitmapDrawable.isCircular = true
                        view.setImageDrawable(circularBitmapDrawable)
                    }
                })
    }

    override fun initData() {
        signInfo = intent.getSerializableExtra(SIGN_INFO) as SignInfo
        setInfos()

        page = 1
        presenter.getSignList(page,step){
            oAdapter.setData(it)
            oAdapter.setMore(true)
            rv_sign_in.adapter = oAdapter
        }

    }

    override fun handleEvent() {
        tb_sign_in.setNavigationOnClickListener {
            onBackPressed()
        }

        btn_sign_in.setOnClickListener {
            btn_sign_in.isEnabled = false
            presenter.sign {
                refreshSignInfo()
                onRefresh() // 打卡完成后，刷新打卡排行数据
            }
        }

        srl_signin.setOnRefreshListener {
            onRefresh()
        }
    }

    /**
     * 显示打卡相关信息
     */
    @SuppressLint("SetTextI18n")
    private fun setInfos(){
        signInfo?.let {
            if (it.iscard == 1){
                btn_sign_in.text = "今日已打卡"
                btn_sign_in.isEnabled = false
            }
            tv_user_name.text = SpUtil.userInfo.name + if (it.rowno == "0") "" else (" No."+ it.rowno)
            tv_sign_continue.text = it.keep.toString()
            tv_sign_count.text = it.total.toString()
            tv_sign_forget.text = it.unpunched.toString()
        }
    }

    private fun refreshSignInfo(){
        presenter.getSignInfo {
            signInfo = it
            setInfos()
        }
    }

    private fun onRefresh(){
        page = 1
        presenter.getSignList(page,step){
            if (srl_signin.isRefreshing) srl_signin.isRefreshing = false
            oAdapter.setData(it)
            oAdapter.setMore(true)
            rv_sign_in.adapter = oAdapter
        }
    }

    fun onLoadMore(){
        page++
        presenter.getSignList(page,step){
            if (it.isEmpty()){
                oAdapter.setMore(false)
                oAdapter.notifyDataSetChanged()
            }else{
                oAdapter.addData(it)
            }
        }
    }
}
