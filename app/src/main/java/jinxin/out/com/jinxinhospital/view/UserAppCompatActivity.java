package jinxin.out.com.jinxinhospital.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import jinxin.out.com.jinxinhospital.R;

/**
 * Created by Administrator on 2017/7/13.
 */

public abstract class UserAppCompatActivity extends AppCompatActivity {
    private TextView mToolbarTitle;
    private TextView mToolbarSubTitle;
    private CharSequence mToolbarString = "";
    private android.support.v7.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolbarSubTitle = (TextView) findViewById(R.id.toolbar_subtitle);
        if (mToolbar != null) {
            //将Toolbar显示到界面  
            setSupportActionBar(mToolbar);
        }
        if (mToolbarTitle != null) {
            //getTitle()的值是activity的android:lable属性值  
            mToolbarTitle.setText(getTitle());
            //设置默认的标题不显示  
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 判断是否有Toolbar,并默认显示返回按钮 
         */
        if (null != getToolbar() && isShowBacking()) {
            showBack();
        }

        if (mToolbarTitle != null) {
            mToolbarTitle.setText(mToolbarString);
        }
    }

    /**
     * 获取头部标题的TextView 
     * @return    
     */
    public TextView getToolbarTitle() {
        Log.d("xie", "getToolbarTitle....");
        return mToolbarTitle;
    }

    /**
     * 获取头部标题的TextView 
     * @return    
     */
    public TextView getSubTitle() {
        return mToolbarSubTitle;
    }

    /**
     * 设置头部标题 
     * @param title    
     */
    public void setToolBarTitle(CharSequence title) {
        mToolbarString =  title;
    }

    /**
     * this Activity of tool bar. 
     * 获取头部. 
     * @return support.v7.widget.Toolbar.   
     */
    public android.support.v7.widget.Toolbar getToolbar() {
        return (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
    }

    /**
     * 版本号小于21的后退按钮图片    
     */
    private void showBack() {
    //setNavigationIcon必须在setSupportActionBar(toolbar);方法后面加入  
        getToolbar().setNavigationIcon(R.drawable.back);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * 是否显示后退按钮,默认显示,可在子类重写该方法. 
     * @return
     */
    protected boolean isShowBacking() {

        return true;
    }

    /**
     * this activity layout res 
     * 设置layout布局,在子类重写该方法. 
     * @return res layout xml id      
     */
    protected abstract int getLayoutId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("xie", "onDestroy...");
    }
}
