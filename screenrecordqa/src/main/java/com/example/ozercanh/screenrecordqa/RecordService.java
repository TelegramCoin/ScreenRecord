package com.example.ozercanh.screenrecordqa;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.CardView;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ozercanh.screenrecordqa.Interface.RecorderListener;
import com.example.ozercanh.screenrecordqa.Model.Place;
import com.example.ozercanh.screenrecordqa.Model.Size;
import com.melnykov.fab.FloatingActionButton;

public class RecordService extends Service {

    private static final int SWIPE_THRESHOLD = 36;
    private static final int SWIPE_VELOCITY_THRESHOLD = 36;

    private WindowManager windowManager;
    private RelativeLayout recorderLayout;
    private FloatingActionButton recordButton;

    private String packageName;
    private int fps;
    private Place where;
    private Size size;
    private Animation rotateAnimation;

    private Handler mHandler;
    private GestureDetectorCompat mDetector;
    private int openMargin;
    private int closeMargin;
    private RelativeLayout activeLayout;
    private CardView cardView;
    private TextView recordingTextView;
    private WindowManager.LayoutParams windowParams;
    private LinearLayout contentHolderLayout;
    private Boolean timerRunning = Boolean.FALSE;
    private int runningSeconds = 0;
    private boolean xposed;

    @Override public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        if(intent == null || intent.getAction() == null){
            return START_NOT_STICKY;
        }

        if(intent.getAction().equals("start")){

            this.packageName = intent.getStringExtra("package");
            this.fps = intent.getIntExtra("fps", 10);
            this.where = (Place) intent.getSerializableExtra("where");
            this.size = (Size) intent.getSerializableExtra("size");
            this.xposed = intent.getBooleanExtra("xposed", false);

            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("where", Place.toInt(where)).apply();
        }
        else if(intent.getAction().equals("startRecording")){

            Utility.getRecorder().startRecording();
            return START_NOT_STICKY;
        }
        else if(intent.getAction().equals("update_settings")){

            updateSettings();
            return START_NOT_STICKY;
        }
        else if(intent.getAction().equals("on_activity_change")){

            activityChanged();
            return START_NOT_STICKY;
        }

        mHandler = new Handler();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Point dimen = new Point();
        windowManager.getDefaultDisplay().getSize(dimen);

        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);

        recorderLayout = (RelativeLayout) View.inflate(this, R.layout.recorder_layout, null);
        activeLayout = (RelativeLayout) recorderLayout.findViewById(R.id.active_layout);
        recordButton = (FloatingActionButton) recorderLayout.findViewById(R.id.fab);
        cardView = (CardView) recorderLayout.findViewById(R.id.card_view);
        recordingTextView = (TextView) recorderLayout.findViewById(R.id.recording_textview);
        contentHolderLayout = (LinearLayout) recorderLayout.findViewById(R.id.content_holder_layout);

        recordingTextView.setText(getString(R.string.settings));
        recordingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utility.getRecorder().isRecording()) {
                    Intent intent = new Intent(RecordService.this, SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utility.getRecorder().isRecording()) {
                    if (xposed) {
                        Intent intent = new Intent(RecordService.this, StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Utility.getRecorder().startRecording();
                    }
                } else {
                    Utility.getRecorder().stopRecording();
                }
            }
        });

        Utility.getRecorder().setFps(fps);
        Utility.getRecorder().setRecorderListener(new RecorderListener() {

            @Override
            public void onStart() {
                recordButton.setColorNormalResId(R.color.recording_primary);
                recordButton.startAnimation(rotateAnimation);
                startTimer();
            }

            @Override
            public void onFail(String reason) {
                recordButton.setColorNormalResId(R.color.primary);
                recordButton.clearAnimation();
                recordingTextView.setText(getString(R.string.settings));
                stopTimer();
                pushFailNotification(reason);
            }

            @Override
            public void onFinish(final String path) {

                recordButton.setColorNormalResId(R.color.primary);
                recordButton.clearAnimation();
                recordingTextView.setText(getString(R.string.settings));
                stopTimer();
                pushSuccessNotification(path);
            }
        });

        mDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(diffY) > SWIPE_THRESHOLD &&
                            Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {

                        if (diffX < 0 && diffY < 0) {

                            br_tl();
                            result = true;
                        }
                        else if(diffX < 0 && diffY > 0){

                            tr_bl();
                            result = true;

                        }
                        else if (diffX > 0 && diffY < 0) {

                            bl_tr();
                            result = true;
                        }
                        else if(diffX > 0 && diffY > 0){

                            tl_br();
                            result = true;

                        }
                    }

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }

            public void br_tl(){
                if(where == Place.BOTTOM_RIGHT){
                    show();
                }
                else if(where == Place.TOP_LEFT){
                    hide();
                }
            }

            public void bl_tr(){
                if(where == Place.BOTTOM_LEFT){
                    show();
                }
                else if(where == Place.TOP_RIGHT){
                    hide();
                }
            }

            public void tr_bl(){
                if(where == Place.TOP_RIGHT){
                    show();
                }
                else if(where == Place.BOTTOM_LEFT){
                    hide();
                }
            }

            public void tl_br(){
                if(where == Place.TOP_LEFT){
                    show();
                }
                else if(where == Place.BOTTOM_RIGHT){
                    hide();
                }
            }

        });
        recorderLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });

        switch (size){
            case SMALL:
                recordButton.setType(FloatingActionButton.TYPE_MINI);
                break;
            case MEDIUM:
                recordButton.setType(FloatingActionButton.TYPE_NORMAL);
                break;
        }

        windowParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        openMargin = (int) getResources().getDimension(R.dimen.open_margin);
        closeMargin = (int) getResources().getDimension(R.dimen.close_margin);

        placeWidget(false);

        windowParams.x = 0;
        windowParams.y = 0;

        windowManager.addView(recorderLayout, windowParams);

        return START_STICKY;
    }

    private void activityChanged() {
        Activity current = Utility.getRecorder().getActivity();
        if(current == null){
            recorderLayout.setVisibility(View.GONE);
        }
        else{
            if(isActivityFromApplication(current, Utility.getApp())){
                recorderLayout.setVisibility(View.VISIBLE);
            }
            else{
                recorderLayout.setVisibility(View.GONE);
            }
        }
    }

    private boolean isActivityFromApplication(Activity current, Application app) {
        if(current.getClass().equals(SettingsActivity.class))
            return false;
        else if(current.getClass().equals(AfterRecordActivity.class))
            return false;
        else if(current.getPackageName().equals(app.getPackageName()))
            return true;

        return false;
    }

    private void updateSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        switch (prefs.getString("position","1" )){
            case "1":
                where = Place.TOP_LEFT;
                break;
            case "2":
                where = Place.TOP_RIGHT;
                break;
            case "3":
                where = Place.BOTTOM_LEFT;
                break;
            case "4":
                where = Place.BOTTOM_RIGHT;
                break;
        }
        placeWidget(true);
    }

    private void pushFailNotification(String reason) {

    }

    private void pushSuccessNotification(String path) {
        Intent intent = new Intent(this, AfterRecordActivity.class);
        intent.putExtra("path", path);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startTimer() {
        timerRunning = true;
        Thread timerThread = new Thread(){
            public void run(){
                synchronized (timerRunning){
                    while(timerRunning && Utility.getRecorder().isRecording()){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                recordingTextView.setText(String.format("%02d:%02d:%02d", runningSeconds / 3600, (runningSeconds % 3600) / 60, runningSeconds % 60));
                            }
                        });
                        try {
                            Thread.sleep(1000);
                            runningSeconds++;
                        } catch (InterruptedException e) {
                            timerRunning = false;
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        timerThread.start();
        timerRunning = true;
    }

    private void stopTimer(){
        synchronized (timerRunning){
            timerRunning = false;
            runningSeconds = 0;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void placeWidget(boolean updateFlag) {

        RelativeLayout.LayoutParams contentHolderParams = (RelativeLayout.LayoutParams) contentHolderLayout.getLayoutParams();
        RelativeLayout.LayoutParams recordButtonParams = (RelativeLayout.LayoutParams) recordButton.getLayoutParams();

        switch (where){
            case TOP_LEFT:
                windowParams.gravity = Gravity.TOP | Gravity.LEFT;
                recorderLayout.setPadding(closeMargin, closeMargin, openMargin, openMargin); //substitute parameters for left, top, right, bottom

                recordButtonParams.removeRule(RelativeLayout.RIGHT_OF);
                recordButtonParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);

                contentHolderParams.removeRule(RelativeLayout.RIGHT_OF);
                contentHolderParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);

                recordButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                contentHolderParams.addRule(RelativeLayout.RIGHT_OF, R.id.fab);
                break;
            case TOP_RIGHT:
                windowParams.gravity = Gravity.TOP | Gravity.RIGHT;
                recorderLayout.setPadding(openMargin, closeMargin, closeMargin, openMargin); //substitute parameters for left, top, right, bottom

                recordButtonParams.removeRule(RelativeLayout.RIGHT_OF);
                recordButtonParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);

                contentHolderParams.removeRule(RelativeLayout.RIGHT_OF);
                contentHolderParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);

                contentHolderParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                recordButtonParams.addRule(RelativeLayout.RIGHT_OF, R.id.content_holder_layout);
                break;
            case BOTTOM_LEFT:
                windowParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
                recorderLayout.setPadding(closeMargin, openMargin, openMargin, closeMargin); //substitute parameters for left, top, right, bottom

                recordButtonParams.removeRule(RelativeLayout.RIGHT_OF);
                recordButtonParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);

                contentHolderParams.removeRule(RelativeLayout.RIGHT_OF);
                contentHolderParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);

                recordButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                contentHolderParams.addRule(RelativeLayout.RIGHT_OF, R.id.fab);
                break;
            case BOTTOM_RIGHT:
                windowParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                recorderLayout.setPadding(openMargin, openMargin, closeMargin, closeMargin); //substitute parameters for left, top, right, bottom

                recordButtonParams.removeRule(RelativeLayout.RIGHT_OF);
                recordButtonParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);

                contentHolderParams.removeRule(RelativeLayout.RIGHT_OF);
                contentHolderParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);

                contentHolderParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                recordButtonParams.addRule(RelativeLayout.RIGHT_OF, R.id.content_holder_layout);
                break;
        }
        contentHolderLayout.setLayoutParams(contentHolderParams);
        recordButton.setLayoutParams(recordButtonParams);

        if(updateFlag) {
            windowManager.updateViewLayout(recorderLayout, windowParams);
        }
    }

    private void hide(){
        Animation hideAnimation = AnimationUtils.loadAnimation(RecordService.this, R.anim.hide);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if(!Utility.getRecorder().isRecording())
                    switch (where){
                        case TOP_LEFT:
                            recorderLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_left_idle_bg));
                            break;
                        case TOP_RIGHT:
                            recorderLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_right_idle_bg));
                            break;
                        case BOTTOM_LEFT:
                            recorderLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_left_idle_bg));
                            break;
                        case BOTTOM_RIGHT:
                            recorderLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_right_idle_bg));
                            break;
                    }
                else
                    switch (where){
                        case TOP_LEFT:
                            recorderLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_left_recording_bg));
                            break;
                        case TOP_RIGHT:
                            recorderLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_right_recording_bg));
                            break;
                        case BOTTOM_LEFT:
                            recorderLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_left_recording_bg));
                            break;
                        case BOTTOM_RIGHT:
                            recorderLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_right_recording_bg));
                            break;
                    }

                activeLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        activeLayout.startAnimation(hideAnimation);

    }

    private void show(){
        Animation showAnimation = AnimationUtils.loadAnimation(RecordService.this, R.anim.show);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                recorderLayout.setBackgroundDrawable(null);
                if(Utility.getRecorder().isRecording()){
                    recordButton.startAnimation(rotateAnimation);
                }
                activeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        activeLayout.startAnimation(showAnimation);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recorderLayout != null) windowManager.removeView(recorderLayout);
    }
}
