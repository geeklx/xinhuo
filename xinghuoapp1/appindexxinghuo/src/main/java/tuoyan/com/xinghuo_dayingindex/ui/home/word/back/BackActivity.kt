package tuoyan.com.xinghuo_dayingindex.ui.home.word.back

import android.view.View
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_back.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.LearnStatus
import tuoyan.com.xinghuo_dayingindex.bean.WordsByCatalogkey
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.ui.home.word.WordPresenter
import tuoyan.com.xinghuo_dayingindex.ui.home.word.words.WordsActivity
import tuoyan.com.xinghuo_dayingindex.utlis.MediaPlayerUtlis
import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import java.lang.reflect.Type


class BackActivity : LifeActivity<WordPresenter>() {
    override val presenter = WordPresenter(this)
    override val layoutResId = R.layout.activity_back
    private val learn by lazy { intent.getBooleanExtra(LEARN, true) }
    override val title by lazy { intent.getStringExtra(TITLE) ?: "" }
    private val isWrong by lazy { intent.getBooleanExtra(IS_WRONG, false) }
    private val gradeKey by lazy { intent.getStringExtra(GRADE_KEY) ?: "" }
    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    private var totalCount = 0
    private var wrongNum = 0
    private var learnStatus: LearnStatus? = null
    private var eliminateWrongWordNum = 0
    private var nextCatalogKey: String? = null
    private var data: List<WordsByCatalogkey>? = null
    private var position = 0
    private var des = ""
    private var url = ""
    private val adapter by lazy {
        BackAdapter(review = !learn, isWrong = isWrong, current = {
            val current = (if (position + 20 > totalCount) totalCount else (position + 20)) - it
            tv_position.text = "$current"
            pb_bar.progress = current

        }, play = { it ->
            //播放音频
            it?.let { sound ->
                play(sound)
            }
        }, recordWrongWord = { it ->
            //提交错词本 如果是复习
            it?.let {
                if (isWrong)
                else {
                    wrongNum++
                    presenter.recordWrongWord(
                        mutableMapOf(
                            "key" to it, "gradeKey" to gradeKey
                        )
                    ) {}
                }
            }
        }, eliminateWrongWord = { it ->
            //移除错词 如果是消灭错词
            it?.let {
                if (isWrong) {
                    eliminateWrongWordNum++
                    presenter.deleteWrongWord(it) {}
                    addData()
                }// : 2018/10/23 10:46
            }
        }, next = {
            if (learn) {// : 2018/10/30 11:30  复习本组
                startActivity<BackActivity>(
                    GRADE_KEY to gradeKey, KEY to key, TITLE to title, LEARN to false
                )
                onBackPressed()
            } else {
                // : 2018/10/30 11:30  再次复习

                startActivity<BackActivity>(
                    BackActivity.GRADE_KEY to gradeKey,
                    BackActivity.KEY to key,
                    BackActivity.TITLE to title,
                    BackActivity.LEARN to false
                )
                onBackPressed()
            }
            //            学习下一组
        }, repeat = {
            if (learn) {// : 2018/10/30 11:30  下一组
                var t = title
                try {
                    if (title.isNotEmpty()) {
                        val s = title.substring(1, title.length - 1)
                        val i = s.toInt() + 1
                        t = "第${i}组"
                    }
                } catch (e: Exception) {
                }
                startActivity<BackActivity>(
                    BackActivity.TITLE to t,
                    BackActivity.GRADE_KEY to gradeKey,
                    BackActivity.KEY to nextCatalogKey,
                    BackActivity.LEARN to true
                )
                onBackPressed()
            } else {
                // : 2018/10/30 11:30  错词本
                startActivity<WordsActivity>(
                    WordsActivity.IS_WRONG to true,
                    WordsActivity.GRADE_KEY to gradeKey,
                    WordsActivity.TITLE to "错词本"
                )
                onBackPressed()

            }
        }, learnSubmit = { _ ->
            // : 2018/10/23 15:06  学习提交
            learnSubmit()
        }) {
            // : 2018/10/23 15:07  复习提交
            reviewSubmit()
            // : 2018/11/23 9:37  显示分享按钮
        }
    }

    private fun addData() {
        if (adapter.getDateCount() == 1) {
            val words = if (data?.size ?: 0 > position + 40) {
                position += 20
                data?.subList(position, position)
            } else if (data?.size ?: 0 > position + 20) {
                position += 20
                data?.subList(position, data?.size ?: 0)
            } else {
                null
            }
            if (words == null || words.isEmpty()) return
            adapter.setData(words)
        }
    }


    private fun reviewSubmit() {
        presenter.reviewSubmit(
            mutableMapOf(
                "catalogKey" to key,
                "totalNum" to totalCount.toString(),
                "rightNum" to (totalCount - wrongNum).toString(),
                "wrongNum" to wrongNum.toString()
            )
        ) {
            adapter.rightRate = it["rightRate"] ?: ""
            adapter.totalNum = totalCount.toString()
            adapter.rightNum = (totalCount - wrongNum).toString()
            adapter.notifyDataSetChanged()
            iv_share.visibility = View.VISIBLE
            des = "本次复习词汇${adapter.totalNum ?: "20"}个单词，熟练掌握${
                adapter.rightNum ?: "20"
            }个，你也来试试？"
            url =
                "$WEB_BASE_URL/share?" + "type=${4}" + "&userName=${SpUtil.userInfo.name}" + "&number1=${adapter.totalNum}&number2=${adapter.rightNum}"

        }
    }

    private fun learnSubmit() {
        if (isWrong) {
            adapter.learnNum = eliminateWrongWordNum.toString()
            adapter.notifyDataSetChanged()
        } else {
            presenter.learnSubmit(
                mutableMapOf(
                    "catalogKey" to key,
                    "classifyKey" to (learnStatus?.classifyKey ?: ""),
                    "totalNum" to totalCount.toString()
                )
            ) {
                adapter.totalNum = it["learnNum"] ?: ""
                nextCatalogKey = it["nextCatalogKey"]
                adapter.isNext = !nextCatalogKey.isNullOrBlank()
                adapter.learnNum = totalCount.toString()
                adapter.notifyDataSetChanged()
                iv_share.visibility = View.VISIBLE
                des = "本次学习词汇${adapter.learnNum ?: "20"}个单词，累计学习词汇${
                    adapter.totalNum ?: "20"
                }个，你也来试试？"
                url =
                    "$WEB_BASE_URL/share?" + "type=${2}" + "&userName=${SpUtil.userInfo.name}" + "&number1=${adapter.learnNum}&number2=${adapter.totalNum}"
            }
        }
    }

    private fun play(sound: String) {
        MediaPlayerUtlis.start(ctx, sound, {
            //                    view.startAnim()
            adapter.isPlay = true
        }, {
            //                    view.stopAnim()
            adapter.isPlay = false
        }) {
            adapter.isPlay = false
            //                    view.stopAnim()
            if (it.isNotBlank()) mToast(it)
        }
    }

    override fun configView() {
        tv_title.text = title
//        recycler_view.adapter = adapter
        // 取 data = MyApp.instance.data
        val data2: String = SPUtils.getInstance().getString("WordsByCatalogkey", "")
        val gson2 = Gson()
        val listType2: Type = object : TypeToken<List<WordsByCatalogkey?>?>() {}.getType()
        data = gson2.fromJson<List<WordsByCatalogkey>>(data2, listType2)
    }


    override fun handleEvent() {}

    override fun initData() {
        if (isWrong) {
            val words = if (data?.size ?: 0 > 20) {
                data?.subList(0, 20)
            } else {
                data//数据过大传参崩溃
            }
            recycler_view.adapter = adapter
            adapter.setData(words)
            totalCount = data?.size ?: 0
            tv_total.text = "/$totalCount"
            tv_position.text = "0"
            pb_bar.max = totalCount
        } else {
            totalCount = 0
            wrongNum = 0
            presenter.getWordsByCatalogkey(key, 1) {
                learnStatus = it
                recycler_view.adapter = adapter
                adapter.setData(it.list)
                totalCount = it.totalCount
                tv_total.text = "/$totalCount"
                tv_position.text = "0"
                pb_bar.max = totalCount
            }
        }
    }

    @SensorsDataTrackViewOnClick
    fun share(v: View) {
        ShareDialog(ctx) {
            ShareUtils.share(
                this, it, "四六级英语大杀器，PickMe,${SpUtil.userInfo.name}！", des, url
            )
            // TODO: 2018/11/23 9:41  分享链接
        }.show()
    }

    override fun onPause() {
        MediaPlayerUtlis.release()
        super.onPause()
    }

    override fun onDestroy() {
//        data = null
        SPUtils.getInstance().put("WordsByCatalogkey", "")
        super.onDestroy()
    }

    companion object {
        const val LEARN = "learn"
        const val GRADE_KEY = "gradeKey"
        const val KEY = "key"
        const val TITLE = "title"
        const val IS_WRONG = "isWrong"
    }
}
