package tuoyan.com.xinghuo_dayingindex.ui.home.word.back

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_add_address.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.nestedScrollView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.Question
import tuoyan.com.xinghuo_dayingindex.bean.WordsByCatalogkey
import tuoyan.com.xinghuo_dayingindex.widegt.PagingScrollHelper
import tuoyan.com.xinghuo_dayingindex.widegt.SweepView
import tuoyan.com.xinghuo_dayingindex.widegt.sweepView
import java.util.concurrent.TimeUnit

/**
 * 创建者：
 * 时间：  2018/9/21.
 */


class BackAdapter(val review: Boolean = false,
                  val isWrong: Boolean = false,
                  val current: (Int) -> Unit,
                  val play: (String?) -> Unit,
                  val recordWrongWord: (String?) -> Unit,
                  val eliminateWrongWord: (String?) -> Unit,
                  val next: (String?) -> Unit,
                  val repeat: (String?) -> Unit,
                  val learnSubmit: (String?) -> Unit,
                  val reviewSubmit: (String?) -> Unit
) : BaseAdapter<WordsByCatalogkey>(isFooter = true) {
    private var recyclerView: RecyclerView? = null
    var isPlay = false
    var learnNum = ""
    var totalNum = ""
    var rightRate = ""
    var rightNum = ""
    var isNext = false
    private var canScroll = true
    private var time = 0
    private var playTime = 0
    private var size = 10
    private val layoutManager by lazy {
        object : LinearLayoutManager(mContext ?: recyclerView?.context,
                RecyclerView.HORIZONTAL, false) {
            override fun canScrollHorizontally(): Boolean {
                return canScroll && super.canScrollHorizontally()
            }

            override fun canScrollVertically(): Boolean {
                return canScroll && super.canScrollVertically()
            }
        }
    }
    private val disposable by lazy {
        Observable.interval(0, 50, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
//            .take(99999999)
                .subscribe {
                    val position = layoutManager.findFirstVisibleItemPosition()
                    val view = layoutManager.findViewByPosition(position)
                    playIcon(view)
                    timing(view)
                }
    }

    /**
     * 答题计时器5s
     */
    private fun timing(view: View?) {
        time += 1
        if (view != null) {
            val sv = findViewById(view, R.id.sv_sweep)
            val sweep = time.toFloat() / 100f * 360
            (sv as? SweepView)?.setSweep(sweep)
            if (sweep == 360f) (sv as? SweepView)?.visibility = View.GONE
        }
        if (time == 100) {
            time = 0
            //                        val view = layoutManager.findViewByPosition(position)
            if (view != null) {
                val sv = findViewById(view, R.id.btn_unkown)
                if (sv?.visibility == View.VISIBLE)
                    sv.performClick()
            }
        }
    }

    /**
     * 音频播放小喇叭
     */
    private fun playIcon(view: View?) {
        if (isPlay) {
            playTime += 1
            if (playTime == 5) {
                playTime = 0
                val view_1 = findViewById(view, R.id.tv_yb)
                val view_2 = findViewById(view, R.id.tv_2_yb)
                view_1?.isSelected = !(view_1?.isSelected ?: false)
                view_2?.isSelected = !(view_2?.isSelected ?: false)
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        if (disposable?.isDisposed == false) disposable?.dispose()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun setData(data: List<WordsByCatalogkey>?) {
        var copy: WordsByCatalogkey? = null
        if (!review) {//解决学习状态下只有一个单词的情况下不跳转答题的问题
            if (data?.size == 1) {
                copy = data[0].copy()
                copy.learn = false
                copy.correct = false
            }
        }
        size = data?.size ?: 10
        if (size < 10)
            size = 10
        super.setData(data)
        addData(copy)
        disposable.isDisposed
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        recyclerView.layoutManager = layoutManager
        val helper = PagingScrollHelper()
        helper.setUpRecycleView(recyclerView)
        helper.setOnPageChangeListener(object : PagingScrollHelper.onPageChangeListener {
            override fun onPageChange(index: Int) {
//                mToast(index.toString())
                selectedItem(index)
            }
        })
    }

    /**
     * 当前选中项 通过处理数据现在右滑
     */
    private fun selectedItem(index: Int) {
        time = 0
        playTime = 0
        if (index > 0) {
            if (review) {//复习逻辑
                removeAt(0)//复习时只进行移除操作
                if (getDateCount() == 0) {
                    // : 2018/10/16 11:38  提交复习结果
                    reviewSubmit("")
                }
            } else {//学了逻辑
                if (getDateCount() > size - 10) {//如果是前10个
                    if (getDateCount() == size - 10 + 2) {//前十个 的最后两个
                        val catalog = removeAt(0)
                        if (catalog?.key != getData()[0].key) {//第一组最后两个为同一单词时不进行队列添加
                            catalog?.learn = false
                            if (catalog?.correct == false) addData(catalog, getDateCount() - (size - 10))//做错了添加到队列尾部
                        }
                    } else {//前十个
                        val catalog = removeAt(0)
                        catalog?.learn = false
                        if (catalog?.correct == false) addData(catalog, getDateCount() - (size - 10)) //做错了添加到队列尾部
                    }
                } else if (getDateCount() == 2) {//后十个 的最后两个
                    val catalog = removeAt(0)
                    if (catalog?.key != getData()[0].key) {//最后两个为同一单词时不进行队列添加
                        catalog?.learn = false
                        if (catalog?.correct == false) addData(catalog)//做错了添加到队列尾部
                    }
                } else {//后十个
                    val catalog = removeAt(0)
                    catalog?.learn = false
                    if (catalog?.correct == false) addData(catalog)//做错了添加到队列尾部
                }
                if (getDateCount() == 0) {
                    // : 2018/10/16 11:38  提交学习结果
                    learnSubmit("")
                }
            }
            canScroll = isCanScroll()
            current(getDateCount())
            if (getData().isNotEmpty()) {
                play(getData()[0].sound)
            }

        }
    }

    private fun findViewById(view: View?, resId: Int): View? {
        if (view == null) return null
        if (view.id == resId) return view
        return if (view is ViewGroup) {
            view.findViewById(resId)
        } else
            null
    }

    /**
     * 是否允许滑动
     */
    fun isCanScroll(): Boolean {
        if (getData().isNotEmpty())
            return getData()[0].learn
        return false
    }

    @SuppressLint("ResourceType")
    override fun convert(holder: ViewHolder, item: WordsByCatalogkey) {
        holder.setIsRecyclable(false)//解决页面反正后复用不显示的问题
//        var mIsShowBack = !item.learn
        if (review) {
            item.learn = false
        }
//        canScroll = !mIsShowBack
        /*初始化动画*/
        val animatorOut = AnimatorInflater.loadAnimator(mContext,
            R.anim.anim_out) as AnimatorSet
        val animatorIn = AnimatorInflater.loadAnimator(mContext, R.anim.anim_in) as AnimatorSet
        animatorOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                holder.getView(R.id.view).visibility = View.VISIBLE
                holder.setVisible(R.id.card_front, View.VISIBLE)
                        .setVisible(R.id.card_back, View.VISIBLE)
                        .setVisible(R.id.btn_unkown, View.GONE)
                canScroll = false
            }
        })
        animatorIn.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
//                holder.getView(R.id.fl_container).isClickable = true
                holder.getView(R.id.view).visibility = View.GONE
                canScroll = item.learn
                if (!item.learn) {
                    holder.setVisible(R.id.card_front, View.GONE)
                            .setVisible(R.id.btn_unkown, View.VISIBLE)
                } else {
                    holder.setVisible(R.id.card_back, View.GONE)
                            .setVisible(R.id.btn_unkown, View.GONE)
                }
            }
        })
        /*设置改变视角距离*/
        val distance = 32000
        val scale = mContext!!.resources.displayMetrics.density * distance
        holder.getView(R.id.card_front).cameraDistance = scale
        holder.getView(R.id.card_back).cameraDistance = scale
        /*绑定数据*/
        holder.setText(R.id.tv_title, item.word ?: "")
                .setText(R.id.tv_2_title, item.word ?: "")
                .setText(R.id.tv_translation, Html.fromHtml(item.paraphrase ?: ""))//文本翻译
                .setText(R.id.tv_yb, "[${item.symbol ?: ""}]")//文本翻译
                .setText(R.id.tv_2_yb, "[${item.symbol ?: ""}]")//文本翻译
                .setText(R.id.tv_liju, Html.fromHtml(item.exampleSentence ?: ""))//文本翻译
//                .setAdapter(R.id.rv_translation, TranslationAdapter(mutableListOf(1, 1, 1)))//列表翻译
                .setAdapter(R.id.rv_option, OptionAdapter(item.questionInfo) {
                    // : 2018/10/12 11:45  操作页面翻转 跳页
                    if (it) {//正确跳页
                        item.correct = true
                        if (itemCount > 1) {//有下一页跳下一页
                            //通过雷系克莱色为有的滚动机制调用自定义的滑动方法
                            canScroll = true
                            recyclerView?.fling((recyclerView?.minFlingVelocity ?: 50) + 1, 0)
                        } else {//没有下一页返回完成
                            // : 2018/10/12 11:59  学习或者复习结束
                        }
                        eliminateWrongWord(item.key)
                    } else {//选错翻转
                        wrong(item, animatorOut, holder, animatorIn)
                    }
                })
                .setOnClickListener(R.id.btn_unkown) {
                    wrong(item, animatorOut, holder, animatorIn)
                }
                .setOnClickListener(R.id.tv_yb) { play(item.sound) }
                .setOnClickListener(R.id.tv_2_yb) { play(item.sound) }

        /*设置默认显示面*/
        if (item.learn) {
            holder.setVisible(R.id.card_front, View.VISIBLE)
                    .setVisible(R.id.card_back, View.GONE)
                    .setVisible(R.id.btn_unkown, View.GONE)
        } else {
            holder.setVisible(R.id.card_front, View.GONE)
                    .setVisible(R.id.card_back, View.VISIBLE)
                    .setVisible(R.id.btn_unkown, View.VISIBLE)
        }

        if (holder.layoutPosition == 0) {
            play(item.sound)
            canScroll = item.learn
        }
    }

    /**
     * 做错时的逻辑
     */
    private fun wrong(item: WordsByCatalogkey, animatorOut: AnimatorSet, holder: ViewHolder, animatorIn: AnimatorSet) {
        if (item.learn) {
            //                        animatorOut.setTarget(holder.getView(R.id.card_front))
            //                        animatorIn.setTarget(holder.getView(R.id.card_back))
            //                        animatorOut.start()
            //                        animatorIn.start()
            //                        mIsShowBack =     true
        } else { // 背面朝上
            animatorOut.setTarget(holder.getView(R.id.card_back))
            animatorIn.setTarget(holder.getView(R.id.card_front))
            animatorOut.start()
            animatorIn.start()
            item.learn = true
            item.correct = false
        }
        if (review) {
            // : 2018/10/16 9:00  添加错词本
            recordWrongWord(item.key)
        } else { //学习时 如果最后一个做错了 复制一份重新添加到队尾
            if (getDateCount() == 1) {//最后一个单词时的逻辑
                val copy = item.copy()
                copy.learn = false
                copy.correct = false
                addData(copy)
            }
            if (getDateCount() == size - 10 + 1) {//前十个的最后一个单词时的逻辑
                val copy = item.copy()
                copy.learn = false
                copy.correct = false
                addData(copy, getDateCount() - (size - 10))
            }
        }
    }


    override fun convertView(context: Context,parent: ViewGroup) = with(context) {
        frameLayout {
            id = R.id.fl_container
            lparams(matchParent, matchParent)
            relativeLayout {
                id = R.id.card_front
                backgroundResource = R.drawable.back_word_bg
//                cardElevation = dip(3).toFloat()
//                radius = dip(22).toFloat()

                nestedScrollView {
                    verticalLayout {
                        textView {
                            id = R.id.tv_title
                            textSize = 24f
                            textColor = resources.getColor(R.color.color_222831)

                        }.lparams {
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                        textView {
                            id = R.id.tv_yb
                            textSize = 15f
                            textColor = resources.getColor(R.color.color_c3c7cb)
                            compoundDrawablePadding = dip(10)
                            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_back_play, 0)
                        }.lparams {
                            gravity = Gravity.CENTER_HORIZONTAL
                            topMargin = dip(20)
                        }
                        space().lparams(wrapContent, dip(23))
//                        recyclerView {
//                            id = R.id.rv_translation
//                            // : 2018/10/12 15:17  列表翻译
//                            layoutManager = LinearLayoutManager(context)
//                        }.lparams(matchParent, wrapContent)
                        textView {
                            id = R.id.tv_translation
                            // : 2018/10/12 15:17   文本翻译
                            textSize = 13f
                            gravity = Gravity.CENTER
                            textColor = resources.getColor(R.color.color_222831)
                        }.lparams(matchParent, wrapContent)
                        space().lparams(wrapContent, dip(40))
                        textView("例句") {
                            textSize = 12f
                            gravity = Gravity.CENTER
                            textColor = resources.getColor(R.color.color_222831)
                            backgroundResource = R.drawable.bg_shape_3_b4cbff
                        }.lparams(dip(32), dip(17))
                        space().lparams(wrapContent, dip(20))
                        textView {
                            id = R.id.tv_liju
                            textSize = 14f
                            textColor = resources.getColor(R.color.color_222831)
                            setLineSpacing(dip(3).toFloat(), 1f)
                        }.lparams(matchParent, wrapContent)
                    }.lparams(matchParent, matchParent)
                }.lparams(matchParent, matchParent) {
                    margin = dip(40)
                }
            }.lparams(matchParent, matchParent) {
                horizontalMargin = dip(17)
                topMargin = dip(7)
                bottomMargin = dip(71)
            }
            relativeLayout {
                id = R.id.card_back
                backgroundResource = R.drawable.back_word_bg
//                cardElevation = dip(3).toFloat()
//                radius = dip(22).toFloat()
                frameLayout {
                    sweepView {
                        id = R.id.sv_sweep
                    }.lparams(dip(20), dip(20)) {
                        gravity = Gravity.END
                        topMargin = dip(15)
                        rightMargin = dip(15)
                    }
                    nestedScrollView {
                        verticalLayout {
                            textView {
                                id = R.id.tv_2_title
                                textSize = 24f
                                textColor = resources.getColor(R.color.color_222831)

                            }.lparams {
                                gravity = Gravity.CENTER_HORIZONTAL
                            }
                            textView {
                                id = R.id.tv_2_yb
                                textSize = 15f
                                textColor = resources.getColor(R.color.color_c3c7cb)
                                compoundDrawablePadding = dip(10)
                                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_back_play, 0)
                            }.lparams {
                                gravity = Gravity.CENTER_HORIZONTAL
                                topMargin = dip(20)
                            }
                            space().lparams(wrapContent, dip(55))
                            recyclerView {
                                id = R.id.rv_option
                                layoutManager = LinearLayoutManager(context)
                            }.lparams(matchParent, wrapContent)

                        }.lparams(matchParent, matchParent)
                    }.lparams(matchParent, matchParent) {
                        margin = dip(40)
                    }
                }.lparams(matchParent, matchParent)
            }.lparams(matchParent, matchParent) {
                horizontalMargin = dip(25)
                topMargin = dip(7)
                bottomMargin = dip(71)
            }
            button("不认识") {
                id = R.id.btn_unkown
                backgroundResource = R.drawable.bg_shape_25_222831
                textSize = 15f
                gravity = Gravity.CENTER
                textColor = resources.getColor(R.color.color_ffffff)
            }.lparams(dip(160), dip(45)) {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                bottomMargin = dip(20)
            }
            view {
                id = R.id.view
                setOnClickListener { }
                visibility = View.GONE
            }.lparams(matchParent, matchParent)
        }
    }

    override fun footer(holder: ViewHolder) {
        when {
            isWrong -> {
                holder.setVisible(R.id.tv_rate_text, View.GONE)
                        .setVisible(R.id.tv_rate, View.GONE)
                        .setText(R.id.tv_rate, "")
                        .setText(R.id.tv_rate_dec, "所有易错单词都消灭完啦~")
                        .setText(R.id.tv_learn_num, learnNum + "个")
                        .setText(R.id.tv_learn_text, "本次学习词汇")
                        .setText(R.id.tv_learn_total_num, totalNum + "个")
                        .setText(R.id.tv_learn_total_text, "累计学习词汇")
                        .setBackgroundResource(R.id.vl_rate, R.drawable.bg_word_good)
                        .setText(R.id.btn_review, "下一组")
                        .setText(R.id.btn_next, "复习本组单词")
                        .setVisible(R.id.btn_review, View.GONE)
                        .setVisible(R.id.btn_next, View.GONE)
                        .setVisible(R.id.vl_learn_total, View.GONE)
            }
            review -> holder.setVisible(R.id.tv_rate_text, View.VISIBLE)
                    .setVisible(R.id.tv_rate, View.VISIBLE)
                    .setText(R.id.tv_rate, "$rightRate%")
                    .setText(R.id.tv_rate_dec, "本组单词已经全部背完")
                    .setText(R.id.tv_learn_num, totalNum + "个")
                    .setText(R.id.tv_learn_text, "本次复习词汇")
                    .setText(R.id.tv_learn_total_num, rightNum + "个")
                    .setText(R.id.tv_learn_total_text, "熟练掌握词汇")
                    .setBackgroundResource(R.id.vl_rate, R.drawable.bg_word_rate)
                    .setText(R.id.btn_review, "查看错词本")
                    .setText(R.id.btn_next, "再次复习")
                    .setOnClickListener(R.id.btn_review) {
                        repeat("")
                    }.setOnClickListener(R.id.btn_next) {
                        next("")
                    }
            else -> holder.setVisible(R.id.tv_rate_text, View.GONE)
                    .setVisible(R.id.tv_rate, View.GONE)
                    .setText(R.id.tv_rate, "")
                    .setText(R.id.tv_rate_dec, "本组单词已经全部背完")
                    .setText(R.id.tv_learn_num, learnNum + "个")
                    .setText(R.id.tv_learn_text, "本次学习词汇")
                    .setText(R.id.tv_learn_total_num, totalNum + "个")
                    .setText(R.id.tv_learn_total_text, "累计学习词汇")
                    .setBackgroundResource(R.id.vl_rate, R.drawable.bg_word_good)
                    .setText(R.id.btn_review, "下一组")
                    .setBackgroundResource(R.id.btn_review, if (isNext)
                        R.drawable.bg_shape_34_4c84ff
                    else
                        R.drawable.bg_shape_5_c3c7cb)
                    .setText(R.id.btn_next, "复习本组单词")
                    .setOnClickListener(R.id.btn_review) {
                        // : 2018/10/23 15:18  左
                        if (isNext)
                            repeat("")
                    }.setOnClickListener(R.id.btn_next) {
                        // : 2018/10/23 15:18  右
                        next("")
                    }
        }

    }

    override fun footerView(context: Context,parent: ViewGroup) = with(context) {
        frameLayout {
            id = R.id.fl_container
            lparams(matchParent, matchParent)
            linearLayout {
                button("再来一次") {
                    id = R.id.btn_review
                    backgroundResource = R.drawable.bg_shape_34_4c84ff
                    textSize = 15f
                    gravity = Gravity.CENTER
                    textColor = resources.getColor(R.color.color_ffffff)
                }.lparams(0, dip(45), 1f) {
                    bottomMargin = dip(20)
                }
                space().lparams(dip(15), wrapContent)
                button("下一组") {
                    id = R.id.btn_next
                    backgroundResource = R.drawable.bg_shape_34_4c84ff
                    textSize = 15f
                    gravity = Gravity.CENTER
                    textColor = resources.getColor(R.color.color_ffffff)
                }.lparams(0, dip(45), 1f) {
                    bottomMargin = dip(20)
                }

            }.lparams(matchParent, wrapContent) {
                gravity = Gravity.BOTTOM
                horizontalMargin = dip(25)
            }
            cardView {
                id = R.id.card_front
                cardElevation = dip(3).toFloat()
                radius = dip(22).toFloat()
                verticalLayout {
                    gravity = Gravity.CENTER_HORIZONTAL
                    verticalLayout {
                        id = R.id.vl_rate
                        gravity = Gravity.CENTER
                        textView("掌握率") {
                            id = R.id.tv_rate_text
                            textSize = 12f
                            gravity = Gravity.CENTER
                            textColor = resources.getColor(R.color.color_ffffff)
                        }
                        space().lparams(wrapContent, dip(10))
                        textView {
                            id = R.id.tv_rate
                            gravity = Gravity.CENTER
                            textSize = 14f
                            textColor = resources.getColor(R.color.color_ffffff)
                        }

                        backgroundResource = R.drawable.bg_word_rate
                    }.lparams(matchParent, dip(260))
                    textView {
                        id = R.id.tv_rate_dec
                        textSize = 14f
                        gravity = Gravity.CENTER
                        textColor = resources.getColor(R.color.color_222831)
                    }
                    linearLayout {
                        gravity = Gravity.CENTER_VERTICAL
                        verticalLayout {
                            gravity = Gravity.CENTER_HORIZONTAL
                            textView("本次学习词汇") {
                                id = R.id.tv_learn_text
                                gravity = Gravity.CENTER
                                textSize = 12f
                                textColor = resources.getColor(R.color.color_222831)
                            }
                            space().lparams(wrapContent, dip(10))
                            textView {
                                id = R.id.tv_learn_num
                                textSize = 14f
                                gravity = Gravity.CENTER
                                textColor = resources.getColor(R.color.color_222831)
                            }
                        }.lparams(0, wrapContent, 1f)
                        verticalLayout {
                            id = R.id.vl_learn_total
                            gravity = Gravity.CENTER_HORIZONTAL
                            textView("累计学习词汇") {
                                id = R.id.tv_learn_total_text
                                textSize = 12f
                                gravity = Gravity.CENTER
                                textColor = resources.getColor(R.color.color_222831)
                            }
                            space().lparams(wrapContent, dip(10))
                            textView {
                                id = R.id.tv_learn_total_num
                                textSize = 14f
                                gravity = Gravity.CENTER
                                textColor = resources.getColor(R.color.color_222831)
                            }
                        }.lparams(0, wrapContent, 1f)
                    }.lparams(matchParent, matchParent)

                }.lparams(matchParent, matchParent) {
                    horizontalMargin = dip(40)
                }
            }.lparams(matchParent, matchParent) {
                horizontalMargin = dip(25)
                topMargin = dip(10)
                bottomMargin = dip(85)
            }

        }
    }
}

private class TranslationAdapter(mData: MutableList<Any>) : BaseAdapter<Any>(mData = mData) {

    override fun convert(holder: ViewHolder, item: Any) {
        holder.setText(R.id.tv_fenlei, "n")
                .setText(R.id.tv_translation, "jhahjsd金发科技发表")
    }

    override fun convertView(context: Context,parent: ViewGroup) = with(context) {
        linearLayout {
            lparams(matchParent, wrapContent) {
                topMargin = dip(17)
            }
            textView {
                id = R.id.tv_fenlei
                textSize = 14f
                textColor = resources.getColor(R.color.color_c3c7cb)
            }.lparams(dip(45), wrapContent)
            textView {
                id = R.id.tv_translation
                textSize = 13f
                textColor = resources.getColor(R.color.color_222831)
            }.lparams(matchParent, wrapContent)

        }

    }

}

private class OptionAdapter(data: List<Question>?, val click: (Boolean) -> Unit) : BaseAdapter<Question>() {
    init {
        setData(data)
    }

    override fun convert(holder: ViewHolder, item: Question) {
        holder.setText(R.id.tv_option, item.order ?: "")
                .setText(R.id.tv_option_content, Html.fromHtml(item.content ?: ""))
                .itemView.setOnClickListener {
            val isAnswer = item.isAnswer == "1"
            click(isAnswer)
            if (isAnswer) {
                holder.itemView.setBackgroundResource(R.drawable.bg_shape_34_edf2ff)//edf2ff
                (holder.getView(R.id.tv_option_content) as?TextView)
                        ?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_back_right, 0)
            } else {
                holder.itemView.setBackgroundResource(R.drawable.bg_shape_34_ffe8e8)//ffe8e8
                (holder.getView(R.id.tv_option_content) as?TextView)
                        ?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_back_wrong, 0)
            }
        }
    }

    override fun convertView(context: Context,parent: ViewGroup) = with(context) {
        linearLayout {
            lparams(matchParent, dip(45)) {
                topMargin = dip(15)
            }
            backgroundResource = R.drawable.bg_shape_34_c3c7cb_ffffff
            gravity = Gravity.CENTER_VERTICAL
            space().lparams(dip(30), wrapContent)
            textView() {
                id = R.id.tv_option
                textSize = 14f
                textColor = resources.getColor(R.color.color_222831)
            }
            space().lparams(dip(15), wrapContent)
            textView {
                id = R.id.tv_option_content
                textSize = 15f
                lines = 1
                ellipsize = TextUtils.TruncateAt.END
                compoundDrawablePadding = dip(15)
                rightPadding = dip(15)
                textColor = resources.getColor(R.color.color_222831)
            }.lparams(matchParent, wrapContent)

        }

    }
}