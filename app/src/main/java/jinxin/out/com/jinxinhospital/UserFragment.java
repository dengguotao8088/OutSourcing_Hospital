package jinxin.out.com.jinxinhospital;

import android.app.AlertDialog;
import android.app.Dialog;
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
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.user_page, container, false);

        mContext = getContext();
        mNameText = mView.findViewById(R.id.user_name);
        mTelText = mView.findViewById(R.id.user_tel);
        mListView = mView.findViewById(R.id.user_listview);

        mTelText.setText(LoadActivity.getTel());
        mNameText.setText(LoadActivity.getName());
        mAdapter = new SimpleAdapter(mContext, getData(), R.layout.user_item,
                new String[]{"icon", "title", "cache", "arrow"}, new int[]{R.id.icon, R.id.title, R.id.cache, R.id.arrow});

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new listener());

        return mView;
    }

    private ArrayList<HashMap<String,Object>> getData() {
        mGroupView = new ArrayList<HashMap<String, Object>>();


        mMap = new HashMap<String, Object>();
        mMap.put("icon", R.drawable.gr1);
        mMap.put("title", "消息中心");
        mMap.put("cache", "");
        mMap.put("arrow", R.drawable.more);
        mGroupView.add(mMap);

        mMap = new HashMap<String, Object>();
        mMap.put("icon", R.drawable.gr2);
        mMap.put("title", "我的二维码");
        mMap.put("cache", "");
        mMap.put("arrow", R.drawable.more);
        mGroupView.add(mMap);

        mMap = new HashMap<String, Object>();
        mMap.put("icon", R.drawable.gr3);
        mMap.put("title", "知情同意书");
        mMap.put("cache", "");
        mMap.put("arrow", R.drawable.more);
        mGroupView.add(mMap);

        mMap = new HashMap<String, Object>();
        mMap.put("icon", R.drawable.gr4);
        mMap.put("arrow", R.drawable.more);
        mMap.put("cache", "");
        mMap.put("title", "联系我们");
        mGroupView.add(mMap);

        mMap = new HashMap<String, Object>();
        mMap.put("icon", R.drawable.gr7);
        mMap.put("title", "清除缓存");
        mMap.put("cache", getCacheData());
        mMap.put("arrow", "");
        mGroupView.add(mMap);

        mMap = new HashMap<String, Object>();
        mMap.put("icon", R.drawable.gr5);
        mMap.put("cache", "");
        mMap.put("title", "退出当前账号");
        mMap.put("arrow", R.drawable.more);
        mGroupView.add(mMap);

        return mGroupView;
    }

    private String getCacheData() {
        double data = 0.0;
        //todo: get CACHE num:
        return Double.toString(data) + " M";
    }

    private class listener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i) {
                case 0:
                    //todo：消息中心
                    mIntent = new Intent("android.intent.action.USERMESSAGECENTER");
                    startActivity(mIntent);
                    break;
                case 1:
                    //todo: 显示二维码
                    mIntent = new Intent("android.intent.action.QRCODEVIEW");
                    bundle = new Bundle();
                    bundle.putString("name", "Test");
                    bundle.putString("num", "12345678");
                    bundle.putString("tel", "12345678");
                    mIntent.putExtras(bundle);
                    startActivity(mIntent);
                    break;
                case 2:
                    //todo: 知情同意书
                    break;
                case 3:
                    mIntent = new Intent("android.intent.action.USERCONTACTME");
                    bundle = new Bundle();
                    mIntent.putExtras(bundle);
                    startActivity(mIntent);
                    break;

                case 4:
                    break;

                default:
                    mIntent = new Intent(mContext, LoadActivity.class);
                    startActivity(mIntent);
                    break;
            }
        }
    }

    @Override
    public void onPageChange(int newPage) {
        super.onPageChange(newPage);
    }
}
