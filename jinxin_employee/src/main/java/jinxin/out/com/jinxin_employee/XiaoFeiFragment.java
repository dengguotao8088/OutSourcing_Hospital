package jinxin.out.com.jinxin_employee;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jinxin.out.com.jinxin_employee.JsonModule.BaseModule;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/12.
 */

/**
 * 1、根据客户Id获取购买记录
 * {
 * url : http://staff.mind-node.com/staff/api/purchase_record/list?token=1111111111&customerId=5
 * responseParam {
 * {
 * "code": 0,
 * "action": "",
 * "message": "获取购买记录成功",
 * "data": [
 * {
 * "customerName": "小李",
 * "id": 5,
 * "projectFrequency": 10,
 * "projectId": 1,
 * "projectName": "眼科",
 * "remark": "100",
 * "status": 1,
 * "useFrequency": 0
 * },
 * {
 * "customerName": "小李",
 * "id": 5,
 * "projectFrequency": 10,
 * "projectId": 2,
 * "projectName": "眼科",
 * "remark": "100",
 * "status": 1,
 * "useFrequency": 0
 * }
 * ]
 * }
 * }
 * }
 */

public class XiaoFeiFragment extends BaseFragment {

    private int custorm_id;

    private View mView;
    private PullToRefreshListView mList;

    private int tab_id = 0;
    private Button mGoumai_btn;
    private Button mDangri_btn;

    private List<PurchaseRecord> mPurchList = new ArrayList<>();


    private MyAdapter mGoumaiAdapter = new MyAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        custorm_id = getArguments().getInt("custorm_id", -1);
        if (tab_id == 0 && mPurchList.size() == 0) {
            loadGouMaiList();
        }
        Log.d("dengguotao", "custorm_id:" + custorm_id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.xiaofei_main, container, false);
        mGoumai_btn = mView.findViewById(R.id.xiaofei_title_goumaijilu);
        mDangri_btn = mView.findViewById(R.id.xiaofei_title_dangri_xiaofei);
        mGoumai_btn.setOnClickListener(mTitle_Btn);
        mDangri_btn.setOnClickListener(mTitle_Btn);
        refreshTitle();

        mList = mView.findViewById(R.id.my_xiaofei_layout_list);
        initListView(mList, (TextView) mView.findViewById(R.id.empty));
        mList.setAdapter(mGoumaiAdapter);
        isViewCreate = true;
        return mView;
    }

    private View.OnClickListener mTitle_Btn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int tab = -1;
            if (view.getId() == R.id.xiaofei_title_goumaijilu) {
                tab = 0;
            } else {
                tab = 1;
            }
            Log.d("dengguotao", "tab: " + tab);
            if (tab != tab_id) {
                tab_id = tab;
                refreshTitle();
            }

        }
    };

    @Override
    public void refreshData() {
        if (tab_id == 0) {
            loadGouMaiList();
        }
    }

    @Override
    public void loadData() {
        if (tab_id == 0) {
            loadGouMaiList();
        }
    }

    @Override
    public void refreshUI() {
        if (isViewCreate) {
            refreshTitle();
            if (tab_id == 0) {
                mGoumaiAdapter.notifyDataSetChanged();
            }
        }
    }

    private void loadGouMaiList() {
        RequestBody body = new FormBody.Builder().add("token", LoginManager.getInstance(mActivity).getToken())
                .add("customerId", custorm_id + "")
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/purchase_record/list?", body, goumaiListCallback);
    }

    private Callback goumaiListCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("dengguotao", "result: " + result);
            BaseModule baseModule = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (baseModule.code == 0) {
                mPurchList.clear();
                PurchaseRecordModule purchaseRecordModule = JsonUtil.parsoJsonWithGson(result, PurchaseRecordModule.class);
                mPurchList.addAll(purchaseRecordModule.data);
                mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
            } else {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
            }
        }
    };

    private class MyAdapter extends BaseAdapter {

        private String[] status = {"可用", "完成", "过期", "退费", "作废"};

        private class ViewHolder {
            public TextView project_name;
            public TextView kehu_name;
            public TextView baohan_cishu;
            public TextView yiyong_cishu;
            public Button status;
            public Button add_xiaofei;
        }

        @Override
        public int getCount() {
            return mPurchList.size();
        }

        @Override
        public Object getItem(int i) {
            return mPurchList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(mActivity).inflate(R.layout.goumaijilu, viewGroup, false);
                viewHolder.project_name = view.findViewById(R.id.goumailiaocheng);
                viewHolder.status = view.findViewById(R.id.goumai_status);
                viewHolder.kehu_name = view.findViewById(R.id.guomai_cusname);
                viewHolder.baohan_cishu = view.findViewById(R.id.baohancishu);
                viewHolder.yiyong_cishu = view.findViewById(R.id.yiyongcishu);
                viewHolder.add_xiaofei = view.findViewById(R.id.tianjiaxiaofei_btn);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            PurchaseRecord record = mPurchList.get(i);
            viewHolder.project_name.setText(record.projectName);
            viewHolder.kehu_name.setText(record.customerName);
            viewHolder.baohan_cishu.setText(record.projectFrequency + "");
            viewHolder.yiyong_cishu.setText(record.useFrequency + "");
            viewHolder.status.setText(status[record.status - 1]);
            return view;
        }
    }

    private class MyAdapter2 extends BaseAdapter {

        private class ViewHolder2 {
            public TextView project_name;
            public TextView kehu_name;
            public TextView baohan_cishu;
            public TextView yiyong_cishu;
            public Button status;
            public Button add_xiaofei;
        }

        @Override
        public int getCount() {
            return mPurchList.size();
        }

        @Override
        public Object getItem(int i) {
            return mPurchList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }

    public class PurchaseRecordModule extends BaseModule {
        public List<PurchaseRecord> data;
    }

    public class PurchaseRecord {
        public int id;//
        public int customerId;//客户Id
        public String customerName;//客户姓名
        public int projectId;//项目Id
        public String projectName;//项目名称
        public int number;//购买数量
        public int projectFrequency;//一次包含项目次数
        public int useFrequency;//使用次数
        public Double totalPrice;//总价
        public int status;//购买项目的状态，1：可用，2：完成，3：过期，4：退费，5：作废
        public int empId;//员工Id
        public String remark;//备注
        public String expirationDate;//到期时间
        public String createTime;//创建时间
        public String updateTime;//更新时间
    }

    private void refreshTitle() {
        if (tab_id == 0) {
            mGoumai_btn.setBackgroundResource(R.drawable.xiaofei_title_btn_parent_bac);
            mGoumai_btn.setTextColor(mActivity.getColor(R.color.tab_bar));
            mDangri_btn.setBackgroundResource(R.drawable.xiaofei_title_btn_bac);
            mDangri_btn.setTextColor(mActivity.getColor(R.color.white));
        } else {
            mDangri_btn.setBackgroundResource(R.drawable.xiaofei_title_btn_parent_bac);
            mDangri_btn.setTextColor(mActivity.getColor(R.color.tab_bar));
            mGoumai_btn.setBackgroundResource(R.drawable.xiaofei_title_btn_bac);
            mGoumai_btn.setTextColor(mActivity.getColor(R.color.white));
        }
    }
}
