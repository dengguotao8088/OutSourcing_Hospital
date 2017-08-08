package jinxin.out.com.jinxinhospital;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by Administrator on 2017/8/8.
 */

public class NewsContentFragment extends BaseFragment{
    private View mView;
    private WebView mWebView;
    private String mTitle;
    private String mPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.news_content_layout, container, false);

        mTitle = getArguments().getString("title");
        mPath = getArguments().getString("content");
        mWebView = mView.findViewById(R.id.webView);
        mWebView.loadUrl(mPath);
        return mView;
    }
}
