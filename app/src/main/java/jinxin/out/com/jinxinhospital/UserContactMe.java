package jinxin.out.com.jinxinhospital;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import jinxin.out.com.jinxinhospital.view.UserAppCompatActivity;

/**
 * Created by Administrator on 2017/7/13.
 */

public class UserContactMe extends UserAppCompatActivity {

    private static final String TAG = "JinXin_ContactMe";
    private Intent mIntent;
    private TextView mTelView;
    private TextView mTel1View;
    private static final String TEL = "15188888888";
    private static final String TEL1 = "02888888888";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntent = getIntent();
        initActionBar();


        mTelView = findViewById(R.id.user_contantme_tel);
        mTel1View = findViewById(R.id.user_contantme_tel1);
        mTelView.setText(TEL);
        mTel1View.setText(TEL1);

        mTelView.setOnLongClickListener(onClickListener);
        mTel1View.setOnLongClickListener(onClickListener);
    //    getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initActionBar() {
        setToolBarTitle("");
    }

    View.OnLongClickListener onClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View view) {
            Intent intent = new Intent();
            switch (view.getId()) {
                case R.id.user_contantme_tel:
                    intent.setAction(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(TEL));
                    startActivity(intent);
                    break;
                case R.id.user_contantme_tel1:
                    intent.setAction(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(TEL1));
                    startActivity(intent);
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_contactme;
    }
}
