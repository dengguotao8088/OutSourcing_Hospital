package jinxin.out.com.jinxin_employee;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Administrator on 2017/7/18.
 */

public class BaseApplication extends Application {
    private LoginManager mLoginManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mLoginManager = LoginManager.getInstance(getApplicationContext());
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this);
        //builder.memoryCacheExtraOptions(320, 480);
        ImageLoader.getInstance().init(builder.build());
    }
}
