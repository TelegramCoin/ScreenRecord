package com.example.ozercanh.screenrecordqa;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ozercanh on 18/08/2015.
 */
public class Utility {
    private static Context context;
    private static SharedPreferences prefs;

    public static void init(Context con){
        context = con;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getNextRecordName(){
        int last = prefs.getInt("recordname", 0);
        prefs.edit().putInt("recordname", last+1).commit();
        return "record" + last + ".mp4";
    }
}
