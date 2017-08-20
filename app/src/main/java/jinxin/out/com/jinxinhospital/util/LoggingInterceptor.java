package jinxin.out.com.jinxinhospital.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.LoadActivity;
import jinxin.out.com.jinxinhospital.MainActivity;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/20.
 */

public class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        return response;
    }
}
