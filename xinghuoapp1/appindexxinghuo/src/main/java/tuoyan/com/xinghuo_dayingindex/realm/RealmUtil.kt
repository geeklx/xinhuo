package tuoyan.com.xinghuo_dayingindex.realm

import io.realm.Realm
import io.realm.RealmConfiguration

class RealmUtil {
    companion object {
        fun instant(): Realm {
            val config = RealmConfiguration.Builder().name("sparke_daying.realm").schemaVersion(1).migration(Migration.getInstance()).build()
            return Realm.getInstance(config)
        }
    }
}