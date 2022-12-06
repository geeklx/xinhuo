package tuoyan.com.xinghuo_dayingindex.utlis

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.view.Gravity
import org.jetbrains.anko.ctx

/**
 * 创建者：
 * 时间：  2018/1/4.
 */
object PermissionUtlis {

    var dialog: AlertDialog? = null

    fun showDialog(ctx: Context, vararg permissions: String) {
        var title = ""
        var content = ""
        if (permissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE) && permissions.contains(Manifest.permission.CAMERA)) {
            title = "存储空间和摄像头权限使用说明"
            content = "为你使用下载资料、头像信息和扫码功能，星火英语需要申请你存储空间权限和摄像头权限。允许后，你可以随时通过手机系统设置对授权进行管理。"
        } else if (permissions.contains(Manifest.permission.CAMERA)) {
            title = "摄像头权限使用说明"
            content = "为你使用扫码功能，星火英语需要申请你的摄像头权限。允许后，你可以随时通过手机系统设置对授权进行管理。"
        } else if (permissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            title = "存储空间权限使用说明"
            content = "为你使用下载资料、头像信息功能，星火英语需要申请你存储空间权限。允许后，你可以随时通过手机系统设置对授权进行管理。"
        }
        if (title.isNotEmpty()) {
            dialog = AlertDialog.Builder(ctx).setCancelable(false).setTitle(title).setMessage(content).create()
            dialog?.window?.setGravity(Gravity.TOP)
            dialog?.show()
        }
    }

    fun checkPermissions(
        ctx: Activity, vararg permissions: String, onCancel: () -> Unit = {}, function: () -> Unit
    ) {
        if (MPermissionUtils.checkPermissions(ctx, *permissions)) {
            function()
        } else {
            showDialog(ctx, *permissions)
            MPermissionUtils.requestPermissionsResult(ctx, 1, arrayOf(*permissions), object : MPermissionUtils.OnPermissionListener {
                override fun onPermissionGranted() {
                    dialog?.dismiss()
                    function()
                }

                override fun onPermissionDenied() {
                    dialog?.dismiss()
                    onCancel()
                    MPermissionUtils.showTipsDialog(ctx)
                }
            })
        }
    }

    fun checkPermissions(
        ctx: Fragment, vararg permissions: String, onCancel: () -> Unit = {}, function: () -> Unit
    ) {
        if (MPermissionUtils.checkPermissions(ctx.ctx, *permissions)) {
            function()
        } else {
            showDialog(ctx.activity, *permissions)
            MPermissionUtils.requestPermissionsResult(ctx, 1, arrayOf(*permissions), object : MPermissionUtils.OnPermissionListener {
                override fun onPermissionGranted() {
                    dialog?.dismiss()
                    function()
                }

                override fun onPermissionDenied() {
                    dialog?.dismiss()
                    onCancel()
                    MPermissionUtils.showTipsDialog(ctx.ctx)
                }
            })
        }
    }

    fun checkPermissions(
        ctx: androidx.fragment.app.Fragment, vararg permissions: String, onCancel: () -> Unit = {}, function: () -> Unit
    ) {
        if (MPermissionUtils.checkPermissions(ctx.requireContext(), *permissions)) {
            function()
        } else {
            showDialog(ctx.requireContext(), *permissions)
            MPermissionUtils.requestPermissionsResult(ctx, 1, arrayOf(*permissions), object : MPermissionUtils.OnPermissionListener {
                override fun onPermissionGranted() {
                    dialog?.dismiss()
                    function()
                }

                override fun onPermissionDenied() {
                    dialog?.dismiss()
                    onCancel()
                    MPermissionUtils.showTipsDialog(ctx.requireContext())
                }
            })
        }
    }
}