package jinxin.out.com.jinxin_employee;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;

/**
 * Created by Administrator on 2017/8/6.
 */

public abstract class BaseFragment extends Fragment {
    public Fragment mParentFragment;

    public abstract boolean onKeyDown(int keyCode, KeyEvent event);
}
