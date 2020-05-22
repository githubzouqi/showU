package com.mushiny.www.showU.application;

import android.app.Application;

import com.mushiny.www.showU.util.LogUtil;
import com.tencent.smtt.sdk.QbSdk;

import org.litepal.LitePal;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        QbSdk.setDownloadWithoutWifi(true);
        preInitX5WebCore();

        LogUtil.setLevel(LogUtil.VERBOSE);// 可打印所有等级 log 信息

        LitePal.initialize(this);// 配置 LitePalApplication

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
        QbSdk.initX5Environment(getApplicationContext(),  cb);

    }
}
