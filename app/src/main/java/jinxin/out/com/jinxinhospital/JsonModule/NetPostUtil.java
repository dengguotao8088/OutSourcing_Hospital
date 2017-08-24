package jinxin.out.com.jinxinhospital.JsonModule;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.logging.Handler;

import jinxin.out.com.jinxinhospital.util.LoggingInterceptor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/5.
 */

public class NetPostUtil {
    private static Context mContext;
    private static String token;
    private static String mMsg;

    private static android.os.Handler myHandler = new android.os.Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(mContext, mMsg, Toast.LENGTH_LONG).show();
        }
    };

    public NetPostUtil(Context context) {
        mContext = context;
    }

    public static void post(String url, RequestBody body, Callback callback) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
        token = sharedPreferences.getString("token", null);

        final OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(new LoggingInterceptor()).build();
        final Request request = new Request.Builder().url(url).post(body).build();
        final Callback mCallback = callback;

        if (token != null && token != "") {
            Log.d("xie", "身份验证 token 不等于空" );
            RequestBody tmpbody = new FormBody.Builder().add("token", token).build();
            Request tmpReq = new Request.Builder().url(Constants.VERFICATION_TOKEN).post(tmpbody).build();
            client.newCall(tmpReq).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Log.d("xie", "身份验证 result = " + result);
                    if (result.contains("502  Bad Gateway")) {
                        return;
                    }
                    BaseModule baseModule = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
                    if (baseModule.code == 1) {
                        mMsg = baseModule.message;
                        myHandler.sendEmptyMessage(0x11111);
                        return;
                    } else {
                        client.newCall(request).enqueue(mCallback);
                    }
                }
            });
        } else {
            Log.d("xie", "身份验证 token 等于空");
            client.newCall(request).enqueue(mCallback);
        }

    }
}
