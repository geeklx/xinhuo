//package tuoyan.com.xinghuo_daying.utlis;
//
//
//import com.google.gson.Gson;
//
//public class GsonUtils {
//    private static GsonUtils instance;
//    private static Gson gson;
//
//    private GsonUtils(){};
//    public static GsonUtils getInstance(){
//        if (instance == null) {
//            synchronized (GsonUtils.class) {
//                if (instance == null) {
//                    instance = new GsonUtils();
//                    gson = new Gson();
//                }
//            }
//        }
//        return instance;
//    }
//
//    public static Gson getGson() {
//        if (gson == null) {
//            gson = new Gson();
//        }
//        return gson;
//    }
//
//    public <T> T fromJson(String json, Class<T> classOfT) {
//        if (gson == null) {
//            gson = new Gson();
//        }
//        return gson.fromJson(json, classOfT);
//    }
//
//}
