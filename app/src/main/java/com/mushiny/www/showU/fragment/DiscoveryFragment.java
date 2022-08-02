package com.mushiny.www.showU.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.JsonArray;
import com.mushiny.www.showU.R;
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.interfaces.TitleListener;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.Retrofit2Util;
import com.mushiny.www.showU.util.ToastUtil;
import com.umeng.commonsdk.debug.E;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 发现页
 */
public class DiscoveryFragment extends BaseFragment {

    public static final String TYPE_HEADLINE = "top";// 头条
    public static final String TYPE_SOCIAL = "shehui";// 社会
    public static final String TYPE_DOMESTIC = "guonei";// 国内
    public static final String TYPE_INTERNATIONAL = "guoji";// 国际
    public static final String TYPE_ENTERTAINMENT = "yule";// 娱乐
    public static final String TYPE_SPORTS = "tiyu";// 体育
    public static final String TYPE_MILITARY = "junshi";// 军事
    public static final String TYPE_TECHNOLOGY = "keji";// 科技
    public static final String TYPE_FINANCE = "caijing";// 财经
    public static final String TYPE_FASHION = "shishang";// 时尚

    @BindView(R.id.view_pager_discovery)ViewPager view_pager_discovery;
    @BindView(R.id.slidingTabLayout)SlidingTabLayout slidingTabLayout;
    @BindView(R.id.iv_discovery_timeout) ImageView iv_discovery_timeout;

    private ArrayList<CustomTabEntity> tabEntitys = new ArrayList<>();
    private List<NewsFragment> newsFragments = new ArrayList<>();
    private String[] titles;

    private List<Object[]> newList = new ArrayList<>();// 保存新闻类型列表

    private static final int DELAY_TIME = 200;
    private static final int  WHAT_DELAY_REQUEST = 0x20;
    private Handler handler;

    public DiscoveryFragment() {
        // Required empty public constructor
    }

    public static class MyHandler extends Handler{
        WeakReference<DiscoveryFragment> weakReference;

        public MyHandler(DiscoveryFragment fragment){
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            DiscoveryFragment fragment = weakReference.get();
            if (fragment != null){
                switch (msg.what){
                    case WHAT_DELAY_REQUEST:
                        fragment.setTabs();
                        break;
                }
            }
        }
    }

    private TitleListener titleListener;
    public void setHeadTitleListener(TitleListener titleListener){
        this.titleListener = titleListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // 简单单例
    public static DiscoveryFragment newInstance(){
        return new DiscoveryFragment();
    }

    private View view;

    /**
     * 创建 TabEntity 继承 CustomTabEntity
     */
    public static class TabEntity implements CustomTabEntity {

        private String title;

        public TabEntity(String title) { this.title = title; }
        @Override
        public String getTabTitle() { return title; }
        @Override
        public int getTabSelectedIcon() { return 0; }
        @Override
        public int getTabUnselectedIcon() { return 0; }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null){
            view = inflater.inflate(R.layout.fragment_discovery, container, false);
        }
        ButterKnife.bind(this, view);// 控件绑定

        initData();
        setListeners();

        return view;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//    }




    /**
     * 设置监听
     */
    private void setListeners() {

        // viewPager
        view_pager_discovery.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0;i < titles.length;i++){
                    if (i == position){
                        slidingTabLayout.getTitleView(position).setTextSize(TypedValue.COMPLEX_UNIT_SP,
                                20);
                    }else {
                        slidingTabLayout.getTitleView(i).setTextSize(TypedValue.COMPLEX_UNIT_SP,
                                16);
                        slidingTabLayout.getTitleView(i).setTypeface(Typeface.DEFAULT);
                    }
                }
                onTitleSet(slidingTabLayout.getTitleView(position).getText().toString());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


    }

    /**
     * 数据初始化
     */
    private void initData() {

        if (handler == null){
            handler = new MyHandler(this);
        }

        // 获取新闻类型列表
        final NetworkInterface anInterface = Retrofit2Util.createWithROLLHeader(NetworkInterface.class);
        Call<ResponseBody> call = anInterface.getNewsType();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() != 200){
                        showFailUi();
                        ToastUtil.showToast(getContext(), "服务器异常，请稍后重试");
                        return;
                    }
                    iv_discovery_timeout.setVisibility(View.GONE);
                    JSONObject obj = new JSONObject(new String(response.body().bytes()));
                    JSONArray array = obj.getJSONArray("data");
                    int length = array.length();
                    for (int i = 0;i < length;i++){
                        int typeId = array.getJSONObject(i).optInt("typeId");
                        String typeName = array.getJSONObject(i).optString("typeName");
                        newList.add(new Object[]{typeId, typeName});
                    }

                    // 有 qps 为 1 的限制，故延迟 1s 再列表数据
                    handler.sendEmptyMessageDelayed(WHAT_DELAY_REQUEST, DELAY_TIME);
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtil.showToast(getContext(), "数据异常，请稍后重试");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showFailUi();
                ToastUtil.showToast(getContext(), "网络异常，请稍后重试");
            }
        });

    }

    private void showFailUi(){
        if (newsFragments.size() == 0){
            // 显示超时刷新 icon
            iv_discovery_timeout.setVisibility(View.VISIBLE);
            onTitleSet(getResources().getString(R.string.app_name));
        }
    }

    @OnClick({R.id.iv_discovery_timeout})
    public void doClick(View view){
        switch (view.getId()){
            case R.id.iv_discovery_timeout:// 超时刷新
                initData();
                break;
        }
    }

    /**
     * 设置 tab
     */
    private void setTabs() {
        for (int i = 0;i < newList.size();i++){
            String typeId = String.valueOf(newList.get(i)[0]);// 509 510
            String typeName = String.valueOf(newList.get(i)[1]);
            if (!TextUtils.isEmpty(typeName) && !typeName.contains("视频")
            && !typeId.equals("509")
            && !typeId.equals("510")){
                tabEntitys.add(new TabEntity(typeName));
                NewsFragment newsFragment = NewsFragment.newInstance(typeId, typeName);
                newsFragments.add(newsFragment);
            }

        }

        if (newsFragments.size() != 0) {
            slidingTabLayout.setVisibility(View.VISIBLE);
            MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getChildFragmentManager(),
                    getContext());
            view_pager_discovery.setAdapter(myPagerAdapter);
            view_pager_discovery.setOffscreenPageLimit(2);

            titles = new String[tabEntitys.size()];
            for (int i = 0;i < titles.length;i++){
                titles[i] = tabEntitys.get(i).getTabTitle();
            }
            slidingTabLayout.setViewPager(view_pager_discovery, titles);
            view_pager_discovery.setCurrentItem(0);
            // 设置 slidingTabLayout 第一个tab内容的字体大小与加粗
            slidingTabLayout.setCurrentTab(0);
            slidingTabLayout.getTitleView(0).setTypeface(Typeface.DEFAULT_BOLD);
            slidingTabLayout.getTitleView(0).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            // 初始化第一个tab内容为标题
            onTitleSet(slidingTabLayout.getTitleView(0).getText().toString());
        }

    }

    public class MyPagerAdapter extends FragmentPagerAdapter{

        private Context context;

        public MyPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int i) {
            return newsFragments.get(i);
        }

        @Override
        public int getCount() {
            return newsFragments.size();
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

}