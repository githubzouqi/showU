package com.mushiny.www.showU.application;

import android.app.Application;

import com.tencent.smtt.sdk.QbSdk;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
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
