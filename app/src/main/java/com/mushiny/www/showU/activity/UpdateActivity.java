package com.mushiny.www.showU.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.util.SPUtil;
import com.mushiny.www.showU.util.ScreenUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateActivity extends BaseActivity {

    @BindView(R.id.cv_update)
    CardView cv_update;
    @BindView(R.id.tv_update_desc)
    TextView tv_update_desc;

    private String update_desc = "";
    private String shortUrl = "";

    private static final String UPDATE_INFO = "updateInfo";
    private static final String SHORT_URL = "shortUrl";

    public static final String key_update_again = "key_update_again";
    private boolean bl_update_again = true;// 默认再次提示更新版本

    /**
     * 启动 UpdateActivity
     * @param context
     * @param updateInfo 更新信息说明
     * @param shortUrl
     */
    public static void start(Context context, String updateInfo, String shortUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(UPDATE_INFO, updateInfo);
        bundle.putString(SHORT_URL, shortUrl);
        Intent intent = new Intent(context, UpdateActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);
        setFinishOnTouchOutside(false);// 设置触摸框体外区域不销毁该activity

        initData();

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cv_update.getLayoutParams();

        int w = new ScreenUtil().getScreenSize(ScreenUtil.WIDTH, this) / 4 * 3;
        lp.width = w;
        lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        cv_update.setLayoutParams(lp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(getResources().getColor(R.color.color_other));// 设置状态栏背景色
        }

    }

    /**
     * 数据初始化
     */
    private void initData() {
        if (getIntent() != null){
            update_desc = getIntent().getExtras().getString(UPDATE_INFO, getResources().
                    getString(R.string.app_name));
            shortUrl = getIntent().getExtras().getString(SHORT_URL, "v2TO");
        }

        tv_update_desc.setText(update_desc);
    }

    /**
     * 点击事件监听
     * @param view
     */
    @OnClick({R.id.tv_update_cancel, R.id.tv_update_ok, R.id.iv_update_close})
    public void doClick(View view){
        switch (view.getId()){
            case R.id.tv_update_cancel:// 下次再说
                finish();
                break;
            case R.id.tv_update_ok:// 现在更新 https://www.pgyer.com/v2TO

                String apkUrl = Constants.pgyer_base_url + shortUrl;
                openBrowserUpdate(apkUrl);
                finish();

                break;
            case R.id.iv_update_close:// 关闭更新提示框
                cv_update.setVisibility(View.INVISIBLE);// 显示对话框时隐藏更新提示布局，优化用户体验
                new AlertDialog.Builder(this).setTitle("不再提示版本更新")
                        .setIcon(R.mipmap.app_icon).setCancelable(false)
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // sharedPreference 记录状态
                                SPUtil.newInstance(UpdateActivity.this).putBoolean(key_update_again,
                                        false);
                                dialog.dismiss();
                                finish();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 退出
                        dialog.dismiss();
                        finish();
                    }
                }).create().show();

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
    }

    /**
     * 打开浏览器更新下载新版本apk
     * @param apkUrl    apk托管地址
     */
    private void openBrowserUpdate(String apkUrl) {

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri apk_url = Uri.parse(apkUrl);
        intent.setData(apk_url);
        startActivity(intent);//打开浏览器

    }
}