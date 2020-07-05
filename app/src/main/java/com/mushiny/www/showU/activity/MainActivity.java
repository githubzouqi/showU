package com.mushiny.www.showU.activity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mushiny.www.showU.R;
import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.fragment.BaseFragment;
import com.mushiny.www.showU.fragment.BlogFragment;
import com.mushiny.www.showU.fragment.DiscoveryFragment;
import com.mushiny.www.showU.fragment.JokeFragment;
import com.mushiny.www.showU.fragment.MineFragment;
import com.mushiny.www.showU.fragment.ToolsFragment;
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.PermissionUtil;
import com.mushiny.www.showU.util.Retrofit2Util;
import com.mushiny.www.showU.util.SPUtil;
import com.mushiny.www.showU.util.ScaleUtil;
import com.mushiny.www.showU.util.ScreenUtil;
import com.mushiny.www.showU.util.ToastUtil;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    @BindView(R.id.iv_title_refresh)ImageView iv_title_refresh;
    @BindView(R.id.iv_title_menu)ImageView iv_title_menu;
    @BindView(R.id.framelayout_container) FrameLayout framelayout_container;

    private PopupWindow pop_window_menu = null;
    private View pop_view_menu = null;
    private RequestOptions options;

    private BlogFragment blogFragment;
    private JokeFragment jokeFragment;

    private Fragment fragment;
    private Fragment mCurrentFragment = null;// 记录保存当前显示的 fragment
//    private FragmentManager manager = getSupportFragmentManager();

    // fragment的tag变量
    private String tag_blogF;
    private String tag_jokeF;
    private String tag_tools;
    private String tag_mine;
    private String tag_discovery;

    private long firstTime = 0;
    private PermissionUtil permissionUtil = null;

    private static Handler handler;
    private static final int WHAT_CHECK_UPDATE = 0x110;

    private static class MyHandler extends Handler{

        WeakReference<Context> weakReference;
        public MyHandler(Context context){
            weakReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakReference.get() != null){
                switch (msg.what){
                    case WHAT_CHECK_UPDATE:// 应用更新
                        checkUpdate(weakReference.get(), true);
                        break;
                }
            }
        }

    }

    // 检查更新
    private static void checkUpdate(final Context context, final boolean isAutoCheck) {

        NetworkInterface anInterface = Retrofit2Util.create(Constants.pgyer_base_url,
                NetworkInterface.class);
        Call<ResponseBody> call = anInterface.getAppInfo(Constants.pgyer_api_key_value,
                Constants.pgyer_app_key_value);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject obj = new JSONObject(new String(response.body().bytes()));
                    int code = obj.optInt("code");
                    if (code == 0){
                        // 获取成功
                        JSONObject data = obj.getJSONObject("data");
                        String app_name = data.optString("buildName");
                        // 根据该值来判断应用是否需要更新
                        String app_versionName = data.optString("buildVersion");
                        String app_updateDesc = data.optString("buildUpdateDescription");
                        String shortUrl = data.optString("buildShortcutUrl");

                        PackageManager pm = context.getPackageManager();
                        PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
                        String local_versionName = info.versionName;// 本地版本

                        if (!app_versionName.equals(local_versionName)){
                            // 发现新版本
                            boolean bl_update_again = SPUtil.newInstance(context)
                                    .getBoolean(UpdateActivity.key_update_again, true);
                            if (bl_update_again){
                                // 再次提示
                                UpdateActivity.start(context, app_updateDesc, shortUrl);
                            }else {
                                // 不再提示
                            }

                        }else {
                            if (isAutoCheck){
                                return;// 自动更新检查不提示，直接回滚
                            }
                            ToastUtil.showToast(context, "已是最新版本");
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);// tbs服务 - 视频为了避免闪屏和透明问题
        ButterKnife.bind(this);// 控件绑定

        initData();
        // 一次性申请所有的危险权限
        applyAllDangerousPermissions();

        // 确保内存重启的时候，不会再次加载根fragment，防止重叠问题的出现
        if (findFragmentByTag(tag_blogF) == null && savedInstanceState == null){
            LogUtil.e("zouqi", "第一次加载根 fragment");
            setHeadTitle(getResources().getString(R.string.str_my_blog));
            blogFragment = BlogFragment.newInstance();
            // 加载根 fragment，第一次进入应用显示的界面
            loadRootFragment(R.id.framelayout_container, blogFragment, tag_blogF);
            mCurrentFragment = blogFragment;
            iv_title_refresh.setVisibility(View.VISIBLE);
            setTabStyle(linear_one,iv_one,tv_one, R.mipmap.main_select);
        }else {
            LogUtil.e("zouqi", "内存重启了");
        }

        // 顶部标题布局背景颜色设置
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

        // 检查应用更新
        handler = new MyHandler(this);
        handler.sendEmptyMessageDelayed(WHAT_CHECK_UPDATE, 2000);
    }

    private void applyAllDangerousPermissions() {
        permissionUtil = new PermissionUtil(this, null);
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION};
        // android 6.0以上的危险权限使用时申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PermissionUtil.ONCE_TIME_APPLY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
        tag_tools = ToolsFragment.class.getSimpleName();
        tag_mine = MineFragment.class.getSimpleName();
        tag_discovery = DiscoveryFragment.class.getSimpleName();

        if (options == null){
            options = new RequestOptions();
            options.placeholder(R.mipmap.app_icon);// 设置占位图
//            options.override(200,200);// 指定加载图片大小
            options.override(Target.SIZE_ORIGINAL);// 加载图片原始尺寸
//            options.skipMemoryCache(true);// 禁用内存缓存。默认是开启的
        }
    }

    /**
     * 控件的点击事件（这里也就是tab的点击事件）
     */
    @OnClick({R.id.linear_one, R.id.linear_two, R.id.linear_three, R.id.linear_four,
            R.id.iv_title_refresh, R.id.iv_title_menu})
    public void doClick(View view){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (view.getId()){
            case R.id.linear_two:// 欢笑，开心的小段落

                setTabStyle(linear_two,iv_two,tv_two, R.mipmap.joke_select);
                show(JokeFragment.newInstance(), tag_jokeF, transaction);

                break;
            case R.id.linear_one:// 主页 博客

                setTabStyle(linear_one,iv_one,tv_one, R.mipmap.main_select);
                show(BlogFragment.newInstance(), tag_blogF, transaction);
                break;
            case R.id.linear_three:// 发现

                setTabStyle(linear_three, iv_three, tv_three, R.mipmap.discovery_select);
                DiscoveryFragment discoveryFragment = DiscoveryFragment.newInstance();
                show(discoveryFragment, tag_discovery, transaction);

                break;

            case R.id.linear_four:// 工具

                setTabStyle(linear_four, iv_four, tv_four, R.mipmap.tool_select);
//                show(MineFragment.newInstance(), tag_mine, transaction);
                show(ToolsFragment.newInstance(), tag_tools, transaction);

                break;

            case R.id.iv_title_refresh:// 主页 - 刷新
                if (mCurrentFragment instanceof BlogFragment){
                    ((BlogFragment)mCurrentFragment).reload();
                }
                break;
            case R.id.iv_title_menu:// 主页 - 菜单
                showMenuPop();
                break;

        }
    }

    /**
     * 弹框显示 menu
     */
    private void showMenuPop() {

        pop_view_menu = getLayoutInflater().inflate(R.layout.pop_view_menu, null);
        final ImageView iv_menu_gif = pop_view_menu.findViewById(R.id.iv_menu_gif);
        Glide.with(this).asGif()
                .load(R.mipmap.menu_bg).apply(options).into(iv_menu_gif);
        // 菜单 - 检查更新
        pop_view_menu.findViewById(R.id.tv_menu_check_update).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpdate(MainActivity.this, false);// 手动检查更新
                pop_window_menu.dismiss();
            }
        });
        // 菜单 - 关于
        pop_view_menu.findViewById(R.id.tv_menu_about).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("关于")
                        .setIcon(R.mipmap.app_icon)
                        .setMessage(getResources().getString(R.string.str_menu_about))
                        .create().show();
                pop_window_menu.dismiss();
            }
        });

        int width = new ScreenUtil().getScreenSize(ScreenUtil.WIDTH, this);
        int w = width - ScaleUtil.dip2px(this, 20);
        pop_window_menu = new PopupWindow(pop_view_menu, w, ViewGroup.LayoutParams.WRAP_CONTENT);
        pop_window_menu.setAnimationStyle(R.style.pop_anim_menu);
        pop_window_menu.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        pop_window_menu.setFocusable(true);
        pop_window_menu.setOutsideTouchable(true);
        pop_window_menu.update();
        pop_window_menu.showAtLocation(framelayout_container, Gravity.TOP, 0, 0);

        bgAlpha(0.618f);
        pop_window_menu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bgAlpha(1.0f);
                Glide.with(MainActivity.this).clear(iv_menu_gif);
            }
        });

    }

    /**
     * 显示选中的页面
     * @param fragment
     * @param tag
     * @param transaction
     */
    private void show(Fragment fragment, String tag, FragmentTransaction transaction) {

        if (fragment instanceof BlogFragment){
            iv_title_refresh.setVisibility(View.VISIBLE);
        }else {
            iv_title_refresh.setVisibility(View.GONE);
        }

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
    private void setTabStyle(LinearLayout linearLayout, ImageView imageView, TextView textView,
                             int resourceId) {

        setAllTabsStyle();

        linearLayout.setBackgroundColor(getResources().getColor(R.color.color_select));
        imageView.setImageResource(resourceId);
        textView.setTextColor(getResources().getColor(R.color.color_other));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

    }

    /**
     * 统一所有底部栏选项的样式
     */
    private void setAllTabsStyle(){

        setStyle(linear_one, iv_one, tv_one, R.mipmap.main_unselect);
        setStyle(linear_two, iv_two, tv_two, R.mipmap.joke_unselect);
        setStyle(linear_three, iv_three, tv_three, R.mipmap.discovery_unselect);
        setStyle(linear_four, iv_four, tv_four, R.mipmap.tool_unselect);

    }

    private void setStyle(LinearLayout linearLayout, ImageView imageView, TextView textView,
                          int resourceId) {
        linearLayout.setBackgroundColor(getResources().getColor(R.color.color_white));
        imageView.setImageResource(resourceId);
        textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
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
                ((BaseFragment) mCurrentFragment).onTitleSet();
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

    /**
     * 窗口背景透明度设置
     * @param f
     */
    private  void bgAlpha(float f){
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = f;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("TAG", "onPause");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("TAG", "onDestroy");
        // 移除消息，防止内存泄漏
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
