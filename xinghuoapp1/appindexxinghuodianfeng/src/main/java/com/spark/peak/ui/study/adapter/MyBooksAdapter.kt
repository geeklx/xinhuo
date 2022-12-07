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
class MyBooksAdapter(isHeader : Boolean, isFooter : Boolean, private var addBtnClick : ()-> Unit,var itemClick : (key: String)-> Unit,var deleteClick : (key: String, position : Int)-> Unit) : StudyBaseAdapter<MyBookNetClass>(isHeader = isHeader, isFooter = isFooter) {

    override fun header(holder: ViewHolder) {
        holder.setText(R.id.tv_books_count,"共"+getData().size+"本")
                .setText(R.id.tv_books_config,if (isConfig) "完成" else "编辑")
                .setOnClickListener(R.id.tv_books_config,{
//                    mContext?.startActivity<BooksConfigActivity>(BooksConfigActivity.DATA to getData(), BooksConfigActivity.IS_BOOKS to true)
                    isConfig = !isConfig
                    setFooter(!isConfig)
                    notifyDataSetChanged()
                })
    }
    override fun headerView(context: Context): View = with(context){
        relativeLayout {
            lparams(matchParent,dip(35))
            textView {
                id = R.id.tv_books_count
                textSize = 12f
                textColor = Color.parseColor("#999999")
            }.lparams(wrapContent, wrapContent){
                alignParentLeft()
                centerVertically()

            }

            textView {
                id = R.id.tv_books_config
                textSize = 12f
                textColor = Color.parseColor("#1482ff")
            }.lparams(wrapContent, wrapContent){
                alignParentRight()
                centerVertically()
                rightMargin = dip(15)
            }
        }
    }

    override fun convert(holder: ViewHolder, item: MyBookNetClass) {
        holder.setText(R.id.my_books_name,item.name)
                .setImageUrl(R.id.my_books_img,item.img?:"",R.drawable.default_book)
                .setVisible(R.id.my_books_delete,if (isConfig) View.VISIBLE else View.GONE)
                .setImageResource(R.id.my_books_delete,R.drawable.ic_delete)
                .setOnClickListener(R.id.my_books_delete){
                    deleteClick(getData()[holder.adapterPosition-1].key?:"", holder.adapterPosition)
                }
        holder.itemView.setOnClickListener {
            if (holder.adapterPosition > 0){
                itemClick(getData()[holder.adapterPosition-1].key?:"")
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context){
        relativeLayout {
            rightPadding = dip(15)
            bottomPadding = dip(19)
            imageView {
                id = R.id.my_books_img
                scaleType = ImageView.ScaleType.FIT_XY
            }.lparams(dip(105),dip(137))

            textView {
                id = R.id.my_books_name
                ellipsize = TextUtils.TruncateAt.END
                lines = 2
                textSize = 15f
                textColor = Color.parseColor("#1e1e1e")
            }.lparams(matchParent, wrapContent){
                leftMargin = dip(5)
                rightMargin = dip(5)
                topMargin = dip(5)
                below(R.id.my_books_img)
            }

            imageView {
                id = R.id.my_books_delete
            }.lparams(wrapContent, wrapContent) {
                alignParentTop()
                alignParentRight()
                margin = dip(4)
            }

        }
    }

    override fun empty(holder: ViewHolder) {

    }

    override fun emptyView(context: Context, parent: ViewGroup): View = with(context){
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
                text = "尚未添加图书"
                textSize = 15f
                textColor = Color.parseColor("#666666")
            }.lparams {
                topMargin = dip(10)
            }

            textView {
                text = "扫描图书条形码添加"
                textSize = 12f
                textColor = Color.parseColor("#999999")
            }.lparams {
                topMargin = dip(10)
            }

            textView {
                text = "添加图书"
                textSize = 15f
                textColor = Color.parseColor("#1482ff")
                gravity = Gravity.CENTER
                backgroundResource = R.drawable.shape_add_books
                setOnClickListener {
                    addBtnClick()
                }
            }.lparams(dip(160),dip(40)){
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
                backgroundResource = R.drawable.add_books
            }.lparams(dip(105),dip(137))
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