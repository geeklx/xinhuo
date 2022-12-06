package tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail

import android.content.Context
import android.graphics.Color
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
import tuoyan.com.xinghuo_dayingindex.bean.Catalog
import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 */
class CatalogAdapter(var onFree: (ResourceListBean) -> Unit) : BaseAdapter<Catalog>(isEmpty = true) {
    override fun convert(holder: ViewHolder, item: Catalog) {
        val context = holder.itemView.context
        val imgToggleOne = holder.getView(R.id.img_toggle_one) as ImageView
        imgToggleOne.isSelected = false
        val rlvTwo = holder.getView(R.id.rlv_two) as RecyclerView
        rlvTwo.layoutManager = LinearLayoutManager(holder.itemView.context)
        rlvTwo.setHasFixedSize(true)
        val adapter = CatalogSecAdapter(onFree)
        adapter.isSel(true)
        rlvTwo.adapter = adapter
        rlvTwo.isNestedScrollingEnabled = false

        holder.itemView.setOnClickListener {
            imgToggleOne.isSelected = !imgToggleOne.isSelected
            if (imgToggleOne.isSelected) {
                adapter.setEmpty(true)
                adapter.setData(item.catalogList)
            } else {
                adapter.setEmpty(false)
                val dataList = mutableListOf<Catalog>()
                adapter.setData(dataList)
            }
        }
        val tvTitle = holder.getView(R.id.tv_title_one) as TextView
        tvTitle.text = item.catalogName
        tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 98f).toInt()
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        linearLayout {
            lparams(matchParent, matchParent)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            imageView {
                imageResource = R.drawable.ic_no_content
            }.lparams {
                topMargin = dip(20)
            }

            textView {
                text = "暂未配置课程"
                textSize = 15f
                textColor = Color.parseColor("#666666")
            }.lparams {
                topMargin = dip(10)
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return context.layoutInflater.inflate(R.layout.layout_lesson_one, parent, false)
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = TextView(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt()
        )
        view.layoutParams = params
        return view
    }
}

class CatalogSecAdapter(var onFree: (ResourceListBean) -> Unit) : BaseAdapter<Catalog>() {
    //    用于三级目录时去除第一个的margintop
    private var isSel = false
    fun isSel(isSel: Boolean) {
        this.isSel = isSel
    }

    override fun convert(holder: ViewHolder, item: Catalog) {
        val context = holder.itemView.context
        val imgToggleTwo = holder.getView(R.id.img_toggle_two) as ImageView
        imgToggleTwo.isSelected = false
        val rlvThree = holder.getView(R.id.rlv_three) as RecyclerView
        rlvThree.layoutManager = LinearLayoutManager(holder.itemView.context)
        rlvThree.setHasFixedSize(true)
        val adapter = CatalogThreeAdapter(onFree)
        rlvThree.adapter = adapter
        rlvThree.isNestedScrollingEnabled = false
        holder.itemView.setOnClickListener {
            imgToggleTwo.isSelected = !imgToggleTwo.isSelected
            if (imgToggleTwo.isSelected) {
                adapter.setEmpty(true)
                adapter.setData(item.resourceList)
            } else {
                adapter.setEmpty(false)
                val dataList = mutableListOf<ResourceListBean>()
                adapter.setData(dataList)
            }
        }
        val tvTitle = holder.getView(R.id.tv_title) as TextView
        tvTitle.text = item.catalogName
        tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 109.5f).toInt()
        if (isSel && holder.adapterPosition == 0) {
            val layout = holder.getView(R.id.ctl_two) as ConstraintLayout
            val params = layout.layoutParams as RecyclerView.LayoutParams
            params.setMargins(0, 0, 0, 0)
            layout.layoutParams = params
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
                imageResource = R.drawable.ic_no_content
            }.lparams {
                topMargin = dip(20)
            }

            textView {
                text = "暂未配置课程"
                textSize = 15f
                textColor = Color.parseColor("#666666")
            }.lparams {
                topMargin = dip(10)
            }
        }
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = TextView(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt()
        )
        view.layoutParams = params
        return view
    }
}

class CatalogThreeAdapter(var onFree: (ResourceListBean) -> Unit) : BaseAdapter<ResourceListBean>() {
    private var isOne = false

    //    是否直接一层目录，一层目录左右间距20->15,字最大宽度变化
    fun isOne(isOne: Boolean) {
        this.isOne = isOne
    }

    override fun convert(holder: ViewHolder, item: ResourceListBean) {
        val context = holder.itemView.context
        if (holder.layoutPosition == 0) {
            val vLine = holder.getView(R.id.v_line)
            vLine.visibility = View.GONE
        }
        val tvTitle = holder.getView(R.id.tv_title) as TextView
        tvTitle.text = item.name
        val img_try = holder.getView(R.id.img_try) as ImageView
        val params = img_try.layoutParams as ConstraintLayout.LayoutParams
        if (isOne) {
            if (item.chargeType == "0") {
                tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 130f).toInt()
                params.rightMargin = DeviceUtil.dp2px(context, 10f).toInt()
            } else {
                tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 85f).toInt()
            }
        } else {
            if (item.chargeType == "0") {
                tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 150f).toInt()
                params.rightMargin = DeviceUtil.dp2px(context, 15f).toInt()
            } else {
                tvTitle.maxWidth = DeviceUtil.getDeviceWidth(context) - DeviceUtil.dp2px(context, 115f).toInt()
            }
        }
        holder.setText(
            R.id.tv_teacher_name, if (item.liveType == "1") "${item.teacher}    ${
                item.liveTime?.replace(
                    "-", "."
                )?.replace(" ", "  ")
                //时间格式调整"-"改为"." 一个空格改为2个空格 2020年9月18日
            }开课" else item.teacher
        ).setText(R.id.tv_num, item.sort).setVisible(R.id.img_try, if (item.chargeType == "0") View.VISIBLE else View.INVISIBLE)
        holder.itemView.setOnClickListener {
            if (item.chargeType == "0") onFree(item)
        }
        //如果当前类型为测评tv_teacher_name.text = "今日披星戴月，明日成就梦想！"
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return context.layoutInflater.inflate(R.layout.layout_catalog_three, parent, false)
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        linearLayout {
            lparams(matchParent, matchParent)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            imageView {
                imageResource = R.drawable.ic_no_content
            }.lparams {}

            textView {
                text = "暂未配置课程"
                textSize = 15f
                textColor = Color.parseColor("#666666")
            }.lparams {
                bottomMargin = dip(20)
            }
        }
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = TextView(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt()
        )
        view.layoutParams = params
        return view
    }
}
