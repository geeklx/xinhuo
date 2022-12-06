//package tuoyan.com.xinghuo_daying.ui.practice.composition
//
//import android.Manifest
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.graphics.Typeface
//import android.support.v7.widget.GridLayoutManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import android.util.Log
//import android.view.Gravity
//import android.view.View
//import android.view.ViewGroup
//import android.widget.LinearLayout
//import android.widget.TextView
//import com.google.gson.Gson
//import kotlinx.android.synthetic.main.activity_composition_detail.*
//import org.jetbrains.anko.*
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
//import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
//import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
//import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
//import tuoyan.com.xinghuo_dayingindex.bean.LevelListBean
//import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo
//import tuoyan.com.xinghuo_dayingindex.ui.dialog.SelectedDialog
//import tuoyan.com.xinghuo_dayingindex.ui.exercise.detail.ExerciseDetailKActivity
//import tuoyan.com.xinghuo_dayingindex.ui.practice.TeacherModifyDetailActivity
//import tuoyan.com.xinghuo_dayingindex.utlis.ImageSeletedUtil
//import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
////未使用
//class CompositionDetailActivity : LifeActivity<BasePresenter>() {
//    override val presenter: BasePresenter
//        get() = BasePresenter(this)
//    override val layoutResId: Int
//        get() = R.layout.activity_composition_detail
//    var dataList = mutableListOf<LevelListBean>()
//    private var subCount = 0 //TODO 评分标准数量，判断当前选择的评分标准是否完整
//    private var keyMap = mutableMapOf<String, LevelListBean>()
//    private val practisekey by lazy { intent.getStringExtra(PRACTISE_KEY) }
//    private val evalKey by lazy { intent.getStringExtra(EVAL_KEY) }
//
//    companion object {
//        val TITLE = "title"
//        val PRACTISE_KEY = "practisekey"
//        val EVAL_KEY = "evalKey"
//    }
//
//    private val commentAdapter by lazy {
//        TranslationAdapter {
//            this.keyMap = it as MutableMap<String, LevelListBean>
//        }
//    }
//    val detailTitle by lazy { intent.getStringExtra(TITLE) }
//
//    override fun configView() {
//        super.configView()
//        setSupportActionBar(tb_composition_detail)
//        tb_composition_detail.setNavigationOnClickListener { onBackPressed() }
//        tv_title.text = detailTitle
//        showByType("3")
//    }
//
//    override fun handleEvent() {
//        super.handleEvent()
//        tv_correct_teach.setOnClickListener {
//            var intent = Intent(this, TeacherModifyDetailActivity::class.java)
//            intent.putExtra(TeacherModifyDetailActivity.QUESTION_KEY, "758232590145803905")
//            startActivityForResult(intent, 999)
//        }
//
//        tv_correct_self.setOnClickListener {
//            showByType("1")
//        }
//    }
//
//    override fun initData() {
//        super.initData()
//        presenter.getExerciseFrame(practisekey, "0", evalKey) {
//        }
//    }
//
//    private val adapter by lazy {
//        EditAdapter {
//            PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
//                selectDialog.show()
//            }
//        }
//    }
//
//    val selectDialog by lazy {
//        //TODO 主观题上传图片
//        SelectedDialog("拍照", "从相册选择", this) {
//            ImageSeletedUtil.phoneClick(it, this, false)
//        }
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
//            showByType("2")
//        } else {
//            //TODO 选择图片后返回
//            ImageSeletedUtil.onActivityResult(this, requestCode, resultCode, data) {
//                it?.let {
//                    addImg(it)
//                }
//            }
//        }
//    }
//
//    fun showByType(type: String) {
//        when (type) {
//            //未作，正常进来
//            "0" -> {
//                ll_correct.visibility = View.VISIBLE
//                rl_self_teach.visibility = View.VISIBLE
//                tv_correct.visibility = View.GONE
//                ll_pic.visibility = View.VISIBLE // 图片上传
//                rl_bottom.visibility = View.VISIBLE //底部提交答案
//                ll_detail.visibility = View.GONE //范文
//                rlv_comment.visibility = View.GONE //自评显示
//            }
//            //已选自评
//            "1" -> {
//                rlv_comment.layoutManager = LinearLayoutManager(this)
//                rlv_comment.adapter = commentAdapter
//                rlv_comment.visibility = View.VISIBLE //自评显示
//                rl_bottom.visibility = View.VISIBLE //底部提交答案
//                ll_pic.visibility = View.GONE // 图片上传
//                ll_correct.visibility = View.GONE
//                rl_self_teach.visibility = View.GONE
//                tv_correct.visibility = View.GONE
//                ll_detail.visibility = View.GONE //范文
//                var infoJson = "{\"subiList\":[{\"levelList\":[{\"levelComment\":\"整体结构条理清晰，层次分明。\",\"levelKey\":\"629031581515474187\",\"levelName\":\"A\",\"levelPoint\":\"23.0\"},{\"levelComment\":\"整体结构较清楚，但个别地方语句的连贯不佳。\",\"levelKey\":\"629031581515474189\",\"levelName\":\"B\",\"levelPoint\":\"19.0\"},{\"levelComment\":\"整体结构混乱，条理不清，思路紊乱。\",\"levelKey\":\"629031581515474191\",\"levelName\":\"C\",\"levelPoint\":\"7.0\"}],\"name\":\"结构\",\"key\":\"629031581515474184\"},{\"levelList\":[{\"levelComment\":\"语法正确，句子结构及衔接手段较为出彩。基本上无语言错误，有较多的亮点词汇及短语。\",\"levelKey\":\"629031581515474195\",\"levelName\":\"A\",\"levelPoint\":\"35.0\"},{\"levelComment\":\"语法结构基本准确，但句式较为单一。有少量语言错误，亮点句式不多。\",\"levelKey\":\"629031581515474197\",\"levelName\":\"B\",\"levelPoint\":\"28.0\"},{\"levelComment\":\"语法结构单调，且出现了较多的语法错误。语言支离破碎，大部分单词均有错误。\",\"levelKey\":\"629031581515474199\",\"levelName\":\"C\",\"levelPoint\":\"14.0\"}],\"name\":\"语法\",\"key\":\"629031581515474192\"},{\"levelList\":[{\"levelComment\":\"运用正确的、符合英语表达习惯的写作格式。\",\"levelKey\":\"629031581515474203\",\"levelName\":\"A\",\"levelPoint\":\"20.0\"},{\"levelComment\":\"运用基本正确，但有个别小错。\",\"levelKey\":\"629031581515474205\",\"levelName\":\"B\",\"levelPoint\":\"18.0\"},{\"levelComment\":\"写作格式基本不符合英语表达习惯。\",\"levelKey\":\"629031581515474207\",\"levelName\":\"C\",\"levelPoint\":\"7.0\"}],\"name\":\"格式\",\"key\":\"629031581515474200\"},{\"levelList\":[{\"levelComment\":\"卷面干净整洁，书写规范。\",\"levelKey\":\"629031581515474211\",\"levelName\":\"A\",\"levelPoint\":\"14.0\"},{\"levelComment\":\"卷面整体整洁，但书写有待进一步提高\",\"levelKey\":\"629031581515474213\",\"levelName\":\"B\",\"levelPoint\":\"10.0\"},{\"levelComment\":\"卷面潦草，影响阅读。\",\"levelKey\":\"629031581515474215\",\"levelName\":\"C\",\"levelPoint\":\"5.0\"}],\"name\":\"卷面\",\"key\":\"629031581515474208\"},{\"levelList\":[{\"levelComment\":\"围绕题目紧扣论点，立意明确。\",\"levelKey\":\"629031581515474219\",\"levelName\":\"A\",\"levelPoint\":\"14.0\"},{\"levelComment\":\"作文切题，表意较清楚。\",\"levelKey\":\"629031581515474221\",\"levelName\":\"B\",\"levelPoint\":\"10.0\"},{\"levelComment\":\"跑题，作文主题与题目要求不符。\",\"levelKey\":\"629031581515474223\",\"levelName\":\"C\",\"levelPoint\":\"5.0\"}],\"name\":\"切题性\",\"key\":\"629031581515474216\"}],\"isOwn\":\"0\",\"item\":{\"questionSubject\":\"\\u003cp\\u003e星火英语测评测试-线下扫码\\u003c/p\\u003e\",\"appQuestionKey\":\"758232590145803905\",\"questionAnalysis\":\"\\u003cp\\u003e2222\\u003c/p\\u003e\"},\"audioFile\":\"\",\"isFree\":\"0\",\"resourceKey\":\"\",\"sort\":\"1\",\"questionType\":\"6\",\"stem\":\"\\u003cp\\u003e星火英语测评测试-线下扫码\\u003c/p\\u003e\"}"
//                //                var infoJson = Gson().
//                var questionInfo = Gson().fromJson(infoJson, QuestionInfo::class.java)
//                var subList = questionInfo.subiList
//                subCount = subList?.size ?: 0
//
//                subList?.forEach {
//                    var sub = it
//                    var nodeLevel = LevelListBean("NODE")
//                    nodeLevel.subName = it.name
//                    nodeLevel.subKey = it.key
//                    dataList.add(nodeLevel) //TODO 添加一个节点数据
//                    it.levelList?.forEach {
//                        it.type = "DATA"
//                        it.subName = sub.name
//                        it.subKey = sub.key
//                        dataList.add(it)
//                    }
//                }
//                commentAdapter.setData(dataList)
//            }
//            //已选人工
//            "2" -> {
//                rlv_img.layoutManager = GridLayoutManager(this, 3)
//                rlv_img.adapter = adapter
//                ll_pic.visibility = View.VISIBLE // 图片上传
//                rl_bottom.visibility = View.VISIBLE //底部提交答案
//                ll_correct.visibility = View.GONE
//                rl_self_teach.visibility = View.GONE
//                tv_correct.visibility = View.GONE
//                ll_detail.visibility = View.GONE //范文
//                rlv_comment.visibility = View.GONE //自评显示
//            }
//            //批改中
//            "3" -> {
//                tv_correct.visibility = View.VISIBLE
//                ll_correct.visibility = View.VISIBLE
//                rl_self_teach.visibility = View.GONE
//                ll_pic.visibility = View.GONE // 图片上传
//                rl_bottom.visibility = View.GONE //底部提交答案
//                ll_detail.visibility = View.GONE //范文
//                rlv_comment.visibility = View.GONE //自评显示
//            }
//        }
//    }
//
//    fun addImg(path: String) {
//        adapter.getData().add(path)
//        adapter.setFooter(adapter.getData().size < 3)
//        adapter.notifyDataSetChanged()
//    }
//}
//
///*
//*人工上传图片
//* */
//private class EditAdapter(var addClick: () -> Unit) : BaseAdapter<String>(isFooter = true) {
//    private var currentView: View? = null
//    override fun convert(holder: ViewHolder, item: String) {
//        holder.setImageUrl(R.id.iv_img, item)
//                .setOnClickListener(R.id.iv_close) {
//                    remove(holder.layoutPosition)
//                    setFooter(getDateCount() < 3)
//                }
//    }
//
//    override fun footer(holder: ViewHolder) {
//        holder.setOnClickListener(R.id.iv_img) {
//            addClick()
//        }
//
//    }
//
//    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
//        frameLayout {
//            lparams(dip(100), dip(100))
//            imageView {
//                id = R.id.iv_img
//            }.lparams(matchParent, matchParent)
//            imageView(R.mipmap.ic_close) {
//                id = R.id.iv_close
//                padding = dip(3)
//            }.lparams {
//                gravity = Gravity.END
//            }
//        }
//    }
//
//    override fun footerView(context: Context) = with(context) {
//        frameLayout {
//            lparams(dip(100), dip(100))
//            imageView(R.drawable.ic_over_up_lmg) {
//                id = R.id.iv_img
//            }.lparams(matchParent, matchParent)
//        }
//    }
//}
//
///*
//* 自评选项
//* */
//private class TranslationAdapter(var onClick: (Map<String, LevelListBean>) -> Unit) : BaseAdapter<LevelListBean>() {
//    private var viewMap = mutableMapOf<String, View>()
//    private var keyMap = mutableMapOf<String, LevelListBean>()
//    override fun convert(holder: ViewHolder, item: LevelListBean) {
//        holder.setIsRecyclable(false)
//        holder.setText(R.id.tv_title, item.levelName)
//                .setText(R.id.tv_des, item.levelComment)
//                .itemView.setOnClickListener {
//            viewMap[item.subKey]?.backgroundResource = R.drawable.bg_shape_5_c3c7cb_ffffff
//            it.backgroundResource = R.drawable.bg_shape_5_4c84ff_ffffff
//            viewMap[item.subKey] = it
//            keyMap[item.subKey] = item
//            onClick(keyMap)
//        }
//        keyMap[item.subKey]?.let {
//            if (it.levelKey == item.levelKey) {
//                holder.itemView.setBackgroundResource(R.drawable.bg_shape_5_4c84ff_ffffff)
//                viewMap[item.subKey] = holder.itemView
//            }
//        }
//    }
//
//    private fun subConvert(holder: ViewHolder, bean: LevelListBean) {
//        if (holder.itemView is TextView)
//            holder.itemView.text = bean.subName
//    }
//
//    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
//        linearLayout {
//            lparams(matchParent, wrapContent) {
//                bottomMargin = dip(10)
//            }
//            orientation = LinearLayout.HORIZONTAL
//            weightSum = 7f
//            backgroundResource = R.drawable.bg_shape_5_c3c7cb_ffffff
//            horizontalPadding = dip(12)
//            verticalPadding = dip(15)
//            gravity = Gravity.CENTER_VERTICAL
//            textView {
//                id = R.id.tv_title
//                textSize = 14f
//                textColor = resources.getColor(R.color.color_4c84ff)
//                gravity = Gravity.CENTER
//                maxEms = 6
//            }.lparams(0, wrapContent) {
//                weight = 1f
//            }
//            space().lparams(dip(15), wrapContent)
//            textView {
//                id = R.id.tv_des
//                textSize = 14f
//                setLineSpacing(dip(3).toFloat(), 1f)
//                textColor = resources.getColor(R.color.color_8d95a1)
//            }.lparams(0, wrapContent) {
//                weight = 6f
//            }
//        }
//    }
//
//    private fun subView(parent: ViewGroup) = TextView(parent.context).apply {
//        id = R.id.tv_tab_title
//        textSize = 12f
//        textColor = resources.getColor(R.color.color_222)
//        typeface = Typeface.DEFAULT_BOLD
//        topPadding = dip(10)
//        bottomPadding = dip(15)
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        if (position > getDateCount() - 1) return super.getItemViewType(position)
//        if (getData()[position].type == "NODE") return SUB
//        return super.getItemViewType(position)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        if (mContext == null) mContext = parent.context
//        return if (viewType == SUB) ViewHolder(subView(parent))
//        else super.onCreateViewHolder(parent, viewType)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        if (holder.itemViewType == SUB) subConvert(holder, getData()[position - if (getIsHeader()) 1 else 0])
//        else super.onBindViewHolder(holder, position)
//    }
//
//    companion object {
//        const val SUB = 0x15d4
//    }
//}