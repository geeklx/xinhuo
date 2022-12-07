package com.spark.peak.ui.study.adapter

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.spark.peak.R
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.MyBookNetClass
import org.jetbrains.anko.*

/**
 * Created by 李昊 on 2018/4/20.
 */
class MyNetclassAdapter(isHeader: Boolean, isFooter: Boolean, private var addBtnClick: () -> Unit,
                        private var goToDetail: (key: String) -> Unit,
                        var deleteClick : (key: String, position : Int)-> Unit) : StudyBaseAdapter<MyBookNetClass>(isHeader = isHeader, isFooter = isFooter) {

    private var checkedIndex = mutableListOf<Int>()

    override fun header(holder: ViewHolder) {
        holder.setText(R.id.tv_books_count, "共" + getData().size + "套")
                .setText(R.id.tv_books_config, if (isConfig) "完成" else "编辑")
                .setOnClickListener(R.id.tv_books_config, {
//                    mContext?.startActivity<BooksConfigActivity>(BooksConfigActivity.DATA to getData(), BooksConfigActivity.IS_BOOKS to false)
                    isConfig = !isConfig
                    setFooter(!isConfig)
                    notifyDataSetChanged()
                })

    }

    override fun headerView(context: Context): View = with(context) {
        relativeLayout {
            lparams(matchParent, dip(35))
            textView {
                id = R.id.tv_books_count
                textSize = 12f
                textColor = Color.parseColor("#999999")
            }.lparams(wrapContent, wrapContent) {
                alignParentLeft()
                centerVertically()
            }

            textView {
                id = R.id.tv_books_config
                textSize = 12f
                textColor = Color.parseColor("#1482ff")
                setOnClickListener {
                }
            }.lparams(wrapContent, wrapContent) {
                alignParentRight()
                centerVertically()
                rightMargin = dip(15)
            }
        }
    }

    override fun convert(holder: ViewHolder, item: MyBookNetClass) {
        holder.setText(R.id.tv_netclass_title, item.name)
                .setImageUrl(R.id.iv_netclass_corver, item.img ?: "",R.drawable.default_lesson)
                .setVisible(R.id.my_books_delete, if (isConfig) View.VISIBLE else View.GONE)
                .setImageResource(R.id.my_books_delete, R.drawable.ic_delete)
                .setOnClickListener(R.id.my_books_delete) {
                    var itemData = getData()[holder.adapterPosition-1]
                    if (itemData.iseffect == "0"){//1 过期 0 有效
                        deleteClick("", holder.adapterPosition) //未过期网课不允许删除 ，返回""
                    }else if (itemData.iseffect == "1"){
                        deleteClick(getData()[holder.adapterPosition-1].key?:"", holder.adapterPosition)
                    }
                }.setVisible(R.id.iv_netclass_outtime, if (item.iseffect == "1") View.VISIBLE else View.GONE) //1 过期 0 有效
                .setVisible(R.id.ic_play,if (item.iseffect == "0") View.VISIBLE else View.GONE)
        holder.itemView.setOnClickListener {
            if (item.iseffect == "0"){
                goToDetail(getData()[holder.adapterPosition - if (getIsHeader()) 1 else 0].key ?: "")
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        relativeLayout {
            rightPadding = dip(15)
            bottomPadding = dip(19)
            imageView {
                id = R.id.iv_netclass_corver
                scaleType = ImageView.ScaleType.FIT_XY
            }.lparams(dip(165), dip(103))

            imageView(R.mipmap.ic_play) {
                id = R.id.ic_play
            }.lparams(dip(37), dip(37)) {
                centerHorizontally()
                sameTop(R.id.iv_netclass_corver)
                sameBottom(R.id.iv_netclass_corver)
            }
            textView {
                id = R.id.tv_netclass_title
                ellipsize = TextUtils.TruncateAt.END
                lines = 2
                textSize = 15f
                textColor = Color.parseColor("#1e1e1e")
            }.lparams(matchParent, wrapContent) {
                leftMargin = dip(5)
                rightMargin = dip(5)
                topMargin = dip(5)
                below(R.id.iv_netclass_corver)
            }
            imageView {
                id = R.id.my_books_delete
            }.lparams(wrapContent, wrapContent) {
                alignParentTop()
                alignParentRight()
                margin = dip(4)
            }

            imageView {
                id = R.id.iv_netclass_outtime
                scaleType = ImageView.ScaleType.FIT_XY
                backgroundResource = R.drawable.ic_out_of_time
            }.lparams(dip(165), dip(103))
        }
    }

    override fun empty(holder: ViewHolder) {

    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context) {
        linearLayout {
            lparams(matchParent, matchParent)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            imageView {
                imageResource = R.drawable.empty_study
            }.lparams {
                topMargin = dip(40)
            }

            textView {
                text = "尚未添加网课"
                textSize = 15f
                textColor = Color.parseColor("#666666")
            }.lparams {
                topMargin = dip(10)
            }

            textView {
                text = "快去添加吧"
                textSize = 12f
                textColor = Color.parseColor("#999999")
            }.lparams {
                topMargin = dip(10)
            }

            textView {
                text = "添加网课"
                textSize = 15f
                textColor = Color.parseColor("#1482ff")
                gravity = Gravity.CENTER
                backgroundResource = R.drawable.shape_add_books
                setOnClickListener {
                    addBtnClick()
                }
            }.lparams(dip(160), dip(40)) {
                topMargin = dip(30)
            }
        }
    }

    override fun footer(holder: ViewHolder) {
        super.footer(holder)
        holder.itemView.setOnClickListener {
            if (!isConfig){
                addBtnClick()
            }
        }
    }

    override fun footerView(context: Context): View = with(context){
        relativeLayout {
            imageView {
                backgroundResource = R.drawable.add_class
            }.lparams(dip(165), dip(103))
        }
    }

    override fun setData(data: List<MyBookNetClass>?) {
        if (data?.isEmpty() == true) {
            setHeader(false)
        }else{
            setHeader(true)
        }
        super.setData(data)
    }

}