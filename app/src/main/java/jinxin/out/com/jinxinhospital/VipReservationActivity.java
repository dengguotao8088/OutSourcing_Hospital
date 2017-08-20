package jinxin.out.com.jinxinhospital;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jinxin.out.com.jinxinhospital.Department.DepartmentResponseJson;
import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.Reservation.Reservation;
import jinxin.out.com.jinxinhospital.Reservation.ReservationResponseJson;
import jinxin.out.com.jinxinhospital.view.UserAppCompatActivity;
import jinxin.out.com.jinxinhospital.view.UserListView;
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
    private NumberPicker mDayView, mHourView, mMinView;
    private Spinner mDepSpinner;
    private Button mSubmitButton;
    private UserListView mListView;
    private TextView mEmptyMsg;
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
    private MyAdapter myAdapter;
    private Calendar mDate;
    private String mDayString;
    private int mDayIndex = 0;
    private int mHourString;
    private int mMinString;
    private String[] mDayArray = {"0", "0", "0"};
    private List<Reservation> mReservationList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.vip_reservation_layout;
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setToolBarTitle(getApplicationContext().getString(R.string.vip_title));
        super.onCreate(savedInstanceState);
        mContext = this;
        myAdapter = new MyAdapter();
        myHandler = new MyHandler(mContext);
        mHourView = findViewById(R.id.vip_hour);
        mMinView = findViewById(R.id.vip_min);
        mDayView = findViewById(R.id.vip_day);

        mDayView.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mHourView.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mMinView.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        mDepSpinner = findViewById(R.id.vip_dep);
        mEmptyMsg = findViewById(R.id.vip_list_msg);
        mSubmitButton = findViewById(R.id.vip_submit);
        mListView = findViewById(R.id.vip_reservation_list);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
        token = sharedPreferences.getString("token", null);
        customerId = sharedPreferences.getInt("customerId", -1);
        mDate = Calendar.getInstance();
        mDepAdpater = new ArrayAdapter<String>(mContext, R.layout.base_item, R.id.vip_dep_item, mDepList);
        mDepSpinner.setAdapter(mDepAdpater);
        mListView.setAdapter(myAdapter);
        mSubmitButton.setOnClickListener(mOnClickListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDepList();
                getReservationList();
            }
        }).start();

        setTimeLimit();

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

    private int mMinute;
    private int mHour;
    private void setTimeLimit(){
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mWay = c.get(Calendar.DAY_OF_WEEK);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        if ( mHour < 15) {
            mHourView.setMaxValue(17);
            mHourView.setMinValue((mHour+2)>9 ? (mHour+2) : 9);
            mDayView.setMinValue(0);
            mDayView.setMaxValue(2);
            mDayString = mYear +"-" +mMonth +"-"+ mDay;
            mHourString = mHour + 2;
            mMinString = mMinute;
            mDayArray [0] = mYear +"-" +mMonth +"-"+ (mDay++);
            mDayArray [1] = mYear +"-" +mMonth +"-"+ (mDay++);
            mDayArray [2] = mYear +"-" +mMonth +"-"+ mDay;
            mMinView.setMinValue(mMinute + 1);
            mMinView.setMaxValue(59);
        } else {
            mHourView.setMaxValue(17);
            mHourView.setMinValue(9);
            mDayView.setMinValue(0);
            mDayView.setMaxValue(1);
            mDayString = mYear +"-" +mMonth +"-"+ mDay;
            mHourString = 9;
            mMinString = 0;
            mMinView.setMinValue(0);
            mMinView.setMaxValue(59);
            mDayArray [0] = mYear +"-" +mMonth +"-"+ (++mDay);
            mDayArray [1] = mYear +"-" +mMonth +"-"+ (++mDay);
        }
        mDayView.setDisplayedValues(mDayArray);

        mHourView.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mHourString = mHourView.getValue();
                if (mHourString == 17) {
                    mMinView.setMinValue(0);
                    mMinView.setMaxValue(0);
                    return;
                }
                if (mHourString !=  mHour+2) {
                    mMinView.setMinValue(0);
                    mMinView.setMaxValue(59);
                } else if (mHourString == mHour+2 && mDayArray.length == 3 && mDayIndex == 0){
                    mMinView.setMinValue(mMinute + 1);
                    mMinView.setMaxValue(59);
                } else {
                    mMinView.setMinValue(0);
                    mMinView.setMaxValue(59);
                }
            }
        });
        mMinView.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mMinString = mMinView.getValue();
            }
        });
        mDayView.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mDayIndex = mDayView.getValue();
                mDayString = mDayArray[mDayIndex];
                if(mDayIndex == 2) {
                    mHourView.setMinValue(9);
                    mHourView.setMaxValue(mHour);
                    mMinView.setMinValue(0);
                    mMinView.setMaxValue(59);
                } else if (mDayIndex == 0 && mHourString != mHour + 2) {
                    mHourView.setMaxValue(17);
                    mHourView.setMinValue((mHour+2)>9 ? (mHour+2) : 9);
                } else if (mDayIndex == 0 && mHourString == mHour + 2) {
                    mHourView.setMaxValue(17);
                    mHourView.setMinValue((mHour+2)>9 ? (mHour+2) : 9);
                    mMinView.setMinValue(mMinute + 1);
                    mMinView.setMaxValue(59);
                } else if (mDayIndex == 1) {
                    mHourView.setMinValue(9);
                    mHourView.setMaxValue(17);
                    mMinView.setMinValue(0);
                    mMinView.setMaxValue(59);
                }
            }
        });
    }

    private void onTimeDataChange() {
        String tmp = mDayString + " " +  mHourString + ":" +  mMinString + ":00";
        dateString = DateToStr(StrToDate(tmp));
        Log.d("xie", "dateString = " +  dateString.toString());
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.vip_submit:
                    onTimeDataChange();
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
//                    mTimeView.setText(DateToStr(StrToDate(dateString)));
                    break;
                case 0x22:
                    mDepAdpater.notifyDataSetChanged();
                    break;
                case 0x33:
                    getReservationList();
                    mDepAdpater.notifyDataSetChanged();
                    Toast.makeText(mContext, mTempString, Toast.LENGTH_LONG).show();
                    break;
                case 0x44:
                    if (mReservationList.isEmpty()) {
                        mEmptyMsg.setVisibility(View.VISIBLE);
                    } else{
                        mEmptyMsg.setVisibility(View.GONE);
                    }
                    myAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getReservationList() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("customerId", customerId +"")
                .build();
        NetPostUtil.post(Constants.GET_RESER_WITH_ID, requestBody, mReservationListCallback);
    }

    private void getDepList() {
        Log.d("xie", "getDepList....");
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .build();
        NetPostUtil.post(Constants.GET_DEPARTMENT_LIST, requestBody, mDepCallback);
    }

    private Callback mReservationListCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie" , "mReservationListCallback result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            }
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0 ) {
                return;
            }
            ReservationResponseJson reservationResponseJson
                    = JsonUtil.parsoJsonWithGson(result, ReservationResponseJson.class);
            mReservationList.clear();
            int length = reservationResponseJson.data.length < 5
                    ? reservationResponseJson.data.length
                    : 5;
            for(int i=0; i<length; i++) {
                mReservationList.add(reservationResponseJson.data[i]);
            }
            myHandler.sendEmptyMessage(0x44);
        }
    };

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
        Date date = null;
        Log.d("xie","str: " +str);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String DateToStr(Date date) {

        Log.d("xie","date: " +date);
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str = format.format(date);
            return str;
        }
        return "";
    }

    private class MyAdapter extends BaseAdapter {

        public class ViewHolder {
            public TextView requstTime;
            public TextView status;
            public TextView reservatonTime;
            public TextView dep;
        }

        @Override
        public int getCount() {
            return mReservationList.size();
        }

        @Override
        public Object getItem(int i) {
            return mReservationList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.vip_reservation_item, viewGroup, false);
                holder = new ViewHolder();
                holder.requstTime = view.findViewById(R.id.vip_request_time);
                holder.reservatonTime = view.findViewById(R.id.vip_reservation_time);
                holder.dep = view.findViewById(R.id.vip_reservation_dep);
                holder.status = view.findViewById(R.id.vip_reservation_status);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Reservation data = mReservationList.get(i);
            holder.requstTime.setText(JsonUtil.getDate(data.createTime));
            holder.reservatonTime.setText(JsonUtil.getDate(data.reservationTime));
            holder.dep.setText(data.departmentName);
            holder.status.setText(data.statusName);
            return view;
        }
    }
}
