package com.spark.peak.utlis

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.util.*

/**
 * 创建者： 霍述雷
 * 时间：  2018/4/23.
 */
object ImageSeletedUtil {
    private const val REQUEST_CODE_PICK_IMAGE = 0x00100//相册
    private const val REQUEST_TAKE_PHOTO = 0x00101//拍照
    private const val REQUEST_CROP_CODE = 0x00d//裁剪
    private var photoURI: Uri? = null
    private var photoFile: File? = null
    private var isCrop = true
    /**
     * 选择头像
     */
    fun phoneClick(position: Int, act: Activity, isCrop: Boolean = true) {
        this.isCrop = isCrop
        when (position) {
            1 -> { // : 2017/5/13 15:31 huoshulei 请求相机权限
//                //相机
                PermissionUtlis.checkPermissions(act, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    camera(act)
                }
            }
            2 -> {//相册
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                act.startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
            }

        }
    }

    private fun camera(act: Activity) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(act.packageManager) != null) {
            photoURI = createImageUri(act)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            act.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
//            val timeStamp = UUID.randomUUID().toString().replace("-".toRegex(), "")
//            photoFile = File(act.externalCacheDir, "$timeStamp.png")
//            if (Build.VERSION.SDK_INT < 24) {
//                photoURI = Uri.fromFile(photoFile)
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//            } else {
//                val contentValues = ContentValues(1)
//                contentValues.put(MediaStore.Images.Media.DATA, photoFile?.absolutePath)
//                photoURI = act.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//            }
//            act.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
        }
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private fun createImageUri(act: Activity): Uri? {
        var status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return act.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues());
        } else {
            return act.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, ContentValues());
        }
    }

    fun onActivityResult(act: Activity, requestCode: Int, resultCode: Int, data: Intent?, startUpload: (String?) -> Unit) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> crop(data?.data, act)
                REQUEST_TAKE_PHOTO -> crop(photoURI, act)
                REQUEST_CROP_CODE -> startUpload(photoFile?.absolutePath)
            }
        }
    }

    private fun crop(uri: Uri?, act: Activity) {
        uri?.let {
            val timeStamp = UUID.randomUUID().toString().replace("-".toRegex(), "")
            photoFile = File(act.externalCacheDir, "$timeStamp.png")
            photoURI = if (android.os.Build.VERSION.SDK_INT < 24) {
                Uri.fromFile(photoFile)
            } else {
                val contentValues = ContentValues(1)
                contentValues.put(MediaStore.Images.Media.DATA, photoFile?.absolutePath)
                act.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            }
            val intent = Intent()//启动裁剪页面
            intent.action = "com.android.camera.action.CROP"
            intent.putExtra("crop", "true")
            if (isCrop) {
                intent.putExtra("aspectX", 1)// 裁剪框比例
                intent.putExtra("aspectY", 1)
                intent.putExtra("outputX", 150)// 输出图片大小
                intent.putExtra("outputY", 150)
            }
            intent.putExtra("scale", true)
            intent.setDataAndType(uri, "image/*")// mUri是已经选择的图片Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            intent.putExtra("return-data", false)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
            act.startActivityForResult(intent, REQUEST_CROP_CODE)
        }
    }

}