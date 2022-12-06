package com.geek.appindex.news.model

import com.geek.biz1.bean.home.ClassificationBean

data class ZXConvertData(
    var name: String? = null,
    var belongId: String? = null,
    var id: String? = null,
    var belongTypeId: String? = null,
    var notice: String? = null,
    var data: ClassificationBean? = null,
    var bannerData: List<ClassificationBean>? = null,
    var type: Int = ZXTYPE.TYPE_ITEM,
) {
    constructor(
        name: String,
        belongId: String,
        id: String,
        belongTypeId: String,
        notice: String,
    ) : this() {
        this.name = name
        this.belongId = belongId
        this.id = id
        this.belongTypeId = belongTypeId
        this.notice = notice
        this.type = ZXTYPE.TYPE_TITLE
    }

    constructor(bean: ClassificationBean) : this() {
        this.data = bean
        this.type = ZXTYPE.TYPE_ITEM
    }

    constructor(bean: List<ClassificationBean>) : this() {
        this.bannerData = bean
        this.type = ZXTYPE.TYPE_BANNER
    }
}


object ZXTYPE {
    const val TYPE_TITLE = 1
    const val TYPE_BANNER = 2
    const val TYPE_ITEM = 3
}