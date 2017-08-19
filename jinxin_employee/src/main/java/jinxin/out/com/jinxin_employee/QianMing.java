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
 * <p>
 * <p>
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
    public static final int MODE_XIAOFEI_DETAIL = 3;
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

    private int zhiqin_mode;

    private int zhiqin_id;

    private int zhiqin_cus_id;
    private int zhiqin_info_id;
    private String zhiqin_relation;

    private int xiaofeidetail_mode;
    private int xiaofeidetail_cusid;
    private int xiaofeidetail_cpid;
    private int filedQueId;

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
        zhiqin_mode = -1;
        zhiqin_id = -1;
        zhiqin_cus_id = -1;
        zhiqin_info_id = -1;
        zhiqin_relation = null;
        xiaofeidetail_mode = -1;
        xiaofeidetail_cusid = -1;
        xiaofeidetail_cpid = -1;
        filedQueId = -1;
        if (mode == MODE_TUIFEI) {
            tuifei_id = getArguments().getInt("tuifei_id");
            custom_id = getArguments().getInt("cus_id");
        } else if (mode == MODE_ZHIQIN) {
            zhiqin_mode = getArguments().getInt("zhiqin_module");
            if (zhiqin_mode == 1) {
                zhiqin_cus_id = getArguments().getInt("zhiqin_cus_id");
                zhiqin_info_id = getArguments().getInt("zhiqin_info_id");
                zhiqin_relation = getArguments().getString("zhiqin_relation");
            } else if (zhiqin_mode == 2) {
                zhiqin_id = getArguments().getInt("zhiqin_id");
            }
        } else if (mode == MODE_XIAOFEI_DETAIL) {
            xiaofeidetail_mode = getArguments().getInt("xiaofeidetail_mode");
            xiaofeidetail_cusid = getArguments().getInt("xiaofeidetail_cusid");
            xiaofeidetail_cpid = getArguments().getInt("xiaofeidetail_conid");
            filedQueId = getArguments().getInt("fieldQueueId");
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
            uploadQianming();
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
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    mActivity.dissmissHUD();
                    upload.setClickable(true);
                    String result = response.body().string();
                    if (response.code() == 200) {
                        BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
                        if (module.code == 1) {
                            if (mMainHandler != null) {
                                mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                                return;
                            }
                        }
                        if (module.code == 0) {
                            JsonModule jsonModule = JsonUtil.parsoJsonWithGson(result,
                                    JsonModule.class);
                            path = jsonModule.data.path;
                            if (mode == MODE_TUIFEI) {
                                do_tuifei(path);
                            } else if (mode == MODE_ZHIQIN) {
                                if (zhiqin_mode == 1) {
                                    do_addZhiqin(path);
                                } else {
                                    do_updateZhiqin(path);
                                }
                            } else if (mode == MODE_XIAOFEI_DETAIL) {
                                uploadXiaofeiDetailQianming(path);
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

    private void uploadXiaofeiDetailQianming(String path) {
        if (xiaofeidetail_cusid == -1 || xiaofeidetail_cpid == -1) {
            return;
        }
        if (xiaofeidetail_mode == 3) {
            RequestBody body = new FormBody.Builder()
                    .add("token", LoginManager.getInstance(mActivity).getToken())
                    .add("customerId", xiaofeidetail_cusid + "")
                    .add("consumptionRecordId", xiaofeidetail_cpid + "")
                    .add("customerSignaturePath", path)
                    .build();
            NetPostUtil.post("http://staff.mind-node.com/staff/api/field_queue/save?"
                    , body, tuifei_back);
        } else {
            RequestBody body = new FormBody.Builder()
                    .add("token", LoginManager.getInstance(mActivity).getToken())
                    .add("customerId", xiaofeidetail_cusid + "")
                    .add("consumptionRecordId", xiaofeidetail_cpid + "")
                    .add("empSignaturePath", path)
                    .add("fieldQueueId", filedQueId + "")
                    .build();
            NetPostUtil.post("http://staff.mind-node.com/staff/api/field_queue/update?"
                    , body, tuifei_back);

        }
    }

    private void do_addZhiqin(String path) {
        if (zhiqin_cus_id == -1 || zhiqin_info_id == -1) {
            return;
        }
        RequestBody body = new FormBody.Builder()
                .add("token", LoginManager.getInstance(mActivity).getToken())
                .add("customerId", zhiqin_cus_id + "")
                .add("informedConsentTemplateId", zhiqin_info_id + "")
                .add("relationShip", zhiqin_relation)
                .add("customerSignaturePath", path)
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/customer_informed_consent_record/save?"
                , body, tuifei_back);
    }

    private void do_updateZhiqin(String path) {
        if (zhiqin_id == -1) {
            return;
        }
        RequestBody body = new FormBody.Builder()
                .add("token", LoginManager.getInstance(mActivity).getToken())
                .add("id", zhiqin_id + "")
                .add("customerSignaturePath", path)
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/customer_informed_consent_record/update"
                , body, tuifei_back);
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
                if (module.code == 1) {
                    if (mMainHandler != null) {
                        mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                        return;
                    }
                }
                if (module.code == 0) {
                    String msg = mode == MODE_ZHIQIN ?
                            (zhiqin_mode == 1 ? "添加知情同意书成功!" : "更新知情同意书成功!")
                            : "退费成功!";
                    if (mode == MODE_XIAOFEI_DETAIL) {
                        if (xiaofeidetail_mode == 3) {
                            msg = "客户签名成功!";
                        } else {
                            if (mParentFragment instanceof XiaoFeiFragment) {
                                msg = "我来服务操作成功!";
                            } else {
                                msg = "技师签名成功!";
                            }
                        }
                    }
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, msg));
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
