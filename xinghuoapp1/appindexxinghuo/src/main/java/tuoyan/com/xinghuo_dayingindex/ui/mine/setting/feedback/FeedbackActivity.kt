package tuoyan.com.xinghuo_dayingindex.ui.mine.setting.feedback

import android.Manifest
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_feedback.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.FeedbackQuestion
import tuoyan.com.xinghuo_dayingindex.ui.dialog.FeedbackDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.SelectedDialog
import tuoyan.com.xinghuo_dayingindex.utlis.ImageSeletedUtil
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import java.io.File

/**
 * 创建者：
 * 时间：
 */
class FeedbackActivity(override val layoutResId: Int = R.layout.activity_feedback)
    : LifeActivity<FeedbackPresenter>() {
    override val presenter by lazy { FeedbackPresenter(this) }
    private val files = mutableListOf<File>()
    private var feedback: FeedbackQuestion? = null
    val type by lazy { intent.getStringExtra("TYPE") ?: "1" }
    val dialog by lazy {
        FeedbackDialog(type, this@FeedbackActivity) {
            tv_type.text = it.name
            feedback = it
        }
    }
    private val phoneDialog by lazy {
        SelectedDialog("拍照", "相册", this) {
            ImageSeletedUtil.phoneClick(it, this, false)
        }
    }

    override fun configView() {
        if ("2" == type) {
            tv_title.text = "优化建议"
            tv_one.text = "优化类型"
            tv_two.text = "优化描述"
            tv_three.text = "优化截图"
        }
    }

    override fun initData() {
        super.initData()
        presenter.getDictInfo(type) {
            dialog.setData(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImageSeletedUtil.onActivityResult(this, requestCode, resultCode, data) {
            it?.let {
                val file = File(it)
                files.add(file)
                showImg(files)
            }
        }
    }

    private fun showImg(file: List<File>) {
//        if (file.isEmpty()) {
        fl_img_1.visibility = View.GONE
        fl_img_2.visibility = View.GONE
        fl_img_3.visibility = View.GONE
        iv_add_img.visibility = View.VISIBLE
//        }
        if (file.isNotEmpty()) {
            fl_img_1.visibility = View.VISIBLE
            Glide.with(ctx).load(file[0]).into(iv_img_1)
        }
        if (file.size > 1) {
            fl_img_2.visibility = View.VISIBLE
            Glide.with(ctx).load(file[1]).into(iv_img_2)
        }
        if (file.size > 2) {
            fl_img_3.visibility = View.VISIBLE
            iv_add_img.visibility = View.GONE
            Glide.with(ctx).load(file[2]).into(iv_img_3)
        }
    }

    @SensorsDataTrackViewOnClick
    fun addImg(v: View) {
//         : 2018/4/19 15:29  添加图片
        if (v.isEnabled)
            PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                phoneDialog.show()
            }
    }

    @SensorsDataTrackViewOnClick
    fun clear_1(v: View) {
        files.removeAt(0)
        showImg(files)
    }

    @SensorsDataTrackViewOnClick
    fun clear_2(v: View) {
        files.removeAt(1)
        showImg(files)

    }

    @SensorsDataTrackViewOnClick
    fun clear_3(v: View) {
        files.removeAt(2)
        showImg(files)

    }

    @SensorsDataTrackViewOnClick
    fun submit(v: View) {
        if (feedback == null) {
            toast("请选择问题类型")
            return
        }
        // TODO: 2018/4/19 15:29  提交
        val content = et_code.text.toString().trim()
        if (content.isBlank()) {
            toast("请输入问题描述")
            return
        }

        if (files.isEmpty()) {
            presenter.feedback(type, feedback!!.id, content, null) {
                mToast("反馈成功")
                onBackPressed()
            }
        } else {
            presenter.uploadFeedback(type, feedback!!.id, content, files) {
                mToast("反馈成功")
                onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        hideDialog()
        super.onDestroy()
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tv_type.setOnClickListener {
            dialog.show()
        }
    }
}