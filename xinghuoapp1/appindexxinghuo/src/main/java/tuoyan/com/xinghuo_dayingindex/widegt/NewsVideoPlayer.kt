package tuoyan.com.xinghuo_dayingindex.widegt

import android.content.Context
import android.util.AttributeSet
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer

class NewsVideoPlayer : NormalGSYVideoPlayer {
    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag!!) {}

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun init(context: Context?) {
        super.init(context)

    }

    fun startClick(click: () -> Unit) {
        mStartButton.setOnClickListener {
            if (mHideKey && mIfCurrentIsFullscreen) {
                CommonUtil.hideNavKey(mContext)
            }
            click()
            clickStartIcon()
        }
    }
}
