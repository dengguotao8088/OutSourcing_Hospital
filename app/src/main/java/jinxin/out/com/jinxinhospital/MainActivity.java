package jinxin.out.com.jinxinhospital;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_POSITION = "key_position";
    String TAG = "dengguotao";
    public static final int HOME_POSITION = 0;
    public static final int CURRENT_CHECK_POSITION = 1;
    public static final int VIP_POSITION = 2;
    public static final int HEALTH_MANAGE_POSITION = 3;
    public static final int USER_MANAGE_POSITION = 4;


    private TextView mTitle;
    private TabLayout mTabLayout;
    private ViewPager mContentPager;
    private TabsAdapter mTabsAdapter;
    private Context mContext;
    private BaseFragment mCurrentFragment;
    private LinearLayout mTitleLayout;

    private  String token = null;
    private  int mCustomerId = -1;
    private  String tel;
    private  String name;
    private boolean vip;
    private int mCurrentTab = 0;
    private int mOlderTab = 0;
    private SharedPreferences sharedPreferences;
    private static final String[] PERMISSIONS_STORAGE = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        //Init JPush
        android.util.Log.d("xie", "IPush  Init");
//        JPushInterface.setDebugMode(true);
//        JPushInterface.init(this);

        verifyStoragePermissions(this);
        initView();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity,  PERMISSIONS_STORAGE,
                    0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED
                        || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // Permission Granted
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        mTitleLayout = findViewById(R.id.title_layout);
        mTitle = findViewById(R.id.title);
        mTabLayout = findViewById(R.id.tab);
        mTabLayout.addOnTabSelectedListener(mTabSelectedListener);
        mContentPager = findViewById(R.id.content_pager);
        mTabsAdapter = new TabsAdapter(this);
        mContentPager.setAdapter(mTabsAdapter);
        mContentPager.addOnPageChangeListener(mTabsAdapter);
        createTabs();
    }

    private void createTabs() {
        final TabLayout.Tab homeTab = mTabLayout.newTab();
        homeTab.setIcon(R.drawable.navigation2);
        homeTab.setText(R.string.page_home);
        mTabsAdapter.addTab(homeTab, HomePageFragment.class, HOME_POSITION);

        final TabLayout.Tab currentCheckTab = mTabLayout.newTab();
        currentCheckTab.setIcon(R.drawable.navigation1);
        currentCheckTab.setText(R.string.page_current_check);
        mTabsAdapter.addTab(currentCheckTab, CurrentCheckFragment.class, CURRENT_CHECK_POSITION);

        final TabLayout.Tab vipTab = mTabLayout.newTab();
        vipTab.setIcon(R.drawable.navigation5);
        vipTab.setText(R.string.page_vip);
        mTabsAdapter.addTab(vipTab, VipFragment.class, VIP_POSITION);

        final TabLayout.Tab healthManageTab = mTabLayout.newTab();
        healthManageTab.setIcon(R.drawable.navigation4);
        healthManageTab.setText(R.string.page_health_manage);
        mTabsAdapter.addTab(healthManageTab, HealthManageFragment.class, HEALTH_MANAGE_POSITION);

        final TabLayout.Tab userManageTab = mTabLayout.newTab();
        userManageTab.setIcon(R.drawable.navigation3);
        userManageTab.setText(R.string.page_user);
        mTabsAdapter.addTab(userManageTab, UserFragment.class, USER_MANAGE_POSITION);

        mContentPager.setCurrentItem(mCurrentTab);
        mTabLayout.getTabAt(mCurrentTab).select();
        mTitle.setText(mTabLayout.getTabAt(mCurrentTab).getText());
        mTabsAdapter.notifyPageChanged(mCurrentTab);
    }

    private TabLayout.OnTabSelectedListener mTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int position = (int) tab.getTag();
            Log.d("xie", "onTabSelected = " + position);
            if (sharedPreferences == null) {
                sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
                token = sharedPreferences.getString("token", null);
                mCustomerId = sharedPreferences.getInt("customerId", -1);
                tel = sharedPreferences.getString("tel", null);
                name = sharedPreferences.getString("name", null);
                vip = sharedPreferences.getBoolean("vip", false);

            }
            if (!vip && position == 2) {
                Toast.makeText(mContext, "您还不是VIP客户", Toast.LENGTH_LONG).show();
                mContentPager.setCurrentItem(mOlderTab);
                return;
            }
            Log.d("xie", "token = " + token + ";  mCustomerId = " + mCustomerId + ";  position = " + position);
            if (token == null || mCustomerId < 0) {
                if (position != 0) {
                    Intent intent = new Intent(mContext, LoadActivity.class);
                    startActivity(intent);
                }
            } else {
                mContentPager.setCurrentItem(position);
                mOlderTab = position;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private class TabsAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        final class TabInfo {
            private final Class<?> clss;
            public Bundle arg;

            public TabInfo(Class<?> _class, int position) {
                clss = _class;
                arg = new Bundle();
                arg.putInt(KEY_POSITION, position);
            }

            public int getPosition() {
                return arg.getInt(KEY_POSITION);
            }
        }

        private AppCompatActivity mActivity;
        private List<TabInfo> mTabs = new ArrayList<TabInfo>();
        private Map<Integer, Fragment> mFragments = new HashMap<>();

        public TabsAdapter(AppCompatActivity activity) {
            super(activity.getSupportFragmentManager());
            mActivity = activity;
        }

        public void addTab(TabLayout.Tab tab, Class<?> clss, int position) {
            TabInfo info = new TabInfo(clss, position);
            tab.setTag(position);
            mTabs.add(info);
            mTabLayout.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = mFragments.get(position);
            if (fragment == null) {
                TabInfo info = mTabs.get(position);
                fragment = Fragment.instantiate(mActivity, info.clss.getName(), info.arg);
                mFragments.put(position, fragment);
            }
            return fragment;
        }


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageSelected(int position) {
            mCurrentTab = position;
            mTabLayout.getTabAt(mCurrentTab).select();
            mTitle.setText(mTabLayout.getTabAt(mCurrentTab).getText());
            notifyPageChanged(position);
        }

        private void notifyPageChanged(int newPage) {
            for (int i = 0; i < mFragments.size(); i++) {
                BaseFragment fragment = (BaseFragment) mFragments.get(i);
                if (fragment != null) {
                    fragment.onPageChange(newPage);
                }
            }
        }
    }

    public void showContent(BaseFragment targetFragment) {
        mCurrentFragment = targetFragment;
        if (targetFragment.mActivity == null) {
            targetFragment.mActivity = this;
        }
        if (mCurrentFragment.getClass().equals(HomePageFragment.class)) {
            mContentPager.setCurrentItem(0);
            mTabLayout.setVisibility(View.VISIBLE);
            mTitle.setVisibility(View.VISIBLE);
            mTitleLayout.setVisibility(View.VISIBLE);
            return;
        } else {
            mTitle.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.GONE);
            mTitleLayout.setVisibility(View.GONE);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction
                = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, mCurrentFragment);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK || mCurrentFragment == null) {
            return super.onKeyDown(keyCode, event);
        }
        boolean result = mCurrentFragment.onKeyDown(keyCode, event);
        if (!result) {
            return super.onKeyDown(keyCode, event);
        }
        return result;
    }

}
