//package tuoyan.com.xinghuo_daying.ui.netLesson.overPackage
//
//import android.content.Context
//import android.graphics.Color
//import android.graphics.Typeface
//import android.os.Bundle
//import android.text.Html
//import android.text.TextUtils
//import android.view.Gravity
//import android.view.ViewGroup
//import android.view.ViewManager
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.shehuan.niv.NiceImageView
//import org.jetbrains.anko.*
//import org.jetbrains.anko.custom.ankoView
//import org.jetbrains.anko.recyclerview.v7.recyclerView
//import org.jetbrains.anko.support.v4.UI
//import org.jetbrains.anko.support.v4.startActivity
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
//import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
//import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
//import tuoyan.com.xinghuo_dayingindex.bean.LessonDetail
//import tuoyan.com.xinghuo_dayingindex.bean.TeacherListBean
//import tuoyan.com.xinghuo_dayingindex.ui.netLesson.myLesson.MineLessonActivity
//
///**
// * 过级包详情 网课fragment
// */
//class NetLessonFragment : LifeV4Fragment<OverPackagePresenter>() {
//    override val presenter = OverPackagePresenter(this)
//    override val layoutResId = 0
//    private val adapter by lazy {
//        NetLessonAdapter {
//            startActivity<MineLessonActivity>(MineLessonActivity.KEY to it.netcoursekey)
//        }
//    }
//
//    override fun initView() = UI {
//        recyclerView {
//            layoutManager = LinearLayoutManager(context)
//            adapter = this@NetLessonFragment.adapter
//        }
//    }.view
//
//    val detail by lazy { arguments?.getSerializable(DATA) as LessonDetail }
//
//    companion object {
//        val DATA = "data"
//        fun newInstance(content: LessonDetail): NetLessonFragment {
//            var f = NetLessonFragment()
//
//            var arg = Bundle()
//            arg.putSerializable(DATA, content)
//            f.arguments = arg
//            return f
//        }
//    }
//
//    override fun initData() {
//        val data = mutableListOf<LessonDetail>()
//        data.add(detail)
//        adapter.setData(data)
//    }
//}
//
//private class NetLessonAdapter(var click: (LessonDetail) -> Unit) : BaseAdapter<LessonDetail>() {
//    override fun convert(holder: ViewHolder, item: LessonDetail) {
//        holder.setText(R.id.tv_title, item.title)
//            .setText(R.id.tv_time, Html.fromHtml(getTimeStr(item)))
//            .setText(R.id.tv_validity_period, item.endTime)
//            .setImageUrl(R.id.iv_img, item.coverimg ?: "", R.drawable.default_lesson)
//
//        holder.itemView.setOnClickListener {
//            click(item)
//        }
//        item.teacherList?.let {
//            var list0 = mutableListOf<TeacherListBean>()
//            list0.addAll(it)
//            if (list0.size > 3) list0 = list0.subList(0, 3)
//            var llTeacher = holder.getView(R.id.ll_teacher) as LinearLayout
//            showTeacher(list0, llTeacher)
//        }
//    }
//
//    fun getTimeStr(item: LessonDetail) = when {
//        item.nextBeginTime.isNotEmpty() -> item.nextBeginTime + "开始直播"
//        item.liveState == "0" -> "<span style='color: #ffaf30'>未开播</span>"
//        item.liveState == "1" -> "<span style='color: #00ca0d'>直播中</span>"
//        item.liveState == "2" -> "<span style='color: #ffaf30'>已完结</span>"
//        else -> ""
//    }
//
//    fun showTeacher(list0: List<TeacherListBean>, llTeacher: LinearLayout) {
//        var teacherStr = ""
//        llTeacher.removeAllViews()
//        list0.forEach {
//            teacherStr += it.teacherName
//            var tvTeacher = TextView(mContext)
//            with(tvTeacher) {
//                text = it.teacherName
//                textColor = Color.parseColor("#ffffff")
//                textSize = 10f
//                gravity = Gravity.CENTER
//                leftPadding = dip(6)
//                rightPadding = dip(6)
//                topPadding = dip(2)
//                bottomPadding = dip(2)
//                backgroundResource = R.drawable.bg_teacher_name
//                var lp = LinearLayout.LayoutParams(wrapContent, wrapContent)
//                lp.rightMargin = dip(4)
//                layoutParams = lp
//            }
//            llTeacher.addView(tvTeacher)
//        }
//
//        if (teacherStr.length > 9 && list0.size > 1) {//TODO 若老师的姓名长度大于9，则显示老师数-1
//            var tList = list0.subList(0, list0.size - 1)
//            showTeacher(tList, llTeacher)
//        }
//    }
//
//    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
//        verticalLayout {
//            lparams(matchParent, wrapContent)
//            topPadding = dip(15)
//            linearLayout {
//                horizontalPadding = dip(15)
//                mImageView {
//                    id = R.id.iv_img
//                    scaleType = ImageView.ScaleType.CENTER_CROP
//                    setCornerTopLeftRadius(dip(2))
//                }.lparams(dip(141), dip(95))
//                space().lparams(dip(10), wrapContent)
//                verticalLayout {
//                    textView {
//                        id = R.id.tv_title
//                        textSize = 14f
//                        textColor = resources.getColor(R.color.color_222831)
//                        lines = 2
//                        setLineSpacing(dip(3).toFloat(), 1.0f)
//                        ellipsize = TextUtils.TruncateAt.END
//                        typeface = Typeface.DEFAULT_BOLD
//                    }.lparams(matchParent, wrapContent)
//
//                    space().lparams(wrapContent, dip(10))
//                    textView {
//                        id = R.id.tv_validity_period
//                        textSize = 10f
//                        textColor = resources.getColor(R.color.color_c3c7cb)
//                    }
//                    space().lparams(wrapContent, dip(8))
////                    textView {
////                        id = R.id.tv_name
////                        textSize = 10f
////                        gravity=Gravity.CENTER
////                        textColor = resources.getColor(R.color.color_ffffff)
////                        backgroundResource = R.drawable.bg_teacher_name
////                    }.lparams(dip(42), dip(16))
//                    linearLayout {
//                        id = R.id.ll_teacher
//                        orientation = LinearLayout.HORIZONTAL
//                    }.lparams(wrapContent, wrapContent) {
//                        topMargin = dip(8)
//                    }
//                }.lparams(matchParent, wrapContent)
//            }
//            space().lparams(wrapContent, dip(10))
//            linearLayout {
//                horizontalPadding = dip(15)
//                space().lparams(0, wrapContent, 1f)
//                gravity = Gravity.CENTER_VERTICAL
//                textView {
//                    id = R.id.tv_time
//                    textSize = 12f
//                    textColor = resources.getColor(R.color.color_ffaf30)
//                }
//                space().lparams(dip(15), wrapContent)
//                view {
//                    backgroundResource = R.color.color_edeff0
//                }.lparams(dip(0.5f), dip(17))
//                space().lparams(dip(10), wrapContent)
//                textView("进入学习") {
//                    id = R.id.tv_to_learn
//                    textSize = 12f
//                    gravity = Gravity.CENTER
//                    textColor = resources.getColor(R.color.color_4c84ff)
//                    backgroundResource = R.drawable.bg_shape_stroke_5_4c84ff
//                }.lparams(dip(60), dip(22))
//            }
//            space().lparams(wrapContent, dip(15))
//            view {
//                backgroundResource = R.color.color_edeff0
//            }.lparams(matchParent, dip(0.5f)) {
//                marginStart = dip(15)
//            }
//        }
//    }
//}
//
//inline fun ViewManager.mImageView(): NiceImageView = mImageView() {}
//inline fun ViewManager.mImageView(init: (NiceImageView).() -> Unit): NiceImageView {
//    return ankoView({ NiceImageView(it) }, theme = 0) { init() }
//}