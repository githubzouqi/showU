package com.mushiny.www.showU.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.mushiny.www.showU.R;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 启动页面
 */
public class LaunchActivity extends Activity {

    @BindView(R.id.tv_text_bottom)TextView tv_text_bottom;

    private static final int DELAY_TIME = 3000;

    // mac 系统选中英文词变大写：⇧ + ⌘ + u
    private static final int WHAT_NEXT = 0x10;

    private static Handler handler;

    private static class MyHandler extends Handler{
        WeakReference<LaunchActivity> weakReference;

        public MyHandler(LaunchActivity activity){
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakReference.get() != null){
                switch (msg.what){
                    case WHAT_NEXT:
                        next(weakReference);
                        break;
                }
            }
        }
    }

    /**
     * 跳转主页面
     * @param weakReference
     */
    private static void next(WeakReference<LaunchActivity> weakReference) {

        LaunchActivity launchActivity = weakReference.get();
        launchActivity.startActivity(new Intent(launchActivity, MainActivity.class));

        launchActivity.finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        ButterKnife.bind(this);

        String copyright = "";
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (currentYear == 2019){
            copyright = "Copyright © " + currentYear + " " + getResources().getString(R.string.app_name_pinyin);
        }else {
            copyright = "Copyright © 2019 - " + currentYear + " " + getResources().getString(R.string.app_name_pinyin);
        }
        tv_text_bottom.setText(copyright);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(getResources().getColor(R.color.color_black));// 设置状态栏背景色
        }
        ActionBar actionBar = getActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        handler = new MyHandler(this);// 创建 Handler
        handler.sendEmptyMessageDelayed(WHAT_NEXT, DELAY_TIME);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            // 不做操作。等价于启动页屏蔽了返回按键，优化用户体验
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除消息
        if (handler != null){
            handler.removeCallbacks(null);
        }
    }
}
