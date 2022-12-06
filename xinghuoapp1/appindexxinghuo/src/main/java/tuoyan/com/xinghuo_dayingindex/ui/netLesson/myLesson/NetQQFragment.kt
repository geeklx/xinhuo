//package tuoyan.com.xinghuo_daying.ui.netLesson.myLesson
//
//import android.app.AlertDialog
//import android.content.ClipData
//import android.content.ClipboardManager
//import android.content.Context
//import android.content.Intent
//import android.graphics.Color
//import android.graphics.Typeface
//import android.net.Uri
//import android.os.Bundle
//import androidx.recyclerview.widget.LinearLayoutManager
//import android.view.Gravity
//import android.view.View
//import android.view.ViewGroup
//import android.widget.LinearLayout
//import org.jetbrains.anko.*
//import org.jetbrains.anko.recyclerview.v7.recyclerView
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
//import tuoyan.com.xinghuo_dayingindex.base.LifeFragment
//import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
//
//
///**
//
// */
//class NetQQFragment : LifeFragment<NetLessonsPresenter>() {
//    override val presenter = NetLessonsPresenter(this)
//    override val layoutResId = 0
//    private val adapter by lazy {
//        QQAdapter {
//            if (parentActivity.lessonDetail?.qqContent.isNullOrEmpty()) {
//                joinQQGroup(parentActivity.lessonDetail?.qqKey ?: "")
//            } else {
//                AlertDialog.Builder(ctx).setTitle(it)
//                    .setMessage("验证为：${parentActivity.lessonDetail?.qqContent}")
//                    .setPositiveButton("打开QQ") { _, _ ->
//                        //获取剪贴板管理器：
//                        val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                        // 创建普通字符型ClipData
//                        val mClipData =
//                            ClipData.newPlainText("Label", parentActivity.lessonDetail?.qqContent)
//                        // 将ClipData内容放到系统剪贴板里。
//                        cm.setPrimaryClip(mClipData)
//                        joinQQGroup(parentActivity.lessonDetail?.qqKey ?: "")
//                    }.setNegativeButton("取消") { _, _ ->
//
//                    }.create().show()
//
//            }
//        }
//    }
//
//    val parentActivity by lazy { activity as MineLessonActivity }
//    override fun initView() = UI {
//        recyclerView {
//            layoutManager = LinearLayoutManager(context)
//            adapter = this@NetQQFragment.adapter
//        }
//    }.view
//
//    override fun initData() {
//        try {
//            if (!parentActivity.lessonDetail?.qqNum.isNullOrEmpty()) {
//                val data = mutableListOf<Map<String, String>>()
//                data.add(
//                    mutableMapOf(
//                        "name" to "沟通学习获取学霸学习技巧",
//                        "num" to (parentActivity.lessonDetail?.qqNum ?: "")
//                    )
//                )
//                adapter.setData(data)
//                adapter.notifyDataSetChanged()
//            }
//        } catch (e: Exception) {
//        }
//    }
//
//    companion object {
//        fun instance(list: String) = NetTableFragment().apply {
//            arguments = Bundle().apply {
//                putString(DATA, list)
//            }
//        }
//
//        const val DATA = "data"
//
//    }
//
//    fun joinQQGroup(key: String): Boolean {
//        val intent = Intent()
//        intent.data =
//            Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
//        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        return try {
//            startActivity(intent)
//            true
//        } catch (e: Exception) {
//            // 未安装手Q或安装的版本不支持
//            toast("未安装手Q客户端")
//            false
//        }
//
//    }
//}
//
//private class QQAdapter(var onClick: (String) -> Unit) :
//    BaseAdapter<Map<String, String>>(isEmpty = true) {
//    override fun convert(holder: ViewHolder, item: Map<String, String>) {
//        holder.setText(R.id.tv_title, item["name"])
//            .setText(R.id.tv_qq, item["num"])
//        holder.itemView.setOnClickListener {
//            onClick(item["num"] ?: "")
//        }
//    }
//
//    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
//        verticalLayout {
//            lparams(matchParent, dip(61))
//            horizontalPadding = dip(15)
//            topPadding = dip(15)
//            textView {
//                id = R.id.tv_title
//                textSize = 14f
//                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_data_item_l, 0, 0, 0)
//                compoundDrawablePadding = dip(15)
//                textColor = resources.getColor(R.color.color_222831)
//                typeface = Typeface.DEFAULT_BOLD
////                gravity = Gravity.CENTER_VERTICAL
//            }
////            space().lparams(wrapContent, dip(5))
//            textView {
//                id = R.id.tv_qq
//                textSize = 11f
//                textColor = resources.getColor(R.color.color_c3c7cb)
//                leftPadding = dip(30)
//            }
//            space().lparams(wrapContent, 0, 1f)
//            view {
//                backgroundResource = R.color.color_edeff0
//            }.lparams(matchParent, dip(0.5f)) {
//                marginStart = dip(30)
//            }
//        }
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
//                text = "暂无QQ群~"
//                textSize = 15f
//                textColor = Color.parseColor("#666666")
//            }.lparams {
//                topMargin = dip(10)
//            }
//        }
//    }
//}