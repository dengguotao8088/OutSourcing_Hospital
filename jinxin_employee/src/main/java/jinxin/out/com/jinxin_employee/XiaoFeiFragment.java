package jinxin.out.com.jinxin_employee;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.necer.ndialog.NDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jinxin.out.com.jinxin_employee.JsonModule.BaseModule;
import jinxin.out.com.jinxin_employee.JsonModule.Employee;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/12.
 */

public class XiaoFeiFragment extends BaseFragment {

    private int custorm_id;

    private View mView;
    private PullToRefreshListView mList;

    private int tab_id = 0;
    private Button mGoumai_btn;
    private Button mDangri_btn;
    private ImageView cameraView;

    private List<PurchaseRecord> mPurchList = new ArrayList<>();
    private List<ConsumptionRecord> mConsumptionList = new ArrayList<>();

    private int colorEnable;
    private int colorDisable;
    private MyAdapter mGoumaiAdapter = new MyAdapter();
    private MyAdapter2 mDangRiAdapter = new MyAdapter2();

    private boolean is_adapter_set = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEmployee = LoginManager.getInstance(mActivity).getEmployee();
        colorEnable = mActivity.getColor(R.color.tab_bar);
        colorDisable = mActivity.getColor(R.color.tab_bar_bac);
        custorm_id = getArguments().getInt("custorm_id", -1);
        mPurchList.clear();
        mConsumptionList.clear();
        boolean reset = getArguments().getBoolean("xiaofei_fragement_reset", false);
        if (reset) {
            //tab_id = 0;
        }
        //if (tab_id == 0) {
        //loadGouMaiList();
        //} else if (tab_id == 1) {
        //loadDangRiList();
        //}
        isFirstShow = false;
        if (tmp_capture == null) {
            tmp_capture = new File(mActivity.getExternalCacheDir(), "capture_tmp.png").getAbsolutePath();
        }
        can_upload_cuspic = false;
        updata_cuspic_count();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.xiaofei_main, container, false);
        mGoumai_btn = mView.findViewById(R.id.xiaofei_title_goumaijilu);
        mDangri_btn = mView.findViewById(R.id.xiaofei_title_dangri_xiaofei);
        mGoumai_btn.setOnClickListener(mTitle_Btn);
        mDangri_btn.setOnClickListener(mTitle_Btn);
        refreshTitle();
        cameraView = mView.findViewById(R.id.xiaofei_title_camera);
        cameraView.setOnClickListener(camera_click);

        ImageView back = mView.findViewById(R.id.xiao_title_back);
        back.setOnClickListener(mBackListener);

        mList = mView.findViewById(R.id.my_xiaofei_layout_list);
        initListView(mList, null);
        mList.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        mList.setOnItemClickListener(onItemClickListener);
        refreshAdapter();
        isViewCreate = true;
        loadGouMaiList();
        loadDangRiList();
        return mView;
    }

    private static final int REQUEST_CODE_IMAGE = 0x100;
    private static final int REQUEST_CODE_CAMERA = 0x101;
    private View.OnClickListener camera_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showPopFormBottom(null);
        }
    };

    private TakePhotoPopWin takePhotoPopWin;
    private View popView;
    private TextView camera;
    private TextView gallery;
    private TextView local_gallery;

    public void showPopFormBottom(View view) {
        if (takePhotoPopWin == null) {
            popView = LayoutInflater.from(mActivity).inflate(R.layout.take_photo_pop, null);
            takePhotoPopWin = new TakePhotoPopWin(popView);
            takePhotoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
                    params.alpha = 1f;
                    mActivity.getWindow().setAttributes(params);
                }
            });

            TextView cacel = popView.findViewById(R.id.pop_cancel);
            cacel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (takePhotoPopWin != null) {
                        takePhotoPopWin.dismiss();
                    }
                }
            });
            TextView camera = popView.findViewById(R.id.pop_camera);
            camera.setOnClickListener(pop_camera_click);
            TextView gallery = popView.findViewById(R.id.pop_gallery);
            gallery.setOnClickListener(pop_gallery_click);
            TextView local_gallery = popView.findViewById(R.id.pop_local_gallery);
            local_gallery.setOnClickListener(pop_local_gallery_click);
        }
        takePhotoPopWin.showAtLocation(mView.findViewById(R.id.xiaofei_main_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = 0.7f;
        mActivity.getWindow().setAttributes(params);
    }

    private View.OnClickListener pop_camera_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!LoginManager.getInstance(mActivity).isNetworkConnected()) {
                mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST,
                        "没有网络，请稍后重试"));
                return;
            }
            if (!can_upload_cuspic) {
                mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST,
                        "数量已达上限，无法上传，请删除后在操作"));
                return;
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(tmp_capture);
            Uri imageUri = null;
            if (Build.VERSION.SDK_INT >= 24) {
                imageUri = FileProvider.getUriForFile(mActivity.getApplicationContext(), "jinxin.out.jinxin_employee.fileprovider", file);
            } else {
                imageUri = Uri.fromFile(file);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
            takePhotoPopWin.dismiss();
        }
    };

    private View.OnClickListener pop_gallery_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!LoginManager.getInstance(mActivity).isNetworkConnected()) {
                mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST,
                        "没有网络，请稍后重试"));
                return;
            }
            if (!can_upload_cuspic) {
                mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST,
                        "数量已达上限，无法上传，请删除后在操作"));
                return;
            }
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, REQUEST_CODE_IMAGE);
            takePhotoPopWin.dismiss();
        }
    };

    private KehuGalleryFragment kehuGalleryFragment;
    private View.OnClickListener pop_local_gallery_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (kehuGalleryFragment == null) {
                kehuGalleryFragment = new KehuGalleryFragment();
                kehuGalleryFragment.mParentFragment = XiaoFeiFragment.this;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("cus_id", custorm_id);
            kehuGalleryFragment.setArguments(bundle);
            mActivity.showContent(kehuGalleryFragment);
            takePhotoPopWin.dismiss();
        }
    };

    private String tmp_capture;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            String path = null;
            Uri uri = data.getData();
            Cursor cursor = mActivity.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                cursor.close();
            }
            if (path != null && !path.equals("")) {
                uploadBitmap(path);
            }
        } else if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            uploadBitmap(tmp_capture);
        }
    }

    private File tem_upload_file;

    private void uploadBitmap(final String path) {
        mActivity.showHUD("图片上传中");
        new Thread() {
            @Override
            public void run() {
                if (!LoginManager.getInstance(mActivity).isNetworkConnected()) {
                    mActivity.dissmissHUD();
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "没有网络,上传失败"));
                }
                if (path == null || "".equals(path)) {
                    mActivity.dissmissHUD();
                    return;
                }
                File file = new File(tmp_capture);
                Bitmap tem_bitmap = BitmapFactory.decodeFile(path, null);
                Bitmap bitmap = Bitmap.createScaledBitmap(tem_bitmap, 450, 450, true);
                if (tem_upload_file == null) {
                    String pa = mActivity.getExternalCacheDir().getAbsolutePath();
                    tem_upload_file = new File(pa, "tmp_upload_pop.png");
                }

                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] buffer = bos.toByteArray();
                    if (buffer != null) {
                        if (tem_upload_file.exists()) {
                            tem_upload_file.delete();
                        }
                        OutputStream outputStream = null;

                        outputStream = new FileOutputStream(tem_upload_file);

                        outputStream.write(buffer);
                        outputStream.close();
                        bos.close();
                    }
                } catch (FileNotFoundException e) {
                    mActivity.dissmissHUD();
                    return;
                } catch (IOException e) {
                    mActivity.dissmissHUD();
                    return;
                } finally {
                    bitmap.recycle();
                    tem_bitmap.recycle();
                    if (file.exists()) {
                        file.delete();
                    }
                }
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"),
                        tem_upload_file);

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", tem_upload_file.getName(),
                                fileBody)
                        .build();
                NetPostUtil.post("http://medical.mind-node.com/files/upload_app", requestBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mActivity.dissmissHUD();
                        mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "上传失败"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        mActivity.dissmissHUD();
                        String result = response.body().string();
                        Log.d("dengguotao", result);
                        if (response.code() == 200) {
                            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
                            if (module.code == 1) {
                                if (mMainHandler != null) {
                                    mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                                    return;
                                }
                            }
                            if (module.code == 0) {
                                //mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "上传成功"));
                                JsonModule jsonModule = JsonUtil.parsoJsonWithGson(result, JsonModule.class);
                                String path = jsonModule.data.path;
                                String name = jsonModule.data.picName;
                                saveCusPicture(path, name);
                            } else {
                                mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "上传失败"));
                            }
                        } else {
                            mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "上传失败"));
                        }
                    }
                });
            }
        }.start();
    }

    private void saveCusPicture(String path, String name) {
        RequestBody body = new FormBody.Builder()
                .add("token", LoginManager.getInstance(mActivity).getToken())
                .add("customerId", custorm_id + "")
                .add("picPath", path)
                .add("picName", name)
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/customer_gallery/save?"
                , body, save_pic_callback);
    }

    private Callback save_pic_callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "上传失败"));
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
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, module.message));
                    updata_cuspic_count();
                } else {
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, module.message));
                }
            } else {
                mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "上传失败"));
            }
        }
    };

    private boolean can_upload_cuspic = false;

    private void updata_cuspic_count() {
        RequestBody body = new FormBody.Builder()
                .add("token", LoginManager.getInstance(mActivity).getToken())
                .add("customerId", custorm_id + "")
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/customer_gallery/count?"
                , body, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() == 200) {
                            String result = response.body().string();
                            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
                            if (module.code == 1) {
                                if (mMainHandler != null) {
                                    mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                                    can_upload_cuspic = false;
                                    return;
                                }
                            }
                            if (module.code == 0) {
                                can_upload_cuspic = true;
                            } else {
                                can_upload_cuspic = false;
                            }
                        }
                    }
                });
    }

    private class JsonModule extends BaseModule {
        public Data data;

        private class Data {
            public String path;
            public String picName;
        }
    }

    private View.OnClickListener mTitle_Btn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int tab = -1;
            if (view.getId() == R.id.xiaofei_title_goumaijilu) {
                tab = 0;
                loadGouMaiList();
            } else {
                tab = 1;
                //if (mConsumptionList.size() == 0) {
                loadDangRiList();
                //}
            }
            if (tab != tab_id) {
                tab_id = tab;
                refreshTitle();
                refreshAdapter();
            }

        }
    };

    private KehuXiaoFeiFragment mKehuXiaoFeiFragment;
    private AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (tab_id != 0) {
                        return;
                    }
                    PurchaseRecord record = mPurchList.get((int) l);
                    if (mKehuXiaoFeiFragment == null) {
                        mKehuXiaoFeiFragment = new KehuXiaoFeiFragment();
                    }
                    Bundle data = new Bundle();
                    data.putInt("purch_id", record.id);
                    data.putString("p_name", record.projectName);
                    data.putString("remark", record.remark);
                    mKehuXiaoFeiFragment.setArguments(data);
                    mKehuXiaoFeiFragment.mParentFragment = XiaoFeiFragment.this;
                    mActivity.showContent(mKehuXiaoFeiFragment);
                }
            };

    private void refreshAdapter() {
        if (tab_id == 0) {
            mList.setAdapter(mGoumaiAdapter);
        } else {
            mList.setAdapter(mDangRiAdapter);
        }
        mList.onRefreshComplete();
    }

    @Override
    public void refreshData() {
        if (tab_id == 0) {
            loadGouMaiList();
        } else {
            loadDangRiList();
        }
    }

    @Override
    public void loadData() {
    }

    @Override
    public void refreshUI() {
        if (isViewCreate) {
            refreshTitle();
            //if(!is_adapter_set) {
                //refreshAdapter();
                //is_adapter_set = true;
            //}
            if (tab_id == 0) {
                mGoumaiAdapter.notifyDataSetChanged();
            } else {
                mDangRiAdapter.notifyDataSetChanged();
            }
        }
    }

    private void loadGouMaiList() {
        RequestBody body = new FormBody.Builder().add("token",
                LoginManager.getInstance(mActivity).getToken())
                .add("customerId", custorm_id + "")
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/purchase_record/list?", body,
                goumaiListCallback);
    }


    private void loadDangRiList() {
        RequestBody body = new FormBody.Builder().add("token",
                LoginManager.getInstance(mActivity).getToken())
                .add("customerId", custorm_id + "")
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/consumption_record/real_list?", body,
                dangRiListCallback);
    }

    private Callback goumaiListCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() != 200) {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
                return;
            }
            String result = response.body().string();
            BaseModule baseModule = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (baseModule.code == 1) {
                if (mMainHandler != null) {
                    mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                    return;
                }
            }

            if (baseModule.code == 0) {
                mPurchList.clear();
                PurchaseRecordModule purchaseRecordModule = JsonUtil.parsoJsonWithGson(result,
                        PurchaseRecordModule.class);
                mPurchList.addAll(purchaseRecordModule.data);
                mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
            } else {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
            }
        }
    };


    private Callback dangRiListCallback = new Callback() {

        //http://staff.mind-node.com/staff/api/consumption_record/real_list?token=11111&customerId=5
        @Override
        public void onFailure(Call call, IOException e) {
            mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() != 200) {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
                return;
            }
            String result = response.body().string();
            Log.d("dengguotao", result);
            BaseModule baseModule = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (baseModule.code == 1) {
                if (mMainHandler != null) {
                    mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                    return;
                }
            }
            if (baseModule.code == 0) {
                mConsumptionList.clear();
                ConsumptionRecordModule purchaseRecordModule = JsonUtil.parsoJsonWithGson(result,
                        ConsumptionRecordModule.class);
                mConsumptionList.addAll(purchaseRecordModule.data);
                mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
            } else {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
            }
        }
    };

    private class MyAdapter extends BaseAdapter {

        private class ViewHolder {
            public TextView project_name;
            public TextView kehu_name;
            public TextView baohan_cishu;
            public TextView yiyong_cishu;
            public Button status;
            public Button add_xiaofei;
        }

        @Override
        public int getCount() {
            return mPurchList.size();
        }

        @Override
        public Object getItem(int i) {
            return mPurchList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(mActivity).inflate(R.layout.goumaijilu, viewGroup,
                        false);
                viewHolder.project_name = view.findViewById(R.id.goumailiaocheng);
                viewHolder.status = view.findViewById(R.id.goumai_status);
                viewHolder.kehu_name = view.findViewById(R.id.guomai_cusname);
                viewHolder.baohan_cishu = view.findViewById(R.id.baohancishu);
                viewHolder.yiyong_cishu = view.findViewById(R.id.yiyongcishu);
                viewHolder.add_xiaofei = view.findViewById(R.id.tianjiaxiaofei_btn);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            PurchaseRecord record = mPurchList.get(i);
            viewHolder.project_name.setText(record.projectName);
            viewHolder.kehu_name.setText(record.customerName);
            viewHolder.baohan_cishu.setText(record.projectFrequency + "");
            viewHolder.yiyong_cishu.setText(record.useFrequency + "");
            viewHolder.status.setText(record.statusName);
            viewHolder.add_xiaofei.setBackgroundColor(
                    (record.projectFrequency - record.useFrequency) > 0 ? colorEnable : colorDisable);
            viewHolder.add_xiaofei.setTag(record);
            viewHolder.add_xiaofei.setOnClickListener(mAdd_xiaofeiClick);
            //viewHolder.add_xiaofei.setClickable(
                    //(record.projectFrequency - record.useFrequency) > 0);
            return view;
        }
    }

    private AlertDialog add_dialog;
    private int add_click_id = -1;
    private View.OnClickListener mAdd_xiaofeiClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PurchaseRecord record = (PurchaseRecord) view.getTag();
            if ((record.projectFrequency - record.useFrequency) <= 0) {
                return;
            }
            add_click_id = record.id;
            if (add_dialog == null) {
                add_dialog = new NDialog(mActivity).setTitle("请输入消费备注")
                        .setInputText("")
                        .setInputTextSize(14)
                        .setInputType(InputType.TYPE_CLASS_TEXT)
                        .setInputLineColor(Color.parseColor("#00ff00"))
                        .setPositiveButtonText("添加")
                        .setNegativeButtonText("取消")
                        .setNegativeTextColor(Color.parseColor("#c1c1c1"))
                        .setOnInputListener(new NDialog.OnInputListener() {
                            @Override
                            public void onClick(String inputText, int which) {
                                //which,0代表NegativeButton，1代表PositiveButton
                                if (which == 1) {
                                    if (inputText == null) inputText = "";
                                    add_xiaofei(add_click_id, inputText);
                                }
                            }
                        }).create(NDialog.INPUT);
            }
            add_dialog.show();
        }
    };

    // http://staff.mind-node.com/staff/api/consumption_record/save?token=11111111
    // &purchaseRecordId=11&remarks=消费记录备注
    private void add_xiaofei(int p_id, String remark) {
        if (add_click_id == -1 || remark == null) return;
        mActivity.showHUD("添加中");
        RequestBody body = new FormBody.Builder()
                .add("token", LoginManager.getInstance(mActivity).getToken())
                .add("purchaseRecordId", p_id + "")
                .add("remarks", remark)
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/consumption_record/save?", body,
                mAddBack);
    }

    private Callback mAddBack = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mActivity.dissmissHUD();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            mActivity.dissmissHUD();
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
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "添加成功"));
                    loadGouMaiList();
                }
            }
        }
    };

    private class MyAdapter2 extends BaseAdapter {

        private class ViewHolder {
            public TextView project_name;
            public TextView kehu_name;
            public TextView date_year;
            public TextView date_hour;
            public TextView fuwu_reyuan;
            public Button wolaifuwu;
            public Button do_work;
            public Button push_msg;
            public Button click_change;
        }

        @Override
        public int getCount() {
            return mConsumptionList.size();
        }

        @Override
        public Object getItem(int i) {
            return mConsumptionList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(mActivity).inflate(R.layout.dangrixiaofei, viewGroup,
                        false);
                viewHolder.project_name = view.findViewById(R.id.dangriliaocheng);
                viewHolder.kehu_name = view.findViewById(R.id.kehu_name);
                viewHolder.date_year = view.findViewById(R.id.jinrixiaofei_year);
                viewHolder.date_hour = view.findViewById(R.id.jinrixiaofei_time);
                viewHolder.fuwu_reyuan = view.findViewById(R.id.fuwu_people);
                viewHolder.do_work = view.findViewById(R.id.shishi_btn);
                viewHolder.push_msg = view.findViewById(R.id.xiaoxituisong_btn);
                viewHolder.click_change = view.findViewById(R.id.dianjixiugaibtn);
                viewHolder.wolaifuwu = view.findViewById(R.id.fuwu_btn);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            ConsumptionRecord record = mConsumptionList.get(i);
            viewHolder.project_name.setText(record.projectName);
            viewHolder.do_work.setText(record.statusName);
            viewHolder.do_work.setClickable(false);
            viewHolder.kehu_name.setText(record.customerName);
            String date = JsonUtil.getDate2(record.createTime);
            viewHolder.date_year.setText(date.substring(0, date.indexOf("-")));
            viewHolder.date_hour.setText(date.substring(date.indexOf("-") + 1));
            viewHolder.fuwu_reyuan.setText(record.empName);
            viewHolder.push_msg.setClickable(record.messagePush);
            viewHolder.push_msg.setBackgroundColor(record.messagePush ?
                    colorEnable : colorDisable);
            viewHolder.push_msg.setTag(i);
            viewHolder.push_msg.setOnClickListener(push_msg_click);
            viewHolder.wolaifuwu.setTag(record);
            viewHolder.wolaifuwu.setOnClickListener(wolaifuwu_click);
            viewHolder.wolaifuwu.setClickable(record.myService);
            viewHolder.wolaifuwu.setBackgroundColor(record.myService ?
                    colorEnable : colorDisable);
            viewHolder.click_change.setTag(record.id);
            viewHolder.click_change.setOnClickListener(do_change_click);
            return view;
        }
    }

    private View.OnClickListener push_msg_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            ConsumptionRecord record = mConsumptionList.get(position);
            String content = record.daySymptom == null ? "" : record.daySymptom;
            String pro_name = record.projectName == null ? "" : record.projectName;
            push_msg(content, pro_name);
        }
    };


    private Callback m_push_Callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "推送失败"));
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() == 200) {
                String result = response.body().string();
                Log.d("dengguotao", result);
                BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
                if (module.code == 1) {
                    mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                    return;
                }
                if (module.code == 0) {
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "推送成功"));
                } else {
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, module.message));
                }
            } else

            {
                mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "推送失败"));
            }
        }
    };

    // http://staff.mind-node.com/staff/api/purchase_record/push?
// customerId  content  projectName
    private void push_msg(String content, String pro_name) {
        if (!LoginManager.getInstance(mActivity).isNetworkConnected()) {
            mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "没有网络，请稍后重试"));
        }
        RequestBody body = new FormBody.Builder()
                .add("token", LoginManager.getInstance(mActivity).getToken())
                .add("customerId", custorm_id + "")
                .add("content", content)
                .add("projectName", pro_name)
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/push_message_record/push?", body,
                m_push_Callback);
    }

    private QianMing mQianMing;
    private Employee mEmployee;
    private View.OnClickListener wolaifuwu_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!LoginManager.getInstance(mActivity).isNetworkConnected()) {
                mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "没有网络，请稍后重试"));
            }
            ConsumptionRecord record = (ConsumptionRecord) view.getTag();
            RequestBody body = new FormBody.Builder()
                    .add("token", LoginManager.getInstance(mActivity).getToken())
                    .add("id", record.id + "")
                    .add("empId", mEmployee.id + "")
                    .add("fieldQueueId", record.fieldQueueId + "")
                    .build();
            NetPostUtil.post("http://staff.mind-node.com/staff/api/consumption_record/server?", body,
                    wolaifuwu_Callback);
        }
    };

    private Callback wolaifuwu_Callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "服务失败"));
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() == 200) {
                String result = response.body().string();
                Log.d("dengguotao", result);
                BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
                if (module.code == 1) {
                    mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                    return;
                }
                if (module.code == 0) {
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "服务成功"));
                    loadDangRiList();
                } else {
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, module.message));
                }
            } else

            {
                mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "服务失败"));
            }
        }
    };

    private XiaoFeiDetailFragment mXiaoFeiDetailFragment;
    private View.OnClickListener do_change_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mXiaoFeiDetailFragment == null) {
                mXiaoFeiDetailFragment = new XiaoFeiDetailFragment();
                mXiaoFeiDetailFragment.mParentFragment = XiaoFeiFragment.this;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("prcu_id", (Integer) view.getTag());
            mXiaoFeiDetailFragment.setArguments(bundle);
            mActivity.showContent(mXiaoFeiDetailFragment);
        }
    };

    public class PurchaseRecordModule extends BaseModule {
        public List<PurchaseRecord> data;
    }

    public class PurchaseRecord {
        public int id;//
        public int customerId;//客户Id
        public String customerName;//客户姓名
        public int projectId;//项目Id
        public String projectName;//项目名称
        public int projectFrequency;//一次包含项目次数
        public int useFrequency;//使用次数
        public Double totalPrice;//总价
        public int status;//购买项目的状态，1：可用，2：完成，3：过期，4：退费，5：作废
        public String statusName;
        public int empId;//员工Id
        public String remark;//备注
        public String expirationDate;//到期时间
        public String createTime;//创建时间
        public String updateTime;//更新时间
    }

    public class ConsumptionRecordModule extends BaseModule {
        public List<ConsumptionRecord> data;
    }

    public class ConsumptionRecord {
        public int id;//
        public int purchaseRecordId;//购买记录Id
        public int empId;//员工Id
        public String empSignaturePath;//员工签名路径
        public String customerSignaturePath;//客户签名路径
        public String daySymptom;//当日症状
        public int status;//当前消费记录状态
        public String statusName;//当前消费记录状态名称
        public String partnerName;//合作人姓名
        public String commentLevel;//评论等级
        public String commentContent;//评论内容
        public String remarks;//备注
        public String endTime;//结束时间
        public String createTime;//创建时间
        public String updateTime;//更新时间
        public int fieldQueueId;
        public int customerId;

        public String empName;//员工姓名
        public String customerName;//客户姓名
        public String projectName;//项目名称

        public boolean myService;//我来服务按钮状态（可点击、不可点击）
        public boolean messagePush;//消息推送按钮（可点击、不可点击）
    }

    private void refreshTitle() {
        if (tab_id == 0) {
            mGoumai_btn.setBackgroundResource(R.drawable.xiaofei_title_btn_parent_bac);
            mGoumai_btn.setTextColor(mActivity.getColor(R.color.tab_bar));
            mDangri_btn.setBackgroundResource(R.drawable.xiaofei_title_btn_bac);
            mDangri_btn.setTextColor(mActivity.getColor(R.color.white));
        } else {
            mDangri_btn.setBackgroundResource(R.drawable.xiaofei_title_btn_parent_bac);
            mDangri_btn.setTextColor(mActivity.getColor(R.color.tab_bar));
            mGoumai_btn.setBackgroundResource(R.drawable.xiaofei_title_btn_bac);
            mGoumai_btn.setTextColor(mActivity.getColor(R.color.white));
        }
    }

    public class TakePhotoPopWin extends PopupWindow {

        private View view;

        private TextView btn_cancel;


        public TakePhotoPopWin(View v) {
            view = v;

            setOutsideTouchable(true);
            // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {

                    int height = view.findViewById(R.id.pop_layout).getTop();

                    int y = (int) event.getY();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (y < height) {
                            dismiss();
                        }
                    }
                    return true;
                }
            });


            this.setContentView(this.view);
            // 设置弹出窗体的宽和高
            this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
            this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

            // 设置弹出窗体可点击
            this.setFocusable(true);

            // 设置弹出窗体显示时的动画，从底部向上弹出
            this.setAnimationStyle(R.style.take_photo_anim);

        }
    }
}
