package jinxin.out.com.jinxinhospital;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.view.UserListView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/11.
 */

public class UserFragment extends BaseFragment {

    private View mView;
    private Context mContext;
    private TextView mNameText;
    private TextView mTelText;
    private UserListView mListView;
    private MainActivity mainActivity;

    private ArrayList<HashMap<String, Object>> mGroupView;
    private HashMap<String, Object> mMap;
    private SimpleAdapter mAdapter;
    private Intent mIntent;
    private Bundle bundle;
    private String token;
    private String tel;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.user_page, container, false);

        mContext = getContext();
        sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
        token = sharedPreferences.getString("token", "");
        tel = sharedPreferences.getString("tel", "");
        mNameText = mView.findViewById(R.id.user_name);
        mTelText = mView.findViewById(R.id.user_tel);
        mListView = mView.findViewById(R.id.user_listview);

        mTelText.setText(sharedPreferences.getString("tel", null));
        mNameText.setText(sharedPreferences.getString("name", null));
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
                    startActivity(mIntent);
                    break;
                case 2:
                    //todo: 知情同意书
                    mIntent = new Intent("android.intent.action.INFORMEDLIST");
                    bundle = new Bundle();
                    mIntent.putExtras(bundle);
                    startActivity(mIntent);
                    break;
                case 3:
                    mIntent = new Intent("android.intent.action.USERCONTACTME");
                    bundle = new Bundle();
                    mIntent.putExtras(bundle);
                    startActivity(mIntent);
                    break;

                case 4:
                    break;
                case 5:
                    loginOut();
                    break;

                default:

                    break;
            }
        }
    }

    private void loginOut() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("mobile", tel)
                .build();
        NetPostUtil.post(Constants.LOGIN_OUT_URL, requestBody, mLoginOutCallback);
    }
    Callback mLoginOutCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie" , "mLoginOutCallback result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            }
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0 ) {
                return;
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", "");
            editor.putInt("customerId", -1);
            editor.putString("tel", "");
            editor.putString("name", "");
            editor.commit();
            //((MainActivity)getContext()).setHomeItem(0);
            mIntent = new Intent(mContext, LoadActivity.class);
            startActivity(mIntent);
        }
    };

    @Override
    public void onPageChange(int newPage) {
        super.onPageChange(newPage);
    }
}
