package jinxin.out.com.jinxin_employee;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/7/22.
 */

public class TabItem extends FrameLayout {
    public View topView;
    public ImageView content_img;
    public TextView content_text;
    public LinearLayout content_layout;

    public TabItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.tab_item, this);
        topView = findViewById(R.id.tab_item_top);
        content_img = findViewById(R.id.tab_img);
        content_text = findViewById(R.id.tab_text);
        content_layout = findViewById(R.id.tab_item_content);
    }

    public TabItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TabItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
