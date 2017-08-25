package jinxin.out.com.jinxin_employee;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
            if (response.code() != 200) {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
                return;
            }
            String result = response.body().string();
            Log.d("dengguotao", result);
            BaseModule bmodule = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (bmodule.code == 1) {
                if (mMainHandler != null) {
                    mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                    return;
                }
            }
            if (bmodule.code == 0) {
                MyResponse response1 = JsonUtil.parsoJsonWithGson(result, MyResponse.class);
                module = response1.data;
                if (mMainHandler != null) {
                    mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
                }
                load_qianming_png();
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
    }

    private ImageLoader imageLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
        }
        Id = getArguments().getInt("zhiqin_id");
        cusName = getArguments().getString("custorm_name");
        if (saveDir == null) {
            saveDir = mActivity.getExternalCacheDir().getAbsolutePath();
        }
        module = null;
        File mqianming_file = new File(saveDir, "tmp_qianming.png");
        if (mqianming_file.exists()) {
            mqianming_file.delete();
        }
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
        RequestBody body = new FormBody.Builder()
                .add("token", LoginManager.getInstance(getActivity()).getToken())
                .add("id", Id + "").build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/customer_informed_consent_record/get?", body, mback);
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
            File mqianming_file = new File(saveDir, "tmp_qianming.png");
            mqianming.setVisibility(View.INVISIBLE);
            if (mqianming_file.exists()) {
                mqianming.setImageURI(Uri.fromFile(mqianming_file));
                mqianming.setVisibility(View.VISIBLE);
            }
        }
    }

    private String saveDir;
    private Callback load_qianming_back = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() != 200) {
                Log.d("dengguotao", "down png: " + response.code());
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
                Log.d("dengguotao", "down png: " + total);
                File mqianming_file = new File(saveDir, "tmp_qianming.png");
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
                mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
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
            }
        }
    };

    private void load_qianming_png() {
        //Log.d("dengguotao", "load png: " + module.customerSignaturePath);
        NetPostUtil.post(module.customerSignaturePath, null, load_qianming_back);
        //imageLoader.displayImage(module.customerSignaturePath,mDetailPhotoView);
    }
}
