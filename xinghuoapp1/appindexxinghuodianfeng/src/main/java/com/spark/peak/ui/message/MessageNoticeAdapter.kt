package com.spark.peak.ui.message

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.MsgBean
import org.jetbrains.anko.*

class MessageNoticeAdapter(var toDetail: (MsgBean) -> Unit, var loadMore: () -> Unit) : BaseAdapter<MsgBean>(isEmpty = true, isMore = true) {

    /**
     * 通知
     * key	        string	消息主键
     * title	    string	消息标题
     * type	        int	    链接对象类型
     * content	    string	通知内容
     * url	        string	链接对象详情页地址
     * time	        string	推送时间
     * targetKey	string	链接对象主键
     */
    override fun convert(holder: ViewHolder, item: MsgBean) {
        holder.setText(R.id.tv_time, item.createDate)
                .setText(R.id.tv_content, item.content)
                .setText(R.id.tv_title, item.title)
        holder.itemView.setOnClickListener { toDetail(item) }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_msg, parent, false)
    }

    override fun empty(holder: ViewHolder) {
        holder.setText(R.id.tv_status, "暂无消息~")
    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        verticalLayout {
            lparams(matchParent, matchParent)
            gravity = Gravity.CENTER_HORIZONTAL
            space().lparams(wrapContent, dip(120))
            imageView(R.mipmap.empty_2)
            textView("暂无消息~") {
                id = R.id.tv_status
                gravity = Gravity.CENTER
                textSize = 14f
                textColor = resources.getColor(R.color.color_999999)
            }
        }
    }

    override fun loadMore(holder: ViewHolder) {
        loadMore()
    }
}
