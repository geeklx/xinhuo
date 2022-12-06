package tuoyan.com.xinghuo_dayingindex.utlis

import android.Manifest
import android.app.Activity
import android.app.Fragment
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import tuoyan.com.xinghuo_dayingindex.ui.dialog.SelectedDialog
import tuoyan.com.xinghuo_dayingindex.utlis.log.L
import org.jetbrains.anko.act
import org.jetbrains.anko.ctx
import java.io.File
import java.util.*

/**
 * 创建者：
 * 时间：  2018/6/7.
 */
class WebCameraHelper {

    /**
     * 图片选择回调
     */
    var mUploadMessage: ValueCallback<Uri>? = null
    var mUploadCallbackAboveL: ValueCallback<Array<Uri>>? = null

    //    lateinit var fileUri: Uri
    lateinit var photoURI: Uri
    lateinit var photoFile: File

    private object SingletonHolder {
        internal val INSTANCE = WebCameraHelper()
    }

    /**
     * 包含拍照和相册选择
     */
    fun showOptions(act: Activity) {
        val selectedDialog = SelectedDialog("拍照", "相册", act) {
            when (it) {
                1 -> { // : 2017/5/13 15:31 huoshulei 请求相机权限
//                //相机
                    PermissionUtlis.checkPermissions(act, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        toCamera(act)
                    }
                }
                2 -> {//相册
                    val i = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)// 调用android的图库
                    act.startActivityForResult(i,
                            TYPE_GALLERY)
                }

            }
        }
        selectedDialog.setOnCancelListener(ReOnCancelListener())
        selectedDialog.show()
//        val alertDialog = AlertDialog.Builder(act)
//        alertDialog.setOnCancelListener(ReOnCancelListener())
//        alertDialog.setTitle("选择")
//        alertDialog.setItems(arrayOf<CharSequence>("相机", "相册")
//        ) { dialog, which ->
//            if (which == 0) {
//
//                PermissionUtlis.checkPermissions(act, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
//                    toCamera(act)
//                }
//
//            } else {
//                val i = Intent(
//                        Intent.ACTION_PICK,
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)// 调用android的图库
//                act.startActivityForResult(i,
//                        TYPE_GALLERY)
//            }
//        }
//        alertDialog.show()
    }

    fun showOptions(act: Fragment) {
        val selectedDialog = SelectedDialog("拍照", "相册", act.act) {
            when (it) {
                1 -> { // : 2017/5/13 15:31 huoshulei 请求相机权限
//                //相机
                    PermissionUtlis.checkPermissions(act, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        toCamera(act)
                    }
                }
                2 -> {//相册
                    val i = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)// 调用android的图库
                    act.startActivityForResult(i,
                            TYPE_GALLERY)
                }

            }
        }
        selectedDialog.setOnCancelListener(ReOnCancelListener())
        selectedDialog.show()
//        val alertDialog = AlertDialog.Builder(act.activity)
//        alertDialog.setOnCancelListener(ReOnCancelListener())
//        alertDialog.setTitle("选择")
//        alertDialog.setItems(arrayOf<CharSequence>("相机", "相册")
//        ) { dialog, which ->
//            if (which == 0) {
//                PermissionUtlis.checkPermissions(act, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
//                    toCamera(act)
//                }
//
//            } else {
//                val i = Intent(
//                        Intent.ACTION_PICK,
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)// 调用android的图库
//                act.startActivityForResult(i,
//                        TYPE_GALLERY)
//            }
//        }
//        alertDialog.show()
    }

    /**
     * 点击取消的回调
     */
    private inner class ReOnCancelListener : DialogInterface.OnCancelListener {

        override fun onCancel(dialogInterface: DialogInterface) {
            if (mUploadMessage != null) {
                mUploadMessage!!.onReceiveValue(null)
                mUploadMessage = null
            }
            if (mUploadCallbackAboveL != null) {
                mUploadCallbackAboveL!!.onReceiveValue(null)
                mUploadCallbackAboveL = null
            }
        }
    }

//    /**
//     * 请求拍照
//     * @param act
//     */
//    fun toCamera(act: Activity) {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)// 调用android的相机
//        // 创建一个文件保存图片
//        fileUri = Uri.fromFile(FileManager.getImgFile(act.applicationContext))
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
//        act.startActivityForResult(intent, TYPE_CAMERA)
//    }
//
//    /**
//     * 请求拍照
//     * @param act
//     */
//    fun toCamera(act: Fragment) {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)// 调用android的相机
//        // 创建一个文件保存图片
//        fileUri = Uri.fromFile(FileManager.getImgFile(act.act))
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
//        act.startActivityForResult(intent, TYPE_CAMERA)
//    }

    private fun toCamera(act: Fragment) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(act.act.packageManager) != null) {
            val timeStamp = UUID.randomUUID().toString().replace("-".toRegex(), "")
            photoFile = File(act.ctx.externalCacheDir, "$timeStamp.png")
            if (Build.VERSION.SDK_INT < 24) {
                photoURI = Uri.fromFile(photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            } else {
                val contentValues = ContentValues(1)
                contentValues.put(MediaStore.Images.Media.DATA, photoFile.absolutePath)
                photoURI = act.ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }
            act.startActivityForResult(takePictureIntent, TYPE_CAMERA)
        }
    }

    private fun toCamera(act: Activity) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(act.packageManager) != null) {
            val timeStamp = UUID.randomUUID().toString().replace("-".toRegex(), "")
            photoFile = File(act.externalCacheDir, "$timeStamp.png")
            if (Build.VERSION.SDK_INT < 24) {
                photoURI = Uri.fromFile(photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            } else {
                val contentValues = ContentValues(1)
                contentValues.put(MediaStore.Images.Media.DATA, photoFile.absolutePath)
                photoURI = act.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }
            act.startActivityForResult(takePictureIntent, TYPE_CAMERA)
        }
    }

    /**
     * startActivityForResult之后要做的处理
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == TYPE_CAMERA) { // 相册选择
            if (resultCode == -1) {//RESULT_OK = -1，拍照成功
                L.d("onActivityResult: $photoURI")
                if (mUploadCallbackAboveL != null) { //高版本SDK处理方法
                    val uris = arrayOf(photoURI)
                    mUploadCallbackAboveL!!.onReceiveValue(uris)
                    mUploadCallbackAboveL = null
                } else if (mUploadMessage != null) { //低版本SDK 处理方法
                    mUploadMessage!!.onReceiveValue(photoURI)
                    mUploadMessage = null
                } else {
                    //                    Toast.makeText(CubeAndroid.this, "无法获取数据", Toast.LENGTH_LONG).show();
                }
            } else { //拍照不成功，或者什么也不做就返回了，以下的处理非常有必要，不然web页面不会有任何响应
                if (mUploadCallbackAboveL != null) {
                    mUploadCallbackAboveL!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent))
                    mUploadCallbackAboveL = null
                } else if (mUploadMessage != null) {
                    mUploadMessage!!.onReceiveValue(photoURI)
                    mUploadMessage = null
                } else {
                    //                    Toast.makeText(CubeAndroid.this, "无法获取数据", Toast.LENGTH_LONG).show();
                }

            }
        } else if (requestCode == TYPE_GALLERY) {// 相册选择
            if (mUploadCallbackAboveL != null) {
                mUploadCallbackAboveL!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent))
                mUploadCallbackAboveL = null
            } else if (mUploadMessage != null) {
                val result = if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            } else {
                //                Toast.makeText(CubeAndroid.this, "无法获取数据", Toast.LENGTH_LONG).show();
            }
        }
    }

    companion object {
        val instance = SingletonHolder.INSTANCE
        val TYPE_REQUEST_PERMISSION = 3
        val TYPE_CAMERA = 1
        val TYPE_GALLERY = 2
    }
}
