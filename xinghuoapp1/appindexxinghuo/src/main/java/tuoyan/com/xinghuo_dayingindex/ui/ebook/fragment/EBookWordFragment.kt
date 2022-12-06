package tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment

import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_e_book_word.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.AnswerItem
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfoItem
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookWordsActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.adapter.ChoiceAdapter
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer

private const val QUESTION_INFO = "QuestionInfo"

class EBookWordFragment : LifeV4Fragment<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_e_book_word

    private var mediaPlayer: IjkExo2MediaPlayer? = null
    private var isPrepared = false//音频已准备好
    private val activity by lazy { requireActivity() as EBookWordsActivity }
    private val adapter by lazy {
        ChoiceAdapter("3") {
            updateAdapter(it)
            Handler().postDelayed({
                activity.scrollPos()
            }, 1000)
        }
    }
    private val infoData by lazy { arguments?.getSerializable(QUESTION_INFO) as? QuestionInfo }

    companion object {
        @JvmStatic
        fun newInstance(info: QuestionInfo) =
            EBookWordFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(QUESTION_INFO, info)
                }
            }
    }

    override fun configView(view: View?) {
        super.configView(view)
        rlv_qes.layoutManager = LinearLayoutManager(this.requireContext())
        rlv_qes.adapter = adapter
        adapter.setFooter(true)
    }

    override fun initData() {
        super.initData()
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val infoList = gson.fromJson<ArrayList<QuestionInfoItem>>(gson.toJson(infoData?.item), object : TypeToken<ArrayList<QuestionInfoItem>>() {}.type)//any 转为 questionInfo类型
        val infoItem = QuestionInfoItem()
        infoItem.type = "1"
        infoList.add(infoItem)
        adapter.setData(infoList)
        infoData?.let { info ->
            if (info.useranswer.isNotEmpty()) {
                val qItem = QuestionInfoItem()
                qItem.order = info.useranswer
                updateAdapter(qItem)
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        lav_audio.setOnClickListener {
            startMusic()
        }
    }

    fun initPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = IjkExo2MediaPlayer(this.requireContext())
        } else {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            mediaPlayer = null
            mediaPlayer = IjkExo2MediaPlayer(this.requireContext())
        }
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.isLooping = false
        mediaPlayer!!.setOnCompletionListener { player ->
            isPrepared = false
            lav_audio.cancelAnimation()
            lav_audio.progress=0f
        }
        mediaPlayer!!.setOnErrorListener { player, what, extra ->
            isPrepared = false
            lav_audio.cancelAnimation()
            lav_audio.progress=0f
            true
        }
        mediaPlayer!!.setOnPreparedListener { player ->
            isPrepared = true
            startMusic()
        }
    }

//    fun setPos(pos: Int, total: Int) {
//        tv_num.text = Html.fromHtml("<font color='#008aff'>${pos}</font>/${total}")
//    }

    fun getResourceInfo() {
        if (!isPrepared) {
            initPlayer()
            infoData?.let { info ->
                presenter.getResourceInfo(info.resourceKey, "4") {
                    mediaPlayer!!.dataSource = it.url
                    mediaPlayer!!.prepareAsync()
                }
            }
        }
    }

    fun startMusic() {
        if (isPrepared && mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            lav_audio.playAnimation()
        } else if (!isPrepared) {
            getResourceInfo()
        }
    }

    fun pauseMusic() {
        if (isPrepared && mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            lav_audio.cancelAnimation()
            lav_audio.progress = 0f
        }
    }

    fun setIndex(index: Int, total: Int) {
        tv_num.text = Html.fromHtml("<font color='#008aff'>${index}</font>/${total}")
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

    private fun updateAdapter(item: QuestionInfoItem) {
        val answers = mutableListOf<AnswerItem>()
        val answerItem = AnswerItem("1", item.order, "")
        answers.add(answerItem)
        // 答案单选多选问题answerQ.answers=
        // 图片作答answerQ.imgs
        //  answerQ.answerType
        // answerQ.answerText
        kotlin.run breaking@{
            activity.questionList.forEach { qItem ->
                if (qItem.questionKey == infoData?.questionKey!!) {
                    qItem.useranswer = item.order
                    return@breaking
                }
            }
        }
        adapter.type = "4"
        val datas = adapter.getData()
        kotlin.run breaking@{
            datas.forEach {
                if (item.order == it.order && "0" == it.isAnswer) {
                    it.isAnswer = "2"
                    return@breaking
                }
            }
        }
        adapter.notifyDataSetChanged()
    }
}