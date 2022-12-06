//package tuoyan.com.xinghuo_daying.utlis;
//
//
//import com.google.gson.Gson;
//import com.google.gson.TypeAdapter;
//import com.google.gson.stream.JsonReader;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//import okhttp3.ResponseBody;
//import retrofit2.Converter;
//
//class MyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
//    private final Gson gson;
//    private final TypeAdapter<T> adapter;
//
//    MyGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
//        this.gson = gson;
//        this.adapter = adapter;
//    }
//
//    @Override
//    public T convert(ResponseBody value) throws IOException {
//        byte[] bytes = value.bytes();
//        if (bytes.length == 0) {
//            return null;
//        }
//        JsonReader jsonReader = gson.newJsonReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
//        try {
//
//            return adapter.read(jsonReader);
//        } finally {
//            value.close();
//        }
//    }
//}
