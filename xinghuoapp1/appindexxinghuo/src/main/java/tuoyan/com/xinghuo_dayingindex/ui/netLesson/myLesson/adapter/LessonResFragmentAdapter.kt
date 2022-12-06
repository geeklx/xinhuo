package tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.adapter

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.WX_PROGRAM_ID
import tuoyan.com.xinghuo_dayingindex.WX_PROGRAM_ID_LISTEN
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.ClassListBean
import tuoyan.com.xinghuo_dayingindex.bean.LessonRes
import tuoyan.com.xinghuo_dayingindex.ui.dialog.ResWarmingDialog
import tuoyan.com.xinghuo_dayingindex.utlis.WxMini

/*
* shareClick 1:copy 2:share
*
* */
class LessonResFragmentAdapter(
    var qqNum: String,
    var isAppLet: String,
    var isAppletListen: String,
    var isDocDown: String,
    var docDownUrl: String,
    private var itemClick: (LessonRes?) -> Unit,
    private var qqClick: () -> Unit,
    var shareClick: (Int, String) -> Unit
) : BaseAdapter<ClassListBean>(isHeader = true, isFooter = true) {

    override fun header(holder: ViewHolder) {
        if (qqNum.isEmpty()) {
            holder.setVisible(R.id.rl_qq, View.GONE)
        } else {
            holder.setVisible(R.id.rl_qq, View.VISIBLE)
            holder.setText(R.id.tv_qq_num, "学习技巧交流群：${qqNum}")
        }
//        是否有小程序，1有，0否
        if ("1" == isAppLet) {
            holder.setVisible(R.id.rl_word, View.VISIBLE)
        } else {
            holder.setVisible(R.id.rl_word, View.GONE)
        }
        if ("1" == isAppletListen) {
            holder.setVisible(R.id.rl_listen, View.VISIBLE)
        } else {
            holder.setVisible(R.id.rl_listen, View.GONE)
        }


        if ("1" == isDocDown) {
            holder.setVisible(R.id.rl_res, View.VISIBLE)
        } else {
            holder.setVisible(R.id.rl_res, View.GONE)
        }
        holder.getView(R.id.rl_qq).setOnClickListener {
            qqClick()
        }
        holder.getView(R.id.rl_word).setOnClickListener {
            shareClick(3, "")
            WxMini.goWxMini(holder.itemView.context, WX_PROGRAM_ID)
        }
        holder.getView(R.id.img_ques).setOnClickListener {
            ResWarmingDialog(holder.itemView.context).show()
        }
        holder.getView(R.id.tv_copy_link).setOnClickListener {
            shareClick(1, docDownUrl)
        }
        holder.getView(R.id.tv_share_link).setOnClickListener {
            shareClick(2, docDownUrl)
        }
        holder.getView(R.id.rl_listen).setOnClickListener {
            shareClick(4, "")
            WxMini.goWxMini(holder.itemView.context, WX_PROGRAM_ID_LISTEN)
        }
    }

    override fun headerView(context: Context,parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_lesson_res_header, null)
    }

    override fun convert(holder: ViewHolder, item: ClassListBean) {
        var rl_title = holder.getView(R.id.rl_title) as RelativeLayout
        var tv_title = holder.getView(R.id.tv_title) as TextView
        var tv_sel = holder.getView(R.id.tv_sel) as TextView
        var view_line = holder.getView(R.id.view_line)
        var img_pre = holder.getView(R.id.img_pre) as ImageView
        if ("XUNIMULU_AAAAA_BBBBB" == item.catalogName) {
            rl_title.visibility = View.GONE
            view_line.visibility = View.GONE
        } else {
            rl_title.visibility = View.VISIBLE
            view_line.visibility = View.VISIBLE
            tv_title.text = item.catalogName
            //2019年8月22日标题前图标，产品让后台配置
            Glide.with(img_pre.context)
                .load(item.icon)
//                .placeholder(R.mipmap.icon_lesson_pre)
//                .error(R.mipmap.icon_lesson_pre)
                .into(img_pre)
//        if (item.catalogName!!.contains("自测")) {
//            tv_title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_lesson_pre, 0, 0, 0)
//        } else if (item.catalogName!!.contains("模考")) {
//            tv_title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_lesson_test, 0, 0, 0)
//        } else if (item.catalogName!!.contains("资源")) {
//            tv_title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_lesson_res, 0, 0, 0)
//        } else {
//            tv_title.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_lesson_pre, 0, 0, 0)
//        }
        }
        tv_sel.setOnClickListener {
            item.isExpand = !item.isExpand
            notifyDataSetChanged()
        }
        var rlv_lesson = holder.getView(R.id.rlv_lesson) as RecyclerView
        var adapter = ItemAdapter() {
            itemClick(it)
        }
        rlv_lesson.layoutManager = LinearLayoutManager(mContext)
        rlv_lesson.adapter = adapter
        adapter.setData(item.resourceList)
        if (item.isExpand) {
            tv_sel.text = "收起"
            rlv_lesson.visibility = View.VISIBLE
        } else {
            tv_sel.text = "展开"
            rlv_lesson.visibility = View.GONE
        }
        tv_sel.isSelected = item.isExpand
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_lesson_res_item, null)
    }

    override fun emptyImageRes(): Int {
        return R.drawable.empty_service
    }
}

class ItemAdapter(var ItemClick: (LessonRes) -> Unit) : BaseAdapter<LessonRes>() {
    override fun convert(holder: ViewHolder, item: LessonRes) {
        holder.setText(R.id.tv_title, item.name)
        holder.itemView.setOnClickListener {
            ItemClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            textView {
                id = R.id.tv_title
                setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_lesson_item_left, 0, 0, 0)
                compoundDrawablePadding = dip(8)
                textSize = 13f
                gravity = Gravity.CENTER_VERTICAL
                textColor = ContextCompat.getColor(context, R.color.color_222831)
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            }.lparams {
                width = matchParent
                height = dip(40)
                leftMargin = dip(23)
                rightMargin = dip(35)
            }
            view {
                backgroundColor = ContextCompat.getColor(context, R.color.color_ededf1)
            }.lparams {
                width = matchParent
                height = 1
                leftMargin = dip(40)
                rightMargin = dip(30)
            }
        }
    }
}