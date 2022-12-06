package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.WrongBookDate

class WrongDateDialog(context: Context, var mAdapter: WrongDateAdapter) : Dialog(context, R.style.custom_dialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(with(context) {
            verticalLayout {
                backgroundResource = R.drawable.shape_corner_white
                relativeLayout {
                    textView {
                        text = "请选择时间"
                        textColor = Color.parseColor("#1e1e1e")
                        textSize = 14f
                        gravity = Gravity.CENTER
                    }.lparams(matchParent, dip(45))

                    imageView {
                        imageResource = R.mipmap.ic_close
                        setOnClickListener {
                            dismiss()
                        }
                        padding = dip(17)
                    }.lparams(dip(45), dip(45)) {
                        alignParentRight()
                        gravity = Gravity.CENTER
                    }
                }

                view {
                    backgroundColor = Color.parseColor("#e6e6e6")
                }.lparams(matchParent, 2)
                recyclerView {
                    layoutManager = LinearLayoutManager(context)
                    val decoration = DividerItemDecoration(ctx, LinearLayout.VERTICAL)
                    decoration.setDrawable(resources.getDrawable(R.drawable.divider))
                    addItemDecoration(decoration)
                    adapter = mAdapter
                }.lparams(matchParent, matchParent) {
                    bottomMargin = dip(20)
                }
            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 9 //设置宽度
        lp?.height = context.resources.displayMetrics.heightPixels / 10 * 6 //设置高度
//        lp.width=WindowManager.LayoutParams.MATCH_PARENT;
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.CENTER)
    }
}

class WrongDateAdapter(val str: String = "题", var click: (WrongBookDate) -> Unit) : BaseAdapter<WrongBookDate>() {
    override fun convert(holder: ViewHolder, item: WrongBookDate) {
        holder.setText(R.id.tv_date, "${item.time ?: ""}(${item.count}$str)")
                .itemView.setOnClickListener {
            click(item)
        }
    }

    override fun convertView(context: Context,parent: ViewGroup): View = with(context) {
        verticalLayout {
            textView {
                id = R.id.tv_date
//                setCompoundDrawables(resources.getDrawable(R.drawable.ic_wrongbook),null,resources.getDrawable(R.mipmap.ic_navigation_right),null)
                textSize = 12f
                horizontalPadding = dip(15)
                textColor = resources.getColor(R.color.color_333333)
                gravity = Gravity.CENTER_VERTICAL
            }.lparams(matchParent, dip(45))
        }
    }
}