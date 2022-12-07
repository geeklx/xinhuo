package com.spark.peak.utlis

import android.app.Activity
import androidx.fragment.app.Fragment

/**
 * 创建者： 霍述雷
 * 时间：  2018/1/4.
 */
object PermissionUtlis {
    fun checkPermissions(ctx: Activity, vararg permissions: String, onCancel: () -> Unit = {}, function: () -> Unit) {
        if (MPermissionUtils.checkPermissions(ctx, *permissions)) {
            function()
        } else {
            MPermissionUtils.requestPermissionsResult(ctx, 1,
                    arrayOf(*permissions),
                    object : MPermissionUtils.OnPermissionListener {
                        override fun onPermissionGranted() {
                            function()
                        }

                        override fun onPermissionDenied() {
                            onCancel()
                            MPermissionUtils.showTipsDialog(ctx)
                        }
                    })
        }
    }

    fun checkPermissions(ctx: Fragment, vararg permissions: String, onCancel: () -> Unit = {}, function: () -> Unit) {
        if (MPermissionUtils.checkPermissions(ctx.requireContext(), *permissions)) {
            function()
        } else {
            MPermissionUtils.requestPermissionsResult(ctx, 1,
                    arrayOf(*permissions),
                    object : MPermissionUtils.OnPermissionListener {
                        override fun onPermissionGranted() {
                            function()
                        }

                        override fun onPermissionDenied() {
                            onCancel()
                            MPermissionUtils.showTipsDialog(ctx.requireContext())
                        }
                    })
        }
    }

}