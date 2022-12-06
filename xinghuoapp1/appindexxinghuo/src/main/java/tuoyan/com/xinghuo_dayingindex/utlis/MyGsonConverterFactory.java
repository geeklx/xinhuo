//package tuoyan.com.xinghuo_daying.utlis;
//
//
//import com.google.gson.TypeAdapter;
//import com.google.gson.reflect.TypeToken;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Type;
//
//import okhttp3.RequestBody;
//import okhttp3.ResponseBody;
//import retrofit2.Converter;
//import retrofit2.Retrofit;
//
//public class MyGsonConverterFactory extends Converter.Factory {
//    public static MyGsonConverterFactory create() {
//        return new MyGsonConverterFactory();
//    }
//
//    private MyGsonConverterFactory() {
//
//    }
//
//    @Override
//    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
//                                                            Retrofit retrofit) {
//        TypeAdapter<?> adapter = GsonUtils.getGson().getAdapter(TypeToken.get(type));
//        return new MyGsonResponseBodyConverter<>(GsonUtils.getGson(), adapter);
//    }
//    @Override
//    public Converter<?, RequestBody> requestBodyConverter(Type type,
//                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
//        TypeAdapter<?> adapter = GsonUtils.getGson().getAdapter(TypeToken.get(type));
//        return new MyGsonRequestBodyConverter<>(GsonUtils.getGson(), adapter);
//    }
//}
