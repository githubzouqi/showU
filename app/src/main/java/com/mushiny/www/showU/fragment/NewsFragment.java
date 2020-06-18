package com.mushiny.www.showU.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.mushiny.www.showU.util.PtrUtil;
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
public class NewsFragment extends BaseFragment {

    private static final int WHAT_GET_NEWS = 0x30;
    private static final String NEWS_TYPE = "news_type";// 新闻类型 key

    @BindView(R.id.recycler_view_news)RecyclerView recycler_view_news;
    @BindView(R.id.ptr_frame_news)PtrFrameLayout ptr_frame_news;

    private List<NewsEntity.ResultBean.DataBean> dataBeans = new ArrayList<>();// 保存新闻实体
    private NewsAdapter adapter = null;

    private String news_type = "top";

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
                            NewsDetailFragment newsDetailFragment = NewsDetailFragment.newInstance();

                            // 跳转携带数据
                            Bundle bundle = new Bundle();
                            bundle.putString(NewsDetailFragment.KEY_URL, dataBeans.get(position).
                                    getUrl());
                            newsDetailFragment.setArguments(bundle);

                            showFragment(getActivity(), NewsFragment.this,
                                    newsDetailFragment, NewsDetailFragment.TAG);
                        }
                    });

                    recycler_view_news.setLayoutManager(new LinearLayoutManager(getContext(),
                            LinearLayoutManager.VERTICAL, false));
                    recycler_view_news.setAdapter(adapter);
                    recycler_view_news.setItemAnimator(new DefaultItemAnimator());

                    // 自动刷新取消显示
                    ptr_frame_news.refreshComplete();
                    ptr_frame_news.setPtrHandler(new PtrDefaultHandler2() {
                        @Override
                        public void onLoadMoreBegin(PtrFrameLayout frame) {// 上拉加载
                        }

                        @Override
                        public void onRefreshBegin(PtrFrameLayout frame) {// 下拉刷新
                            getNews(news_type);
                        }
                    });

                break;
            }
        }
    };

    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance(String news_type){
        NewsFragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(NEWS_TYPE, news_type);
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

    private boolean isViewCreated = false;
    private boolean isVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        ButterKnife.bind(this, view);

        initData();
        setPtrFrame();
        setListener();

        isViewCreated = true;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.e("TAG", "onResume = " + news_type);
        lazyLoad();
    }

    /**
     * 恢复标记 防止重复加载
     */
    private void reset(){
        isViewCreated = false;
        isVisible = false;
    }

    /**
     * 对用户可见且view创建完成后才加载数据
     */
    private void lazyLoad() {
        if (isVisible && isViewCreated){
            loadData();
            reset();
        }
    }

    /**
     *
     */
    private void loadData() {
        getNews(news_type);
    }

    /**
     * 设置 下拉刷新和上拉加载
     */
    private void setPtrFrame() {

        PtrUtil.newInstance(getContext()).set_1_BaseSetting(ptr_frame_news);
        PtrUtil.newInstance(getContext()).set_2_MaterialHeader(ptr_frame_news, PtrUtil
                .DEFAULT_COLOR);
        PtrUtil.newInstance(getContext()).set_3_Footer(ptr_frame_news);
        ptr_frame_news.setMode(PtrFrameLayout.Mode.REFRESH);// 设置模式
        PtrUtil.newInstance(getContext()).autoRefresh(ptr_frame_news);

    }

    /**
     * 获取新闻头条
     * @param type 新闻类型
     */
    private void getNews(String type) {

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

                try {
                    NewsEntity newsEntity = response.body();
                    int error_code = newsEntity.getError_code();
                    String reason = newsEntity.getReason();

                    if (error_code != 0){
                        // 异常情况，如超出每日限制数
                        ptr_frame_news.refreshComplete();
                        ToastUtil.showToast(getContext(), reason);
                        return;
                    }else {
                        // 获取信息成功
                        dataBeans.clear();
                        List<NewsEntity.ResultBean.DataBean> beans = newsEntity.getResult().
                                getData();
                        for (int i = 0;i < beans.size();i++){
                            dataBeans.add(beans.get(i));
                        }
                        Message message = handler.obtainMessage();
                        message.what = WHAT_GET_NEWS;
                        handler.sendMessage(message);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtil.showToast(getContext(), "异常 - " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<NewsEntity> call, Throwable t) {
                ToastUtil.showToast(getContext(), t.getMessage());
            }
        });

    }

    // 设置监听
    private void setListener() {


    }

    // 初始化
    private void initData() {

        if (getArguments() != null) {
            news_type = getArguments().getString(NEWS_TYPE, "top");
        }

    }

    /**
     * 是否对用户可见 优先于 onCreateView 生命周期方法
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.e("TAG", "setUserVisibleHint = " + news_type);
        if (isVisibleToUser){
            isVisible = true;
            lazyLoad();
        }else {
            isVisible = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
        dataBeans.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 预防handler的内存泄漏
        if (handler != null){
            handler.removeCallbacks(null);
            handler = null;
        }
    }
}
