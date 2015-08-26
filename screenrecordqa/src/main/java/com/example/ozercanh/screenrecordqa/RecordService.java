package com.example.ozercanh.screenrecordqa;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.example.ozercanh.screenrecordqa.Interface.RecorderListener;
import com.melnykov.fab.FloatingActionButton;

import static com.example.ozercanh.screenrecordqa.ScreenRecord.myRecorder;

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
        }
        else if(intent.getAction().equals("startRecording")){
            Log.d("injection", "start recording from action");
            myRecorder.startRecording();

            return START_NOT_STICKY;
        }

        mHandler = new Handler();

        myRecorder = new RecorderBuilder(this)
                .setFps(fps)
                .setListener(new RecorderListener() {

                    @Override
                    public void onStart() {
                        recordButton.setColorNormalResId(R.color.recording_primary);
                        recordButton.startAnimation(rotateAnimation);
                    }

                    @Override
                    public void onFail(String reason) {
                        recordButton.setColorNormalResId(R.color.primary);
                        recordButton.clearAnimation();
                    }

                    @Override
                    public void onFinish(final String path) {

                        recordButton.setColorNormalResId(R.color.primary);
                        recordButton.clearAnimation();
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

        try {
            ActivityInfo[] list = getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).activities;

            for(int i = 0;i< list.length;i++)
            {
                Log.i("activity", "Activity "+list[i].name);
            }
        }
        catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Point dimen = new Point();
        windowManager.getDefaultDisplay().getSize(dimen);

        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);

        recorderLayout = (RelativeLayout) View.inflate(this, R.layout.recorder_layout, null);
        recordButton = (FloatingActionButton) recorderLayout.findViewById(R.id.fab);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!myRecorder.isRecording()) {
                    Intent intent = new Intent(RecordService.this, StartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else{
                    myRecorder.stopRecording();
                }
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

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        openMargin = (int) getResources().getDimension(R.dimen.open_margin);
        closeMargin = (int) getResources().getDimension(R.dimen.close_margin);

        switch (where){
            case TOP_LEFT:
                params.gravity = Gravity.TOP | Gravity.LEFT;
                recorderLayout.setPadding(closeMargin, closeMargin, openMargin, openMargin); //substitute parameters for left, top, right, bottom
                break;
            case TOP_RIGHT:
                params.gravity = Gravity.TOP | Gravity.RIGHT;
                recorderLayout.setPadding(openMargin, closeMargin, closeMargin, openMargin); //substitute parameters for left, top, right, bottom
                break;
            case BOTTOM_LEFT:
                params.gravity = Gravity.BOTTOM | Gravity.LEFT;
                recorderLayout.setPadding(closeMargin, openMargin, openMargin, closeMargin); //substitute parameters for left, top, right, bottom
                break;
            case BOTTOM_RIGHT:
                params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                recorderLayout.setPadding(openMargin, openMargin, closeMargin, closeMargin); //substitute parameters for left, top, right, bottom
                break;
        }

        params.x = 0;
        params.y = 0;

        windowManager.addView(recorderLayout, params);

        return START_STICKY;
    }

    private void hide(){
        Animation hideAnimation = AnimationUtils.loadAnimation(RecordService.this, R.anim.hide);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                recordButton.setVisibility(View.GONE);

                if(!myRecorder.isRecording())
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
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        recordButton.startAnimation(hideAnimation);

    }

    private void show(){
        Animation showAnimation = AnimationUtils.loadAnimation(RecordService.this, R.anim.show);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                recorderLayout.setBackgroundDrawable(null);
                if(myRecorder.isRecording()){
                    recordButton.startAnimation(rotateAnimation);
                }
                recordButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        recordButton.startAnimation(showAnimation);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recorderLayout != null) windowManager.removeView(recorderLayout);
    }
}
