package com.spark.peak.bean

import java.io.Serializable

/**
 * Created by Zzz on 2020/12/30
 */
class Grammar : Serializable {
    var catalogList = arrayListOf<Grammar>()
    var name = ""
    var sort = ""
    var key = ""
}

class GrammarSearch {
    var title = ""
    var key = ""
    var content = ""
}

class GrammarDetail() {
    val title = ""
    val content = ""
}