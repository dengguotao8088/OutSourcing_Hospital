package jinxin.out.com.jinxin_employee;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import jinxin.out.com.jinxin_employee.JsonModule.BaseModule;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/7.
 */

public class ZhiQinDetail extends BaseFragment {

    private int Id = -1;
    private String cusName;

    private View mView;
    private TextView mContent;
    private TextView mre;
    private TextView mdate;
    private ImageView mqianming;

    private ZhiQinDetailModule module;
    private ZhiQin zhiqin;

//    url : http://staff.mind-node.com/staff/api/customer_informed_consent_record/get?token=11111&id=11
//    responseParam {
//        {
//            "code": 0,
//                "action": "",
//                "message": "获取客户知情同意书成功",
//                "data": {
//            "customerId": 5,
//                    "customerSignaturePath": "http://img002.21cnimg.com/photos/album/20150702/m600/2D79154370E073A2BA3CD4D07868861D.jpeg",
//                    "id": 1,
//                    "informedConsentTemplateId": 1,
//                    "relationShip": "本人",
//                    "updateTime": 1501506529000
//        }
//        }
//    }

    public class MyResponse extends BaseModule {
        public ZhiQinDetailModule data;
    }

    public class ZhiQinDetailModule {
        public String customerSignaturePath;
        public int id;
        public String informedConsentContent;
        public String content;
        public int customerId;
        public int informedConsentTemplateId;
        public String relationShip;
        public String updateTime;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private Callback mback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("dengguotao", result);
            MyResponse response1 = JsonUtil.parsoJsonWithGson(result, MyResponse.class);
            if (response1.code == 0) {
                module = response1.data;
                //RequestBody body2 = new FormBody.Builder()
                //        .add("token", LoginManager.getInstance(getActivity()).getToken())
                //        .add("id", module.informedConsentTemplateId + "").build();
                //NetPostUtil.post("http://staff.mind-node.com/staff/api/informed_consent_template/get?", body2, mback2);
                if (mMainHandler != null) {
                    mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
                }
            }
        }
    };
    private Callback mback2 = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            ZhiQinItem item = JsonUtil.parsoJsonWithGson(result, ZhiQinItem.class);
            if (item.code == 0) {
                zhiqin = item.data;
                if (mMainHandler != null) {
                    mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
                }
            }
        }
    };

    /**
     * 1、根据Id获取知情同意书
     * {
     * url : http://staff.mind-node.com/staff/api/informed_consent_template/get?token=11111&id=1
     * responseParam {
     * {
     * "code": 0,
     * "action": "",
     * "message": "获取知情同意书成功",
     * "data": {
     * "content": "知情同意书1",
     * "createTime": 1501154554000,
     * "id": 1,
     * "status": 1,
     * "title": "知情同意书1",
     * "updateTime": 1501154554000
     * }
     * }
     * }
     * }
     */
    public class ZhiQin {
        public int id;//
        public String title;//标题
        public String content;//内容
        public int status;//状态，1：正常，2：停用
        public String createTime;//创建时间
        public String updateTime;//更新时间
    }

    public class ZhiQinItem extends BaseModule {
        public ZhiQin data;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Id = getArguments().getInt("zhiqin_id");
        cusName = getArguments().getString("custorm_name");
        RequestBody body = new FormBody.Builder()
                .add("token", LoginManager.getInstance(getActivity()).getToken())
                .add("id", Id + "").build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/customer_informed_consent_record/get?", body, mback);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.zhiqin_detail, container, false);
        mContent = mView.findViewById(R.id.zhiqin_content);
        mre = mView.findViewById(R.id.zhiqin_re);
        mdate = mView.findViewById(R.id.zhiqin_date);
        mqianming = mView.findViewById(R.id.zhiqin_qianming);
        TextView title = mView.findViewById(R.id.header_title);
        title.setText("查看知情同意书");
        ImageView button = mView.findViewById(R.id.back);
        button.setOnClickListener(mBackListener);
        isViewCreate = true;
        refreshUI();
        return mView;
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void loadData() {

    }

    @Override
    public void refreshUI() {
        if (isViewCreate && module != null) {
            mContent.setText(module.informedConsentContent);
            mre.setText(module.content);
            mdate.setText("日期:" + JsonUtil.getDate(module.updateTime));
        }
    }

}
