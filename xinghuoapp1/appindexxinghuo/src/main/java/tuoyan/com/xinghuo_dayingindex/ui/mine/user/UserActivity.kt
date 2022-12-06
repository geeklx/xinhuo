package tuoyan.com.xinghuo_dayingindex.ui.mine.user
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_user.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent
import tuoyan.com.xinghuo_dayingindex.*
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.ui.common.changePhone.ChangePhoneActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.GradeDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.SelectedDialog
import tuoyan.com.xinghuo_dayingindex.ui.mine.user.nickname.ChangeNicknameActivity
import tuoyan.com.xinghuo_dayingindex.ui.mine.user.sign.ChangeSignActivity
import tuoyan.com.xinghuo_dayingindex.utlis.ImageSeletedUtil
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 创建者：
 * 时间：
 */
class UserActivity(override val layoutResId: Int = R.layout.activity_user) : LifeActivity<UserPresenter>() {
    override val presenter by lazy { UserPresenter(this) }

    private val phoneDialog by lazy {
        SelectedDialog("拍照", "从相册选择", this) {
            ImageSeletedUtil.phoneClick(it, this)
        }
    }
    private val sexDialog by lazy {
        SelectedDialog("男", "女", this) {
            when (it) {
                1 -> presenter.changeUserInfo(mutableMapOf("sex" to "男")) {
                    updateUserInfo()
                }
                2 -> presenter.changeUserInfo(mutableMapOf("sex" to "女")) {
                    updateUserInfo()
                }
            }
        }
    }
    private val gradeDialog by lazy {
        GradeDialog("四级", "六级", "考研", "专四", "专八", this) {
            tv_grade.text = it
            when (it) {
                "四级" -> {
                    presenter.changeUserInfo(mutableMapOf("grade" to GRAD_KEY_CET4)) {
                        updateUserInfo()
                    }
                }
                "六级" -> {
                    presenter.changeUserInfo(mutableMapOf("grade" to GRAD_KEY_CET6)) {
                        updateUserInfo()
                    }
                }
                "考研" -> {
                    presenter.changeUserInfo(mutableMapOf("grade" to GRAD_KEY_YAN)) {
                        updateUserInfo()
                    }
                }
                "专四" -> {
                    presenter.changeUserInfo(mutableMapOf("grade" to GRAD_KEY_TEM4)) {
                        updateUserInfo()
                    }
                }
                "专八" -> {
                    presenter.changeUserInfo(mutableMapOf("grade" to GRAD_KEY_TEM8)) {
                        updateUserInfo()
                    }
                }
            }
        }
    }
    private val brithdayDialog by lazy {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        val selectedDate = Calendar.getInstance()
        startDate.set(1970, 0, 1)
        endDate.set(2018, 11, 31)
        selectedDate.set(1990, 0, 1)
        val picker = TimePickerBuilder(ctx) { d, v ->
            val format = SimpleDateFormat("yyyy/MM/dd")
//            tv_birthday.text = format.format(d)
            presenter.changeUserInfo(mutableMapOf("birth" to format.format(d))) {
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
            .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
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
        ll_yan.setOnClickListener {
            val userInfo = SpUtil.userInfo
            val intent = Intent(this, YanMessageActivity::class.java)
            intent.putExtra(YanMessageActivity.KEY, userInfo.postgraduateRecordKey)
            startActivity(intent)
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
        tv_nickname.text = userInfo.name
        tv_birthday.text = userInfo.birth
        tv_sex.text = userInfo.sex
        userInfo.phone?.let {
            tv_phone.text = "${it.replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2")}"
//            tv_phone.text = "$it"
        }//340
//        tv_grade.text = GradUtil.parseGradStr(userInfo.sectionname ?: "", userInfo.gradename ?: "")
        tv_grade.text = userInfo.gradename
        tv_signature.text = userInfo.signature
        if (GRAD_KEY_YAN == userInfo.grade) {
            ll_yan.visibility = View.VISIBLE
        } else {
            ll_yan.visibility = View.GONE
        }
        if (userInfo.postgraduateRecordKey.isNullOrEmpty()) {
            tv_yan.text = "完善考研档案"//查看考研档案
        } else {
            tv_yan.text = "查看考研档案"//查看考研档案
        }
    }

    @SensorsDataTrackViewOnClick
    fun clickAvatar(v: View) {
// : 2018/4/16 11:23  更新头像
        PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            phoneDialog.show()
        }
    }

    @SensorsDataTrackViewOnClick
    fun clickNickname(v: View) {
        startActivity<ChangeNicknameActivity>()
// : 2018/4/16 11:23  nickname
    }


    @SensorsDataTrackViewOnClick
    fun clickBirthday(v: View) {
        brithdayDialog.show()
// : 2018/4/16 11:24  选择生日

    }

    @SensorsDataTrackViewOnClick
    fun clickSex(v: View) {
// : 2018/4/16 11:24  选择性别
        sexDialog.show()
    }

    @SensorsDataTrackViewOnClick
    fun clickGrade(v: View) {
//        startActivity<GradeActivity>()
        // : 2018/4/16 11:24  选择年级
        gradeDialog.show()

    }

    @SensorsDataTrackViewOnClick
    fun clickPhone(v: View) {
        startActivity<ChangePhoneActivity>(
            ChangePhoneActivity.TITLE to "验证手机号",
            ChangePhoneActivity.OLD to true
        )
        // : 2018/4/16 11:24  手机
    }

    @SensorsDataTrackViewOnClick
    fun clickSignature(v: View) {
        startActivity<ChangeSignActivity>()
        // : 2018/4/16 11:24  个性签名
    }


}