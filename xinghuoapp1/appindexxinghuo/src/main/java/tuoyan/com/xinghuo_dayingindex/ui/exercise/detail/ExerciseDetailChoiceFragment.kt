package tuoyan.com.xinghuo_dayingindex.ui.exercise.detail

import android.media.AudioManager
import android.os.Bundle
import android.text.Html
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_exercise_choice.*
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.support.v4.runOnUiThread
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseV4Fragment
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo
import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.ExercisePagerV4Adapter
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer
import java.util.*
import kotlin.collections.ArrayList


/**
 * 做题模块转原生
 *对应ExerciseDetailFragment
 */
class ExerciseDetailChoiceFragment : LifeV4Fragment<ExerciseDetailPresenter>() {
    private var startY = 0f//用于触摸选项滑动的y坐标
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_exercise_choice
    private val parentActivity by lazy { activity as ExerciseDetailKActivity }
    private var fragmentList = ArrayList<BaseV4Fragment>()
    var qIndex = 0//当前小节第一个题位置-1
    var answerIndex = 0 //当前小题位于当前小节的位置
    var qSort = ""
    var questionKey = ""
    private var isCollected = "" //是否收藏当前小节
    val itemData by lazy { arguments?.getSerializable(ITEM) as? ExerciseFrameItem }

    //-------------音频播放有关----------------
    private var mediaPlayer: IjkExo2MediaPlayer? = null
    private var isPerpared = false//音频是否已经准备就绪
    private var timer: Timer? = null
    private var isPlaying = false//是否正在播放

    //----------------------------------------------
    override fun configView(view: View?) {
        super.configView(view)
        img_play_pause.isSelected = true
    }

    override fun initData() {
        val gson = GsonBuilder().disableHtmlEscaping().create()
        itemData!!.let {
            isCollected = it.isCollected!!
            if (it.questionInfo?.resourceKey.isNullOrEmpty()) {
                ctl_audio.visibility = View.GONE
            } else {
                initPlayer()
                getResourceInfo(it.questionInfo?.resourceKey!!)
                ctl_audio.visibility = View.VISIBLE
            }
            tv_content.text = Html.fromHtml(it.questionInfo?.stem)
            val list = it.questionInfo?.item as ArrayList<*>
            list.forEachIndexed { index, infoItem ->
                val info = gson.fromJson(gson.toJson(infoItem), QuestionInfo::class.java)//any 转为 questionInfo类型
                if (index == 0) {
                    qSort = info.sort
                    questionKey = info.questionKey
                }
                fragmentList.add(ExerciseChoiceItemFragment.newInstance(info, (index + 1).toString(), list.size.toString()))
            }
            // 子Fragment中有viewpager需要用getChildFragmentManager()
            val oAdapter = ExercisePagerV4Adapter(childFragmentManager, fragmentList)
            vp_choice.offscreenPageLimit = 10
            vp_choice.adapter = oAdapter
        }
    }

    override fun handleEvent() {
        img_touch.setOnTouchListener { view, motionEvent ->
            when (motionEvent.getAction()) {
                MotionEvent.ACTION_DOWN -> {
                    startY = motionEvent.rawY
                }
                MotionEvent.ACTION_UP -> {
                }
                MotionEvent.ACTION_MOVE -> {
                    var moveY = motionEvent.rawY
                    var offsetY = moveY - startY
                    val params = img_touch.layoutParams as ConstraintLayout.LayoutParams
                    var top = params.topMargin
                    params.topMargin = (top + offsetY).toInt()
                    img_touch.layoutParams = params
                    startY = moveY
                }
                else -> {
                }
            }
            true
        }
        vp_choice.onPageChangeListener {
            onPageSelected {
                answerIndex = it
                when (val fragment = fragmentList[it]) {
                    is ExerciseChoiceItemFragment -> {
                        qSort = fragment.qSort
                        questionKey = fragment.questionKey
                        parentActivity.setIndex(fragment.qSort)
                    }
                }
            }
        }
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_current_time.text = time(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isPlaying = mediaPlayer!!.isPlaying
                mediaPlayer?.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mediaPlayer?.seekTo(it.progress.toLong())
                }
                if (isPlaying) {
                    mediaPlayer?.start()
                } else {
                    mediaPlayer?.pause()
                }
            }
        })
        img_play_pause.setOnClickListener {
            playAndPause()
        }
    }

    private fun playAndPause() {
        if (isPerpared) {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                cancelTask()
                img_play_pause.isSelected = true
            } else {
                mediaPlayer?.start()
                initTask()
                img_play_pause.isSelected = false
            }
        } else {
            Toast.makeText(context, "正在加载，请稍等", Toast.LENGTH_SHORT).show()
        }
    }

    //activity 调用关闭当前音乐播放
    fun musicStop() {
        if (isPerpared) {
            if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                cancelTask()
                img_play_pause.isSelected = true
            }
        }
    }

    fun completeMusic() {
        cancelTask()
        if (mediaPlayer != null) {
            mediaPlayer!!.seekTo(0)
            mediaPlayer!!.pause()
            img_play_pause.isSelected = true
        }
    }

    private fun getResourceInfo(resourceKey: String) {
        presenter.getResourceInfo(resourceKey, "3") {
            mediaPlayer?.dataSource = it.url
            mediaPlayer?.prepareAsync()
        }
    }

    /**
     * 初始化音乐播放器
     */
    private fun initPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = IjkExo2MediaPlayer(context)
        } else {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
            mediaPlayer = IjkExo2MediaPlayer(context)
        }
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.isLooping = false
        mediaPlayer!!.setOnCompletionListener { mp ->
            completeMusic()
        }

        mediaPlayer!!.setOnErrorListener { player, what, extra ->
            player.stop()
            player.reset()
            isPerpared = false
            cancelTask()
            true
        }

        mediaPlayer!!.setOnPreparedListener {
            isPerpared = true
            tv_current_time?.text = "00:00"
            tv_total_time?.text = time(it.duration)
            seek_bar?.progress = 0
            seek_bar?.secondaryProgress = 0
            seek_bar?.max = it.duration.toInt()
        }
    }

    /**
     * 格式化音频 格式00：00
     */
    private fun time(time: Long): String {
        val i = time / 1000
        val s = i % 60
        val m = i / 60
        return "${if (m < 10) "0$m" else "$m"}:${if (s < 10) "0$s" else "$s"}"
    }

    //是否收藏当前小节
    fun getCollection(): String {
        return isCollected
    }

    fun setCollection(isCollected: String) {
        this.isCollected = isCollected
    }

    /**
     * 刷新进度定时器
     */
    private fun initTask() {
        timer = Timer(true)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    try {
                        if (mediaPlayer?.isPlaying == true) {
                            val index = mediaPlayer?.currentPosition ?: 0
                            seek_bar.progress = index.toInt()
                            tv_current_time.text = time(index)
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }, 1000, 500)
    }

    companion object {
        private const val ITEM = "item"

        @JvmStatic
        fun newInstance(item: ExerciseFrameItem) =
            ExerciseDetailChoiceFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ITEM, item)
                }
            }
    }

    override fun onPause() {
        super.onPause()
        musicStop()
    }

    private fun cancelTask() {
        if (timer != null) {
            timer?.cancel()
        }
    }

    //答题卡定位到当前小题
    fun jumpIndex(pos: Int) {
        vp_choice.currentItem = pos
    }

    /**
     * 非最后一题，作答后 跳转下一题
     */
    fun goNext() {
        if (vp_choice.currentItem == fragmentList.size - 1) {
            parentActivity.goNext()
        } else {
            answerIndex += 1
            vp_choice.currentItem = answerIndex
        }
    }
}
