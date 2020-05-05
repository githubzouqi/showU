package com.mushiny.www.showU.application;

import android.app.Application;

import com.mushiny.www.showU.util.LogUtil;
import com.tencent.smtt.sdk.QbSdk;

import org.litepal.LitePal;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtil.setLevel(LogUtil.VERBOSE);// 可打印所有等级 log 信息

        LitePal.initialize(this);// 配置 LitePalApplication
        preInitX5WebCore();
    }

    /**
     * 预加载 x5 内核
     */
    private void preInitX5WebCore() {

        if(!QbSdk.isTbsCoreInited()){
            QbSdk.initX5Environment(this, null);
        }

    }
}
