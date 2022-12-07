package com.spark.peak.ui.mine


import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.Glide
import com.spark.peak.R
import com.spark.peak.base.LifeFragment
import com.spark.peak.servicePhone
import com.spark.peak.ui.common.grade.GradeActivity
import com.spark.peak.ui.message.MessageNoticeActivity
import com.spark.peak.ui.mine.collection.CollectionActivity
import com.spark.peak.ui.mine.offline.OfflineActivity
import com.spark.peak.ui.mine.setting.AboutActivity
import com.spark.peak.ui.mine.setting.SettingActivity
import com.spark.peak.ui.mine.user.UserActivity
import com.spark.peak.ui.wrongbook.WrongBookActivity
import com.spark.peak.utlis.PermissionUtlis
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.fragment_mine.*
import org.jetbrains.anko.support.v4.startActivity


class MineFragment : LifeFragment<MinePresenter>() {
    override val presenter = MinePresenter(this)
    override val layoutResId = R.layout.fragment_mine

    override fun initData() {
        if (SpUtil.isLogin) {
            updateUserInfo()
            presenter.getMsgReaded {
                img_msg.isSelected = "0" == it["readFlag"]
            }
        } else {
            img_msg.isSelected = false
            tv_name.text = "登录/注册"
            tv_data_completeness.text = "开启更多功能"
            iv_avatar.setImageResource(R.mipmap.ic_avatar)
            tv_grade_name.text = ""
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun updateUserInfo() {
        val userInfo = SpUtil.userInfo
        tv_data_completeness.text =
            if (userInfo.signature.isNullOrEmpty()) "个性签名" else userInfo.signature
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
        tv_grade_name.text = "${userInfo.sectionname}${userInfo.gradename}"
    }

    override fun handleEvent() {
        ctl_wrong.setOnClickListener {
            checkLogin {
                presenter.getSubjects(SpUtil.userInfo.grade ?: SpUtil.defaultGrade.id) {
                    startActivity<WrongBookActivity>(WrongBookActivity.SUBJECTS to it.list)
                }
            }
        }
        ctl_grade.setOnClickListener {
            checkLogin {
                startActivity<GradeActivity>()
            }
        }
        ctl_header.setOnClickListener {
            checkLogin {
                startActivity<UserActivity>()
            }
        }
//        ll_answer.setOnClickListener {
//            isLogin {
//                startActivity<AnswerActivity>()
//                // : 2018/4/14 15:59 霍述雷 问答
//            }
//        }
//        ll_circle.setOnClickListener {
//            isLogin {
//                startActivity<CircleActivity>()
//                // : 2018/4/14 15:59 霍述雷 圈子
//            }
//        }
//        ll_attention.setOnClickListener {
//            isLogin {
//                startActivity<AttentionActivity>()
//                // : 2018/4/14 15:59 霍述雷 关注
//            }
//        }
//        ll_fans.setOnClickListener {
//            isLogin {
//                startActivity<FansActivity>()
//                // : 2018/4/14 16:00 霍述雷 粉丝
//            }
//        }
        ctl_collect.setOnClickListener {
            checkLogin {
                startActivity<CollectionActivity>()
            }
        }
        ctl_down.setOnClickListener {
            checkLogin {
                PermissionUtlis.checkPermissions(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) {
                    startActivity<OfflineActivity>()
                }
            }
        }
//        tv_record.setOnClickListener {
//            isLogin {
//                startActivity<HistoryActivity>()
//                // : 2018/4/14 16:00 霍述雷 练习记录
//            }
//        }
//        ll_order.setOnClickListener {
//            isLogin {
//                startActivity<OrderActivity>()
//                // : 2018/4/14 16:00 霍述雷 订单
//            }
//        }
//        ll_voucher.setOnClickListener {
//            isLogin {
//                startActivity<CouponActivity>()
//                // : 2018/4/14 16:00 霍述雷 兑换券
//            }
//        }
//        tv_feedback.setOnClickListener {
//            isLogin { startActivity<FeedbackActivity>() }
//
//            // : 2018/4/14 16:00 霍述雷 帮助与反馈
//        }
        img_setting.setOnClickListener {
            checkLogin {
                startActivity<SettingActivity>()
            }
        }
        ctl_us.setOnClickListener {
            startActivity<AboutActivity>()
        }
        ctl_line.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$servicePhone"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        img_msg.setOnClickListener {
            checkLogin {
                startActivity<MessageNoticeActivity>()
            }
        }
    }
}
