//package tuoyan.com.xinghuo_daying.ui.mine.history
//
//import android.content.Context
//import android.graphics.Color
//import android.text.TextUtils
//import android.view.Gravity
//import android.view.View
//import android.view.ViewGroup
//import android.widget.LinearLayout
//import org.jetbrains.anko.*
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
//import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
//import tuoyan.com.xinghuo_dayingindex.bean.ExerciseHistory
//
///**
// * Created by  on 2018/5/16.
// */
//class HistoryAdapter(
//    private var onItemClick: (ExerciseHistory) -> Unit,
//    var loadMore: () -> Unit
//) : BaseAdapter<ExerciseHistory>(isEmpty = true) {
//    override fun convert(holder: ViewHolder, item: ExerciseHistory) {
//        holder.setText(R.id.tv_name, item.paperName)
//            .setText(R.id.tv_grade, item.createtime ?: "")
//            .setText(R.id.tv_create_time, "")
//        holder.itemView.setOnClickListener {
//            onItemClick(getData()[holder.adapterPosition])
//        }
//    }
//
//    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
//        relativeLayout {
//            lparams(matchParent, wrapContent)
//            backgroundColor = Color.parseColor("#ffffff")
//            textView {
//                id = R.id.tv_name
//                textColor = Color.parseColor("#1e1e1e")
//                textSize = 15f
//                maxLines = 1
//                ellipsize = TextUtils.TruncateAt.END
//            }.lparams(matchParent, wrapContent) {
//                topMargin = dip(15)
//                leftMargin = dip(15)
//                rightMargin = dip(15)
//            }
//
//            textView {
//                id = R.id.tv_grade
//                textColor = Color.parseColor("#999999")
//                textSize = 11f
//            }.lparams {
//                below(R.id.tv_name)
//                topMargin = dip(8)
//                bottomMargin = dip(15)
//                leftMargin = dip(15)
//            }
//
//            textView {
//                id = R.id.tv_create_time
//                textColor = resources.getColor(R.color.color_ff6032)
//                textSize = 11f
//            }.lparams {
//                alignParentRight()
//                baselineOf(R.id.tv_grade)
//                rightMargin = dip(15)
//            }
//
//            view {
//                backgroundColor = Color.parseColor("#ececec")
//            }.lparams(matchParent, 2)
//        }
//    }
//
//    override fun empty(holder: ViewHolder) {
//
//    }
//
//    override fun emptyView(context: Context,parent: ViewGroup): View = with(context) {
//        linearLayout {
//            lparams(matchParent, matchParent)
//            orientation = LinearLayout.VERTICAL
//            gravity = Gravity.CENTER_HORIZONTAL
//            imageView {
//                imageResource = R.drawable.empty_study
//            }.lparams {
//                topMargin = dip(120)
//            }
//
//            textView {
//                text = "暂无练习记录哦"
//                textSize = 15f
//                textColor = Color.parseColor("#666666")
//            }.lparams {
//                topMargin = dip(10)
//            }
//        }
//    }
//
//    override fun loadMore(holder: ViewHolder) {
//        loadMore()
//    }
//
//}
