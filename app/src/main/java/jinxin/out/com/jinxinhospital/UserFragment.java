package jinxin.out.com.jinxinhospital;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import jinxin.out.com.jinxinhospital.view.UserListView;

/**
 * Created by Administrator on 2017/7/11.
 */

public class UserFragment extends BaseFragment {

    private View mView;
    private Context mContext;
    private TextView mNameText;
    private TextView mTelText;
    private UserListView mListView;

    private ArrayList<HashMap<String, Object>> mGroupView;
    private HashMap<String, Object> mMap;
    private SimpleAdapter mAdapter;
    private Intent mIntent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.user_page, container, false);

        mContext = getContext();
        mNameText = mView.findViewById(R.id.user_name);
        mTelText = mView.findViewById(R.id.user_tel);
        mListView = mView.findViewById(R.id.user_listview);

        mAdapter = new SimpleAdapter(mContext, getData(), R.layout.user_item,
                new String[]{"icon", "title", "arrow"}, new int[]{R.id.icon, R.id.title, R.id.arrow});

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new listener());

        return mView;
    }

    private ArrayList<HashMap<String,Object>> getData() {
        mGroupView = new ArrayList<HashMap<String, Object>>();

        mMap = new HashMap<String, Object>();
        mMap.put("icon", R.drawable.gr1);
        mMap.put("title", "消息中心");
        mMap.put("arrow", R.drawable.user_arrow);
        mGroupView.add(mMap);

        mMap = new HashMap<String, Object>();
        mMap.put("icon", R.drawable.gr2);
        mMap.put("title", "我的二维码");
        mMap.put("arrow", R.drawable.user_arrow);
        mGroupView.add(mMap);

        mMap = new HashMap<String, Object>();
        mMap.put("icon", R.drawable.gr3);
        mMap.put("title", "知情同意书");
        mMap.put("arrow", R.drawable.user_arrow);
        mGroupView.add(mMap);

        mMap = new HashMap<String, Object>();
        mMap.put("icon", R.drawable.gr4);
        mMap.put("arrow", R.drawable.user_arrow);
        mMap.put("title", "联系我们");
        mGroupView.add(mMap);

        mMap = new HashMap<String, Object>();
        mMap.put("icon", R.drawable.gr7);
        mMap.put("title", "清除缓存");
        mMap.put("arrow", getCacheData());
        mGroupView.add(mMap);

        mMap = new HashMap<String, Object>();
        mMap.put("icon", R.drawable.gr5);
        mMap.put("title", "退出当前账号");
        mMap.put("arrow", R.drawable.user_arrow);
        mGroupView.add(mMap);

        return mGroupView;
    }

    private String getCacheData() {
        double data = 0.0;
        //todo: get CACHE num:
        return Double.toString(data) + "M";
    }

    private class listener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i) {
                case 0:
                    Toast.makeText(mContext, "first", Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    mIntent = new Intent(mContext, LoadActivity.class);
                    startActivity(mIntent);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onPageChange(int newPage) {
        super.onPageChange(newPage);
    }
}
