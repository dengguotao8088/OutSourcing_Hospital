package jinxin.out.com.jinxin_employee;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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

    private int Id;
    private String cusName;

    private HomeActivity mActivity;

    private View mView;
    private TextView mContent;
    private TextView mre;
    private TextView mdate;
    private ImageView mqianming;

    private ZhiQinDetailModule module;

    private MyHandler handler;

    private class MyHandler extends Handler {
        public MyHandler(Context context) {
            super(context.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 111:
                    mContent.setText("sdkfjlsdkljfkljsdjlfkskldjfjklsdjklf");
                    mre.setText("我是" + cusName + module.relationShip);
                    mdate.setText("日期:" + JsonUtil.getDate(module.updateTime));
                    break;
                default:
                    break;
            }
        }
    }

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
        public int informedConsentTemplateId;
        public String relationShip;
        public String updateTime;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new MyHandler(getActivity());
        RequestBody body = new FormBody.Builder()
                .add("token", LoginManager.getInstance(getActivity()).getToken())
                .add("id", Id + "").build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/customer_informed_consent_record/get?", body, mback);
    }

    private Callback mback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            MyResponse response1 = JsonUtil.parsoJsonWithGson(result, MyResponse.class);
            if (response1.code == 0) {
                module = response1.data;
                handler.sendEmptyMessage(111);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (HomeActivity) context;
        Id = getArguments().getInt("zhiqin_id");
        cusName = getArguments().getString("custorm_name");
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
        return mView;
    }

    private View.OnClickListener mBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivity.showContent(mParentFragment);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mParentFragment == null || mActivity == null) return false;
        mActivity.showContent(mParentFragment);
        return true;
    }
}
