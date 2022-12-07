package com.spark.peak.ui.scan

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.widget.Toast
import com.gyf.immersionbar.ImmersionBar
import com.spark.peak.R
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.LifeActivity
import com.spark.peak.utlis.UriUtils
import com.zxing.google.zxing.QRCodeParseUtils
import com.zxing.journeyapps.barcodescanner.CaptureManager
import com.zxing.journeyapps.barcodescanner.DecoratedBarcodeView
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_scanner.*
import java.io.File
import java.util.*


class ScannerActivity : LifeActivity<BasePresenter>(), DecoratedBarcodeView.TorchListener {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_scanner
    private val captureManager by lazy { CaptureManager(this, db_code) }
    private var isCrop = true
    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .fullScreen(true)
                .statusBarColor(android.R.color.transparent)
                .statusBarDarkFont(true)
                .init()
        fullScreen = true
        super.onCreate(savedInstanceState)
        captureManager.initializeFromIntent(intent, savedInstanceState)
        captureManager.decode()
    }

    override fun handleEvent() {
        ic_close.setOnClickListener {
            onBackPressed()
        }

        ic_flash.setOnClickListener {
            if (it.isSelected) db_code.setTorchOff() else db_code.setTorchOn()
            it.isSelected = !it.isSelected
        }
        ic_pic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            this.startActivityForResult(intent, 300)
        }
        ctl_scan_warming.setOnClickListener {
            //图书二维码说明
            startActivity(Intent(this, ScanExplainActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return db_code.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onTorchOn() {
    }

    override fun onTorchOff() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                300 -> if (isCrop) crop(data?.data, this) else compress(data?.data, this)
                200 -> scanFile(photoFile?.absolutePath!!)
            }
        }
    }

    private var photoFile: File? = null
    private var photoURI: Uri? = null
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
                intent.putExtra("outputX", 256)// 输出图片大小
                intent.putExtra("outputY", 256)
            }
            intent.putExtra("scale", true)
            intent.setDataAndType(uri, "image/*")// mUri是已经选择的图片Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            intent.putExtra("return-data", false)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
            act.startActivityForResult(intent, 200)
        }
    }

    private fun compress(uri: Uri?, act: Activity) {
        uri?.let {
            val path: String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                UriUtils.getPath(act, it)
            } else {
                UriUtils.uri2Path(act, it)
            }
            scanFile(path!!)
        }
    }

    private fun scanFile(path: String) {
        Observable.create(ObservableOnSubscribe<String> { emitter ->
            emitter.onNext(QRCodeParseUtils.syncDecodeQRCode(path))
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            if (it.isNullOrEmpty()) {
                Toast.makeText(this, "请适当移动裁剪框贴合二维码", Toast.LENGTH_LONG).show()
            } else {
                val resultIntent = Intent()
                resultIntent.putExtra("code", it)
                setResult(93, resultIntent)
                finish()
            }
        }
    }
}