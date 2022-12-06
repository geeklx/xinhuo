package tuoyan.com.xinghuo_dayingindex.ui.home.word.detail


import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_word_detail.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.support.v4.ctx
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.WordsByCatalogkey
import tuoyan.com.xinghuo_dayingindex.ui.dialog.AlertDialog
import tuoyan.com.xinghuo_dayingindex.ui.home.word.WordPresenter
import tuoyan.com.xinghuo_dayingindex.utlis.MediaPlayerUtlis
import java.util.concurrent.TimeUnit

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [WordDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class WordDetailFragment : LifeV4Fragment<WordPresenter>() {
    override val presenter = WordPresenter(this)
    override val layoutResId = R.layout.fragment_word_detail
    private val word by lazy { arguments?.getSerializable(WORD) as? WordsByCatalogkey }
    private val gradeKey by lazy { arguments?.getString(GRADE_KEY) ?: "" }
    private val isNewWord by lazy { requireArguments().getBoolean(IS_NEW_WORD, false) }
    private var disposable: Disposable? = null

    override fun initData() {
        tv_name.text = word?.word ?: ""
        if (word?.symbol.isNullOrEmpty()) {
            tv_yb.text = ""
        } else {
            tv_yb.text = "[${word?.symbol?.replace("[", "")?.replace("]", "") ?: ""}]"
        }
        iv_new_word.isSelected = word?.isAdd == "1" // : 2018/10/23 15:51  单词状态 是否生词
        tv_translation.text = Html.fromHtml(word?.paraphrase ?: "")
        tv_liju.text = Html.fromHtml(word?.exampleSentence ?: "")
        if (isNewWord)
            iv_new_word.imageResource = R.drawable.ic_deleted_new_word
    }

    override fun handleEvent() {
        iv_new_word.setOnClickListener { it ->
            if (!isNewWord)
                if (!it.isSelected) {
                    // : 2018/10/16 14:23  添加生词本
                    word?.key?.let {
                        presenter.recordNewWord(
                            mutableMapOf(
                                "key" to it,
                                "gradeKey" to gradeKey
                            )
                        ) {
                            iv_new_word.isSelected = it.ret == 100
                            mToast(it.msg)
                        }
                    }
                } else {
                    word?.key?.let {
                        presenter.deleteNewWord(it) {
                            iv_new_word.isSelected = false
                        }
                    }
                }
            if (isNewWord) {
                // \: 2018/10/23 13:35   移除生词本
                word?.key?.let {
                    AlertDialog(ctx, "确定要移除生词本吗？") {
                        presenter.deleteNewWord(it) {
                            iv_new_word.isSelected = false
                            iv_new_word.visibility = View.GONE
                        }
                    }.show()
                }
            }
        }
        tv_yb.setOnClickListener {
            play(ctx)
        }
    }

    fun play(ctx: Context) {
        word?.sound?.let { sound ->
            MediaPlayerUtlis.start(ctx, sound, {
                if (disposable?.isDisposed == false) disposable?.dispose()
                disposable = Observable.interval(0, 250, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        tv_yb.isSelected = !tv_yb.isSelected
                    }
            }, {
                if (disposable?.isDisposed == false) disposable?.dispose()
            }) {
                if (disposable?.isDisposed == false) disposable?.dispose()
                if (it.isNotBlank())
                    mToast(it)
            }
        }
    }

    override fun onPause() {
        if (disposable?.isDisposed == false) disposable?.dispose()
        super.onPause()
    }

    companion object {
        @JvmStatic
        fun newInstance(word: WordsByCatalogkey, gradeKey: String, isNewWord: Boolean) =
            WordDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(WORD, word)
                    putString(GRADE_KEY, gradeKey)
                    putBoolean(IS_NEW_WORD, isNewWord)
                }
            }

        private const val WORD = "word"
        const val GRADE_KEY = "gradeKey"
        const val IS_NEW_WORD = "isNewWord"
    }
}
