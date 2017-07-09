package jinxin.out.com.jinxinhospital;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/7/9.
 */

public class HomePageFragment extends BaseFragment {

    private static final int CHANGE_SHOW_IMAGE = 0x111;

    private int mIndex;

    private int mCurrentPage;
    private int mCurrentShow;

    private ImageView mShowImageView;

    private Button mYHBtn;
    private Button mNewsBtn;
    private int colorId;

    private int[] mHomePageShow = new int[]{
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3,
            R.drawable.banner4,
            R.drawable.banner5
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndex = getArguments().getInt(MainActivity.KEY_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_page, container, false);
        mShowImageView = view.findViewById(R.id.home_page_show_img);
        mShowImageView.setImageResource(mHomePageShow[0]);
        mYHBtn = view.findViewById(R.id.home_page_yihurenyuan_btn);
        mYHBtn.setBackgroundColor(Color.WHITE);
        mYHBtn.setTextColor(getResources().getColor(R.color.colorHomePageBlue));
        mYHBtn.setOnClickListener(mYHOnclickListener);
        mNewsBtn = view.findViewById(R.id.home_page_news_btn);
        mNewsBtn.setOnClickListener(mNewsBtnOnclickListener);
        mCurrentShow = 0;
        return view;
    }

    private View.OnClickListener mYHOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mYHBtn.setBackgroundColor(Color.WHITE);
            mYHBtn.setTextColor(getResources().getColor(R.color.colorHomePageBlue));
            mNewsBtn.setBackgroundColor(getResources().getColor(R.color.colorHomePageBlue));
            mNewsBtn.setTextColor(Color.WHITE);
        }
    };

    private View.OnClickListener mNewsBtnOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mNewsBtn.setBackgroundColor(Color.WHITE);
            mYHBtn.setBackgroundColor(getResources().getColor(R.color.colorHomePageBlue));
            mNewsBtn.setTextColor(getResources().getColor(R.color.colorHomePageBlue));
            mYHBtn.setTextColor(Color.WHITE);
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler.sendEmptyMessageDelayed(CHANGE_SHOW_IMAGE, 2000);
    }

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_SHOW_IMAGE:
                    mCurrentShow = (mCurrentShow + 1) % 5;
                    mShowImageView.setImageResource(mHomePageShow[mCurrentShow]);
                    if (mCurrentPage == mIndex) {
                        mHandler.removeMessages(CHANGE_SHOW_IMAGE);
                        mHandler.sendEmptyMessageDelayed(CHANGE_SHOW_IMAGE, 1500);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onPageChange(int newPage) {
        mCurrentPage = newPage;
        mHandler.removeMessages(CHANGE_SHOW_IMAGE);
        if (mCurrentPage == mIndex) {
            mHandler.sendEmptyMessageDelayed(CHANGE_SHOW_IMAGE, 1500);
        }
    }
}
