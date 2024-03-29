package com.mushiny.www.showU.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.mushiny.www.showU.R;
import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.util.AppDetailSettingUtil;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.Retrofit2Util;
import com.mushiny.www.showU.util.ToastUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.ShareBoardConfig;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * 我的 界面
 */
public class MineFragment extends BaseFragment {

    private static final int WHAT_PERMISSION_CAMERA = 0x40;
    private static final int WHAT_PERMISSION_STORAGE = 0x41;

    private static final int REQUEST_CODE_GALLERY_CAMERA = 0x42;
    private static final int REQUEST_CODE_CROP = 0x43;

    private static final String SP_FILE_NAME = "MineFragment";
    private static final String SP_KEY_HEAD_ICON = "SP_KEY_HEAD_ICON";

    private static final String IMAGE_GALLERY_FILE_NAME = "qi_si";
    private static final String IMAGE_CAMERA_FILE_NAME = "qi_si_camera";

    private Uri crop_uri = null;
    private Uri cameraUri = null;

    private RequestOptions options;

    private String items_my_photo[] = {"拍照","相册"};
    private String parentPath = "";
    private String cropNewImageName = "";

    @BindView(R.id.iv_my_photo)ImageView iv_my_photo;

    private SharedPreferences sharedPreferences = null;

    public MineFragment() {
        // Required empty public constructor
    }

    public static MineFragment newInstance(){
        return new MineFragment();
    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null){
            view = inflater.inflate(R.layout.fragment_mine, container, false);
        }

        ButterKnife.bind(this, view);

        initData();

        setListener();

        return view;
    }

    // 设置监听
    private void setListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onTitleSet(String mBaseTitle) {
        super.onTitleSet(getResources().getString(R.string.str_mine));
    }

    // 初始化数据
    private void initData() {

        parentPath = Environment.getExternalStorageDirectory() + "/imxiaoqi";

        if (options == null){
            options = new RequestOptions();
            options.placeholder(R.mipmap.app_icon);// 设置占位图
//            options.override(160,160)// 指定加载图片大小
            options.diskCacheStrategy(DiskCacheStrategy.ALL);// 缓存所有：原型、转换后的
            options.centerCrop();
//        options.override(Target.SIZE_ORIGINAL);// 加载图片原始尺寸
//            options.skipMemoryCache(true);// 禁用内存缓存。默认是开启的

        }

        sharedPreferences = getContext().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);

        loadMyPhoto();

    }

    // 打开应用后，加载头像
    private void loadMyPhoto() {

        try {

            String fileName = sharedPreferences.getString(SP_KEY_HEAD_ICON, "");

            File photoFile = new File(parentPath, fileName);
            if (photoFile.exists()){
//                addTimestamp();
                Glide.with(getContext()).load(photoFile).apply(options).into(iv_my_photo);
            }else {
                Glide.with(getContext()).load(R.mipmap.app_icon).apply(options)
                        .into(iv_my_photo);
            }
        }catch (Exception e){
            e.printStackTrace();
            Glide.with(getContext()).load(R.mipmap.app_icon).apply(options).into(iv_my_photo);
        }
    }

    @OnClick({R.id.linear_my_weather, R.id.linear_my_share, R.id.iv_my_photo,
            R.id.linear_my_rubbish_sort})
    public void doClick(View view){
        switch (view.getId()){

            case R.id.linear_my_weather:// 天气

                WeatherFragment weatherFragment = WeatherFragment.newInstance();

                showFragment(getActivity(), MineFragment.this, weatherFragment,
                        WeatherFragment.TAG);

                break;

            case R.id.linear_my_share:// 分享

                // 微信分享
                UMShareListener umShareListener = new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        LogUtil.e("TAG", "分享回调 onStart");
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        LogUtil.e("TAG", "分享回调 onResult");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        LogUtil.e("TAG", "分享回调 onError ：" + throwable.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        LogUtil.e("TAG", "分享回调 onCancel");
                    }
                };

                ShareBoardConfig config = new ShareBoardConfig();//新建ShareBoardConfig
                config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER);//设置位置
                config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
                config.setCancelButtonVisibility(true);

                UMImage umImage = new UMImage(getActivity(), R.mipmap.app_icon);

                new ShareAction(getActivity()).withText("UHello").setDisplayList(SHARE_MEDIA.WEIXIN,
                        SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE)
                        .withMedia(umImage).setCallback(umShareListener).open(config);


                break;

            case R.id.iv_my_photo:// 头像

                new AlertDialog.Builder(getContext())
                        .setItems(items_my_photo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:// 拍照
                                        request_camera_permission();
                                        break;
                                    case 1:// 相册
                                        request_storage_permission();
                                        break;
                                }
                            }
                        }).setCancelable(true).create().show();

                break;
            case R.id.linear_my_rubbish_sort:// 垃圾分类

                ToastUtil.showToast(getContext(), "敬请期待");
                break;
        }
    }

    // 申请 camera 运行时权限
    private void request_camera_permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            String permission_camera = Manifest.permission.CAMERA;
            requestPermissions(new String[]{permission_camera}, WHAT_PERMISSION_CAMERA);
        }
    }

    /**
     * 申请 storage 权限
     */
    private void request_storage_permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            String permission_write_external_storage = Manifest.permission.READ_EXTERNAL_STORAGE;
            requestPermissions(new String[]{permission_write_external_storage},
                    WHAT_PERMISSION_STORAGE);
        }
    }

    // 申请权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case WHAT_PERMISSION_STORAGE:// storage 权限组
                if (isGranted(grantResults)){
                    gallery();

                }else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            permissions[0])){
                        // 用户只拒绝了使用权限

                    }else {
                        // 此时用户拒绝使用权限，并勾选了不再询问项

                        new AlertDialog.Builder(getContext())
                                .setTitle("应用详情页")
                                .setIcon(R.mipmap.app_icon)
                                .setMessage("请手动开启应用的存储权限，否则无法使用存储功能")
                                .setCancelable(true)
                                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = AppDetailSettingUtil
                                                .getAppDetailSettingIntent(getContext());
                                        startActivity(intent);
                                    }
                                }).create().show();

                    }
                }
                break;

            case WHAT_PERMISSION_CAMERA:// camera 权限组
                if (isGranted(grantResults)){
                    takePhoto();
                }else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            permissions[0])){
                        // 用户只拒绝了使用权限

                    }else {
                        // 此时用户拒绝使用权限，并勾选了不再询问项

                        new AlertDialog.Builder(getContext())
                                .setTitle("应用详情页")
                                .setIcon(R.mipmap.app_icon)
                                .setMessage("请手动开启应用的相机权限，否则无法使用拍照功能")
                                .setCancelable(true)
                                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = AppDetailSettingUtil
                                                .getAppDetailSettingIntent(getContext());
                                        startActivity(intent);
                                    }
                                }).create().show();

                    }

                }

                break;
        }
    }


    // 用户是否同意权限
    private boolean isGranted(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    // 拍照
    private void takePhoto() {

        Intent cameraIntent = null;
        cameraUri = null;

        // 创建文件
        String cameraImageName = IMAGE_CAMERA_FILE_NAME
                + System.currentTimeMillis() + ".jpg";
        File file_camera_photo = createFileWithPathAndName(parentPath, cameraImageName);

        // Android 7 的相机拍照后图片文件的 uri 读取适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraUri = FileProvider.getUriForFile(getContext(), "com.mushiny.www.showU.provider",
                     file_camera_photo);
        }else {
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraUri = Uri.fromFile(file_camera_photo);
        }

        // 拍照的结果存到 cameraUri 对应的路径中
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(cameraIntent, REQUEST_CODE_GALLERY_CAMERA);

    }

    /**
     * 图库选择图片
     */
    private void gallery() {

        cameraUri = null;// 从图库选择的时候，记得将拍照的uri置为null
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY_CAMERA);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_GALLERY_CAMERA:// 图库选图裁剪

                    try {

                        Uri originUri = null;

                        if (cameraUri != null){
                            originUri = cameraUri;// 拍照后图片的 uri
                        }else if(data.getData() != null){
                            Uri galleryUri = data.getData();// 获取图库中图片的 uri
                            originUri = galleryUri;
                        }else {
                            return;
                        }

                        Intent intent_gallery_crop = new Intent("com.android.camera.action.CROP");
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
                            // 添加这一句表示对目标应用临时授权该 Uri 所代表的文件
                            intent_gallery_crop.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }

                        intent_gallery_crop.setDataAndType(originUri, "image/*");

                        // 设置裁剪
                        intent_gallery_crop.putExtra("crop", "true");
                        intent_gallery_crop.putExtra("scale", true);
                        // aspectX aspectY 是宽高的比例
                        intent_gallery_crop.putExtra("aspectX", 1);
                        intent_gallery_crop.putExtra("aspectY", 1);
                        // outputX outputY 是裁剪图片宽高
                        intent_gallery_crop.putExtra("outputX", 400);
                        intent_gallery_crop.putExtra("outputY", 400);
                        intent_gallery_crop.putExtra("return-data", false);

                        cropNewImageName = IMAGE_GALLERY_FILE_NAME
                                + System.currentTimeMillis() + ".jpg";

                        File file_crop = createFileWithPathAndName(parentPath, cropNewImageName);

                        crop_uri = Uri.fromFile(file_crop);

                        intent_gallery_crop.putExtra(MediaStore.EXTRA_OUTPUT, crop_uri);
                        intent_gallery_crop.putExtra("outputFormat", Bitmap.CompressFormat
                                .JPEG.toString());

                        startActivityForResult(intent_gallery_crop, REQUEST_CODE_CROP);

                    }catch (Exception e){
                        e.printStackTrace();
                        ToastUtil.showToast(getContext(), "图库选图异常：" + e.getMessage());
                    }

                    break;
                case REQUEST_CODE_CROP:// 裁剪后

                    try {
                        if (crop_uri != null){

                            if (sharedPreferences != null){
                                String fileName = sharedPreferences
                                        .getString(SP_KEY_HEAD_ICON, "");
                                if (!TextUtils.isEmpty(fileName)){
                                    // 不为空，先删除旧的头像图片
                                    File oldFile = new File(parentPath, fileName);
                                    if (oldFile.exists()){
                                        oldFile.delete();
                                    }
                                }
                                // 最后肯定要保存新的头像图片名称
                                sharedPreferences.edit().putString(SP_KEY_HEAD_ICON,
                                        cropNewImageName).apply();
                            }

//                            addTimestamp();
                            Glide.with(getContext()).load(crop_uri).apply(options).into(iv_my_photo);

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        ToastUtil.showToast(getContext(), "裁剪后异常：" + e.getMessage());
                    }

                    break;
            }
        }
    }

    /**
     * 根据 路径和文件名称 创建文件
     * @param parentPath
     * @param cropNewImageName
     * @return
     */
    private File createFileWithPathAndName(String parentPath,@NonNull String cropNewImageName) {
        // 第一次需要递归创建文件目录 parentPath，防止中间目录不存在的情况
        File file_dir = new File(parentPath);
        if (!file_dir.exists()){
            file_dir.mkdirs();
        }
        File file = new File(parentPath, cropNewImageName);
        return file;
    }

    // 添加时间戳
    private void addTimestamp(){
        if (options != null){
            options.signature(new ObjectKey(System.currentTimeMillis()));
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }
}
