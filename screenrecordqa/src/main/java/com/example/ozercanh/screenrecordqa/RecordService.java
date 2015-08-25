package com.example.ozercanh.screenrecordqa;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.example.ozercanh.screenrecordqa.Interface.RecorderListener;
import com.melnykov.fab.FloatingActionButton;
import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;

public class RecordService extends Service {
    private WindowManager windowManager;
    private RelativeLayout recorderLayout;
    private RelativeLayout contentLayout;
    private FloatingActionButton recordButton;
    private Recorder myRecorder;

    @Override public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override public void onCreate() {
        super.onCreate();

        hookActivityMethods();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);

        recorderLayout = (RelativeLayout) View.inflate(this, R.layout.recorder_layout, null);
        contentLayout = (RelativeLayout) recorderLayout.findViewById(R.id.content_layout);
        recordButton = (FloatingActionButton) recorderLayout.findViewById(R.id.fab);

        contentLayout.setVisibility(View.GONE);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!myRecorder.isRecording()) {
                    myRecorder.startRecording();
                    recordButton.setColorNormalResId(R.color.recording_primary);
                    recordButton.startAnimation(animation);
                }
                else{
                    myRecorder.stopRecording();
                    recordButton.setColorNormalResId(R.color.primary);
                    recordButton.clearAnimation();
                }
            }
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM | Gravity.END;

        params.x = 0;
        params.y = 0;

        windowManager.addView(recorderLayout, params);

        myRecorder = new RecorderBuilder(this)
                .setFps(10)
                .setListener(new RecorderListener() {

                    @Override
                    public void onStart() {
                        //textView.setText("started");
                    }

                    @Override
                    public void onFail(String reason) {
                        //textView.setText("failed " + reason);
                    }

                    @Override
                    public void onFinish(final String path) {
                        /*if (path == null) {
                            textView.setText("failed");
                        } else {
                            textView.setText("finished " + path);
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
                                    intent.setDataAndType(Uri.parse(path), "video/mp4");
                                    startActivity(intent);
                                }
                            });
                        }*/
                    }
                })
                .build();
    }

    private void hookActivityMethods() {
        boolean isSupport = DexposedBridge.canDexposed(this);
        boolean isLDevice = Build.VERSION.SDK_INT >= 21;

        if (isSupport) {
            DexposedBridge.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam arg0) throws Throwable {
                    myRecorder.onResume((Activity) arg0.thisObject);
                    Log.i("onresume", "an activity is resumed");
                }
            });

            DexposedBridge.findAndHookMethod(Activity.class, "onPause", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam arg0) throws Throwable {
                    myRecorder.onPause();
                    Log.i("onresume", "an activity is paused");
                }
            });
            if (isLDevice) {
                Log.d("dexposed", "It doesn't support AOP to system method on ART devices");
            }
        } else {
            Log.e("dexposed","This device doesn't support dexposed!");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recorderLayout != null) windowManager.removeView(recorderLayout);
    }
}
