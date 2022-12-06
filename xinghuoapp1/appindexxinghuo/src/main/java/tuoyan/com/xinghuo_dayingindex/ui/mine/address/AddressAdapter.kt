//package tuoyan.com.xinghuo_daying.ui.mine.address
//
//import android.content.Context
//import android.view.Gravity
//import android.view.ViewGroup
//import org.jetbrains.anko.*
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
//import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
//import tuoyan.com.xinghuo_dayingindex.bean.Address
//
///**
// * 创建者：
// * 时间：  2018/10/9.
// */
//class AddressAdapter(val click: (Address) -> Unit) : BaseAdapter<Address>(isEmpty = true) {
//    override fun convert(holder: ViewHolder, item: Address) {
//        var substring: String? = null
//        try {
//            substring = item.receiver?.substring(0, 1)
//        } catch (e: Exception) {
//        }
//        val default = if (item.isDefault == "1") "【默认】" else ""
//        holder.setText(R.id.tv_name, item.receiver ?: "")
//            .setText(R.id.tv_phone, item.receiverMobile ?: "")
//            .setText(R.id.tv_xing, substring ?: "")
//            .setText(
//                R.id.tv_address, "$default${item.fullName ?: ""}${item.detailAddress
//                    ?: ""}"
//            )
//            .setOnClickListener(R.id.tv_edit) {
//                click(item)
//            }
//    }
//
//    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
//        linearLayout {
//            lparams(matchParent, dip(85))
//            gravity = Gravity.CENTER_VERTICAL
//            textView {
//                id = R.id.tv_xing
//                textSize = 15f
//                gravity = Gravity.CENTER
//                textColor = resources.getColor(R.color.color_ffffff)
//                backgroundResource = R.drawable.bg_address_xing
//            }.lparams(dip(32), dip(32)) {
//                marginStart = dip(15)
//                marginEnd = dip(10)
//            }
//            verticalLayout {
//                linearLayout {
//                    gravity = Gravity.CENTER_VERTICAL
//                    textView {
//                        id = R.id.tv_name
//                        textSize = 15f
//                        textColor = resources.getColor(R.color.color_222831)
//                    }
//                    space().lparams(dip(14), wrapContent)
//                    textView {
//                        id = R.id.tv_phone
//                        textSize = 12f
//                        textColor = resources.getColor(R.color.color_c3c7cb)
//                    }
//                }
//                textView {
//                    id = R.id.tv_address
//                    textSize = 13f
//                    textColor = resources.getColor(R.color.color_222831)
//                }
//            }.lparams(0, wrapContent, 1f)
//            view { backgroundResource = R.drawable.divider }.lparams(dip(1), dip(24))
//            textView("编辑") {
//                id = R.id.tv_edit
//                textSize = 13f
//                textColor = resources.getColor(R.color.color_8d95a1)
//                gravity = Gravity.CENTER
//            }.lparams(dip(50), wrapContent)
//        }
//    }
//
//    override fun emptyImageRes(): Int {
//        return R.drawable.ic_emtry_address
//    }
//
//    override fun emptyText(): String {
//        return "暂无收货地址哦"
//    }
//}