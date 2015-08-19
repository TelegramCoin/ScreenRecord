package com.example.ozercanh.screenrecordqa;

import android.content.Context;

import com.example.ozercanh.screenrecordqa.Interface.RecorderListener;

public class RecorderBuilder {

    private Recorder recorder;

    public RecorderBuilder(Context context){
        recorder = new Recorder(context);
    }

    public RecorderBuilder setFps(int fps){
        recorder.setFps(fps);
        return this;
    }

    public RecorderBuilder setListener(RecorderListener listener){
        recorder.setRecorderListener(listener);
        return this;
    }

    public Recorder build(){
        return recorder;
    }
}