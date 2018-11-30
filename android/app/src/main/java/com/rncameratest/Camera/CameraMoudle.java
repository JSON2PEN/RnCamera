package com.rncameratest.Camera;

import android.content.Intent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

/**
 * Created by user on 2018/11/27.
 */

public class CameraMoudle extends ReactContextBaseJavaModule {
    private ReactApplicationContext reactContext;
    public CameraMoudle(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext =reactContext;
    }

    @Override
    public String getName() {
        return "CameraMoudle";
    }
    //打开登录设置界面，用于给RN调用
    @ReactMethod
    public void openNative(int tag) {
        Intent intent = new Intent();
        CameraAct.reactContext=reactContext;
        CameraAct.tag=tag;
        intent.setClass(reactContext, CameraAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        reactContext.startActivity(intent);
    }
}
