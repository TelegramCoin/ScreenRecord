package com.example.ozercanh.screenrecordqa;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;

/**
 * Created by ozercanh on 18/08/2015.
 */
public class Utility {
    private static Context context;
    private static SharedPreferences prefs;
    private static Application app;
    private static Recorder myRecorder;

    public static void init(Context con){
        context = con;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getNextRecordName(){
        int last = prefs.getInt("recordname", 0);
        prefs.edit().putInt("recordname", last+1).apply();
        return "record" + last + ".mp4";
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);

        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);

            if (TextUtils.isEmpty(type))
                type = "video/*"; // No MIME type found, so use the video wildcard
        }

        return type;
    }

    public static boolean hookActivityMethods(Context con) {
        boolean isSupport = DexposedBridge.canDexposed(con);
        boolean isLDevice = Build.VERSION.SDK_INT >= 21;

        if (isSupport) {
            DexposedBridge.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam arg0) throws Throwable {
                    myRecorder.onResume( (Activity) arg0.thisObject );
                    Log.d("injection", "resumed "+ ((Activity) arg0.thisObject).getComponentName());
                }
            });
            Log.d("injection", "onresume injected");

            DexposedBridge.findAndHookMethod(Activity.class, "onPause", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam arg0) throws Throwable {
                    myRecorder.onPause();
                }
            });
            if (isLDevice) {
                Log.d("dexposed", "This device does not support injection. Falling back to manual call mode.");
                return false;
            }
            return true;
        } else {
            Log.e("dexposed","This device does not support injection. Falling back to manual call mode.");
            return false;
        }
    }

    public static Context getContext() {
        return context;
    }

    public static Application getApp() {
        return app;
    }

    public static Recorder getRecorder(){
        return myRecorder;
    }

    public static void setApp(Application app) {
        Utility.app = app;
    }

    public static void setRecorder(Recorder myRecorder) {
        Utility.myRecorder = myRecorder;
    }
}
