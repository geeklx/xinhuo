package tuoyan.com.xinghuo_dayingindex.ui.mine.user

import com.geek.libutils.app.BaseApp
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
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
//        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
//        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
//        val uuid = UUID.randomUUID().toString().replace("-".toRegex(), "") + ".png"
//        api.uploadTopImage(part).sub({ _ ->
//            userInfo {
//                SpUtil.userInfo = it
//                EventBus.getDefault().post(EventMsg("home", -1))
//                onNext()
//            }
//        })
        Luban.with(BaseApp.get()).load(file).ignoreBy(300)
            .setTargetDir(BaseApp.get().externalCacheDir?.path)
            .setCompressListener(object : OnCompressListener {
                override fun onError(index: Int, e: Throwable?) {
                }

                override fun onStart() {
                }

//                    override fun onSuccess(index: Int, compressFile: File?) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onError(index: Int, e: Throwable?) {
//                        TODO("Not yet implemented")
//                    }

                override fun onSuccess(index: Int, file: File?) {
                    file?.let {
//                        val requestBody =
//                            it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), it)
                        val part = MultipartBody.Part.createFormData("file", it.name, requestBody)
                        api.uploadTopImage(part).sub({
                            userInfo {
                                SpUtil.userInfo = it
                                onNext()
                            }
                        })
                    }
                }
            }).launch()

    }

    /**
     * 获取考研档案信息
     */
    fun getPostgraduateRecord(key: String, onNext: (YanMessage) -> Unit) {
        api.getPostgraduateRecord(key).sub(onNext = { onNext(it.body) })
    }

    /**
     *查询考研学校和专业type=（学校= SCHOOL、 专业=MAJOR）
     */
    fun getDictInfo(type: String, name: String, onNext: (List<FeedbackQuestion>) -> Unit) {
        api.getDictInfoSchool(type, name).sub(onNext = { onNext(it.body) })
    }

    /**
     * 跳到考研档案信息
     */
    fun ignorePostgraduateRecord(onNext: () -> Unit) {
        api.ignorePostgraduateRecord().sub(onNext = { onNext() })
    }

    /**
     * 研档案新增更新
     */
    fun postgraduateRecord(data: YanMessage, onNext: () -> Unit) {
        api.postgraduateRecord(data).sub(onNext = {
            userInfo {
                SpUtil.userInfo = it
                onNext()
            }
        })
    }

    fun getExamList(onNext: (List<examList>) -> Unit) {
        api.getExamList().sub(onNext = { onNext(it.body) })
    }

    fun mySpokenExamine(state: String, onNext: (SpokenExamin) -> Unit) {
        api.mySpokenExamine(state).sub(onNext = { onNext(it.body) })
    }
}