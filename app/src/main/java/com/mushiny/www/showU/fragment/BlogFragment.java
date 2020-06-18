package com.mushiny.www.showU.fragment;


import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.PtrUtil;
import com.mushiny.www.showU.util.ToastUtil;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.xml.sax.ext.DefaultHandler2;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlogFragment extends BaseFragment {

    private WebView x5WebView;
    private WebSettings x5WebSettings;
    private boolean hidden = false;// 主页该值默认为false，因为是打开app后显示的第一个页面
    private int x5WebViewScrollY = 0;

    @BindView(R.id.linear_webview_parent)LinearLayout linear_webview_parent;// x5 webView 父布局
    @BindView(R.id.ptr_frame_blog)PtrFrameLayout ptr_frame_blog;

    private int width = 300;
    private int height = 300;

    public static BlogFragment newInstance(){
        return new BlogFragment();
    }

    public BlogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blog, container, false);
        ButterKnife.bind(this, view);

        initData();
        setPtrFrame();

        loadWebPage();

        return view;
    }

    /**
     * 刷新加载设置
     */
    private void setPtrFrame() {

        PtrUtil.newInstance(getContext()).set_1_BaseSetting(ptr_frame_blog);
        PtrUtil.newInstance(getContext()).set_2_MaterialHeader(ptr_frame_blog, PtrUtil.DEFAULT_COLOR);
        PtrUtil.newInstance(getContext()).set_3_Footer(ptr_frame_blog);
        ptr_frame_blog.setMode(PtrFrameLayout.Mode.REFRESH);
        PtrUtil.newInstance(getContext()).autoRefresh(ptr_frame_blog);

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
            linear_webview_parent.addView(x5WebView);

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

                    LogUtil.e("TAG", "shouldOverrideUrlLoading url is:" + url);
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
//                    webView.loadUrl(url);
                    return false;
//                    return super.shouldOverrideUrlLoading(webView, s);
                }

                @Override
                public void onLoadResource(WebView webView, String s) {
                    super.onLoadResource(webView, s);
//                    int h = linear_webview_parent.getMeasuredHeight();
//                    if (h == height){
//                        ViewGroup.LayoutParams params_load = x5WebView.getLayoutParams();
//                        params_load.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                        x5WebView.setLayoutParams(params_load);
//                    }
                }

                @Override
                public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest,
                                            WebResourceError webResourceError) {
                    super.onReceivedError(webView, webResourceRequest, webResourceError);
                    // 当没网络 加载失败 设置webView固定高度 优化体验
//                    ViewGroup.LayoutParams params_error = x5WebView.getLayoutParams();
//                    params.height = height;
//                    x5WebView.setLayoutParams(params_error);
                    ptr_frame_blog.refreshComplete();
                }
            });

            // 设置 WebChromeClient
            x5WebView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(final WebView webView, int i) {
                    super.onProgressChanged(webView, i);
                    // 页面加载完，隐藏进度提示
                    if (i == 100){
                        ptr_frame_blog.refreshComplete();
                        ptr_frame_blog.setPtrHandler(new PtrHandler() {
                            @Override
                            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content,
                                                             View header) {
                                if (x5WebViewScrollY == 0){
                                    return true;
                                }
                                return false;
                            }

                            @Override
                            public void onRefreshBegin(PtrFrameLayout frame) {
                                x5WebView.reload();
                            }
                        });

                        // webView 滑动判断
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            x5WebView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                                @Override
                                public void onScrollChange(View v, int scrollX, int scrollY,
                                                           int oldScrollX, int oldScrollY) {
                                    x5WebViewScrollY = scrollY;
                                }
                            });
                        }

                    }

                }

            });

            // 根据url加载页面
//            x5WebView.loadUrl(Constants.URL_qq_browser_feedback);
//            x5WebView.loadUrl("http://debugtbs.qq.com");// debug调试页面
            x5WebView.loadUrl(Constants.URL_BLOG);
        }

    }

    private void initData() {

        // x5的webview创建
        if (x5WebView == null){
            x5WebView = new WebView(getContext().getApplicationContext());
        }

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        // 获取屏幕宽高 px
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
//        ToastUtil.showToast(getContext(),"主页 hidden = " + hidden);
        if (hidden){

        }else {

        }
    }

    /**
     * 网页是否可以后退
     * @return
     */
    public boolean canBack(){
        if (hidden){
            return false;
        }
        if (x5WebView.canGoBack()){
//            x5WebView.goBack();
            return true;
        }
        return false;
    }

    public void goBack(){
        if (x5WebView != null){
            x5WebView.goBack();
        }
    }

    /**
     * 控件单击事件监听
     * @param
     */
//    @OnClick({R.id.btn_imxiaoqi})
//    public void doClick(View view){
//        switch (view.getId()){
//            case R.id.btn_imxiaoqi:
//
//                if (BlogFragment.this.isAdded()){
//                    LogUtil.e("TAG", " BlogFragment is added!");
//                }else {
//                    LogUtil.e("TAG", " BlogFragment is not added!");
//                }
//
//                break;
//        }
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (x5WebView != null){
            clearX5WebView();
        }
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
}
