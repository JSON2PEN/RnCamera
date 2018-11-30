package com.rncameratest.Camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.io.File;

/**
 * Created by user on 2018/11/27.
 */

public class CameraAct extends Activity {
    //拍照
    public static final int CAMERA_REQUEST_CODE = 0;
    //相册选择
    public static final int PHOTO_REQUEST_CODE = 1;
    public Uri mImageUri;
    public String mPhotoPath;
    public static ReactContext reactContext;
    public static int tag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startCamera();
    }

    private void startCamera() {
        if(checkPermission()){
            if (tag==0) {
                takePhoto();
            }else {
                choosePhoto();
            }
        }
    }
    private boolean checkPermission(){
            String [] permissions =new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };
        for (String permission:permissions) {
            if (ContextCompat.checkSelfPermission(CameraAct.this, permission) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(CameraAct.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        tag==0?CAMERA_REQUEST_CODE:PHOTO_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
        startActivityForResult(intentToPickPic, PHOTO_REQUEST_CODE);
    }


    private void takePhoto() {
        // 跳转到系统的拍照界面
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定照片存储位置为sd卡本目录下
        mPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        // 获取图片所在位置的Uri路径 由于使用的tagBuildVersion 26需要通过内容提供者获取文件写入操作
        mImageUri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".my.provider",
                new File(mPhotoPath));
        //下面这句指定调用相机拍照后的照片存储的路径
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intentToTakePhoto, CAMERA_REQUEST_CODE);
    }

    //当拍摄照片完成时会回调到onActivityResult 在这里处理照片的裁剪
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE: {
                    sendToRn();
                    break;
                }
                case PHOTO_REQUEST_CODE: {
                    Uri mImageUri=data.getData();
                    if (!TextUtils.isEmpty(mImageUri.getAuthority())) {
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = managedQuery(mImageUri, proj, null, null,
                                null);
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        mPhotoPath = cursor.getString(column_index);
                    }else{
                        mPhotoPath= mImageUri.getPath();
                    }

                    sendToRn();
                    break;
                }
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendToRn() {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("photo", mPhotoPath);//原生调Rn
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    takePhoto();
                    break;
                case PHOTO_REQUEST_CODE:
                    choosePhoto();
                    break;
                default:
                    break;
            }

        } else {
            Toast.makeText(this, "一些权限被禁止", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reactContext=null;
    }
}
