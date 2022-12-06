package tuoyan.com.xinghuo_dayingindex.ui.ebook
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.media.AudioManager
import android.os.Handler
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_ebook_exercise.*
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.AnswerQuestion
import tuoyan.com.xinghuo_dayingindex.bean.AnswerSubmit
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem
import tuoyan.com.xinghuo_dayingindex.bean.LrcRow
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.EBookAnswerCardFragment
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.MaterialQesFragment
import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.ExercisePagerAdapter
import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailPresenter
import tuoyan.com.xinghuo_dayingindex.utlis.IRowsParser
import tuoyan.com.xinghuo_dayingindex.utlis.MyLrcDataBuilder
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer
import java.util.*
import kotlin.math.ceil

/**
 * 点读书模考   音频：一整个音频，音频不可拖动
 */
class EBookExerciseActivity : EBookLifeActivity<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_ebook_exercise

    private val answerCardFragment by lazy { EBookAnswerCardFragment.newInstance() }
    private val fragmentList by lazy { mutableListOf<Fragment>() }
    private var currentIndex = 0//当前vp 的位置

    /**
     * 当前小题的位置 循环+1 赋值 最后为所有小题的数量
     */
    private var current = 0
    var isDown = false

    /**
     * 构造显示题目及答题卡的list
     * 普通做题模块下调用
     * 答题卡页面调用
     */
    var answerList = mutableListOf<AnswerQuestion>()

    private var mediaPlayer: IjkExo2MediaPlayer? = null
    private var timer: Timer? = null
    private var isPrepared = false//音频已准备好
    private var enterTime = 0L
    var lrcData: List<LrcRow>? = null//提问句，当前小题答题时长
    private val parser by lazy {
        object : IRowsParser {
            override fun parse(lrcRowDada: String): MutableList<LrcRow> {
                try {
                    val lastIndexOfRightBracket = lrcRowDada.indexOf("]")
                    val content = lrcRowDada.substring(lastIndexOfRightBracket + 1)
                    val times = lrcRowDada.substring(0, lastIndexOfRightBracket + 1).replace("[", "-").replace("]", "-")
                    val arrTimes = times.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val listTimes = mutableListOf<LrcRow>()
                    val var10 = arrTimes.size
                    for (var11 in 0 until var10) {
                        val temp = arrTimes[var11]
                        if (temp.trim { it <= ' ' }.isNotEmpty()) {
                            val pre = temp.split(",")
                            if (pre.size == 2 && "#" == pre[1]) {//提问句 主要拿到时间 计算答题时长
                                val lrcRow = LrcRow(content, pre[0], this.timeConvert(pre[0]))
                                listTimes.add(lrcRow)
                            }
                        }
                    }
                    return listTimes
                } catch (e: Exception) {
                    return mutableListOf()
                }
            }

            private fun timeConvert(timeString: String): Long {
                var timeStr = timeString
                timeStr = timeStr.replace('.', ':')
                val times = timeStr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                return when (times.size) {
                    4 -> (Integer.valueOf(times[0]) * 60 * 60 * 1000 + Integer.valueOf(times[1]) * 60 * 1000 + Integer.valueOf(times[2]) * 1000 + Integer.valueOf(times[3])).toLong()
                    3 -> (Integer.valueOf(times[0]) * 60 * 1000 + Integer.valueOf(times[1]) * 1000 + Integer.valueOf(times[2])).toLong()
                    2 -> (Integer.valueOf(times[0]) * 60 * 1000 + Integer.valueOf(times[1]) * 1000).toLong()
                    1 -> (Integer.valueOf(times[0]) * 1000).toLong()
                    else -> 0
                }
            }
        }
    }
    private var dDialog: DDialog? = null
//    private var seekToTime = 0L

    override fun configView() {
        super.configView()
        tv_time.text = Html.fromHtml("<font color='#008AFF'>--:--</font> / --:--")
        layout_guide.visibility = if (SpUtil.eBookExerciseShow) View.GONE else View.VISIBLE
        SpUtil.eBookExerciseShow = true
        initAnswerCard()
    }

    override fun initData() {
        super.initData()
        fragmentList.clear()
        presenter.getExerciseParsingFrame(bookParam?.resourceKey ?: "", bookParam?.userpractisekey ?: "") { paper ->
            enterTime = paper.lastAudioTime ?: 0L
            getResourceInfo(paper.paperResourceKey ?: "")
            getLrcData(paper.paperLrcUrl ?: "")
            try {
                paper.questionlist?.let { list -> if (list.isNotEmpty()) initExerciseData(paper.lastQuestionKey ?: "", list) else toast("数据异常~请您稍候再试~") }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener { onBackPressed() }
        vp_exercise.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var flag = false
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(pos: Int) {
                currentIndex = pos
                initTitle()
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager.SCROLL_STATE_DRAGGING -> flag = true
                    ViewPager.SCROLL_STATE_SETTLING -> flag = false
                    ViewPager.SCROLL_STATE_IDLE -> {
                        if (flag && vp_exercise.currentItem == fragmentList.size - 1) {//最后一页继续滑动，弹出答题卡
                            img_card.performClick()
                        }
                    }
                }
            }
        })
        img_card.setOnClickListener {
            if (View.VISIBLE == fl_answer_card.visibility) {
                initTitle()
                fl_answer_card.visibility = View.GONE
                img_card.visibility = View.VISIBLE
            } else {
                tv_title.text = "答题卡"
                img_card.visibility = View.GONE
                fl_answer_card.visibility = View.VISIBLE
                answerCardFragment.showAnswerCard()
            }
        }
        layout_guide.setOnClickListener {
            layout_guide.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPrepared && mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            lav_play.playAnimation()
            initTask()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseMusic()
    }


    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
    }

    private fun initExerciseData(lastQuestionKey: String, dataList: List<ExerciseFrameItem>) {
        var pIndex = 0
        for (index in dataList.indices) { // 大题分类的结构 body->questionlist
            val item: ExerciseFrameItem = dataList[index]
            item.questionlist?.forEach { pInfo ->//body->questionlist->questionlist
                if ("11" == pInfo.questionType) {//听力材料题
                    val instance = MaterialQesFragment.newInstance(pInfo)
                    instance.qIndex = current
                    fragmentList.add(instance)
                    if (pInfo.questionlist != null && pInfo.questionlist!!.isNotEmpty()) {
                        //如果当前题目中存在小题（材料题、多选题，填空题都算做一个题），则解析该题结构
                        pInfo.questionlist!!.forEachIndexed { index, info ->//body->questionlist->questionlist->questionlist
                            ++current
                            val answerQuestionItem = AnswerQuestion()
                            answerQuestionItem.sort = info.questionSort ?: ""
                            answerQuestionItem.type = info.questionType ?: ""
                            answerQuestionItem.questionKey = info.questionKey
                            answerQuestionItem.haveDone = false
                            answerQuestionItem.qPosition = fragmentList.size - 1
                            answerQuestionItem.mPosition = index
                            answerQuestionItem.parentType = pInfo.questionType ?: ""
                            answerList.add(answerQuestionItem)
                            if (info.questionKey == lastQuestionKey) {
                                val scrollPos = pIndex
                                currentIndex = scrollPos
                                Handler().postDelayed({
                                    vp_exercise.currentItem = scrollPos
                                    instance.scrollPos(index)
                                }, 1000)
                            }
                        }
                    }
                    pIndex++
                }
            }
        }
        vp_exercise.offscreenPageLimit = 100
        vp_exercise.adapter = ExercisePagerAdapter(supportFragmentManager, fragmentList)
        initTitle()
    }

    private fun initAnswerCard() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fl_answer_card, answerCardFragment)
        transaction.show(answerCardFragment).commit()
    }

    override fun onBackPressed() {
        when (View.VISIBLE) {
            layout_guide.visibility -> {
                layout_guide.visibility = View.GONE
            }
            fl_answer_card.visibility -> {
                fl_answer_card.visibility = View.GONE
                img_card.visibility = View.VISIBLE
                initTitle()
            }
            else -> {
                dDialog = DDialog(this).setMessage("你正在进行模考，是否确定退出？")
                    .setNegativeButton("确定") {
                        dDialog?.dismiss()
                        save()
                    }.setPositiveButton("取消") {
                        dDialog?.dismiss()
                    }
                dDialog?.show()
            }
        }
    }

    fun initTitle() {
        when (val fragment = fragmentList[currentIndex]) {
            is MaterialQesFragment -> {
                tv_title.text = Html.fromHtml("<font color='#008AFF'>${fragment.qIndex + fragment.answerIndex + 1}</font>/${current}")
            }
        }
    }

    fun scrollPos(fPos: Int, sPos: Int) {
        img_card.performClick()
        vp_exercise.currentItem = fPos
        when (val fragment = fragmentList[fPos]) {
            is MaterialQesFragment -> {
                fragment.scrollPos(sPos)
            }
        }
    }

    /**
     * 非最后一题，作答后 跳转下一题
     */
    fun goNext() {
        if (vp_exercise.currentItem == fragmentList.size - 1) {
            //是最后一题，则显示答题卡，非最后一题，则跳转到下一题
            img_card.performClick()
        } else {
            currentIndex += 1
            vp_exercise.currentItem = currentIndex
        }
    }

    /**
     * 提交试卷startActivity<EBookReportActivity>()
     */
    fun submit() {
        if (!isDown) {
            Toast.makeText(this, "不能交白卷喔~", Toast.LENGTH_LONG).show()
            return
        }
        checkAnswers(answerList) {
            val submit = AnswerSubmit()
            submit.answerlist = answerList as ArrayList<AnswerQuestion>
            submit.time = "0"
            submit.catalogkey = bookParam?.catalogKey ?: ""
            submit.source = "9"
            submit.practicekey = bookParam?.bookKey ?: ""
            submit.paperkey = bookParam?.resourceKey ?: ""
            submit.userPractiseKey = bookParam?.userpractisekey ?: ""
            submit.submitType = "1"
            presenter.submitType(submit) {
                startActivity<EBookReportActivity>(EBookReportActivity.DATA to it, EBOOK_PARAM to bookParam)
                finish()
            }
        }
    }

    /**
     * 退出保存试卷
     */
    fun save() {
        if (!isDown) {
            super.onBackPressed()
        } else {
            when (val fragment = fragmentList[currentIndex]) {
                is MaterialQesFragment -> {
                    val submit = AnswerSubmit()
                    submit.answerlist = answerList as ArrayList<AnswerQuestion>
                    submit.time = "$enterTime"
                    submit.catalogkey = bookParam?.catalogKey ?: ""
                    submit.source = "9"
                    submit.practicekey = bookParam?.bookKey ?: ""
                    submit.paperkey = bookParam?.resourceKey ?: ""
                    submit.lastQuestionKey = answerList[fragment.qIndex + fragment.answerIndex].questionKey
                    submit.userPractiseKey = bookParam?.userpractisekey ?: ""
                    submit.submitType = "0"
                    presenter.submitType(submit) {
                        super.onBackPressed()
                    }
                }
            }
        }
    }

    private fun checkAnswers(answerList: List<AnswerQuestion>, action: () -> Unit) {
        var noDownNum = 0
        answerList.forEach {
            if (!it.haveDone) {
                noDownNum++
            }
        }
        when (noDownNum) {
            0 -> {
                action()
            }
            else -> {
                dDialog = DDialog(this).setMessage("你还有 <font color='#FF7800'><b>${noDownNum}</b></font> 道题没有答，是否提交？")
                    .setNegativeButton("提交答案") {
                        dDialog?.dismiss()
                        action()
                    }.setPositiveButton("继续作答") {
                        dDialog?.dismiss()
                    }
                dDialog?.show()
            }
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
            stopMusic()
        }
        mediaPlayer!!.setOnErrorListener { player, what, extra ->
            stopMusic()
            true
        }
        mediaPlayer!!.setOnPreparedListener { player ->
            tv_time.text = Html.fromHtml("<font color='#008AFF'>00:00</font> / ${time(player.duration)}")
            pb_class.progress = 0
            pb_class.secondaryProgress = 0
            pb_class.max = player.duration.toInt()
            startMusic()
        }
    }

    /**
     * 刷新进度定时器
     */
    private fun initTask() {
        timer = null
        timer = Timer(true)
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    try {
                        if (mediaPlayer!!.isPlaying) {
                            val index = mediaPlayer!!.currentPosition ?: 0
                            pb_class.progress = index.toInt()
                            tv_time.text = Html.fromHtml("<font color='#008AFF'>${time(index)}</font> / ${time(mediaPlayer!!.duration)}")
                            if (enterTime < mediaPlayer!!.duration) {
                                enterTime = index
                            } else {
                                enterTime += 500L
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }, 1000, 500)
    }

    /**
     * 1.更新音频播放进度
     * 2.答题计时，当页面结束的时候停止计时，播放完成不停止
     */
    fun cancelTask() {
        if (timer != null) {
            timer!!.cancel()
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

    fun getResourceInfo(key: String) {
        if (!isPrepared && key.isNotEmpty()) {
            initPlayer()
            presenter.getResourceInfo(key, "3") {
                mediaPlayer!!.dataSource = it.url
                mediaPlayer!!.prepareAsync()
            }
        }
    }

    fun startMusic() {
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            if (enterTime >= 0 && enterTime < mediaPlayer!!.duration) {
                mediaPlayer!!.seekTo(enterTime)
                mediaPlayer!!.start()
                initTask()
                isPrepared = true
                lav_play.playAnimation()
            }
        }
    }

    fun pauseMusic() {
        cancelTask()
        if (mediaPlayer != null) {
            mediaPlayer!!.pause()
        }
        lav_play.cancelAnimation()
        lav_play.progress = 0f
    }

    fun stopMusic() {
        isPrepared = false
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
        }
        lav_play.cancelAnimation()
        lav_play.progress = 0f
    }

    private fun getLrcData(url: String) {
        if (url.isNotEmpty()) {
            presenter.lrc(url) { data ->
                lrcData = MyLrcDataBuilder().Build(data, parser)
            }
        }
    }

    /**
     * 答题用时：答题用时从开始读问题计算，到选出选项结束计时。
     * 全站平均用时：统计全站所有模考本道题的用户的平均用时用时≤1S 时，均显示为 1S
     * 用时>1S 时，不足整秒的四舍五入显示整秒。
     * 本题未答/返回再答/没念问题之前就答题的题目答题用时显示为0,
     * 直接向上取整
     */

    fun getAnswerTime(): Int {
        return try {
            when (val fragment = fragmentList[currentIndex]) {
                is MaterialQesFragment -> {
                    if (answerList.size != lrcData?.size) {
                        toast("答题用时无法计算")
                    }
                    val temp = enterTime - (lrcData?.get(fragment.qIndex + fragment.answerIndex)?.currentRowTime ?: 0L)
                    if (temp > 0) ceil(temp / 1000f).toInt() else 0
                }
                else -> {
                    0
                }
            }

        } catch (e: Exception) {
            0
        }
    }
}