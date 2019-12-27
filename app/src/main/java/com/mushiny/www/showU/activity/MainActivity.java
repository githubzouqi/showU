package com.mushiny.www.showU.activity;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.fragment.BlogFragment;
import com.mushiny.www.showU.fragment.JokeFragment;
import com.mushiny.www.showU.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * fragment 的容器
 */

public class MainActivity extends BaseActivity {

    private static final String TITLE_ONE = "主页";
    private static final String TITLE_TWO = "开心一刻";
    private static final String TITLE_THREE = "发现";
    private static final String TITLE_FOUR = "我的";
    private static final long TIME_INTERVAL = 2000;// 点击两次返回生效的间隔时间
    @BindView(R.id.tv_title)TextView tv_title;

    // 底部选项控件
    @BindView(R.id.linear_one)LinearLayout linear_one;
    @BindView(R.id.linear_two)LinearLayout linear_two;
    @BindView(R.id.iv_one)ImageView iv_one;
    @BindView(R.id.iv_two)ImageView iv_two;
    @BindView(R.id.tv_one)TextView tv_one;
    @BindView(R.id.tv_two)TextView tv_two;

    private BlogFragment blogFragment;
    private JokeFragment jokeFragment;

    private Fragment fragment;
    private Fragment mCurrentFragment = null;// 记录保存当前显示的 fragment
    private FragmentManager manager = getSupportFragmentManager();

    // fragment的tag变量
    private String tag_blogF;
    private String tag_jokeF;
    private String tag_discovery;
    private String tag_mine;

    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);// 控件绑定

        initData();

//        if (blogFragment == null){
//            setHeadTitle(TITLE_ONE);
//            blogFragment = BlogFragment.newInstance();
//            // 加载根 fragment，第一次进入应用显示的界面
//            loadRootFragment(R.id.framelayout_container, blogFragment, tag_blogF);
//            mCurrentFragment = blogFragment;
//        }

        if (jokeFragment == null){
            setHeadTitle(TITLE_TWO);
            jokeFragment = JokeFragment.newInstance();
            // 加载根 fragment，第一次进入应用显示的界面
            loadRootFragment(R.id.framelayout_container, jokeFragment, tag_jokeF);
            mCurrentFragment = jokeFragment;
            setTabStyle(linear_two,iv_two,tv_two);
        }



        // 透明化状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLACK);
        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 沉浸式设置
//        if (hasFocus && Build.VERSION.SDK_INT >= 19){
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
    }

    /**
     * 初始化
     */
    private void initData() {

        // tag变量初始化
        tag_blogF = BlogFragment.class.getSimpleName();
        tag_jokeF = JokeFragment.class.getSimpleName();

    }

    /**
     * 控件的点击事件（这里也就是tab的点击事件）
     */
    @OnClick({R.id.linear_one, R.id.linear_two})
    public void doClick(View view){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (view.getId()){
            case R.id.linear_two:// 笑话

                setTabStyle(linear_two,iv_two,tv_two);

                setHeadTitle(TITLE_TWO);

                /*
                if (findFragmentByTag(tag_jokeF) == null){
                    hideAllFragment(transaction);
                    jokeFragment = JokeFragment.newInstance();
                    // activity 添加还没有添加的 fragment
                    transaction.add(R.id.framelayout_container, jokeFragment, tag_jokeF).commitAllowingStateLoss();
                    mCurrentFragment = jokeFragment;
                    return;
                } else {
                    // 通过添加fragment时设置的tag标记来获取已经添加过的fragment实例
                    jokeFragment = (JokeFragment) findFragmentByTag(tag_jokeF);
                }

                // 隐藏所有fragment
                hideAllFragment(transaction);
                // 显示
                transaction.show(jokeFragment).commitAllowingStateLoss();

                mCurrentFragment = jokeFragment;
                */
                show(JokeFragment.newInstance(), tag_jokeF, transaction);
                break;
            case R.id.linear_one:// 主页 博客

                setTabStyle(linear_one,iv_one,tv_one);

                setHeadTitle(TITLE_ONE);

                show(BlogFragment.newInstance(), tag_blogF, transaction);

                break;
        }
    }

    /**
     * 显示选中的页面
     * @param fragment
     * @param tag
     * @param transaction
     */
    private void show(Fragment fragment, String tag, FragmentTransaction transaction) {

        if (findFragmentByTag(tag) == null){
            hideAllFragment(transaction);
            // activity 添加还没有添加的 fragment
            transaction.add(R.id.framelayout_container, fragment, tag).commitAllowingStateLoss();
            mCurrentFragment = fragment;
            return;
        }else {
            this.fragment = findFragmentByTag(tag);
        }

        // 隐藏所有fragment
        hideAllFragment(transaction);

        // 显示
        transaction.show(this.fragment).commitAllowingStateLoss();
        mCurrentFragment = this.fragment;

    }

    /**
     * 设置某个选中底部选项的样式
     * @param linearLayout
     * @param imageView
     * @param textView
     */
    private void setTabStyle(LinearLayout linearLayout, ImageView imageView, TextView textView) {

        setAllTabsStyle();

        linearLayout.setBackgroundColor(getResources().getColor(R.color.color_select));
        imageView.setImageResource(R.mipmap.selected_zhui);
        textView.setTextColor(getResources().getColor(R.color.color_white));

    }

    /**
     * 统一所有底部栏选项的样式
     */
    private void setAllTabsStyle(){

        setStyle(linear_one, iv_one, tv_one);
        setStyle(linear_two, iv_two, tv_two);

    }

    private void setStyle(LinearLayout linearLayout, ImageView imageView, TextView textView) {
        linearLayout.setBackgroundColor(getResources().getColor(R.color.color_white));
        imageView.setImageResource(R.mipmap.showu_icon);
        textView.setTextColor(getResources().getColor(R.color.color_black));
    }

    /**
     * 设置顶部标题内容
     * @param title
     */
    private void setHeadTitle(String title) {

        if (tv_title != null){
            tv_title.setText(title);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (manager.getBackStackEntryCount() != 0){
                manager.popBackStack();
            }else {

                if ((mCurrentFragment instanceof BlogFragment) && findFragmentByTag(tag_blogF) != null && !((BlogFragment)findFragmentByTag(tag_blogF)).canBack()){
                    exitApp(TIME_INTERVAL);
                }

                if (!(mCurrentFragment instanceof BlogFragment)){
                    exitApp(TIME_INTERVAL);
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出应用
     * @param timeInterval
     */
    private void exitApp(long timeInterval) {
        if ((System.currentTimeMillis() - firstTime) >= timeInterval){
            ToastUtil.showToast(this, getResources().getString(R.string.str_exit_app));
            firstTime = System.currentTimeMillis();
        }else {
            finish();
            System.exit(0);
        }
    }
}