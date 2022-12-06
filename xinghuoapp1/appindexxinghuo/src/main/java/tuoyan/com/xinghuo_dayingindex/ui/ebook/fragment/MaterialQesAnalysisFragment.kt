package tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_material_qes_analysis.*
import org.jetbrains.anko.support.v4.runOnUiThread
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.ExerciseFrameItem
import tuoyan.com.xinghuo_dayingindex.bean.MatcherBean
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookListenExerciseActivity
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.exercise.adapter.ExercisePagerAdapter
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer
import java.io.BufferedReader
import java.io.IOException
import java.io.StringReader
import java.util.*
import java.util.regex.Pattern

private const val TYPE = "TYPE"
private const val ITEM = "ExerciseFrameItem"

/**
 * 材料题
 */
class MaterialQesAnalysisFragment : LifeV4Fragment<EBookPresenter>() {
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_material_qes_analysis

    val itemData by lazy { arguments?.getSerializable(ITEM) as? ExerciseFrameItem }
    private val type by lazy { arguments?.getString(TYPE) ?: "1" }
    private var mediaPlayer: IjkExo2MediaPlayer? = null
    private var timer: Timer? = null
    private var isPrepared = false//音频已准备好
    private var isPlaying = false
    var lrcStr = ""//音频字幕文件
    var qIndex = 0//当前页面位于父组件的第几个
    var answerIndex = 0//当前页面的小题处在第几个
    private val fragmentList by lazy { mutableListOf<Fragment>() }

    private val mActivity by lazy { requireActivity() }

    companion object {
        /**
         * type 1:点读书模考解析；2:点读书精练 听力练习 3：精练听力练习 解析状态  4:简听力  基础篇（练习） 5:简听力 进阶篇 特训篇（试卷）
         */
        @JvmStatic
        fun newInstance(type: String, param: ExerciseFrameItem) =
            MaterialQesAnalysisFragment().apply {
                arguments = Bundle().apply {
                    putString(TYPE, type)
                    putSerializable(ITEM, param)
                }
            }
    }

    override fun configView(view: View?) {
        super.configView(view)
        tv_time.text = Html.fromHtml("<font color='#008AFF'>--:--</font> / --:--")

    }

    override fun initData() {
        super.initData()
        fragmentList.clear()
        val gson = GsonBuilder().disableHtmlEscaping().create()
        itemData?.let { question ->
            tv_title.visibility = if ("4" == type || "5" == type || "7" == type) View.VISIBLE else View.GONE
            question.questionInfo?.materialTitle?.let { materialTitle ->
                tv_title.text = materialTitle
            }
            val params = RadioGroup.LayoutParams(DeviceUtil.dp2px(this.requireContext(), 70f).toInt(), RadioGroup.LayoutParams.MATCH_PARENT)
            tv_content.visibility = if ("1" == type || "4" == type) View.VISIBLE else View.GONE
            when (type) {
                "1" -> {
                    tv_content.text = Html.fromHtml(question.questionInfo?.stem)
                }
                "4" -> {
                    question.questionInfo?.stem?.let { configViewRead(it) }
                }
            }
            val list = question.questionInfo?.item as? ArrayList<*>
            list?.forEachIndexed { index, infoItem ->
                val info = gson.fromJson(gson.toJson(infoItem), QuestionInfo::class.java)//any 转为 questionInfo类型
                info.preAuditoryPrediction = question.questionInfo?.preAuditoryPrediction ?: ""
                val rb1 = RadioButton(this.requireContext())
                rb1.layoutParams = params
                rb1.textSize = 17f
                rb1.text = "第${info.sort}题"
                rb1.setTextColor(ContextCompat.getColorStateList(this.requireContext(), R.color.color_008aff_c4cbde))
                rb1.typeface = Typeface.DEFAULT_BOLD
                rb1.gravity = Gravity.CENTER
                rb1.background = null
                rb1.buttonDrawable = null
                rg_qes.addView(rb1)
                fragmentList.add(ChoiceQesAnalysisFragment.newInstance(type, info))
                if (index == 0) {
                    rb1.isChecked = true
                }
            }
            vp_exercise.offscreenPageLimit = 10
            vp_exercise.adapter = ExercisePagerAdapter(childFragmentManager, fragmentList)
            setLrc(question)
        }
        startOrPause()
    }

    override fun handleEvent() {
        super.handleEvent()
        rg_qes.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                vp_exercise.currentItem = group.indexOfChild(group.findViewById(checkedId))
                for (index in 0 until group.childCount) {
                    val tempV = group.getChildAt(index) as RadioButton
                    tempV.textSize = 15f
                }
                val v = group.findViewById(checkedId) as RadioButton
                v.textSize = 17f
            }
        }
        vp_exercise.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(position: Int) {
                answerIndex = position
                val rb = rg_qes.getChildAt(position) as RadioButton
                if (!rb.isChecked) {
                    rb.isChecked = true
                }
                setFragmentLrc(position)
                when (val ctx = mActivity) {
                    is EBookListenExerciseActivity -> ctx.initTitle()
                }
            }

            override fun onPageScrollStateChanged(p0: Int) {
            }
        })
        chb_audio.setOnClickListener {
            startOrPause()
        }
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    seekTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun configViewRead(mStem: String) {
        //        <!--简听力字体15 textstyle normal-->
        tv_content.visibility = View.VISIBLE
        tv_content.textSize = 15f
        tv_content.typeface = Typeface.DEFAULT
        val stem = mStem.replace("<p>", "").replace("</p>", "")
        val matchers = regex(stem)
        if (matchers.isNotEmpty()) {
            refreshSpan(matchers)
        }
    }

    private fun regex(stem: String): List<MatcherBean> {
        val listMatch = mutableListOf<String>()
        val regex = "<h>.*?</h>"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(stem)
        while (matcher.find()) {
            val tempStr = matcher.group()
            val temp = tempStr.replace("<h>", "").replace("</h>", "")
            if (temp.isNotEmpty()) {
                listMatch.add(tempStr)
            }
        }
        val temp = stem.replace("<h>", "").replace("</h>", "")
        var stemTemp = stem
        val matchers = mutableListOf<MatcherBean>()
        listMatch.forEach { regex ->
            val regexTemp = regex.replace("<h>", "").replace("</h>", "")
            val bean = MatcherBean()
            bean.parentStr = temp
            bean.patternStr = regexTemp
            bean.startIndex = stemTemp.indexOf(regex)
            matchers.add(bean)
            stemTemp = stemTemp.replaceFirst(regex, regexTemp)
        }
        return matchers
    }

    private fun refreshSpan(matchers: List<MatcherBean>) {
        val contentSpan = SpannableStringBuilder()
        contentSpan.append(matchers[0].parentStr)
        try {
            matchers.forEach { bean ->
                if (!bean.isClick) {
                    contentSpan.setSpan(object : ClickableSpan() {
                        override fun onClick(view: View) {
                            bean.isClick = true
                            refreshSpan(matchers)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = false
                        }
                    }, bean.startIndex, bean.startIndex + (bean.patternStr?.length ?: 0), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    contentSpan.setSpan(ForegroundColorSpan(Color.parseColor("#D6ECFF")), bean.startIndex, bean.startIndex + (bean.patternStr?.length ?: 0), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    contentSpan.setSpan(BackgroundColorSpan(Color.parseColor("#D6ECFF")), bean.startIndex, bean.startIndex + (bean.patternStr?.length ?: 0), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                } else {
                    contentSpan.setSpan(ForegroundColorSpan(Color.parseColor("#62CA00")), bean.startIndex, bean.startIndex + (bean.patternStr?.length ?: 0), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                }
            }
        } catch (e: Exception) {
        }
        tv_content.text = contentSpan
        tv_content.movementMethod = LinkMovementMethod.getInstance()
    }

    fun scrollPos(pos: Int) {
        vp_exercise.currentItem = pos
    }

    fun setFragmentLrc(position: Int) {
        when (val fragment = fragmentList[position]) {
            is ChoiceQesAnalysisFragment -> {
                if (fragment.isAdded) {
                    fragment.setLrc(lrcStr)
                }
            }
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
            completeMusic()
        }
        mediaPlayer!!.setOnErrorListener { player, what, extra ->
            stopMusic()
            true
        }
        mediaPlayer!!.setOnPreparedListener { player ->
            try {
                isPrepared = true
                tv_time.text = Html.fromHtml("<font color='#008AFF'>00:00</font> / ${time(player.duration)}")
                seek_bar.progress = 0
                seek_bar.secondaryProgress = 0
                seek_bar.max = player.duration.toInt()
            } catch (e: Exception) {
            }
        }
    }

    /**
     * 刷新进度定时器
     */
    private fun initTask() {
        cancelTask()
        timer = Timer(true)
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    try {
                        if (mediaPlayer!!.isPlaying) {
                            val index = mediaPlayer!!.currentPosition ?: 0
                            seek_bar.progress = index.toInt()
                            tv_time.text = Html.fromHtml("<font color='#008AFF'>${time(index)}</font> / ${time(mediaPlayer!!.duration)}")
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }, 1000, 500)
    }

    fun cancelTask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
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
        if (!isPrepared) {
            initPlayer()
            presenter.getResourceInfo(key, "3") {
                mediaPlayer!!.dataSource = it.url
                mediaPlayer!!.prepareAsync()
            }
        }
    }

    fun startMusic() {
        if (isPrepared && mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            chb_audio.isChecked = true
            mediaPlayer!!.start()
            initTask()
        }
    }

    fun seekTo(time: Long) {
        if (isPrepared && mediaPlayer != null) {
            tv_time.text = Html.fromHtml("<font color='#008AFF'>${time(time)}</font> / ${time(mediaPlayer!!.duration)}")
            isPlaying = mediaPlayer!!.isPlaying
            if (isPlaying) {
                mediaPlayer!!.pause()
            }
            mediaPlayer!!.seekTo(time)
            if (isPlaying) {
                mediaPlayer!!.start()
            } else {
                mediaPlayer!!.pause()
            }
        }
    }

    fun startOrPause() {
        if (isPrepared && mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            initTask()
            chb_audio.isChecked = true
            mediaPlayer!!.start()
        } else if (isPrepared && mediaPlayer != null && mediaPlayer!!.isPlaying) {
            cancelTask()
            chb_audio.isChecked = false
            mediaPlayer!!.pause()
        } else if (!isPrepared) {
            itemData?.questionInfo?.let { item ->
                getResourceInfo(item.resourceKey ?: "")
            }
        }
    }

    fun pauseMusic() {
        cancelTask()
        chb_audio.isChecked = false
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
        }
    }

    fun stopMusic() {
        try {
            isPrepared = false
            cancelTask()
            chb_audio.isChecked = false
            if (mediaPlayer != null) {
                mediaPlayer!!.stop()
                mediaPlayer!!.reset()
            }
        } catch (e: Exception) {
        }
    }

    fun completeMusic() {
        cancelTask()
        chb_audio.isChecked = false
        if (mediaPlayer != null) {
            mediaPlayer!!.seekTo(0)
            mediaPlayer!!.pause()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseMusic()
    }

    /**
     * 一期中听力原文在此处请求网络获得，二期听力原文统一在此赋值
     */
    private fun setLrc(question: ExerciseFrameItem) {
        if ("1" == type || "3" == type) {
            //一期中 在lrc文件中给出，lrc计算答题时长答案定位句 提问句
            getLrcData(question.questionInfo?.lrcUrl ?: "")
        } else if ("4" == type || "7" == type) {
            //简听力 听力原文直接在接口listeningText 返回了
            lrcStr = question.questionInfo?.listeningText?.replace("<*>", "<font color='#62CA00'>")?.replace("</*>", "</font>") ?: ""
            Handler().postDelayed({
                setFragmentLrc(answerIndex)
            }, 800)
        }
    }

    private fun getLrcData(url: String) {
        if (url.isNotEmpty()) {
            presenter.lrc(url) { data ->
                lrcStr = getLrcStr(data ?: "")
                Handler().postDelayed({
                    setFragmentLrc(answerIndex)
                }, 800)
            }
        }
    }

    private fun getLrcStr(data: String): String {
        val lrcStr = StringBuilder()
        if (TextUtils.isEmpty(data)) {
            Log.e(this.tag, " lrcFile do not exist")
            return ""
        } else {
            val reader = StringReader(data)
            val br = BufferedReader(reader)
            try {
                var line: String?
                do {
                    line = br.readLine()
                    if (line != null && line.trim { it <= ' ' }.isNotEmpty()) {
                        lrcStr.append(parse("$line<br>"))
                    }
                } while (line != null)
            } catch (e: Exception) {
                return ""
            } finally {
                try {
                    br.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                reader.close()
            }
        }
        return lrcStr.toString().replace("<*>", "<font color='#62CA00'>").replace("</*>", "</font>")
    }

    private fun parse(lrcRowDada: String): String {
        return try {
            val lastIndexOfRightBracket = lrcRowDada.indexOf("]")
            val content = lrcRowDada.substring(lastIndexOfRightBracket + 1)
            content
        } catch (var14: Exception) {
            ""
        }
    }

    fun goNext() {
        if (vp_exercise.currentItem == fragmentList.size - 1) {
            when (val ctx = mActivity) {
                is EBookListenExerciseActivity -> {
                    ctx.goNext()
                }
            }
        } else {
            answerIndex += 1
            vp_exercise.currentItem = answerIndex
        }
    }
}