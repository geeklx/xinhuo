package tuoyan.com.xinghuo_dayingindex.ui.netLesson.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * onClick  String 1:看课；2:预习；3:评论；4：课后作业
 */
class LessonItemAdapter(val onClick: (String, ResourceListBean, Int) -> Unit) : BaseAdapter<ResourceListBean>() {

    //排序类型默认0；1：按时间排序 主要影响定位图标（img_location）和tv_title的最大宽度
    private var sortType = "0"

    override fun convert(holder: ViewHolder, item: ResourceListBean) {
        val context = holder.itemView.context
        //是否必修
        val title = if ("1" == item.sectionProperties) "\u3000\u3000\u0020\u0020${item.name}" else item.name
        val tvTitle = holder.getView(R.id.tv_title) as TextView
        tvTitle.text = title
        holder.setText(R.id.tv_sort, item.sort).setText(R.id.tv_teacher, item.teacher)
            .setVisible(R.id.tv_tag, if ("1" == item.sectionProperties) View.VISIBLE else View.GONE)
            .setText(R.id.tv_time, item.liveTime)
            //直播列表 （直播中,今日开课,未开课）只有预习，回放列表必有上课和评论
            .setVisible(R.id.ctl_pre, if ("0" != item.prepareState && isLiving(item)) View.VISIBLE else View.GONE)
            .setVisible(R.id.ctl_bottom, if (isLiving(item)) View.GONE else View.VISIBLE)
//            .setText(R.id.tv_pre_t, if ("0" == item.prepareState && "2" == item.liveState) "上课" else "预习")
            .setVisible(R.id.img_end, if ("true" == item.allFinished) View.VISIBLE else View.GONE)
            //预习状态
            .setText(R.id.tv_pre_state, getPreState(item.prepareState)).setTextColor(R.id.tv_pre_state, setPreStateColor(context, item.prepareState))
            .setVisible(R.id.ctl_living_state, if ("4" == item.liveState) View.GONE else View.VISIBLE)//可回放
            .setText(R.id.tv_living_state, getLivingState(item.liveState)).setBackgroundResource(R.id.ctl_living_state, setLivingStateBackground(item.liveState))
            .setVisible(R.id.lav_living, if ("1" == item.liveState) View.VISIBLE else View.GONE).setOnClickListener(R.id.ctl_pre) {
                if ("0" != item.prepareState) {
                    onClick("2", item, holder.adapterPosition)
                }
            }.setOnClickListener(R.id.ctl_common) {
                onClick("3", item, holder.adapterPosition)
            }.setOnClickListener(R.id.ctl_b_pre) {
                onClick("2", item, holder.adapterPosition)
            }.setOnClickListener(R.id.ctl_work) {
                onClick("4", item, holder.adapterPosition)
            }.itemView.setOnClickListener {
                if ("1" == item.liveState || "4" == item.liveState) {
                    onClick("1", item, holder.adapterPosition)
                }
            }
        showCommon(holder, item, !isLiving(item))
        showPre(holder, item, "0" != item.prepareState)
        showWork(holder, item, "0" != item.workState && !isLiving(item))
        showClass(holder, item, !isLiving(item))
        setParams(holder, item)
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_lesson_living_item, parent, false)
    }

    override fun emptyImageRes(): Int {
        return R.drawable.empty_living
    }

    override fun footerView(context: Context,parent: ViewGroup): View {
        val view = TextView(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }

    /**
     * 直播列表中
     */
    private fun isLiving(item: ResourceListBean): Boolean {
        return "0" == item.liveState || "1" == item.liveState || "99" == item.liveState
    }

    private fun getPreState(state: String): String {
        return when (state) {
            "1" -> "未学习"
            "2" -> "继续学习"
            "3" -> "已完成"
            else -> "未学习"
        }
    }

    private fun getWorkState(state: String): String {
        return when (state) {
            "1" -> "未学习"
            "2" -> "已完成"
            "3" -> "继续学习"
            else -> "未学习"
        }
    }

    /**
     * 0未开始 1上课中 2已完成
     */
    private fun getClassState(state: String): String {
        return when (state) {
            "0" -> "未学习"
            "1" -> "继续学习"
            "2" -> "已完成"
            else -> "未学习"
        }
    }

    private fun setPreStateColor(context: Context, state: String): Int {
        return when (state) {
            "1" -> ContextCompat.getColor(context, R.color.color_c4cbde)
            "2" -> ContextCompat.getColor(context, R.color.color_ff7800)
            "3" -> ContextCompat.getColor(context, R.color.color_15d25f)
            else -> ContextCompat.getColor(context, R.color.color_c4cbde)
        }
    }

    private fun setWorkStateColor(context: Context, state: String): Int {
        return when (state) {
            "1" -> ContextCompat.getColor(context, R.color.color_c4cbde)
            "2" -> ContextCompat.getColor(context, R.color.color_15d25f)
            "3" -> ContextCompat.getColor(context, R.color.color_ff7800)
            else -> ContextCompat.getColor(context, R.color.color_c4cbde)
        }
    }

    private fun setClassStateColor(context: Context, state: String): Int {
        return when (state) {
            "0" -> ContextCompat.getColor(context, R.color.color_c4cbde)
            "1" -> ContextCompat.getColor(context, R.color.color_ff7800)
            "2" -> ContextCompat.getColor(context, R.color.color_15d25f)
            else -> ContextCompat.getColor(context, R.color.color_c4cbde)
        }
    }

    private fun getLivingState(state: String): String {
        return when (state) {
            "0", "5" -> "未开课"
            "2" -> "回放生成中"
            "1" -> "直播中"
            "99" -> "今日开课"
            else -> "未开课"
        }
    }

    private fun setLivingStateBackground(state: String): Int {
        return when (state) {
            "0", "5" -> R.drawable.shape_8_c4cbde
            "2" -> R.drawable.shape_8_ff7800
            "1", "99" -> R.drawable.shape_8_5467ff
            else -> R.drawable.shape_8_c4cbde
        }
    }

    private fun showCommon(holder: ViewHolder, item: ResourceListBean, isShow: Boolean) {
        holder.setVisible(R.id.ctl_common, if (isShow) View.VISIBLE else View.GONE).setVisible(R.id.v1, if (isShow) View.VISIBLE else View.GONE)
            .setText(R.id.tv_1_state, getCommentCount(item))
    }

    private fun showPre(holder: ViewHolder, item: ResourceListBean, isShow: Boolean) {
        val context = holder.itemView.context
        holder.setVisible(R.id.ctl_b_pre, if (isShow) View.VISIBLE else View.GONE).setVisible(R.id.v2, if (isShow) View.VISIBLE else View.GONE)
            .setText(R.id.tv_2_state, getPreState(item.prepareState))
            .setTextColor(R.id.tv_2_state, setPreStateColor(context, item.prepareState))
    }

    private fun showWork(holder: ViewHolder, item: ResourceListBean, isShow: Boolean) {
        val context = holder.itemView.context
        holder.setVisible(R.id.ctl_work, if (isShow) View.VISIBLE else View.GONE).setVisible(R.id.v3, if (isShow) View.VISIBLE else View.GONE)
            .setText(R.id.tv_3_state, getWorkState(item.workState))
            .setTextColor(R.id.tv_3_state, setWorkStateColor(context, item.workState))
    }

    private fun showClass(holder: ViewHolder, item: ResourceListBean, isShow: Boolean) {
        val context = holder.itemView.context
        holder.setVisible(R.id.tv_4, if (isShow) View.VISIBLE else View.GONE).setVisible(R.id.tv_4_state, if (isShow) View.VISIBLE else View.GONE)
            .setText(R.id.tv_4_state, getClassState(item.completeState)).setTextColor(R.id.tv_4_state, setClassStateColor(context, item.completeState))
    }

    override fun getItemViewType(position: Int): Int {
        if (getIsHeader() && position == 0) return super.getItemViewType(position)
        val pos = if (getIsHeader()) position - 1 else position
        return if (pos < getData().size) {
            when (getData()[pos].liveType) {
                "0" -> {
                    TYPE_PLAYBACK
                }
                "3" -> {
                    TYPE_EXERCISE
                }
                "4" -> {
                    TYPE_AUDIO
                }
                else -> {
                    super.getItemViewType(position)
                }
            }
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_PLAYBACK, TYPE_AUDIO -> {
                playBackConvert(holder, getData()[position - if (getIsHeader()) 1 else 0])
            }
            TYPE_EXERCISE -> {
                exerciseConvert(holder, getData()[position - if (getIsHeader()) 1 else 0])
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }
    }

    private fun playBackConvert(holder: ViewHolder, item: ResourceListBean) {
        //是否必修
        val title = if ("1" == item.sectionProperties) "\u3000\u3000\u0020\u0020${item.name}" else item.name
        val tvTitle = holder.getView(R.id.tv_title) as TextView
        tvTitle.text = title
        holder.setText(R.id.tv_sort, item.sort).setText(R.id.tv_teacher, item.teacher)
            .setVisible(R.id.tv_tag, if ("1" == item.sectionProperties) View.VISIBLE else View.GONE)
            .setText(R.id.tv_time, if (TYPE_AUDIO == holder.itemViewType) "音频课" else "录播课").setVisible(R.id.ctl_pre, View.GONE).setVisible(R.id.ctl_bottom, View.VISIBLE)
            .setVisible(R.id.ctl_living_state,if (item.liveType=="0"&&TextUtils.isEmpty(item.downUrl))View.VISIBLE else View.GONE).setVisible(R.id.img_end, if ("true" == item.allFinished) View.VISIBLE else View.GONE)
            .setVisible(R.id.lav_living,View.GONE).setText(R.id.tv_living_state,if (item.liveType=="0"&&TextUtils.isEmpty(item.downUrl))"待更新" else "")
            .setBackgroundResource(R.id.ctl_living_state, R.drawable.shape_8_ff7800)
            .setOnClickListener(R.id.ctl_common) {
                onClick("3", item, holder.adapterPosition)
            }.setOnClickListener(R.id.ctl_b_pre) {
                onClick("2", item, holder.adapterPosition)
            }.setOnClickListener(R.id.ctl_work) {
                onClick("4", item, holder.adapterPosition)
            }.itemView.setOnClickListener {
                onClick("1", item, holder.adapterPosition)
            }
        showCommon(holder, item, true)
        showPre(holder, item, "0" != item.prepareState)
        showWork(holder, item, "0" != item.workState)
        showClass(holder, item, true)
        setParams(holder, item)
    }

    private fun exerciseConvert(holder: ViewHolder, item: ResourceListBean) {
//是否必修
        val title = if ("1" == item.sectionProperties) "\u3000\u3000\u0020\u0020${item.name}" else item.name
        val tvTitle = holder.getView(R.id.tv_title) as TextView
        tvTitle.text = title
        holder.setText(R.id.tv_sort, item.sort).setText(R.id.tv_teacher, "今日披星戴月，明日成就梦想！")
            .setVisible(R.id.tv_tag, if ("1" == item.sectionProperties) View.VISIBLE else View.GONE)
            .setVisible(R.id.tv_time, View.GONE).setVisible(R.id.ctl_pre, View.GONE).setVisible(R.id.ctl_bottom, View.GONE)
            .setVisible(R.id.img_end, if ("true" == item.allFinished) View.VISIBLE else View.GONE)
            .setVisible(R.id.ctl_living_state, View.GONE).itemView.setOnClickListener {
                onClick("1", item, holder.adapterPosition)
            }
        setParams(holder, item)
    }

    private fun getCommentCount(item: ResourceListBean): String {
        return try {
            val count = item.pointsCount.toInt()
            if (count > 99) {
                "99+"
            } else {
                item.pointsCount
            }
        } catch (e: Exception) {
            "0"
        }
    }

    //设置排序类型，主要影响定位图标（img_location）和tv_title的最大宽度
    //展示不同的点位图标
    fun setSortType(type: String) {
        this.sortType = type
    }

    private fun setParams(holder: ViewHolder, item: ResourceListBean) {
        val context = holder.itemView.context
        val imgLearned = holder.getView(R.id.img_location) as ImageView
        val tvTitle = holder.getView(R.id.tv_title) as TextView
        val tvParams = tvTitle.layoutParams as ConstraintLayout.LayoutParams
        val ctlBottom = holder.getView(R.id.ctl_bottom) as ConstraintLayout
        val params = ctlBottom.layoutParams as ConstraintLayout.LayoutParams
        if ("1" == item.isLastLook && "1" == sortType) {
            imgLearned.setImageResource(R.mipmap.icon_learned_one)
            tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 153f).toInt()
            params.setMargins(0, DeviceUtil.dp2px(context, 10f).toInt(), DeviceUtil.dp2px(context, 30f).toInt(), 0)
            tvParams.setMargins(DeviceUtil.dp2px(context, 50f).toInt(), 0, 0, 0)
            imgLearned.visibility = View.VISIBLE
            ctlBottom.setBackgroundResource(R.drawable.shape_12_f9fafc)
        } else if ("1" == item.isLastLook) {
            imgLearned.setImageResource(R.mipmap.icon_learned_two)
            tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 145f).toInt()
            params.setMargins(0, DeviceUtil.dp2px(context, 10f).toInt(), DeviceUtil.dp2px(context, 20f).toInt(), 0)
            tvParams.setMargins(DeviceUtil.dp2px(context, 45f).toInt(), 0, 0, 0)
            imgLearned.visibility = View.VISIBLE
            ctlBottom.setBackgroundResource(R.drawable.shape_12_ff)
        } else if ("1" == sortType) {
            tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 115f).toInt()
            params.setMargins(0, DeviceUtil.dp2px(context, 10f).toInt(), DeviceUtil.dp2px(context, 30f).toInt(), 0)
            tvParams.setMargins(DeviceUtil.dp2px(context, 50f).toInt(), 0, 0, 0)
            imgLearned.visibility = View.GONE
            ctlBottom.setBackgroundResource(R.drawable.shape_12_f9fafc)
        } else {
            tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 125f).toInt()
            params.setMargins(0, DeviceUtil.dp2px(context, 10f).toInt(), DeviceUtil.dp2px(context, 20f).toInt(), 0)
            tvParams.setMargins(DeviceUtil.dp2px(context, 45f).toInt(), 0, 0, 0)
            imgLearned.visibility = View.GONE
            ctlBottom.setBackgroundResource(R.drawable.shape_12_ff)
        }
        setImgEndParams(holder)
    }

    private fun setImgEndParams(holder: ViewHolder) {
        val img_end = holder.getView(R.id.img_end) as ImageView
        if (View.VISIBLE == img_end.visibility) {
            val context = holder.itemView.context
            val imgEndParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            if (TYPE_EXERCISE == holder.itemViewType) {
                imgEndParams.bottomToBottom = R.id.tv_teacher
            } else {
                imgEndParams.bottomToBottom = R.id.tv_time
            }
            imgEndParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            if ("1" == sortType) {
                imgEndParams.setMargins(0, 0, DeviceUtil.dp2px(context, 40f).toInt(), 0)
            } else {
                imgEndParams.setMargins(0, 0, DeviceUtil.dp2px(context, 30f).toInt(), 0)
            }
            img_end.layoutParams = imgEndParams
        }
    }

    override fun headerView(context: Context,parent: ViewGroup): View {
        val view = TextView(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 5f).toInt())
        view.layoutParams = params
        return view
    }

    companion object {
        const val TYPE_PLAYBACK = 2000
        const val TYPE_AUDIO = 2001
        const val TYPE_EXERCISE = 2002
    }
}
