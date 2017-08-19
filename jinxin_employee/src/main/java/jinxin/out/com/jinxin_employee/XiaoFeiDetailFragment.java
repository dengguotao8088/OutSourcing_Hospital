package jinxin.out.com.jinxin_employee;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

    private String[] tengTong_pg = {"无痛",
            "轻微疼痛",
            "轻度疼痛",
            "中度疼痛",
            "重度疼痛",
            "剧烈疼痛"};

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

    private TextView kehuqianming;
    private ImageView kehuqianming_img;
    private Button kehuqianming_btn;

    private TextView jishiqianming;
    private ImageView jishiqianming_img;
    private Button jishiqianming_btn;

    private TextView yishiqianming;
    private ImageView yishiqianming_img;
    private Button yishiqianming_btn;

    private AlertDialog zhenduan_content_dialog;
    private AlertDialog tt_pg_dialog;

    private QianMing mQianMing;

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

            kehuqianming_img.setImageURI(null);
            jishiqianming_img.setImageURI(null);
            yishiqianming_img.setImageURI(null);

            File kehuqianming = new File(mSaveDir, "tmp_kehuqianming.png");
            if (kehuqianming.exists()) {
                kehuqianming_img.setImageURI(Uri.fromFile(kehuqianming));
            }

            File jishiqianming = new File(mSaveDir, "tmp_jishiqianming.png");
            if (jishiqianming.exists()) {
                jishiqianming_img.setImageURI(Uri.fromFile(jishiqianming));
            }

            File yishiqianming = new File(mSaveDir, "tmp_kehuqianming.png");
            if (yishiqianming.exists()) {
                yishiqianming_img.setImageURI(Uri.fromFile(yishiqianming));
            }
        }
    }

    private String mSaveDir;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xiaofei_ID = getArguments().getInt("prcu_id");
        if (mSaveDir == null) {
            mSaveDir = mActivity.getExternalCacheDir().getAbsolutePath();
        }
        if (ZhenduanList.size() == 0) {
            RequestBody body = new FormBody.Builder().add("token",
                    LoginManager.getInstance(mActivity).getToken())
                    .build();
            NetPostUtil.post("http://staff.mind-node.com/staff/api/health_diagnostic_template/list?",
                    body, getZhenDuanMoban_callback);
        }
        mCurrentPurRecord = new CurrentPurRecord();
        loadDetail();
    }

    //http://staff.mind-node.com/staff/api/consumption_record/get?
    private void loadDetail() {
        RequestBody body = new FormBody.Builder().add("token",
                LoginManager.getInstance(mActivity).getToken())
                .add("id", xiaofei_ID + "")
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
        zhenduan_bt.setOnClickListener(zd_btn_click);
        ttpinggu_ed = mView.findViewById(R.id.fuwujilu_ed_tengtong_neirong);
        ttpinggu_bt = mView.findViewById(R.id.fuwujilu_tengtong_btn);
        ttpinggu_bt.setOnClickListener(tt_pg_btn_click);
        xiaofei_remark_ed = mView.findViewById(R.id.fuwujilu_ed_beizhu);
        goumai_remark = mView.findViewById(R.id.fuwujilu_gm_beizhu);
        partener_remark = mView.findViewById(R.id.fuwujilu_tv_hehuoren);

        kehuqianming = mView.findViewById(R.id.fuwujilu_kehuqianming);
        kehuqianming_img = mView.findViewById(R.id.fuwujilu_kehuqianming_img);
        kehuqianming_btn = mView.findViewById(R.id.fuwujilu_kehuqianming_btn);
        kehuqianming_btn.setOnClickListener(kehuqianMing);

        jishiqianming = mView.findViewById(R.id.fuwujilu_jishiqianming);
        jishiqianming_img = mView.findViewById(R.id.fuwujilu_jishiqianming_img);
        jishiqianming_btn = mView.findViewById(R.id.fuwujilu_jishiqianming_btn);
        jishiqianming_btn.setOnClickListener(jishiQianming);

        yishiqianming = mView.findViewById(R.id.fuwujilu_yishiqianming);
        yishiqianming_img = mView.findViewById(R.id.fuwujilu_yishiqianming_img);
        yishiqianming_btn = mView.findViewById(R.id.fuwujilu_yishiqianming_btn);
        yishiqianming_btn.setOnClickListener(yishiQianming);

        isViewCreate = true;
        refreshUI();
        return mView;
    }

    private View.OnClickListener zd_btn_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (ZhenduanList.size() == 0) {
                mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST,
                        "诊断模板数据获取中，请稍等!"));
            }
            if (zhenduan_content_dialog == null) {

                zhenduan_content_dialog = new AlertDialog.Builder(mActivity)
                        .setTitle("诊断模板选项")
                        .setItems(zhenduan_title_array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                zhenduan_ed.setText(ZhenduanList.get(i).content);
                            }
                        })
                        .create();
            }
            zhenduan_content_dialog.show();
        }
    };

    private View.OnClickListener tt_pg_btn_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (tt_pg_dialog == null) {
                tt_pg_dialog = new AlertDialog.Builder(mActivity)
                        .setTitle("疼痛评估选项")
                        .setItems(tengTong_pg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ttpinggu_ed.setText(tengTong_pg[i]);
                            }
                        })
                        .create();
            }
            tt_pg_dialog.show();
        }
    };

    private View.OnClickListener kehuqianMing = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mCurrentPurRecord.customerId == -1) {
                return;
            }
            if (mQianMing == null) {
                mQianMing = new QianMing();
                mQianMing.mode = QianMing.MODE_XIAOFEI_DETAIL;
                mQianMing.mParentFragment = XiaoFeiDetailFragment.this;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("xiaofeidetail_mode", 3);
            bundle.putInt("xiaofeidetail_cusid", mCurrentPurRecord.customerId);
            bundle.putInt("xiaofeidetail_conid", xiaofei_ID);
            mQianMing.setArguments(bundle);
            mActivity.showContent(mQianMing);
        }
    };

    private View.OnClickListener jishiQianming = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mCurrentPurRecord.customerId == -1) {
                return;
            }
            if (mQianMing == null) {
                mQianMing = new QianMing();
                mQianMing.mode = QianMing.MODE_XIAOFEI_DETAIL;
                mQianMing.mParentFragment = XiaoFeiDetailFragment.this;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("xiaofeidetail_mode", 4);
            bundle.putInt("xiaofeidetail_cusid", mCurrentPurRecord.customerId);
            bundle.putInt("xiaofeidetail_conid", xiaofei_ID);
            bundle.putInt("fieldQueueId", mCurrentPurRecord.fieldQueueId);
            mQianMing.setArguments(bundle);
            mActivity.showContent(mQianMing);
        }
    };

    private View.OnClickListener yishiQianming = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mQianMing == null) {
                mQianMing = new QianMing();
                mQianMing.mode = QianMing.MODE_XIAOFEI_DETAIL;
                mQianMing.mParentFragment = XiaoFeiDetailFragment.this;
            }
        }
    };

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
                if (module.code == 1) {
                    if (mMainHandler != null) {
                        mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                        return;
                    }
                }
                if (module.code == 0) {
                    JsonModule jsonModule = JsonUtil.parsoJsonWithGson(result, JsonModule.class);
                    mCurrentPurRecord = jsonModule.data;
                    if (null != mCurrentPurRecord.empSignaturePath
                            && !"".equals(mCurrentPurRecord.empSignaturePath)) {
                        loadjsqianming(mCurrentPurRecord.empSignaturePath);
                    }
                    if (null != mCurrentPurRecord.customerSignaturePath
                            && !"".equals(mCurrentPurRecord.customerSignaturePath)) {
                        loadkhqianming(mCurrentPurRecord.customerSignaturePath);
                    }
                    if (null != mCurrentPurRecord.physicianSignaturePath
                            && !"".equals(mCurrentPurRecord.physicianSignaturePath)) {
                        loadysqianming(mCurrentPurRecord.physicianSignaturePath);
                    }
                    mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
                }
            } else {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
            }
        }
    };

    private void loadkhqianming(String path) {
        Log.d("dengguotao","loadkhqianming: "+path);
        NetPostUtil.post(path, null, load_kehuqianming_back);
    }

    private void loadjsqianming(String path) {
        Log.d("dengguotao","loadjsqianming: "+path);
        NetPostUtil.post(path, null, load_jishiqianming_back);
    }

    private void loadysqianming(String path) {
        Log.d("dengguotao","loadysqianming: "+path);
        NetPostUtil.post(path, null, load_yishiqianming_back);
    }

    private class JsonModule extends BaseModule {
        public CurrentPurRecord data;
    }

    private class CurrentPurRecord {
        public String customerName;
        public int customerId = -1;
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

    private List<ZhenDuanMoban> ZhenduanList = new ArrayList<>();
    private String[] zhenduan_title_array;
    private Callback getZhenDuanMoban_callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() == 200) {
                String result = response.body().string();
                Log.d("dengguotao", "result: " + result);
                BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
                if (module.code == 1) {
                    if (mMainHandler != null) {
                        mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                        return;
                    }
                }
                if (module.code == 0) {
                    ZhenDuanMobanModule jsonModule = JsonUtil.parsoJsonWithGson(result, ZhenDuanMobanModule.class);
                    ZhenduanList.clear();
                    ZhenduanList.addAll(jsonModule.data);
                    int size = ZhenduanList.size();
                    if (size == 0) return;
                    zhenduan_title_array = new String[size];
                    for (int i = 0; i < size; i++) {
                        zhenduan_title_array[i] = ZhenduanList.get(i).name;
                    }
                }
            }
        }
    };

    public class ZhenDuanMobanModule extends BaseModule {
        public List<ZhenDuanMoban> data;
    }

    public class ZhenDuanMoban {
        public String content;
        public int id;
        public String name;
    }

    private Callback load_kehuqianming_back = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() != 200) {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
                return;
            }
            InputStream is = null;
            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = null;
            try {
                is = response.body().byteStream();
                long total = response.body().contentLength();
                File mqianming_file = new File(mSaveDir, "tmp_kehuqianming.png");
                if (mqianming_file.exists()) {
                    mqianming_file.delete();
                }
                fos = new FileOutputStream(mqianming_file);
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
            } catch (Exception e) {
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                }
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                }
                mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
            }
        }
    };

    private Callback load_jishiqianming_back = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() != 200) {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
                return;
            }
            InputStream is = null;
            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = null;
            try {
                is = response.body().byteStream();
                long total = response.body().contentLength();
                File mqianming_file = new File(mSaveDir, "tmp_jishiqianming.png");
                if (mqianming_file.exists()) {
                    mqianming_file.delete();
                }
                fos = new FileOutputStream(mqianming_file);
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
            } catch (Exception e) {
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                }
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                }
                mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
            }
        }
    };

    private Callback load_yishiqianming_back = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() != 200) {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
                return;
            }
            InputStream is = null;
            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = null;
            try {
                is = response.body().byteStream();
                long total = response.body().contentLength();
                File mqianming_file = new File(mSaveDir, "tmp_yishiqianming.png");
                if (mqianming_file.exists()) {
                    mqianming_file.delete();
                }
                fos = new FileOutputStream(mqianming_file);
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
            } catch (Exception e) {
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                }
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                }
                mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
            }
        }
    };
}
