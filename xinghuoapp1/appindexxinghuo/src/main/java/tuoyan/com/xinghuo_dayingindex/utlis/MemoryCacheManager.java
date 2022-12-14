//package tuoyan.com.xinghuo_daying.utlis;
//
//import android.util.LruCache;
//
//public class MemoryCacheManager {
//    private static class Holder {
//        public static final MemoryCacheManager instance = new MemoryCacheManager();
//    }
//
//    public static MemoryCacheManager getInstance() {
//        return Holder.instance;
//    }
//
//    private MemoryCacheManager() {
//    }
//
//    final static int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
//    static LruCache<String, Object> mMemoryCache = new LruCache<String, Object>(
//            cacheSize);
//
//    public static void add(String key, Object mObject) {
//        mMemoryCache.put(key, mObject);
//    }
//
//    public static Object get(String key) {
//        return mMemoryCache.get(key);
//    }
//}
