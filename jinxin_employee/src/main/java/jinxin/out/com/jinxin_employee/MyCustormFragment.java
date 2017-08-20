package jinxin.out.com.jinxin_employee;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.w3c.dom.Text;

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

public class MyCustormFragment extends BaseFragment {

    private LoginManager mLoginManager;
    private Employee mEmployee;

    private View mView;
    private SearchView mSearchView;
    private String searhText;
    private ImageView mSearchClose;
    private PullToRefreshListView mList;
    private List<CustormData> mCusDatas = new ArrayList<>();

    private MyAdapter myAdapter = new MyAdapter();

    private CustomerInformedFragment mCustomerInformedFragment;
    private XiaoFeiFragment xiaoFeiFragment;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public MyCustormFragment() {
        mLoginManager = LoginManager.getInstance(mActivity);
        mEmployee = mLoginManager.getEmployee();
    }

    private Callback mGetCustormCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mActivity.dissmissHUD();
            if (page_id > 1) {
                page_id = page_id - 1;
            }
            mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            mActivity.dissmissHUD();
            if (response.code() != 200) {
                if (page_id > 1) {
                    page_id = page_id - 1;
                }
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
                return;
            }
            String result = response.body().string();
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code == 1) {
                if (mMainHandler != null) {
                    mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                    return;
                }
            }
            if (module.code != 0) {
                if (page_id > 1) {
                    page_id = page_id - 1;
                }
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
                return;
            }
            MyCustormJson module2 = JsonUtil.parsoJsonWithGson(result, MyCustormJson.class);
            if (page_id == 1) {
                mCusDatas.clear();
            }
            mCusDatas.addAll(module2.data);
//            if(mCusDatas.size() == 10 * page_id) {
//                //page_id = page_id
//            }
            if (module2.data.size() == 0 && page_id > 1) {
                page_id = page_id - 1;
            }
            mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
        }
    };

    public class MyCustormJson extends BaseModule {
        public List<CustormData> data;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void refreshData() {
        if (!LoginManager.getInstance(mActivity).isNetworkConnected()) {
            return;
        }
        page_id = 1;
        loadAllData();
    }

    @Override
    public void loadData() {
        if (!LoginManager.getInstance(mActivity).isNetworkConnected()) {
            return;
        }
        if (mCusDatas != null && mCusDatas.size() >= (10 * page_id)) {
            page_id = page_id + 1;
        }
        loadAllData();
    }

    @Override
    public void refreshUI() {
        if (isViewCreate) {
            myAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page_id = 1;
        searhText = "";
        Bundle data = getArguments();
        if (data != null) {
            String search_text = data.getString("search_data");
            if (search_text != null && !search_text.equals("")) {
                searchWithMobile(search_text);
            } else {
                loadAllData();
            }
        } else {
            loadAllData();
        }
        isFirstShow = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.my_custorm_layout, container, false);
        mSearchView = mView.findViewById(R.id.custom_search);
        mSearchView.setText(searhText);
        mSearchView.setOnEditorActionListener(mSearchActionListener);
        mSearchClose = mView.findViewById(R.id.custom_search_close);
        mSearchClose.setOnClickListener(mSearchCloseListener);

        mList = mView.findViewById(R.id.my_custorm_layout_list);
        initListView(mList, null);
        mList.setOnItemClickListener(onItemClickListener);
        mList.setAdapter(myAdapter);
        isViewCreate = true;
        return mView;
    }

    private AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (xiaoFeiFragment == null) {
                        xiaoFeiFragment = new XiaoFeiFragment();
                        xiaoFeiFragment.mParentFragment = MyCustormFragment.this;
                    }
                    Bundle data = new Bundle();
                    data.putInt("custorm_id", mCusDatas.get((int) l).id);
                    Log.d("dengguotao","click: "+l+"   cid: "+mCusDatas.get((int) l).id);
                    data.putBoolean("xiaofei_fragement_reset", true);
                    xiaoFeiFragment.setArguments(data);
                    mActivity.showContent(xiaoFeiFragment);
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

    private TextView.OnEditorActionListener mSearchActionListener =
            new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_SEARCH) {
                        mSearchView.clearFocus();
                        String searh = mSearchView.getText().toString();
                        HideSoft();
                        searchWithMobile(searh);
                        mActivity.showHUD("搜索中");
                        return true;
                    }
                    return false;
                }
            };

    private void searchWithMobile(String mobile) {
        page_id = 1;
        searhText = mobile;
        RequestBody body = new FormBody.Builder().add("token", mLoginManager.getToken())
                .add("mobile", mobile).build();
        NetPostUtil.post(Constants.CUSTORM_WITH_PHONE, body, mGetCustormCallback);
    }

    int page_id = 1;

    private void loadAllData() {
        Log.d("dengguotao","page_id: "+page_id);
        RequestBody body = new FormBody.Builder().add("token", mLoginManager.getToken())
                .add("empId", mEmployee.id + "").add("page", page_id + "")
                .add("size", "10").build();
        NetPostUtil.post(Constants.CUSTORM_LIST, body, mGetCustormCallback);
    }

    private void HideSoft() {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(),
                    0);
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
                view = LayoutInflater.from(mActivity).inflate(R.layout.custorm_item_layout,
                        viewGroup, false);
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
            holder.zhiqing_btn.setTag(data);
            holder.zhiqing_btn.setOnClickListener(mZhiQinClick);
            return view;
        }
    }

    private View.OnClickListener mZhiQinClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CustormData data = (CustormData) view.getTag();
            if (mCustomerInformedFragment == null) {
                mCustomerInformedFragment = new CustomerInformedFragment();
            }
            Bundle bundle = new Bundle();
            bundle.putInt("custorm_id", data.id);
            bundle.putString("custorm_name", data.name);
            mCustomerInformedFragment.setArguments(bundle);
            mCustomerInformedFragment.mParentFragment = MyCustormFragment.this;
            mActivity.showContent(mCustomerInformedFragment);
        }
    };

    public class CustormData {
        public String birthday;
        public String customerSource;
        public int id;
        public String mobile;
        public String name;

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
