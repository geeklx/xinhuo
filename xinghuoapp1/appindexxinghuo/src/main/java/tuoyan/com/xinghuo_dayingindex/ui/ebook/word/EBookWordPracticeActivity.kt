package tuoyan.com.xinghuo_dayingindex.ui.ebook.word

import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_ebook_word_practice.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.bean.EBookPractice
import tuoyan.com.xinghuo_dayingindex.bean.EBookPracticeAnswer
import tuoyan.com.xinghuo_dayingindex.bean.EBookPracticeQuestion
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.EBookWordDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.fragment.EBookWordSpellFragment
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.fragment.PracticeFragment

class EBookWordPracticeActivity : EBookLifeActivity<EBookPresenter>() {

    override val layoutResId: Int
        get() = R.layout.activity_ebook_word_practice
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)
    private val fragmentList by lazy { mutableListOf<Fragment>() }
    private var currentIndex = 0
    private val practiceList by lazy { mutableListOf<EBookPractice>() }
    private val answer by lazy { EBookPracticeAnswer() }
    private var dDialog: DDialog? = null

    private val dialog by lazy {
        EBookWordDialog(this) {
            submit()
        }
    }

    override fun configView() {
        super.configView()
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun initData() {
        super.initData()
        initAnswer()
        presenter.getQuestionList(bookParam?.catalogKey ?: "") { list ->
            practiceList.clear()
            list.forEach { practice ->
                practice.qIndex = (0 until practice.questionList.size).random()
                val question = practice.questionList[practice.qIndex]
                if (question.questionType == "1") {
                    val fragment = PracticeFragment.newInstance(practice)
                    fragmentList.add(fragment)
                } else if (question.questionType == "2") {
                    val fragment = EBookWordSpellFragment.newInstance(practice)
                    fragmentList.add(fragment)
                }
            }
            if (fragmentList.size > 0) {
                supportFragmentManager.beginTransaction().add(R.id.flv, fragmentList[currentIndex]).show(fragmentList[currentIndex]).commit()
            }
        }
    }

    fun addFragment(practice: EBookPractice) {
        practice.qIndex = (practice.qIndex + 1) % practice.questionList.size
        val question = practice.questionList[practice.qIndex]
        question.item.forEach { item ->
            item.isAnswer = if (item.isAnswer == "2") "0" else item.isAnswer
        }
        if (question.questionType == "1") {
            val fragment = PracticeFragment.newInstance(practice)
            fragmentList.add(fragment)
        } else if (question.questionType == "2") {
            val fragment = EBookWordSpellFragment.newInstance(practice)
            fragmentList.add(fragment)
        }
    }

    fun toNext() {
        if (currentIndex < fragmentList.size - 1) {
            supportFragmentManager.findFragmentById(R.id.flv)?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
            currentIndex++
            supportFragmentManager.beginTransaction().replace(R.id.flv, fragmentList[currentIndex]).commit()
        } else {
            dialog.show()
        }
    }

    private fun initAnswer() {
        bookParam?.let { book ->
            answer.smartBookKey = book.bookKey
            answer.catalogKey = book.catalogKey
            answer.questionList.clear()
        }
    }

    fun addAnswer(qAnswer: EBookPracticeQuestion) {
        answer.questionList.add(qAnswer)
    }

    private fun submit() {
        if (answer.questionList.isNotEmpty()) {
            presenter.questionSubmit(answer) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onBackPressed() {
        dDialog = DDialog(this).setMessage("你正在进行练习，是否确定退出？")
            .setNegativeButton("确定") {
                dDialog?.dismiss()
                submit()
            }.setPositiveButton("取消") {
                dDialog?.dismiss()
            }
        dDialog?.show()
    }
}