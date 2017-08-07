package jinxin.out.com.jinxinhospital;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import jinxin.out.com.jinxinhospital.Customer.LoginManager;
import jinxin.out.com.jinxinhospital.Customer.LoginResponseJson;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit.Retrofit;

/**
 * Created by Administrator on 2017/7/12.
 */

public class LoadActivity extends Activity {

    private Context mContext;
    private Button mLoginBt;
    private TextView mRegistChild;
    private TextView mRegistAdult;
    private TextView mLoginNo;
    private TextView mForgetPwd;
    private EditText mTel;
    private EditText mPwd;
    private static String token;
    private static int mCustomerId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_main);
        mContext = this;
        mLoginBt = findViewById(R.id.login);
        mLoginBt.setOnClickListener(mLoginOnClickListener);
        mRegistChild = findViewById(R.id.regist_child);
        mRegistAdult = findViewById(R.id.regist_adult);
        mLoginNo = findViewById(R.id.login_no);
        mForgetPwd = findViewById(R.id.forget_pwd);
        mTel = findViewById(R.id.tel);
        mPwd = findViewById(R.id.pwd);
    }
    public static String getToken(){
        return token;
    }
    public static int getmCustomerId(){
        return mCustomerId;
    }

    private View.OnClickListener mLoginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String tel = mTel.getText().toString();
            String pwd = mPwd.getText().toString();
            if ("".equals(tel) || "".equals(pwd)) {
                Toast.makeText(mContext, "用户名和密码不能空", Toast.LENGTH_SHORT).show();
            } else {
                RequestBody body = new FormBody.Builder().add("mobile", tel)
                        .add("password", pwd)
                        .build();
                NetPostUtil.post(Constants.LOGIN_URL, body, mLoginCallback);
            }
        }
    };

    private Callback mLoginCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d("xie", "mLoginCallback onResponse");
            String result = response.body().string();
            LoginResponseJson loginResponseJson =
                    JsonUtil.parsoJsonWithGson(result, LoginResponseJson.class);
            if (loginResponseJson.code == 0) {
                token = loginResponseJson.data.token;
                mCustomerId = loginResponseJson.data.customer.id;
                Log.d("xie", "....token = " + token);
                Log.d("xie", "....customerId = " + mCustomerId);
                LoginManager.getInstance(mContext).setToken(token);
                LoginManager.getInstance(mContext).getCustomer(mCustomerId, customerDoneCallBack);
            }
        }
    };

    private LoginManager.GetCustomerDoneCallBack customerDoneCallBack = new LoginManager.GetCustomerDoneCallBack() {
        @Override
        public void getCustomerDone() {
            Intent intent = new Intent(mContext, HomePageFragment.class);
            startActivity(intent);
            finish();
        }
    };
}
