package jinxin.out.com.jinxin_employee;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by Administrator on 2017/8/5.
 */

public class JsonUtil {
    public static <T> T parsoJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }


    public static <T> List<T> parsoArrayJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        List<T> result = gson.fromJson(jsonData, new TypeToken<List<T>>() {
        }.getType());
        return result;
    }
}
