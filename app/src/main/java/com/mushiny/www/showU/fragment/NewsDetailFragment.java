package com.mushiny.www.showU.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.util.ToastUtil;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * 新闻详情页面
 */
public class NewsDetailFragment extends BaseFragment {

    private WebView x5WebView;
    private WebSettings x5WebSettings;

    @BindView(R.id.linear_new_detail)LinearLayout linear_new_detail;// x5 webView 父布局
    @BindView(R.id.loading_new_detail)MaterialProgressBar loading_new_detail;

    public final static String KEY_URL = "url";
    public final static String TAG = NewsDetailFragment.class.getSimpleName();

    private String url = "http://www.baidu.com";

    public NewsDetailFragment() {
        // Required empty public constructor
    }

    // 单例
    public static NewsDetailFragment newInstance(){
        return new NewsDetailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_detail, container, false);
        ButterKnife.bind(this, view);

        initData();
        loadWebPage();

        return view;
    }

    private void initData() {

        Bundle bundle = getArguments();
        url = bundle.getString(NewsDetailFragment.KEY_URL);

        // x5的webview创建
        if (x5WebView == null){
            x5WebView = new WebView(getContext().getApplicationContext());
        }

    }

    private void loadWebPage() {

//        ToastUtil.showToast(getContext(), "url = " + url);

        if (x5WebView != null){
            IX5WebViewExtension ix5WebViewExtension = x5WebView.getX5WebViewExtension();
            if (ix5WebViewExtension != null){
                ix5WebViewExtension.setScrollBarFadingEnabled(false);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            x5WebView.setLayoutParams(params);
            linear_new_detail.addView(x5WebView);

            // x5 webView 设置
            x5WebView.clearCache(true);
            x5WebView.clearHistory();
            x5WebView.clearFormData();

            // x5 webSettings 设置
            x5WebSettings = x5WebView.getSettings();
            x5WebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            x5WebSettings.setJavaScriptEnabled(true);
            x5WebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            // 加载页面适应手机屏幕
            x5WebSettings.setUseWideViewPort(true);
            x5WebSettings.setLoadWithOverviewMode(true);
            x5WebSettings.setBuiltInZoomControls(true);
            x5WebSettings.setLoadsImagesAutomatically(true);
            x5WebSettings.setDefaultTextEncodingName("utf-8");
            x5WebSettings.setSavePassword(false);
            x5WebSettings.setDomStorageEnabled(true);

            // 设置 WebViewClient
            x5WebView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView webView, String url) {

                    // 如果不是http或者https开头的url，那么使用手机自带的浏览器打开
                    if (!url.startsWith("http://") && !url.startsWith("https://")){
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                            return true;
                        }catch (Exception e){
                            e.printStackTrace();
                            return true;
                        }
                    }
                    webView.loadUrl(url);
                    return false;

//                    return super.shouldOverrideUrlLoading(webView, s);
                }

                @Override
                public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                    super.onReceivedSslError(webView, sslErrorHandler, sslError);
                }
            });

            // 设置 WebChromeClient
            x5WebView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(WebView webView, int i) {
                    super.onProgressChanged(webView, i);
                    // 页面加载完，隐藏进度提示
                    if (i == 100){
                        loading_new_detail.setVisibility(View.GONE);
                    }

                }
            });

            // 根据url加载页面
//            x5WebView.loadUrl(Constants.URL_qq_browser_feedback);
            x5WebView.loadUrl(url);
        }

    }

    /**
     * 网页是否可以后退
     * @return
     */
    public boolean canBack(){
        if (x5WebView.canGoBack()){
            x5WebView.goBack();
            return true;
        }
        return false;
    }

    private void clearX5WebView() {
        if (x5WebView != null){
            x5WebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            x5WebView.clearHistory();
            x5WebView.clearCache(true);// 清除缓存

            ((ViewGroup)x5WebView.getParent()).removeView(x5WebView);
            x5WebView.destroy();
            x5WebView = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (x5WebView != null){
            clearX5WebView();
        }
    }
}
