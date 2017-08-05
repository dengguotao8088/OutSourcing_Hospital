package jinxin.out.com.jinxin_employee;

import android.app.Application;

/**
 * Created by Administrator on 2017/7/18.
 */

public class BaseApplication extends Application {
    private LoginManager mLoginManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mLoginManager = LoginManager.getInstance(getApplicationContext());
    }
}
