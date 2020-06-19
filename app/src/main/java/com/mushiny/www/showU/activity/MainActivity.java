package com.mushiny.www.showU.activity;

import android.Manifest;
import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.fragment.BlogFragment;
import com.mushiny.www.showU.fragment.DiscoveryFragment;
import com.mushiny.www.showU.fragment.NewsFragment;
import com.mushiny.www.showU.fragment.JokeFragment;
import com.mushiny.www.showU.fragment.MineFragment;
import com.mushiny.www.showU.interfaces.TitleListener;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.PermissionUtil;
import com.mushiny.www.showU.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * fragment 的容器
 */

public class MainActivity extends BaseActivity {

    private static final long TIME_INTERVAL = 2000;// 点击两次返回生效的间隔时间
    @BindView(R.id.tv_title)TextView tv_title;

    // 底部选项控件
    @BindView(R.id.linear_one) LinearLayout linear_one;
    @BindView(R.id.linear_two) LinearLayout linear_two;
    @BindView(R.id.linear_three) LinearLayout linear_three;
    @BindView(R.id.linear_four) LinearLayout linear_four;
    @BindView(R.id.iv_one) ImageView iv_one;
    @BindView(R.id.iv_two) ImageView iv_two;
    @BindView(R.id.iv_three) ImageView iv_three;
    @BindView(R.id.iv_four) ImageView iv_four;
    @BindView(R.id.tv_one) TextView tv_one;
    @BindView(R.id.tv_two) TextView tv_two;
    @BindView(R.id.tv_three) TextView tv_three;
    @BindView(R.id.tv_four) TextView tv_four;

    @BindView(R.id.linear_tab)LinearLayout linear_tab;
    @BindView(R.id.relative_layout_title) RelativeLayout relative_layout_title;

    private BlogFragment blogFragment;
    private JokeFragment jokeFragment;

    private Fragment fragment;
    private Fragment mCurrentFragment = null;// 记录保存当前显示的 fragment
//    private FragmentManager manager = getSupportFragmentManager();

    // fragment的tag变量
    private String tag_blogF;
    private String tag_jokeF;
    private String tag_news;
    private String tag_mine;
    private String tag_discovery;

    private long firstTime = 0;
    private PermissionUtil permissionUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);// tbs服务 - 视频为了避免闪屏和透明问题
        ButterKnife.bind(this);// 控件绑定

        initData();

        // 确保内存重启的时候，不会再次加载根fragment，防止重叠问题的出现
        if (findFragmentByTag(tag_blogF) == null && savedInstanceState == null){
            LogUtil.e("zouqi", "第一次加载根 fragment");
            setHeadTitle(getResources().getString(R.string.str_my_blog));
            blogFragment = BlogFragment.newInstance();
            // 加载根 fragment，第一次进入应用显示的界面
            loadRootFragment(R.id.framelayout_container, blogFragment, tag_blogF);
            mCurrentFragment = blogFragment;
            setTabStyle(linear_one,iv_one,tv_one);
        }else {
            LogUtil.e("zouqi", "内存重启了");
        }

//        if (jokeFragment == null){
//            setHeadTitle(TITLE_TWO);
//            jokeFragment = JokeFragment.newInstance();
//            // 加载根 fragment，第一次进入应用显示的界面
//            loadRootFragment(R.id.framelayout_container, jokeFragment, tag_jokeF);
//            mCurrentFragment = jokeFragment;
//            setTabStyle(linear_two,iv_two,tv_two);
//        }

        relative_layout_title.setBackgroundColor(getResources().getColor(R.color.color_other));

        // 透明化状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.color_other));
        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        // 一次性申请所有的危险权限
        applyAllDangerousPermissions();

    }

    private void applyAllDangerousPermissions() {
        permissionUtil = new PermissionUtil(this, null);
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA};
        // android 6.0以上的危险权限使用时申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PermissionUtil.ONCE_TIME_APPLY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PermissionUtil.ONCE_TIME_APPLY:
                permissionUtil.onceTimeApplyResult(permissions, grantResults);
                break;
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
        tag_news = NewsFragment.class.getSimpleName();
        tag_mine = MineFragment.class.getSimpleName();
        tag_discovery = DiscoveryFragment.class.getSimpleName();

    }

    /**
     * 控件的点击事件（这里也就是tab的点击事件）
     */
    @OnClick({R.id.linear_one, R.id.linear_two, R.id.linear_three, R.id.linear_four})
    public void doClick(View view){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (view.getId()){
            case R.id.linear_two:// 欢笑，开心的小段落

                setTabStyle(linear_two,iv_two,tv_two);

                setHeadTitle(getResources().getString(R.string.str_joker_title));

                show(JokeFragment.newInstance(), tag_jokeF, transaction);
                break;
            case R.id.linear_one:// 主页 博客

                setTabStyle(linear_one,iv_one,tv_one);

                setHeadTitle(getResources().getString(R.string.str_my_blog));

                show(BlogFragment.newInstance(), tag_blogF, transaction);

                break;
            case R.id.linear_three:// 发现

                setTabStyle(linear_three, iv_three, tv_three);
                setHeadTitle(getResources().getString(R.string.str_finder) + "-头条");
                DiscoveryFragment discoveryFragment = DiscoveryFragment.newInstance();
                discoveryFragment.setHeadTitleListener(new TitleListener() {
                    @Override
                    public void getTitle(String title) {
                        setHeadTitle(getResources().getString(R.string.str_finder) +
                                "-" + title);
                    }
                });
                show(discoveryFragment, tag_discovery, transaction);

                break;

            case R.id.linear_four:// 我的

                setTabStyle(linear_four, iv_four, tv_four);
                setHeadTitle(getResources().getString(R.string.str_mine));
                show(MineFragment.newInstance(), tag_mine, transaction);

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
        textView.setTextColor(getResources().getColor(R.color.color_other));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTypeface(Typeface.DEFAULT_BOLD);

    }

    /**
     * 统一所有底部栏选项的样式
     */
    private void setAllTabsStyle(){

        setStyle(linear_one, iv_one, tv_one);
        setStyle(linear_two, iv_two, tv_two);
        setStyle(linear_three, iv_three, tv_three);
        setStyle(linear_four, iv_four, tv_four);

    }

    private void setStyle(LinearLayout linearLayout, ImageView imageView, TextView textView) {
        linearLayout.setBackgroundColor(getResources().getColor(R.color.color_white));
        imageView.setImageResource(R.mipmap.showu_icon);
        textView.setTextColor(getResources().getColor(R.color.color_other));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTypeface(Typeface.DEFAULT);
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

    // 隐藏底部选项栏
    public void goneTab(){
        linear_tab.setVisibility(View.GONE);
    }

    // 显示底部选项栏
    public void visibleTab(){
        linear_tab.setVisibility(View.VISIBLE);
    }

    // 设置标题内容
    public void setTitle(String title){
        setHeadTitle(title);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){

            boolean canBack = false;
            if (getBackStackEntryCount() == 1){
                visibleTab();
            }

            if (getBackStackEntryCount() != 0){
//                if (mCurrentFragment instanceof DiscoveryFragment
//                        && findFragmentByTag(NewsDetailFragment.class.getSimpleName()) != null
//                        && (((NewsDetailFragment)findFragmentByTag(NewsDetailFragment.TAG)).canBack()) ){
//
//                }else {
//                    popBack();
//                }

                popBack();
            }else {

                LogUtil.e("TAG", "getBackStackEntryCount() = " + getBackStackEntryCount());
                canBack = ((BlogFragment)findFragmentByTag(tag_blogF)).canBack();
                LogUtil.e("TAG", "else canBack is :" + canBack);

                if ((mCurrentFragment instanceof BlogFragment) && findFragmentByTag(tag_blogF) != null
                        && !canBack){
                    exitApp(TIME_INTERVAL);
                }

                if (!(mCurrentFragment instanceof BlogFragment)){
                    exitApp(TIME_INTERVAL);
                }


            }

            // 表示 webView 可以返回
            if (canBack){
                ((BlogFragment)findFragmentByTag(tag_blogF)).goBack();
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
