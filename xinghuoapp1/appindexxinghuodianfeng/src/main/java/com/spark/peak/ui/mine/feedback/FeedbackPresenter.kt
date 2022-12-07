package com.spark.peak.ui.mine.feedback

import com.geek.libutils.app.BaseApp
import com.spark.peak.MyApp
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Feedback
import okhttp3.MediaType
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

/**
 * 创建者：
 * 时间：
 */
class FeedbackPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun upload(content: String, files: List<File>, onNext: () -> Unit) {
        val imgs = mutableListOf<MultipartBody.Part>()
//        files.forEach {
//            val file = Compressor(App.instance.application)
//                    .setQuality(50)
//                    .compressToFile(it)
//            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
//            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
//            imgs.add(part)
//        }
        var size = 0
        luban(files) {
            it?.let {
//                val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), it)
                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), it)
                val part = MultipartBody.Part.createFormData("file", it.name, requestBody)
                imgs.add(part)
            }
            size++
            if (size == files.size) {
                api.uploadImage("FKGL", imgs).subs({
                    feedback(content, it.body["list"], onNext)
                })
            }
        }
    }

    fun feedback(content: String, imgs: Any?, onNext: () -> Unit) {
        api.feedback(Feedback(content, imgs)).subs({ onNext() })
    }

    fun luban(file: List<File>, onNext: (File?) -> Unit) {
//        Luban.with(MyApp.instance)
        Luban.with(BaseApp.get())
            .load(file)
            .ignoreBy(300)
            .setTargetDir(BaseApp.get().externalCacheDir?.path)
            .setCompressListener(object : OnCompressListener {
                override fun onError(index: Int,e: Throwable?) {
                }

                override fun onStart() {
                }

                override fun onSuccess(index: Int,file: File?) {
                    onNext(file)
                }
            })
            .launch()
    }
}