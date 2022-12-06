package tuoyan.com.xinghuo_dayingindex.realm

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults

fun<T : RealmObject> Realm.create(obj: T){
    beginTransaction()
    copyToRealm(obj)
    commitTransaction()
}

fun<T : RealmObject> Realm.showAll(clazz: Class<T>): RealmResults<T>{
    return where(clazz).findAll()
}

fun <T : RealmObject> Realm.delete(clazz: Class<T>, key: String, value: String) {
    beginTransaction()
    val results = where(clazz).equalTo(key, value).findAll()
    results.deleteAllFromRealm()
    commitTransaction()
}



