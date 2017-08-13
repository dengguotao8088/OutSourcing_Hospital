package jinxin.out.com.jinxinhospital;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by Administrator on 2017/7/9.
 */

public class BaseFragment extends Fragment {

    public BaseFragment mParentFragment;

    public MainActivity mActivity;
    public void onPageChange(int newPage) {
    }

    public View.OnClickListener mBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           mActivity.showContent(mParentFragment);
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("xie", "onKeyDown........");
        if (mParentFragment == null || mActivity == null){
            return false;
        }
        mActivity.showContent(mParentFragment);
        return true;
    }
}
