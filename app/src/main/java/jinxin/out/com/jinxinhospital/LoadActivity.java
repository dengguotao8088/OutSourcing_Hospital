package jinxin.out.com.jinxinhospital;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import jinxin.out.com.jinxinhospital.Customer.LoginManager;
import jinxin.out.com.jinxinhospital.Customer.LoginResponseJson;
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
 * Created by Administrator on 2017/7/12.
 */

public class LoadActivity extends FragmentActivity {

    private static LoadActivity mContext;
    private Button mLoginBt;
    private TextView mRegistChild;
    private TextView mRegistAdult;
    private TextView mLoginNo;
    private TextView mForgetPwd;
    private LinearLayout mLinearLayout;
    private EditText mTel;
    private EditText mPwd;
    private  String token;
    private  int mCustomerId = -1;
    private  String name= "";
    private  String tel = "";
    private boolean vip = false;
    private int backPressNum = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_main);
        mContext = this;
        NetPostUtil netPostUtil = new NetPostUtil(getApplicationContext());
        mLoginBt = findViewById(R.id.login);
        mLoginBt.setOnClickListener(mLoginOnClickListener);
        mRegistChild = findViewById(R.id.regist_child);
        mRegistAdult = findViewById(R.id.regist_adult);
        mLoginNo = findViewById(R.id.login_no);
        mForgetPwd = findViewById(R.id.forget_pwd);
        mTel = findViewById(R.id.tel);
        mPwd = findViewById(R.id.pwd);
        mLinearLayout = findViewById(R.id.login_linear_layout);

        mLoginNo.setOnClickListener(mOnClickListener);
        mRegistChild.setOnClickListener(mOnClickListener);
        mRegistAdult.setOnClickListener(mOnClickListener);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app",0);
        token = sharedPreferences.getString("token", null);
    }

    private AdultRegisterFragment adultRegisterFragment = new AdultRegisterFragment();
    private ChildRegisterFragment childRegisterFragment = new ChildRegisterFragment();
    private int isShow = 0;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.regist_adult:
                    if (adultRegisterFragment == null){
                        adultRegisterFragment = new AdultRegisterFragment();
                    }
                    isShow = 1;
                    mLinearLayout.setVisibility(View.GONE);
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.login_content, adultRegisterFragment).commitAllowingStateLoss();
                    break;
                case R.id.regist_child:
                    if (childRegisterFragment == null){
                        childRegisterFragment = new ChildRegisterFragment();
                    }
                    isShow = 2;
                    mLinearLayout.setVisibility(View.GONE);
                    android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.login_content, childRegisterFragment).commitAllowingStateLoss();
                    break;
                case R.id.login_no:
                    finish();
                    break;
            }
        }
    };

    private View.OnClickListener mLoginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Log.d("xie", "Login....");
            String tel = mTel.getText().toString();
            String pwd = mPwd.getText().toString();
            if ("".equals(tel) || "".equals(pwd)) {
                Toast.makeText(mContext, "用户名和密码不能空", Toast.LENGTH_SHORT).show();
            } else if (!isNetworkConnected()) {
                Toast.makeText(mContext, "网络连接不可用，请检查网络。", Toast.LENGTH_SHORT).show();
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
            Log.d("xie", "mLoginCallback error");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d("xie", "mLoginCallback onResponse");
            String result = response.body().string();
            Log.d("xie", "result = "  + result);
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0) {
                return;
            }
            LoginResponseJson loginResponseJson =
                    JsonUtil.parsoJsonWithGson(result, LoginResponseJson.class);
            if (loginResponseJson.code == 0) {
                token = loginResponseJson.data.token;
                mCustomerId = loginResponseJson.data.customer.id;
                name = loginResponseJson.data.customer.name;
                tel = loginResponseJson.data.customer.mobile;
                vip = loginResponseJson.data.customer.vip;
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token",token);
                editor.putInt("customerId",mCustomerId);
                editor.putString("tel",tel);
                editor.putString("name",name);
                editor.putBoolean("vip", vip);
                editor.commit();
                Log.d("xie", "Login: Customer = " + loginResponseJson.data.customer.toString());
                Log.d("xie", "....token = " + token);
                Log.d("xie", "....customerId = " + mCustomerId);
                Log.d("xie", "....vip = " + vip);
                LoginManager loginManager = LoginManager.getInstance(mContext, token, mCustomerId+"");
                loginManager.getCustomer(customerDoneCallBack);
            }
        }
    };

    private LoginManager.GetCustomerDoneCallBack customerDoneCallBack = new LoginManager.GetCustomerDoneCallBack() {
        @Override
        public void getCustomerDone() {
            Log.d("xie", "getCustomerDone, startActivity:HomePageFragment");
            finish();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("xie", "onKeyDown........keyCode = ." + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DEL)
        {
            return super.onKeyDown(keyCode, event);
        }
        if (keyCode != KeyEvent.KEYCODE_BACK || (adultRegisterFragment == null
                && childRegisterFragment == null ) || (isShow == 0) ) {
//            backPressNum ++;
//            if (backPressNum == 1) {
//                Toast.makeText(mContext, "再按一次返回键将退出应用。", Toast.LENGTH_SHORT).show();
//            } else if (backPressNum == 2){
//                System.exit(0);
////                finish();
//            } else {
//                return super.onKeyDown(keyCode, event);
//            }
            return super.onKeyDown(keyCode, event);
        }
        showSelf();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("xie", "onBackPressed.........");

    }

    public void showSelf(){
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (isShow == 1) {
            transaction.remove(adultRegisterFragment).commit();
            adultRegisterFragment = null;
        } else if (isShow == 2){
            transaction.remove(childRegisterFragment).commit();
            childRegisterFragment = null;
        }
        isShow = 0;
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    public static LoadActivity getObj(){
        return mContext;
    }

    public boolean isNetworkConnected() {
        if (mContext != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
