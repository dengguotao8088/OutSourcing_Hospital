package jinxin.out.com.jinxin_employee;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uuzuche.lib_zxing.activity.CaptureFragment;

/**
 * Created by admin on 2017/8/18.
 */

public class SaoyiSao extends CaptureFragment {
    public HomeActivity mActivity;
    public boolean isViewCreate;
    public BaseFragment mParentFragment;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mView = super.onCreateView(inflater, container, savedInstanceState);
        ImageView back = mView.findViewById(R.id.saoyisao_back);
        back.setOnClickListener(mBackListener);
        isViewCreate = true;
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreate = false;
    }

    public View.OnClickListener mBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivity.showContent(mParentFragment);
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mParentFragment == null || mActivity == null) return false;
        mActivity.showContent(mParentFragment);
        return true;
    }
}
