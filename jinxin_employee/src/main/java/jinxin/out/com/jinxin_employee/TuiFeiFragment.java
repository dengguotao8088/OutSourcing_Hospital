package jinxin.out.com.jinxin_employee;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
 * 1、点击签名（退费申请）
 * {
 * url : http://staff.mind-node.com/staff/api/customer_refund_apply_record/update?token=111&id=11&customerId=12&customerSignaturePath=客户退费申请签字图片地址
 * responseParam {
 * {
 * "code": 0,
 * "action": "",
 * "message": "退费成功",
 * "data": ""
 * }
 * }
 * }
 * <p>
 * 2、根据Id获取退费申请记录
 * {
 * url : http://staff.mind-node.com/staff/api/customer_refund_apply_record/get?token=111&id=11
 * responseParam {
 * {
 * "code": 0,
 * "action": "",
 * "message": "退费成功",
 * "data": {
 * "id": 1
 * "customerId": 5,
 * "customerName": 张三,
 * "purchaseRecordId": 1
 * "customerSignaturePath": "http://img002.21cnimg.com/photos/album/20150702/m600/2D79154370E073A2BA3CD4D07868861D.jpeg",
 * "refundType": VIP
 * "status": 1
 * "remarks": 退费200
 * "createTime": 				（申请时间）
 * "updateTime": 				（退费时间）
 * }
 * }
 * }
 * }
 * <p>
 * 3、获取退费申请记录列表
 * {
 * url : http://staff.mind-node.com/staff/api/customer_refund_apply_record/list?token=111&page=1&size10
 * responseParam {
 * {
 * "code": 0,
 * "action": "",
 * "message": "退费成功",
 * "data": [
 * {
 * "id": 1
 * "customerId": 5,
 * "customerName": 张三,
 * "refundMoney": 200
 * },
 * {
 * "id": 1
 * "customerId": 5,
 * "customerName": 张三,
 * "refundMoney": 200
 * }
 * ]
 * }
 * }
 * }
 */

public class TuiFeiFragment extends BaseFragment {

    private View mView;
    private PullToRefreshListView mList;

    private List<TuiFeiModule> mdatas = new ArrayList<>();
    private MyAdapter myAdapter = new MyAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mdatas.size() == 0) {
            loadTuiFeiList();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tuifei_layout, container, false);

        TextView textView = mView.findViewById(R.id.header_title);
        textView.setText("退费申请");
        ImageView backView = mView.findViewById(R.id.back);
        backView.setOnClickListener(mBackListener);

        mList = mView.findViewById(R.id.my_tuifei_layout_list);
        TextView empty = mView.findViewById(R.id.empty);
        initListView(mList, empty);
        mList.setAdapter(myAdapter);
        isViewCreate = true;
        return mView;
    }

    @Override
    public void refreshData() {
        loadTuiFeiList();
    }

    @Override
    public void loadData() {
        loadTuiFeiList();
    }

    @Override
    public void refreshUI() {
        if (isViewCreate) {
            myAdapter.notifyDataSetChanged();
        }
    }

    private void loadTuiFeiList() {
        RequestBody body = new FormBody.Builder().add("token", LoginManager.getInstance(mActivity).getToken())
                .add("page", "1")
                .add("size", "10").build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/customer_refund_apply_record/list?", body, mGetTuiFeiListCallBack);
    }

    private Callback mGetTuiFeiListCallBack = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d("dengguotao", "code: " + response.code());
            String result = response.body().string();
            Log.d("dengguotao", "result: " + result);
            BaseModule baseModule = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (baseModule.code == 0) {
                mdatas.clear();
                TuiFeiListModule tuiFeiList = JsonUtil.parsoJsonWithGson(result, TuiFeiListModule.class);
                mdatas.addAll(tuiFeiList.data);
                mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
            } else {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
            }
        }
    };

    public class MyAdapter extends BaseAdapter {

        private class ViewHolder {
            public TextView name;
            public TextView money;
            public Button qianming;
        }

        @Override
        public int getCount() {
            return mdatas.size();
        }

        @Override
        public Object getItem(int i) {
            return mdatas.get(i);
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
                view = LayoutInflater.from(mActivity).inflate(R.layout.tuifei, viewGroup, false);
                viewHolder.name = view.findViewById(R.id.name);
                viewHolder.money = view.findViewById(R.id.money);
                viewHolder.qianming = view.findViewById(R.id.qianzi_btn);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            TuiFeiModule module = mdatas.get(i);
            viewHolder.name.setText(module.customerName);
            viewHolder.money.setText(module.refundMoney + "");
            viewHolder.qianming.setTag(module);
            viewHolder.qianming.setOnClickListener(mTuiFeiQianMClick);
            return view;
        }
    }

    private QianMing mQianMing;
    private View.OnClickListener mTuiFeiQianMClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TuiFeiModule module = (TuiFeiModule) view.getTag();
            if (mQianMing == null) {
                mQianMing = new QianMing();
                mQianMing.mode = QianMing.MODE_TUIFEI;
                mQianMing.mParentFragment = TuiFeiFragment.this;
            }
            mActivity.showContent(mQianMing);
        }
    };

    public class TuiFeiItemModule extends BaseModule {
        public TuiFeiModule data;
    }

    public class TuiFeiListModule extends BaseModule {
        public List<TuiFeiModule> data;
    }

    public class TuiFeiModule {
        public int id;//
        public int customerId;//客户Id
        public String customerName;//客户姓名
        public int purchaseRecordId;//购买记录Id
        public String customerSignaturePath;//客户退费签名路径
        public int refundType;//退费类型
        public Double refundMoney;//
        public int status;//退费状态（1：待签字、2：已完成）
        public String remarks;//备注
        public String createTime;//创建时间
        public String updateTime;//更新时间
    }
}
