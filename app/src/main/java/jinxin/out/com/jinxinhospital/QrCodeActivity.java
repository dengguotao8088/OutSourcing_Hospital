package jinxin.out.com.jinxinhospital;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import jinxin.out.com.jinxinhospital.R;
import jinxin.out.com.jinxinhospital.util.QRCodeUtil;
import jinxin.out.com.jinxinhospital.view.UserAppCompatActivity;

/**
 * Created by Administrator on 2017/7/16.
 */

public class QrCodeActivity extends UserAppCompatActivity {

    private static final String path = "/sdcard/qr.png";
    private String mName;
    private String mTel;
    private TextView mNameView;
    private TextView mNumView;
    private ImageView mQrView;
    //二维码携带的内容， 格式为mName + ":" + mNum + ":" + mTel
    private String mContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initActionBar();
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = this.getSharedPreferences("jinxin_clien_app", 0);
        mName = sharedPreferences.getString("name", null);
        mTel = sharedPreferences.getString("tel", null);
        //TODO: 二维码数据
        mContent = mTel;

        mNameView = findViewById(R.id.user_qr_name);
        mNumView = findViewById(R.id.user_qr_num);
        mQrView = findViewById(R.id.user_qrcode);

        mNameView.setText(mName);
        mNumView.setText(mTel);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void initActionBar() {
        setToolBarTitle(getApplicationContext().getString(R.string.user_qr_title));
    }
    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("xie", "mContent = " + mContent);
                boolean success = QRCodeUtil.createQRImage(mContent, 800, 800,
                        null, path);
                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("xie", "add mQrView.......");
                            mQrView.setImageBitmap(BitmapFactory.decodeFile(path));
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.qrcodeview;
    }
}
