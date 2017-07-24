package jinxin.out.com.jinxinhospital;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.widget.TextView;

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

    private int mCurrentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init JPush
        //android.util.Log.d("xie", "IPush  Init");
        //JPushInterface.setDebugMode(true);
        //JPushInterface.init(this);

        initView();
    }

    private void initView() {
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
            mContentPager.setCurrentItem(position);
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
            Log.d(TAG, "position: " + position);
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

}
