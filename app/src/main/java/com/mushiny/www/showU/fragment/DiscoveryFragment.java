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
import com.mushiny.www.showU.R;
import com.mushiny.www.showU.interfaces.TitleListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        if (view == null && savedInstanceState == null){
            view = inflater.inflate(R.layout.fragment_discovery, container, false);
        }
        ButterKnife.bind(this, view);// 控件绑定

        initData();
        setListeners();

        return view;
    }

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
                if (titleListener != null){
                    titleListener.getTitle(titles[position]);
                }
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

        tabEntitys.add(new TabEntity("头条"));
        tabEntitys.add(new TabEntity("社会"));
        tabEntitys.add(new TabEntity("国内"));
        tabEntitys.add(new TabEntity("国际"));
        tabEntitys.add(new TabEntity("娱乐"));
        tabEntitys.add(new TabEntity("体育"));
        tabEntitys.add(new TabEntity("军事"));
        tabEntitys.add(new TabEntity("科技"));
        tabEntitys.add(new TabEntity("财经"));
        tabEntitys.add(new TabEntity("时尚"));

        List<String> params = new ArrayList<>();
        params.add(TYPE_HEADLINE);
        params.add(TYPE_SOCIAL);
        params.add(TYPE_DOMESTIC);
        params.add(TYPE_INTERNATIONAL);
        params.add(TYPE_ENTERTAINMENT);
        params.add(TYPE_SPORTS);
        params.add(TYPE_MILITARY);
        params.add(TYPE_TECHNOLOGY);
        params.add(TYPE_FINANCE);
        params.add(TYPE_FASHION);

        for (int i = 0;i < tabEntitys.size();i++){
            String news_type = params.get(i);
            NewsFragment newsFragment = NewsFragment.newInstance(news_type);
            newsFragments.add(newsFragment);
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
            slidingTabLayout.getTitleView(0).setTextColor(getResources().getColor(R.color.
                    color_black));
            slidingTabLayout.getTitleView(0).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
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
}