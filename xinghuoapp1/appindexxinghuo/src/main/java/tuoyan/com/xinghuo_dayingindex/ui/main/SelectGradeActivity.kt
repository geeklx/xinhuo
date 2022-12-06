package tuoyan.com.xinghuo_dayingindex.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_select_grade.*
import kotlinx.android.synthetic.main.layout_grade_item.view.*
import kotlinx.android.synthetic.main.layout_selected_grade_item.view.*
import org.jetbrains.anko.textColor
import tuoyan.com.xinghuo_dayingindex.GRAD_KEY_CET4_CET6
import tuoyan.com.xinghuo_dayingindex.GRAD_KEY_TEM4_TEM8
import tuoyan.com.xinghuo_dayingindex.GRAD_KEY_YAN
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.GradeBean
import java.util.*

class SelectGradeActivity : LifeActivity<BasePresenter>() {
    companion object {
        const val SEL_GRADE_LIST = "SEL_GRADE_LIST"
    }

    val gradeList by lazy { ArrayList<GradeBean>() }
    val selGradeList: ArrayList<GradeBean> by lazy { intent.getParcelableArrayListExtra(SEL_GRADE_LIST) ?: ArrayList<GradeBean>() }
    val adapter = GradeAdapter {
        var pos = gradeList.indexOf(it)
        gradeList[pos].isAdd = false
        normalAdapter.notifyItemChanged(pos)
    }

    val normalAdapter = GradeNormalAdapter(this@SelectGradeActivity) {
        updateSelAdapter(it)
    }

    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_select_grade

    fun updateSelAdapter(it: GradeBean) {
        selGradeList.add(it)
        adapter.notifyDataSetChanged()
    }

    override fun configView() {
        super.configView()
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        itemHelper.attachToRecyclerView(rlv_selected)
        rlv_selected.layoutManager = LinearLayoutManager(this)
        rlv_selected.adapter = adapter
        rlv_grade_normal.layoutManager = LinearLayoutManager(this)
        rlv_grade_normal.adapter = normalAdapter
    }

    override fun handleEvent() {
        super.handleEvent()
        tv_ok.setOnClickListener {
            var intent = Intent()
            intent.putExtra(SEL_GRADE_LIST, selGradeList)
            setResult(Activity.RESULT_OK, intent)
            onBackPressed()
        }
    }

    override fun initData() {
        super.initData()
        initGrade()
        adapter.setData(selGradeList)
        normalAdapter.setData(gradeList)
    }

    fun initGrade() {
        var item1 = GradeBean()
        item1.name = "四六级"
        item1.id = GRAD_KEY_CET4_CET6
        item1.key = "1"
        var item2 = GradeBean()
        item2.name = "考研"
        item2.id = GRAD_KEY_YAN
        item2.key = "2"
        var item3 = GradeBean()
        item3.name = "专四专八"
        item3.id = GRAD_KEY_TEM4_TEM8
        item3.key = "3"
        gradeList.add(item1)
        gradeList.add(item2)
        gradeList.add(item3)
        for ((index, item) in gradeList.withIndex()) {
            for ((selIndex, selItem) in selGradeList.withIndex()) {
                if (item.id == selItem.id) {
                    item.isAdd = true
                    break
                } else {
                    item.isAdd = false
                }

            }
        }
    }

    val itemHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN;
            val swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            if (viewHolder != null && target != null) {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                if (selGradeList.size >= toPos + 1) {
                    Collections.swap(selGradeList, fromPos, toPos)
                    adapter.notifyItemMoved(fromPos, toPos)
                    return true
                } else {
                    return false
                }
            }
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }
    })

    class GradeAdapter(val closeClick: (item: GradeBean) -> Unit) :
        RecyclerView.Adapter<GradeAdapter.ViewHolder>() {
        var mData = ArrayList<GradeBean>()

        fun setData(data: ArrayList<GradeBean>) {
            this.mData = data
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var v = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_selected_grade_item, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int = 3

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (mData.size >= position + 1) {
                var item = mData[position]
                holder.itemView.rl_sel.visibility = View.VISIBLE
                holder.itemView.rl_normal.visibility = View.GONE
                holder.itemView.tv_grade.text = item.name
                //size==1时，不显示img_close
                if (mData.size == 1) {
                    holder.itemView.img_close.visibility = View.GONE
                } else {
                    holder.itemView.img_close.visibility = View.VISIBLE
                }

                holder.itemView.img_close.setOnClickListener {
                    mData.removeAt(position)
                    notifyDataSetChanged()
                    closeClick(item)
                }
            } else {
                holder.itemView.rl_sel.visibility = View.GONE
                holder.itemView.rl_normal.visibility = View.VISIBLE
            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    class GradeNormalAdapter(val context: Context, val itemClick: (item: GradeBean) -> Unit) :
        RecyclerView.Adapter<GradeNormalAdapter.ViewHolder>() {
        var mData = ArrayList<GradeBean>()

        fun setData(data: ArrayList<GradeBean>) {
            this.mData = data
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var v = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_grade_item, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int = 3

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            var item = mData[position]
            holder.itemView.tv_grade_normal.text = item.name
            holder.itemView.setOnClickListener {
                if (!item.isAdd) {
                    item.isAdd = true
                    notifyItemChanged(position)
                    itemClick(item)
                }
            }

            if (item.isAdd) {
                holder.itemView.tv_grade_normal.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_grade_sel_item)
                holder.itemView.tv_grade_normal.textColor =
                    ContextCompat.getColor(context, R.color.white)
            } else {
                holder.itemView.tv_grade_normal.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_item)
                holder.itemView.tv_grade_normal.textColor =
                    ContextCompat.getColor(context, R.color.color_222831)
            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

}