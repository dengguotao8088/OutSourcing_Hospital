package jinxin.out.com.jinxinhospital;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jinxin.out.com.jinxinhospital.Employee.EmployeeResponseJson;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.LoginResponseJson;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.News.News;
import jinxin.out.com.jinxinhospital.News.NewsListResponseJson;
import jinxin.out.com.jinxinhospital.News.NewsResponseJson;
import jinxin.out.com.jinxinhospital.view.UserListView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/9.
 */

public class HomePageFragment extends BaseFragment {

    private static final int CHANGE_SHOW_IMAGE = 0x111;
    private static final String TAG = "HomePageFragment";

    private int mIndex;

    private int mCurrentPage;
    private int mCurrentShow;

    private ImageView mShowImageView;
    private UserListView mListView;

    private Button mYHBtn;
    private Button mNewsBtn;
    private int colorId;

    private NewsResponseJson mNewsResponseJson;
    private EmployeeResponseJson mEmployeesResponseJson;
    private Context mContext;

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
        mContext = getContext();
        View view = inflater.inflate(R.layout.home_page, container, false);
        mShowImageView = view.findViewById(R.id.home_page_show_img);
        mShowImageView.setImageResource(mHomePageShow[0]);
        mYHBtn = view.findViewById(R.id.home_page_yihurenyuan_btn);
        mYHBtn.setBackgroundColor(Color.WHITE);
        mYHBtn.setTextColor(getResources().getColor(R.color.colorHomePageBlue));
        mYHBtn.setOnClickListener(mYHOnclickListener);
        mNewsBtn = view.findViewById(R.id.home_page_news_btn);
        mNewsBtn.setOnClickListener(mNewsBtnOnclickListener);
        mListView = view.findViewById(R.id.home_page_list);
        mCurrentShow = 0;
        //默认显示员工数据
        getEmpDataFromHttp();
        return view;
    }

    private View.OnClickListener mYHOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getEmpDataFromHttp();
            mYHBtn.setBackgroundColor(Color.WHITE);
            mYHBtn.setTextColor(getResources().getColor(R.color.colorHomePageBlue));
            mNewsBtn.setBackgroundColor(getResources().getColor(R.color.colorHomePageBlue));
            mNewsBtn.setTextColor(Color.WHITE);
        }
    };

    private View.OnClickListener mNewsBtnOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getDataFromHttp();
            Log.d("xie","mNewsBtnOnclickListener");
            mNewsBtn.setBackgroundColor(Color.WHITE);
            mYHBtn.setBackgroundColor(getResources().getColor(R.color.colorHomePageBlue));
            mNewsBtn.setTextColor(getResources().getColor(R.color.colorHomePageBlue));
            mYHBtn.setTextColor(Color.WHITE);
        }
    };

    private AdapterView.OnItemClickListener mNewsOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            int id = mNewsResponseJson.data[i].id;
            RequestBody requestBody = new FormBody.Builder()
                    .add("id", String.valueOf(id))
                    .build();
            NetPostUtil.post(Constants.GET_NEWS_CONTENT_WITH_ID, requestBody, mNewsContentListCallback);
        }
    };

    private void AddEmployeeToList() {
        Log.d("xie", "AddEmployeeToList...");
        SimpleAdapter mAdpter = new SimpleAdapter(mContext, getEmployeesListFromJson(),
                R.layout.news_item,
                new String[]{"img", "title", "content"},
                new int[]{R.id.news_img, R.id.news_title, R.id.news_content});
        mListView.setAdapter(mAdpter);
        //TODO: 员工详情显示
        //mListView.setOnItemClickListener(mNewsOnItemClickListener);
    }
    private void AddNewsToList() {
        Log.d("xie", "AddNewsToList...");
        mListView.setAdapter(null);
        SimpleAdapter mAdpter = new SimpleAdapter(mContext, getNewsListFromJson(),
                R.layout.news_item,
                new String[]{"img", "title", "content"},
                new int[]{R.id.news_img, R.id.news_title, R.id.news_content});
        mListView.setAdapter(mAdpter);
        //TODO: 新闻详情显示
        //mListView.setOnItemClickListener(mNewsOnItemClickListener);
    }

    private void getDataFromHttp(){
        //获取新闻列表
        RequestBody requestBodyNews = new FormBody.Builder().add("page", "1")
                .add("size", "10")
                .build();
        NetPostUtil.post(Constants.GET_NEWS_LIST, requestBodyNews, mNewsContentListCallback);
    }
    private void getEmpDataFromHttp(){
        mListView.setAdapter(null);
        //获取员工列表
        RequestBody requestBodyEmp = new FormBody.Builder().add("page", "1")
                .add("size", "10")
                .build();
        NetPostUtil.post(Constants.GET_EMPLOYEE_LIST, requestBodyEmp, mEmployeesListCallback);
    }

    private ArrayList<HashMap<String,Object>> getNewsListFromJson() {
        ArrayList<HashMap<String,Object>> mGroupView = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> mMap;

        if (mNewsResponseJson != null ) {
                for (int i = 0; i < mNewsResponseJson.data.length; i++) {
                    mMap = new HashMap<String, Object>();
                    //TODO:加载网络图片  news.data.coverPath
                    mMap.put("img", R.drawable.gr1);
                    mMap.put("title", mNewsResponseJson.data[i].title);
                    mMap.put("content", mNewsResponseJson.data[i].content);
                    mGroupView.add(mMap);
                }
        }
        return mGroupView;
    }

    private ArrayList<HashMap<String,Object>> getEmployeesListFromJson() {
        ArrayList<HashMap<String,Object>> mGroupView = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> mMap;

        if (mEmployeesResponseJson != null ) {
            for (int i = 0; i < mEmployeesResponseJson.data.length; i++) {
                mMap = new HashMap<String, Object>();
                //TODO:加载网络图片  news.data.coverPath
                mMap.put("img", R.drawable.gr1);
                mMap.put("title", mEmployeesResponseJson.data[i].name);
                mMap.put("content", mEmployeesResponseJson.data[i].shows);
                mGroupView.add(mMap);
            }
        }
        return mGroupView;
    }
    private Callback mNewsContentListCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG,"mNewsListCallback onFailure...");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            mNewsResponseJson =
                    JsonUtil.parsoJsonWithGson(result, NewsResponseJson.class);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AddNewsToList();
                }
            });
        }
    };

    private Callback mEmployeesListCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG,"mEmployeesListCallback onFailure...");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d("xie", "mEmployeesListCallback onResponse...");
            String result = response.body().string();
            mEmployeesResponseJson =
                    JsonUtil.parsoJsonWithGson(result, EmployeeResponseJson.class);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AddEmployeeToList();
                }
            });
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
