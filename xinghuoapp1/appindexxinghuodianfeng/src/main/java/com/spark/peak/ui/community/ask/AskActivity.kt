package com.spark.peak.ui.community.ask

import android.Manifest
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.spark.peak.base.LifeActivity
import com.spark.peak.R
import com.spark.peak.ui.dialog.SelectedDialog
import com.spark.peak.utlis.ImageSeletedUtil
import com.spark.peak.utlis.PermissionUtlis
import kotlinx.android.synthetic.main.activity_askdf.*
import org.jetbrains.anko.*
import java.io.File

/**
 * 创建者：
 * 时间：
 */
class AskActivity(override val layoutResId: Int = R.layout.activity_askdf)
    : LifeActivity<AskPresenter>() {
    override val presenter by lazy { AskPresenter(this) }
    private val phoneDialog by lazy {
        SelectedDialog("拍照", "相册", this) {
            ImageSeletedUtil.phoneClick(it, this)
        }
    }

    override fun configView() {

    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImageSeletedUtil.onActivityResult(this, requestCode, resultCode, data) {
            it?.let {
                when {
                    iv_img_1.visibility == View.GONE -> {
                        iv_img_1.visibility = View.VISIBLE
                        Glide.with(ctx).load(File(it)).into(iv_img_1)
                    }
                    iv_img_2.visibility == View.GONE -> {
                        iv_img_2.visibility = View.VISIBLE
                        Glide.with(ctx).load(File(it)).into(iv_img_2)
                    }
                    else -> {
                        Glide.with(ctx).load(File(it)).into(iv_add_img)
                        iv_add_img.isEnabled = false
                    }
                }
            }
        }
    }

    fun addImg(v: View) {
//         : 2018/4/19 15:29 霍述雷 添加图片
        if (v.isEnabled)
            PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                phoneDialog.show()
            }
    }

    fun askClass(v: View) {}
    fun belongBook(v: View) {}

}