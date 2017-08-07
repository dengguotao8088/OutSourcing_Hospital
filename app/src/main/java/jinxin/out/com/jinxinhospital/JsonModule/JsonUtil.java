package jinxin.out.com.jinxinhospital.JsonModule;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public static String getDate(String time) {
        Date date = new Date(Long.parseLong(time));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        time = formatter.format(date);
        return time;
    }
}
