//package com.spark.spark_ns.ui.search.adapter
//
//import android.view.Gravity
//import android.view.View
//import android.view.ViewGroup
//import android.widget.AbsListView
//import android.widget.BaseAdapter
//import android.widget.TextView
//import com.spark.spark_ns.bean.Grade
//import com.spark.spark_ns.bean.Subject
//import org.jetbrains.anko.dip
//
///**
// * 创建者： 霍述雷
// * 时间：  2017/7/18.
// */
//
////class DefaultSpinnerAdapter(private val mTypes: List<String>) : BaseAdapter() {
////
////    private var listener: SpinnerItemClickListener? = null
////
////
////    fun setListener(listener: SpinnerItemClickListener) {
////        this.listener = listener
////    }
////
////    override fun getCount(): Int {
////        return mTypes.size
////    }
////
////    override fun getItem(position: Int): String {
////        return mTypes[position]
////    }
////
//
////        return position.toLong()
////    }
////
////    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
////        val context = parent.context
////        val view = TextView(context)
////        view.layoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
////        val h = context.dip(10)
////        val w = context.dip(20)
////        view.setPadding(w, h, w, h)
////        view.gravity = Gravity.CENTER
////        //        view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
////        view.text = mTypes[position]
////        view.setOnClickListener { v -> listener?.OnItemClick(position, mTypes[position]) }
////        return view
////    }
////
////
////}
//
//class GradeSpinnerAdapter(private val mTypes: List<Grade>) : BaseAdapter() {
//
//    private var listener: SpinnerItemClickListener? = null
//
//
//    fun setListener(listener: SpinnerItemClickListener) {
//        this.listener = listener
//    }
//
//    override fun getCount(): Int {
//        return mTypes.size
//    }
//
//    override fun getItem(position: Int): String {
//        return mTypes[position].name
//    }
//
//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }
//
//    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
//        val context = parent.context
//        val view = TextView(context)
//        view.layoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        val h = context.dip(10)
//        val w = context.dip(20)
//        view.setPadding(w, h, w, h)
//        view.gravity = Gravity.CENTER
//        //        view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//        view.text = mTypes[position].name
//        view.setOnClickListener { v -> listener?.OnItemClick(position, mTypes[position].id) }
//        return view
//    }
//
//
//}
//
//class SubjectSpinnerAdapter(private val mTypes: List<Subject>) : BaseAdapter() {
//
//    private var listener: SpinnerItemClickListener? = null
//
//
//    fun setListener(listener: SpinnerItemClickListener) {
//        this.listener = listener
//    }
//
//    override fun getCount(): Int {
//        return mTypes.size
//    }
//
//    override fun getItem(position: Int): String {
//        return mTypes[position].name ?: ""
//    }
//
//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }
//
//    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
//        val context = parent.context
//        val view = TextView(context)
//        view.layoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        val h = context.dip(10)
//        val w = context.dip(20)
//        view.setPadding(w, h, w, h)
//        view.gravity = Gravity.CENTER
//        //        view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//        view.text = mTypes[position].name
//        view.setOnClickListener { v -> listener?.OnItemClick(position, mTypes[position].key ?: "") }
//        return view
//    }
//
//
//}
//
//interface SpinnerItemClickListener {
//    fun OnItemClick(position: Int, id: String)
//}