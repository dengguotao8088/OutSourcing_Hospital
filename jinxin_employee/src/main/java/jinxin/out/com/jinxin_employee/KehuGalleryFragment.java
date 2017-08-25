package jinxin.out.com.jinxin_employee;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jinxin.out.com.jinxin_employee.JsonModule.BaseModule;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2017/8/20.
 */

public class KehuGalleryFragment extends BaseFragment {
    @Override
    public void refreshData() {

    }

    @Override
    public void loadData() {

    }

    @Override
    public void refreshUI() {
        if (isViewCreate) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private ImageLoader imageLoader;

    private View mView;
    private GridView mGrid;
    private GridAdapter mAdapter = new GridAdapter();
    private FrameLayout mDetailContent;
    private PhotoView mDetailPhotoView;
    private ImageButton delete_btn;

    private int cus_id = -1;

    private List<CustomGalleryRecord> customGalleryRecords = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
        }
        if (!LoginManager.getInstance(mActivity).isNetworkConnected()) {
            mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "没有网络，加载图库失败!"));
        }
        cus_id = getArguments().getInt("cus_id", -1);
        Log.d("dengguotao","cus_id: "+cus_id);
        customGalleryRecords.clear();
        mItems = null;
        //if (cus_id != -1) {
            //load_cus_pic_list();
        //}
        current_click = -1;
    }

    private void load_cus_pic_list() {
        RequestBody body = new FormBody.Builder()
                .add("token", LoginManager.getInstance(mActivity).getToken())
                .add("customerId", cus_id + "")
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/customer_gallery/list?"
                , body, getListCallback);
    }

    private Callback getListCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() == 200) {
                String result = response.body().string();
                Log.d("dengguotao",result);
                BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
                if (module.code == 1) {
                    if (mMainHandler != null) {
                        mMainHandler.sendEmptyMessage(NEED_RELOGIN);
                        return;
                    }
                }
                if (module.code == 0) {
                    JsonModule jsonModule = JsonUtil.parsoJsonWithGson(result, JsonModule.class);
                    customGalleryRecords.clear();
                    customGalleryRecords.addAll(jsonModule.data);
                    mMainHandler.sendEmptyMessage(LOAD_DATA_DONE);
                } else {
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, module.message));
                }
            } else {
                mMainHandler.sendEmptyMessage(LOAD_DATA_ERROR);
            }
        }
    };

    private class JsonModule extends BaseModule {
        public List<CustomGalleryRecord> data;
    }

    private class CustomGalleryRecord {
        public int id;//
        public int customerId;//用户Id
        public String picName;//图片名称
        public String picPath;//图片地址
        public int empId;//员工Id
        public int status;//图片状态，1：正常，2：停用
        public String statusName;//图片状态名称
        public String createTime;//创建时间
        public String updateTime;//更新时间
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.kehu_gallery, container, false);

        TextView title = mView.findViewById(R.id.header_title);
        title.setText("客户图库");
        ImageView button = mView.findViewById(R.id.back);
        button.setOnClickListener(mBackListener);

        mGrid = mView.findViewById(R.id.kehu_gallery_grid);
        mGrid.setAdapter(mAdapter);
        mGrid.setOnItemClickListener(grid_item_click);
        mDetailContent = mView.findViewById(R.id.kehu_gallery_content);
        mDetailContent.setVisibility(View.INVISIBLE);
        mDetailPhotoView = mView.findViewById(R.id.kehu_gallery_photo);
        delete_btn = mView.findViewById(R.id.grid_item_delete);
        delete_btn.setOnClickListener(delete_click);
        isViewCreate = true;
        if (cus_id != -1) {
            load_cus_pic_list();
        }
        return mView;
    }

    private View.OnClickListener delete_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (current_click != -1) {
                delete_pic(customGalleryRecords.get(current_click).id, current_click);
            }
        }
    };

    private void delete_pic(int id, final int position) {
        if (!LoginManager.getInstance(mActivity).isNetworkConnected()) {
            mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "没有网络,删除失败"));
            return;
        }
        RequestBody body = new FormBody.Builder()
                .add("token", LoginManager.getInstance(mActivity).getToken())
                .add("id", id + "")
                .build();
        NetPostUtil.post("http://staff.mind-node.com/staff/api/customer_gallery/delete?"
                , body, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "删除失败"));
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
                                if (mItems.get(position) != null) {
                                    mItems.remove(position);
                                    customGalleryRecords.remove(position);
                                    current_click = -1;
                                    mMainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mDetailContent.setVisibility(View.INVISIBLE);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            } else {
                                mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "删除失败"));
                            }
                        } else {
                            mMainHandler.sendMessage(mMainHandler.obtainMessage(SHOW_TOAST, "没有网络,删除失败"));
                        }
                    }
                });
    }

    private int current_click = -1;
    private Bitmap mShowBitmap;
    private AdapterView.OnItemClickListener grid_item_click = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            view.setDrawingCacheEnabled(true);
            mShowBitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            zoomImageFromThumb(view);
            mDetailPhotoView.setImageBitmap(mShowBitmap);
            imageLoader.displayImage(customGalleryRecords.get((int) l).picPath, mDetailPhotoView);
            current_click = (int) l;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        if (mShowBitmap != null) {
            mShowBitmap.recycle();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private List<View> mItems = new ArrayList<>();

    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return customGalleryRecords.size();
        }

        @Override
        public Object getItem(int i) {
            return customGalleryRecords.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        private class ViewHolder {
            public ImageView imageView;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (mItems == null) {
                mItems = new ArrayList<>();
            }
            View item_view = null;
            if (mItems.size() > i) {
                item_view = mItems.get(i);
            }
            ViewHolder holder;
            if (item_view == null) {
                CustomGalleryRecord record = customGalleryRecords.get(i);
                if (view == null) {
                    view = LayoutInflater.from(mActivity).inflate(R.layout.grid_item, null);
                    holder = new ViewHolder();
                    holder.imageView = view.findViewById(R.id.grid_item_img);
                    imageLoader.displayImage(record.picPath, holder.imageView);
                    view.setTag(holder);
                    item_view = view;
                } else {
                    holder = (ViewHolder) item_view.getTag();
                }
                mItems.add(i, item_view);
            } else {
                holder = (ViewHolder) item_view.getTag();
            }
            return item_view;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isViewCreate && mDetailContent.getVisibility() == View.VISIBLE) {
            mDetailContent.setVisibility(View.INVISIBLE);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Animator mCurrentAnimator;

    private void zoomImageFromThumb(final View thumbView) {
        // 如果有动画在执行，立即取消，然后执行现在这个动画
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        // 计算开始和结束位置的图片范围
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();
        // 开始的范围就是ImageButton的范围，
        // 结束的范围是容器（FrameLayout）的范围
        // getGlobalVisibleRect(Rect)得到的是view相对于整个硬件屏幕的Rect
        // 即绝对坐标，减去偏移，获得动画需要的坐标，即相对坐标
        // getGlobalVisibleRect(Rect,Point)中，Point获得的是view在它在
        // 父控件上的坐标与在屏幕上坐标的偏移
        thumbView.getGlobalVisibleRect(startBounds);
        mView.findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);
        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        // 下面这段逻辑其实就是保持纵横比
        float startScale;
        // 如果结束图片的宽高比比开始图片的宽高比大
        // 就是结束时“视觉上”拉宽了（压扁了）图片
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }
        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        // 隐藏小的图片，展示大的图片。当动画开始的时候，
        // 要把大的图片发在小的图片的位置上
        //小的设置透明
        //thumbView.setAlpha(0f);
        //大的可见
        mDetailContent.setVisibility(View.VISIBLE);
        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        mDetailContent.setPivotX(0f);
        mDetailContent.setPivotY(0f);
        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(mDetailContent, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(mDetailContent, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(mDetailContent, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(mDetailContent,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(300);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        // 再次点击返回小的图片，就是上面扩大的反向动画。即预览完成
        final float startScaleFinal = startScale;
        mDetailPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }
                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(mDetailContent, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(mDetailContent,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(mDetailContent,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(mDetailContent,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(500);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDetailContent.setVisibility(View.INVISIBLE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mDetailContent.setVisibility(View.INVISIBLE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
