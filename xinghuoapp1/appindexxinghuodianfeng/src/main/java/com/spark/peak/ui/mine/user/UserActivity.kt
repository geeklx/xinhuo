package com.spark.peak.ui.mine.user

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.Glide
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.UserInfo
import com.spark.peak.ui.dialog.SelectedDialog
import com.spark.peak.ui.mine.changeNickname.ChangeNicknameActivity
import com.spark.peak.ui.mine.changeSign.ChangeSignActivity
import com.spark.peak.utlis.ImageSeletedUtil
import com.spark.peak.utlis.PermissionUtlis
import com.spark.peak.utlis.SpUtil
import kotlinx.android.synthetic.main.activity_userdf.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.wrapContent
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 创建者：
 * 时间：
 */
class UserActivity(override val layoutResId: Int = R.layout.activity_userdf)
    : LifeActivity<UserPresenter>() {
    override val presenter by lazy { UserPresenter(this) }

    private val phoneDialog by lazy {
        SelectedDialog("拍照", "相册", this) {
            ImageSeletedUtil.phoneClick(it, this)
        }
    }
    private val sexDialog by lazy {
        SelectedDialog("男", "女", this) {
            when (it) {
                1 -> presenter.changeUserInfo(UserInfo(sex = "男")) {
                    updateUserInfo()
                }
                2 -> presenter.changeUserInfo(UserInfo(sex = "女")) {
                    updateUserInfo()
                }
            }
        }
    }
    private val brithdayDialog by lazy {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        startDate.set(1990, 0, 1)
        endDate.set(2018, 11, 31)
        val picker = TimePickerBuilder(ctx) { d, v ->
            val format = SimpleDateFormat("yyyy/MM/dd")
//            tv_birthday.text = format.format(d)
            presenter.changeUserInfo(UserInfo(age = format.format(d))) {
                updateUserInfo()
            }
        }
                .setType(booleanArrayOf(true, true, true, false, false, false))// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("完成")//确认按钮文字
                .setContentTextSize(18)//滚轮文字大小
                .setTitleSize(15)//标题文字大小
//                .setTitleText("Title")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
//                .setTitleColor(Color.BLACK)//标题文字颜色
                .setDividerColor(resources.getColor(R.color.color_e6e6e6))
                .setTextColorOut(resources.getColor(R.color.color_a7a7a7))
                .setTextColorCenter(resources.getColor(R.color.color_1e1e1e))
                .setSubmitColor(resources.getColor(R.color.color_1482ff))//确定按钮文字颜色
                .setCancelColor(resources.getColor(R.color.color_1482ff))//取消按钮文字颜色
                .setTitleBgColor(resources.getColor(R.color.color_ffffff))//标题背景颜色 Night mode
                .setBgColor(resources.getColor(R.color.color_ffffff))//滚轮背景颜色 Night mode
//                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true)//是否显示为对话框样式
                .build()
        picker.dialog.window?.setGravity(Gravity.BOTTOM)
        val params = FrameLayout.LayoutParams(matchParent, wrapContent, Gravity.BOTTOM)
        params.leftMargin = 0
        params.rightMargin = 0
        picker.dialogContainerLayout.layoutParams = params
        picker
//        BirthdayDialog(this) { y, m, d ->
//            tv_birthday.text = "${y}年${m}月${d}日"
//        }

    }

    override fun configView() {
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        ctl_signature.setOnClickListener {
            startActivity<ChangeSignActivity>()
        }
    }

    override fun onResume() {
        super.onResume()
        updateUserInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImageSeletedUtil.onActivityResult(this, requestCode, resultCode, data) {
            it?.let {
                presenter.upLoadImg(File(it)) {
                    updateUserInfo()
                }
            }
        }
    }

    /**
     * 更新用户信息
     */
    @SuppressLint("SetTextI18n")
    private fun updateUserInfo() {
        val userInfo = SpUtil.userInfo
        Glide.with(this)
                .asBitmap()
                .load(userInfo.img)
                .placeholder(R.mipmap.ic_avatar)
                .error(R.mipmap.ic_avatar)
                .centerCrop()
                .into(object : BitmapImageViewTarget(iv_avatar) {
                    override fun setResource(resource: Bitmap?) {
                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(view.context.resources, resource)
                        circularBitmapDrawable.isCircular = true
                        view.setImageDrawable(circularBitmapDrawable)
                    }
                })
        tv_name.text = userInfo.name
        tv_birthday.text = userInfo.age
        userInfo.phone?.let { tv_phone.text = "$it" }
        tv_signature.text = userInfo.signature ?: "未填写"
    }

    fun clickAvatar(v: View) {
        PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            phoneDialog.show()
        }
    }

    fun clickNickname(v: View) {
        startActivity<ChangeNicknameActivity>()
    }


    fun clickBirthday(v: View) {
        brithdayDialog.show()
    }
}