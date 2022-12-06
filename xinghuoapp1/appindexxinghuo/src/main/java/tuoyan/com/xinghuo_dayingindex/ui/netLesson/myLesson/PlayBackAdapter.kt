package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.CatalogBean
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.adapter.LessonItemAdapter
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 */
class PlayBackAdapter(
    var onItemClick: (item: ResourceListBean) -> Unit,
    var onComClick: (ResourceListBean) -> Unit,
    var onWorkClick: (ResourceListBean) -> Unit,
    var onWorkPre: (ResourceListBean) -> Unit
) : BaseAdapter<CatalogBean>(isEmpty = true) {
    private var oldItemPP: CatalogBean? = null
    override fun convert(holder: ViewHolder, item: CatalogBean) {
        val context = holder.itemView.context
        val imgToggleOne = holder.getView(R.id.img_toggle_one) as ImageView
        imgToggleOne.isSelected = false
        val rlvTwo = holder.getView(R.id.rlv_two) as RecyclerView
        rlvTwo.layoutManager = LinearLayoutManager(context)
        rlvTwo.setHasFixedSize(true)
        rlvTwo.isNestedScrollingEnabled = false
        val adapter = PlayBackSecAdapter(onItemClick, onComClick, onWorkClick, onWorkPre)
        rlvTwo.adapter = adapter
        showLastLook(context, adapter, item, holder, imgToggleOne)
        holder.itemView.setOnClickListener {
            imgToggleOne.isSelected = !imgToggleOne.isSelected
            showOrHideLesson(item, adapter, imgToggleOne.isSelected)
        }
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        linearLayout {
            lparams(matchParent, matchParent)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            imageView {
                imageResource = R.drawable.empty_playback
            }.lparams {
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return context.layoutInflater.inflate(R.layout.layout_lesson_one, parent, false)
    }

    //默认展开最后一次观看位置
    private fun showLastLook(
        context: Context,
        adapter: PlayBackSecAdapter,
        item: CatalogBean,
        holder: ViewHolder,
        imgToggleOne: ImageView
    ) {
        val imgLearnedOne = holder.getView(R.id.img_learned_one) as ImageView
        val tvTitle = holder.getView(R.id.tv_title_one) as TextView
        tvTitle.text = item.catalogName
        if ("1" == item.isLastLook) {
            oldItemPP = item
            imgLearnedOne.visibility = View.VISIBLE
            //左边距30dp 右边距 146dp
            tvTitle.maxWidth =
                DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 176f).toInt()
            imgToggleOne.isSelected = true
            showLesson(item, adapter)
        } else {
            //左边距30dp 右边距 68dp
            tvTitle.maxWidth =
                DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 98f).toInt()
            imgLearnedOne.visibility = View.GONE
            hideLesson(adapter)
        }
    }

    //展示和隐藏下一级目录
    private fun showOrHideLesson(
        item: CatalogBean,
        adapter: PlayBackSecAdapter,
        isSelected: Boolean
    ) {
        if (isSelected) {
            showLesson(item, adapter)
        } else {
            hideLesson(adapter)
        }
    }

    //    展示下一级目录
    private fun showLesson(item: CatalogBean, adapter: PlayBackSecAdapter) {
        adapter.setEmpty(true)
        adapter.setData(item.catalogList)
    }

    //    隐藏下一级目录
    private fun hideLesson(adapter: PlayBackSecAdapter) {
        adapter.setEmpty(false)
        var dataList = mutableListOf<CatalogBean>()
        adapter.setData(dataList)
    }

    override fun footerView(context: Context,parent: ViewGroup): View {
        val view = TextView(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            DeviceUtil.dp2px(context, 60f).toInt()
        )
        view.layoutParams = params
        return view
    }
}

class PlayBackSecAdapter(
    var onItemClick: (ResourceListBean) -> Unit,
    var onComClick: (ResourceListBean) -> Unit,
    var onWorkClick: (ResourceListBean) -> Unit,
    var onWorkPre: (ResourceListBean) -> Unit
) : BaseAdapter<CatalogBean>() {
    //    是否为两级，是展示上次学到图片，否则展示定位图片并修改间距
    private var isSecond = false

    private var oldItemP: CatalogBean? = null
    fun isSecond(isSecond: Boolean) {
        this.isSecond = isSecond
    }

    override fun convert(holder: ViewHolder, item: CatalogBean) {
        val context = holder.itemView.context
        val imgToggleTwo = holder.getView(R.id.img_toggle_two) as ImageView
        imgToggleTwo.isSelected = false
        val rlvThree = holder.getView(R.id.rlv_three) as RecyclerView
        rlvThree.layoutManager = LinearLayoutManager(context)
        rlvThree.setHasFixedSize(true)
//        val adapter = PlayBackThreeAdapter({ itemR, pos, oldItem ->
//            onItemClick(itemR, item, pos, oldItem, oldItemP)
//        }, onComClick, onWorkClick, onWorkPre)
//        onClick  String 1:看课；2:预习；3:评论；4：课后作业
        val adapter = LessonItemAdapter { type, item, pos ->
            when (type) {
                "1" -> {
                    onItemClick(item)
                }
                "2" -> {
                    onWorkPre(item)
                }
                "3" -> {
                    onComClick(item)
                }
                "4" -> {
                    onWorkClick(item)
                }
            }
        }
        rlvThree.adapter = adapter
        rlvThree.isNestedScrollingEnabled = false
        showLastLook(context, holder, item, adapter, imgToggleTwo)
        firstItemParams(holder)
        holder.itemView.setOnClickListener {
            imgToggleTwo.isSelected = !imgToggleTwo.isSelected
            showOrHideLesson(imgToggleTwo.isSelected, item, adapter)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return context.layoutInflater.inflate(R.layout.layout_lesson_two, parent, false)
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        linearLayout {
            lparams(matchParent, matchParent)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            imageView {
                imageResource = R.drawable.empty_playback
            }.lparams {
            }
        }
    }

    //设置第一个元素的params
    private fun firstItemParams(holder: ViewHolder) {
        if (holder.layoutPosition == 0) {
            val ctlTwo = holder.getView(R.id.ctl_two) as ConstraintLayout
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 0)
            ctlTwo.layoutParams = params
        }
    }

    //默认展开最后一次观看位置
    private fun showLastLook(
        context: Context,
        holder: ViewHolder,
        item: CatalogBean,
        adapter: LessonItemAdapter,
        imgToggleTwo: ImageView
    ) {
        val imgLearned = holder.getView(R.id.img_learned_two) as ImageView
        val tvTitle = holder.getView(R.id.tv_title) as TextView
        tvTitle.text = item.catalogName
        if ("1" == item.isLastLook) {
            oldItemP = item
            imgLearned.visibility = View.VISIBLE
            if (isSecond) {
                //左边距41.5dp 右边距 146dp
                tvTitle.maxWidth =
                    DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 187.5f).toInt()
                imgLearned.setImageResource(R.mipmap.icon_learned_one)
            } else {
                //左边距41.5dp 右边距 98dp
                tvTitle.maxWidth =
                    DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 139.5f).toInt()
                imgLearned.setImageResource(R.mipmap.icon_learned_two)
            }
            imgToggleTwo.isSelected = true
            showLesson(item, adapter)
        } else {
            //左边距41.5dp 右边距 68dp
            tvTitle.maxWidth =
                DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 109.5f).toInt()
            imgLearned.visibility = View.GONE
        }
    }

    //展示和隐藏下一级目录
    private fun showOrHideLesson(
        isSelected: Boolean,
        item: CatalogBean,
        adapter: LessonItemAdapter
    ) {
        if (isSelected) {
            showLesson(item, adapter)
        } else {
            hideLesson(adapter)
        }
    }

    //显示下一级目录
    private fun showLesson(item: CatalogBean, adapter: LessonItemAdapter) {
        adapter.setEmpty(true)
        adapter.setData(item.resourceList)
    }

    //    隐藏下一级目录
    private fun hideLesson(adapter: LessonItemAdapter) {
        adapter.setEmpty(false)
        val dataList = mutableListOf<ResourceListBean>()
        adapter.setData(dataList)
    }

    override fun footerView(context: Context,parent: ViewGroup): View {
        val view = TextView(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            DeviceUtil.dp2px(context, 60f).toInt()
        )
        view.layoutParams = params
        return view
    }
}

/**
 * onItemClick item点击事件
 * onComClick 评论点击事件
 *onWorkClick 课后作业点击事件
 * onWorkPre 预习点击事件
 */
//class PlayBackThreeAdapter(
//    var onItemClick: (item: ResourceListBean, Int, oldItem: ResourceListBean?) -> Unit,
//    var onComClick: (ResourceListBean) -> Unit,
//    var onWorkClick: (id: String) -> Unit,
//    var onWorkPre: (ResourceListBean) -> Unit
//) :
//    BaseAdapter<ResourceListBean>() {
//    //排序类型默认0；1：按时间排序 主要影响定位图标（img_learned_three）和tv_title的最大宽度
//    private var sortType = "0"
//    private var oldItem: ResourceListBean? = null
//    override fun convert(holder: ViewHolder, item: ResourceListBean) {
//        if (holder.adapterPosition == 0) {
//            holder.setVisible(R.id.v_line, View.GONE)
//        } else {
//            holder.setVisible(R.id.v_line, View.VISIBLE)
//        }
//        //TODO 0 录播 1 直播 3 音频课
//        holder.setText(R.id.tv_num, item.sort + ".")
//            .setText(R.id.tv_title, item.name)
//            .setText(
//                R.id.tv_time,
//                if (item.liveType == "0") "录播课" else if (item.liveType == "4") "音频课" else item.liveTime?.replace(
//                    "-",
//                    "."
//                )?.replace(" ", "    ")
//            )
//            .setText(R.id.tv_teacher_name, item.teacher)
//            .setText(R.id.tv_comment, "评论 " + getCommentCount(item))
//            .setOnClickListener(R.id.tv_comment) {
//                onComClick(item)
//            }
//            .setVisible(R.id.tv_work, if (item.workState != "0") View.VISIBLE else View.GONE)
//            .setText(R.id.tv_work, if (item.workState == "2") "完成" else "课后作业")
//            .setOnClickListener(R.id.tv_work) {
//                if (item.workState == "1") onWorkClick(item.id)
//            }.setVisible(R.id.tv_comment, if ("3" == item.liveType) View.GONE else View.VISIBLE)
//            .setVisible(R.id.tv_time, if ("3" == item.liveType) View.INVISIBLE else View.VISIBLE)
//            .setVisible(R.id.tv_work_pre, if ("0" == item.prepareState) View.GONE else View.VISIBLE)
//            .setText(R.id.tv_work_pre, if ("1" == item.prepareState) "预习" else if ("2" == item.prepareState) "继续预习" else "已预习")
//            .setOnClickListener(R.id.tv_work_pre) {
//                onWorkPre(item)
//            }
//        val tvTime = holder.getView(R.id.tv_time) as TextView
//        val tvTeacherName = holder.getView(R.id.tv_teacher_name) as TextView
//        if (item.liveType == "0") {
//            tvTeacherName.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_teacher, 0, 0, 0)
//            tvTime.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_play_back, 0, 0, 0)
//        } else if (item.liveType == "4") {
//            tvTeacherName.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_teacher, 0, 0, 0)
//            tvTime.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_audio, 0, 0, 0)
//        } else if (item.liveType == "3") {
//            tvTeacherName.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_exercise_test, 0, 0, 0)
//        } else {
//            tvTeacherName.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_teacher, 0, 0, 0)
//            tvTime.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_time, 0, 0, 0)
//        }
//        holder.itemView.setOnClickListener {
//            onItemClick(item, holder.adapterPosition, oldItem)
//        }
//        setParams(holder, item)
//        //如果当前类型为测评tv_teacher_name.text = "今日披星戴月，明日成就梦想！" drawableleft="@mipmap/icon_exercise_test" tv_time 隐藏
//    }
//
//    fun setParams(holder: ViewHolder, item: ResourceListBean) {
//        val context = holder.itemView.context
//        val imgLearned = holder.getView(R.id.img_learned_three) as ImageView
//        val tvTitle = holder.getView(R.id.tv_title) as TextView
//        val ctlThree = holder.getView(R.id.ctl_three) as ConstraintLayout
//        val vLine = holder.getView(R.id.v_line)
//        val params = ctlThree.layoutParams as RecyclerView.LayoutParams
//        val paramsLine = vLine.layoutParams as ConstraintLayout.LayoutParams
//        if ("1" == item.isLastLook) {
//            if ("1" == sortType) {
//                imgLearned.setImageResource(R.mipmap.icon_learned_one)
//                //左边距55dp 右边距 103dp
//                tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 183f).toInt()
//                params.setMargins(DeviceUtil.dp2px(context, 5f).toInt(), 0, DeviceUtil.dp2px(context, 14f).toInt(), 0)
//                paramsLine.setMargins(DeviceUtil.dp2px(context, 5f).toInt(), 0, 0, 0)
//            } else {
//                params.setMargins(0, 0, DeviceUtil.dp2px(context, 9f).toInt(), 0)
//                imgLearned.setImageResource(R.mipmap.icon_learned_two)
//                //左边距55dp 右边距 50dp
//                tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 155f).toInt()
//                paramsLine.setMargins(0, 0, 0, 0)
//            }
//            imgLearned.visibility = View.VISIBLE
//        } else {
//            //左边距65dp 右边距 60dp
//            if ("1" == sortType) {
//                params.setMargins(DeviceUtil.dp2px(context, 5f).toInt(), 0, DeviceUtil.dp2px(context, 14f).toInt(), 0)
//                tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 100f).toInt()
//                paramsLine.setMargins(DeviceUtil.dp2px(context, 5f).toInt(), 0, 0, 0)
//            } else {
//                tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 125f).toInt()
//                params.setMargins(0, 0, DeviceUtil.dp2px(context, 9f).toInt(), 0)
//                paramsLine.setMargins(0, 0, 0, 0)
//            }
//            imgLearned.visibility = View.GONE
//        }
//        ctlThree.layoutParams = params
//        vLine.layoutParams = paramsLine
//        //tv_comment tv_work都是隐藏设置tvWorkPre的layout_marginRight=0 else layout_marginRight=10
//        val tvWorkPre = holder.getView(R.id.tv_work_pre) as TextView
//        val tvComment = holder.getView(R.id.tv_comment) as TextView
//        val tvWork = holder.getView(R.id.tv_work) as TextView
//        val tvWorkPreParams = tvWorkPre.layoutParams as ConstraintLayout.LayoutParams
//        if (tvComment.visibility == View.GONE && tvWork.visibility == View.GONE) {
//            tvWorkPreParams.setMargins(0, DeviceUtil.dp2px(context, 11f).toInt(), 0, DeviceUtil.dp2px(context, 11f).toInt())
//        } else {
//            tvWorkPreParams.setMargins(0, DeviceUtil.dp2px(context, 11f).toInt(), DeviceUtil.dp2px(context, 10f).toInt(), DeviceUtil.dp2px(context, 11f).toInt())
//        }
//        tvWorkPre.layoutParams = tvWorkPreParams
//    }
//
//    override fun convertView(context: Context, parent: ViewGroup): View {
//        return context.layoutInflater.inflate(R.layout.layout_lesson_three, parent, false)
//    }
//
//    override fun emptyView(context: Context,parent: ViewGroup): View = with(context) {
//        linearLayout {
//            lparams(matchParent, matchParent)
//            orientation = LinearLayout.VERTICAL
//            gravity = Gravity.CENTER_HORIZONTAL
//            imageView {
//                imageResource = R.drawable.empty_playback
//            }.lparams {
//            }
//        }
//    }
//
//    private fun getCommentCount(item: ResourceListBean): String {
//        return try {
//            var count = item.pointsCount.toInt()
//            if (count > 99) {
//                "99+"
//            } else {
//                item.pointsCount
//            }
//        } catch (e: Exception) {
//            item.pointsCount
//        }
//    }
//
//    //设置排序类型，主要影响定位图标（img_learned_three）和tv_title的最大宽度
//    //展示不同的点位图标
//    fun setSortType(type: String) {
//        this.sortType = type
//    }
//
//    override fun footerView(context: Context): View {
//        val view = TextView(context)
//        val params = ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            DeviceUtil.dp2px(context, 60f).toInt()
//        )
//        view.layoutParams = params
//        return view
//    }
//}
