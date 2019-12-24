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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.mushiny.www.showU.interfaces.MyItemClickInterface;
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.R;
import com.mushiny.www.showU.adapter.JokerCollectionAdapter;
import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.entity.JokerCollectionEntity;
import com.mushiny.www.showU.util.ScreenUtil;
import com.mushiny.www.showU.util.ToastUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @BindView(R.id.banner)Banner banner;// 轮播控件
    @BindView(R.id.tv_banner_animation_setting)TextView tv_banner_animation_setting;
    @BindView(R.id.tv_get_joker)TextView tv_get_joker;
    @BindView(R.id.recycle_view_joker)RecyclerView recycle_view_joker;
    @BindView(R.id.linear_joker_root)LinearLayout linear_joker_root;

    private static final int WHAT_GET_JOKER = 0x10;

    private List<Integer> images = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    private List<Class<? extends ViewPager.PageTransformer>> animations = new ArrayList<>();
    private CharSequence items[];
    private int checkedItem = -1;

    private String BASE_URL_JUHE = "http://v.juhe.cn/";
    private int PAGE = 1;
    private List<JokerCollectionEntity.ResultBean.DataBean> dataBeans = new ArrayList<>();
    private JokerCollectionAdapter adapter = null;

    private PopupWindow pop_window_joker = null;
    private View pop_view_joker = null;
    private int deviceH;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_GET_JOKER:// 获取最新笑话
                    if (dataBeans != null && dataBeans.size() != 0){
                        if (adapter == null){
                            adapter = new JokerCollectionAdapter(getContext(), dataBeans);

                            // 设置recyclerview的item点击事件监听
                            adapter.setOnItemClick(new MyItemClickInterface() {
                                @Override
                                public void OnRecyclerViewItemClick(View itemView, int position) {
                                    showJokerPop(dataBeans.get(position).getContent().replaceAll("&nbsp;", ""));
                                }
                            });
                        }
                        recycle_view_joker.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        recycle_view_joker.setAdapter(adapter);
                        recycle_view_joker.setItemAnimator(new DefaultItemAnimator());
                        recycle_view_joker.setVisibility(View.VISIBLE);
                        tv_get_joker.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    /**
     * 弹框显示 joker 内容
     * @param joker_content
     */
    private void showJokerPop(String joker_content) {

        deviceH = new ScreenUtil(getContext()).getScreenSize(ScreenUtil.HEIGHT);
        int height = (int) (deviceH * 0.618);
        pop_view_joker = getLayoutInflater().inflate(R.layout.pop_view_joker, null);
        TextView tv_pop_view_joker = pop_view_joker.findViewById(R.id.tv_pop_view_joker);
        tv_pop_view_joker.setText(joker_content);
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_joke, container, false);

        ButterKnife.bind(this, view);

        initData();
        setListener();// 设置监听

        return view;
    }

    /**
     * 点击事件
     * @param view
     */
    @OnClick({R.id.relative_banner_animation_setting, R.id.tv_get_joker})
    public void doClick(View view){
        switch (view.getId()){
            case R.id.relative_banner_animation_setting:// 动画设置

                new AlertDialog.Builder(getContext())
                        .setTitle("轮播动画设置")
                        .setIcon(R.mipmap.showu_icon)
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

                getJoker();

                break;
        }
    }

    /**
     * 接口调用获取最新数据
     */
    private void getJoker() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_JUHE)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        NetworkInterface networkInterface = retrofit.create(NetworkInterface.class);

        Map<String, Object> params = new HashMap<>();
        params.put("key", Constants.JUHE_JOKE_APP_KEY);
        params.put("page", PAGE);// 默认1，最大20。超过20 则显示第20页的笑话
//        params.put("pagesize", 2);// 默认是一页返回20条笑话
        Call<JokerCollectionEntity> call = networkInterface.getLatestJoker(params);

        call.enqueue(new Callback<JokerCollectionEntity>() {
            @Override
            public void onResponse(Call<JokerCollectionEntity> call, Response<JokerCollectionEntity> response) {
                try {
                    JokerCollectionEntity jokerCollectionEntity = response.body();
                    int error_code = jokerCollectionEntity.getError_code();
                    if (error_code == 0){
                        if (dataBeans != null && dataBeans.size() == 0){
                            dataBeans = jokerCollectionEntity.getResult().getData();
                            Message message = handler.obtainMessage();
                            message.what = WHAT_GET_JOKER;
                            handler.sendMessage(message);
                        }

                    }else {
                        String reason = jokerCollectionEntity.getReason();
                        tv_get_joker.setText(reason);
                        tv_get_joker.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    tv_get_joker.setText("获取异常：" + e.getMessage());
                    tv_get_joker.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onFailure(Call<JokerCollectionEntity> call, Throwable t) {
                tv_get_joker.setText("获取失败。请尝试重新获取");
                tv_get_joker.setVisibility(View.VISIBLE);
            }
        });

    }

    private void initData() {

        images.add(R.drawable.xiaoyu);
        images.add(R.drawable.zha);
        images.add(R.drawable.miao);
        images.add(R.drawable.daju);
        titles.add("小语");
        titles.add("不是渣男");
        titles.add("FPP 猛男");
        titles.add("大橘不胖");

        // 设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        banner.setImages(images);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setBannerTitles(titles);
        banner.setBannerAnimation(Transformer.Default);

        banner.start();

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

    private void setListener() {


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

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {

            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.ic_launcher_round);// 设置占位图
//            options.override(200,200);// 指定加载图片大小
            options.override(Target.SIZE_ORIGINAL);// 加载图片原始尺寸
//            options.skipMemoryCache(true);// 禁用内存缓存。默认是开启的

            Glide.with(context).load(path).apply(options).into(imageView);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // banner 优化用户体验
        if (!hidden){
            if (banner != null){
                banner.startAutoPlay();
            }
        }else {
            if (banner != null){
                banner.stopAutoPlay();
            }
        }
    }
}
