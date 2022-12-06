package tuoyan.com.xinghuo_dayingindex.ui.mine.user

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.IME_ACTION_UNSPECIFIED
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_search_school.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.FeedbackQuestion
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil


class SearchSchoolActivity : LifeActivity<UserPresenter>() {
    override val presenter: UserPresenter
        get() = UserPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_search_school
    private val type by lazy { intent.getStringExtra(TYPE) ?: "" }
    private val schoolAdapter by lazy {
        SchoolAdapter() {
            intent.putExtra("item", it)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun configView() {
        super.configView()
        rlv_school.layoutManager = LinearLayoutManager(this)
        rlv_school.adapter = schoolAdapter
        if (SCHOOL == type) {
            schoolAdapter.setType(SCHOOL)
            edt_name.hint = "请输入院校名称"
        } else {
            schoolAdapter.setType(MAJOR)
            edt_name.hint = "请输入专业名称"
        }
    }

    override fun initData() {
        super.initData()
    }


    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        edt_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.isNullOrEmpty()) {
                    img_del.visibility = View.GONE
                } else {
                    img_del.visibility = View.VISIBLE
                    presenter.getDictInfo(type, edt_name.text.toString()) {
                        if (it.size == 1 && it[0].code == "0000") {
                            schoolAdapter.setHeader(true)
                        } else {
                            schoolAdapter.setHeader(false)
                        }
                        schoolAdapter.setData(it)
                    }
                }
            }

        })
        edt_name.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(textView: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
                if (actionId == IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    presenter.getDictInfo(type, edt_name.text.toString()) {
                        if (it.size == 1 && it[0].code == "0000") {
                            schoolAdapter.setHeader(true)
                        } else {
                            schoolAdapter.setHeader(false)
                        }
                        schoolAdapter.setData(it)
                    }
                    hideSoftKeyboard()
                    return true
                }
                return false
            }
        })
        img_del.setOnClickListener {
            edt_name.text.clear()
        }
    }

    fun hideSoftKeyboard() {
        val view = currentFocus
        if (view != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    companion object {
        const val TYPE = "type"
        const val SCHOOL = "SCHOOL"
        const val MAJOR = "MAJOR"
    }
}

class SchoolAdapter(val OnClick: (item: FeedbackQuestion) -> Unit) : BaseAdapter<FeedbackQuestion>(isFooter = true) {
    private var type = SearchSchoolActivity.SCHOOL
    fun setType(type: String) {
        this.type = type
    }

    override fun convert(holder: ViewHolder, item: FeedbackQuestion) {
        if (SearchSchoolActivity.MAJOR == type) {
            val imgSchool = holder.getView(R.id.img_school) as ImageView
            imgSchool.setImageResource(R.mipmap.icon_search_major)
        }
        holder.setText(R.id.tv_title, item.name)
        holder.itemView.setOnClickListener {
            OnClick(item)
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_school_item, parent, false)
    }

    override fun headerView(context: Context, parent: ViewGroup): View {
        val textView = TextView(context)
        val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 30f).toInt())
        params.setMargins(0, 0, 0, DeviceUtil.dp2px(context, 5f).toInt())
        textView.layoutParams = params
        textView.textColor = ContextCompat.getColor(context, R.color.color_ffaf00)
        textView.backgroundColor = ContextCompat.getColor(context, R.color.color_fffac8)
        if (SearchSchoolActivity.MAJOR == type) {
            textView.text = "未找到对应专业,请选择\"其他专业\""
        } else {
            textView.text = "未找到对应院校,请选择\"其他院校\""
        }
        textView.textSize = 12f
        textView.gravity = Gravity.CENTER
        return textView
    }

    override fun footerView(context: Context, parent: ViewGroup): View {
        val view = View(context)
        val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }

}