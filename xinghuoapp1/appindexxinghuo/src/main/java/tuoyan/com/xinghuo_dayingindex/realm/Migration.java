package tuoyan.com.xinghuo_dayingindex.realm;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {

    private static Migration instance = null;

    static {
        instance = new Migration();
    }

    private Migration() {
    }

    public static Migration getInstance() {
        return instance;
    }

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
//        RealmSchema schema = realm.getSchema();
//        if (oldVersion == 1) {
//            schema.get("Group").addField("userId", String.class);
//            schema.get("Resource").addField("key", String.class);
//            schema.get("Resource").addField("liveSource", String.class);
//            oldVersion = 4;
//            Log.e("updata successful", oldVersion + "");
//        } else if (oldVersion == 2) {
//            schema.get("Group").addField("userId", String.class);
//            schema.get("Resource").addField("liveSource", String.class);
//            oldVersion = 4;
//            Log.e("updata successful", oldVersion + "");
//        } else if (oldVersion == 3) {
//            schema.get("Resource").addField("liveSource", String.class);
//            oldVersion = 4;
//            Log.e("updata successful", oldVersion + "");
//        }
    }
}
