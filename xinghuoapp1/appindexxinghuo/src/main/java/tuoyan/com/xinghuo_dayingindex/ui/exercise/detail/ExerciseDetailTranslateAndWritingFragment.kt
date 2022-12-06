package tuoyan.com.xinghuo_dayingindex.ui.exercise.detail

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.fragment_exercise_translation_writing.*
import org.jetbrains.anko.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.TYPE_TEST
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.*
import tuoyan.com.xinghuo_dayingindex.ui.practice.TeacherModifyDetailActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
import tuoyan.com.xinghuo_dayingindex.utlis.PermissionUtlis
import java.io.File

/**
 * 做题模块转原生
 *对应ExerciseDetailFragment
 */
class ExerciseDetailTranslateAndWritingFragment : LifeV4Fragment<ExerciseDetailPresenter>() {
    override val presenter: ExerciseDetailPresenter
        get() = ExerciseDetailPresenter(this)
    override val layoutResId: Int
        get() = R.layout.fragment_exercise_translation_writing
    var qIndex = 0//当前小节位置
    var answerIndex = 0 //当前小题在当前小节的位置
    var qSort = ""//当前小题维护的题号
    var questionKey = ""//当前小题维护的题号
    private var isCollected = ""//是否收藏当前小节
    val itemData by lazy { arguments?.getSerializable(ITEM) as? ExerciseFrameItem }////body->questionlist->questionlist
    private val parentActivity by lazy { activity as ExerciseDetailKActivity }
    private val submitLevels by lazy { HashMap<String, AnswerItem>() }
    var haveDown = false//当前题是否做了
    private val scoredAdapter by lazy {
        ScoreAdapter() { bean, key ->
            val item = AnswerItem(key, bean.levelKey, bean.levelPoint)
            submitLevels[key] = item
        }
    }
    private val imgAdapter by lazy {
        ImageAdapter() {
            PermissionUtlis.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                parentActivity.selectDialog.show()
            }
        }
    }

    val imgPaths by lazy { ArrayList<File>() }
//    val imgPaths by lazy { ArrayList<CorrectBean>() }

    override fun initData() {
//        arguments?.let {
//            itemData = it.getSerializable(ITEM) as ExerciseFrameItem
        itemData!!.let { item ->
            qSort = item.questionInfo?.sort ?: ""
            questionKey = item.questionInfo?.questionKey ?: ""
            //根据不同的状态展示不同的内容 ，稍等修改
            isCollected = item.isCollected!!
            web_view.loadDataWithBaseURL(null, format(item.questionInfo?.stem ?: ""), "text/html", "utf-8", null);
            initEvalMode()
        }
//    }
    }

    override fun handleEvent() {
        super.handleEvent()
        tv_watch_analysis.setOnClickListener {
            if (tv_analysis.visibility == View.VISIBLE) {
                tv_analysis.visibility = View.GONE
                tv_watch_analysis.text = "查看解析"
            } else {
                tv_analysis.visibility = View.VISIBLE
                tv_watch_analysis.text = "解析"
            }
        }
        ctl_right.setOnClickListener {
            ctl_left.isSelected = false
            ctl_right.isSelected = true
            initAnswer("1")
            parentActivity.goNext()
        }
        ctl_left.setOnClickListener {
            ctl_right.isSelected = false
            ctl_left.isSelected = true
            initAnswer("0")
            parentActivity.goNext()
        }
        tv_self.setOnClickListener {
            imgOrSelf("0")
            scoreSelfTest(true)
        }
        tv_teacher.setOnClickListener {
//            evalMode    0自判 ,1免费人工, 2付费人工, 10自判+免费人工,20自判+付费人工
            if ("2" == itemData?.questionInfo?.evalMode || "20" == itemData?.questionInfo?.evalMode) {
                val intent = Intent(parentActivity, TeacherModifyDetailActivity::class.java)
                intent.putExtra(TeacherModifyDetailActivity.QUESTION_KEY, itemData?.questionKey)
                intent.putExtra(TeacherModifyDetailActivity.EVAL_KEY, parentActivity.catKey)
                intent.putExtra(TeacherModifyDetailActivity.NAME, parentActivity.paperName)
                parentActivity.startActivityForResult(intent, 999)
            } else {
                imgOrSelf("0")
                imgTest(true)
            }
        }
        tv_self_submit.setOnClickListener {
            if (rlv_score.visibility == View.VISIBLE) {
                if (submitLevels.values.size < itemData?.questionInfo?.subiList?.size ?: 0) {
                    Toast.makeText(this.requireActivity(), "请将评分标准填写完整", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                kotlin.run breaking@{
                    parentActivity.answerList.forEach {
                        it.qList.forEach { qItem ->
                            if (qItem.questionKey == itemData?.questionInfo?.questionKey!!) {
                                qItem.haveDone = true
                                qItem.answers.clear()
                                qItem.answers.addAll(submitLevels.values)
                                return@breaking
                            }
                        }
                    }
                }
            } else if (ctl_upload.visibility == View.VISIBLE) {
                if (imgPaths.size <= 0) {
                    Toast.makeText(parentActivity, "请正确作答", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                kotlin.run breaking@{
                    parentActivity.answerList.forEach {
                        it.qList.forEach { qItem ->
                            if (qItem.questionKey == itemData?.questionInfo?.questionKey!!) {
                                qItem.haveDone = true
                                qItem.answers.clear()
                                parentActivity.saPaperAnswer(qItem)
                                return@breaking
                            }
                        }
                    }
                }
            }
            parentActivity.isDone = true
            haveDown = true
            parentActivity.goNext()
        }
    }

    private fun initAnswer(right: String) {
        parentActivity.isDone = true
        haveDown = true
        // 主观题普通做题 1:我答对了；0：我打错了;order:多选时答案的序号从1开始
        val answers = mutableListOf<AnswerItem>()
        val answerItem = AnswerItem("1", right, "")
        answers.add(answerItem)
        // 答案单选多选问题answerQ.answers=
        // 图片作答answerQ.imgs
        //  answerQ.answerType
        // answerQ.answerText
        parentActivity.answerList.forEach {
            it.qList.forEach { qItem ->
                if (qItem.questionKey == itemData?.questionInfo?.questionKey!!) {
                    qItem.haveDone = true
                    qItem.answers.clear()
                    qItem.answers.addAll(answers)
                    parentActivity.saPaperAnswer(qItem)
                    return
                }
            }
        }
    }

    //是否收藏当前小节
    fun getCollection(): String {
        return isCollected
    }

    fun setCollection(isCollected: String) {
        this.isCollected = isCollected
    }

    /**当前evalMode默认值
     * questionInfo isOwn 0:未购买；1：已购买 主观题已购买 人工阅卷
     * evalMode    0自判 ,1免费人工, 2付费人工, 10自判+免费人工,20自判+付费人工
     * isOwn==1 显示人工
     * isOwn==0 ->evalMode==10 显示自判和免费人工
     *          ->evalMode==20 显示自判和付费人工
     *          ->evalMode==1  1免费人工,显示图片
     *          ->evalMode==2  显示人工，选择选项 只显示人工图标
     *          ->evalMode==0  显示自判标准
     */
    fun initEvalMode() {
        if (parentActivity.typeName == TYPE_TEST && "1" == itemData?.questionInfo?.isOwn) {
            imgTest(true)
        } else if (parentActivity.typeName == TYPE_TEST && "0" == itemData?.questionInfo?.isOwn && ("10" == itemData?.questionInfo?.evalMode || "20" == itemData?.questionInfo?.evalMode)) {
//                    ordinaryTest(false)
//                    scoreSelfTest(false)
//                    imgTest(false)
            imgOrSelf("2")
        } else if (parentActivity.typeName == TYPE_TEST && "0" == itemData?.questionInfo?.isOwn && "1" == itemData?.questionInfo?.evalMode) {
//                    ordinaryTest(false)
//                    scoreSelfTest(false)
//                    imgOrSelf("0")
            imgTest(true)
        } else if (parentActivity.typeName == TYPE_TEST && "0" == itemData?.questionInfo?.isOwn && "2" == itemData?.questionInfo?.evalMode) {
//                    ordinaryTest(false)
//                    scoreSelfTest(false)
//                    imgTest(false)
            imgOrSelf("1")
        } else if (parentActivity.typeName == TYPE_TEST) {
//            && "0" == itemData?.questionInfo?.isOwn && "0" == itemData?.questionInfo?.evalMode
//                    imgOrSelf("0")
//                    imgTest(false)
//                    scoreSelfTest(false)
            scoreSelfTest(true)
        } else {
//            imgOrSelf("0")
//            imgTest(false)
//            scoreSelfTest(false)
            ordinaryTest(true)
        }
    }

    override fun configView(view: View?) {
        super.configView(view)
        rlv_score.layoutManager = LinearLayoutManager(this.requireActivity())
        rlv_score.isNestedScrollingEnabled = false
        rlv_score.adapter = scoredAdapter
        rlv_img.layoutManager = GridLayoutManager(this.requireActivity(), 3)
        rlv_img.adapter = imgAdapter
    }

    /**
     * 普通试卷展示  我打对了 我打错了
     */
    fun ordinaryTest(isShow: Boolean) {
        if (isShow) {
            itemData?.questionInfo?.item?.let { infoItem ->
                val analysisItem = infoItem as LinkedTreeMap<String, String>
                tv_analysis.text = Html.fromHtml(analysisItem["questionAnalysis"])
            }
//            itemData?.questionInfo?.useranswer?.let { useranswer ->
//                ctl_right.isSelected = useranswer == itemData?.questionInfo?.isright
//                ctl_left.isSelected = !ctl_right.isSelected
//                initAnswer(useranswer)
//            }
            ctl_ordinary_bottom.visibility = View.VISIBLE
            ll_ordinary.visibility = View.VISIBLE
            line.visibility = View.VISIBLE
        } else {
            ctl_ordinary_bottom.visibility = View.GONE
            ll_ordinary.visibility = View.GONE
            line.visibility = View.GONE
        }
    }

    /**
     * 展示评分标准
     */
    fun scoreSelfTest(isShow: Boolean) {
        if (isShow) {
            rlv_score.visibility = View.VISIBLE
            ctl_self_bottom.visibility = View.VISIBLE
            scoredAdapter.setData(itemData?.questionInfo?.subiList)
//            itemData?.questionInfo?.subiList?.let { list ->
//                submitLevels.clear()
//                list.forEach { sub ->
//                    sub.levelList?.forEach { level ->
//                        if (level.levelPoint.isNotEmpty()) {
//                            val item = AnswerItem(sub.key, level.levelKey, level.levelPoint)
//                            submitLevels[sub.key] = item
//                        }
//                    }
//                }
//                if (!submitLevels.isNullOrEmpty()) {
//                    parentActivity.isDone = true
//                    haveDown = true
//                    kotlin.run breaking@{
//                        parentActivity.answerList.forEach { answer ->
//                            answer.qList.forEach { qItem ->
//                                if (qItem.questionKey == itemData?.questionInfo?.questionKey!!) {
//                                    qItem.haveDone = true
//                                    qItem.answers.clear()
//                                    qItem.answers.addAll(submitLevels.values)
//                                    return@breaking
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        } else {
            rlv_score.visibility = View.GONE
            ctl_self_bottom.visibility = View.GONE
        }
    }

    /**
     * 人工判卷上传图片
     */
    fun imgTest(isShow: Boolean) {
        if (isShow) {
            ctl_upload.visibility = View.VISIBLE
            ctl_self_bottom.visibility = View.VISIBLE
//            itemData?.questionInfo?.imgKeyList?.let { list ->
//                if (list.isNotEmpty()) {
//                    kotlin.run breaking@{
//                        parentActivity.answerList.forEach { answer ->
//                            answer.qList.forEach { qItem ->
//                                if (qItem.questionKey == itemData?.questionInfo?.questionKey!!) {
//                                    qItem.haveDone = true
//                                    qItem.answers.clear()
//                                    return@breaking
//                                }
//                            }
//                        }
//                    }
//                    parentActivity.isDone = true
//                    haveDown = true
//                    imgPaths.clear()
//                    imgPaths.addAll(list)
//                    imgAdapter.setFooter(imgPaths.size < 3)
//                    imgAdapter.setData(imgPaths)
//                }
//            }
        } else {
            ctl_upload.visibility = View.GONE
            ctl_self_bottom.visibility = View.GONE
        }
    }

    /**
     * 展示人工和自判的选项,1:展示人工；2：人工和自判都展示
     */
    fun imgOrSelf(type: String) {
        if ("1" == type) {
            ctl_select.visibility = View.VISIBLE
            tv_self.visibility = View.GONE
        } else if ("2" == type) {
            ctl_select.visibility = View.VISIBLE
            tv_self.visibility = View.VISIBLE
        } else {
            ctl_select.visibility = View.GONE
        }
    }

    companion object {
        private const val ITEM = "item"

        @JvmStatic
        fun newInstance(item: ExerciseFrameItem) =
            ExerciseDetailTranslateAndWritingFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ITEM, item)
                }
            }
    }

    fun format(data: String) = "<!doctype html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"utf-8\">\n" +
            "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no\" />\n" +
            "<style>\n" +
            "*{margin:5; padding:25;font-size:13px;color:#222222;}\n" + "img { width: 100%; }\n" +
            "</style>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            data +
            "</body>\n" +
            "</html>"

    fun updateImg(path: String) {
        imgPaths.add(File(path))
        imgAdapter.setFooter(imgPaths.size < 3)
        imgAdapter.setData(imgPaths)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
            imgOrSelf("0")
            imgTest(true)
        }
    }
}

// it.questionInfo?.subiList
class ScoreAdapter(val OnItemClick: (LevelListBean, String) -> Unit) : BaseAdapter<SubiListBean>(isFooter = true) {
    override fun convert(holder: ViewHolder, item: SubiListBean) {
        val context = holder.itemView.context
        holder.setText(R.id.tv_title, item.name)
        val rlvAnswer = holder.getView(R.id.rlv_answer) as RecyclerView
        rlvAnswer.layoutManager = LinearLayoutManager(context)
        rlvAnswer.isNestedScrollingEnabled = false
        val answerAdapter = AnswerAdapter() {
            OnItemClick(it, item.key)
        }
        rlvAnswer.adapter = answerAdapter
        answerAdapter.setData(item.levelList)
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_translate_writing_score, parent, false)
    }

    override fun footerView(context: Context,parent: ViewGroup): View {
        val view = View(context)
        val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(context, 60f).toInt())
        view.layoutParams = params
        return view
    }
}

class AnswerAdapter(val OnItemClick: (LevelListBean) -> Unit) : BaseAdapter<LevelListBean>() {
    private var lastClickPos = -1
    override fun convert(holder: ViewHolder, item: LevelListBean) {
        val stlItem = holder.getView(R.id.ctl_item) as ConstraintLayout
//        if (item.levelPoint.isNotEmpty()) {
//            lastClickPos = holder.adapterPosition
//            item.levelPoint = ""
//        }
        stlItem.isSelected = lastClickPos == holder.adapterPosition
        holder.setText(R.id.tv_answer_pre, item.levelName).setText(R.id.tv_answer_c, item.levelComment)
        holder.itemView.setOnClickListener {
            OnItemClick(item)
            lastClickPos = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    override fun convertView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_translate_item, parent, false)
    }
}

class ImageAdapter(val addClick: () -> Unit) : BaseAdapter<File>(isFooter = true) {
    override fun convert(holder: ViewHolder, item: File) {
        holder.setImageUrl(R.id.iv_img, item.path)
            .setOnClickListener(R.id.iv_close) {
                remove(holder.layoutPosition)
                setFooter(getDateCount() < 3)
            }
    }

    override fun footer(holder: ViewHolder) {
        holder.setOnClickListener(R.id.iv_img) {
            addClick()
        }
    }

    override fun convertView(context: Context, parent: ViewGroup) = with(context) {
        frameLayout {
            lparams(dip(100), dip(100))
            imageView {
                id = R.id.iv_img
            }.lparams(matchParent, matchParent)
            imageView(R.mipmap.ic_close) {
                id = R.id.iv_close
                padding = dip(3)
            }.lparams {
                gravity = Gravity.END
            }
        }
    }

    override fun footerView(context: Context,parent: ViewGroup) = with(context) {
        frameLayout {
            lparams(dip(100), dip(100))
            imageView(R.drawable.ic_over_up_lmg) {
                id = R.id.iv_img
            }.lparams(matchParent, matchParent)
        }
    }
}
