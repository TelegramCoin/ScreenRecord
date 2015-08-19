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

    public static String getNextFolderName(){
        int last = prefs.getInt("foldername", 0);
        prefs.edit().putInt("foldername", last+1).commit();
        return "ss" + last;
    }
}
