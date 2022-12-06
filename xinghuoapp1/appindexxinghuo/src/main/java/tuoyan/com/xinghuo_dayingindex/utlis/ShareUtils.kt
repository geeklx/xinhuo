package tuoyan.com.xinghuo_dayingindex.utlis

import android.annotation.SuppressLint
import android.app.Activity
import com.geek.libutils.app.BaseApp
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R

/**
 *
 */
@SuppressLint("StaticFieldLeak")
object ShareUtils {
    private var mActivity: Activity? = null
    private val umShareListener = object : UMShareListener {
        override fun onStart(share_media: SHARE_MEDIA) {
//            back?.let {
//                it.onSure()
//            }
        }
        override fun onResult(share_media: SHARE_MEDIA) {
//            MyApp.instance.toast("分享成功")
        }

        override fun onError(share_media: SHARE_MEDIA, throwable: Throwable) {
            val install = UMShareAPI.get(mActivity!!).isInstall(mActivity, share_media)
            if (install) BaseApp.get().toast("分享失败" + throwable.message)
            else BaseApp.get().toast("客户端未安装")
            mActivity = null
        }

        override fun onCancel(share_media: SHARE_MEDIA) {
//            MyApp.instance.toast("取消分享")
        }
    }

    fun share(
        activity: Activity, media: SHARE_MEDIA, title: String, description: String, url: String, iconUrl: String? = null
    ) {
//        PermissionUtlis.checkPermissions(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE) {
        val thumb = if (iconUrl.isNullOrBlank()) UMImage(activity, R.mipmap.logo)
        else UMImage(activity, iconUrl)
        val web = UMWeb(url)
        web.setThumb(thumb)
        web.description = if (description.isNotBlank()) description else "专业.让学习更简单"
        web.title = title
        mActivity = activity
//            ShareAction(activity)
//                    .setDisplayList(SHARE_MEDIA.SINA,
//                            SHARE_MEDIA.QQ,
//                            SHARE_MEDIA.WEIXIN,
//                            SHARE_MEDIA.WEIXIN_CIRCLE,
//                            SHARE_MEDIA.QZONE)
//                    .setShareboardclickCallback { snsPlatform, share_media ->
//                        //                        if (share_media == SHARE_MEDIA.SINA)
//                        web.description = "我在用星火考研词汇学习单词，快来围观我的战绩和进步吧！小火苗燃起ing！ " + description
//                        ShareAction(activity)
//                                .withMedia(web)
//                                .setPlatform(share_media)
//                                .setCallback(umShareListener)
//                                .share()
//                    }
//                    .open()
        ShareAction(activity).withMedia(web).setPlatform(media).setCallback(umShareListener).share()
//        }
    }

    private var back: Callback? = null

    public fun setCallback(callback: Callback) {
        this.back = callback
    }

    interface Callback {
        fun onSure()
    }
}


