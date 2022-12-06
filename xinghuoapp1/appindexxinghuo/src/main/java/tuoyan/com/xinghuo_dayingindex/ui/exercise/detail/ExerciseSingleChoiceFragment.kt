package tuoyan.com.xinghuo_dayingindex.ui.exercise.detail

import android.media.AudioManager
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_exercise_single_choice.*
import org.jetbrains.anko.support.v4.runOnUiThread
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.AnswerItem
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfoItem
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer
import java.util.*

private const val ITEM = "ExerciseFrameItem"

class ExerciseSingleChoiceFragment : LifeV4Fragment<ExerciseDetailPresenter>() {

    override val layoutResId: Int
        get() = R.layout.fragment_exercise_single_choice
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    private val parentActivity by lazy { activity as ExerciseDetailKActivity }
    private val adapter by lazy {
        ChoiceAdapter { answer ->
            initAnswer(answer)
            parentActivity.goNext()
        }
    }

    var itemData: ExerciseFrameItem? = null
    var qIndex = 0//当前小节第一个题位置-1
    var answerIndex = 0 //当前小题在当前小节的位置
    var qSort = ""//
    var questionKey = ""//
    private var isCollected = "" //是否收藏当前小节

    //-------------音频播放有关----------------
    private var mediaPlayer: IjkExo2MediaPlayer? = null
    private var isPerpared = false//音频是否已经准备就绪
    private var timer: Timer? = null
    private var isPlaying = false//是否正在播放

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemData = it.getSerializable(ITEM) as? ExerciseFrameItem
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(item: ExerciseFrameItem) =
            ExerciseSingleChoiceFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ITEM, item)
                }
            }
    }

    override fun initData() {
        super.initData()
        val gson = GsonBuilder().disableHtmlEscaping().create()
        itemData?.let { item ->
            if (item.questionInfo?.resourceKey.isNullOrEmpty()) {
                ctl_audio.visibility = View.GONE
            } else {
                initPlayer()
                getResourceInfo(item.questionInfo?.resourceKey!!)
                ctl_audio.visibility = View.VISIBLE
            }
            qSort = item.questionSort ?: ""
            questionKey = item.questionKey ?: ""
            isCollected = item.isCollected!!
            tv_content.text = Html.fromHtml(item.questionInfo?.stem ?: "")
            val infoList = gson.fromJson<List<QuestionInfoItem>>(gson.toJson(item.questionInfo?.item), object : TypeToken<List<QuestionInfoItem>>() {}.type)//any 转为 questionInfo类型
            if (true == item.questionInfo?.useranswer?.isNotEmpty()) {
                initAnswer(item.questionInfo?.useranswer ?: "")
                adapter.order = item.questionInfo?.useranswer ?: ""
            }
            adapter.setData(infoList)
        }
    }

    override fun configView(view: View?) {
        super.configView(view)
        img_play_pause.isSelected = true
        rlv_answers.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_answers.adapter = adapter
    }

    override fun handleEvent() {
        super.handleEvent()
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

    override fun onPause() {
        super.onPause()
        musicStop()
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
            tv_current_time.text = "00:00"
            tv_total_time.text = time(it.duration)
            seek_bar.progress = 0
            seek_bar.secondaryProgress = 0
            seek_bar.max = it.duration.toInt()
        }
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

    private fun cancelTask() {
        if (timer != null) {
            timer?.cancel()
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

    private fun initAnswer(answer: String) {
        parentActivity.isDone = true
        val answers = mutableListOf<AnswerItem>()
        val answerItem = AnswerItem("1", answer, "")
        answers.add(answerItem)
        // 答案单选多选问题answerQ.answers=
        // 图片作答answerQ.imgs
        //  answerQ.answerType
        // answerQ.answerText
        parentActivity.answerList.forEach {
            it.qList.forEach { qItem ->
                if (qItem.questionKey == itemData?.questionKey!!) {
                    qItem.haveDone = true
                    qItem.answers.clear()
                    qItem.answers.addAll(answers)
                    parentActivity.saPaperAnswer(qItem)
                    return
                }
            }
        }
    }

    //是否收藏当前小节
    fun getCollection(): String {
        return isCollected
    }

    fun setCollection(isCollected: String) {
        this.isCollected = isCollected
    }
}