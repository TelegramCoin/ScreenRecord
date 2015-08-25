package com.example.ozercanh.screenrecordapp;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;

/**
 * Created by ozercanh on 25/08/2015.
 */
public class ScreenRecordApp extends Application {

    private boolean isSupport;
    private boolean isLDevice;

    @Override
    public void onCreate(){
        super.onCreate();


    }
}
