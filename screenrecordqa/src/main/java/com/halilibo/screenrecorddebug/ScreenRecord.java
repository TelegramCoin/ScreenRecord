package com.halilibo.screenrecorddebug;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.halilibo.screenrecorddebug.Model.Place;
import com.halilibo.screenrecorddebug.Model.Size;
import com.taobao.android.dexposed.DexposedBridge;

/**
 * Created by ozercanh on 26/08/2015.
 */
public class ScreenRecord {

    private final Context context;
    private int fps;
    private Place where;
    private Size size;
    private boolean xposed;

    public ScreenRecord(Application app){
        Utility.setApp(app);
        this.context = app.getApplicationContext();

    }

    public static ScreenRecord with(Application app){
        ScreenRecord screenRecord = new ScreenRecord(app);

        Utility.setRecorder(new Recorder(screenRecord.context));
        return screenRecord;
    }

    public ScreenRecord maxFPS(int fps){
        this.fps = fps;
        return this;
    }

    public ScreenRecord place(Place where){
        this.where = where;
        return this;
    }

    public ScreenRecord size(Size size ){
        this.size = size;
        return this;
    }

    public ScreenRecord xposedInjection(boolean xposed){
        boolean isSupport = DexposedBridge.canDexposed(context);
        boolean isLDevice = Build.VERSION.SDK_INT >= 21;
        if(isSupport && !isLDevice) {
            this.xposed = xposed;
        }
        else{
            this.xposed = false;
            Log.e("ScreenRecord", "This device does not support code injection into activities.");
        }
        return this;
    }

    public Recorder start(){
        if(xposed) {
            Utility.hookActivityMethods(context);
        }
        else{
            Utility.getApp().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {
                    Utility.getRecorder().onResume(activity);
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    Utility.getRecorder().onPause();
                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            });
        }

        Intent intent = new Intent(context, RecordService.class);
        intent.setAction("start");
        intent.putExtra("fps", fps);
        intent.putExtra("where", where);
        intent.putExtra("size", size);
        intent.putExtra("xposed", xposed);
        intent.putExtra("package", context.getPackageName());
        context.startService(intent);

        return Utility.getRecorder();
    }
}
