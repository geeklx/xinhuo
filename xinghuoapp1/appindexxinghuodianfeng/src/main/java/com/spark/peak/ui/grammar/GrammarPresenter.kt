package com.spark.peak.ui.grammar

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.Grammar
import com.spark.peak.bean.GrammarDetail
import com.spark.peak.bean.GrammarSearch

/**
 * Created by Zzz on 2020/12/30
 */

class GrammarPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun getGrammarCatalog(key: String, serial: String, onNext: (List<Grammar>) -> Unit) {
        api.getGrammarCatalog(key, serial).sub({ onNext(it.body) })
    }

    fun searchGrammar(content: String, parentKey: String, onNext: (List<GrammarSearch>) -> Unit) {
        api.getGrammarSearch(content, parentKey).subs({ onNext(it.body) })
    }

    fun grammarDetail(key: String, onNext: (GrammarDetail) -> Unit) {
        api.getGrammarDetail(key).sub({ onNext(it.body) })
    }
}