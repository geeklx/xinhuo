package com.spark.peak.widegt

import android.content.Context
import android.graphics.Color
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.spark.peak.R
import com.spark.peak.base.BaseAdapter
import com.spark.peak.base.ViewHolder
import com.spark.peak.bean.Subject
import org.jetbrains.anko.*

/**
 * Created by 李昊 on 2018/4/14.
 */
class MenuView : LinearLayout {

    private val viewPager = ViewPager(this.context)
    private val llPoint = LinearLayout(this.context)

    private var dataList : List<Subject>? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    init {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        initLayout()
    }

    private fun initLayout(){
        //设置viewpager
        viewPager.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        viewPager.leftPadding = dip(10)
        viewPager.rightPadding = dip(10)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                position(position)
            }

        })
        addView(viewPager)
        //设置指示器容器
        llPoint.orientation = LinearLayout.HORIZONTAL
        llPoint.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        addView(llPoint)
    }

    fun setData(list : List<Subject>,onClick: (Subject) -> Unit){
        dataList = list
        if (dataList?.size!!<=4){
            viewPager.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dip(84))
        }else{
            viewPager.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dip(168))
        }
        if (dataList?.size!! > 0){
            if (dataList?.size!! > 8){
                llPoint.visibility = View.VISIBLE
                initPoint(dataList?.size!!)
            }else{
                pagerCount = 1
                llPoint.visibility = View.GONE
            }
            viewPager.adapter = MenuPagerAdapter(onClick)
            viewPager.visibility = View.VISIBLE
        }else{
            viewPager.visibility = View.GONE
        }
    }

    internal var pagerCount = 0
    /**
     * 初始化指示点
     */
    private fun initPoint(size : Int){
        pagerCount = size / 8
        if (size % 8 > 0){
            pagerCount++
        }

        llPoint.removeAllViews()
        var lp = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        lp.leftMargin = 10
        lp.rightMargin = 10

        for (i in 0..(pagerCount-1)){
            var oPoint = ImageView(this.context)
            oPoint.layoutParams = lp
            oPoint.imageResource = R.drawable.selector_home_menu_points
            llPoint.addView(oPoint)
        }
        position(0)
    }

    private fun position(index : Int){
        if (index < llPoint.childCount){
            for (i in 0 until llPoint.childCount){
                llPoint.getChildAt(i).isEnabled = false
            }
            llPoint.getChildAt(index).isEnabled = true
        }
    }

    internal fun getIndexDataList(index : Int) :List<Subject> {
        var startIndex = index*8
        var endIndex = if (dataList?.size!! < (index*8+8)){
            dataList?.size!!
        }else{
            index*8+8
        }
        return dataList?.subList(startIndex,endIndex)?:ArrayList()
    }

    internal inner class MenuPagerAdapter(var onClick: (Subject) -> Unit) : PagerAdapter() {

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
             return view == `object`
        }

        override fun getCount(): Int = pagerCount

        override fun instantiateItem(container: ViewGroup, position: Int): Any = with(RecyclerView(container.context)){
            layoutParams = LayoutParams(matchParent, wrapContent)
            var oAdapter = MenuAdapter(onClick)
            oAdapter.setData(getIndexDataList(position))
            layoutManager = GridLayoutManager(context, 4)
            adapter = oAdapter
            container.addView(this)
            this
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    inner class MenuAdapter(var onClick : (subject: Subject)->Unit) : BaseAdapter<Subject>(){

        override fun convert(holder: ViewHolder, item: Subject) {
            holder.setImageResource(R.id.home_menu_img,item.img())
                    .setText(R.id.home_menu_title,item.name)
            holder.itemView.setOnClickListener {
                onClick(getData()[holder.adapterPosition])
            }
        }


        override fun convertView(context: Context, parent: ViewGroup): View = with(context){
            linearLayout {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                imageView {
                    id = R.id.home_menu_img
                }.lparams(dip(39), dip(39)){
                    bottomMargin = dip(8)
                }

                textView {
                    id = R.id.home_menu_title
                    textSize = 12f
                    textColor = Color.parseColor("#1e1e1e")
                }.lparams(wrapContent, wrapContent){
                    bottomMargin = dip(20)
                }
            }
        }

    }


}

