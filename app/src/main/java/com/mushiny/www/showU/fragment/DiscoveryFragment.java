package com.mushiny.www.showU.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.adapter.NewsAdapter;
import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.entity.NewsEntity;
import com.mushiny.www.showU.interfaces.MyItemClickInterface;
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.ProgressDialogUtil;
import com.mushiny.www.showU.util.ToastUtil;

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
import in.srain.cube.views.ptr.util.PtrLocalDisplay;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 发现 界面
 * 新闻头条
 */
public class DiscoveryFragment extends BaseFragment {

    private static final int WHAT_GET_NEWS = 0x30;

    @BindView(R.id.recycler_view_news)RecyclerView recycler_view_news;
    @BindView(R.id.ptr_frame_news)PtrFrameLayout ptr_frame_news;

    @BindView(R.id.btn_headline)Button btn_headline;
    @BindView(R.id.btn_social)Button btn_social;
    @BindView(R.id.btn_domestic)Button btn_domestic;
    @BindView(R.id.btn_international)Button btn_international;
    @BindView(R.id.btn_entertainment)Button btn_entertainment;
    @BindView(R.id.btn_sports)Button btn_sports;
    @BindView(R.id.btn_military)Button btn_military;
    @BindView(R.id.btn_technology)Button btn_technology;
    @BindView(R.id.btn_finance)Button btn_finance;
    @BindView(R.id.btn_fashion)Button btn_fashion;

    public static final String TYPE_HEADLINE = "top";
    public static final String TYPE_SOCIAL = "shehui";
    public static final String TYPE_DOMESTIC = "guonei";
    public static final String TYPE_INTERNATIONAL = "guoji";
    public static final String TYPE_ENTERTAINMENT = "yule";
    public static final String TYPE_SPORTS = "tiyu";
    public static final String TYPE_MILITARY = "junshi";
    public static final String TYPE_TECHNOLOGY = "keji";
    public static final String TYPE_FINANCE = "caijing";
    public static final String TYPE_FASHION = "shishang";

    private List<NewsEntity.ResultBean.DataBean> dataBeans = new ArrayList<>();// 保存新闻实体
    private NewsAdapter adapter = null;

    private String CURRENT_TYPE = "top";// 默认是top，保存当前用户选择的新闻类型

    private ProgressDialogUtil progressDialogUtil;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_GET_NEWS:// 新闻信息

//                    if (adapter != null && dataBeans.size() != 0){
//                        adapter.notifyDataSetChanged();
//                        ToastUtil.showToast(getContext(), "刷新成功");
//                        return;
//                    }

                    adapter = new NewsAdapter(getContext(), dataBeans);

                    adapter.setOnItemClick(new MyItemClickInterface() {
                        @Override
                        public void OnRecyclerViewItemClick(View itemView, int position) {
                            // 点击进入新闻链接页面

                        }
                    });

                    recycler_view_news.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    recycler_view_news.setAdapter(adapter);
                    recycler_view_news.setItemAnimator(new DefaultItemAnimator());

                    ptr_frame_news.refreshComplete();

                break;
            }
        }
    };

    public DiscoveryFragment() {
        // Required empty public constructor
    }

    public static DiscoveryFragment newInstance(){
        return new DiscoveryFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);

        ButterKnife.bind(this, view);

//        LogUtil.e("TAG","onCreateView," + System.currentTimeMillis() );

        initData();
        setPtrFrame();
        setListener();

        return view;
    }

    /**
     * 设置 下拉刷新和上拉加载
     */
    private void setPtrFrame() {

        // 头部和底部的阻尼系数
        ptr_frame_news.setResistanceHeader(1.7f);
        ptr_frame_news.setResistanceFooter(1.7f);

        ptr_frame_news.setDurationToCloseHeader(2000);
        ptr_frame_news.setDurationToCloseFooter(2000);
        ptr_frame_news.setDurationToBackHeader(500);
        ptr_frame_news.setDurationToBackFooter(500);

        ptr_frame_news.setPinContent(true);
        ptr_frame_news.setPullToRefresh(false);// false 表示手指释放才会刷新，true 表示下拉刷新

        // Materail 风格头部实现
        MaterialHeader header = new MaterialHeader(getContext());
        // 设置下拉刷新头部view的颜色
        header.setColorSchemeColors(new int[]{
//                0xFFC93437,
//                0xFF375BF1,
//                0xFFF7D23E,
                0xFF34A350
        });
        header.setPadding(0, PtrLocalDisplay.dp2px(15),0,0);
        ptr_frame_news.setHeaderView(header);
        ptr_frame_news.addPtrUIHandler(header);

        // 经典底部布局实现
        PtrClassicDefaultFooter footer = new PtrClassicDefaultFooter(getContext());
        footer.setPadding(0, PtrLocalDisplay.dp2px(15),0,0);
        ptr_frame_news.setFooterView(footer);
        ptr_frame_news.addPtrUIHandler(footer);

        ptr_frame_news.setMode(PtrFrameLayout.Mode.NONE);// 支持下拉刷新

        ptr_frame_news.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {// 上拉加载
                frame.refreshComplete();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {// 下拉刷新
//                getNews(CURRENT_TYPE);
//                ToastUtil.showToast(getContext(), "type = " + CURRENT_TYPE);
                frame.refreshComplete();
            }
        });

        // 第一次进入界面 自动刷新
        ptr_frame_news.post(new Runnable() {
            @Override
            public void run() {
                ptr_frame_news.autoRefresh();
                getNews(TYPE_HEADLINE);
            }
        });

    }

    /**
     * 获取新闻头条
     * @param type 新闻类型
     */
    private void getNews(String type) {

        progressDialogUtil.show();
        CURRENT_TYPE = type;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.V_JUHE_CN)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        NetworkInterface networkInterface = retrofit.create(NetworkInterface.class);

        final Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("key", Constants.JUHE_NEWS_HEADLINE_APP_KEY);

        Call<NewsEntity> call = networkInterface.getNews(params);
        call.enqueue(new Callback<NewsEntity>() {
            @Override
            public void onResponse(Call<NewsEntity> call, Response<NewsEntity> response) {
                progressDialogUtil.dismiss();

                try {
                    NewsEntity newsEntity = response.body();
                    int error_code = newsEntity.getError_code();
                    String reason = newsEntity.getReason();

                    if (error_code != 0){
                        ToastUtil.showToast(getContext(), reason);
                        return;
                    }else {
                        // 获取信息成功
                        dataBeans = newsEntity.getResult().getData();
                        Message message = handler.obtainMessage();
                        message.what = WHAT_GET_NEWS;
                        handler.sendMessage(message);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtil.showToast(getContext(), e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<NewsEntity> call, Throwable t) {
                progressDialogUtil.dismiss();
                ToastUtil.showToast(getContext(), t.getMessage());
            }
        });

    }

    // 设置监听
    private void setListener() {


    }

    // 初始化
    private void initData() {

        progressDialogUtil = new ProgressDialogUtil(getContext());

    }

    // 绑定控件单击事件
    @OnClick({R.id.btn_headline, R.id.btn_social, R.id.btn_domestic, R.id.btn_international,
            R.id.btn_entertainment, R.id.btn_sports, R.id.btn_military, R.id.btn_technology,
            R.id.btn_finance, R.id.btn_fashion,})
    public void doClick(View view){
        switch (view.getId()){
            case R.id.btn_headline:// 头条

                setSelectedStyle(btn_headline);
                getNews(TYPE_HEADLINE);

                break;
            case R.id.btn_social:// 社会

                setSelectedStyle(btn_social);
                getNews(TYPE_SOCIAL);

                break;
            case R.id.btn_domestic:// 国内

                setSelectedStyle(btn_domestic);
                getNews(TYPE_DOMESTIC);

                break;
            case R.id.btn_international:// 国际

                setSelectedStyle(btn_international);
                getNews(TYPE_INTERNATIONAL);

                break;
            case R.id.btn_entertainment:// 娱乐

                setSelectedStyle(btn_entertainment);
                getNews(TYPE_ENTERTAINMENT);

                break;
            case R.id.btn_sports:// 体育

                setSelectedStyle(btn_sports);
                getNews(TYPE_SPORTS);

                break;
            case R.id.btn_military:// 军事

                setSelectedStyle(btn_military);
                getNews(TYPE_MILITARY);

                break;
            case R.id.btn_technology:// 科技

                setSelectedStyle(btn_technology);
                getNews(TYPE_TECHNOLOGY);

                break;
            case R.id.btn_finance:// 财经

                setSelectedStyle(btn_finance);
                getNews(TYPE_FINANCE);

                break;
            case R.id.btn_fashion:// 时尚

                setSelectedStyle(btn_fashion);
                getNews(TYPE_FASHION);

                break;
        }
    }

    /**
     * 选项选中 设置右侧选项样式
     * @param selected_button
     */
    private void setSelectedStyle(Button selected_button) {

        ptr_frame_news.autoRefresh();// 显示框架的下拉刷新样式，优化用户体验

        defaultButtonStyle(btn_headline);
        defaultButtonStyle(btn_domestic);
        defaultButtonStyle(btn_international);
        defaultButtonStyle(btn_social);
        defaultButtonStyle(btn_entertainment);
        defaultButtonStyle(btn_sports);
        defaultButtonStyle(btn_military);
        defaultButtonStyle(btn_technology);
        defaultButtonStyle(btn_finance);
        defaultButtonStyle(btn_fashion);

        selected_button.setBackgroundColor(getResources().getColor(R.color.color_white));
        selected_button.setTextColor(getResources().getColor(R.color.color_news_tab));

    }

    // 默认样式
    private void defaultButtonStyle(Button default_button) {

        default_button.setBackgroundColor(getResources().getColor(R.color.color_news_tab));
        default_button.setTextColor(getResources().getColor(R.color.color_white));

    }

}
