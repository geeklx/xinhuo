//package tuoyan.com.xinghuo_daying.ui.study.adapter
//
//import android.content.Context
//import android.graphics.Color
//import android.text.TextUtils
//import android.view.Gravity
//import android.view.View
//import android.view.ViewGroup
//import android.view.ViewManager
//import android.widget.ImageView
//import android.widget.LinearLayout
//import com.shehuan.niv.NiceImageView
//import org.jetbrains.anko.*
//import org.jetbrains.anko.custom.ankoView
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
//import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
//import tuoyan.com.xinghuo_dayingindex.bean.MyBookLesson
//
///**
// * Created by  on 2018/4/20.
// */
//class MyBooksAdapter(
//    isHeader: Boolean,
//    isFooter: Boolean,
//    private var addBtnClick: () -> Unit,
//    var itemClick: (key: String) -> Unit,
//    var deleteClick: (key: String, position: Int) -> Unit
//) : BaseAdapter<MyBookLesson>(isHeader = isHeader, isFooter = isFooter) {
//
//    var isConfig: Boolean = false
//
//    override fun header(holder: ViewHolder) {
//        holder.setText(R.id.tv_books_count, "共" + getData().size + "本")
//            .setText(R.id.tv_books_config, if (isConfig) "完成" else "管理")
//            .setOnClickListener(R.id.tv_books_config, {
//                isConfig = !isConfig
//                setFooter(!isConfig)
//                notifyDataSetChanged()
//            })
//    }
//
//    override fun headerView(context: Context,parent: ViewGroup): View = with(context) {
//        relativeLayout {
//            lparams(matchParent, wrapContent)
//            topPadding = dip(15)
//            leftPadding = dip(15)
//            rightPadding = dip(15)
//            textView {
//                id = R.id.tv_books_count
//                textSize = 13f
//                textColor = Color.parseColor("#c3c7cb")
//            }.lparams(wrapContent, wrapContent) {
//                alignParentLeft()
//            }
//
//            textView {
//                id = R.id.tv_books_config
//                textSize = 13f
//                textColor = Color.parseColor("#222831")
//            }.lparams(wrapContent, wrapContent) {
//                alignParentRight()
//            }
//        }
//    }
//
//    override fun convert(holder: ViewHolder, item: MyBookLesson) {
//        holder.setText(R.id.my_books_title, item.name)
//            .setImageUrl(R.id.my_books_cover, item.img, 0, 0)
//            .setVisible(R.id.my_books_delete, if (isConfig) View.VISIBLE else View.GONE)
//            .setOnClickListener(R.id.my_books_delete) {
//                deleteClick(getData()[holder.adapterPosition].key, holder.adapterPosition)
//            }
//        holder.itemView.setOnClickListener {
//            itemClick(item.key)
//        }
//    }
//
//    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
//        relativeLayout {
//            lparams {
//                bottomMargin = dip(17)
//            }
//            relativeLayout {
//                id = R.id.rl_corver
//                backgroundResource = R.drawable.bg_book_cover
//                mImageView {
//                    id = R.id.my_books_cover
//                    scaleType = ImageView.ScaleType.CENTER_CROP
//                    setCornerRadius(dip(2))
//                }.lparams(matchParent, matchParent)
//            }.lparams(matchParent, dip(147))
//
//            textView {
//                id = R.id.my_books_title
//                ellipsize = TextUtils.TruncateAt.END
//                maxLines = 1
//                textSize = 14f
//                textColor = Color.parseColor("#222831")
//            }.lparams(matchParent, wrapContent) {
//                leftMargin = dip(5)
//                rightMargin = dip(5)
//                topMargin = dip(5)
//                below(R.id.rl_corver)
//            }
//
//            imageView {
//                id = R.id.my_books_delete
//                imageResource = R.mipmap.icon_book_delete
//                visibility = View.GONE
//                padding = dip(5)
//            }.lparams(wrapContent, wrapContent) {
//                alignParentTop()
//                alignParentRight()
//            }
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
//                imageResource = R.drawable.ic_no_content
//            }.lparams {
//                topMargin = dip(40)
//            }
//            textView {
//                text = "尚未添加图书"
//                textSize = 15f
//                textColor = Color.parseColor("#666666")
//            }.lparams {
//                topMargin = dip(10)
//            }
//
//            textView {
//                text = "扫描图书条形码添加"
//                textSize = 12f
//                textColor = Color.parseColor("#999999")
//                visibility = View.GONE
//            }.lparams {
//                topMargin = dip(10)
//            }
//
//            textView {
//                text = "添加图书"
//                textSize = 15f
//                textColor = Color.parseColor("#1482ff")
//                gravity = Gravity.CENTER
//                backgroundResource = R.drawable.shape_add_books
//                setOnClickListener {
//                    addBtnClick()
//                }
//                visibility = View.GONE
//            }.lparams(dip(160), dip(40)) {
//                topMargin = dip(30)
//            }
//        }
//    }
//
//    override fun footer(holder: ViewHolder) {
//        super.footer(holder)
//        holder.itemView.setOnClickListener {
//            if (!isConfig) {
//                addBtnClick()
//            }
//        }
//    }
//
//    override fun footerView(context: Context,parent: ViewGroup): View = with(context) {
//        relativeLayout {
//            imageView {
//                backgroundResource = R.mipmap.add_books
//            }.lparams(dip(105), dip(137)) {
//                topMargin = dip(5)
//                bottomMargin = dip(17)
//                centerHorizontally()
//            }
//        }
//    }
//
//    override fun setData(data: List<MyBookLesson>?) {
////        if (data?.isEmpty() == true) {
////            setFooter(false)
////        } else {
//        setFooter(true)
////        }
//        super.setData(data)
//    }
//
//}
//
//inline fun ViewManager.mImageView(): NiceImageView = mImageView() {}
//inline fun ViewManager.mImageView(init: (NiceImageView).() -> Unit): NiceImageView {
//    return ankoView({ NiceImageView(it) }, theme = 0) { init() }
//}