package com.example.ozercanh.screenrecordqa;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.example.ozercanh.screenrecordqa.Interface.RecorderListener;
import com.example.ozercanh.screenrecordqa.Model.RecorderParams;
import com.example.ozercanh.screenrecordqa.Model.Screenshot;
import com.example.ozercanh.screenrecordqa.Model.Status;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ozercanh on 18/08/2015.
 */
public class Recorder {

    private final Context context;
    private String recordName;
    private int fps;
    private int count;

    private Status statusFlag;
    private long initiateTime;
    private Activity activity;
    private Point size;
    private RecorderListener mRecorderListener;
    private File videoFile;


    private Handler handler;
    private RecorderThread recorderThread;
    private RecorderParams recorderParams;

    public Recorder(Context context){
        Utility.init(context);
        this.context = context;
    }

    public void onResume(Activity activity){
        this.activity = activity;
        Display display = activity.getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    public void onPause(){
        activity = null;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public void setRecorderListener(RecorderListener _listener){
        this.mRecorderListener = _listener;
    }

    private void initializeVariables() {
        this.recordName = Utility.getNextRecordName();
        this.handler = new Handler();
        this.videoFile = new File(Environment.getExternalStorageDirectory() , this.recordName);
        this.count = 0;

        recorderParams = new RecorderParams();
        recorderParams.videoPath = videoFile.getPath();
        recorderParams.fps = fps;
        recorderParams.screenWidth = size.x;
        recorderParams.screenHeight = size.y;
        recorderParams.queue = new LinkedBlockingQueue<>();

        recorderThread = new RecorderThread(recorderParams, mRecorderListener, handler);

    }

    public void startRecording(){
        initializeVariables();

        initiateTime = System.currentTimeMillis();

        recorderThread.start();
        statusFlag = Status.RECORDING;
        mRecorderListener.onStarted();
        recordFrame();
    }

    public void stopRecording(){
        this.statusFlag = Status.STOPPED;
        recorderThread.stopRecording();
    }

    public void cancelRecording(){
        statusFlag = Status.CANCELLED;

        mRecorderListener.onRecordCancel();
    }

    private void recordFrame() {
        if(statusFlag != Status.RECORDING){
            Log.d("recording", "takescreenshot status is not recording");
            return;
        }

        Screenshot ss = new Screenshot();

        long startTime = System.currentTimeMillis();
        ss.time = System.currentTimeMillis();
        ss.frameNumber = count;

        Bitmap drawing = null;
        try {
            View rootView = activity.findViewById(android.R.id.content).getRootView();
            drawing = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(drawing);
            rootView.draw(canvas);
        }
        catch(Error e){

        }
        catch(Exception e){
            stopRecording();
        }

        ss.bitmap = drawing;
        recorderParams.queue.offer(ss);

        count++;

        long finishTime = System.currentTimeMillis();
        if(finishTime - initiateTime >= 1000000){
            stopRecording();
        }
        else{
            int passedTime = (int) (finishTime - startTime);
            int delay = 1000/fps - passedTime;
            Log.i("delay", delay +"");
            if(delay <= 0){
                delay = 0;
            }

            Timer timer = new Timer();

            timer.schedule(new TimerTask() {

                synchronized public void run() {

                    recordFrame();
                }

            }, delay);

        }
    }



}
