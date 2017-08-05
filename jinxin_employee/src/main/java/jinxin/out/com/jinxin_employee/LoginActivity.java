package jinxin.out.com.jinxin_employee;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mContext = this;
        initView(this);
    }

    private void initView(Context context) {
        mName = findViewById(R.id.login_name);
        mPassword = findViewById(R.id.login_password);
        mRemCheckbox = findViewById(R.id.remenber);
        mLogin = findViewById(R.id.login);
        mLogin.setOnClickListener(onClickListener);
    }

    private boolean remenber(String name, String password) {
        return true;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String name;
            String password;
            name = mName.getText().toString();
            password = mPassword.getText().toString();

            if ("".equals(name) || "".equals(password)) {
                Toast.makeText(mContext, "用户名和密码不能空", Toast.LENGTH_SHORT).show();
            } else {
                if (mRemCheckbox.isChecked()) {
                    Log.d("xie", "remenber.....");
                    remenber(name, password);
                }
                RequestBody body = new FormBody.Builder().add("jobNumber", name)
                        .add("password", password)
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
            String result = response.body().string();
            LoginResponseJson loginResponseJson =
                    JsonUtil.parsoJsonWithGson(result, LoginResponseJson.class);
            if (loginResponseJson.code == 0) {
                LoginManager.getInstance(mContext).setToken(loginResponseJson.data.token);
                LoginManager.getInstance(mContext).getEmployee(loginResponseJson.data.empDO.id, mGetEmployeeDoneCallBack);
            }
        }
    };

    private LoginManager.GetEmployeeDoneCallBack mGetEmployeeDoneCallBack = new LoginManager.GetEmployeeDoneCallBack() {
        @Override
        public void getEmployeeDone() {
            Intent intent = new Intent(mContext, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
