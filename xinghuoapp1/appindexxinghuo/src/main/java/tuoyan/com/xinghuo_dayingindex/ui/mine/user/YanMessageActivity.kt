package tuoyan.com.xinghuo_dayingindex.ui.mine.user

import android.content.Intent
import android.graphics.Bitmap
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.request.target.BitmapImageViewTarget
import kotlinx.android.synthetic.main.activity_yan_message.*
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.FeedbackQuestion
import tuoyan.com.xinghuo_dayingindex.bean.YanMessage
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tuoyan.com.xinghuo_dayingindex.utlis.RegularUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil

class YanMessageActivity : LifeActivity<UserPresenter>() {
    override val presenter: UserPresenter
        get() = UserPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_yan_message
    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    private var yanMsg = YanMessage()

    override fun configView() {
        super.configView()
        val userInfo = SpUtil.userInfo
        Glide.with(this)
            .asBitmap()
            .load(userInfo.img)
            .placeholder(R.mipmap.ic_avatar)
            .error(R.mipmap.ic_avatar)
            .centerCrop()
            .into(object : BitmapImageViewTarget(img_header) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(view.context.resources, resource)
                    circularBitmapDrawable.isCircular = true
                    view.setImageDrawable(circularBitmapDrawable)
                }
            })
        tv_nick_name.text = userInfo.name
        et_phone.setText(userInfo.phone)
    }

    override fun initData() {
        super.initData()
        if (!key.isNullOrEmpty() && key != "0") {
            tv_cancel.visibility = View.GONE
            val params = ConstraintLayout.LayoutParams(DeviceUtil.dp2px(this, 200f).toInt(), DeviceUtil.dp2px(this, 44f).toInt())
            params.topToBottom = R.id.ctl_content
            params.topMargin = DeviceUtil.dp2px(this, 25f).toInt()
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            tv_save.layoutParams = params
            presenter.getPostgraduateRecord(key) {
                yanMsg = it
                updateMessage(it)
                initFocus()
            }
        } else {
            initFocus()
        }
    }

    private fun updateMessage(yan: YanMessage) {
        et_name.setText(yan.name)
        et_phone.setText(yan.phone)
        et_school.text = yan.currentSchool
        et_2_school.text = yan.applySchool
        et_project.text = yan.applyMajor
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        et_name.setOnFocusChangeListener { view, b ->
            if (et_name.text.isNullOrEmpty()) {
                if (b) {
                    tv_name.visibility = View.VISIBLE
                    et_name.gravity = Gravity.CENTER_VERTICAL
                    et_name.hint = ""
                } else {
                    tv_name.visibility = View.GONE
                    et_name.gravity = Gravity.CENTER
                    et_name.hint = "真实姓名"
                }
            } else {
                tv_name.visibility = View.VISIBLE
                et_name.gravity = Gravity.CENTER_VERTICAL
                et_name.hint = ""
            }
        }
        et_phone.setOnFocusChangeListener { view, b ->
            if (et_phone.text.isNullOrEmpty()) {
                if (b) {
                    tv_phone.visibility = View.VISIBLE
                    et_phone.gravity = Gravity.CENTER_VERTICAL
                    et_phone.hint = ""
                } else {
                    tv_phone.visibility = View.GONE
                    et_phone.gravity = Gravity.CENTER
                    et_phone.hint = "手机号码"
                }
            } else {
                tv_phone.visibility = View.VISIBLE
                et_phone.gravity = Gravity.CENTER_VERTICAL
                et_phone.hint = ""
            }
        }
        ctl_school.setOnClickListener {
            val intent = Intent(this, SearchSchoolActivity::class.java)
            intent.putExtra(SearchSchoolActivity.TYPE, SearchSchoolActivity.SCHOOL)
            startActivityForResult(intent, 400)
        }
        ctl_2_school.setOnClickListener {
            val intent = Intent(this, SearchSchoolActivity::class.java)
            intent.putExtra(SearchSchoolActivity.TYPE, SearchSchoolActivity.SCHOOL)
            startActivityForResult(intent, 401)
        }
        ctl_project.setOnClickListener {
            val intent = Intent(this, SearchSchoolActivity::class.java)
            intent.putExtra(SearchSchoolActivity.TYPE, SearchSchoolActivity.MAJOR)
            startActivityForResult(intent, 402)
        }
        tv_cancel.setOnClickListener {
            presenter.ignorePostgraduateRecord {
                onBackPressed()
            }
        }
        tv_save.setOnClickListener {
            if (et_name.text.isNullOrEmpty()) {
                Toast.makeText(this, "请完善考研档案", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (!RegularUtils.isMobileSimple(et_phone.text.toString())) {
                Toast.makeText(this, "手机号格式不正确", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (et_school.text.isNullOrEmpty()) {
                Toast.makeText(this, "请完善考研档案", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (et_2_school.text.isNullOrEmpty()) {
                Toast.makeText(this, "请完善考研档案", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (et_project.text.isNullOrEmpty()) {
                Toast.makeText(this, "请完善考研档案", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            yanMsg.name = et_name.text.toString()
            yanMsg.phone = et_phone.text.toString()
            presenter.postgraduateRecord(yanMsg) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        }
    }

    private fun initFocus() {
        if (et_name.text.isNullOrEmpty()) {
            tv_name.visibility = View.GONE
            et_name.gravity = Gravity.CENTER
        } else {
            tv_name.visibility = View.VISIBLE
            et_name.gravity = Gravity.CENTER_VERTICAL
        }
        if (et_phone.text.isNullOrEmpty()) {
            tv_phone.visibility = View.GONE
            et_phone.gravity = Gravity.CENTER
        } else {
            tv_phone.visibility = View.VISIBLE
            et_phone.gravity = Gravity.CENTER_VERTICAL
        }
        if (et_school.text.isNullOrEmpty()) {
            tv_school.visibility = View.GONE
            et_school.gravity = Gravity.CENTER
        } else {
            tv_school.visibility = View.VISIBLE
            et_school.gravity = Gravity.CENTER_VERTICAL
            val params = ConstraintLayout.LayoutParams(0, 0)
            params.rightToLeft = R.id.img_right
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            params.rightMargin = DeviceUtil.dp2px(this, 5f).toInt()
            et_school.setPadding(DeviceUtil.dp2px(this, 25f).toInt(), 0, 0, 0)
            et_school.layoutParams = params
        }
        if (et_2_school.text.isNullOrEmpty()) {
            tv_2_school.visibility = View.GONE
            et_2_school.gravity = Gravity.CENTER
        } else {
            tv_2_school.visibility = View.VISIBLE
            et_2_school.gravity = Gravity.CENTER_VERTICAL
            val params = ConstraintLayout.LayoutParams(0, 0)
            params.rightToLeft = R.id.img_2_right
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            params.rightMargin = DeviceUtil.dp2px(this, 5f).toInt()
            et_2_school.setPadding(DeviceUtil.dp2px(this, 25f).toInt(), 0, 0, 0)
            et_2_school.layoutParams = params
        }
        if (et_project.text.isNullOrEmpty()) {
            tv_project.visibility = View.GONE
            et_project.gravity = Gravity.CENTER
        } else {
            tv_project.visibility = View.VISIBLE
            et_project.gravity = Gravity.CENTER_VERTICAL
            val params = ConstraintLayout.LayoutParams(0, 0)
            params.rightToLeft = R.id.img_3_right
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            params.rightMargin = DeviceUtil.dp2px(this, 5f).toInt()
            et_project.setPadding(DeviceUtil.dp2px(this, 25f).toInt(), 0, 0, 0)
            et_project.layoutParams = params
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val item = data?.getSerializableExtra("item") as FeedbackQuestion
            when (requestCode) {
                400 -> {
                    yanMsg.currentSchool = item.name
                    yanMsg.currentSchoolId = item.code
                    et_school.text = item.name
                    et_school.gravity = Gravity.CENTER_VERTICAL
                    tv_school.visibility = View.VISIBLE
                    val params = ConstraintLayout.LayoutParams(0, 0)
                    params.rightToLeft = R.id.img_right
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    params.rightMargin = DeviceUtil.dp2px(this, 5f).toInt()
                    et_school.setPadding(DeviceUtil.dp2px(this, 25f).toInt(), 0, 0, 0)
                    et_school.layoutParams = params
                }
                401 -> {
                    yanMsg.applySchool = item.name
                    yanMsg.applySchoolId = item.code
                    et_2_school.text = item.name
                    et_2_school.gravity = Gravity.CENTER_VERTICAL
                    tv_2_school.visibility = View.VISIBLE
                    val params = ConstraintLayout.LayoutParams(0, 0)
                    params.rightToLeft = R.id.img_2_right
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    params.rightMargin = DeviceUtil.dp2px(this, 5f).toInt()
                    et_2_school.setPadding(DeviceUtil.dp2px(this, 25f).toInt(), 0, 0, 0)
                    et_2_school.layoutParams = params
                }
                402 -> {
                    yanMsg.applyMajor = item.name
                    yanMsg.applyMajorId = item.code
                    et_project.text = item.name
                    et_project.gravity = Gravity.CENTER_VERTICAL
                    tv_project.visibility = View.VISIBLE
                    val params = ConstraintLayout.LayoutParams(0, 0)
                    params.rightToLeft = R.id.img_3_right
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    params.rightMargin = DeviceUtil.dp2px(this, 5f).toInt()
                    et_project.setPadding(DeviceUtil.dp2px(this, 25f).toInt(), 0, 0, 0)
                    et_project.layoutParams = params
                }
            }
        }
    }

    companion object {
        const val KEY = "KEY"
    }
}