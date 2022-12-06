package tuoyan.com.xinghuo_dayingindex.ui.practice.composition

import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_composition_detail.*
import kotlinx.android.synthetic.main.activity_teacher_modify_detail.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.QuestionInfo

class CompositionDetailWebActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_composition_detail_web

    private val practisekey by lazy { intent.getStringExtra(PRACTISE_KEY) ?: "" }
    private val evalKey by lazy { intent.getStringExtra(EVAL_KEY) ?: "" }
    val detailTitle by lazy { intent.getStringExtra(TITLE) ?: "" }

    companion object {
        val TITLE = "title"
        val PRACTISE_KEY = "practisekey"
        val EVAL_KEY = "evalKey"

    }

    override fun configView() {
        super.configView()
        setSupportActionBar(tb_composition_detail)
        tb_composition_detail.setNavigationOnClickListener { onBackPressed() }
        tv_title.text = detailTitle
        webView.webChromeClient = object : WebChromeClient() {}
        webView.webViewClient = object : WebViewClient() {}
    }


    override fun initData() {
        super.initData()
        presenter.getExerciseFrame(practisekey, "0", evalKey) {
            var infoJson = Gson().toJson(it.questionlist?.get(0)?.questionlist?.get(0)?.questionInfo)
            var questionInfo = Gson().fromJson(infoJson, QuestionInfo::class.java)
            webView.loadDataWithBaseURL(null, format(questionInfo.stem), "text/html", "utf-8", null);

        }
    }


    fun format(data: String) = "<!doctype html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"utf-8\">\n" +
            "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no\" />\n" +
            "<style>\n" +
            "*{margin:5; padding:25;}\n" + "img {\n" +
            "            max-width: 50%;\n" +
            "</style>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            data +
            "</body>\n" +
            "</html>"
}