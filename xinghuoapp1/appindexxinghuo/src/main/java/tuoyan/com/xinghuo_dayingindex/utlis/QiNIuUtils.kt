//package tuoyan.com.xinghuo_daying.utlis
//
//import android.content.res.Configuration
//import com.qiniu.android.storage.Configuration
//import com.qiniu.android.storage.UploadManager
//
//
///**
// * 创建者：
// * 时间：  2017/7/7.
// */
//
//object QiNIuUtils {
//    private var uploadManager: UploadManager? = null
//
//    fun getUploadManager(): UploadManager? {
//        synchronized(UploadManager::class.java) {
//            if (uploadManager == null) {
//                synchronized(UploadManager::class.java) {
//                    if (uploadManager == null)
//                        uploadManager = UploadManager(Configuration.Builder()
//                                .connectTimeout(10) // 链接超时。默认10秒
//                                .responseTimeout(60) // 服务器响应超时。默认60秒
//                                .build())
//                }
//            }
//        }
//        return uploadManager
//    }
//
//}
