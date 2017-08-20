package jinxin.out.com.jinxin_employee;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Administrator on 2017/8/6.
 */

public abstract class BaseFragment extends Fragment {
    public static final int LOAD_DATA_DONE = 0x100;
    public static final int LOAD_DATA_ERROR = 0x101;
    public static final int LOAD_DATA_IIMEOUT = 0x102;
    public static final int SHOW_TOAST = 0x103;
    public static final int NEED_RELOGIN = 0x104;

    public BaseFragment mParentFragment;
    public HomeActivity mActivity;

    public boolean isUserHint = false;
    public boolean isViewCreate = false;
    public boolean isFirstShow = true;

    private TextView emptyView;
    private PullToRefreshListView mListView;
    public MainHandler mMainHandler;

    private Toast mToast;

    public class MainHandler extends Handler {
        public MainHandler(Context context) {
            super(context.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_DATA_DONE:
                    mMainHandler.removeMessages(LOAD_DATA_IIMEOUT);
                    if (mListView != null) {
                        mListView.onRefreshComplete();
                    }
                    refreshUI();
                    break;
                case LOAD_DATA_ERROR:
                    mMainHandler.removeMessages(LOAD_DATA_IIMEOUT);
                    if (mListView != null) {
                        mListView.onRefreshComplete();
                    }
                    break;
                case LOAD_DATA_IIMEOUT:
                    mMainHandler.removeMessages(LOAD_DATA_IIMEOUT);
                    if (mListView != null) {
                        mListView.onRefreshComplete();
                    }
                    break;
                case SHOW_TOAST:
                    String text = (String) msg.obj;
                    if (mToast == null) {
                        mToast = Toast.makeText(mActivity, text, Toast.LENGTH_SHORT);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                    break;
                case NEED_RELOGIN:
                    if (mActivity != null && !mActivity.isDestroyed()) {
                        LoginManager.getInstance(mActivity).clearEmp();
                        //LoginManager.getInstance(mActivity).loadEmp();
                        Toast.makeText(mActivity,
                                "登录已过期，请重新登录！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mActivity, LoginActivity.class);
                        mActivity.startActivity(intent);
                        mActivity.finish();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private PullToRefreshBase.OnRefreshListener2<ListView> mListViewOnRefreshListener2 =
            new PullToRefreshBase.OnRefreshListener2<ListView>() {


                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    String label = JsonUtil.getDate(System.currentTimeMillis() + "");
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
                    refreshData();
                    mMainHandler.sendEmptyMessageDelayed(LOAD_DATA_IIMEOUT, 5000);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    String label = JsonUtil.getDate(System.currentTimeMillis() + "");
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
                    loadData();
                    mMainHandler.sendEmptyMessageDelayed(LOAD_DATA_IIMEOUT, 5000);
                }
            };

    public abstract void refreshData();

    public abstract void loadData();

    public abstract void refreshUI();

    public void initListView(PullToRefreshListView listView, TextView emptyView) {
        mListView = listView;
        if (emptyView != null) {
            //mListView.setEmptyView(emptyView);
        }
        mListView.setOnRefreshListener(mListViewOnRefreshListener2);
        mListView.setScrollingWhileRefreshingEnabled(true);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mMainHandler == null) {
            mMainHandler = new MainHandler(getContext());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("dengguotao", "isVisibleToUser: " + isVisibleToUser);
        isUserHint = isVisibleToUser;
        refreshUI();
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
