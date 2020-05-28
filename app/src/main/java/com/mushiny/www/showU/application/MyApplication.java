package com.mushiny.www.showU.application;

import android.app.Application;
import android.util.Log;

import com.mushiny.www.showU.util.LogUtil;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import org.litepal.LitePal;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        QbSdk.setDownloadWithoutWifi(true);
        preInitX5WebCore();
        initUmeng();

        LogUtil.setLevel(LogUtil.VERBOSE);// 可打印所有等级 log 信息
//        LogUtil.setLevel(LogUtil.NOTHING);// 正式上线 不打印 log 信息

        LitePal.initialize(this);// 配置 LitePalApplication

    }

    /**
     * 友盟sdk相关初始化
     */
    private void initUmeng() {
        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         * 注意：App正式上线前请关闭SDK运行调试日志。避免无关Log输出。
         */
        UMConfigure.setLogEnabled(true);
        /**
         * 注意：如果您已经在AndroidManifest.xml中配置过appkey和channel值，可以调用此版本初始化函数。
         * 因为我还未集成推送功能，所以这里第三个参数的值我设为了空串
         */
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");

        // 选用 AUTO 页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }

    /**
     * 预加载 x5 内核
     */
    private void preInitX5WebCore() {

//        if(!QbSdk.isTbsCoreInited()){
//            QbSdk.initX5Environment(this, null);
//        }

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtil.e("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(this,  cb);

    }
}
