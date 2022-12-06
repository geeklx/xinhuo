package tuoyan.com.xinghuo_dayingindex.ui.mine.wrong

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_wrong.*
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.Grade
import tuoyan.com.xinghuo_dayingindex.ui.dialog.WrongDateAdapter
import tuoyan.com.xinghuo_dayingindex.ui.dialog.WrongDateDialog
import tuoyan.com.xinghuo_dayingindex.widegt.onLoadMoreListener

class WrongActivity : LifeActivity<WrongPresenter>() {
    override val presenter = WrongPresenter(this)
    override val layoutResId = R.layout.activity_wrong
    private var page = 0
    private var gradeKey = ""
    private var subGradeKey = ""
    private var data = ""
    private val adapter by lazy {
        WrongAdapter(more = {
            more()
        }) {
            // : 2018/10/29 14:06  错题
            var intent = Intent()
            intent.setClass(ctx, WrongDetailActivity::class.java)
            intent.putExtra(WrongDetailActivity.WRONG_ITEM, it)
            startActivityForResult(intent, 66)
        }
    }

    private fun more() {
        page++
        presenter.getWrongList(data, "", "", page) {
            adapter.addData(it.body)
//            adapter.setMore(adapter.getDateCount() < it.totalCount)
        }

    }

    val sectionAdapter by lazy {
        Adapter {
            // : 2018/10/29 13:45  学段
            subGradeKey = it.id
        }
    }

    private val popupWindow by lazy {
        val popupView = with(ctx) {
            verticalLayout {
                lparams(matchParent, wrapContent)
                backgroundResource = R.color.color_ffffff
                view { backgroundResource = R.color.color_f0f0f0 }.lparams(matchParent, dip(0.5f))
                recyclerView {
                    padding = dip(9)
                    layoutManager = androidx.recyclerview.widget.GridLayoutManager(ctx, 3)
                    overScrollMode = View.OVER_SCROLL_NEVER
                    adapter = sectionAdapter
                }
                view { backgroundResource = R.color.color_f0f0f0 }.lparams(matchParent, dip(0.5f))

                linearLayout {
                    textView("取消") {
                        gravity = Gravity.CENTER
                        textSize = 15f
                        textColor = resources.getColor(R.color.color_222831)
                        setOnClickListener {
                            cancel()
                        }
                    }.lparams(0, dip(42), 1f)
                    textView("确认") {
                        gravity = Gravity.CENTER
                        backgroundResource = R.color.color_4c84ff
                        textSize = 15f
                        textColor = resources.getColor(R.color.color_ffffff)
                        setOnClickListener {
                            confirm()
                        }
                    }.lparams(0, dip(42), 1f)
                }
            }
        }
        PopupWindow(popupView, matchParent, wrapContent).apply {
            setOnDismissListener {
                bg_view.visibility = View.GONE
                tv_all.isSelected = false
            }
        }
    }
    private val dateAdapter by lazy {
        WrongDateAdapter {
            data = it.time ?: ""
            var count = it.count
            tv_date.text = "$data($count 题)"
            selectedDate()
        }
    }
    private val dateDialog by lazy {
        WrongDateDialog(ctx, dateAdapter)
    }

    override fun configView() {
        recycler_view.layoutManager = LinearLayoutManager(ctx)
        val decoration = DividerItemDecoration(ctx, LinearLayout.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.drawable.divider))
        recycler_view.addItemDecoration(decoration)
        recycler_view.adapter = adapter
    }

    override fun initData() {
        presenter.getSectionAndGradeList {
            sectionAdapter.setData(it[0].resourceList)
        }

        presenter.getWrongDate { it ->
            dateAdapter.setData(it)
            if (it.isNotEmpty()) {
//                if (data.isBlank()){
                data = it[0].time ?: ""
                var count = it[0].count
                tv_date.text = "$data($count 题)"
//                }
                initWrong()
            } else {
                data = ""
                tv_date.text = ""
            }
        }
    }

    override fun handleEvent() {
        super.handleEvent()
        recycler_view.addOnScrollListener(object : onLoadMoreListener() {
            override fun onLoading(countItem: Int, lastItem: Int) {
                more()
            }
        })
    }

    private fun initWrong() {
        page = 1
        presenter.getWrongList(data, gradeKey, "", page) {
            adapter.setData(it.body)
            adapter.setMore(adapter.getDateCount() < it.totalCount)
        }
    }

    private fun selectedDate() {
        dateDialog.dismiss()
        initWrong()
    }

    private fun cancel() {
        popupWindow.dismiss()
    }

    private fun confirm() {
//        tv_date.text = currentDate
//        getWrongInDate()
        gradeKey = subGradeKey
        initWrong()
        popupWindow.dismiss()
    }

    @SensorsDataTrackViewOnClick
    fun date(v: View) {
        dateDialog.show()
    }

    @SensorsDataTrackViewOnClick
    fun all(v: View) {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()

        } else {
            bg_view.visibility = View.VISIBLE
            popupWindow.showAsDropDown(toolbar)
            v.isSelected = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 123) {
            initData()
        }
    }

    override fun onBackPressed() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (dateDialog.isShowing) dateDialog.dismiss()
        if (popupWindow.isShowing) popupWindow.dismiss()
        super.onDestroy()
    }


}

class Adapter(var onClick: (Grade) -> Unit) : BaseAdapter<Grade>() {
    var position = -1
    override fun convert(holder: ViewHolder, item: Grade) {
        holder.setText(R.id.tv_name, item.name)
        if (position == holder.layoutPosition) {
            holder.setBackgroundResource(R.id.tv_name, R.drawable.bg_shape_5_4c84ff_ffffff)
                .setTextColorRes(R.id.tv_name, R.color.color_4c84ff)
        } else {
            holder.setBackgroundResource(R.id.tv_name, R.drawable.bg_shape_5_edeff0_ffffff)
                .setTextColorRes(R.id.tv_name, R.color.color_8d95a1)
        }
        holder.itemView.setOnClickListener {
            if (position != holder.layoutPosition) {
                position = holder.layoutPosition
                notifyDataSetChanged()
                onClick(item)
            }
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View = with(context) {
        linearLayout {
            padding = dip(6)
            textView {
                id = R.id.tv_name
                gravity = Gravity.CENTER
                textSize = 13f
                textColor = resources.getColor(R.color.color_8d95a1)
                backgroundResource = R.drawable.bg_shape_5_edeff0_ffffff
            }.lparams(matchParent, dip(37))
        }
    }
}