package com.mushiny.www.showU.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mushiny.www.showU.R;
import com.mushiny.www.showU.util.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 我的 界面
 */
public class MineFragment extends BaseFragment {

    @BindView(R.id.iv_my_photo)ImageView iv_my_photo;

    private static final int WHAT_PERMISSION_STORAGE = 0x40;

    private static final int REQUEST_CODE_GALLERY = 0x41;
    private static final int REQUEST_CODE_CROP = 0x42;

    private static final String IMAGE_GALLERY_FILE_NAME = "qi_si_mine_photo_gallery_temp.jpg";
    private Uri crop_uri = null;

    private RequestOptions options;

    public MineFragment() {
        // Required empty public constructor
    }

    public static MineFragment newInstance(){
        return new MineFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        ButterKnife.bind(this, view);

        initData();

        setListener();

        return view;
    }

    // 设置监听
    private void setListener() {

        Glide.with(getContext()).load(R.drawable.xiaoyu_one).apply(options).into(iv_my_photo);

    }

    // 初始化数据
    private void initData() {

        if (options == null){
            options = new RequestOptions();
            options.placeholder(R.mipmap.ic_launcher_round);// 设置占位图
//            options.override(160,160)// 指定加载图片大小
            options.diskCacheStrategy(DiskCacheStrategy.ALL);// 缓存所有：原型、转换后的
            options.centerCrop();
//        options.override(Target.SIZE_ORIGINAL);// 加载图片原始尺寸
//            options.skipMemoryCache(true);// 禁用内存缓存。默认是开启的
        }

    }

    @OnClick({R.id.linear_my_weather, R.id.linear_my_setting, R.id.iv_my_photo})
    public void doClick(View view){
        switch (view.getId()){

            case R.id.linear_my_weather:// 天气

                WeatherFragment weatherFragment = WeatherFragment.newInstance();

                showFragment(getActivity(), MineFragment.this, weatherFragment, WeatherFragment.TAG);

                break;

            case R.id.linear_my_setting:// 设置

                break;

            case R.id.iv_my_photo:// 头像

                requestPermission();

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_GALLERY:// 图库选图裁剪

                    try {
                        Uri uri = data.getData();

                        Intent intent_gallery_crop = new Intent("com.android.camera.action.CROP");
                        intent_gallery_crop.setDataAndType(uri, "image/*");

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

                        File file_crop = new File(Environment.getExternalStorageDirectory(), IMAGE_GALLERY_FILE_NAME);
                        if (file_crop.exists()){
                            file_crop.delete();
                        }
                        file_crop.createNewFile();
                        crop_uri = Uri.fromFile(file_crop);

                        intent_gallery_crop.putExtra(MediaStore.EXTRA_OUTPUT, crop_uri);
                        intent_gallery_crop.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

                        startActivityForResult(intent_gallery_crop, REQUEST_CODE_CROP);

                    }catch (Exception e){
                        e.printStackTrace();
                        ToastUtil.showToast(getContext(), "图库选图异常：" + e.getMessage());
                    }

                    break;
                case REQUEST_CODE_CROP:// 裁剪后

                    try {
                        if (crop_uri != null){
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
     * 申请权限
     */
    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            /*
            if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                // 进行授权
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WHAT_PERMISSION_STORAGE);
            }
            */

            // 必要申请权限
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WHAT_PERMISSION_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case WHAT_PERMISSION_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    ToastUtil.showToast(getContext(), "您已授权，谢谢信任");

                    gallery();

                }else {
                    ToastUtil.showToast(getContext(), "存储权限未开启");
                }
                break;
        }
    }

    /**
     * 图片选择图片
     */
    private void gallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);

    }
}
