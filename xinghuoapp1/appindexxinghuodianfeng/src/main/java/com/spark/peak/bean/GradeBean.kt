package com.spark.peak.bean

/**
 * Created by Zzz on 2020/12/30
 */

class GradeBean {
    var key = ""
    var dictKey = ""
    var name = ""
    override fun toString(): String {
        return name
    }
}

class GradeType {
    var editorLists = arrayListOf<GradeBean>()
    var termList = arrayListOf<GradeBean>()
    var subjectKey = ""
}

class BookContent {
    var appSyncStudyKey = ""
    var name = ""
    var catalogList = arrayListOf<BookCatalog>()
}

class BookCatalog {
    var catalogResources = arrayListOf<AudioRes>()
}

class BookCatalogResource {
    var showName = ""
    var appResourceKey = ""
    var lrcUrl = ""
    var resourcePath = ""
}