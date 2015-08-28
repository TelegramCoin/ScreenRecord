package com.example.ozercanh.screenrecordapp;

import android.app.Application;

import com.halilibo.screenrecorddebug.Model.Place;
import com.halilibo.screenrecorddebug.Model.Size;
import com.halilibo.screenrecorddebug.ScreenRecord;

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
