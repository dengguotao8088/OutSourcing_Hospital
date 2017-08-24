package jinxin.out.com.jinxinhospital;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.ZhiQinDetail.ZhiQinDetaiData;
import jinxin.out.com.jinxinhospital.ZhiQinDetail.ZhiQinDetaiResponseJson;
import jinxin.out.com.jinxinhospital.view.UserAppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/7.
 */

public class ZhiQinDetailActivity extends UserAppCompatActivity {

    private int Id = -1;
    private String cusName;

    public static final int LOAD_DATA_DONE = 0x100;
    public static final int LOAD_DATA_ERROR = 0x101;
    public static final int LOAD_DATA_IIMEOUT = 0x102;
    public static final int SHOW_TOAST = 0x103;
    public static final int NEED_RELOGIN = 0x104;
    private View mView;
    private TextView mContent;
    private TextView mre;
    private TextView mdate;
    private ImageView mqianming;

    private ZhiQinDetaiData module;
    private String token;
    private int customerId;
    private MainHandler mMainHandler;
    private boolean isViewCreate = false;
    private Context mContext;

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
            Log.d("xie", result);
            BaseModule bmodule = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (bmodule.code == 0) {
                ZhiQinDetaiResponseJson response1 = JsonUtil.parsoJsonWithGson(result, ZhiQinDetaiResponseJson.class);
                module = response1.data;
                if (mMainHandler != null) {
                    mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
                    mMainHandler.sendEmptyMessage(0x22);
                }
            }
        }
    };

    public class MainHandler extends Handler {
        public MainHandler(Context context) {
            super(context.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x22:
                    Glide.with(mContext).load(module.customerSignaturePath).into(mqianming);
                    break;
                case LOAD_DATA_DONE:
                    mMainHandler.removeMessages(LOAD_DATA_IIMEOUT);
                    refreshUI();
                    break;
                case LOAD_DATA_ERROR:
                    mMainHandler.removeMessages(LOAD_DATA_IIMEOUT);
                    break;
                case LOAD_DATA_IIMEOUT:
                    mMainHandler.removeMessages(LOAD_DATA_IIMEOUT);
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private ImageLoader imageLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mContent = findViewById(R.id.zhiqin_content);
        mre = findViewById(R.id.zhiqin_re);
        mdate = findViewById(R.id.zhiqin_date);
        mqianming = findViewById(R.id.zhiqin_qianming);
        isViewCreate = true;
        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
        }
        SharedPreferences sharedPreferences = this.getSharedPreferences("jinxin_clien_app", 0);
        token = sharedPreferences.getString("token", null);
        customerId = sharedPreferences.getInt("customerId", -1);
        Id = getIntent().getExtras().getInt("id");
        cusName = getIntent().getExtras().getString("name");
        setToolBarTitle(cusName);
        mMainHandler = new MainHandler(this);
        if (saveDir == null) {
            saveDir = this.getExternalCacheDir().getAbsolutePath();
        }
        module = null;
        File mqianming_file = new File(saveDir, "tmp_qianming.png");
        if (mqianming_file.exists()) {
            mqianming_file.delete();
        }
        RequestBody body = new FormBody.Builder()
                .add("token",token)
                .add("id", Id + "").build();
        NetPostUtil.post("http://client.mind-node.com/client/api/customer_informed_consent_record/get?", body, mback);
    }

    public void refreshUI() {
        if (isViewCreate && module != null) {
            mContent.setText(module.informedConsentContent);
            mre.setText(module.content);
            mdate.setText("日期:" + JsonUtil.getDate(module.updateTime));
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
        RequestBody boby = new FormBody.Builder().build();
        NetPostUtil.post(module.customerSignaturePath, boby, load_qianming_back);
        //imageLoader.displayImage(module.customerSignaturePath,mDetailPhotoView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.zhiqin_detail;
    }
}
