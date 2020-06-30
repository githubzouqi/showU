package com.mushiny.www.showU.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    private ArrayList<CustomTabEntity> tabEntitys = new ArrayList<>();
    private List<NewsFragment> newsFragments = new ArrayList<>();
    private String[] titles;

    private List<Object[]> newList = new ArrayList<>();// 保存新闻类型列表

    public DiscoveryFragment() {
        // Required empty public constructor
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
                baseTitle = slidingTabLayout.getTitleView(position).getText().toString();
                onTitleSet();
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

        // 获取新闻类型列表
        final NetworkInterface anInterface = Retrofit2Util.createWithROLLHeader(NetworkInterface.class);
        Call<ResponseBody> call = anInterface.getNewsType();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    JSONObject obj = new JSONObject(new String(response.body().bytes()));
                    JSONArray array = obj.getJSONArray("data");
                    int length = array.length();
                    for (int i = 0;i < length;i++){
                        int typeId = array.getJSONObject(i).optInt("typeId");
                        String typeName = array.getJSONObject(i).optString("typeName");
                        newList.add(new Object[]{typeId, typeName});
                    }

                    setTabs();
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtil.showToast(getContext(), "获取失败：" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ToastUtil.showToast(getContext(), "获取失败：" + t.getMessage());
            }
        });

    }

    /**
     * 设置 tab
     */
    private void setTabs() {
        for (int i = 0;i < newList.size();i++){
            String typeId = String.valueOf(newList.get(i)[0]);
            String typeName = String.valueOf(newList.get(i)[1]);
            if (!TextUtils.isEmpty(typeName) && !typeName.contains("视频")){
                tabEntitys.add(new TabEntity(typeName));
                NewsFragment newsFragment = NewsFragment.newInstance(typeId, typeName);
                newsFragments.add(newsFragment);
            }
        }

        if (newsFragments.size() != 0) {
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
            baseTitle = slidingTabLayout.getTitleView(0).getText().toString();
            onTitleSet();
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