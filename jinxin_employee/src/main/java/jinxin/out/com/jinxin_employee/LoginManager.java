package jinxin.out.com.jinxin_employee;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/4.
 */

public class LoginManager {
    public static LoginManager sInstance;

    public static LoginManager getInstance() {
        if (sInstance == null) {
            sInstance = new LoginManager();
        }
        return sInstance;
    }

    public void login(String userId, String password, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("jobNumber", userId)
                .add("password", password).build();
        Request request = new Request.Builder().
                url("http://staff.mind-node.com/staff/api/emp/login?")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
