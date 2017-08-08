package jinxin.out.com.jinxinhospital.Customer;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
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

    public static LoginManager getInstance(Context context,String token, String id) {
        if (sInstance == null) {
            sInstance = new LoginManager(context,token, id);
        }
        return sInstance;
    }

    private String mToken;
    private Context mContext;
    private String mId;
    private Customer mCustomerData;

    public LoginManager(Context context,String token, String id) {
        mContext = context;
        this.mToken = token;
        this.mId = id;
    }

    public void getCustomer(GetCustomerDoneCallBack callBack) {
        Log.d("xie","LoginManager: getCustomer().");
        final GetCustomerDoneCallBack doneCallBack = callBack;
        RequestBody body = new FormBody.Builder().add("token", mToken).add("id", mId).build();
        NetPostUtil.post(Constants.GET_CUSTOMER_WITH_ID, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("xie", "LoginManager, getCustomer Failure...");
                return;
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
                if (module.code != 0) {
                    return;
                }
                CustomerResponseJson customerResponseJson =
                        JsonUtil.parsoJsonWithGson(result, CustomerResponseJson.class);
                if (customerResponseJson.code == 0) {
                    //Todo: 保存客户资料
                    mCustomerData = customerResponseJson.data;
                    doneCallBack.getCustomerDone();
                }
            }
        });
    }

    public interface GetCustomerDoneCallBack {
        public void getCustomerDone();
    }
}
