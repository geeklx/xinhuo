package tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video

import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import kotlinx.android.synthetic.main.activity_video.*


class VideoPortraitActivity : VideoActivity() {
    override fun playVideo() {
        val ou = OrientationUtils(this, video_player)
        video_player.startWithChange(ou) {
            onBackPressed()
        }
    }
}
