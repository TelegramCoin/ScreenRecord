package com.example.ozercanh.screenrecordapp;

import android.app.Application;

import com.example.ozercanh.screenrecordqa.Model.Place;
import com.example.ozercanh.screenrecordqa.ScreenRecord;
import com.example.ozercanh.screenrecordqa.Model.Size;

/**
 * Created by ozercanh on 25/08/2015.
 */
public class ScreenRecordApp extends Application {


    @Override
    public void onCreate(){
        super.onCreate();

        ScreenRecord.
                with(this).
                maxFPS(2).
                place(Place.BOTTOM_LEFT).
                size(Size.MEDIUM).
                xposedInjection(true).start();
    }
}
