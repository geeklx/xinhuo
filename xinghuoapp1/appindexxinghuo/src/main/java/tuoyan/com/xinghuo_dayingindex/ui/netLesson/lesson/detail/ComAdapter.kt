package tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.detail

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.Comment

/**
 * Created by  on 2018/10/10.
 */
class ComAdapter(private var showStar: Boolean = true, var onLoadMore: ()-> Unit): BaseAdapter<Comment>(isHeader = true,isEmpty = true){


    var points: Double = 5.0
    var total: Int = 0
    override fun header(holder: ViewHolder) {
        var ll_star = holder.getView(R.id.ll_star) as LinearLayout
        showStars(ll_star,points)
        holder.setText(R.id.tv_all_comment,"全部评论（"+ total +"条）")
                .setText(R.id.tv_scor,points.toString())
                .setVisible(R.id.ll_comment,if (showStar) View.VISIBLE else View.GONE)
    }

    override fun headerView(context: Context,parent: ViewGroup): View = context.relativeLayout {
        textView {
            id = R.id.tv_all_comment
            textSize = 12f
            textColor = Color.parseColor("#222831")
        }.lparams(wrapContent, wrapContent){
            leftMargin = dip(15)
            topMargin = dip(16)
            bottomMargin = dip(16)
        }

        linearLayout {
            id = R.id.ll_comment
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            textView {
                text = "好评度："
                textColor = Color.parseColor("#222831")
                textSize = 12f
            }.lparams(wrapContent, wrapContent)
            linearLayout {
                id = R.id.ll_star
                orientation = LinearLayout.HORIZONTAL
            }.lparams(wrapContent, wrapContent)
            textView {
                id = R.id.tv_scor
                textColor = Color.parseColor("#222831")
                textSize = 12f
            }.lparams(wrapContent, wrapContent)
        }.lparams(wrapContent, wrapContent){
            centerVertically()
            alignParentRight()
            rightMargin = dip(15)
        }
    }


    override fun convert(holder: ViewHolder, item: Comment) {
        holder.setText(R.id.tv_name,item.name)
                .setText(R.id.tv_date,item.time)
                .setText(R.id.tv_comment,item.content)
                .setCircularImage(R.id.iv_avatar,item.img)
        var ll_star = holder.getView(R.id.ll_star) as LinearLayout
        showStars(ll_star,item.points)

    }

    override fun convertView(context: Context, parent: ViewGroup): View = context.relativeLayout {
        bottomPadding = dip(15)
        imageView {
            id = R.id.iv_avatar
        }.lparams(dip(35),dip(35)){
            leftMargin = dip(15)
        }

        textView {
            id = R.id.tv_name
            textSize = 15f
            textColor = Color.parseColor("#222831")
            paint.isFakeBoldText = true
        }.lparams(wrapContent, wrapContent){
            topMargin = dip(5)
            leftMargin = dip(15)
            rightOf(R.id.iv_avatar)
        }

        textView {
            id = R.id.tv_date
            textColor = Color.parseColor("#c3c7cb")
            textSize = 12f
        }.lparams(wrapContent, wrapContent){
            alignParentRight()
            rightMargin = dip(15)
            baselineOf(R.id.tv_name)
        }

        linearLayout {
            id = R.id.ll_star
            orientation = LinearLayout.HORIZONTAL
        }.lparams(wrapContent, wrapContent){
            sameLeft(R.id.tv_name)
            below(R.id.tv_name)
            topMargin = dip(5)
        }


        textView {
            id = R.id.tv_comment
            textSize = 14f
            textColor = Color.parseColor("#222831")
        }.lparams(wrapContent, wrapContent){
            rightMargin = dip(15)
            sameLeft(R.id.tv_name)
            below(R.id.ll_star)
            topMargin = dip(10)
        }
    }


    private fun showStars(vg: ViewGroup, scor: Double){
        vg.removeAllViews()
        for (i in 1..5){
            var disCount = i.toDouble() - scor
            var star = mContext!!.relativeLayout {
                lparams(dip(12),dip(12)){
                    rightMargin = dip(3)
                }
                view {
                    backgroundColor = Color.parseColor("#cccccc")
                }.lparams(matchParent, matchParent)
                view {
                    backgroundColor = Color.parseColor("#FFAF30")
                }.lparams(if (disCount<= 0){
                    dip(12)
                }else if (disCount> 0 && disCount< 1){
                    dip((12*(1 - disCount)).toInt())
                }else {
                    0
                }, matchParent)
                view {
                    backgroundResource = R.drawable.ic_star_empty
                }.lparams(matchParent, matchParent)
            }
            vg.addView(star)
        }
    }

    override fun emptyView(context: Context,parent: ViewGroup): View = with(context){
        linearLayout {
            lparams(matchParent, matchParent)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            imageView {
                imageResource = R.drawable.ic_no_com
            }.lparams {
                topMargin = dip(40)
            }
            textView {
                text = "还没有人评论过哟~"
                textSize = 15f
                textColor = Color.parseColor("#666666")
            }.lparams {
                topMargin = dip(10)
            }
        }
    }

    override fun setData(data: List<Comment>?) {
        if (data?.isEmpty() == true) {
            setHeader(false)
        }else{
            setHeader(true)
        }
        super.setData(data)
    }

    override fun loadMore(holder: ViewHolder) {
        onLoadMore()
    }

    override fun moreView(context: Context,parent: ViewGroup) = with(context) {
        verticalLayout {
            lparams(matchParent, dip(50))
            textView("加载更多评论") {
                gravity = Gravity.CENTER
                lines = 1
                textSize = 15f
                textColor = 0xff666666.toInt()
            }.lparams(matchParent, matchParent)
        }
    }


}