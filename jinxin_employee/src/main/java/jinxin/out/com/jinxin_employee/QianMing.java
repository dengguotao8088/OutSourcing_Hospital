package jinxin.out.com.jinxin_employee;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import jinxin.out.com.jinxin_employee.view.LinePathView;

/**
 * Created by Administrator on 2017/8/7.
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
 *
 *
 * 2、添加客户知情同意书
 {
 url : http://staff.mind-node.com/staff/api/customer_informed_consent_record/save?
 token=eyJ0eXAiOiJqd3QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1MDE1MDQ4MjMsInN1YiI6IntcInVzZXJJZFwiOlwiM
 VwiLFwidXNlck5hbWVcIjpcIua1i-ivlVwiLFwiam9iTnVtYmVyXCI6XCIwMDFcIn0iLCJleHAiOjE1MDIxMDk2MjMsIm5iZiI6
 MTUwMTUwNDgyM30.s1TYLIV75hP4io-xfefmJ-u9NLTXSAQXzYOW7r1hkHM
 &customerId=5&informedConsentTemplateId=1&relationShip=本人&customerSignaturePath=http://img002.21cnimg.com/photos/album/20150702/m600/2D79154370E073A2BA3CD4D07868861D.jpeg
 responseParam {
 {
 {
 "code": 0,
 "action": "",
 "message": "添加客户知情同意书成功",
 "data": ""
 }
 }
 }
 }
 */
public class QianMing extends BaseFragment {
    public static final int MODE_ZHIQIN = 1;
    public static final int MODE_TUIFEI = 2;

    private HomeActivity activity;

    private View mView;
    private Button save;
    private Button upload;
    private Button refresh;
    private LinePathView qianmingban;
    private ImageView yulan;

    private String mSaveDir;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
        //mSaveDir = activity.gete
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.qianming, container, false);
        save = mView.findViewById(R.id.qianming_save);
        save.setOnClickListener(msaveListener);
        upload = mView.findViewById(R.id.qianming_upload);
        upload.setOnClickListener(muploadListener);
        qianmingban = mView.findViewById(R.id.qianminban);
        qianmingban.setBackColor(getActivity().getColor(android.R.color.darker_gray));
        qianmingban.setPaintWidth(4);
        qianmingban.setPenColor(getActivity().getColor(android.R.color.holo_red_light));
        qianmingban.init(getActivity());
        refresh = mView.findViewById(R.id.qianming_refresh);
        refresh.setOnClickListener(mrefreshListener);
        yulan = mView.findViewById(R.id.qianming_yulan);
        TextView title = mView.findViewById(R.id.header_title);
        title.setText("签名");
        ImageView button = mView.findViewById(R.id.back);
        button.setOnClickListener(mBackListener);
        isViewCreate = true;
        return mView;
    }

    private View.OnClickListener msaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bitmap bitmap = qianmingban.getBitMap();
            if (bitmap != null && !bitmap.isRecycled()) {
                yulan.setImageBitmap(bitmap);
            }
        }
    };
    private View.OnClickListener mrefreshListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            qianmingban.clear();
        }
    };
    private View.OnClickListener muploadListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    @Override
    public void refreshData() {

    }

    @Override
    public void loadData() {

    }

    @Override
    public void refreshUI() {

    }
}
