package jinxin.out.com.jinxinhospital;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init JPush
        android.util.Log.d("xie", "IPush  Init");
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}
