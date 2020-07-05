package com.mushiny.www.showU.util;

import android.content.Context;

import in.srain.cube.views.ptr.PtrClassicDefaultFooter;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

public class PtrUtil {

//    public static final int DEFAULT_COLOR = 0x3389f7;
    public static final int DEFAULT_COLOR = 0x44BB44;
    private static PtrUtil ptrUtil;
    private Context context;

    public PtrUtil(Context context) {
        this.context = context.getApplicationContext();
    }

    public static PtrUtil newInstance(Context context){
        if (ptrUtil == null){
            synchronized (PtrUtil.class){
                if (ptrUtil == null) {
                    ptrUtil = new PtrUtil(context);
                }
            }
        }
        return ptrUtil;
    }

    /**
     * 设置基本属性
     * @param ptr
     */
    public void set_1_BaseSetting(PtrFrameLayout ptr){
        // 头部和底部的阻尼系数
        ptr.setResistanceHeader(1.7f);
        ptr.setResistanceFooter(1.7f);

        ptr.setDurationToCloseHeader(1500);
        ptr.setDurationToCloseFooter(500);
        ptr.setDurationToBackHeader(500);
        ptr.setDurationToBackFooter(500);

        ptr.setPinContent(true);
        ptr.setPullToRefresh(false);// false 表示手指释放才会刷新，true 表示下拉刷新
    }

    /**
     * 设置Material风格header
     * @param ptr
     * @param colorInt
     */
    public void set_2_MaterialHeader(PtrFrameLayout ptr, int colorInt){
        // Materail 风格头部实现
        MaterialHeader header = new MaterialHeader(context);
        // 设置下拉刷新头部view的颜色
        header.setColorSchemeColors(new int[]{colorInt});
        header.setPadding(0, PtrLocalDisplay.dp2px(15),0,0);
        ptr.setHeaderView(header);
        ptr.addPtrUIHandler(header);
    }

    public void set_3_Footer(PtrFrameLayout ptr){
        // 经典底部布局实现
        PtrClassicDefaultFooter footer = new PtrClassicDefaultFooter(context);
        footer.setPadding(0, PtrLocalDisplay.dp2px(15),0,0);
        ptr.setFooterView(footer);
        ptr.addPtrUIHandler(footer);
    }

    /**
     * 提供默认初始化设置
     * @param ptr
     * @param colorInt
     */
    public void initial(PtrFrameLayout ptr, int colorInt){
        set_1_BaseSetting(ptr);
        set_2_MaterialHeader(ptr, colorInt);
        set_3_Footer(ptr);
    }

    public void initialRefreshOnly(PtrFrameLayout ptr, int colorInt){
        initial(ptr, colorInt);
        ptr.setMode(PtrFrameLayout.Mode.REFRESH);
    }

    public void autoRefresh(final PtrFrameLayout ptr){
        // 自动刷新
        ptr.post(new Runnable() {
            @Override
            public void run() { ptr.autoRefresh(); }
        });
    }

}
