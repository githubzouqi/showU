package com.mushiny.www.showU.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mushiny.www.showU.R;

/**
 * 自定义吐丝工具类
 */
public class ToastUtil {

    public static Toast toast;
    private static View toastView;
    /**
     * 吐丝的方法，可以避免重复吐丝。当你点击多次按钮的时候，吐丝只出现一次。
     * @param context 上下文对象
     * @param string    吐丝的内容
     */
    public static void showToast(Context context, String string) {

        if (toastView == null){
            toastView = LayoutInflater.from(context).inflate(R.layout.toast_view_exit, null);
        }
        ((TextView)toastView.findViewById(R.id.tv_exit_app)).setText(string);

        if (toast == null){
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastView);
            toast.setGravity(Gravity.CENTER, 0, 0);
        }

        toast.show();

    }

}
