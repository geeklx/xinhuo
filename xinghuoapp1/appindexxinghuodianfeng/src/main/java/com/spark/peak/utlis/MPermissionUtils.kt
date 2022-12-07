package com.spark.peak.utlis

import android.annotation.TargetApi
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.geek.libutils.app.BaseApp
import com.spark.peak.MyApp

/**
 * Class desc:
 *
 *
 * 6.0 运行时权限处理工具类。
 */
object MPermissionUtils {

    private var mRequestCode = -1

    fun requestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        permission: Array<String>,
        callback: OnPermissionListener
    ) {
        requestPermissions(activity, requestCode, permission, callback)
    }

    fun requestPermissionsResult(
        fragment: android.app.Fragment,
        requestCode: Int,
        permission: Array<String>,
        callback: OnPermissionListener
    ) {
        requestPermissions(fragment, requestCode, permission, callback)
    }

    fun requestPermissionsResult(
        fragment: androidx.fragment.app.Fragment,
        requestCode: Int,
        permission: Array<String>,
        callback: OnPermissionListener
    ) {
        requestPermissions(fragment, requestCode, permission, callback)
    }

    /**
     * 请求权限处理

     * @param object      activity or fragment
     * *
     * @param requestCode 请求码
     * *
     * @param permissions 需要请求的权限
     * *
     * @param callback    结果回调
     */
    @TargetApi(Build.VERSION_CODES.M)
    private fun requestPermissions(
        `object`: Any,
        requestCode: Int,
        permissions: Array<String>,
        callback: OnPermissionListener
    ) {

        checkCallingObjectSuitability(`object`)
        mOnPermissionListener = callback

        if (checkPermissions(getContext(`object`), *permissions)) {
            if (mOnPermissionListener != null)
                mOnPermissionListener!!.onPermissionGranted()
        } else {
            val deniedPermissions = getDeniedPermissions(getContext(`object`), *permissions)
            if (deniedPermissions.isNotEmpty()) {
                mRequestCode = requestCode
                if (`object` is Activity) {
                    `object`.requestPermissions(
                        deniedPermissions
                            .toTypedArray(), requestCode
                    )
                } else if (`object` is android.app.Fragment) {
                    `object`.requestPermissions(
                        deniedPermissions
                            .toTypedArray(), requestCode
                    )
                } else if (`object` is androidx.fragment.app.Fragment) {
                    `object`.requestPermissions(
                        deniedPermissions
                            .toTypedArray(), requestCode
                    )
                } else {
                    mRequestCode = -1
                }
            }
        }
    }

    /**
     * 获取上下文
     */
    private fun getContext(`object`: Any): Context {
        return when (`object`) {
            is Fragment -> `object`.activity
//            is androidx.fragment.app.Fragment -> MyApp.instance
            is androidx.fragment.app.Fragment -> BaseApp.get()
            else -> `object` as Activity
        }
    }

    /**
     * 请求权限结果，对应onRequestPermissionsResult()方法。
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (mRequestCode != -1 && requestCode == mRequestCode) {
            if (verifyPermissions(grantResults)) {
                if (mOnPermissionListener != null)
                    mOnPermissionListener!!.onPermissionGranted()
            } else {
                if (mOnPermissionListener != null)
                    mOnPermissionListener!!.onPermissionDenied()
            }
        }
    }

    /**
     * 显示提示对话框
     */
    fun showTipsDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("提示信息")
            .setMessage("当前应用缺少必要权限，该功能暂时无法使用。如若需要，请单击【确定】按钮前往设置中心进行权限授权。")
            .setNegativeButton("取消", null)
            .setPositiveButton("确定") { dialog, which -> startAppSettings(context) }.show()
    }

    /**
     * 启动当前应用设置页面
     */
    private fun startAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }

    /**
     * 验证权限是否都已经授权
     */
    fun verifyPermissions(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 获取权限列表中所有需要授权的权限

     * @param context     上下文
     * *
     * @param permissions 权限列表
     * *
     * @return
     */
    private fun getDeniedPermissions(context: Context, vararg permissions: String): List<String> {
        val deniedPermissions = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            ) {
                deniedPermissions.add(permission)
            }
        }
        return deniedPermissions
    }

    /**
     * 检查所传递对象的正确性

     * @param object 必须为 activity or fragment
     */
    private fun checkCallingObjectSuitability(`object`: Any?) {
        if (`object` == null) {
            throw NullPointerException("Activity or Fragment should not be null")
        }

        val isActivity = `object` is Activity
        val isSupportFragment = `object` is androidx.fragment.app.Fragment
        val isAppFragment = `object` is android.app.Fragment

        if (!(isActivity || isSupportFragment || isAppFragment)) {
            throw IllegalArgumentException(
                "Caller must be an Activity or a Fragment"
            )
        }
    }

    /**
     * 检查所有的权限是否已经被授权

     * @param permissions 权限列表
     * *
     * @return
     */
    fun checkPermissions(context: Context, vararg permissions: String): Boolean {
        if (isOverMarshmallow) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * 判断当前手机API版本是否 >= 6.0
     */
    private val isOverMarshmallow: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    interface OnPermissionListener {
        fun onPermissionGranted()

        fun onPermissionDenied()
    }

    private var mOnPermissionListener: OnPermissionListener? = null

}