package tuoyan.com.xinghuo_dayingindex.ui.mine.setting.feedback

import android.os.Build
import tuoyan.com.xinghuo_dayingindex.YHJY
import tuoyan.com.xinghuo_dayingindex.YJFK
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.Feedback
import tuoyan.com.xinghuo_dayingindex.bean.FeedbackQuestion
import java.io.File

/**
 * 创建者：
 * 时间：
 */
class FeedbackPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun uploadFeedback(type: String, wtKey: String, content: String, files: List<File>, onNext: () -> Unit) {
//        val imgs = mutableListOf<MultipartBody.Part>()
//        files.forEach {
//            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), it)
//            val part = MultipartBody.Part.createFormData("file", it.name, requestBody)
//            imgs.add(part)
//        }
//        api.uploadImage("4", imgs).subs({
//            feedback(content, it.body["list"], onNext)
//        })
        upload("4", files) {
            feedback(type, wtKey, content, it, onNext)
        }
    }

    fun feedback(type: String, wtKey: String, content: String, imgs: Any?, onNext: () -> Unit) {
        val deviceInfo = "Product-" + Build.PRODUCT + "|VERSION_CODES.BASE-" + Build.VERSION_CODES.BASE + "|system-" + Build.DISPLAY + "|MODEL-" + Build.MODEL + "|BRAND-" + Build.BRAND + "|CPU_ABI-" + Build.CPU_ABI + "|SDK-" + Build.VERSION.SDK + "|VERSION.RELEASE-" + Build.VERSION.RELEASE
        var wtflKey = YJFK
        if ("2" == type) {
            wtflKey = YHJY
        }
        api.feedback(Feedback(content, deviceInfo, imgs, wtflKey, wtKey)).subs({ onNext() })
    }

    fun getDictInfo(type: String, onNext: (List<FeedbackQuestion>) -> Unit) {
//        890182678493021247:异常问题  890182678493021260:优化建议
        var qType = YJFK
        if ("2" == type) {
            qType = YHJY
        }
        api.getDictInfo(qType).subs({ onNext(it.body) })
    }

}