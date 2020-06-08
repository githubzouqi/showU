package com.mushiny.www.showU.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.interfaces.GrantListener;


/**
 * 危险权限 申请使用 工具类
 * 目前是 9组24个
 */
public class PermissionUtil {

    public static final int CAMERA = 0x1001;//相机

    public static final int READ_CALENDAR = 0x1002;// 日历
    public static final int WRITE_CALENDAR = 0x1003;

    public static final int READ_CONTACTS = 0x1004;// 联系人
    public static final int WRITE_CONTACTS = 0x1005;
    public static final int GET_ACCOUNTS = 0x1006;

    public static final int ACCESS_FINE_LOCATION = 0x1007;// 位置
    public static final int ACCESS_COARSE_LOCATION = 0x1008;

    public static final int RECORD_AUDIO = 0x1009;// 麦克风

    public static final int READ_PHONE_STATE = 0x1010;// 手机
    public static final int CALL_PHONE = 0x1011;
    public static final int READ_CALL_LOG = 0x1012;
    public static final int WRITE_CALL_LOG = 0x1013;
    public static final int ADD_VOICEMAIL = 0x1014;
    public static final int USE_SIP = 0x1015;
    public static final int PROCESS_OUTGOING_CALLS = 0x1016;

    public static final int BODY_SENSORS = 0x1017;// 传感器

    public static final int SEND_SMS = 0x1018;// 短信
    public static final int RECEIVE_SMS = 0x1019;
    public static final int READ_SMS = 0x1020;
    public static final int RECEIVE_WAP_PUSH = 0x1021;
    public static final int RECEIVE_MMS = 0x1022;

    public static final int READ_EXTERNAL_STORAGE = 0x1023;// 存储卡
    public static final int WRITE_EXTERNAL_STORAGE = 0x1024;

    public static final int ONCE_TIME_APPLY = 0x2000;

    private String GROUP_NAME = "";// 声明该变量用来保存危险权限组名

    private GrantListener grantListener = null;

    private Context context;
    private Fragment fragment;

    public PermissionUtil(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    /**
     * 申请危险权限
     * @param requestCode 请求码
     */
    public void apply(int requestCode, GrantListener grantListener) {

        this.grantListener = grantListener;// 权限同意的监听设置
        String permission = "";
        permission = getPermission(requestCode);
        if (TextUtils.isEmpty(permission)){
            // 未获取到对应危险权限，一般是请求码不按照给定变量设置或请求的不是危险权限时出现
            return;
        }
        // 危险权限申请使用 适配6.0及以上版本
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if (fragment != null){
                fragment.requestPermissions(new String[]{permission}, requestCode);
            }else {
                ((Activity)context).requestPermissions(new String[]{permission}, requestCode);
            }

        }

    }

    /**
     * 根据请求码获取对应危险权限    同时赋值对应的权限组名
     * @param requestCode
     * @return
     */
    private String getPermission(int requestCode) {
        String permission = "";
        switch (requestCode){
            // 相机
            case CAMERA: permission = Manifest.permission.CAMERA;
                GROUP_NAME = "相机";
                break;
            // 日历
            case READ_CALENDAR: permission = Manifest.permission.READ_CALENDAR;
                GROUP_NAME = "日历";
                break;
            case WRITE_CALENDAR: permission = Manifest.permission.WRITE_CALENDAR;
                GROUP_NAME = "日历";
                break;
            // 联系人
            case READ_CONTACTS: permission = Manifest.permission.READ_CONTACTS;
                GROUP_NAME = "联系人";
                break;
            case WRITE_CONTACTS: permission = Manifest.permission.WRITE_CONTACTS;
                GROUP_NAME = "联系人";
                break;
            case GET_ACCOUNTS: permission = Manifest.permission.GET_ACCOUNTS;
                GROUP_NAME = "联系人";
                break;
            // 位置
            case ACCESS_FINE_LOCATION: permission = Manifest.permission.ACCESS_FINE_LOCATION;
                GROUP_NAME = "位置";
                break;
            case ACCESS_COARSE_LOCATION: permission = Manifest.permission.ACCESS_COARSE_LOCATION;
                GROUP_NAME = "位置";
                break;
            // 麦克风
            case RECORD_AUDIO: permission = Manifest.permission.RECORD_AUDIO;
                GROUP_NAME = "麦克风";
                break;
            // 手机
            case READ_PHONE_STATE: permission = Manifest.permission.READ_PHONE_STATE;
                GROUP_NAME = "手机";
                break;
            case CALL_PHONE: permission = Manifest.permission.CALL_PHONE;
                GROUP_NAME = "手机";
                break;
            case READ_CALL_LOG: permission = Manifest.permission.READ_CALL_LOG;
                GROUP_NAME = "手机";
                break;
            case WRITE_CALL_LOG: permission = Manifest.permission.WRITE_CALL_LOG;
                GROUP_NAME = "手机";
                break;
            case ADD_VOICEMAIL: permission = Manifest.permission.ADD_VOICEMAIL;
                GROUP_NAME = "手机";
                break;
            case USE_SIP: permission = Manifest.permission.USE_SIP;
                GROUP_NAME = "手机";
                break;
            case PROCESS_OUTGOING_CALLS: permission = Manifest.permission.PROCESS_OUTGOING_CALLS;
                GROUP_NAME = "手机";
                break;
            // 传感器
            case BODY_SENSORS: permission = Manifest.permission.BODY_SENSORS;
                GROUP_NAME = "传感器";
                break;
            // 短信
            case SEND_SMS: permission = Manifest.permission.SEND_SMS;
                GROUP_NAME = "短信";
                break;
            case RECEIVE_SMS: permission = Manifest.permission.RECEIVE_SMS;
                GROUP_NAME = "短信";
                break;
            case READ_SMS: permission = Manifest.permission.READ_SMS;
                GROUP_NAME = "短信";
                break;
            case RECEIVE_WAP_PUSH: permission = Manifest.permission.RECEIVE_WAP_PUSH;
                GROUP_NAME = "短信";
                break;
            case RECEIVE_MMS: permission = Manifest.permission.RECEIVE_MMS;
                GROUP_NAME = "短信";
                break;
            // 存储卡
            case READ_EXTERNAL_STORAGE: permission = Manifest.permission.READ_EXTERNAL_STORAGE;
                GROUP_NAME = "存储卡";
                break;
            case WRITE_EXTERNAL_STORAGE: permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                GROUP_NAME = "存储卡";
                break;
        }
        return permission;
    }

    /**
     * 用户是否同意权限
     * @param grantResults
     * @return 返回 true 表示用户已同意了，false 则反之
     */
    private boolean isGranted(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * 请求使用某个权限 结果返回
     * @param requestCode
     * @param grantResults
     */
    public void singleApplyResult(int requestCode, @NonNull int[] grantResults) {
        if(isGranted(grantResults)){
            // 权限同意
            if (grantListener != null){
                grantListener.onAgree();
            }
        }else {
            // 未同意 分两种情况
            if (ActivityCompat.shouldShowRequestPermissionRationale(((Activity)context),
                    getPermission(requestCode))){
                // 情况一  用户只拒绝，未选不再提示项
                if(grantListener != null){
                    grantListener.onDeny();
                }
            }else {
                // 情况二  用户拒绝了，并选择了不再提示项 这时候可跳转到应用详情页让用户手动选择打开相关权限操作
                if (grantListener != null){
                    grantListener.onDenyNotAskAgain();
                }
            }

        }
    }

    /**
     * 一次性同时申请所有的权限 结果返回
     * @param permissions
     * @param grantResults
     */
    public void onceTimeApplyResult(String[] permissions, int[] grantResults){
        String divider = "";
        String appendContent = "相关权限 】";
        StringBuilder notAllowedPermissionNames = new StringBuilder(divider);
        for (int i = 0;i < permissions.length;i++){
            String permission = permissions[i];// 某个权限

            if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED){

            }else {
                notAllowedPermissionNames.append(" 【" + getPermissionGroupName(permission) + appendContent);
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        permission)){

                }else {

                }
            }
        }

        // 有拒绝使用的权限，简单提示用户并添加相关说明
        if (!divider.equals(notAllowedPermissionNames.toString().trim())){
            new AlertDialog.Builder(context)
//                    .setTitle("应用权限说明")
//                    .setIcon(R.mipmap.showu_icon)
                    .setMessage("您拒绝的权限有" + notAllowedPermissionNames + "，后面使用相关功能时会再次" +
                            "向您申请被拒的相关权限，请您知晓！")
                    .setCancelable(false)
                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }

    }

    /**
     * 根据某权限获取权限组名
     * @param permission
     * @return
     */
    private String getPermissionGroupName(String permission) {

        String groupName = "";
        switch (permission){
            case Manifest.permission.READ_CALENDAR:
            case Manifest.permission.WRITE_CALENDAR:
                groupName = "日历";
                break;
            case Manifest.permission.CAMERA:
                groupName = "相机";
                break;
            case Manifest.permission.READ_CONTACTS:
            case Manifest.permission.WRITE_CONTACTS:
            case Manifest.permission.GET_ACCOUNTS:
                groupName = "联系人";
                break;
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                groupName = "位置";
                break;
            case Manifest.permission.RECORD_AUDIO:
                groupName = "麦克风";
                break;
            case Manifest.permission.READ_PHONE_STATE:
            case Manifest.permission.CALL_PHONE:
            case Manifest.permission.READ_CALL_LOG:
            case Manifest.permission.WRITE_CALL_LOG:
            case Manifest.permission.ADD_VOICEMAIL:
            case Manifest.permission.USE_SIP:
            case Manifest.permission.PROCESS_OUTGOING_CALLS:
                groupName = "手机";
                break;
            case Manifest.permission.BODY_SENSORS:
                groupName = "传感器";
                break;
            case Manifest.permission.SEND_SMS:
            case Manifest.permission.RECEIVE_SMS:
            case Manifest.permission.READ_SMS:
            case Manifest.permission.RECEIVE_WAP_PUSH:
            case Manifest.permission.RECEIVE_MMS:
                groupName = "短信";
                break;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                groupName = "存储";
                break;
        }
        return groupName;
    }

}
