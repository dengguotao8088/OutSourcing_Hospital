package jinxin.out.com.jinxin_employee;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kaopiz.kprogresshud.KProgressHUD;

public class HomeActivity extends AppCompatActivity {

    private int mCurrentSelect = 0;
    private TabItem mTabItem_MyCus;
    private TabItem mTabItem_Camera;
    private TabItem mTabItem_TuiFei;
    private TabItem mTabs[];

    private Fragment mCurrentFragment;
    private MyCustormFragment myCustormFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initTab();
        myCustormFragment = new MyCustormFragment();
        mCurrentFragment = myCustormFragment;
        showContent(mCurrentFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initTab() {
        mTabs = new TabItem[3];
        mTabItem_MyCus = findViewById(R.id.my_cus);
        mTabItem_Camera = findViewById(R.id.saoyisao);
        mTabItem_TuiFei = findViewById(R.id.tuifei);
        mTabs[0] = mTabItem_MyCus;
        mTabs[1] = mTabItem_Camera;
        mTabs[2] = mTabItem_TuiFei;
        mTabItem_MyCus.content_img.setImageResource(R.drawable.trust);
        mTabItem_MyCus.content_text.setText(R.string.tab_item1);
        mTabItem_MyCus.topView.setBackground(getDrawable(R.drawable.tab_background));
        mTabItem_MyCus.content_layout.setBackgroundColor(getColor(R.color.tab_bar_pressed));
        mTabItem_MyCus.setOnClickListener(mOnClickListener1);
        mTabItem_Camera.content_img.setImageResource(R.drawable.scanning);
        mTabItem_Camera.content_text.setText(R.string.tab_item2);
        mTabItem_Camera.setOnClickListener(mOnClickListener2);
        mTabItem_TuiFei.content_img.setImageResource(R.drawable.component);
        mTabItem_TuiFei.content_text.setText(R.string.tab_item3);
        mTabItem_TuiFei.setOnClickListener(mOnClickListener3);
    }

    private View.OnClickListener mOnClickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            refreshTab(0);
        }
    };

    private View.OnClickListener mOnClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            refreshTab(1);
        }
    };

    private View.OnClickListener mOnClickListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            refreshTab(2);
        }
    };

    void refreshTab(int newSelect) {
        if (newSelect > 2) return;
        if (mCurrentSelect != newSelect) {
            mTabs[mCurrentSelect].topView.setBackground(null);
            mTabs[mCurrentSelect].content_layout.setBackgroundColor(getColor(R.color.tab_bar));
            mTabs[newSelect].topView.setBackground(getDrawable(R.drawable.tab_background));
            mTabs[newSelect].content_layout.setBackgroundColor(getColor(R.color.tab_bar_pressed));
            mCurrentSelect = newSelect;
        }
    }

    private void showContent(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction
                = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }


    private KProgressHUD mHUD;

    public void showHUD(String text) {
        if (mHUD == null) {
            mHUD = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f);
        }
        mHUD.setLabel(text);
        mHUD.show();
    }

    public void dissmissHUD() {
        if (mHUD != null) {
            mHUD.dismiss();
        }
    }
}
