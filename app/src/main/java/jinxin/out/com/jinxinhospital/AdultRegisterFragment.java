package jinxin.out.com.jinxinhospital;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jinxin.out.com.jinxinhospital.ArchivesType.ArchivesTypeResponseJson;
import jinxin.out.com.jinxinhospital.Department.DepartmentResponseJson;
import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/13.
 */

public class AdultRegisterFragment extends BaseFragment{

    private View mView;
    private ImageView mBack;
    private Button mOkButton;
    private Context mContext;
    private String mMsg = "";
    private EditText mName;
    private EditText mTel;
    private EditText mPwd;
    private EditText mAddr;
    private EditText mIDCard;
    private Spinner mType;
    private Spinner mSex;
    private Spinner mAllergy;
    private Spinner mDisease;
    private Spinner mDelivery_1;
    private Spinner mDelivery_2;
    private Spinner mWhere;
    private TextView mBirthDay;
    private TextView mBabyBirthDay;
    private Date mBirthDayDate;
    private Date mBabyBirthDayDate;
    private MyHandler mHandler;
    private String mTypeValue;
    private String mSexValue;
    private String mAllergyValue;
    private String mDiseaseValue;
    private String mDeliveryValue;
    private String mDeliverySexValue;
    private String mWhereValue;
    private  int archivesTypeId = 0;
    private ArrayAdapter<String> mTypeAdpater;
    private List<String> mTypeList = new ArrayList<>();
    private List<Integer> mTypeIndexList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.adult_register_layout, container, false);

        initView();
        getTypeList();
        mTypeAdpater = new ArrayAdapter<String>(mContext, R.layout.base_item, R.id.vip_dep_item, mTypeList);
        mType.setAdapter(mTypeAdpater);
        mType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mTypeValue = mTypeIndexList.get(i) + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mHandler = new MyHandler();
        return mView;
    }
    private void getTypeList() {
        Log.d("xie", "getDepList....");
        RequestBody requestBody = new FormBody.Builder().build();
        NetPostUtil.post(Constants.GET_CUS_TYPE, requestBody, mTypeCallback);
        NetPostUtil.post(Constants.GET_ADULT_ARCHIVETYPE, requestBody, mAdultCallback);
    }

    private Callback mAdultCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("xie", "mAdultCallback onFailure");
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie" , "mAdultCallback result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            }
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0 ) {
                return;
            }
            ArchivesTypeResponseJson archivesTypeResponseJson
                    = JsonUtil.parsoJsonWithGson(result, ArchivesTypeResponseJson.class);
            archivesTypeId = archivesTypeResponseJson.data.id;
        }
    };
    private Callback mTypeCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("xie", "mTypeCallback onFailure");
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie" , "mTypeCallback result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            }
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0 ) {
                return;
            }
            DepartmentResponseJson departmentResponseJson
                    = JsonUtil.parsoJsonWithGson(result, DepartmentResponseJson.class);
            mTypeList.clear();
            mTypeIndexList.clear();
            for(int i=0; i<departmentResponseJson.data.length; i++) {
                mTypeList.add(departmentResponseJson.data[i].name);
                mTypeIndexList.add(departmentResponseJson.data[i].id);
            }
            mHandler.sendEmptyMessage(0x44);
        }
    };


    private void initView() {
        mName = mView.findViewById(R.id.r_name);
        mTel = mView.findViewById(R.id.r_tel);
        mPwd = mView.findViewById(R.id.r_pwd);
        mAddr = mView.findViewById(R.id.r_addr);
        mIDCard = mView.findViewById(R.id.r_idcard);
        mType = mView.findViewById(R.id.r_type);
        mSex = mView.findViewById(R.id.r_sex);
        mAllergy = mView.findViewById(R.id.r_allergy);
        mDisease = mView.findViewById(R.id.r_disease);
        mDelivery_1 = mView.findViewById(R.id.r_delivery_situation_1);
        mDelivery_2 = mView.findViewById(R.id.r_delivery_situation_2);
        mWhere = mView.findViewById(R.id.r_where);

        mWhere.setOnItemSelectedListener(onItemClickListener);
        mSex.setOnItemSelectedListener(onItemClickListener);
        mAllergy.setOnItemSelectedListener(onItemClickListener);
        mDisease.setOnItemSelectedListener(onItemClickListener);
        mDelivery_1.setOnItemSelectedListener(onItemClickListener);
        mDelivery_2.setOnItemSelectedListener(onItemClickListener);
        mBirthDay = mView.findViewById(R.id.r_birthday);
        mBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePicker=new DatePickerDialog( mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                 public void onDateSet(DatePicker view, int year, int monthOfYear,
                                       int dayOfMonth) {
                        mBirthDayDate
                                = StrToDate(year + "-" + monthOfYear + "-" + dayOfMonth + " 00:00:00");
                        mHandler.sendEmptyMessage(0x22);

                    }
             }, 2017, 1, 1);
             datePicker.show();
            }
        });
        mBabyBirthDay = mView.findViewById(R.id.r_babyBirthday);
        mBabyBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePicker=new DatePickerDialog( mContext,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        mBabyBirthDayDate
                                = StrToDate(year + "-" + monthOfYear + "-" + dayOfMonth + " 00:00:00");
                        mHandler.sendEmptyMessage(0x33);
                    }
                }, 2017, 1, 1);
                datePicker.show();
            }
        });

        mBack = mView.findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LoadActivity.getObj() != null) {
                    LoadActivity.getObj().showSelf();
                }
            }
        });
        mOkButton = mView.findViewById(R.id.r_ok);
        mOkButton.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("xie", "type = "  + "mTypeValue");
            RequestBody requestBody = new FormBody.Builder()
                    .add("consumptionTypeId", mTypeValue)
                    .add("name", mName.getText().toString())
                    .add("mobile", mTel.getText().toString())
                    .add("password", mPwd.getText().toString())
                    .add("sex", mSexValue)
                    .add("address", mAddr.getText().toString())
                    .add("idCard", mIDCard.getText().toString())
                    .add("birthday", DateToStr(mBirthDayDate))
                    .add("allergy", mAllergyValue)
                    .add("disease", mDiseaseValue)
                    .add("childbirthTime", DateToStr(mBabyBirthDayDate))
                    .add("childbirthSituation", mDeliveryValue)
                    .add("guardianName", "")
                    .add("childbirthSex", mDeliverySexValue)
                    .add("customerSource", mWhereValue)
                    .add("archivesTypeId", archivesTypeId + "")
                    .build();
            NetPostUtil.post(Constants.CUSTOMER_REGIST, requestBody, mRegisterCallBack );
        }
    };

    private Callback mRegisterCallBack = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("xie", "mRegisterCallBack fail.......................");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie", "mRegisterCallBack result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            }
            BaseModule baseModule = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            mMsg = baseModule.message;
            mHandler.sendEmptyMessage(0x11);
        }
    };
    private class MyHandler extends android.os.Handler {
        public MyHandler(){
            super(Looper.getMainLooper());
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x11:
                    Log.d("xie" , "register: msg = " + mMsg);
                    Toast.makeText(mContext, mMsg, Toast.LENGTH_LONG).show();
                    break;
                case 0x22:
                    mBirthDay.setText(DateToStr(mBirthDayDate));
                    break;
                case 0x33:
                    mBabyBirthDay.setText(DateToStr(mBabyBirthDayDate));
                    break;
                case 0x44:
                    mTypeAdpater.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }
    private AdapterView.OnItemSelectedListener onItemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d("xie", "adapterView = " + view + " i = " + i);
            switch (adapterView.getId()) {
                case R.id.r_sex:
                    mSexValue = i + 1 + "";
                    break;
                case R.id.r_allergy:
                    mAllergyValue = i + 1 + "";
                    break;
                case R.id.r_disease:
                    mDiseaseValue = i + 1 + "";
                    break;
                case R.id.r_delivery_situation_1:
                    String[] delivery = getResources().getStringArray(R.array.spinner_delivery);
                    mDeliveryValue = delivery[i];
                    break;
                case R.id.r_delivery_situation_2:
                    mDeliverySexValue = i + 1 + "";
                    break;
                case R.id.r_where:
                    String[] where = getResources().getStringArray(R.array.spinner_where);
                    mWhereValue = where[i];
                    Log.d("xie","where = " + mWhereValue);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

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
}
