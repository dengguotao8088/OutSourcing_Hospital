package jinxin.out.com.jinxin_employee;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private Employee mEmployee = new Employee();
    private ConnectivityManager mConnectivityManager;
    private SharedPreferences mSharedPreferences;

    public LoginManager(Context context) {
        mContext = context;
        mConnectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mSharedPreferences = mContext.getSharedPreferences("emp_msg", Context.MODE_PRIVATE);
        loadEmp();
    }

    public boolean isNetworkConnected() {
        if (mContext != null) {
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public boolean isWifiConnected() {
        if (mContext != null) {
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /*
public int id;//
public String jobNumber;//员工工号
public String name;//
public int sex;//性别，1：男，2：女；默认1
public String avatarPath;//员工头像
 */
    public void saveEmp() {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("token", mToken);
            editor.putInt("id", mEmployee.id);
            editor.putString("jobNumber", mEmployee.jobNumber);
            editor.putString("name", mEmployee.name);
            editor.putString("avatarPath", mEmployee.avatarPath);
            editor.putInt("sex", mEmployee.sex);
            editor.commit();
        }
    }

    public void loadEmp() {
        if (mSharedPreferences != null) {
            mToken = mSharedPreferences.getString("token", null);
            mEmployee.id = mSharedPreferences.getInt("id", -1);
            mEmployee.jobNumber = mSharedPreferences.getString("jobNumber", null);
            mEmployee.name = mSharedPreferences.getString("name", null);
            mEmployee.avatarPath = mSharedPreferences.getString("avatarPath", null);
            mEmployee.sex = mSharedPreferences.getInt("sex", -1);
        }
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getToken() {
        return mToken;
    }

    public Employee getEmployee() {
        return mEmployee;
    }

    public void setEmployee(Employee employee) {
        mEmployee = employee;
    }

}
