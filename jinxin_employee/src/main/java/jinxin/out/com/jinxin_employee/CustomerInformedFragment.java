package jinxin.out.com.jinxin_employee;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jinxin.out.com.jinxin_employee.JsonModule.BaseModule;
import jinxin.out.com.jinxin_employee.JsonModule.ZhiQinModule;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/6.
 */

public class CustomerInformedFragment extends BaseFragment {

    private int mCusId;
    private String mCusName;

    private View mView;
    private TextView mAddinfoText;
    private ImageView mAddinfoImg;
    private TextView mAddreText;
    private ImageView mAddreImg;
    private Button mAdd;

    private PullToRefreshListView mList;

    private MyAdapter myAdapter = new MyAdapter();

    private LoginManager manager;

    private List<ZhiQinModule> mZhiQinList = new ArrayList<ZhiQinModule>();
    private List<MyCusZhiQinModule> mCusZhiQinList = new ArrayList<MyCusZhiQinModule>();
    private List<MyCusZhiQinModule> mCusZhiQinList_temp = new ArrayList<MyCusZhiQinModule>();

    private Dialog mChooseInfoDialog;
    private ZhiQinModule mChooseModule;

    private Dialog mChooseRelationDialog;

    public CustomerInformedFragment() {
        manager = LoginManager.getInstance(mActivity);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private Callback mGetZhiQinListCallback = new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() != 200) {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
                return;
            }
            String result = response.body().string();
            BaseModule baseModule = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (baseModule.code == 1) {
                if (mMainHandler != null) {
                    mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                    return;
                }
            }
            if (baseModule.code == 0) {
                ZhiQinList list = JsonUtil.parsoJsonWithGson(result, ZhiQinList.class);
                if (list.data.size() > 0) {
                    mZhiQinList.addAll(list.data);
                }
            }
        }
    };

    private Callback mGetZhiQinCallback = new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {
            mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            BaseModule baseModule = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (baseModule.code == 1) {
                if (mMainHandler != null) {
                    mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                    return;
                }
            }
            if (baseModule.code == 0) {
                Log.d("dengguotao", "result: " + result);
                MyResponseModule baseModule2 = JsonUtil.parsoJsonWithGson(result, MyResponseModule.class);
                mCusZhiQinList_temp.clear();
                mCusZhiQinList_temp.addAll(baseModule2.data);
                mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
            } else {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
            }
        }
    };

    public class ZhiQinList extends BaseModule {
        public List<ZhiQinModule> data;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCusId = getArguments().getInt("custorm_id");
        mCusName = getArguments().getString("custorm_name");
        mChooseModule = null;
        mChooseRelation = null;
        //if (mCusZhiQinList.size() == 0) {
        mCusZhiQinList.clear();
        mCusZhiQinList_temp.clear();
        //loadAllData();
        //}
        if (mZhiQinList.size() == 0) {
            RequestBody body = new FormBody.Builder().add("token", manager.getToken()).build();
            NetPostUtil.post(Constants.ZHIQIN_LIST, body, mGetZhiQinListCallback);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (HomeActivity) context;
        mCusId = getArguments().getInt("custorm_id");
        mCusName = getArguments().getString("custorm_name");
    }

    private void loadAllData() {
        RequestBody body = new FormBody.Builder().add("token", manager.getToken())
                .add("customerId", mCusId + "")
                .build();
        NetPostUtil.post(
                "http://staff.mind-node.com/staff/api/customer_informed_consent_record/list?",
                body, mGetZhiQinCallback);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.custorm_info_page, container, false);
        ImageView backView = mView.findViewById(R.id.back);
        backView.setOnClickListener(mBackListener);
        TextView title = mView.findViewById(R.id.header_title);
        title.setText("知情同意书");

        mAddinfoText = mView.findViewById(R.id.add_info_text);
        mAddinfoText.setText("选择知情同意书：");
        mAddinfoImg = mView.findViewById(R.id.add_info_btn);
        mAddinfoImg.setOnClickListener(mAddinfoListener);
        mAddreText = mView.findViewById(R.id.add_relation_text);
        mAddreText.setText("签字人与客户关系：");
        mAddreImg = mView.findViewById(R.id.add_relation_btn);
        mAddreImg.setOnClickListener(mAddReListener);

        mAdd = mView.findViewById(R.id.add_btn);
        mAdd.setOnClickListener(mAddListener);

        mList = mView.findViewById(R.id.my_custorm_info_layout_list);
        initListView(mList, null);
        mList.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        mList.setAdapter(myAdapter);
        mList.setOnItemClickListener(mListClick);
        isViewCreate = true;
        loadAllData();
        return mView;
    }

    private ZhiQinDetail mZhiQinDetail;
    private AdapterView.OnItemClickListener mListClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            MyCusZhiQinModule module = mCusZhiQinList.get((int) l);
            if (mZhiQinDetail == null) {
                mZhiQinDetail = new ZhiQinDetail();
            }
            Bundle data = new Bundle();
            data.putInt("zhiqin_id", module.id);
            data.putString("custorm_name", mCusName);
            mZhiQinDetail.setArguments(data);
            mZhiQinDetail.mParentFragment = CustomerInformedFragment.this;
            mActivity.showContent(mZhiQinDetail);
        }
    };

    private View.OnClickListener mAddinfoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mChooseInfoDialog == null) {
                int size = mZhiQinList.size();
                String[] list = new String[size];
                for (int i = 0; i < size; i++) {
                    list[i] = mZhiQinList.get(i).title;
                }
                mChooseInfoDialog = new AlertDialog.Builder(mActivity)
                        .setTitle("选择知情同意书")
                        .setItems(list, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mChooseModule = mZhiQinList.get(i);
                                mAddinfoText.setText("选择知情同意书：" + mChooseModule.title);
                            }
                        })
                        .create();
            }
            mChooseInfoDialog.show();
        }
    };

    private String[] relations = {"本人", "监护人", "委托人"};
    private String mChooseRelation;
    private View.OnClickListener mAddReListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mChooseRelationDialog == null) {
                mChooseRelationDialog = new AlertDialog.Builder(mActivity)
                        .setTitle("选择知情同意书")
                        .setItems(relations, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mChooseRelation = relations[i];
                                mAddreText.setText("签字人与客户关系：" + relations[i]);
                            }
                        })
                        .create();
            }
            mChooseRelationDialog.show();
        }
    };

    private View.OnClickListener mAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mChooseModule == null || mChooseRelation == null) {
                mMainHandler.sendMessage(
                        mMainHandler.obtainMessage(SHOW_TOAST, "请选择模板及关系"));
                return;
            }
            if (!LoginManager.getInstance(mActivity).isNetworkConnected()) {
                mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "没有网络"));
                return;
            }
            if (qianMing == null) {
                qianMing = new QianMing();
                qianMing.mode = QianMing.MODE_ZHIQIN;
                qianMing.mParentFragment = CustomerInformedFragment.this;
            }
            Bundle data = new Bundle();
            data.putInt("zhiqin_module", 1);
            data.putInt("zhiqin_cus_id", mCusId);
            data.putInt("zhiqin_info_id", mChooseModule.id);
            data.putString("zhiqin_relation", mChooseRelation);
            qianMing.setArguments(data);
            mActivity.showContent(qianMing);
        }
    };

    public class MyResponseModule extends BaseModule {
        public List<MyCusZhiQinModule> data;
    }

    public class MyCusZhiQinModule extends BaseModule {
        public int id;
        public int informedConsentTemplateId;
        public String informedConsentTemplateName;
    }

    public class MyAdapter extends BaseAdapter {

        public class ViewHolder {
            public TextView name;
            public Button qianming;
        }

        @Override
        public int getCount() {
            return mCusZhiQinList.size();
        }

        @Override
        public Object getItem(int i) {
            return mCusZhiQinList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = mActivity.getLayoutInflater().inflate(R.layout.custorm_info_item, viewGroup,
                        false);
                holder = new ViewHolder();
                holder.name = view.findViewById(R.id.cus_info_item_title);
                holder.qianming = view.findViewById(R.id.cus_info_item_qianming);
                holder.qianming.setOnClickListener(mQianMing);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            MyCusZhiQinModule zhiQinModule = mCusZhiQinList.get(i);
            holder.name.setText(zhiQinModule.informedConsentTemplateName);
            holder.qianming.setTag(zhiQinModule.id);
            return view;
        }
    }

    private QianMing qianMing;
    private View.OnClickListener mQianMing = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (qianMing == null) {
                qianMing = new QianMing();
                qianMing.mode = QianMing.MODE_ZHIQIN;
                qianMing.mParentFragment = CustomerInformedFragment.this;
            }
            Bundle data = new Bundle();
            data.putInt("zhiqin_module", 2);
            data.putInt("zhiqin_id", (Integer) view.getTag());
            qianMing.setArguments(data);
            mActivity.showContent(qianMing);
        }
    };

    @Override
    public void refreshData() {
        loadAllData();
    }

    @Override
    public void loadData() {
        loadAllData();
    }

    @Override
    public void refreshUI() {
        if (isViewCreate) {
            mCusZhiQinList.clear();
            mCusZhiQinList.addAll(mCusZhiQinList_temp);
            mCusZhiQinList_temp.clear();
            myAdapter.notifyDataSetChanged();
        }
    }

}
