package com.spark.peak.ui.mine.feedback

import android.Manifest
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.ui.dialog.SelectedDialog
import com.spark.peak.utlis.ImageSeletedUtil
import com.spark.peak.utlis.PermissionUtlis
import kotlinx.android.synthetic.main.activity_feedback.*
import org.jetbrains.anko.ctx
import java.io.File

/**
 * 创建者：
 * 时间：
 */
class FeedbackActivity(override val layoutResId: Int = R.layout.activity_feedback)
    : LifeActivity<FeedbackPresenter>() {
    override val presenter by lazy { FeedbackPresenter(this) }
    private val files = mutableListOf<File>()
    private val phoneDialog by lazy {
        SelectedDialog("拍照", "相册", this) {
            ImageSeletedUtil.phoneClick(it, this, false)
        }
    }

    override fun configView() {
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

    fun addImg(v: View) {
//         : 2018/4/19 15:29 霍述雷 添加图片
        if (v.isEnabled)
            PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                phoneDialog.show()
            }
    }

    fun clear_1(v: View) {
        files.removeAt(0)
        showImg(files)
    }

    fun clear_2(v: View) {
        files.removeAt(1)
        showImg(files)

    }

    fun clear_3(v: View) {
        files.removeAt(2)
        showImg(files)

    }

    fun submit(v: View) {
        // TODO: 2018/4/19 15:29 霍述雷 提交
        val content = et_code.text.toString().trim()
        if (content.isBlank()) {
            return
        }

        if (files.isEmpty()) {
            presenter.feedback(content, null) {
                mToast("反馈成功")
                onBackPressed()
            }
        } else {
            presenter.upload(content, files) {
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
    }
}