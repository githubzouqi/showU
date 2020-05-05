package com.mushiny.www.showU.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

// 应用详情页跳转的 intent 获取
public class AppDetailSettingUtil {

    public static Intent getAppDetailSettingIntent(Context context){
        Intent detailSettingIntent = new Intent();
        detailSettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            detailSettingIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            detailSettingIntent.setData(Uri.fromParts("package",
                    context.getApplicationContext().getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            detailSettingIntent.setAction(Intent.ACTION_VIEW);
            detailSettingIntent.setClassName("com.android.settings",
                    "com.android.settings.InstalledAppDetails");
            detailSettingIntent.putExtra("com.android.settings.ApplicationPkgName",
                    context.getPackageName());
        }
        return detailSettingIntent;
    }

}
