package com.example.ozercanh.screenrecordqa;

import android.content.Context;
import android.content.Intent;

/**
 * Created by ozercanh on 26/08/2015.
 */
public class ScreenRecord {

    public static Recorder myRecorder;

    private final Context context;
    private int fps;
    private Place where;
    private Size size;
    private boolean injection;

    public ScreenRecord(Context context){
        this.context = context;
    }

    public static ScreenRecord with(Context context){
        ScreenRecord screenRecord = new ScreenRecord(context);
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

    public ScreenRecord xposedActivity(boolean injection){
        this.injection = injection;
        return this;
    }

    public void start(){
        if(injection) {
            Utility.hookActivityMethods(context);
        }

        Intent intent = new Intent(context, RecordService.class);
        intent.setAction("start");
        intent.putExtra("fps", fps);
        intent.putExtra("where", where);
        intent.putExtra("size", size);
        intent.putExtra("package", context.getPackageName());
        context.startService(intent);
    }
}
