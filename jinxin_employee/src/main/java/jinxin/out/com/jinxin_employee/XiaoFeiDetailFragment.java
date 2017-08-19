package jinxin.out.com.jinxin_employee;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;

import jinxin.out.com.jinxin_employee.JsonModule.BaseModule;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by admin on 2017/8/18.
 */

public class XiaoFeiDetailFragment extends BaseFragment {

    private int xiaofei_ID;

    private CurrentPurRecord mCurrentPurRecord;

    private View mView;
    private Button save_btn;
    private TextView cusName;
    private int cusId;

    private EditText zhenduan_ed;
    private Button zhenduan_bt;

    private EditText ttpinggu_ed;
    private Button ttpinggu_bt;

    private EditText xiaofei_remark_ed;

    private TextView goumai_remark;

    private TextView partener_remark;

    private PopupWindow mTTpop;

    @Override
    public void refreshData() {

    }

    @Override
    public void loadData() {

    }

    @Override
    public void refreshUI() {
        if (isViewCreate && mCurrentPurRecord != null) {
            cusName.setText("客户姓名： " + mCurrentPurRecord.customerName);
            zhenduan_ed.setText(mCurrentPurRecord.daySymptom);
            ttpinggu_ed.setText(mCurrentPurRecord.painAssessment);
            xiaofei_remark_ed.setText(mCurrentPurRecord.remarks);
            goumai_remark.setText("购买备注: " + mCurrentPurRecord.purchaseRecordRemarks);
            partener_remark.setText("合伙人: " + mCurrentPurRecord.partnerName);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xiaofei_ID = getArguments().getInt("prcu_id");
        mCurrentPurRecord = new CurrentPurRecord();
        loadDetail();
    }

    //http://staff.mind-node.com/staff/api/consumption_record/get?
    private void loadDetail() {
        RequestBody body = new FormBody.Builder().add("token",
                LoginManager.getInstance(mActivity).getToken())
                .add("id", 1 + "")
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/consumption_record/get?",
                body, mCallback);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fuwujilu, container, false);
        ImageView back = mView.findViewById(R.id.fuwujilu_back);
        back.setOnClickListener(mBackListener);
        save_btn = mView.findViewById(R.id.fuwujilu_save);

        cusName = mView.findViewById(R.id.fuwujilu_kuhuxingming);
        zhenduan_ed = mView.findViewById(R.id.fuwujilu_ed_neirong);
        zhenduan_bt = mView.findViewById(R.id.fuwujilu_zhengduanneirong_btn);
        ttpinggu_ed = mView.findViewById(R.id.fuwujilu_ed_tengtong_neirong);
        ttpinggu_bt = mView.findViewById(R.id.fuwujilu_tengtong_btn);
        xiaofei_remark_ed = mView.findViewById(R.id.fuwujilu_ed_beizhu);
        goumai_remark = mView.findViewById(R.id.fuwujilu_gm_beizhu);
        partener_remark = mView.findViewById(R.id.fuwujilu_tv_hehuoren);

        isViewCreate = true;
        refreshUI();
        return mView;
    }

    private Callback mCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() == 200) {
                String result = response.body().string();
                BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
                if (module.code == 0) {
                    JsonModule jsonModule = JsonUtil.parsoJsonWithGson(result, JsonModule.class);
                    mCurrentPurRecord = jsonModule.data;
                    mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
                }
            } else{
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
            }
        }
    };

    private class JsonModule extends BaseModule {
        public CurrentPurRecord data;
    }

    private class CurrentPurRecord {
        public String customerName;
        public int customerId;
        public String customerSignaturePath;
        public String daySymptom;
        public int empId;
        public String empName;
        public String empSignaturePath;
        public int id;
        public String partnerName;
        public String projectName;
        public String remarks;
        public int status;
        public String statusName;
        public String physicianSignaturePath;
        public String painAssessment;
        public String purchaseRecordRemarks;
        public int fieldQueueId;
    }
}
