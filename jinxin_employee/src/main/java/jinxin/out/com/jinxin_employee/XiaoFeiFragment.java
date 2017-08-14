package jinxin.out.com.jinxin_employee;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
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
    private List<ConsumptionRecord> mConsumptionList = new ArrayList<>();

    private int colorEnable;
    private int colorDisable;
    private MyAdapter mGoumaiAdapter = new MyAdapter();
    private MyAdapter2 mDangRiAdapter = new MyAdapter2();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colorEnable = mActivity.getColor(R.color.tab_bar);
        colorDisable = mActivity.getColor(R.color.tab_bar_bac);
        custorm_id = getArguments().getInt("custorm_id", -1);
        if (tab_id == 0 && mPurchList.size() == 0) {
            loadGouMaiList();
        } else if (tab_id == 1 && mConsumptionList.size() == 0) {
            loadDangRiList();
        }
        isFirstShow = false;
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

        ImageView back = mView.findViewById(R.id.xiao_title_back);
        back.setOnClickListener(mBackListener);

        mList = mView.findViewById(R.id.my_xiaofei_layout_list);
        initListView(mList, (TextView) mView.findViewById(R.id.empty));
        mList.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        mList.setOnItemClickListener(onItemClickListener);
        refreshAdapter();
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
                if (mConsumptionList.size() == 0) {
                    loadDangRiList();
                }
            }
            if (tab != tab_id) {
                tab_id = tab;
                refreshTitle();
                refreshAdapter();
            }

        }
    };

    private KehuXiaoFeiFragment mKehuXiaoFeiFragment;
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d("dengguotao", "click: " + l);
            if (tab_id != 0) {
                return;
            }
            PurchaseRecord record = mPurchList.get((int) l);
            if (mKehuXiaoFeiFragment == null) {
                mKehuXiaoFeiFragment = new KehuXiaoFeiFragment();
            }
            Bundle data = new Bundle();
            data.putInt("purch_id", record.id);
            data.putString("p_name", record.projectName);
            mKehuXiaoFeiFragment.setArguments(data);
            mKehuXiaoFeiFragment.mParentFragment = XiaoFeiFragment.this;
            mActivity.showContent(mKehuXiaoFeiFragment);
        }
    };

    private void refreshAdapter() {
        if (tab_id == 0) {
            mList.setAdapter(mGoumaiAdapter);
        } else {
            mList.setAdapter(mDangRiAdapter);
        }
        mList.onRefreshComplete();
    }

    @Override
    public void refreshData() {
        if (tab_id == 0) {
            loadGouMaiList();
        } else {
            loadDangRiList();
        }
    }

    @Override
    public void loadData() {
    }

    @Override
    public void refreshUI() {
        if (isViewCreate) {
            refreshTitle();
            if (tab_id == 0) {
                mGoumaiAdapter.notifyDataSetChanged();
            } else {
                mDangRiAdapter.notifyDataSetChanged();
            }
        }
    }

    private void loadGouMaiList() {
        RequestBody body = new FormBody.Builder().add("token", LoginManager.getInstance(mActivity).getToken())
                .add("customerId", custorm_id + "")
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/purchase_record/list?", body, goumaiListCallback);
    }


    private void loadDangRiList() {
        RequestBody body = new FormBody.Builder().add("token", LoginManager.getInstance(mActivity).getToken())
                .add("customerId", custorm_id + "")
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/consumption_record/real_list?", body, dangRiListCallback);
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


    private Callback dangRiListCallback = new Callback() {

        //http://staff.mind-node.com/staff/api/consumption_record/real_list?token=11111&customerId=5
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
                mConsumptionList.clear();
                ConsumptionRecordModule purchaseRecordModule = JsonUtil.parsoJsonWithGson(result, ConsumptionRecordModule.class);
                mConsumptionList.addAll(purchaseRecordModule.data);
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

        private class ViewHolder {
            public TextView project_name;
            public TextView kehu_name;
            public TextView date_year;
            public TextView date_hour;
            public TextView fuwu_reyuan;
            public Button wolaifuwu;
            public Button do_work;
            public Button push_msg;
            public Button click_change;
        }

        @Override
        public int getCount() {
            return mConsumptionList.size();
        }

        @Override
        public Object getItem(int i) {
            return mConsumptionList.get(i);
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
                view = LayoutInflater.from(mActivity).inflate(R.layout.dangrixiaofei, viewGroup, false);
                viewHolder.project_name = view.findViewById(R.id.dangriliaocheng);
                viewHolder.kehu_name = view.findViewById(R.id.kehu_name);
                viewHolder.date_year = view.findViewById(R.id.jinrixiaofei_year);
                viewHolder.date_hour = view.findViewById(R.id.jinrixiaofei_time);
                viewHolder.fuwu_reyuan = view.findViewById(R.id.fuwu_people);
                viewHolder.do_work = view.findViewById(R.id.shishi_btn);
                viewHolder.push_msg = view.findViewById(R.id.xiaoxituisong_btn);
                viewHolder.click_change = view.findViewById(R.id.dianjixiugaibtn);
                viewHolder.wolaifuwu = view.findViewById(R.id.fuwu_btn);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            ConsumptionRecord record = mConsumptionList.get(i);
            viewHolder.project_name.setText(record.projectName);
            viewHolder.kehu_name.setText(record.customerName);
            String date = JsonUtil.getDate2(record.createTime);
            viewHolder.date_year.setText(date.substring(0, date.indexOf("-")));
            viewHolder.date_hour.setText(date.substring(date.indexOf("-") + 1));
            viewHolder.fuwu_reyuan.setText(record.empName);
            viewHolder.push_msg.setClickable(record.messagePush);
            viewHolder.push_msg.setBackgroundColor(record.messagePush ?
                    colorEnable : colorDisable);
            viewHolder.wolaifuwu.setClickable(record.myService);
            viewHolder.wolaifuwu.setBackgroundColor(record.myService ?
                    colorEnable : colorDisable);
            return view;
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

    public class ConsumptionRecordModule extends BaseModule {
        public List<ConsumptionRecord> data;
    }

    public class ConsumptionRecord {
        public int id;//
        public int purchaseRecordId;//购买记录Id
        public int empId;//员工Id
        public String empSignaturePath;//员工签名路径
        public String customerSignaturePath;//客户签名路径
        public String daySymptom;//当日症状
        public int status;//当前消费记录状态
        public String statusName;//当前消费记录状态名称
        public String partnerName;//合作人姓名
        public String commentLevel;//评论等级
        public String commentContent;//评论内容
        public String remarks;//备注
        public String endTime;//结束时间
        public String createTime;//创建时间
        public String updateTime;//更新时间

        String empName;//员工姓名
        String customerName;//客户姓名
        String projectName;//项目名称

        boolean myService;//我来服务按钮状态（可点击、不可点击）
        boolean messagePush;//消息推送按钮（可点击、不可点击）
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
