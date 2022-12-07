package com.spark.peak.ui.grammar

import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.spark.peak.R
import com.spark.peak.base.LifeActivity
import com.spark.peak.ui.community.post.PostActivity
import com.spark.peak.ui.grammar.adapter.GrammarSearchAdapter
import kotlinx.android.synthetic.main.activity_grammar_searchdf.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class GrammarSearchActivity : LifeActivity<GrammarPresenter>() {
    override val presenter: GrammarPresenter
        get() = GrammarPresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_grammar_searchdf
    private val parentKey by lazy { intent.getStringExtra("key") ?: "" }
    private val parentName by lazy { intent.getStringExtra("parentName") ?: "语法词典" }
    private val searchAdapter by lazy {
        GrammarSearchAdapter() { search ->
            presenter.grammarDetail(search.key) { detail ->
                saDetail(detail.title)
                startActivity<PostActivity>(
                    PostActivity.TITLE to detail.title,
                    PostActivity.URL to detail.content
                )
            }
        }
    }

    override fun configView() {
        super.configView()
        rlv_search.layoutManager = LinearLayoutManager(this)
        rlv_search.adapter = searchAdapter
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener { onBackPressed() }
        tv_search.setOnClickListener {
            val text = et_search.text.trim().toString()
            if (text.isNotEmpty()) {
                saSearch(text)
                presenter.searchGrammar(text, parentKey) {
                    searchAdapter.setData(it)
                }
            } else {
                Toast.makeText(this, "请输入检索内容", Toast.LENGTH_LONG).show()
            }
        }
        et_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val text = et_search.text.trim().toString()
                if (text.isNotEmpty()) {
                    hideSoftKeybord(v)
                    presenter.searchGrammar(text, parentKey) {
                        searchAdapter.setData(it)
                    }
                } else {
                    Toast.makeText(this, "请输入检索内容", Toast.LENGTH_LONG).show()
                }
                true
            }
            false
        }
    }

    override fun initData() {
        super.initData()
    }

    private fun saSearch(keyword: String) {
        try {
            val property = JSONObject()
            property.put("keyword", keyword)
            property.put("search_classify", parentName)
            SensorsDataAPI.sharedInstance().track("df_grammar_search", property)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saDetail(title: String) {
        try {
            val property = JSONObject()
            property.put("grammar_tile", title)
            property.put("first_classify", "")
            property.put("second_classify", "")
            SensorsDataAPI.sharedInstance().track("df_grammar_detail", property)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}