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
import android.widget.ListAdapter;
import android.widget.ListView;
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
 * Created by Administrator on 2017/8/14.
 */

public class KehuXiaoFeiFragment extends BaseFragment {
    private int purchId;
    private String p_name;
    private String remark = "";

    private View mView;
    private TextView mRemark;
    private PullToRefreshListView mList;

    private List<XiaofeiRecord> mXiaofeiList = new ArrayList<>();
    private List<XiaofeiRecord> mXiaofeiList_temp = new ArrayList<>();

    @Override
    public void refreshData() {
        loadList();
    }

    @Override
    public void loadData() {

    }

    @Override
    public void refreshUI() {
        if (isViewCreate) {
            mRemark.setText("购买备注: " + remark);
            mXiaofeiList.clear();
            mXiaofeiList.addAll(mXiaofeiList_temp);
            mXiaofeiList_temp.clear();
            myAdapter.notifyDataSetChanged();
            mList.getRefreshableView().setSelection(0);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        purchId = getArguments().getInt("purch_id");
        p_name = getArguments().getString("p_name");
        remark = getArguments().getString("remark");
        mXiaofeiList.clear();
        mXiaofeiList_temp.clear();
        loadList();
    }

    //http://staff.mind-node.com/staff/api/consumption_record/list?token=11111
    // &purchaseRecordId=11&projectName=射频治疗盆底肌修复（疗程）&remark=购买备注
    private void loadList() {
        RequestBody body = new FormBody.Builder().add("token",
                LoginManager.getInstance(mActivity).getToken())
                .add("purchaseRecordId", purchId + "")
                .add("projectName", p_name)
                .add("remark", remark).build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/consumption_record/list?",
                body, mGetListCallback);
    }

    private Callback mGetListCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            if (mMainHandler != null) {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() != 200) {
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
            if (module.code == 0) {
                JsonModule module1 = JsonUtil.parsoJsonWithGson(result, JsonModule.class);
                remark = module1.data.remark;
                p_name = module1.data.projectName;
                mXiaofeiList_temp.clear();
                mXiaofeiList_temp.addAll(module1.data.datas);
                if (mMainHandler != null) {
                    mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
                }
            } else {
                if (mMainHandler != null) {
                    mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.kehuxiaofei_main, container, false);
        mList = mView.findViewById(R.id.xiaofeijilu_layout_list);
        ImageView back = mView.findViewById(R.id.back);
        back.setOnClickListener(mBackListener);
        TextView title = mView.findViewById(R.id.header_title);
        title.setText("客户消费记录");

        mRemark = mView.findViewById(R.id.xiaofeijilu_remark);
        mRemark.setText("购买备注: ");

        initListView(mList, null);
        mList.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        mList.setAdapter(myAdapter);
        isViewCreate = true;
        return mView;
    }

    private XiaoFeiAdapter myAdapter = new XiaoFeiAdapter();

    public class XiaoFeiAdapter extends BaseAdapter {

        private class ViewHolder {
            public TextView proName;
            public TextView date;
            public TextView fuwu_Pe;
            public Button statue;
            public Button do_change;
        }

        @Override
        public int getCount() {
            return mXiaofeiList.size();
        }

        @Override
        public Object getItem(int i) {
            return mXiaofeiList.get(i);
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
                view = LayoutInflater.from(mActivity).inflate(R.layout.xiaofeijilu, viewGroup,
                        false);
                viewHolder.proName = view.findViewById(R.id.xiaofei_liaocheng);
                viewHolder.statue = view.findViewById(R.id.xiaofei_ok_btn);
                viewHolder.date = view.findViewById(R.id.xiaofeijilu_date);
                viewHolder.fuwu_Pe = view.findViewById(R.id.xiaofei_fuwunames);
                viewHolder.do_change = view.findViewById(R.id.xiaofei_xiugai);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            XiaofeiRecord record = mXiaofeiList.get(i);
            viewHolder.statue.setText(record.statusName);
            viewHolder.proName.setText(p_name);
            viewHolder.fuwu_Pe.setText(record.empName);
            String date = JsonUtil.getDate2(record.createTime);
            viewHolder.date.setText(date.substring(0, date.indexOf("-")) + "    " +
                    date.substring(date.indexOf("-") + 1));
            viewHolder.do_change.setTag(record.id);
            viewHolder.do_change.setOnClickListener(do_change_click);
            return view;
        }
    }

    private XiaoFeiDetailFragment mXiaoFeiDetailFragment;
    private View.OnClickListener do_change_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mXiaoFeiDetailFragment == null) {
                mXiaoFeiDetailFragment = new XiaoFeiDetailFragment();
                mXiaoFeiDetailFragment.mParentFragment = KehuXiaoFeiFragment.this;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("prcu_id", (Integer) view.getTag());
            mXiaoFeiDetailFragment.setArguments(bundle);
            mActivity.showContent(mXiaoFeiDetailFragment);
        }
    };

    private class JsonModule extends BaseModule {
        public Data data;

        public class Data {
            public List<XiaofeiRecord> datas;
            public String remark;
            public String projectName;
        }
    }

    public class XiaofeiRecord {
        public int id;//
        public String empName;//购买记录Id
        public int empId;//员工Id
        public String statusName;//当前消费记录状态
        public int status;
        public String createTime;//创建时间
    }
}
