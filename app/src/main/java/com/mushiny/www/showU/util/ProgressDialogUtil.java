package com.mushiny.www.showU.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mushiny.www.showU.R;

/**
 * 自定义 加载进度 工具类
 */
public class ProgressDialogUtil {

    private Context context;
    private AlertDialog dialog;
    private TextView tv_progress_message;
    private View view_progress;

    public ProgressDialogUtil(Context context) {
        this.context = context.getApplicationContext();
        dialog = new AlertDialog.Builder(context).create();
        view_progress = LayoutInflater.from(context).inflate(R.layout.dialog_view_progress, null);
    }

    /**
     * 设置进度条显示的内容
     */
    public void setMessage(String message){

        if (view_progress != null){
            tv_progress_message = view_progress.findViewById(R.id.tv_progress_message);
        }

        if (!TextUtils.isEmpty(message)){
            tv_progress_message.setText(message);
        }else {
            tv_progress_message.setText("加载中...");
        }
    }

    /**
     * 进度条显示
     */
    public void show(){

        view_progress = LayoutInflater.from(context).inflate(R.layout.dialog_view_progress, null);
        dialog = new AlertDialog.Builder(context).create();
        dialog.setView(view_progress);
        dialog.show();

    }

    /**
     * 进度条隐藏
     */
    public void dismiss(){
        if (dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }
}
