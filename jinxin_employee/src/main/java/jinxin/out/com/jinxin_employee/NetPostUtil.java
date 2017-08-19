package jinxin.out.com.jinxin_employee;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/5.
 */

public class NetPostUtil {

    public static boolean post(String url, RequestBody body, Callback callback) {
        if (!LoginManager.getInstance(null).isNetworkConnected()) {
            return false;
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = null;
        if (body != null) {
            request = new Request.Builder().url(url).post(body).build();
        } else {
            request = new Request.Builder().url(url).build();
        }
        client.newCall(request).enqueue(callback);
        return true;
    }

}
