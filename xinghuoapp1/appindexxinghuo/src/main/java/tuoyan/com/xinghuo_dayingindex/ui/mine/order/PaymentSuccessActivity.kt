package tuoyan.com.xinghuo_dayingindex.ui.mine.order
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_payment_success.*
import kotlinx.android.synthetic.main.layout_add_us.view.*
import kotlinx.android.synthetic.main.layout_pay_success.view.*
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WEB_BASE_URL
import tuoyan.com.xinghuo_dayingindex.WX_PROGRAM_ID_SPARKE
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.AssembleOrderBean
import tuoyan.com.xinghuo_dayingindex.bean.AssembleTeam
import tuoyan.com.xinghuo_dayingindex.ui.MainActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ShareDialog
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail.LessonDetailActivity
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.MineLessonActivity
import tuoyan.com.xinghuo_dayingindex.utlis.ShareUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini
import kotlin.math.ceil
import kotlin.math.floor

/**
 * 支付成功页面PaymentSuccessActivity
 * isSetResult：做题页面主观题支付成功跳转页面startActivityForResult 返回影响主观题的状态
 *其他支付成功startActivity：加群，拼团，文本展示等
 */
class PaymentSuccessActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int = R.layout.activity_payment_success

    private val key by lazy { intent.getStringExtra(KEY) ?: "" }
    private val isSetResult by lazy { intent.getBooleanExtra(IS_SET_RESULT, false) }
    private val assembleKey by lazy { intent.getStringExtra(ASSEMBLE_KEY) ?: "" }
    private val lessonKey by lazy { intent.getStringExtra(LESSON_KEY) ?: "" }
    private val cTitle by lazy { intent.getStringExtra(TITLE) ?: "星火英语" }
    private val data by lazy { intent.getParcelableExtra(DATA) as? AssembleTeam }
    private var qrCode = ""//二维码图片
    private var cTimer: CountDownTimer? = null
    private lateinit var assembleTeam: AssembleTeam
    val adapter by lazy {
        Adapter()
    }

    override fun initData() {
        super.initData()
        getData()
    }

    /**
     * 设置加群View
     */
    private fun initAddUs(assembleTeam: AssembleTeam) {
        //设置加群
        layout_one.visibility = View.GONE
        layout_two.visibility = View.VISIBLE
        layout_two.tv_content.text = assembleTeam.remark
        Glide.with(this@PaymentSuccessActivity).load(assembleTeam.qrCode)
            .into(layout_two.img_content)
        qrCode = assembleTeam.qrCode
    }

    /**
     * 设置正常View
     */
    private fun initNormal(assembleTeam: AssembleTeam) {
        //判断拼团或者平常网课
        layout_one.visibility = View.VISIBLE
        layout_two.visibility = View.GONE
        if (assembleKey.isEmpty()) {
            //平常网课展示成功
            layout_one.btn_home.visibility = View.VISIBLE
            layout_one.ll_assemble.visibility = View.GONE
            if (isSetResult) {
                layout_one.btn_home.text = "返回"
            }
        } else {
            //拼团网课展示拼团情况，拼团人员和倒计时，邀请参与
            layout_one.btn_home.visibility = View.GONE
            layout_one.ll_assemble.visibility = View.VISIBLE
            val manager = LinearLayoutManager(this)
            manager.orientation = RecyclerView.HORIZONTAL
            layout_one.rlv_student.layoutManager = manager
            layout_one.rlv_student.adapter = adapter
            adapter.setData(initAssembleStu(assembleTeam))
            showState(assembleTeam)
        }
    }

    /**
     *规范拼团的人员
     */
    private fun initAssembleStu(assembleTeam: AssembleTeam): ArrayList<AssembleOrderBean> {
        val dataList = ArrayList<AssembleOrderBean>()
        if (assembleTeam.assembleLimitNum >= assembleTeam.assembleOrderList?.size ?: 0) {
            /**
             * 拼团人数少于限制人数，剩余位置展示添加人员的符号，倒计时结束，后台默认拼团成功，不论多少人数
             * 如果拼团成功，人数少于限制人数，添加虚拟人员，userKey是否为空，为空展示加号，否则展示人像
             */
            dataList.addAll(assembleTeam.assembleOrderList!!)
            val sum =
                assembleTeam.assembleLimitNum - (assembleTeam.assembleOrderList?.size ?: 0)
            for (index in 0 until sum) {
                val item = AssembleOrderBean()
                if (assembleTeam.assembleState == "1") {
                    item.userKey = "111111"
                }
                dataList.add(item)
            }
        } else {
            /**
             * 人员加入数量大于展示数量
             * 默认第一个为团长，如果我不是团长，第二个位置添加我，然后依次添加剩余人员（非我）
             */
            for (index in 0 until assembleTeam.assembleOrderList!!.size) {
                if (index == 0) {
                    dataList.add(assembleTeam.assembleOrderList!![index])
                    if (assembleTeam.assembleOrderList!![index].userKey != SpUtil.user.userId) {
                        val data = AssembleOrderBean()
                        data.img = SpUtil.userInfo.img ?: ""
                        data.userKey = SpUtil.user.userId ?: ""
                        dataList.add(data)
                    }
                } else {
                    if (assembleTeam.assembleOrderList!![index].userKey != SpUtil.user.userId && dataList.size < assembleTeam.assembleLimitNum) {
                        dataList.add(assembleTeam.assembleOrderList!![index])
                    }
                }
            }
        }
        return dataList
    }

    /**
     * 根据拼团的不同状态展示文本
     * assembleState   0拼团中  1拼团成功 2拼团失败
     */
    private fun showState(assembleTeam: AssembleTeam) {
        when (assembleTeam.assembleState) {
            //assembleState   0拼团中  1拼团成功 2拼团失败
            "0" -> {
                layout_one.tv_stu.text =
                    Html.fromHtml("还差<font color='#ff5d32'>${assembleTeam.assembleLimitNum - assembleTeam.assembleOrderList!!.size}</font>人拼团成功")
                layout_one.tv_down_time.text =
                    Html.fromHtml("距离结束 <font color='#ff5d32'>" + "10天1小时8分22秒" + "</font>")
                layout_one.tv_ok.text = "立即邀请好友参团"
                downTimer(assembleTeam.assembleTeamEndTime - System.currentTimeMillis())
            }
            "1" -> {
                if (cTimer != null) {
                    cTimer?.cancel()
                    cTimer = null
                }
                layout_one.tv_stu.text = "拼团成功"
                layout_one.tv_down_time.text = "您已经拼团成功"
                layout_one.tv_ok.text = "去上课"
            }
            else -> {
                if (cTimer != null) {
                    cTimer?.cancel()
                    cTimer = null
                }
                layout_one.tv_stu.text = "拼团失败"
                layout_one.tv_down_time.text = "您已经拼团失败"
                layout_one.tv_ok.text = "重新开团"
            }
        }
    }

    fun getData() {
        if (key.isNotEmpty()) {
            //  一键加群
            presenter.goodsInfoAfterPay(key) {
                if ("1" == it.isAdditiveGroup) {
                    //设置加群
                    initAddUs(it)
                } else {
                    if (assembleKey.isNotEmpty()) {
                        //  拼团
                        presenter.getAssembleTeam(key) {
                            assembleTeam = it
                            initNormal(it)
                        }
                    }
                }
            }
        } else if ("1" == data?.isAdditiveGroup) {
            initAddUs(data!!)
        } else {
            initNormal(AssembleTeam())
        }
    }

    /**
     * 拼团倒计时
     */
    private fun downTimer(time: Long) {
        cTimer?.cancel()
        cTimer = null
        cTimer = object : CountDownTimer(time, 1000) {
            override fun onFinish() {
                getData()
            }

            override fun onTick(millisUntilFinished: Long) {
                layout_one.tv_down_time.text =
                    Html.fromHtml("距离结束 <font color='#ff5d32'>" + formatTime(millisUntilFinished) + "</font>")
            }
        }.start()
    }

    /**
     * 时间格式化；格式为 **天**:**:**
     */
    private fun formatTime(time: Long): String {
        val allTime = ceil((time / 1000).toDouble())
        val day = floor(allTime / (60 * 60 * 24)).toInt()
        val h = floor((allTime - day * (60 * 60 * 24)) / (60 * 60)).toInt()
        val m = floor((allTime - day * (60 * 60 * 24) - h * (60 * 60)) / 60).toInt()
        val s = ((allTime - day * (60 * 60 * 24) - h * (60 * 60)) % 60).toInt()
        return "${toDouble(day)}天 ${toDouble(h)}:${toDouble(m)}:${toDouble(s)}"
    }

    /**
     * 数字格式化；两位数字
     */
    private fun toDouble(time: Int): String {
        return if (time < 10) {
            "0${time}"
        } else {
            "$time"
        }
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        layout_one.btn_home.setOnClickListener {
            onBackPressed()
        }
        layout_two.tv_back.setOnClickListener {
            onBackPressed()
        }
        layout_two.tv_wx.setOnClickListener {
            WxMini.goWxMini(this, WX_PROGRAM_ID_SPARKE, qrCode)
        }
        //立即邀请好友参团
        layout_one.tv_ok.setOnClickListener {
            isLogin {
                //assembleState   0拼团中  1拼团成功 2拼团失败
                if (assembleTeam.assembleState == "0") {
                    ShareDialog(this@PaymentSuccessActivity) {
                        ShareUtils.share(
                            this, it, cTitle, "",
                            WEB_BASE_URL + "groupbuy?groupId=${assembleTeam.assembleTeamKey}&assembleKey=${assembleKey}"
                        )
                    }.show()
                } else if (assembleTeam.assembleState == "1") {
                    onBackPressed()
                    if (lessonKey.isNotEmpty()) {
                        startActivity<MineLessonActivity>(MineLessonActivity.KEY to lessonKey)
                    }
                } else {
                    onBackPressed()
                    if (lessonKey.isNotEmpty()) {
                        startActivity<LessonDetailActivity>(LessonDetailActivity.KEY to lessonKey)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (isSetResult) {
            setResult(Activity.RESULT_OK)
        } else {
            startActivity<MainActivity>()
        }
        super.onBackPressed()
    }

    companion object {
        const val KEY = "key"
        const val IS_SET_RESULT = "isSetResult"
        const val POSITION = "position"
        const val ASSEMBLE_KEY = "ASSEMBLEKEY"
        const val LESSON_KEY = "lessonKey"
        const val TITLE = "title"
        const val DATA = "assembleTeam"
    }

    class Adapter : BaseAdapter<AssembleOrderBean>() {
        override fun convert(holder: ViewHolder, item: AssembleOrderBean) {
            if (item.userKey.isNotEmpty()) {
                Glide.with(holder.itemView.context).load(item.img)
                    .placeholder(R.mipmap.ic_avatar)
                    .error(R.mipmap.ic_avatar)
                    .into(holder.getView(R.id.img_cover) as ImageView)
            } else {
                holder.setBackgroundResource(R.id.img_cover, R.mipmap.icon_assemble_add)
            }
            val tvTag = holder.getView(R.id.tv_tag) as TextView
            if (holder.adapterPosition != 0) {
                if (item.userKey == SpUtil.user.userId) {
                    tvTag.background = ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.bg_shape_8_ffbd22
                    )
                    tvTag.text = "我"
                    tvTag.visibility = View.VISIBLE
                } else {
                    tvTag.visibility = View.INVISIBLE
                }
            } else {
                tvTag.background =
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.bg_shape_8_5467ff
                    )
                tvTag.text = "团长"
                tvTag.visibility = View.VISIBLE
            }
        }

        override fun convertView(context: Context, parent: ViewGroup): View {
            return LayoutInflater.from(context)
                .inflate(R.layout.layout_assemble_student, parent, false)
        }
    }
}