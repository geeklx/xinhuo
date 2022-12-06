package tuoyan.com.xinghuo_dayingindex.ui.mine

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.gyf.immersionbar.ImmersionBar
import com.sensorsdata.analytics.android.sdk.SensorsDataFragmentTitle
import kotlinx.android.synthetic.main.fragment_mine.*
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.HomePageInfo
import tuoyan.com.xinghuo_dayingindex.servicePhone
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.message.MessageActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.collection.CollectionActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.coupon.CouponsActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.exam.ExamTimeActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.offline.OfflineActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.setting.AboutActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.setting.SettingActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.spoken.MineSpokenActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.user.UserActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.wrong.WrongActivity
import tuoyan.com.xinghuo_dayingindex.utlis.NetWorkUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.UdeskUtils


/**
 * 创建者：
 * 时间：  2018/9/12.
 */
@SensorsDataFragmentTitle(title = "我的页面")
class MineFragment : LifeV4Fragment<MinePresenter>() {
    private var homeInfo: HomePageInfo? = null
    override val presenter = MinePresenter(this)
    override val layoutResId = R.layout.fragment_mine

    private var dDialog: DDialog? = null

    override fun configView(view: View?) {
        initStatusBar()
        super.configView(view)
    }

    override fun onResume() {
        super.onResume()
        if (SpUtil.isLogin) {
//            tv_data_completeness.text = "成功总是坚持最后5分钟！！"
//            tv_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_edit_mine, 0)
            tv_go_info.text="个人信息"
            updateUserInfo()
        } else {
            tv_name.text = "登录/注册"
            tv_data_completeness.text = "开启更多功能"
            tv_wrong_num.text = ""
            tv_spoken_num.text=""
            tv_coupon_num.text = ""
            iv_avatar.setImageResource(R.mipmap.ic_avatar)
//            tv_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_login_mine, 0)
            tv_go_info.text=""
            tv_exam_time_num.text=""
        }
    }

    fun updateUserInfo() {
        val userInfo = SpUtil.userInfo
        Glide.with(this)
            .asBitmap()
            .load(userInfo.img)
            .placeholder(R.mipmap.ic_avatar)
            .error(R.mipmap.ic_avatar)
            .centerCrop()
            .into(object : BitmapImageViewTarget(iv_avatar) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(view.context.resources, resource)
                    circularBitmapDrawable.isCircular = true
                    view.setImageDrawable(circularBitmapDrawable)
                }
            })
        tv_name.text = userInfo.name
        tv_data_completeness.text=if (TextUtils.isEmpty(userInfo.signature))"Spark Your Dream" else userInfo.signature
        getPromotionalFlag()
        upMsg()
    }

    private fun getPromotionalFlag() {
        presenter.getPromotionalFlag() { map ->
            updated("1" == map["newPromotional"])
        }
    }

    fun updated(isShow: Boolean) {
        if (SpUtil.isLogin) {
            presenter.updated { info ->
                homeInfo = info
                tv_wrong_num.text = "${info.errQuestionCount ?: ""}题"
                tv_exam_time_num.text = "${info.examCount ?: ""}个"
                tv_spoken_num.text="${info.spokenCount  ?: ""}个"
                showOrHideCouponTag(isShow)
            }
        }
    }

    private fun showOrHideCouponTag(isShow: Boolean) {
        if (isShow) {
            img_coupons.visibility = View.VISIBLE
            tv_coupon_num.text = ""
        } else {
            img_coupons.visibility = View.GONE
            homeInfo?.let {
                tv_coupon_num.text = "${it.discountcount ?: ""}张"
            }
        }
    }

    fun upMsg() {
        presenter.ifNewFB {
        }
    }

    override fun handleEvent() {
        tv_go_info.setOnClickListener {
            // : 2018/4/14 15:59  个人主页
            isLogin {
                startActivity(Intent(activity, UserActivity::class.java))
            }
        }
        tv_name.setOnClickListener {
            // : 2018/4/14 15:59  个人主页
            isLogin {
                startActivity(Intent(activity, UserActivity::class.java))
            }
        }
        img_msg.setOnClickListener {
            if (!NetWorkUtils.isNetWorkReachable()) {
                mToast("请检查网络")
                return@setOnClickListener
            }
            isLogin {
                startActivity(Intent(activity, MessageActivity::class.java))
            }
        }
        tv_order.setOnClickListener {
            if (!NetWorkUtils.isNetWorkReachable()) {
                mToast("请检查网络")
                return@setOnClickListener
            }
            isLogin {
                var intent = Intent(activity, PostActivity::class.java)
                intent.putExtra(PostActivity.URL, "morders")
                startActivity(intent)
            }
        }
        tv_offline.setOnClickListener {
            //            toast("去往离线中线的列车马上发车了")
            isLogin {
                startActivity(Intent(activity, OfflineActivity::class.java))
            }
        }
        tv_collection.setOnClickListener {
            if (!NetWorkUtils.isNetWorkReachable()) {
                mToast("请检查网络")
                return@setOnClickListener
            }
            isLogin {
                startActivity(Intent(activity, CollectionActivity::class.java))
                // 收藏
            }
        }
        tv_address.setOnClickListener {
            if (!NetWorkUtils.isNetWorkReachable()) {
                mToast("请检查网络")
                return@setOnClickListener
            }
            isLogin {
                var intent = Intent(activity, PostActivity::class.java)
                intent.putExtra(PostActivity.URL, "address")
                startActivity(intent)
                // : 2018/9/18 13:30  收货地址
            }
        }
        ll_spoken.setOnClickListener {
            if (!NetWorkUtils.isNetWorkReachable()) {
                mToast("请检查网络")
                return@setOnClickListener
            }
            isLogin {
                startActivity(Intent(activity, MineSpokenActivity::class.java))

            }
        }
        ll_wrong.setOnClickListener {
            if (!NetWorkUtils.isNetWorkReachable()) {
                mToast("请检查网络")
                return@setOnClickListener
            }
            isLogin {
                startActivity(Intent(activity, WrongActivity::class.java))
                // : 2018/9/18 13:31  错题本
            }
        }
        ll_coupon.setOnClickListener {
            if (!NetWorkUtils.isNetWorkReachable()) {
                mToast("请检查网络")
                return@setOnClickListener
            }
            isLogin {
                showOrHideCouponTag(false)
                startActivity(Intent(activity, CouponsActivity::class.java))
                // : 2018/9/18 13:31  优惠券
            }
        }
        ll_exam_time.setOnClickListener {
            if (!NetWorkUtils.isNetWorkReachable()) {
                mToast("请检查网络")
                return@setOnClickListener
            }
            isLogin {
                startActivity(Intent(activity, ExamTimeActivity::class.java))

            }
        }
        ll_online.setOnClickListener {
            // : 2018/9/18 13:31  在线咨询
            isLogin {
                UdeskUtils.openChatView(activity)
            }
        }
        ll_service.setOnClickListener {
            // : 2018/9/18 13:31  客服热线  平板闪退
            try {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$servicePhone"))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } catch (e: Exception) {
                dDialog = DDialog(this.requireContext()).setWidth(250).setMessage("该设备无法在线拨打电话<br>客服热线：400 832 0009")
                    .setNegativeButton("确定") {
                        dDialog?.dismiss()
                    }.setPositiveButton("取消") {
                        dDialog?.dismiss()
                    }
                dDialog?.show()
            }
        }
        img_setting.setOnClickListener {
            startActivity(Intent(activity, SettingActivity::class.java))
            // : 2018/4/14 16:00  设置
        }
        ll_about_us.setOnClickListener {
            startActivity(Intent(activity, AboutActivity::class.java))
        }
    }

    private fun initStatusBar() {
        val params = top.layoutParams as ConstraintLayout.LayoutParams
        params.height = ImmersionBar.getStatusBarHeight(this.requireActivity())
    }
}
