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
            //????????????
            it?.let { sound ->
                play(sound)
            }
        }, recordWrongWord = { it ->
            //??????????????? ???????????????
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
            //???????????? ?????????????????????
            it?.let {
                if (isWrong) {
                    eliminateWrongWordNum++
                    presenter.deleteWrongWord(it) {}
                    addData()
                }// : 2018/10/23 10:46
            }
        }, next = {
            if (learn) {// : 2018/10/30 11:30  ????????????
                startActivity<BackActivity>(
                    GRADE_KEY to gradeKey, KEY to key, TITLE to title, LEARN to false
                )
                onBackPressed()
            } else {
                // : 2018/10/30 11:30  ????????????

                startActivity<BackActivity>(
                    BackActivity.GRADE_KEY to gradeKey,
                    BackActivity.KEY to key,
                    BackActivity.TITLE to title,
                    BackActivity.LEARN to false
                )
                onBackPressed()
            }
            //            ???????????????
        }, repeat = {
            if (learn) {// : 2018/10/30 11:30  ?????????
                var t = title
                try {
                    if (title.isNotEmpty()) {
                        val s = title.substring(1, title.length - 1)
                        val i = s.toInt() + 1
                        t = "???${i}???"
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
                // : 2018/10/30 11:30  ?????????
                startActivity<WordsActivity>(
                    WordsActivity.IS_WRONG to true,
                    WordsActivity.GRADE_KEY to gradeKey,
                    WordsActivity.TITLE to "?????????"
                )
                onBackPressed()

            }
        }, learnSubmit = { _ ->
            // : 2018/10/23 15:06  ????????????
            learnSubmit()
        }) {
            // : 2018/10/23 15:07  ????????????
            reviewSubmit()
            // : 2018/11/23 9:37  ??????????????????
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
            des = "??????????????????${adapter.totalNum ?: "20"}????????????????????????${
                adapter.rightNum ?: "20"
            }????????????????????????"
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
                des = "??????????????????${adapter.learnNum ?: "20"}??????????????????????????????${
                    adapter.totalNum ?: "20"
                }????????????????????????"
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
        // ??? data = MyApp.instance.data
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
                data//????????????????????????
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
                this, it, "???????????????????????????PickMe,${SpUtil.userInfo.name}???", des, url
            )
            // TODO: 2018/11/23 9:41  ????????????
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
