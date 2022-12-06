package tuoyan.com.xinghuo_dayingindex.realm.bean

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class Group: RealmObject() {
    open var userId: String = ""//根据不同用户查看对应的下载
    open var name: String = ""
    open var key: String = ""
    open var type: String = "" //图书 & 网课 &EBookWord
    open var resList: RealmList<Resource> ?= null
}