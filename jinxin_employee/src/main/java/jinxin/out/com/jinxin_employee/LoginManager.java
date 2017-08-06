package jinxin.out.com.jinxin_employee;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import jinxin.out.com.jinxin_employee.JsonModule.Employee;
import jinxin.out.com.jinxin_employee.JsonModule.EmployeeResponseJson;
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
    private Employee mEmployee;

    public LoginManager(Context context) {
        mContext = context;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getToken() {
        return mToken;
    }

    public void getEmployee(int id, GetEmployeeDoneCallBack callBack) {
        final GetEmployeeDoneCallBack doneCallBack = callBack;
        RequestBody body = new FormBody.Builder().add("token", mToken).add("id", id + "").build();
        NetPostUtil.post(Constants.GET_EMPLOYEE_WITH_ID, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                EmployeeResponseJson employeeResponseJson =
                        JsonUtil.parsoJsonWithGson(result, EmployeeResponseJson.class);
                if (employeeResponseJson.code == 0) {
                    mEmployee = employeeResponseJson.data;
                    doneCallBack.getEmployeeDone();
                }
            }
        });
    }

    public Employee getEmployee(){
        return mEmployee;
    }

    public interface GetEmployeeDoneCallBack {
        public void getEmployeeDone();
    }
}
