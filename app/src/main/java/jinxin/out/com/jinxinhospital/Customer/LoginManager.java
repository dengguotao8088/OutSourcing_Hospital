package jinxin.out.com.jinxinhospital.Customer;

import android.content.Context;

import java.io.IOException;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/4.
 */

public class LoginManager {
    public static LoginManager sInstance;

    public static LoginManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LoginManager(context);
        }
        return sInstance;
    }

    private String mToken;
    private Context mContext;
    private Customer mCustomer;

    public LoginManager(Context context) {
        mContext = context;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public void getCustomer(int id, GetCustomerDoneCallBack callBack) {
        final GetCustomerDoneCallBack doneCallBack = callBack;
        RequestBody body = new FormBody.Builder().add("token", mToken).add("id", id + "").build();
        NetPostUtil.post(Constants.LOGIN_URL, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                CustomerResponseJson customerResponseJson =
                        JsonUtil.parsoJsonWithGson(result, CustomerResponseJson.class);
                if (customerResponseJson.code == 0) {
                    //Todo: 保存客户资料
                    mCustomer = customerResponseJson.data;
                    doneCallBack.getCustomerDone();
                }
            }
        });
    }

    public interface GetCustomerDoneCallBack {
        public void getCustomerDone();
    }
}
