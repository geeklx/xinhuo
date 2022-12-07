package com.spark.peak.ui.grammar

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.dialog.ShareDialog
import com.spark.peak.ui.grammar.adapter.GrammarAdapter
import com.spark.peak.utlis.ShareUtils
import kotlinx.android.synthetic.main.activity_grammar.*
import org.jetbrains.anko.startActivity

class GrammarActivity : LifeActivity<GrammarPresenter>() {
    override val presenter: GrammarPresenter
        get() = GrammarPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_grammar
    private val dialog by lazy {
        ShareDialog(this) {
            ShareUtils.share(this, it, "", "", "", "")
        }
    }

    private val grammarAdapter by lazy {
        GrammarAdapter() {
            val intent = Intent(this, GrammarSecActivity::class.java)
//            intent.putExtra("title", "名词")
            intent.putExtra("grammar", it)
            startActivity(intent)
        }
    }

    override fun configView() {
        super.configView()
        rlv_grammar.layoutManager = LinearLayoutManager(this)
        rlv_grammar.adapter = grammarAdapter
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener { onBackPressed() }
        img_share.setOnClickListener {
            dialog.show()
        }
        tv_grammar_search.setOnClickListener {
            startActivity<GrammarSearchActivity>("key" to "")
        }
    }

    override fun initData() {
        super.initData()
        presenter.getGrammarCatalog("", "") {
            grammarAdapter.setData(it)
        }
    }
}