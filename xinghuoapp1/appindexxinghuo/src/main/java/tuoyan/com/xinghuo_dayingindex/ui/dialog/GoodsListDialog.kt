package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.layout_goods_list_dialog.*
import kotlinx.android.synthetic.main.layout_googdslist_item.view.*
import org.jetbrains.anko.matchParent
import com.bumptech.glide.Glide
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.LiveShoppingList
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil

/**
 * 创建者： huoshulei
 * 时间：  2017/5/13.
 */
class GoodsListDialog(
    context: Context,
    val click: (item: LiveShoppingList, liveManagementKey: String) -> Unit,
    val clickDiscount: () -> Unit
) : Dialog(context, R.style.custom_dialog) {
    private var msg: String = ""
    private var liveManagementKey: String = ""
    private val adapter by lazy {
        Adapter() {
            click(it, liveManagementKey)
//            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_goods_list_dialog)
        val ll_discount = findViewById<LinearLayout>(R.id.ll_discount)
        ll_discount.setOnClickListener({
            clickDiscount()
//            dismiss()
        })

        val rlv_goods_list = findViewById<RecyclerView>(R.id.rlv_goods_list)
        rlv_goods_list.layoutManager = LinearLayoutManager(context)

        rlv_goods_list.adapter = adapter
        tv_content?.text = msg;

    }

    fun setData(data: List<LiveShoppingList>, message: String, liveManagementKey: String) {
        adapter.setData(data)
        msg = message
        this.liveManagementKey = liveManagementKey
        tv_content?.text = msg
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
//        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 9 //设置宽度
        lp?.width = matchParent
        window?.attributes = lp
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.BOTTOM)
    }

    class Adapter(val itemClick: (item: LiveShoppingList) -> Unit) :
        BaseAdapter<LiveShoppingList>(isEmpty = true, isFooter = true) {
        override fun convert(holder: ViewHolder, item: LiveShoppingList) {
//            holder.setText(R.id.tv_content, item.name)
//            holder.itemView.setOnClickListener {
//                itemClick(item)
//            }
            holder.setText(R.id.tv_num, item.sort)
                .setText(R.id.tv_name, item.title)
                .setText(R.id.tv_price, item.disprice)
                .setText(R.id.tv_go, if (item.isOwn == "0") "马上抢" else "已购买")
                .setBackgroundResource(
                    R.id.tv_go,
                    if (item.isOwn == "0") R.drawable.shape_gradient_ff8824_ff4e34 else R.drawable.shape_16_c7c7c7
                )
            var iv_img = holder.getView(R.id.iv_img) as ImageView
            Glide.with(holder.itemView.context).load(item.img).into(iv_img)
            holder.itemView.tv_go.setOnClickListener {
                itemClick(item)
            }
        }

        override fun convertView(context: Context, parent: ViewGroup): View {
            return LayoutInflater.from(context).inflate(R.layout.layout_googdslist_item, null)
        }

        override fun emptyText(): String {
            return "暂无商品"
        }

        override fun footerView(context: Context, parent: ViewGroup): View {
            val view = View(context)
            val params = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                DeviceUtil.dp2px(context, 60f).toInt()
            )
            view.layoutParams = params
            return view
        }
    }
}