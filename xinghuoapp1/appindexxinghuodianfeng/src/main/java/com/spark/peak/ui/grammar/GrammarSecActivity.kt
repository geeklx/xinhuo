package com.spark.peak.ui.grammar

import androidx.recyclerview.widget.LinearLayoutManager
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.bean.Grammar
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.dialog.ShareDialog
import com.spark.peak.ui.grammar.adapter.GrammarSecAdapter
import com.spark.peak.utlis.ShareUtils
import kotlinx.android.synthetic.main.activity_grammar.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class GrammarSecActivity : LifeActivity<GrammarPresenter>() {
    override val presenter: GrammarPresenter
        get() = GrammarPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_grammar

    private val grammar by lazy { intent.getSerializableExtra("grammar") as Grammar }
    private val dialog by lazy {
        ShareDialog(this) {
            ShareUtils.share(this, it, "", "", "", "")
        }
    }
    private val grammarAdapter by lazy {
        GrammarSecAdapter() { sGrammar, grammar ->
            saDetail(sGrammar.name, grammar.name)
            presenter.grammarDetail(grammar.key) { detail ->
                startActivity<PostActivity>(
                    PostActivity.TITLE to detail.title,
                    PostActivity.URL to detail.content,
                    PostActivity.IS_SHARE to true
                )
            }
        }
    }

    override fun configView() {
        super.configView()
        tv_title.text = grammar.name
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
            startActivity<GrammarSearchActivity>("key" to grammar.key, "parentName" to grammar.name)
        }
    }

    override fun initData() {
        super.initData()
        presenter.getGrammarCatalog(grammar.key, grammar.sort) {
            grammarAdapter.setData(it)
        }
    }

    private fun saDetail(sTitle: String, tTitle: String) {
        try {
            val property = JSONObject()
            property.put("grammar_tile", tTitle)
            property.put("first_classify", grammar.name)
            property.put("second_classify", sTitle)
            SensorsDataAPI.sharedInstance().track("df_grammar_detail", property)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}