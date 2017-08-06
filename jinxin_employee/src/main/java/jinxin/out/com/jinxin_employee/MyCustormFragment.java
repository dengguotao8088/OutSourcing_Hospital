package jinxin.out.com.jinxin_employee;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jinxin.out.com.jinxin_employee.JsonModule.BaseModule;
import jinxin.out.com.jinxin_employee.JsonModule.Employee;
import jinxin.out.com.jinxin_employee.view.SearchView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/6.
 */

public class MyCustormFragment extends Fragment {

    private HomeActivity mContext;
    private LoginManager mLoginManager;
    private Employee mEmployee;

    private View mView;
    private SearchView mSearchView;
    private ImageView mSearchClose;
    private ListView mList;
    private List<CustormData> mCusDatas = new ArrayList<>();

    private MyAdapter myAdapter;

    private MyHandler mMainHandler;

    private class MyHandler extends Handler {
        public MyHandler(Context context) {
            super(context.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            myAdapter.notifyDataSetChanged();
        }
    }

    public MyCustormFragment() {
        mLoginManager = LoginManager.getInstance(mContext);
        mEmployee = mLoginManager.getEmployee();
        CustormData data = new CustormData();
        data.name = "小李";
        data.birthday = "1500249600000";
        data.mobile = "13702355665";
        data.customerSource = "朋友推荐";
        data.id = 1;
        mCusDatas.add(data);
        mCusDatas.add(data);
        mCusDatas.add(data);
        mCusDatas.add(data);
        mCusDatas.add(data);
        mCusDatas.add(data);
        mCusDatas.add(data);
        mCusDatas.add(data);
        myAdapter = new MyAdapter();
    }

    private Callback mGetCustormCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mContext.dissmissHUD();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            mContext.dissmissHUD();
            String result = response.body().string();
            Log.d("dengguotao", result);
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0) {
                return;
            }
            if (result.contains("[")) {
                module = JsonUtil.parsoJsonWithGson(result, MyCustormJson1.class);
            } else {
                module = JsonUtil.parsoJsonWithGson(result, MyCustormJson2.class);
            }
            mCusDatas.clear();
            if (module instanceof MyCustormJson1) {
                mCusDatas.addAll(((MyCustormJson1) module).data);
            } else {
                mCusDatas.add(((MyCustormJson2) module).data);
            }
            mMainHandler.sendMessage(mMainHandler.obtainMessage());
        }
    };

    public class MyCustormJson1 extends BaseModule {
        public List<CustormData> data;
    }

    public class MyCustormJson2 extends BaseModule {
        public CustormData data;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (HomeActivity) context;
        mMainHandler = new MyHandler(mContext);
        RequestBody body = new FormBody.Builder().add("token", mLoginManager.getToken())
                .add("empId", mEmployee.jobNumber + "").add("page", "1")
                .add("size", "10").build();
        NetPostUtil.post(Constants.CUSTORM_LIST, body, mGetCustormCallback);
        mContext.showHUD("downloading");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.my_custorm_layout, container, false);
        mSearchView = mView.findViewById(R.id.custom_search);
        mSearchView.setOnEditorActionListener(mSearchActionListener);
        mSearchClose = mView.findViewById(R.id.custom_search_close);
        mSearchClose.setOnClickListener(mSearchCloseListener);
        mList = mView.findViewById(R.id.my_custorm_layout_list);
        mList.setOnItemClickListener(onItemClickListener);
        mList.setAdapter(myAdapter);
        return mView;
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    };

    private View.OnClickListener mSearchCloseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSearchView.clearFocus();
            mSearchView.setText("");
            HideSoft();
        }
    };

    private TextView.OnEditorActionListener mSearchActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                HideSoft();
                RequestBody body = new FormBody.Builder().add("token", mLoginManager.getToken())
                        .add("mobile", mSearchView.getText().toString()).build();
                NetPostUtil.post(Constants.CUSTORM_WITH_PHONE, body, mGetCustormCallback);
                mContext.showHUD("searching");
                return true;
            }
            return false;
        }
    };

    private void HideSoft() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public class MyAdapter extends BaseAdapter {

        public class ViewHolder {
            public TextView name;
            public TextView birthday;
            public TextView mobile;
            public TextView source;
            public Button zhiqing_btn;
        }

        @Override
        public int getCount() {
            return mCusDatas.size();
        }

        @Override
        public Object getItem(int i) {
            return mCusDatas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.custorm_item_layout, viewGroup, false);
                holder = new ViewHolder();
                holder.name = view.findViewById(R.id.custom_name);
                holder.birthday = view.findViewById(R.id.custom_birthday);
                holder.mobile = view.findViewById(R.id.custom_mobile);
                holder.source = view.findViewById(R.id.custom_source);
                holder.zhiqing_btn = view.findViewById(R.id.zhiqing_btn);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            CustormData data = mCusDatas.get(i);
            holder.name.setText(data.name);
            holder.birthday.setText(JsonUtil.getDate(data.birthday));
            if (data.mobile.length() == 11) {
                holder.mobile.setText(data.mobile.substring(0, 3) + "-" +
                        data.mobile.substring(3, 7) + "-" + data.mobile.substring(7));
            }
            holder.source.setText(data.customerSource);
            holder.zhiqing_btn.setTag(data.id);
            return view;
        }
    }

    public class CustormData {
        public String birthday;
        public String customerSource;
        public int id;
        public String mobile;
        public String name;

        @Override
        public String toString() {
            return name + "-" + JsonUtil.getDate(birthday)
                    + "-" + mobile + "-" + customerSource + "-" + id;
        }
    }

    public class Custorm {
        public int id;//
        public String name;//客户姓名
        public String password;//客户密码
        public String guardianName;//监护人姓名
        public double balance;//余额
        public int sex;//性别，1：男，2：女，默认1
        public Date birthday;//生日
        public String mobile;//电话
        public String idCard;//身份证号码
        public String address;//住址
        public int allergy;//有无过敏史，1：无，2：有，默认1
        public int disease;//有无疾病史，1：无，2：有，默认1
        public Date childbirthTime;//分娩日期
        public String childbirthSituation;//分娩情况
        public int childbirthSex;//分娩性别，1：男，2：女
        public int diagnosticAnalysisId;//诊断分析Id
        public int archivesTypeId;//档案类型
        public int consumptionTypeId;//消费类型Id
        public int departmentId;//所属部门
        public int empId;//建档人Id(员工Id)
        public String customerSource;//客户来源
        public int archivesStatus;//档案状态，3：待审，1：正常，2：停用
        public int oldArchivesId;//旧档案号(客户资料Id)
        public Date createTime;//创建时间
        public Date updateTime;//更新时间
    }
}
