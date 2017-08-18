package jinxin.out.com.jinxin_employee;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import jinxin.out.com.jinxin_employee.JsonModule.BaseModule;
import jinxin.out.com.jinxin_employee.view.LinePathView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/7.
 */


/**
 * 1、点击签名（退费申请）
 * {
 * url : http://staff.mind-node.com/staff/api/customer_refund_apply_record/update?token=111&id=11
 * &customerId=12&customerSignaturePath=客户退费申请签字图片地址
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
 * {
 * url : http://staff.mind-node.com/staff/api/customer_informed_consent_record/save?
 * token=eyJ0eXAiOiJqd3QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1MDE1MDQ4MjMsInN1YiI6IntcInVzZXJJZFwiOlwiM
 * VwiLFwidXNlck5hbWVcIjpcIua1i
 * -ivlVwiLFwiam9iTnVtYmVyXCI6XCIwMDFcIn0iLCJleHAiOjE1MDIxMDk2MjMsIm5iZiI6
 * MTUwMTUwNDgyM30.s1TYLIV75hP4io-xfefmJ-u9NLTXSAQXzYOW7r1hkHM
 * &customerId=5&informedConsentTemplateId=1&relationShip=本人&customerSignaturePath=http
 * ://img002.21cnimg.com/photos/album/20150702/m600/2D79154370E073A2BA3CD4D07868861D.jpeg
 * responseParam {
 * {
 * {
 * "code": 0,
 * "action": "",
 * "message": "添加客户知情同意书成功",
 * "data": ""
 * }
 * }
 * }
 * }
 */
public class QianMing extends BaseFragment {
    public static final int MODE_ZHIQIN = 1;
    public static final int MODE_TUIFEI = 2;
    private String url = "http://medical.mind-node.com/files/upload_app";

    private HomeActivity activity;

    private View mView;
    private Button save;
    private Button upload;
    private Button refresh;
    private LinePathView qianmingban;
    private ImageView yulan;

    private String mSaveDir;
    private File mtem;

    public int mode;

    private int tuifei_id;
    private int custom_id;

    private String path;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mSaveDir == null) {
            mSaveDir = mActivity.getExternalCacheDir().getAbsolutePath();
            mtem = new File(mSaveDir, "tmp.png");
        }
        tuifei_id = -1;
        custom_id = -1;
        if (mode == MODE_TUIFEI) {
            tuifei_id = getArguments().getInt("tuifei_id");
            custom_id = getArguments().getInt("cus_id");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
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
            try {
                qianmingban.save(mtem.getAbsolutePath(), true, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            mActivity.showHUD("上传中");
            upload.setClickable(false);
            //uploadTask.cancel(true);
            //uploadTask.execute("");
            uploadQianming();
        }
    };

    AsyncTask uploadTask = new AsyncTask() {
        @Override
        protected Object doInBackground(Object[] objects) {
            uploadQianming();
            return null;
        }
    };

    private void uploadQianming() {
        try {
            if (!LoginManager.getInstance(mActivity).isNetworkConnected()) {
                mActivity.dissmissHUD();
                upload.setClickable(true);
                return;
            }
            boolean result = qianmingban.save(mtem.getAbsolutePath(), true, 0);
            if (!result) {
                mActivity.dissmissHUD();
                upload.setClickable(true);
                return;
            }

            RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"),
                    mtem);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", mtem.getName(),
                            fileBody)
                    .build();
            NetPostUtil.post(url, requestBody, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mActivity.dissmissHUD();
                    upload.setClickable(true);
                    Log.d("dengguotao", "upload error");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    mActivity.dissmissHUD();
                    upload.setClickable(true);
                    String result = response.body().string();
                    if (response.code() == 200) {
                        BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
                        if (module.code == 0) {
                            JsonModule jsonModule = JsonUtil.parsoJsonWithGson(result,
                                    JsonModule.class);
                            path = jsonModule.data.path;
                            Log.d("dengguotao", path);
                            if (mode == MODE_TUIFEI) {
                                do_tuifei(path);
                            }
                        }
                    }
                }
            });
        } catch (IOException e) {
            mActivity.dissmissHUD();
            upload.setClickable(true);
        }
    }

    //http://staff.mind-node.com/staff/api/customer_refund_apply_record/update?
    //  token=111&id=11&customerId=12&customerSignaturePath=客户退费申请签字图片地址
    private void do_tuifei(String path) {
        if (custom_id == -1 || tuifei_id == -1) {
            return;
        }
        RequestBody body = new FormBody.Builder()
                .add("token", LoginManager.getInstance(mActivity).getToken())
                .add("id", tuifei_id + "")
                .add("customerId", custom_id + "")
                .add("customerSignaturePath", path)
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/customer_refund_apply_record/update?"
                , body, tuifei_back);
    }

    private Callback tuifei_back = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() == 200) {
                String result = response.body().string();
                BaseModule module = JsonUtil.parsoJsonWithGson(result,
                        BaseModule.class);
                if (module.code == 0) {
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "退费成功!"));
                    mActivity.showContent(mParentFragment);
                }
            }
        }
    };

    private class JsonModule extends BaseModule {
        public Data data;

        private class Data {
            public String path;
        }
    }

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
