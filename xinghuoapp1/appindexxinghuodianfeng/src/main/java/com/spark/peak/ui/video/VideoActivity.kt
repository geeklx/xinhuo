//package com.spark.peak.ui.video
//
//import android.os.Bundle
//import android.view.WindowManager
//import com.shuyu.gsyvideoplayer.GSYVideoManager
//import com.shuyu.gsyvideoplayer.utils.OrientationUtils
//import com.spark.peak.R
//import com.spark.peak.base.BasePresenter
//import com.spark.peak.base.LifeActivity
//import com.spark.peak.utlis.NetWorkUtils
//import kotlinx.android.synthetic.main.activity_videodf.*
//import org.jetbrains.anko.toast
//
//class VideoActivity : LifeActivity<BasePresenter>() {
//    override val presenter: BasePresenter
//        get() = BasePresenter(this)
//    override val layoutResId: Int
//        get() = R.layout.activity_video
//
//    private val url by lazy { intent.getStringExtra(URL)?:"" }
//    private val title by lazy { intent.getStringExtra(TITLE)?:"" }
//
//    companion object {
//        val URL = "url"
//        val TITLE = "title"
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        fullScreen = true
//        landScreen = true
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        super.onCreate(savedInstanceState)
//    }
//
//
//    override fun initData() {
//        if (url.isNullOrEmpty()) {
//            toast("数据出错啦，请重试~~")
//            return
//        }
//
////        url?.let {
////            var videoFile = File(DownloadManager.getFilePath(url))
////            if (videoFile.exists()){
////                playAction(DownloadManager.getFilePath(url))
////            }else if (NetWorkUtils.isNetWorkReachable()) {
////                if (NetWorkUtils.isWifiConnected()) {
////                    playAction(url)
////                } else {
////                    AlertDialog.Builder(ctx).setMessage("您正在使用非wifi网络，播放将产生流量费用").setPositiveButton("继续使用") { _, _ -> playAction(url) }.setNegativeButton("取消") { _, _ -> finish() }.show()
////                }
////            } else {
////                toast("请检查网络")
////            }
//            when {
//                !url.startsWith("http") -> playAction(url)
//                NetWorkUtils.isNetWorkReachable() -> playAction(url)
//                else -> toast("请检查网络")
////            }
//        }
//
//    }
//
//    private fun playAction(path: String) {
//        video_player.setUp(path, true, title ?: "")
//        video_player.setIsTouchWiget(true)
//        val ou = OrientationUtils(this, video_player)
//        video_player.startWithLand(ou) {
//            onBackPressed()
//        }
//        video_player.backButton.setOnClickListener {
//            onBackPressed()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        video_player.onVideoResume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        video_player.onVideoPause()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        GSYVideoManager.releaseAllVideos()
//        video_player.releaseOU()
//        video_player.setVideoAllCallBack(null)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//    }
//}
