package com.spark.peak.ui.mine.user

import com.geek.libutils.app.BaseApp
import com.spark.peak.MyApp
import com.spark.peak.base.BasePresenter
import com.spark.peak.base.EventMsg
import com.spark.peak.base.OnProgress
import com.spark.peak.utlis.SpUtil
import okhttp3.MediaType
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

/**
 * 创建者：
 * 时间：
 */
class UserPresenter(progress: OnProgress) : BasePresenter(progress) {
//    fun upLoadImg(path: String, onNext: () -> Unit) {
//        api.getQiNiuToken().sub(onNext = {
//            QiNIuUtils.getUploadManager()?.put(path,
//                    UUID.randomUUID().toString().replace("-".toRegex(), "") + ".png",
//                    it.body.token,
//                    { key, info, _ ->
//                        if (info.isOK) {
//                            changeUserInfo(UserInfo(img = "http://kaoyancihui.sparke.cn/$key"), onNext)
//                        } else {
//                            App.instance.runOnUiThread {
//                                Toast.makeText(App.instance, info.error, Toast.LENGTH_SHORT).show()
//                            }
//                        }
//
//                    }, null)
//        })
//    }

    fun upLoadImg(file: File, onNext: () -> Unit) {
//        val newFile = Compressor(App.instance.application)
//                .setQuality(50)
//                .compressToFile(file)
//        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), newFile)
//        val part = MultipartBody.Part.createFormData("file", newFile.name, requestBody)
//        val uuid = UUID.randomUUID().toString().replace("-".toRegex(), "") + ".png"
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

                override fun onSuccess(index: Int, file: File?) {
                    file?.let {
//                        val requestBody =
//                            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), it)
                        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), it)
                        val part = MultipartBody.Part.createFormData("file", it.name, requestBody)
                        api.uploadTopImage(part).sub({
                            userInfo {
                                SpUtil.userInfo = it
                                EventBus.getDefault().post(EventMsg("home", -1))
                                onNext()
                            }
                        })
                    }
                }
            })
            .launch()

    }


}