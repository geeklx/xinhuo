//package tuoyan.com.xinghuo_daying.ui.netLesson.myLesson
//
//import android.content.Context
//import android.graphics.Typeface
//import android.os.Bundle
//import androidx.recyclerview.widget.LinearLayoutManager
//import android.view.Gravity
//import android.view.View
//import android.view.ViewGroup
//import org.jetbrains.anko.*
//import org.jetbrains.anko.custom.async
//import org.jetbrains.anko.recyclerview.v7.recyclerView
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
//import tuoyan.com.xinghuo_dayingindex.base.LifeFragment
//import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
//import tuoyan.com.xinghuo_dayingindex.bean.ClassListBean
//import tuoyan.com.xinghuo_dayingindex.bean.ResourceList
//import tuoyan.com.xinghuo_dayingindex.bean.ResourceListBean
//
///**x
// */
//class NetDataFragment : LifeFragment<NetLessonsPresenter>() {
//    override val presenter = NetLessonsPresenter(this)
//    override val layoutResId = 0
//    val dataList by lazy { arguments.getSerializable(DATA) as ArrayList<ClassListBean>}
//    private val adapter by lazy { DataAdapter() }
//    override fun initView() = UI {
//        recyclerView {
//            layoutManager = LinearLayoutManager(context)
//            adapter = this@NetDataFragment.adapter
//        }
//    }.view
//
//    override fun initData() {
//        val dataList0 = mutableListOf<Any>()
//        dataList.forEach {
//            dataList0.add(it)
//            it.resourceList?.let {
//                dataList0.addAll(it)
//            }
//        }
//        adapter.setData(dataList0)
//    }
//
//    companion object {
//        fun instance(list:ArrayList<ClassListBean>) = NetDataFragment().apply {
//            arguments = Bundle().apply {
//                putSerializable(DATA, list)
//            }
//        }
//
//        const val DATA = "data"
//
//    }
//}
//
//private class DataAdapter : BaseAdapter<Any>() {
//    private var currentView: View? = null
//    override fun convert(holder: ViewHolder, item: Any) {
//        var item0 = item as? ClassListBean
//        holder.setText(R.id.tv_title, item0?.catalogName?:"NO TITLE")
////                .setOnClickListener(R.id.tv_title) { it ->
////                    if (currentView == it) { //判断点击是否是当前展开item
////                        //设置收起状态
////                        currentView?.isSelected = false
////                        //移除子item
////                        (itemCount - 1 downTo 0).forEach {
////                            if (getItemViewType(it) == SUB)
////                                remove(it)
////                        }
////                        currentView = null
////
////                    } else {
////                        currentView?.isSelected = false
////                        (itemCount - 1 downTo 0).forEach {
////                            if (getItemViewType(it) == SUB) {
////                                remove(it)
////                                val position = holder.layoutPosition
////                            }
////                        }
//////                        改变当前展开item
////                        currentView = it
////                        currentView?.isSelected = true
////                        val map = item["sub"]
////                        if (map is List<*>)
////                            //查找当前item位置并插入到当前item下方
////                            addData(map as List<Map<String, Any>>, getData().indexOf(item) + 1)
////                    }
////                }
//
//    }
//
//    private fun subConvert(holder: ViewHolder, map: Any) {
//        var map0 = map as? ResourceListBean
//        holder.setText(R.id.tv_title, map0?.name?:"NO TITLE")
//    }
//
//    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
//        verticalLayout {
//            backgroundResource = R.color.color_ffffff
//            lparams(matchParent, wrapContent)
//            textView() {
//                id = R.id.tv_title
//                textSize = 14f
//                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_data_item_l, 0, R.drawable.ic_data_item_r, 0)
//                compoundDrawablePadding = dip(15)
//                textColor = resources.getColor(R.color.color_222831)
//                typeface = Typeface.DEFAULT_BOLD
//                gravity = Gravity.CENTER_VERTICAL
//                horizontalPadding = dip(15)
//            }.lparams(matchParent, dip(50))
//            view {
//                backgroundResource = R.color.color_edeff0
//            }.lparams(matchParent, dip(0.5f)) {
//                marginStart = dip(45)
//            }
//        }
//    }
//
//    private fun subView(parent: ViewGroup) = with(parent.context) {
//        verticalLayout {
//            backgroundResource = R.color.color_f6f7f8
//            lparams(matchParent, wrapContent)
//            view {
//                backgroundResource = R.color.color_edeff0
//            }.lparams(matchParent, dip(0.5f)) {
//                marginStart = dip(45)
//            }
//            textView {
//                id = R.id.tv_title
//                textSize = 13f
//                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_video, 0, 0, 0)
//                compoundDrawablePadding = dip(15)
//                textColor = resources.getColor(R.color.color_222831)
//                gravity = Gravity.CENTER_VERTICAL
//                rightPadding = dip(15)
//                leftPadding=dip(45)
//            }.lparams(matchParent, dip(50))
//        }
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        if (getData()[position] is ResourceListBean) return SUB
//        return super.getItemViewType(position)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        if (mContext == null) mContext = parent.context
//        return if (viewType == SUB) ViewHolder(subView(parent))
//        else super.onCreateViewHolder(parent, viewType)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        if (holder.itemViewType == SUB) subConvert(holder, getData()[position - if (getIsHeader()) 1 else 0])
//        else super.onBindViewHolder(holder, position)
//    }
//
//    companion object {
//        const val SUB = 0x15d4
//    }
//}