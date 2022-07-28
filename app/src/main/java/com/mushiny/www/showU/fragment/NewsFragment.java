package com.mushiny.www.showU.fragment;


import android.annotation.SuppressLint;
import android.icu.util.ValueIterator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.activity.MainActivity;
import com.mushiny.www.showU.adapter.NewsAdapter;
import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.entity.NewsEntity;
import com.mushiny.www.showU.interfaces.MyItemClickInterface;
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.ProgressDialogUtil;
import com.mushiny.www.showU.util.PtrUtil;
import com.mushiny.www.showU.util.Retrofit2Util;
import com.mushiny.www.showU.util.ToastUtil;

import java.lang.ref.WeakReference;
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
    private static final String TYPE_ID = "TYPE_ID";// 新闻类型 id
    private static final String TYPE_NAME = "TYPE_NAME";

    @BindView(R.id.recycler_view_news)RecyclerView recycler_view_news;
    @BindView(R.id.ptr_frame_news)PtrFrameLayout ptr_frame_news;
    @BindView(R.id.iv_news_timeout)ImageView iv_news_timeout;

    private List<NewsEntity.DataBean> dataBeans = new ArrayList<>();// 保存新闻实体
    private NewsAdapter adapter = null;

    private String typeId = ""; //
    private String typeName = "OpenMe";
    private int page = 1;// 从1开始

    private String TAG = "NewsFragment";
    private Handler handler = new MyHandler(this);

    static class MyHandler extends Handler{
        private WeakReference<NewsFragment> weakReference;

        public MyHandler(NewsFragment fragment){
            this.weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            NewsFragment fragment = weakReference.get();
            if (fragment != null){
                switch (msg.what){
                    case WHAT_GET_NEWS:
                        fragment.display();
                        break;
                }
            }
        }
    }


    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * 单例携带数据写法
     * @param type_Id 对应id
     * @param typeName tab名称
     * @return
     */
    public static NewsFragment newInstance(String type_Id, String typeName){
        NewsFragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE_ID, type_Id);
        bundle.putString(TYPE_NAME, typeName);
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

    private boolean isViewCreated = false;
    private boolean isVisible = false;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_news, container, false);
        }

        ButterKnife.bind(this, view);

        initData();
        setPtrFrame();
        setListener();

        isViewCreated = true;

        return view;
    }

    // 初始化
    private void initData() {
        if (getArguments() != null) {
            typeId = getArguments().getString(TYPE_ID, "");
            typeName = getArguments().getString(TYPE_NAME, "OpenMe");
        }

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

    }

    // init listener
    private void setListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        lazyLoad();
    }

    @Override
    public void onTitleSet(String mBaseTitle) {
       if (isVisible){
           super.onTitleSet(typeName);
       }
    }

    /**
     * 对用户可见且view创建完成后才加载数据
     */
    private void lazyLoad() {
        if (isVisible && isViewCreated){
            if (TextUtils.isEmpty(typeId)){
                ToastUtil.showToast(getActivity(), "typeId 为空");
                return;
            }

            LogUtil.e(TAG, ":> lazyLoad, typeId: " + typeId);

            ptr_frame_news.setPtrHandler(new PtrDefaultHandler2() {
                @Override
                public void onLoadMoreBegin(PtrFrameLayout frame) {// 上拉加载
                    page += 1;
                    getNews(typeId);
                    LogUtil.e(TAG, ":> onLoadMoreBegin - " + typeId);
                }

                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {// 下拉刷新
                    page = 1;
                    getNews(typeId);
                    LogUtil.e(TAG, ":> onRefreshBegin - " + typeId);
                }
            });

            // 调用该方法会触发 onRefreshBegin 回调
            PtrUtil.newInstance(getContext()).autoRefresh(ptr_frame_news);

//            loadData();
            reset();
        }
    }

    /**
     * 恢复标记 防止重复加载
     */
    private void reset(){
        isViewCreated = false;
        isVisible = false;
    }

    private void loadData() {
        if (dataBeans.size() == 0){
            getNews(typeId);
            LogUtil.e(TAG, ":> loadData - " + typeId);
        }
    }

    /**
     * 免费最新新闻
     * @param type_Id 新闻类型 id
     */
    private void getNews(String type_Id) {
        NetworkInterface networkInterface = Retrofit2Util
                .createWithROLLHeader(NetworkInterface.class);

        final Map<String, Object> params = new HashMap<>();
        params.put("typeId", type_Id);
        params.put("page", page);

        Call<NewsEntity> call = networkInterface.getNewsList(params);
        call.enqueue(new Callback<NewsEntity>() {
            @Override
            public void onResponse(Call<NewsEntity> call, Response<NewsEntity> response) {
                try {
                    if (response.code() != 200){
                        showFailUi();
                        ToastUtil.showToast(getContext(), "服务器异常，请稍后重试");
                        return;
                    }
                    NewsEntity newsEntity = response.body();
                    int code = newsEntity.getCode();
                    String msg = newsEntity.getMsg();

                    if (code != 1){
                        // 请求失败
                        showFailUi();
                        ToastUtil.showToast(getContext(), msg);
                        return;
                    }else {
                        iv_news_timeout.setVisibility(View.GONE);
                        // 获取信息成功
                        List<NewsEntity.DataBean> beans = newsEntity.getData();
                        // 刷新操作 - 相关数据进行重置
                        if (page == 1){
                            dataBeans.clear();
                            adapter = null;
                        }

                        for (int i = 0;i < beans.size();i++){
                            dataBeans.add(beans.get(i));
                        }

                        Message message = handler.obtainMessage();
                        message.what = WHAT_GET_NEWS;
                        handler.sendMessage(message);
                        ToastUtil.showToast(getContext(), "新闻获取成功");
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    showFailUi();
                    ToastUtil.showToast(getContext(), "数据异常，请稍后重试");
                }
            }

            @Override
            public void onFailure(Call<NewsEntity> call, Throwable t) {
                showFailUi();
                ToastUtil.showToast(getContext(), "网络异常，请稍后重试");
            }
        });

    }

    private void display() {
        // 自动刷新取消显示
        ptr_frame_news.refreshComplete();
        recycler_view_news.setVisibility(View.VISIBLE);
        ptr_frame_news.setMode(PtrFrameLayout.Mode.BOTH);

        if (adapter != null && dataBeans.size() != 0){
            adapter.notifyDataSetChanged();
            if (page == 1){
                ToastUtil.showToast(getContext(), "刷新成功");
            }else {
                ToastUtil.showToast(getContext(), "加载成功");
            }
            return;
        }


        adapter = new NewsAdapter(getContext(), dataBeans);

        adapter.setOnItemClick(new MyItemClickInterface() {
            @Override
            public void OnRecyclerViewItemClick(View itemView, int position) {

                // 点击进入新闻链接页面
                String newsId = dataBeans.get(position).getNewsId();
                String imgUrl = "";
                if (dataBeans.get(position).getImgList() != null){
                    imgUrl = dataBeans.get(position).getImgList().get(0);
                }
                String source = dataBeans.get(position).getSource();
                String postTime = dataBeans.get(position).getPostTime();
                if (TextUtils.isEmpty(source)){
                    source = getResources().getString(R.string.app_name);
                }
                String title = dataBeans.get(position).getTitle();
                String source_time = source + "  " + postTime;

                NewsDetailFragment newsDetailFragment = NewsDetailFragment
                        .newInstance(newsId, title, source_time, imgUrl);
                showFragment(getActivity(), NewsFragment.this,
                        newsDetailFragment, NewsDetailFragment.TAG);
            }
        });

        recycler_view_news.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recycler_view_news.setAdapter(adapter);
        recycler_view_news.setItemAnimator(new DefaultItemAnimator());
        ptr_frame_news.setMode(PtrFrameLayout.Mode.BOTH);
    }

    private void showFailUi(){
        ptr_frame_news.refreshComplete();
        if (dataBeans.size() == 0){
            iv_news_timeout.setVisibility(View.VISIBLE);
        }
        page = 1;// 重置分页数
    }

    private boolean isRecyclerTop = true;
    private boolean isRecyclerBottom = false;

    @OnClick({R.id.iv_news_timeout})
    public void doClick(View view){
        switch (view.getId()){
            case R.id.iv_news_timeout:// 网络错误 刷新操作
                getNews(typeId);
                break;
        }
    }

    /**
     * 是否对用户可见 优先于 onCreateView 生命周期方法
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.e(TAG, ":> setUserVisibleHint = " + typeId + ", isVisibleToUser: " + isVisibleToUser);
        if (isVisibleToUser){
            isVisible = true;
            lazyLoad();
        }else {
            isVisible = false;
        }
    }

    @Override
    public void onDestroyView() {
        page = 1;
        super.onDestroyView();
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
