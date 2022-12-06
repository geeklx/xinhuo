package tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.media.AudioManager
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_ebook_word_list.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.EBookPracticeJumpDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookListenActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.EBookWordAdapter
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer

class EBookWordListActivity : EBookLifeActivity<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_ebook_word_list

    private val adapter by lazy {
        EBookWordAdapter() {
            isAllPlay = false
            setClickAudioState()
            getResourceInfo(it.resourceKey)
        }
    }

    private var mediaPlayer: IjkExo2MediaPlayer? = null
    private var isPrepared = false//音频已准备好
    private var isAllPlay = false//是否播放全部
    private var currentPos = 0//当前播放位置
    private val questionList by lazy { intent.getSerializableExtra(ANSWER_LIST) as ArrayList<QuestionInfo> }
    private var dDialog: DDialog? = null

    override fun configView() {
        super.configView()
        EBookPracticeJumpDialog(this).setFTitle(bookParam?.catalogName ?: "").setSTitle(bookParam?.name ?: "").setType(2).show()
        rlv_words.isNestedScrollingEnabled = false
        rlv_words.layoutManager = LinearLayoutManager(this)
        rlv_words.adapter = adapter
    }

    override fun initData() {
        super.initData()
        var rightNum = 0
        var errorNum = 0
        questionList.forEach {
            if (it.useranswer == it.rightanswer) {
                rightNum++
            } else {
                errorNum++
            }
        }
        tv_right_rate.text = if (questionList.isEmpty()) "0%" else "${rightNum * 100 / questionList.size}%"
        tv_right_num.text = "${rightNum}"
        tv_err_num.text = "${errorNum}"
        adapter.setData(questionList)
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener { onBackPressed() }
        ctl_play.setOnClickListener {
            isAllPlay = !isAllPlay
            setCtlAudioState()
            continueStartMusic()
        }
        tv_practice.setOnClickListener {
            bookParam?.userpractisekey = ""
            startActivity<EBookListenActivity>(EBOOK_PARAM to bookParam)
            super.onBackPressed()
        }
    }

    fun initPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = IjkExo2MediaPlayer(this)
        } else {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            mediaPlayer = null
            mediaPlayer = IjkExo2MediaPlayer(this)
        }
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.isLooping = false
        mediaPlayer!!.setOnCompletionListener { player ->
            isPrepared = false
            continueStartMusic()
        }
        mediaPlayer!!.setOnErrorListener { player, what, extra ->
            isPrepared = false
            continueStartMusic()
            true
        }
        mediaPlayer!!.setOnPreparedListener { player ->
            isPrepared = true
            startMusic()
        }
    }

    fun getResourceInfo(key: String) {
        initPlayer()
        presenter.getResourceInfo(key, "4") {
            mediaPlayer!!.dataSource = it.url
            mediaPlayer!!.prepareAsync()
        }
    }

    fun startMusic() {
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
        }
    }

    fun continueStartMusic() {
        if (isAllPlay) {
            if (currentPos < questionList.size) {
                adapter.currentPos = currentPos
                getResourceInfo(questionList[currentPos].resourceKey)
                currentPos++
            } else {
                //更新adapter，更新全部播放按钮状态
                isAllPlay = false
                currentPos = 0
                setCtlAudioState()
            }
        }
    }

    fun updateAdapter() {
        if (!isAllPlay) {
            adapter.currentPos = -1
            adapter.notifyDataSetChanged()
        }
    }

    fun setClickAudioState() {
        img_play.isSelected = isAllPlay
        lav_audio.visibility = if (isAllPlay) View.VISIBLE else View.GONE
        tv_play_text.visibility = if (isAllPlay) View.GONE else View.VISIBLE
    }

    fun setCtlAudioState() {
        setClickAudioState()
        updateAdapter()
    }

    fun pauseMusic() {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
        }
    }

    fun stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
        }
    }

    override fun onStop() {
        super.onStop()
        stopMusic()
    }

    companion object {
        val ANSWER_LIST = "answerList"
    }

    override fun onBackPressed() {
        dDialog = DDialog(this).setMessage("你正在进行精练，是否确定退出？")
            .setNegativeButton("确定") {
                dDialog?.dismiss()
                super.onBackPressed()
            }.setPositiveButton("取消") {
                dDialog?.dismiss()
            }
        dDialog?.show()
    }
}