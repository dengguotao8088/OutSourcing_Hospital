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

public class QianMing extends BaseFragment {
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
