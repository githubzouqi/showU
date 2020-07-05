package com.mushiny.www.showU.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
//import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonObject;
import com.mushiny.www.showU.activity.MainActivity;
import com.mushiny.www.showU.interfaces.MyItemClickInterface;
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.R;
import com.mushiny.www.showU.adapter.JokerCollectionAdapter;
import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.entity.JokerCollectionEntity;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.ProgressDialogUtil;
import com.mushiny.www.showU.util.PtrUtil;
import com.mushiny.www.showU.util.Retrofit2Util;
import com.mushiny.www.showU.util.ScaleUtil;
import com.mushiny.www.showU.util.ScreenUtil;
import com.mushiny.www.showU.util.ToastUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicDefaultFooter;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.header.MaterialProgressDrawable;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class JokeFragment extends BaseFragment {

    private boolean isPause = false;// 默认轮播开启

    @BindView(R.id.banner)Banner banner;// 轮播控件
    @BindView(R.id.tv_banner_animation_setting)TextView tv_banner_animation_setting;
    @BindView(R.id.tv_get_joker)TextView tv_get_joker;
    @BindView(R.id.recycle_view_joker)RecyclerView recycle_view_joker;
    @BindView(R.id.linear_joker_root)LinearLayout linear_joker_root;
    @BindView(R.id.ptr_frame_joker)PtrFrameLayout ptr_frame_joker;// 支持上拉加载，下拉刷新
    @BindView(R.id.tv_support_refresh_hint)TextView tv_support_refresh_hint;

    private static final int WHAT_GET_JOKER = 0x10;
    private static final int WHAT_AUTO_RELOAD = 0x11;

    private List<String> images = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    private List<Class<? extends ViewPager.PageTransformer>> animations = new ArrayList<>();
    private CharSequence items[];
    private int checkedItem = -1;

    private List<JokerCollectionEntity.DataBean> dataBeans = new ArrayList<>();
    private JokerCollectionAdapter adapter = null;

    private PopupWindow pop_window_joker = null;
    private View pop_view_joker = null;
    private int deviceH;

    private int deviceW = 400;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_GET_JOKER:// 获取最新笑话
                    if (dataBeans != null && dataBeans.size() != 0){
                        tv_get_joker.setVisibility(View.GONE);
                        tv_support_refresh_hint.setVisibility(View.VISIBLE);

                        if (adapter != null){
                            adapter.notifyDataSetChanged();
                            ToastUtil.showToast(getContext(), "刷新成功");
                            return;
                        }

                        if (adapter == null){
                            adapter = new JokerCollectionAdapter(getContext(), dataBeans);

                            // 设置recyclerview的item点击事件监听
                            /*
                            adapter.setOnItemClick(new MyItemClickInterface() {
                                @Override
                                public void OnRecyclerViewItemClick(View itemView, int position) {
                                    position_current = position;
                                    showJokerPop(dataBeans.get(position).getContent()
                                            .replaceAll("&nbsp;", ""));
                                }
                            });
                            */
                        }


                        recycle_view_joker.setLayoutManager(new LinearLayoutManager(getContext(),
                                LinearLayoutManager.VERTICAL, false));
                        recycle_view_joker.setAdapter(adapter);
                        recycle_view_joker.setItemAnimator(new DefaultItemAnimator());
                        recycle_view_joker.setVisibility(View.VISIBLE);
                        ToastUtil.showToast(getContext(), "获取成功");
                        ptr_frame_joker.setPtrHandler(new PtrDefaultHandler2() {
                            @Override
                            public void onLoadMoreBegin(PtrFrameLayout frame) {

                            }

                            @Override
                            public void onRefreshBegin(PtrFrameLayout frame) {
                                // 下拉刷新
                                getJoker();
                            }
                        });
                    }
                    break;

                case WHAT_AUTO_RELOAD:// 自动加载数据
                    getJoker();
                    break;
            }
        }
    };

    /**
     * 弹框显示 joker 内容
     * @param joker_content
     */
    private void showJokerPop(String joker_content) {

        int height = (int) (deviceH * 0.618);
        pop_view_joker = getLayoutInflater().inflate(R.layout.pop_view_joker, null);
        final TextView tv_pop_view_joker = pop_view_joker.findViewById(R.id.tv_pop_view_joker);
        tv_pop_view_joker.setText(joker_content);

        final TextView tv_prev = pop_view_joker.findViewById(R.id.tv_prev);
        final TextView tv_next = pop_view_joker.findViewById(R.id.tv_next);

        // 前一个 单击监听
        tv_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // 下一个 单击监听
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        pop_window_joker = new PopupWindow(pop_view_joker, ViewGroup.LayoutParams.MATCH_PARENT, height);
        pop_window_joker.setAnimationStyle(R.style.pop_anim);
        pop_window_joker.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        pop_window_joker.setFocusable(true);
        pop_window_joker.setOutsideTouchable(true);
        pop_window_joker.update();
        pop_window_joker.showAtLocation(linear_joker_root, Gravity.BOTTOM, 0, 0);

        bgAlpha(0.618f);
        pop_window_joker.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bgAlpha(1.0f);
            }
        });

    }

    public static JokeFragment newInstance(){
        return new JokeFragment();
    }

    public JokeFragment() {
        // Required empty public constructor
    }

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_joke, container, false);
        }
        ButterKnife.bind(this, view);

        initData();
        setListener();// 设置监听

        return view;
    }

    /**
     * 点击事件
     * @param view
     */
    @OnClick({R.id.relative_banner_animation_setting, R.id.tv_get_joker, R.id.iv_another,
    R.id.iv_pause, R.id.iv_start})
    public void doClick(View view){
        switch (view.getId()){
            case R.id.relative_banner_animation_setting:// 动画设置

                new AlertDialog.Builder(getContext())
                        .setTitle("轮播动画设置")
                        .setIcon(R.mipmap.app_icon)
                        .setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tv_banner_animation_setting.setText("当前动画样式 - " + items[which]);
                                checkedItem = which;

                                // 设置动画的时候，先停止轮播，然后再开启
                                banner.stopAutoPlay();
                                banner.setBannerAnimation(animations.get(which));
                                banner.start();

                                dialog.dismiss();
                            }
                        }).create().show();

                break;
            case R.id.tv_get_joker:// 获取最新笑话

                PtrUtil.newInstance(getContext()).autoRefresh(ptr_frame_joker);
                getJoker();

                break;
            case R.id.iv_another:// 刷新换一组
                if (banner != null){
                    banner.stopAutoPlay();
                    banner.releaseBanner();
                    getBannerPic();
                }
                break;
            case R.id.iv_pause:// 轮播暂停 banner
                if (banner != null){
                    if (isPause){
                        ToastUtil.showToast(getContext(), "轮播已暂停");
                        return;
                    }
                    isPause = true;
                    banner.stopAutoPlay();
                    ToastUtil.showToast(getContext(), "轮播暂停成功");
                }
                break;
            case R.id.iv_start:// 轮播启动 banner

                if (banner != null){
                    if (!isPause){
                        ToastUtil.showToast(getContext(), "轮播已开启");
                        return;
                    }
                    isPause = false;
                    banner.startAutoPlay();
                    ToastUtil.showToast(getContext(), "轮播开启成功");
                }
                break;
        }
    }


    /**
     * 获取随机笑话
     */
    private void getJoker() {

        NetworkInterface networkInterface = Retrofit2Util
                .createWithROLLHeader(NetworkInterface.class);
        Call<JokerCollectionEntity> call = networkInterface.getRandomJokers();
        call.enqueue(new Callback<JokerCollectionEntity>() {
            @Override
            public void onResponse(Call<JokerCollectionEntity> call,
                                   Response<JokerCollectionEntity> response) {

                JokerCollectionEntity entity = response.body();
                List<JokerCollectionEntity.DataBean> data = entity.getData();
                if (dataBeans.size() != 0){
                    dataBeans.clear();
                }
                for (int i = 0;i < data.size();i++){
                    dataBeans.add(data.get(i));
                }
                handler.sendEmptyMessage(WHAT_GET_JOKER);
                ptr_frame_joker.refreshComplete();
            }

            @Override
            public void onFailure(Call<JokerCollectionEntity> call, Throwable t) {
                tv_get_joker.setVisibility(View.VISIBLE);
                tv_support_refresh_hint.setVisibility(View.GONE);
                tv_get_joker.setText(getResources().getString(R.string.str_get_joker));
                ptr_frame_joker.refreshComplete();
                ToastUtil.showToast(getContext(), "获取失败：" + t.getMessage());
            }
        });


    }

    private void initData() {
        baseTitle = getResources().getString(R.string.str_joker_title);// 标题内容设置

        // 设备宽高
        deviceW = getResources().getDisplayMetrics().widthPixels;
        deviceH = new ScreenUtil().getScreenSize(ScreenUtil.HEIGHT, getContext());
        // 设置banner大小
        LinearLayout.LayoutParams bp = (LinearLayout.LayoutParams) banner.getLayoutParams();
        bp.width = deviceW - ScaleUtil.dip2px(getContext(), 20);
        bp.height = (int) ((deviceW - ScaleUtil.dip2px(getContext(), 20)) * (0.618f));
        banner.setLayoutParams(bp);

        getBannerPic();

        items = new CharSequence[]{"Accordion", "BackgroundToForeground","CubeIn","CubeOut"
        ,"Default","DepthPage","FlipHorizontal","FlipVertical","ForegroundToBackground",
        "RotateDown","RotateUp","ScaleInOut","Stack","Tablet","ZoomIn","ZoomOut","ZoomOutSlide"};

        animations.add(Transformer.Accordion);
        animations.add(Transformer.BackgroundToForeground);
        animations.add(Transformer.CubeIn);
        animations.add(Transformer.CubeOut);
        animations.add(Transformer.Default);
        animations.add(Transformer.DepthPage);
        animations.add(Transformer.FlipHorizontal);
        animations.add(Transformer.FlipVertical);
        animations.add(Transformer.ForegroundToBackground);
        animations.add(Transformer.RotateDown);
        animations.add(Transformer.RotateUp);
        animations.add(Transformer.ScaleInOut);
        animations.add(Transformer.Stack);
        animations.add(Transformer.Tablet);
        animations.add(Transformer.ZoomIn);
        animations.add(Transformer.ZoomOut);
        animations.add(Transformer.ZoomOutSlide);

    }

    /**
     * 获取轮播图片
     */
    private void getBannerPic() {
        images.clear();
        titles.clear();

        NetworkInterface anInterface = Retrofit2Util.createWithROLLHeader(NetworkInterface.class);
        Call<ResponseBody> call = anInterface.getRandomGirl();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String s = new String(response.body().bytes());
                    JSONObject obj = new JSONObject(s);
                    JSONArray array = obj.getJSONArray("data");
                    int length = array.length();
                    for (int i = 0;i < length;i++){
                        String imageUrl = array.getJSONObject(i).optString("imageUrl");
                        images.add(imageUrl);
                        titles.add(getResources().getString(R.string.app_name));
                    }
                    setBanner();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                images.add("http://pics1.baidu.com/feed/" +
                        "bf096b63f6246b60a6338e6292dd064a510fa296.jpeg?" +
                        "token=fc9beda1f65bbe6d13aff6fc3b338a19&s=13528C6C81945C6E1D0E52500300D0DB");
                titles.add("UHello");
                setBanner();
                ToastUtil.showToast(getContext(), "图片获取失败 :" + t.getMessage());
            }
        });


    }

    /**
     * Banner 图片设置
     */
    private void setBanner(){
        // 设置图片加载器
        banner.setVisibility(View.VISIBLE);
        banner.setImageLoader(new GlideImageLoader());
        banner.setImages(images);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setBannerTitles(titles);
        banner.setBannerAnimation(Transformer.CubeOut);
        banner.start();
        isPause = false;
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                ToastUtil.showToast(getContext(), titles.get(position));
            }
        });
    }

    // 设置监听
    private void setListener() {

        setPtrFrame();

        handler.sendEmptyMessageDelayed(WHAT_AUTO_RELOAD, 500);

        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                isPause = false;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    /**
     * 设置 下拉刷新和上拉加载
     */
    private void setPtrFrame() {

        PtrUtil.newInstance(getContext()).set_1_BaseSetting(ptr_frame_joker);
        PtrUtil.newInstance(getContext()).set_2_MaterialHeader(ptr_frame_joker,
                PtrUtil.DEFAULT_COLOR);
//        PtrUtil.newInstance(getContext()).set_3_Footer(ptr_frame_joker);

        ptr_frame_joker.setMode(PtrFrameLayout.Mode.REFRESH);
        PtrUtil.newInstance(getContext()).autoRefresh(ptr_frame_joker);
    }

    /**
     * 窗口背景透明度设置
     * @param f
     */
    private  void bgAlpha(float f){
        WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
        layoutParams.alpha = f;
        getActivity().getWindow().setAttributes(layoutParams);
    }

    /**
     * 自定义 GlideImageLoader 继承 ImageLoader
     */
    public class GlideImageLoader extends ImageLoader{

        private static final long serialVersionUID = 174520926963593401L;

        @Override
        public void displayImage(Context context, final Object path, ImageView imageView) {

            RequestOptions options = new RequestOptions();
//            options.placeholder(R.mipmap.app_icon);// 设置占位图
            options.override(deviceW,deviceW);// 指定加载图片大小
            options.override(Target.SIZE_ORIGINAL);// 加载图片原始尺寸
//            options.skipMemoryCache(true);// 禁用内存缓存。默认是开启的
            options.centerCrop();
            Glide.with(context).load(path).apply(options).into(imageView);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // banner 优化用户体验
        if (!hidden){
            // 页面可见
            if (banner != null){
                if(!isPause){
                    banner.startAutoPlay();
                }
            }
        }else {
            // 页面不可见
            if (banner != null){
                banner.stopAutoPlay();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
