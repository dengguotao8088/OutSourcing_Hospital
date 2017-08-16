package jinxin.out.com.jinxinhospital;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jinxin.out.com.jinxinhospital.Department.Department;
import jinxin.out.com.jinxinhospital.Department.DepartmentResponseJson;
import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.view.UserAppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/16.
 */

public class VipReservationActivity extends UserAppCompatActivity {

    private String token;
    private TextView mTimeView;
    private Spinner mDepSpinner;
    private Button mSubmitButton;
    private ListView mListView;
    private List<String> mDepList = new ArrayList<>();
    private List<Integer> mDepIndexList = new ArrayList<>();
    private Context mContext;
    private ArrayAdapter<String> mDepAdpater;
    private String dateString = "";
    private String timeString = "";
    private MyHandler myHandler;
    private int customerId;
    private int departmentId= -1;
    private String mTempString;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBarTitle(getResources().getString(R.string.vip_title));
        mContext = this;
        myHandler = new MyHandler(mContext);
        mTimeView = findViewById(R.id.vip_time);
        mDepSpinner = findViewById(R.id.vip_dep);
        mSubmitButton = findViewById(R.id.vip_submit);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
        token = sharedPreferences.getString("token", null);
        customerId = sharedPreferences.getInt("customerId", -1);
        mDepAdpater = new ArrayAdapter<String>(mContext, R.layout.base_item, R.id.vip_dep_item, mDepList);
        mDepSpinner.setAdapter(mDepAdpater);
        mTimeView.setOnClickListener(mOnClickListener);
        mSubmitButton.setOnClickListener(mOnClickListener);
        getDepList();

        mDepSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                departmentId = mDepIndexList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.vip_time:
                    dateString = null;
                    timeString = null;
                    TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            timeString = " " + timePicker.getHour() + ":" + timePicker.getMinute() + ":" + "00";
                            dateString += timeString;
                            Log.d("xie", "dateString = " + dateString);
                            myHandler.sendEmptyMessage(0x11);
                        }
                    }, 9, 0, true);
                    timePickerDialog.show();

                    DatePickerDialog datePicker=new DatePickerDialog( mContext, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            dateString  = year + "-" + monthOfYear + "-" + dayOfMonth + " ";

                        }
                    }, 2017, 1, 1);
                    datePicker.show();
                    break;
                case R.id.vip_submit:
                    RequestBody requestBody = new FormBody.Builder()
                            .add("token", token)
                            .add("customerId", customerId + "")
                            .add("departmentId", departmentId +"")
                            .add("reservationTime", dateString)
                            .build();
                    NetPostUtil.post(Constants.ADD_RESER, requestBody, mSubmitCallback);
                    break;
                default:
                    break;
            }
        }
    };

    private class MyHandler extends Handler {
        public MyHandler(Context context) {
            super(context.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x11:
                    mTimeView.setText(DateToStr(StrToDate(dateString)));
                    break;
                case 0x22:
                    mDepAdpater.notifyDataSetChanged();
                    break;
                case 0x33:
                    Toast.makeText(mContext, mTempString, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private void getDepList() {
        Log.d("xie", "getDepList....");
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .build();
        NetPostUtil.post(Constants.GET_DEPARTMENT_LIST, requestBody, mDepCallback);
    }

    private Callback mSubmitCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("xie", "mSubmitCallback onFailure");
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie" , "mSubmitCallback result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            }
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            mTempString = module.message;
            myHandler.sendEmptyMessage(0x33);
        }
    };

    private Callback mDepCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("xie", "mDepCallback onFailure");
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie" , "mDepCallback result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            }
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0 ) {
                return;
            }
            DepartmentResponseJson departmentResponseJson
                    = JsonUtil.parsoJsonWithGson(result, DepartmentResponseJson.class);
            mDepList.clear();
            for(int i=0; i<departmentResponseJson.data.length; i++) {
                mDepList.add(departmentResponseJson.data[i].name);
                mDepIndexList.add(departmentResponseJson.data[i].id);
            }
            myHandler.sendEmptyMessage(0x22);
        }
    };

    public static Date StrToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String DateToStr(Date date) {

        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str = format.format(date);
            return str;
        }
        return "";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.vip_reservation_layout;
    }
}
