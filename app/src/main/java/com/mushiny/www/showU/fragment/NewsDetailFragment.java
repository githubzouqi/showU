package com.mushiny.www.showU.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mushiny.www.showU.R;
import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.PtrUtil;
import com.mushiny.www.showU.util.Retrofit2Util;
import com.mushiny.www.showU.util.ScaleUtil;
import com.mushiny.www.showU.util.ScreenUtil;
import com.mushiny.www.showU.util.ToastUtil;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.commonsdk.debug.E;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrFrameLayout;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

/**
 * 新闻详情页面
 */
public class NewsDetailFragment extends BaseFragment {


    @BindView(R.id.linear_new_detail)LinearLayout linear_new_detail;
    @BindView(R.id.tv_new_detail) TextView tv_new_detail;
    @BindView(R.id.tv_detail_title) TextView tv_detail_title;
    @BindView(R.id.tv_detail_source_time) TextView tv_detail_source_time;
    @BindView(R.id.iv_detail) ImageView iv_detail;
    @BindView(R.id.ptr_detail) PtrFrameLayout ptr_detail;

    private final static int WHAT_LOAD_HTML_TEXT = 0x80;

    private static final String TITLE = "TITLE";
    private static final String SOURCE_TIME = "SOURCE_TIME";
    private static final String IMGURL = "IMGURL";
    public final static String NEWS_ID = "NEWS_ID";

    public final static String TAG = NewsDetailFragment.class.getSimpleName();

    private String title = "";
    private String  source_time= "";
    private String imgUrl = "";
    private String newsId = "";
    private List<String[]> images= new ArrayList<>();// 图片集合

    private RequestOptions options;
    private int w = 300;
    private int h = 300;

    public NewsDetailFragment() {
        // Required empty public constructor
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_LOAD_HTML_TEXT:

                    CharSequence content = (CharSequence) msg.obj;
                    if (!TextUtils.isEmpty(content)){
                        tv_new_detail.setText(content);
                        ptr_detail.refreshComplete();
//                        ptr_detail.setMode(PtrFrameLayout.Mode.NONE);
                    }

                    break;
            }
        }
    };


    /**
     * 单例携带数据
     * @param newsId 具体新闻id
     * @param title 新闻标题
     * @param source_time 来源和时间
     * @param imgUrl 图片url 可能为空
     * @return
     */
    public static NewsDetailFragment newInstance(String newsId, String title, String source_time,
                                                 String imgUrl){
        NewsDetailFragment detailFragment = new NewsDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(NewsDetailFragment.NEWS_ID, newsId);
        bundle.putString(NewsDetailFragment.TITLE, title);
        bundle.putString(NewsDetailFragment.SOURCE_TIME, source_time);
        bundle.putString(NewsDetailFragment.IMGURL, imgUrl);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_news_detail, container, false);
        }
        ButterKnife.bind(this, view);

        initData();
        setPtrFrame();

        return view;
    }

    /**
     * 刷新加载设置
     */
    private void setPtrFrame() {

        PtrUtil.newInstance(getContext()).set_1_BaseSetting(ptr_detail);
        PtrUtil.newInstance(getContext()).set_2_MaterialHeader(ptr_detail, PtrUtil.DEFAULT_COLOR);
        PtrUtil.newInstance(getContext()).set_3_Footer(ptr_detail);
        ptr_detail.setMode(PtrFrameLayout.Mode.REFRESH);
        PtrUtil.newInstance(getContext()).autoRefresh(ptr_detail);

    }

    /**
     * 数据初始化
     */
    private void initData() {

        Bundle bundle = getArguments();
        newsId = bundle.getString(NewsDetailFragment.NEWS_ID, "");

        title = bundle.getString(NewsDetailFragment.TITLE, "");
        source_time = bundle.getString(NewsDetailFragment.SOURCE_TIME, "");
        imgUrl = bundle.getString(NewsDetailFragment.IMGURL, "");

        w = new ScreenUtil().getScreenSize(ScreenUtil.WIDTH, getContext())
                - ScaleUtil.dip2px(getContext(), 20);
        h = (int) (w * (0.618f));

        if (options == null){
            options = new RequestOptions();
            options.placeholder(R.mipmap.app_icon);// 设置占位图
            options.error(R.mipmap.load_error);// 加载失败占位图
            options.override(w,h);// 指定加载图片大小
            options.fitCenter();
            options.diskCacheStrategy(DiskCacheStrategy.ALL);// 缓存所有：原型、转换后的
//        options.override(Target.SIZE_ORIGINAL);// 加载图片原始尺寸
//            options.skipMemoryCache(true);// 禁用内存缓存。默认是开启的
        }

        baseTitle = title;
        setTopTitle();

    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * 加载新闻详情数据
     */
    private void loadData() {

        NetworkInterface anInterface = Retrofit2Util.createWithROLLHeader(NetworkInterface.class);
        final Map<String, Object> param = new HashMap<>();
        param.put("newsId", newsId);
        LogUtil.e("TAG", "newsId = " + newsId);
        Call<ResponseBody> call = anInterface.getNewsDetail(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    JSONObject obj = new JSONObject(new String(response.body().bytes()));
                    if (obj.optInt("code") == 0){
                        // 请求超时，请稍后再试
                        tv_detail_title.setText(title);
                        tv_detail_source_time.setText(source_time);

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iv_detail
                                .getLayoutParams();
                        params.width = w;
                        params.height = h;
                        iv_detail.setLayoutParams(params);
                        Glide.with(NewsDetailFragment.this).load(imgUrl).apply(options)
                                .into(iv_detail);
                        iv_detail.setVisibility(View.VISIBLE);

                        ptr_detail.refreshComplete();
//                        ToastUtil.showToast(getContext(), obj.optString("msg"));
                        new AlertDialog.Builder(getContext()).setMessage(obj.optString("msg"))
                        .create().show();
                        return;
                    }
                    JSONObject objData = obj.getJSONObject("data");
                    String content = objData.optString("content");

                    String title = objData.optString("title");
                    String source_time = objData.optString("source") + "  " +
                            objData.optString("ptime");
                    tv_detail_title.setText(title);
                    tv_detail_source_time.setText(source_time);

                    JSONArray arrayImages = objData.getJSONArray("images");
                    // 图片集合
                    if (arrayImages != null && arrayImages.length() > 0){
                        for (int i = 0;i < arrayImages.length();i++){
                            String position = arrayImages.getJSONObject(i)
                                    .optString("position");
                            String imgSrc = arrayImages.getJSONObject(i)
                                    .optString("imgSrc");
                            String size = arrayImages.getJSONObject(i)
                                    .optString("size");
                            images.add(new String[]{position, imgSrc, size});
                        }

                        content = replaceContent(content);
                    }

                    setHtmlContent(content);


                }catch (Exception e){
                    ptr_detail.refreshComplete();
                    e.printStackTrace();
                    ToastUtil.showToast(getContext(),"详情数据解析异常：" + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ptr_detail.refreshComplete();
                ToastUtil.showToast(getContext(),"详情获取异常：" + t.getMessage());
            }
        });

    }

    /**
     * 往 html 富文本中替换 <!--IMG#0--> 为 <img src=""/> 图片
     * @param content
     * @return
     */
    private String replaceContent(String content) {
        for (int i = 0;i < images.size();i++){
            String position = images.get(i)[0];// "<!--IMG#0-->"
            // "http://dingyue.ws.126.net/2020/0602/eb5636c8j00qba7vb001vd000s600l4p.jpg"
            String imgSrc = images.get(i)[1];
            String size = images.get(i)[2];// "1014*760"

            String htmlImg = "<img src=\"" + imgSrc + "\"/>";
//            LogUtil.e("TAG", "htmlImg = " + htmlImg);
            content = content.replace(position, htmlImg);
        }

        return content;
    }

    /**
     * 设置 html 富文本内容
     * @param content
     */
    private void setHtmlContent(final String content) {

        new Thread(){
            @Override
            public void run() {
                final Html.ImageGetter getter = new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        Drawable drawable = null;
                        URL url = null;
                        try {
                            url = new URL(source);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream inputStream = connection.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            drawable = new BitmapDrawable(getResources(), bitmap);
                            LogUtil.e("TAG", "Html.ImageGetter url = " + url);
                            inputStream.close();
                            if (drawable != null) {
                                int w = 400;
                                int h = 400;
                                for (int i = 0;i < images.size();i++){
                                    String imgSrc = images.get(i)[1];
                                    String size = images.get(i)[2];
                                    String[] arr = size.split(Pattern.quote("*"));
                                    LogUtil.e("TAG", "imgSrc " + i + " = " + imgSrc);
                                    if (url.toString().equals(imgSrc)){
                                        w = Integer.parseInt(arr[0]);
                                        h = Integer.parseInt(arr[1]);
                                    }
                                }
                                LogUtil.e("TAG", "w,h = " + w + "," + h);
                                drawable.setBounds(0, 0, w, h);
                            } else if (drawable == null) {
                                return null;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            return null;
                        }
                        return drawable;
                    }
                };

                CharSequence charSequence = Html.fromHtml(content, getter, null);
                Message message = handler.obtainMessage();
                message.what = WHAT_LOAD_HTML_TEXT;
                message.obj = charSequence;
                handler.sendMessage(message);

            }
        }.start();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }
}
