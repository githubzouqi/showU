package com.mushiny.www.showU.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.PtrUtil;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class FeedbackActivity extends BaseActivity {

    private WebView x5WebView;
    private WebSettings x5WebSettings;

    @BindView(R.id.linear_webview_feedback)LinearLayout linear_webview_feedback;// x5 webView 父布局
    @BindView(R.id.ptr_frame_feedback)PtrFrameLayout ptr_frame_feedback;

    private String loadUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        initData();
        PtrUtil.newInstance(this).initial(ptr_frame_feedback, PtrUtil.DEFAULT_COLOR);
        ptr_frame_feedback.setMode(PtrFrameLayout.Mode.REFRESH);
        setStatusBar();
        loadWebPage();
    }

    @OnClick({R.id.iv_feedback_exit})
    public void doClick(View view){
        switch (view.getId()){
            case R.id.iv_feedback_exit:
                clearX5WebView();
                finish();
                break;
        }
    }

    private void setStatusBar() {
        // 透明化状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.color_txc));
        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
    }

    private void initData() {
        loadUrl = Constants.url_txc_root + Constants.productId;

        // x5的webview创建
        if (x5WebView == null){
            x5WebView = new WebView(getApplicationContext());
        }
    }

    // 加载主页
    private void loadWebPage() {

        if (x5WebView != null){
            IX5WebViewExtension ix5WebViewExtension = x5WebView.getX5WebViewExtension();
            if (ix5WebViewExtension != null){
                ix5WebViewExtension.setScrollBarFadingEnabled(false);
            }

            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.
                    LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            x5WebView.setLayoutParams(params);
            linear_webview_feedback.addView(x5WebView);

            // x5 webView 设置
            x5WebView.clearCache(true);
            x5WebView.clearHistory();
            x5WebView.clearFormData();

            // x5 webSettings 设置
            x5WebSettings = x5WebView.getSettings();
            x5WebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            x5WebSettings.setJavaScriptEnabled(true);
            x5WebSettings.setJavaScriptCanOpenWindowsAutomatically(true);// 获取当前的内核版本号
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

                    if (url == null){
                        return false;
                    }
                    LogUtil.e("TAG", "shouldOverrideUrlLoading url is:" + url);
                    // 如果不是http或者https开头的url，那么使用手机自带的浏览器打开
                    if (!url.startsWith("http://") && !url.startsWith("https://")){
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                            return true;
                        }catch (Exception e){
//                            e.printStackTrace();
                            return false;
                        }
                    }
//                    webView.loadUrl(url);
                    return false;
//                    return super.shouldOverrideUrlLoading(webView, url);
                }


                @Override
                public void onLoadResource(WebView webView, String s) {
                    super.onLoadResource(webView, s);
                }

                @Override
                public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest,
                                            WebResourceError webResourceError) {
                    super.onReceivedError(webView, webResourceRequest, webResourceError);
                    ptr_frame_feedback.refreshComplete();
                }
            });

            // 设置 WebChromeClient
            x5WebView.setWebChromeClient(new WebChromeClient(){

                @Override
                public void onProgressChanged(final WebView webView, int i) {
                    super.onProgressChanged(webView, i);
                    // 页面加载完，隐藏进度提示
                    if (i == 100){
                        // 二级页面，隐藏底部栏
                        if(x5WebView.canGoBack()){

                        }else {

                        }


                        ptr_frame_feedback.refreshComplete();
                    }

                }

                @Override
                public void onReceivedTitle(WebView webView, String title) {
                    super.onReceivedTitle(webView, title);
                }
            });

            // 根据url加载页面
//            x5WebView.loadUrl(Constants.URL_qq_browser_feedback);
//            x5WebView.loadUrl("http://debugtbs.qq.com");// debug调试页面
//            x5WebView.loadUrl(Constants.URL_BLOG);
            x5WebView.loadUrl(loadUrl);

        }

    }

    @Override
    public void onBackPressed() {
        if (x5WebView != null && x5WebView.canGoBack()){
            x5WebView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearX5WebView();
    }

    private void clearX5WebView() {
        if (x5WebView != null){
            x5WebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            x5WebView.clearHistory();
            x5WebView.clearCache(true);// 清除缓存
            x5WebView.destroy();
            ((ViewGroup)x5WebView.getParent()).removeView(x5WebView);
            x5WebView = null;
        }
    }

}