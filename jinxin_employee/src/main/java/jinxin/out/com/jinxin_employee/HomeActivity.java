package jinxin.out.com.jinxin_employee;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private int mCurrentSelect = 0;
    private TabItem mTabItem_MyCus;
    private TabItem mTabItem_Camera;
    private TabItem mTabItem_TuiFei;
    private TabItem mTabs[];

    private BaseFragment mCurrentFragment;
    private BaseFragment mTuiFeiQianMingFragment;
    private SaoyiSao mCaptureFragment;
    private MyCustormFragment myCustormFragment;
    private TuiFeiFragment mtuifeiFragment;

    private static final String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        verifyStoragePermissions(this);
        initTab();
        myCustormFragment = new MyCustormFragment();
        mCurrentFragment = myCustormFragment;
        showContent(mCurrentFragment);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        List<String> list = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS.length; i++) {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    PERMISSIONS[i]);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                list.add(PERMISSIONS[i]);
            }
        }
        if (list.size() > 0) {
            ActivityCompat.requestPermissions(activity, list.toArray(new String[list.size()]),
                    0);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        finish();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
            if (mCurrentFragment != null) {
                showContent(mCurrentFragment);
            }
        }
    };

    private View.OnClickListener mOnClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            refreshTab(1);
            if (mCaptureFragment == null) {
                mCaptureFragment = new SaoyiSao();
                CodeUtils.setFragmentArgs(mCaptureFragment, R.layout.saoyisao_layout);
                mCaptureFragment.setAnalyzeCallback(analyzeCallback);
                mCaptureFragment.getView();
                mCaptureFragment.mActivity = HomeActivity.this;
                mCaptureFragment.mParentFragment = myCustormFragment;
            }
            showSaoYiSao(mCaptureFragment);
        }
    };

    private View.OnClickListener mOnClickListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            refreshTab(2);
            if (mtuifeiFragment == null) {
                mtuifeiFragment = new TuiFeiFragment();
                mtuifeiFragment.mParentFragment = myCustormFragment;
            }
            showContent(mtuifeiFragment);
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

    public void showContent(BaseFragment fragment) {
        if (!(fragment instanceof TuiFeiFragment)) {
            if (fragment instanceof QianMing) {
                int mode = ((QianMing) fragment).mode;
                if (mode == QianMing.MODE_ZHIQIN) {
                    mCurrentFragment = fragment;
                } else {
                    mTuiFeiQianMingFragment = fragment;
                }
            } else {
                mCurrentFragment = fragment;
                mTuiFeiQianMingFragment = null;
            }
        } else {
            mTuiFeiQianMingFragment = null;
        }

        if (fragment instanceof MyCustormFragment) {
            refreshTab(0);
        }
        if (fragment.mActivity == null) {
            fragment.mActivity = this;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction
                = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }

    public void showSaoYiSao(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction
                = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }

    private KProgressHUD mHUD;

    public void showHUD(String text) {
        mHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        mHUD.setLabel(text);
        mHUD.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mHUD != null) {
            mHUD.dismiss();
        }

    }

    public void dissmissHUD() {
        if (mHUD != null) {
            mHUD.dismiss();
        }
    }

    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Bundle data = new Bundle();
            data.putString("search_data", result);
            myCustormFragment.setArguments(data);
            showContent(myCustormFragment);
            refreshTab(0);
        }

        @Override
        public void onAnalyzeFailed() {
            Log.d("dengguotao", "fail ");
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result;
        if (keyCode != KeyEvent.KEYCODE_BACK || (mCurrentFragment == null
                && mTuiFeiQianMingFragment == null)) {
            return super.onKeyDown(keyCode, event);
        }
        if (mTuiFeiQianMingFragment != null) {
            result = mTuiFeiQianMingFragment.onKeyDown(keyCode, event);
        } else if (mtuifeiFragment != null && mtuifeiFragment.isViewCreate) {
            result = mtuifeiFragment.onKeyDown(keyCode, event);
        } else if (mCaptureFragment != null && mCaptureFragment.isViewCreate) {
            result = mCaptureFragment.onKeyDown(keyCode, event);
        } else {
            result = mCurrentFragment.onKeyDown(keyCode, event);
        }
        if (!result) {
            return super.onKeyDown(keyCode, event);
        }
        return result;
    }
}
