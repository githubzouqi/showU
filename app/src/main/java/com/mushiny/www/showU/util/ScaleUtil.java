package com.mushiny.www.showU.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * px 与 dp 互转工具类
 */
public class ScaleUtil {

    //dp转px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    //px转dp
    public static int px2dip(Context context, int pxValue) {
        return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxValue,
                context.getResources().getDisplayMetrics()));

    }
}
