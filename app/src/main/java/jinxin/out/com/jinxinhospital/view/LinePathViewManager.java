package jinxin.out.com.jinxinhospital.view;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;

import jinxin.out.com.jinxinhospital.view.LinePathView;

/**
 * Created by Administrator on 2017/7/10.
 */

public class LinePathViewManager {
    private static final String TAG = "JinXin";
    private Context context;
    private LinePathView linePathView;
    private int width;
    private int height;

    //    LinePathViewManager(Context context, LinePathView linePathView) {
    LinePathViewManager(Context context) {
        this.context = context;
    }

    public LinePathView InitLinePathView(int width, int height, int paintWidth, int backgroundColor, int penColor) {
        linePathView = new LinePathView(context);
        Log.d(TAG, "InitLinePathView()");
        int mWidth = width > 0 ? width : 250;
        int mHeight = height > 0 ? height : 250;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mWidth, mHeight);
        linePathView.setLayoutParams(layoutParams);
        linePathView.setBackgroundColor(backgroundColor);
        linePathView.setPenColor(Color.RED);
        linePathView.setPaintWidth(50);
        linePathView.setPenColor(penColor);
        linePathView.setPaintWidth(paintWidth);
        linePathView.init(context);

        return linePathView;
    }

    public void save() {
        if (linePathView.getTouched()) {
            try {
                linePathView.save("/sdcard/qm.png");
                Toast.makeText(context, "保存成功....", Toast.LENGTH_SHORT).show();
//                setResult(100);
//                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "您没有签名~", Toast.LENGTH_SHORT).show();
        }
    }

    public void clear() {
        linePathView.clear();
    }
}
