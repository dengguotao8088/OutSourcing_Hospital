package jinxin.out.com.jinxinhospital;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jinxin.out.com.jinxinhospital.Employee.Employee;
import jinxin.out.com.jinxinhospital.Employee.EmployeeResponseJson;
import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.Customer.LoginResponseJson;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.News.News;
import jinxin.out.com.jinxinhospital.News.NewsContentResponseJson;
import jinxin.out.com.jinxinhospital.News.NewsResponseJson;
import jinxin.out.com.jinxinhospital.Notice.NoticeResponseJson;
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
    private static final int ADD_EMPLOYEE_TO_LIST = 0x112;
    private static final int ADD_NEWS_TO_LIST = 0x113;
    private static final int SHOW_NEWS_CONTENT = 0x114;
    private static final int SHOW_EMPLOYEE_CONTENT = 0x114;
    private static final String TAG = "HomePageFragment";

    private int mIndex;

    private int mCurrentPage;
    private int mCurrentShow;

    private ImageView mShowImageView;
    private UserListView mListView;

    private Button mYHBtn;
    private Button mNewsBtn;
    private int colorId;
    private TextView mNoticeView;

    private NewsResponseJson mNewsResponseJson;
    private NewsContentResponseJson mNewsContentResponseJson;
    private EmployeeResponseJson mEmployeesResponseJson;
    private Context mContext;
    private MainActivity mMainContext;
    private EmployeeMyAdapter mEmployeeAdpter;
    private NewsMyAdapter mNewsMyAdapter;
    private MyHandler myHandler;

    private String mNotice;
    private List<News> mNewsList = new ArrayList<>();
    private List<Employee> mEmployeeList = new ArrayList<>();

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

    private class MyHandler extends Handler {
        public MyHandler(Context context) {
            super(context.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 000:
                    mNewsMyAdapter = new NewsMyAdapter();
                    mEmployeeAdpter = new EmployeeMyAdapter();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainContext = (MainActivity)context;
        myHandler = new MyHandler(mMainContext);
        myHandler.sendEmptyMessage(000);
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
        mNoticeView = view.findViewById(R.id.notice);
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
      //      NetPostUtil.post(Constants.GET_NEWS_CONTENT_WITH_ID, requestBody, mNewsContentListCallback);
        }
    };

    private void addEmployeeToList() {
        Log.d("xie", "AddEmployeeToList...");
        mListView.setAdapter(mEmployeeAdpter);
        //TODO: 员工详情显示
        //mListView.setOnItemClickListener(mNewsOnItemClickListener);
    }
    private void addNewsToList() {
        Log.d("xie", "AddNewsToList...");
        mListView.setAdapter(null);
        mListView.setAdapter(mNewsMyAdapter);
        //TODO: 新闻详情显示
        mListView.setOnItemClickListener(mNewsOnItemClickListener);
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

        RequestBody requestBodyNotice = new FormBody.Builder().build();
        NetPostUtil.post(Constants.GET_NOTICE, requestBodyNotice, mNoticeCallback);
    }

    private Callback mNewsContentListCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG,"mNewsListCallback onFailure...");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            if (result.contains("502  Bad Gateway")) {
                return;
            }
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0 ) {
                return;
            }
            mNewsResponseJson =
                    JsonUtil.parsoJsonWithGson(result, NewsResponseJson.class);
            mNewsList.clear();
            for(int i=0; i<mNewsResponseJson.data.length; i++) {
                mNewsList.add(mNewsResponseJson.data[i]);
            }
            mHandler.removeMessages(ADD_NEWS_TO_LIST);
            mHandler.sendEmptyMessage(ADD_NEWS_TO_LIST);
        }
    };

    private Callback mNoticeCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG,"mNoticeCallback onFailure...");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie" , "result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            }
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0 ) {
                return;
            }
            NoticeResponseJson noticeJson = JsonUtil.parsoJsonWithGson(result, NoticeResponseJson.class);
            mNotice = noticeJson.data[0].content;
            mHandler.sendEmptyMessage(0x33);
        }
    };

    private Callback mEmployeesListCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG,"mEmployeesListCallback onFailure...");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie" , "result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            }
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0 ) {
                return;
            }
            mEmployeesResponseJson =
                    JsonUtil.parsoJsonWithGson(result, EmployeeResponseJson.class);
            mEmployeeList.clear();
            for(int i=0; i<mEmployeesResponseJson.data.length; i++) {
                mEmployeeList.add(mEmployeesResponseJson.data[i]);
            }
            mHandler.removeMessages(ADD_EMPLOYEE_TO_LIST);
            mHandler.sendEmptyMessage(ADD_EMPLOYEE_TO_LIST);
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
                case ADD_EMPLOYEE_TO_LIST:
                    addEmployeeToList();
                    break;
                case ADD_NEWS_TO_LIST:
                    addNewsToList();
                    break;
                case 0x33:
                    mNoticeView.setText(mNotice);
                case SHOW_NEWS_CONTENT:
//                    NewsContentFragment mNewsContentFragment = new NewsContentFragment();
//                    Bundle data = new Bundle();
//                    data.putString("title", mNewsContentResponseJson.data.title);
//                    data.putString("content", mNewsContentResponseJson.data.coverPath);
//                    mNewsContentFragment.setArguments(data);
//                    mMainContext.showContent(mNewsContentFragment);
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

    private class EmployeeMyAdapter extends BaseAdapter {

        public class ViewHolder {
            public ImageView mImageView;
            public TextView name;
            public TextView summary;
            public LinearLayout mItem;
        }

        @Override
        public int getCount() {
            return mEmployeeList.size();
        }

        @Override
        public Object getItem(int i) {
            return mEmployeeList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.news_item, viewGroup, false);
                holder = new ViewHolder();
                holder.mImageView = view.findViewById(R.id.news_img);
                holder.name = view.findViewById(R.id.news_title);
                holder.summary = view.findViewById(R.id.news_content);
                holder.mItem = view.findViewById(R.id.news_item);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final Employee data = mEmployeeList.get(i);
            holder.name.setText(data.name);
            holder.summary.setText(data.summary);
            holder.mImageView.setImageResource(R.drawable.user);
            holder.mItem.setTag(R.id.tag_first, false);
            holder.mItem.setTag(R.id.tag_second, data.id);
            holder.mItem.setOnClickListener(onItemClickListener);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        URL url= new URL(data.avatarPath);
//                        Bitmap pngBM = BitmapFactory.decodeStream(url.openStream());
//                        holder.mImageView.setImageBitmap(pngBM);
//                    } catch (MalformedURLException e) {
//
//                    } catch (IOException e) {
//
//                    }
//                }
//            }).start();
            //TODO:
            return view;
        }
    }

    private class NewsMyAdapter extends BaseAdapter {

        public class ViewHolder {
            public ImageView mImageView;
            public TextView name;
            public TextView summary;
            public LinearLayout mItem;
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public Object getItem(int i) {
            return mNewsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.news_item, viewGroup, false);
                holder = new ViewHolder();
                holder.mImageView = view.findViewById(R.id.news_img);
                holder.name = view.findViewById(R.id.news_title);
                holder.summary = view.findViewById(R.id.news_content);
                holder.mItem = view.findViewById(R.id.news_item);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final News data = mNewsList.get(i);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        URL url= new URL(data.coverPath);
//                        Bitmap pngBM = BitmapFactory.decodeStream(url.openStream());
//                        holder.mImageView.setImageBitmap(pngBM);
//                    } catch (MalformedURLException e) {
//
//                    } catch (IOException e) {
//
//                    }
//                }
//            }).start();

            holder.mImageView.setImageResource(R.drawable.user);
            holder.name.setText(data.title);
            holder.summary.setText(data.summary);
            holder.mItem.setTag(R.id.tag_first, true);
            holder.mItem.setTag(R.id.tag_second, data.id);
            holder.mItem.setOnClickListener(onItemClickListener);
            //TODO:
            return view;
        }
    }

    private NewsContentFragment newsContentFragment;
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("xie", "222222222222222222222222222222");
            Boolean isNews = (Boolean) view.getTag(R.id.tag_first);
            int id = (int) view.getTag(R.id.tag_second);
            Bundle bundle = new Bundle();
            if (isNews) {
                bundle.putString("type", "news");
            } else  {
                bundle.putString("type", "employee");
            }
            bundle.putInt("id", id);
            Intent intent = new Intent("android.intent.action.SHOW_DETAIL_ACTION");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };
}
