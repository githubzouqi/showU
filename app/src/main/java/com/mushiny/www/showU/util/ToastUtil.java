package com.mushiny.www.showU.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mushiny.www.showU.R;

import org.w3c.dom.Text;

/**
 * 自定义吐丝工具类
 */
public class ToastUtil {

    public static Toast toast;
    private static View toastView;
    private static TextView tv_exit_app;

    /**
     * 吐丝的方法，可以避免重复吐丝。当你点击多次按钮的时候，吐丝只出现一次。
     * @param context 上下文对象
     * @param string    吐丝的内容
     */
    public static void showToast(Context context, String string) {

        if (toastView == null){
            toastView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout
                    .toast_view_exit, null);
        }
        tv_exit_app = toastView.findViewById(R.id.tv_exit_app);
        tv_exit_app.setText(string);
        int w = context.getResources().getDisplayMetrics().widthPixels / 2;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, ViewGroup.LayoutParams
                .WRAP_CONTENT);
        tv_exit_app.setLayoutParams(params);
        if (toast == null){
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastView);
            toast.setGravity(Gravity.CENTER, 0, 0);
        }

        toast.show();

    }

}
