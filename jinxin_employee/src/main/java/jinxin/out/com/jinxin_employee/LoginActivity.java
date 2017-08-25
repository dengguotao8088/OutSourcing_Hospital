package jinxin.out.com.jinxin_employee;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.IOException;

import jinxin.out.com.jinxin_employee.JsonModule.BaseModule;
import jinxin.out.com.jinxin_employee.JsonModule.LoginResponseJson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by admin on 2017/7/22.
 */

public class LoginActivity extends Activity {

    private Context mContext;
    private EditText mName;
    private EditText mPassword;
    private Button mLogin;
    private CheckBox mRemCheckbox;

    private KProgressHUD mHUD;

    private MainHandler mainHandler;

    private class MainHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    String str = (String) msg.obj;
                    Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mainHandler = new MainHandler();

        setContentView(R.layout.login_activity);

        initView(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (LoginManager.getInstance(this).getToken() != null) {
            Intent intent = new Intent(mContext, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initView(Context context) {
        mName = findViewById(R.id.login_name);
        mName.setText(LoginManager.getInstance(this).getUserName());
        mPassword = findViewById(R.id.login_password);
        mPassword.setText(LoginManager.getInstance(this).getPassword());
        mRemCheckbox = findViewById(R.id.remenber);
        mRemCheckbox.setChecked(true);
        mLogin = findViewById(R.id.login);
        mLogin.setOnClickListener(onClickListener);
    }

    private void remenber(String name, String password) {
        LoginManager.getInstance(mContext).saveUserAndPass(name, password);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String name = mName.getText().toString();
            String password = mPassword.getText().toString();

            if ("".equals(name) || "".equals(password)) {
                Toast.makeText(mContext, "用户名和密码不能空", Toast.LENGTH_SHORT).show();
            } else if (!LoginManager.getInstance(mContext).isNetworkConnected()) {
                Toast.makeText(mContext, "没有网络", Toast.LENGTH_SHORT).show();
            } else {
                if (mRemCheckbox.isChecked()) {
                    remenber(name, password);
                } else {
                    LoginManager.getInstance(mContext).deleteUserAndPass();
                }
                RequestBody body = new FormBody.Builder().add("jobNumber", name)
                        .add("password", password)
                        .build();
                NetPostUtil.post(Constants.LOGIN_URL, body, mLoginCallback);
                if (mHUD == null) {
                    mHUD = KProgressHUD.create(LoginActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("登录中")
                            .setCancellable(true)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f);
                }
                mHUD.show();
            }

        }
    };

    private Callback mLoginCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mHUD.dismiss();
            mainHandler.sendMessage(mainHandler.obtainMessage(100, "登录失败"));
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            mHUD.dismiss();
            if (response.code() != 200) {
                //Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                mainHandler.sendMessage(mainHandler.obtainMessage(100, "登录失败"));
                return;
            }
            String result = response.body().string();
            BaseModule baseModule = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (baseModule.code == 0) {
                LoginResponseJson loginResponseJson =
                        JsonUtil.parsoJsonWithGson(result, LoginResponseJson.class);
                LoginManager.getInstance(mContext).setToken(loginResponseJson.data.token);
                LoginManager.getInstance(mContext).setEmployee(loginResponseJson.data.empDO);
                //LoginManager.getInstance(mContext).saveEmp();
                //Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                mainHandler.sendMessage(mainHandler.obtainMessage(100, "登录成功"));
                Intent intent = new Intent(mContext, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                //Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                mainHandler.sendMessage(mainHandler.obtainMessage(100, baseModule.message));
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (mHUD != null) {
            mHUD.dismiss();
        }
    }

}
